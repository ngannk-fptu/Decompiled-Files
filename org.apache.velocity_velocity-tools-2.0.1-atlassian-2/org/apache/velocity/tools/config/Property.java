/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import org.apache.velocity.tools.config.Data;

public class Property
extends Data {
    public void setName(String name) {
        this.setKey(name);
    }

    public String getName() {
        return this.getKey();
    }
}

