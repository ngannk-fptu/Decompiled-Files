/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.json.marshal.Jsonable
 */
package com.atlassian.webresource.api.assembler;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.concurrent.CompletionStage;

@ExperimentalApi
public interface RequiredData {
    public RequiredData requireData(String var1, Jsonable var2);

    public RequiredData requireData(String var1, Jsonable var2, ResourcePhase var3);

    public RequiredData requireData(String var1, Number var2);

    public RequiredData requireData(String var1, Number var2, ResourcePhase var3);

    public RequiredData requireData(String var1, String var2);

    public RequiredData requireData(String var1, String var2, ResourcePhase var3);

    public RequiredData requireData(String var1, Boolean var2);

    public RequiredData requireData(String var1, Boolean var2, ResourcePhase var3);

    public RequiredData requireData(String var1, CompletionStage<Jsonable> var2);
}

