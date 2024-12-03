/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.RuntimeInvisibleAnnotations;
import org.apache.bcel.classfile.RuntimeVisibleAnnotations;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ClassObserver;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.BCELComparator;
import org.apache.commons.lang3.ArrayUtils;

public class ClassGen
extends AccessFlags
implements Cloneable {
    private static BCELComparator bcelComparator = new BCELComparator(){

        @Override
        public boolean equals(Object o1, Object o2) {
            ClassGen THIS = (ClassGen)o1;
            ClassGen THAT = (ClassGen)o2;
            return Objects.equals(THIS.getClassName(), THAT.getClassName());
        }

        @Override
        public int hashCode(Object o) {
            ClassGen THIS = (ClassGen)o;
            return THIS.getClassName().hashCode();
        }
    };
    private String className;
    private String superClassName;
    private final String fileName;
    private int classNameIndex = -1;
    private int superclassNameIndex = -1;
    private int major = 45;
    private int minor = 3;
    private ConstantPoolGen cp;
    private final List<Field> fieldList = new ArrayList<Field>();
    private final List<Method> methodList = new ArrayList<Method>();
    private final List<Attribute> attributeList = new ArrayList<Attribute>();
    private final List<String> interfaceList = new ArrayList<String>();
    private final List<AnnotationEntryGen> annotationList = new ArrayList<AnnotationEntryGen>();
    private List<ClassObserver> observers;

    public static BCELComparator getComparator() {
        return bcelComparator;
    }

    public static void setComparator(BCELComparator comparator) {
        bcelComparator = comparator;
    }

    public ClassGen(JavaClass clazz) {
        super(clazz.getAccessFlags());
        this.classNameIndex = clazz.getClassNameIndex();
        this.superclassNameIndex = clazz.getSuperclassNameIndex();
        this.className = clazz.getClassName();
        this.superClassName = clazz.getSuperclassName();
        this.fileName = clazz.getSourceFileName();
        this.cp = new ConstantPoolGen(clazz.getConstantPool());
        this.major = clazz.getMajor();
        this.minor = clazz.getMinor();
        Attribute[] attributes = clazz.getAttributes();
        AnnotationEntryGen[] annotations = this.unpackAnnotations(attributes);
        Collections.addAll(this.interfaceList, clazz.getInterfaceNames());
        for (Attribute attribute : attributes) {
            if (attribute instanceof Annotations) continue;
            this.addAttribute(attribute);
        }
        Collections.addAll(this.annotationList, annotations);
        Collections.addAll(this.methodList, clazz.getMethods());
        Collections.addAll(this.fieldList, clazz.getFields());
    }

    public ClassGen(String className, String superClassName, String fileName, int accessFlags, String[] interfaces) {
        this(className, superClassName, fileName, accessFlags, interfaces, new ConstantPoolGen());
    }

    public ClassGen(String className, String superClassName, String fileName, int accessFlags, String[] interfaces, ConstantPoolGen cp) {
        super(accessFlags);
        this.className = className;
        this.superClassName = superClassName;
        this.fileName = fileName;
        this.cp = cp;
        if (fileName != null) {
            this.addAttribute(new SourceFile(cp.addUtf8("SourceFile"), 2, cp.addUtf8(fileName), cp.getConstantPool()));
        }
        this.classNameIndex = cp.addClass(className);
        this.superclassNameIndex = cp.addClass(superClassName);
        if (interfaces != null) {
            Collections.addAll(this.interfaceList, interfaces);
        }
    }

    public void addAnnotationEntry(AnnotationEntryGen a) {
        this.annotationList.add(a);
    }

    public void addAttribute(Attribute a) {
        this.attributeList.add(a);
    }

    public void addEmptyConstructor(int accessFlags) {
        InstructionList il = new InstructionList();
        il.append(InstructionConst.THIS);
        il.append(new INVOKESPECIAL(this.cp.addMethodref(this.superClassName, "<init>", "()V")));
        il.append(InstructionConst.RETURN);
        MethodGen mg = new MethodGen(accessFlags, Type.VOID, Type.NO_ARGS, null, "<init>", this.className, il, this.cp);
        mg.setMaxStack(1);
        this.addMethod(mg.getMethod());
    }

    public void addField(Field f) {
        this.fieldList.add(f);
    }

    public void addInterface(String name) {
        this.interfaceList.add(name);
    }

    public void addMethod(Method m) {
        this.methodList.add(m);
    }

    public void addObserver(ClassObserver o) {
        if (this.observers == null) {
            this.observers = new ArrayList<ClassObserver>();
        }
        this.observers.add(o);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Clone Not Supported");
        }
    }

    public boolean containsField(Field f) {
        return this.fieldList.contains(f);
    }

    public Field containsField(String name) {
        for (Field f : this.fieldList) {
            if (!f.getName().equals(name)) continue;
            return f;
        }
        return null;
    }

    public Method containsMethod(String name, String signature) {
        for (Method m : this.methodList) {
            if (!m.getName().equals(name) || !m.getSignature().equals(signature)) continue;
            return m;
        }
        return null;
    }

    public boolean equals(Object obj) {
        return bcelComparator.equals(this, obj);
    }

    public AnnotationEntryGen[] getAnnotationEntries() {
        return this.annotationList.toArray(AnnotationEntryGen.EMPTY_ARRAY);
    }

    public Attribute[] getAttributes() {
        return this.attributeList.toArray(Attribute.EMPTY_ARRAY);
    }

    public String getClassName() {
        return this.className;
    }

    public int getClassNameIndex() {
        return this.classNameIndex;
    }

    public ConstantPoolGen getConstantPool() {
        return this.cp;
    }

    public Field[] getFields() {
        return this.fieldList.toArray(Field.EMPTY_ARRAY);
    }

    public String getFileName() {
        return this.fileName;
    }

    public String[] getInterfaceNames() {
        return this.interfaceList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public int[] getInterfaces() {
        int size = this.interfaceList.size();
        int[] interfaces = new int[size];
        Arrays.setAll(interfaces, i -> this.cp.addClass(this.interfaceList.get(i)));
        return interfaces;
    }

    public JavaClass getJavaClass() {
        int[] interfaces = this.getInterfaces();
        Field[] fields = this.getFields();
        Method[] methods = this.getMethods();
        Attribute[] attributes = null;
        if (this.annotationList.isEmpty()) {
            attributes = this.getAttributes();
        } else {
            Attribute[] annAttributes = AnnotationEntryGen.getAnnotationAttributes(this.cp, this.getAnnotationEntries());
            attributes = new Attribute[this.attributeList.size() + annAttributes.length];
            this.attributeList.toArray(attributes);
            System.arraycopy(annAttributes, 0, attributes, this.attributeList.size(), annAttributes.length);
        }
        ConstantPool cp = this.cp.getFinalConstantPool();
        return new JavaClass(this.classNameIndex, this.superclassNameIndex, this.fileName, this.major, this.minor, super.getAccessFlags(), cp, interfaces, fields, methods, attributes);
    }

    public int getMajor() {
        return this.major;
    }

    public Method getMethodAt(int pos) {
        return this.methodList.get(pos);
    }

    public Method[] getMethods() {
        return this.methodList.toArray(Method.EMPTY_ARRAY);
    }

    public int getMinor() {
        return this.minor;
    }

    public String getSuperclassName() {
        return this.superClassName;
    }

    public int getSuperclassNameIndex() {
        return this.superclassNameIndex;
    }

    public int hashCode() {
        return bcelComparator.hashCode(this);
    }

    public void removeAttribute(Attribute a) {
        this.attributeList.remove(a);
    }

    public void removeField(Field f) {
        this.fieldList.remove(f);
    }

    public void removeInterface(String name) {
        this.interfaceList.remove(name);
    }

    public void removeMethod(Method m) {
        this.methodList.remove(m);
    }

    public void removeObserver(ClassObserver o) {
        if (this.observers != null) {
            this.observers.remove(o);
        }
    }

    public void replaceField(Field old, Field newField) {
        if (newField == null) {
            throw new ClassGenException("Replacement method must not be null");
        }
        int i = this.fieldList.indexOf(old);
        if (i < 0) {
            this.fieldList.add(newField);
        } else {
            this.fieldList.set(i, newField);
        }
    }

    public void replaceMethod(Method old, Method newMethod) {
        if (newMethod == null) {
            throw new ClassGenException("Replacement method must not be null");
        }
        int i = this.methodList.indexOf(old);
        if (i < 0) {
            this.methodList.add(newMethod);
        } else {
            this.methodList.set(i, newMethod);
        }
    }

    public void setClassName(String name) {
        this.className = Utility.pathToPackage(name);
        this.classNameIndex = this.cp.addClass(name);
    }

    public void setClassNameIndex(int classNameIndex) {
        this.classNameIndex = classNameIndex;
        this.className = Utility.pathToPackage(this.cp.getConstantPool().getConstantString(classNameIndex, (byte)7));
    }

    public void setConstantPool(ConstantPoolGen constantPool) {
        this.cp = constantPool;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setMethodAt(Method method, int pos) {
        this.methodList.set(pos, method);
    }

    public void setMethods(Method[] methods) {
        this.methodList.clear();
        Collections.addAll(this.methodList, methods);
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setSuperclassName(String name) {
        this.superClassName = Utility.pathToPackage(name);
        this.superclassNameIndex = this.cp.addClass(name);
    }

    public void setSuperclassNameIndex(int superclassNameIndex) {
        this.superclassNameIndex = superclassNameIndex;
        this.superClassName = Utility.pathToPackage(this.cp.getConstantPool().getConstantString(superclassNameIndex, (byte)7));
    }

    private AnnotationEntryGen[] unpackAnnotations(Attribute[] attrs) {
        ArrayList annotationGenObjs = new ArrayList();
        for (Attribute attr : attrs) {
            if (attr instanceof RuntimeVisibleAnnotations) {
                RuntimeVisibleAnnotations rva = (RuntimeVisibleAnnotations)attr;
                rva.forEach(a -> annotationGenObjs.add(new AnnotationEntryGen((AnnotationEntry)a, this.getConstantPool(), false)));
                continue;
            }
            if (!(attr instanceof RuntimeInvisibleAnnotations)) continue;
            RuntimeInvisibleAnnotations ria = (RuntimeInvisibleAnnotations)attr;
            ria.forEach(a -> annotationGenObjs.add(new AnnotationEntryGen((AnnotationEntry)a, this.getConstantPool(), false)));
        }
        return annotationGenObjs.toArray(AnnotationEntryGen.EMPTY_ARRAY);
    }

    public void update() {
        if (this.observers != null) {
            for (ClassObserver observer : this.observers) {
                observer.notify(this);
            }
        }
    }
}

