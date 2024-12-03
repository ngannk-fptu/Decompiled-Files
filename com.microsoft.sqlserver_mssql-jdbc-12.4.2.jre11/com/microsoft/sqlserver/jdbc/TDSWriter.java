/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import com.microsoft.sqlserver.jdbc.DDC;
import com.microsoft.sqlserver.jdbc.DriverError;
import com.microsoft.sqlserver.jdbc.GregorianChange;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.Nanos;
import com.microsoft.sqlserver.jdbc.ParameterUtils;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerError;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerMetaData;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerSecurityUtility;
import com.microsoft.sqlserver.jdbc.SQLServerSortOrder;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLState;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.SharedTimer;
import com.microsoft.sqlserver.jdbc.SqlVariant;
import com.microsoft.sqlserver.jdbc.TDS;
import com.microsoft.sqlserver.jdbc.TDSChannel;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSType;
import com.microsoft.sqlserver.jdbc.TVP;
import com.microsoft.sqlserver.jdbc.TVPType;
import com.microsoft.sqlserver.jdbc.UTC;
import com.microsoft.sqlserver.jdbc.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import microsoft.sql.DateTimeOffset;

final class TDSWriter {
    private static Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Writer");
    private static final String UNEXPECTED_SSTYPE = "Unexpected SSType: ";
    private final String traceID;
    private final TDSChannel tdsChannel;
    private final SQLServerConnection con;
    private boolean dataIsLoggable = true;
    private TDSCommand command = null;
    private byte tdsMessageType;
    private volatile int sendResetConnection = 0;
    private int currentPacketSize = 0;
    private static final int TDS_PACKET_HEADER_SIZE = 8;
    private static final byte[] placeholderHeader = new byte[8];
    private byte[] valueBytes = new byte[256];
    private int packetNum = 0;
    private static final int BYTES16 = 16;
    public static final int BIGDECIMAL_MAX_LENGTH = 17;
    private boolean isEOMSent = false;
    private ByteBuffer stagingBuffer;
    private ByteBuffer socketBuffer;
    private ByteBuffer logBuffer;
    private char[] streamCharBuffer;
    private byte[] streamByteBuffer;
    private CryptoMetadata cryptoMeta = null;

    public final String toString() {
        return this.traceID;
    }

    void setDataLoggable(boolean value) {
        this.dataIsLoggable = value;
    }

    SharedTimer getSharedTimer() throws SQLServerException {
        return this.con.getSharedTimer();
    }

    boolean isEOMSent() {
        return this.isEOMSent;
    }

    TDSWriter(TDSChannel tdsChannel, SQLServerConnection con) {
        this.tdsChannel = tdsChannel;
        this.con = con;
        this.traceID = "TDSWriter@" + Integer.toHexString(this.hashCode()) + " (" + con.toString() + ")";
    }

    boolean checkIfTdsMessageTypeIsBatchOrRPC() {
        return this.tdsMessageType == 1 || this.tdsMessageType == 3;
    }

    void preparePacket() throws SQLServerException {
        if (this.tdsChannel.isLoggingPackets()) {
            Arrays.fill(this.logBuffer.array(), (byte)-2);
            ((Buffer)this.logBuffer).clear();
        }
        this.writeBytes(placeholderHeader);
    }

    void writeMessageHeader() throws SQLServerException {
        if (1 == this.tdsMessageType || 14 == this.tdsMessageType || 3 == this.tdsMessageType) {
            int totalHeaderLength = 22;
            this.writeInt(totalHeaderLength);
            this.writeInt(18);
            this.writeShort((short)2);
            this.writeBytes(this.con.getTransactionDescriptor());
            this.writeInt(1);
        }
    }

    void startMessage(TDSCommand command, byte tdsMessageType) throws SQLServerException {
        this.command = command;
        this.tdsMessageType = tdsMessageType;
        this.packetNum = 0;
        this.isEOMSent = false;
        this.dataIsLoggable = true;
        int negotiatedPacketSize = this.con.getTDSPacketSize();
        if (this.currentPacketSize != negotiatedPacketSize) {
            this.socketBuffer = ByteBuffer.allocate(negotiatedPacketSize).order(ByteOrder.LITTLE_ENDIAN);
            this.stagingBuffer = ByteBuffer.allocate(negotiatedPacketSize).order(ByteOrder.LITTLE_ENDIAN);
            this.logBuffer = ByteBuffer.allocate(negotiatedPacketSize).order(ByteOrder.LITTLE_ENDIAN);
            this.currentPacketSize = negotiatedPacketSize;
            this.streamCharBuffer = new char[2 * this.currentPacketSize];
            this.streamByteBuffer = new byte[4 * this.currentPacketSize];
        }
        ((Buffer)this.socketBuffer).position(this.socketBuffer.limit());
        ((Buffer)this.stagingBuffer).clear();
        this.preparePacket();
        this.writeMessageHeader();
    }

