/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;

public class BindOperationExecutor
implements CompensatingTransactionOperationExecutor {
    private static Logger log = LoggerFactory.getLogger(BindOperationExecutor.class);
    private LdapOperations ldapOperations;
    private Name dn;
    private Object originalObject;
    private Attributes originalAttributes;

    public BindOperationExecutor(LdapOperations ldapOperations, Name dn, Object originalObject, Attributes originalAttributes) {
        this.ldapOperations = ldapOperations;
        this.dn = dn;
        this.originalObject = originalObject;
        this.originalAttributes = originalAttributes;
    }

    @Override
    public void rollback() {
        try {
            this.ldapOperations.unbind(this.dn);
        }
        catch (Exception e) {
            log.warn("Failed to rollback, dn:" + this.dn.toString(), (Throwable)e);
        }
    }

    @Override
    public void commit() {
        log.debug("Nothing to do in commit for bind operation");
    }

    @Override
    public void performOperation() {
        log.debug("Performing bind operation");
        this.ldapOperations.bind(this.dn, this.originalObject, this.originalAttributes);
    }

    Name getDn() {
        return this.dn;
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }

    Attributes getOriginalAttributes() {
        return this.originalAttributes;
    }

    Object getOriginalObject() {
        return this.originalObject;
    }
}

