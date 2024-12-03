/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import net.sourceforge.jtds.jdbc.DefaultProperties;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;

public class Driver
implements java.sql.Driver {
    private static String driverPrefix = "jdbc:jtds:";
    static final int MAJOR_VERSION = 1;
    static final int MINOR_VERSION = 3;
    static final String MISC_VERSION = ".1";
    public static final int TDS42 = 1;
    public static final int TDS50 = 2;
    public static final int TDS70 = 3;
    public static final int TDS80 = 4;
    public static final int TDS81 = 5;
    public static final int TDS90 = 6;
    public static final int SQLSERVER = 1;
    public static final int SYBASE = 2;
    public static final String APPNAME = "prop.appname";
    public static final String AUTOCOMMIT = "prop.autocommit";
    public static final String BATCHSIZE = "prop.batchsize";
    public static final String BINDADDRESS = "prop.bindaddress";
    public static final String BUFFERDIR = "prop.bufferdir";
    public static final String BUFFERMAXMEMORY = "prop.buffermaxmemory";
    public static final String BUFFERMINPACKETS = "prop.bufferminpackets";
    public static final String CACHEMETA = "prop.cachemetadata";
    public static final String CHARSET = "prop.charset";
    public static final String DATABASENAME = "prop.databasename";
    public static final String DOMAIN = "prop.domain";
    public static final String INSTANCE = "prop.instance";
    public static final String LANGUAGE = "prop.language";
    public static final String LASTUPDATECOUNT = "prop.lastupdatecount";
    public static final String LOBBUFFER = "prop.lobbuffer";
    public static final String LOGFILE = "prop.logfile";
    public static final String LOGINTIMEOUT = "prop.logintimeout";
    public static final String MACADDRESS = "prop.macaddress";
    public static final String MAXSTATEMENTS = "prop.maxstatements";
    public static final String NAMEDPIPE = "prop.namedpipe";
    public static final String PACKETSIZE = "prop.packetsize";
    public static final String PASSWORD = "prop.password";
    public static final String PORTNUMBER = "prop.portnumber";
    public static final String PREPARESQL = "prop.preparesql";
    public static final String PROGNAME = "prop.progname";
    public static final String SERVERNAME = "prop.servername";
    public static final String SERVERTYPE = "prop.servertype";
    public static final String SOTIMEOUT = "prop.sotimeout";
    public static final String SOKEEPALIVE = "prop.sokeepalive";
    public static final String PROCESSID = "prop.processid";
    public static final String SSL = "prop.ssl";
    public static final String TCPNODELAY = "prop.tcpnodelay";
    public static final String TDS = "prop.tds";
    public static final String USECURSORS = "prop.usecursors";
    public static final String USEJCIFS = "prop.usejcifs";
    public static final String USENTLMV2 = "prop.usentlmv2";
    public static final String USEKERBEROS = "prop.usekerberos";
    public static final String USELOBS = "prop.uselobs";
    public static final String USER = "prop.user";
    public static final String SENDSTRINGPARAMETERSASUNICODE = "prop.useunicode";
    public static final String WSID = "prop.wsid";
    public static final String XAEMULATION = "prop.xaemulation";

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 3;
    }

    public static final String getVersion() {
        return "1.3" + (MISC_VERSION == null ? "" : MISC_VERSION);
    }

    public String toString() {
        return "jTDS " + Driver.getVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }
        return url.toLowerCase().startsWith(driverPrefix);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null || !url.toLowerCase().startsWith(driverPrefix)) {
            return null;
        }
        Properties props = this.setupConnectProperties(url, info);
        return new JtdsConnection(url, props);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties props) throws SQLException {
        Properties parsedProps = Driver.parseURL(url, props == null ? new Properties() : props);
        if (parsedProps == null) {
            throw new SQLException(Messages.get("error.driver.badurl", url), "08001");
        }
        parsedProps = DefaultProperties.addDefaultProperties(parsedProps);
        HashMap propertyMap = new HashMap();
        HashMap descriptionMap = new HashMap();
        Messages.loadDriverProperties(propertyMap, descriptionMap);
        Map choicesMap = Driver.createChoicesMap();
        Map requiredTrueMap = Driver.createRequiredTrueMap();
        DriverPropertyInfo[] dpi = new DriverPropertyInfo[propertyMap.size()];
        Iterator iterator = propertyMap.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String key = (String)entry.getKey();
            String name = (String)entry.getValue();
            DriverPropertyInfo info = new DriverPropertyInfo(name, parsedProps.getProperty(name));
            info.description = (String)descriptionMap.get(key);
            info.required = requiredTrueMap.containsKey(name);
            if (choicesMap.containsKey(name)) {
                info.choices = (String[])choicesMap.get(name);
            }
            dpi[i] = info;
            ++i;
        }
        return dpi;
    }

    private Properties setupConnectProperties(String url, Properties info) throws SQLException {
        Properties props = Driver.parseURL(url, info);
        if (props == null) {
            throw new SQLException(Messages.get("error.driver.badurl", url), "08001");
        }
        if (props.getProperty(Messages.get(LOGINTIMEOUT)) == null) {
            props.setProperty(Messages.get(LOGINTIMEOUT), Integer.toString(DriverManager.getLoginTimeout()));
        }
        props = DefaultProperties.addDefaultProperties(props);
        return props;
    }

    private static Map createChoicesMap() {
        HashMap<String, String[]> choicesMap = new HashMap<String, String[]>();
        String[] booleanChoices = new String[]{"true", "false"};
        choicesMap.put(Messages.get(CACHEMETA), booleanChoices);
        choicesMap.put(Messages.get(LASTUPDATECOUNT), booleanChoices);
        choicesMap.put(Messages.get(NAMEDPIPE), booleanChoices);
        choicesMap.put(Messages.get(TCPNODELAY), booleanChoices);
        choicesMap.put(Messages.get(SENDSTRINGPARAMETERSASUNICODE), booleanChoices);
        choicesMap.put(Messages.get(USECURSORS), booleanChoices);
        choicesMap.put(Messages.get(USELOBS), booleanChoices);
        choicesMap.put(Messages.get(XAEMULATION), booleanChoices);
        String[] prepareSqlChoices = new String[]{String.valueOf(0), String.valueOf(1), String.valueOf(2), String.valueOf(3)};
        choicesMap.put(Messages.get(PREPARESQL), prepareSqlChoices);
        String[] serverTypeChoices = new String[]{String.valueOf(1), String.valueOf(2)};
        choicesMap.put(Messages.get(SERVERTYPE), serverTypeChoices);
        String[] tdsChoices = new String[]{"4.2", "5.0", "7.0", "8.0"};
        choicesMap.put(Messages.get(TDS), tdsChoices);
        String[] sslChoices = new String[]{"off", "request", "require", "authenticate"};
        choicesMap.put(Messages.get(SSL), sslChoices);
        return choicesMap;
    }

    private static Map createRequiredTrueMap() {
        HashMap<String, Object> requiredTrueMap = new HashMap<String, Object>();
        requiredTrueMap.put(Messages.get(SERVERNAME), null);
        requiredTrueMap.put(Messages.get(SERVERTYPE), null);
        return requiredTrueMap;
    }

    private static Properties parseURL(String url, Properties info) {
        Properties props = new Properties();
        Enumeration<?> e = info.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = info.getProperty(key);
            if (value == null) continue;
            props.setProperty(key.toUpperCase(), value);
        }
        StringBuilder token = new StringBuilder(16);
        int pos = 0;
        pos = Driver.nextToken(url, pos, token);
        if (!"jdbc".equalsIgnoreCase(token.toString())) {
            return null;
        }
        pos = Driver.nextToken(url, pos, token);
        if (!"jtds".equalsIgnoreCase(token.toString())) {
            return null;
        }
        pos = Driver.nextToken(url, pos, token);
        String type = token.toString().toLowerCase();
        Integer serverType = DefaultProperties.getServerType(type);
        if (serverType == null) {
            return null;
        }
        props.setProperty(Messages.get(SERVERTYPE), String.valueOf(serverType));
        pos = Driver.nextToken(url, pos, token);
        if (token.length() > 0) {
            return null;
        }
        pos = Driver.nextToken(url, pos, token);
        String host = token.toString();
        if (host.length() == 0 && ((host = props.getProperty(Messages.get(SERVERNAME))) == null || host.length() == 0)) {
            return null;
        }
        props.setProperty(Messages.get(SERVERNAME), host);
        if (url.charAt(pos - 1) == ':' && pos < url.length()) {
            pos = Driver.nextToken(url, pos, token);
            try {
                int port = Integer.parseInt(token.toString());
                props.setProperty(Messages.get(PORTNUMBER), Integer.toString(port));
            }
            catch (NumberFormatException e2) {
                return null;
            }
        }
        if (url.charAt(pos - 1) == '/' && pos < url.length()) {
            pos = Driver.nextToken(url, pos, token);
            props.setProperty(Messages.get(DATABASENAME), token.toString());
        }
        while (url.charAt(pos - 1) == ';' && pos < url.length()) {
            pos = Driver.nextToken(url, pos, token);
            String tmp = token.toString();
            int index = tmp.indexOf(61);
            if (index > 0 && index < tmp.length() - 1) {
                props.setProperty(tmp.substring(0, index).toUpperCase(), tmp.substring(index + 1));
                continue;
            }
            props.setProperty(tmp.toUpperCase(), "");
        }
        return props;
    }

    private static int nextToken(String url, int pos, StringBuilder token) {
        token.setLength(0);
        boolean inQuote = false;
        while (pos < url.length()) {
            char ch = url.charAt(pos++);
            if (!inQuote) {
                if (ch == ':' || ch == ';') break;
                if (ch == '/') {
                    if (pos >= url.length() || url.charAt(pos) != '/') break;
                    ++pos;
                    break;
                }
            }
            if (ch == '[') {
                inQuote = true;
                continue;
            }
            if (ch == ']') {
                inQuote = false;
                continue;
            }
            token.append(ch);
        }
        return pos;
    }

    public static void main(String[] args) {
        System.out.println("jTDS " + Driver.getVersion());
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new AbstractMethodError();
    }

    static {
        try {
            DriverManager.registerDriver(new Driver());
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }
}

