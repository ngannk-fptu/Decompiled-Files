/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.transaction.compensating.LdapTransactionUtils;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;
import org.springframework.ldap.transaction.compensating.UnbindOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;

public class UnbindOperationRecorder
implements CompensatingTransactionOperationRecorder {
    private LdapOperations ldapOperations;
    private TempEntryRenamingStrategy renamingStrategy;

    public UnbindOperationRecorder(LdapOperations ldapOperations, TempEntryRenamingStrategy renamingStrategy) {
        this.ldapOperations = ldapOperations;
        this.renamingStrategy = renamingStrategy;
    }

    @Override
    public CompensatingTransactionOperationExecutor recordOperation(Object[] args) {
        Name dn = LdapTransactionUtils.getFirstArgumentAsName(args);
        Name temporaryDn = this.renamingStrategy.getTemporaryName(dn);
        return new UnbindOperationExecutor(this.ldapOperations, dn, temporaryDn);
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }

    public TempEntryRenamingStrategy getRenamingStrategy() {
        return this.renamingStrategy;
    }
}

