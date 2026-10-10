package cz.gagi.dailykeys;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DailyKeysMod implements ModInitializer {
    private static final long COOLDOWN_MS = 24L * 60L * 60L * 1000L;
    private static final int KEY_AMOUNT = 4;
    private static final String KEY_ID = "daily_key";
    private static final Path DATA_FILE =
            FabricLoader.getInstance().getConfigDir().resolve("dailykeys-cooldowns.json");
    private static final Map<UUID, Long> LAST_CLAIM = new HashMap<>();

    @Override
    public void onInitialize() {
        loadData();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("daily")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            return claim(player);
                        })));
    }

    private static int claim(ServerPlayerEntity player) {
        long now = System.currentTimeMillis();
        UUID id = player.getUuid();
        long last = LAST_CLAIM.getOrDefault(id, 0L);
        long remaining = COOLDOWN_MS - (now - last);

        if (remaining > 0) {
            long hours = remaining / 3_600_000L;
            long minutes = (remaining % 3_600_000L + 59_999L) / 60_000L;
            if (minutes >= 60) {
                hours++;
                minutes = 0;
            }
            player.sendMessage(Text.literal("§cDaily odměnu už máš vyzvednutou. Zkus to znovu za "
                    + hours + " h " + minutes + " min."), false);
            return 0;
        }

        // Run the official SkiesCrates command as the server console.
        String command = "crates key give " + player.getGameProfile().getName()
                + " " + KEY_ID + " " + KEY_AMOUNT + " silent";
        try {
            player.getServer().getCommandManager().executeWithPrefix(
                    player.getServer().getCommandSource(), command);
        } catch (Exception e) {
            player.sendMessage(Text.literal("§cDaily klíče se nepodařilo vydat. Kontaktuj správce."));
            return 0;
        }

        LAST_CLAIM.put(id, now);
        saveData();
        player.sendMessage(Text.literal("§aDenní odměna: získal jsi 4 Daily Keys!"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static void loadData() {
        if (!Files.exists(DATA_FILE)) return;
        try {
            String raw = Files.readString(DATA_FILE, StandardCharsets.UTF_8).trim();
            if (raw.startsWith("{") && raw.endsWith("}")) {
                String body = raw.substring(1, raw.length() - 1).trim();
                if (!body.isEmpty()) {
                    for (String entry : body.split(",")) {
                        String[] pair = entry.split(":", 2);
                        if (pair.length == 2) {
                            String uuidText = pair[0].trim().replace("\"", "");
                            String timeText = pair[1].trim();
                            LAST_CLAIM.put(UUID.fromString(uuidText), Long.parseLong(timeText));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DailyKeys] Could not read cooldown file: " + e.getMessage());
        }
    }

    private static void saveData() {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            StringBuilder out = new StringBuilder("{\n");
            int i = 0;
            for (Map.Entry<UUID, Long> entry : LAST_CLAIM.entrySet()) {
                if (i++ > 0) out.append(",\n");
                out.append("  \"").append(entry.getKey()).append("\": ").append(entry.getValue());
            }
            out.append("\n}\n");
            Files.writeString(DATA_FILE, out.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[DailyKeys] Could not save cooldown file: " + e.getMessage());
        }
    }
}
