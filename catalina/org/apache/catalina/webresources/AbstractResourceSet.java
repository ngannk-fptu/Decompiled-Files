/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.webresources;

import java.util.jar.Manifest;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.util.LifecycleBase;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractResourceSet
extends LifecycleBase
implements WebResourceSet {
    private WebResourceRoot root;
    private String base;
    private String internalPath = "";
    private String webAppMount;
    private boolean classLoaderOnly;
    private boolean staticOnly;
    private Manifest manifest;
    protected static final StringManager sm = StringManager.getManager(AbstractResourceSet.class);

    protected final void checkPath(String path) {
        if (path == null || path.length() == 0 || path.charAt(0) != '/') {
            throw new IllegalArgumentException(sm.getString("abstractResourceSet.checkPath", new Object[]{path}));
        }
    }

    @Override
    public final void setRoot(WebResourceRoot root) {
        this.root = root;
    }

    protected final WebResourceRoot getRoot() {
        return this.root;
    }

    protected final String getInternalPath() {
        return this.internalPath;
    }

    public final void setInternalPath(String internalPath) {
        this.checkPath(internalPath);
        this.internalPath = internalPath.equals("/") ? "" : internalPath;
    }

    public final void setWebAppMount(String webAppMount) {
        this.checkPath(webAppMount);
        this.webAppMount = webAppMount.equals("/") ? "" : webAppMount;
    }

    protected final String getWebAppMount() {
        return this.webAppMount;
    }

    public final void setBase(String base) {
        this.base = base;
    }

    protected final String getBase() {
        return this.base;
    }

    @Override
    public boolean getClassLoaderOnly() {
        return this.classLoaderOnly;
    }

    @Override
    public void setClassLoaderOnly(boolean classLoaderOnly) {
        this.classLoaderOnly = classLoaderOnly;
    }

    @Override
    public boolean getStaticOnly() {
        return this.staticOnly;
    }

    @Override
    public void setStaticOnly(boolean staticOnly) {
        this.staticOnly = staticOnly;
    }

    protected final void setManifest(Manifest manifest) {
        this.manifest = manifest;
    }

    protected final Manifest getManifest() {
        return this.manifest;
    }

    @Override
    protected final void startInternal() throws LifecycleException {
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected final void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
    }

    @Override
    protected final void destroyInternal() throws LifecycleException {
        this.gc();
    }
}

