/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter;

import java.util.Map;

public interface OsgiServiceRegistrationListener {
    public void registered(Object var1, Map var2) throws Exception;

    public void unregistered(Object var1, Map var2) throws Exception;
}

