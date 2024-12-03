/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.tags;

import org.springframework.lang.Nullable;

public class Param {
    @Nullable
    private String name;
    @Nullable
    private String value;

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "JSP Tag Param: name '" + this.name + "', value '" + this.value + "'";
    }
}

