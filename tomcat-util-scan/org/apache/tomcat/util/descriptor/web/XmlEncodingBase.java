/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class XmlEncodingBase {
    private Charset charset = StandardCharsets.UTF_8;

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return this.charset;
    }
}

