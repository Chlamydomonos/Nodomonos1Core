package xyz.chlamydomonos.nodomonos.core.api;

import java.util.Map;
import java.util.Set;

public interface INode
{
    byte getVersion();
    boolean isUidInitialized();
    void setUidInitialized(boolean uidInitialized);
    int getUid();
    void setUid(int uid);
    boolean isModulesLoaded();
    Set<ModuleEntry> getModules();
    boolean isDataLoaded();
    boolean isCommandHandlersLoaded();
    Map<Short, ICommandHandler> getCommandHandlers();
}
