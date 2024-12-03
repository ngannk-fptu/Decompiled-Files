/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  org.jvnet.staxex.StreamingDataHandler
 */
package com.sun.xml.ws.developer;

import java.net.URL;
import javax.activation.DataSource;

public abstract class StreamingDataHandler
extends org.jvnet.staxex.StreamingDataHandler {
    public StreamingDataHandler(Object o, String s) {
        super(o, s);
    }

    public StreamingDataHandler(URL url) {
        super(url);
    }

    public StreamingDataHandler(DataSource dataSource) {
        super(dataSource);
    }
}

