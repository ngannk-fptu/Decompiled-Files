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
import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.Property;
import org.apache.velocity.tools.config.ToolConfiguration;
import org.apache.velocity.tools.config.ToolboxConfiguration;
import org.xml.sax.Attributes;

public class XmlFactoryConfigurationRuleSet
extends RuleSetBase {
    protected Class toolboxConfigurationClass = ToolboxConfiguration.class;
    protected Class toolConfigurationClass = ToolConfiguration.class;
    protected Class dataClass = Data.class;
    protected Class propertyClass = Property.class;

    public void setToolboxConfigurationClass(Class clazz) {
        this.toolboxConfigurationClass = clazz;
    }

    public void setToolConfigurationClass(Class clazz) {
        this.toolConfigurationClass = clazz;
    }

    public void setDataClass(Class clazz) {
        this.dataClass = clazz;
    }

    public void setPropertyClass(Class clazz) {
        this.propertyClass = clazz;
    }

    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate("tools/property", this.propertyClass);
        digester.addObjectCreate("tools/*/property", this.propertyClass);
        digester.addObjectCreate("tools/data", this.dataClass);
        digester.addObjectCreate("tools/toolbox", this.toolboxConfigurationClass);
        digester.addObjectCreate("tools/toolbox/tool", this.toolConfigurationClass);
        digester.addSetProperties("tools/property");
        digester.addSetProperties("tools/*/property");
        digester.addSetProperties("tools");
        digester.addSetProperties("tools/data");
        digester.addSetProperties("tools/toolbox");
        digester.addSetProperties("tools/toolbox/tool");
        digester.addRule("tools", (Rule)new PropertyAttributeRule());
        digester.addRule("tools/toolbox", (Rule)new PropertyAttributeRule());
        digester.addRule("tools/toolbox/tool", (Rule)new PropertyAttributeRule());
        digester.addRule("tools/data", (Rule)new DataValueInBodyRule());
        digester.addRule("tools/*/property", (Rule)new DataValueInBodyRule());
        digester.addSetNext("tools/property", "addProperty");
        digester.addSetNext("tools/*/property", "addProperty");
        digester.addSetNext("tools/data", "addData");
        digester.addSetNext("tools/toolbox", "addToolbox");
        digester.addSetNext("tools/toolbox/tool", "addTool");
    }

    public static class PropertyAttributeRule
    extends Rule {
        public void begin(String namespace, String element, Attributes attributes) throws Exception {
            Configuration config = (Configuration)this.digester.peek();
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getLocalName(i);
                if ("".equals(name)) {
                    name = attributes.getQName(i);
                }
                if ("class".equals(name)) continue;
                String value = attributes.getValue(i);
                config.setProperty(name, value);
            }
        }
    }

    public static class DataValueInBodyRule
    extends Rule {
        public void body(String namespace, String element, String value) throws Exception {
            Data data = (Data)this.digester.peek();
            if (data.getValue() == null) {
                data.setValue(value);
            }
        }
    }
}

