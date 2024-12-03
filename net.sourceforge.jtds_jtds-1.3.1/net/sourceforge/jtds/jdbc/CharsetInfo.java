/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.util.Logger;

public final class CharsetInfo {
    private static final String CHARSETS_RESOURCE_NAME = "net/sourceforge/jtds/jdbc/Charsets.properties";
    private static final HashMap charsets = new HashMap();
    private static final HashMap lcidToCharsetMap = new HashMap();
    private static final CharsetInfo[] sortToCharsetMap = new CharsetInfo[256];
    private final String charset;
    private final boolean wideChars;

    public static CharsetInfo getCharset(String serverCharset) {
        return (CharsetInfo)charsets.get(serverCharset.toUpperCase());
    }

    public static CharsetInfo getCharsetForLCID(int lcid) {
        return (CharsetInfo)lcidToCharsetMap.get(new Integer(lcid));
    }

    public static CharsetInfo getCharsetForSortOrder(int sortOrder) {
        return sortToCharsetMap[sortOrder];
    }

    public static CharsetInfo getCharset(byte[] collation) throws SQLException {
        CharsetInfo charset = collation[4] != 0 ? CharsetInfo.getCharsetForSortOrder(collation[4] & 0xFF) : CharsetInfo.getCharsetForLCID((collation[2] & 0xF) << 16 | (collation[1] & 0xFF) << 8 | collation[0] & 0xFF);
        if (charset == null) {
            throw new SQLException(Messages.get("error.charset.nocollation", Support.toHex(collation)), "2C000");
        }
        return charset;
    }

    public CharsetInfo(String descriptor) {
        this.wideChars = !"1".equals(descriptor.substring(0, 1));
        this.charset = descriptor.substring(2);
    }

    public String getCharset() {
        return this.charset;
    }

    public boolean isWideChars() {
        return this.wideChars;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CharsetInfo)) {
            return false;
        }
        CharsetInfo charsetInfo = (CharsetInfo)o;
        return this.charset.equals(charsetInfo.charset);
    }

    public int hashCode() {
        return this.charset.hashCode();
    }

    public String toString() {
        return this.charset;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        InputStream stream = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                stream = classLoader.getResourceAsStream(CHARSETS_RESOURCE_NAME);
            }
            if (stream == null) {
                stream = (InputStream)AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        ClassLoader loader = CharsetInfo.class.getClassLoader();
                        if (loader == null) {
                            loader = ClassLoader.getSystemClassLoader();
                        }
                        return loader.getResourceAsStream(CharsetInfo.CHARSETS_RESOURCE_NAME);
                    }
                });
            }
            if (stream != null) {
                Properties tmp = new Properties();
                tmp.load(stream);
                HashMap instances = new HashMap();
                Enumeration<?> e = tmp.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    CharsetInfo value = new CharsetInfo(tmp.getProperty(key));
                    CharsetInfo prevInstance = (CharsetInfo)instances.get(value.getCharset());
                    if (prevInstance != null) {
                        if (prevInstance.isWideChars() != value.isWideChars()) {
                            throw new IllegalStateException("Inconsistent Charsets.properties");
                        }
                        value = prevInstance;
                    }
                    if (key.startsWith("LCID_")) {
                        Integer lcid = new Integer(key.substring(5));
                        lcidToCharsetMap.put(lcid, value);
                        continue;
                    }
                    if (key.startsWith("SORT_")) {
                        CharsetInfo.sortToCharsetMap[Integer.parseInt((String)key.substring((int)5))] = value;
                        continue;
                    }
                    charsets.put(key, value);
                }
            } else {
                Logger.println("Can't load Charsets.properties");
            }
        }
        catch (IOException e) {
            Logger.logException(e);
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (Exception exception) {}
            }
        }
    }
}

