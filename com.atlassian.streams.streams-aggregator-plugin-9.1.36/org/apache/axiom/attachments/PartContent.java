/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

abstract class PartContent {
    PartContent() {
    }

    abstract InputStream getInputStream() throws IOException;

    abstract DataSource getDataSource(String var1);

    abstract void writeTo(OutputStream var1) throws IOException;

    abstract long getSize();

    abstract void destroy() throws IOException;
}

