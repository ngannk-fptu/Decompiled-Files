/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.classmate.util;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedRecursiveType;
import java.util.ArrayList;

public final class ClassStack {
    protected final ClassStack _parent;
    protected final Class<?> _current;
    private ArrayList<ResolvedRecursiveType> _selfRefs;

    public ClassStack(Class<?> rootType) {
        this(null, rootType);
    }

    private ClassStack(ClassStack parent, Class<?> curr) {
        this._parent = parent;
        this._current = curr;
    }

    public ClassStack child(Class<?> cls) {
        return new ClassStack(this, cls);
    }

    public void addSelfReference(ResolvedRecursiveType ref) {
        if (this._selfRefs == null) {
            this._selfRefs = new ArrayList();
        }
        this._selfRefs.add(ref);
    }

    public void resolveSelfReferences(ResolvedType resolved) {
        if (this._selfRefs != null) {
            for (ResolvedRecursiveType ref : this._selfRefs) {
                ref.setReference(resolved);
            }
        }
    }

    public ClassStack find(Class<?> cls) {
        if (this._current == cls) {
            return this;
        }
        ClassStack curr = this._parent;
        while (curr != null) {
            if (curr._current == cls) {
                return curr;
            }
            curr = curr._parent;
        }
        return null;
    }
}

