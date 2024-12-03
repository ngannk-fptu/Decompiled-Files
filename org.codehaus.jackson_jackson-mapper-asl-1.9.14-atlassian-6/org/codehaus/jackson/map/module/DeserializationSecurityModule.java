/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.Version
 */
package org.codehaus.jackson.map.module;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.deser.SecurityBeanDeserializer;

public class DeserializationSecurityModule
extends Module {
    public String getModuleName() {
        return this.getClass().getName();
    }

    public Version version() {
        return new Version(1, 9, 13, "");
    }

    public void setupModule(Module.SetupContext context) {
        context.addDeserializers(new SecurityBeanDeserializer());
    }
}

