/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.BeanUtils
 *  org.apache.commons.beanutils.DynaBean
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

public class SetNestedPropertiesRule
extends Rule {
    private Log log = null;
    private boolean trimData = true;
    private boolean allowUnknownChildElements = false;
    private HashMap<String, String> elementNames = new HashMap();

    public SetNestedPropertiesRule() {
    }

    public SetNestedPropertiesRule(String elementName, String propertyName) {
        this.elementNames.put(elementName, propertyName);
    }

    public SetNestedPropertiesRule(String[] elementNames, String[] propertyNames) {
        int size = elementNames.length;
        for (int i = 0; i < size; ++i) {
            String propName = null;
            if (i < propertyNames.length) {
                propName = propertyNames[i];
            }
            this.elementNames.put(elementNames[i], propName);
        }
    }

    public void setDigester(Digester digester) {
        super.setDigester(digester);
        this.log = digester.getLogger();
    }

    public void setTrimData(boolean trimData) {
        this.trimData = trimData;
    }

    public boolean getTrimData() {
        return this.trimData;
    }

    public void setAllowUnknownChildElements(boolean allowUnknownChildElements) {
        this.allowUnknownChildElements = allowUnknownChildElements;
    }

    public boolean getAllowUnknownChildElements() {
        return this.allowUnknownChildElements;
    }

    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Rules oldRules = this.digester.getRules();
        AnyChildRule anyChildRule = new AnyChildRule();
        anyChildRule.setDigester(this.digester);
        AnyChildRules newRules = new AnyChildRules(anyChildRule);
        newRules.init(this.digester.getMatch() + "/", oldRules);
        this.digester.setRules(newRules);
    }

    public void body(String bodyText) throws Exception {
        AnyChildRules newRules = (AnyChildRules)this.digester.getRules();
        this.digester.setRules(newRules.getOldRules());
    }

    public void addAlias(String elementName, String propertyName) {
        this.elementNames.put(elementName, propertyName);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SetNestedPropertiesRule[");
        sb.append("allowUnknownChildElements=");
        sb.append(this.allowUnknownChildElements);
        sb.append(", trimData=");
        sb.append(this.trimData);
        sb.append(", elementNames=");
        sb.append(this.elementNames);
        sb.append("]");
        return sb.toString();
    }

    private class AnyChildRule
    extends Rule {
        private String currChildNamespaceURI = null;
        private String currChildElementName = null;

        private AnyChildRule() {
        }

        public void begin(String namespaceURI, String name, Attributes attributes) throws Exception {
            this.currChildNamespaceURI = namespaceURI;
            this.currChildElementName = name;
        }

        public void body(String value) throws Exception {
            PropertyDescriptor desc;
            String propName = this.currChildElementName;
            if (SetNestedPropertiesRule.this.elementNames.containsKey(this.currChildElementName) && (propName = (String)SetNestedPropertiesRule.this.elementNames.get(this.currChildElementName)) == null) {
                return;
            }
            boolean debug = SetNestedPropertiesRule.this.log.isDebugEnabled();
            if (debug) {
                SetNestedPropertiesRule.this.log.debug((Object)("[SetNestedPropertiesRule]{" + this.digester.match + "} Setting property '" + propName + "' to '" + value + "'"));
            }
            Object top = this.digester.peek();
            if (debug) {
                if (top != null) {
                    SetNestedPropertiesRule.this.log.debug((Object)("[SetNestedPropertiesRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " properties"));
                } else {
                    SetNestedPropertiesRule.this.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Set NULL properties"));
                }
            }
            if (SetNestedPropertiesRule.this.trimData) {
                value = value.trim();
            }
            if (!SetNestedPropertiesRule.this.allowUnknownChildElements && (top instanceof DynaBean ? (desc = ((DynaBean)top).getDynaClass().getDynaProperty(propName)) == null : (desc = PropertyUtils.getPropertyDescriptor((Object)top, (String)propName)) == null)) {
                throw new NoSuchMethodException("Bean has no property named " + propName);
            }
            try {
                BeanUtils.setProperty((Object)top, (String)propName, (Object)value);
            }
            catch (NullPointerException e) {
                SetNestedPropertiesRule.this.log.error((Object)("NullPointerException: top=" + top + ",propName=" + propName + ",value=" + value + "!"));
                throw e;
            }
        }

        public void end(String namespace, String name) throws Exception {
            this.currChildElementName = null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class AnyChildRules
    implements Rules {
        private String matchPrefix = null;
        private Rules decoratedRules = null;
        private ArrayList<Rule> rules = new ArrayList(1);
        private AnyChildRule rule;

        public AnyChildRules(AnyChildRule rule) {
            this.rule = rule;
            this.rules.add(rule);
        }

        @Override
        public Digester getDigester() {
            return null;
        }

        @Override
        public void setDigester(Digester digester) {
        }

        @Override
        public String getNamespaceURI() {
            return null;
        }

        @Override
        public void setNamespaceURI(String namespaceURI) {
        }

        @Override
        public void add(String pattern, Rule rule) {
        }

        @Override
        public void clear() {
        }

        @Override
        public List<Rule> match(String matchPath) {
            return this.match(null, matchPath);
        }

        @Override
        public List<Rule> match(String namespaceURI, String matchPath) {
            List<Rule> match = this.decoratedRules.match(namespaceURI, matchPath);
            if (matchPath.startsWith(this.matchPrefix) && matchPath.indexOf(47, this.matchPrefix.length()) == -1) {
                if (match == null || match.size() == 0) {
                    return this.rules;
                }
                LinkedList<Rule> newMatch = new LinkedList<Rule>(match);
                newMatch.addLast(this.rule);
                return newMatch;
            }
            return match;
        }

        @Override
        public List<Rule> rules() {
            SetNestedPropertiesRule.this.log.debug((Object)"AnyChildRules.rules invoked.");
            return this.decoratedRules.rules();
        }

        public void init(String prefix, Rules rules) {
            this.matchPrefix = prefix;
            this.decoratedRules = rules;
        }

        public Rules getOldRules() {
            return this.decoratedRules;
        }
    }
}

