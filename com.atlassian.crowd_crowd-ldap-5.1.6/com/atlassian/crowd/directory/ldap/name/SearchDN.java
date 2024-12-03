/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap.name;

import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.name.Converter;
import com.atlassian.crowd.directory.ldap.name.GenericConverter;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchDN {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LDAPPropertiesMapper propertiesMapper;
    private final Converter converter;

    public SearchDN(LDAPPropertiesMapper propertiesMapper, Converter converter) {
        this.propertiesMapper = propertiesMapper;
        this.converter = converter;
    }

    public LdapName getGroup() {
        try {
            return this.getSearchDN("ldap.group.dn");
        }
        catch (InvalidNameException e) {
            this.logger.error("Group Search DN could not be parsed", (Throwable)e);
            return GenericConverter.emptyLdapName();
        }
    }

    @Deprecated
    public LdapName getRole() {
        try {
            return this.getSearchDN("ldap.role.dn");
        }
        catch (InvalidNameException e) {
            this.logger.error("Role Search DN could not be parsed", (Throwable)e);
            return GenericConverter.emptyLdapName();
        }
    }

    public LdapName getUser() {
        try {
            return this.getSearchDN("ldap.user.dn");
        }
        catch (InvalidNameException e) {
            this.logger.error("User Search DN could not be parsed", (Throwable)e);
            return GenericConverter.emptyLdapName();
        }
    }

    public Name getBase() throws InvalidNameException {
        return this.converter.getName(this.propertiesMapper.getAttribute("ldap.basedn"));
    }

    public Name getNamingContext() {
        Name baseDN;
        try {
            baseDN = this.getBase();
        }
        catch (InvalidNameException e) {
            baseDN = GenericConverter.emptyLdapName();
        }
        return baseDN;
    }

    protected LdapName getSearchDN(String propertyName) throws InvalidNameException {
        String baseDN;
        String searchDN = "";
        String additionalDN = this.propertiesMapper.getAttribute(propertyName);
        if (StringUtils.isNotBlank((CharSequence)additionalDN)) {
            searchDN = additionalDN;
        }
        if (StringUtils.isNotBlank((CharSequence)(baseDN = this.propertiesMapper.getAttribute("ldap.basedn")))) {
            if (StringUtils.isNotBlank((CharSequence)searchDN)) {
                searchDN = searchDN + ",";
            }
            searchDN = searchDN + baseDN;
        }
        return this.converter.getName(searchDN);
    }
}

