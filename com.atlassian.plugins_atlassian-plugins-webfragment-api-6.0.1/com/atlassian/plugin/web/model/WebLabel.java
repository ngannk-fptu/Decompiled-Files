/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.web.model.WebParam;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface WebLabel
extends WebParam {
    public String getKey();

    public String getNoKeyValue();

    public String getDisplayableLabel(HttpServletRequest var1, Map<String, Object> var2);
}

