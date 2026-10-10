DAILY KEYS – Fabric 1.21.1

Co mod dělá:
- Přidá hráčský příkaz /daily.
- Vydá 4 klíče s ID daily_key přes příkaz SkiesCrates.
- Každý hráč má vlastní cooldown 24 hodin.
- Cooldown se ukládá do config/dailykeys-cooldowns.json a přežije restart serveru.

DŮLEŽITÉ:
- Toto je ZDROJOVÝ PROJEKT, ne hotový .jar.
- Je potřeba jej sestavit pomocí Java 21 a Gradle:
    gradle build
  Výsledný JAR bude v build/libs/dailykeys-1.0.0.jar.
- Mod musí být nainstalovaný jen na SERVERU; hráči nepotřebují klientský mod.
- Server musí mít Fabric API a SkiesCrates.
- Před nasazením nejdřív otestuj na záloze.
- Příkaz SkiesCrates použitý v kódu: crates key give <hráč> daily_key 4 silent.
- Pokud tvoje instalace nepřijme argument "silent", uprav v DailyKeysMod.java řetězec command tak, aby končil pouze na KEY_AMOUNT, pak projekt znovu sestav.
