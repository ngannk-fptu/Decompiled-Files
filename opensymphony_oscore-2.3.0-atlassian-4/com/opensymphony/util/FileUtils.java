/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtils {
    private static final Log log = LogFactory.getLog(FileUtils.class);

    public static final InputStream getResource(String uri) throws IOException {
        try {
            return new URL(uri).openStream();
        }
        catch (MalformedURLException mue) {
            return new FileInputStream(uri);
        }
    }

    public static final File checkBackupDirectory(File file) {
        File backupDirectory = new File(file.getParent() + System.getProperty("file.separator") + "osedit_backup");
        if (!backupDirectory.exists()) {
            backupDirectory.mkdirs();
        }
        return backupDirectory;
    }

    public static final File createFile(String path) {
        File file = new File(path);
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            log.error((Object)e);
        }
        return file;
    }

    public static final String[] dirList(String path) {
        return FileUtils.dirList(new File(path));
    }

    public static final String[] dirList(File path) {
        String[] list = path.list();
        return list;
    }

    public static final String readFile(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String s = new String();
            StringBuffer buffer = new StringBuffer();
            while ((s = in.readLine()) != null) {
                buffer.append(s + "\n");
            }
            in.close();
            return buffer.toString();
        }
        catch (FileNotFoundException e) {
            log.warn((Object)"File not found");
        }
        catch (IOException e) {
            log.error((Object)e);
        }
        return null;
    }

    public static final void write(File file, String content) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(content);
            out.close();
        }
        catch (FileNotFoundException e) {
            log.warn((Object)"File not found", (Throwable)e);
        }
        catch (IOException e) {
            log.error((Object)e);
        }
    }

    public static final void writeAndBackup(File file, String content) {
        try {
            SimpleDateFormat backupDF = new SimpleDateFormat("ddMMyy_hhmmss");
            File backupDirectory = FileUtils.checkBackupDirectory(file);
            File original = new File(file.getAbsolutePath());
            File backup = new File(backupDirectory, original.getName() + "." + backupDF.format(new Date()));
            if (log.isDebugEnabled()) {
                log.debug((Object)("Backup file is " + backup));
            }
            original.renameTo(backup);
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(content);
            out.close();
        }
        catch (FileNotFoundException e) {
            log.warn((Object)"File not found", (Throwable)e);
        }
        catch (IOException e) {
            log.error((Object)e);
        }
    }
}

