/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.EntityBuilder;
import java.util.List;

public interface FormBuilder
extends EntityBuilder {
    public FormBuilder addParam(String var1);

    public FormBuilder addParam(String var1, String var2);

    public FormBuilder setParam(String var1, List<String> var2);
}

