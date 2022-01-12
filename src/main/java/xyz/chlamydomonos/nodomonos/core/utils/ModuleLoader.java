package xyz.chlamydomonos.nodomonos.core.utils;

import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import xyz.chlamydomonos.nodomonos.core.api.ModuleEntry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleLoader
{
    public static @NotNull ArrayList<ModuleEntry> loadModules(@NotNull File moduleFile)
    {
        ArrayList<ModuleEntry> out = new ArrayList<>();

        URLClassLoader classLoader;
        try
        {
            classLoader = new URLClassLoader(
                    new URL[]{moduleFile.toURI().toURL()},
                    Thread.currentThread().getContextClassLoader()
            );
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }

        JarFile jarFile;
        try
        {
            jarFile = new JarFile(moduleFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();

        JarEntry entry;

        while (entries.hasMoreElements())
        {
            entry = entries.nextElement();
            if(!entry.getName().contains("META-INF"))
            {
                String className = entry.getName()
                        .substring(
                                0,
                                entry.getName().lastIndexOf(".class"))
                        .replace('/', '.')
                        .replace('\\', '.');

                Class<?> moduleClass;

                try
                {
                    moduleClass = classLoader.loadClass(className);
                }
                catch (ClassNotFoundException e)
                {
                    LogManager.getLogger().warn(String.format(
                            "Class %s not found",
                            className));
                    continue;
                }

                if(ModuleEntry.class.isAssignableFrom(moduleClass))
                {
                    try
                    {
                        out.add((ModuleEntry) moduleClass.getConstructor().newInstance());
                    }
                    catch (InstantiationException
                            | IllegalAccessException
                            | InvocationTargetException
                            | NoSuchMethodException e)
                    {
                        LogManager.getLogger().warn(String.format(
                                "Failed to load module %s, caused by:\n%s",
                                className,
                                e.getMessage()));
                    }
                }
            }
        }

        return out;
    }
}
