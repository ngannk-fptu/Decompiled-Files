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
import java.util.ArrayList;
import javax.activation.DataSource;
import org.apache.axiom.attachments.PartContent;
import org.apache.axiom.attachments.utils.BAAInputStream;

class PartContentOnMemory
extends PartContent {
    private final ArrayList data;
    private final int length;

    PartContentOnMemory(ArrayList data, int length) {
        this.data = data;
        this.length = length;
    }

    InputStream getInputStream() {
        return new BAAInputStream(this.data, this.length);
    }

    DataSource getDataSource(String contentType) {
        return null;
    }

    void writeTo(OutputStream os) throws IOException {
        new BAAInputStream(this.data, this.length).writeTo(os);
    }

    long getSize() {
        return this.length;
    }

    void destroy() throws IOException {
        this.data.clear();
    }
}

