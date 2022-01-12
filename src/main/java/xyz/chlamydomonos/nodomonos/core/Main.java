package xyz.chlamydomonos.nodomonos.core;

import org.apache.logging.log4j.LogManager;
import xyz.chlamydomonos.nodomonos.core.utils.Log4jInitializer;

import java.io.File;

public class Main
{
    static
    {
        Log4jInitializer.init();
    }

    public static void main(String[] args)
    {
        File test = new File("G:/idea/IntelliJ IDEA Community Edition 2021.3/bin/idea64.exe");
        LogManager.getLogger().debug(test.getAbsolutePath());
    }
}
