/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.owner;

import com.atlassian.confluence.plugins.gatekeeper.model.Copiable;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import java.util.Objects;

public class TinyUser
extends TinyOwner
implements Cloneable,
Copiable<TinyUser> {
    public TinyUser() {
    }

    public TinyUser(String name, String displayName, boolean active) {
        this(name.toLowerCase(), displayName, active, false);
    }

    public TinyUser(String name, String displayName, boolean active, boolean canUse) {
        super(name.toLowerCase(), displayName, active, canUse);
    }

    @Override
    public TinyUser copy() {
        TinyUser result = null;
        try {
            result = (TinyUser)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public boolean isUser() {
        return true;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TinyUser that = (TinyUser)o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }
}

