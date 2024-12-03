/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.transaction.compensating;

import javax.naming.Name;

public interface TempEntryRenamingStrategy {
    public Name getTemporaryName(Name var1);
}

