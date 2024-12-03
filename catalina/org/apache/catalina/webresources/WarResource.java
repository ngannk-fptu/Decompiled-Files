/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.UriUtil
 */
package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import org.apache.catalina.webresources.AbstractArchiveResourceSet;
import org.apache.catalina.webresources.AbstractSingleArchiveResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.UriUtil;

public class WarResource
extends AbstractSingleArchiveResource {
    private static final Log log = LogFactory.getLog(WarResource.class);

    public WarResource(AbstractArchiveResourceSet archiveResourceSet, String webAppPath, String baseUrl, JarEntry jarEntry) {
        super(archiveResourceSet, webAppPath, "war:" + baseUrl + UriUtil.getWarSeparator(), jarEntry, baseUrl);
    }

    @Override
    protected Log getLog() {
        return log;
    }
}

