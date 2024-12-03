/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.transaction.compensating;

import java.util.HashSet;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;
import org.springframework.ldap.core.IncrementalAttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.support.DefaultIncrementalAttributesMapper;
import org.springframework.ldap.transaction.compensating.LdapTransactionUtils;
import org.springframework.ldap.transaction.compensating.ModifyAttributesOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationExecutor;
import org.springframework.transaction.compensating.CompensatingTransactionOperationRecorder;
import org.springframework.util.Assert;

public class ModifyAttributesOperationRecorder
implements CompensatingTransactionOperationRecorder {
    private LdapOperations ldapOperations;

    public ModifyAttributesOperationRecorder(LdapOperations ldapOperations) {
        this.ldapOperations = ldapOperations;
    }

    @Override
    public CompensatingTransactionOperationExecutor recordOperation(Object[] args) {
        Assert.notNull((Object)args);
        Name dn = LdapTransactionUtils.getFirstArgumentAsName(args);
        if (args.length != 2 || !(args[1] instanceof ModificationItem[])) {
            throw new IllegalArgumentException("Unexpected arguments to ModifyAttributes operation");
        }
        ModificationItem[] incomingModifications = (ModificationItem[])args[1];
        HashSet<String> set = new HashSet<String>();
        for (ModificationItem incomingModification : incomingModifications) {
            set.add(incomingModification.getAttribute().getID());
        }
        String[] attributeNameArray = set.toArray(new String[set.size()]);
        IncrementalAttributesMapper<?> attributesMapper = this.getAttributesMapper(attributeNameArray);
        while (attributesMapper.hasMore()) {
            this.ldapOperations.lookup(dn, attributesMapper.getAttributesForLookup(), attributesMapper);
        }
        Attributes currentAttributes = attributesMapper.getCollectedAttributes();
        ModificationItem[] rollbackItems = new ModificationItem[incomingModifications.length];
        for (int i = 0; i < incomingModifications.length; ++i) {
            rollbackItems[i] = this.getCompensatingModificationItem(currentAttributes, incomingModifications[i]);
        }
        return new ModifyAttributesOperationExecutor(this.ldapOperations, dn, incomingModifications, rollbackItems);
    }

    IncrementalAttributesMapper<?> getAttributesMapper(String[] attributeNames) {
        return new DefaultIncrementalAttributesMapper(attributeNames);
    }

    protected ModificationItem getCompensatingModificationItem(Attributes originalAttributes, ModificationItem modificationItem) {
        Attribute modificationAttribute = modificationItem.getAttribute();
        Attribute originalAttribute = originalAttributes.get(modificationAttribute.getID());
        if (modificationItem.getModificationOp() == 3) {
            if (modificationAttribute.size() == 0) {
                return new ModificationItem(1, (Attribute)originalAttribute.clone());
            }
            return new ModificationItem(1, (Attribute)modificationAttribute.clone());
        }
        if (modificationItem.getModificationOp() == 2) {
            if (originalAttribute != null) {
                return new ModificationItem(2, (Attribute)originalAttribute.clone());
            }
            return new ModificationItem(3, new BasicAttribute(modificationAttribute.getID()));
        }
        if (originalAttribute == null) {
            return new ModificationItem(3, new BasicAttribute(modificationAttribute.getID()));
        }
        return new ModificationItem(2, (Attribute)originalAttribute.clone());
    }

    LdapOperations getLdapOperations() {
        return this.ldapOperations;
    }
}

