/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.directory.DirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.SingleContextSource;
import org.springframework.ldap.transaction.compensating.BindOperationRecorder;
import org.springframework.ldap.transaction.compensating.ModifyAttributesOperationRecorder;
import org.springframework.ldap.transaction.compensating.NullOperationRecorder;
import org.springframework.ldap.transaction.compensating.RebindOperationRecorder;
import org.springframework.ldap.transaction.compensating.RenameOperationRecorder;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;
import org.springframework.ldap.transaction.compensating.UnbindOperationRecorder;
import org.springframework.transaction.compensating.CompensatingTransactionOperationFactory;
import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;
import org.springframework.util.ObjectUtils;

public class LdapCompensatingTransactionOperationFactory
implements CompensatingTransactionOperationFactory {
    private static Logger log = LoggerFactory.getLogger(LdapCompensatingTransactionOperationFactory.class);
    private TempEntryRenamingStrategy renamingStrategy;

    public LdapCompensatingTransactionOperationFactory(TempEntryRenamingStrategy renamingStrategy) {
        this.renamingStrategy = renamingStrategy;
    }

    @Override
    public CompensatingTransactionOperationRecorder createRecordingOperation(Object resource, String operation) {
        if (ObjectUtils.nullSafeEquals((Object)operation, (Object)"bind")) {
            log.debug("Bind operation recorded");
            return new BindOperationRecorder(this.createLdapOperationsInstance((DirContext)resource));
        }
        if (ObjectUtils.nullSafeEquals((Object)operation, (Object)"rebind")) {
            log.debug("Rebind operation recorded");
            return new RebindOperationRecorder(this.createLdapOperationsInstance((DirContext)resource), this.renamingStrategy);
        }
        if (ObjectUtils.nullSafeEquals((Object)operation, (Object)"rename")) {
            log.debug("Rename operation recorded");
            return new RenameOperationRecorder(this.createLdapOperationsInstance((DirContext)resource));
        }
        if (ObjectUtils.nullSafeEquals((Object)operation, (Object)"modifyAttributes")) {
            return new ModifyAttributesOperationRecorder(this.createLdapOperationsInstance((DirContext)resource));
        }
        if (ObjectUtils.nullSafeEquals((Object)operation, (Object)"unbind")) {
            return new UnbindOperationRecorder(this.createLdapOperationsInstance((DirContext)resource), this.renamingStrategy);
        }
        log.warn("No suitable CompensatingTransactionOperationRecorder found for method " + operation + ". Operation will not be transacted.");
        return new NullOperationRecorder();
    }

    LdapOperations createLdapOperationsInstance(DirContext ctx) {
        return new LdapTemplate(new SingleContextSource(ctx));
    }
}

