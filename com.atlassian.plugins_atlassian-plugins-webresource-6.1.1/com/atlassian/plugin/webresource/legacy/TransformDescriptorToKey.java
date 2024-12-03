/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import com.google.common.base.Function;

public class TransformDescriptorToKey
implements Function<ModuleDescriptorStub, String> {
    public String apply(ModuleDescriptorStub resource) {
        return resource.getCompleteKey();
    }
}

