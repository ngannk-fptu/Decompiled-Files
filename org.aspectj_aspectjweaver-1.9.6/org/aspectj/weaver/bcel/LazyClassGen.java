/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.Signature;
import org.aspectj.apache.bcel.classfile.Synthetic;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.generic.BasicType;
import org.aspectj.apache.bcel.generic.ClassGen;
import org.aspectj.apache.bcel.generic.FieldGen;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberImpl;
import org.aspectj.weaver.MemberKind;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.RuntimeVersion;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.SignatureUtils;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelField;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelShadow;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Range;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.bcel.asm.AsmDetector;
import org.aspectj.weaver.bcel.asm.StackMapAdder;

public final class LazyClassGen {
    private static final Type[] ARRAY_7STRING_INT = new Type[]{Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.INT};
    private static final Type[] ARRAY_8STRING_INT = new Type[]{Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.INT};
    private static final Type[] PARAMSIGNATURE_MAKESJP_METHOD = new Type[]{Type.STRING, Type.INT, Type.STRING, Type.CLASS, Type.CLASS_ARRAY, Type.STRING_ARRAY, Type.CLASS_ARRAY, Type.CLASS, Type.INT};
    private static final Type[] PARAMSIGNATURE_MAKESJP_CONSTRUCTOR = new Type[]{Type.STRING, Type.INT, Type.CLASS, Type.CLASS_ARRAY, Type.STRING_ARRAY, Type.CLASS_ARRAY, Type.INT};
    private static final Type[] PARAMSIGNATURE_MAKESJP_CATCHCLAUSE = new Type[]{Type.STRING, Type.CLASS, Type.CLASS, Type.STRING, Type.INT};
    private static final Type[] PARAMSIGNATURE_MAKESJP_FIELD = new Type[]{Type.STRING, Type.INT, Type.STRING, Type.CLASS, Type.CLASS, Type.INT};
    private static final Type[] PARAMSIGNATURE_MAKESJP_INITIALIZER = new Type[]{Type.STRING, Type.INT, Type.CLASS, Type.INT};
    private static final Type[] PARAMSIGNATURE_MAKESJP_MONITOR = new Type[]{Type.STRING, Type.CLASS, Type.INT};
    private static final Type[] PARAMSIGNATURE_MAKESJP_ADVICE = new Type[]{Type.STRING, Type.INT, Type.STRING, Type.CLASS, Type.CLASS_ARRAY, Type.STRING_ARRAY, Type.CLASS_ARRAY, Type.CLASS, Type.INT};
    private static final int ACC_SYNTHETIC = 4096;
    private static final String[] NO_STRINGS = new String[0];
    int highestLineNumber = 0;
    private final SortedMap<String, InlinedSourceFileInfo> inlinedFiles = new TreeMap<String, InlinedSourceFileInfo>();
    private boolean regenerateGenericSignatureAttribute = false;
    private BcelObjectType myType;
    private ClassGen myGen;
    private final ConstantPool cp;
    private final World world;
    private final String packageName;
    private final List<BcelField> fields = new ArrayList<BcelField>();
    private final List<LazyMethodGen> methodGens = new ArrayList<LazyMethodGen>();
    private final List<LazyClassGen> classGens = new ArrayList<LazyClassGen>();
    private final List<AnnotationGen> annotations = new ArrayList<AnnotationGen>();
    private int childCounter = 0;
    private final InstructionFactory fact;
    private boolean isSerializable = false;
    private boolean hasSerialVersionUIDField = false;
    private boolean serialVersionUIDRequiresInitialization = false;
    private long calculatedSerialVersionUID;
    private boolean hasClinit = false;
    private ResolvedType[] extraSuperInterfaces = null;
    private ResolvedType superclass = null;
    private Map<BcelShadow, Field> tjpFields = new HashMap<BcelShadow, Field>();
    Map<CacheKey, Field> annotationCachingFieldCache = new HashMap<CacheKey, Field>();
    private int tjpFieldsCounter = -1;
    private int annoFieldsCounter = 0;
    public static final ObjectType proceedingTjpType = new ObjectType("org.aspectj.lang.ProceedingJoinPoint");
    public static final ObjectType tjpType = new ObjectType("org.aspectj.lang.JoinPoint");
    public static final ObjectType staticTjpType = new ObjectType("org.aspectj.lang.JoinPoint$StaticPart");
    public static final ObjectType typeForAnnotation = new ObjectType("java.lang.annotation.Annotation");
    public static final ObjectType enclosingStaticTjpType = new ObjectType("org.aspectj.lang.JoinPoint$EnclosingStaticPart");
    private static final ObjectType sigType = new ObjectType("org.aspectj.lang.Signature");
    private static final ObjectType factoryType = new ObjectType("org.aspectj.runtime.reflect.Factory");
    private static final ObjectType classType = new ObjectType("java.lang.Class");

    void addInlinedSourceFileInfo(String fullpath, int highestLineNumber) {
        Object o = this.inlinedFiles.get(fullpath);
        if (o != null) {
            InlinedSourceFileInfo info = (InlinedSourceFileInfo)o;
            if (info.highestLineNumber < highestLineNumber) {
                info.highestLineNumber = highestLineNumber;
            }
        } else {
            this.inlinedFiles.put(fullpath, new InlinedSourceFileInfo(highestLineNumber));
        }
    }

    void calculateSourceDebugExtensionOffsets() {
        int i = LazyClassGen.roundUpToHundreds(this.highestLineNumber);
        for (InlinedSourceFileInfo element : this.inlinedFiles.values()) {
            element.offset = i;
            i = LazyClassGen.roundUpToHundreds(i + element.highestLineNumber);
        }
    }

    private static int roundUpToHundreds(int i) {
        return (i / 100 + 1) * 100;
    }

    int getSourceDebugExtensionOffset(String fullpath) {
        return ((InlinedSourceFileInfo)this.inlinedFiles.get((Object)fullpath)).offset;
    }

    public static void disassemble(String path, String name, PrintStream out) throws IOException {
        if (null == out) {
            return;
        }
        BcelWorld world = new BcelWorld(path);
        UnresolvedType ut = UnresolvedType.forName(name);
        ut.setNeedsModifiableDelegate(true);
        LazyClassGen clazz = new LazyClassGen(BcelWorld.getBcelObjectType(world.resolve(ut)));
        clazz.print(out);
        out.println();
    }

    public String getNewGeneratedNameTag() {
        return new Integer(this.childCounter++).toString();
    }

    public LazyClassGen(String class_name, String super_class_name, String file_name, int access_flags, String[] interfaces, World world) {
        this.packageName = null;
        this.myGen = new ClassGen(class_name, super_class_name, file_name, access_flags, interfaces);
        this.cp = this.myGen.getConstantPool();
        this.fact = new InstructionFactory(this.myGen, this.cp);
        this.regenerateGenericSignatureAttribute = true;
        this.world = world;
    }

