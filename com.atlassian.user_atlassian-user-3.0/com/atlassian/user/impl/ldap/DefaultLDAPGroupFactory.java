/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.EntityException;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.DefaultLDAPGroup;
import com.atlassian.user.impl.ldap.LDAPGroupFactory;
import com.atlassian.user.impl.ldap.properties.LdapMembershipProperties;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.util.profiling.UtilTimerStack;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class DefaultLDAPGroupFactory
implements LDAPGroupFactory {
    private final LdapSearchProperties searchProperties;
    private final LdapMembershipProperties membershipProperties;

    public DefaultLDAPGroupFactory(LdapSearchProperties searchProperties, LdapMembershipProperties membershipProperties) {
        this.membershipProperties = membershipProperties;
        this.searchProperties = searchProperties;
    }

    public DefaultLDAPGroup getGroup(Attributes attrs, String distinguishedName) throws EntityException {
        DefaultLDAPGroup group;
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_delegating_getGroup(attrs, " + distinguishedName + ")"));
        }
        try {
            Attribute groupNameAttribute = attrs.get(this.searchProperties.getGroupnameAttribute());
            if (groupNameAttribute != null) {
                String groupName = (String)groupNameAttribute.get();
                group = new DefaultLDAPGroup(groupName, distinguishedName);
            } else {
                Attribute membershipAttribute = attrs.get(this.membershipProperties.getMembershipAttribute());
                group = this.getGroup(membershipAttribute);
            }
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_delegating_getGroup(attrs, " + distinguishedName + ")"));
        }
        return group;
    }

    public DefaultLDAPGroup getGroup(String distinguishedName) {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_delegating_getGroup(" + distinguishedName + ")"));
        }
        String groupName = this.getGroupNameFromDN(distinguishedName);
        DefaultLDAPGroup group = new DefaultLDAPGroup(groupName, distinguishedName);
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_delegating_getGroup(" + distinguishedName + ")"));
        }
        return group;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DefaultLDAPGroup getGroup(Attribute attribute) throws EntityException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_delegating_getGroup(attr)"));
        }
        try {
            String groupDN = this.getGroupDNFromMembershipAttribute(attribute);
            String groupName = this.getGroupNameFromDN(groupDN);
            DefaultLDAPGroup defaultLDAPGroup = new DefaultLDAPGroup(groupName, groupDN);
            return defaultLDAPGroup;
        }
        finally {
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_delegating_getGroup(attr)"));
            }
        }
    }

    private String getGroupDNFromMembershipAttribute(Attribute groupMembershipAtt) throws EntityException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_delegating_getGroupDNFromMembershipAttribute"));
        }
        String groupDN = null;
        try {
            NamingEnumeration<?> groupAttrs = groupMembershipAtt.getAll();
            while (groupAttrs.hasMoreElements() && (groupDN = (String)groupAttrs.nextElement()) == null) {
            }
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_delegating_getGroupDNFromMembershipAttribute"));
        }
        return groupDN;
    }

    private String getGroupNameFromDN(String groupDN) {
        if (groupDN.indexOf("=") == -1) {
            return groupDN;
        }
        String[] names = groupDN.split(",");
        return names[0].split("=")[1];
    }

    public DefaultLDAPGroup getEntity(Attributes attrs, String distinguishedName) throws EntityException {
        return this.getGroup(attrs, distinguishedName);
    }

    public DefaultLDAPGroup getEntity(Attribute attr, String distinguishedName) throws EntityException {
        return this.getGroup(attr);
    }
}

