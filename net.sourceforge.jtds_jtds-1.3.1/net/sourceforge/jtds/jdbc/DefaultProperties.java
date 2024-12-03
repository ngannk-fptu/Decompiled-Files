/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.sourceforge.jtds.jdbc.Messages;

public final class DefaultProperties {
    public static final String APP_NAME = "jTDS";
    public static final String AUTO_COMMIT = "true";
    public static final String BATCH_SIZE_SQLSERVER = "0";
    public static final String BATCH_SIZE_SYBASE = "1000";
    public static final String BIND_ADDRESS = "";
    public static final String BUFFER_MAX_MEMORY = "1024";
    public static final String BUFFER_MIN_PACKETS = "8";
    public static final String CACHEMETA = "false";
    public static final String CHARSET = "";
    public static final String DATABASE_NAME = "";
    public static final String INSTANCE = "";
    public static final String DOMAIN = "";
    public static final String LAST_UPDATE_COUNT = "true";
    public static final String LOB_BUFFER_SIZE = "32768";
    public static final String LOGIN_TIMEOUT = "0";
    public static final String MAC_ADDRESS = "000000000000";
    public static final String MAX_STATEMENTS = "500";
    public static final String NAMED_PIPE = "false";
    public static final String NAMED_PIPE_PATH_SQLSERVER = "/sql/query";
    public static final String NAMED_PIPE_PATH_SYBASE = "/sybase/query";
    public static final String PACKET_SIZE_42 = String.valueOf(512);
    public static final String PACKET_SIZE_50 = "0";
    public static final String PACKET_SIZE_70_80 = "0";
    public static final String PASSWORD = "";
    public static final String PORT_NUMBER_SQLSERVER = "1433";
    public static final String PORT_NUMBER_SYBASE = "7100";
    public static final String LANGUAGE = "";
    public static final String PREPARE_SQLSERVER = String.valueOf(3);
    public static final String PREPARE_SYBASE = String.valueOf(1);
    public static final String PROG_NAME = "jTDS";
    public static final String TCP_NODELAY = "true";
    public static final String BUFFER_DIR = new File(System.getProperty("java.io.tmpdir")).toString();
    public static final String USE_UNICODE = "true";
    public static final String USECURSORS = "false";
    public static final String USEJCIFS = "false";
    public static final String USELOBS = "true";
    public static final String USENTLMV2 = "false";
    public static final String USEKERBEROS = "false";
    public static final String USER = "";
    public static final String WSID = "";
    public static final String XAEMULATION = "true";
    public static final String LOGFILE = "";
    public static final String SOCKET_TIMEOUT = "0";
    public static final String SOCKET_KEEPALIVE = "false";
    public static final String PROCESS_ID = "123";
    public static final String SERVER_TYPE_SQLSERVER = "sqlserver";
    public static final String SERVER_TYPE_SYBASE = "sybase";
    public static final String TDS_VERSION_42 = "4.2";
    public static final String TDS_VERSION_50 = "5.0";
    public static final String TDS_VERSION_70 = "7.0";
    public static final String TDS_VERSION_80 = "8.0";
    public static final String TDS_VERSION_90 = "9.0";
    public static final String SSL = "off";
    private static final HashMap tdsDefaults = new HashMap(2);
    private static final HashMap portNumberDefaults;
    private static final HashMap packetSizeDefaults;
    private static final HashMap batchSizeDefaults;
    private static final HashMap prepareSQLDefaults;

