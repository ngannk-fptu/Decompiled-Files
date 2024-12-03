/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.AbstractSingleArchiveResourceSet;
import org.apache.catalina.webresources.JarResource;

public class JarResourceSet
extends AbstractSingleArchiveResourceSet {
    public JarResourceSet() {
    }

    public JarResourceSet(WebResourceRoot root, String webAppMount, String base, String internalPath) throws IllegalArgumentException {
        super(root, webAppMount, base, internalPath);
    }

    @Override
    protected WebResource createArchiveResource(JarEntry jarEntry, String webAppPath, Manifest manifest) {
        return new JarResource(this, webAppPath, this.getBaseUrlString(), jarEntry);
    }
}

