/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.core.Encoding;
import org.postgresql.util.ByteConverter;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

public class HStoreConverter {
    public static Map<String, @Nullable String> fromBytes(byte[] b, Encoding encoding) throws SQLException {
        HashMap<String, @Nullable String> m = new HashMap<String, String>();
        int pos = 0;
        int numElements = ByteConverter.int4(b, pos);
        pos += 4;
        try {
            for (int i = 0; i < numElements; ++i) {
                String val;
                int keyLen = ByteConverter.int4(b, pos);
                String key = encoding.decode(b, pos += 4, keyLen);
                int valLen = ByteConverter.int4(b, pos += keyLen);
                pos += 4;
                if (valLen == -1) {
                    val = null;
                } else {
                    val = encoding.decode(b, pos, valLen);
                    pos += valLen;
                }
                m.put(key, val);
            }
        }
        catch (IOException ioe) {
            throw new PSQLException(GT.tr("Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.", new Object[0]), PSQLState.DATA_ERROR, (Throwable)ioe);
        }
        return m;
    }

    public static byte[] toBytes(Map<?, ?> m, Encoding encoding) throws SQLException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4 + 10 * m.size());
        byte[] lenBuf = new byte[4];
        try {
            ByteConverter.int4(lenBuf, 0, m.size());
            baos.write(lenBuf);
            for (Map.Entry<?, ?> e : m.entrySet()) {
                Object mapKey = e.getKey();
                if (mapKey == null) {
                    throw new PSQLException(GT.tr("hstore key must not be null", new Object[0]), PSQLState.INVALID_PARAMETER_VALUE);
                }
                byte[] key = encoding.encode(mapKey.toString());
                ByteConverter.int4(lenBuf, 0, key.length);
                baos.write(lenBuf);
                baos.write(key);
                if (e.getValue() == null) {
                    ByteConverter.int4(lenBuf, 0, -1);
                    baos.write(lenBuf);
                    continue;
                }
                byte[] val = encoding.encode(e.getValue().toString());
                ByteConverter.int4(lenBuf, 0, val.length);
                baos.write(lenBuf);
                baos.write(val);
            }
        }
        catch (IOException ioe) {
            throw new PSQLException(GT.tr("Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.", new Object[0]), PSQLState.DATA_ERROR, (Throwable)ioe);
        }
        return baos.toByteArray();
    }

    public static String toString(Map<?, ?> map) {
        if (map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(map.size() * 8);
        for (Map.Entry<?, ?> e : map.entrySet()) {
            HStoreConverter.appendEscaped(sb, e.getKey());
            sb.append("=>");
            HStoreConverter.appendEscaped(sb, e.getValue());
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    private static void appendEscaped(StringBuilder sb, @Nullable Object val) {
        if (val != null) {
            sb.append('\"');
            String s = val.toString();
            for (int pos = 0; pos < s.length(); ++pos) {
                char ch = s.charAt(pos);
                if (ch == '\"' || ch == '\\') {
                    sb.append('\\');
                }
                sb.append(ch);
            }
            sb.append('\"');
        } else {
            sb.append("NULL");
        }
    }

    public static Map<String, @Nullable String> fromString(String s) {
        HashMap<String, @Nullable String> m = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder();
        for (int pos = 0; pos < s.length(); ++pos) {
            String val;
            sb.setLength(0);
            int start = s.indexOf(34, pos);
            int end = HStoreConverter.appendUntilQuote(sb, s, start);
            String key = sb.toString();
            pos = end + 3;
            if (s.charAt(pos) == 'N') {
                val = null;
                pos += 4;
            } else {
                sb.setLength(0);
                end = HStoreConverter.appendUntilQuote(sb, s, pos);
                val = sb.toString();
                pos = end;
            }
            m.put(key, val);
        }
        return m;
    }

    private static int appendUntilQuote(StringBuilder sb, String s, int pos) {
        char ch;
        ++pos;
        while (pos < s.length() && (ch = s.charAt(pos)) != '\"') {
            if (ch == '\\') {
                ch = s.charAt(++pos);
            }
            sb.append(ch);
            ++pos;
        }
        return pos;
    }
}

