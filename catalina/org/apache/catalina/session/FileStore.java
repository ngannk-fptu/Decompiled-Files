/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.session;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StoreBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public final class FileStore
extends StoreBase {
    private static final Log log = LogFactory.getLog(FileStore.class);
    private static final StringManager sm = StringManager.getManager(FileStore.class);
    private static final String FILE_EXT = ".session";
    private String directory = ".";
    private File directoryFile = null;
    private static final String storeName = "fileStore";
    private static final String threadName = "FileStore";

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String path) {
        String oldDirectory = this.directory;
        this.directory = path;
        this.directoryFile = null;
        this.support.firePropertyChange("directory", oldDirectory, this.directory);
    }

    public String getThreadName() {
        return threadName;
    }

    @Override
    public String getStoreName() {
        return storeName;
    }

    @Override
    public int getSize() throws IOException {
        File dir = this.directory();
        if (dir == null) {
            return 0;
        }
        String[] files = dir.list();
        int keycount = 0;
        if (files != null) {
            for (String file : files) {
                if (!file.endsWith(FILE_EXT)) continue;
                ++keycount;
            }
        }
        return keycount;
    }

    @Override
    public void clear() throws IOException {
        String[] keys;
        for (String key : keys = this.keys()) {
            this.remove(key);
        }
    }

    @Override
    public String[] keys() throws IOException {
        File dir = this.directory();
        if (dir == null) {
            return new String[0];
        }
        String[] files = dir.list();
        if (files == null || files.length < 1) {
            return new String[0];
        }
        ArrayList<String> list = new ArrayList<String>();
        int n = FILE_EXT.length();
        for (String file : files) {
            if (!file.endsWith(FILE_EXT)) continue;
            list.add(file.substring(0, file.length() - n));
        }
        return list.toArray(new String[0]);
    }

    /*
     * Exception decompiling
     */
    @Override
    public Session load(String id) throws ClassNotFoundException, IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void remove(String id) throws IOException {
        File file = this.file(id);
        if (file == null) {
            return;
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)sm.getString(this.getStoreName() + ".removing", new Object[]{id, file.getAbsolutePath()}));
        }
        if (file.exists() && !file.delete()) {
            throw new IOException(sm.getString("fileStore.deleteSessionFailed", new Object[]{file}));
        }
    }

    @Override
    public void save(Session session) throws IOException {
        File file = this.file(session.getIdInternal());
        if (file == null) {
            return;
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)sm.getString(this.getStoreName() + ".saving", new Object[]{session.getIdInternal(), file.getAbsolutePath()}));
        }
        try (FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos));){
            ((StandardSession)session).writeObjectData(oos);
        }
    }

    private File directory() throws IOException {
        if (this.directory == null) {
            return null;
        }
        if (this.directoryFile != null) {
            return this.directoryFile;
        }
        File file = new File(this.directory);
        if (!file.isAbsolute()) {
            Context context = this.manager.getContext();
            ServletContext servletContext = context.getServletContext();
            File work = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
            file = new File(work, this.directory);
        }
        if (!file.exists() || !file.isDirectory()) {
            if (!file.delete() && file.exists()) {
                throw new IOException(sm.getString("fileStore.deleteFailed", new Object[]{file}));
            }
            if (!file.mkdirs() && !file.isDirectory()) {
                throw new IOException(sm.getString("fileStore.createFailed", new Object[]{file}));
            }
        }
        this.directoryFile = file;
        return file;
    }

    private File file(String id) throws IOException {
        File storageDir = this.directory();
        if (storageDir == null) {
            return null;
        }
        String filename = id + FILE_EXT;
        File file = new File(storageDir, filename);
        File canonicalFile = file.getCanonicalFile();
        if (!canonicalFile.toPath().startsWith(storageDir.getCanonicalFile().toPath())) {
            log.warn((Object)sm.getString("fileStore.invalid", new Object[]{file.getPath(), id}));
            return null;
        }
        return canonicalFile;
    }
}

