/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

import org.apache.commons.digester.plugins.PluginConfigurationException;

public interface InitializableRule {
    public void postRegisterInit(String var1) throws PluginConfigurationException;
}

