/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class SetPropertyRule
extends Rule {
    protected String name = null;
    protected String value = null;

    public SetPropertyRule(Digester digester, String name, String value) {
        this(name, value);
    }

    public SetPropertyRule(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void begin(Attributes attributes) throws Exception {
        Object desc;
        if (attributes.getLength() == 0) {
            return;
        }
        String actualName = null;
        String actualValue = null;
        for (int i = 0; i < attributes.getLength(); ++i) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            if (name.equals(this.name)) {
                actualName = value;
                continue;
            }
            if (!name.equals(this.value)) continue;
            actualValue = value;
        }
        Object top = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[SetPropertyRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " property " + actualName + " to " + actualValue));
        }
        if (top instanceof DynaBean ? (desc = ((DynaBean)top).getDynaClass().getDynaProperty(actualName)) == null : (desc = PropertyUtils.getPropertyDescriptor(top, actualName)) == null) {
            throw new NoSuchMethodException("Bean has no property named " + actualName);
        }
        BeanUtils.setProperty(top, actualName, actualValue);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SetPropertyRule[");
        sb.append("name=");
        sb.append(this.name);
        sb.append(", value=");
        sb.append(this.value);
        sb.append("]");
        return sb.toString();
    }
}

