/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context;

import java.util.Map;

public interface ContextElement {
    public Class identifier();

    public Map<String, Object> attributes();
}

