JAK ZÍSKAT JAR PŘES GITHUB ACTIONS

Tento balíček obsahuje zdrojový kód a automatický build workflow. Samotný .jar zatím není zkompilovaný.

1. Přihlas se na github.com a vytvoř nový repozitář (např. DailyKeysMod).
2. Rozbal ZIP do počítače.
3. Nahraj do repozitáře všechny soubory a složku .github (při nahrávání zkontroluj, že .github/workflows/build.yml je zachovaná cesta).
4. Po nahrání otevři v repozitáři záložku Actions.
5. Otevři „Build DailyKeys mod“ a klikni „Run workflow“ (pokud se nespustil automaticky po pushi).
6. Po dokončení zeleným stavem otevři běh workflow dole v části Artifacts stáhni „DailyKeys-Fabric-1.21.1“.
7. V ZIPu bude dailykeys-1.0.0.jar. Nahraj ho do složky mods na serveru a restartuj server.

Mod je určený pro Minecraft 1.21.1 Fabric. Příkaz /daily má dát 4 klíče s ID daily_key a cooldown 24 hodin.
Před nasazením ověř, že ID klíče ve SkiesCrates je přesně daily_key a že funguje příkaz:
/crates key give TVŮJ_NICK daily_key 4 silent

Poznámka: build workflow kompiluje zdrojový kód; kompilace sama o sobě nepotvrzuje, že příkaz klíče odpovídá přesně konfiguraci serveru. Nejprve otestuj na záloze/testovacím serveru.
