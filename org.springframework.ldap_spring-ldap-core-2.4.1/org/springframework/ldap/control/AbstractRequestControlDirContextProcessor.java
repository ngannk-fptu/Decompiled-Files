/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.springframework.ldap.control;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextProcessor;

public abstract class AbstractRequestControlDirContextProcessor
implements DirContextProcessor {
    protected Logger log = LoggerFactory.getLogger(AbstractRequestControlDirContextProcessor.class);
    private boolean replaceSameControlEnabled = true;

    public boolean isReplaceSameControlEnabled() {
        return this.replaceSameControlEnabled;
    }

    public void setReplaceSameControlEnabled(boolean replaceSameControlEnabled) {
        this.replaceSameControlEnabled = replaceSameControlEnabled;
    }

    @Override
    public void preProcess(DirContext ctx) throws NamingException {
        if (!(ctx instanceof LdapContext)) {
            throw new IllegalArgumentException("Request Control operations require LDAPv3 - Context must be of type LdapContext");
        }
        LdapContext ldapContext = (LdapContext)ctx;
        Control[] requestControls = ldapContext.getRequestControls();
        if (requestControls == null) {
            requestControls = new Control[]{};
        }
        Control newControl = this.createRequestControl();
        Control[] newControls = new Control[requestControls.length + 1];
        for (int i = 0; i < requestControls.length; ++i) {
            if (this.replaceSameControlEnabled && requestControls[i].getClass() == newControl.getClass()) {
                this.log.debug("Replacing already existing control in context: " + newControl);
                requestControls[i] = newControl;
                ldapContext.setRequestControls(requestControls);
                return;
            }
            newControls[i] = requestControls[i];
        }
        newControls[newControls.length - 1] = newControl;
        ldapContext.setRequestControls(newControls);
    }

    public abstract Control createRequestControl();
}

