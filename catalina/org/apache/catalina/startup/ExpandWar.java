/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import org.apache.catalina.Host;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class ExpandWar {
    private static final Log log = LogFactory.getLog(ExpandWar.class);
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");

    public static String expand(Host host, URL war, String pathname) throws IOException {
        JarURLConnection juc = (JarURLConnection)war.openConnection();
        juc.setUseCaches(false);
        URL jarFileUrl = juc.getJarFileURL();
        URLConnection jfuc = jarFileUrl.openConnection();
        boolean success = false;
        File docBase = new File(host.getAppBaseFile(), pathname);
        File warTracker = new File(host.getAppBaseFile(), pathname + "/META-INF/war-tracker");
        long warLastModified = -1L;
        try (InputStream is = jfuc.getInputStream();){
            warLastModified = jfuc.getLastModified();
        }
        if (docBase.exists()) {
            if (!warTracker.exists() || warTracker.lastModified() == warLastModified) {
                success = true;
                return docBase.getAbsolutePath();
            }
            log.info((Object)sm.getString("expandWar.deleteOld", new Object[]{docBase}));
            if (!ExpandWar.delete(docBase)) {
                throw new IOException(sm.getString("expandWar.deleteFailed", new Object[]{docBase}));
            }
        }
        if (!docBase.mkdir() && !docBase.isDirectory()) {
            throw new IOException(sm.getString("expandWar.createFailed", new Object[]{docBase}));
        }
        Path canonicalDocBasePath = docBase.getCanonicalFile().toPath();
        File warTrackerParent = warTracker.getParentFile();
        if (!warTrackerParent.isDirectory() && !warTrackerParent.mkdirs()) {
            throw new IOException(sm.getString("expandWar.createFailed", new Object[]{warTrackerParent.getAbsolutePath()}));
        }
        try (JarFile jarFile = juc.getJarFile();){
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                File parent;
                JarEntry jarEntry = jarEntries.nextElement();
                String name = jarEntry.getName();
                File expandedFile = new File(docBase, name);
                if (!expandedFile.getCanonicalFile().toPath().startsWith(canonicalDocBasePath)) {
                    throw new IllegalArgumentException(sm.getString("expandWar.illegalPath", new Object[]{war, name, expandedFile.getCanonicalPath(), canonicalDocBasePath}));
                }
                int last = name.lastIndexOf(47);
                if (last >= 0 && !(parent = new File(docBase, name.substring(0, last))).mkdirs() && !parent.isDirectory()) {
                    throw new IOException(sm.getString("expandWar.createFailed", new Object[]{parent}));
                }
                if (name.endsWith("/")) continue;
                InputStream input = jarFile.getInputStream(jarEntry);
                try {
                    if (null == input) {
                        throw new ZipException(sm.getString("expandWar.missingJarEntry", new Object[]{jarEntry.getName()}));
                    }
                    ExpandWar.expand(input, expandedFile);
                    long lastModified = jarEntry.getTime();
                    if (lastModified == -1L || lastModified == 0L || expandedFile.setLastModified(lastModified)) continue;
                    throw new IOException(sm.getString("expandWar.lastModifiedFailed", new Object[]{expandedFile}));
                }
                finally {
                    if (input == null) continue;
                    input.close();
                }
            }
            if (!warTracker.createNewFile()) {
                throw new IOException(sm.getString("expandWar.createFileFailed", new Object[]{warTracker}));
            }
            if (!warTracker.setLastModified(warLastModified)) {
                throw new IOException(sm.getString("expandWar.lastModifiedFailed", new Object[]{warTracker}));
            }
            success = true;
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (!success) {
                ExpandWar.deleteDir(docBase);
            }
        }
        return docBase.getAbsolutePath();
    }

    public static void validate(Host host, URL war, String pathname) throws IOException {
        File docBase = new File(host.getAppBaseFile(), pathname);
        Path canonicalDocBasePath = docBase.getCanonicalFile().toPath();
        JarURLConnection juc = (JarURLConnection)war.openConnection();
        juc.setUseCaches(false);
        try (JarFile jarFile = juc.getJarFile();){
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String name = jarEntry.getName();
                File expandedFile = new File(docBase, name);
                if (expandedFile.getCanonicalFile().toPath().startsWith(canonicalDocBasePath)) continue;
                throw new IllegalArgumentException(sm.getString("expandWar.illegalPath", new Object[]{war, name, expandedFile.getCanonicalPath(), canonicalDocBasePath}));
            }
        }
    }

    public static boolean copy(File src, File dest) {
        boolean result = true;
        String[] files = null;
        if (src.isDirectory()) {
            files = src.list();
            result = dest.mkdir();
        } else {
            files = new String[]{""};
        }
        if (files == null) {
            files = new String[]{};
        }
        for (int i = 0; i < files.length && result; ++i) {
            File fileSrc = new File(src, files[i]);
            File fileDest = new File(dest, files[i]);
            if (fileSrc.isDirectory()) {
                result = ExpandWar.copy(fileSrc, fileDest);
                continue;
            }
            try (FileChannel ic = new FileInputStream(fileSrc).getChannel();
                 FileChannel oc = new FileOutputStream(fileDest).getChannel();){
                long count;
                long position = 0L;
                for (long size = ic.size(); size > 0L; size -= count) {
                    count = ic.transferTo(position, size, oc);
                    if (count > 0L) {
                        position += count;
                        continue;
                    }
                    throw new EOFException();
                }
                continue;
            }
            catch (IOException e) {
                log.error((Object)sm.getString("expandWar.copy", new Object[]{fileSrc, fileDest}), (Throwable)e);
                result = false;
            }
        }
        return result;
    }

    public static boolean delete(File dir) {
        return ExpandWar.delete(dir, true);
    }

    public static boolean delete(File dir, boolean logFailure) {
        boolean result = dir.isDirectory() ? ExpandWar.deleteDir(dir, logFailure) : (dir.exists() ? dir.delete() : true);
        if (logFailure && !result) {
            log.error((Object)sm.getString("expandWar.deleteFailed", new Object[]{dir.getAbsolutePath()}));
        }
        return result;
    }

    public static boolean deleteDir(File dir) {
        return ExpandWar.deleteDir(dir, true);
    }

    public static boolean deleteDir(File dir, boolean logFailure) {
        String[] files = dir.list();
        if (files == null) {
            files = new String[]{};
        }
        for (String s : files) {
            File file = new File(dir, s);
            if (file.isDirectory()) {
                ExpandWar.deleteDir(file, logFailure);
                continue;
            }
            file.delete();
        }
        boolean result = dir.exists() ? dir.delete() : true;
        if (logFailure && !result) {
            log.error((Object)sm.getString("expandWar.deleteFailed", new Object[]{dir.getAbsolutePath()}));
        }
        return result;
    }

    private static void expand(InputStream input, File file) throws IOException {
        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));){
            int n;
            byte[] buffer = new byte[2048];
            while ((n = input.read(buffer)) > 0) {
                output.write(buffer, 0, n);
            }
        }
    }
}

