/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.ConvertUtils
 */
package org.apache.commons.digester.xmlrules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.NodeCreateRule;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.ObjectParamRule;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.commons.digester.SetPropertyRule;
import org.apache.commons.digester.SetRootRule;
import org.apache.commons.digester.SetTopRule;
import org.apache.commons.digester.xmlrules.CircularIncludeException;
import org.apache.commons.digester.xmlrules.DigesterRulesSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DigesterRuleParser
extends RuleSetBase {
    public static final String DIGESTER_PUBLIC_ID = "-//Jakarta Apache //DTD digester-rules XML V1.0//EN";
    private String digesterDtdUrl;
    protected Digester targetDigester;
    protected String basePath = "";
    protected PatternStack<String> patternStack;
    private Set<String> includedFiles = new HashSet<String>();

    public DigesterRuleParser() {
        this.patternStack = new PatternStack();
    }

    public DigesterRuleParser(Digester targetDigester) {
        this.targetDigester = targetDigester;
        this.patternStack = new PatternStack();
    }

    private DigesterRuleParser(Digester targetDigester, PatternStack<String> stack, Set<String> includedFiles) {
        this.targetDigester = targetDigester;
        this.patternStack = stack;
        this.includedFiles = includedFiles;
    }

    public void setTarget(Digester d) {
        this.targetDigester = d;
    }

    public void setBasePath(String path) {
        this.basePath = path == null ? "" : (path.length() > 0 && !path.endsWith("/") ? path + "/" : path);
    }

    public void setDigesterRulesDTD(String dtdURL) {
        this.digesterDtdUrl = dtdURL;
    }

    protected String getDigesterRulesDTD() {
        return this.digesterDtdUrl;
    }

    public void add(Rule rule) {
        this.targetDigester.addRule(this.basePath + this.patternStack.toString(), rule);
    }

    @Override
    public void addRuleInstances(Digester digester) {
        String ruleClassName = Rule.class.getName();
        digester.register(DIGESTER_PUBLIC_ID, this.getDigesterRulesDTD());
        digester.addRule("*/pattern", new PatternRule("value"));
        digester.addRule("*/include", new IncludeRule());
        digester.addFactoryCreate("*/bean-property-setter-rule", new BeanPropertySetterRuleFactory());
        digester.addRule("*/bean-property-setter-rule", new PatternRule("pattern"));
        digester.addSetNext("*/bean-property-setter-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/call-method-rule", new CallMethodRuleFactory());
        digester.addRule("*/call-method-rule", new PatternRule("pattern"));
        digester.addSetNext("*/call-method-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/object-param-rule", new ObjectParamRuleFactory());
        digester.addRule("*/object-param-rule", new PatternRule("pattern"));
        digester.addSetNext("*/object-param-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/call-param-rule", new CallParamRuleFactory());
        digester.addRule("*/call-param-rule", new PatternRule("pattern"));
        digester.addSetNext("*/call-param-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/factory-create-rule", new FactoryCreateRuleFactory());
        digester.addRule("*/factory-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/factory-create-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/object-create-rule", new ObjectCreateRuleFactory());
        digester.addRule("*/object-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/object-create-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/node-create-rule", new NodeCreateRuleFactory());
        digester.addRule("*/node-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/node-create-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-properties-rule", new SetPropertiesRuleFactory());
        digester.addRule("*/set-properties-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-properties-rule", "add", ruleClassName);
        digester.addRule("*/set-properties-rule/alias", new SetPropertiesAliasRule());
        digester.addFactoryCreate("*/set-property-rule", new SetPropertyRuleFactory());
        digester.addRule("*/set-property-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-property-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-nested-properties-rule", new SetNestedPropertiesRuleFactory());
        digester.addRule("*/set-nested-properties-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-nested-properties-rule", "add", ruleClassName);
        digester.addRule("*/set-nested-properties-rule/alias", new SetNestedPropertiesAliasRule());
        digester.addFactoryCreate("*/set-top-rule", new SetTopRuleFactory());
        digester.addRule("*/set-top-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-top-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-next-rule", new SetNextRuleFactory());
        digester.addRule("*/set-next-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-next-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-root-rule", new SetRootRuleFactory());
        digester.addRule("*/set-root-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-root-rule", "add", ruleClassName);
    }

    protected class SetNestedPropertiesAliasRule
    extends Rule {
        public void begin(Attributes attributes) {
            String attrName = attributes.getValue("attr-name");
            String propName = attributes.getValue("prop-name");
            SetNestedPropertiesRule rule = (SetNestedPropertiesRule)this.digester.peek();
            rule.addAlias(attrName, propName);
        }
    }

    protected class SetPropertiesAliasRule
    extends Rule {
        public void begin(Attributes attributes) {
            String attrName = attributes.getValue("attr-name");
            String propName = attributes.getValue("prop-name");
            SetPropertiesRule rule = (SetPropertiesRule)this.digester.peek();
            rule.addAlias(attrName, propName);
        }
    }

    protected class SetRootRuleFactory
    extends AbstractObjectCreationFactory {
        protected SetRootRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return paramType == null || paramType.length() == 0 ? new SetRootRule(methodName) : new SetRootRule(methodName, paramType);
        }
    }

    protected class SetNextRuleFactory
    extends AbstractObjectCreationFactory {
        protected SetNextRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return paramType == null || paramType.length() == 0 ? new SetNextRule(methodName) : new SetNextRule(methodName, paramType);
        }
    }

    protected class SetTopRuleFactory
    extends AbstractObjectCreationFactory {
        protected SetTopRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return paramType == null || paramType.length() == 0 ? new SetTopRule(methodName) : new SetTopRule(methodName, paramType);
        }
    }

    protected class SetNestedPropertiesRuleFactory
    extends AbstractObjectCreationFactory {
        protected SetNestedPropertiesRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            boolean allowUnknownChildElements = "true".equalsIgnoreCase(attributes.getValue("allow-unknown-child-elements"));
            SetNestedPropertiesRule snpr = new SetNestedPropertiesRule();
            snpr.setAllowUnknownChildElements(allowUnknownChildElements);
            return snpr;
        }
    }

    protected class SetPropertyRuleFactory
    extends AbstractObjectCreationFactory {
        protected SetPropertyRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            return new SetPropertyRule(name, value);
        }
    }

    protected class SetPropertiesRuleFactory
    extends AbstractObjectCreationFactory {
        protected SetPropertiesRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            return new SetPropertiesRule();
        }
    }

    protected class ObjectCreateRuleFactory
    extends AbstractObjectCreationFactory {
        protected ObjectCreateRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            String className = attributes.getValue("classname");
            String attrName = attributes.getValue("attrname");
            return attrName == null || attrName.length() == 0 ? new ObjectCreateRule(className) : new ObjectCreateRule(className, attrName);
        }
    }

    protected class FactoryCreateRuleFactory
    extends AbstractObjectCreationFactory {
        protected FactoryCreateRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            String className = attributes.getValue("classname");
            String attrName = attributes.getValue("attrname");
            boolean ignoreExceptions = "true".equalsIgnoreCase(attributes.getValue("ignore-exceptions"));
            return attrName == null || attrName.length() == 0 ? new FactoryCreateRule(className, ignoreExceptions) : new FactoryCreateRule(className, attrName, ignoreExceptions);
        }
    }

    protected class NodeCreateRuleFactory
    extends AbstractObjectCreationFactory {
        protected NodeCreateRuleFactory() {
        }

        public Object createObject(Attributes attributes) throws Exception {
            String nodeType = attributes.getValue("type");
            if (nodeType == null || "".equals(nodeType)) {
                return new NodeCreateRule();
            }
            if ("element".equals(nodeType)) {
                return new NodeCreateRule(1);
            }
            if ("fragment".equals(nodeType)) {
                return new NodeCreateRule(11);
            }
            throw new RuntimeException("Unrecognized node type: " + nodeType + ".  This attribute is optional or can have a value of element|fragment.");
        }
    }

    protected class ObjectParamRuleFactory
    extends AbstractObjectCreationFactory {
        protected ObjectParamRuleFactory() {
        }

        public Object createObject(Attributes attributes) throws Exception {
            int paramIndex = Integer.parseInt(attributes.getValue("paramnumber"));
            String attributeName = attributes.getValue("attrname");
            String type = attributes.getValue("type");
            String value = attributes.getValue("value");
            ObjectParamRule objectParamRule = null;
            if (type == null) {
                throw new RuntimeException("Attribute 'type' is required.");
            }
            Object param = null;
            Class<?> clazz = Class.forName(type);
            param = value == null ? (Object)clazz.newInstance() : ConvertUtils.convert((String)value, clazz);
            objectParamRule = attributeName == null ? new ObjectParamRule(paramIndex, param) : new ObjectParamRule(paramIndex, attributeName, param);
            return objectParamRule;
        }
    }

    protected class CallParamRuleFactory
    extends AbstractObjectCreationFactory {
        protected CallParamRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            int paramIndex = Integer.parseInt(attributes.getValue("paramnumber"));
            String attributeName = attributes.getValue("attrname");
            String fromStack = attributes.getValue("from-stack");
            String stackIndex = attributes.getValue("stack-index");
            CallParamRule callParamRule = null;
            if (attributeName == null) {
                callParamRule = stackIndex != null ? new CallParamRule(paramIndex, Integer.parseInt(stackIndex)) : (fromStack != null ? new CallParamRule(paramIndex, Boolean.valueOf(fromStack)) : new CallParamRule(paramIndex));
            } else if (fromStack == null) {
                callParamRule = new CallParamRule(paramIndex, attributeName);
            } else {
                throw new RuntimeException("Attributes from-stack and attrname cannot both be present.");
            }
            return callParamRule;
        }
    }

    protected class CallMethodRuleFactory
    extends AbstractObjectCreationFactory {
        protected CallMethodRuleFactory() {
        }

        public Object createObject(Attributes attributes) {
            CallMethodRule callMethodRule = null;
            String methodName = attributes.getValue("methodname");
            int targetOffset = 0;
            String targetOffsetStr = attributes.getValue("targetoffset");
            if (targetOffsetStr != null) {
                targetOffset = Integer.parseInt(targetOffsetStr);
            }
            if (attributes.getValue("paramcount") == null) {
                callMethodRule = new CallMethodRule(targetOffset, methodName);
            } else {
                int paramCount = Integer.parseInt(attributes.getValue("paramcount"));
                String paramTypesAttr = attributes.getValue("paramtypes");
                if (paramTypesAttr == null || paramTypesAttr.length() == 0) {
                    callMethodRule = new CallMethodRule(targetOffset, methodName, paramCount);
                } else {
                    String[] paramTypes = this.getParamTypes(paramTypesAttr);
                    callMethodRule = new CallMethodRule(targetOffset, methodName, paramCount, paramTypes);
                }
            }
            return callMethodRule;
        }

        private String[] getParamTypes(String paramTypes) {
            String[] paramTypesArray;
            if (paramTypes != null) {
                ArrayList<String> paramTypesList = new ArrayList<String>();
                StringTokenizer tokens = new StringTokenizer(paramTypes, " \t\n\r,");
                while (tokens.hasMoreTokens()) {
                    paramTypesList.add(tokens.nextToken());
                }
                paramTypesArray = paramTypesList.toArray(new String[0]);
            } else {
                paramTypesArray = new String[]{};
            }
            return paramTypesArray;
        }
    }

    private class BeanPropertySetterRuleFactory
    extends AbstractObjectCreationFactory {
        private BeanPropertySetterRuleFactory() {
        }

        public Object createObject(Attributes attributes) throws Exception {
            BeanPropertySetterRule beanPropertySetterRule = null;
            String propertyname = attributes.getValue("propertyname");
            beanPropertySetterRule = propertyname == null ? new BeanPropertySetterRule() : new BeanPropertySetterRule(propertyname);
            return beanPropertySetterRule;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class RulesPrefixAdapter
    implements Rules {
        private Rules delegate;
        private String prefix;

        public RulesPrefixAdapter(String patternPrefix, Rules rules) {
            this.prefix = patternPrefix;
            this.delegate = rules;
        }

        @Override
        public void add(String pattern, Rule rule) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(this.prefix);
            if (!pattern.startsWith("/")) {
                buffer.append('/');
            }
            buffer.append(pattern);
            this.delegate.add(buffer.toString(), rule);
        }

        @Override
        public void clear() {
            this.delegate.clear();
        }

        @Override
        public Digester getDigester() {
            return this.delegate.getDigester();
        }

        @Override
        public String getNamespaceURI() {
            return this.delegate.getNamespaceURI();
        }

        @Override
        @Deprecated
        public List<Rule> match(String pattern) {
            return this.delegate.match(pattern);
        }

        @Override
        public List<Rule> match(String namespaceURI, String pattern) {
            return this.delegate.match(namespaceURI, pattern);
        }

        @Override
        public List<Rule> rules() {
            return this.delegate.rules();
        }

        @Override
        public void setDigester(Digester digester) {
            this.delegate.setDigester(digester);
        }

        @Override
        public void setNamespaceURI(String namespaceURI) {
            this.delegate.setNamespaceURI(namespaceURI);
        }
    }

    private class IncludeRule
    extends Rule {
        public void begin(Attributes attributes) throws Exception {
            String className;
            String fileName = attributes.getValue("path");
            if (fileName != null && fileName.length() > 0) {
                this.includeXMLRules(fileName);
            }
            if ((className = attributes.getValue("class")) != null && className.length() > 0) {
                this.includeProgrammaticRules(className);
            }
        }

        private void includeXMLRules(String fileName) throws IOException, SAXException, CircularIncludeException {
            URL fileURL;
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = DigesterRuleParser.this.getClass().getClassLoader();
            }
            if ((fileURL = cl.getResource(fileName)) == null) {
                throw new FileNotFoundException("File \"" + fileName + "\" not found.");
            }
            fileName = fileURL.toExternalForm();
            if (!DigesterRuleParser.this.includedFiles.add(fileName)) {
                throw new CircularIncludeException(fileName);
            }
            DigesterRuleParser includedSet = new DigesterRuleParser(DigesterRuleParser.this.targetDigester, DigesterRuleParser.this.patternStack, DigesterRuleParser.this.includedFiles);
            includedSet.setDigesterRulesDTD(DigesterRuleParser.this.getDigesterRulesDTD());
            Digester digester = new Digester();
            digester.addRuleSet(includedSet);
            digester.push(DigesterRuleParser.this);
            digester.parse(fileName);
            DigesterRuleParser.this.includedFiles.remove(fileName);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void includeProgrammaticRules(String className) throws ClassNotFoundException, ClassCastException, InstantiationException, IllegalAccessException {
            Class<?> cls = Class.forName(className);
            DigesterRulesSource rulesSource = (DigesterRulesSource)cls.newInstance();
            Rules digesterRules = DigesterRuleParser.this.targetDigester.getRules();
            RulesPrefixAdapter prefixWrapper = new RulesPrefixAdapter(DigesterRuleParser.this.patternStack.toString(), digesterRules);
            DigesterRuleParser.this.targetDigester.setRules(prefixWrapper);
            try {
                rulesSource.getRules(DigesterRuleParser.this.targetDigester);
            }
            finally {
                DigesterRuleParser.this.targetDigester.setRules(digesterRules);
            }
        }
    }

    private class PatternRule
    extends Rule {
        private String attrName;
        private String pattern = null;

        public PatternRule(String attrName) {
            this.attrName = attrName;
        }

        public void begin(Attributes attributes) {
            this.pattern = attributes.getValue(this.attrName);
            if (this.pattern != null) {
                DigesterRuleParser.this.patternStack.push(this.pattern);
            }
        }

        public void end() {
            if (this.pattern != null) {
                DigesterRuleParser.this.patternStack.pop();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class PatternStack<E>
    extends Stack<E> {
        private static final long serialVersionUID = 1L;

        protected PatternStack() {
        }

        @Override
        public String toString() {
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < this.size(); ++i) {
                String elem = this.get(i).toString();
                if (elem.length() <= 0) continue;
                if (str.length() > 0) {
                    str.append('/');
                }
                str.append(elem);
            }
            return str.toString();
        }
    }
}

