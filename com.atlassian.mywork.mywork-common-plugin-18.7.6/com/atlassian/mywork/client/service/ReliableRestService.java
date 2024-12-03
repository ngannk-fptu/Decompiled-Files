/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.mywork.rest.JsonObject;
import java.util.List;
import java.util.concurrent.Future;
import org.codehaus.jackson.type.TypeReference;

public interface ReliableRestService {
    public <T extends JsonObject> Future<List<T>> post(String var1, String var2, List<T> var3, TypeReference<List<T>> var4);

    public <T extends JsonObject> Future<T> post(String var1, String var2, T var3);

    public void delete(String var1, String var2);

    public String get(String var1, String var2) throws CredentialsRequiredException;

    public <T> T get(String var1, String var2, Class<T> var3) throws CredentialsRequiredException;

    public <T> T get(String var1, String var2, TypeReference<T> var3) throws CredentialsRequiredException;
}

