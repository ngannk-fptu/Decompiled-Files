/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.Objects;
import org.apache.lucene.store.ByteBufferIndexInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.util.Constants;

public class MMapDirectory
extends FSDirectory {
    private boolean useUnmapHack = UNMAP_SUPPORTED;
    private boolean preload;
    public static final int DEFAULT_MAX_BUFF = Constants.JRE_IS_64BIT ? 0x40000000 : 0x10000000;
    final int chunkSizePower;
    public static final boolean UNMAP_SUPPORTED;
    public static final String UNMAP_NOT_SUPPORTED_REASON;
    private static final ByteBufferIndexInput.BufferCleaner CLEANER;

    public MMapDirectory(File path, LockFactory lockFactory) throws IOException {
        this(path, lockFactory, DEFAULT_MAX_BUFF);
    }

    public MMapDirectory(File path) throws IOException {
        this(path, null);
    }

    public MMapDirectory(File path, LockFactory lockFactory, int maxChunkSize) throws IOException {
        super(path, lockFactory);
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("Maximum chunk size for mmap must be >0");
        }
        this.chunkSizePower = 31 - Integer.numberOfLeadingZeros(maxChunkSize);
        assert (this.chunkSizePower >= 0 && this.chunkSizePower <= 30);
    }

    public void setUseUnmap(boolean useUnmapHack) {
        if (useUnmapHack && !UNMAP_SUPPORTED) {
            throw new IllegalArgumentException(UNMAP_NOT_SUPPORTED_REASON);
        }
        this.useUnmapHack = useUnmapHack;
    }

    public boolean getUseUnmap() {
        return this.useUnmapHack;
    }

    public void setPreload(boolean preload) {
        this.preload = preload;
    }

    public boolean getPreload() {
        return this.preload;
    }

    public final int getMaxChunkSize() {
        return 1 << this.chunkSizePower;
    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        this.ensureOpen();
        Path path = this.directory.toPath().resolve(name);
        try (FileChannel c = FileChannel.open(path, StandardOpenOption.READ);){
            String resourceDescription = "MMapIndexInput(path=\"" + path.toString() + "\")";
            boolean useUnmap = this.getUseUnmap();
            ByteBufferIndexInput byteBufferIndexInput = ByteBufferIndexInput.newInstance(resourceDescription, this.map(resourceDescription, c, 0L, c.size()), c.size(), this.chunkSizePower, useUnmap ? CLEANER : null, useUnmap);
            return byteBufferIndexInput;
        }
    }

    final ByteBuffer[] map(String resourceDescription, FileChannel fc, long offset, long length) throws IOException {
        if (length >>> this.chunkSizePower >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("RandomAccessFile too big for chunk size: " + resourceDescription);
        }
        long chunkSize = 1L << this.chunkSizePower;
        int nrBuffers = (int)(length >>> this.chunkSizePower) + 1;
        ByteBuffer[] buffers = new ByteBuffer[nrBuffers];
        long bufferStart = 0L;
        for (int bufNr = 0; bufNr < nrBuffers; ++bufNr) {
            MappedByteBuffer buffer;
            int bufSize = (int)(length > bufferStart + chunkSize ? chunkSize : length - bufferStart);
            try {
                buffer = fc.map(FileChannel.MapMode.READ_ONLY, offset + bufferStart, bufSize);
            }
            catch (IOException ioe) {
                throw this.convertMapFailedIOException(ioe, resourceDescription, bufSize);
            }
            if (this.preload) {
                buffer.load();
            }
            buffers[bufNr] = buffer;
            bufferStart += (long)bufSize;
        }
        return buffers;
    }

    private IOException convertMapFailedIOException(IOException ioe, String resourceDescription, int bufSize) {
        Throwable originalCause;
        String originalMessage;
        if (ioe.getCause() instanceof OutOfMemoryError) {
            originalMessage = "Map failed";
            originalCause = null;
        } else {
            originalMessage = ioe.getMessage();
            originalCause = ioe.getCause();
        }
        String moreInfo = !Constants.JRE_IS_64BIT ? "MMapDirectory should only be used on 64bit platforms, because the address space on 32bit operating systems is too small. " : (Constants.WINDOWS ? "Windows is unfortunately very limited on virtual address space. If your index size is several hundred Gigabytes, consider changing to Linux. " : (Constants.LINUX ? "Please review 'ulimit -v', 'ulimit -m' (both should return 'unlimited'), and 'sysctl vm.max_map_count'. " : "Please review 'ulimit -v', 'ulimit -m' (both should return 'unlimited'). "));
        IOException newIoe = new IOException(String.format(Locale.ENGLISH, "%s: %s [this may be caused by lack of enough unfragmented virtual address space or too restrictive virtual memory limits enforced by the operating system, preventing us to map a chunk of %d bytes. %sMore information: http://blog.thetaphi.de/2012/07/use-lucenes-mmapdirectory-on-64bit.html]", originalMessage, resourceDescription, bufSize, moreInfo), originalCause);
        newIoe.setStackTrace(ioe.getStackTrace());
        return newIoe;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Object unmapHackImpl() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            MethodHandle unmapper = lookup.findVirtual(unsafeClass, "invokeCleaner", MethodType.methodType(Void.TYPE, ByteBuffer.class));
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Object theUnsafe = f.get(null);
            return MMapDirectory.newBufferCleaner(ByteBuffer.class, unmapper.bindTo(theUnsafe));
        }
        catch (SecurityException se) {
            try {
                throw se;
                catch (ReflectiveOperationException | RuntimeException e) {
                    Class<?> directBufferClass = Class.forName("java.nio.DirectByteBuffer");
                    Method m = directBufferClass.getMethod("cleaner", new Class[0]);
                    m.setAccessible(true);
                    MethodHandle directBufferCleanerMethod = lookup.unreflect(m);
                    TypeDescriptor.OfField cleanerClass = directBufferCleanerMethod.type().returnType();
                    MethodHandle cleanMethod = lookup.findVirtual((Class<?>)cleanerClass, "clean", MethodType.methodType(Void.TYPE));
                    MethodHandle nonNullTest = lookup.findStatic(MMapDirectory.class, "nonNull", MethodType.methodType(Boolean.TYPE, Object.class)).asType(MethodType.methodType(Boolean.TYPE, cleanerClass));
                    MethodHandle noop = MethodHandles.dropArguments(MethodHandles.constant(Object.class, null).asType(MethodType.methodType(Void.TYPE)), 0, new Class[]{cleanerClass});
                    MethodHandle unmapper = MethodHandles.filterReturnValue(directBufferCleanerMethod, MethodHandles.guardWithTest(nonNullTest, cleanMethod, noop)).asType(MethodType.methodType(Void.TYPE, ByteBuffer.class));
                    return MMapDirectory.newBufferCleaner(directBufferClass, unmapper);
                }
            }
            catch (SecurityException se2) {
                return "Unmapping is not supported, because not all required permissions are given to the Lucene JAR file: " + se2 + " [Please grant at least the following permissions: RuntimePermission(\"accessClassInPackage.sun.misc\")  and ReflectPermission(\"suppressAccessChecks\")]";
            }
            catch (ReflectiveOperationException | RuntimeException e) {
                return "Unmapping is not supported on this platform, because internal Java APIs are not compatible with this Lucene version: " + e;
            }
        }
    }

    static boolean nonNull(Object o) {
        return o != null;
    }

    private static ByteBufferIndexInput.BufferCleaner newBufferCleaner(final Class<?> unmappableBufferClass, final MethodHandle unmapper) {
        assert (Objects.equals(MethodType.methodType(Void.TYPE, ByteBuffer.class), unmapper.type()));
        return new ByteBufferIndexInput.BufferCleaner(){

            @Override
            public void freeBuffer(ByteBufferIndexInput parent, final ByteBuffer buffer) throws IOException {
                if (!buffer.isDirect()) {
                    throw new IllegalArgumentException("unmapping only works with direct buffers");
                }
                if (!unmappableBufferClass.isInstance(buffer)) {
                    throw new IllegalArgumentException("buffer is not an instance of " + unmappableBufferClass.getName());
                }
                Throwable error = AccessController.doPrivileged(new PrivilegedAction<Throwable>(){

                    @Override
                    public Throwable run() {
                        try {
                            unmapper.invokeExact(buffer);
                            return null;
                        }
                        catch (Throwable t) {
                            return t;
                        }
                    }
                });
                if (error != null) {
                    throw new IOException("Unable to unmap the mapped buffer: " + parent, error);
                }
            }
        };
    }

    @Override
    public Directory.IndexInputSlicer createSlicer(String name, IOContext context) throws IOException {
        final ByteBufferIndexInput full = (ByteBufferIndexInput)this.openInput(name, context);
        return new Directory.IndexInputSlicer(){

            @Override
            public IndexInput openSlice(String sliceDescription, long offset, long length) throws IOException {
                MMapDirectory.this.ensureOpen();
                return full.slice(sliceDescription, offset, length);
            }

            @Override
            public IndexInput openFullSlice() throws IOException {
                MMapDirectory.this.ensureOpen();
                return full.clone();
            }

            @Override
            public void close() throws IOException {
                full.close();
            }
        };
    }

    ByteBuffer[] map(RandomAccessFile raf, long offset, long length) throws IOException {
        if (length >>> this.chunkSizePower >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("RandomAccessFile too big for chunk size: " + raf.toString());
        }
        long chunkSize = 1L << this.chunkSizePower;
        int nrBuffers = (int)(length >>> this.chunkSizePower) + 1;
        ByteBuffer[] buffers = new ByteBuffer[nrBuffers];
        long bufferStart = 0L;
        FileChannel rafc = raf.getChannel();
        for (int bufNr = 0; bufNr < nrBuffers; ++bufNr) {
            int bufSize = (int)(length > bufferStart + chunkSize ? chunkSize : length - bufferStart);
            buffers[bufNr] = rafc.map(FileChannel.MapMode.READ_ONLY, offset + bufferStart, bufSize);
            bufferStart += (long)bufSize;
        }
        return buffers;
    }

    static {
        Object hack = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                return MMapDirectory.unmapHackImpl();
            }
        });
        if (hack instanceof ByteBufferIndexInput.BufferCleaner) {
            CLEANER = (ByteBufferIndexInput.BufferCleaner)hack;
            UNMAP_SUPPORTED = true;
            UNMAP_NOT_SUPPORTED_REASON = null;
        } else {
            CLEANER = null;
            UNMAP_SUPPORTED = false;
            UNMAP_NOT_SUPPORTED_REASON = hack.toString();
        }
    }
}

