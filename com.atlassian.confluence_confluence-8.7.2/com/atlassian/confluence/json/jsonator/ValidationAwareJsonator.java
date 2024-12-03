/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.xwork2.interceptor.ValidationAware
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.xwork2.interceptor.ValidationAware;

public class ValidationAwareJsonator
implements Jsonator<ValidationAware> {
    @Override
    public Json convert(ValidationAware action) {
        JsonObject json = new JsonObject();
        Jsonator defaultJsonator = (Jsonator)ContainerManager.getComponent((String)"jsonator");
        if (action.hasActionErrors()) {
            json.setProperty("actionErrors", defaultJsonator.convert(action.getActionErrors()));
        }
        if (action.hasFieldErrors()) {
            json.setProperty("fieldErrors", defaultJsonator.convert(action.getFieldErrors()));
        }
        if (action.hasActionMessages()) {
            json.setProperty("actionMessages", defaultJsonator.convert(action.getActionMessages()));
        }
        return json;
    }
}

