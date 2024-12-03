/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

public class JarHelper {
    private static final int BUFFER_SIZE = 2156;
    private final byte[] mBuffer = new byte[2156];
    private boolean mVerbose = false;
    private String mDestJarName = "";
    private static final char SEP = '/';

    public void jarDir(File dirOrFile2Jar, File destJar) throws IOException {
        if (dirOrFile2Jar == null || destJar == null) {
            throw new IllegalArgumentException();
        }
        this.mDestJarName = destJar.getCanonicalPath();
        try (FileOutputStream fout = new FileOutputStream(destJar);
             JarOutputStream jout = new JarOutputStream(fout);){
            this.jarDir(dirOrFile2Jar, jout, null);
        }
    }

    public void unjarDir(File jarFile, File destDir) throws IOException {
        try (FileInputStream fis = new FileInputStream(jarFile);){
            this.unjar(fis, destDir);
        }
    }

    public void unjar(InputStream in, File destDir) throws IOException {
        try (JarInputStream jis = new JarInputStream(in);){
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    File dir = new File(destDir, entry.getName());
                    if (!dir.getCanonicalFile().toPath().startsWith(destDir.getCanonicalFile().toPath())) {
                        throw new IOException("Entry is outside of the target directory " + entry.getName());
                    }
                    dir.mkdir();
                    if (entry.getTime() == -1L) continue;
                    dir.setLastModified(entry.getTime());
                    continue;
                }
                byte[] data = new byte[2156];
                File destFile = new File(destDir, entry.getName());
                if (!destFile.getCanonicalFile().toPath().startsWith(destDir.getCanonicalFile().toPath())) {
                    throw new IOException("Entry is outside of the target directory: " + entry.getName());
                }
                if (this.mVerbose) {
                    System.out.println("unjarring " + destFile + " from " + entry.getName());
                }
                try (FileOutputStream fos = new FileOutputStream(destFile);
                     BufferedOutputStream dest = new BufferedOutputStream(fos, 2156);){
                    int count;
                    while ((count = jis.read(data, 0, 2156)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                }
                if (entry.getTime() == -1L) continue;
                destFile.setLastModified(entry.getTime());
            }
        }
    }

    public void setVerbose(boolean b) {
        this.mVerbose = b;
    }

    private void jarDir(File dirOrFile2jar, JarOutputStream jos, String path) throws IOException {
        block20: {
            block19: {
                String subPath;
                if (this.mVerbose) {
                    System.out.println("checking " + dirOrFile2jar);
                }
                if (!dirOrFile2jar.isDirectory()) break block19;
                String[] dirList = dirOrFile2jar.list();
                String string = subPath = path == null ? "" : path + dirOrFile2jar.getName() + '/';
                if (path != null) {
                    JarEntry je = new JarEntry(subPath);
                    je.setTime(dirOrFile2jar.lastModified());
                    jos.putNextEntry(je);
                    jos.flush();
                    jos.closeEntry();
                }
                if (dirList == null) break block20;
                for (String s : dirList) {
                    File f = new File(dirOrFile2jar, s);
                    this.jarDir(f, jos, subPath);
                }
                break block20;
            }
            if (dirOrFile2jar.getCanonicalPath().equals(this.mDestJarName)) {
                if (this.mVerbose) {
                    System.out.println("skipping " + dirOrFile2jar.getPath());
                }
                return;
            }
            if (this.mVerbose) {
                System.out.println("adding " + dirOrFile2jar.getPath());
            }
            try (FileInputStream fis = new FileInputStream(dirOrFile2jar);){
                int mByteCount;
                JarEntry entry = new JarEntry(path + dirOrFile2jar.getName());
                entry.setTime(dirOrFile2jar.lastModified());
                jos.putNextEntry(entry);
                while ((mByteCount = fis.read(this.mBuffer)) != -1) {
                    jos.write(this.mBuffer, 0, mByteCount);
                    if (!this.mVerbose) continue;
                    System.out.println("wrote " + mByteCount + " bytes");
                }
                jos.flush();
                jos.closeEntry();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: JarHelper jarname.jar directory");
            return;
        }
        JarHelper jarHelper = new JarHelper();
        jarHelper.mVerbose = true;
        File destJar = new File(args[0]);
        File dirOrFile2Jar = new File(args[1]);
        jarHelper.jarDir(dirOrFile2Jar, destJar);
    }
}

