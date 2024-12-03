/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.classmate.MemberResolver
 *  com.fasterxml.classmate.TypeResolver
 */
package org.hibernate.boot.internal;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.TypeResolver;

public class ClassmateContext {
    private TypeResolver typeResolver = new TypeResolver();
    private MemberResolver memberResolver = new MemberResolver(this.typeResolver);

    public TypeResolver getTypeResolver() {
        if (this.typeResolver == null) {
            throw new IllegalStateException("Classmate context has been released");
        }
        return this.typeResolver;
    }

    public MemberResolver getMemberResolver() {
        if (this.memberResolver == null) {
            throw new IllegalStateException("Classmate context has been released");
        }
        return this.memberResolver;
    }

    public void release() {
        this.typeResolver = null;
        this.memberResolver = null;
    }
}

