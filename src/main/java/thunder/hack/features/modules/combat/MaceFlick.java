package thunder.hack.features.modules.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class MaceFlick extends Module {

    // В ThunderHack обычно "mc" доступен. Если нет — используем MinecraftClient.getInstance()
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public final Setting<Float> attackRange = new Setting<>("Range", 3.0f, 1.0f, 6.0f);
    private int switchDelay = 0;
    private int lastSlot = 0;

    public MaceFlick() {
        super("MaceFlick", Category.COMBAT);
    }

    @EventHandler
    public void onUpdate(PlayerUpdateEvent event) {
        if (mc.player == null || mc.interactionManager == null) return;

        Entity ent = Managers.PLAYER.getRtxTarget(mc.player.getYaw(), mc.player.getPitch(), attackRange.getValue(), false);

        if (ent instanceof LivingEntity target) {
            if (mc.player.fallDistance > 0.2f && mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                int maceSlot = findItemInHotbar();
                if (maceSlot != -1) {
                    lastSlot = mc.player.getInventory().selectedSlot;

                    // Отправляем пакет на сервер
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(maceSlot));

                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);

                    // Задержка перед возвратом слота (для Grim)
                    switchDelay = 2;
                }
            }
        }

        if (switchDelay > 0) {
            switchDelay--;
            if (switchDelay == 0) {
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(lastSlot));
            }
        }
    }

    private int findItemInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.MACE) return i;
        }
        return -1;
    }
}