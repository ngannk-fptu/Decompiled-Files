/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 */
package org.apache.axis.attachments;

import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class DynamicContentDataHandler
extends DataHandler {
    int chunkSize = 0x100000;

    public DynamicContentDataHandler(DataSource arg0) {
        super(arg0);
    }

    public DynamicContentDataHandler(Object arg0, String arg1) {
        super(arg0, arg1);
    }

    public DynamicContentDataHandler(URL arg0) {
        super(arg0);
    }

    public int getChunkSize() {
        return this.chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}

