package thunder.hack.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import thunder.hack.core.Managers;
import thunder.hack.events.impl.TotemPopEvent;
import thunder.hack.features.hud.HudElement;
import thunder.hack.features.modules.combat.AntiBot;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Timer;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.TextureStorage;
import java.awt.*;
import static thunder.hack.features.modules.client.ClientSettings.isRu;

public class Companion extends HudElement {

    public enum Mode {
        Boykisser, Paimon, Baltika, Kowk, velvet, niko2
    }

    public final Setting<Integer> scale = new Setting<>("Scale", 50, 0, 100);
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.Boykisser);

    public static int currentFrame;
    private String message = "";
    private final Timer lastPop = new Timer();
    private final Timer frameRate = new Timer();

    public Companion() {
        super("2DCompanion", 50, 10);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || AntiBot.bots.contains(player) || player.getHealth() > 0 || !Managers.COMBAT.popList.containsKey(player.getName().getString()))
                continue;
            if (isRu())
                message = player.getName().getString() + " попнул " + (Managers.COMBAT.popList.get(player.getName().getString()) > 1 ? Managers.COMBAT.popList.get(player.getName().getString()) + " тотемов и сдох! ИЗЗЗЗИИ" : "тотем и сдох! ИЗЗЗЗИИ");
            else
                message = player.getName().getString() + " popped " + (Managers.COMBAT.popList.get(player.getName().getString()) > 1 ? Managers.COMBAT.popList.get(player.getName().getString()) + " totems and died EZ LMAO!" : "totem and died EZ LMAO!");
            lastPop.reset();
        }
    }

    @Override
    public void onRender2D(DrawContext context) {
        super.onRender2D(context);

        context.getMatrices().push();
        context.getMatrices().translate((int) getPosX() + 100, (int) getPosY() + 100, 0);
        context.getMatrices().scale((float) scale.getValue() / 100f, (float) scale.getValue() / 100f, 1);
        context.getMatrices().translate(-((int) getPosX() + 100), -((int) getPosY() + 100), 0);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();

        // Классический switch для старых версий Java
        switch (mode.getValue()) {
            case Boykisser: context.drawTexture(TextureStorage.boykisser, (int) getPosX(), (int) getPosY(), 0, currentFrame * 128, 130, 128, 130, 6784); break;
            case Paimon: context.drawTexture(TextureStorage.paimon, (int) getPosX(), (int) getPosY(), 0, currentFrame * 200, 200, 200, 200, 10600); break;
            case Baltika: context.drawTexture(TextureStorage.baltika, (int) getPosX(), (int) getPosY(), 0, 0, 421, 800, 421, 800); break;
            case Kowk: context.drawTexture(TextureStorage.kowk, (int) getPosX(), (int) getPosY(), 0, 0, 287, 252, 287, 252); break;
            case velvet: context.drawTexture(TextureStorage.velvet, (int) getPosX(), (int) getPosY(), 0, (currentFrame % 10) * 256, 256, 256, 256, 2560); break;
            case niko2: context.drawTexture(TextureStorage.niko2, (int) getPosX(), (int) getPosY(), 0, (currentFrame % 4) * 200, 200, 200, 200, 800); break;
        }

        context.getMatrices().pop();

        if (!lastPop.passedMs(2000)) {
            Render2DEngine.drawRound(context.getMatrices(), getPosX() + scale.getValue() / 3f, getPosY() + 70 - scale.getValue(), 100, 10, 3, new Color(0xFCD7DD));
            FontRenderers.sf_bold.drawString(context.getMatrices(), message, getPosX() + 2 + scale.getValue() / 3f, getPosY() + 72 - scale.getValue(), new Color(0x484848).getRGB());
        }

        if (frameRate.passedMs(64)) {
            frameRate.reset();
            currentFrame++;
            if (currentFrame > 52) currentFrame = 0;
        }
    }

    @EventHandler
    public void onTotemPop(@NotNull TotemPopEvent event) {
        if (event.getEntity() == mc.player) return;
        if (isRu())
            message = event.getEntity().getName().getString() + " попнул " + (event.getPops() > 1 ? event.getPops() + " тотемов!" : "тотем!");
        else
            message = event.getEntity().getName().getString() + " popped " + (event.getPops() > 1 ? event.getPops() + " totems!" : " a totem!");
        lastPop.reset();
    }
}