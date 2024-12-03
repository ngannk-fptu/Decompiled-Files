/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.authority.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;
import org.springframework.util.Assert;

public class SimpleAttributes2GrantedAuthoritiesMapper
implements Attributes2GrantedAuthoritiesMapper,
InitializingBean {
    private String attributePrefix = "ROLE_";
    private boolean convertAttributeToUpperCase = false;
    private boolean convertAttributeToLowerCase = false;
    private boolean addPrefixIfAlreadyExisting = false;

    public void afterPropertiesSet() {
        Assert.isTrue((!this.isConvertAttributeToUpperCase() || !this.isConvertAttributeToLowerCase() ? 1 : 0) != 0, (String)"Either convertAttributeToUpperCase or convertAttributeToLowerCase can be set to true, but not both");
    }

    public List<GrantedAuthority> getGrantedAuthorities(Collection<String> attributes) {
        ArrayList<GrantedAuthority> result = new ArrayList<GrantedAuthority>(attributes.size());
        for (String attribute : attributes) {
            result.add(this.getGrantedAuthority(attribute));
        }
        return result;
    }

    private GrantedAuthority getGrantedAuthority(String attribute) {
        if (this.isConvertAttributeToLowerCase()) {
            attribute = attribute.toLowerCase(Locale.getDefault());
        } else if (this.isConvertAttributeToUpperCase()) {
            attribute = attribute.toUpperCase(Locale.getDefault());
        }
        if (this.isAddPrefixIfAlreadyExisting() || !attribute.startsWith(this.getAttributePrefix())) {
            return new SimpleGrantedAuthority(this.getAttributePrefix() + attribute);
        }
        return new SimpleGrantedAuthority(attribute);
    }

    private boolean isConvertAttributeToLowerCase() {
        return this.convertAttributeToLowerCase;
    }

    public void setConvertAttributeToLowerCase(boolean b) {
        this.convertAttributeToLowerCase = b;
    }

    private boolean isConvertAttributeToUpperCase() {
        return this.convertAttributeToUpperCase;
    }

    public void setConvertAttributeToUpperCase(boolean b) {
        this.convertAttributeToUpperCase = b;
    }

    private String getAttributePrefix() {
        return this.attributePrefix != null ? this.attributePrefix : "";
    }

    public void setAttributePrefix(String string) {
        this.attributePrefix = string;
    }

    private boolean isAddPrefixIfAlreadyExisting() {
        return this.addPrefixIfAlreadyExisting;
    }

    public void setAddPrefixIfAlreadyExisting(boolean b) {
        this.addPrefixIfAlreadyExisting = b;
    }
}

