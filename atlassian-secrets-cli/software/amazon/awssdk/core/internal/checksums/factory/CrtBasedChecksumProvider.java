/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.checksums.factory;

import java.util.Optional;
import java.util.zip.Checksum;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class CrtBasedChecksumProvider {
    public static final Logger LOG = Logger.loggerFor(CrtBasedChecksumProvider.class);
    private static final String CRT_CLASSPATH_FOR_CRC32C = "software.amazon.awssdk.crt.checksums.CRC32C";
    private static final String CRT_CLASSPATH_FOR_CRC32 = "software.amazon.awssdk.crt.checksums.CRC32";
    private static final Lazy<Optional<Class<?>>> CRT_CRC32_CLASS_LOADER = new Lazy<Optional>(() -> CrtBasedChecksumProvider.initializeCrtChecksumClass(CRT_CLASSPATH_FOR_CRC32));
    private static final Lazy<Optional<Class<?>>> CRT_CRC32_C_CLASS_LOADER = new Lazy<Optional>(() -> CrtBasedChecksumProvider.initializeCrtChecksumClass(CRT_CLASSPATH_FOR_CRC32C));

    private CrtBasedChecksumProvider() {
    }

    public static Checksum createCrc32() {
        return CrtBasedChecksumProvider.createCrtBasedChecksum(CRT_CRC32_CLASS_LOADER);
    }

    public static Checksum createCrc32C() {
        return CrtBasedChecksumProvider.createCrtBasedChecksum(CRT_CRC32_C_CLASS_LOADER);
    }

    private static Checksum createCrtBasedChecksum(Lazy<Optional<Class<?>>> lazyClassLoader) {
        return lazyClassLoader.getValue().map(checksumClass -> {
            try {
                return (Checksum)checksumClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ReflectiveOperationException e) {
                return null;
            }
        }).orElse(null);
    }

    private static Optional<Class<?>> initializeCrtChecksumClass(String classPath) {
        try {
            return Optional.of(ClassLoaderHelper.loadClass(classPath, false, new Class[0]));
        }
        catch (ClassNotFoundException e) {
            LOG.debug(() -> "Cannot find the " + classPath + " class. To invoke a request that requires a CRT based checksums.", e);
            return Optional.empty();
        }
    }
}

