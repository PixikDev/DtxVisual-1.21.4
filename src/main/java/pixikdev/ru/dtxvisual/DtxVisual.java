package pixikdev.ru.dtxvisual;
import pixikdev.ru.dtxvisual.client.managers.*;
import pixikdev.ru.dtxvisual.client.util.Wrapper;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import pixikdev.ru.dtxvisual.client.ui.clickgui.ClickGui;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.*;
import pixikdev.ru.dtxvisual.client.ui.hud.impl.WaypointOverlay;

import java.io.File;
import java.lang.invoke.MethodHandles;

@Getter
public class DtxVisual implements ModInitializer, Wrapper {

    @Getter private static DtxVisual instance;

    private IEventBus eventHandler;
    private long initTime;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private AutoSaveManager autoSaveManager;
    private NotifyManager notifyManager;
    private PerformanceManager performanceManager;
    private ClickGui clickGui;
    private HudManager hudManager;
    private AltManager altManager;
    private WaypointOverlay waypointOverlay;

    public static Logger LOGGER = LogManager.getLogger(DtxVisual.class);
    private final File globalsDir = new File(mc.runDirectory, "dtxvisual");
    private final File configsDir = new File(globalsDir, "configs");

    @Override
    public void onInitialize() {
        LOGGER.info("[dtxvisual] Starting initialization.");
        initTime = System.currentTimeMillis();
        instance = this;

        createDirs(globalsDir, configsDir);
        eventHandler = new EventBus();

        eventHandler.registerLambdaFactory("pixikdev.ru.dtxvisual",
                (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup())
        );

        FriendsManager.init(globalsDir);
        AltManager.init(globalsDir);
        String lastAlt = AltManager.getLastUsedNickname();
        if (lastAlt != null && !lastAlt.isEmpty()) {
            AltManager.applyNickname(lastAlt);
        }

        notifyManager = new NotifyManager();
        performanceManager = new PerformanceManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        configManager = new ConfigManager();
        autoSaveManager = new AutoSaveManager();
        clickGui = new ClickGui();
        hudManager = new HudManager();

        waypointOverlay = new WaypointOverlay();
        eventHandler.subscribe(waypointOverlay);

        
        autoSaveManager.loadAutoSave();

        

        LOGGER.info("[dtxvisual] Successfully initialized for {} ms.", System.currentTimeMillis() - initTime);
    }

    private void createDirs(File... file) {
        for (File f : file) f.mkdirs();
    }

    public static Identifier id(String path) {
        return Identifier.of("dtxvisual", path);
    }
}
