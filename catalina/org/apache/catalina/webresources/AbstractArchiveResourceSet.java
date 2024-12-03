/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.ResourceSet;
import org.apache.catalina.webresources.AbstractResourceSet;
import org.apache.catalina.webresources.EmptyResource;
import org.apache.catalina.webresources.JarContents;
import org.apache.catalina.webresources.JarResourceRoot;
import org.apache.tomcat.util.compat.JreCompat;

public abstract class AbstractArchiveResourceSet
extends AbstractResourceSet {
    private URL baseUrl;
    private String baseUrlString;
    private JarFile archive = null;
    protected Map<String, JarEntry> archiveEntries = null;
    protected final Object archiveLock = new Object();
    private long archiveUseCount = 0L;
    private JarContents jarContents;
    private boolean retainBloomFilterForArchives = false;

    protected final void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
        this.baseUrlString = baseUrl == null ? null : baseUrl.toString();
    }

    @Override
    public final URL getBaseUrl() {
        return this.baseUrl;
    }

    protected final String getBaseUrlString() {
        return this.baseUrlString;
    }

    @Override
    public final String[] list(String path) {
        this.checkPath(path);
        String webAppMount = this.getWebAppMount();
        ArrayList<String> result = new ArrayList<String>();
        if (path.startsWith(webAppMount)) {
            String pathInJar = this.getInternalPath() + path.substring(webAppMount.length());
            if (pathInJar.length() > 0 && pathInJar.charAt(0) == '/') {
                pathInJar = pathInJar.substring(1);
            }
            for (String name : this.getArchiveEntries(false).keySet()) {
                if (name.length() <= pathInJar.length() || !name.startsWith(pathInJar) || (name = name.charAt(name.length() - 1) == '/' ? name.substring(pathInJar.length(), name.length() - 1) : name.substring(pathInJar.length())).length() == 0) continue;
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                if (name.length() <= 0 || name.lastIndexOf(47) != -1) continue;
                result.add(name);
            }
        } else {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (webAppMount.startsWith(path)) {
                int i = webAppMount.indexOf(47, path.length());
                if (i == -1) {
                    return new String[]{webAppMount.substring(path.length())};
                }
                return new String[]{webAppMount.substring(path.length(), i)};
            }
        }
        return result.toArray(new String[0]);
    }

    @Override
    public final Set<String> listWebAppPaths(String path) {
        this.checkPath(path);
        String webAppMount = this.getWebAppMount();
        ResourceSet<String> result = new ResourceSet<String>();
        if (path.startsWith(webAppMount)) {
            String pathInJar = this.getInternalPath() + path.substring(webAppMount.length());
            if (pathInJar.length() > 0) {
                if (pathInJar.charAt(pathInJar.length() - 1) != '/') {
                    pathInJar = pathInJar.substring(1) + '/';
                }
                if (pathInJar.charAt(0) == '/') {
                    pathInJar = pathInJar.substring(1);
                }
            }
            for (String name : this.getArchiveEntries(false).keySet()) {
                if (name.length() <= pathInJar.length() || !name.startsWith(pathInJar)) continue;
                int nextSlash = name.indexOf(47, pathInJar.length());
                if (nextSlash != -1 && nextSlash != name.length() - 1) {
                    name = name.substring(0, nextSlash + 1);
                }
                result.add(webAppMount + '/' + name.substring(this.getInternalPath().length()));
            }
        } else {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (webAppMount.startsWith(path)) {
                int i = webAppMount.indexOf(47, path.length());
                if (i == -1) {
                    result.add(webAppMount + "/");
                } else {
                    result.add(webAppMount.substring(0, i + 1));
                }
            }
        }
        result.setLocked(true);
        return result;
    }

    protected abstract Map<String, JarEntry> getArchiveEntries(boolean var1);

    protected abstract JarEntry getArchiveEntry(String var1);

    @Override
    public final boolean mkdir(String path) {
        this.checkPath(path);
        return false;
    }

    @Override
    public final boolean write(String path, InputStream is, boolean overwrite) {
        this.checkPath(path);
        if (is == null) {
            throw new NullPointerException(sm.getString("dirResourceSet.writeNpe"));
        }
        return false;
    }

    @Override
    public final WebResource getResource(String path) {
        this.checkPath(path);
        String webAppMount = this.getWebAppMount();
        WebResourceRoot root = this.getRoot();
        if (this.jarContents != null && !this.jarContents.mightContainResource(path, webAppMount)) {
            return new EmptyResource(root, path);
        }
        if (path.startsWith(webAppMount)) {
            String pathInJar = this.getInternalPath() + path.substring(webAppMount.length());
            if (pathInJar.length() > 0 && pathInJar.charAt(0) == '/') {
                pathInJar = pathInJar.substring(1);
            }
            if (pathInJar.equals("")) {
                if (!path.endsWith("/")) {
                    path = path + "/";
                }
                return new JarResourceRoot(root, new File(this.getBase()), this.baseUrlString, path);
            }
            JarEntry jarEntry = null;
            if (this.isMultiRelease()) {
                jarEntry = this.getArchiveEntry(pathInJar);
            } else {
                Map<String, JarEntry> jarEntries = this.getArchiveEntries(true);
                if (pathInJar.charAt(pathInJar.length() - 1) != '/' && (jarEntry = jarEntries == null ? this.getArchiveEntry(pathInJar + '/') : jarEntries.get(pathInJar + '/')) != null) {
                    path = path + '/';
                }
                if (jarEntry == null) {
                    jarEntry = jarEntries == null ? this.getArchiveEntry(pathInJar) : jarEntries.get(pathInJar);
                }
            }
            if (jarEntry == null) {
                return new EmptyResource(root, path);
            }
            return this.createArchiveResource(jarEntry, path, this.getManifest());
        }
        return new EmptyResource(root, path);
    }

    protected abstract boolean isMultiRelease();

    protected abstract WebResource createArchiveResource(JarEntry var1, String var2, Manifest var3);

    @Override
    public final boolean isReadOnly() {
        return true;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            return;
        }
        throw new IllegalArgumentException(sm.getString("abstractArchiveResourceSet.setReadOnlyFalse"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected JarFile openJarFile() throws IOException {
        Object object = this.archiveLock;
        synchronized (object) {
            if (this.archive == null) {
                this.archive = JreCompat.getInstance().jarFileNewInstance(this.getBase());
                WebResourceRoot root = this.getRoot();
                if (root.getArchiveIndexStrategyEnum().getUsesBloom() || root.getContext() != null && root.getContext().getUseBloomFilterForArchives()) {
                    this.jarContents = new JarContents(this.archive);
                    this.retainBloomFilterForArchives = root.getArchiveIndexStrategyEnum().getRetain();
                }
            }
            ++this.archiveUseCount;
            return this.archive;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeJarFile() {
        Object object = this.archiveLock;
        synchronized (object) {
            --this.archiveUseCount;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void gc() {
        Object object = this.archiveLock;
        synchronized (object) {
            if (this.archive != null && this.archiveUseCount == 0L) {
                try {
                    this.archive.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.archive = null;
                this.archiveEntries = null;
                if (!this.retainBloomFilterForArchives) {
                    this.jarContents = null;
                }
            }
        }
    }
}

