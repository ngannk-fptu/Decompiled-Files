/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.classfile.Unknown;
import org.aspectj.apache.bcel.classfile.annotation.ArrayElementValue;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.aspectj.apache.bcel.classfile.annotation.SimpleElementValue;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.BasicType;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionByte;
import org.aspectj.apache.bcel.generic.InstructionCP;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InstructionSelect;
import org.aspectj.apache.bcel.generic.InstructionShort;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.LineNumberTag;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.ReferenceType;
import org.aspectj.apache.bcel.generic.SwitchBuilder;
import org.aspectj.apache.bcel.generic.TargetLostException;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ConstantPoolReader;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Lint;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.Utils;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelConstantPoolWriter;
import org.aspectj.weaver.bcel.BcelVar;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyMethodGen;

public class Utility {
    private static final char PACKAGE_INITIAL_CHAR = "org.aspectj.weaver".charAt(0);
    private static String[] argNames = new String[]{"arg0", "arg1", "arg2", "arg3", "arg4"};
    private static Hashtable<String, String> validBoxing = new Hashtable();
    public static int testingParseCounter;

    public static List<AjAttribute> readAjAttributes(String classname, Attribute[] as, ISourceContext context, World w, AjAttribute.WeaverVersionInfo version, ConstantPoolReader dataDecompressor) {
        Attribute a;
        int i;
        ArrayList<AjAttribute> attributes = new ArrayList<AjAttribute>();
        ArrayList<Unknown> forSecondPass = new ArrayList<Unknown>();
        for (i = as.length - 1; i >= 0; --i) {
            Unknown u;
            String name;
            a = as[i];
            if (!(a instanceof Unknown) || (name = (u = (Unknown)a).getName()).charAt(0) != PACKAGE_INITIAL_CHAR || !name.startsWith("org.aspectj.weaver")) continue;
            if (name.endsWith("org.aspectj.weaver.WeaverVersion") && (version = (AjAttribute.WeaverVersionInfo)AjAttribute.read(version, name, u.getBytes(), context, w, dataDecompressor)).getMajorVersion() > AjAttribute.WeaverVersionInfo.getCurrentWeaverMajorVersion()) {
                throw new BCException("Unable to continue, this version of AspectJ supports classes built with weaver version " + AjAttribute.WeaverVersionInfo.toCurrentVersionString() + " but the class " + classname + " is version " + version.toString() + ".  Please update your AspectJ.");
            }
            forSecondPass.add(u);
        }
        for (i = forSecondPass.size() - 1; i >= 0; --i) {
            a = (Unknown)forSecondPass.get(i);
            String name = ((Unknown)a).getName();
            AjAttribute attr = AjAttribute.read(version, name, ((Unknown)a).getBytes(), context, w, dataDecompressor);
            if (attr == null) continue;
            attributes.add(attr);
        }
        return attributes;
    }

    public static String beautifyLocation(ISourceLocation isl) {
        StringBuffer nice = new StringBuffer();
        if (isl == null || isl.getSourceFile() == null || isl.getSourceFile().getName().indexOf("no debug info available") != -1) {
            nice.append("no debug info available");
        } else {
            int takeFrom = isl.getSourceFile().getPath().lastIndexOf(47);
            if (takeFrom == -1) {
                takeFrom = isl.getSourceFile().getPath().lastIndexOf(92);
            }
            nice.append(isl.getSourceFile().getPath().substring(takeFrom + 1));
            if (isl.getLine() != 0) {
                nice.append(":").append(isl.getLine());
            }
        }
        return nice.toString();
    }

    public static Instruction createSuperInvoke(InstructionFactory fact, BcelWorld world, Member signature) {
        if (Modifier.isInterface(signature.getModifiers())) {
            throw new RuntimeException("bad");
        }
        if (Modifier.isPrivate(signature.getModifiers()) || signature.getName().equals("<init>")) {
            throw new RuntimeException("unimplemented, possibly bad");
        }
        if (Modifier.isStatic(signature.getModifiers())) {
            throw new RuntimeException("bad");
        }
        short kind = 183;
        return fact.createInvoke(signature.getDeclaringType().getName(), signature.getName(), BcelWorld.makeBcelType(signature.getReturnType()), BcelWorld.makeBcelTypes(signature.getParameterTypes()), kind);
    }

