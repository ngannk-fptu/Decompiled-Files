/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.tools.ForwardingJavaFileObject;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class HookedJavaFileObject
extends ForwardingJavaFileObject<JavaFileObject> {
    protected final BatchFilerImpl _filer;
    protected final String _fileName;
    private boolean _closed = false;
    private String _typeName;

    public HookedJavaFileObject(JavaFileObject fileObject, String fileName, String typeName, BatchFilerImpl filer) {
        super(fileObject);
        this._filer = filer;
        this._fileName = fileName;
        this._typeName = typeName;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return new ForwardingOutputStream(super.openOutputStream());
    }

    @Override
    public Writer openWriter() throws IOException {
        return new ForwardingWriter(super.openWriter());
    }

    protected void closed() {
        block9: {
            if (this._closed) break block9;
            this._closed = true;
            switch (this.getKind()) {
                case SOURCE: {
                    CompilationUnit unit = new CompilationUnit(null, this._fileName, null, null, this._filer._env.shouldIgnoreOptionalProblems(this._fileName.toCharArray()), null);
                    this._filer.addNewUnit(unit);
                    break;
                }
                case CLASS: {
                    char[] name;
                    ReferenceBinding type;
                    ClassFileReader binaryType = null;
                    try {
                        binaryType = ClassFileReader.read(this._fileName);
                    }
                    catch (ClassFormatException classFormatException) {
                        ReferenceBinding type2 = this._filer._env._compiler.lookupEnvironment.getType(CharOperation.splitOn('.', this._typeName.toCharArray()));
                        if (type2 != null) {
                            this._filer.addNewClassFile(type2);
                        }
                    }
                    catch (IOException iOException) {}
                    if (binaryType == null || (type = this._filer._env._compiler.lookupEnvironment.getType(CharOperation.splitOn('/', name = binaryType.getName()))) == null || !type.isValidBinding()) break;
                    if (type.isBinaryBinding()) {
                        this._filer.addNewClassFile(type);
                        break;
                    }
                    BinaryTypeBinding binaryBinding = new BinaryTypeBinding(type.getPackage(), binaryType, this._filer._env._compiler.lookupEnvironment, true);
                    if (binaryBinding == null) break;
                    this._filer.addNewClassFile(binaryBinding);
                    break;
                }
            }
        }
    }

    private class ForwardingOutputStream
    extends OutputStream {
        private final OutputStream _os;

        ForwardingOutputStream(OutputStream os) {
            this._os = os;
        }

        @Override
        public void close() throws IOException {
            this._os.close();
            HookedJavaFileObject.this.closed();
        }

        @Override
        public void flush() throws IOException {
            this._os.flush();
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this._os.write(b, off, len);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this._os.write(b);
        }

        @Override
        public void write(int b) throws IOException {
            this._os.write(b);
        }

        protected Object clone() throws CloneNotSupportedException {
            return new ForwardingOutputStream(this._os);
        }

        public int hashCode() {
            return this._os.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ForwardingOutputStream other = (ForwardingOutputStream)obj;
            return !(this._os == null ? other._os != null : !this._os.equals(other._os));
        }

        public String toString() {
            return "ForwardingOutputStream wrapping " + this._os.toString();
        }
    }

    private class ForwardingWriter
    extends Writer {
        private final Writer _w;

        ForwardingWriter(Writer w) {
            this._w = w;
        }

        @Override
        public Writer append(char c) throws IOException {
            return this._w.append(c);
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) throws IOException {
            return this._w.append(csq, start, end);
        }

        @Override
        public Writer append(CharSequence csq) throws IOException {
            return this._w.append(csq);
        }

        @Override
        public void close() throws IOException {
            this._w.close();
            HookedJavaFileObject.this.closed();
        }

        @Override
        public void flush() throws IOException {
            this._w.flush();
        }

        @Override
        public void write(char[] cbuf) throws IOException {
            this._w.write(cbuf);
        }

        @Override
        public void write(int c) throws IOException {
            this._w.write(c);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            this._w.write(str, off, len);
        }

        @Override
        public void write(String str) throws IOException {
            this._w.write(str);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            this._w.write(cbuf, off, len);
        }

        protected Object clone() throws CloneNotSupportedException {
            return new ForwardingWriter(this._w);
        }

        public int hashCode() {
            return this._w.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ForwardingWriter other = (ForwardingWriter)obj;
            return !(this._w == null ? other._w != null : !this._w.equals(other._w));
        }

        public String toString() {
            return "ForwardingWriter wrapping " + this._w.toString();
        }
    }
}

