ELYTRA SWAP (Fabric 1.21.11, client-side)

Build:
  1. Easiest: go to https://fabricmc.net/develop , pick Minecraft 1.21.11, make a template project,
     then copy this project's "src" folder over the template's "src" folder
     (and update fabric.mod.json / package name if you changed anything).
  2. Or use this project directly: install JDK 21, add the Gradle wrapper
     (run "gradle wrapper" once), then run:  ./gradlew build
  3. Your mod jar will be in build/libs/ (use the one WITHOUT "-sources").
  4. Put it in your .minecraft/mods folder together with Fabric API.

Use:
  - Options > Controls > Key Binds > "Elytra Swap" to set your own keys.
  - "Swap Chestplate / Elytra" (default R): switches between chestplate and elytra.
  - "Turn Elytra Swap On/Off" (unbound by default): quick on/off hotkey.
  - On/off state is saved in config/elytraswap.json ("enabled": true/false).
