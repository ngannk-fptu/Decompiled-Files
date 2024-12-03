/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;

public abstract class RuleLoader {
    public abstract void addRules(Digester var1, String var2) throws PluginException;
}

