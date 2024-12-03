/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PsdHeaderInfo {
    private static final Logger LOGGER = Logger.getLogger(PsdHeaderInfo.class.getName());
    public final int version;
    private final byte[] reserved;
    public final int channels;
    public final int rows;
    public final int columns;
    public final int depth;
    public final int mode;

    public PsdHeaderInfo(int version, byte[] reserved, int channels, int rows, int columns, int depth, int mode) {
        this.version = version;
        this.reserved = (byte[])reserved.clone();
        this.channels = channels;
        this.rows = rows;
        this.columns = columns;
        this.depth = depth;
        this.mode = mode;
    }

    public byte[] getReserved() {
        return (byte[])this.reserved.clone();
    }

    public void dump() {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw);){
            this.dump(pw);
            pw.flush();
            sw.flush();
            LOGGER.fine(sw.toString());
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void dump(PrintWriter pw) {
        pw.println("");
        pw.println("Header");
        pw.println("Version: " + this.version + " (" + Integer.toHexString(this.version) + ")");
        pw.println("Channels: " + this.channels + " (" + Integer.toHexString(this.channels) + ")");
        pw.println("Rows: " + this.rows + " (" + Integer.toHexString(this.rows) + ")");
        pw.println("Columns: " + this.columns + " (" + Integer.toHexString(this.columns) + ")");
        pw.println("Depth: " + this.depth + " (" + Integer.toHexString(this.depth) + ")");
        pw.println("Mode: " + this.mode + " (" + Integer.toHexString(this.mode) + ")");
        pw.println("Reserved: " + this.reserved.length);
        pw.println("");
        pw.flush();
    }
}

