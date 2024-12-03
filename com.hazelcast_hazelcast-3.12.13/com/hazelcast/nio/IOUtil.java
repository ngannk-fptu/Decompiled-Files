/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelOption;
import com.hazelcast.internal.networking.ChannelOptions;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.ClassNameFilter;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@PrivateApi
public final class IOUtil {
    public static final byte PRIMITIVE_TYPE_BOOLEAN = 1;
    public static final byte PRIMITIVE_TYPE_BYTE = 2;
    public static final byte PRIMITIVE_TYPE_SHORT = 3;
    public static final byte PRIMITIVE_TYPE_INTEGER = 4;
    public static final byte PRIMITIVE_TYPE_LONG = 5;
    public static final byte PRIMITIVE_TYPE_FLOAT = 6;
    public static final byte PRIMITIVE_TYPE_DOUBLE = 7;
    public static final byte PRIMITIVE_TYPE_UTF = 8;

    private IOUtil() {
    }

    public static void compactOrClear(ByteBuffer bb) {
        if (bb.hasRemaining()) {
            bb.compact();
        } else {
            bb.clear();
        }
    }

    public static ByteBuffer newByteBuffer(int bufferSize, boolean direct) {
        if (direct) {
            return ByteBuffer.allocateDirect(bufferSize);
        }
        return ByteBuffer.allocate(bufferSize);
    }

    public static void writeByteArray(ObjectDataOutput out, byte[] value) throws IOException {
        int size = value == null ? 0 : value.length;
        out.writeInt(size);
        if (size > 0) {
            out.write(value);
        }
    }

    public static byte[] readByteArray(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        if (size == 0) {
            return null;
        }
        byte[] b = new byte[size];
        in.readFully(b);
        return b;
    }

    public static void writeObject(ObjectDataOutput out, Object object) throws IOException {
        boolean isBinary = object instanceof Data;
        out.writeBoolean(isBinary);
        if (isBinary) {
            out.writeData((Data)object);
        } else {
            out.writeObject(object);
        }
    }

    public static <T> T readObject(ObjectDataInput in) throws IOException {
        boolean isBinary = in.readBoolean();
        if (isBinary) {
            return (T)in.readData();
        }
        return in.readObject();
    }

    public static boolean readFullyOrNothing(InputStream in, byte[] buffer) throws IOException {
        int count;
        int bytesRead = 0;
        do {
            if ((count = in.read(buffer, bytesRead, buffer.length - bytesRead)) >= 0) continue;
            if (bytesRead == 0) {
                return false;
            }
            throw new EOFException();
        } while ((bytesRead += count) < buffer.length);
        return true;
    }

    public static void readFully(InputStream in, byte[] buffer) throws IOException {
        if (!IOUtil.readFullyOrNothing(in, buffer)) {
            throw new EOFException();
        }
    }

    public static ObjectInputStream newObjectInputStream(ClassLoader classLoader, ClassNameFilter classFilter, InputStream in) throws IOException {
        return new ClassLoaderAwareObjectInputStream(classLoader, classFilter, in);
    }

    public static OutputStream newOutputStream(final ByteBuffer dst) {
        return new OutputStream(){

            @Override
            public void write(int b) {
                dst.put((byte)b);
            }

            @Override
            public void write(byte[] bytes, int off, int len) {
                dst.put(bytes, off, len);
            }
        };
    }

    public static InputStream newInputStream(final ByteBuffer src) {
        return new InputStream(){

            @Override
            public int read() {
                if (!src.hasRemaining()) {
                    return -1;
                }
                return src.get() & 0xFF;
            }

            @Override
            public int read(byte[] bytes, int off, int len) {
                if (!src.hasRemaining()) {
                    return -1;
                }
                len = Math.min(len, src.remaining());
                src.get(bytes, off, len);
                return len;
            }
        };
    }