    public void setMajorMinor(int major, int minor) {
        this.myGen.setMajor(major);
        this.myGen.setMinor(minor);
    }

    public int getMajor() {
        return this.myGen.getMajor();
    }

    public int getMinor() {
        return this.myGen.getMinor();
    }

    public LazyClassGen(BcelObjectType myType) {
        int i;
        ResolvedMember[] methods;
        this.packageName = null;
        this.myGen = new ClassGen(myType.getJavaClass());
        this.cp = this.myGen.getConstantPool();
        this.fact = new InstructionFactory(this.myGen, this.cp);
        this.myType = myType;
        this.world = myType.getResolvedTypeX().getWorld();
        if (this.implementsSerializable(this.getType())) {
            this.isSerializable = true;
            this.hasSerialVersionUIDField = LazyClassGen.hasSerialVersionUIDField(this.getType());
            methods = this.getType().getDeclaredMethods();
            for (i = 0; i < methods.length; ++i) {
                ResolvedMember method = methods[i];
                if (!method.getName().equals("<clinit>")) continue;
                if (method.getKind() != Member.STATIC_INITIALIZATION) {
                    throw new RuntimeException("qui?");
                }
                this.hasClinit = true;
            }
            if (!this.getType().isInterface() && !this.hasSerialVersionUIDField && this.world.isAddSerialVerUID()) {
                this.calculatedSerialVersionUID = this.myGen.getSUID();
                FieldGen fg = new FieldGen(26, BasicType.LONG, "serialVersionUID", this.getConstantPool());
                this.addField(fg);
                this.hasSerialVersionUIDField = true;
                this.serialVersionUIDRequiresInitialization = true;
                if (this.world.getLint().calculatingSerialVersionUID.isEnabled()) {
                    this.world.getLint().calculatingSerialVersionUID.signal(new String[]{this.getClassName(), Long.toString(this.calculatedSerialVersionUID) + "L"}, null, null);
                }
            }
        }
        methods = myType.getDeclaredMethods();
        for (i = 0; i < methods.length; ++i) {
            this.addMethodGen(new LazyMethodGen((BcelMethod)methods[i], this));
        }
        ResolvedMember[] fields = myType.getDeclaredFields();
        for (int i2 = 0; i2 < fields.length; ++i2) {
            this.fields.add((BcelField)fields[i2]);
        }
    }

    public static boolean hasSerialVersionUIDField(ResolvedType type) {
        ResolvedMember[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            ResolvedMember field = fields[i];
            if (!field.getName().equals("serialVersionUID") || !Modifier.isStatic(field.getModifiers()) || !field.getType().equals(UnresolvedType.LONG)) continue;
            return true;
        }
        return false;
    }

    public String getInternalClassName() {
        return this.getConstantPool().getConstantString_CONSTANTClass(this.myGen.getClassNameIndex());
    }

    public String getInternalFileName() {
        String str = this.getInternalClassName();
        int index = str.lastIndexOf(47);
        if (index == -1) {
            return this.getFileName();
        }
        return str.substring(0, index + 1) + this.getFileName();
    }

    public String getPackageName() {
        if (this.packageName != null) {
            return this.packageName;
        }
        String str = this.getInternalClassName();
        int index = str.indexOf("<");
        if (index != -1) {
            str = str.substring(0, index);
        }
        if ((index = str.lastIndexOf("/")) == -1) {
            return "";
        }
        return str.substring(0, index).replace('/', '.');
    }

    public void addMethodGen(LazyMethodGen gen) {
        this.methodGens.add(gen);
        if (this.highestLineNumber < gen.highestLineNumber) {
            this.highestLineNumber = gen.highestLineNumber;
        }
    }

    public boolean removeMethodGen(LazyMethodGen gen) {
        return this.methodGens.remove(gen);
    }

    public void addMethodGen(LazyMethodGen gen, ISourceLocation sourceLocation) {
        this.addMethodGen(gen);
        if (!gen.getMethod().isPrivate()) {
            this.warnOnAddedMethod(gen.getMethod(), sourceLocation);
        }
    }

    public void errorOnAddedField(FieldGen field, ISourceLocation sourceLocation) {
        if (this.isSerializable && !this.hasSerialVersionUIDField) {
            this.getWorld().getLint().serialVersionUIDBroken.signal(new String[]{this.myType.getResolvedTypeX().getName(), field.getName()}, sourceLocation, null);
        }
    }

    public void warnOnAddedInterface(String name, ISourceLocation sourceLocation) {
        this.warnOnModifiedSerialVersionUID(sourceLocation, "added interface " + name);
    }

    public void warnOnAddedMethod(Method method, ISourceLocation sourceLocation) {
        this.warnOnModifiedSerialVersionUID(sourceLocation, "added non-private method " + method.getName());
    }

    public void warnOnAddedStaticInitializer(Shadow shadow, ISourceLocation sourceLocation) {
        if (!this.hasClinit) {
            this.warnOnModifiedSerialVersionUID(sourceLocation, "added static initializer");
        }
    }

    public void warnOnModifiedSerialVersionUID(ISourceLocation sourceLocation, String reason) {
        if (this.isSerializable && !this.hasSerialVersionUIDField) {
            this.getWorld().getLint().needsSerialVersionUIDField.signal(new String[]{this.myType.getResolvedTypeX().getName().toString(), reason}, sourceLocation, null);
        }
    }

    public World getWorld() {
        return this.world;
    }

    public List<LazyMethodGen> getMethodGens() {
        return this.methodGens;
    }

    public List<BcelField> getFieldGens() {
        return this.fields;
    }

    public boolean fieldExists(String name) {
        for (BcelField f : this.fields) {
            if (!f.getName().equals(name)) continue;
            return true;
        }
        return false;
    }

