/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.transaction.compensating.BindOperationExecutor;
import org.springframework.ldap.transaction.compensating.LdapTransactionUtils;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;

public class BindOperationRecorder
implements CompensatingTransactionOperationRecorder {
    private LdapOperations ldapOperations;

    public BindOperationRecorder(LdapOperations ldapOperations) {
        this.ldapOperations = ldapOperations;
    }

    @Override
    public CompensatingTransactionOperationExecutor recordOperation(Object[] args) {
        if (args == null || args.length != 3) {
            throw new IllegalArgumentException("Invalid arguments for bind operation");
        }
        Name dn = LdapTransactionUtils.getFirstArgumentAsName(args);
        Object object = args[1];
        Attributes attributes = null;
        if (args[2] != null && !(args[2] instanceof Attributes)) {
            throw new IllegalArgumentException("Invalid third argument to bind operation");
        }
        if (args[2] != null) {
            attributes = (Attributes)args[2];
        }
        return new BindOperationExecutor(this.ldapOperations, dn, object, attributes);
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }
}

