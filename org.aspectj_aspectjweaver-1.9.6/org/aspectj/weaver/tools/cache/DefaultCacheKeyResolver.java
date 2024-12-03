/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;
import org.aspectj.weaver.tools.cache.CacheKeyResolver;
import org.aspectj.weaver.tools.cache.CachedClassReference;

public class DefaultCacheKeyResolver
implements CacheKeyResolver {
    public static final String GENERATED_SUFFIX = ".generated";
    public static final String WEAVED_SUFFIX = ".weaved";

    @Override
    public String createClassLoaderScope(ClassLoader cl, List<String> aspects) {
        String name = cl != null ? cl.getClass().getSimpleName() : "unknown";
        LinkedList<String> hashableStrings = new LinkedList<String>();
        StringBuilder hashable = new StringBuilder(256);
        if (cl != null && cl instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader)cl).getURLs();
            for (int i = 0; i < urls.length; ++i) {
                hashableStrings.add(urls[i].toString());
            }
        }
        hashableStrings.addAll(aspects);
        Collections.sort(hashableStrings);
        for (String url : hashableStrings) {
            hashable.append(url);
        }
        String hash = null;
        byte[] bytes = hashable.toString().getBytes();
        hash = this.crc(bytes);
        return name + '.' + hash;
    }

    private String crc(byte[] input) {
        CRC32 crc32 = new CRC32();
        crc32.update(input);
        return String.valueOf(crc32.getValue());
    }

    @Override
    public String getGeneratedRegex() {
        return ".*.generated";
    }

    @Override
    public String getWeavedRegex() {
        return ".*.weaved";
    }

    @Override
    public String keyToClass(String key) {
        if (key.endsWith(GENERATED_SUFFIX)) {
            return key.replaceAll(".generated$", "");
        }
        if (key.endsWith(WEAVED_SUFFIX)) {
            return key.replaceAll("\\.[^.]+.weaved", "");
        }
        return key;
    }

    @Override
    public CachedClassReference weavedKey(String className, byte[] original_bytes) {
        String hash = this.crc(original_bytes);
        return new CachedClassReference(className + "." + hash + WEAVED_SUFFIX, className);
    }

    @Override
    public CachedClassReference generatedKey(String className) {
        return new CachedClassReference(className + GENERATED_SUFFIX, className);
    }
}