    private void writeBack(BcelWorld world) {
        if (this.getConstantPool().getSize() > Short.MAX_VALUE) {
            this.reportClassTooBigProblem();
            return;
        }
        if (this.annotations.size() > 0) {
            for (AnnotationGen element : this.annotations) {
                this.myGen.addAnnotation(element);
            }
        }
        if (!this.myGen.hasAttribute("org.aspectj.weaver.WeaverVersion")) {
            this.myGen.addAttribute(Utility.bcelAttribute(new AjAttribute.WeaverVersionInfo(), this.getConstantPool()));
        }
        if (world.isOverWeaving()) {
            if (this.myGen.hasAttribute("org.aspectj.weaver.WeaverState") && this.myType != null && this.myType.getWeaverState() != null) {
                this.myGen.removeAttribute(this.myGen.getAttribute("org.aspectj.weaver.WeaverState"));
                this.myGen.addAttribute(Utility.bcelAttribute(new AjAttribute.WeaverState(this.myType.getWeaverState()), this.getConstantPool()));
            }
        } else if (!this.myGen.hasAttribute("org.aspectj.weaver.WeaverState") && this.myType != null && this.myType.getWeaverState() != null) {
            this.myGen.addAttribute(Utility.bcelAttribute(new AjAttribute.WeaverState(this.myType.getWeaverState()), this.getConstantPool()));
        }
        this.addAjcInitializers();
        boolean sourceDebugExtensionSupportSwitchedOn = false;
        if (sourceDebugExtensionSupportSwitchedOn) {
            this.calculateSourceDebugExtensionOffsets();
        }
        int len = this.methodGens.size();
        this.myGen.setMethods(Method.NoMethods);
        for (LazyMethodGen lazyMethodGen : this.methodGens) {
            if (this.isEmptyClinit(lazyMethodGen)) continue;
            this.myGen.addMethod(lazyMethodGen.getMethod());
        }
        len = this.fields.size();
        this.myGen.setFields(Field.NoFields);
        for (int i = 0; i < len; ++i) {
            BcelField bcelField = this.fields.get(i);
            this.myGen.addField(bcelField.getField(this.cp));
        }
        if (sourceDebugExtensionSupportSwitchedOn && this.inlinedFiles.size() != 0 && LazyClassGen.hasSourceDebugExtensionAttribute(this.myGen)) {
            world.showMessage(IMessage.WARNING, WeaverMessages.format("overwriteJSR45", this.getFileName()), null, null);
        }
        this.fixupGenericSignatureAttribute();
    }

    private void fixupGenericSignatureAttribute() {
        if (this.getWorld() != null && !this.getWorld().isInJava5Mode()) {
            return;
        }
        if (!this.regenerateGenericSignatureAttribute) {
            return;
        }
        Signature sigAttr = null;
        if (this.myType != null) {
            sigAttr = (Signature)this.myGen.getAttribute("Signature");
        }
        boolean needAttribute = false;
        if (sigAttr != null) {
            needAttribute = true;
        }
        if (!needAttribute) {
            ResolvedType superclassRTX;
            if (this.myType != null) {
                int i;
                ResolvedType[] interfaceRTXs = this.myType.getDeclaredInterfaces();
                for (i = 0; i < interfaceRTXs.length; ++i) {
                    ResolvedType typeX = interfaceRTXs[i];
                    if (!typeX.isGenericType() && !typeX.isParameterizedType()) continue;
                    needAttribute = true;
                }
                if (this.extraSuperInterfaces != null) {
                    for (i = 0; i < this.extraSuperInterfaces.length; ++i) {
                        ResolvedType interfaceType = this.extraSuperInterfaces[i];
                        if (!interfaceType.isGenericType() && !interfaceType.isParameterizedType()) continue;
                        needAttribute = true;
                    }
                }
            }
            if (this.myType == null) {
                superclassRTX = this.superclass;
                if (superclassRTX != null && (superclassRTX.isGenericType() || superclassRTX.isParameterizedType())) {
                    needAttribute = true;
                }
            } else {
                superclassRTX = this.getSuperClass();
                if (superclassRTX.isGenericType() || superclassRTX.isParameterizedType()) {
                    needAttribute = true;
                }
            }
        }
        if (needAttribute) {
            TypeVariable[] tVars;
            StringBuffer signature = new StringBuffer();
            if (this.myType != null && (tVars = this.myType.getTypeVariables()).length > 0) {
                signature.append("<");
                for (int i = 0; i < tVars.length; ++i) {
                    TypeVariable variable = tVars[i];
                    signature.append(variable.getSignatureForAttribute());
                }
                signature.append(">");
            }
            String supersig = this.getSuperClass().getSignatureForAttribute();
            signature.append(supersig);
            if (this.myType != null) {
                String s;
                int i;
                ResolvedType[] interfaceRTXs = this.myType.getDeclaredInterfaces();
                for (i = 0; i < interfaceRTXs.length; ++i) {
                    s = interfaceRTXs[i].getSignatureForAttribute();
                    signature.append(s);
                }
                if (this.extraSuperInterfaces != null) {
                    for (i = 0; i < this.extraSuperInterfaces.length; ++i) {
                        s = this.extraSuperInterfaces[i].getSignatureForAttribute();
                        signature.append(s);
                    }
                }
            }
            if (sigAttr != null) {
                this.myGen.removeAttribute(sigAttr);
            }
            this.myGen.addAttribute(this.createSignatureAttribute(signature.toString()));
        }
    }

    private Signature createSignatureAttribute(String signature) {
        int nameIndex = this.cp.addUtf8("Signature");
        int sigIndex = this.cp.addUtf8(signature);
        return new Signature(nameIndex, 2, sigIndex, this.cp);
    }

