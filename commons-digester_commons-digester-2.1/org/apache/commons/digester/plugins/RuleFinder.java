/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.plugins;

import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleLoader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class RuleFinder {
    public abstract RuleLoader findLoader(Digester var1, Class<?> var2, Properties var3) throws PluginException;
}

