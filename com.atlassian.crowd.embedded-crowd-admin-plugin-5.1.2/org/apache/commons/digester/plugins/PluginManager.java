/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.Declaration;
import org.apache.commons.digester.plugins.LogUtils;
import org.apache.commons.digester.plugins.PluginContext;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.RuleFinder;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.logging.Log;

public class PluginManager {
    private HashMap declarationsByClass = new HashMap();
    private HashMap declarationsById = new HashMap();
    private PluginManager parent;
    private PluginContext pluginContext;

    public PluginManager(PluginContext r) {
        this.pluginContext = r;
    }

    public PluginManager(PluginManager parent) {
        this.parent = parent;
        this.pluginContext = parent.pluginContext;
    }

    public void addDeclaration(Declaration decl) {
        Log log = LogUtils.getLogger(null);
        boolean debug = log.isDebugEnabled();
        Class pluginClass = decl.getPluginClass();
        String id = decl.getId();
        this.declarationsByClass.put(pluginClass.getName(), decl);
        if (id != null) {
            this.declarationsById.put(id, decl);
            if (debug) {
                log.debug((Object)("Indexing plugin-id [" + id + "]" + " -> class [" + pluginClass.getName() + "]"));
            }
        }
    }

    public Declaration getDeclarationByClass(String className) {
        Declaration decl = (Declaration)this.declarationsByClass.get(className);
        if (decl == null && this.parent != null) {
            decl = this.parent.getDeclarationByClass(className);
        }
        return decl;
    }

    public Declaration getDeclarationById(String id) {
        Declaration decl = (Declaration)this.declarationsById.get(id);
        if (decl == null && this.parent != null) {
            decl = this.parent.getDeclarationById(id);
        }
        return decl;
    }

    public RuleLoader findLoader(Digester digester, String id, Class pluginClass, Properties props) throws PluginException {
        Log log = LogUtils.getLogger(digester);
        boolean debug = log.isDebugEnabled();
        log.debug((Object)"scanning ruleFinders to locate loader..");
        List ruleFinders = this.pluginContext.getRuleFinders();
        RuleLoader ruleLoader = null;
        try {
            Iterator i = ruleFinders.iterator();
            while (i.hasNext() && ruleLoader == null) {
                RuleFinder finder = (RuleFinder)i.next();
                if (debug) {
                    log.debug((Object)("checking finder of type " + finder.getClass().getName()));
                }
                ruleLoader = finder.findLoader(digester, pluginClass, props);
            }
        }
        catch (PluginException e) {
            throw new PluginException("Unable to locate plugin rules for plugin with id [" + id + "]" + ", and class [" + pluginClass.getName() + "]" + ":" + e.getMessage(), e.getCause());
        }
        log.debug((Object)"scanned ruleFinders.");
        return ruleLoader;
    }
}

