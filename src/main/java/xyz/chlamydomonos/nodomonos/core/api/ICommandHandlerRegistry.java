package xyz.chlamydomonos.nodomonos.core.api;

public interface ICommandHandlerRegistry
{
    boolean registerCommandHandler(short commandID, ICommandHandler handler);
}
