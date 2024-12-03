/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.core;

import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.IterableNamingEnumeration;
import org.springframework.ldap.core.NameAwareAttribute;
import org.springframework.util.Assert;

public final class NameAwareAttributes
implements Attributes {
    private Map<String, NameAwareAttribute> attributes = new HashMap<String, NameAwareAttribute>();

    public NameAwareAttributes() {
    }

    public NameAwareAttributes(Attributes attributes) {
        NamingEnumeration<? extends Attribute> allAttributes = attributes.getAll();
        while (allAttributes.hasMoreElements()) {
            Attribute attribute = (Attribute)allAttributes.nextElement();
            this.put(new NameAwareAttribute(attribute));
        }
    }

    @Override
    public boolean isCaseIgnored() {
        return true;
    }

    @Override
    public int size() {
        return this.attributes.size();
    }

    @Override
    public NameAwareAttribute get(String attrID) {
        Assert.hasLength((String)attrID, (String)"Attribute ID must not be empty");
        return this.attributes.get(attrID.toLowerCase());
    }

    public NamingEnumeration<NameAwareAttribute> getAll() {
        return new IterableNamingEnumeration<NameAwareAttribute>(this.attributes.values());
    }

    @Override
    public NamingEnumeration<String> getIDs() {
        return new IterableNamingEnumeration<String>(this.attributes.keySet());
    }

    @Override
    public Attribute put(String attrID, Object val) {
        Assert.hasLength((String)attrID, (String)"Attribute ID must not be empty");
        NameAwareAttribute newAttribute = new NameAwareAttribute(attrID, val);
        this.attributes.put(attrID.toLowerCase(), newAttribute);
        return newAttribute;
    }

    @Override
    public Attribute put(Attribute attr) {
        Assert.notNull((Object)attr, (String)"Attribute must not be null");
        NameAwareAttribute newAttribute = new NameAwareAttribute(attr);
        this.attributes.put(attr.getID().toLowerCase(), newAttribute);
        return newAttribute;
    }

    @Override
    public Attribute remove(String attrID) {
        Assert.hasLength((String)attrID, (String)"Attribute ID must not be empty");
        return this.attributes.remove(attrID.toLowerCase());
    }

    @Override
    public Object clone() {
        return new NameAwareAttributes(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NameAwareAttributes that = (NameAwareAttributes)o;
        return !(this.attributes != null ? !this.attributes.equals(that.attributes) : that.attributes != null);
    }

    public int hashCode() {
        return this.attributes != null ? this.attributes.hashCode() : 0;
    }

    public String toString() {
        return String.format("NameAwareAttribute; attributes: %s", this.attributes.toString());
    }
}

