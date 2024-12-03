/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.auth.AWS3Signer;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWS4UnsignedPayloadSigner;
import com.amazonaws.auth.NoOpSigner;
import com.amazonaws.auth.QueryStringSigner;
import com.amazonaws.auth.RegionAwareSigner;
import com.amazonaws.auth.ServiceAwareSigner;
import com.amazonaws.auth.Signer;
import com.amazonaws.auth.SignerParams;
import com.amazonaws.internal.config.InternalConfig;
import com.amazonaws.internal.config.SignerConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SignerFactory {
    public static final String QUERY_STRING_SIGNER = "QueryStringSignerType";
    public static final String VERSION_THREE_SIGNER = "AWS3SignerType";
    public static final String VERSION_FOUR_SIGNER = "AWS4SignerType";
    public static final String VERSION_FOUR_UNSIGNED_PAYLOAD_SIGNER = "AWS4UnsignedPayloadSignerType";
    public static final String NO_OP_SIGNER = "NoOpSignerType";
    private static final String S3_V4_SIGNER = "AWSS3V4SignerType";
    private static final Map<String, Class<? extends Signer>> SIGNERS = new ConcurrentHashMap<String, Class<? extends Signer>>();

    private SignerFactory() {
    }

    public static void registerSigner(String signerType, Class<? extends Signer> signerClass) {
        if (signerType == null) {
            throw new IllegalArgumentException("signerType cannot be null");
        }
        if (signerClass == null) {
            throw new IllegalArgumentException("signerClass cannot be null");
        }
        SIGNERS.put(signerType, signerClass);
    }

    public static Signer getSigner(String serviceName, String regionName) {
        return SignerFactory.lookupAndCreateSigner(serviceName, regionName);
    }

    public static Signer getSignerByTypeAndService(String signerType, String serviceName) {
        return SignerFactory.createSigner(signerType, serviceName);
    }

    private static String lookUpSignerTypeByServiceAndRegion(String serviceName, String regionName) {
        InternalConfig config = InternalConfig.Factory.getInternalConfig();
        SignerConfig signerConfig = config.getSignerConfig(serviceName, regionName);
        return signerConfig.getSignerType();
    }

    private static Signer lookupAndCreateSigner(String serviceName, String regionName) {
        String signerType = SignerFactory.lookUpSignerTypeByServiceAndRegion(serviceName, regionName);
        return SignerFactory.createSigner(signerType, serviceName);
    }

    private static Signer createSigner(String signerType, String serviceName) {
        Class<? extends Signer> signerClass = SIGNERS.get(signerType);
        if (signerClass == null) {
            throw new IllegalArgumentException("unknown signer type: " + signerType);
        }
        Signer signer = SignerFactory.createSigner(signerType);
        if (signer instanceof ServiceAwareSigner) {
            ((ServiceAwareSigner)signer).setServiceName(serviceName);
        }
        return signer;
    }

    @SdkProtectedApi
    public static Signer createSigner(String signerType, SignerParams params) {
        Signer signer = SignerFactory.createSigner(signerType);
        if (signer instanceof ServiceAwareSigner) {
            ((ServiceAwareSigner)signer).setServiceName(params.getServiceName());
        }
        if (signer instanceof RegionAwareSigner) {
            ((RegionAwareSigner)signer).setRegionName(params.getRegionName());
        }
        return signer;
    }

    private static Signer createSigner(String signerType) {
        Signer signer;
        Class<? extends Signer> signerClass = SIGNERS.get(signerType);
        try {
            signer = signerClass.newInstance();
        }
        catch (InstantiationException ex) {
            throw new IllegalStateException("Cannot create an instance of " + signerClass.getName(), ex);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException("Cannot create an instance of " + signerClass.getName(), ex);
        }
        return signer;
    }

    static {
        SIGNERS.put(QUERY_STRING_SIGNER, QueryStringSigner.class);
        SIGNERS.put(VERSION_THREE_SIGNER, AWS3Signer.class);
        SIGNERS.put(VERSION_FOUR_SIGNER, AWS4Signer.class);
        SIGNERS.put(VERSION_FOUR_UNSIGNED_PAYLOAD_SIGNER, AWS4UnsignedPayloadSigner.class);
        SIGNERS.put(NO_OP_SIGNER, NoOpSigner.class);
        try {
            SIGNERS.put(S3_V4_SIGNER, Class.forName("com.amazonaws.services.s3.internal.AWSS3V4Signer"));
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }
}

