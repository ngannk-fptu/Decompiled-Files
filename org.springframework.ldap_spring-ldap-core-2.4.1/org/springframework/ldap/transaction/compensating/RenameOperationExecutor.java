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

public class RenameOperationExecutor
implements CompensatingTransactionOperationExecutor {
    private static Logger log = LoggerFactory.getLogger(RenameOperationExecutor.class);
    private LdapOperations ldapOperations;
    private Name newDn;
    private Name originalDn;

    public RenameOperationExecutor(LdapOperations ldapOperations, Name originalDn, Name newDn) {
        this.ldapOperations = ldapOperations;
        this.originalDn = originalDn;
        this.newDn = newDn;
    }

    @Override
    public void rollback() {
        log.debug("Rolling back rename operation");
        try {
            this.ldapOperations.rename(this.newDn, this.originalDn);
        }
        catch (Exception e) {
            log.warn("Unable to rollback rename operation. originalDn: " + this.newDn + "; newDn: " + this.originalDn);
        }
    }

    @Override
    public void commit() {
        log.debug("Nothing to do in commit for rename operation");
    }

    @Override
    public void performOperation() {
        log.debug("Performing rename operation");
        this.ldapOperations.rename(this.originalDn, this.newDn);
    }

    Name getNewDn() {
        return this.newDn;
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }

    Name getOriginalDn() {
        return this.originalDn;
    }
}

