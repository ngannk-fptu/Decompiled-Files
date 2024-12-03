/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.BaseLdapPathSource;

public abstract class DelegatingBaseLdapPathContextSourceSupport
implements BaseLdapPathSource {
    protected abstract ContextSource getTarget();

    private BaseLdapPathSource getTargetAsBaseLdapPathSource() {
        try {
            return (BaseLdapPathSource)((Object)this.getTarget());
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This operation is not supported on a target ContextSource that does not  implement BaseLdapPathContextSource", e);
        }
    }

    @Override
    public final LdapName getBaseLdapName() {
        return this.getTargetAsBaseLdapPathSource().getBaseLdapName();
    }

    @Override
    public final DistinguishedName getBaseLdapPath() {
        return this.getTargetAsBaseLdapPathSource().getBaseLdapPath();
    }

    @Override
    public final String getBaseLdapPathAsString() {
        return this.getTargetAsBaseLdapPathSource().getBaseLdapPathAsString();
    }
}

