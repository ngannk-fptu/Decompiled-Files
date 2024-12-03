/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

public interface SessionAttributeStore {
    public void storeAttribute(WebRequest var1, String var2, Object var3);

    @Nullable
    public Object retrieveAttribute(WebRequest var1, String var2);

    public void cleanupAttribute(WebRequest var1, String var2);
}

