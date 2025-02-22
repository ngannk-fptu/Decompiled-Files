/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl.util;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Date;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

final class OpenJdkSelfSignedCertGenerator {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenJdkSelfSignedCertGenerator.class);
    private static final Method CERT_INFO_SET_METHOD;
    private static final Constructor<?> ISSUER_NAME_CONSTRUCTOR;
    private static final Constructor<X509CertImpl> CERT_IMPL_CONSTRUCTOR;
    private static final Method CERT_IMPL_GET_METHOD;
    private static final Method CERT_IMPL_SIGN_METHOD;

    @SuppressJava6Requirement(reason="Usage guarded by dependency check")
    static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter, String algorithm) throws Exception {
        if (CERT_INFO_SET_METHOD == null || ISSUER_NAME_CONSTRUCTOR == null || CERT_IMPL_CONSTRUCTOR == null || CERT_IMPL_GET_METHOD == null || CERT_IMPL_SIGN_METHOD == null) {
            throw new UnsupportedOperationException(OpenJdkSelfSignedCertGenerator.class.getSimpleName() + " not supported on the used JDK version");
        }
        PrivateKey key = keypair.getPrivate();
        X509CertInfo info = new X509CertInfo();
        X500Name owner = new X500Name("CN=" + fqdn);
        CERT_INFO_SET_METHOD.invoke((Object)info, "version", new CertificateVersion(2));
        CERT_INFO_SET_METHOD.invoke((Object)info, "serialNumber", new CertificateSerialNumber(new BigInteger(64, random)));
        try {
            CERT_INFO_SET_METHOD.invoke((Object)info, "subject", new CertificateSubjectName(owner));
        }
        catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof CertificateException) {
                CERT_INFO_SET_METHOD.invoke((Object)info, "subject", owner);
            }
            throw ex;
        }
        try {
            CERT_INFO_SET_METHOD.invoke((Object)info, "issuer", ISSUER_NAME_CONSTRUCTOR.newInstance(owner));
        }
        catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof CertificateException) {
                CERT_INFO_SET_METHOD.invoke((Object)info, "issuer", owner);
            }
            throw ex;
        }
        CERT_INFO_SET_METHOD.invoke((Object)info, "validity", new CertificateValidity(notBefore, notAfter));
        CERT_INFO_SET_METHOD.invoke((Object)info, "key", new CertificateX509Key(keypair.getPublic()));
        CERT_INFO_SET_METHOD.invoke((Object)info, "algorithmID", new CertificateAlgorithmId(AlgorithmId.get("1.2.840.113549.1.1.11")));
        X509CertImpl cert = CERT_IMPL_CONSTRUCTOR.newInstance(info);
        CERT_IMPL_SIGN_METHOD.invoke((Object)cert, key, algorithm.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA");
        CERT_INFO_SET_METHOD.invoke((Object)info, "algorithmID.algorithm", CERT_IMPL_GET_METHOD.invoke((Object)cert, "x509.algorithm"));
        cert = CERT_IMPL_CONSTRUCTOR.newInstance(info);
        CERT_IMPL_SIGN_METHOD.invoke((Object)cert, key, algorithm.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA");
        cert.verify(keypair.getPublic());
        return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, cert);
    }

    private OpenJdkSelfSignedCertGenerator() {
    }

    static {
        Method certInfoSetMethod = null;
        Constructor issuerNameConstructor = null;
        Constructor certImplConstructor = null;
        Method certImplGetMethod = null;
        Method certImplSignMethod = null;
        try {
            Constructor constructor;
            Object maybeCertInfoSetMethod = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        return X509CertInfo.class.getMethod("set", String.class, Object.class);
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeCertInfoSetMethod instanceof Method)) {
                throw (Throwable)maybeCertInfoSetMethod;
            }
            certInfoSetMethod = (Method)maybeCertInfoSetMethod;
            Object maybeIssuerNameConstructor = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        Class<?> issuerName = Class.forName("sun.security.x509.CertificateIssuerName", false, PlatformDependent.getClassLoader(OpenJdkSelfSignedCertGenerator.class));
                        return issuerName.getConstructor(X500Name.class);
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeIssuerNameConstructor instanceof Constructor)) {
                throw (Throwable)maybeIssuerNameConstructor;
            }
            issuerNameConstructor = (Constructor)maybeIssuerNameConstructor;
            Object maybeCertImplConstructor = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        return X509CertImpl.class.getConstructor(X509CertInfo.class);
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeCertImplConstructor instanceof Constructor)) {
                throw (Throwable)maybeCertImplConstructor;
            }
            certImplConstructor = constructor = (Constructor)maybeCertImplConstructor;
            Object maybeCertImplGetMethod = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        return X509CertImpl.class.getMethod("get", String.class);
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeCertImplGetMethod instanceof Method)) {
                throw (Throwable)maybeCertImplGetMethod;
            }
            certImplGetMethod = (Method)maybeCertImplGetMethod;
            Object maybeCertImplSignMethod = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        return X509CertImpl.class.getMethod("sign", PrivateKey.class, String.class);
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (!(maybeCertImplSignMethod instanceof Method)) {
                throw (Throwable)maybeCertImplSignMethod;
            }
            certImplSignMethod = (Method)maybeCertImplSignMethod;
        }
        catch (Throwable cause) {
            logger.debug(OpenJdkSelfSignedCertGenerator.class.getSimpleName() + " not supported", cause);
        }
        CERT_INFO_SET_METHOD = certInfoSetMethod;
        ISSUER_NAME_CONSTRUCTOR = issuerNameConstructor;
        CERT_IMPL_CONSTRUCTOR = certImplConstructor;
        CERT_IMPL_GET_METHOD = certImplGetMethod;
        CERT_IMPL_SIGN_METHOD = certImplSignMethod;
    }
}

