/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type.descriptor.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public final class DataHelper {
    private static final int BUFFER_SIZE = 4096;
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)DataHelper.class.getName());

    private DataHelper() {
    }

    public static boolean isNClob(Class type) {
        return NClob.class.isAssignableFrom(type);
    }

    public static String extractString(Reader reader) {
        return DataHelper.extractString(reader, 4096);
    }

    public static String extractString(Reader reader, int lengthHint) {
        int bufferSize = DataHelper.getSuggestedBufferSize(lengthHint);
        StringBuilder stringBuilder = new StringBuilder(bufferSize);
        try {
            int amountRead;
            char[] buffer = new char[bufferSize];
            while ((amountRead = reader.read(buffer, 0, bufferSize)) != -1) {
                stringBuilder.append(buffer, 0, amountRead);
            }
        }
        catch (IOException ioe) {
            throw new HibernateException("IOException occurred reading text", ioe);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException e) {
                LOG.unableToCloseStream(e);
            }
        }
        return stringBuilder.toString();
    }

    private static String extractString(Reader characterStream, long start, int length) {
        if (length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(length);
        try {
            int amountRead;
            long skipped = characterStream.skip(start);
            if (skipped != start) {
                throw new HibernateException("Unable to skip needed bytes");
            }
            int bufferSize = DataHelper.getSuggestedBufferSize(length);
            char[] buffer = new char[bufferSize];
            int charsRead = 0;
            while ((amountRead = characterStream.read(buffer, 0, bufferSize)) != -1) {
                stringBuilder.append(buffer, 0, amountRead);
                if (amountRead >= bufferSize && (charsRead += amountRead) < length) continue;
                break;
            }
        }
        catch (IOException ioe) {
            throw new HibernateException("IOException occurred reading a binary value", ioe);
        }
        return stringBuilder.toString();
    }

    public static Object subStream(Reader characterStream, long start, int length) {
        return new StringReader(DataHelper.extractString(characterStream, start, length));
    }

    public static byte[] extractBytes(InputStream inputStream) {
        if (BinaryStream.class.isInstance(inputStream)) {
            return ((BinaryStream)((Object)inputStream)).getBytes();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
        try {
            int amountRead;
            byte[] buffer = new byte[4096];
            while ((amountRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, amountRead);
            }
        }
        catch (IOException ioe) {
            throw new HibernateException("IOException occurred reading a binary value", ioe);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                LOG.unableToCloseInputStream(e);
            }
            try {
                outputStream.close();
            }
            catch (IOException e) {
                LOG.unableToCloseOutputStream(e);
            }
        }
        return outputStream.toByteArray();
    }

    public static byte[] extractBytes(InputStream inputStream, long start, int length) {
        if (BinaryStream.class.isInstance(inputStream) && Integer.MAX_VALUE > start) {
            byte[] data = ((BinaryStream)((Object)inputStream)).getBytes();
            int size = Math.min(length, data.length);
            byte[] result = new byte[size];
            System.arraycopy(data, (int)start, result, 0, size);
            return result;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(length);
        try {
            int amountRead;
            long skipped = inputStream.skip(start);
            if (skipped != start) {
                throw new HibernateException("Unable to skip needed bytes");
            }
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((amountRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, amountRead);
                if (amountRead >= buffer.length && (bytesRead += amountRead) < length) continue;
                break;
            }
        }
        catch (IOException ioe) {
            throw new HibernateException("IOException occurred reading a binary value", ioe);
        }
        return outputStream.toByteArray();
    }

    public static InputStream subStream(InputStream inputStream, long start, int length) {
        return new BinaryStreamImpl(DataHelper.extractBytes(inputStream, start, length));
    }

    public static String extractString(Clob value) {
        try {
            Reader characterStream = value.getCharacterStream();
            long length = DataHelper.determineLengthForBufferSizing(value);
            return length > Integer.MAX_VALUE ? DataHelper.extractString(characterStream, Integer.MAX_VALUE) : DataHelper.extractString(characterStream, (int)length);
        }
        catch (SQLException e) {
            throw new HibernateException("Unable to access lob stream", e);
        }
    }

    private static long determineLengthForBufferSizing(Clob value) throws SQLException {
        try {
            return value.length();
        }
        catch (SQLFeatureNotSupportedException e) {
            return 4096L;
        }
    }

    private static int getSuggestedBufferSize(int lengthHint) {
        return Math.max(1, Math.min(lengthHint, 4096));
    }
}

