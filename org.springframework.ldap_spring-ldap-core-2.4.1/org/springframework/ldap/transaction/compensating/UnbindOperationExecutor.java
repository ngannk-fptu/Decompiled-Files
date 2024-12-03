/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;

public class UnbindOperationExecutor
implements CompensatingTransactionOperationExecutor {
    private static Logger log = LoggerFactory.getLogger(UnbindOperationExecutor.class);
    private LdapOperations ldapOperations;
    private Name originalDn;
    private Name temporaryDn;

    public UnbindOperationExecutor(LdapOperations ldapOperations, Name originalDn, Name temporaryDn) {
        this.ldapOperations = ldapOperations;
        this.originalDn = originalDn;
        this.temporaryDn = temporaryDn;
    }

    @Override
    public void rollback() {
        try {
            this.ldapOperations.rename(this.temporaryDn, this.originalDn);
        }
        catch (Exception e) {
            log.warn("Filed to rollback unbind operation, temporaryDn: " + this.temporaryDn + "; originalDn: " + this.originalDn);
        }
    }

    @Override
    public void commit() {
        log.debug("Committing unbind operation - unbinding temporary entry");
        this.ldapOperations.unbind(this.temporaryDn);
    }

    @Override
    public void performOperation() {
        log.debug("Performing operation for unbind - renaming to temporary entry.");
        this.ldapOperations.rename(this.originalDn, this.temporaryDn);
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }

    Name getOriginalDn() {
        return this.originalDn;
    }

    Name getTemporaryDn() {
        return this.temporaryDn;
    }
}

