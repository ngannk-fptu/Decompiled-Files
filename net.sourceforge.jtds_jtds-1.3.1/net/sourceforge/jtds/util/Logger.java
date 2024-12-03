/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;

public class Logger {
    private static PrintWriter log;
    private static final char[] hex;

    public static void setLogWriter(PrintWriter out) {
        log = out;
    }

    public static PrintWriter getLogWriter() {
        return log;
    }

    public static boolean isActive() {
        return log != null || DriverManager.getLogWriter() != null;
    }

    public static void println(String message) {
        if (log != null) {
            log.println(message);
        } else {
            PrintWriter pw = DriverManager.getLogWriter();
            if (pw != null) {
                pw.println(message);
                pw.flush();
            }
        }
    }

    public static void logPacket(int streamId, boolean in, byte[] pkt) {
        int len = (pkt[2] & 0xFF) << 8 | pkt[3] & 0xFF;
        StringBuffer line = new StringBuffer(80);
        line.append("----- Stream #");
        line.append(streamId);
        line.append(in ? " read" : " send");
        line.append(pkt[1] != 0 ? " last " : " ");
        switch (pkt[0]) {
            case 1: {
                line.append("Request packet ");
                break;
            }
            case 2: {
                line.append("Login packet ");
                break;
            }
            case 3: {
                line.append("RPC packet ");
                break;
            }
            case 4: {
                line.append("Reply packet ");
                break;
            }
            case 6: {
                line.append("Cancel packet ");
                break;
            }
            case 14: {
                line.append("XA control packet ");
                break;
            }
            case 15: {
                line.append("TDS5 Request packet ");
                break;
            }
            case 16: {
                line.append("MS Login packet ");
                break;
            }
            case 17: {
                line.append("NTLM Authentication packet ");
                break;
            }
            case 18: {
                line.append("MS Prelogin packet ");
                break;
            }
            default: {
                line.append("Invalid packet ");
            }
        }
        Logger.println(line.toString());
        Logger.println("");
        line.setLength(0);
        for (int i = 0; i < len; i += 16) {
            int val;
            int j;
            if (i < 1000) {
                line.append(' ');
            }
            if (i < 100) {
                line.append(' ');
            }
            if (i < 10) {
                line.append(' ');
            }
            line.append(i);
            line.append(':').append(' ');
            for (j = 0; j < 16 && i + j < len; ++j) {
                val = pkt[i + j] & 0xFF;
                line.append(hex[val >> 4]);
                line.append(hex[val & 0xF]);
                line.append(' ');
            }
            while (j < 16) {
                line.append("   ");
                ++j;
            }
            line.append('|');
            for (j = 0; j < 16 && i + j < len; ++j) {
                val = pkt[i + j] & 0xFF;
                if (val > 31 && val < 127) {
                    line.append((char)val);
                    continue;
                }
                line.append(' ');
            }
            line.append('|');
            Logger.println(line.toString());
            line.setLength(0);
        }
        Logger.println("");
    }

    public static void logException(Exception e) {
        if (log != null) {
            e.printStackTrace(log);
        } else {
            PrintWriter pw = DriverManager.getLogWriter();
            if (pw != null) {
                e.printStackTrace(pw);
                pw.flush();
            }
        }
    }

    public static void setActive(boolean value) {
        if (value && log == null) {
            try {
                log = new PrintWriter(new FileOutputStream("log.out"), true);
            }
            catch (IOException e) {
                log = null;
            }
        }
    }

    static {
        hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    }
}

