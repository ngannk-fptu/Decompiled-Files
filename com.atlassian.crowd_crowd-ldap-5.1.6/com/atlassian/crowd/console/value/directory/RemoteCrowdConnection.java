/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 */
package com.atlassian.crowd.console.value.directory;

import com.atlassian.crowd.console.value.directory.SynchronisableDirectoryConnection;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.directory.DirectoryImpl;

public class RemoteCrowdConnection
extends SynchronisableDirectoryConnection {
    private long httpTimeout = 5L;
    private long httpMaxConnections = 20L;

    public long getHttpTimeout() {
        return this.httpTimeout;
    }

    public void setHttpTimeout(long httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    public long getHttpMaxConnections() {
        return this.httpMaxConnections;
    }

    public void setHttpMaxConnections(long httpMaxConnections) {
        this.httpMaxConnections = httpMaxConnections;
    }

    @Override
    public void loadFromDirectory(Directory directory) {
        String httpMaxConnections;
        super.loadFromDirectory(directory);
        String httpTimeout = directory.getValue("crowd.server.http.timeout");
        if (httpTimeout != null) {
            this.setHttpTimeout(Long.parseLong(httpTimeout) / 1000L);
        }
        if ((httpMaxConnections = directory.getValue("crowd.server.http.max.connections")) != null) {
            this.setHttpMaxConnections(Long.parseLong(httpMaxConnections));
        }
    }

    @Override
    public void updateDirectory(DirectoryImpl directory) {
        super.updateDirectory(directory);
        directory.setAttribute("crowd.server.http.timeout", Long.toString(this.getHttpTimeout() * 1000L));
        directory.setAttribute("crowd.server.http.max.connections", Long.toString(this.getHttpMaxConnections()));
    }
}

