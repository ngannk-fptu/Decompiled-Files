/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.ResourceSet;
import org.apache.catalina.webresources.AbstractFileResourceSet;
import org.apache.catalina.webresources.EmptyResource;
import org.apache.catalina.webresources.FileResource;
import org.apache.catalina.webresources.VirtualResource;

public class FileResourceSet
extends AbstractFileResourceSet {
    public FileResourceSet() {
        super("/");
    }

    public FileResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) {
        super(internalPath);
        this.setRoot(root);
        this.setWebAppMount(webAppMount);
        this.setBase(base);
        if (this.getRoot().getState().isAvailable()) {
            try {
                this.start();
            }
            catch (LifecycleException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public WebResource getResource(String path) {
        this.checkPath(path);
        String webAppMount = this.getWebAppMount();
        WebResourceRoot root = this.getRoot();
        if (path.equals(webAppMount)) {
            File f = this.file("", true);
            if (f == null) {
                return new EmptyResource(root, path);
            }
            return new FileResource(root, path, f, this.isReadOnly(), null);
        }
        if (path.charAt(path.length() - 1) != '/') {
            path = path + '/';
        }
        if (webAppMount.startsWith(path)) {
            String name = path.substring(0, path.length() - 1);
            if ((name = name.substring(name.lastIndexOf(47) + 1)).length() > 0) {
                return new VirtualResource(root, path, name);
            }
        }
        return new EmptyResource(root, path);
    }

    @Override
    public String[] list(String path) {
        String webAppMount;
        this.checkPath(path);
        if (path.charAt(path.length() - 1) != '/') {
            path = path + '/';
        }
        if ((webAppMount = this.getWebAppMount()).startsWith(path)) {
            if ((webAppMount = webAppMount.substring(path.length())).equals(this.getFileBase().getName())) {
                return new String[]{this.getFileBase().getName()};
            }
            int i = webAppMount.indexOf(47);
            if (i > 0) {
                return new String[]{webAppMount.substring(0, i)};
            }
        }
        return EMPTY_STRING_ARRAY;
    }

    @Override
    public Set<String> listWebAppPaths(String path) {
        String webAppMount;
        this.checkPath(path);
        ResourceSet<String> result = new ResourceSet<String>();
        if (path.charAt(path.length() - 1) != '/') {
            path = path + '/';
        }
        if ((webAppMount = this.getWebAppMount()).startsWith(path)) {
            if ((webAppMount = webAppMount.substring(path.length())).equals(this.getFileBase().getName())) {
                result.add(path + this.getFileBase().getName());
            } else {
                int i = webAppMount.indexOf(47);
                if (i > 0) {
                    result.add(path + webAppMount.substring(0, i + 1));
                }
            }
        }
        result.setLocked(true);
        return result;
    }

    @Override
    public boolean mkdir(String path) {
        this.checkPath(path);
        return false;
    }

    @Override
    public boolean write(String path, InputStream is, boolean overwrite) {
        this.checkPath(path);
        return false;
    }

    @Override
    protected void checkType(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException(sm.getString("fileResourceSet.notFile", new Object[]{this.getBase(), File.separator, this.getInternalPath()}));
        }
    }
}

