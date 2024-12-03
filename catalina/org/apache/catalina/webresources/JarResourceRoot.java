/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.AbstractResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class JarResourceRoot
extends AbstractResource {
    private static final Log log = LogFactory.getLog(JarResourceRoot.class);
    private final File base;
    private final String baseUrl;
    private final String name;

    public JarResourceRoot(WebResourceRoot root, File base, String baseUrl, String webAppPath) {
        super(root, webAppPath);
        if (!webAppPath.endsWith("/")) {
            throw new IllegalArgumentException(sm.getString("jarResourceRoot.invalidWebAppPath", new Object[]{webAppPath}));
        }
        this.base = base;
        this.baseUrl = "jar:" + baseUrl;
        String resourceName = webAppPath.substring(0, webAppPath.length() - 1);
        int i = resourceName.lastIndexOf(47);
        if (i > -1) {
            resourceName = resourceName.substring(i + 1);
        }
        this.name = resourceName;
    }

    @Override
    public long getLastModified() {
        return this.base.lastModified();
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getContentLength() {
        return -1L;
    }

    @Override
    public String getCanonicalPath() {
        return null;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    protected InputStream doGetInputStream() {
        return null;
    }

    @Override
    public byte[] getContent() {
        return null;
    }

    @Override
    public long getCreation() {
        return this.base.lastModified();
    }

    @Override
    public URL getURL() {
        String url = this.baseUrl + "!/";
        try {
            return new URI(url).toURL();
        }
        catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("fileResource.getUrlFail", new Object[]{url}), (Throwable)e);
            }
            return null;
        }
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URI(this.baseUrl).toURL();
        }
        catch (MalformedURLException | URISyntaxException e) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)sm.getString("fileResource.getUrlFail", new Object[]{this.baseUrl}), (Throwable)e);
            }
            return null;
        }
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    public Certificate[] getCertificates() {
        return null;
    }

    @Override
    public Manifest getManifest() {
        return null;
    }
}

