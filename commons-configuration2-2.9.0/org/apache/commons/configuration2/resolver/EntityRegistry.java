/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.resolver;

import java.net.URL;
import java.util.Map;

public interface EntityRegistry {
    public void registerEntityId(String var1, URL var2);

    public Map<String, URL> getRegisteredEntities();
}

