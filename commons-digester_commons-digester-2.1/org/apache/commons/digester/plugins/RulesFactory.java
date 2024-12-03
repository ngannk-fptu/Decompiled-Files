/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.plugins.PluginException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class RulesFactory {
    public abstract Rules newRules(Digester var1, Class<?> var2) throws PluginException;
}

