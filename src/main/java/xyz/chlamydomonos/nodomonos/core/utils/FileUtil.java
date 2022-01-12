package xyz.chlamydomonos.nodomonos.core.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class FileUtil
{
    public static @NotNull ArrayList<File> getFilesInDir(@NotNull File dir)
    {
        ArrayList<File> out = new ArrayList<>();

        if(dir.isFile())
        {
            out.add(dir);
            return out;
        }


        else if(dir.isDirectory())
        {
            File[] files = dir.listFiles();
            if(files == null)
                return out;

            for(File i : files)
                out.addAll(getFilesInDir(i));
        }

        return out;
    }

    public static boolean inFormat(@NotNull File file, String format)
    {
        return file.getName().endsWith(format);
    }
}
