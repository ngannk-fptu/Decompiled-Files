/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.DirListingExportHandler;
import org.apache.jackrabbit.server.io.IOManagerImpl;
import org.apache.jackrabbit.server.io.VersionHandler;
import org.apache.jackrabbit.server.io.VersionHistoryHandler;
import org.apache.jackrabbit.server.io.XmlHandler;
import org.apache.jackrabbit.server.io.ZipHandler;

public class DefaultIOManager
extends IOManagerImpl {
    public DefaultIOManager() {
        this.init();
    }

    protected void init() {
        this.addIOHandler(new VersionHistoryHandler(this));
        this.addIOHandler(new VersionHandler(this));
        this.addIOHandler(new ZipHandler(this));
        this.addIOHandler(new XmlHandler(this));
        this.addIOHandler(new DirListingExportHandler(this));
        this.addIOHandler(new DefaultHandler(this));
    }
}

