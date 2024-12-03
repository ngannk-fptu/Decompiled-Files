/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.tomcat.dbcp.dbcp2.LifetimeExceededException;
import org.apache.tomcat.dbcp.pool2.PooledObject;

public final class Utils {
    private static final ResourceBundle messages = ResourceBundle.getBundle(Utils.class.getPackage().getName() + ".LocalStrings");
    @Deprecated
    public static final boolean IS_SECURITY_ENABLED = Utils.isSecurityEnabled();
    public static final String DISCONNECTION_SQL_CODE_PREFIX = "08";
    @Deprecated
    public static final Set<String> DISCONNECTION_SQL_CODES;
    static final ResultSet[] EMPTY_RESULT_SET_ARRAY;
    static final String[] EMPTY_STRING_ARRAY;

    public static char[] clone(char[] value) {
        return value == null ? null : (char[])value.clone();
    }

    public static Properties cloneWithoutCredentials(Properties properties) {
        if (properties != null) {
            Properties temp = (Properties)properties.clone();
            temp.remove("user");
            temp.remove("password");
            return temp;
        }
        return properties;
    }

    public static void close(AutoCloseable autoCloseable, Consumer<Exception> exceptionHandler) {
        block3: {
            if (autoCloseable != null) {
                try {
                    autoCloseable.close();
                }
                catch (Exception e) {
                    if (exceptionHandler == null) break block3;
                    exceptionHandler.accept(e);
                }
            }
        }
    }

    public static void closeQuietly(AutoCloseable autoCloseable) {
        Utils.close(autoCloseable, null);
    }

    @Deprecated
    public static void closeQuietly(Connection connection) {
        Utils.closeQuietly((AutoCloseable)connection);
    }

    @Deprecated
    public static void closeQuietly(ResultSet resultSet) {
        Utils.closeQuietly((AutoCloseable)resultSet);
    }

    @Deprecated
    public static void closeQuietly(Statement statement) {
        Utils.closeQuietly((AutoCloseable)statement);
    }

    public static Set<String> getDisconnectionSqlCodes() {
        return new HashSet<String>(DISCONNECTION_SQL_CODES);
    }

    public static String getMessage(String key) {
        return Utils.getMessage(key, null);
    }

    public static String getMessage(String key, Object ... args) {
        String msg = messages.getString(key);
        if (args == null || args.length == 0) {
            return msg;
        }
        MessageFormat mf = new MessageFormat(msg);
        return mf.format(args, new StringBuffer(), (FieldPosition)null).toString();
    }

    static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    static boolean isSecurityEnabled() {
        return System.getSecurityManager() != null;
    }

    public static char[] toCharArray(String value) {
        return value != null ? value.toCharArray() : null;
    }

    public static String toString(char[] value) {
        return value == null ? null : String.valueOf(value);
    }

    public static void validateLifetime(PooledObject<?> p, Duration maxDuration) throws LifetimeExceededException {
        Duration lifetimeDuration;
        if (maxDuration.compareTo(Duration.ZERO) > 0 && (lifetimeDuration = Duration.between(p.getCreateInstant(), Instant.now())).compareTo(maxDuration) > 0) {
            throw new LifetimeExceededException(Utils.getMessage("connectionFactory.lifetimeExceeded", lifetimeDuration, maxDuration));
        }
    }

    private Utils() {
    }

    static {
        EMPTY_RESULT_SET_ARRAY = new ResultSet[0];
        EMPTY_STRING_ARRAY = new String[0];
        DISCONNECTION_SQL_CODES = new HashSet<String>();
        DISCONNECTION_SQL_CODES.add("57P01");
        DISCONNECTION_SQL_CODES.add("57P02");
        DISCONNECTION_SQL_CODES.add("57P03");
        DISCONNECTION_SQL_CODES.add("01002");
        DISCONNECTION_SQL_CODES.add("JZ0C0");
        DISCONNECTION_SQL_CODES.add("JZ0C1");
    }
}

