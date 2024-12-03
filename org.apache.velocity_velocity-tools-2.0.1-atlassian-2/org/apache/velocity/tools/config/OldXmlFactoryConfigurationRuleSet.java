/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.digester.Digester
 *  org.apache.commons.digester.Rule
 *  org.apache.commons.digester.RuleSetBase
 */
package org.apache.velocity.tools.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.ToolConfiguration;
import org.apache.velocity.tools.config.ToolboxConfiguration;
import org.xml.sax.Attributes;

@Deprecated
public class OldXmlFactoryConfigurationRuleSet
extends RuleSetBase {
    public void addRuleInstances(Digester digester) {
        digester.addRule("toolbox/create-session", (Rule)new CreateSessionRule());
        digester.addRule("toolbox/xhtml", (Rule)new XhtmlRule());
        digester.addObjectCreate("toolbox", ToolboxConfiguration.class);
        digester.addRule("toolbox", (Rule)new DeprecationRule());
        digester.addSetNext("toolbox", "addToolbox");
        digester.addObjectCreate("toolbox/tool", ToolConfiguration.class);
        digester.addBeanPropertySetter("toolbox/tool/key", "key");
        digester.addBeanPropertySetter("toolbox/tool/class", "classname");
        digester.addBeanPropertySetter("toolbox/tool/request-path", "restrictTo");
        digester.addRule("toolbox/tool/scope", (Rule)new ScopeRule());
        digester.addRule("toolbox/tool/parameter", (Rule)new ParameterRule());
        digester.addSetNext("toolbox/tool", "addTool");
        digester.addObjectCreate("toolbox/data", Data.class);
        digester.addSetProperties("toolbox/data");
        digester.addBeanPropertySetter("toolbox/data/key", "key");
        digester.addBeanPropertySetter("toolbox/data/value", "value");
        digester.addRule("toolbox/data", (Rule)new SetNextDataRule());
    }

    protected static class XhtmlRule
    extends BooleanConfigRule {
        protected XhtmlRule() {
        }

        @Override
        public void setBoolean(FactoryConfiguration factory, Boolean b) {
            factory.setProperty("XHTML", b);
        }
    }

    protected static class CreateSessionRule
    extends BooleanConfigRule {
        protected CreateSessionRule() {
        }

        @Override
        public void setBoolean(FactoryConfiguration factory, Boolean b) {
            factory.setProperty("createSession", b);
        }
    }

    protected static abstract class BooleanConfigRule
    extends Rule {
        protected BooleanConfigRule() {
        }

        public void body(String ns, String name, String text) throws Exception {
            FactoryConfiguration factory = (FactoryConfiguration)this.digester.getRoot();
            if ("yes".equalsIgnoreCase(text)) {
                this.setBoolean(factory, Boolean.TRUE);
            } else {
                this.setBoolean(factory, Boolean.valueOf(text));
            }
        }

        public abstract void setBoolean(FactoryConfiguration var1, Boolean var2);
    }

    protected static class SetNextDataRule
    extends Rule {
        protected SetNextDataRule() {
        }

        public void end() throws Exception {
            Data data = (Data)this.digester.peek(0);
            FactoryConfiguration factory = (FactoryConfiguration)this.digester.getRoot();
            factory.addData(data);
        }
    }

    protected static class ParameterRule
    extends Rule {
        protected ParameterRule() {
        }

        public void begin(String ns, String ln, Attributes attributes) throws Exception {
            ToolConfiguration config = (ToolConfiguration)this.digester.peek();
            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            config.setProperty(name, value);
        }
    }

    protected static class ScopeRule
    extends Rule {
        protected ScopeRule() {
        }

        public void body(String namespace, String element, String value) throws Exception {
            ToolConfiguration tool = (ToolConfiguration)this.digester.peek(0);
            ToolboxConfiguration toolbox = (ToolboxConfiguration)this.digester.peek(1);
            if (value != null && !value.equals(toolbox.getScope())) {
                FactoryConfiguration factory = (FactoryConfiguration)this.digester.peek(2);
                factory.addToolbox(toolbox);
                this.digester.pop();
                this.digester.pop();
                ToolboxConfiguration newbox = new ToolboxConfiguration();
                newbox.setScope(value);
                this.digester.push((Object)newbox);
                this.digester.push((Object)tool);
            }
        }
    }

    protected static class DeprecationRule
    extends Rule {
        protected DeprecationRule() {
        }

        public void begin(String ns, String ln, Attributes attributes) throws Exception {
            FactoryConfiguration factory = (FactoryConfiguration)this.digester.getRoot();
            factory.setProperty("deprecationSupportMode", true);
        }
    }
}

