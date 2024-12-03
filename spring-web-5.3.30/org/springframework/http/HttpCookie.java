/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class HttpCookie {
    private final String name;
    private final String value;

    public HttpCookie(String name, @Nullable String value) {
        Assert.hasLength((String)name, (String)"'name' is required and must not be empty.");
        this.name = name;
        this.value = value != null ? value : "";
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HttpCookie)) {
            return false;
        }
        HttpCookie otherCookie = (HttpCookie)other;
        return this.name.equalsIgnoreCase(otherCookie.getName());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return this.name + '=' + this.value;
    }
}

