/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.HashMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class SetPropertiesRule
extends Rule {
    private String[] attributeNames;
    private String[] propertyNames;
    private boolean ignoreMissingProperty = true;

    public SetPropertiesRule(Digester digester) {
        this();
    }

    public SetPropertiesRule() {
    }

    public SetPropertiesRule(String attributeName, String propertyName) {
        this.attributeNames = new String[1];
        this.attributeNames[0] = attributeName;
        this.propertyNames = new String[1];
        this.propertyNames[0] = propertyName;
    }

    public SetPropertiesRule(String[] attributeNames, String[] propertyNames) {
        int i;
        this.attributeNames = new String[attributeNames.length];
        int size = attributeNames.length;
        for (i = 0; i < size; ++i) {
            this.attributeNames[i] = attributeNames[i];
        }
        this.propertyNames = new String[propertyNames.length];
        size = propertyNames.length;
        for (i = 0; i < size; ++i) {
            this.propertyNames[i] = propertyNames[i];
        }
    }

    public void begin(Attributes attributes) throws Exception {
        HashMap<String, String> values = new HashMap<String, String>();
        int attNamesLength = 0;
        if (this.attributeNames != null) {
            attNamesLength = this.attributeNames.length;
        }
        int propNamesLength = 0;
        if (this.propertyNames != null) {
            propNamesLength = this.propertyNames.length;
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            Object top;
            boolean test;
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            for (int n = 0; n < attNamesLength; ++n) {
                if (!name.equals(this.attributeNames[n])) continue;
                if (n < propNamesLength) {
                    name = this.propertyNames[n];
                    break;
                }
                name = null;
                break;
            }
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Setting property '" + name + "' to '" + value + "'"));
            }
            if (!this.ignoreMissingProperty && name != null && !(test = PropertyUtils.isWriteable(top = this.digester.peek(), name))) {
                throw new NoSuchMethodException("Property " + name + " can't be set");
            }
            if (name == null) continue;
            values.put(name, value);
        }
        Object top = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            if (top != null) {
                this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " properties"));
            } else {
                this.digester.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Set NULL properties"));
            }
        }
        BeanUtils.populate(top, values);
    }

    public void addAlias(String attributeName, String propertyName) {
        if (this.attributeNames == null) {
            this.attributeNames = new String[1];
            this.attributeNames[0] = attributeName;
            this.propertyNames = new String[1];
            this.propertyNames[0] = propertyName;
        } else {
            int length = this.attributeNames.length;
            String[] tempAttributes = new String[length + 1];
            for (int i = 0; i < length; ++i) {
                tempAttributes[i] = this.attributeNames[i];
            }
            tempAttributes[length] = attributeName;
            String[] tempProperties = new String[length + 1];
            for (int i = 0; i < length && i < this.propertyNames.length; ++i) {
                tempProperties[i] = this.propertyNames[i];
            }
            tempProperties[length] = propertyName;
            this.propertyNames = tempProperties;
            this.attributeNames = tempAttributes;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SetPropertiesRule[");
        sb.append("]");
        return sb.toString();
    }

    public boolean isIgnoreMissingProperty() {
        return this.ignoreMissingProperty;
    }

    public void setIgnoreMissingProperty(boolean ignoreMissingProperty) {
        this.ignoreMissingProperty = ignoreMissingProperty;
    }
}

