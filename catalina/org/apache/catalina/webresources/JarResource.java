/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import org.apache.catalina.webresources.AbstractArchiveResourceSet;
import org.apache.catalina.webresources.AbstractSingleArchiveResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class JarResource
extends AbstractSingleArchiveResource {
    private static final Log log = LogFactory.getLog(JarResource.class);

    public JarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry) {
        super(archiveResourceSet, webAppPath, "jar:" + baseUrl + "!/", jarEntry, baseUrl);
    }

    @Override
    protected Log getLog() {
        return log;
    }
}

