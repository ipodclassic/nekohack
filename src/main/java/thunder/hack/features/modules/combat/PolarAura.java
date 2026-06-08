package thunder.hack.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class PolarAura extends Module {
    // Настройка радиуса (стандарт 2.8 - 3.0 оптимален для Grim)
    public final Setting<Float> range = new Setting<>("Range", 2.8f, 2.0f, 3.5f);

    public PolarAura() {
        super("PolarAura", Category.COMBAT);
    }

    @EventHandler
    public void onUpdate(PlayerUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        // Ищем ближайшую цель
        Entity target = findTarget();

        // Если цель найдена и дистанция позволяет, атакуем
        if (target != null && mc.interactionManager != null) {
            // Проверка кулдауна (чтобы не спамить пакеты атаки и не получать Timer/AutoClicker)
            if (mc.player.getAttackCooldownProgress(0) >= 1.0f) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private Entity findTarget() {
        Entity best = null;
        double dist = range.getValue();

        // Перебираем всех сущностей в мире
        for (Entity ent : mc.world.getEntities()) {
            // Бьем только игроков, не себя, живых и в радиусе
            if (ent instanceof PlayerEntity && ent != mc.player && ent.isAlive()) {
                double d = mc.player.distanceTo(ent);
                if (d < dist) {
                    dist = d;
                    best = ent;
                }
            }
        }
        return best;
    }
}