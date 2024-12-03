/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import java.util.UUID;
import javax.xml.bind.annotation.XmlElement;

public abstract class UuidBacked {
    private UUID id;

    protected UuidBacked() {
    }

    public UuidBacked(UUID id) {
        this.id = id;
    }

    @XmlElement
    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

