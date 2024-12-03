/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.result.plain;

import java.io.StringWriter;

class BodyWriter {
    private final StringWriter body = new StringWriter();

    BodyWriter() {
    }

    public BodyWriter write(String out) {
        this.body.write(out);
        return this;
    }

    public BodyWriter writeLine(String out) {
        this.body.write(out);
        this.body.write("\n");
        return this;
    }

    public String getBody() {
        return this.body.toString();
    }
}

