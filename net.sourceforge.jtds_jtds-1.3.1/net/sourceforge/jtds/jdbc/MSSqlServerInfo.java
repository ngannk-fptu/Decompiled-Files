/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.util.Logger;

public class MSSqlServerInfo {
    private final int numRetries = 3;
    private final int timeout = 2000;
    private String[] serverInfoStrings;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public MSSqlServerInfo(String host) throws SQLException {
        block13: {
            this.numRetries = 3;
            this.timeout = 2000;
            try (DatagramSocket socket = null;){
                InetAddress addr = InetAddress.getByName(host);
                socket = new DatagramSocket();
                byte[] msg = new byte[]{2};
                DatagramPacket requestp = new DatagramPacket(msg, msg.length, addr, 1434);
                socket.setSoTimeout(2000);
                for (int i = 0; i < 3; ++i) {
                    try {
                        DatagramPacket responsep;
                        int length;
                        byte[] buf = new byte[]{};
                        do {
                            buf = new byte[buf.length + 4096];
                            responsep = new DatagramPacket(buf, buf.length);
                            socket.send(requestp);
                            socket.receive(responsep);
                        } while ((length = responsep.getLength()) == buf.length);
                        String infoString = MSSqlServerInfo.extractString(buf, length);
                        this.serverInfoStrings = MSSqlServerInfo.split(infoString, 59);
                        return;
                    }
                    catch (InterruptedIOException toEx) {
                        if (!Logger.isActive()) continue;
                        Logger.logException(toEx);
                        continue;
                    }
                }
                if (socket != null) {
                    socket.close();
                }
                break block13;
                {
                    catch (Exception e) {
                        if (Logger.isActive()) {
                            Logger.logException(e);
                        }
                    }
                }
            }
        }
        throw new SQLException(Messages.get("error.msinfo.badinfo", host), "HY000");
    }

    public int getPortForInstance(String instanceName) throws SQLException {
        if (this.serverInfoStrings == null) {
            return -1;
        }
        if (instanceName == null || instanceName.length() == 0) {
            instanceName = "MSSQLSERVER";
        }
        String curInstance = null;
        String curPort = null;
        for (int index = 0; index < this.serverInfoStrings.length; ++index) {
            if (this.serverInfoStrings[index].length() == 0) {
                curInstance = null;
                curPort = null;
                continue;
            }
            String key = this.serverInfoStrings[index];
            String value = "";
            if (++index < this.serverInfoStrings.length) {
                value = this.serverInfoStrings[index];
            }
            if ("InstanceName".equals(key)) {
                curInstance = value;
            }
            if ("tcp".equals(key)) {
                curPort = value;
            }
            if (curInstance == null || curPort == null || !curInstance.equalsIgnoreCase(instanceName)) continue;
            try {
                return Integer.parseInt(curPort);
            }
            catch (NumberFormatException e) {
                throw new SQLException(Messages.get("error.msinfo.badport", instanceName), "HY000");
            }
        }
        return -1;
    }

    private static final String extractString(byte[] buf, int len) {
        int headerLength = 3;
        return new String(buf, 3, len - 3);
    }

    public static String[] split(String s, int ch) {
        int size = 0;
        int pos = 0;
        while (pos != -1) {
            pos = s.indexOf(ch, pos + 1);
            ++size;
        }
        String[] res = new String[size];
        int i = 0;
        int p1 = 0;
        int p2 = s.indexOf(ch);
        do {
            res[i++] = s.substring(p1, p2 == -1 ? s.length() : p2);
            p1 = p2 + 1;
            p2 = s.indexOf(ch, p1);
        } while (p1 != 0);
        return res;
    }
}

