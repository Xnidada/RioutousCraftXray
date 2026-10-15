# RioutousCraftXray

Minecraft Java Edition 1.12.2 Forge mod based on Advanced XRay. It requires **IndustrialCraft 2 2.8.222-ex112**.

Six powered glasses tiers are registered (`glasses_l1`…`glasses_l6`). Wearing configured glasses automatically enables X-Ray and consumes 100 EU per second. The radius and target count are fixed by the item tier and are not read from client configuration; no keybind or selection menu is required.

The glasses are configured in the X-Ray Injector; they are no longer configured by a direct glasses+ORE crafting recipe.

The X-Ray Injector block is used to configure glasses. Insert glasses and an ORE item, charge the machine with IC2 EU, then use **Inject ORE** or **Clear ORE**. Each operation costs 10,000 EU and the server enforces the tier target limit; once the limit is full, clear a selected target before injecting another.

Build with JDK 8 and ForgeGradle:

```bash
gradle build
```
