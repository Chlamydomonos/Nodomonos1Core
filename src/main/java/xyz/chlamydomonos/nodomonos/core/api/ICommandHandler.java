package xyz.chlamydomonos.nodomonos.core.api;

public interface ICommandHandler
{
    void processCommand(byte[] commandCustomData, INode node);
}
