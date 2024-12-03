/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.util;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import org.springframework.data.util.TypeDiscoverer;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;

public abstract class ParentTypeAwareTypeInformation<S>
extends TypeDiscoverer<S> {
    private final TypeDiscoverer<?> parent;
    private int hashCode;

    protected ParentTypeAwareTypeInformation(Type type, TypeDiscoverer<?> parent) {
        this(type, parent, parent.getTypeVariableMap());
    }

    protected ParentTypeAwareTypeInformation(Type type, TypeDiscoverer<?> parent, Map<TypeVariable<?>, Type> map) {
        super(type, map);
        this.parent = parent;
    }

    @Override
    protected TypeInformation<?> createInfo(Type fieldType) {
        if (this.parent.getType().equals(fieldType)) {
            return this.parent;
        }
        return super.createInfo(fieldType);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        ParentTypeAwareTypeInformation that = (ParentTypeAwareTypeInformation)obj;
        return this.parent == null ? that.parent == null : this.parent.equals(that.parent);
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = super.hashCode() + 31 * this.parent.hashCode();
        }
        return this.hashCode;
    }
}

