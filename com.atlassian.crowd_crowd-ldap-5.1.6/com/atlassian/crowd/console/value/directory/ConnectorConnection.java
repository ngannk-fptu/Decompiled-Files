/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.synchronisation.Defaults
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 */
package com.atlassian.crowd.console.value.directory;

import com.atlassian.crowd.console.value.directory.SynchronisableDirectoryConnection;
import com.atlassian.crowd.directory.synchronisation.Defaults;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import java.util.concurrent.TimeUnit;

public class ConnectorConnection
extends SynchronisableDirectoryConnection {
    private long readTimeoutInSec = Defaults.READ_TIMEOUT.getSeconds();
    private long searchTimeoutInSec = Defaults.SEARCH_TIMEOUT.getSeconds();
    private long connectionTimeoutInSec = Defaults.CONNECTION_TIMEOUT.getSeconds();

    public long getReadTimeoutInSec() {
        return this.readTimeoutInSec;
    }

    public void setReadTimeoutInSec(long readTimeoutInSec) {
        this.readTimeoutInSec = readTimeoutInSec;
    }

    public long getSearchTimeoutInSec() {
        return this.searchTimeoutInSec;
    }

    public void setSearchTimeoutInSec(long searchTimeoutInSec) {
        this.searchTimeoutInSec = searchTimeoutInSec;
    }

    public long getConnectionTimeoutInSec() {
        return this.connectionTimeoutInSec;
    }

    public void setConnectionTimeoutInSec(long connectionTimeoutInSec) {
        this.connectionTimeoutInSec = connectionTimeoutInSec;
    }

    @Override
    public void loadFromDirectory(Directory directory) {
        super.loadFromDirectory(directory);
        this.setReadTimeoutInSec(TimeUnit.SECONDS.convert(this.getAttributeValueAsLong(directory, "ldap.read.timeout"), TimeUnit.MILLISECONDS));
        this.setSearchTimeoutInSec(TimeUnit.SECONDS.convert(this.getAttributeValueAsLong(directory, "ldap.search.timelimit"), TimeUnit.MILLISECONDS));
        this.setConnectionTimeoutInSec(TimeUnit.SECONDS.convert(this.getAttributeValueAsLong(directory, "ldap.connection.timeout"), TimeUnit.MILLISECONDS));
    }

    @Override
    public void updateDirectory(DirectoryImpl directory) {
        super.updateDirectory(directory);
        directory.setAttribute("ldap.read.timeout", Long.toString(TimeUnit.MILLISECONDS.convert(this.readTimeoutInSec, TimeUnit.SECONDS)));
        directory.setAttribute("ldap.search.timelimit", Long.toString(TimeUnit.MILLISECONDS.convert(this.searchTimeoutInSec, TimeUnit.SECONDS)));
        directory.setAttribute("ldap.connection.timeout", Long.toString(TimeUnit.MILLISECONDS.convert(this.connectionTimeoutInSec, TimeUnit.SECONDS)));
    }
}

