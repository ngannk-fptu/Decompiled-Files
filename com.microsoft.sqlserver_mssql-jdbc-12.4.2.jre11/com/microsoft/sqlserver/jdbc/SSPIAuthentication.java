/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.dns.DNSKerberosLocator;
import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.NamingException;

abstract class SSPIAuthentication {
    private static final Pattern SPN_PATTERN = Pattern.compile("MSSQLSvc/(.*):([^:@]+)(@.+)?", 2);
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.SSPIAuthentication");
    private RealmValidator validator;

    SSPIAuthentication() {
    }

    abstract byte[] generateClientContext(byte[] var1, boolean[] var2) throws SQLServerException;

    abstract void releaseClientContext();

    private String makeSpn(SQLServerConnection con, String server, int port) {
        StringBuilder spn = new StringBuilder("MSSQLSvc/");
        if (con.serverNameAsACE()) {
            spn.append(IDN.toASCII(server));
        } else {
            spn.append(server);
        }
        spn.append(":");
        spn.append(port);
        return spn.toString();
    }

    private RealmValidator getRealmValidator() {
        if (null != this.validator) {
            return this.validator;
        }
        this.validator = new RealmValidator(){

            @Override
            public boolean isRealmValid(String realm) {
                try {
                    return DNSKerberosLocator.isRealmValid(realm);
                }
                catch (NamingException err) {
                    return false;
                }
            }
        };
        return this.validator;
    }

    private String findRealmFromHostname(RealmValidator realmValidator, String hostname) {
        if (hostname == null) {
            return null;
        }
        int index = 0;
        while (index != -1 && index < hostname.length() - 2) {
            String realm = hostname.substring(index);
            if (realmValidator.isRealmValid(realm)) {
                return realm.toUpperCase();
            }
            if (-1 == (index = hostname.indexOf(46, index + 1))) continue;
            ++index;
        }
        return null;
    }

    String enrichSpnWithRealm(SQLServerConnection con, String spn, boolean allowHostnameCanonicalization) {
        String portOrInstance;
        String dnsName;
        String realm;
        block15: {
            if (spn == null) {
                return spn;
            }
            Matcher m = SPN_PATTERN.matcher(spn);
            if (!m.matches()) {
                return spn;
            }
            realm = con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.REALM.toString());
            if (m.group(3) != null && (null == realm || realm.trim().isEmpty())) {
                return spn;
            }
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Deriving realm");
            }
            dnsName = m.group(1);
            portOrInstance = m.group(2);
            try {
                if (null == realm || realm.trim().isEmpty()) {
                    RealmValidator realmValidator = this.getRealmValidator();
                    realm = this.findRealmFromHostname(realmValidator, dnsName);
                    if (null == realm && allowHostnameCanonicalization) {
                        if (logger.isLoggable(Level.FINER)) {
                            logger.finer("Attempt to derive realm using canonical host name with InetAddress");
                        }
                        String canonicalHostName = InetAddress.getByName(dnsName).getCanonicalHostName();
                        realm = this.findRealmFromHostname(realmValidator, canonicalHostName);
                        dnsName = canonicalHostName;
                    }
                } else if (allowHostnameCanonicalization) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.finer("Since realm is provided, try to resolve canonical host name to host name with InetAddress");
                    }
                    dnsName = InetAddress.getByName(dnsName).getCanonicalHostName();
                }
            }
            catch (UnknownHostException e) {
                if (!logger.isLoggable(Level.FINER)) break block15;
                logger.finer("Could not canonicalize host name. " + e.toString());
            }
        }
        if (null == realm) {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Could not derive realm.");
            }
            return spn;
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Derived realm: " + realm);
        }
        StringBuilder sb = new StringBuilder("MSSQLSvc/");
        sb.append(dnsName).append(":").append(portOrInstance).append("@").append(realm.toUpperCase(Locale.ENGLISH));
        return sb.toString();
    }

    String getSpn(SQLServerConnection con) {
        Object spn;
        if (null == con || null == con.activeConnectionProperties) {
            return null;
        }
        String userSuppliedServerSpn = con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SERVER_SPN.toString());
        if (null != userSuppliedServerSpn) {
            if (con.serverNameAsACE()) {
                int slashPos = userSuppliedServerSpn.indexOf(47);
                spn = userSuppliedServerSpn.substring(0, slashPos + 1) + IDN.toASCII(userSuppliedServerSpn.substring(slashPos + 1));
            } else {
                spn = userSuppliedServerSpn;
            }
        } else {
            spn = this.makeSpn(con, con.currentConnectPlaceHolder.getServerName(), con.currentConnectPlaceHolder.getPortNumber());
        }
        return this.enrichSpnWithRealm(con, (String)spn, null == userSuppliedServerSpn);
    }

    static interface RealmValidator {
        public boolean isRealmValid(String var1);
    }
}