    final void endMessage() throws SQLServerException {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.toString() + " Finishing TDS message");
        }
        this.writePacket(1);
    }

    final boolean ignoreMessage() throws SQLServerException {
        if (this.packetNum > 0 || 7 == this.tdsMessageType) {
            assert (!this.isEOMSent);
            if (logger.isLoggable(Level.FINER)) {
                logger.finest(this.toString() + " Finishing TDS message by sending ignore bit and end of message");
            }
            this.writePacket(3);
            return true;
        }
        return false;
    }

    final void resetPooledConnection() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.toString() + " resetPooledConnection");
        }
        this.sendResetConnection = 8;
    }

    void writeByte(byte value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 1) {
            this.stagingBuffer.put(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.put(value);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + 1);
                }
            }
        } else {
            this.valueBytes[0] = value;
            this.writeWrappedBytes(this.valueBytes, 1);
        }
    }

    void writeCollationForSqlVariant(SqlVariant variantType) throws SQLServerException {
        this.writeInt(variantType.getCollation().getCollationInfo());
        this.writeByte((byte)(variantType.getCollation().getCollationSortID() & 0xFF));
    }

    void writeChar(char value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 2) {
            this.stagingBuffer.putChar(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putChar(value);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + 2);
                }
            }
        } else {
            Util.writeShort((short)value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 2);
        }
    }

    void writeShort(short value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 2) {
            this.stagingBuffer.putShort(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putShort(value);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + 2);
                }
            }
        } else {
            Util.writeShort(value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 2);
        }
    }

    void writeInt(int value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 4) {
            this.stagingBuffer.putInt(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putInt(value);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + 4);
                }
            }
        } else {
            Util.writeInt(value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 4);
        }
    }

    void writeReal(float value) throws SQLServerException {
        this.writeInt(Float.floatToRawIntBits(value));
    }

    void writeDouble(double value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 8) {
            this.stagingBuffer.putDouble(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putDouble(value);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + 8);
                }
            }
        } else {
            long bits = Double.doubleToLongBits(value);
            long mask = 255L;
            int nShift = 0;
            for (int i = 0; i < 8; ++i) {
                this.writeByte((byte)((bits & mask) >> nShift));
                nShift += 8;
                mask <<= 8;
            }
        }
    }

    void writeBigDecimal(BigDecimal bigDecimalVal, int srcJdbcType, int precision, int scale) throws SQLServerException {
        bigDecimalVal = bigDecimalVal.setScale(scale, RoundingMode.HALF_UP);
        int bLength = 17;
        this.writeByte((byte)bLength);
        byte[] bytes = new byte[bLength];
        byte[] val = DDC.convertBigDecimalToBytes(bigDecimalVal, scale);
        System.arraycopy(val, 2, bytes, 0, val.length - 2);
        this.writeBytes(bytes);
    }

    void writeMoney(BigDecimal moneyVal, int srcJdbcType) throws SQLServerException {
        moneyVal = moneyVal.setScale(4, RoundingMode.HALF_UP);
        int bLength = srcJdbcType == -148 ? 8 : 4;
        this.writeByte((byte)bLength);
        byte[] val = DDC.convertMoneyToBytes(moneyVal, bLength);
        this.writeBytes(val);
    }

    void writeSqlVariantInternalBigDecimal(BigDecimal bigDecimalVal, int srcJdbcType) throws SQLServerException {
        boolean isNegative = bigDecimalVal.signum() < 0;
        BigInteger bi = bigDecimalVal.unscaledValue();
        if (isNegative) {
            bi = bi.negate();
        }
        int bLength = 16;
        this.writeByte((byte)(!isNegative ? 1 : 0));
        byte[] unscaledBytes = bi.toByteArray();
        if (unscaledBytes.length > bLength) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
            Object[] msgArgs = new Object[]{JDBCType.of(srcJdbcType)};
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET, null);
        }
        byte[] bytes = new byte[bLength];
        int remaining = bLength - unscaledBytes.length;
        int i = 0;
        int j = unscaledBytes.length - 1;
        while (i < unscaledBytes.length) {
            bytes[i++] = unscaledBytes[j--];
        }
        while (i < remaining) {
            bytes[i] = 0;
            ++i;
        }
        this.writeBytes(bytes);
    }

    void writeSmalldatetime(String value) throws SQLServerException {
        GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        Timestamp timestampValue = Timestamp.valueOf(value);
        long utcMillis = timestampValue.getTime();
        calendar.setTimeInMillis(utcMillis);
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(calendar.get(1), calendar.get(6), 1900);
        int millisSinceMidnight = 1000 * calendar.get(13) + 60000 * calendar.get(12) + 3600000 * calendar.get(11);
        if (86399999 <= millisSinceMidnight) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        this.writeShort((short)daysSinceSQLBaseDate);
        int secondsSinceMidnight = millisSinceMidnight / 1000;
        int minutesSinceMidnight = secondsSinceMidnight / 60;
        minutesSinceMidnight = (double)(secondsSinceMidnight % 60) > 29.998 ? minutesSinceMidnight + 1 : minutesSinceMidnight;
        this.writeShort((short)minutesSinceMidnight);
    }

    void writeDatetime(String value) throws SQLServerException {
        GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        Timestamp timestampValue = Timestamp.valueOf(value);
        long utcMillis = timestampValue.getTime();
        int subSecondNanos = timestampValue.getNanos();
        calendar.setTimeInMillis(utcMillis);
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(calendar.get(1), calendar.get(6), 1900);
        int millisSinceMidnight = (subSecondNanos + 500000) / 1000000 + 1000 * calendar.get(13) + 60000 * calendar.get(12) + 3600000 * calendar.get(11);
        if (86399999 <= millisSinceMidnight) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1753, 1, 1900) || daysSinceSQLBaseDate >= DDC.daysSinceBaseDate(10000, 1, 1900)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
            Object[] msgArgs = new Object[]{SSType.DATETIME};
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
        }
        this.writeInt(daysSinceSQLBaseDate);
        this.writeInt((3 * millisSinceMidnight + 5) / 10);
    }

    void writeDate(String value) throws SQLServerException {
        GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        Date dateValue = Date.valueOf(value);
        long utcMillis = dateValue.getTime();
        calendar.setTimeInMillis(utcMillis);
        this.writeScaledTemporal(calendar, 0, 0, SSType.DATE);
    }

    void writeTime(Timestamp value, int scale) throws SQLServerException {
        GregorianCalendar calendar = this.initializeCalender(TimeZone.getDefault());
        long utcMillis = value.getTime();
        int subSecondNanos = value.getNanos();
        calendar.setTimeInMillis(utcMillis);
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.TIME);
    }

    void writeDateTimeOffset(Object value, int scale, SSType destSSType) throws SQLServerException {
        int subSecondNanos;
        GregorianCalendar calendar;
        int minutesOffset;
        if (value instanceof String) {
            try {
                String timestampString;
                String stringValue = (String)value;
                int lastColon = stringValue.lastIndexOf(58);
                String offsetString = stringValue.substring(lastColon - 3);
                if (!offsetString.startsWith("+") && !offsetString.startsWith("-")) {
                    minutesOffset = 0;
                    timestampString = stringValue;
                } else {
                    minutesOffset = 60 * Integer.parseInt(offsetString.substring(1, 3)) + Integer.parseInt(offsetString.substring(4, 6));
                    timestampString = stringValue.substring(0, lastColon - 4);
                    if (offsetString.startsWith("-")) {
                        minutesOffset = -minutesOffset;
                    }
                }
                TimeZone timeZone = SSType.DATETIMEOFFSET == destSSType ? UTC.timeZone : new SimpleTimeZone(minutesOffset * 60 * 1000, "");
                calendar = new GregorianCalendar(timeZone);
                int year = Integer.parseInt(timestampString.substring(0, 4));
                int month = Integer.parseInt(timestampString.substring(5, 7));
                int day = Integer.parseInt(timestampString.substring(8, 10));
                int hour = Integer.parseInt(timestampString.substring(11, 13));
                int minute = Integer.parseInt(timestampString.substring(14, 16));
                int second = Integer.parseInt(timestampString.substring(17, 19));
                subSecondNanos = 19 == timestampString.indexOf(46) ? new BigDecimal(timestampString.substring(19)).scaleByPowerOfTen(9).intValue() : 0;
                calendar.setLenient(true);
                calendar.set(1, year);
                calendar.set(2, month - 1);
                calendar.set(5, day);
                calendar.set(11, hour);
                calendar.set(12, minute);
                calendar.set(13, second);
                calendar.add(12, -minutesOffset);
            }
            catch (IndexOutOfBoundsException | NumberFormatException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ParsingDataError"));
                Object[] msgArgs = new Object[]{value, JDBCType.DATETIMEOFFSET};
                throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
            }
        } else {
            DateTimeOffset dtoValue = (DateTimeOffset)value;
            long utcMillis = dtoValue.getTimestamp().getTime();
            subSecondNanos = dtoValue.getTimestamp().getNanos();
            minutesOffset = dtoValue.getMinutesOffset();
            TimeZone timeZone = SSType.DATETIMEOFFSET == destSSType ? UTC.timeZone : new SimpleTimeZone(minutesOffset * 60 * 1000, "");
            calendar = new GregorianCalendar(timeZone, Locale.US);
            calendar.setLenient(true);
            calendar.clear();
            calendar.setTimeInMillis(utcMillis);
        }
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }

    void writeOffsetDateTimeWithTimezone(OffsetDateTime offsetDateTimeValue, int scale) throws SQLServerException {
        int minutesOffset = 0;
        try {
            minutesOffset = offsetDateTimeValue.getOffset().getTotalSeconds() / 60;
        }
        catch (Exception e) {
            throw new SQLServerException(SQLServerException.getErrString("R_zoneOffsetError"), null, 0, (Throwable)e);
        }
        int subSecondNanos = offsetDateTimeValue.getNano();
        for (int padding = 9 - String.valueOf(subSecondNanos).length(); padding > 0; --padding) {
            subSecondNanos *= 10;
        }
        TimeZone timeZone = UTC.timeZone;
        String offDateTimeStr = String.format("%04d", offsetDateTimeValue.getYear()) + "-" + offsetDateTimeValue.getMonthValue() + "-" + offsetDateTimeValue.getDayOfMonth() + " " + offsetDateTimeValue.getHour() + ":" + offsetDateTimeValue.getMinute() + ":" + offsetDateTimeValue.getSecond();
        long utcMillis = Timestamp.valueOf(offDateTimeStr).getTime();
        GregorianCalendar calendar = this.initializeCalender(timeZone);
        calendar.setTimeInMillis(utcMillis);
        int minuteAdjustment = TimeZone.getDefault().getRawOffset() / 60000;
        if (TimeZone.getDefault().inDaylightTime(calendar.getTime())) {
            minuteAdjustment += TimeZone.getDefault().getDSTSavings() / 60000;
        }
        calendar.add(12, minuteAdjustment += minuteAdjustment < 0 ? minutesOffset * -1 : minutesOffset);
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }

    void writeOffsetTimeWithTimezone(OffsetTime offsetTimeValue, int scale) throws SQLServerException {
        int minutesOffset = 0;
        try {
            minutesOffset = offsetTimeValue.getOffset().getTotalSeconds() / 60;
        }
        catch (Exception e) {
            throw new SQLServerException(SQLServerException.getErrString("R_zoneOffsetError"), null, 0, (Throwable)e);
        }
        int subSecondNanos = offsetTimeValue.getNano();
        for (int padding = 9 - String.valueOf(subSecondNanos).length(); padding > 0; --padding) {
            subSecondNanos *= 10;
        }
        TimeZone timeZone = UTC.timeZone;
        String offsetTimeStr = "1900-01-01 " + offsetTimeValue.getHour() + ":" + offsetTimeValue.getMinute() + ":" + offsetTimeValue.getSecond();
        long utcMillis = Timestamp.valueOf(offsetTimeStr).getTime();
        GregorianCalendar calendar = this.initializeCalender(timeZone);
        calendar.setTimeInMillis(utcMillis);
        int minuteAdjustment = TimeZone.getDefault().getRawOffset() / 60000;
        if (TimeZone.getDefault().inDaylightTime(calendar.getTime())) {
            minuteAdjustment += TimeZone.getDefault().getDSTSavings() / 60000;
        }
        calendar.add(12, minuteAdjustment += minuteAdjustment < 0 ? minutesOffset * -1 : minutesOffset);
        this.writeScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }

    void writeLong(long value) throws SQLServerException {
        if (this.stagingBuffer.remaining() >= 8) {
            this.stagingBuffer.putLong(value);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.putLong(value);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + 8);
                }
            }
        } else {
            Util.writeLong(value, this.valueBytes, 0);
            this.writeWrappedBytes(this.valueBytes, 8);
        }
    }

    void writeBytes(byte[] value) throws SQLServerException {
        this.writeBytes(value, 0, value.length);
    }

    void writeBytes(byte[] value, int offset, int length) throws SQLServerException {
        int bytesToWrite;
        assert (length <= value.length);
        int bytesWritten = 0;
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.toString() + " Writing " + length + " bytes");
        }
        while ((bytesToWrite = length - bytesWritten) > 0) {
            if (0 == this.stagingBuffer.remaining()) {
                this.writePacket(0);
            }
            if (bytesToWrite > this.stagingBuffer.remaining()) {
                bytesToWrite = this.stagingBuffer.remaining();
            }
            this.stagingBuffer.put(value, offset + bytesWritten, bytesToWrite);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.put(value, offset + bytesWritten, bytesToWrite);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + bytesToWrite);
                }
            }
            bytesWritten += bytesToWrite;
        }
    }

    void writeWrappedBytes(byte[] value, int valueLength) throws SQLServerException {
        assert (valueLength <= value.length);
        int remaining = this.stagingBuffer.remaining();
        assert (remaining < valueLength);
        assert (valueLength <= this.stagingBuffer.capacity());
        remaining = this.stagingBuffer.remaining();
        if (remaining > 0) {
            this.stagingBuffer.put(value, 0, remaining);
            if (this.tdsChannel.isLoggingPackets()) {
                if (this.dataIsLoggable) {
                    this.logBuffer.put(value, 0, remaining);
                } else {
                    ((Buffer)this.logBuffer).position(this.logBuffer.position() + remaining);
                }
            }
        }
        this.writePacket(0);
        this.stagingBuffer.put(value, remaining, valueLength - remaining);
        if (this.tdsChannel.isLoggingPackets()) {
            if (this.dataIsLoggable) {
                this.logBuffer.put(value, remaining, valueLength - remaining);
            } else {
                ((Buffer)this.logBuffer).position(this.logBuffer.position() + remaining);
            }
        }
    }

    void writeString(String value) throws SQLServerException {
        int charsCopied = 0;
        int length = value.length();
        while (charsCopied < length) {
            long bytesToCopy = 2L * ((long)length - (long)charsCopied);
            if (bytesToCopy > (long)this.valueBytes.length) {
                bytesToCopy = this.valueBytes.length;
            }
            int bytesCopied = 0;
            try {
                while ((long)bytesCopied < bytesToCopy) {
                    char ch = value.charAt(charsCopied++);
                    this.valueBytes[bytesCopied++] = (byte)(ch >> 0 & 0xFF);
                    this.valueBytes[bytesCopied++] = (byte)(ch >> 8 & 0xFF);
                }
                this.writeBytes(this.valueBytes, 0, bytesCopied);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
                Object[] msgArgs = new Object[]{bytesCopied};
                this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
            }
        }
    }

    void writeStream(InputStream inputStream, long advertisedLength, boolean writeChunkSizes) throws SQLServerException {
        Object[] msgArgs;
        MessageFormat form;
        int bytesToWrite;
        assert (-1L == advertisedLength || advertisedLength >= 0L);
        long actualLength = 0L;
        byte[] buff = new byte[4 * this.currentPacketSize];
        int bytesRead = 0;
        do {
            for (bytesToWrite = 0; -1 != bytesRead && bytesToWrite < buff.length; bytesToWrite += bytesRead) {
                try {
                    bytesRead = inputStream.read(buff, bytesToWrite, buff.length - bytesToWrite);
                }
                catch (IOException e) {
                    MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    Object[] msgArgs2 = new Object[]{e.toString()};
                    this.error(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
                if (-1 == bytesRead) break;
                if (bytesRead >= 0 && bytesRead <= buff.length - bytesToWrite) continue;
                form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                msgArgs = new Object[]{SQLServerException.getErrString("R_streamReadReturnedInvalidValue")};
                this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
            }
            if (writeChunkSizes) {
                this.writeInt(bytesToWrite);
            }
            this.writeBytes(buff, 0, bytesToWrite);
            actualLength += (long)bytesToWrite;
        } while (-1 != bytesRead || bytesToWrite > 0);
        if (-1L != advertisedLength && actualLength != advertisedLength) {
            form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
            msgArgs = new Object[]{advertisedLength, actualLength};
            this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET);
        }
    }

    void writeNonUnicodeReader(Reader reader, long advertisedLength, boolean isDestBinary) throws SQLServerException {
        MessageFormat form;
        int charsToWrite;
        assert (-1L == advertisedLength || advertisedLength >= 0L);
        long actualLength = 0L;
        int charsRead = 0;
        do {
            int bytesToWrite;
            for (charsToWrite = 0; -1 != charsRead && charsToWrite < this.currentPacketSize; charsToWrite += charsRead) {
                try {
                    charsRead = reader.read(this.streamCharBuffer, charsToWrite, this.currentPacketSize - charsToWrite);
                }
                catch (IOException e) {
                    MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    Object[] msgArgs = new Object[]{e.toString()};
                    this.error(form2.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
                if (-1 == charsRead) break;
                if (charsRead >= 0 && charsRead <= this.currentPacketSize - charsToWrite) continue;
                form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                Object[] msgArgs = new Object[]{SQLServerException.getErrString("R_streamReadReturnedInvalidValue")};
                this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
            }
            if (!isDestBinary) {
                Charset charSet = this.con.getDatabaseCollation().getCharset();
                if (null == charSet) {
                    this.writeInt(charsToWrite);
                    for (int charsCopied = 0; charsCopied < charsToWrite; ++charsCopied) {
                        this.streamByteBuffer[charsCopied] = (byte)(this.streamCharBuffer[charsCopied] & 0xFF);
                    }
                    this.writeBytes(this.streamByteBuffer, 0, charsToWrite);
                } else {
                    bytesToWrite = 0;
                    for (int charsCopied = 0; charsCopied < charsToWrite; ++charsCopied) {
                        byte[] charBytes = new String("" + this.streamCharBuffer[charsCopied]).getBytes(charSet);
                        System.arraycopy(charBytes, 0, this.streamByteBuffer, bytesToWrite, charBytes.length);
                        bytesToWrite += charBytes.length;
                    }
                    this.writeInt(bytesToWrite);
                    this.writeBytes(this.streamByteBuffer, 0, bytesToWrite);
                }
            } else {
                bytesToWrite = charsToWrite;
                if (0 != charsToWrite) {
                    bytesToWrite = charsToWrite / 2;
                }
                String streamString = new String(this.streamCharBuffer, 0, this.currentPacketSize);
                byte[] bytes = ParameterUtils.hexToBin(streamString.trim());
                this.writeInt(bytesToWrite);
                this.writeBytes(bytes, 0, bytesToWrite);
            }
            actualLength += (long)charsToWrite;
        } while (-1 != charsRead || charsToWrite > 0);
        if (-1L != advertisedLength && actualLength != advertisedLength) {
            form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
            Object[] msgArgs = new Object[]{advertisedLength, actualLength};
            this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET);
        }
    }

    void writeReader(Reader reader, long advertisedLength, boolean writeChunkSizes) throws SQLServerException {
        Object[] msgArgs;
        int charsToWrite;
        assert (-1L == advertisedLength || advertisedLength >= 0L);
        long actualLength = 0L;
        int charsRead = 0;
        do {
            for (charsToWrite = 0; -1 != charsRead && charsToWrite < this.streamCharBuffer.length; charsToWrite += charsRead) {
                try {
                    charsRead = reader.read(this.streamCharBuffer, charsToWrite, this.streamCharBuffer.length - charsToWrite);
                }
                catch (IOException e) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                    Object[] msgArgs2 = new Object[]{e.toString()};
                    this.error(form.format(msgArgs2), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
                }
                if (-1 == charsRead) break;
                if (charsRead >= 0 && charsRead <= this.streamCharBuffer.length - charsToWrite) continue;
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                msgArgs = new Object[]{SQLServerException.getErrString("R_streamReadReturnedInvalidValue")};
                this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET);
            }
            if (writeChunkSizes) {
                this.writeInt(2 * charsToWrite);
            }
            for (int charsCopied = 0; charsCopied < charsToWrite; ++charsCopied) {
                this.streamByteBuffer[2 * charsCopied] = (byte)(this.streamCharBuffer[charsCopied] >> 0 & 0xFF);
                this.streamByteBuffer[2 * charsCopied + 1] = (byte)(this.streamCharBuffer[charsCopied] >> 8 & 0xFF);
            }
            this.writeBytes(this.streamByteBuffer, 0, 2 * charsToWrite);
            actualLength += (long)charsToWrite;
        } while (-1 != charsRead || charsToWrite > 0);
        if (-1L != advertisedLength && actualLength != advertisedLength) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_mismatchedStreamLength"));
            msgArgs = new Object[]{advertisedLength, actualLength};
            this.error(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET);
        }
    }

    GregorianCalendar initializeCalender(TimeZone timeZone) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone, Locale.US);
        calendar.setLenient(true);
        calendar.clear();
        return calendar;
    }

    final void error(String reason, SQLState sqlState, DriverError driverError) throws SQLServerException {
        assert (null != this.command);
        this.command.interrupt(reason);
        throw new SQLServerException(reason, sqlState, driverError, null);
    }

    final boolean sendAttention() throws SQLServerException {
        if (this.packetNum > 0) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(this + ": sending attention...");
            }
            ++this.tdsChannel.numMsgsSent;
            this.startMessage(this.command, (byte)6);
            this.endMessage();
            return true;
        }
        return false;
    }

    private void writePacket(int tdsMessageStatus) throws SQLServerException {
        boolean isCancelled;
        boolean atEOM = 1 == (1 & tdsMessageStatus);
        boolean bl = isCancelled = 6 == this.tdsMessageType || (tdsMessageStatus & 2) == 2;
        if (null != this.command && !isCancelled) {
            this.command.checkForInterrupt();
        }
        this.writePacketHeader(tdsMessageStatus | this.sendResetConnection);
        this.sendResetConnection = 0;
        this.flush(atEOM);
        if (atEOM) {
            this.flush(atEOM);
            this.isEOMSent = true;
            ++this.tdsChannel.numMsgsSent;
        }
        if (16 == this.tdsMessageType && 1 == this.packetNum && 0 == this.con.getNegotiatedEncryptionLevel()) {
            this.tdsChannel.disableSSL();
        }
        if (null != this.command && !isCancelled && atEOM) {
            this.command.onRequestComplete();
        }
    }

    private void writePacketHeader(int tdsMessageStatus) {
        int tdsMessageLength = this.stagingBuffer.position();
        ++this.packetNum;
        this.stagingBuffer.put(0, this.tdsMessageType);
        this.stagingBuffer.put(1, (byte)tdsMessageStatus);
        this.stagingBuffer.put(2, (byte)(tdsMessageLength >> 8 & 0xFF));
        this.stagingBuffer.put(3, (byte)(tdsMessageLength >> 0 & 0xFF));
        this.stagingBuffer.put(4, (byte)(this.tdsChannel.getSPID() >> 8 & 0xFF));
        this.stagingBuffer.put(5, (byte)(this.tdsChannel.getSPID() >> 0 & 0xFF));
        this.stagingBuffer.put(6, (byte)(this.packetNum % 256));
        this.stagingBuffer.put(7, (byte)0);
        if (this.tdsChannel.isLoggingPackets()) {
            this.logBuffer.put(0, this.tdsMessageType);
            this.logBuffer.put(1, (byte)tdsMessageStatus);
            this.logBuffer.put(2, (byte)(tdsMessageLength >> 8 & 0xFF));
            this.logBuffer.put(3, (byte)(tdsMessageLength >> 0 & 0xFF));
            this.logBuffer.put(4, (byte)(this.tdsChannel.getSPID() >> 8 & 0xFF));
            this.logBuffer.put(5, (byte)(this.tdsChannel.getSPID() >> 0 & 0xFF));
            this.logBuffer.put(6, (byte)(this.packetNum % 256));
            this.logBuffer.put(7, (byte)0);
        }
    }

    void flush(boolean atEOM) throws SQLServerException {
        this.tdsChannel.write(this.socketBuffer.array(), this.socketBuffer.position(), this.socketBuffer.remaining());
        ((Buffer)this.socketBuffer).position(this.socketBuffer.limit());
        if (this.stagingBuffer.position() >= 8) {
            ByteBuffer swapBuffer = this.stagingBuffer;
            this.stagingBuffer = this.socketBuffer;
            this.socketBuffer = swapBuffer;
            ((Buffer)this.socketBuffer).flip();
            ((Buffer)this.stagingBuffer).clear();
            if (this.tdsChannel.isLoggingPackets()) {
                this.tdsChannel.logPacket(this.logBuffer.array(), 0, this.socketBuffer.limit(), this.toString() + " sending packet (" + this.socketBuffer.limit() + " bytes)");
            }
            if (!atEOM) {
                this.preparePacket();
            }
            this.tdsChannel.write(this.socketBuffer.array(), this.socketBuffer.position(), this.socketBuffer.remaining());
            ((Buffer)this.socketBuffer).position(this.socketBuffer.limit());
        }
    }

    void writeRPCNameValType(String sName, boolean bOut, TDSType tdsType) throws SQLServerException {
        int nNameLen = 0;
        if (null != sName) {
            nNameLen = sName.length() + 1;
        }
        this.writeByte((byte)nNameLen);
        if (nNameLen > 0) {
            this.writeChar('@');
            this.writeString(sName);
        }
        if (null != this.cryptoMeta) {
            this.writeByte((byte)(bOut ? 9 : 8));
        } else {
            this.writeByte((byte)(bOut ? 1 : 0));
        }
        this.writeByte(tdsType.byteValue());
    }

    void writeRPCBit(String sName, Boolean booleanValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BITN);
        this.writeByte((byte)1);
        if (null == booleanValue) {
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)1);
            this.writeByte((byte)(booleanValue != false ? 1 : 0));
        }
    }

    void writeRPCByte(String sName, Byte byteValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)1);
        if (null == byteValue) {
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)1);
            this.writeByte(byteValue);
        }
    }

    void writeRPCShort(String sName, Short shortValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)2);
        if (null == shortValue) {
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)2);
            this.writeShort(shortValue);
        }
    }

    void writeRPCInt(String sName, Integer intValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)4);
        if (null == intValue) {
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)4);
            this.writeInt(intValue);
        }
    }

    void writeRPCLong(String sName, Long longValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.INTN);
        this.writeByte((byte)8);
        if (null == longValue) {
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)8);
            this.writeLong(longValue);
        }
    }

    void writeRPCReal(String sName, Float floatValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.FLOATN);
        if (null == floatValue) {
            this.writeByte((byte)4);
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)4);
            this.writeByte((byte)4);
            this.writeInt(Float.floatToRawIntBits(floatValue.floatValue()));
        }
    }

    void writeRPCSqlVariant(String sName, SqlVariant sqlVariantValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.SQL_VARIANT);
        if (null == sqlVariantValue) {
            this.writeInt(0);
            this.writeInt(0);
        }
    }

    void writeRPCDouble(String sName, Double doubleValue, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.FLOATN);
        int l = 8;
        this.writeByte((byte)l);
        if (null == doubleValue) {
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)l);
            long bits = Double.doubleToLongBits(doubleValue);
            long mask = 255L;
            int nShift = 0;
            for (int i = 0; i < 8; ++i) {
                this.writeByte((byte)((bits & mask) >> nShift));
                nShift += 8;
                mask <<= 8;
            }
        }
    }

    void writeRPCBigDecimal(String sName, BigDecimal bdValue, int nScale, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DECIMALN);
        this.writeByte((byte)17);
        this.writeByte((byte)38);
        byte[] val = DDC.convertBigDecimalToBytes(bdValue, nScale);
        this.writeBytes(val, 0, val.length);
    }

    void writeVMaxHeader(long headerLength, boolean isNull, SQLCollation collation) throws SQLServerException {
        this.writeShort((short)-1);
        if (null != collation) {
            collation.writeCollation(this);
        }
        if (isNull) {
            this.writeLong(-1L);
        } else if (-1L == headerLength) {
            this.writeLong(-2L);
        } else {
            this.writeLong(headerLength);
        }
    }

    void writeRPCStringUnicode(String sValue) throws SQLServerException {
        this.writeRPCStringUnicode(null, sValue, false, null);
    }

    void writeRPCStringUnicode(String sName, String sValue, boolean bOut, SQLCollation collation) throws SQLServerException {
        int nValueLen;
        boolean bValueNull = sValue == null;
        int n = nValueLen = bValueNull ? 0 : 2 * sValue.length();
        if (null == collation) {
            collation = this.con.getDatabaseCollation();
        }
        if (nValueLen > 8000 || bOut) {
            this.writeRPCNameValType(sName, bOut, TDSType.NVARCHAR);
            this.writeVMaxHeader(nValueLen, bValueNull, collation);
            if (!bValueNull) {
                if (nValueLen > 0) {
                    this.writeInt(nValueLen);
                    this.writeString(sValue);
                }
                this.writeInt(0);
            }
        } else {
            this.writeRPCNameValType(sName, bOut, TDSType.NVARCHAR);
            this.writeShort((short)8000);
            collation.writeCollation(this);
            if (bValueNull) {
                this.writeShort((short)-1);
            } else {
                this.writeShort((short)nValueLen);
                if (0 != nValueLen) {
                    this.writeString(sValue);
                }
            }
        }
    }

    void writeTVP(TVP value) throws SQLServerException {
        if (!value.isNull()) {
            this.writeByte((byte)0);
        } else {
            this.writeByte((byte)2);
        }
        this.writeByte((byte)-13);
        if (null != value.getDbNameTVP()) {
            this.writeByte((byte)value.getDbNameTVP().length());
            this.writeString(value.getDbNameTVP());
        } else {
            this.writeByte((byte)0);
        }
        if (null != value.getOwningSchemaNameTVP()) {
            this.writeByte((byte)value.getOwningSchemaNameTVP().length());
            this.writeString(value.getOwningSchemaNameTVP());
        } else {
            this.writeByte((byte)0);
        }
        if (null != value.getTVPName()) {
            this.writeByte((byte)value.getTVPName().length());
            this.writeString(value.getTVPName());
        } else {
            this.writeByte((byte)0);
        }
        if (!value.isNull()) {
            this.writeTVPColumnMetaData(value);
            this.writeTvpOrderUnique(value);
        } else {
            this.writeShort((short)-1);
        }
        this.writeByte((byte)0);
        try {
            this.writeTVPRows(value);
        }
        catch (ClassCastException | NumberFormatException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_TVPInvalidColumnValue"), e);
        }
    }

    void writeTVPRows(TVP value) throws SQLServerException {
        boolean tdsWritterCached = false;
        ByteBuffer cachedTVPHeaders = null;
        TDSCommand cachedCommand = null;
        boolean cachedRequestComplete = false;
        boolean cachedInterruptsEnabled = false;
        boolean cachedProcessedResponse = false;
        if (!value.isNull()) {
            if (TVPType.RESULTSET == value.tvpType && value.sourceResultSet instanceof SQLServerResultSet) {
                SQLServerResultSet sourceResultSet = (SQLServerResultSet)value.sourceResultSet;
                SQLServerStatement srcStmt = (SQLServerStatement)sourceResultSet.getStatement();
                int resultSetServerCursorId = sourceResultSet.getServerCursorId();
                if (this.con.equals(srcStmt.getConnection()) && 0 != resultSetServerCursorId) {
                    cachedTVPHeaders = ByteBuffer.allocate(this.stagingBuffer.capacity()).order(this.stagingBuffer.order());
                    cachedTVPHeaders.put(this.stagingBuffer.array(), 0, this.stagingBuffer.position());
                    cachedCommand = this.command;
                    cachedRequestComplete = this.command.getRequestComplete();
                    cachedInterruptsEnabled = this.command.getInterruptsEnabled();
                    cachedProcessedResponse = this.command.getProcessedResponse();
                    tdsWritterCached = true;
                    if (sourceResultSet.isForwardOnly()) {
                        sourceResultSet.setFetchSize(1);
                    }
                }
            }
            Map<Integer, SQLServerMetaData> columnMetadata = value.getColumnMetadata();
            while (value.next()) {
                if (tdsWritterCached) {
                    this.command = cachedCommand;
                    ((Buffer)this.stagingBuffer).clear();
                    ((Buffer)this.logBuffer).clear();
                    this.writeBytes(cachedTVPHeaders.array(), 0, cachedTVPHeaders.position());
                }
                Object[] rowData = value.getRowData();
                this.writeByte((byte)1);
                Iterator<Map.Entry<Integer, SQLServerMetaData>> columnsIterator = columnMetadata.entrySet().iterator();
                int currentColumn = 0;
                while (columnsIterator.hasNext()) {
                    Map.Entry<Integer, SQLServerMetaData> columnPair = columnsIterator.next();
                    if (columnPair.getValue().useServerDefault) {
                        ++currentColumn;
                        continue;
                    }
                    JDBCType jdbcType = JDBCType.of(columnPair.getValue().javaSqlType);
                    String currentColumnStringValue = null;
                    Object currentObject = null;
                    if (null != rowData && rowData.length > currentColumn && null != (currentObject = rowData[currentColumn])) {
                        currentColumnStringValue = String.valueOf(currentObject);
                    }
                    this.writeInternalTVPRowValues(jdbcType, currentColumnStringValue, currentObject, columnPair, false);
                    ++currentColumn;
                }
                if (!tdsWritterCached) continue;
                this.writeByte((byte)0);
                this.writePacket(1);
                TDSReader tdsReader = this.tdsChannel.getReader(this.command);
                int tokenType = tdsReader.peekTokenType();
                if (170 == tokenType) {
                    SQLServerError databaseError = new SQLServerError();
                    databaseError.setFromTDS(tdsReader);
                    SQLServerException.makeFromDatabaseError(this.con, null, databaseError.getErrorMessage(), databaseError, false);
                }
                this.command.setInterruptsEnabled(true);
                this.command.setRequestComplete(false);
            }
        }
        if (tdsWritterCached) {
            this.command.setRequestComplete(cachedRequestComplete);
            this.command.setInterruptsEnabled(cachedInterruptsEnabled);
            this.command.setProcessedResponse(cachedProcessedResponse);
        } else {
            this.writeByte((byte)0);
        }
    }

    private void writeInternalTVPRowValues(JDBCType jdbcType, String currentColumnStringValue, Object currentObject, Map.Entry<Integer, SQLServerMetaData> columnPair, boolean isSqlVariant) throws SQLServerException {
        switch (jdbcType) {
            case BIGINT: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(10, TDSType.INT8.byteValue(), (byte)0);
                } else {
                    this.writeByte((byte)8);
                }
                this.writeLong(Long.parseLong(currentColumnStringValue));
                break;
            }
            case BIT: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(3, TDSType.BIT1.byteValue(), (byte)0);
                } else {
                    this.writeByte((byte)1);
                }
                this.writeByte((byte)(Boolean.parseBoolean(currentColumnStringValue) ? 1 : 0));
                break;
            }
            case INTEGER: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (!isSqlVariant) {
                    this.writeByte((byte)4);
                } else {
                    this.writeTVPSqlVariantHeader(6, TDSType.INT4.byteValue(), (byte)0);
                }
                this.writeInt(Integer.parseInt(currentColumnStringValue));
                break;
            }
            case SMALLINT: 
            case TINYINT: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(6, TDSType.INT4.byteValue(), (byte)0);
                    this.writeInt(Integer.parseInt(currentColumnStringValue));
                    break;
                }
                this.writeByte((byte)2);
                this.writeShort(Short.parseShort(currentColumnStringValue));
                break;
            }
            case DECIMAL: 
            case NUMERIC: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(21, TDSType.DECIMALN.byteValue(), (byte)2);
                    this.writeByte((byte)38);
                    this.writeByte((byte)4);
                } else {
                    this.writeByte((byte)17);
                }
                BigDecimal bdValue = new BigDecimal(currentColumnStringValue);
                bdValue = bdValue.setScale(columnPair.getValue().scale, RoundingMode.HALF_UP);
                byte[] val = DDC.convertBigDecimalToBytes(bdValue, bdValue.scale());
                byte[] byteValue = new byte[17];
                System.arraycopy(val, 2, byteValue, 0, val.length - 2);
                this.writeBytes(byteValue);
                break;
            }
            case DOUBLE: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(10, TDSType.FLOAT8.byteValue(), (byte)0);
                    this.writeDouble(Double.parseDouble(currentColumnStringValue));
                    break;
                }
                this.writeByte((byte)8);
                long bits = Double.doubleToLongBits(Double.parseDouble(currentColumnStringValue));
                long mask = 255L;
                int nShift = 0;
                for (int i = 0; i < 8; ++i) {
                    this.writeByte((byte)((bits & mask) >> nShift));
                    nShift += 8;
                    mask <<= 8;
                }
                break;
            }
            case FLOAT: 
            case REAL: {
                if (null == currentColumnStringValue) {
                    this.writeByte((byte)0);
                    break;
                }
                if (isSqlVariant) {
                    this.writeTVPSqlVariantHeader(6, TDSType.FLOAT4.byteValue(), (byte)0);
                    this.writeInt(Float.floatToRawIntBits(Float.parseFloat(currentColumnStringValue)));
                    break;
                }
                this.writeByte((byte)4);
                this.writeInt(Float.floatToRawIntBits(Float.parseFloat(currentColumnStringValue)));
                break;
            }
            case DATE: 
            case TIME: 
            case TIMESTAMP: 
            case DATETIMEOFFSET: 
            case DATETIME: 
            case SMALLDATETIME: 
            case TIMESTAMP_WITH_TIMEZONE: 
            case TIME_WITH_TIMEZONE: 
            case CHAR: 
            case VARCHAR: 
            case NCHAR: 
            case NVARCHAR: 
            case LONGVARCHAR: 
            case LONGNVARCHAR: 
            case SQLXML: {
                int dataLength;
                boolean isShortValue = 2L * (long)columnPair.getValue().precision <= 8000L;
                boolean isNull = null == currentColumnStringValue;
                int n = dataLength = isNull ? 0 : currentColumnStringValue.length() * 2;
                if (!isShortValue) {
                    if (isNull) {
                        this.writeLong(-1L);
                    } else {
                        if (isSqlVariant) {
                            if (dataLength > 16000) {
                                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidStringValue"));
                                throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                            }
                            int length = currentColumnStringValue.length();
                            this.writeTVPSqlVariantHeader(9 + length, TDSType.BIGVARCHAR.byteValue(), (byte)7);
                            SQLCollation col = this.con.getDatabaseCollation();
                            this.writeInt(col.getCollationInfo());
                            this.writeByte((byte)col.getCollationSortID());
                            this.writeShort((short)length);
                            this.writeBytes(currentColumnStringValue.getBytes());
                            break;
                        }
                        if (-1 == dataLength) {
                            this.writeLong(-2L);
                        } else {
                            this.writeLong(dataLength);
                        }
                    }
                    if (isNull) break;
                    if (dataLength > 0) {
                        this.writeInt(dataLength);
                        this.writeString(currentColumnStringValue);
                    }
                    this.writeInt(0);
                    break;
                }
                if (isNull) {
                    this.writeShort((short)-1);
                    break;
                }
                if (isSqlVariant) {
                    int length = currentColumnStringValue.length() * 2;
                    this.writeTVPSqlVariantHeader(9 + length, TDSType.NVARCHAR.byteValue(), (byte)7);
                    SQLCollation col = this.con.getDatabaseCollation();
                    this.writeInt(col.getCollationInfo());
                    this.writeByte((byte)col.getCollationSortID());
                    int stringLength = currentColumnStringValue.length();
                    byte[] typevarlen = new byte[]{(byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF)};
                    this.writeBytes(typevarlen);
                    this.writeString(currentColumnStringValue);
                    break;
                }
                this.writeShort((short)dataLength);
                this.writeString(currentColumnStringValue);
                break;
            }
            case BINARY: 
            case VARBINARY: 
            case LONGVARBINARY: {
                int dataLength;
                boolean isNull;
                boolean isShortValue = columnPair.getValue().precision <= 8000;
                boolean bl = isNull = null == currentObject;
                if (currentObject instanceof String) {
                    dataLength = ParameterUtils.hexToBin(currentObject.toString()).length;
                } else {
                    int n = dataLength = isNull ? 0 : ((byte[])currentObject).length;
                }
                if (!isShortValue) {
                    if (isNull) {
                        this.writeLong(-1L);
                    } else if (-1 == dataLength) {
                        this.writeLong(-2L);
                    } else {
                        this.writeLong(dataLength);
                    }
                    if (isNull) break;
                    if (dataLength > 0) {
                        this.writeInt(dataLength);
                        if (currentObject instanceof String) {
                            this.writeBytes(ParameterUtils.hexToBin(currentObject.toString()));
                        } else {
                            this.writeBytes((byte[])currentObject);
                        }
                    }
                    this.writeInt(0);
                    break;
                }
                if (isNull) {
                    this.writeShort((short)-1);
                    break;
                }
                this.writeShort((short)dataLength);
                if (currentObject instanceof String) {
                    this.writeBytes(ParameterUtils.hexToBin(currentObject.toString()));
                    break;
                }
                this.writeBytes((byte[])currentObject);
                break;
            }
            case SQL_VARIANT: {
                boolean isShiloh;
                boolean bl = isShiloh = 8 >= this.con.getServerMajorVersion();
                if (isShiloh) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_SQLVariantSupport"));
                    throw new SQLServerException(null, form.format(new Object[0]), null, 0, false);
                }
                JavaType javaType = JavaType.of(currentObject);
                JDBCType internalJDBCType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
                this.writeInternalTVPRowValues(internalJDBCType, currentColumnStringValue, currentObject, columnPair, true);
                break;
            }
            default: {
                assert (false) : "Unexpected JDBC type " + jdbcType.toString();
                break;
            }
        }
    }

    private void writeTVPSqlVariantHeader(int length, byte tdsType, byte probBytes) throws SQLServerException {
        this.writeInt(length);
        this.writeByte(tdsType);
        this.writeByte(probBytes);
    }

    void writeTVPColumnMetaData(TVP value) throws SQLServerException {
        this.writeShort((short)value.getTVPColumnCount());
        Map<Integer, SQLServerMetaData> columnMetadata = value.getColumnMetadata();
        for (Map.Entry<Integer, SQLServerMetaData> pair : columnMetadata.entrySet()) {
            JDBCType jdbcType = JDBCType.of(pair.getValue().javaSqlType);
            boolean useServerDefault = pair.getValue().useServerDefault;
            this.writeInt(0);
            short flags = 1;
            if (useServerDefault) {
                flags = (short)(flags | 0x200);
            }
            this.writeShort(flags);
            switch (jdbcType) {
                case BIGINT: {
                    this.writeByte(TDSType.INTN.byteValue());
                    this.writeByte((byte)8);
                    break;
                }
                case BIT: {
                    this.writeByte(TDSType.BITN.byteValue());
                    this.writeByte((byte)1);
                    break;
                }
                case INTEGER: {
                    this.writeByte(TDSType.INTN.byteValue());
                    this.writeByte((byte)4);
                    break;
                }
                case SMALLINT: 
                case TINYINT: {
                    this.writeByte(TDSType.INTN.byteValue());
                    this.writeByte((byte)2);
                    break;
                }
                case DECIMAL: 
                case NUMERIC: {
                    this.writeByte(TDSType.NUMERICN.byteValue());
                    this.writeByte((byte)17);
                    this.writeByte((byte)pair.getValue().precision);
                    this.writeByte((byte)pair.getValue().scale);
                    break;
                }
                case DOUBLE: {
                    this.writeByte(TDSType.FLOATN.byteValue());
                    this.writeByte((byte)8);
                    break;
                }
                case FLOAT: 
                case REAL: {
                    this.writeByte(TDSType.FLOATN.byteValue());
                    this.writeByte((byte)4);
                    break;
                }
                case DATE: 
                case TIME: 
                case TIMESTAMP: 
                case DATETIMEOFFSET: 
                case DATETIME: 
                case SMALLDATETIME: 
                case TIMESTAMP_WITH_TIMEZONE: 
                case TIME_WITH_TIMEZONE: 
                case CHAR: 
                case VARCHAR: 
                case NCHAR: 
                case NVARCHAR: 
                case LONGVARCHAR: 
                case LONGNVARCHAR: 
                case SQLXML: {
                    boolean isShortValue;
                    this.writeByte(TDSType.NVARCHAR.byteValue());
                    boolean bl = isShortValue = 2L * (long)pair.getValue().precision <= 8000L;
                    if (!isShortValue) {
                        this.writeShort((short)-1);
                        this.con.getDatabaseCollation().writeCollation(this);
                        break;
                    }
                    this.writeShort((short)8000);
                    this.con.getDatabaseCollation().writeCollation(this);
                    break;
                }
                case BINARY: 
                case VARBINARY: 
                case LONGVARBINARY: {
                    boolean isShortValue;
                    this.writeByte(TDSType.BIGVARBINARY.byteValue());
                    boolean bl = isShortValue = pair.getValue().precision <= 8000;
                    if (!isShortValue) {
                        this.writeShort((short)-1);
                        break;
                    }
                    this.writeShort((short)8000);
                    break;
                }
                case SQL_VARIANT: {
                    this.writeByte(TDSType.SQL_VARIANT.byteValue());
                    this.writeInt(8009);
                    break;
                }
                default: {
                    assert (false) : "Unexpected JDBC type " + jdbcType.toString();
                    break;
                }
            }
            this.writeByte((byte)0);
        }
    }

    void writeTvpOrderUnique(TVP value) throws SQLServerException {
        Map<Integer, SQLServerMetaData> columnMetadata = value.getColumnMetadata();
        Iterator<Map.Entry<Integer, SQLServerMetaData>> columnsIterator = columnMetadata.entrySet().iterator();
        LinkedList<TdsOrderUnique> columnList = new LinkedList<TdsOrderUnique>();
        while (columnsIterator.hasNext()) {
            byte flags = 0;
            Map.Entry<Integer, SQLServerMetaData> pair = columnsIterator.next();
            SQLServerMetaData metaData = pair.getValue();
            if (SQLServerSortOrder.ASCENDING == metaData.sortOrder) {
                flags = 1;
            } else if (SQLServerSortOrder.DESCENDING == metaData.sortOrder) {
                flags = 2;
            }
            if (metaData.isUniqueKey) {
                flags = (byte)(flags | 4);
            }
            if (0 == flags) continue;
            columnList.add(new TdsOrderUnique(pair.getKey(), flags));
        }
        if (!columnList.isEmpty()) {
            this.writeByte((byte)16);
            this.writeShort((short)columnList.size());
            for (TdsOrderUnique column : columnList) {
                this.writeShort((short)(column.columnOrdinal + 1));
                this.writeByte(column.flags);
            }
        }
    }

    void setCryptoMetaData(CryptoMetadata cryptoMetaForBulk) {
        this.cryptoMeta = cryptoMetaForBulk;
    }

    CryptoMetadata getCryptoMetaData() {
        return this.cryptoMeta;
    }

    void writeEncryptedRPCByteArray(byte[] bValue) throws SQLServerException {
        boolean isPLP;
        boolean bValueNull = bValue == null;
        long nValueLen = bValueNull ? 0L : (long)bValue.length;
        boolean isShortValue = nValueLen <= 8000L;
        boolean bl = isPLP = !isShortValue && nValueLen <= Integer.MAX_VALUE;
        if (isShortValue) {
            this.writeShort((short)8000);
        } else if (isPLP) {
            this.writeShort((short)-1);
        } else {
            this.writeInt(Integer.MAX_VALUE);
        }
        if (bValueNull) {
            this.writeShort((short)-1);
        } else {
            if (isShortValue) {
                this.writeShort((short)nValueLen);
            } else if (isPLP) {
                this.writeLong(nValueLen);
            } else {
                this.writeInt((int)nValueLen);
            }
            if (0L != nValueLen) {
                if (isPLP) {
                    this.writeInt((int)nValueLen);
                }
                this.writeBytes(bValue);
            }
            if (isPLP) {
                this.writeInt(0);
            }
        }
    }

    void writeEncryptedRPCPLP() throws SQLServerException {
        this.writeShort((short)-1);
        this.writeLong(0L);
        this.writeInt(0);
    }

    void writeCryptoMetaData() throws SQLServerException {
        this.writeByte(this.cryptoMeta.cipherAlgorithmId);
        this.writeByte(this.cryptoMeta.encryptionType.getValue());
        this.writeInt(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get((int)0).databaseId);
        this.writeInt(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get((int)0).cekId);
        this.writeInt(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get((int)0).cekVersion);
        this.writeBytes(this.cryptoMeta.cekTableEntry.getColumnEncryptionKeyValues().get((int)0).cekMdVersion);
        this.writeByte(this.cryptoMeta.normalizationRuleVersion);
    }

    void writeRPCByteArray(String sName, byte[] bValue, boolean bOut, JDBCType jdbcType, SQLCollation collation) throws SQLServerException {
        TDSType tdsType;
        boolean usePLP;
        boolean bValueNull = bValue == null;
        int nValueLen = bValueNull ? 0 : bValue.length;
        boolean isShortValue = nValueLen <= 8000;
        boolean bl = usePLP = !isShortValue || bOut;
        if (null != this.cryptoMeta) {
            tdsType = isShortValue || usePLP ? TDSType.BIGVARBINARY : TDSType.IMAGE;
            collation = null;
        } else {
            switch (jdbcType) {
                case CHAR: 
                case VARCHAR: 
                case LONGVARCHAR: 
                case CLOB: {
                    TDSType tDSType = tdsType = isShortValue || usePLP ? TDSType.BIGVARCHAR : TDSType.TEXT;
                    if (null != collation) break;
                    collation = this.con.getDatabaseCollation();
                    break;
                }
                case NCHAR: 
                case NVARCHAR: 
                case LONGNVARCHAR: 
                case NCLOB: {
                    TDSType tDSType = tdsType = isShortValue || usePLP ? TDSType.NVARCHAR : TDSType.NTEXT;
                    if (null != collation) break;
                    collation = this.con.getDatabaseCollation();
                    break;
                }
                default: {
                    tdsType = isShortValue || usePLP ? TDSType.BIGVARBINARY : TDSType.IMAGE;
                    collation = null;
                }
            }
        }
        this.writeRPCNameValType(sName, bOut, tdsType);
        if (usePLP) {
            this.writeVMaxHeader(nValueLen, bValueNull, collation);
            if (!bValueNull) {
                if (nValueLen > 0) {
                    this.writeInt(nValueLen);
                    this.writeBytes(bValue);
                }
                this.writeInt(0);
            }
        } else {
            if (isShortValue) {
                this.writeShort((short)8000);
            } else {
                this.writeInt(Integer.MAX_VALUE);
            }
            if (null != collation) {
                collation.writeCollation(this);
            }
            if (bValueNull) {
                this.writeShort((short)-1);
            } else {
                if (isShortValue) {
                    this.writeShort((short)nValueLen);
                } else {
                    this.writeInt(nValueLen);
                }
                if (0 != nValueLen) {
                    this.writeBytes(bValue);
                }
            }
        }
    }

    void writeRPCDateTime(String sName, GregorianCalendar cal, int subSecondNanos, boolean bOut) throws SQLServerException {
        assert (subSecondNanos >= 0 && subSecondNanos < 1000000000) : "Invalid subNanoSeconds value: " + subSecondNanos;
        assert (cal != null || subSecondNanos == 0) : "Invalid subNanoSeconds value when calendar is null: " + subSecondNanos;
        this.writeRPCNameValType(sName, bOut, TDSType.DATETIMEN);
        this.writeByte((byte)8);
        if (null == cal) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)8);
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1900);
        int millisSinceMidnight = (subSecondNanos + 500000) / 1000000 + 1000 * cal.get(13) + 60000 * cal.get(12) + 3600000 * cal.get(11);
        if (millisSinceMidnight >= 86399999) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1753, 1, 1900) || daysSinceSQLBaseDate >= DDC.daysSinceBaseDate(10000, 1, 1900)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
            Object[] msgArgs = new Object[]{SSType.DATETIME};
            throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
        }
        this.writeInt(daysSinceSQLBaseDate);
        this.writeInt((3 * millisSinceMidnight + 5) / 10);
    }

    void writeRPCTime(String sName, GregorianCalendar localCalendar, int subSecondNanos, int scale, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.TIMEN);
        this.writeByte((byte)scale);
        if (null == localCalendar) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)TDS.timeValueLength(scale));
        this.writeScaledTemporal(localCalendar, subSecondNanos, scale, SSType.TIME);
    }

    void writeRPCDate(String sName, GregorianCalendar localCalendar, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DATEN);
        if (null == localCalendar) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)3);
        this.writeScaledTemporal(localCalendar, 0, 0, SSType.DATE);
    }

    void writeEncryptedRPCTime(String sName, GregorianCalendar localCalendar, int subSecondNanos, int scale, boolean bOut, SQLServerStatement statement) throws SQLServerException {
        if (this.con.getSendTimeAsDatetime()) {
            throw new SQLServerException(SQLServerException.getErrString("R_sendTimeAsDateTimeForAE"), null);
        }
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == localCalendar) {
            this.writeEncryptedRPCByteArray(null);
        } else {
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(localCalendar, subSecondNanos, scale, SSType.TIME, (short)0, statement));
        }
        this.writeByte(TDSType.TIMEN.byteValue());
        this.writeByte((byte)scale);
        this.writeCryptoMetaData();
    }

    void writeEncryptedRPCDate(String sName, GregorianCalendar localCalendar, boolean bOut, SQLServerStatement statement) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == localCalendar) {
            this.writeEncryptedRPCByteArray(null);
        } else {
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(localCalendar, 0, 0, SSType.DATE, (short)0, statement));
        }
        this.writeByte(TDSType.DATEN.byteValue());
        this.writeCryptoMetaData();
    }

    void writeEncryptedRPCDateTime(String sName, GregorianCalendar cal, int subSecondNanos, boolean bOut, JDBCType jdbcType, SQLServerStatement statement) throws SQLServerException {
        assert (subSecondNanos >= 0 && subSecondNanos < 1000000000) : "Invalid subNanoSeconds value: " + subSecondNanos;
        assert (cal != null || subSecondNanos == 0) : "Invalid subNanoSeconds value when calendar is null: " + subSecondNanos;
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == cal) {
            this.writeEncryptedRPCByteArray(null);
        } else {
            this.writeEncryptedRPCByteArray(this.getEncryptedDateTimeAsBytes(cal, subSecondNanos, jdbcType, statement));
        }
        if (JDBCType.SMALLDATETIME == jdbcType) {
            this.writeByte(TDSType.DATETIMEN.byteValue());
            this.writeByte((byte)4);
        } else {
            this.writeByte(TDSType.DATETIMEN.byteValue());
            this.writeByte((byte)8);
        }
        this.writeCryptoMetaData();
    }

    byte[] getEncryptedDateTimeAsBytes(GregorianCalendar cal, int subSecondNanos, JDBCType jdbcType, SQLServerStatement statement) throws SQLServerException {
        int daysSinceSQLBaseDate = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1900);
        int millisSinceMidnight = (subSecondNanos + 500000) / 1000000 + 1000 * cal.get(13) + 60000 * cal.get(12) + 3600000 * cal.get(11);
        if (millisSinceMidnight >= 86399999) {
            ++daysSinceSQLBaseDate;
            millisSinceMidnight = 0;
        }
        if (JDBCType.SMALLDATETIME == jdbcType) {
            int secondsSinceMidnight = millisSinceMidnight / 1000;
            int minutesSinceMidnight = secondsSinceMidnight / 60;
            minutesSinceMidnight = (double)(secondsSinceMidnight % 60) > 29.998 ? minutesSinceMidnight + 1 : minutesSinceMidnight;
            int maxMinutesSinceMidnightSmallDateTime = 1440;
            if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1900, 1, 1900) || daysSinceSQLBaseDate > DDC.daysSinceBaseDate(2079, 157, 1900) || daysSinceSQLBaseDate == DDC.daysSinceBaseDate(2079, 157, 1900) && minutesSinceMidnight >= maxMinutesSinceMidnightSmallDateTime) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                Object[] msgArgs = new Object[]{SSType.SMALLDATETIME};
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
            }
            ByteBuffer days = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            days.putShort((short)daysSinceSQLBaseDate);
            ByteBuffer seconds = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            seconds.putShort((short)minutesSinceMidnight);
            byte[] value = new byte[4];
            System.arraycopy(days.array(), 0, value, 0, 2);
            System.arraycopy(seconds.array(), 0, value, 2, 2);
            return SQLServerSecurityUtility.encryptWithKey(value, this.cryptoMeta, this.con, statement);
        }
        if (JDBCType.DATETIME == jdbcType) {
            if (daysSinceSQLBaseDate < DDC.daysSinceBaseDate(1753, 1, 1900) || daysSinceSQLBaseDate >= DDC.daysSinceBaseDate(10000, 1, 1900)) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                Object[] msgArgs = new Object[]{SSType.DATETIME};
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
            }
            ByteBuffer days = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            days.putInt(daysSinceSQLBaseDate);
            ByteBuffer seconds = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            seconds.putInt((3 * millisSinceMidnight + 5) / 10);
            byte[] value = new byte[8];
            System.arraycopy(days.array(), 0, value, 0, 4);
            System.arraycopy(seconds.array(), 0, value, 4, 4);
            return SQLServerSecurityUtility.encryptWithKey(value, this.cryptoMeta, this.con, statement);
        }
        assert (false) : "Unexpected JDBCType type " + jdbcType;
        return null;
    }

    void writeEncryptedRPCDateTime2(String sName, GregorianCalendar localCalendar, int subSecondNanos, int scale, boolean bOut, SQLServerStatement statement) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == localCalendar) {
            this.writeEncryptedRPCByteArray(null);
        } else {
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(localCalendar, subSecondNanos, scale, SSType.DATETIME2, (short)0, statement));
        }
        this.writeByte(TDSType.DATETIME2N.byteValue());
        this.writeByte((byte)scale);
        this.writeCryptoMetaData();
    }

    void writeEncryptedRPCDateTimeOffset(String sName, GregorianCalendar utcCalendar, int minutesOffset, int subSecondNanos, int scale, boolean bOut, SQLServerStatement statement) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.BIGVARBINARY);
        if (null == utcCalendar) {
            this.writeEncryptedRPCByteArray(null);
        } else {
            assert (0 == utcCalendar.get(15));
            this.writeEncryptedRPCByteArray(this.writeEncryptedScaledTemporal(utcCalendar, subSecondNanos, scale, SSType.DATETIMEOFFSET, (short)minutesOffset, statement));
        }
        this.writeByte(TDSType.DATETIMEOFFSETN.byteValue());
        this.writeByte((byte)scale);
        this.writeCryptoMetaData();
    }

    void writeRPCDateTime2(String sName, GregorianCalendar localCalendar, int subSecondNanos, int scale, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DATETIME2N);
        this.writeByte((byte)scale);
        if (null == localCalendar) {
            this.writeByte((byte)0);
            return;
        }
        this.writeByte((byte)TDS.datetime2ValueLength(scale));
        this.writeScaledTemporal(localCalendar, subSecondNanos, scale, SSType.DATETIME2);
    }

    void writeRPCDateTimeOffset(String sName, GregorianCalendar utcCalendar, int minutesOffset, int subSecondNanos, int scale, boolean bOut) throws SQLServerException {
        this.writeRPCNameValType(sName, bOut, TDSType.DATETIMEOFFSETN);
        this.writeByte((byte)scale);
        if (null == utcCalendar) {
            this.writeByte((byte)0);
            return;
        }
        assert (0 == utcCalendar.get(15));
        this.writeByte((byte)TDS.datetimeoffsetValueLength(scale));
        this.writeScaledTemporal(utcCalendar, subSecondNanos, scale, SSType.DATETIMEOFFSET);
        this.writeShort((short)minutesOffset);
    }

    private int getRoundedSubSecondNanos(int subSecondNanos) {
        return (subSecondNanos + Nanos.PER_MAX_SCALE_INTERVAL / 2) / Nanos.PER_MAX_SCALE_INTERVAL * Nanos.PER_MAX_SCALE_INTERVAL;
    }

    private void writeScaledTemporal(GregorianCalendar cal, int subSecondNanos, int scale, SSType ssType) throws SQLServerException {
        assert (this.con.isKatmaiOrLater());
        assert (SSType.DATE == ssType || SSType.TIME == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) : "Unexpected SSType: " + ssType;
        if (SSType.TIME == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) {
            long scaledNanos;
            assert (subSecondNanos >= 0);
            assert (subSecondNanos < 1000000000);
            assert (scale >= 0);
            assert (scale <= 7);
            int secondsSinceMidnight = cal.get(13) + 60 * cal.get(12) + 3600 * cal.get(11);
            long divisor = (long)Nanos.PER_MAX_SCALE_INTERVAL * (long)Math.pow(10.0, 7.0 - (double)scale);
            if (86400000000000L / divisor == (scaledNanos = (1000000000L * (long)secondsSinceMidnight + (long)this.getRoundedSubSecondNanos(subSecondNanos) + divisor / 2L) / divisor)) {
                if (SSType.TIME == ssType) {
                    --scaledNanos;
                } else {
                    assert (SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) : "Unexpected SSType: " + ssType;
                    cal.add(13, 1);
                    if (cal.get(1) <= 9999) {
                        scaledNanos = 0L;
                    } else {
                        cal.add(13, -1);
                        --scaledNanos;
                    }
                }
            }
            int encodedLength = TDS.nanosSinceMidnightLength(scale);
            byte[] encodedBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength);
            this.writeBytes(encodedBytes);
        }
        if (SSType.DATE == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) {
            int daysIntoCE;
            if (cal.getTimeInMillis() < GregorianChange.STANDARD_CHANGE_DATE.getTime() || cal.getActualMaximum(6) < 365) {
                int year = cal.get(1);
                int month = cal.get(2);
                int date = cal.get(5);
                cal.setGregorianChange(GregorianChange.PURE_CHANGE_DATE);
                cal.set(year, month, date);
            }
            if ((daysIntoCE = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1)) < 0 || daysIntoCE >= DDC.daysSinceBaseDate(10000, 1, 1)) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                Object[] msgArgs = new Object[]{ssType};
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
            }
            byte[] encodedBytes = new byte[]{(byte)(daysIntoCE >> 0 & 0xFF), (byte)(daysIntoCE >> 8 & 0xFF), (byte)(daysIntoCE >> 16 & 0xFF)};
            this.writeBytes(encodedBytes);
        }
    }

    byte[] writeEncryptedScaledTemporal(GregorianCalendar cal, int subSecondNanos, int scale, SSType ssType, short minutesOffset, SQLServerStatement statement) throws SQLServerException {
        byte[] encodedBytes;
        assert (this.con.isKatmaiOrLater());
        assert (SSType.DATE == ssType || SSType.TIME == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) : "Unexpected SSType: " + ssType;
        byte[] encodedBytesForEncryption = null;
        int secondsSinceMidnight = 0;
        long divisor = 0L;
        long scaledNanos = 0L;
        if (SSType.TIME == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) {
            assert (subSecondNanos >= 0);
            assert (subSecondNanos < 1000000000);
            assert (scale >= 0);
            assert (scale <= 7);
            secondsSinceMidnight = cal.get(13) + 60 * cal.get(12) + 3600 * cal.get(11);
            divisor = (long)Nanos.PER_MAX_SCALE_INTERVAL * (long)Math.pow(10.0, 7.0 - (double)scale);
            scaledNanos = (1000000000L * (long)secondsSinceMidnight + (long)this.getRoundedSubSecondNanos(subSecondNanos) + divisor / 2L) / divisor * divisor / 100L;
            if (SSType.TIME == ssType && 864000000000L <= scaledNanos) {
                scaledNanos = (1000000000L * (long)secondsSinceMidnight + (long)this.getRoundedSubSecondNanos(subSecondNanos)) / divisor * divisor / 100L;
            }
            if (86400000000000L / divisor == scaledNanos) {
                if (SSType.TIME == ssType) {
                    --scaledNanos;
                } else {
                    assert (SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) : "Unexpected SSType: " + ssType;
                    cal.add(13, 1);
                    if (cal.get(1) <= 9999) {
                        scaledNanos = 0L;
                    } else {
                        cal.add(13, -1);
                        --scaledNanos;
                    }
                }
            }
            int encodedLength = TDS.nanosSinceMidnightLength(7);
            encodedBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength);
            if (SSType.TIME == ssType) {
                return SQLServerSecurityUtility.encryptWithKey(encodedBytes, this.cryptoMeta, this.con, statement);
            }
            if (SSType.DATETIME2 == ssType) {
                encodedBytesForEncryption = new byte[encodedLength + 3];
                System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, 0, encodedBytes.length);
            } else if (SSType.DATETIMEOFFSET == ssType) {
                encodedBytesForEncryption = new byte[encodedLength + 5];
                System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, 0, encodedBytes.length);
            }
        }
        if (SSType.DATE == ssType || SSType.DATETIME2 == ssType || SSType.DATETIMEOFFSET == ssType) {
            byte[] cipherText;
            int daysIntoCE;
            if (cal.getTimeInMillis() < GregorianChange.STANDARD_CHANGE_DATE.getTime() || cal.getActualMaximum(6) < 365) {
                int year = cal.get(1);
                int month = cal.get(2);
                int date = cal.get(5);
                cal.setGregorianChange(GregorianChange.PURE_CHANGE_DATE);
                cal.set(year, month, date);
            }
            if ((daysIntoCE = DDC.daysSinceBaseDate(cal.get(1), cal.get(6), 1)) < 0 || daysIntoCE >= DDC.daysSinceBaseDate(10000, 1, 1)) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                Object[] msgArgs = new Object[]{ssType};
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW, DriverError.NOT_SET, null);
            }
            encodedBytes = new byte[]{(byte)(daysIntoCE >> 0 & 0xFF), (byte)(daysIntoCE >> 8 & 0xFF), (byte)(daysIntoCE >> 16 & 0xFF)};
            if (SSType.DATE == ssType) {
                cipherText = SQLServerSecurityUtility.encryptWithKey(encodedBytes, this.cryptoMeta, this.con, statement);
            } else if (SSType.DATETIME2 == ssType) {
                if (3652058 == daysIntoCE && 864000000000L == scaledNanos) {
                    scaledNanos = (1000000000L * (long)secondsSinceMidnight + (long)this.getRoundedSubSecondNanos(subSecondNanos)) / divisor * divisor / 100L;
                    int encodedLength = TDS.nanosSinceMidnightLength(7);
                    byte[] encodedNanoBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength);
                    encodedBytesForEncryption = new byte[encodedLength + 3];
                    System.arraycopy(encodedNanoBytes, 0, encodedBytesForEncryption, 0, encodedNanoBytes.length);
                }
                if (encodedBytesForEncryption == null) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NullValue"));
                    Object[] msgArgs1 = new Object[]{"encodedBytesForEncryption"};
                    throw new SQLServerException(form.format(msgArgs1), null);
                }
                System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, encodedBytesForEncryption.length - 3, 3);
                cipherText = SQLServerSecurityUtility.encryptWithKey(encodedBytesForEncryption, this.cryptoMeta, this.con, statement);
            } else {
                if (3652058 == daysIntoCE && 864000000000L == scaledNanos) {
                    scaledNanos = (1000000000L * (long)secondsSinceMidnight + (long)this.getRoundedSubSecondNanos(subSecondNanos)) / divisor * divisor / 100L;
                    int encodedLength = TDS.nanosSinceMidnightLength(7);
                    byte[] encodedNanoBytes = this.scaledNanosToEncodedBytes(scaledNanos, encodedLength);
                    encodedBytesForEncryption = new byte[encodedLength + 5];
                    System.arraycopy(encodedNanoBytes, 0, encodedBytesForEncryption, 0, encodedNanoBytes.length);
                }
                if (encodedBytesForEncryption == null) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NullValue"));
                    Object[] msgArgs1 = new Object[]{"encodedBytesForEncryption"};
                    throw new SQLServerException(form.format(msgArgs1), null);
                }
                System.arraycopy(encodedBytes, 0, encodedBytesForEncryption, encodedBytesForEncryption.length - 5, 3);
                System.arraycopy(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(minutesOffset).array(), 0, encodedBytesForEncryption, encodedBytesForEncryption.length - 2, 2);
                cipherText = SQLServerSecurityUtility.encryptWithKey(encodedBytesForEncryption, this.cryptoMeta, this.con, statement);
            }
            return cipherText;
        }
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unknownSSType"));
        Object[] msgArgs = new Object[]{ssType};
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
        return null;
    }

    private byte[] scaledNanosToEncodedBytes(long scaledNanos, int encodedLength) {
        byte[] encodedBytes = new byte[encodedLength];
        for (int i = 0; i < encodedLength; ++i) {
            encodedBytes[i] = (byte)(scaledNanos >> 8 * i & 0xFFL);
        }
        return encodedBytes;
    }

    void writeRPCInputStream(String sName, InputStream stream, long streamLength, boolean bOut, JDBCType jdbcType, SQLCollation collation) throws SQLServerException {
        boolean usePLP;
        assert (null != stream);
        assert (-1L == streamLength || streamLength >= 0L);
        boolean bl = usePLP = -1L == streamLength || streamLength > 8000L;
        if (usePLP) {
            assert (-1L == streamLength || streamLength <= Integer.MAX_VALUE);
            this.writeRPCNameValType(sName, bOut, jdbcType.isTextual() ? TDSType.BIGVARCHAR : TDSType.BIGVARBINARY);
            this.writeVMaxHeader(streamLength, false, jdbcType.isTextual() ? collation : null);
        } else {
            boolean useVarType;
            if (-1L == streamLength) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(8000);
                long maxStreamLength = 65535L * (long)this.con.getTDSPacketSize();
                try {
                    int bytesRead;
                    byte[] buff = new byte[8000];
                    for (streamLength = 0L; streamLength < maxStreamLength && -1 != (bytesRead = stream.read(buff, 0, buff.length)); streamLength += (long)bytesRead) {
                        baos.write(buff);
                    }
                }
                catch (IOException e) {
                    throw new SQLServerException(e.getMessage(), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, (Throwable)e);
                }
                if (streamLength >= maxStreamLength) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
                    Object[] msgArgs = new Object[]{streamLength};
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
                }
                assert (streamLength <= Integer.MAX_VALUE);
                stream = new ByteArrayInputStream(baos.toByteArray(), 0, (int)streamLength);
            }
            assert (0L <= streamLength && streamLength <= Integer.MAX_VALUE);
            boolean bl2 = useVarType = streamLength <= 8000L;
            this.writeRPCNameValType(sName, bOut, jdbcType.isTextual() ? (useVarType ? TDSType.BIGVARCHAR : TDSType.TEXT) : (useVarType ? TDSType.BIGVARBINARY : TDSType.IMAGE));
            if (useVarType) {
                this.writeShort((short)8000);
                if (jdbcType.isTextual()) {
                    collation.writeCollation(this);
                }
                this.writeShort((short)streamLength);
            } else {
                this.writeInt(Integer.MAX_VALUE);
                if (jdbcType.isTextual()) {
                    collation.writeCollation(this);
                }
                this.writeInt((int)streamLength);
            }
        }
        this.writeStream(stream, streamLength, usePLP);
    }

    void writeRPCXML(String sName, InputStream stream, long streamLength, boolean bOut) throws SQLServerException {
        assert (-1L == streamLength || streamLength >= 0L);
        assert (-1L == streamLength || streamLength <= Integer.MAX_VALUE);
        this.writeRPCNameValType(sName, bOut, TDSType.XML);
        this.writeByte((byte)0);
        if (null == stream) {
            this.writeLong(-1L);
        } else if (-1L == streamLength) {
            this.writeLong(-2L);
        } else {
            this.writeLong(streamLength);
        }
        if (null != stream) {
            this.writeStream(stream, streamLength, true);
        }
    }

    void writeRPCReaderUnicode(String sName, Reader re, long reLength, boolean bOut, SQLCollation collation) throws SQLServerException {
        boolean usePLP;
        assert (null != re);
        assert (-1L == reLength || reLength >= 0L);
        if (null == collation) {
            collation = this.con.getDatabaseCollation();
        }
        boolean bl = usePLP = -1L == reLength || reLength > 4000L;
        if (usePLP) {
            assert (-1L == reLength || reLength <= 0x3FFFFFFFL);
            this.writeRPCNameValType(sName, bOut, TDSType.NVARCHAR);
            this.writeVMaxHeader(-1L == reLength ? -1L : 2L * reLength, false, collation);
        } else {
            assert (0L <= reLength && reLength <= 0x3FFFFFFFL);
            boolean useVarType = reLength <= 4000L;
            this.writeRPCNameValType(sName, bOut, useVarType ? TDSType.NVARCHAR : TDSType.NTEXT);
            if (useVarType) {
                this.writeShort((short)8000);
                collation.writeCollation(this);
                this.writeShort((short)(2L * reLength));
            } else {
                this.writeInt(0x3FFFFFFF);
                collation.writeCollation(this);
                this.writeInt((int)(2L * reLength));
            }
        }
        this.writeReader(re, reLength, usePLP);
    }

    void sendEnclavePackage(String sql, ArrayList<byte[]> enclaveCEKs) throws SQLServerException {
        if (null != this.con && this.con.isAEv2()) {
            if (null != sql && !sql.isEmpty() && null != enclaveCEKs && !enclaveCEKs.isEmpty() && this.con.enclaveEstablished()) {
                byte[] b = this.con.generateEnclavePackage(sql, enclaveCEKs);
                if (null != b && 0 != b.length) {
                    this.writeShort((short)b.length);
                    this.writeBytes(b);
                } else {
                    this.writeShort((short)0);
                }
            } else {
                this.writeShort((short)0);
            }
        }
    }

    private class TdsOrderUnique {
        int columnOrdinal;
        byte flags;

        TdsOrderUnique(int ordinal, byte flags) {
            this.columnOrdinal = ordinal;
            this.flags = flags;
        }
    }
}

