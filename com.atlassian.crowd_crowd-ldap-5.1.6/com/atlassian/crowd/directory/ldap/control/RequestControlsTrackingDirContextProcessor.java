/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.ldap.core.DirContextProcessor
 */
package com.atlassian.crowd.directory.ldap.control;

import com.google.common.base.Preconditions;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import org.springframework.ldap.core.DirContextProcessor;

public class RequestControlsTrackingDirContextProcessor
implements DirContextProcessor {
    private Control[] currentRequestControls;

    public void preProcess(DirContext ctx) throws NamingException {
        LdapContext ldapContext = this.castToLdapContext(ctx);
        this.currentRequestControls = ldapContext.getRequestControls();
    }

    public void postProcess(DirContext ctx) throws NamingException {
        LdapContext ldapContext = this.castToLdapContext(ctx);
        ldapContext.setRequestControls(null);
        if (this.currentRequestControls != null) {
            ldapContext.setRequestControls(this.currentRequestControls);
        }
    }

    private LdapContext castToLdapContext(DirContext ctx) {
        Preconditions.checkArgument((boolean)(ctx instanceof LdapContext), (Object)"Request Control operations require LDAPv3 - Context must be of type LdapContext");
        return (LdapContext)ctx;
    }
}

