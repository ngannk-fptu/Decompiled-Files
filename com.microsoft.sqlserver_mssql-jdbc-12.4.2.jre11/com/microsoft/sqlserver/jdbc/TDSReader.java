/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DDC;
import com.microsoft.sqlserver.jdbc.Encoding;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLIdentifier;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TDS;
import com.microsoft.sqlserver.jdbc.TDSChannel;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSPacket;
import com.microsoft.sqlserver.jdbc.TDSReaderMark;
import com.microsoft.sqlserver.jdbc.TDSTimeoutTask;
import com.microsoft.sqlserver.jdbc.TypeInfo;
import com.microsoft.sqlserver.jdbc.Util;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TDSReader
implements Serializable {
    private static final long serialVersionUID = -392905303734809731L;
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Reader");
    private final String traceID;
    private transient ScheduledFuture<?> timeout;
    private final TDSChannel tdsChannel;
    private final SQLServerConnection con;
    private final TDSCommand command;
    private transient TDSPacket currentPacket;
    private transient TDSPacket lastPacket;
    private int payloadOffset;
    private int packetNum;
    private boolean isStreaming;
    private boolean useColumnEncryption;
    private boolean serverSupportsColumnEncryption;
    private boolean serverSupportsDataClassification;
    private byte serverSupportedDataClassificationVersion;
    private final transient Lock lock;
    private final byte[] valueBytes;
    protected transient SensitivityClassification sensitivityClassification;
    private static final AtomicInteger lastReaderID = new AtomicInteger(0);
    private static final int[] SCALED_MULTIPLIERS = new int[]{10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
    static final String GUID_TEMPLATE = "NNNNNNNN-NNNN-NNNN-NNNN-NNNNNNNNNNNN";

    public final String toString() {
        return this.traceID;
    }

    final TDSCommand getCommand() {
        assert (null != this.command);
        return this.command;
    }

    final SQLServerConnection getConnection() {
        return this.con;
    }

    private static int nextReaderID() {
        return lastReaderID.incrementAndGet();
    }

    TDSReader(TDSChannel tdsChannel, SQLServerConnection con, TDSCommand command) {
        this.lastPacket = this.currentPacket = new TDSPacket(0);
        this.payloadOffset = 0;
        this.packetNum = 0;
        this.isStreaming = true;
        this.useColumnEncryption = false;
        this.serverSupportsColumnEncryption = false;
        this.serverSupportsDataClassification = false;
        this.serverSupportedDataClassificationVersion = 0;
        this.lock = new ReentrantLock();
        this.valueBytes = new byte[256];
        this.tdsChannel = tdsChannel;
        this.con = con;
        this.command = command;
        this.traceID = logger.isLoggable(Level.FINE) ? "TDSReader@" + TDSReader.nextReaderID() + " (" + con.toString() + ")" : con.toString();
        if (con.isColumnEncryptionSettingEnabled()) {
            this.useColumnEncryption = true;
        }
        this.serverSupportsColumnEncryption = con.getServerSupportsColumnEncryption();
        this.serverSupportsDataClassification = con.getServerSupportsDataClassification();
        this.serverSupportedDataClassificationVersion = con.getServerSupportedDataClassificationVersion();
    }

    final boolean isColumnEncryptionSettingEnabled() {
        return this.useColumnEncryption;
    }

    final boolean getServerSupportsColumnEncryption() {
        return this.serverSupportsColumnEncryption;
    }

    final boolean getServerSupportsDataClassification() {
        return this.serverSupportsDataClassification;
    }

    final byte getServerSupportedDataClassificationVersion() {
        return this.serverSupportedDataClassificationVersion;
    }

    final void throwInvalidTDS() throws SQLServerException {
        if (logger.isLoggable(Level.SEVERE)) {
            logger.severe(this.toString() + " got unexpected value in TDS response at offset:" + this.payloadOffset);
        }
        this.con.throwInvalidTDS();
    }

    final void throwInvalidTDSToken(String tokenName) throws SQLServerException {
        if (logger.isLoggable(Level.SEVERE)) {
            logger.severe(this.toString() + " got unexpected value in TDS response at offset:" + this.payloadOffset);
        }
        this.con.throwInvalidTDSToken(tokenName);
    }

    private boolean ensurePayload() throws SQLServerException {
        if (this.payloadOffset == this.currentPacket.payloadLength && !this.nextPacket()) {
            return false;
        }
        assert (this.payloadOffset < this.currentPacket.payloadLength);
        return true;
    }

    private boolean nextPacket() throws SQLServerException {
        assert (null != this.currentPacket);
        TDSPacket consumedPacket = this.currentPacket;
        assert (this.payloadOffset == consumedPacket.payloadLength);
        if (null == consumedPacket.next) {
            if (null != this.command && this.command.getTDSWriter().checkIfTdsMessageTypeIsBatchOrRPC()) {
                this.command.getCounter().resetCounter();
            }
            this.readPacket();
            if (null == consumedPacket.next) {
                return false;
            }
        }
        TDSPacket nextPacket = consumedPacket.next;
        if (this.isStreaming) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Moving to next packet -- unlinking consumed packet");
            }
            consumedPacket.next = null;
        }
        this.currentPacket = nextPacket;
        this.payloadOffset = 0;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final boolean readPacket() throws SQLServerException {
        this.lock.lock();
        try {
            int bytesRead;
            int packetLength;
            int bytesRead2;
            if (null != this.command && !this.command.readingResponse()) {
                boolean bl = false;
                return bl;
            }
            assert (this.tdsChannel.numMsgsRcvd < this.tdsChannel.numMsgsSent) : "numMsgsRcvd:" + this.tdsChannel.numMsgsRcvd + " should be less than numMsgsSent:" + this.tdsChannel.numMsgsSent;
            TDSPacket newPacket = new TDSPacket(this.con.getTDSPacketSize());
            if (null != this.command && this.command.getCancelQueryTimeoutSeconds() > 0 && this.command.getQueryTimeoutSeconds() > 0) {
                int seconds = this.command.getCancelQueryTimeoutSeconds() + this.command.getQueryTimeoutSeconds();
                this.timeout = this.con.getSharedTimer().schedule(new TDSTimeoutTask(this.command, this.con), seconds);
            }
            for (int headerBytesRead = 0; headerBytesRead < 8; headerBytesRead += bytesRead2) {
                bytesRead2 = this.tdsChannel.read(newPacket.header, headerBytesRead, 8 - headerBytesRead);
                if (bytesRead2 >= 0) continue;
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer(this.toString() + " Premature EOS in response. packetNum:" + this.packetNum + " headerBytesRead:" + headerBytesRead);
                }
                this.con.terminate(3, 0 == this.packetNum && 0 == headerBytesRead ? SQLServerException.getErrString("R_noServerResponse") : SQLServerException.getErrString("R_truncatedServerResponse"));
            }
            if (this.timeout != null) {
                this.timeout.cancel(false);
                this.timeout = null;
            }
            if ((packetLength = Util.readUnsignedShortBigEndian(newPacket.header, 2)) < 8 || packetLength > this.con.getTDSPacketSize()) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning(this.toString() + " TDS header contained invalid packet length:" + packetLength + "; packet size:" + this.con.getTDSPacketSize());
                }
                this.throwInvalidTDS();
            }
            newPacket.payloadLength = packetLength - 8;
            this.tdsChannel.setSPID(Util.readUnsignedShortBigEndian(newPacket.header, 4));
            byte[] logBuffer = null;
            if (this.tdsChannel.isLoggingPackets()) {
                logBuffer = new byte[packetLength];
                System.arraycopy(newPacket.header, 0, logBuffer, 0, 8);
            }
            if (this.tdsChannel.getWriter().checkIfTdsMessageTypeIsBatchOrRPC() && null != this.command) {
                this.command.getCounter().increaseCounter(packetLength);
            }
            for (int payloadBytesRead = 0; payloadBytesRead < newPacket.payloadLength; payloadBytesRead += bytesRead) {
                bytesRead = this.tdsChannel.read(newPacket.payload, payloadBytesRead, newPacket.payloadLength - payloadBytesRead);
                if (bytesRead >= 0) continue;
                this.con.terminate(3, SQLServerException.getErrString("R_truncatedServerResponse"));
            }
            ++this.packetNum;
            this.lastPacket.next = newPacket;
            this.lastPacket = newPacket;
            if (this.tdsChannel.isLoggingPackets() && logBuffer != null) {
                System.arraycopy(newPacket.payload, 0, logBuffer, 8, newPacket.payloadLength);
                this.tdsChannel.logPacket(logBuffer, 0, packetLength, this.toString() + " received Packet:" + this.packetNum + " (" + newPacket.payloadLength + " bytes)");
            }
            if (newPacket.isEOM()) {
                ++this.tdsChannel.numMsgsRcvd;
                if (null != this.command) {
                    this.command.onResponseEOM();
                }
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    final TDSReaderMark mark() {
        TDSReaderMark mark = new TDSReaderMark(this.currentPacket, this.payloadOffset);
        this.isStreaming = false;
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.toString() + ": Buffering from: " + mark.toString());
        }
        return mark;
    }

    final void reset(TDSReaderMark mark) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.toString() + ": Resetting to: " + mark.toString());
        }
        this.currentPacket = mark.packet;
        this.payloadOffset = mark.payloadOffset;
    }

    final void stream() {
        this.isStreaming = true;
    }

    final int available() {
        int available = this.currentPacket.payloadLength - this.payloadOffset;
        TDSPacket packet = this.currentPacket.next;
        while (null != packet) {
            available += packet.payloadLength;
            packet = packet.next;
        }
        return available;
    }

    final int availableCurrentPacket() {
        return this.currentPacket.payloadLength - this.payloadOffset;
    }

    final int peekTokenType() throws SQLServerException {
        if (!this.ensurePayload()) {
            return -1;
        }
        return this.currentPacket.payload[this.payloadOffset] & 0xFF;
    }

    final short peekStatusFlag() {
        if (this.payloadOffset + 3 <= this.currentPacket.payloadLength) {
            return Util.readShort(this.currentPacket.payload, this.payloadOffset + 1);
        }
        return 0;
    }

    final int readUnsignedByte() throws SQLServerException {
        if (!this.ensurePayload()) {
            this.throwInvalidTDS();
        }
        return this.currentPacket.payload[this.payloadOffset++] & 0xFF;
    }

    final short readShort() throws SQLServerException {
        if (this.payloadOffset + 2 <= this.currentPacket.payloadLength) {
            short value = Util.readShort(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 2;
            return value;
        }
        return Util.readShort(this.readWrappedBytes(2), 0);
    }

    final int readUnsignedShort() throws SQLServerException {
        if (this.payloadOffset + 2 <= this.currentPacket.payloadLength) {
            int value = Util.readUnsignedShort(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 2;
            return value;
        }
        return Util.readUnsignedShort(this.readWrappedBytes(2), 0);
    }

    final String readUnicodeString(int length) throws SQLServerException {
        int byteLength = 2 * length;
        byte[] bytes = new byte[byteLength];
        this.readBytes(bytes, 0, byteLength);
        return Util.readUnicodeString(bytes, 0, byteLength, this.con);
    }

    final char readChar() throws SQLServerException {
        return (char)this.readShort();
    }

    final int readInt() throws SQLServerException {
        if (this.payloadOffset + 4 <= this.currentPacket.payloadLength) {
            int value = Util.readInt(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 4;
            return value;
        }
        return Util.readInt(this.readWrappedBytes(4), 0);
    }

    final int readIntBigEndian() throws SQLServerException {
        if (this.payloadOffset + 4 <= this.currentPacket.payloadLength) {
            int value = Util.readIntBigEndian(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 4;
            return value;
        }
        return Util.readIntBigEndian(this.readWrappedBytes(4), 0);
    }

    final long readUnsignedInt() throws SQLServerException {
        return (long)this.readInt() & 0xFFFFFFFFL;
    }

    final long readLong() throws SQLServerException {
        if (this.payloadOffset + 8 <= this.currentPacket.payloadLength) {
            long value = Util.readLong(this.currentPacket.payload, this.payloadOffset);
            this.payloadOffset += 8;
            return value;
        }
        return Util.readLong(this.readWrappedBytes(8), 0);
    }

    final void readBytes(byte[] value, int valueOffset, int valueLength) throws SQLServerException {
        int bytesRead = 0;
        while (bytesRead < valueLength) {
            int bytesToCopy;
            if (!this.ensurePayload()) {
                this.throwInvalidTDS();
            }
            if ((bytesToCopy = valueLength - bytesRead) > this.currentPacket.payloadLength - this.payloadOffset) {
                bytesToCopy = this.currentPacket.payloadLength - this.payloadOffset;
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Reading " + bytesToCopy + " bytes from offset " + this.payloadOffset);
            }
            System.arraycopy(this.currentPacket.payload, this.payloadOffset, value, valueOffset + bytesRead, bytesToCopy);
            bytesRead += bytesToCopy;
            this.payloadOffset += bytesToCopy;
        }
    }

    final void readSkipBytes(int valueLength) throws SQLServerException {
        int bytesSkipped = 0;
        while (bytesSkipped < valueLength) {
            int bytesToSkip;
            if (!this.ensurePayload()) {
                this.throwInvalidTDS();
            }
            if ((bytesToSkip = valueLength - bytesSkipped) > this.currentPacket.payloadLength - this.payloadOffset) {
                bytesToSkip = this.currentPacket.payloadLength - this.payloadOffset;
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest(this.toString() + " Skipping " + bytesToSkip + " bytes from offset " + this.payloadOffset);
            }
            bytesSkipped += bytesToSkip;
            this.payloadOffset += bytesToSkip;
        }
    }

    final byte[] readWrappedBytes(int valueLength) throws SQLServerException {
        assert (valueLength <= this.valueBytes.length);
        this.readBytes(this.valueBytes, 0, valueLength);
        return this.valueBytes;
    }

    final Object readDecimal(int valueLength, TypeInfo typeInfo, JDBCType jdbcType, StreamType streamType) throws SQLServerException {
        if (valueLength > this.valueBytes.length) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(this.toString() + " Invalid value length:" + valueLength);
            }
            this.throwInvalidTDS();
        }
        this.readBytes(this.valueBytes, 0, valueLength);
        return DDC.convertBigDecimalToObject(Util.readBigDecimal(this.valueBytes, valueLength, typeInfo.getScale()), jdbcType, streamType);
    }

    final Object readMoney(int valueLength, JDBCType jdbcType, StreamType streamType) throws SQLServerException {
        BigInteger bi;
        switch (valueLength) {
            case 8: {
                int intBitsHi = this.readInt();
                int intBitsLo = this.readInt();
                if (JDBCType.BINARY == jdbcType) {
                    byte[] value = new byte[8];
                    Util.writeIntBigEndian(intBitsHi, value, 0);
                    Util.writeIntBigEndian(intBitsLo, value, 4);
                    return value;
                }
                bi = BigInteger.valueOf((long)intBitsHi << 32 | (long)intBitsLo & 0xFFFFFFFFL);
                break;
            }
            case 4: {
                if (JDBCType.BINARY == jdbcType) {
                    byte[] value = new byte[4];
                    Util.writeIntBigEndian(this.readInt(), value, 0);
                    return value;
                }
                bi = BigInteger.valueOf(this.readInt());
                break;
            }
            default: {
                this.throwInvalidTDS();
                return null;
            }
        }
        return DDC.convertBigDecimalToObject(new BigDecimal(bi, 4), jdbcType, streamType);
    }

    final Object readReal(int valueLength, JDBCType jdbcType, StreamType streamType) throws SQLServerException {
        if (4 != valueLength) {
            this.throwInvalidTDS();
        }
        return DDC.convertFloatToObject(Float.intBitsToFloat(this.readInt()), jdbcType, streamType);
    }

    final Object readFloat(int valueLength, JDBCType jdbcType, StreamType streamType) throws SQLServerException {
        if (8 != valueLength) {
            this.throwInvalidTDS();
        }
        return DDC.convertDoubleToObject(Double.longBitsToDouble(this.readLong()), jdbcType, streamType);
    }

    final Object readDateTime(int valueLength, Calendar appTimeZoneCalendar, JDBCType jdbcType, StreamType streamType) throws SQLServerException {
        int msecSinceMidnight;
        int daysSinceSQLBaseDate;
        switch (valueLength) {
            case 8: {
                daysSinceSQLBaseDate = this.readInt();
                int ticksSinceMidnight = this.readInt();
                if (JDBCType.BINARY == jdbcType) {
                    byte[] value = new byte[8];
                    Util.writeIntBigEndian(daysSinceSQLBaseDate, value, 0);
                    Util.writeIntBigEndian(ticksSinceMidnight, value, 4);
                    return value;
                }
                msecSinceMidnight = (ticksSinceMidnight * 10 + 1) / 3;
                break;
            }
            case 4: {
                daysSinceSQLBaseDate = this.readUnsignedShort();
                int ticksSinceMidnight = this.readUnsignedShort();
                if (JDBCType.BINARY == jdbcType) {
                    byte[] value = new byte[4];
                    Util.writeShortBigEndian((short)daysSinceSQLBaseDate, value, 0);
                    Util.writeShortBigEndian((short)ticksSinceMidnight, value, 2);
                    return value;
                }
                msecSinceMidnight = ticksSinceMidnight * 60 * 1000;
                break;
            }
            default: {
                this.throwInvalidTDS();
                return null;
            }
        }
        return DDC.convertTemporalToObject(jdbcType, SSType.DATETIME, appTimeZoneCalendar, daysSinceSQLBaseDate, msecSinceMidnight, 0);
    }

    final Object readDate(int valueLength, Calendar appTimeZoneCalendar, JDBCType jdbcType) throws SQLServerException {
        if (3 != valueLength) {
            this.throwInvalidTDS();
        }
        int localDaysIntoCE = this.readDaysIntoCE();
        return DDC.convertTemporalToObject(jdbcType, SSType.DATE, appTimeZoneCalendar, localDaysIntoCE, 0L, 0);
    }

    final Object readTime(int valueLength, TypeInfo typeInfo, Calendar appTimeZoneCalendar, JDBCType jdbcType) throws SQLServerException {
        if (TDS.timeValueLength(typeInfo.getScale()) != valueLength) {
            this.throwInvalidTDS();
        }
        long localNanosSinceMidnight = this.readNanosSinceMidnight(typeInfo.getScale());
        return DDC.convertTemporalToObject(jdbcType, SSType.TIME, appTimeZoneCalendar, 0, localNanosSinceMidnight, typeInfo.getScale());
    }

    final Object readDateTime2(int valueLength, TypeInfo typeInfo, Calendar appTimeZoneCalendar, JDBCType jdbcType) throws SQLServerException {
        if (TDS.datetime2ValueLength(typeInfo.getScale()) != valueLength) {
            this.throwInvalidTDS();
        }
        long localNanosSinceMidnight = this.readNanosSinceMidnight(typeInfo.getScale());
        int localDaysIntoCE = this.readDaysIntoCE();
        return DDC.convertTemporalToObject(jdbcType, SSType.DATETIME2, appTimeZoneCalendar, localDaysIntoCE, localNanosSinceMidnight, typeInfo.getScale());
    }

    final Object readDateTimeOffset(int valueLength, TypeInfo typeInfo, JDBCType jdbcType) throws SQLServerException {
        if (TDS.datetimeoffsetValueLength(typeInfo.getScale()) != valueLength) {
            this.throwInvalidTDS();
        }
        long utcNanosSinceMidnight = this.readNanosSinceMidnight(typeInfo.getScale());
        int utcDaysIntoCE = this.readDaysIntoCE();
        short localMinutesOffset = this.readShort();
        return DDC.convertTemporalToObject(jdbcType, SSType.DATETIMEOFFSET, new GregorianCalendar(new SimpleTimeZone(localMinutesOffset * 60 * 1000, ""), Locale.US), utcDaysIntoCE, utcNanosSinceMidnight, typeInfo.getScale());
    }

    private int readDaysIntoCE() throws SQLServerException {
        byte[] value = new byte[3];
        this.readBytes(value, 0, value.length);
        int daysIntoCE = 0;
        for (int i = 0; i < value.length; ++i) {
            daysIntoCE |= (value[i] & 0xFF) << 8 * i;
        }
        if (daysIntoCE < 0) {
            this.throwInvalidTDS();
        }
        return daysIntoCE;
    }

    private long readNanosSinceMidnight(int scale) throws SQLServerException {
        assert (0 <= scale && scale <= 7);
        byte[] value = new byte[TDS.nanosSinceMidnightLength(scale)];
        this.readBytes(value, 0, value.length);
        long hundredNanosSinceMidnight = 0L;
        for (int i = 0; i < value.length; ++i) {
            hundredNanosSinceMidnight |= ((long)value[i] & 0xFFL) << 8 * i;
        }
        if (0L > (hundredNanosSinceMidnight *= (long)SCALED_MULTIPLIERS[scale]) || hundredNanosSinceMidnight >= 864000000000L) {
            this.throwInvalidTDS();
        }
        return 100L * hundredNanosSinceMidnight;
    }

    final Object readGUID(int valueLength, JDBCType jdbcType, StreamType streamType) throws SQLServerException {
        if (16 != valueLength) {
            this.throwInvalidTDS();
        }
        byte[] guid = new byte[16];
        this.readBytes(guid, 0, 16);
        switch (jdbcType) {
            case CHAR: 
            case VARCHAR: 
            case LONGVARCHAR: 
            case GUID: {
                int i;
                StringBuilder sb = new StringBuilder(GUID_TEMPLATE.length());
                for (i = 0; i < 4; ++i) {
                    sb.append(Util.HEXCHARS[(guid[3 - i] & 0xF0) >> 4]);
                    sb.append(Util.HEXCHARS[guid[3 - i] & 0xF]);
                }
                sb.append('-');
                for (i = 0; i < 2; ++i) {
                    sb.append(Util.HEXCHARS[(guid[5 - i] & 0xF0) >> 4]);
                    sb.append(Util.HEXCHARS[guid[5 - i] & 0xF]);
                }
                sb.append('-');
                for (i = 0; i < 2; ++i) {
                    sb.append(Util.HEXCHARS[(guid[7 - i] & 0xF0) >> 4]);
                    sb.append(Util.HEXCHARS[guid[7 - i] & 0xF]);
                }
                sb.append('-');
                for (i = 0; i < 2; ++i) {
                    sb.append(Util.HEXCHARS[(guid[8 + i] & 0xF0) >> 4]);
                    sb.append(Util.HEXCHARS[guid[8 + i] & 0xF]);
                }
                sb.append('-');
                for (i = 0; i < 6; ++i) {
                    sb.append(Util.HEXCHARS[(guid[10 + i] & 0xF0) >> 4]);
                    sb.append(Util.HEXCHARS[guid[10 + i] & 0xF]);
                }
                try {
                    return DDC.convertStringToObject(sb.toString(), Encoding.UNICODE.charset(), jdbcType, streamType);
                }
                catch (UnsupportedEncodingException e) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
                    throw new SQLServerException(form.format(new Object[]{"UNIQUEIDENTIFIER", jdbcType}), null, 0, (Throwable)e);
                }
            }
        }
        if (StreamType.BINARY == streamType || StreamType.ASCII == streamType) {
            return new ByteArrayInputStream(guid);
        }
        return guid;
    }

    final SQLIdentifier readSQLIdentifier() throws SQLServerException {
        int numParts = this.readUnsignedByte();
        if (1 > numParts || numParts > 4) {
            this.throwInvalidTDS();
        }
        String[] nameParts = new String[numParts];
        for (int i = 0; i < numParts; ++i) {
            nameParts[i] = this.readUnicodeString(this.readUnsignedShort());
        }
        SQLIdentifier identifier = new SQLIdentifier();
        identifier.setObjectName(nameParts[numParts - 1]);
        if (numParts >= 2) {
            identifier.setSchemaName(nameParts[numParts - 2]);
        }
        if (numParts >= 3) {
            identifier.setDatabaseName(nameParts[numParts - 3]);
        }
        if (4 == numParts) {
            identifier.setServerName(nameParts[numParts - 4]);
        }
        return identifier;
    }

    final SQLCollation readCollation() throws SQLServerException {
        SQLCollation collation = null;
        try {
            collation = new SQLCollation(this);
        }
        catch (UnsupportedEncodingException e) {
            this.con.terminate(4, e.getMessage(), e);
        }
        return collation;
    }

    final void skip(int bytesToSkip) throws SQLServerException {
        assert (bytesToSkip >= 0);
        while (bytesToSkip > 0) {
            int bytesSkipped;
            if (!this.ensurePayload()) {
                this.throwInvalidTDS();
            }
            if ((bytesSkipped = bytesToSkip) > this.currentPacket.payloadLength - this.payloadOffset) {
                bytesSkipped = this.currentPacket.payloadLength - this.payloadOffset;
            }
            bytesToSkip -= bytesSkipped;
            this.payloadOffset += bytesSkipped;
        }
    }

    final void tryProcessFeatureExtAck(boolean featureExtAckReceived) throws SQLServerException {
        if (null != this.con.getRoutingInfo()) {
            return;
        }
        if (this.isColumnEncryptionSettingEnabled() && !featureExtAckReceived) {
            throw new SQLServerException((Object)this, SQLServerException.getErrString("R_AE_NotSupportedByServer"), null, 0, false);
        }
    }

    final void trySetSensitivityClassification(SensitivityClassification sensitivityClassification) {
        this.sensitivityClassification = sensitivityClassification;
    }
}

