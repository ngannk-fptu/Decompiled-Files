/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.net.URI;
import java.util.Map;

public interface UriTemplateHandler {
    public URI expand(String var1, Map<String, ?> var2);

    public URI expand(String var1, Object ... var2);
}

