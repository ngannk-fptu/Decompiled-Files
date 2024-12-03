/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.internal.util.ClassLoaderHelper
 *  software.amazon.awssdk.core.signer.Signer
 */
package software.amazon.awssdk.auth.signer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.core.signer.Signer;

@SdkProtectedApi
public final class SignerLoader {
    private static final Map<String, Signer> SIGNERS = new ConcurrentHashMap<String, Signer>();

    private SignerLoader() {
    }

    public static Signer getSigV4aSigner() {
        return SignerLoader.get("software.amazon.awssdk.authcrt.signer.AwsCrtV4aSigner");
    }

    public static Signer getS3SigV4aSigner() {
        return SignerLoader.get("software.amazon.awssdk.authcrt.signer.AwsCrtS3V4aSigner");
    }

    private static Signer get(String fqcn) {
        return SIGNERS.computeIfAbsent(fqcn, SignerLoader::initializeV4aSigner);
    }

    private static Signer initializeV4aSigner(String fqcn) {
        try {
            Class signerClass = ClassLoaderHelper.loadClass((String)fqcn, (boolean)false, (Class[])new Class[]{null});
            Method m = signerClass.getDeclaredMethod("create", new Class[0]);
            Object o = m.invoke(null, new Object[0]);
            return (Signer)o;
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the " + fqcn + " class. To invoke a request that requires a SigV4a signer, such as region independent signing, the 'auth-crt' core module must be on the class path. ", e);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create " + fqcn, e);
        }
    }
}

