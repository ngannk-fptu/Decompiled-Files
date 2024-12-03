/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.directory.ModificationItem;

public interface AttributeModificationsAware {
    public ModificationItem[] getModificationItems();
}

