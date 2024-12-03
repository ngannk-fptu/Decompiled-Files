/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model.owner;

import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class TinyAnonymous
extends TinyUser {
    public static final TinyAnonymous ANONYMOUS = new TinyAnonymous("<anonymous>");

    private TinyAnonymous() {
    }

    public TinyAnonymous(String name) {
        super(name, "", true, true);
    }

    @Override
    public boolean isUser() {
        return false;
    }

    @Override
    @JsonProperty(value="anonymous")
    public boolean isAnonymous() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TinyAnonymous that = (TinyAnonymous)o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }
}

