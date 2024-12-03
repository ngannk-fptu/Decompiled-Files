/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.serialization.SerializerModule
 *  org.codehaus.jackson.map.Module
 */
package com.atlassian.confluence.plugins.files.rest.serialization.factory;

import com.atlassian.confluence.rest.serialization.SerializerModule;
import org.codehaus.jackson.map.Module;

public class SerializerModuleFactory {
    public static Module create() {
        return new SerializerModule();
    }
}

