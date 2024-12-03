/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache.marshallers;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.Marshaller;
import com.atlassian.vcache.marshallers.JavaSerializationMarshaller;
import com.atlassian.vcache.marshallers.OptionalMarshaller;
import com.atlassian.vcache.marshallers.StringMarshaller;
import java.io.Serializable;
import java.util.Optional;

@Deprecated
@PublicApi
public class MarshallerFactory {
    public static <T> Marshaller<Optional<T>> optionalMarshaller(Marshaller<T> valueMarshaller) {
        return new OptionalMarshaller<T>(valueMarshaller);
    }

    public static Marshaller<String> stringMarshaller() {
        return new StringMarshaller();
    }

    public static <T extends Serializable> Marshaller<T> serializableMarshaller(Class<T> clazz) {
        return new JavaSerializationMarshaller<T>(clazz);
    }

    public static <T extends Serializable> Marshaller<T> serializableMarshaller(Class<T> clazz, ClassLoader loader) {
        return new JavaSerializationMarshaller<T>(clazz, loader);
    }
}

