/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 */
package org.apache.axiom.attachments;

import java.net.URL;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class ConfigurableDataHandler
extends DataHandler {
    private String transferEncoding;
    private String contentType;
    private String contentID;

    public ConfigurableDataHandler(DataSource ds) {
        super(ds);
    }

    public ConfigurableDataHandler(Object data, String type) {
        super(data, type);
    }

    public ConfigurableDataHandler(URL url) {
        super(url);
    }

    public String getContentType() {
        if (this.contentType != null) {
            return this.contentType;
        }
        return super.getContentType();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTransferEncoding() {
        return this.transferEncoding;
    }

    public void setTransferEncoding(String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }
}

