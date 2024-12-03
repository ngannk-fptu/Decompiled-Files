/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.jar.Manifest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.ResourceSet;
import org.apache.catalina.webresources.AbstractFileResourceSet;
import org.apache.catalina.webresources.EmptyResource;
import org.apache.catalina.webresources.FileResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class DirResourceSet
extends AbstractFileResourceSet {
    private static final Log log = LogFactory.getLog(DirResourceSet.class);

    public DirResourceSet() {
        super("/");
    }

    public DirResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) {
        super(internalPath);
        this.setRoot(root);
        this.setWebAppMount(webAppMount);
        this.setBase(base);
        if (root.getContext().getAddWebinfClassesResources()) {
            File f = new File(base, internalPath);
            if ((f = new File(f, "/WEB-INF/classes/META-INF/resources")).isDirectory()) {
                root.createWebResourceSet(WebResourceRoot.ResourceSetType.RESOURCE_JAR, "/", f.getAbsolutePath(), null, "/");
            }
        }
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
        if (path.startsWith(webAppMount)) {
            File f = this.file(path.substring(webAppMount.length()), false);
            if (f == null) {
                return new EmptyResource(root, path);
            }
            if (!f.exists()) {
                return new EmptyResource(root, path, f);
            }
            if (f.isDirectory() && path.charAt(path.length() - 1) != '/') {
                path = path + '/';
            }
            return new FileResource(root, path, f, this.isReadOnly(), this.getManifest());
        }
        return new EmptyResource(root, path);
    }

    @Override
    public String[] list(String path) {
        this.checkPath(path);
        String webAppMount = this.getWebAppMount();
        if (path.startsWith(webAppMount)) {
            File f = this.file(path.substring(webAppMount.length()), true);
            if (f == null) {
                return EMPTY_STRING_ARRAY;
            }
            String[] result = f.list();
            if (result == null) {
                return EMPTY_STRING_ARRAY;
            }
            return result;
        }
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
        return EMPTY_STRING_ARRAY;
    }

    @Override
    public Set<String> listWebAppPaths(String path) {
        this.checkPath(path);
        String webAppMount = this.getWebAppMount();
        ResourceSet<String> result = new ResourceSet<String>();
        if (path.startsWith(webAppMount)) {
            File[] list;
            File f = this.file(path.substring(webAppMount.length()), true);
            if (f != null && (list = f.listFiles()) != null) {
                for (File entry : list) {
                    if (!this.getRoot().getAllowLinking()) {
                        boolean symlink = true;
                        String absPath = null;
                        String canPath = null;
                        try {
                            absPath = entry.getAbsolutePath().substring(f.getAbsolutePath().length());
                            String entryCanPath = entry.getCanonicalPath();
                            String fCanPath = f.getCanonicalPath();
                            if (entryCanPath.length() >= fCanPath.length() && absPath.equals(canPath = entryCanPath.substring(fCanPath.length()))) {
                                symlink = false;
                            }
                        }
                        catch (IOException ioe) {
                            canPath = "Unknown";
                        }
                        if (symlink) {
                            this.logIgnoredSymlink(this.getRoot().getContext().getName(), absPath, canPath);
                            continue;
                        }
                    }
                    StringBuilder sb = new StringBuilder(path);
                    if (path.charAt(path.length() - 1) != '/') {
                        sb.append('/');
                    }
                    sb.append(entry.getName());
                    if (entry.isDirectory()) {
                        sb.append('/');
                    }
                    result.add(sb.toString());
                }
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

    @Override
    public boolean mkdir(String path) {
        this.checkPath(path);
        if (this.isReadOnly()) {
            return false;
        }
        String webAppMount = this.getWebAppMount();
        if (path.startsWith(webAppMount)) {
            File f = this.file(path.substring(webAppMount.length()), false);
            if (f == null) {
                return false;
            }
            return f.mkdir();
        }
        return false;
    }

    @Override
    public boolean write(String path, InputStream is, boolean overwrite) {
        this.checkPath(path);
        if (is == null) {
            throw new NullPointerException(sm.getString("dirResourceSet.writeNpe"));
        }
        if (this.isReadOnly()) {
            return false;
        }
        if (path.endsWith("/")) {
            return false;
        }
        File dest = null;
        String webAppMount = this.getWebAppMount();
        if (path.startsWith(webAppMount)) {
            dest = this.file(path.substring(webAppMount.length()), false);
            if (dest == null) {
                return false;
            }
        } else {
            return false;
        }
        if (dest.exists() && !overwrite) {
            return false;
        }
        try {
            if (overwrite) {
                Files.copy(is, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(is, dest.toPath(), new CopyOption[0]);
            }
        }
        catch (IOException ioe) {
            return false;
        }
        return true;
    }

    @Override
    protected void checkType(File file) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(sm.getString("dirResourceSet.notDirectory", new Object[]{this.getBase(), File.separator, this.getInternalPath()}));
        }
    }

    @Override
    protected void initInternal() throws LifecycleException {
        File mf;
        super.initInternal();
        if (this.getWebAppMount().equals("") && (mf = this.file("META-INF/MANIFEST.MF", true)) != null && mf.isFile()) {
            try (FileInputStream fis = new FileInputStream(mf);){
                this.setManifest(new Manifest(fis));
            }
            catch (IOException e) {
                log.warn((Object)sm.getString("dirResourceSet.manifestFail", new Object[]{mf.getAbsolutePath()}), (Throwable)e);
            }
        }
    }
}

