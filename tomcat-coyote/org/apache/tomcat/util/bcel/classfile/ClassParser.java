/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.bcel.classfile.Annotations;
import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ConstantUtf8;
import org.apache.tomcat.util.bcel.classfile.JavaClass;
import org.apache.tomcat.util.bcel.classfile.Utility;

public final class ClassParser {
    private static final int BUFSIZE = 8192;
    private final DataInput dataInputStream;
    private String className;
    private String superclassName;
    private int accessFlags;
    private String[] interfaceNames;
    private ConstantPool constantPool;
    private Annotations runtimeVisibleAnnotations;
    private List<Annotations> runtimeVisibleFieldOrMethodAnnotations;
    private static final String[] INTERFACES_EMPTY_ARRAY = new String[0];

    public ClassParser(InputStream inputStream) {
        this.dataInputStream = new DataInputStream(new BufferedInputStream(inputStream, 8192));
    }

    public JavaClass parse() throws IOException, ClassFormatException {
        this.readID();
        this.readVersion();
        this.readConstantPool();
        this.readClassInfo();
        this.readInterfaces();
        this.readFields();
        this.readMethods();
        this.readAttributes(false);
        return new JavaClass(this.className, this.superclassName, this.accessFlags, this.constantPool, this.interfaceNames, this.runtimeVisibleAnnotations, this.runtimeVisibleFieldOrMethodAnnotations);
    }

    private void readAttributes(boolean fieldOrMethod) throws IOException, ClassFormatException {
        int attributesCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < attributesCount; ++i) {
            int name_index = this.dataInputStream.readUnsignedShort();
            ConstantUtf8 c = (ConstantUtf8)this.constantPool.getConstant(name_index, (byte)1);
            String name = c.getBytes();
            int length = this.dataInputStream.readInt();
            if (name.equals("RuntimeVisibleAnnotations")) {
                if (fieldOrMethod) {
                    Annotations fieldOrMethodAnnotations = new Annotations(this.dataInputStream, this.constantPool);
                    if (this.runtimeVisibleFieldOrMethodAnnotations == null) {
                        this.runtimeVisibleFieldOrMethodAnnotations = new ArrayList<Annotations>();
                    }
                    this.runtimeVisibleFieldOrMethodAnnotations.add(fieldOrMethodAnnotations);
                    continue;
                }
                if (this.runtimeVisibleAnnotations != null) {
                    throw new ClassFormatException("RuntimeVisibleAnnotations attribute is not allowed more than once in a class file");
                }
                this.runtimeVisibleAnnotations = new Annotations(this.dataInputStream, this.constantPool);
                continue;
            }
            Utility.skipFully(this.dataInputStream, length);
        }
    }

    private void readClassInfo() throws IOException, ClassFormatException {
        this.accessFlags = this.dataInputStream.readUnsignedShort();
        if ((this.accessFlags & 0x200) != 0) {
            this.accessFlags |= 0x400;
        }
        if ((this.accessFlags & 0x400) != 0 && (this.accessFlags & 0x10) != 0) {
            throw new ClassFormatException("Class can't be both final and abstract");
        }
        int classNameIndex = this.dataInputStream.readUnsignedShort();
        this.className = Utility.getClassName(this.constantPool, classNameIndex);
        int superclass_name_index = this.dataInputStream.readUnsignedShort();
        this.superclassName = superclass_name_index > 0 ? Utility.getClassName(this.constantPool, superclass_name_index) : "java.lang.Object";
    }

    private void readConstantPool() throws IOException, ClassFormatException {
        this.constantPool = new ConstantPool(this.dataInputStream);
    }

    private void readFields() throws IOException, ClassFormatException {
        int fieldsCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < fieldsCount; ++i) {
            Utility.skipFully(this.dataInputStream, 6);
            this.readAttributes(true);
        }
    }

    private void readID() throws IOException, ClassFormatException {
        if (this.dataInputStream.readInt() != -889275714) {
            throw new ClassFormatException("It is not a Java .class file");
        }
    }

    private void readInterfaces() throws IOException, ClassFormatException {
        int interfacesCount = this.dataInputStream.readUnsignedShort();
        if (interfacesCount > 0) {
            this.interfaceNames = new String[interfacesCount];
            for (int i = 0; i < interfacesCount; ++i) {
                int index = this.dataInputStream.readUnsignedShort();
                this.interfaceNames[i] = Utility.getClassName(this.constantPool, index);
            }
        } else {
            this.interfaceNames = INTERFACES_EMPTY_ARRAY;
        }
    }

    private void readMethods() throws IOException, ClassFormatException {
        int methodsCount = this.dataInputStream.readUnsignedShort();
        for (int i = 0; i < methodsCount; ++i) {
            Utility.skipFully(this.dataInputStream, 6);
            this.readAttributes(true);
        }
    }

    private void readVersion() throws IOException, ClassFormatException {
        Utility.skipFully(this.dataInputStream, 4);
    }
}

