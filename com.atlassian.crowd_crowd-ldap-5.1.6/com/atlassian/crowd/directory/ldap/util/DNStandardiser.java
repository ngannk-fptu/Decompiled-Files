/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.crowd.directory.ldap.util;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

public class DNStandardiser {
    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="returned as String")
    public static String standardise(String dn, boolean forceProperStandard) {
        if (forceProperStandard) {
            try {
                return DNStandardiser.standardise(new LdapName(dn), true);
            }
            catch (InvalidNameException invalidNameException) {
                // empty catch block
            }
        }
        return IdentifierUtils.toLowerCase((String)dn);
    }

    public static String standardise(LdapName name, boolean forceProperStandard) {
        if (forceProperStandard) {
            return IdentifierUtils.toLowerCase((String)new LdapName(name.getRdns()).toString());
        }
        return IdentifierUtils.toLowerCase((String)name.toString());
    }
}

