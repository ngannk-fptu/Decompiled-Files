/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.transaction.compensating.LdapTransactionUtils;
import org.springframework.ldap.transaction.compensating.RenameOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;
import org.springframework.util.Assert;

public class RenameOperationRecorder
implements CompensatingTransactionOperationRecorder {
    private static Logger log = LoggerFactory.getLogger(RenameOperationRecorder.class);
    private LdapOperations ldapOperations;

    public RenameOperationRecorder(LdapOperations ldapOperations) {
        this.ldapOperations = ldapOperations;
    }

    @Override
    public CompensatingTransactionOperationExecutor recordOperation(Object[] args) {
        log.debug("Storing rollback information for rename operation");
        Assert.notEmpty((Object[])args);
        if (args.length != 2) {
            throw new IllegalArgumentException("Illegal argument length");
        }
        Name oldDn = LdapTransactionUtils.getArgumentAsName(args[0]);
        Name newDn = LdapTransactionUtils.getArgumentAsName(args[1]);
        return new RenameOperationExecutor(this.ldapOperations, oldDn, newDn);
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }
}

