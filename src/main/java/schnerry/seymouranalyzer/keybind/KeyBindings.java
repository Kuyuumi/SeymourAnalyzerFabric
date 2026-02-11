package schnerry.seymouranalyzer.keybind;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import schnerry.seymouranalyzer.config.ConfigScreen;
import schnerry.seymouranalyzer.gui.DatabaseScreen;

/**
 * Keybinding to open GUIs - alternative to commands
 */
public class KeyBindings {

    // 1.21.9+: Categories must be created as KeyBinding.Category objects
    private static final KeyBinding.Category SEYMOURANALYZER_CATEGORY =
        KeyBinding.Category.create(Identifier.of("seymouranalyzer", "main"));

    private static KeyBinding openDatabaseGuiKey;
    private static KeyBinding openConfigGuiKey;

    public static void register() {
        // P for Database GUI
        openDatabaseGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.seymouranalyzer.opendatabasegui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            SEYMOURANALYZER_CATEGORY
        ));

        // I for Config GUI
        openConfigGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.seymouranalyzer.openconfiggui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            SEYMOURANALYZER_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (openDatabaseGuiKey.wasPressed()) {
                client.setScreen(new DatabaseScreen(null));
            }

            if (openConfigGuiKey.wasPressed()) {
                client.setScreen(ConfigScreen.createConfigScreen(client.currentScreen));
            }
        });
    }
}

