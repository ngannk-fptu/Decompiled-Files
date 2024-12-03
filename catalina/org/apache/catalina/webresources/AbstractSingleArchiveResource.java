/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.catalina.webresources.AbstractArchiveResource;
import org.apache.catalina.webresources.AbstractArchiveResourceSet;

public abstract class AbstractSingleArchiveResource
extends AbstractArchiveResource {
    protected AbstractSingleArchiveResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry, String codeBaseUrl) {
        super(archiveResourceSet, webAppPath, baseUrl, jarEntry, codeBaseUrl);
    }

    @Override
    protected AbstractArchiveResource.JarInputStreamWrapper getJarInputStreamWrapper() {
        JarFile jarFile = null;
        try {
            jarFile = this.getArchiveResourceSet().openJarFile();
            JarEntry jarEntry = jarFile.getJarEntry(this.getResource().getName());
            InputStream is = jarFile.getInputStream(jarEntry);
            return new AbstractArchiveResource.JarInputStreamWrapper(jarEntry, is);
        }
        catch (IOException e) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)sm.getString("jarResource.getInputStreamFail", new Object[]{this.getResource().getName(), this.getBaseUrl()}), (Throwable)e);
            }
            if (jarFile != null) {
                this.getArchiveResourceSet().closeJarFile();
            }
            return null;
        }
    }
}

