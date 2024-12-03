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

public class RebindOperationExecutor
implements CompensatingTransactionOperationExecutor {
    private static Logger log = LoggerFactory.getLogger(RebindOperationExecutor.class);
    private LdapOperations ldapOperations;
    private Name originalDn;
    private Name temporaryDn;
    private Object originalObject;
    private Attributes originalAttributes;

    public RebindOperationExecutor(LdapOperations ldapOperations, Name originalDn, Name temporaryDn, Object originalObject, Attributes originalAttributes) {
        this.ldapOperations = ldapOperations;
        this.originalDn = originalDn;
        this.temporaryDn = temporaryDn;
        this.originalObject = originalObject;
        this.originalAttributes = originalAttributes;
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }

    @Override
    public void rollback() {
        log.debug("Rolling back rebind operation");
        try {
            this.ldapOperations.unbind(this.originalDn);
            this.ldapOperations.rename(this.temporaryDn, this.originalDn);
        }
        catch (Exception e) {
            log.warn("Failed to rollback operation, dn: " + this.originalDn + "; temporary DN: " + this.temporaryDn, (Throwable)e);
        }
    }

    @Override
    public void commit() {
        log.debug("Committing rebind operation");
        this.ldapOperations.unbind(this.temporaryDn);
    }

    @Override
    public void performOperation() {
        log.debug("Performing rebind operation - renaming original entry and binding new contents to entry.");
        this.ldapOperations.rename(this.originalDn, this.temporaryDn);
        this.ldapOperations.bind(this.originalDn, this.originalObject, this.originalAttributes);
    }

    Attributes getOriginalAttributes() {
        return this.originalAttributes;
    }

    Name getOriginalDn() {
        return this.originalDn;
    }

    Object getOriginalObject() {
        return this.originalObject;
    }

    Name getTemporaryDn() {
        return this.temporaryDn;
    }
}

