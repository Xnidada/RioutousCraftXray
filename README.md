# RioutousCraftXray

Minecraft Java Edition 1.12.2 Forge mod based on Advanced XRay. It requires **IndustrialCraft 2 2.8.222-ex112**.

Six powered glasses tiers are registered (`glasses_l1`…`glasses_l6`). Wearing configured glasses consumes 100 EU per second and enables the existing X-Ray overlay. The radius and target count are fixed by the item tier and are not read from client configuration.

Craft glasses with any block item whose registry id contains `ore` (NBT is preserved by the recipe matcher). The ore block state is stored in the result; crafting the configured glasses with another ore replaces/appends targets up to the tier limit, consuming the old input.

Build with JDK 8 and ForgeGradle:

```bash
gradle build
```
