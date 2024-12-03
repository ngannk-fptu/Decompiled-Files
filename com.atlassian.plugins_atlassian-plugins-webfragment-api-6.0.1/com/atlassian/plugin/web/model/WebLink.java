/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface WebLink {
    public String getRenderedUrl(Map<String, Object> var1);

    public String getDisplayableUrl(HttpServletRequest var1, Map<String, Object> var2);

    public boolean hasAccessKey();

    public String getAccessKey(Map<String, Object> var1);

    public String getId();

    public WebFragmentModuleDescriptor getDescriptor();
}

