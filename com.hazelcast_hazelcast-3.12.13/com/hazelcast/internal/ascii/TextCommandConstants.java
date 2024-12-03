/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"MS_MUTABLE_ARRAY"})
public final class TextCommandConstants {
    public static final byte[] SPACE = StringUtil.stringToBytes(" ");
    public static final byte[] RETURN = StringUtil.stringToBytes("\r\n");
    public static final byte[] FLAG_ZERO = StringUtil.stringToBytes(" 0 ");
    public static final byte[] VALUE_SPACE = StringUtil.stringToBytes("VALUE ");
    public static final byte[] DELETED = StringUtil.stringToBytes("DELETED\r\n");
    public static final byte[] STORED = StringUtil.stringToBytes("STORED\r\n");
    public static final byte[] TOUCHED = StringUtil.stringToBytes("TOUCHED\r\n");
    public static final byte[] NOT_STORED = StringUtil.stringToBytes("NOT_STORED\r\n");
    public static final byte[] NOT_FOUND = StringUtil.stringToBytes("NOT_FOUND\r\n");
    public static final byte[] RETURN_END = StringUtil.stringToBytes("\r\nEND\r\n");
    public static final byte[] END = StringUtil.stringToBytes("END\r\n");
    public static final byte[] ERROR = StringUtil.stringToBytes("ERROR");
    public static final byte[] CLIENT_ERROR = StringUtil.stringToBytes("CLIENT_ERROR ");
    public static final byte[] SERVER_ERROR = StringUtil.stringToBytes("SERVER_ERROR ");
    public static final byte[] MIME_TEXT_PLAIN = StringUtil.stringToBytes("text/plain");
    private static final int SECOND = 60;
    private static final int MINUTE = 60;
    private static final int HOUR = 24;
    private static final int MONTH = 30;
    private static int monthSeconds = 2592000;

    private TextCommandConstants() {
    }

    public static int getMonthSeconds() {
        return monthSeconds;
    }

    public static enum TextCommandType {
        GET(0),
        BULK_GET(1),
        GETS(2),
        SET(3),
        APPEND(4),
        PREPEND(5),
        ADD(6),
        REPLACE(7),
        DELETE(8),
        QUIT(9),
        STATS(10),
        GET_END(11),
        ERROR_CLIENT(12),
        ERROR_SERVER(13),
        UNKNOWN(14),
        VERSION(15),
        TOUCH(16),
        INCREMENT(17),
        DECREMENT(18),
        HTTP_GET(30),
        HTTP_POST(31),
        HTTP_PUT(32),
        HTTP_DELETE(33),
        HTTP_HEAD(34),
        NO_OP(98),
        STOP(99);

        final byte value;

        private TextCommandType(byte type) {
            this.value = type;
        }

        public byte getValue() {
            return this.value;
        }
    }
}

