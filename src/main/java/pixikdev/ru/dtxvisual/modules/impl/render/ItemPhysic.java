package pixikdev.ru.dtxvisual.modules.impl.render;

import pixikdev.ru.dtxvisual.client.events.impl.EventTick;
import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.modules.settings.impl.BooleanSetting;
import pixikdev.ru.dtxvisual.modules.settings.impl.NumberSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import java.util.Random;

public class ItemPhysic extends Module {

    private final BooleanSetting enableBounce = new BooleanSetting("Bounce", true, () -> true);
    private final NumberSetting bounceFactor = new NumberSetting("Bounce Factor", 0.7f, 0.1f, 1.0f, 0.05f, () -> enableBounce.getValue());
    private final NumberSetting friction = new NumberSetting("Friction", 0.6f, 0.1f, 0.9f, 0.05f, () -> enableBounce.getValue());

    private final BooleanSetting enableSpin = new BooleanSetting("Spin", true, () -> true);
    private final NumberSetting spinSpeed = new NumberSetting("Spin Speed", 1.5f, 0.5f, 5.0f, 0.1f, () -> enableSpin.getValue());

    private final BooleanSetting enableAirResistance = new BooleanSetting("Air Resistance", true, () -> true);
    private final NumberSetting airResistance = new NumberSetting("Air Resistance", 0.98f, 0.9f, 0.999f, 0.001f, () -> enableAirResistance.getValue());

    private final Random random = new Random();

    public ItemPhysic() {
        super("ItemPhysic", Category.Render, I18n.translate("module.itemphysic.description"));
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (fullNullCheck()) return;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ItemEntity itemEntity)) continue;
            if (!entity.isAlive()) continue;

            applyItemPhysics(itemEntity);
        }
    }

    private void applyItemPhysics(ItemEntity item) {
        Vec3d velocity = item.getVelocity();
        Vec3d position = item.getPos();

        if (enableAirResistance.getValue()) {
            double resistance = airResistance.getValue();
            velocity = velocity.multiply(resistance);
        }

        if (enableSpin.getValue()) {
            float currentYaw = item.getYaw();
            item.setYaw(currentYaw + spinSpeed.getValue().floatValue());

            if (item.age % 20 == 0) {
                item.setPitch((item.getPitch() + random.nextFloat() * 10 - 5) % 360);
            }
        }

        if (enableBounce.getValue() && !item.isOnGround()) {
            Vec3d nextPos = position.add(velocity);
            RaycastContext context = new RaycastContext(
                    position,
                    nextPos,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    item
            );

            HitResult hit = mc.world.raycast(context);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hit;

                Vec3d normal = Vec3d.of(blockHit.getSide().getVector());

                double dot = velocity.dotProduct(normal);
                if (dot < 0) {
                    Vec3d bounceVelocity = velocity.subtract(normal.multiply(2 * dot * bounceFactor.getValue()));

                    bounceVelocity = bounceVelocity.multiply(
                            friction.getValue(),
                            1.0,
                            friction.getValue()
                    );

                    item.setVelocity(bounceVelocity);

                    if (random.nextFloat() < 0.3f) {
                        item.addVelocity(
                                (random.nextDouble() - 0.5) * 0.1,
                                random.nextDouble() * 0.05,
                                (random.nextDouble() - 0.5) * 0.1
                        );
                    }
                }
            }
        }

        if (item.isOnGround()) {
            double groundFriction = 0.8;
            Vec3d groundVelocity = new Vec3d(
                    velocity.x * groundFriction,
                    Math.max(velocity.y, -0.1),
                    velocity.z * groundFriction
            );

            if (Math.abs(groundVelocity.x) < 0.01 && Math.abs(groundVelocity.z) < 0.01) {
                groundVelocity = new Vec3d(0, groundVelocity.y, 0);
            }

            item.setVelocity(groundVelocity);
        }

        if (item.isTouchingWater()) {
            double waterEffect = 0.3;
            if (item.age % 10 == 0) {
                item.addVelocity(
                        (random.nextDouble() - 0.5) * waterEffect,
                        random.nextDouble() * waterEffect * 0.5,
                        (random.nextDouble() - 0.5) * waterEffect
                );
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world != null) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof ItemEntity item) {
                    item.setYaw(0);
                    item.setPitch(0);
                }
            }
        }
    }
}