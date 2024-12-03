/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins;

import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.RulesBase;
import org.apache.commons.digester.plugins.InitializableRule;
import org.apache.commons.digester.plugins.LogUtils;
import org.apache.commons.digester.plugins.PluginConfigurationException;
import org.apache.commons.digester.plugins.PluginContext;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.PluginManager;
import org.apache.commons.digester.plugins.RulesFactory;
import org.apache.commons.logging.Log;

public class PluginRules
implements Rules {
    protected Digester digester = null;
    private RulesFactory rulesFactory;
    private Rules decoratedRules;
    private PluginManager pluginManager;
    private String mountPoint = null;
    private PluginRules parent = null;
    private PluginContext pluginContext = null;

    public PluginRules() {
        this(new RulesBase());
    }

    public PluginRules(Rules decoratedRules) {
        this.decoratedRules = decoratedRules;
        this.pluginContext = new PluginContext();
        this.pluginManager = new PluginManager(this.pluginContext);
    }

    PluginRules(Digester digester, String mountPoint, PluginRules parent, Class pluginClass) throws PluginException {
        this.digester = digester;
        this.mountPoint = mountPoint;
        this.parent = parent;
        this.rulesFactory = parent.rulesFactory;
        this.decoratedRules = this.rulesFactory == null ? new RulesBase() : this.rulesFactory.newRules(digester, pluginClass);
        this.pluginContext = parent.pluginContext;
        this.pluginManager = new PluginManager(parent.pluginManager);
    }

    public Rules getParent() {
        return this.parent;
    }

    public Digester getDigester() {
        return this.digester;
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
        this.decoratedRules.setDigester(digester);
    }

    public String getNamespaceURI() {
        return this.decoratedRules.getNamespaceURI();
    }

    public void setNamespaceURI(String namespaceURI) {
        this.decoratedRules.setNamespaceURI(namespaceURI);
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public List getRuleFinders() {
        return this.pluginContext.getRuleFinders();
    }

    public void setRuleFinders(List ruleFinders) {
        this.pluginContext.setRuleFinders(ruleFinders);
    }

    public RulesFactory getRulesFactory() {
        return this.rulesFactory;
    }

    public void setRulesFactory(RulesFactory factory) {
        this.rulesFactory = factory;
    }

    Rules getDecoratedRules() {
        return this.decoratedRules;
    }

    public List rules() {
        return this.decoratedRules.rules();
    }

    public void add(String pattern, Rule rule) {
        Log log = LogUtils.getLogger(this.digester);
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("add entry: mapping pattern [" + pattern + "]" + " to rule of type [" + rule.getClass().getName() + "]"));
        }
        if (pattern.startsWith("/")) {
            pattern = pattern.substring(1);
        }
        if (this.mountPoint != null && !pattern.equals(this.mountPoint) && !pattern.startsWith(this.mountPoint + "/")) {
            log.warn((Object)("An attempt was made to add a rule with a pattern thatis not at or below the mountpoint of the current PluginRules object. Rule pattern: " + pattern + ", mountpoint: " + this.mountPoint + ", rule type: " + rule.getClass().getName()));
            return;
        }
        this.decoratedRules.add(pattern, rule);
        if (rule instanceof InitializableRule) {
            try {
                ((InitializableRule)((Object)rule)).postRegisterInit(pattern);
            }
            catch (PluginConfigurationException e) {
                if (debug) {
                    log.debug((Object)"Rule initialisation failed", (Throwable)e);
                }
                return;
            }
        }
        if (debug) {
            log.debug((Object)("add exit: mapped pattern [" + pattern + "]" + " to rule of type [" + rule.getClass().getName() + "]"));
        }
    }

    public void clear() {
        this.decoratedRules.clear();
    }

    public List match(String path) {
        return this.match(null, path);
    }

    public List match(String namespaceURI, String path) {
        List matches;
        Log log = LogUtils.getLogger(this.digester);
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("Matching path [" + path + "] on rules object " + this.toString()));
        }
        if (this.mountPoint != null && path.length() <= this.mountPoint.length()) {
            if (debug) {
                log.debug((Object)("Path [" + path + "] delegated to parent."));
            }
            matches = this.parent.match(namespaceURI, path);
        } else {
            log.debug((Object)"delegating to decorated rules.");
            matches = this.decoratedRules.match(namespaceURI, path);
        }
        return matches;
    }

    public void setPluginClassAttribute(String namespaceUri, String attrName) {
        this.pluginContext.setPluginClassAttribute(namespaceUri, attrName);
    }

    public void setPluginIdAttribute(String namespaceUri, String attrName) {
        this.pluginContext.setPluginIdAttribute(namespaceUri, attrName);
    }

    public String getPluginClassAttrNs() {
        return this.pluginContext.getPluginClassAttrNs();
    }

    public String getPluginClassAttr() {
        return this.pluginContext.getPluginClassAttr();
    }

    public String getPluginIdAttrNs() {
        return this.pluginContext.getPluginIdAttrNs();
    }

    public String getPluginIdAttr() {
        return this.pluginContext.getPluginIdAttr();
    }
}

