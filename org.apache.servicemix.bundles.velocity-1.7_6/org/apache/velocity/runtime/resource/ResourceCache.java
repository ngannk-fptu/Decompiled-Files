/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource;

import java.util.Iterator;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;

public interface ResourceCache {
    public void initialize(RuntimeServices var1);

    public Resource get(Object var1);

    public Resource put(Object var1, Resource var2);

    public Resource remove(Object var1);

    public Iterator enumerateKeys();
}

