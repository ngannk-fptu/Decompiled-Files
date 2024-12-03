/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.loader;

import java.util.Iterator;
import java.util.ServiceLoader;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.util.ClassLoaderHelper;

@SdkInternalApi
class SdkServiceLoader {
    public static final SdkServiceLoader INSTANCE = new SdkServiceLoader();

    SdkServiceLoader() {
    }

    <T> Iterator<T> loadServices(Class<T> clzz) {
        return ServiceLoader.load(clzz, ClassLoaderHelper.classLoader(SdkServiceLoader.class)).iterator();
    }
}

