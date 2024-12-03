/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import org.apache.lucene.store.Directory;

public final class IOUtils {
    public static final String UTF_8 = "UTF-8";
    public static final Charset CHARSET_UTF_8;
    private static final Method SUPPRESS_METHOD;

    private IOUtils() {
    }

    public static <E extends Exception> void closeWhileHandlingException(E priorException, Closeable ... objects) throws E, IOException {
        Throwable th = null;
        for (Closeable object : objects) {
            try {
                if (object == null) continue;
                object.close();
            }
            catch (Throwable t) {
                IOUtils.addSuppressed(priorException == null ? th : (Throwable)priorException, t);
                if (th != null) continue;
                th = t;
            }
        }
        if (priorException != null) {
            throw priorException;
        }
        if (th != null) {
            if (th instanceof IOException) {
                throw (IOException)th;
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            if (th instanceof Error) {
                throw (Error)th;
            }
            throw new RuntimeException(th);
        }
    }

    public static <E extends Exception> void closeWhileHandlingException(E priorException, Iterable<? extends Closeable> objects) throws E, IOException {
        Throwable th = null;
        for (Closeable closeable : objects) {
            try {
                if (closeable == null) continue;
                closeable.close();
            }
            catch (Throwable t) {
                IOUtils.addSuppressed(priorException == null ? th : (Throwable)priorException, t);
                if (th != null) continue;
                th = t;
            }
        }
        if (priorException != null) {
            throw priorException;
        }
        if (th != null) {
            if (th instanceof IOException) {
                throw (IOException)th;
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            if (th instanceof Error) {
                throw (Error)th;
            }
            throw new RuntimeException(th);
        }
    }

    public static void close(Closeable ... objects) throws IOException {
        Throwable th = null;
        for (Closeable object : objects) {
            try {
                if (object == null) continue;
                object.close();
            }
            catch (Throwable t) {
                IOUtils.addSuppressed(th, t);
                if (th != null) continue;
                th = t;
            }
        }
        if (th != null) {
            if (th instanceof IOException) {
                throw (IOException)th;
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            if (th instanceof Error) {
                throw (Error)th;
            }
            throw new RuntimeException(th);
        }
    }

    public static void close(Iterable<? extends Closeable> objects) throws IOException {
        Throwable th = null;
        for (Closeable closeable : objects) {
            try {
                if (closeable == null) continue;
                closeable.close();
            }
            catch (Throwable t) {
                IOUtils.addSuppressed(th, t);
                if (th != null) continue;
                th = t;
            }
        }
        if (th != null) {
            if (th instanceof IOException) {
                throw (IOException)th;
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            if (th instanceof Error) {
                throw (Error)th;
            }
            throw new RuntimeException(th);
        }
    }

    public static void closeWhileHandlingException(Closeable ... objects) {
        for (Closeable object : objects) {
            try {
                if (object == null) continue;
                object.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public static void closeWhileHandlingException(Iterable<? extends Closeable> objects) {
        for (Closeable closeable : objects) {
            try {
                if (closeable == null) continue;
                closeable.close();
            }
            catch (Throwable throwable) {}
        }
    }

    private static final void addSuppressed(Throwable exception, Throwable suppressed) {
        if (SUPPRESS_METHOD != null && exception != null && suppressed != null) {
            try {
                SUPPRESS_METHOD.invoke((Object)exception, suppressed);
            }
            catch (Exception exception2) {
                // empty catch block
            }
        }
    }

    public static Reader getDecodingReader(InputStream stream, Charset charSet) {
        CharsetDecoder charSetDecoder = charSet.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        return new BufferedReader(new InputStreamReader(stream, charSetDecoder));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Reader getDecodingReader(File file, Charset charSet) throws IOException {
        Reader reader;
        block3: {
            FileInputStream stream = null;
            boolean success = false;
            try {
                stream = new FileInputStream(file);
                Reader reader2 = IOUtils.getDecodingReader(stream, charSet);
                success = true;
                reader = reader2;
                if (success) break block3;
            }
            catch (Throwable throwable) {
                if (!success) {
                    IOUtils.close(stream);
                }
                throw throwable;
            }
            IOUtils.close(stream);
        }
        return reader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Reader getDecodingReader(Class<?> clazz, String resource, Charset charSet) throws IOException {
        Reader reader;
        block3: {
            InputStream stream = null;
            boolean success = false;
            try {
                stream = clazz.getResourceAsStream(resource);
                Reader reader2 = IOUtils.getDecodingReader(stream, charSet);
                success = true;
                reader = reader2;
                if (success) break block3;
            }
            catch (Throwable throwable) {
                if (!success) {
                    IOUtils.close(stream);
                }
                throw throwable;
            }
            IOUtils.close(stream);
        }
        return reader;
    }

    public static void deleteFilesIgnoringExceptions(Directory dir, String ... files) {
        for (String name : files) {
            try {
                dir.deleteFile(name);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void copy(File source, File target) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            int len;
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);
            byte[] buffer = new byte[8192];
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
        catch (Throwable throwable) {
            IOUtils.close(fis, fos);
            throw throwable;
        }
        IOUtils.close(fis, fos);
    }

    static {
        Method m;
        CHARSET_UTF_8 = Charset.forName(UTF_8);
        try {
            m = Throwable.class.getMethod("addSuppressed", Throwable.class);
        }
        catch (Exception e) {
            m = null;
        }
        SUPPRESS_METHOD = m;
    }
}

