/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import org.apache.jackrabbit.webdav.AbstractLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DavLocatorFactoryImpl
extends AbstractLocatorFactory {
    private static Logger log = LoggerFactory.getLogger(DavLocatorFactoryImpl.class);

    public DavLocatorFactoryImpl(String pathPrefix) {
        super(pathPrefix);
    }

    @Override
    protected String getRepositoryPath(String resourcePath, String wspPath) {
        if (resourcePath == null) {
            return null;
        }
        if (resourcePath.equals(wspPath)) {
            log.debug("Resource path represents workspace path -> repository path is null.");
            return null;
        }
        String pfx = wspPath + "/jcr:root";
        if (resourcePath.startsWith(pfx)) {
            String repositoryPath = resourcePath.substring(pfx.length());
            return repositoryPath.length() == 0 ? "/" : repositoryPath;
        }
        log.error("Unexpected format of resource path.");
        throw new IllegalArgumentException("Unexpected format of resource path: " + resourcePath + " (workspace: " + wspPath + ")");
    }

    @Override
    protected String getResourcePath(String repositoryPath, String wspPath) {
        if (wspPath != null) {
            StringBuffer b = new StringBuffer(wspPath);
            if (repositoryPath != null) {
                b.append("/jcr:root");
                if (!"/".equals(repositoryPath)) {
                    b.append(repositoryPath);
                }
            }
            return b.toString();
        }
        log.debug("Workspace path is 'null' -> 'null' resource path");
        return null;
    }
}

