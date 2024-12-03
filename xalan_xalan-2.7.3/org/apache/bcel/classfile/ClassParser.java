/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public final class ClassParser {
    private static final int BUFSIZE = 8192;
    private DataInputStream dataInputStream;
    private final boolean fileOwned;
    private final String fileName;
    private String zipFile;
    private int classNameIndex;
    private int superclassNameIndex;
    private int major;
    private int minor;
    private int accessFlags;
    private int[] interfaces;
    private ConstantPool constantPool;
    private Field[] fields;
    private Method[] methods;
    private Attribute[] attributes;
    private final boolean isZip;

    public ClassParser(InputStream inputStream, String fileName) {
        this.fileName = fileName;
        this.fileOwned = false;
        String clazz = inputStream.getClass().getName();
        this.isZip = clazz.startsWith("java.util.zip.") || clazz.startsWith("java.util.jar.");
        this.dataInputStream = inputStream instanceof DataInputStream ? (DataInputStream)inputStream : new DataInputStream(new BufferedInputStream(inputStream, 8192));
    }

    public ClassParser(String fileName) {
        this.isZip = false;
        this.fileName = fileName;
        this.fileOwned = true;
    }

    public ClassParser(String zipFile, String fileName) {
        this.isZip = true;
        this.fileOwned = true;
        this.zipFile = zipFile;
        this.fileName = fileName;
    }

    public JavaClass parse() throws IOException, ClassFormatException {
        ZipFile zip = null;
        try {
            if (this.fileOwned) {
                if (this.isZip) {
                    zip = new ZipFile(this.zipFile);
                    ZipEntry entry = zip.getEntry(this.fileName);
                    if (entry == null) {
                        throw new IOException("File " + this.fileName + " not found");
                    }
                    this.dataInputStream = new DataInputStream(new BufferedInputStream(zip.getInputStream(entry), 8192));
                } else {
                    this.dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(this.fileName), 8192));
                }
            }
            this.readID();
            this.readVersion();
            this.readConstantPool();
            this.readClassInfo();
            this.readInterfaces();
            this.readFields();
            this.readMethods();
            this.readAttributes();
        }
        finally {
            if (this.fileOwned) {
                try {
                    if (this.dataInputStream != null) {
                        this.dataInputStream.close();
                    }
                }
                catch (IOException iOException) {}
            }
            try {
                if (zip != null) {
                    zip.close();
                }
            }
            catch (IOException iOException) {}
        }
        return new JavaClass(this.classNameIndex, this.superclassNameIndex, this.fileName, this.major, this.minor, this.accessFlags, this.constantPool, this.interfaces, this.fields, this.methods, this.attributes, this.isZip ? (byte)3 : 2);
    }

    private void readAttributes() throws IOException, ClassFormatException {
        int attributesCount = this.dataInputStream.readUnsignedShort();
        this.attributes = new Attribute[attributesCount];
        for (int i = 0; i < attributesCount; ++i) {
            this.attributes[i] = Attribute.readAttribute(this.dataInputStream, this.constantPool);
        }
    }

    private void readClassInfo() throws IOException, ClassFormatException {
        this.accessFlags = this.dataInputStream.readUnsignedShort();
        if ((this.accessFlags & 0x200) != 0) {
            this.accessFlags |= 0x400;
        }
        if ((this.accessFlags & 0x400) != 0 && (this.accessFlags & 0x10) != 0) {
            throw new ClassFormatException("Class " + this.fileName + " can't be both final and abstract");
        }
        this.classNameIndex = this.dataInputStream.readUnsignedShort();
        this.superclassNameIndex = this.dataInputStream.readUnsignedShort();
    }

    private void readConstantPool() throws IOException, ClassFormatException {
        this.constantPool = new ConstantPool(this.dataInputStream);
    }

    private void readFields() throws IOException, ClassFormatException {
        int fieldsCount = this.dataInputStream.readUnsignedShort();
        this.fields = new Field[fieldsCount];
        for (int i = 0; i < fieldsCount; ++i) {
            this.fields[i] = new Field((DataInput)this.dataInputStream, this.constantPool);
        }
    }

    private void readID() throws IOException, ClassFormatException {
        if (this.dataInputStream.readInt() != -889275714) {
            throw new ClassFormatException(this.fileName + " is not a Java .class file");
        }
    }

    private void readInterfaces() throws IOException, ClassFormatException {
        int interfacesCount = this.dataInputStream.readUnsignedShort();
        this.interfaces = new int[interfacesCount];
        for (int i = 0; i < interfacesCount; ++i) {
            this.interfaces[i] = this.dataInputStream.readUnsignedShort();
        }
    }

    private void readMethods() throws IOException {
        int methodsCount = this.dataInputStream.readUnsignedShort();
        this.methods = new Method[methodsCount];
        for (int i = 0; i < methodsCount; ++i) {
            this.methods[i] = new Method((DataInput)this.dataInputStream, this.constantPool);
        }
    }

    private void readVersion() throws IOException, ClassFormatException {
        this.minor = this.dataInputStream.readUnsignedShort();
        this.major = this.dataInputStream.readUnsignedShort();
    }
}

