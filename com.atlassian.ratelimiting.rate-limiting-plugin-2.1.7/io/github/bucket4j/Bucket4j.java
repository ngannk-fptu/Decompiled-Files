/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.AbstractBucketBuilder;
import io.github.bucket4j.ConfigurationBuilder;
import io.github.bucket4j.Extension;
import io.github.bucket4j.local.LocalBucketBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class Bucket4j {
    private static final Map<Class, Extension> extensions = new HashMap<Class, Extension>();

    private Bucket4j() {
    }

    public static LocalBucketBuilder builder() {
        return new LocalBucketBuilder();
    }

    public static ConfigurationBuilder configurationBuilder() {
        return new ConfigurationBuilder();
    }

    public static <T extends AbstractBucketBuilder<T>, E extends Extension<T>> E extension(Class<E> extensionClass) {
        Extension extension = extensions.get(extensionClass);
        if (extension == null) {
            String msg = "extension with class [" + extensionClass + "] is not registered";
            throw new IllegalArgumentException(msg);
        }
        return (E)extension;
    }

    static {
        for (Extension extension : ServiceLoader.load(Extension.class)) {
            extensions.put(extension.getClass(), extension);
        }
    }
}

