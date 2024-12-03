/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import org.apache.velocity.util.introspection.ClassMap;

public interface IntrospectorCache {
    public void clear();

    public ClassMap get(Class var1);

    public ClassMap put(Class var1);
}

