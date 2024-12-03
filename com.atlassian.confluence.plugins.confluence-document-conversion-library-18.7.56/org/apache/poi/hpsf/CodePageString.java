/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hpsf.TypedPropertyValue;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

@Internal
public class CodePageString {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000;
    private static int MAX_RECORD_LENGTH = 100000;
    private static final Logger LOG = LogManager.getLogger(CodePageString.class);
    private byte[] _value;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public void read(LittleEndianByteArrayInputStream lei) {
        int offset = lei.getReadIndex();
        int size = lei.readInt();
        this._value = IOUtils.safelyAllocate(size, MAX_RECORD_LENGTH);
        if (size == 0) {
            return;
        }
        lei.readFully(this._value);
        if (this._value[size - 1] != 0) {
            LOG.atWarn().log("CodePageString started at offset #{} is not NULL-terminated", (Object)Unbox.box(offset));
        }
        TypedPropertyValue.skipPadding(lei);
    }

    public String getJavaValue(int codepage) throws UnsupportedEncodingException {
        int cp = codepage == -1 ? 1252 : codepage;
        String result = CodePageUtil.getStringFromCodePage(this._value, cp);
        int terminator = result.indexOf(0);
        if (terminator == -1) {
            LOG.atWarn().log("String terminator (\\0) for CodePageString property value not found. Continue without trimming and hope for the best.");
            return result;
        }
        if (terminator != result.length() - 1) {
            LOG.atDebug().log("String terminator (\\0) for CodePageString property value occurred before the end of string. Trimming and hope for the best.");
        }
        return result.substring(0, terminator);
    }

    public int getSize() {
        return 4 + this._value.length;
    }

    public void setJavaValue(String string, int codepage) throws UnsupportedEncodingException {
        int cp = codepage == -1 ? 1252 : codepage;
        this._value = CodePageUtil.getBytesInCodePage(string + "\u0000", cp);
    }

    public int write(OutputStream out) throws IOException {
        LittleEndian.putUInt(this._value.length, out);
        out.write(this._value);
        return 4 + this._value.length;
    }
}

