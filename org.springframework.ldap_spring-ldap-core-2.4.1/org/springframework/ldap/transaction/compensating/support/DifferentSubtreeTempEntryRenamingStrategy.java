/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.transaction.compensating.support;

import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;

public class DifferentSubtreeTempEntryRenamingStrategy
implements TempEntryRenamingStrategy {
    private Name subtreeNode;
    private static final AtomicInteger NEXT_SEQUENCE_NO = new AtomicInteger(1);

    public DifferentSubtreeTempEntryRenamingStrategy(Name subtreeNode) {
        this.subtreeNode = subtreeNode;
    }

    public DifferentSubtreeTempEntryRenamingStrategy(String subtreeNode) {
        this(LdapUtils.newLdapName(subtreeNode));
    }

    public Name getSubtreeNode() {
        return this.subtreeNode;
    }

    public void setSubtreeNode(Name subtreeNode) {
        this.subtreeNode = subtreeNode;
    }

    int getNextSequenceNo() {
        return NEXT_SEQUENCE_NO.get();
    }

    @Override
    public Name getTemporaryName(Name originalName) {
        int thisSequenceNo = NEXT_SEQUENCE_NO.getAndIncrement();
        LdapName tempName = LdapUtils.newLdapName(originalName);
        try {
            String leafNode = tempName.get(tempName.size() - 1) + thisSequenceNo;
            LdapName newName = LdapUtils.newLdapName(this.subtreeNode);
            newName.add(leafNode);
            return newName;
        }
        catch (javax.naming.InvalidNameException e) {
            throw new InvalidNameException(e);
        }
    }
}

