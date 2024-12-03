/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;

public class EntityTag {
    private String value;
    private boolean weak;
    private static final RuntimeDelegate.HeaderDelegate<EntityTag> delegate = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);

    public EntityTag(String value) {
        this(value, false);
    }

    public EntityTag(String value, boolean weak) {
        if (value == null) {
            throw new IllegalArgumentException("value==null");
        }
        this.value = value;
        this.weak = weak;
    }

    public static EntityTag valueOf(String value) throws IllegalArgumentException {
        return delegate.fromString(value);
    }

    public boolean isWeak() {
        return this.weak;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EntityTag)) {
            return super.equals(obj);
        }
        EntityTag other = (EntityTag)obj;
        return this.value.equals(other.getValue()) && this.weak == other.isWeak();
    }

    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 17 * hash + (this.weak ? 1 : 0);
        return hash;
    }

    public String toString() {
        return delegate.toString(this);
    }
}