    public static int copyToHeapBuffer(ByteBuffer src, ByteBuffer dst) {
        if (src == null) {
            return 0;
        }
        int n = Math.min(src.remaining(), dst.remaining());
        if (n > 0) {
            if (n < 16) {
                for (int i = 0; i < n; ++i) {
                    dst.put(src.get());
                }
            } else {
                int srcPosition = src.position();
                int destPosition = dst.position();
                System.arraycopy(src.array(), srcPosition, dst.array(), destPosition, n);
                src.position(srcPosition + n);
                dst.position(destPosition + n);
            }
        }
        return n;
    }

    public static byte[] compress(byte[] input) {
        if (input.length == 0) {
            return new byte[0];
        }
        int len = Math.max(input.length / 10, 10);
        Deflater compressor = new Deflater();
        compressor.setLevel(1);
        compressor.setInput(input);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(len);
        byte[] buf = new byte[len];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        compressor.end();
        return bos.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData) {
        if (compressedData.length == 0) {
            return compressedData;
        }
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
        byte[] buf = new byte[1024];
        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(buf);
                bos.write(buf, 0, count);
            }
            catch (DataFormatException e) {
                Logger.getLogger(IOUtil.class).finest("Decompression failed", e);
            }
        }
        inflater.end();
        return bos.toByteArray();
    }

    public static void writeAttributeValue(Object value, ObjectDataOutput out) throws IOException {
        Class<?> type = value.getClass();
        if (type.equals(Boolean.class)) {
            out.writeByte(1);
            out.writeBoolean((Boolean)value);
        } else if (type.equals(Byte.class)) {
            out.writeByte(2);
            out.writeByte(((Byte)value).byteValue());
        } else if (type.equals(Short.class)) {
            out.writeByte(3);
            out.writeShort(((Short)value).shortValue());
        } else if (type.equals(Integer.class)) {
            out.writeByte(4);
            out.writeInt((Integer)value);
        } else if (type.equals(Long.class)) {
            out.writeByte(5);
            out.writeLong((Long)value);
        } else if (type.equals(Float.class)) {
            out.writeByte(6);
            out.writeFloat(((Float)value).floatValue());
        } else if (type.equals(Double.class)) {
            out.writeByte(7);
            out.writeDouble((Double)value);
        } else if (type.equals(String.class)) {
            out.writeByte(8);
            out.writeUTF((String)value);
        } else {
            throw new IllegalStateException("Illegal attribute type ID found");
        }
    }

    public static Object readAttributeValue(ObjectDataInput in) throws IOException {
        byte type = in.readByte();
        switch (type) {
            case 1: {
                return in.readBoolean();
            }
            case 2: {
                return in.readByte();
            }
            case 3: {
                return in.readShort();
            }
            case 4: {
                return in.readInt();
            }
            case 5: {
                return in.readLong();
            }
            case 6: {
                return Float.valueOf(in.readFloat());
            }
            case 7: {
                return in.readDouble();
            }
            case 8: {
                return in.readUTF();
            }
        }
        throw new IllegalStateException("Illegal attribute type ID found");
    }

    public static void closeResource(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        }
        catch (IOException e) {
            Logger.getLogger(IOUtil.class).finest("closeResource failed", e);
        }
    }

    public static void close(Connection conn, String reason) {
        if (conn == null) {
            return;
        }
        try {
            conn.close(reason, null);
        }
        catch (Throwable e) {
            Logger.getLogger(IOUtil.class).finest("closeResource failed", e);
        }
    }

    public static void close(ServerSocket serverSocket) {
        if (serverSocket == null) {
            return;
        }
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            Logger.getLogger(IOUtil.class).finest("closeResource failed", e);
        }
    }

    public static void close(Socket socket) {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
        }
        catch (IOException e) {
            Logger.getLogger(IOUtil.class).finest("closeResource failed", e);
        }
    }

    public static void deleteQuietly(File f) {
        try {
            IOUtil.delete(f);
        }
        catch (Exception e) {
            EmptyStatement.ignore(e);
        }
    }

    public static void delete(File f) {
        if (!f.exists()) {
            return;
        }
        File[] subFiles = f.listFiles();
        if (subFiles != null) {
            for (File sf : subFiles) {
                IOUtil.delete(sf);
            }
        }
        if (!f.delete()) {
            throw new HazelcastException("Failed to delete " + f);
        }
    }

    public static void rename(File fileNow, File fileToBe) {
        if (fileNow.renameTo(fileToBe)) {
            return;
        }
        if (!fileNow.exists()) {
            throw new HazelcastException(String.format("Failed to rename %s to %s because %s doesn't exist.", fileNow, fileToBe, fileNow));
        }
        if (!fileToBe.exists()) {
            throw new HazelcastException(String.format("Failed to rename %s to %s even though %s doesn't exist.", fileNow, fileToBe, fileToBe));
        }
        if (!fileToBe.delete()) {
            throw new HazelcastException(String.format("Failed to rename %s to %s. %s exists and could not be deleted.", fileNow, fileToBe, fileToBe));
        }
        if (!fileNow.renameTo(fileToBe)) {
            throw new HazelcastException(String.format("Failed to rename %s to %s even after deleting %s.", fileNow, fileToBe, fileToBe));
        }
    }

    public static String toFileName(String name) {
        return name.replaceAll("[:\\\\/*\"?|<>',]", "_");
    }

    public static String getPath(String ... parts) {
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("Parts is null or empty.");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; ++i) {
            boolean hasMore;
            String part = parts[i];
            builder.append(part);
            boolean bl = hasMore = i < parts.length - 1;
            if (part.endsWith(File.separator) || !hasMore) continue;
            builder.append(File.separator);
        }
        return builder.toString();
    }

    public static File getFileFromResources(String resourceFileName) {
        try {
            URL resource = IOUtil.class.getClassLoader().getResource(resourceFileName);
            return new File(resource.toURI());
        }
        catch (Exception e) {
            throw new HazelcastException("Could not find resource file " + resourceFileName, e);
        }
    }

    public static InputStream getFileFromResourcesAsStream(String resourceFileName) {
        try {
            return IOUtil.class.getClassLoader().getResourceAsStream(resourceFileName);
        }
        catch (Exception e) {
            throw new HazelcastException("Could not find resource file " + resourceFileName, e);
        }
    }

    public static void touch(File file) {
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                fos = new FileOutputStream(file);
            }
            if (!file.setLastModified(System.currentTimeMillis())) {
                throw new HazelcastException("Could not touch file " + file.getAbsolutePath());
            }
        }
        catch (IOException e) {
            try {
                throw ExceptionUtil.rethrow(e);
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(fos);
                throw throwable;
            }
        }
        IOUtil.closeResource(fos);
    }

    public static void copy(File source, File target) {
        if (!source.exists()) {
            throw new IllegalArgumentException("Source does not exist");
        }
        if (source.isDirectory()) {
            IOUtil.copyDirectory(source, target);
        } else {
            IOUtil.copyFile(source, target, -1L);
        }
    }

    public static void copy(InputStream source, File target) {
        if (!target.exists()) {
            throw new HazelcastException("The target file doesn't exist " + target.getAbsolutePath());
        }
        FileOutputStream out = null;
        try {
            int length;
            out = new FileOutputStream(target);
            byte[] buff = new byte[8192];
            while ((length = source.read(buff)) > 0) {
                out.write(buff, 0, length);
            }
        }
        catch (Exception e) {
            try {
                throw new HazelcastException("Error occurred while copying InputStream", e);
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(out);
                throw throwable;
            }
        }
        IOUtil.closeResource(out);
    }

    public static void copyFile(File source, File target, long sourceCount) {
        if (!source.exists()) {
            throw new IllegalArgumentException("Source does not exist " + source.getAbsolutePath());
        }
        if (!source.isFile()) {
            throw new IllegalArgumentException("Source is not a file " + source.getAbsolutePath());
        }
        if (!target.exists() && !target.mkdirs()) {
            throw new HazelcastException("Could not create the target directory " + target.getAbsolutePath());
        }
        File destination = target.isDirectory() ? new File(target, source.getName()) : target;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(destination);
            FileChannel inChannel = in.getChannel();
            FileChannel outChannel = out.getChannel();
            long transferCount = sourceCount > 0L ? sourceCount : inChannel.size();
            inChannel.transferTo(0L, transferCount, outChannel);
        }
        catch (Exception e) {
            try {
                throw new HazelcastException("Error occurred while copying file", e);
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(in);
                IOUtil.closeResource(out);
                throw throwable;
            }
        }
        IOUtil.closeResource(in);
        IOUtil.closeResource(out);
    }

    private static void copyDirectory(File source, File target) {
        if (target.exists() && !target.isDirectory()) {
            throw new IllegalArgumentException("Cannot copy source directory since the target already exists, but it is not a directory");
        }
        File targetSubDir = new File(target, source.getName());
        if (!targetSubDir.exists() && !targetSubDir.mkdirs()) {
            throw new HazelcastException("Could not create the target directory " + target);
        }
        File[] sourceFiles = source.listFiles();
        if (sourceFiles == null) {
            throw new HazelcastException("Error occurred while listing directory contents for copy");
        }
        for (File file : sourceFiles) {
            IOUtil.copy(file, targetSubDir);
        }
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            IOUtil.drainTo(is, os);
            byte[] byArray = os.toByteArray();
            return byArray;
        }
        finally {
            IOUtil.closeResource(os);
        }
    }

    public static void drainTo(InputStream input, OutputStream output) throws IOException {
        int n;
        byte[] buffer = new byte[1024];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }

    public static String toDebugString(String name, ByteBuffer byteBuffer) {
        return name + "(pos:" + byteBuffer.position() + " lim:" + byteBuffer.limit() + " remain:" + byteBuffer.remaining() + " cap:" + byteBuffer.capacity() + ")";
    }

    public static void setChannelOptions(Channel channel, EndpointConfig config) {
        ChannelOptions options = channel.options();
        options.setOption(ChannelOption.DIRECT_BUF, config.isSocketBufferDirect()).setOption(ChannelOption.TCP_NODELAY, config.isSocketTcpNoDelay()).setOption(ChannelOption.SO_KEEPALIVE, config.isSocketKeepAlive()).setOption(ChannelOption.SO_SNDBUF, config.getSocketSendBufferSizeKb() * 1024).setOption(ChannelOption.SO_RCVBUF, config.getSocketRcvBufferSizeKb() * 1024).setOption(ChannelOption.SO_LINGER, config.getSocketLingerSeconds());
    }

    private static final class ClassLoaderAwareObjectInputStream
    extends ObjectInputStream {
        private final ClassLoader classLoader;
        private final ClassNameFilter classFilter;

        private ClassLoaderAwareObjectInputStream(ClassLoader classLoader, ClassNameFilter classFilter, InputStream in) throws IOException {
            super(in);
            this.classLoader = classLoader;
            this.classFilter = classFilter;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
            String name = desc.getName();
            if (this.classFilter != null) {
                this.classFilter.filter(name);
            }
            return ClassLoaderUtil.loadClass(this.classLoader, name);
        }

        @Override
        protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
            ClassLoader theClassLoader = this.getClassLoader();
            if (theClassLoader == null) {
                return super.resolveProxyClass(interfaces);
            }
            ClassLoader nonPublicLoader = null;
            Class[] classObjs = new Class[interfaces.length];
            for (int i = 0; i < interfaces.length; ++i) {
                Class<?> cl = ClassLoaderUtil.loadClass(theClassLoader, interfaces[i]);
                if ((cl.getModifiers() & 1) == 0) {
                    if (nonPublicLoader != null) {
                        if (nonPublicLoader != cl.getClassLoader()) {
                            throw new IllegalAccessError("conflicting non-public interface class loaders");
                        }
                    } else {
                        nonPublicLoader = cl.getClassLoader();
                    }
                }
                classObjs[i] = cl;
            }
            try {
                return Proxy.getProxyClass(nonPublicLoader != null ? nonPublicLoader : theClassLoader, classObjs);
            }
            catch (IllegalArgumentException e) {
                throw new ClassNotFoundException(null, e);
            }
        }

        private ClassLoader getClassLoader() {
            ClassLoader theClassLoader = this.classLoader;
            if (theClassLoader == null) {
                theClassLoader = Thread.currentThread().getContextClassLoader();
            }
            return theClassLoader;
        }
    }
}

