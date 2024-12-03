/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins;

import java.util.List;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.plugins.Declaration;
import org.apache.commons.digester.plugins.InitializableRule;
import org.apache.commons.digester.plugins.LogUtils;
import org.apache.commons.digester.plugins.PluginConfigurationException;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.PluginInvalidInputException;
import org.apache.commons.digester.plugins.PluginManager;
import org.apache.commons.digester.plugins.PluginRules;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

public class PluginCreateRule
extends Rule
implements InitializableRule {
    private String pluginClassAttrNs = null;
    private String pluginClassAttr = null;
    private String pluginIdAttrNs = null;
    private String pluginIdAttr = null;
    private String pattern;
    private Class baseClass = null;
    private Declaration defaultPlugin;
    private PluginConfigurationException initException;
    static /* synthetic */ Class class$java$lang$Object;

    public PluginCreateRule(Class baseClass) {
        this.baseClass = baseClass;
    }

    public PluginCreateRule(Class baseClass, Class dfltPluginClass) {
        this.baseClass = baseClass;
        if (dfltPluginClass != null) {
            this.defaultPlugin = new Declaration(dfltPluginClass);
        }
    }

    public PluginCreateRule(Class baseClass, Class dfltPluginClass, RuleLoader dfltPluginRuleLoader) {
        this.baseClass = baseClass;
        if (dfltPluginClass != null) {
            this.defaultPlugin = new Declaration(dfltPluginClass, dfltPluginRuleLoader);
        }
    }

    public void setPluginClassAttribute(String namespaceUri, String attrName) {
        this.pluginClassAttrNs = namespaceUri;
        this.pluginClassAttr = attrName;
    }

    public void setPluginIdAttribute(String namespaceUri, String attrName) {
        this.pluginIdAttrNs = namespaceUri;
        this.pluginIdAttr = attrName;
    }

    public void postRegisterInit(String matchPattern) throws PluginConfigurationException {
        Log log = LogUtils.getLogger(this.digester);
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("PluginCreateRule.postRegisterInit: rule registered for pattern [" + matchPattern + "]"));
        }
        if (this.digester == null) {
            this.initException = new PluginConfigurationException("Invalid invocation of postRegisterInit: digester not set.");
            throw this.initException;
        }
        if (this.pattern != null) {
            this.initException = new PluginConfigurationException("A single PluginCreateRule instance has been mapped to multiple patterns; this is not supported.");
            throw this.initException;
        }
        if (matchPattern.indexOf(42) != -1) {
            this.initException = new PluginConfigurationException("A PluginCreateRule instance has been mapped to pattern [" + matchPattern + "]." + " This pattern includes a wildcard character." + " This is not supported by the plugin architecture.");
            throw this.initException;
        }
        if (this.baseClass == null) {
            this.baseClass = class$java$lang$Object == null ? (class$java$lang$Object = PluginCreateRule.class$("java.lang.Object")) : class$java$lang$Object;
        }
        PluginRules rules = (PluginRules)this.digester.getRules();
        PluginManager pm = rules.getPluginManager();
        if (this.defaultPlugin != null) {
            if (!this.baseClass.isAssignableFrom(this.defaultPlugin.getPluginClass())) {
                this.initException = new PluginConfigurationException("Default class [" + this.defaultPlugin.getPluginClass().getName() + "] does not inherit from [" + this.baseClass.getName() + "].");
                throw this.initException;
            }
            try {
                this.defaultPlugin.init(this.digester, pm);
            }
            catch (PluginException pwe) {
                throw new PluginConfigurationException(pwe.getMessage(), pwe.getCause());
            }
        }
        this.pattern = matchPattern;
        if (this.pluginClassAttr == null) {
            this.pluginClassAttrNs = rules.getPluginClassAttrNs();
            this.pluginClassAttr = rules.getPluginClassAttr();
            if (debug) {
                log.debug((Object)("init: pluginClassAttr set to per-digester values [ns=" + this.pluginClassAttrNs + ", name=" + this.pluginClassAttr + "]"));
            }
        } else if (debug) {
            log.debug((Object)("init: pluginClassAttr set to rule-specific values [ns=" + this.pluginClassAttrNs + ", name=" + this.pluginClassAttr + "]"));
        }
        if (this.pluginIdAttr == null) {
            this.pluginIdAttrNs = rules.getPluginIdAttrNs();
            this.pluginIdAttr = rules.getPluginIdAttr();
            if (debug) {
                log.debug((Object)("init: pluginIdAttr set to per-digester values [ns=" + this.pluginIdAttrNs + ", name=" + this.pluginIdAttr + "]"));
            }
        } else if (debug) {
            log.debug((Object)("init: pluginIdAttr set to rule-specific values [ns=" + this.pluginIdAttrNs + ", name=" + this.pluginIdAttr + "]"));
        }
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Log log = this.digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("PluginCreateRule.begin: pattern=[" + this.pattern + "]" + " match=[" + this.digester.getMatch() + "]"));
        }
        if (this.initException != null) {
            throw this.initException;
        }
        PluginRules oldRules = (PluginRules)this.digester.getRules();
        PluginManager pluginManager = oldRules.getPluginManager();
        Declaration currDeclaration = null;
        String pluginClassName = this.pluginClassAttrNs == null ? attributes.getValue(this.pluginClassAttr) : attributes.getValue(this.pluginClassAttrNs, this.pluginClassAttr);
        String pluginId = this.pluginIdAttrNs == null ? attributes.getValue(this.pluginIdAttr) : attributes.getValue(this.pluginIdAttrNs, this.pluginIdAttr);
        if (pluginClassName != null) {
            currDeclaration = pluginManager.getDeclarationByClass(pluginClassName);
            if (currDeclaration == null) {
                currDeclaration = new Declaration(pluginClassName);
                try {
                    currDeclaration.init(this.digester, pluginManager);
                }
                catch (PluginException pwe) {
                    throw new PluginInvalidInputException(pwe.getMessage(), pwe.getCause());
                }
                pluginManager.addDeclaration(currDeclaration);
            }
        } else if (pluginId != null) {
            currDeclaration = pluginManager.getDeclarationById(pluginId);
            if (currDeclaration == null) {
                throw new PluginInvalidInputException("Plugin id [" + pluginId + "] is not defined.");
            }
        } else if (this.defaultPlugin != null) {
            currDeclaration = this.defaultPlugin;
        } else {
            throw new PluginInvalidInputException("No plugin class specified for element " + this.pattern);
        }
        Class pluginClass = currDeclaration.getPluginClass();
        String path = this.digester.getMatch();
        PluginRules newRules = new PluginRules(this.digester, path, oldRules, pluginClass);
        this.digester.setRules(newRules);
        if (debug) {
            log.debug((Object)("PluginCreateRule.begin: installing new plugin: oldrules=" + oldRules.toString() + ", newrules=" + newRules.toString()));
        }
        currDeclaration.configure(this.digester, this.pattern);
        Object instance = pluginClass.newInstance();
        this.getDigester().push(instance);
        if (debug) {
            log.debug((Object)("PluginCreateRule.begin: pattern=[" + this.pattern + "]" + " match=[" + this.digester.getMatch() + "]" + " pushed instance of plugin [" + pluginClass.getName() + "]"));
        }
        List rules = newRules.getDecoratedRules().match(namespace, path);
        this.fireBeginMethods(rules, namespace, name, attributes);
    }

    public void body(String namespace, String name, String text) throws Exception {
        String path = this.digester.getMatch();
        PluginRules newRules = (PluginRules)this.digester.getRules();
        List rules = newRules.getDecoratedRules().match(namespace, path);
        this.fireBodyMethods(rules, namespace, name, text);
    }

    public void end(String namespace, String name) throws Exception {
        String path = this.digester.getMatch();
        PluginRules newRules = (PluginRules)this.digester.getRules();
        List rules = newRules.getDecoratedRules().match(namespace, path);
        this.fireEndMethods(rules, namespace, name);
        this.digester.setRules(newRules.getParent());
        this.digester.pop();
    }

    public String getPattern() {
        return this.pattern;
    }

    public void fireBeginMethods(List rules, String namespace, String name, Attributes list) throws Exception {
        if (rules != null && rules.size() > 0) {
            Log log = this.digester.getLogger();
            boolean debug = log.isDebugEnabled();
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    Rule rule = (Rule)rules.get(i);
                    if (debug) {
                        log.debug((Object)("  Fire begin() for " + rule));
                    }
                    rule.begin(namespace, name, list);
                    continue;
                }
                catch (Exception e) {
                    throw this.digester.createSAXException(e);
                }
                catch (Error e) {
                    throw e;
                }
            }
        }
    }

    private void fireBodyMethods(List rules, String namespaceURI, String name, String text) throws Exception {
        if (rules != null && rules.size() > 0) {
            Log log = this.digester.getLogger();
            boolean debug = log.isDebugEnabled();
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    Rule rule = (Rule)rules.get(i);
                    if (debug) {
                        log.debug((Object)("  Fire body() for " + rule));
                    }
                    rule.body(namespaceURI, name, text);
                    continue;
                }
                catch (Exception e) {
                    throw this.digester.createSAXException(e);
                }
                catch (Error e) {
                    throw e;
                }
            }
        }
    }

    public void fireEndMethods(List rules, String namespaceURI, String name) throws Exception {
        if (rules != null) {
            Log log = this.digester.getLogger();
            boolean debug = log.isDebugEnabled();
            for (int i = 0; i < rules.size(); ++i) {
                int j = rules.size() - i - 1;
                try {
                    Rule rule = (Rule)rules.get(j);
                    if (debug) {
                        log.debug((Object)("  Fire end() for " + rule));
                    }
                    rule.end(namespaceURI, name);
                    continue;
                }
                catch (Exception e) {
                    throw this.digester.createSAXException(e);
                }
                catch (Error e) {
                    throw e;
                }
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

