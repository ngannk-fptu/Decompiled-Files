/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.ClassMemberAccessPolicy;

public interface MemberAccessPolicy {
    public ClassMemberAccessPolicy forClass(Class<?> var1);

    public boolean isToStringAlwaysExposed();
}

