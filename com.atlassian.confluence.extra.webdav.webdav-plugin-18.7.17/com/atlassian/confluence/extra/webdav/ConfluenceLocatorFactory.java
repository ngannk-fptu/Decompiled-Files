/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav;

import org.apache.jackrabbit.webdav.simple.LocatorFactoryImplEx;

public class ConfluenceLocatorFactory
extends LocatorFactoryImplEx {
    public ConfluenceLocatorFactory(String pathPrefix) {
        super(pathPrefix);
    }

    @Override
    protected String getResourcePath(String repositoryPath, String wspPath) {
        if (repositoryPath == null) {
            throw new IllegalArgumentException("Cannot build resource path from 'null' repository path");
        }
        return this.startsWithWorkspace(repositoryPath, wspPath) ? repositoryPath : wspPath + repositoryPath;
    }

    private boolean startsWithWorkspace(String repositoryPath, String wspPath) {
        if (wspPath == null) {
            return true;
        }
        return repositoryPath.startsWith(wspPath + "/");
    }
}

