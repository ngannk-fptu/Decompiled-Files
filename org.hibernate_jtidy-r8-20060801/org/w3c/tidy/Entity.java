/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

public class Entity {
    private String name;
    private short code;

    public Entity(String name, int code) {
        this.name = name;
        this.code = (short)code;
    }

    public short getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}

