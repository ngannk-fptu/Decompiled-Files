/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.map.Module
 *  org.codehaus.jackson.map.module.SimpleModule
 */
package com.atlassian.confluence.api.serialization;

import com.atlassian.annotations.Internal;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.module.SimpleModule;

@Internal
public class ApiSerializerModuleFactory {
    public static Module create() {
        SimpleModule module = new SimpleModule("api-serializers", new Version(1, 0, 0, null));
        return module;
    }
}

