package xyz.chlamydomonos.nodomonos.core;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import xyz.chlamydomonos.nodomonos.core.api.ICommandHandler;
import xyz.chlamydomonos.nodomonos.core.api.ICommandHandlerRegistry;
import xyz.chlamydomonos.nodomonos.core.api.INode;
import xyz.chlamydomonos.nodomonos.core.api.ModuleEntry;
import xyz.chlamydomonos.nodomonos.core.utils.FileUtil;
import xyz.chlamydomonos.nodomonos.core.utils.ModuleLoader;

import java.io.File;
import java.util.*;

public class Node implements INode
{
    private byte version;
    private int uid;
    private boolean dataLoaded;
    private boolean uidInitialized;
    private final Set<ModuleEntry> modules;
    private boolean modulesLoaded;
    private final Map<Short, ICommandHandler> commandHandlers;
    private boolean commandHandlersLoaded;

    public Node()
    {
        version = 0;
        uid = 0;
        dataLoaded = false;
        uidInitialized = false;
        modules = new HashSet<>();
        modulesLoaded = false;
        commandHandlers = new HashMap<>();
        commandHandlersLoaded = false;
    }

    public void loadModules(String modulesPath)
    {
        ArrayList<File> moduleFiles = FileUtil.getFilesInDir(new File(modulesPath));
        LogManager.getLogger().info(String.format(
                "Start loading modules from %s...",
                modulesPath));

        for(File i : moduleFiles)
        {
            if(FileUtil.inFormat(i, "jar"))
            {
                try
                {
                    LogManager.getLogger().info(String.format(
                            "Trying to load modules from %s...",
                            i.getName()));

                    modules.addAll(ModuleLoader.loadModules(i));
                }
                catch (Exception e)
                {
                    LogManager.getLogger().warn(String.format(
                            "Failed to load modules from %s, caused by:\n%s",
                            i.getAbsolutePath(),
                            e.getMessage()));
                }
            }
        }

        modulesLoaded = true;
    }

    public void loadConfig(@NotNull JsonObject config)
    {
        LogManager.getLogger().info("Start loading config...");
        for (ModuleEntry i : modules)
        {
            if(config.has(Integer.toString(i.getModuleID())))
                i.loadConfig(config.getAsJsonObject(Integer.toString(i.getModuleID())));
            else
            {
                LogManager.getLogger().info(String.format(
                        "Cannot find config for module #%d, trying to create it...",
                        i.getModuleID()));

                JsonObject moduleConfig = i.createConfig();
                config.add(Integer.toString(i.getModuleID()), moduleConfig);

                LogManager.getLogger().info(String.format(
                        "Successfully created config for module #%d",
                        i.getModuleID()));
            }
        }

        LogManager.getLogger().info("Loaded config for all modules");
    }

    public void initAsNew(byte version)
    {
        LogManager.getLogger().info(String.format(
                "Initializing new node, version %d...",
                (int) version));

        this.version = version;
        dataLoaded = true;

        for (ModuleEntry i : modules)
        {
            LogManager.getLogger().info(String.format(
                    "Initializing new module #%d...",
                    i.getModuleID()));

            i.initAsNew(version);

            LogManager.getLogger().info(String.format(
                    "Initialized new module #%d",
                    i.getModuleID()));
        }

        LogManager.getLogger().info(String.format(
                "Initialized new node, version %d",
                (int) version));

        dataLoaded = true;
    }

    public void initFromData(@NotNull JsonObject data)
    {
        LogManager.getLogger().info("Loading node data...");

        version = data.get("version").getAsByte();
        uidInitialized = data.get("hasUID").getAsBoolean();
        if(uidInitialized)
            uid = data.get("uid").getAsInt();

        JsonObject moduleData = data.getAsJsonObject("moduleData");
        for(ModuleEntry i : modules)
        {

            if(data.has(Integer.toString(i.getModuleID())))
            {
                LogManager.getLogger().info(String.format(
                        "Loading data for module #%d...",
                        i.getModuleID()));

                i.initFromData(moduleData.getAsJsonObject(Integer.toString(i.getModuleID())));

                LogManager.getLogger().info(String.format(
                        "Loaded data for module #%d",
                        i.getModuleID()));
            }
            else
            {
                LogManager.getLogger().info(String.format(
                        "Cannot find data for module #%d, trying to initialize as new module...",
                        i.getModuleID()));

                i.initAsNew(version);

                LogManager.getLogger().info(String.format(
                        "Initialized new module #%d",
                        i.getModuleID()));
            }
        }
    }

    public void registerCommandHandlers()
    {
        LogManager.getLogger().info("Start registering command handlers...");

        ICommandHandlerRegistry registry = (commandID, handler) ->
        {
            if(commandHandlers.containsKey(commandID))
                return false;
            commandHandlers.put(commandID, handler);
            return true;
        };

        CommandHandlers.registerAll(registry);

        LogManager.getLogger().info("Registered local command handlers");

        for(ModuleEntry i : modules)
        {
            LogManager.getLogger().info(String.format(
                    "Registering command handlers of module #%d...",
                    i.getModuleID()));

            i.registerCommandHandlers(registry);
        }

        commandHandlersLoaded = true;
    }

    @Override
    public byte getVersion()
    {
        return version;
    }

    @Override
    public int getUid()
    {
        return uid;
    }

    @Override
    public boolean isDataLoaded()
    {
        return dataLoaded;
    }

    @Override
    public boolean isUidInitialized()
    {
        return uidInitialized;
    }

    @Override
    public Set<ModuleEntry> getModules()
    {
        return modules;
    }

    @Override
    public boolean isModulesLoaded()
    {
        return modulesLoaded;
    }

    @Override
    public void setUid(int uid)
    {
        this.uid = uid;
    }

    @Override
    public void setUidInitialized(boolean uidInitialized)
    {
        this.uidInitialized = uidInitialized;
    }

    @Override
    public Map<Short, ICommandHandler> getCommandHandlers()
    {
        return commandHandlers;
    }

    @Override
    public boolean isCommandHandlersLoaded()
    {
        return commandHandlersLoaded;
    }
}
