/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer;

import java.util.Map;

public interface OsgiServiceLifecycleListener {
    public void bind(Object var1, Map var2) throws Exception;

    public void unbind(Object var1, Map var2) throws Exception;
}

