/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.Modifiers;
import org.aspectj.apache.bcel.classfile.SourceFile;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisAnnos;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.MethodGen;
import org.aspectj.apache.bcel.generic.Type;

public class ClassGen
extends Modifiers
implements Cloneable {
    private String classname;
    private String superclassname;
    private String filename;
    private int classnameIndex = -1;
    private int superclassnameIndex = -1;
    private int major = 45;
    private int minor = 3;
    private ConstantPool cpool;
    private List<Field> fieldsList = new ArrayList<Field>();
    private List<Method> methodsList = new ArrayList<Method>();
    private List<Attribute> attributesList = new ArrayList<Attribute>();
    private List<String> interfaceList = new ArrayList<String>();
    private List<AnnotationGen> annotationsList = new ArrayList<AnnotationGen>();

    public ClassGen(String classname, String superclassname, String filename, int modifiers, String[] interfacenames, ConstantPool cpool) {
        this.classname = classname;
        this.superclassname = superclassname;
        this.filename = filename;
        this.modifiers = modifiers;
        this.cpool = cpool;
        if (filename != null) {
            this.addAttribute(new SourceFile(cpool.addUtf8("SourceFile"), 2, cpool.addUtf8(filename), cpool));
        }
        this.classnameIndex = cpool.addClass(classname);
        this.superclassnameIndex = cpool.addClass(superclassname);
        if (interfacenames != null) {
            for (String interfacename : interfacenames) {
                this.addInterface(interfacename);
            }
        }
    }

    public ClassGen(String classname, String superclassname, String filename, int modifiers, String[] interfacenames) {
        this(classname, superclassname, filename, modifiers, interfacenames, new ConstantPool());
    }

    public ClassGen(JavaClass clazz) {
        int i;
        Attribute[] attributes;
        this.classnameIndex = clazz.getClassNameIndex();
        this.superclassnameIndex = clazz.getSuperclassNameIndex();
        this.classname = clazz.getClassName();
        this.superclassname = clazz.getSuperclassName();
        this.filename = clazz.getSourceFileName();
        this.modifiers = clazz.getModifiers();
        this.cpool = clazz.getConstantPool().copy();
        this.major = clazz.getMajor();
        this.minor = clazz.getMinor();
        Method[] methods = clazz.getMethods();
        Field[] fields = clazz.getFields();
        String[] interfaces = clazz.getInterfaceNames();
        for (int i2 = 0; i2 < interfaces.length; ++i2) {
            this.addInterface(interfaces[i2]);
        }
        for (Attribute attr : attributes = clazz.getAttributes()) {
            List<AnnotationGen> annos;
            if (attr instanceof RuntimeVisAnnos) {
                RuntimeVisAnnos rva = (RuntimeVisAnnos)attr;
                annos = rva.getAnnotations();
                for (AnnotationGen a : annos) {
                    this.annotationsList.add(new AnnotationGen(a, this.cpool, false));
                }
                continue;
            }
            if (attr instanceof RuntimeInvisAnnos) {
                RuntimeInvisAnnos ria = (RuntimeInvisAnnos)attr;
                annos = ria.getAnnotations();
                for (AnnotationGen anno : annos) {
                    this.annotationsList.add(new AnnotationGen(anno, this.cpool, false));
                }
                continue;
            }
            this.attributesList.add(attr);
        }
        for (i = 0; i < methods.length; ++i) {
            this.addMethod(methods[i]);
        }
        for (i = 0; i < fields.length; ++i) {
            this.addField(fields[i]);
        }
    }

    public JavaClass getJavaClass() {
        int[] interfaces = this.getInterfaces();
        Field[] fields = this.getFields();
        Method[] methods = this.getMethods();
        List<Attribute> attributes = null;
        if (this.annotationsList.size() == 0) {
            attributes = this.attributesList;
        } else {
            attributes = new ArrayList<Attribute>();
            attributes.addAll(Utility.getAnnotationAttributes(this.cpool, this.annotationsList));
            attributes.addAll(this.attributesList);
        }
        ConstantPool cp = this.cpool.getFinalConstantPool();
        return new JavaClass(this.classnameIndex, this.superclassnameIndex, this.filename, this.major, this.minor, this.modifiers, cp, interfaces, fields, methods, attributes.toArray(new Attribute[attributes.size()]));
    }

    public void addInterface(String name) {
        this.interfaceList.add(name);
    }

    public void removeInterface(String name) {
        this.interfaceList.remove(name);
    }

    public int getMajor() {
        return this.major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMinor() {
        return this.minor;
    }

    public void addAttribute(Attribute a) {
        this.attributesList.add(a);
    }

    public void addAnnotation(AnnotationGen a) {
        this.annotationsList.add(a);
    }

    public void addMethod(Method m) {
        this.methodsList.add(m);
    }

    public void addEmptyConstructor(int access_flags) {
        InstructionList il = new InstructionList();
        il.append(InstructionConstants.THIS);
        il.append(new InvokeInstruction(183, this.cpool.addMethodref(this.superclassname, "<init>", "()V")));
        il.append(InstructionConstants.RETURN);
        MethodGen mg = new MethodGen(access_flags, Type.VOID, Type.NO_ARGS, null, "<init>", this.classname, il, this.cpool);
        mg.setMaxStack(1);
        mg.setMaxLocals();
        this.addMethod(mg.getMethod());
    }

    public void addField(Field f) {
        this.fieldsList.add(f);
    }

    public boolean containsField(Field f) {
        return this.fieldsList.contains(f);
    }

    public Field findsField(String name) {
        for (Field field : this.fieldsList) {
            if (!field.getName().equals(name)) continue;
            return field;
        }
        return null;
    }

    public Method containsMethod(String name, String signature) {
        for (Method method : this.methodsList) {
            if (!method.getName().equals(name) || !method.getSignature().equals(signature)) continue;
            return method;
        }
        return null;
    }

    public void removeAttribute(Attribute a) {
        this.attributesList.remove(a);
    }

    public void removeAnnotation(AnnotationGen a) {
        this.annotationsList.remove(a);
    }

    public void removeMethod(Method m) {
        this.methodsList.remove(m);
    }

    public void replaceMethod(Method old, Method new_) {
        if (new_ == null) {
            throw new ClassGenException("Replacement method must not be null");
        }
        int i = this.methodsList.indexOf(old);
        if (i < 0) {
            this.methodsList.add(new_);
        } else {
            this.methodsList.set(i, new_);
        }
    }

    public void replaceField(Field old, Field new_) {
        if (new_ == null) {
            throw new ClassGenException("Replacement method must not be null");
        }
        int i = this.fieldsList.indexOf(old);
        if (i < 0) {
            this.fieldsList.add(new_);
        } else {
            this.fieldsList.set(i, new_);
        }
    }

    public void removeField(Field f) {
        this.fieldsList.remove(f);
    }

    public String getClassName() {
        return this.classname;
    }

    public String getSuperclassName() {
        return this.superclassname;
    }

    public String getFileName() {
        return this.filename;
    }

    public void setClassName(String name) {
        this.classname = name.replace('/', '.');
        this.classnameIndex = this.cpool.addClass(name);
    }

    public void setSuperclassName(String name) {
        this.superclassname = name.replace('/', '.');
        this.superclassnameIndex = this.cpool.addClass(name);
    }

    public Method[] getMethods() {
        Method[] methods = new Method[this.methodsList.size()];
        this.methodsList.toArray(methods);
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methodsList.clear();
        for (int m = 0; m < methods.length; ++m) {
            this.addMethod(methods[m]);
        }
    }

    public void setFields(Field[] fs) {
        this.fieldsList.clear();
        for (int m = 0; m < fs.length; ++m) {
            this.addField(fs[m]);
        }
    }

    public void setMethodAt(Method method, int pos) {
        this.methodsList.set(pos, method);
    }

    public Method getMethodAt(int pos) {
        return this.methodsList.get(pos);
    }

    public String[] getInterfaceNames() {
        int size = this.interfaceList.size();
        String[] interfaces = new String[size];
        this.interfaceList.toArray(interfaces);
        return interfaces;
    }

    public int[] getInterfaces() {
        int size = this.interfaceList.size();
        int[] interfaces = new int[size];
        for (int i = 0; i < size; ++i) {
            interfaces[i] = this.cpool.addClass(this.interfaceList.get(i));
        }
        return interfaces;
    }

    public Field[] getFields() {
        Field[] fields = new Field[this.fieldsList.size()];
        this.fieldsList.toArray(fields);
        return fields;
    }

    public Collection<Attribute> getAttributes() {
        return this.attributesList;
    }

    public AnnotationGen[] getAnnotations() {
        AnnotationGen[] annotations = new AnnotationGen[this.annotationsList.size()];
        this.annotationsList.toArray(annotations);
        return annotations;
    }

    public ConstantPool getConstantPool() {
        return this.cpool;
    }

    public void setConstantPool(ConstantPool constant_pool) {
        this.cpool = constant_pool;
    }

    public void setClassNameIndex(int class_name_index) {
        this.classnameIndex = class_name_index;
        this.classname = this.cpool.getConstantString(class_name_index, (byte)7).replace('/', '.');
    }

    public void setSuperclassNameIndex(int superclass_name_index) {
        this.superclassnameIndex = superclass_name_index;
        this.superclassname = this.cpool.getConstantString(superclass_name_index, (byte)7).replace('/', '.');
    }

    public int getSuperclassNameIndex() {
        return this.superclassnameIndex;
    }

    public int getClassNameIndex() {
        return this.classnameIndex;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println(e);
            return null;
        }
    }

    public final boolean isAnnotation() {
        return (this.modifiers & 0x2000) != 0;
    }

    public final boolean isEnum() {
        return (this.modifiers & 0x4000) != 0;
    }

    public long getSUID() {
        try {
            int pos;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(this.getClassName());
            int classmods = 0;
            classmods |= this.isPublic() ? 1 : 0;
            classmods |= this.isFinal() ? 16 : 0;
            classmods |= this.isInterface() ? 512 : 0;
            if (this.isAbstract()) {
                if (this.isInterface()) {
                    if (this.methodsList.size() > 0) {
                        classmods |= 0x400;
                    }
                } else {
                    classmods |= 0x400;
                }
            }
            dos.writeInt(classmods);
            Object[] names = this.getInterfaceNames();
            if (names != null) {
                Arrays.sort(names);
                for (int i = 0; i < names.length; ++i) {
                    dos.writeUTF((String)names[i]);
                }
            }
            ArrayList<Field> relevantFields = new ArrayList<Field>();
            for (Field field : this.fieldsList) {
                if (field.isPrivate() && field.isStatic() || field.isPrivate() && field.isTransient()) continue;
                relevantFields.add(field);
            }
            Collections.sort(relevantFields, new FieldComparator());
            int relevantFlags = 223;
            for (Field field : relevantFields) {
                dos.writeUTF(field.getName());
                dos.writeInt(relevantFlags & field.getModifiers());
                dos.writeUTF(field.getType().getSignature());
            }
            ArrayList<Method> arrayList = new ArrayList<Method>();
            ArrayList<Method> arrayList2 = new ArrayList<Method>();
            boolean hasClinit = false;
            for (Method m : this.methodsList) {
                boolean couldBeInitializer;
                boolean bl = couldBeInitializer = m.getName().charAt(0) == '<';
                if (couldBeInitializer && m.getName().equals("<clinit>")) {
                    hasClinit = true;
                    continue;
                }
                if (couldBeInitializer && m.getName().equals("<init>")) {
                    if (m.isPrivate()) continue;
                    arrayList2.add(m);
                    continue;
                }
                if (m.isPrivate()) continue;
                arrayList.add(m);
            }
            Collections.sort(arrayList2, new ConstructorComparator());
            Collections.sort(arrayList, new MethodComparator());
            if (hasClinit) {
                dos.writeUTF("<clinit>");
                dos.writeInt(8);
                dos.writeUTF("()V");
            }
            relevantFlags = 3391;
            for (Method ctor : arrayList2) {
                dos.writeUTF(ctor.getName());
                dos.writeInt(relevantFlags & ctor.getModifiers());
                dos.writeUTF(ctor.getSignature().replace('/', '.'));
            }
            for (Method m : arrayList) {
                dos.writeUTF(m.getName());
                dos.writeInt(relevantFlags & m.getModifiers());
                dos.writeUTF(m.getSignature().replace('/', '.'));
            }
            dos.flush();
            dos.close();
            byte[] bs = baos.toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] result = md.digest(bs);
            long suid = 0L;
            int n = pos = result.length > 8 ? 7 : result.length - 1;
            while (pos >= 0) {
                suid = suid << 8 | (long)result[pos--] & 0xFFL;
            }
            return suid;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to calculate suid for " + this.getClassName() + ": " + e.toString());
        }
    }

    public boolean hasAttribute(String attributeName) {
        for (Attribute attr : this.attributesList) {
            if (!attr.getName().equals(attributeName)) continue;
            return true;
        }
        return false;
    }

    public Attribute getAttribute(String attributeName) {
        for (Attribute attr : this.attributesList) {
            if (!attr.getName().equals(attributeName)) continue;
            return attr;
        }
        return null;
    }

    private static class MethodComparator
    implements Comparator<Method> {
        private MethodComparator() {
        }

        @Override
        public int compare(Method m0, Method m1) {
            int result = m0.getName().compareTo(m1.getName());
            if (result == 0) {
                result = m0.getSignature().compareTo(m1.getSignature());
            }
            return result;
        }
    }

    private static class ConstructorComparator
    implements Comparator<Method> {
        private ConstructorComparator() {
        }

        @Override
        public int compare(Method m0, Method m1) {
            return m0.getSignature().compareTo(m1.getSignature());
        }
    }

    private static class FieldComparator
    implements Comparator<Field> {
        private FieldComparator() {
        }

        @Override
        public int compare(Field f0, Field f1) {
            return f0.getName().compareTo(f1.getName());
        }
    }
}

