/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.salext.mail;

import java.io.Serializable;

public class SupportRequestAttachment
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String type;
    private final Serializable data;

    public SupportRequestAttachment(String name, String type, Serializable data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public Serializable getData() {
        return this.data;
    }
}

