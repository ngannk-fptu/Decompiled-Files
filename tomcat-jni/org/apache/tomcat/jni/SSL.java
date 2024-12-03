/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.BIOCallback;
import org.apache.tomcat.jni.PasswordCallback;

public final class SSL {
    public static final int UNSET = -1;
    public static final int SSL_ALGO_UNKNOWN = 0;
    public static final int SSL_ALGO_RSA = 1;
    public static final int SSL_ALGO_DSA = 2;
    public static final int SSL_ALGO_ALL = 3;
    public static final int SSL_AIDX_RSA = 0;
    public static final int SSL_AIDX_DSA = 1;
    public static final int SSL_AIDX_ECC = 3;
    public static final int SSL_AIDX_MAX = 4;
    public static final int SSL_TMP_KEY_RSA_512 = 0;
    public static final int SSL_TMP_KEY_RSA_1024 = 1;
    public static final int SSL_TMP_KEY_RSA_2048 = 2;
    public static final int SSL_TMP_KEY_RSA_4096 = 3;
    public static final int SSL_TMP_KEY_DH_512 = 4;
    public static final int SSL_TMP_KEY_DH_1024 = 5;
    public static final int SSL_TMP_KEY_DH_2048 = 6;
    public static final int SSL_TMP_KEY_DH_4096 = 7;
    public static final int SSL_TMP_KEY_MAX = 8;
    public static final int SSL_OPT_NONE = 0;
    public static final int SSL_OPT_RELSET = 1;
    public static final int SSL_OPT_STDENVVARS = 2;
    public static final int SSL_OPT_EXPORTCERTDATA = 8;
    public static final int SSL_OPT_FAKEBASICAUTH = 16;
    public static final int SSL_OPT_STRICTREQUIRE = 32;
    public static final int SSL_OPT_OPTRENEGOTIATE = 64;
    public static final int SSL_OPT_ALL = 122;
    public static final int SSL_PROTOCOL_NONE = 0;
    public static final int SSL_PROTOCOL_SSLV2 = 1;
    public static final int SSL_PROTOCOL_SSLV3 = 2;
    public static final int SSL_PROTOCOL_TLSV1 = 4;
    public static final int SSL_PROTOCOL_TLSV1_1 = 8;
    public static final int SSL_PROTOCOL_TLSV1_2 = 16;
    public static final int SSL_PROTOCOL_TLSV1_3 = 32;
    public static final int SSL_PROTOCOL_ALL = SSL.version() >= 0x1010100F ? 60 : 28;
    public static final int SSL_CVERIFY_UNSET = -1;
    public static final int SSL_CVERIFY_NONE = 0;
    public static final int SSL_CVERIFY_OPTIONAL = 1;
    public static final int SSL_CVERIFY_REQUIRE = 2;
    public static final int SSL_CVERIFY_OPTIONAL_NO_CA = 3;
    public static final int SSL_VERIFY_NONE = 0;
    public static final int SSL_VERIFY_PEER = 1;
    public static final int SSL_VERIFY_FAIL_IF_NO_PEER_CERT = 2;
    public static final int SSL_VERIFY_CLIENT_ONCE = 4;
    public static final int SSL_VERIFY_PEER_STRICT = 3;
    public static final int SSL_OP_MICROSOFT_SESS_ID_BUG = 1;
    public static final int SSL_OP_NETSCAPE_CHALLENGE_BUG = 2;
    public static final int SSL_OP_NETSCAPE_REUSE_CIPHER_CHANGE_BUG = 8;
    public static final int SSL_OP_SSLREF2_REUSE_CERT_TYPE_BUG = 16;
    public static final int SSL_OP_MICROSOFT_BIG_SSLV3_BUFFER = 32;
    public static final int SSL_OP_MSIE_SSLV2_RSA_PADDING = 64;
    public static final int SSL_OP_SSLEAY_080_CLIENT_DH_BUG = 128;
    public static final int SSL_OP_TLS_D5_BUG = 256;
    public static final int SSL_OP_TLS_BLOCK_PADDING_BUG = 512;
    public static final int SSL_OP_DONT_INSERT_EMPTY_FRAGMENTS = 2048;
    public static final int SSL_OP_ALL = 4095;
    public static final int SSL_OP_NO_SESSION_RESUMPTION_ON_RENEGOTIATION = 65536;
    public static final int SSL_OP_NO_COMPRESSION = 131072;
    public static final int SSL_OP_ALLOW_UNSAFE_LEGACY_RENEGOTIATION = 262144;
    public static final int SSL_OP_SINGLE_ECDH_USE = 524288;
    public static final int SSL_OP_SINGLE_DH_USE = 0x100000;
    public static final int SSL_OP_EPHEMERAL_RSA = 0x200000;
    public static final int SSL_OP_CIPHER_SERVER_PREFERENCE = 0x400000;
    public static final int SSL_OP_TLS_ROLLBACK_BUG = 0x800000;
    public static final int SSL_OP_NO_SSLv2 = 0x1000000;
    public static final int SSL_OP_NO_SSLv3 = 0x2000000;
    public static final int SSL_OP_NO_TLSv1 = 0x4000000;
    public static final int SSL_OP_NO_TLSv1_2 = 0x8000000;
    public static final int SSL_OP_NO_TLSv1_1 = 0x10000000;
    public static final int SSL_OP_NO_TICKET = 16384;
    @Deprecated
    public static final int SSL_OP_PKCS1_CHECK_1 = 0x8000000;
    @Deprecated
    public static final int SSL_OP_PKCS1_CHECK_2 = 0x10000000;
    public static final int SSL_OP_NETSCAPE_CA_DN_BUG = 0x20000000;
    public static final int SSL_OP_NETSCAPE_DEMO_CIPHER_CHANGE_BUG = 0x40000000;
    public static final int SSL_CRT_FORMAT_UNDEF = 0;
    public static final int SSL_CRT_FORMAT_ASN1 = 1;
    public static final int SSL_CRT_FORMAT_TEXT = 2;
    public static final int SSL_CRT_FORMAT_PEM = 3;
    public static final int SSL_CRT_FORMAT_NETSCAPE = 4;
    public static final int SSL_CRT_FORMAT_PKCS12 = 5;
    public static final int SSL_CRT_FORMAT_SMIME = 6;
    public static final int SSL_CRT_FORMAT_ENGINE = 7;
    public static final int SSL_MODE_CLIENT = 0;
    public static final int SSL_MODE_SERVER = 1;
    public static final int SSL_MODE_COMBINED = 2;
    public static final int SSL_CONF_FLAG_CMDLINE = 1;
    public static final int SSL_CONF_FLAG_FILE = 2;
    public static final int SSL_CONF_FLAG_CLIENT = 4;
    public static final int SSL_CONF_FLAG_SERVER = 8;
    public static final int SSL_CONF_FLAG_SHOW_ERRORS = 16;
    public static final int SSL_CONF_FLAG_CERTIFICATE = 32;
    public static final int SSL_CONF_TYPE_UNKNOWN = 0;
    public static final int SSL_CONF_TYPE_STRING = 1;
    public static final int SSL_CONF_TYPE_FILE = 2;
    public static final int SSL_CONF_TYPE_DIR = 3;
    public static final int SSL_SHUTDOWN_TYPE_UNSET = 0;
    public static final int SSL_SHUTDOWN_TYPE_STANDARD = 1;
    public static final int SSL_SHUTDOWN_TYPE_UNCLEAN = 2;
    public static final int SSL_SHUTDOWN_TYPE_ACCURATE = 3;
    public static final int SSL_INFO_SESSION_ID = 1;
    public static final int SSL_INFO_CIPHER = 2;
    public static final int SSL_INFO_CIPHER_USEKEYSIZE = 3;
    public static final int SSL_INFO_CIPHER_ALGKEYSIZE = 4;
    public static final int SSL_INFO_CIPHER_VERSION = 5;
    public static final int SSL_INFO_CIPHER_DESCRIPTION = 6;
    public static final int SSL_INFO_PROTOCOL = 7;
    public static final int SSL_INFO_CLIENT_S_DN = 16;
    public static final int SSL_INFO_CLIENT_I_DN = 32;
    public static final int SSL_INFO_SERVER_S_DN = 64;
    public static final int SSL_INFO_SERVER_I_DN = 128;
    public static final int SSL_INFO_DN_COUNTRYNAME = 1;
    public static final int SSL_INFO_DN_STATEORPROVINCENAME = 2;
    public static final int SSL_INFO_DN_LOCALITYNAME = 3;
    public static final int SSL_INFO_DN_ORGANIZATIONNAME = 4;
    public static final int SSL_INFO_DN_ORGANIZATIONALUNITNAME = 5;
    public static final int SSL_INFO_DN_COMMONNAME = 6;
    public static final int SSL_INFO_DN_TITLE = 7;
    public static final int SSL_INFO_DN_INITIALS = 8;
    public static final int SSL_INFO_DN_GIVENNAME = 9;
    public static final int SSL_INFO_DN_SURNAME = 10;
    public static final int SSL_INFO_DN_DESCRIPTION = 11;
    public static final int SSL_INFO_DN_UNIQUEIDENTIFIER = 12;
    public static final int SSL_INFO_DN_EMAILADDRESS = 13;
    public static final int SSL_INFO_CLIENT_M_VERSION = 257;
    public static final int SSL_INFO_CLIENT_M_SERIAL = 258;
    public static final int SSL_INFO_CLIENT_V_START = 259;
    public static final int SSL_INFO_CLIENT_V_END = 260;
    public static final int SSL_INFO_CLIENT_A_SIG = 261;
    public static final int SSL_INFO_CLIENT_A_KEY = 262;
    public static final int SSL_INFO_CLIENT_CERT = 263;
    public static final int SSL_INFO_CLIENT_V_REMAIN = 264;
    public static final int SSL_INFO_SERVER_M_VERSION = 513;
    public static final int SSL_INFO_SERVER_M_SERIAL = 514;
    public static final int SSL_INFO_SERVER_V_START = 515;
    public static final int SSL_INFO_SERVER_V_END = 516;
    public static final int SSL_INFO_SERVER_A_SIG = 517;
    public static final int SSL_INFO_SERVER_A_KEY = 518;
    public static final int SSL_INFO_SERVER_CERT = 519;
    public static final int SSL_INFO_CLIENT_CERT_CHAIN = 1024;
    public static final long SSL_SESS_CACHE_OFF = 0L;
    public static final long SSL_SESS_CACHE_SERVER = 2L;
    public static final int SSL_SELECTOR_FAILURE_NO_ADVERTISE = 0;
    public static final int SSL_SELECTOR_FAILURE_CHOOSE_MY_LAST_PROTOCOL = 1;
    public static final int SSL_SENT_SHUTDOWN = 1;
    public static final int SSL_RECEIVED_SHUTDOWN = 2;
    public static final int SSL_ERROR_NONE = 0;
    public static final int SSL_ERROR_SSL = 1;
    public static final int SSL_ERROR_WANT_READ = 2;
    public static final int SSL_ERROR_WANT_WRITE = 3;
    public static final int SSL_ERROR_WANT_X509_LOOKUP = 4;
    public static final int SSL_ERROR_SYSCALL = 5;
    public static final int SSL_ERROR_ZERO_RETURN = 6;
    public static final int SSL_ERROR_WANT_CONNECT = 7;
    public static final int SSL_ERROR_WANT_ACCEPT = 8;

