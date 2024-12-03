/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.encoding;

import org.apache.axis.components.encoding.UTF8Encoder;

public class DefaultXMLEncoder
extends UTF8Encoder {
    private String encoding = null;

    public DefaultXMLEncoder(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return this.encoding;
    }
}

