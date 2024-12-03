/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.dns;

import com.microsoft.sqlserver.jdbc.dns.DNSRecordSRV;
import com.microsoft.sqlserver.jdbc.dns.DNSUtilities;
import java.util.Set;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

public final class DNSKerberosLocator {
    private DNSKerberosLocator() {
    }

    public static boolean isRealmValid(String realmName) throws NamingException {
        if (realmName == null || realmName.length() < 2) {
            return false;
        }
        if (realmName.charAt(0) == '.') {
            realmName = realmName.substring(1);
        }
        try {
            Set<DNSRecordSRV> records = DNSUtilities.findSrvRecords("_kerberos._udp." + realmName);
            return !records.isEmpty();
        }
        catch (NameNotFoundException wrongDomainException) {
            return false;
        }
    }
}

