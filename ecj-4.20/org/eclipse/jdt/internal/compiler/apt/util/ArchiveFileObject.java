/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.internal.compiler.apt.util.Util;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;

public class ArchiveFileObject
implements JavaFileObject {
    protected String entryName;
    protected File file;
    protected ZipFile zipFile;
    protected Charset charset;

    public ArchiveFileObject(File file, String entryName, Charset charset) {
        this.entryName = entryName;
        this.file = file;
        this.charset = charset;
    }

    protected void finalize() throws Throwable {
        if (this.zipFile != null) {
            try {
                this.zipFile.close();
            }
            catch (IOException iOException) {}
        }
        super.finalize();
    }

    @Override
    public Modifier getAccessLevel() {
        if (this.getKind() != JavaFileObject.Kind.CLASS) {
            return null;
        }
        ClassFileReader reader = this.getClassReader();
        if (reader == null) {
            return null;
        }
        int accessFlags = reader.accessFlags();
        if ((accessFlags & 1) != 0) {
            return Modifier.PUBLIC;
        }
        if ((accessFlags & 0x400) != 0) {
            return Modifier.ABSTRACT;
        }
        if ((accessFlags & 0x10) != 0) {
            return Modifier.FINAL;
        }
        return null;
    }

    protected ClassFileReader getClassReader() {
        ClassFileReader reader = null;
        try {
            Throwable throwable = null;
            Object var3_4 = null;
            try (ZipFile zip = new ZipFile(this.file);){
                reader = ClassFileReader.read(zip, this.entryName);
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (ClassFormatException classFormatException) {
        }
        catch (IOException iOException) {}
        return reader;
    }

    @Override
    public JavaFileObject.Kind getKind() {
        String name = this.entryName.toLowerCase();
        if (name.endsWith(JavaFileObject.Kind.CLASS.extension)) {
            return JavaFileObject.Kind.CLASS;
        }
        if (name.endsWith(JavaFileObject.Kind.SOURCE.extension)) {
            return JavaFileObject.Kind.SOURCE;
        }
        if (name.endsWith(JavaFileObject.Kind.HTML.extension)) {
            return JavaFileObject.Kind.HTML;
        }
        return JavaFileObject.Kind.OTHER;
    }

    @Override
    public NestingKind getNestingKind() {
        switch (this.getKind()) {
            case SOURCE: {
                return NestingKind.TOP_LEVEL;
            }
            case CLASS: {
                ClassFileReader reader = this.getClassReader();
                if (reader == null) {
                    return null;
                }
                if (reader.isAnonymous()) {
                    return NestingKind.ANONYMOUS;
                }
                if (reader.isLocal()) {
                    return NestingKind.LOCAL;
                }
                if (reader.isMember()) {
                    return NestingKind.MEMBER;
                }
                return NestingKind.TOP_LEVEL;
            }
        }
        return null;
    }

    @Override
    public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind) {
        return this.entryName.endsWith(String.valueOf(simpleName) + kind.extension);
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ArchiveFileObject)) {
            return false;
        }
        ArchiveFileObject archiveFileObject = (ArchiveFileObject)o;
        return archiveFileObject.toUri().equals(this.toUri());
    }

    public int hashCode() {
        return this.toUri().hashCode();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        if (this.getKind() == JavaFileObject.Kind.SOURCE) {
            Throwable throwable = null;
            Object var3_4 = null;
            try (ZipFile zipFile2 = new ZipFile(this.file);){
                ZipEntry zipEntry = zipFile2.getEntry(this.entryName);
                return Util.getCharContents(this, ignoreEncodingErrors, org.eclipse.jdt.internal.compiler.util.Util.getZipEntryByteContent(zipEntry, zipFile2), this.charset.name());
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        return null;
    }

    @Override
    public long getLastModified() {
        try {
            Throwable throwable = null;
            Object var2_3 = null;
            try (ZipFile zip = new ZipFile(this.file);){
                ZipEntry zipEntry = zip.getEntry(this.entryName);
                return zipEntry.getTime();
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException iOException) {
            return 0L;
        }
    }

    @Override
    public String getName() {
        return this.entryName;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }
        ZipEntry zipEntry = this.zipFile.getEntry(this.entryName);
        return this.zipFile.getInputStream(zipEntry);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI toUri() {
        try {
            return new URI("jar:" + this.file.toURI().getPath() + "!" + this.entryName);
        }
        catch (URISyntaxException uRISyntaxException) {
            return null;
        }
    }

    public String toString() {
        return String.valueOf(this.file.getAbsolutePath()) + "[" + this.entryName + "]";
    }
}

