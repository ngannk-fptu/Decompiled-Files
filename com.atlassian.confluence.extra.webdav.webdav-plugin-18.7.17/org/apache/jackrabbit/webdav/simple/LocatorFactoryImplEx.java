/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import org.apache.jackrabbit.webdav.AbstractLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorFactoryImplEx
extends AbstractLocatorFactory {
    private static Logger log = LoggerFactory.getLogger(LocatorFactoryImplEx.class);

    public LocatorFactoryImplEx(String pathPrefix) {
        super(pathPrefix);
    }

    @Override
    protected String getRepositoryPath(String resourcePath, String wspPath) {
        if (resourcePath == null) {
            return resourcePath;
        }
        if (resourcePath.equals(wspPath) || this.startsWithWorkspace(resourcePath, wspPath)) {
            String repositoryPath = resourcePath.substring(wspPath.length());
            return repositoryPath.length() == 0 ? "/" : repositoryPath;
        }
        throw new IllegalArgumentException("Unexpected format of resource path: " + resourcePath + " (workspace: " + wspPath + ")");
    }

    @Override
    protected String getResourcePath(String repositoryPath, String wspPath) {
        if (repositoryPath == null) {
            throw new IllegalArgumentException("Cannot build resource path from 'null' repository path");
        }
        return wspPath + repositoryPath;
    }

    private boolean startsWithWorkspace(String repositoryPath, String wspPath) {
        if (wspPath == null) {
            return true;
        }
        return repositoryPath.startsWith(wspPath + "/");
    }
}

