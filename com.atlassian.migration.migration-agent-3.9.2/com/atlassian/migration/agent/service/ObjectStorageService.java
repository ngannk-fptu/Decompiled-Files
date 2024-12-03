/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.migration.agent.service;

import org.codehaus.jackson.type.TypeReference;

public interface ObjectStorageService {
    public Object download(String var1, TypeReference<?> var2);
}