    public static native int version();

    public static native String versionString();

    public static native int initialize(String var0);

    public static native int fipsModeGet() throws Exception;

    public static native int fipsModeSet(int var0) throws Exception;

    @Deprecated
    public static native boolean randLoad(String var0);

    @Deprecated
    public static native boolean randSave(String var0);

    @Deprecated
    public static native boolean randMake(String var0, int var1, boolean var2);

    public static native void randSet(String var0);

    @Deprecated
    public static native long newBIO(long var0, BIOCallback var2) throws Exception;

    @Deprecated
    public static native int closeBIO(long var0);

    @Deprecated
    public static native void setPasswordCallback(PasswordCallback var0);

    @Deprecated
    public static native void setPassword(String var0);

    @Deprecated
    public static native String getLastError();

    @Deprecated
    public static native boolean hasOp(int var0);

    public static native int getHandshakeCount(long var0);

    public static native long newSSL(long var0, boolean var2);

    @Deprecated
    public static native void setBIO(long var0, long var2, long var4);

    @Deprecated
    public static native int getError(long var0, int var2);

    public static native int pendingWrittenBytesInBIO(long var0);

    public static native int pendingReadableBytesInSSL(long var0);

    public static native int writeToBIO(long var0, long var2, int var4);

    public static native int readFromBIO(long var0, long var2, int var4);

