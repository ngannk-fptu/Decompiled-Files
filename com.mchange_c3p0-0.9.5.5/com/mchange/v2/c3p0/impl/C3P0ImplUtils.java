/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.lang.ByteUtils
 *  com.mchange.v1.identicator.Identicator
 *  com.mchange.v1.identicator.IdentityHashCodeIdenticator
 *  com.mchange.v2.encounter.EncounterCounter
 *  com.mchange.v2.encounter.EncounterUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.log.jdk14logging.ForwardingLogger
 *  com.mchange.v2.ser.SerializableUtils
 *  com.mchange.v2.sql.SqlUtils
 *  com.mchange.v2.uid.UidUtils
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.lang.ByteUtils;
import com.mchange.v1.identicator.Identicator;
import com.mchange.v1.identicator.IdentityHashCodeIdenticator;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.impl.DbAuth;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import com.mchange.v2.encounter.EncounterCounter;
import com.mchange.v2.encounter.EncounterUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.log.jdk14logging.ForwardingLogger;
import com.mchange.v2.ser.SerializableUtils;
import com.mchange.v2.sql.SqlUtils;
import com.mchange.v2.uid.UidUtils;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

public final class C3P0ImplUtils {
    private static final boolean CONDITIONAL_LONG_TOKENS = false;
    static final MLogger logger = MLog.getLogger(C3P0ImplUtils.class);
    public static final DbAuth NULL_AUTH = new DbAuth(null, null);
    public static final Object[] NOARGS = new Object[0];
    public static final Logger PARENT_LOGGER = new ForwardingLogger(MLog.getLogger((String)"com.mchange.v2.c3p0"), null);
    private static final EncounterCounter ID_TOKEN_COUNTER = C3P0ImplUtils.createEncounterCounter();
    public static final String VMID_PROPKEY = "com.mchange.v2.c3p0.VMID";
    private static final String VMID_PFX;
    static String connectionTesterClassName;
    private static final String HASM_HEADER = "HexAsciiSerializedMap";

    private static EncounterCounter createEncounterCounter() {
        return EncounterUtils.syncWrap((EncounterCounter)EncounterUtils.createWeak((Identicator)IdentityHashCodeIdenticator.INSTANCE));
    }

    public static String allocateIdentityToken(Object o) {
        long count;
        if (o == null) {
            return null;
        }
        String shortIdToken = Integer.toString(System.identityHashCode(o), 16);
        StringBuffer sb = new StringBuffer(128);
        sb.append(VMID_PFX);
        if (ID_TOKEN_COUNTER != null && (count = ID_TOKEN_COUNTER.encounter((Object)shortIdToken)) > 0L) {
            sb.append(shortIdToken);
            sb.append('#');
            sb.append(count);
        } else {
            sb.append(shortIdToken);
        }
        String out = sb.toString().intern();
        return out;
    }

    public static DbAuth findAuth(Object o) throws SQLException {
        if (o == null) {
            return NULL_AUTH;
        }
        String user = null;
        String password = null;
        String overrideDefaultUser = null;
        String overrideDefaultPassword = null;
        try {
            BeanInfo bi = Introspector.getBeanInfo(o.getClass());
            for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                Method readMethod;
                Class<?> propCl = pd.getPropertyType();
                String propName = pd.getName();
                if (propCl != String.class || (readMethod = pd.getReadMethod()) == null) continue;
                Object propVal = readMethod.invoke(o, NOARGS);
                String value = (String)propVal;
                if ("user".equals(propName)) {
                    user = value;
                    continue;
                }
                if ("password".equals(propName)) {
                    password = value;
                    continue;
                }
                if ("overrideDefaultUser".equals(propName)) {
                    overrideDefaultUser = value;
                    continue;
                }
                if (!"overrideDefaultPassword".equals(propName)) continue;
                overrideDefaultPassword = value;
            }
            if (overrideDefaultUser != null) {
                return new DbAuth(overrideDefaultUser, overrideDefaultPassword);
            }
            if (user != null) {
                return new DbAuth(user, password);
            }
            return NULL_AUTH;
        }
        catch (Exception e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "An exception occurred while trying to extract the default authentification info from a bean.", (Throwable)e);
            }
            throw SqlUtils.toSQLException((Throwable)e);
        }
    }

    static void resetTxnState(Connection pCon, boolean forceIgnoreUnresolvedTransactions, boolean autoCommitOnClose, boolean txnKnownResolved) throws SQLException {
        if (!forceIgnoreUnresolvedTransactions && !pCon.getAutoCommit()) {
            if (!autoCommitOnClose && !txnKnownResolved) {
                pCon.rollback();
            }
            pCon.setAutoCommit(true);
        }
    }

    public static boolean supportsMethod(Object target, String mname, Class[] argTypes) {
        try {
            return target.getClass().getMethod(mname, argTypes) != null;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
        catch (SecurityException e) {
            if (logger.isLoggable(MLevel.FINE)) {
                logger.log(MLevel.FINE, "We were denied access in a check of whether " + target + " supports method " + mname + ". Prob means external clients have no access, returning false.", (Throwable)e);
            }
            return false;
        }
    }

    public static String createUserOverridesAsString(Map userOverrides) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(HASM_HEADER);
        sb.append('[');
        sb.append(ByteUtils.toHexAscii((byte[])SerializableUtils.toByteArray((Object)userOverrides)));
        sb.append(']');
        return sb.toString();
    }

    public static Map parseUserOverridesAsString(String userOverridesAsString) throws IOException, ClassNotFoundException {
        if (userOverridesAsString != null) {
            String hexAscii = userOverridesAsString.substring(HASM_HEADER.length() + 1, userOverridesAsString.length() - 1);
            byte[] serBytes = ByteUtils.fromHexAscii((String)hexAscii);
            return Collections.unmodifiableMap((Map)SerializableUtils.fromByteArray((byte[])serBytes));
        }
        return Collections.EMPTY_MAP;
    }

    public static void assertCompileTimePresenceOfJdbc4_Jdk17Api(NewProxyConnection npc) throws SQLException {
        npc.getNetworkTimeout();
    }

    private C3P0ImplUtils() {
    }

    static {
        String vmid = C3P0Config.getPropsFileConfigProperty(VMID_PROPKEY);
        VMID_PFX = vmid == null || (vmid = vmid.trim()).equals("") || vmid.equals("AUTO") ? UidUtils.VM_ID + '|' : (vmid.equals("NONE") ? "" : vmid + "|");
        connectionTesterClassName = null;
    }
}