    public static Instruction createInvoke(InstructionFactory fact, BcelWorld world, Member signature) {
        int signatureModifiers = signature.getModifiers();
        short kind = Modifier.isInterface(signatureModifiers) ? (short)185 : (Modifier.isStatic(signatureModifiers) ? (short)184 : (Modifier.isPrivate(signatureModifiers) || signature.getName().equals("<init>") ? (short)183 : 182));
        UnresolvedType targetType = signature.getDeclaringType();
        if (targetType.isParameterizedType()) {
            targetType = targetType.resolve(world).getGenericType();
        }
        return fact.createInvoke(targetType.getName(), signature.getName(), BcelWorld.makeBcelType(signature.getReturnType()), BcelWorld.makeBcelTypes(signature.getParameterTypes()), kind);
    }

    public static Instruction createGet(InstructionFactory fact, Member signature) {
        short kind = Modifier.isStatic(signature.getModifiers()) ? (short)178 : 180;
        return fact.createFieldAccess(signature.getDeclaringType().getName(), signature.getName(), BcelWorld.makeBcelType(signature.getReturnType()), kind);
    }

    public static Instruction createSet(InstructionFactory fact, Member signature) {
        short kind = Modifier.isStatic(signature.getModifiers()) ? (short)179 : 181;
        return fact.createFieldAccess(signature.getDeclaringType().getName(), signature.getName(), BcelWorld.makeBcelType(signature.getReturnType()), kind);
    }

    public static Instruction createInstanceof(InstructionFactory fact, ReferenceType t) {
        int cpoolEntry = t instanceof ArrayType ? fact.getConstantPool().addArrayClass((ArrayType)t) : fact.getConstantPool().addClass((ObjectType)t);
        return new InstructionCP(193, cpoolEntry);
    }

    public static Instruction createInvoke(InstructionFactory fact, LazyMethodGen m) {
        short kind = m.getEnclosingClass().isInterface() ? (m.isStatic() ? (short)184 : 185) : (m.isStatic() ? (short)184 : (m.isPrivate() || m.getName().equals("<init>") ? (short)183 : 182));
        return fact.createInvoke(m.getClassName(), m.getName(), m.getReturnType(), m.getArgumentTypes(), kind, m.getEnclosingClass().isInterface());
    }

    public static Instruction createInvoke(InstructionFactory fact, short kind, Member member) {
        return fact.createInvoke(member.getDeclaringType().getName(), member.getName(), BcelWorld.makeBcelType(member.getReturnType()), BcelWorld.makeBcelTypes(member.getParameterTypes()), kind);
    }

    public static String[] makeArgNames(int n) {
        String[] ret = new String[n];
        for (int i = 0; i < n; ++i) {
            ret[i] = i < 5 ? argNames[i] : "arg" + i;
        }
        return ret;
    }

