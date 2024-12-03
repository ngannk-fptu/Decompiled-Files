/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 */
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.axiom.attachments.PartDataSource;
import org.apache.axiom.attachments.PartImpl;
import org.apache.axiom.attachments.lifecycle.DataHandlerExt;

class PartDataHandler
extends DataHandler
implements DataHandlerExt {
    private final PartImpl part;
    private DataSource dataSource;

    public PartDataHandler(PartImpl part) {
        super((DataSource)new PartDataSource(part));
        this.part = part;
    }

    public DataSource getDataSource() {
        if (this.dataSource == null) {
            this.dataSource = this.part.getDataSource();
            if (this.dataSource == null) {
                this.dataSource = super.getDataSource();
            }
        }
        return this.dataSource;
    }

    public void writeTo(OutputStream os) throws IOException {
        this.part.writeTo(os);
    }

    public InputStream readOnce() throws IOException {
        return this.part.getInputStream(false);
    }

    public void purgeDataSource() throws IOException {
        this.part.releaseContent();
    }

    public void deleteWhenReadOnce() throws IOException {
        this.purgeDataSource();
    }
}

