/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.Name;
import javax.naming.directory.DirContext;
import org.springframework.LdapDataEntry;
import org.springframework.ldap.core.AttributeModificationsAware;

public interface DirContextOperations
extends DirContext,
LdapDataEntry,
AttributeModificationsAware {
    public boolean isUpdateMode();

    public String[] getNamesOfModifiedAttributes();

    public void update();

    public void setDn(Name var1);

    @Override
    public String getNameInNamespace();

    public String getReferralUrl();

    public boolean isReferral();
}

