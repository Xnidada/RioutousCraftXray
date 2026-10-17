# RioutousCraftXray

Minecraft Java Edition 1.12.2 Forge mod based on Advanced XRay. It requires **IndustrialCraft 2 2.8.222-ex112**.

Six powered glasses tiers are registered (`glasses_l1`…`glasses_l6`). Wearing configured glasses automatically enables X-Ray and consumes 100 EU per second. The radius and target count are fixed by the item tier and are not read from client configuration; no keybind or selection menu is required.

The glasses are configured in the X-Ray Injector; they are no longer configured by a direct glasses+ORE crafting recipe.

The X-Ray Injector block is used to configure glasses. Insert glasses and an ORE item, charge the machine with IC2 EU, then use **Inject ORE** or **Clear ORE**. Items tagged with a Forge OreDictionary name beginning with `ore` are accepted, as are registry IDs containing `ore`; untagged blocks can be added to the server's `Additional ore blocks` config as `modid:block` or `modid:block:metadata`. The server is authoritative; the client only filters the slot to block items. Each operation costs 2,500 EU and the server enforces the tier target limit; once the limit is full, clear a selected target before injecting another.

Build with JDK 8 and ForgeGradle:

```bash
JAVA_HOME=/opt/minecraft-tools/jdk8 /tmp/gradle/gradle-4.9/bin/gradle --no-daemon --offline -Dnet.minecraftforge.gradle.test_certs=false clean build
```

The resulting re-obfuscated mod jar is written to `build/libs/`.

This project is licensed under GPLv3. The original Advanced XRay attribution is retained in the license and mod metadata.