    private void reportClassTooBigProblem() {
        this.myGen = new ClassGen(this.myGen.getClassName(), this.myGen.getSuperclassName(), this.myGen.getFileName(), this.myGen.getModifiers(), this.myGen.getInterfaceNames());
        this.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("classTooBig", this.getClassName()), new SourceLocation(new File(this.myGen.getFileName()), 0), null);
    }

    private static boolean hasSourceDebugExtensionAttribute(ClassGen gen) {
        return gen.hasAttribute("SourceDebugExtension");
    }

    public JavaClass getJavaClass(BcelWorld world) {
        this.writeBack(world);
        return this.myGen.getJavaClass();
    }

    public byte[] getJavaClassBytesIncludingReweavable(BcelWorld world) {
        WeaverStateInfo wsi;
        this.writeBack(world);
        byte[] wovenClassFileData = this.myGen.getJavaClass().getBytes();
        if (this.myGen.getMajor() == 50 && world.shouldGenerateStackMaps() || this.myGen.getMajor() > 50) {
            if (!AsmDetector.isAsmAround) {
                throw new BCException("Unable to find Asm for stackmap generation (Looking for 'aj.org.objectweb.asm.ClassReader'). Stackmap generation for woven code is required to avoid verify errors on a Java 1.7 or higher runtime");
            }
            wovenClassFileData = StackMapAdder.addStackMaps(world, wovenClassFileData);
        }
        if ((wsi = this.myType.getWeaverState()) != null && wsi.isReweavable() && !world.isOverWeaving()) {
            return wsi.replaceKeyWithDiff(wovenClassFileData);
        }
        return wovenClassFileData;
    }

    public void addGeneratedInner(LazyClassGen newClass) {
        this.classGens.add(newClass);
    }

    public void addInterface(ResolvedType newInterface, ISourceLocation sourceLocation) {
        this.regenerateGenericSignatureAttribute = true;
        if (this.extraSuperInterfaces == null) {
            this.extraSuperInterfaces = new ResolvedType[1];
            this.extraSuperInterfaces[0] = newInterface;
        } else {
            ResolvedType[] x = new ResolvedType[this.extraSuperInterfaces.length + 1];
            System.arraycopy(this.extraSuperInterfaces, 0, x, 1, this.extraSuperInterfaces.length);
            x[0] = newInterface;
            this.extraSuperInterfaces = x;
        }
        this.myGen.addInterface(newInterface.getRawName());
        if (!newInterface.equals(UnresolvedType.SERIALIZABLE)) {
            this.warnOnAddedInterface(newInterface.getName(), sourceLocation);
        }
    }

    public void setSuperClass(ResolvedType newSuperclass) {
        this.regenerateGenericSignatureAttribute = true;
        this.superclass = newSuperclass;
        if (newSuperclass.getGenericType() != null) {
            newSuperclass = newSuperclass.getGenericType();
        }
        this.myGen.setSuperclassName(newSuperclass.getName());
    }

    public ResolvedType getSuperClass() {
        if (this.superclass != null) {
            return this.superclass;
        }
        return this.myType.getSuperclass();
    }

    public String[] getInterfaceNames() {
        return this.myGen.getInterfaceNames();
    }

    private List<LazyClassGen> getClassGens() {
        ArrayList<LazyClassGen> ret = new ArrayList<LazyClassGen>();
        ret.add(this);
        ret.addAll(this.classGens);
        return ret;
    }

    public List<UnwovenClassFile.ChildClass> getChildClasses(BcelWorld world) {
        if (this.classGens.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<UnwovenClassFile.ChildClass> ret = new ArrayList<UnwovenClassFile.ChildClass>();
        for (LazyClassGen clazz : this.classGens) {
            byte[] bytes = clazz.getJavaClass(world).getBytes();
            String name = clazz.getName();
            int index = name.lastIndexOf(36);
            name = name.substring(index + 1);
            ret.add(new UnwovenClassFile.ChildClass(name, bytes));
        }
        return ret;
    }

    public String toString() {
        return this.toShortString();
    }

    public String toShortString() {
        String s = org.aspectj.apache.bcel.classfile.Utility.accessToString(this.myGen.getModifiers(), true);
        if (!s.equals("")) {
            s = s + " ";
        }
        s = s + org.aspectj.apache.bcel.classfile.Utility.classOrInterface(this.myGen.getModifiers());
        s = s + " ";
        s = s + this.myGen.getClassName();
        return s;
    }

    public String toLongString() {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        this.print(new PrintStream(s));
        return new String(s.toByteArray());
    }

    public void print() {
        this.print(System.out);
    }

    public void print(PrintStream out) {
        List<LazyClassGen> classGens = this.getClassGens();
        Iterator<LazyClassGen> iter = classGens.iterator();
        while (iter.hasNext()) {
            LazyClassGen element = iter.next();
            element.printOne(out);
            if (!iter.hasNext()) continue;
            out.println();
        }
    }

    private void printOne(PrintStream out) {
        out.print(this.toShortString());
        out.print(" extends ");
        out.print(org.aspectj.apache.bcel.classfile.Utility.compactClassName(this.myGen.getSuperclassName(), false));
        int size = this.myGen.getInterfaces().length;
        if (size > 0) {
            out.print(" implements ");
            for (int i = 0; i < size; ++i) {
                out.print(this.myGen.getInterfaceNames()[i]);
                if (i >= size - 1) continue;
                out.print(", ");
            }
        }
        out.print(":");
        out.println();
        if (this.myType != null) {
            this.myType.printWackyStuff(out);
        }
        Field[] fields = this.myGen.getFields();
        int len = fields.length;
        for (int i = 0; i < len; ++i) {
            out.print("  ");
            out.println(fields[i]);
        }
        List<LazyMethodGen> methodGens = this.getMethodGens();
        Iterator<LazyMethodGen> iter = methodGens.iterator();
        while (iter.hasNext()) {
            LazyMethodGen gen = iter.next();
            if (this.isEmptyClinit(gen)) continue;
            gen.print(out, this.myType != null ? this.myType.getWeaverVersionAttribute() : AjAttribute.WeaverVersionInfo.UNKNOWN);
            if (!iter.hasNext()) continue;
            out.println();
        }
        out.println("end " + this.toShortString());
    }

    private boolean isEmptyClinit(LazyMethodGen gen) {
        if (!gen.getName().equals("<clinit>")) {
            return false;
        }
        for (InstructionHandle start = gen.getBody().getStart(); start != null; start = start.getNext()) {
            if (Range.isRangeHandle(start) || start.getInstruction().opcode == 177) {
                continue;
            }
            return false;
        }
        return true;
    }

    public ConstantPool getConstantPool() {
        return this.cp;
    }

    public String getName() {
        return this.myGen.getClassName();
    }

    public boolean isWoven() {
        return this.myType.getWeaverState() != null;
    }

    public boolean isReweavable() {
        if (this.myType.getWeaverState() == null) {
            return true;
        }
        return this.myType.getWeaverState().isReweavable();
    }

    public Set<String> getAspectsAffectingType() {
        if (this.myType.getWeaverState() == null) {
            return null;
        }
        return this.myType.getWeaverState().getAspectsAffectingType();
    }

    public WeaverStateInfo getOrCreateWeaverStateInfo(boolean inReweavableMode) {
        WeaverStateInfo ret = this.myType.getWeaverState();
        if (ret != null) {
            return ret;
        }
        ret = new WeaverStateInfo(inReweavableMode);
        this.myType.setWeaverState(ret);
        return ret;
    }

    public InstructionFactory getFactory() {
        return this.fact;
    }

    public LazyMethodGen getStaticInitializer() {
        for (LazyMethodGen gen : this.methodGens) {
            if (!gen.getName().equals("<clinit>")) continue;
            return gen;
        }
        LazyMethodGen clinit = new LazyMethodGen(8, Type.VOID, "<clinit>", new Type[0], NO_STRINGS, this);
        clinit.getBody().insert(InstructionConstants.RETURN);
        this.methodGens.add(clinit);
        return clinit;
    }

    public LazyMethodGen getAjcPreClinit() {
        if (this.isInterface()) {
            throw new IllegalStateException();
        }
        for (LazyMethodGen methodGen : this.methodGens) {
            if (!methodGen.getName().equals("ajc$preClinit")) continue;
            return methodGen;
        }
        LazyMethodGen ajcPreClinit = new LazyMethodGen(10, Type.VOID, "ajc$preClinit", Type.NO_ARGS, NO_STRINGS, this);
        ajcPreClinit.getBody().insert(InstructionConstants.RETURN);
        this.methodGens.add(ajcPreClinit);
        this.getStaticInitializer().getBody().insert(Utility.createInvoke(this.fact, ajcPreClinit));
        return ajcPreClinit;
    }

    public LazyMethodGen createExtendedAjcPreClinit(LazyMethodGen previousPreClinit, int i) {
        LazyMethodGen ajcPreClinit = new LazyMethodGen(10, Type.VOID, "ajc$preClinit" + i, Type.NO_ARGS, NO_STRINGS, this);
        ajcPreClinit.getBody().insert(InstructionConstants.RETURN);
        this.methodGens.add(ajcPreClinit);
        previousPreClinit.getBody().insert(Utility.createInvoke(this.fact, ajcPreClinit));
        return ajcPreClinit;
    }

    public Field getTjpField(BcelShadow shadow, boolean isEnclosingJp) {
        Field tjpField = this.tjpFields.get(shadow);
        if (tjpField != null) {
            return tjpField;
        }
        int modifiers = 8;
        if (shadow.getEnclosingClass().isInterface()) {
            modifiers |= 0x10;
        }
        LazyMethodGen encMethod = shadow.getEnclosingMethod();
        boolean shadowIsInAroundAdvice = false;
        if (encMethod != null && encMethod.getName().startsWith("ajc$around")) {
            shadowIsInAroundAdvice = true;
        }
        modifiers = this.getType().isInterface() || shadowIsInAroundAdvice ? (modifiers |= 1) : (modifiers |= 2);
        ObjectType jpType = null;
        if (this.world.isTargettingAspectJRuntime12()) {
            jpType = staticTjpType;
        } else {
            ObjectType objectType = jpType = isEnclosingJp ? enclosingStaticTjpType : staticTjpType;
        }
        if (this.tjpFieldsCounter == -1) {
            if (!this.world.isOverWeaving()) {
                this.tjpFieldsCounter = 0;
            } else {
                List<BcelField> existingFields = this.getFieldGens();
                if (existingFields == null) {
                    this.tjpFieldsCounter = 0;
                } else {
                    MemberImpl lastField = null;
                    for (BcelField field : existingFields) {
                        if (!field.getName().startsWith("ajc$tjp_")) continue;
                        lastField = field;
                    }
                    this.tjpFieldsCounter = lastField == null ? 0 : Integer.parseInt(lastField.getName().substring(8)) + 1;
                }
            }
        }
        if (!this.isInterface() && this.world.isTransientTjpFields()) {
            modifiers |= 0x80;
        }
        FieldGen fGen = new FieldGen(modifiers, jpType, "ajc$tjp_" + this.tjpFieldsCounter++, this.getConstantPool());
        this.addField(fGen);
        tjpField = fGen.getField();
        this.tjpFields.put(shadow, tjpField);
        return tjpField;
    }

    public Field getAnnotationCachingField(BcelShadow shadow, ResolvedType toType, boolean isWithin) {
        CacheKey cacheKey = new CacheKey(shadow, toType, isWithin);
        Field field = this.annotationCachingFieldCache.get(cacheKey);
        if (field == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("ajc$anno$");
            sb.append(this.annoFieldsCounter++);
            FieldGen annotationCacheField = new FieldGen(10, typeForAnnotation, sb.toString(), this.cp);
            this.addField(annotationCacheField);
            field = annotationCacheField.getField();
            this.annotationCachingFieldCache.put(cacheKey, field);
        }
        return field;
    }

    private void addAjcInitializers() {
        if (this.tjpFields.size() == 0 && !this.serialVersionUIDRequiresInitialization) {
            return;
        }
        InstructionList[] il = null;
        if (this.tjpFields.size() > 0) {
            il = this.initializeAllTjps();
        }
        if (this.serialVersionUIDRequiresInitialization) {
            InstructionList[] ilSVUID = new InstructionList[]{new InstructionList()};
            ilSVUID[0].append(InstructionFactory.PUSH(this.getConstantPool(), this.calculatedSerialVersionUID));
            ilSVUID[0].append(this.getFactory().createFieldAccess(this.getClassName(), "serialVersionUID", BasicType.LONG, (short)179));
            if (il == null) {
                il = ilSVUID;
            } else {
                InstructionList[] newIl = new InstructionList[il.length + ilSVUID.length];
                System.arraycopy(il, 0, newIl, 0, il.length);
                System.arraycopy(ilSVUID, 0, newIl, il.length, ilSVUID.length);
                il = newIl;
            }
        }
        LazyMethodGen nextMethod = null;
        LazyMethodGen prevMethod = this.isInterface() ? this.getStaticInitializer() : this.getAjcPreClinit();
        for (int counter = 1; counter <= il.length; ++counter) {
            if (il.length > counter) {
                nextMethod = this.createExtendedAjcPreClinit(prevMethod, counter);
            }
            prevMethod.getBody().insert(il[counter - 1]);
            prevMethod = nextMethod;
        }
    }

    private InstructionList initInstructionList() {
        InstructionList list = new InstructionList();
        InstructionFactory fact = this.getFactory();
        list.append(fact.createNew(factoryType));
        list.append(InstructionFactory.createDup(1));
        list.append(InstructionFactory.PUSH(this.getConstantPool(), this.getFileName()));
        list.append(fact.PUSHCLASS(this.cp, this.myGen.getClassName()));
        list.append(fact.createInvoke(factoryType.getClassName(), "<init>", Type.VOID, new Type[]{Type.STRING, classType}, (short)183));
        list.append(InstructionFactory.createStore(factoryType, 0));
        return list;
    }

    private InstructionList[] initializeAllTjps() {
        Vector<InstructionList> lists = new Vector<InstructionList>();
        InstructionList list = this.initInstructionList();
        lists.add(list);
        ArrayList<Map.Entry<BcelShadow, Field>> entries = new ArrayList<Map.Entry<BcelShadow, Field>>(this.tjpFields.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<BcelShadow, Field>>(){

            @Override
            public int compare(Map.Entry<BcelShadow, Field> a, Map.Entry<BcelShadow, Field> b) {
                return a.getValue().getName().compareTo(b.getValue().getName());
            }
        });
        long estimatedSize = 0L;
        for (Map.Entry entry : entries) {
            if (estimatedSize > 65536L) {
                estimatedSize = 0L;
                list = this.initInstructionList();
                lists.add(list);
            }
            estimatedSize += (long)((Field)entry.getValue()).getSignature().getBytes().length;
            this.initializeTjp(this.fact, list, (Field)entry.getValue(), (BcelShadow)entry.getKey());
        }
        InstructionList[] listArrayModel = new InstructionList[1];
        return lists.toArray(listArrayModel);
    }

    private void initializeTjp(InstructionFactory fact, InstructionList list, Field field, BcelShadow shadow) {
        BcelWorld w;
        if (this.world.getTargetAspectjRuntimeLevel() == RuntimeVersion.V1_9) {
            this.initializeTjpOptimal(fact, list, field, shadow);
            return;
        }
        boolean fastSJP = false;
        boolean isFastSJPAvailable = shadow.getWorld().isTargettingRuntime1_6_10() && !enclosingStaticTjpType.equals(field.getType());
        Member sig = shadow.getSignature();
        list.append(InstructionFactory.createLoad(factoryType, 0));
        list.append(InstructionFactory.PUSH(this.getConstantPool(), shadow.getKind().getName()));
        if (this.world.isTargettingAspectJRuntime12() || !isFastSJPAvailable || !sig.getKind().equals(Member.METHOD)) {
            list.append(InstructionFactory.createLoad(factoryType, 0));
        }
        String signatureMakerName = SignatureUtils.getSignatureMakerName(sig);
        ObjectType signatureType = new ObjectType(SignatureUtils.getSignatureType(sig));
        UnresolvedType[] exceptionTypes = null;
        if (this.world.isTargettingAspectJRuntime12()) {
            list.append(InstructionFactory.PUSH(this.cp, SignatureUtils.getSignatureString(sig, shadow.getWorld())));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY1, (short)182));
        } else if (sig.getKind().equals(Member.METHOD)) {
            w = shadow.getWorld();
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getModifiers(w))));
            list.append(InstructionFactory.PUSH(this.cp, sig.getName()));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterTypes())));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterNames(w))));
            exceptionTypes = sig.getExceptions(w);
            if (isFastSJPAvailable && exceptionTypes.length == 0) {
                fastSJP = true;
            } else {
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(exceptionTypes)));
            }
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getReturnType())));
            if (isFastSJPAvailable) {
                fastSJP = true;
            } else {
                list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY7, (short)182));
            }
        } else if (sig.getKind().equals(Member.MONITORENTER)) {
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY1, (short)182));
        } else if (sig.getKind().equals(Member.MONITOREXIT)) {
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY1, (short)182));
        } else if (sig.getKind().equals(Member.HANDLER)) {
            w = shadow.getWorld();
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterTypes())));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterNames(w))));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY3, (short)182));
        } else if (sig.getKind().equals(Member.CONSTRUCTOR)) {
            w = shadow.getWorld();
            if (w.isJoinpointArrayConstructionEnabled() && sig.getDeclaringType().isArray()) {
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(1)));
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterTypes())));
                list.append(InstructionFactory.PUSH(this.cp, ""));
                list.append(InstructionFactory.PUSH(this.cp, ""));
                list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY5, (short)182));
            } else {
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getModifiers(w))));
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterTypes())));
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterNames(w))));
                list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getExceptions(w))));
                list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY5, (short)182));
            }
        } else if (sig.getKind().equals(Member.FIELD)) {
            w = shadow.getWorld();
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getModifiers(w))));
            list.append(InstructionFactory.PUSH(this.cp, sig.getName()));
            UnresolvedType dType = sig.getDeclaringType();
            if (dType.getTypekind() == UnresolvedType.TypeKind.PARAMETERIZED || dType.getTypekind() == UnresolvedType.TypeKind.GENERIC) {
                dType = sig.getDeclaringType().resolve(this.world).getGenericType();
            }
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(dType)));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getReturnType())));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY4, (short)182));
        } else if (sig.getKind().equals(Member.ADVICE)) {
            w = shadow.getWorld();
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getModifiers(w))));
            list.append(InstructionFactory.PUSH(this.cp, sig.getName()));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterTypes())));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getParameterNames(w))));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getExceptions(w))));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getReturnType())));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, new Type[]{Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.STRING}, (short)182));
        } else if (sig.getKind().equals(Member.STATIC_INITIALIZATION)) {
            w = shadow.getWorld();
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getModifiers(w))));
            list.append(InstructionFactory.PUSH(this.cp, this.makeString(sig.getDeclaringType())));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY2, (short)182));
        } else {
            list.append(InstructionFactory.PUSH(this.cp, SignatureUtils.getSignatureString(sig, shadow.getWorld())));
            list.append(fact.createInvoke(factoryType.getClassName(), signatureMakerName, signatureType, Type.STRINGARRAY1, (short)182));
        }
        list.append(Utility.createConstant(fact, shadow.getSourceLine()));
        if (this.world.isTargettingAspectJRuntime12()) {
            list.append(fact.createInvoke(factoryType.getClassName(), "makeSJP", staticTjpType, new Type[]{Type.STRING, sigType, Type.INT}, (short)182));
            list.append(fact.createFieldAccess(this.getClassName(), field.getName(), staticTjpType, (short)179));
        } else {
            String factoryMethod;
            if (staticTjpType.equals(field.getType())) {
                factoryMethod = "makeSJP";
            } else if (enclosingStaticTjpType.equals(field.getType())) {
                factoryMethod = "makeESJP";
            } else {
                throw new Error("should not happen");
            }
            if (fastSJP) {
                if (exceptionTypes != null && exceptionTypes.length != 0) {
                    list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), ARRAY_8STRING_INT, (short)182));
                } else {
                    list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), ARRAY_7STRING_INT, (short)182));
                }
            } else {
                list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), new Type[]{Type.STRING, sigType, Type.INT}, (short)182));
            }
            list.append(fact.createFieldAccess(this.getClassName(), field.getName(), field.getType(), (short)179));
        }
    }

    public String getFactoryMethod(Field field, BcelShadow shadow) {
        StringBuilder b = new StringBuilder();
        b.append("make");
        MemberKind kind = shadow.getSignature().getKind();
        if (kind.equals(Member.METHOD)) {
            b.append("Method");
        } else if (kind.equals(Member.CONSTRUCTOR)) {
            b.append("Constructor");
        } else if (kind.equals(Member.HANDLER)) {
            b.append("CatchClause");
        } else if (kind.equals(Member.FIELD)) {
            b.append("Field");
        } else if (kind.equals(Member.STATIC_INITIALIZATION)) {
            b.append("Initializer");
        } else if (kind.equals(Member.MONITORENTER)) {
            b.append("Lock");
        } else if (kind.equals(Member.MONITOREXIT)) {
            b.append("Unlock");
        } else if (kind.equals(Member.ADVICE)) {
            b.append("Advice");
        } else {
            throw new IllegalStateException(kind.toString());
        }
        if (staticTjpType.equals(field.getType())) {
            b.append("SJP");
        } else if (enclosingStaticTjpType.equals(field.getType())) {
            b.append("ESJP");
        }
        return b.toString();
    }

    private void initializeTjpOptimal(InstructionFactory fact, InstructionList list, Field field, BcelShadow shadow) {
        list.append(InstructionFactory.createLoad(factoryType, 0));
        this.pushString(list, shadow.getKind().getName());
        String factoryMethod = this.getFactoryMethod(field, shadow);
        Member sig = shadow.getSignature();
        BcelWorld w = shadow.getWorld();
        if (sig.getKind().equals(Member.METHOD)) {
            this.pushInt(list, sig.getModifiers(w));
            this.pushString(list, sig.getName());
            this.pushClass(list, sig.getDeclaringType());
            this.pushClasses(list, sig.getParameterTypes());
            this.pushStrings(list, sig.getParameterNames(w));
            this.pushClasses(list, sig.getExceptions(w));
            this.pushClass(list, sig.getReturnType());
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_METHOD, (short)182));
        } else if (sig.getKind().equals(Member.CONSTRUCTOR)) {
            if (w.isJoinpointArrayConstructionEnabled() && sig.getDeclaringType().isArray()) {
                this.pushInt(list, 1);
                this.pushClass(list, sig.getDeclaringType());
                this.pushClasses(list, sig.getParameterTypes());
                this.pushStrings(list, null);
                this.pushClasses(list, null);
            } else {
                this.pushInt(list, sig.getModifiers(w));
                this.pushClass(list, sig.getDeclaringType());
                this.pushClasses(list, sig.getParameterTypes());
                this.pushStrings(list, sig.getParameterNames(w));
                this.pushClasses(list, sig.getExceptions(w));
            }
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_CONSTRUCTOR, (short)182));
        } else if (sig.getKind().equals(Member.HANDLER)) {
            this.pushClass(list, sig.getDeclaringType());
            this.pushClass(list, sig.getParameterTypes()[0]);
            String pname = null;
            String[] pnames = sig.getParameterNames(w);
            if (pnames != null && pnames.length > 0) {
                pname = pnames[0];
            }
            this.pushString(list, pname);
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_CATCHCLAUSE, (short)182));
        } else if (sig.getKind().equals(Member.FIELD)) {
            this.pushInt(list, sig.getModifiers(w));
            this.pushString(list, sig.getName());
            UnresolvedType dType = sig.getDeclaringType();
            if (dType.getTypekind() == UnresolvedType.TypeKind.PARAMETERIZED || dType.getTypekind() == UnresolvedType.TypeKind.GENERIC) {
                dType = sig.getDeclaringType().resolve(this.world).getGenericType();
            }
            this.pushClass(list, dType);
            this.pushClass(list, sig.getReturnType());
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_FIELD, (short)182));
        } else if (sig.getKind().equals(Member.STATIC_INITIALIZATION)) {
            this.pushInt(list, sig.getModifiers(w));
            this.pushClass(list, sig.getDeclaringType());
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_INITIALIZER, (short)182));
        } else if (sig.getKind().equals(Member.MONITORENTER)) {
            this.pushClass(list, sig.getDeclaringType());
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_MONITOR, (short)182));
        } else if (sig.getKind().equals(Member.MONITOREXIT)) {
            this.pushClass(list, sig.getDeclaringType());
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_MONITOR, (short)182));
        } else if (sig.getKind().equals(Member.ADVICE)) {
            this.pushInt(list, sig.getModifiers(w));
            this.pushString(list, sig.getName());
            this.pushClass(list, sig.getDeclaringType());
            this.pushClasses(list, sig.getParameterTypes());
            this.pushStrings(list, sig.getParameterNames(w));
            this.pushClasses(list, sig.getExceptions(w));
            this.pushClass(list, sig.getReturnType());
            this.pushInt(list, shadow.getSourceLine());
            list.append(fact.createInvoke(factoryType.getClassName(), factoryMethod, field.getType(), PARAMSIGNATURE_MAKESJP_ADVICE, (short)182));
        } else {
            throw new IllegalStateException("not sure what to do: " + shadow);
        }
        list.append(fact.createFieldAccess(this.getClassName(), field.getName(), field.getType(), (short)179));
    }

    private void pushStrings(InstructionList list, String[] strings) {
        if (strings == null || strings.length == 0) {
            list.append(InstructionFactory.ACONST_NULL);
        } else {
            list.append(InstructionFactory.PUSH(this.cp, strings.length));
            list.append(this.fact.createNewArray(Type.STRING, (short)1));
            for (int s = 0; s < strings.length; ++s) {
                list.append(InstructionFactory.DUP);
                list.append(InstructionFactory.PUSH(this.cp, s));
                list.append(InstructionFactory.PUSH(this.cp, strings[s]));
                list.append(InstructionFactory.AASTORE);
            }
        }
    }

    private void pushClass(InstructionList list, UnresolvedType type) {
        if (type.isPrimitiveType()) {
            if (type.getSignature().equals("I")) {
                list.append(this.fact.createGetStatic("java/lang/Integer", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("D")) {
                list.append(this.fact.createGetStatic("java/lang/Double", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("S")) {
                list.append(this.fact.createGetStatic("java/lang/Short", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("J")) {
                list.append(this.fact.createGetStatic("java/lang/Long", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("F")) {
                list.append(this.fact.createGetStatic("java/lang/Float", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("C")) {
                list.append(this.fact.createGetStatic("java/lang/Character", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("B")) {
                list.append(this.fact.createGetStatic("java/lang/Byte", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("Z")) {
                list.append(this.fact.createGetStatic("java/lang/Boolean", "TYPE", Type.CLASS));
            } else if (type.getSignature().equals("V")) {
                list.append(InstructionFactory.ACONST_NULL);
            }
            return;
        }
        String classString = this.makeLdcClassString(type);
        if (classString == null) {
            list.append(InstructionFactory.ACONST_NULL);
        } else {
            list.append(this.fact.PUSHCLASS(this.cp, classString));
        }
    }

    private void pushClasses(InstructionList list, UnresolvedType[] types) {
        if (types == null || types.length == 0) {
            list.append(InstructionFactory.ACONST_NULL);
        } else {
            list.append(InstructionFactory.PUSH(this.cp, types.length));
            list.append(this.fact.createNewArray(Type.CLASS, (short)1));
            for (int t = 0; t < types.length; ++t) {
                list.append(InstructionFactory.DUP);
                list.append(InstructionFactory.PUSH(this.cp, t));
                this.pushClass(list, types[t]);
                list.append(InstructionFactory.AASTORE);
            }
        }
    }

    private final void pushString(InstructionList list, String string) {
        list.append(InstructionFactory.PUSH(this.cp, string));
    }

    private final void pushInt(InstructionList list, int value) {
        list.append(InstructionFactory.PUSH(this.cp, value));
    }

    protected String makeString(int i) {
        return Integer.toString(i, 16);
    }

    protected String makeString(UnresolvedType t) {
        if (t.isArray()) {
            return t.getSignature().replace('/', '.');
        }
        if (t.isParameterizedType()) {
            return t.getRawType().getName();
        }
        return t.getName();
    }

    protected String makeLdcClassString(UnresolvedType type) {
        String signature;
        if (type.isVoid() || type.isPrimitiveType()) {
            return null;
        }
        if (type.isArray()) {
            return type.getSignature();
        }
        if (type.isParameterizedType()) {
            type = type.getRawType();
        }
        if ((signature = type.getSignature()).length() == 1) {
            return signature;
        }
        return signature.substring(1, signature.length() - 1);
    }

    protected String makeString(UnresolvedType[] types) {
        if (types == null) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        int len = types.length;
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                buf.append(':');
            }
            buf.append(this.makeString(types[i]));
        }
        return buf.toString();
    }

    protected String makeString(String[] names) {
        if (names == null) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        int len = names.length;
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                buf.append(':');
            }
            buf.append(names[i]);
        }
        return buf.toString();
    }

    public ResolvedType getType() {
        if (this.myType == null) {
            return null;
        }
        return this.myType.getResolvedTypeX();
    }

    public BcelObjectType getBcelObjectType() {
        return this.myType;
    }

    public String getFileName() {
        return this.myGen.getFileName();
    }

    private void addField(FieldGen field) {
        this.makeSyntheticAndTransientIfNeeded(field);
        BcelField bcelField = null;
        bcelField = this.getBcelObjectType() != null ? new BcelField(this.getBcelObjectType(), field.getField()) : new BcelField(this.getName(), field.getField(), this.world);
        this.fields.add(bcelField);
    }

    private void makeSyntheticAndTransientIfNeeded(FieldGen field) {
        if (field.getName().startsWith("ajc$") && !field.getName().startsWith("ajc$interField$") && !field.getName().startsWith("ajc$instance$")) {
            if (!field.isStatic()) {
                field.setModifiers(field.getModifiers() | 0x80);
            }
            if (this.getWorld().isInJava5Mode()) {
                field.setModifiers(field.getModifiers() | 0x1000);
            }
            if (!this.hasSyntheticAttribute(field.getAttributes())) {
                ConstantPool cpg = this.myGen.getConstantPool();
                int index = cpg.addUtf8("Synthetic");
                Synthetic synthetic = new Synthetic(index, 0, new byte[0], cpg);
                field.addAttribute(synthetic);
            }
        }
    }

    private boolean hasSyntheticAttribute(List<Attribute> attributes) {
        for (int i = 0; i < attributes.size(); ++i) {
            if (!attributes.get(i).getName().equals("Synthetic")) continue;
            return true;
        }
        return false;
    }

    public void addField(FieldGen field, ISourceLocation sourceLocation) {
        this.addField(field);
        if (!field.isPrivate() || !field.isStatic() && !field.isTransient()) {
            this.errorOnAddedField(field, sourceLocation);
        }
    }

    public String getClassName() {
        return this.myGen.getClassName();
    }

    public boolean isInterface() {
        return this.myGen.isInterface();
    }

    public boolean isAbstract() {
        return this.myGen.isAbstract();
    }

    public LazyMethodGen getLazyMethodGen(Member m) {
        return this.getLazyMethodGen(m.getName(), m.getSignature(), false);
    }

    public LazyMethodGen getLazyMethodGen(String name, String signature) {
        return this.getLazyMethodGen(name, signature, false);
    }

    public LazyMethodGen getLazyMethodGen(String name, String signature, boolean allowMissing) {
        for (LazyMethodGen gen : this.methodGens) {
            if (!gen.getName().equals(name) || !gen.getSignature().equals(signature)) continue;
            return gen;
        }
        if (!allowMissing) {
            throw new BCException("Class " + this.getName() + " does not have a method " + name + " with signature " + signature);
        }
        return null;
    }

    public void forcePublic() {
        this.myGen.setModifiers(Utility.makePublic(this.myGen.getModifiers()));
    }

    public boolean hasAnnotation(UnresolvedType t) {
        AnnotationGen[] agens = this.myGen.getAnnotations();
        if (agens == null) {
            return false;
        }
        for (int i = 0; i < agens.length; ++i) {
            AnnotationGen gen = agens[i];
            if (!t.equals(UnresolvedType.forSignature(gen.getTypeSignature()))) continue;
            return true;
        }
        return false;
    }

    public void addAnnotation(AnnotationGen a) {
        if (!this.hasAnnotation(UnresolvedType.forSignature(a.getTypeSignature()))) {
            this.annotations.add(new AnnotationGen(a, this.getConstantPool(), true));
        }
    }

    public void addAttribute(AjAttribute attribute) {
        this.myGen.addAttribute(Utility.bcelAttribute(attribute, this.getConstantPool()));
    }

    public void addAttribute(Attribute attribute) {
        this.myGen.addAttribute(attribute);
    }

    public Collection<Attribute> getAttributes() {
        return this.myGen.getAttributes();
    }

    private boolean implementsSerializable(ResolvedType aType) {
        if (aType.getSignature().equals(UnresolvedType.SERIALIZABLE.getSignature())) {
            return true;
        }
        ResolvedType[] interfaces = aType.getDeclaredInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i].isMissing() || !this.implementsSerializable(interfaces[i])) continue;
            return true;
        }
        ResolvedType superType = aType.getSuperclass();
        if (superType != null && !superType.isMissing()) {
            return this.implementsSerializable(superType);
        }
        return false;
    }

    public boolean isAtLeastJava5() {
        return this.myGen.getMajor() >= 49;
    }

    public String allocateField(String prefix) {
        int highestAllocated = -1;
        List<BcelField> fs = this.getFieldGens();
        for (BcelField field : fs) {
            if (!field.getName().startsWith(prefix)) continue;
            try {
                int num = Integer.parseInt(field.getName().substring(prefix.length()));
                if (num <= highestAllocated) continue;
                highestAllocated = num;
            }
            catch (NumberFormatException numberFormatException) {}
        }
        return prefix + Integer.toString(highestAllocated + 1);
    }

    static class CacheKey {
        private Object key;
        private ResolvedType annotationType;

        CacheKey(BcelShadow shadow, ResolvedType annotationType, boolean isWithin) {
            this.key = isWithin ? shadow : shadow.toString();
            this.annotationType = annotationType;
        }

        public int hashCode() {
            return this.key.hashCode() * 37 + this.annotationType.hashCode();
        }

        public boolean equals(Object other) {
            if (!(other instanceof CacheKey)) {
                return false;
            }
            CacheKey oCacheKey = (CacheKey)other;
            return this.key.equals(oCacheKey.key) && this.annotationType.equals(oCacheKey.annotationType);
        }
    }

    static class InlinedSourceFileInfo {
        int highestLineNumber;
        int offset;

        InlinedSourceFileInfo(int highestLineNumber) {
            this.highestLineNumber = highestLineNumber;
        }
    }
}