    public static Properties addDefaultProperties(Properties props) {
        String serverType = props.getProperty(Messages.get("prop.servertype"));
        if (serverType == null) {
            return null;
        }
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.tds", "prop.servertype", tdsDefaults);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.portnumber", "prop.servertype", portNumberDefaults);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.user", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.password", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.databasename", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.instance", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.domain", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.appname", "jTDS");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.autocommit", "true");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.progname", "jTDS");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.wsid", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.batchsize", "prop.servertype", batchSizeDefaults);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.lastupdatecount", "true");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.lobbuffer", LOB_BUFFER_SIZE);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.logintimeout", "0");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.sotimeout", "0");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.sokeepalive", "false");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.processid", PROCESS_ID);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.macaddress", MAC_ADDRESS);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.maxstatements", MAX_STATEMENTS);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.namedpipe", "false");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.packetsize", "prop.tds", packetSizeDefaults);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.cachemetadata", "false");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.charset", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.language", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.preparesql", "prop.servertype", prepareSQLDefaults);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.useunicode", "true");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.tcpnodelay", "true");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.xaemulation", "true");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.logfile", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.ssl", SSL);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.usecursors", "false");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.usentlmv2", "false");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.usekerberos", "false");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.buffermaxmemory", BUFFER_MAX_MEMORY);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.bufferminpackets", BUFFER_MIN_PACKETS);
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.uselobs", "true");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.bindaddress", "");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.usejcifs", "false");
        DefaultProperties.addDefaultPropertyIfNotSet(props, "prop.bufferdir", BUFFER_DIR);
        return props;
    }

    private static void addDefaultPropertyIfNotSet(Properties props, String key, String defaultValue) {
        String messageKey = Messages.get(key);
        if (props.getProperty(messageKey) == null) {
            props.setProperty(messageKey, defaultValue);
        }
    }

    private static void addDefaultPropertyIfNotSet(Properties props, String key, String defaultKey, Map defaults) {
        Object defaultValue;
        String defaultKeyValue = props.getProperty(Messages.get(defaultKey));
        if (defaultKeyValue == null) {
            return;
        }
        String messageKey = Messages.get(key);
        if (props.getProperty(messageKey) == null && (defaultValue = defaults.get(defaultKeyValue)) != null) {
            props.setProperty(messageKey, String.valueOf(defaultValue));
        }
    }

    public static String getNamedPipePath(int serverType) {
        if (serverType == 0 || serverType == 1) {
            return NAMED_PIPE_PATH_SQLSERVER;
        }
        if (serverType == 2) {
            return NAMED_PIPE_PATH_SYBASE;
        }
        throw new IllegalArgumentException("Unknown serverType: " + serverType);
    }

    public static String getServerType(int serverType) {
        if (serverType == 1) {
            return SERVER_TYPE_SQLSERVER;
        }
        if (serverType == 2) {
            return SERVER_TYPE_SYBASE;
        }
        return null;
    }

    public static Integer getServerType(String serverType) {
        if (SERVER_TYPE_SQLSERVER.equals(serverType)) {
            return new Integer(1);
        }
        if (SERVER_TYPE_SYBASE.equals(serverType)) {
            return new Integer(2);
        }
        return null;
    }

    public static String getServerTypeWithDefault(int serverType) {
        if (serverType == 0) {
            return SERVER_TYPE_SQLSERVER;
        }
        if (serverType == 1 || serverType == 2) {
            return DefaultProperties.getServerType(serverType);
        }
        throw new IllegalArgumentException("Only 0, 1 and 2 accepted for serverType");
    }

    public static Integer getTdsVersion(String tdsVersion) {
        if (TDS_VERSION_42.equals(tdsVersion)) {
            return new Integer(1);
        }
        if (TDS_VERSION_50.equals(tdsVersion)) {
            return new Integer(2);
        }
        if (TDS_VERSION_70.equals(tdsVersion)) {
            return new Integer(3);
        }
        if (TDS_VERSION_80.equals(tdsVersion)) {
            return new Integer(4);
        }
        return null;
    }

    static {
        tdsDefaults.put(String.valueOf(1), TDS_VERSION_80);
        tdsDefaults.put(String.valueOf(2), TDS_VERSION_50);
        portNumberDefaults = new HashMap(2);
        portNumberDefaults.put(String.valueOf(1), PORT_NUMBER_SQLSERVER);
        portNumberDefaults.put(String.valueOf(2), PORT_NUMBER_SYBASE);
        packetSizeDefaults = new HashMap(5);
        packetSizeDefaults.put(TDS_VERSION_42, PACKET_SIZE_42);
        packetSizeDefaults.put(TDS_VERSION_50, "0");
        packetSizeDefaults.put(TDS_VERSION_70, "0");
        packetSizeDefaults.put(TDS_VERSION_80, "0");
        packetSizeDefaults.put(TDS_VERSION_90, "0");
        batchSizeDefaults = new HashMap(2);
        batchSizeDefaults.put(String.valueOf(1), "0");
        batchSizeDefaults.put(String.valueOf(2), BATCH_SIZE_SYBASE);
        prepareSQLDefaults = new HashMap(2);
        prepareSQLDefaults.put(String.valueOf(1), PREPARE_SQLSERVER);
        prepareSQLDefaults.put(String.valueOf(2), PREPARE_SYBASE);
    }
}

