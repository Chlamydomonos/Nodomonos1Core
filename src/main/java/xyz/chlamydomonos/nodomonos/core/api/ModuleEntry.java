package xyz.chlamydomonos.nodomonos.core.api;

import com.google.gson.JsonObject;

public abstract class ModuleEntry
{
    private final int moduleID;

    public ModuleEntry(int moduleID)
    {
        this.moduleID = moduleID;
    }

    public final int getModuleID()
    {
        return moduleID;
    }

    public abstract JsonObject createConfig();
    public abstract void loadConfig(JsonObject config);
    public abstract void initAsNew(byte version);
    public abstract void initFromData(JsonObject data);
    public abstract void registerCommandHandlers(ICommandHandlerRegistry registry);

    @Override
    public final boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ModuleEntry)) return false;
        ModuleEntry that = (ModuleEntry) o;
        return moduleID == that.moduleID;
    }

    @Override
    public final int hashCode()
    {
        return moduleID;
    }
}
