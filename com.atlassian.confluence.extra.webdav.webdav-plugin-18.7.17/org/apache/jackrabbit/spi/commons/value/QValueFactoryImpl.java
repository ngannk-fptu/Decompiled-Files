/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.value;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.commons.value.AbstractQValue;
import org.apache.jackrabbit.spi.commons.value.AbstractQValueFactory;
import org.apache.jackrabbit.util.TransientFileFactory;

public class QValueFactoryImpl
extends AbstractQValueFactory {
    private static final QValueFactory INSTANCE = new QValueFactoryImpl();

    protected QValueFactoryImpl() {
    }

    public static QValueFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public QValue create(byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new BinaryQValue(value);
    }

    @Override
    public QValue create(InputStream value) throws IOException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new BinaryQValue(value);
    }

    @Override
    public QValue create(File value) throws IOException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot create QValue from null value.");
        }
        return new BinaryQValue(value);
    }

    private static class BinaryQValue
    extends AbstractQValue
    implements Serializable {
        private static final Object DUMMY_VALUE = new Serializable(){
            private static final long serialVersionUID = 2849470089518940117L;
        };
        private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
        private static final int MAX_BUFFER_SIZE = 65536;
        private transient File file;
        private transient boolean temp;
        private byte[] buffer = EMPTY_BYTE_ARRAY;

        private BinaryQValue(InputStream in) throws IOException {
            this(in, true);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private BinaryQValue(InputStream in, boolean temp) throws IOException {
            super(DUMMY_VALUE, 2);
            byte[] spoolBuffer = new byte[8192];
            int len = 0;
            OutputStream out = null;
            File spoolFile = null;
            try {
                int read;
                while ((read = in.read(spoolBuffer)) > 0) {
                    if (out != null) {
                        out.write(spoolBuffer, 0, read);
                        len += read;
                        continue;
                    }
                    if (len + read > 65536) {
                        TransientFileFactory fileFactory = TransientFileFactory.getInstance();
                        spoolFile = fileFactory.createTransientFile("bin", null, null);
                        out = new FileOutputStream(spoolFile);
                        out.write(this.buffer, 0, len);
                        out.write(spoolBuffer, 0, read);
                        this.buffer = null;
                        len += read;
                        continue;
                    }
                    byte[] newBuffer = new byte[len + read];
                    System.arraycopy(this.buffer, 0, newBuffer, 0, len);
                    System.arraycopy(spoolBuffer, 0, newBuffer, len, read);
                    this.buffer = newBuffer;
                    len += read;
                }
            }
            finally {
                in.close();
                if (out != null) {
                    out.close();
                }
            }
            this.file = spoolFile;
            this.temp = temp;
        }

        private BinaryQValue(byte[] bytes) {
            super(DUMMY_VALUE, 2);
            this.buffer = bytes;
            this.file = null;
            this.temp = false;
        }

        private BinaryQValue(File file) throws IOException {
            super(DUMMY_VALUE, 2);
            String path = file.getCanonicalPath();
            if (!file.isFile()) {
                throw new IOException(path + ": the specified file does not exist");
            }
            if (!file.canRead()) {
                throw new IOException(path + ": the specified file can not be read");
            }
            this.file = file;
            this.temp = false;
        }

        @Override
        public long getLength() {
            if (this.file != null) {
                if (this.file.exists()) {
                    return this.file.length();
                }
                return -1L;
            }
            return this.buffer.length;
        }

        @Override
        public InputStream getStream() throws RepositoryException {
            if (this.file != null) {
                try {
                    return new FileInputStream(this.file);
                }
                catch (FileNotFoundException fnfe) {
                    throw new RepositoryException("file backing binary value not found", fnfe);
                }
            }
            return new ByteArrayInputStream(this.buffer);
        }

        @Override
        public Name getName() throws RepositoryException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Path getPath() throws RepositoryException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void discard() {
            if (!this.temp) {
                return;
            }
            if (this.file != null) {
                this.file.delete();
            } else if (this.buffer != null) {
                this.buffer = EMPTY_BYTE_ARRAY;
            }
        }

        @Override
        public String toString() {
            if (this.file != null) {
                return this.file.toString();
            }
            return this.buffer.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof BinaryQValue) {
                BinaryQValue other = (BinaryQValue)obj;
                return (this.file == null ? other.file == null : this.file.equals(other.file)) && Arrays.equals(this.buffer, other.buffer);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeBoolean(this.file != null);
            if (this.file != null) {
                int bytes;
                byte[] buffer = new byte[4096];
                FileInputStream stream = new FileInputStream(this.file);
                while ((bytes = ((InputStream)stream).read(buffer)) >= 0) {
                    if (bytes <= 0) continue;
                    out.writeInt(bytes);
                    out.write(buffer, 0, bytes);
                }
                out.writeInt(0);
                ((InputStream)stream).close();
            }
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            boolean hasFile = in.readBoolean();
            if (hasFile) {
                this.file = File.createTempFile("binary-qvalue", "bin");
                FileOutputStream out = new FileOutputStream(this.file);
                byte[] buffer = new byte[4096];
                int bytes = in.readInt();
                while (bytes > 0) {
                    if (buffer.length < bytes) {
                        buffer = new byte[bytes];
                    }
                    in.readFully(buffer, 0, bytes);
                    ((OutputStream)out).write(buffer, 0, bytes);
                    bytes = in.readInt();
                }
                ((OutputStream)out).close();
            }
            this.temp = true;
        }
    }
}

