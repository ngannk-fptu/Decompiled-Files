/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;
import javax.naming.directory.ModificationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;

public class ModifyAttributesOperationExecutor
implements CompensatingTransactionOperationExecutor {
    private static Logger log = LoggerFactory.getLogger(ModifyAttributesOperationExecutor.class);
    private LdapOperations ldapOperations;
    private Name dn;
    private ModificationItem[] compensatingModifications;
    private ModificationItem[] actualModifications;

    public ModifyAttributesOperationExecutor(LdapOperations ldapOperations, Name dn, ModificationItem[] actualModifications, ModificationItem[] compensatingModifications) {
        this.ldapOperations = ldapOperations;
        this.dn = dn;
        this.actualModifications = (ModificationItem[])actualModifications.clone();
        this.compensatingModifications = (ModificationItem[])compensatingModifications.clone();
    }

    @Override
    public void rollback() {
        try {
            log.debug("Rolling back modifyAttributes operation");
            this.ldapOperations.modifyAttributes(this.dn, this.compensatingModifications);
        }
        catch (Exception e) {
            log.warn("Failed to rollback ModifyAttributes operation, dn: " + this.dn);
        }
    }

    @Override
    public void commit() {
        log.debug("Nothing to do in commit for modifyAttributes");
    }

    @Override
    public void performOperation() {
        log.debug("Performing modifyAttributes operation");
        this.ldapOperations.modifyAttributes(this.dn, this.actualModifications);
    }

    Name getDn() {
        return this.dn;
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }

    ModificationItem[] getActualModifications() {
        return this.actualModifications;
    }

    ModificationItem[] getCompensatingModifications() {
        return this.compensatingModifications;
    }
}

