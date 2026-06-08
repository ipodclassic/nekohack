package thunder.hack.features.modules.movement;

import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.orbit.EventHandler;
import thunder.hack.events.impl.PlayerUpdateEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;

public class WindJump extends Module {

    public final Setting<Mode> mode = new Setting<>("Mode", Mode.HvH);
    public final Setting<Boolean> motionCorrection = new Setting<>("MoveCorrection", true);

    public enum Mode { HvH, Legit }

    private int stage = 0;
    private int oldSlot = -1;
    private int timer = 0;

    public WindJump() {
        super("WindJump", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) { toggle(); return; }

        int windSlot = findItemInHotbar(Items.WIND_CHARGE);
        if (windSlot == -1) { sendMessage("§cЗаряд ветра не найден!"); toggle(); return; }

        oldSlot = mc.player.getInventory().selectedSlot;

        if (mode.getValue() == Mode.HvH) {
            executeHvH(windSlot);
            toggle();
        } else {
            // Легит режим: начинаем стадию 1
            mc.player.getInventory().selectedSlot = windSlot;
            stage = 1;
            timer = 0;
        }
    }

    private void executeHvH(int windSlot) {
        if (motionCorrection.getValue()) mc.player.setVelocity(Vec3d.ZERO);

        float oldPitch = mc.player.getPitch();
        mc.player.setPitch(90.0f);

        // Пакетный свап для HvH
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(windSlot));
        mc.player.getInventory().selectedSlot = windSlot;
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(oldSlot));
        mc.player.getInventory().selectedSlot = oldSlot;

        mc.player.setPitch(oldPitch);

        if (motionCorrection.getValue() && mc.player.input != null) {
            float f = mc.player.getYaw() * MathHelper.RADIANS_PER_DEGREE;
            mc.player.setVelocity(-MathHelper.sin(f) * 0.45, mc.player.getVelocity().y, MathHelper.cos(f) * 0.45);
        }
    }

    @EventHandler
    public void onUpdate(PlayerUpdateEvent event) {
        if (mode.getValue() == Mode.HvH) return;

        timer++;
        // Растягиваем выполнение Легит-режима на 2 тика, чтобы Grim не кикал
        if (stage == 1 && timer >= 2) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            stage = 2;
            timer = 0;
        } else if (stage == 2 && timer >= 2) {
            mc.player.getInventory().selectedSlot = oldSlot;
            stage = 0;
            toggle();
        }
    }

    private int findItemInHotbar(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) return i;
        }
        return -1;
    }
}