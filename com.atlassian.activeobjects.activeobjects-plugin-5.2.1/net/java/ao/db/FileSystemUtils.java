/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 */
package net.java.ao.db;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Locale;

final class FileSystemUtils {
    private static final Supplier<Boolean> CASE_SENSITIVE_SUPPLIER = Suppliers.memoize((Supplier)new Supplier<Boolean>(){

        public Boolean get() {
            try {
                File tmp = File.createTempFile("active_objects", "_case_tmp");
                tmp.deleteOnExit();
                File tmpWithUpperCaseName = new File(tmp.getParent(), FileSystemUtils.toUpperCase(tmp.getName()));
                tmpWithUpperCaseName.deleteOnExit();
                if (!tmpWithUpperCaseName.exists()) {
                    return false;
                }
                return !FileSystemUtils.sameContent(tmp, tmpWithUpperCaseName);
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    });
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    FileSystemUtils() {
    }

    static boolean isCaseSensitive() {
        return (Boolean)CASE_SENSITIVE_SUPPLIER.get();
    }

    private static boolean sameContent(File tmp, File tmpWithUpperCaseName) throws IOException {
        String data = FileSystemUtils.class.getName();
        FileSystemUtils.write(tmp, data);
        return FileSystemUtils.read(tmpWithUpperCaseName).equals(data);
    }

    private static String toUpperCase(String name) {
        return name.toUpperCase(Locale.US);
    }

    private static void write(File file, CharSequence data) throws IOException {
        String str = data == null ? null : data.toString();
        FileSystemUtils.writeStringToFile(file, str);
    }

    private static void writeStringToFile(File file, String data) throws IOException {
        FileOutputStream out = null;
        try {
            out = FileSystemUtils.openOutputStream(file);
            ((OutputStream)out).write(data.getBytes());
        }
        finally {
            FileSystemUtils.closeQuietly(out);
        }
    }

    private static String read(File file) throws IOException {
        FileInputStream in = null;
        try {
            in = FileSystemUtils.openInputStream(file);
            String string = FileSystemUtils.toString(in);
            return string;
        }
        finally {
            FileSystemUtils.closeQuietly(in);
        }
    }

    private static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    private static String toString(InputStream input) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        FileSystemUtils.copy(new InputStreamReader(input), sw);
        return sw.toString();
    }

    private static long copy(Reader input, Writer output) throws IOException {
        char[] buffer = new char[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }

    private static FileOutputStream openOutputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("File '" + file + "' could not be created");
            }
        }
        return new FileOutputStream(file);
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static final class StringBuilderWriter
    extends Writer
    implements Serializable {
        private final StringBuilder builder;

        public StringBuilderWriter() {
            this.builder = new StringBuilder();
        }

        public StringBuilderWriter(int capacity) {
            this.builder = new StringBuilder(capacity);
        }

        public StringBuilderWriter(StringBuilder builder) {
            this.builder = builder != null ? builder : new StringBuilder();
        }

        @Override
        public Writer append(char value) {
            this.builder.append(value);
            return this;
        }

        @Override
        public Writer append(CharSequence value) {
            this.builder.append(value);
            return this;
        }

        @Override
        public Writer append(CharSequence value, int start, int end) {
            this.builder.append(value, start, end);
            return this;
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
        }

        @Override
        public void write(String value) {
            if (value != null) {
                this.builder.append(value);
            }
        }

        @Override
        public void write(char[] value, int offset, int length) {
            if (value != null) {
                this.builder.append(value, offset, length);
            }
        }

        public StringBuilder getBuilder() {
            return this.builder;
        }

        public String toString() {
            return this.builder.toString();
        }
    }
}

