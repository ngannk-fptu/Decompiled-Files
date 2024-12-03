/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.plugins.PluginException;

public abstract class RulesFactory {
    public abstract Rules newRules(Digester var1, Class var2) throws PluginException;
}

