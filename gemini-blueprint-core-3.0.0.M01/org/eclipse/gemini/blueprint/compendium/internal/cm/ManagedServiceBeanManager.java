/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.util.Map;

public interface ManagedServiceBeanManager {
    public Object register(Object var1);

    public void unregister(Object var1);

    public void updated(Map var1);
}

