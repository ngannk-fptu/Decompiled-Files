/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.util.classloader.FileResourceStore;
import com.opensymphony.xwork2.util.classloader.ResourceStore;
import com.opensymphony.xwork2.util.classloader.ResourceStoreClassLoader;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class ReloadingClassLoader
extends ClassLoader {
    private static final Logger LOG = LogManager.getLogger(ReloadingClassLoader.class);
    private final ClassLoader parent;
    private ResourceStore[] stores;
    private ClassLoader delegate;
    private Set<Pattern> acceptClasses = Collections.emptySet();

    public ReloadingClassLoader(ClassLoader pParent) {
        super(pParent);
        this.parent = pParent;
        URL parentRoot = pParent.getResource("");
        FileManager fileManager = ActionContext.getContext().getInstance(FileManagerFactory.class).getFileManager();
        URL root = fileManager.normalizeToFileProtocol(parentRoot);
        root = (URL)ObjectUtils.defaultIfNull((Object)root, (Object)parentRoot);
        try {
            if (root == null) {
                throw new StrutsException("Unable to start the reloadable class loader, consider setting 'struts.convention.classes.reload' to false");
            }
            this.stores = new ResourceStore[]{new FileResourceStore(new File(root.toURI()))};
        }
        catch (URISyntaxException e) {
            throw new StrutsException("Unable to start the reloadable class loader, consider setting 'struts.convention.classes.reload' to false", e);
        }
        catch (RuntimeException e) {
            if (root != null) {
                LOG.error("Exception while trying to build the ResourceStore for URL [{}]", (Object)root.toString(), (Object)e);
            } else {
                LOG.error("Exception while trying to get root resource from class loader", (Throwable)e);
            }
            LOG.error("Consider setting struts.convention.classes.reload=false");
            throw e;
        }
        this.delegate = new ResourceStoreClassLoader(this.parent, this.stores);
    }

    public boolean addResourceStore(ResourceStore pStore) {
        try {
            int n = this.stores.length;
            ResourceStore[] newStores = new ResourceStore[n + 1];
            System.arraycopy(this.stores, 0, newStores, 1, n);
            newStores[0] = pStore;
            this.stores = newStores;
            this.delegate = new ResourceStoreClassLoader(this.parent, this.stores);
            return true;
        }
        catch (RuntimeException e) {
            LOG.error("Could not add resource store", (Throwable)e);
            return false;
        }
    }

    public boolean removeResourceStore(ResourceStore pStore) {
        int i;
        int n = this.stores.length;
        for (i = 0; i < n && this.stores[i] != pStore; ++i) {
        }
        if (i == n) {
            return false;
        }
        ResourceStore[] newStores = new ResourceStore[n - 1];
        if (i > 0) {
            System.arraycopy(this.stores, 0, newStores, 0, i);
        }
        if (i < n - 1) {
            System.arraycopy(this.stores, i + 1, newStores, i, n - i - 1);
        }
        this.stores = newStores;
        this.delegate = new ResourceStoreClassLoader(this.parent, this.stores);
        return true;
    }

    public void reload() {
        LOG.trace("Reloading class loader");
        this.delegate = new ResourceStoreClassLoader(this.parent, this.stores);
    }

    @Override
    public void clearAssertionStatus() {
        this.delegate.clearAssertionStatus();
    }

    @Override
    public URL getResource(String name) {
        return this.delegate.getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.delegate.getResourceAsStream(name);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return this.isAccepted(name) ? this.delegate.loadClass(name) : this.parent.loadClass(name);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        this.delegate.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        this.delegate.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        this.delegate.setPackageAssertionStatus(packageName, enabled);
    }

    public void setAccepClasses(Set<Pattern> acceptClasses) {
        this.acceptClasses = acceptClasses;
    }

    protected boolean isAccepted(String className) {
        if (!this.acceptClasses.isEmpty()) {
            for (Pattern pattern : this.acceptClasses) {
                Matcher matcher = pattern.matcher(className);
                if (!matcher.matches()) continue;
                return true;
            }
            return false;
        }
        return true;
    }
}

