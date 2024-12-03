/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ModuleRef;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

public class ModuleReaderProxy
implements Closeable {
    private final AutoCloseable moduleReader;
    private static Class<?> collectorClass;
    private static Object collectorsToList;
    private ReflectionUtils reflectionUtils;

    ModuleReaderProxy(ModuleRef moduleRef) throws IOException {
        try {
            this.reflectionUtils = moduleRef.reflectionUtils;
            if (collectorClass == null || collectorsToList == null) {
                collectorClass = this.reflectionUtils.classForNameOrNull("java.util.stream.Collector");
                Class<?> collectorsClass = this.reflectionUtils.classForNameOrNull("java.util.stream.Collectors");
                if (collectorsClass != null) {
                    collectorsToList = this.reflectionUtils.invokeStaticMethod(true, collectorsClass, "toList");
                }
            }
            this.moduleReader = (AutoCloseable)this.reflectionUtils.invokeMethod(true, moduleRef.getReference(), "open");
            if (this.moduleReader == null) {
                throw new IllegalArgumentException("moduleReference.open() should not return null");
            }
        }
        catch (SecurityException e) {
            throw new IOException("Could not open module " + moduleRef.getName(), e);
        }
    }

    @Override
    public void close() {
        try {
            this.moduleReader.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public List<String> list() throws SecurityException {
        if (collectorsToList == null) {
            throw new IllegalArgumentException("Could not call Collectors.toList()");
        }
        Object resourcesStream = this.reflectionUtils.invokeMethod(true, this.moduleReader, "list");
        if (resourcesStream == null) {
            throw new IllegalArgumentException("Could not call moduleReader.list()");
        }
        Object resourcesList = this.reflectionUtils.invokeMethod(true, resourcesStream, "collect", collectorClass, collectorsToList);
        if (resourcesList == null) {
            throw new IllegalArgumentException("Could not call moduleReader.list().collect(Collectors.toList())");
        }
        List resourcesListTyped = (List)resourcesList;
        return resourcesListTyped;
    }

    public InputStream open(String path) throws SecurityException {
        Object optionalInputStream = this.reflectionUtils.invokeMethod(true, this.moduleReader, "open", String.class, path);
        if (optionalInputStream == null) {
            throw new IllegalArgumentException("Got null result from ModuleReader#open for path " + path);
        }
        InputStream inputStream = (InputStream)this.reflectionUtils.invokeMethod(true, optionalInputStream, "get");
        if (inputStream == null) {
            throw new IllegalArgumentException("Got null result from ModuleReader#open(String)#get()");
        }
        return inputStream;
    }

    public ByteBuffer read(String path) throws SecurityException, OutOfMemoryError {
        Object optionalByteBuffer = this.reflectionUtils.invokeMethod(true, this.moduleReader, "read", String.class, path);
        if (optionalByteBuffer == null) {
            throw new IllegalArgumentException("Got null result from ModuleReader#read(String)");
        }
        ByteBuffer byteBuffer = (ByteBuffer)this.reflectionUtils.invokeMethod(true, optionalByteBuffer, "get");
        if (byteBuffer == null) {
            throw new IllegalArgumentException("Got null result from ModuleReader#read(String).get()");
        }
        return byteBuffer;
    }

    public void release(ByteBuffer byteBuffer) {
        this.reflectionUtils.invokeMethod(true, this.moduleReader, "release", ByteBuffer.class, byteBuffer);
    }

    public URI find(String path) {
        Object optionalURI = this.reflectionUtils.invokeMethod(true, this.moduleReader, "find", String.class, path);
        if (optionalURI == null) {
            throw new IllegalArgumentException("Got null result from ModuleReader#find(String)");
        }
        URI uri = (URI)this.reflectionUtils.invokeMethod(true, optionalURI, "get");
        if (uri == null) {
            throw new IllegalArgumentException("Got null result from ModuleReader#find(String).get()");
        }
        return uri;
    }
}