    public static native int writeToSSL(long var0, long var2, int var4);

    public static native int readFromSSL(long var0, long var2, int var4);

    public static native int getShutdown(long var0);

    @Deprecated
    public static native void setShutdown(long var0, int var2);

    public static native void freeSSL(long var0);

    public static native long makeNetworkBIO(long var0);

    public static native void freeBIO(long var0);

    public static native int shutdownSSL(long var0);

    public static native int getLastErrorNumber();

    public static native String getCipherForSSL(long var0);

    public static native String getVersion(long var0);

    public static native int doHandshake(long var0);

    public static native int renegotiate(long var0);

    public static native int renegotiatePending(long var0);

    public static native int verifyClientPostHandshake(long var0);

    public static native int getPostHandshakeAuthInProgress(long var0);

    public static native int isInInit(long var0);

    @Deprecated
    public static native String getNextProtoNegotiated(long var0);

    public static native String getAlpnSelected(long var0);

    public static native byte[][] getPeerCertChain(long var0);

    public static native byte[] getPeerCertificate(long var0);

    public static native String getErrorString(long var0);

    public static native long getTime(long var0);

    public static native void setVerify(long var0, int var2, int var3);

    public static native void setOptions(long var0, int var2);

    public static native int getOptions(long var0);

    public static native String[] getCiphers(long var0);

    public static native boolean setCipherSuites(long var0, String var2) throws Exception;

    public static native byte[] getSessionId(long var0);
}

