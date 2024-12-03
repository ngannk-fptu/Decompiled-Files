/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.plugins.diskpersistence;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDiskPersistenceListener
implements PersistenceListener,
Serializable {
    public static final String CACHE_PATH_KEY = "cache.path";
    protected static final String CACHE_EXTENSION = "cache";
    protected static final String GROUP_DIRECTORY = "__groups__";
    protected static final String APPLICATION_CACHE_SUBPATH = "application";
    protected static final String SESSION_CACHE_SUBPATH = "session";
    protected static final String CONTEXT_TMPDIR = "javax.servlet.context.tempdir";
    private static final transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$plugins$diskpersistence$AbstractDiskPersistenceListener == null ? (class$com$opensymphony$oscache$plugins$diskpersistence$AbstractDiskPersistenceListener = AbstractDiskPersistenceListener.class$("com.opensymphony.oscache.plugins.diskpersistence.AbstractDiskPersistenceListener")) : class$com$opensymphony$oscache$plugins$diskpersistence$AbstractDiskPersistenceListener));
    private File cachePath = null;
    private File contextTmpDir;
    private String root = null;
    static /* synthetic */ Class class$com$opensymphony$oscache$plugins$diskpersistence$AbstractDiskPersistenceListener;

    public File getCachePath() {
        return this.cachePath;
    }

    public String getRoot() {
        return this.root;
    }

    public File getContextTmpDir() {
        return this.contextTmpDir;
    }

    public boolean isGroupStored(String group) throws CachePersistenceException {
        try {
            File file = this.getCacheGroupFile(group);
            return file.exists();
        }
        catch (Exception e) {
            throw new CachePersistenceException("Unable verify group '" + group + "' exists in the cache: " + e);
        }
    }

    public boolean isStored(String key) throws CachePersistenceException {
        try {
            File file = this.getCacheFile(key);
            return file.exists();
        }
        catch (Exception e) {
            throw new CachePersistenceException("Unable verify id '" + key + "' is stored in the cache: " + e);
        }
    }

    public void clear() throws CachePersistenceException {
        this.clear(this.root);
    }

    public PersistenceListener configure(Config config) {
        String sessionId = null;
        int scope = 0;
        this.initFileCaching(config.getProperty(CACHE_PATH_KEY));
        if (config.getProperty("sessionId") != null) {
            sessionId = config.getProperty("sessionId");
        }
        if (config.getProperty("scope") != null) {
            scope = Integer.parseInt(config.getProperty("scope"));
        }
        StringBuffer root = new StringBuffer(this.getCachePath().getPath());
        root.append("/");
        root.append(this.getPathPart(scope));
        if (sessionId != null && sessionId.length() > 0) {
            root.append("/");
            root.append(sessionId);
        }
        this.root = root.toString();
        this.contextTmpDir = (File)config.get("context.tempdir");
        return this;
    }

    public void remove(String key) throws CachePersistenceException {
        File file = this.getCacheFile(key);
        this.remove(file);
    }

    public void removeGroup(String groupName) throws CachePersistenceException {
        File file = this.getCacheGroupFile(groupName);
        this.remove(file);
    }

    public Object retrieve(String key) throws CachePersistenceException {
        return this.retrieve(this.getCacheFile(key));
    }

    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        File groupFile = this.getCacheGroupFile(groupName);
        try {
            return (Set)this.retrieve(groupFile);
        }
        catch (ClassCastException e) {
            throw new CachePersistenceException("Group file " + groupFile + " was not persisted as a Set: " + e);
        }
    }

    public void store(String key, Object obj) throws CachePersistenceException {
        File file = this.getCacheFile(key);
        this.store(file, obj);
    }

    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        File groupFile = this.getCacheGroupFile(groupName);
        this.store(groupFile, (Object)group);
    }

    protected String adjustFileCachePath(String cachePathStr) {
        if (cachePathStr.compareToIgnoreCase(CONTEXT_TMPDIR) == 0) {
            cachePathStr = this.contextTmpDir.getAbsolutePath();
        }
        return cachePathStr;
    }

    protected void initFileCaching(String cachePathStr) {
        if (cachePathStr != null) {
            this.cachePath = new File(cachePathStr);
            try {
                if (!this.cachePath.exists()) {
                    if (log.isInfoEnabled()) {
                        log.info((Object)("cache.path '" + cachePathStr + "' does not exist, creating"));
                    }
                    this.cachePath.mkdirs();
                }
                if (!this.cachePath.isDirectory()) {
                    log.error((Object)("cache.path '" + cachePathStr + "' is not a directory"));
                    this.cachePath = null;
                } else if (!this.cachePath.canWrite()) {
                    log.error((Object)("cache.path '" + cachePathStr + "' is not a writable location"));
                    this.cachePath = null;
                }
            }
            catch (Exception e) {
                log.error((Object)("cache.path '" + cachePathStr + "' could not be used"), (Throwable)e);
                this.cachePath = null;
            }
        }
    }

    protected void remove(File file) throws CachePersistenceException {
        try {
            while (!file.delete() && file.exists()) {
            }
        }
        catch (Exception e) {
            throw new CachePersistenceException("Unable to remove '" + file + "' from the cache: " + e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void store(File file, Object obj) throws CachePersistenceException {
        File filepath = new File(file.getParent());
        try {
            if (!filepath.exists()) {
                filepath.mkdirs();
            }
        }
        catch (Exception e) {
            throw new CachePersistenceException("Unable to create the directory " + filepath);
        }
        while (file.exists() && !file.delete()) {
        }
        FileOutputStream fout = null;
        ObjectOutputStream oout = null;
        try {
            fout = new FileOutputStream(file);
            try {
                oout = new ObjectOutputStream(fout);
                try {
                    oout.writeObject(obj);
                    oout.flush();
                }
                finally {
                    try {
                        oout.close();
                    }
                    catch (Exception e) {}
                }
            }
            finally {
                try {
                    fout.close();
                }
                catch (Exception e) {}
            }
        }
        catch (Exception e) {
            while (file.exists() && !file.delete()) {
            }
            throw new CachePersistenceException("Unable to write '" + file + "' in the cache. Exception: " + e.getClass().getName() + ", Message: " + e.getMessage());
        }
    }

    protected File getCacheFile(String key) {
        char[] fileChars = this.getCacheFileName(key);
        File file = new File(this.root, new String(fileChars) + "." + CACHE_EXTENSION);
        return file;
    }

    protected abstract char[] getCacheFileName(String var1);

    private File getCacheGroupFile(String group) {
        int AVERAGE_PATH_LENGTH = 30;
        if (group == null || group.length() == 0) {
            throw new IllegalArgumentException("Invalid group '" + group + "' specified to getCacheGroupFile.");
        }
        StringBuffer path = new StringBuffer(AVERAGE_PATH_LENGTH);
        path.append(GROUP_DIRECTORY).append('/');
        path.append(group).append('.').append(CACHE_EXTENSION);
        return new File(this.root, path.toString());
    }

    private String getPathPart(int scope) {
        if (scope == 3) {
            return SESSION_CACHE_SUBPATH;
        }
        return APPLICATION_CACHE_SUBPATH;
    }

    private void clear(String baseDirName) throws CachePersistenceException {
        File baseDir = new File(baseDirName);
        File[] fileList = baseDir.listFiles();
        try {
            if (fileList != null) {
                for (int count = 0; count < fileList.length; ++count) {
                    if (fileList[count].isFile()) {
                        fileList[count].delete();
                        continue;
                    }
                    this.clear(fileList[count].toString());
                    fileList[count].delete();
                }
            }
            baseDir.delete();
        }
        catch (Exception e) {
            throw new CachePersistenceException("Unable to clear the cache directory");
        }
    }

    private Object retrieve(File file) throws CachePersistenceException {
        boolean fileExist;
        Object readContent = null;
        try {
            fileExist = file.exists();
        }
        catch (Exception e) {
            throw new CachePersistenceException("Unable to verify if " + file + " exists: " + e);
        }
        if (fileExist) {
            BufferedInputStream in = null;
            ObjectInputStream oin = null;
            try {
                in = new BufferedInputStream(new FileInputStream(file));
                oin = new ObjectInputStream(in);
                readContent = oin.readObject();
            }
            catch (Exception e) {
                throw new CachePersistenceException("Unable to read '" + file.getAbsolutePath() + "' from the cache: " + e);
            }
            finally {
                try {
                    oin.close();
                }
                catch (Exception ex) {}
                try {
                    in.close();
                }
                catch (Exception ex) {}
            }
        }
        return readContent;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

