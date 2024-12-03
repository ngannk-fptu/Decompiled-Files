/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.InputStreamGetterArgs;
import com.microsoft.sqlserver.jdbc.PLPInputStream;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.ServerDTVImpl;
import com.microsoft.sqlserver.jdbc.TDSReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

final class PLPXMLInputStream
extends PLPInputStream {
    private static final byte[] xmlBOM = new byte[]{-1, -2};
    private final ByteArrayInputStream bomStream = new ByteArrayInputStream(xmlBOM);

    static final PLPXMLInputStream makeXMLStream(TDSReader tdsReader, InputStreamGetterArgs getterArgs, ServerDTVImpl dtv) throws SQLServerException {
        long payloadLength = tdsReader.readLong();
        if (-1L == payloadLength) {
            return null;
        }
        PLPXMLInputStream is = new PLPXMLInputStream(tdsReader, payloadLength, getterArgs, dtv);
        is.setLoggingInfo(getterArgs.logContext);
        return is;
    }

    PLPXMLInputStream(TDSReader tdsReader, long statedPayloadLength, InputStreamGetterArgs getterArgs, ServerDTVImpl dtv) {
        super(tdsReader, statedPayloadLength, getterArgs.isAdaptive, getterArgs.isStreaming, dtv);
    }

    @Override
    int readBytes(byte[] b, int offset, int maxBytes) throws IOException {
        int bytesRead;
        assert (offset >= 0);
        assert (maxBytes >= 0);
        if (0 == maxBytes) {
            return 0;
        }
        int xmlBytesRead = 0;
        if (null == b) {
            int bomBytesSkipped;
            for (bytesRead = 0; bytesRead < maxBytes && 0 != (bomBytesSkipped = (int)this.bomStream.skip((long)maxBytes - (long)bytesRead)); bytesRead += bomBytesSkipped) {
            }
        } else {
            int bomBytesRead;
            while (bytesRead < maxBytes && -1 != (bomBytesRead = this.bomStream.read(b, offset + bytesRead, maxBytes - bytesRead))) {
                bytesRead += bomBytesRead;
            }
        }
        while (bytesRead < maxBytes && -1 != (xmlBytesRead = super.readBytes(b, offset + bytesRead, maxBytes - bytesRead))) {
            bytesRead += xmlBytesRead;
        }
        if (bytesRead > 0) {
            return bytesRead;
        }
        assert (-1 == xmlBytesRead);
        return -1;
    }

    @Override
    public void mark(int readLimit) {
        this.bomStream.mark(xmlBOM.length);
        super.mark(readLimit);
    }

    @Override
    public void reset() throws IOException {
        this.bomStream.reset();
        super.reset();
    }

    @Override
    byte[] getBytes() throws SQLServerException {
        byte[] bom = new byte[2];
        byte[] bytesToReturn = null;
        try {
            int bytesread = this.bomStream.read(bom);
            byte[] valueWithoutBOM = super.getBytes();
            if (bytesread > 0) {
                assert (2 == bytesread);
                byte[] valueWithBOM = new byte[valueWithoutBOM.length + bytesread];
                System.arraycopy(bom, 0, valueWithBOM, 0, bytesread);
                System.arraycopy(valueWithoutBOM, 0, valueWithBOM, bytesread, valueWithoutBOM.length);
                bytesToReturn = valueWithBOM;
            } else {
                bytesToReturn = valueWithoutBOM;
            }
        }
        catch (IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        return bytesToReturn;
    }
}

