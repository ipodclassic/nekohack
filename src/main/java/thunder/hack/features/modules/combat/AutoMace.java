package thunder.hack.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class AutoMace extends Module {

    public final Setting<Float> attackRange = new Setting<>("Range", 3.0f, 1.0f, 6.0f);
    public final Setting<Boolean> pauseEating = new Setting<>("PauseWhileEating", true);

    public AutoMace() {
        super("AutoMace", Category.COMBAT);
    }

    @EventHandler
    public void onUpdate(PlayerUpdateEvent event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (mc.player.isUsingItem() && pauseEating.getValue()) return;
        if (mc.player.getMainHandStack().getItem() != Items.MACE) return;

        // Поиск цели по направлению взгляда
        Entity ent = Managers.PLAYER.getRtxTarget(mc.player.getYaw(), mc.player.getPitch(), attackRange.getValue(), false);

        if (ent instanceof LivingEntity target && !Managers.FRIEND.isFriend(target.getName().getString())) {

            // Проверка кулдауна и того, что ты падаешь (хотя бы чуть-чуть)
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f && mc.player.fallDistance > 0.1f) {

                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}