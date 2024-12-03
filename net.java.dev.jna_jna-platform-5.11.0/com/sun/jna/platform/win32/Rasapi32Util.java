/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.ptr.IntByReference
 */
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.Rasapi32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinRas;
import com.sun.jna.ptr.IntByReference;
import java.util.HashMap;
import java.util.Map;

public abstract class Rasapi32Util {
    private static final int RASP_PppIp = 32801;
    private static Object phoneBookMutex = new Object();
    public static final Map CONNECTION_STATE_TEXT = new HashMap();

    public static String getRasErrorString(int code) {
        int len;
        char[] msg = new char[1024];
        int err = Rasapi32.INSTANCE.RasGetErrorString(code, msg, msg.length);
        if (err != 0) {
            return "Unknown error " + code;
        }
        for (len = 0; len < msg.length && msg[len] != '\u0000'; ++len) {
        }
        return new String(msg, 0, len);
    }

    public static String getRasConnectionStatusText(int connStatus) {
        if (!CONNECTION_STATE_TEXT.containsKey(connStatus)) {
            return Integer.toString(connStatus);
        }
        return (String)CONNECTION_STATE_TEXT.get(connStatus);
    }

    public static WinNT.HANDLE getRasConnection(String connName) throws Ras32Exception {
        int i;
        IntByReference lpcb = new IntByReference(0);
        IntByReference lpcConnections = new IntByReference();
        int err = Rasapi32.INSTANCE.RasEnumConnections(null, lpcb, lpcConnections);
        if (err != 0 && err != 603) {
            throw new Ras32Exception(err);
        }
        if (lpcb.getValue() == 0) {
            return null;
        }
        WinRas.RASCONN[] connections = new WinRas.RASCONN[lpcConnections.getValue()];
        for (i = 0; i < lpcConnections.getValue(); ++i) {
            connections[i] = new WinRas.RASCONN();
        }
        lpcb = new IntByReference(connections[0].dwSize * lpcConnections.getValue());
        err = Rasapi32.INSTANCE.RasEnumConnections(connections, lpcb, lpcConnections);
        if (err != 0) {
            throw new Ras32Exception(err);
        }
        for (i = 0; i < lpcConnections.getValue(); ++i) {
            if (!new String(connections[i].szEntryName).equals(connName)) continue;
            return connections[i].hrasconn;
        }
        return null;
    }

    public static void hangupRasConnection(String connName) throws Ras32Exception {
        WinNT.HANDLE hrasConn = Rasapi32Util.getRasConnection(connName);
        if (hrasConn == null) {
            return;
        }
        int err = Rasapi32.INSTANCE.RasHangUp(hrasConn);
        if (err != 0) {
            throw new Ras32Exception(err);
        }
    }

    public static void hangupRasConnection(WinNT.HANDLE hrasConn) throws Ras32Exception {
        if (hrasConn == null) {
            return;
        }
        int err = Rasapi32.INSTANCE.RasHangUp(hrasConn);
        if (err != 0) {
            throw new Ras32Exception(err);
        }
    }

