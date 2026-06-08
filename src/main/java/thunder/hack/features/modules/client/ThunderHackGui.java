package thunder.hack.features.modules.client;

import thunder.hack.gui.thundergui.ThunderGui;
import thunder.hack.features.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;

import java.awt.*;

public final class ThunderHackGui extends Module {
    public static final Setting<ColorSetting> onColor1 = new Setting<>("OnColor1", new ColorSetting(new Color(71, 0, 117, 255).getRGB()));
    public static final Setting<ColorSetting> onColor2 = new Setting<>("OnColor2", new ColorSetting(new Color(32, 1, 96, 255).getRGB()));
    public static final Setting<Float> scrollSpeed = new Setting<>("ScrollSpeed", 1f, 0.1F, 2.0F);

    // Новая настройка для переключения стиля
    public enum GuiMode { Thunder, Fatality }
    public static final Setting<GuiMode> guiMode = new Setting<>("GuiMode", GuiMode.Thunder);

    public ThunderHackGui() {
        super("ThunderGui", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.setScreen(ThunderGui.getThunderGui());
        disable();
    }

    public static Color getColorByTheme(int id) {
        // Если выбрана тема Fatality, возвращаем палитру Fatality
        if (guiMode.is(GuiMode.Fatality)) {
            return switch (id) {
                case 0 -> new Color(22, 22, 30, 255); // Фон
                case 1 -> new Color(30, 30, 45, 255); // Заголовки
                case 2 -> new Color(255, 255, 255);   // Основной текст
                case 4 -> new Color(30, 30, 45, 200); // Неактивный элемент
                case 5 -> new Color(200, 0, 200);     // Розовый акцент
                default -> new Color(22, 22, 30, 255);
            };
        }

        // Стандартная тема Thunder
        return switch (id) {
            case 0 -> new Color(37, 27, 41, 250);
            case 1 -> new Color(50, 35, 60, 250);
            case 2 -> new Color(-1);
            case 3, 8 -> new Color(0x656565);
            case 4 -> new Color(50, 35, 60, 178);
            case 5 -> new Color(133, 93, 162, 178);
            case 6 -> new Color(88, 64, 107, 178);
            case 7 -> new Color(25, 20, 30, 255);
            case 9 -> new Color(50, 35, 60, 178);
            default -> new Color(37, 27, 41, 250);
        };
    }
}