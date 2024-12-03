/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.owner;

import com.atlassian.confluence.plugins.gatekeeper.model.Copiable;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import java.util.Objects;

public final class TinyGroup
extends TinyOwner
implements Cloneable,
Copiable<TinyGroup> {
    protected TinyGroup() {
    }

    public TinyGroup(String name) {
        super(name, true);
    }

    @Override
    public TinyGroup copy() {
        TinyGroup result = null;
        try {
            result = (TinyGroup)super.clone();
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
        return false;
    }

    @Override
    public boolean isGroup() {
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
        TinyGroup that = (TinyGroup)o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }
}

