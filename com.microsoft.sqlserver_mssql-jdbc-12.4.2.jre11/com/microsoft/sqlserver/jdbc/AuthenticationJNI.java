/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.DLLException;
import com.microsoft.sqlserver.jdbc.FedAuthDllInfo;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSPIAuthentication;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

final class AuthenticationJNI
extends SSPIAuthentication {
    private static final int MAXPOINTERSIZE = 128;
    private static boolean enabled = false;
    private static Logger authLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.AuthenticationJNI");
    private static int sspiBlobMaxlen = 0;
    private byte[] sniSec = new byte[128];
    private int[] sniSecLen = new int[]{0};
    private final String dnsName;
    private final int port;
    private SQLServerConnection con;
    private static final UnsatisfiedLinkError linkError;

    static int getMaxSSPIBlobSize() {
        return sspiBlobMaxlen;
    }

    static boolean isDllLoaded() {
        return enabled;
    }

    AuthenticationJNI(SQLServerConnection con, String address, int serverport) throws SQLServerException {
        if (!enabled) {
            con.terminate(0, SQLServerException.getErrString("R_notConfiguredForIntegrated"), linkError);
        }
        this.con = con;
        this.dnsName = AuthenticationJNI.initDNSArray(address);
        this.port = serverport;
    }

    static FedAuthDllInfo getAccessTokenForWindowsIntegrated(String stsURL, String servicePrincipalName, String clientConnectionId, String clientId, long expirationFileTime) throws DLLException {
        return AuthenticationJNI.ADALGetAccessTokenForWindowsIntegrated(stsURL, servicePrincipalName, clientConnectionId, clientId, expirationFileTime, authLogger);
    }

    @Override
    byte[] generateClientContext(byte[] pin, boolean[] done) throws SQLServerException {
        int[] outsize = new int[]{AuthenticationJNI.getMaxSSPIBlobSize()};
        byte[] pOut = new byte[outsize[0]];
        assert (this.dnsName != null);
        int failure = AuthenticationJNI.SNISecGenClientContext(this.sniSec, this.sniSecLen, pin, pin.length, pOut, outsize, done, this.dnsName, this.port, null, null, authLogger);
        if (failure != 0) {
            if (authLogger.isLoggable(Level.WARNING)) {
                authLogger.warning(this.toString() + " Authentication failed code : " + failure);
            }
            this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), linkError);
        }
        byte[] output = new byte[outsize[0]];
        System.arraycopy(pOut, 0, output, 0, outsize[0]);
        return output;
    }

    @Override
    void releaseClientContext() {
        int success = 0;
        if (this.sniSecLen[0] > 0) {
            success = AuthenticationJNI.SNISecReleaseClientContext(this.sniSec, this.sniSecLen[0], authLogger);
            this.sniSecLen[0] = 0;
        }
        if (authLogger.isLoggable(Level.FINER)) {
            authLogger.finer(this.toString() + " Release client context status : " + success);
        }
    }

    private static String initDNSArray(String address) {
        String[] dns = new String[1];
        if (AuthenticationJNI.GetDNSName(address, dns, authLogger) != 0) {
            dns[0] = address;
        }
        return dns[0];
    }

    private static native int SNISecGenClientContext(byte[] var0, int[] var1, byte[] var2, int var3, byte[] var4, int[] var5, boolean[] var6, String var7, int var8, String var9, String var10, Logger var11);

    private static native int SNISecReleaseClientContext(byte[] var0, int var1, Logger var2);

    private static native int SNISecInitPackage(int[] var0, Logger var1);

    private static native int SNISecTerminatePackage(Logger var0);

    private static native int SNIGetSID(byte[] var0, Logger var1);

    private static native boolean SNIIsEqualToCurrentSID(byte[] var0, Logger var1);

    private static native int GetDNSName(String var0, String[] var1, Logger var2);

    private static synchronized native FedAuthDllInfo ADALGetAccessTokenForWindowsIntegrated(String var0, String var1, String var2, String var3, long var4, Logger var6);

    static synchronized native byte[] DecryptColumnEncryptionKey(String var0, String var1, byte[] var2) throws DLLException;

    static synchronized native boolean VerifyColumnMasterKeyMetadata(String var0, boolean var1, byte[] var2) throws DLLException;

    static {
        UnsatisfiedLinkError temp = null;
        try {
            System.loadLibrary(SQLServerDriver.AUTH_DLL_NAME);
            int[] pkg = new int[]{0};
            if (0 != AuthenticationJNI.SNISecInitPackage(pkg, authLogger)) {
                throw new UnsatisfiedLinkError();
            }
            sspiBlobMaxlen = pkg[0];
            enabled = true;
        }
        catch (UnsatisfiedLinkError e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnableLoadAuthDll"));
            temp = new UnsatisfiedLinkError(form.format(new Object[]{SQLServerDriver.AUTH_DLL_NAME}));
        }
        finally {
            linkError = temp;
        }
    }
}

