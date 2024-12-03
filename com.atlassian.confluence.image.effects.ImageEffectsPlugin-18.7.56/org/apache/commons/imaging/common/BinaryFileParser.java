/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BinaryFileParser {
    private static final Logger LOGGER = Logger.getLogger(BinaryFileParser.class.getName());
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    public BinaryFileParser(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public BinaryFileParser() {
    }

    protected void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }

    protected final void debugNumber(String msg, int data, int bytes) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw);){
            this.debugNumber(pw, msg, data, bytes);
            pw.flush();
            sw.flush();
            LOGGER.fine(sw.toString());
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    protected final void debugNumber(PrintWriter pw, String msg, int data, int bytes) {
        pw.print(msg + ": " + data + " (");
        int byteData = data;
        for (int i = 0; i < bytes; ++i) {
            if (i > 0) {
                pw.print(",");
            }
            int singleByte = 0xFF & byteData;
            pw.print((char)singleByte + " [" + singleByte + "]");
            byteData >>= 8;
        }
        pw.println(") [0x" + Integer.toHexString(data) + ", " + Integer.toBinaryString(data) + "]");
        pw.flush();
    }
}

