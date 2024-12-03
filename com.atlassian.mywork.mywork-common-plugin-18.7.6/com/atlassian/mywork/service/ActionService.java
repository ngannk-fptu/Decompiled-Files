/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 */
package com.atlassian.mywork.service;

import com.atlassian.mywork.service.ActionResult;
import org.codehaus.jackson.JsonNode;

public interface ActionService {
    public String getApplication();

    public ActionResult execute(String var1, JsonNode var2);
}

