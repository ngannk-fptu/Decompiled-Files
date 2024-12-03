/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarFileResource
extends JarResource {
    private static final Logger LOG = LoggerFactory.getLogger(JarFileResource.class);
    private JarFile _jarFile;
    private File _file;
    private String[] _list;
    private JarEntry _entry;
    private boolean _directory;
    private String _jarUrl;
    private String _path;
    private boolean _exists;

    protected JarFileResource(URL url, boolean useCaches) {
        super(url, useCaches);
    }

    @Override
    public void close() {
        try (AutoLock l = this._lock.lock();){
            this._exists = false;
            this._list = null;
            this._entry = null;
            this._file = null;
            if (!this.getUseCaches() && this._jarFile != null) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Closing JarFile {}", (Object)this._jarFile.getName());
                    }
                    this._jarFile.close();
                }
                catch (IOException ioe) {
                    LOG.trace("IGNORED", (Throwable)ioe);
                }
            }
            this._jarFile = null;
            super.close();
        }
    }

    @Override
    protected boolean checkConnection() {
        try (AutoLock l = this._lock.lock();){
            try {
                super.checkConnection();
            }
            finally {
                if (this._jarConnection == null) {
                    this._entry = null;
                    this._file = null;
                    this._jarFile = null;
                    this._list = null;
                }
            }
            boolean bl = this._jarFile != null;
            return bl;
        }
    }

    @Override
    protected void newConnection() throws IOException {
        try (AutoLock l = this._lock.lock();){
            super.newConnection();
            this._entry = null;
            this._file = null;
            this._jarFile = null;
            this._list = null;
            int sep = this._urlString.lastIndexOf("!/");
            this._jarUrl = this._urlString.substring(0, sep + 2);
            this._path = URIUtil.decodePath(this._urlString.substring(sep + 2));
            if (this._path.length() == 0) {
                this._path = null;
            }
            this._jarFile = this._jarConnection.getJarFile();
            this._file = new File(this._jarFile.getName());
        }
    }

    @Override
    public boolean exists() {
        if (this._exists) {
            return true;
        }
        if (this._urlString.endsWith("!/")) {
            String fileUrl = this._urlString.substring(4, this._urlString.length() - 2);
            try {
                this._directory = JarFileResource.newResource(fileUrl).exists();
                return this._directory;
            }
            catch (Exception e) {
                LOG.trace("IGNORED", (Throwable)e);
                return false;
            }
        }
        boolean check = this.checkConnection();
        if (this._jarUrl != null && this._path == null) {
            this._directory = check;
            return true;
        }
        boolean closeJarFile = false;
        JarFile jarFile = null;
        if (check) {
            jarFile = this._jarFile;
        } else {
            try {
                JarURLConnection c = (JarURLConnection)new URL(this._jarUrl).openConnection();
                c.setUseCaches(this.getUseCaches());
                jarFile = c.getJarFile();
                closeJarFile = !this.getUseCaches();
            }
            catch (Exception e) {
                LOG.trace("IGNORED", (Throwable)e);
            }
        }
        if (jarFile != null && this._entry == null && !this._directory) {
            JarEntry entry = jarFile.getJarEntry(this._path);
            if (entry == null) {
                this._exists = false;
            } else if (entry.isDirectory()) {
                this._directory = true;
                this._entry = entry;
            } else {
                JarEntry directory = jarFile.getJarEntry(this._path + "/");
                if (directory != null) {
                    this._directory = true;
                    this._entry = directory;
                } else {
                    this._directory = false;
                    this._entry = entry;
                }
            }
        }
        if (closeJarFile && jarFile != null) {
            try {
                jarFile.close();
            }
            catch (IOException ioe) {
                LOG.trace("IGNORED", (Throwable)ioe);
            }
        }
        this._exists = this._directory || this._entry != null;
        return this._exists;
    }

    @Override
    public boolean isDirectory() {
        return this.exists() && this._directory;
    }

    @Override
    public long lastModified() {
        if (this.checkConnection() && this._file != null) {
            if (this.exists() && this._entry != null) {
                return this._entry.getTime();
            }
            return this._file.lastModified();
        }
        return -1L;
    }

    @Override
    public String[] list() {
        try (AutoLock l = this._lock.lock();){
            if (this.isDirectory() && this._list == null) {
                List<String> list = null;
                try {
                    list = this.listEntries();
                }
                catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.warn("JarFile list failure", (Throwable)e);
                    } else {
                        LOG.warn("JarFile list failure {}", (Object)e.toString());
                    }
                    this.close();
                    list = this.listEntries();
                }
                if (list != null) {
                    this._list = new String[list.size()];
                    list.toArray(this._list);
                }
            }
            String[] stringArray = this._list;
            return stringArray;
        }
    }

    private List<String> listEntries() {
        this.checkConnection();
        ArrayList<String> list = new ArrayList<String>(32);
        JarFile jarFile = this._jarFile;
        if (jarFile == null) {
            try {
                JarURLConnection jc = (JarURLConnection)new URL(this._jarUrl).openConnection();
                jc.setUseCaches(this.getUseCaches());
                jarFile = jc.getJarFile();
            }
            catch (Exception e) {
                e.printStackTrace();
                LOG.trace("IGNORED", (Throwable)e);
            }
            if (jarFile == null) {
                throw new IllegalStateException();
            }
        }
        Enumeration<JarEntry> e = jarFile.entries();
        String encodedDir = this._urlString.substring(this._urlString.lastIndexOf("!/") + 2);
        String dir = URIUtil.decodePath(encodedDir);
        while (e.hasMoreElements()) {
            String listName;
            int dash;
            JarEntry entry = e.nextElement();
            String name = entry.getName();
            if (!name.startsWith(dir) || name.length() == dir.length() || (dash = (listName = name.substring(dir.length())).indexOf(47)) >= 0 && (dash == 0 && listName.length() == 1 || list.contains(listName = dash == 0 ? listName.substring(dash + 1, listName.length()) : listName.substring(0, dash + 1)))) continue;
            list.add(listName);
        }
        return list;
    }

    @Override
    public long length() {
        if (this.isDirectory()) {
            return -1L;
        }
        if (this._entry != null) {
            return this._entry.getSize();
        }
        return -1L;
    }

    @Override
    public boolean isContainedIn(Resource resource) throws MalformedURLException {
        String string = this._urlString;
        int index = string.lastIndexOf("!/");
        if (index > 0) {
            string = string.substring(0, index);
        }
        if (string.startsWith("jar:")) {
            string = string.substring(4);
        }
        URL url = new URL(string);
        return url.sameFile(resource.getURI().toURL());
    }

    public File getJarFile() {
        if (this._file != null) {
            return this._file;
        }
        return null;
    }
}

