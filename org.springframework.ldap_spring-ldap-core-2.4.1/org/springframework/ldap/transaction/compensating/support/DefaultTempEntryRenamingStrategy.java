/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.transaction.compensating.support;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;

public class DefaultTempEntryRenamingStrategy
implements TempEntryRenamingStrategy {
    public static final String DEFAULT_TEMP_SUFFIX = "_temp";
    private String tempSuffix = "_temp";

    @Override
    public Name getTemporaryName(Name originalName) {
        LdapName temporaryName = LdapUtils.newLdapName(originalName);
        try {
            String leafNode = (String)temporaryName.remove(temporaryName.size() - 1);
            temporaryName.add(new Rdn(leafNode + this.tempSuffix));
        }
        catch (javax.naming.InvalidNameException e) {
            throw new InvalidNameException(e);
        }
        return temporaryName;
    }

    public String getTempSuffix() {
        return this.tempSuffix;
    }

    public void setTempSuffix(String tempSuffix) {
        this.tempSuffix = tempSuffix;
    }
}

