/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tomcat.jni.CertificateVerifier;

public final class SSLContext {
    public static final byte[] DEFAULT_SESSION_ID_CONTEXT = new byte[]{100, 101, 102, 97, 117, 108, 116};
    private static final Map<Long, SNICallBack> sniCallBacks = new ConcurrentHashMap<Long, SNICallBack>();

    public static native long make(long var0, int var2, int var3) throws Exception;

    public static native int free(long var0);

    @Deprecated
    public static native void setContextId(long var0, String var2);

    @Deprecated
    public static native void setBIO(long var0, long var2, int var4);

    public static native void setOptions(long var0, int var2);

    public static native int getOptions(long var0);

    public static native void clearOptions(long var0, int var2);

    public static native String[] getCiphers(long var0);

    @Deprecated
    public static native void setQuietShutdown(long var0, boolean var2);

    public static native boolean setCipherSuite(long var0, String var2) throws Exception;

    public static native boolean setCARevocation(long var0, String var2, String var3) throws Exception;

    public static native boolean setCertificateChainFile(long var0, String var2, boolean var3);

    public static native boolean setCertificate(long var0, String var2, String var3, String var4, int var5) throws Exception;

    public static native long setSessionCacheSize(long var0, long var2);

    public static native long getSessionCacheSize(long var0);

    public static native long setSessionCacheTimeout(long var0, long var2);

    public static native long getSessionCacheTimeout(long var0);

    public static native long setSessionCacheMode(long var0, long var2);

    public static native long getSessionCacheMode(long var0);

    public static native long sessionAccept(long var0);

    public static native long sessionAcceptGood(long var0);

    public static native long sessionAcceptRenegotiate(long var0);

    public static native long sessionCacheFull(long var0);

    public static native long sessionCbHits(long var0);

    public static native long sessionConnect(long var0);

    public static native long sessionConnectGood(long var0);

    public static native long sessionConnectRenegotiate(long var0);

    public static native long sessionHits(long var0);

    public static native long sessionMisses(long var0);

    public static native long sessionNumber(long var0);

    public static native long sessionTimeouts(long var0);

    public static native void setSessionTicketKeys(long var0, byte[] var2);

    public static native boolean setCACertificate(long var0, String var2, String var3) throws Exception;

    @Deprecated
    public static native void setRandom(long var0, String var2);

    @Deprecated
    public static native void setShutdownType(long var0, int var2);

    public static native void setVerify(long var0, int var2, int var3);

    @Deprecated
    public static native int setALPN(long var0, byte[] var2, int var3);

    public static long sniCallBack(long currentCtx, String sniHostName) {
        SNICallBack sniCallBack = sniCallBacks.get(currentCtx);
        if (sniCallBack == null) {
            return 0L;
        }
        String hostName = sniHostName == null ? null : sniHostName.toLowerCase(Locale.ENGLISH);
        return sniCallBack.getSslContext(hostName);
    }

    @Deprecated
    public static void registerDefault(Long defaultSSLContext, SNICallBack sniCallBack) {
        sniCallBacks.put(defaultSSLContext, sniCallBack);
    }

    @Deprecated
    public static void unregisterDefault(Long defaultSSLContext) {
        sniCallBacks.remove(defaultSSLContext);
    }

    public static native void setCertVerifyCallback(long var0, CertificateVerifier var2);

    @Deprecated
    public static void setNextProtos(long ctx, String nextProtos) {
        SSLContext.setNpnProtos(ctx, nextProtos.split(","), 1);
    }

    @Deprecated
    public static native void setNpnProtos(long var0, String[] var2, int var3);

    public static native void setAlpnProtos(long var0, String[] var2, int var3);

    @Deprecated
    public static native void setTmpDH(long var0, String var2) throws Exception;

    @Deprecated
    public static native void setTmpECDHByCurveName(long var0, String var2) throws Exception;

    public static native boolean setSessionIdContext(long var0, byte[] var2);

    public static native boolean setCertificateRaw(long var0, byte[] var2, byte[] var3, int var4);

    public static native boolean addChainCertificateRaw(long var0, byte[] var2);

    public static native boolean addClientCACertificateRaw(long var0, byte[] var2);

    public static interface SNICallBack {
        public long getSslContext(String var1);
    }
}