    public static void appendConversion(InstructionList il, InstructionFactory fact, ResolvedType fromType, ResolvedType toType) {
        block21: {
            if (!toType.isConvertableFrom(fromType) && !fromType.isConvertableFrom(toType)) {
                throw new BCException("can't convert from " + fromType + " to " + toType);
            }
            World w = toType.getWorld();
            if (w == null) {
                throw new IllegalStateException("Debug349636: Unexpectedly found world null for type " + toType.getName());
            }
            if (!w.isInJava5Mode() ? toType.needsNoConversionFrom(fromType) : toType.needsNoConversionFrom(fromType) && !(toType.isPrimitiveType() ^ fromType.isPrimitiveType())) {
                return;
            }
            if (toType.equals(UnresolvedType.VOID)) {
                il.append(InstructionFactory.createPop(fromType.getSize()));
            } else {
                if (fromType.equals(UnresolvedType.VOID)) {
                    il.append(InstructionFactory.createNull(Type.OBJECT));
                    return;
                }
                if (fromType.equals(UnresolvedType.OBJECT)) {
                    Type to = BcelWorld.makeBcelType(toType);
                    if (toType.isPrimitiveType()) {
                        String name = toType.toString() + "Value";
                        il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, to, new Type[]{Type.OBJECT}, (short)184));
                    } else {
                        il.append(fact.createCheckCast((ReferenceType)to));
                    }
                } else if (toType.equals(UnresolvedType.OBJECT)) {
                    Type from = BcelWorld.makeBcelType(fromType);
                    String name = fromType.toString() + "Object";
                    il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, Type.OBJECT, new Type[]{from}, (short)184));
                } else if (toType.getWorld().isInJava5Mode() && validBoxing.get(toType.getSignature() + fromType.getSignature()) != null) {
                    Type from = BcelWorld.makeBcelType(fromType);
                    Type to = BcelWorld.makeBcelType(toType);
                    String name = validBoxing.get(toType.getSignature() + fromType.getSignature());
                    if (toType.isPrimitiveType()) {
                        il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, to, new Type[]{Type.OBJECT}, (short)184));
                    } else {
                        il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, Type.OBJECT, new Type[]{from}, (short)184));
                        il.append(fact.createCheckCast((ReferenceType)to));
                    }
                } else if (fromType.isPrimitiveType()) {
                    Type from = BcelWorld.makeBcelType(fromType);
                    Type to = BcelWorld.makeBcelType(toType);
                    try {
                        Instruction i = fact.createCast(from, to);
                        if (i != null) {
                            il.append(i);
                            break block21;
                        }
                        il.append(fact.createCast(from, Type.INT));
                        il.append(fact.createCast(Type.INT, to));
                    }
                    catch (RuntimeException e) {
                        il.append(fact.createCast(from, Type.INT));
                        il.append(fact.createCast(Type.INT, to));
                    }
                } else {
                    Type to = BcelWorld.makeBcelType(toType);
                    il.append(fact.createCheckCast((ReferenceType)to));
                }
            }
        }
    }

    public static InstructionList createConversion(InstructionFactory factory, Type fromType, Type toType) {
        return Utility.createConversion(factory, fromType, toType, false);
    }

    public static InstructionList createConversion(InstructionFactory fact, Type fromType, Type toType, boolean allowAutoboxing) {
        InstructionList il = new InstructionList();
        if ((fromType.equals(Type.BYTE) || fromType.equals(Type.CHAR) || fromType.equals(Type.SHORT)) && toType.equals(Type.INT)) {
            return il;
        }
        if (fromType.equals(toType)) {
            return il;
        }
        if (toType.equals(Type.VOID)) {
            il.append(InstructionFactory.createPop(fromType.getSize()));
            return il;
        }
        if (fromType.equals(Type.VOID)) {
            if (toType instanceof BasicType) {
                throw new BCException("attempting to cast from void to basic type");
            }
            il.append(InstructionFactory.createNull(Type.OBJECT));
            return il;
        }
        if (fromType.equals(Type.OBJECT) && toType instanceof BasicType) {
            String name = toType.toString() + "Value";
            il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, toType, new Type[]{Type.OBJECT}, (short)184));
            return il;
        }
        if (toType.equals(Type.OBJECT)) {
            if (fromType instanceof BasicType) {
                String name = fromType.toString() + "Object";
                il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, Type.OBJECT, new Type[]{fromType}, (short)184));
                return il;
            }
            if (fromType instanceof ReferenceType) {
                return il;
            }
            throw new RuntimeException();
        }
        if (fromType instanceof ReferenceType && ((ReferenceType)fromType).isAssignmentCompatibleWith(toType)) {
            return il;
        }
        if (allowAutoboxing) {
            if (toType instanceof BasicType && fromType instanceof ReferenceType) {
                String name = toType.toString() + "Value";
                il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, toType, new Type[]{Type.OBJECT}, (short)184));
                return il;
            }
            if (fromType instanceof BasicType && toType instanceof ReferenceType) {
                String name = fromType.toString() + "Object";
                il.append(fact.createInvoke("org.aspectj.runtime.internal.Conversions", name, Type.OBJECT, new Type[]{fromType}, (short)184));
                il.append(fact.createCast(Type.OBJECT, toType));
                return il;
            }
        }
        il.append(fact.createCast(fromType, toType));
        return il;
    }

    public static Instruction createConstant(InstructionFactory fact, int value) {
        Instruction inst;
        switch (value) {
            case -1: {
                inst = InstructionConstants.ICONST_M1;
                break;
            }
            case 0: {
                inst = InstructionConstants.ICONST_0;
                break;
            }
            case 1: {
                inst = InstructionConstants.ICONST_1;
                break;
            }
            case 2: {
                inst = InstructionConstants.ICONST_2;
                break;
            }
            case 3: {
                inst = InstructionConstants.ICONST_3;
                break;
            }
            case 4: {
                inst = InstructionConstants.ICONST_4;
                break;
            }
            case 5: {
                inst = InstructionConstants.ICONST_5;
                break;
            }
            default: {
                if (value <= 127 && value >= -128) {
                    inst = new InstructionByte(16, (byte)value);
                    break;
                }
                if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
                    inst = new InstructionShort(17, (short)value);
                    break;
                }
                int ii = fact.getClassGen().getConstantPool().addInteger(value);
                inst = new InstructionCP(value <= 255 ? (short)18 : 19, ii);
            }
        }
        return inst;
    }

    public static JavaClass makeJavaClass(String filename, byte[] bytes) {
        try {
            ++testingParseCounter;
            ClassParser parser = new ClassParser(new ByteArrayInputStream(bytes), filename);
            return parser.parse();
        }
        catch (IOException e) {
            throw new BCException("malformed class file");
        }
    }

    public static void replaceInstruction(InstructionHandle ih, InstructionList replacementInstructions, LazyMethodGen enclosingMethod) {
        InstructionList il = enclosingMethod.getBody();
        InstructionHandle fresh = il.append(ih, replacementInstructions);
        Utility.deleteInstruction(ih, fresh, enclosingMethod);
    }

    public static void deleteInstruction(InstructionHandle ih, LazyMethodGen enclosingMethod) {
        Utility.deleteInstruction(ih, ih.getNext(), enclosingMethod);
    }

    public static void deleteInstruction(InstructionHandle ih, InstructionHandle retargetTo, LazyMethodGen enclosingMethod) {
        InstructionList il = enclosingMethod.getBody();
        for (InstructionTargeter targeter : ih.getTargetersCopy()) {
            targeter.updateTarget(ih, retargetTo);
        }
        ih.removeAllTargeters();
        try {
            il.delete(ih);
        }
        catch (TargetLostException e) {
            throw new BCException("this really can't happen");
        }
    }

    public static Instruction copyInstruction(Instruction i) {
        if (i instanceof InstructionSelect) {
            InstructionSelect freshSelect = (InstructionSelect)i;
            InstructionHandle[] targets = new InstructionHandle[freshSelect.getTargets().length];
            for (int ii = 0; ii < targets.length; ++ii) {
                targets[ii] = freshSelect.getTargets()[ii];
            }
            return new SwitchBuilder(freshSelect.getMatchs(), targets, freshSelect.getTarget()).getInstruction();
        }
        return i.copy();
    }

    public static int getSourceLine(InstructionHandle ih) {
        int lookahead = 0;
        while (lookahead++ < 100) {
            if (ih == null) {
                return -1;
            }
            for (InstructionTargeter t : ih.getTargeters()) {
                if (!(t instanceof LineNumberTag)) continue;
                return ((LineNumberTag)t).getLineNumber();
            }
            ih = ih.getPrev();
        }
        return -1;
    }

    public static void setSourceLine(InstructionHandle ih, int lineNumber) {
        ih.addTargeter(new LineNumberTag(lineNumber));
    }

    public static int makePublic(int i) {
        return i & 0xFFFFFFF9 | 1;
    }

    public static BcelVar[] pushAndReturnArrayOfVars(ResolvedType[] proceedParamTypes, InstructionList il, InstructionFactory fact, LazyMethodGen enclosingMethod) {
        int len = proceedParamTypes.length;
        BcelVar[] ret = new BcelVar[len];
        for (int i = len - 1; i >= 0; --i) {
            ResolvedType typeX = proceedParamTypes[i];
            Type type = BcelWorld.makeBcelType(typeX);
            int local = enclosingMethod.allocateLocal(type);
            il.append(InstructionFactory.createStore(type, local));
            ret[i] = new BcelVar(typeX, local);
        }
        return ret;
    }

    public static boolean isConstantPushInstruction(Instruction i) {
        long ii = Constants.instFlags[i.opcode];
        return (ii & 1L) != 0L && (ii & 2L) != 0L;
    }

    public static boolean isSuppressing(Member member, String lintkey) {
        boolean isSuppressing = Utils.isSuppressing(member.getAnnotations(), lintkey);
        if (isSuppressing) {
            return true;
        }
        UnresolvedType type = member.getDeclaringType();
        if (type instanceof ResolvedType) {
            return Utils.isSuppressing(((ResolvedType)type).getAnnotations(), lintkey);
        }
        return false;
    }

    public static List<Lint.Kind> getSuppressedWarnings(AnnotationAJ[] anns, Lint lint) {
        if (anns == null) {
            return Collections.emptyList();
        }
        ArrayList<Lint.Kind> suppressedWarnings = new ArrayList<Lint.Kind>();
        boolean found = false;
        for (int i = 0; !found && i < anns.length; ++i) {
            if (!UnresolvedType.SUPPRESS_AJ_WARNINGS.getSignature().equals(((BcelAnnotation)anns[i]).getBcelAnnotation().getTypeSignature())) continue;
            found = true;
            List<NameValuePair> vals = ((BcelAnnotation)anns[i]).getBcelAnnotation().getValues();
            if (vals == null || vals.isEmpty()) {
                suppressedWarnings.addAll(lint.allKinds());
                continue;
            }
            ArrayElementValue array = (ArrayElementValue)vals.get(0).getValue();
            ElementValue[] values = array.getElementValuesArray();
            for (int j = 0; j < values.length; ++j) {
                SimpleElementValue value = (SimpleElementValue)values[j];
                Lint.Kind lintKind = lint.getLintKind(value.getValueString());
                if (lintKind == null) continue;
                suppressedWarnings.add(lintKind);
            }
        }
        return suppressedWarnings;
    }

    public static Attribute bcelAttribute(AjAttribute a, ConstantPool pool) {
        int nameIndex = pool.addUtf8(a.getNameString());
        byte[] bytes = a.getBytes(new BcelConstantPoolWriter(pool));
        int length = bytes.length;
        return new Unknown(nameIndex, length, bytes, pool);
    }

    static {
        validBoxing.put("Ljava/lang/Byte;B", "byteObject");
        validBoxing.put("Ljava/lang/Character;C", "charObject");
        validBoxing.put("Ljava/lang/Double;D", "doubleObject");
        validBoxing.put("Ljava/lang/Float;F", "floatObject");
        validBoxing.put("Ljava/lang/Integer;I", "intObject");
        validBoxing.put("Ljava/lang/Long;J", "longObject");
        validBoxing.put("Ljava/lang/Short;S", "shortObject");
        validBoxing.put("Ljava/lang/Boolean;Z", "booleanObject");
        validBoxing.put("BLjava/lang/Byte;", "byteValue");
        validBoxing.put("CLjava/lang/Character;", "charValue");
        validBoxing.put("DLjava/lang/Double;", "doubleValue");
        validBoxing.put("FLjava/lang/Float;", "floatValue");
        validBoxing.put("ILjava/lang/Integer;", "intValue");
        validBoxing.put("JLjava/lang/Long;", "longValue");
        validBoxing.put("SLjava/lang/Short;", "shortValue");
        validBoxing.put("ZLjava/lang/Boolean;", "booleanValue");
        testingParseCounter = 0;
    }
}

