/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.crowd.directory.ldap.name;

import com.atlassian.crowd.directory.ldap.name.Converter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class GenericConverter
implements Converter {
    @Override
    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
    public LdapName getName(String dn) throws InvalidNameException {
        try {
            return new LdapName(dn);
        }
        catch (IllegalArgumentException e) {
            throw (InvalidNameException)new InvalidNameException("Cannot convert <" + dn + "> to a LDAP name").initCause(e);
        }
    }

    @Override
    public LdapName getName(String attributeName, String objectName, LdapName baseDN) throws InvalidNameException {
        List<Object> rdns = baseDN != null ? baseDN.getRdns() : Collections.emptyList();
        ArrayList<Rdn> newRdns = new ArrayList<Rdn>(rdns);
        newRdns.add(new Rdn(attributeName, objectName));
        return new LdapName(newRdns);
    }

    public static LdapName emptyLdapName() {
        return new LdapName(Collections.emptyList());
    }
}

