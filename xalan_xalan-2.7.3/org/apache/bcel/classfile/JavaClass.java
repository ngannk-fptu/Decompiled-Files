/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.classfile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TreeSet;
import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.BCELComparator;
import org.apache.bcel.util.ClassQueue;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.commons.lang3.ArrayUtils;

public class JavaClass
extends AccessFlags
implements Cloneable,
Node,
Comparable<JavaClass> {
    public static final String EXTENSION = ".class";
    public static final JavaClass[] EMPTY_ARRAY = new JavaClass[0];
    public static final byte HEAP = 1;
    public static final byte FILE = 2;
    public static final byte ZIP = 3;
    private static final boolean debug = Boolean.getBoolean("JavaClass.debug");
    private static BCELComparator bcelComparator = new BCELComparator(){

        @Override
        public boolean equals(Object o1, Object o2) {
            JavaClass THIS = (JavaClass)o1;
            JavaClass THAT = (JavaClass)o2;
            return Objects.equals(THIS.getClassName(), THAT.getClassName());
        }

        @Override
        public int hashCode(Object o) {
            JavaClass THIS = (JavaClass)o;
            return THIS.getClassName().hashCode();
        }
    };
    private String fileName;
    private final String packageName;
    private String sourceFileName = "<Unknown>";
    private int classNameIndex;
    private int superclassNameIndex;
    private String className;
    private String superclassName;
    private int major;
    private int minor;
    private ConstantPool constantPool;
    private int[] interfaces;
    private String[] interfaceNames;
    private Field[] fields;
    private Method[] methods;
    private Attribute[] attributes;
    private AnnotationEntry[] annotations;
    private byte source = 1;
    private boolean isAnonymous;
    private boolean isNested;
    private boolean computedNestedTypeStatus;
    private transient Repository repository = SyntheticRepository.getInstance();

    static void Debug(String str) {
        if (debug) {
            System.out.println(str);
        }
    }

    public static BCELComparator getComparator() {
        return bcelComparator;
    }

    private static String indent(Object obj) {
        StringTokenizer tokenizer = new StringTokenizer(obj.toString(), "\n");
        StringBuilder buf = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            buf.append("\t").append(tokenizer.nextToken()).append("\n");
        }
        return buf.toString();
    }

    public static void setComparator(BCELComparator comparator) {
        bcelComparator = comparator;
    }

    public JavaClass(int classNameIndex, int superclassNameIndex, String fileName, int major, int minor, int accessFlags, ConstantPool constantPool, int[] interfaces, Field[] fields, Method[] methods, Attribute[] attributes) {
        this(classNameIndex, superclassNameIndex, fileName, major, minor, accessFlags, constantPool, interfaces, fields, methods, attributes, 1);
    }

    public JavaClass(int classNameIndex, int superclassNameIndex, String fileName, int major, int minor, int accessFlags, ConstantPool constantPool, int[] interfaces, Field[] fields, Method[] methods, Attribute[] attributes, byte source) {
        super(accessFlags);
        if (interfaces == null) {
            interfaces = ArrayUtils.EMPTY_INT_ARRAY;
        }
        if (attributes == null) {
            attributes = Attribute.EMPTY_ARRAY;
        }
        if (fields == null) {
            fields = Field.EMPTY_FIELD_ARRAY;
        }
        if (methods == null) {
            methods = Method.EMPTY_METHOD_ARRAY;
        }
        this.classNameIndex = classNameIndex;
        this.superclassNameIndex = superclassNameIndex;
        this.fileName = fileName;
        this.major = major;
        this.minor = minor;
        this.constantPool = constantPool;
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.attributes = attributes;
        this.source = source;
        for (Attribute attribute : attributes) {
            if (!(attribute instanceof SourceFile)) continue;
            this.sourceFileName = ((SourceFile)attribute).getSourceFileName();
            break;
        }
        this.className = constantPool.getConstantString(classNameIndex, (byte)7);
        this.className = Utility.compactClassName(this.className, false);
        int index = this.className.lastIndexOf(46);
        this.packageName = index < 0 ? "" : this.className.substring(0, index);
        if (superclassNameIndex > 0) {
            this.superclassName = constantPool.getConstantString(superclassNameIndex, (byte)7);
            this.superclassName = Utility.compactClassName(this.superclassName, false);
        } else {
            this.superclassName = "java.lang.Object";
        }
        this.interfaceNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            String str = constantPool.getConstantString(interfaces[i], (byte)7);
            this.interfaceNames[i] = Utility.compactClassName(str, false);
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitJavaClass(this);
    }

    @Override
    public int compareTo(JavaClass obj) {
        return this.getClassName().compareTo(obj.getClassName());
    }

    private void computeNestedTypeStatus() {
        if (this.computedNestedTypeStatus) {
            return;
        }
        for (Attribute attribute : this.attributes) {
            if (!(attribute instanceof InnerClasses)) continue;
            ((InnerClasses)attribute).forEach(innerClass -> {
                boolean innerClassAttributeRefersToMe = false;
                String innerClassName = this.constantPool.getConstantString(innerClass.getInnerClassIndex(), (byte)7);
                if ((innerClassName = Utility.compactClassName(innerClassName, false)).equals(this.getClassName())) {
                    innerClassAttributeRefersToMe = true;
                }
                if (innerClassAttributeRefersToMe) {
                    this.isNested = true;
                    if (innerClass.getInnerNameIndex() == 0) {
                        this.isAnonymous = true;
                    }
                }
            });
        }
        this.computedNestedTypeStatus = true;
    }

    public JavaClass copy() {
        try {
            JavaClass c = (JavaClass)this.clone();
            c.constantPool = this.constantPool.copy();
            c.interfaces = (int[])this.interfaces.clone();
            c.interfaceNames = (String[])this.interfaceNames.clone();
            c.fields = new Field[this.fields.length];
            Arrays.setAll(c.fields, i -> this.fields[i].copy(c.constantPool));
            c.methods = new Method[this.methods.length];
            Arrays.setAll(c.methods, i -> this.methods[i].copy(c.constantPool));
            c.attributes = new Attribute[this.attributes.length];
            Arrays.setAll(c.attributes, i -> this.attributes[i].copy(c.constantPool));
            return c;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeInt(-889275714);
        file.writeShort(this.minor);
        file.writeShort(this.major);
        this.constantPool.dump(file);
        file.writeShort(super.getAccessFlags());
        file.writeShort(this.classNameIndex);
        file.writeShort(this.superclassNameIndex);
        file.writeShort(this.interfaces.length);
        for (int interface1 : this.interfaces) {
            file.writeShort(interface1);
        }
        file.writeShort(this.fields.length);
        for (Field field : this.fields) {
            field.dump(file);
        }
        file.writeShort(this.methods.length);
        for (Method method : this.methods) {
            method.dump(file);
        }
        if (this.attributes != null) {
            file.writeShort(this.attributes.length);
            for (Attribute attribute : this.attributes) {
                attribute.dump(file);
            }
        } else {
            file.writeShort(0);
        }
        file.flush();
    }

    public void dump(File file) throws IOException {
        File dir;
        String parent = file.getParent();
        if (parent != null && !(dir = new File(parent)).mkdirs() && !dir.isDirectory()) {
            throw new IOException("Could not create the directory " + dir);
        }
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));){
            this.dump(dos);
        }
    }

    public void dump(OutputStream file) throws IOException {
        this.dump(new DataOutputStream(file));
    }

    public void dump(String fileName) throws IOException {
        this.dump(new File(fileName));
    }

    public boolean equals(Object obj) {
        return bcelComparator.equals(this, obj);
    }

    public JavaClass[] getAllInterfaces() throws ClassNotFoundException {
        ClassQueue queue = new ClassQueue();
        TreeSet<JavaClass> allInterfaces = new TreeSet<JavaClass>();
        queue.enqueue(this);
        while (!queue.empty()) {
            JavaClass clazz = queue.dequeue();
            JavaClass souper = clazz.getSuperClass();
            JavaClass[] interfaces = clazz.getInterfaces();
            if (clazz.isInterface()) {
                allInterfaces.add(clazz);
            } else if (souper != null) {
                queue.enqueue(souper);
            }
            for (JavaClass iface : interfaces) {
                queue.enqueue(iface);
            }
        }
        return allInterfaces.toArray(EMPTY_ARRAY);
    }

    public AnnotationEntry[] getAnnotationEntries() {
        if (this.annotations == null) {
            this.annotations = AnnotationEntry.createAnnotationEntries(this.getAttributes());
        }
        return this.annotations;
    }

    public Attribute[] getAttributes() {
        return this.attributes;
    }

    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos);){
            this.dump(dos);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public String getClassName() {
        return this.className;
    }

    public int getClassNameIndex() {
        return this.classNameIndex;
    }

    public ConstantPool getConstantPool() {
        return this.constantPool;
    }

    public Field[] getFields() {
        return this.fields;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int[] getInterfaceIndices() {
        return this.interfaces;
    }

    public String[] getInterfaceNames() {
        return this.interfaceNames;
    }

    public JavaClass[] getInterfaces() throws ClassNotFoundException {
        String[] interfaces = this.getInterfaceNames();
        JavaClass[] classes = new JavaClass[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            classes[i] = this.repository.loadClass(interfaces[i]);
        }
        return classes;
    }

    public int getMajor() {
        return this.major;
    }

    public Method getMethod(java.lang.reflect.Method m) {
        for (Method method : this.methods) {
            if (!m.getName().equals(method.getName()) || m.getModifiers() != method.getModifiers() || !Type.getSignature(m).equals(method.getSignature())) continue;
            return method;
        }
        return null;
    }

    public Method[] getMethods() {
        return this.methods;
    }

    public int getMinor() {
        return this.minor;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public Repository getRepository() {
        return this.repository;
    }

    public final byte getSource() {
        return this.source;
    }

    public String getSourceFileName() {
        return this.sourceFileName;
    }

    public String getSourceFilePath() {
        StringBuilder outFileName = new StringBuilder();
        if (!this.packageName.isEmpty()) {
            outFileName.append(Utility.packageToPath(this.packageName));
            outFileName.append('/');
        }
        outFileName.append(this.sourceFileName);
        return outFileName.toString();
    }

    public JavaClass getSuperClass() throws ClassNotFoundException {
        if ("java.lang.Object".equals(this.getClassName())) {
            return null;
        }
        return this.repository.loadClass(this.getSuperclassName());
    }

    public JavaClass[] getSuperClasses() throws ClassNotFoundException {
        JavaClass clazz = this;
        ArrayList<JavaClass> allSuperClasses = new ArrayList<JavaClass>();
        for (clazz = clazz.getSuperClass(); clazz != null; clazz = clazz.getSuperClass()) {
            allSuperClasses.add(clazz);
        }
        return allSuperClasses.toArray(EMPTY_ARRAY);
    }

    public String getSuperclassName() {
        return this.superclassName;
    }

    public int getSuperclassNameIndex() {
        return this.superclassNameIndex;
    }

    public int hashCode() {
        return bcelComparator.hashCode(this);
    }

    public boolean implementationOf(JavaClass inter) throws ClassNotFoundException {
        JavaClass[] superInterfaces;
        if (!inter.isInterface()) {
            throw new IllegalArgumentException(inter.getClassName() + " is no interface");
        }
        if (this.equals(inter)) {
            return true;
        }
        for (JavaClass superInterface : superInterfaces = this.getAllInterfaces()) {
            if (!superInterface.equals(inter)) continue;
            return true;
        }
        return false;
    }

    public final boolean instanceOf(JavaClass superclass) throws ClassNotFoundException {
        if (this.equals(superclass)) {
            return true;
        }
        for (JavaClass clazz : this.getSuperClasses()) {
            if (!clazz.equals(superclass)) continue;
            return true;
        }
        if (superclass.isInterface()) {
            return this.implementationOf(superclass);
        }
        return false;
    }

    public final boolean isAnonymous() {
        this.computeNestedTypeStatus();
        return this.isAnonymous;
    }

    public final boolean isClass() {
        return (super.getAccessFlags() & 0x200) == 0;
    }

    public final boolean isNested() {
        this.computeNestedTypeStatus();
        return this.isNested;
    }

    public final boolean isSuper() {
        return (super.getAccessFlags() & 0x20) != 0;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setClassNameIndex(int classNameIndex) {
        this.classNameIndex = classNameIndex;
    }

    public void setConstantPool(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setInterfaceNames(String[] interfaceNames) {
        this.interfaceNames = interfaceNames;
    }

    public void setInterfaces(int[] interfaces) {
        this.interfaces = interfaces;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public void setSuperclassName(String superclassName) {
        this.superclassName = superclassName;
    }

    public void setSuperclassNameIndex(int superclassNameIndex) {
        this.superclassNameIndex = superclassNameIndex;
    }

    public String toString() {
        AnnotationEntry[] annotations;
        String access = Utility.accessToString(super.getAccessFlags(), true);
        access = access.isEmpty() ? "" : access + " ";
        StringBuilder buf = new StringBuilder(128);
        buf.append(access).append(Utility.classOrInterface(super.getAccessFlags())).append(" ").append(this.className).append(" extends ").append(Utility.compactClassName(this.superclassName, false)).append('\n');
        int size = this.interfaces.length;
        if (size > 0) {
            buf.append("implements\t\t");
            for (int i = 0; i < size; ++i) {
                buf.append(this.interfaceNames[i]);
                if (i >= size - 1) continue;
                buf.append(", ");
            }
            buf.append('\n');
        }
        buf.append("file name\t\t").append(this.fileName).append('\n');
        buf.append("compiled from\t\t").append(this.sourceFileName).append('\n');
        buf.append("compiler version\t").append(this.major).append(".").append(this.minor).append('\n');
        buf.append("access flags\t\t").append(super.getAccessFlags()).append('\n');
        buf.append("constant pool\t\t").append(this.constantPool.getLength()).append(" entries\n");
        buf.append("ACC_SUPER flag\t\t").append(this.isSuper()).append("\n");
        if (this.attributes.length > 0) {
            buf.append("\nAttribute(s):\n");
            for (Attribute attribute : this.attributes) {
                buf.append(JavaClass.indent(attribute));
            }
        }
        if ((annotations = this.getAnnotationEntries()) != null && annotations.length > 0) {
            buf.append("\nAnnotation(s):\n");
            for (AnnotationEntry annotation : annotations) {
                buf.append(JavaClass.indent(annotation));
            }
        }
        if (this.fields.length > 0) {
            buf.append("\n").append(this.fields.length).append(" fields:\n");
            for (Field field : this.fields) {
                buf.append("\t").append(field).append('\n');
            }
        }
        if (this.methods.length > 0) {
            buf.append("\n").append(this.methods.length).append(" methods:\n");
            for (Method method : this.methods) {
                buf.append("\t").append(method).append('\n');
            }
        }
        return buf.toString();
    }
}

