package me.ghostpixels.ghostpings;

import me.ghostpixels.ghostpings.GhostPings.SummonLightningS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;

public class GhostPingsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		// From Fabric Docs on networking
		ClientPlayNetworking.registerGlobalReceiver(SummonLightningS2CPayload.ID, (payload, context) -> {
			ClientWorld world = context.client().world;

			if (world == null) {
				return;
			}

			BlockPos lightningPos = payload.pos();
			LightningEntity entity = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.TRIGGERED);

			if (entity != null) {
				entity.setPosition(lightningPos.getX(), lightningPos.getY(), lightningPos.getZ());
				world.addEntity(entity);
			}
		});
	}
}