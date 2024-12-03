/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.BeanUtils
 *  org.apache.commons.beanutils.DynaBean
 *  org.apache.commons.beanutils.DynaProperty
 *  org.apache.commons.beanutils.PropertyUtils
 */
package org.apache.commons.digester;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

public class BeanPropertySetterRule
extends Rule {
    protected String propertyName = null;
    protected String bodyText = null;

    @Deprecated
    public BeanPropertySetterRule(Digester digester, String propertyName) {
        this(propertyName);
    }

    @Deprecated
    public BeanPropertySetterRule(Digester digester) {
        this();
    }

    public BeanPropertySetterRule(String propertyName) {
        this.propertyName = propertyName;
    }

    public BeanPropertySetterRule() {
        this((String)null);
    }

    public void body(String namespace, String name, String text) throws Exception {
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[BeanPropertySetterRule]{" + this.digester.match + "} Called with text '" + text + "'"));
        }
        this.bodyText = text.trim();
    }

    public void end(String namespace, String name) throws Exception {
        DynaProperty desc;
        String property = this.propertyName;
        if (property == null) {
            property = name;
        }
        Object top = this.digester.peek();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[BeanPropertySetterRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " property " + property + " with text " + this.bodyText));
        }
        if (top instanceof DynaBean ? (desc = ((DynaBean)top).getDynaClass().getDynaProperty(property)) == null : (desc = PropertyUtils.getPropertyDescriptor((Object)top, (String)property)) == null) {
            throw new NoSuchMethodException("Bean has no property named " + property);
        }
        BeanUtils.setProperty((Object)top, (String)property, (Object)this.bodyText);
    }

    public void finish() throws Exception {
        this.bodyText = null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("BeanPropertySetterRule[");
        sb.append("propertyName=");
        sb.append(this.propertyName);
        sb.append("]");
        return sb.toString();
    }
}

