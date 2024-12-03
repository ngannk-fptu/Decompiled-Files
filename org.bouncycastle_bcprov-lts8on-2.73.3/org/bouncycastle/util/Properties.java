/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class Properties {
    public static final String EMULATE_ORACLE = "org.bouncycastle.emulate.oracle";
    private static final ThreadLocal threadProperties = new ThreadLocal();

    private Properties() {
    }

    public static boolean isOverrideSet(String propertyName) {
        try {
            return Properties.isSetTrue(Properties.getPropertyValue(propertyName));
        }
        catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean isOverrideSetTo(String propertyName, boolean isTrue) {
        try {
            String propertyValue = Properties.getPropertyValue(propertyName);
            if (isTrue) {
                return Properties.isSetTrue(propertyValue);
            }
            return Properties.isSetFalse(propertyValue);
        }
        catch (AccessControlException e) {
            return false;
        }
    }

    public static boolean setThreadOverride(String propertyName, boolean enable) {
        boolean isSet = Properties.isOverrideSet(propertyName);
        HashMap<String, String> localProps = (HashMap<String, String>)threadProperties.get();
        if (localProps == null) {
            localProps = new HashMap<String, String>();
            threadProperties.set(localProps);
        }
        localProps.put(propertyName, enable ? "true" : "false");
        return isSet;
    }

    public static boolean removeThreadOverride(String propertyName) {
        String p;
        Map localProps = (Map)threadProperties.get();
        if (localProps != null && (p = (String)localProps.remove(propertyName)) != null) {
            if (localProps.isEmpty()) {
                threadProperties.remove();
            }
            return "true".equals(Strings.toLowerCase(p));
        }
        return false;
    }

    public static int asInteger(String propertyName, int defaultValue) {
        String p = Properties.getPropertyValue(propertyName);
        if (p != null) {
            return Integer.parseInt(p);
        }
        return defaultValue;
    }

    public static BigInteger asBigInteger(String propertyName) {
        String p = Properties.getPropertyValue(propertyName);
        if (p != null) {
            return new BigInteger(p);
        }
        return null;
    }

    public static Set<String> asKeySet(String propertyName) {
        HashSet<String> set = new HashSet<String>();
        String p = Properties.getPropertyValue(propertyName);
        if (p != null) {
            StringTokenizer sTok = new StringTokenizer(p, ",");
            while (sTok.hasMoreElements()) {
                set.add(Strings.toLowerCase(sTok.nextToken()).trim());
            }
        }
        return Collections.unmodifiableSet(set);
    }

    public static String getPropertyValue(final String propertyName) {
        String p;
        String val = (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return Security.getProperty(propertyName);
            }
        });
        if (val != null) {
            return val;
        }
        Map localProps = (Map)threadProperties.get();
        if (localProps != null && (p = (String)localProps.get(propertyName)) != null) {
            return p;
        }
        return (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(propertyName);
            }
        });
    }

    public static String getPropertyValue(String propertyName, String defValue) {
        String rv = Properties.getPropertyValue(propertyName);
        if (rv == null) {
            return defValue;
        }
        return rv;
    }

    private static boolean isSetFalse(String p) {
        if (p == null || p.length() != 5) {
            return false;
        }
        return !(p.charAt(0) != 'f' && p.charAt(0) != 'F' || p.charAt(1) != 'a' && p.charAt(1) != 'A' || p.charAt(2) != 'l' && p.charAt(2) != 'L' || p.charAt(3) != 's' && p.charAt(3) != 'S' || p.charAt(4) != 'e' && p.charAt(4) != 'E');
    }

    private static boolean isSetTrue(String p) {
        if (p == null || p.length() != 4) {
            return false;
        }
        return !(p.charAt(0) != 't' && p.charAt(0) != 'T' || p.charAt(1) != 'r' && p.charAt(1) != 'R' || p.charAt(2) != 'u' && p.charAt(2) != 'U' || p.charAt(3) != 'e' && p.charAt(3) != 'E');
    }
}

