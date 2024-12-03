/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.support;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

public interface RequestDataValueProcessor {
    public String processAction(HttpServletRequest var1, String var2, String var3);

    public String processFormFieldValue(HttpServletRequest var1, @Nullable String var2, String var3, String var4);

    @Nullable
    public Map<String, String> getExtraHiddenFields(HttpServletRequest var1);

    public String processUrl(HttpServletRequest var1, String var2);
}

