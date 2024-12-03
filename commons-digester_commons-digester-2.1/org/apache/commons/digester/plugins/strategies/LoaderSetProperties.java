/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins.strategies;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.logging.Log;

public class LoaderSetProperties
extends RuleLoader {
    public void addRules(Digester digester, String path) {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("LoaderSetProperties loading rules for plugin at path [" + path + "]"));
        }
        digester.addSetProperties(path);
    }
}

