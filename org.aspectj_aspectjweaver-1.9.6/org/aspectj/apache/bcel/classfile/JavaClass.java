/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.AttributeUtils;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.InnerClass;
import org.aspectj.apache.bcel.classfile.InnerClasses;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.Modifiers;
import org.aspectj.apache.bcel.classfile.Node;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.apache.bcel.classfile.SourceFile;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeAnnos;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.apache.bcel.util.Repository;
import org.aspectj.apache.bcel.util.SyntheticRepository;

public class JavaClass
extends Modifiers
implements Cloneable,
Node {
    private static final String[] NoInterfaceNames = new String[0];
    private static final Field[] NoFields = new Field[0];
    private static final Method[] NoMethod = new Method[0];
    private static final int[] NoInterfaceIndices = new int[0];
    private static final Attribute[] NoAttributes = new Attribute[0];
    private String fileName;
    private String packageName;
    private String sourcefileName;
    private int classnameIdx;
    private int superclassnameIdx;
    private String classname;
    private String superclassname;
    private int major;
    private int minor;
    private ConstantPool cpool;
    private int[] interfaces;
    private String[] interfacenames;
    private Field[] fields;
    private Method[] methods;
    private Attribute[] attributes;
    private AnnotationGen[] annotations;
    private boolean isGeneric = false;
    private boolean isAnonymous = false;
    private boolean isNested = false;
    private boolean computedNestedTypeStatus = false;
    private boolean annotationsOutOfDate = true;
    private String signatureAttributeString = null;
    private Signature signatureAttribute = null;
    private boolean searchedForSignatureAttribute = false;
    private transient Repository repository = null;

    public JavaClass(int classnameIndex, int superclassnameIndex, String filename, int major, int minor, int access_flags, ConstantPool cpool, int[] interfaces, Field[] fields, Method[] methods, Attribute[] attributes) {
        if (interfaces == null) {
            interfaces = NoInterfaceIndices;
        }
        this.classnameIdx = classnameIndex;
        this.superclassnameIdx = superclassnameIndex;
        this.fileName = filename;
        this.major = major;
        this.minor = minor;
        this.modifiers = access_flags;
        this.cpool = cpool;
        this.interfaces = interfaces;
        this.fields = fields == null ? NoFields : fields;
        this.methods = methods == null ? NoMethod : methods;
        this.attributes = attributes == null ? NoAttributes : attributes;
        this.annotationsOutOfDate = true;
        SourceFile sfAttribute = AttributeUtils.getSourceFileAttribute(attributes);
        this.sourcefileName = sfAttribute == null ? "<Unknown>" : sfAttribute.getSourceFileName();
        this.classname = cpool.getConstantString(classnameIndex, (byte)7);
        this.classname = Utility.compactClassName(this.classname, false);
        int index = this.classname.lastIndexOf(46);
        this.packageName = index < 0 ? "" : this.classname.substring(0, index);
        if (superclassnameIndex > 0) {
            this.superclassname = cpool.getConstantString(superclassnameIndex, (byte)7);
            this.superclassname = Utility.compactClassName(this.superclassname, false);
        } else {
            this.superclassname = "java.lang.Object";
        }
        if (interfaces.length == 0) {
            this.interfacenames = NoInterfaceNames;
        } else {
            this.interfacenames = new String[interfaces.length];
            for (int i = 0; i < interfaces.length; ++i) {
                String str = cpool.getConstantString(interfaces[i], (byte)7);
                this.interfacenames[i] = Utility.compactClassName(str, false);
            }
        }
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitJavaClass(this);
    }

    public void dump(File file) throws IOException {
        String parent = file.getParent();
        if (parent != null) {
            File dir = new File(parent);
            dir.mkdirs();
        }
        this.dump(new DataOutputStream(new FileOutputStream(file)));
    }

    public void dump(String file_name) throws IOException {
        this.dump(new File(file_name));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getBytes() {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(s);
        try {
            this.dump(ds);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                ds.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return s.toByteArray();
    }

    public void dump(OutputStream file) throws IOException {
        this.dump(new DataOutputStream(file));
    }

    public void dump(DataOutputStream file) throws IOException {
        int i;
        file.writeInt(-889275714);
        file.writeShort(this.minor);
        file.writeShort(this.major);
        this.cpool.dump(file);
        file.writeShort(this.modifiers);
        file.writeShort(this.classnameIdx);
        file.writeShort(this.superclassnameIdx);
        file.writeShort(this.interfaces.length);
        for (i = 0; i < this.interfaces.length; ++i) {
            file.writeShort(this.interfaces[i]);
        }
        file.writeShort(this.fields.length);
        for (i = 0; i < this.fields.length; ++i) {
            this.fields[i].dump(file);
        }
        file.writeShort(this.methods.length);
        for (i = 0; i < this.methods.length; ++i) {
            this.methods[i].dump(file);
        }
        AttributeUtils.writeAttributes(this.attributes, file);
        file.close();
    }

    public Attribute[] getAttributes() {
        return this.attributes;
    }

    public AnnotationGen[] getAnnotations() {
        if (this.annotationsOutOfDate) {
            ArrayList<AnnotationGen> accumulatedAnnotations = new ArrayList<AnnotationGen>();
            for (int i = 0; i < this.attributes.length; ++i) {
                Attribute attribute = this.attributes[i];
                if (!(attribute instanceof RuntimeAnnos)) continue;
                RuntimeAnnos runtimeAnnotations = (RuntimeAnnos)attribute;
                accumulatedAnnotations.addAll(runtimeAnnotations.getAnnotations());
            }
            this.annotations = accumulatedAnnotations.toArray(new AnnotationGen[0]);
            this.annotationsOutOfDate = false;
        }
        return this.annotations;
    }

    public String getClassName() {
        return this.classname;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getClassNameIndex() {
        return this.classnameIdx;
    }

    public ConstantPool getConstantPool() {
        return this.cpool;
    }

    public Field[] getFields() {
        return this.fields;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String[] getInterfaceNames() {
        return this.interfacenames;
    }

    public int[] getInterfaceIndices() {
        return this.interfaces;
    }

    public int getMajor() {
        return this.major;
    }

    public Method[] getMethods() {
        return this.methods;
    }

    public Method getMethod(java.lang.reflect.Method m) {
        for (int i = 0; i < this.methods.length; ++i) {
            Method method = this.methods[i];
            if (!m.getName().equals(method.getName()) || m.getModifiers() != method.getModifiers() || !Type.getSignature(m).equals(method.getSignature())) continue;
            return method;
        }
        return null;
    }

    public Method getMethod(Constructor<?> c) {
        for (int i = 0; i < this.methods.length; ++i) {
            Method method = this.methods[i];
            if (!method.getName().equals("<init>") || c.getModifiers() != method.getModifiers() || !Type.getSignature(c).equals(method.getSignature())) continue;
            return method;
        }
        return null;
    }

    public Field getField(java.lang.reflect.Field field) {
        String fieldName = field.getName();
        for (Field f : this.fields) {
            if (!f.getName().equals(fieldName)) continue;
            return f;
        }
        return null;
    }

    public int getMinor() {
        return this.minor;
    }

    public String getSourceFileName() {
        return this.sourcefileName;
    }

    public String getSuperclassName() {
        return this.superclassname;
    }

    public int getSuperclassNameIndex() {
        return this.superclassnameIdx;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
        this.annotationsOutOfDate = true;
    }

    public void setClassName(String class_name) {
        this.classname = class_name;
    }

    public void setClassNameIndex(int class_name_index) {
        this.classnameIdx = class_name_index;
    }

    public void setConstantPool(ConstantPool constant_pool) {
        this.cpool = constant_pool;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public void setFileName(String file_name) {
        this.fileName = file_name;
    }

    public void setInterfaceNames(String[] interface_names) {
        this.interfacenames = interface_names;
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

    public void setSourceFileName(String source_file_name) {
        this.sourcefileName = source_file_name;
    }

    public void setSuperclassName(String superclass_name) {
        this.superclassname = superclass_name;
    }

    public void setSuperclassNameIndex(int superclass_name_index) {
        this.superclassnameIdx = superclass_name_index;
    }

    public String toString() {
        int i;
        String access = Utility.accessToString(this.modifiers, true);
        access = access.equals("") ? "" : access + " ";
        StringBuffer buf = new StringBuffer(access + Utility.classOrInterface(this.modifiers) + " " + this.classname + " extends " + Utility.compactClassName(this.superclassname, false) + '\n');
        int size = this.interfaces.length;
        if (size > 0) {
            buf.append("implements\t\t");
            for (i = 0; i < size; ++i) {
                buf.append(this.interfacenames[i]);
                if (i >= size - 1) continue;
                buf.append(", ");
            }
            buf.append('\n');
        }
        buf.append("filename\t\t" + this.fileName + '\n');
        buf.append("compiled from\t\t" + this.sourcefileName + '\n');
        buf.append("compiler version\t" + this.major + "." + this.minor + '\n');
        buf.append("access flags\t\t" + this.modifiers + '\n');
        buf.append("constant pool\t\t" + this.cpool.getLength() + " entries\n");
        buf.append("ACC_SUPER flag\t\t" + this.isSuper() + "\n");
        if (this.attributes.length > 0) {
            buf.append("\nAttribute(s):\n");
            for (i = 0; i < this.attributes.length; ++i) {
                buf.append(JavaClass.indent(this.attributes[i]));
            }
        }
        if (this.annotations != null && this.annotations.length > 0) {
            buf.append("\nAnnotation(s):\n");
            for (i = 0; i < this.annotations.length; ++i) {
                buf.append(JavaClass.indent(this.annotations[i]));
            }
        }
        if (this.fields.length > 0) {
            buf.append("\n" + this.fields.length + " fields:\n");
            for (i = 0; i < this.fields.length; ++i) {
                buf.append("\t" + this.fields[i] + '\n');
            }
        }
        if (this.methods.length > 0) {
            buf.append("\n" + this.methods.length + " methods:\n");
            for (i = 0; i < this.methods.length; ++i) {
                buf.append("\t" + this.methods[i] + '\n');
            }
        }
        return buf.toString();
    }

    private static final String indent(Object obj) {
        StringTokenizer tok = new StringTokenizer(obj.toString(), "\n");
        StringBuffer buf = new StringBuffer();
        while (tok.hasMoreTokens()) {
            buf.append("\t" + tok.nextToken() + "\n");
        }
        return buf.toString();
    }

    public final boolean isSuper() {
        return (this.modifiers & 0x20) != 0;
    }

    public final boolean isClass() {
        return (this.modifiers & 0x200) == 0;
    }

    public final boolean isAnonymous() {
        this.computeNestedTypeStatus();
        return this.isAnonymous;
    }

    public final boolean isNested() {
        this.computeNestedTypeStatus();
        return this.isNested;
    }

    private final void computeNestedTypeStatus() {
        if (this.computedNestedTypeStatus) {
            return;
        }
        for (int i = 0; i < this.attributes.length; ++i) {
            if (!(this.attributes[i] instanceof InnerClasses)) continue;
            InnerClass[] innerClasses = ((InnerClasses)this.attributes[i]).getInnerClasses();
            for (int j = 0; j < innerClasses.length; ++j) {
                boolean innerClassAttributeRefersToMe = false;
                String inner_class_name = this.cpool.getConstantString(innerClasses[j].getInnerClassIndex(), (byte)7);
                if ((inner_class_name = Utility.compactClassName(inner_class_name)).equals(this.getClassName())) {
                    innerClassAttributeRefersToMe = true;
                }
                if (!innerClassAttributeRefersToMe) continue;
                this.isNested = true;
                if (innerClasses[j].getInnerNameIndex() != 0) continue;
                this.isAnonymous = true;
            }
        }
        this.computedNestedTypeStatus = true;
    }

    public final boolean isAnnotation() {
        return (this.modifiers & 0x2000) != 0;
    }

    public final boolean isEnum() {
        return (this.modifiers & 0x4000) != 0;
    }

    public Repository getRepository() {
        if (this.repository == null) {
            this.repository = SyntheticRepository.getInstance();
        }
        return this.repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public final boolean instanceOf(JavaClass super_class) {
        if (this.equals(super_class)) {
            return true;
        }
        JavaClass[] super_classes = this.getSuperClasses();
        for (int i = 0; i < super_classes.length; ++i) {
            if (!super_classes[i].equals(super_class)) continue;
            return true;
        }
        if (super_class.isInterface()) {
            return this.implementationOf(super_class);
        }
        return false;
    }

    public boolean implementationOf(JavaClass inter) {
        if (!inter.isInterface()) {
            throw new IllegalArgumentException(inter.getClassName() + " is no interface");
        }
        if (this.equals(inter)) {
            return true;
        }
        Collection<JavaClass> superInterfaces = this.getAllInterfaces();
        for (JavaClass superInterface : superInterfaces) {
            if (!superInterface.equals(inter)) continue;
            return true;
        }
        return false;
    }

    public JavaClass getSuperClass() {
        if ("java.lang.Object".equals(this.getClassName())) {
            return null;
        }
        try {
            return this.getRepository().loadClass(this.getSuperclassName());
        }
        catch (ClassNotFoundException e) {
            System.err.println(e);
            return null;
        }
    }

    public JavaClass[] getSuperClasses() {
        JavaClass clazz = this;
        ArrayList<JavaClass> vec = new ArrayList<JavaClass>();
        for (clazz = clazz.getSuperClass(); clazz != null; clazz = clazz.getSuperClass()) {
            vec.add(clazz);
        }
        return vec.toArray(new JavaClass[vec.size()]);
    }

    public JavaClass[] getInterfaces() {
        String[] interfaces = this.getInterfaceNames();
        JavaClass[] classes = new JavaClass[interfaces.length];
        try {
            for (int i = 0; i < interfaces.length; ++i) {
                classes[i] = this.getRepository().loadClass(interfaces[i]);
            }
        }
        catch (ClassNotFoundException e) {
            System.err.println(e);
            return null;
        }
        return classes;
    }

    public Collection<JavaClass> getAllInterfaces() {
        LinkedList<JavaClass> queue = new LinkedList<JavaClass>();
        ArrayList<JavaClass> interfaceList = new ArrayList<JavaClass>();
        queue.add(this);
        while (!queue.isEmpty()) {
            JavaClass clazz = (JavaClass)queue.remove();
            JavaClass souper = clazz.getSuperClass();
            JavaClass[] interfaces = clazz.getInterfaces();
            if (clazz.isInterface()) {
                interfaceList.add(clazz);
            } else if (souper != null) {
                queue.add(souper);
            }
            for (int i = 0; i < interfaces.length; ++i) {
                queue.add(interfaces[i]);
            }
        }
        return interfaceList;
    }

    public final String getGenericSignature() {
        this.loadGenericSignatureInfoIfNecessary();
        return this.signatureAttributeString;
    }

    public boolean isGeneric() {
        this.loadGenericSignatureInfoIfNecessary();
        return this.isGeneric;
    }

    private void loadGenericSignatureInfoIfNecessary() {
        if (!this.searchedForSignatureAttribute) {
            this.signatureAttribute = AttributeUtils.getSignatureAttribute(this.attributes);
            this.signatureAttributeString = this.signatureAttribute == null ? null : this.signatureAttribute.getSignature();
            this.isGeneric = this.signatureAttribute != null && this.signatureAttributeString.charAt(0) == '<';
            this.searchedForSignatureAttribute = true;
        }
    }

    public final Signature getSignatureAttribute() {
        this.loadGenericSignatureInfoIfNecessary();
        return this.signatureAttribute;
    }
}