    public static WinRas.RASPPPIP getIPProjection(WinNT.HANDLE hrasConn) throws Ras32Exception {
        WinRas.RASPPPIP pppIpProjection = new WinRas.RASPPPIP();
        IntByReference lpcb = new IntByReference(pppIpProjection.size());
        pppIpProjection.write();
        int err = Rasapi32.INSTANCE.RasGetProjectionInfo(hrasConn, 32801, pppIpProjection.getPointer(), lpcb);
        if (err != 0) {
            throw new Ras32Exception(err);
        }
        pppIpProjection.read();
        return pppIpProjection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static WinRas.RASENTRY.ByReference getPhoneBookEntry(String entryName) throws Ras32Exception {
        Object object = phoneBookMutex;
        synchronized (object) {
            WinRas.RASENTRY.ByReference rasEntry = new WinRas.RASENTRY.ByReference();
            IntByReference lpdwEntryInfoSize = new IntByReference(rasEntry.size());
            int err = Rasapi32.INSTANCE.RasGetEntryProperties(null, entryName, rasEntry, lpdwEntryInfoSize, null, null);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
            return rasEntry;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setPhoneBookEntry(String entryName, WinRas.RASENTRY.ByReference rasEntry) throws Ras32Exception {
        Object object = phoneBookMutex;
        synchronized (object) {
            int err = Rasapi32.INSTANCE.RasSetEntryProperties(null, entryName, rasEntry, rasEntry.size(), null, 0);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static WinRas.RASDIALPARAMS getPhoneBookDialingParams(String entryName) throws Ras32Exception {
        Object object = phoneBookMutex;
        synchronized (object) {
            WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
            System.arraycopy(rasDialParams.szEntryName, 0, entryName.toCharArray(), 0, entryName.length());
            WinDef.BOOLByReference lpfPassword = new WinDef.BOOLByReference();
            int err = Rasapi32.INSTANCE.RasGetEntryDialParams(null, rasDialParams, lpfPassword);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
            return rasDialParams;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static WinNT.HANDLE dialEntry(String entryName) throws Ras32Exception {
        WinRas.RASCREDENTIALS.ByReference credentials = new WinRas.RASCREDENTIALS.ByReference();
        Object object = phoneBookMutex;
        synchronized (object) {
            credentials.dwMask = 7;
            int err = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
        WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
        System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
        System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
        System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
        System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);
        WinNT.HANDLEByReference hrasConn = new WinNT.HANDLEByReference();
        int err = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 0, null, hrasConn);
        if (err != 0) {
            if (hrasConn.getValue() != null) {
                Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue());
            }
            throw new Ras32Exception(err);
        }
        return hrasConn.getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static WinNT.HANDLE dialEntry(String entryName, WinRas.RasDialFunc2 func2) throws Ras32Exception {
        WinRas.RASCREDENTIALS.ByReference credentials = new WinRas.RASCREDENTIALS.ByReference();
        Object object = phoneBookMutex;
        synchronized (object) {
            credentials.dwMask = 7;
            int err = Rasapi32.INSTANCE.RasGetCredentials(null, entryName, credentials);
            if (err != 0) {
                throw new Ras32Exception(err);
            }
        }
        WinRas.RASDIALPARAMS.ByReference rasDialParams = new WinRas.RASDIALPARAMS.ByReference();
        System.arraycopy(entryName.toCharArray(), 0, rasDialParams.szEntryName, 0, entryName.length());
        System.arraycopy(credentials.szUserName, 0, rasDialParams.szUserName, 0, credentials.szUserName.length);
        System.arraycopy(credentials.szPassword, 0, rasDialParams.szPassword, 0, credentials.szPassword.length);
        System.arraycopy(credentials.szDomain, 0, rasDialParams.szDomain, 0, credentials.szDomain.length);
        WinNT.HANDLEByReference hrasConn = new WinNT.HANDLEByReference();
        int err = Rasapi32.INSTANCE.RasDial(null, null, rasDialParams, 2, func2, hrasConn);
        if (err != 0) {
            if (hrasConn.getValue() != null) {
                Rasapi32.INSTANCE.RasHangUp(hrasConn.getValue());
            }
            throw new Ras32Exception(err);
        }
        return hrasConn.getValue();
    }

    static {
        CONNECTION_STATE_TEXT.put(0, "Opening the port...");
        CONNECTION_STATE_TEXT.put(1, "Port has been opened successfully");
        CONNECTION_STATE_TEXT.put(2, "Connecting to the device...");
        CONNECTION_STATE_TEXT.put(3, "The device has connected successfully.");
        CONNECTION_STATE_TEXT.put(4, "All devices in the device chain have successfully connected.");
        CONNECTION_STATE_TEXT.put(5, "Verifying the user name and password...");
        CONNECTION_STATE_TEXT.put(6, "An authentication event has occurred.");
        CONNECTION_STATE_TEXT.put(7, "Requested another validation attempt with a new user.");
        CONNECTION_STATE_TEXT.put(8, "Server has requested a callback number.");
        CONNECTION_STATE_TEXT.put(9, "The client has requested to change the password");
        CONNECTION_STATE_TEXT.put(10, "Registering your computer on the network...");
        CONNECTION_STATE_TEXT.put(11, "The link-speed calculation phase is starting...");
        CONNECTION_STATE_TEXT.put(12, "An authentication request is being acknowledged.");
        CONNECTION_STATE_TEXT.put(13, "Reauthentication (after callback) is starting.");
        CONNECTION_STATE_TEXT.put(14, "The client has successfully completed authentication.");
        CONNECTION_STATE_TEXT.put(15, "The line is about to disconnect for callback.");
        CONNECTION_STATE_TEXT.put(16, "Delaying to give the modem time to reset for callback.");
        CONNECTION_STATE_TEXT.put(17, "Waiting for an incoming call from server.");
        CONNECTION_STATE_TEXT.put(18, "Projection result information is available.");
        CONNECTION_STATE_TEXT.put(19, "User authentication is being initiated or retried.");
        CONNECTION_STATE_TEXT.put(20, "Client has been called back and is about to resume authentication.");
        CONNECTION_STATE_TEXT.put(21, "Logging on to the network...");
        CONNECTION_STATE_TEXT.put(22, "Subentry has been connected");
        CONNECTION_STATE_TEXT.put(23, "Subentry has been disconnected");
        CONNECTION_STATE_TEXT.put(4096, "Terminal state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(4097, "Retry authentication state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(4098, "Callback state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(4099, "Change password state supported by RASPHONE.EXE.");
        CONNECTION_STATE_TEXT.put(4100, "Displaying authentication UI");
        CONNECTION_STATE_TEXT.put(8192, "Connected to remote server successfully");
        CONNECTION_STATE_TEXT.put(8193, "Disconnected");
    }

    public static class Ras32Exception
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final int code;

        public int getCode() {
            return this.code;
        }

        public Ras32Exception(int code) {
            super(Rasapi32Util.getRasErrorString(code));
            this.code = code;
        }
    }
}

