/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.dns;

import com.microsoft.sqlserver.jdbc.dns.DNSRecordSRV;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class DNSUtilities {
    private static final Logger LOG = Logger.getLogger(DNSUtilities.class.getName());
    private static final Level DNS_ERR_LOG_LEVEL = Level.FINE;

    private DNSUtilities() {
    }

    public static Set<DNSRecordSRV> findSrvRecords(String dnsSrvRecordToFind) throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        InitialDirContext ctx = new InitialDirContext(env);
        Attributes attrs = ctx.getAttributes(dnsSrvRecordToFind, new String[]{"SRV"});
        NamingEnumeration<? extends Attribute> allServers = attrs.getAll();
        TreeSet<DNSRecordSRV> records = new TreeSet<DNSRecordSRV>();
        while (allServers.hasMoreElements()) {
            Attribute a = (Attribute)allServers.nextElement();
            NamingEnumeration<?> srvRecord = a.getAll();
            while (srvRecord.hasMore()) {
                String record = String.valueOf(srvRecord.nextElement());
                try {
                    DNSRecordSRV rec = DNSRecordSRV.parseFromDNSRecord(record);
                    if (rec == null) continue;
                    records.add(rec);
                }
                catch (IllegalArgumentException errorParsingRecord) {
                    if (!LOG.isLoggable(DNS_ERR_LOG_LEVEL)) continue;
                    LOG.log(DNS_ERR_LOG_LEVEL, String.format("Failed to parse SRV DNS Record: '%s'", record), errorParsingRecord);
                }
            }
            srvRecord.close();
        }
        allServers.close();
        return records;
    }
}

