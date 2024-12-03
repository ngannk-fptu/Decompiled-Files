/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.apache.bcel.Constants;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.PassVerifier;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.ClassConstraintException;
import org.apache.bcel.verifier.exc.LocalVariableInfoInconsistentException;
import org.apache.bcel.verifier.statics.LocalVariablesInfo;
import org.apache.bcel.verifier.statics.StringRepresentation;

public final class Pass2Verifier
extends PassVerifier
implements Constants {
    private LocalVariablesInfo[] localVariablesInfos;
    private final Verifier verifier;

    private static String tostring(Node n) {
        return new StringRepresentation(n).toString();
    }

    private static boolean validClassMethodName(String name) {
        return Pass2Verifier.validMethodName(name, false);
    }

    private static boolean validClassName(String name) {
        Objects.requireNonNull(name, "name");
        return true;
    }

    private static boolean validFieldName(String name) {
        return Pass2Verifier.validJavaIdentifier(name);
    }

    private static boolean validInterfaceMethodName(String name) {
        if (name.startsWith("<")) {
            return false;
        }
        return Pass2Verifier.validJavaLangMethodName(name);
    }

    private static boolean validJavaIdentifier(String name) {
        if (name.isEmpty() || !Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }
        for (int i = 1; i < name.length(); ++i) {
            if (Character.isJavaIdentifierPart(name.charAt(i))) continue;
            return false;
        }
        return true;
    }

    private static boolean validJavaLangMethodName(String name) {
        return Pass2Verifier.validJavaIdentifier(name);
    }

    private static boolean validMethodName(String name, boolean allowStaticInit) {
        if (Pass2Verifier.validJavaLangMethodName(name)) {
            return true;
        }
        if (allowStaticInit) {
            return name.equals("<init>") || name.equals("<clinit>");
        }
        return name.equals("<init>");
    }

    public Pass2Verifier(Verifier verifier) {
        this.verifier = verifier;
    }

    private void constantPoolEntriesSatisfyStaticConstraints() {
        try {
            JavaClass jc = Repository.lookupClass(this.verifier.getClassName());
            new CPESSC_Visitor(jc);
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    @Override
    public VerificationResult do_verify() {
        try {
            VerificationResult vr1 = this.verifier.doPass1();
            if (vr1.equals(VerificationResult.VR_OK)) {
                this.localVariablesInfos = new LocalVariablesInfo[Repository.lookupClass(this.verifier.getClassName()).getMethods().length];
                VerificationResult vr = VerificationResult.VR_OK;
                try {
                    this.constantPoolEntriesSatisfyStaticConstraints();
                    this.fieldAndMethodRefsAreValid();
                    this.everyClassHasAnAccessibleSuperclass();
                    this.finalMethodsAreNotOverridden();
                }
                catch (ClassConstraintException cce) {
                    vr = new VerificationResult(2, cce.getMessage());
                }
                return vr;
            }
            return VerificationResult.VR_NOTYET;
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    private void everyClassHasAnAccessibleSuperclass() {
        try {
            HashSet<String> hs = new HashSet<String>();
            JavaClass jc = Repository.lookupClass(this.verifier.getClassName());
            int supidx = -1;
            while (supidx != 0) {
                supidx = jc.getSuperclassNameIndex();
                if (supidx == 0) {
                    if (jc == Repository.lookupClass(Type.OBJECT.getClassName())) continue;
                    throw new ClassConstraintException("Superclass of '" + jc.getClassName() + "' missing but not " + Type.OBJECT.getClassName() + " itself!");
                }
                String supername = jc.getSuperclassName();
                if (!hs.add(supername)) {
                    throw new ClassConstraintException("Circular superclass hierarchy detected.");
                }
                Verifier v = VerifierFactory.getVerifier(supername);
                VerificationResult vr = v.doPass1();
                if (vr != VerificationResult.VR_OK) {
                    throw new ClassConstraintException("Could not load in ancestor class '" + supername + "'.");
                }
                jc = Repository.lookupClass(supername);
                if (!jc.isFinal()) continue;
                throw new ClassConstraintException("Ancestor class '" + supername + "' has the FINAL access modifier and must therefore not be subclassed.");
            }
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    private void fieldAndMethodRefsAreValid() {
        try {
            JavaClass jc = Repository.lookupClass(this.verifier.getClassName());
            DescendingVisitor v = new DescendingVisitor(jc, new FAMRAV_Visitor(jc));
            v.visit();
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    private void finalMethodsAreNotOverridden() {
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            JavaClass jc = Repository.lookupClass(this.verifier.getClassName());
            int supidx = -1;
            while (supidx != 0) {
                Method[] methods;
                supidx = jc.getSuperclassNameIndex();
                for (Method method : methods = jc.getMethods()) {
                    String nameAndSig = method.getName() + method.getSignature();
                    if (map.containsKey(nameAndSig) && method.isFinal()) {
                        if (!method.isPrivate()) {
                            throw new ClassConstraintException("Method '" + nameAndSig + "' in class '" + (String)map.get(nameAndSig) + "' overrides the final (not-overridable) definition in class '" + jc.getClassName() + "'.");
                        }
                        this.addMessage("Method '" + nameAndSig + "' in class '" + (String)map.get(nameAndSig) + "' overrides the final (not-overridable) definition in class '" + jc.getClassName() + "'. This is okay, as the original definition was private; however this constraint leverage was introduced by JLS 8.4.6 (not vmspec2) and the behavior of the Sun verifiers.");
                        continue;
                    }
                    if (method.isStatic()) continue;
                    map.put(nameAndSig, jc.getClassName());
                }
                jc = Repository.lookupClass(jc.getSuperclassName());
            }
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    public LocalVariablesInfo getLocalVariablesInfo(int methodNr) {
        if (this.verify() != VerificationResult.VR_OK) {
            return null;
        }
        if (methodNr < 0 || methodNr >= this.localVariablesInfos.length) {
            throw new AssertionViolatedException("Method number out of range.");
        }
        return this.localVariablesInfos[methodNr];
    }

    private static class InnerClassDetector
    extends EmptyVisitor {
        private boolean hasInnerClass;
        private final JavaClass jc;
        private final ConstantPool cp;

        public InnerClassDetector(JavaClass javaClass) {
            this.jc = javaClass;
            this.cp = this.jc.getConstantPool();
            new DescendingVisitor(this.jc, this).visit();
        }

        public boolean innerClassReferenced() {
            return this.hasInnerClass;
        }

        @Override
        public void visitConstantClass(ConstantClass obj) {
            String className;
            Object c = this.cp.getConstant(obj.getNameIndex());
            if (c instanceof ConstantUtf8 && (className = ((ConstantUtf8)c).getBytes()).startsWith(Utility.packageToPath(this.jc.getClassName()) + "$")) {
                this.hasInnerClass = true;
            }
        }
    }

    private final class FAMRAV_Visitor
    extends EmptyVisitor {
        private final ConstantPool cp;

        private FAMRAV_Visitor(JavaClass jc) {
            this.cp = jc.getConstantPool();
        }

        @Override
        public void visitConstantFieldref(ConstantFieldref obj) {
            if (obj.getTag() != 9) {
                throw new ClassConstraintException("ConstantFieldref '" + Pass2Verifier.tostring(obj) + "' has wrong tag!");
            }
            int nameAndTypeIndex = obj.getNameAndTypeIndex();
            ConstantNameAndType cnat = (ConstantNameAndType)this.cp.getConstant(nameAndTypeIndex);
            String name = ((ConstantUtf8)this.cp.getConstant(cnat.getNameIndex())).getBytes();
            if (!Pass2Verifier.validFieldName(name)) {
                throw new ClassConstraintException("Invalid field name '" + name + "' referenced by '" + Pass2Verifier.tostring(obj) + "'.");
            }
            int classIndex = obj.getClassIndex();
            ConstantClass cc = (ConstantClass)this.cp.getConstant(classIndex);
            String className = ((ConstantUtf8)this.cp.getConstant(cc.getNameIndex())).getBytes();
            if (!Pass2Verifier.validClassName(className)) {
                throw new ClassConstraintException("Illegal class name '" + className + "' used by '" + Pass2Verifier.tostring(obj) + "'.");
            }
            String sig = ((ConstantUtf8)this.cp.getConstant(cnat.getSignatureIndex())).getBytes();
            try {
                Type.getType(sig);
            }
            catch (ClassFormatException cfe) {
                throw new ClassConstraintException("Illegal descriptor (==signature) '" + sig + "' used by '" + Pass2Verifier.tostring(obj) + "'.", cfe);
            }
        }

        @Override
        public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
            if (obj.getTag() != 11) {
                throw new ClassConstraintException("ConstantInterfaceMethodref '" + Pass2Verifier.tostring(obj) + "' has wrong tag!");
            }
            int nameAndTypeIndex = obj.getNameAndTypeIndex();
            ConstantNameAndType cnat = (ConstantNameAndType)this.cp.getConstant(nameAndTypeIndex);
            String name = ((ConstantUtf8)this.cp.getConstant(cnat.getNameIndex())).getBytes();
            if (!Pass2Verifier.validInterfaceMethodName(name)) {
                throw new ClassConstraintException("Invalid (interface) method name '" + name + "' referenced by '" + Pass2Verifier.tostring(obj) + "'.");
            }
            int classIndex = obj.getClassIndex();
            ConstantClass cc = (ConstantClass)this.cp.getConstant(classIndex);
            String className = ((ConstantUtf8)this.cp.getConstant(cc.getNameIndex())).getBytes();
            if (!Pass2Verifier.validClassName(className)) {
                throw new ClassConstraintException("Illegal class name '" + className + "' used by '" + Pass2Verifier.tostring(obj) + "'.");
            }
            String sig = ((ConstantUtf8)this.cp.getConstant(cnat.getSignatureIndex())).getBytes();
            try {
                Type t = Type.getReturnType(sig);
                if (name.equals("<clinit>") && t != Type.VOID) {
                    Pass2Verifier.this.addMessage("Class or interface initialization method '<clinit>' usually has VOID return type instead of '" + t + "'. Note this is really not a requirement of The Java Virtual Machine Specification, Second Edition.");
                }
            }
            catch (ClassFormatException cfe) {
                throw new ClassConstraintException("Illegal descriptor (==signature) '" + sig + "' used by '" + Pass2Verifier.tostring(obj) + "'.", cfe);
            }
        }

        @Override
        public void visitConstantMethodref(ConstantMethodref obj) {
            if (obj.getTag() != 10) {
                throw new ClassConstraintException("ConstantMethodref '" + Pass2Verifier.tostring(obj) + "' has wrong tag!");
            }
            int nameAndTypeIndex = obj.getNameAndTypeIndex();
            ConstantNameAndType cnat = (ConstantNameAndType)this.cp.getConstant(nameAndTypeIndex);
            String name = ((ConstantUtf8)this.cp.getConstant(cnat.getNameIndex())).getBytes();
            if (!Pass2Verifier.validClassMethodName(name)) {
                throw new ClassConstraintException("Invalid (non-interface) method name '" + name + "' referenced by '" + Pass2Verifier.tostring(obj) + "'.");
            }
            int classIndex = obj.getClassIndex();
            ConstantClass cc = (ConstantClass)this.cp.getConstant(classIndex);
            String className = ((ConstantUtf8)this.cp.getConstant(cc.getNameIndex())).getBytes();
            if (!Pass2Verifier.validClassName(className)) {
                throw new ClassConstraintException("Illegal class name '" + className + "' used by '" + Pass2Verifier.tostring(obj) + "'.");
            }
            String sig = ((ConstantUtf8)this.cp.getConstant(cnat.getSignatureIndex())).getBytes();
            try {
                Type t = Type.getReturnType(sig);
                if (name.equals("<init>") && t != Type.VOID) {
                    throw new ClassConstraintException("Instance initialization method must have VOID return type.");
                }
            }
            catch (ClassFormatException cfe) {
                throw new ClassConstraintException("Illegal descriptor (==signature) '" + sig + "' used by '" + Pass2Verifier.tostring(obj) + "'.", cfe);
            }
        }
    }

    private final class CPESSC_Visitor
    extends EmptyVisitor {
        private final Class<?> CONST_Class;
        private final Class<?> CONST_String;
        private final Class<?> CONST_Integer;
        private final Class<?> CONST_Float;
        private final Class<?> CONST_Long;
        private final Class<?> CONST_Double;
        private final Class<?> CONST_NameAndType;
        private final Class<?> CONST_Utf8;
        private final JavaClass jc;
        private final ConstantPool cp;
        private final int cplen;
        private final DescendingVisitor carrier;
        private final Set<String> fieldNames = new HashSet<String>();
        private final Set<String> fieldNamesAndDesc = new HashSet<String>();
        private final Set<String> methodNamesAndDesc = new HashSet<String>();

        private CPESSC_Visitor(JavaClass jc) {
            this.jc = jc;
            this.cp = jc.getConstantPool();
            this.cplen = this.cp.getLength();
            this.CONST_Class = ConstantClass.class;
            this.CONST_String = ConstantString.class;
            this.CONST_Integer = ConstantInteger.class;
            this.CONST_Float = ConstantFloat.class;
            this.CONST_Long = ConstantLong.class;
            this.CONST_Double = ConstantDouble.class;
            this.CONST_NameAndType = ConstantNameAndType.class;
            this.CONST_Utf8 = ConstantUtf8.class;
            this.carrier = new DescendingVisitor(jc, this);
            this.carrier.visit();
        }

        private void checkIndex(Node referrer, int index, Class<?> shouldbe) {
            if (index < 0 || index >= this.cplen) {
                throw new ClassConstraintException("Invalid index '" + index + "' used by '" + Pass2Verifier.tostring(referrer) + "'.");
            }
            Object c = this.cp.getConstant(index);
            if (!shouldbe.isInstance(c)) {
                throw new ClassConstraintException("Illegal constant '" + Pass2Verifier.tostring(c) + "' at index '" + index + "'. '" + Pass2Verifier.tostring(referrer) + "' expects a '" + shouldbe + "'.");
            }
        }

        @Override
        public void visitCode(Code obj) {
            try {
                Attribute[] atts;
                int mn;
                CodeException[] excTable;
                this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
                String name = ((ConstantUtf8)this.cp.getConstant(obj.getNameIndex())).getBytes();
                if (!name.equals("Code")) {
                    throw new ClassConstraintException("The Code attribute '" + Pass2Verifier.tostring(obj) + "' is not correctly named 'Code' but '" + name + "'.");
                }
                if (!(this.carrier.predecessor() instanceof Method)) {
                    Pass2Verifier.this.addMessage("Code attribute '" + Pass2Verifier.tostring(obj) + "' is not declared in a method_info structure but in '" + this.carrier.predecessor() + "'. Ignored.");
                    return;
                }
                Method m = (Method)this.carrier.predecessor();
                if (obj.getCode().length == 0) {
                    throw new ClassConstraintException("Code array of Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + m + "') must not be empty.");
                }
                for (CodeException element : excTable = obj.getExceptionTable()) {
                    int excIndex = element.getCatchType();
                    if (excIndex == 0) continue;
                    this.checkIndex(obj, excIndex, this.CONST_Class);
                    ConstantClass cc = (ConstantClass)this.cp.getConstant(excIndex);
                    this.checkIndex(cc, cc.getNameIndex(), this.CONST_Utf8);
                    String cname = Utility.pathToPackage(((ConstantUtf8)this.cp.getConstant(cc.getNameIndex())).getBytes());
                    Verifier v = VerifierFactory.getVerifier(cname);
                    VerificationResult vr = v.doPass1();
                    if (vr != VerificationResult.VR_OK) {
                        throw new ClassConstraintException("Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + m + "') has an exception_table entry '" + Pass2Verifier.tostring(element) + "' that references '" + cname + "' as an Exception but it does not pass verification pass 1: " + vr);
                    }
                    JavaClass e = Repository.lookupClass(cname);
                    JavaClass t = Repository.lookupClass(Type.THROWABLE.getClassName());
                    JavaClass o = Repository.lookupClass(Type.OBJECT.getClassName());
                    while (e != o && e != t) {
                        v = VerifierFactory.getVerifier(e.getSuperclassName());
                        vr = v.doPass1();
                        if (vr != VerificationResult.VR_OK) {
                            throw new ClassConstraintException("Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + m + "') has an exception_table entry '" + Pass2Verifier.tostring(element) + "' that references '" + cname + "' as an Exception but '" + e.getSuperclassName() + "' in the ancestor hierachy does not pass verification pass 1: " + vr);
                        }
                        e = Repository.lookupClass(e.getSuperclassName());
                    }
                    if (e == t) continue;
                    throw new ClassConstraintException("Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + m + "') has an exception_table entry '" + Pass2Verifier.tostring(element) + "' that references '" + cname + "' as an Exception but it is not a subclass of '" + t.getClassName() + "'.");
                }
                int methodNumber = -1;
                Method[] ms = Repository.lookupClass(Pass2Verifier.this.verifier.getClassName()).getMethods();
                for (mn = 0; mn < ms.length; ++mn) {
                    if (m != ms[mn]) continue;
                    methodNumber = mn;
                    break;
                }
                if (methodNumber < 0) {
                    for (mn = 0; mn < ms.length; ++mn) {
                        if (!m.getName().equals(ms[mn].getName())) continue;
                        methodNumber = mn;
                        break;
                    }
                }
                if (methodNumber < 0) {
                    throw new AssertionViolatedException("Could not find a known BCEL Method object in the corresponding BCEL JavaClass object.");
                }
                ((Pass2Verifier)Pass2Verifier.this).localVariablesInfos[methodNumber] = new LocalVariablesInfo(obj.getMaxLocals());
                int numOfLvtAttribs = 0;
                for (Attribute att : atts = obj.getAttributes()) {
                    if (!(att instanceof LineNumberTable) && !(att instanceof LocalVariableTable)) {
                        Pass2Verifier.this.addMessage("Attribute '" + Pass2Verifier.tostring(att) + "' as an attribute of Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + m + "') is unknown and will therefore be ignored.");
                    } else {
                        Pass2Verifier.this.addMessage("Attribute '" + Pass2Verifier.tostring(att) + "' as an attribute of Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + m + "') will effectively be ignored and is only useful for debuggers and such.");
                    }
                    if (!(att instanceof LocalVariableTable)) continue;
                    LocalVariableTable lvt = (LocalVariableTable)att;
                    this.checkIndex(lvt, lvt.getNameIndex(), this.CONST_Utf8);
                    String lvtname = ((ConstantUtf8)this.cp.getConstant(lvt.getNameIndex())).getBytes();
                    if (!lvtname.equals("LocalVariableTable")) {
                        throw new ClassConstraintException("The LocalVariableTable attribute '" + Pass2Verifier.tostring(lvt) + "' is not correctly named 'LocalVariableTable' but '" + lvtname + "'.");
                    }
                    for (LocalVariable localvariable : lvt.getLocalVariableTable()) {
                        Type t;
                        this.checkIndex(lvt, localvariable.getNameIndex(), this.CONST_Utf8);
                        String localname = ((ConstantUtf8)this.cp.getConstant(localvariable.getNameIndex())).getBytes();
                        if (!Pass2Verifier.validJavaIdentifier(localname)) {
                            throw new ClassConstraintException("LocalVariableTable '" + Pass2Verifier.tostring(lvt) + "' references a local variable by the name '" + localname + "' which is not a legal Java simple name.");
                        }
                        this.checkIndex(lvt, localvariable.getSignatureIndex(), this.CONST_Utf8);
                        String localsig = ((ConstantUtf8)this.cp.getConstant(localvariable.getSignatureIndex())).getBytes();
                        try {
                            t = Type.getType(localsig);
                        }
                        catch (ClassFormatException cfe) {
                            throw new ClassConstraintException("Illegal descriptor (==signature) '" + localsig + "' used by LocalVariable '" + Pass2Verifier.tostring(localvariable) + "' referenced by '" + Pass2Verifier.tostring(lvt) + "'.", cfe);
                        }
                        int localindex = localvariable.getIndex();
                        if ((t == Type.LONG || t == Type.DOUBLE ? localindex + 1 : localindex) >= obj.getMaxLocals()) {
                            throw new ClassConstraintException("LocalVariableTable attribute '" + Pass2Verifier.tostring(lvt) + "' references a LocalVariable '" + Pass2Verifier.tostring(localvariable) + "' with an index that exceeds the surrounding Code attribute's max_locals value of '" + obj.getMaxLocals() + "'.");
                        }
                        try {
                            Pass2Verifier.this.localVariablesInfos[methodNumber].add(localindex, localname, localvariable.getStartPC(), localvariable.getLength(), t);
                        }
                        catch (LocalVariableInfoInconsistentException lviie) {
                            throw new ClassConstraintException("Conflicting information in LocalVariableTable '" + Pass2Verifier.tostring(lvt) + "' found in Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + Pass2Verifier.tostring(m) + "'). " + lviie.getMessage(), lviie);
                        }
                    }
                    if (m.isStatic() || ++numOfLvtAttribs <= obj.getMaxLocals()) continue;
                    throw new ClassConstraintException("Number of LocalVariableTable attributes of Code attribute '" + Pass2Verifier.tostring(obj) + "' (method '" + Pass2Verifier.tostring(m) + "') exceeds number of local variable slots '" + obj.getMaxLocals() + "' ('There may be at most one LocalVariableTable attribute per local variable in the Code attribute.').");
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitCodeException(CodeException obj) {
        }

        @Override
        public void visitConstantClass(ConstantClass obj) {
            if (obj.getTag() != 7) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
        }

        @Override
        public void visitConstantDouble(ConstantDouble obj) {
            if (obj.getTag() != 6) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
        }

        @Override
        public void visitConstantFieldref(ConstantFieldref obj) {
            if (obj.getTag() != 9) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
            this.checkIndex(obj, obj.getClassIndex(), this.CONST_Class);
            this.checkIndex(obj, obj.getNameAndTypeIndex(), this.CONST_NameAndType);
        }

        @Override
        public void visitConstantFloat(ConstantFloat obj) {
            if (obj.getTag() != 4) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
        }

        @Override
        public void visitConstantInteger(ConstantInteger obj) {
            if (obj.getTag() != 3) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
        }

        @Override
        public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
            if (obj.getTag() != 11) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
            this.checkIndex(obj, obj.getClassIndex(), this.CONST_Class);
            this.checkIndex(obj, obj.getNameAndTypeIndex(), this.CONST_NameAndType);
        }

        @Override
        public void visitConstantLong(ConstantLong obj) {
            if (obj.getTag() != 5) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
        }

        @Override
        public void visitConstantMethodref(ConstantMethodref obj) {
            if (obj.getTag() != 10) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
            this.checkIndex(obj, obj.getClassIndex(), this.CONST_Class);
            this.checkIndex(obj, obj.getNameAndTypeIndex(), this.CONST_NameAndType);
        }

        @Override
        public void visitConstantNameAndType(ConstantNameAndType obj) {
            if (obj.getTag() != 12) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            this.checkIndex(obj, obj.getSignatureIndex(), this.CONST_Utf8);
        }

        @Override
        public void visitConstantPool(ConstantPool obj) {
        }

        @Override
        public void visitConstantString(ConstantString obj) {
            if (obj.getTag() != 8) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
            this.checkIndex(obj, obj.getStringIndex(), this.CONST_Utf8);
        }

        @Override
        public void visitConstantUtf8(ConstantUtf8 obj) {
            if (obj.getTag() != 1) {
                throw new ClassConstraintException("Wrong constant tag in '" + Pass2Verifier.tostring(obj) + "'.");
            }
        }

        @Override
        public void visitConstantValue(ConstantValue obj) {
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            String name = ((ConstantUtf8)this.cp.getConstant(obj.getNameIndex())).getBytes();
            if (!name.equals("ConstantValue")) {
                throw new ClassConstraintException("The ConstantValue attribute '" + Pass2Verifier.tostring(obj) + "' is not correctly named 'ConstantValue' but '" + name + "'.");
            }
            Object pred = this.carrier.predecessor();
            if (pred instanceof Field) {
                Field f = (Field)pred;
                Type fieldType = Type.getType(((ConstantUtf8)this.cp.getConstant(f.getSignatureIndex())).getBytes());
                int index = obj.getConstantValueIndex();
                if (index < 0 || index >= this.cplen) {
                    throw new ClassConstraintException("Invalid index '" + index + "' used by '" + Pass2Verifier.tostring(obj) + "'.");
                }
                Object c = this.cp.getConstant(index);
                if (this.CONST_Long.isInstance(c) && fieldType.equals(Type.LONG) || this.CONST_Float.isInstance(c) && fieldType.equals(Type.FLOAT)) {
                    return;
                }
                if (this.CONST_Double.isInstance(c) && fieldType.equals(Type.DOUBLE)) {
                    return;
                }
                if (this.CONST_Integer.isInstance(c) && (fieldType.equals(Type.INT) || fieldType.equals(Type.SHORT) || fieldType.equals(Type.CHAR) || fieldType.equals(Type.BYTE) || fieldType.equals(Type.BOOLEAN))) {
                    return;
                }
                if (this.CONST_String.isInstance(c) && fieldType.equals(Type.STRING)) {
                    return;
                }
                throw new ClassConstraintException("Illegal type of ConstantValue '" + obj + "' embedding Constant '" + c + "'. It is referenced by field '" + Pass2Verifier.tostring(f) + "' expecting a different type: '" + fieldType + "'.");
            }
        }

        @Override
        public void visitDeprecated(Deprecated obj) {
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            String name = ((ConstantUtf8)this.cp.getConstant(obj.getNameIndex())).getBytes();
            if (!name.equals("Deprecated")) {
                throw new ClassConstraintException("The Deprecated attribute '" + Pass2Verifier.tostring(obj) + "' is not correctly named 'Deprecated' but '" + name + "'.");
            }
        }

        @Override
        public void visitExceptionTable(ExceptionTable obj) {
            try {
                int[] excIndices;
                this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
                String name = ((ConstantUtf8)this.cp.getConstant(obj.getNameIndex())).getBytes();
                if (!name.equals("Exceptions")) {
                    throw new ClassConstraintException("The Exceptions attribute '" + Pass2Verifier.tostring(obj) + "' is not correctly named 'Exceptions' but '" + name + "'.");
                }
                for (int excIndice : excIndices = obj.getExceptionIndexTable()) {
                    this.checkIndex(obj, excIndice, this.CONST_Class);
                    ConstantClass cc = (ConstantClass)this.cp.getConstant(excIndice);
                    this.checkIndex(cc, cc.getNameIndex(), this.CONST_Utf8);
                    String cname = Utility.pathToPackage(((ConstantUtf8)this.cp.getConstant(cc.getNameIndex())).getBytes());
                    Verifier v = VerifierFactory.getVerifier(cname);
                    VerificationResult vr = v.doPass1();
                    if (vr != VerificationResult.VR_OK) {
                        throw new ClassConstraintException("Exceptions attribute '" + Pass2Verifier.tostring(obj) + "' references '" + cname + "' as an Exception but it does not pass verification pass 1: " + vr);
                    }
                    JavaClass e = Repository.lookupClass(cname);
                    JavaClass t = Repository.lookupClass(Type.THROWABLE.getClassName());
                    JavaClass o = Repository.lookupClass(Type.OBJECT.getClassName());
                    while (e != o && e != t) {
                        v = VerifierFactory.getVerifier(e.getSuperclassName());
                        vr = v.doPass1();
                        if (vr != VerificationResult.VR_OK) {
                            throw new ClassConstraintException("Exceptions attribute '" + Pass2Verifier.tostring(obj) + "' references '" + cname + "' as an Exception but '" + e.getSuperclassName() + "' in the ancestor hierachy does not pass verification pass 1: " + vr);
                        }
                        e = Repository.lookupClass(e.getSuperclassName());
                    }
                    if (e == t) continue;
                    throw new ClassConstraintException("Exceptions attribute '" + Pass2Verifier.tostring(obj) + "' references '" + cname + "' as an Exception but it is not a subclass of '" + t.getClassName() + "'.");
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitField(Field obj) {
            Attribute[] atts;
            if (this.jc.isClass()) {
                int maxone = 0;
                if (obj.isPrivate()) {
                    ++maxone;
                }
                if (obj.isProtected()) {
                    ++maxone;
                }
                if (obj.isPublic()) {
                    ++maxone;
                }
                if (maxone > 1) {
                    throw new ClassConstraintException("Field '" + Pass2Verifier.tostring(obj) + "' must only have at most one of its ACC_PRIVATE, ACC_PROTECTED, ACC_PUBLIC modifiers set.");
                }
                if (obj.isFinal() && obj.isVolatile()) {
                    throw new ClassConstraintException("Field '" + Pass2Verifier.tostring(obj) + "' must only have at most one of its ACC_FINAL, ACC_VOLATILE modifiers set.");
                }
            } else {
                if (!obj.isPublic()) {
                    throw new ClassConstraintException("Interface field '" + Pass2Verifier.tostring(obj) + "' must have the ACC_PUBLIC modifier set but hasn't!");
                }
                if (!obj.isStatic()) {
                    throw new ClassConstraintException("Interface field '" + Pass2Verifier.tostring(obj) + "' must have the ACC_STATIC modifier set but hasn't!");
                }
                if (!obj.isFinal()) {
                    throw new ClassConstraintException("Interface field '" + Pass2Verifier.tostring(obj) + "' must have the ACC_FINAL modifier set but hasn't!");
                }
            }
            if ((obj.getAccessFlags() & 0xFFFFFF20) > 0) {
                Pass2Verifier.this.addMessage("Field '" + Pass2Verifier.tostring(obj) + "' has access flag(s) other than ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_VOLATILE, ACC_TRANSIENT set (ignored).");
            }
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            String name = obj.getName();
            if (!Pass2Verifier.validFieldName(name)) {
                throw new ClassConstraintException("Field '" + Pass2Verifier.tostring(obj) + "' has illegal name '" + obj.getName() + "'.");
            }
            this.checkIndex(obj, obj.getSignatureIndex(), this.CONST_Utf8);
            String sig = ((ConstantUtf8)this.cp.getConstant(obj.getSignatureIndex())).getBytes();
            try {
                Type.getType(sig);
            }
            catch (ClassFormatException cfe) {
                throw new ClassConstraintException("Illegal descriptor (==signature) '" + sig + "' used by '" + Pass2Verifier.tostring(obj) + "'.", cfe);
            }
            String nameanddesc = name + sig;
            if (this.fieldNamesAndDesc.contains(nameanddesc)) {
                throw new ClassConstraintException("No two fields (like '" + Pass2Verifier.tostring(obj) + "') are allowed have same names and descriptors!");
            }
            if (this.fieldNames.contains(name)) {
                Pass2Verifier.this.addMessage("More than one field of name '" + name + "' detected (but with different type descriptors). This is very unusual.");
            }
            this.fieldNamesAndDesc.add(nameanddesc);
            this.fieldNames.add(name);
            for (Attribute att : atts = obj.getAttributes()) {
                if (!(att instanceof ConstantValue || att instanceof Synthetic || att instanceof Deprecated)) {
                    Pass2Verifier.this.addMessage("Attribute '" + Pass2Verifier.tostring(att) + "' as an attribute of Field '" + Pass2Verifier.tostring(obj) + "' is unknown and will therefore be ignored.");
                }
                if (att instanceof ConstantValue) continue;
                Pass2Verifier.this.addMessage("Attribute '" + Pass2Verifier.tostring(att) + "' as an attribute of Field '" + Pass2Verifier.tostring(obj) + "' is not a ConstantValue and is therefore only of use for debuggers and such.");
            }
        }

        @Override
        public void visitInnerClass(InnerClass obj) {
        }

        @Override
        public void visitInnerClasses(InnerClasses innerClasses) {
            this.checkIndex(innerClasses, innerClasses.getNameIndex(), this.CONST_Utf8);
            String name = ((ConstantUtf8)this.cp.getConstant(innerClasses.getNameIndex())).getBytes();
            if (!name.equals("InnerClasses")) {
                throw new ClassConstraintException("The InnerClasses attribute '" + Pass2Verifier.tostring(innerClasses) + "' is not correctly named 'InnerClasses' but '" + name + "'.");
            }
            innerClasses.forEach(ic -> {
                int innernameIdx;
                this.checkIndex(innerClasses, ic.getInnerClassIndex(), this.CONST_Class);
                int outerIdx = ic.getOuterClassIndex();
                if (outerIdx != 0) {
                    this.checkIndex(innerClasses, outerIdx, this.CONST_Class);
                }
                if ((innernameIdx = ic.getInnerNameIndex()) != 0) {
                    this.checkIndex(innerClasses, innernameIdx, this.CONST_Utf8);
                }
                int acc = ic.getInnerAccessFlags();
                if ((acc &= 0xFFFFF9E0) != 0) {
                    Pass2Verifier.this.addMessage("Unknown access flag for inner class '" + Pass2Verifier.tostring(ic) + "' set (InnerClasses attribute '" + Pass2Verifier.tostring(innerClasses) + "').");
                }
            });
        }

        @Override
        public void visitJavaClass(JavaClass obj) {
            Attribute[] atts = obj.getAttributes();
            boolean foundSourceFile = false;
            boolean foundInnerClasses = false;
            boolean hasInnerClass = new InnerClassDetector(this.jc).innerClassReferenced();
            for (Attribute att : atts) {
                if (!(att instanceof SourceFile || att instanceof Deprecated || att instanceof InnerClasses || att instanceof Synthetic)) {
                    Pass2Verifier.this.addMessage("Attribute '" + Pass2Verifier.tostring(att) + "' as an attribute of the ClassFile structure '" + Pass2Verifier.tostring(obj) + "' is unknown and will therefore be ignored.");
                }
                if (att instanceof SourceFile) {
                    if (foundSourceFile) {
                        throw new ClassConstraintException("A ClassFile structure (like '" + Pass2Verifier.tostring(obj) + "') may have no more than one SourceFile attribute.");
                    }
                    foundSourceFile = true;
                }
                if (!(att instanceof InnerClasses)) continue;
                if (!foundInnerClasses) {
                    foundInnerClasses = true;
                } else if (hasInnerClass) {
                    throw new ClassConstraintException("A Classfile structure (like '" + Pass2Verifier.tostring(obj) + "') must have exactly one InnerClasses attribute if at least one Inner Class is referenced (which is the case). More than one InnerClasses attribute was found.");
                }
                if (hasInnerClass) continue;
                Pass2Verifier.this.addMessage("No referenced Inner Class found, but InnerClasses attribute '" + Pass2Verifier.tostring(att) + "' found. Strongly suggest removal of that attribute.");
            }
            if (hasInnerClass && !foundInnerClasses) {
                Pass2Verifier.this.addMessage("A Classfile structure (like '" + Pass2Verifier.tostring(obj) + "') must have exactly one InnerClasses attribute if at least one Inner Class is referenced (which is the case). No InnerClasses attribute was found.");
            }
        }

        @Override
        public void visitLineNumber(LineNumber obj) {
        }

        @Override
        public void visitLineNumberTable(LineNumberTable obj) {
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            String name = ((ConstantUtf8)this.cp.getConstant(obj.getNameIndex())).getBytes();
            if (!name.equals("LineNumberTable")) {
                throw new ClassConstraintException("The LineNumberTable attribute '" + Pass2Verifier.tostring(obj) + "' is not correctly named 'LineNumberTable' but '" + name + "'.");
            }
        }

        @Override
        public void visitLocalVariable(LocalVariable obj) {
        }

        @Override
        public void visitLocalVariableTable(LocalVariableTable obj) {
        }

        @Override
        public void visitMethod(Method obj) {
            String nameanddesc;
            Object v;
            VerificationResult vr;
            Type[] ts;
            Type t;
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            String name = obj.getName();
            if (!Pass2Verifier.validMethodName(name, true)) {
                throw new ClassConstraintException("Method '" + Pass2Verifier.tostring(obj) + "' has illegal name '" + name + "'.");
            }
            this.checkIndex(obj, obj.getSignatureIndex(), this.CONST_Utf8);
            String sig = ((ConstantUtf8)this.cp.getConstant(obj.getSignatureIndex())).getBytes();
            try {
                t = Type.getReturnType(sig);
                ts = Type.getArgumentTypes(sig);
            }
            catch (ClassFormatException cfe) {
                throw new ClassConstraintException("Illegal descriptor (==signature) '" + sig + "' used by Method '" + Pass2Verifier.tostring(obj) + "'.", cfe);
            }
            Object act = t;
            if (act instanceof ArrayType) {
                act = ((ArrayType)act).getBasicType();
            }
            if (act instanceof ObjectType && (vr = ((Verifier)(v = VerifierFactory.getVerifier(((ObjectType)act).getClassName()))).doPass1()) != VerificationResult.VR_OK) {
                throw new ClassConstraintException("Method '" + Pass2Verifier.tostring(obj) + "' has a return type that does not pass verification pass 1: '" + vr + "'.");
            }
            for (Type element : ts) {
                Verifier v2;
                VerificationResult vr2;
                act = element;
                if (act instanceof ArrayType) {
                    act = ((ArrayType)act).getBasicType();
                }
                if (!(act instanceof ObjectType) || (vr2 = (v2 = VerifierFactory.getVerifier(((ObjectType)act).getClassName())).doPass1()) == VerificationResult.VR_OK) continue;
                throw new ClassConstraintException("Method '" + Pass2Verifier.tostring(obj) + "' has an argument type that does not pass verification pass 1: '" + vr2 + "'.");
            }
            if (name.equals("<clinit>") && ts.length != 0) {
                throw new ClassConstraintException("Method '" + Pass2Verifier.tostring(obj) + "' has illegal name '" + name + "'. Its name resembles the class or interface initialization method which it isn't because of its arguments (==descriptor).");
            }
            if (this.jc.isClass()) {
                int maxone = 0;
                if (obj.isPrivate()) {
                    ++maxone;
                }
                if (obj.isProtected()) {
                    ++maxone;
                }
                if (obj.isPublic()) {
                    ++maxone;
                }
                if (maxone > 1) {
                    throw new ClassConstraintException("Method '" + Pass2Verifier.tostring(obj) + "' must only have at most one of its ACC_PRIVATE, ACC_PROTECTED, ACC_PUBLIC modifiers set.");
                }
                if (obj.isAbstract()) {
                    if (obj.isFinal()) {
                        throw new ClassConstraintException("Abstract method '" + Pass2Verifier.tostring(obj) + "' must not have the ACC_FINAL modifier set.");
                    }
                    if (obj.isNative()) {
                        throw new ClassConstraintException("Abstract method '" + Pass2Verifier.tostring(obj) + "' must not have the ACC_NATIVE modifier set.");
                    }
                    if (obj.isPrivate()) {
                        throw new ClassConstraintException("Abstract method '" + Pass2Verifier.tostring(obj) + "' must not have the ACC_PRIVATE modifier set.");
                    }
                    if (obj.isStatic()) {
                        throw new ClassConstraintException("Abstract method '" + Pass2Verifier.tostring(obj) + "' must not have the ACC_STATIC modifier set.");
                    }
                    if (obj.isStrictfp()) {
                        throw new ClassConstraintException("Abstract method '" + Pass2Verifier.tostring(obj) + "' must not have the ACC_STRICT modifier set.");
                    }
                    if (obj.isSynchronized()) {
                        throw new ClassConstraintException("Abstract method '" + Pass2Verifier.tostring(obj) + "' must not have the ACC_SYNCHRONIZED modifier set.");
                    }
                }
                if (name.equals("<init>") && (obj.isStatic() || obj.isFinal() || obj.isSynchronized() || obj.isNative() || obj.isAbstract())) {
                    throw new ClassConstraintException("Instance initialization method '" + Pass2Verifier.tostring(obj) + "' must not have any of the ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED, ACC_NATIVE, ACC_ABSTRACT modifiers set.");
                }
            } else if (!name.equals("<clinit>")) {
                if (this.jc.getMajor() >= 52) {
                    if (obj.isPublic() == obj.isPrivate()) {
                        throw new ClassConstraintException("Interface method '" + Pass2Verifier.tostring(obj) + "' must have exactly one of its ACC_PUBLIC and ACC_PRIVATE modifiers set.");
                    }
                    if (obj.isProtected() || obj.isFinal() || obj.isSynchronized() || obj.isNative()) {
                        throw new ClassConstraintException("Interface method '" + Pass2Verifier.tostring(obj) + "' must not have any of the ACC_PROTECTED, ACC_FINAL, ACC_SYNCHRONIZED, or ACC_NATIVE modifiers set.");
                    }
                } else {
                    if (!obj.isPublic()) {
                        throw new ClassConstraintException("Interface method '" + Pass2Verifier.tostring(obj) + "' must have the ACC_PUBLIC modifier set but hasn't!");
                    }
                    if (!obj.isAbstract()) {
                        throw new ClassConstraintException("Interface method '" + Pass2Verifier.tostring(obj) + "' must have the ACC_ABSTRACT modifier set but hasn't!");
                    }
                    if (obj.isPrivate() || obj.isProtected() || obj.isStatic() || obj.isFinal() || obj.isSynchronized() || obj.isNative() || obj.isStrictfp()) {
                        throw new ClassConstraintException("Interface method '" + Pass2Verifier.tostring(obj) + "' must not have any of the ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED, ACC_NATIVE, ACC_ABSTRACT, ACC_STRICT modifiers set.");
                    }
                }
            }
            if ((obj.getAccessFlags() & 0xFFFFF2C0) > 0) {
                Pass2Verifier.this.addMessage("Method '" + Pass2Verifier.tostring(obj) + "' has access flag(s) other than ACC_PUBLIC, ACC_PRIVATE, ACC_PROTECTED, ACC_STATIC, ACC_FINAL, ACC_SYNCHRONIZED, ACC_NATIVE, ACC_ABSTRACT, ACC_STRICT set (ignored).");
            }
            if (this.methodNamesAndDesc.contains(nameanddesc = name + sig)) {
                throw new ClassConstraintException("No two methods (like '" + Pass2Verifier.tostring(obj) + "') are allowed have same names and desciptors!");
            }
            this.methodNamesAndDesc.add(nameanddesc);
            Attribute[] atts = obj.getAttributes();
            int numCodeAtts = 0;
            for (Attribute att : atts) {
                if (!(att instanceof Code || att instanceof ExceptionTable || att instanceof Synthetic || att instanceof Deprecated)) {
                    Pass2Verifier.this.addMessage("Attribute '" + Pass2Verifier.tostring(att) + "' as an attribute of Method '" + Pass2Verifier.tostring(obj) + "' is unknown and will therefore be ignored.");
                }
                if (!(att instanceof Code) && !(att instanceof ExceptionTable)) {
                    Pass2Verifier.this.addMessage("Attribute '" + Pass2Verifier.tostring(att) + "' as an attribute of Method '" + Pass2Verifier.tostring(obj) + "' is neither Code nor Exceptions and is therefore only of use for debuggers and such.");
                }
                if (att instanceof Code && (obj.isNative() || obj.isAbstract())) {
                    throw new ClassConstraintException("Native or abstract methods like '" + Pass2Verifier.tostring(obj) + "' must not have a Code attribute like '" + Pass2Verifier.tostring(att) + "'.");
                }
                if (!(att instanceof Code)) continue;
                ++numCodeAtts;
            }
            if (!obj.isNative() && !obj.isAbstract() && numCodeAtts != 1) {
                throw new ClassConstraintException("Non-native, non-abstract methods like '" + Pass2Verifier.tostring(obj) + "' must have exactly one Code attribute (found: " + numCodeAtts + ").");
            }
        }

        @Override
        public void visitSourceFile(SourceFile obj) {
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            String name = ((ConstantUtf8)this.cp.getConstant(obj.getNameIndex())).getBytes();
            if (!name.equals("SourceFile")) {
                throw new ClassConstraintException("The SourceFile attribute '" + Pass2Verifier.tostring(obj) + "' is not correctly named 'SourceFile' but '" + name + "'.");
            }
            this.checkIndex(obj, obj.getSourceFileIndex(), this.CONST_Utf8);
            String sourceFileName = ((ConstantUtf8)this.cp.getConstant(obj.getSourceFileIndex())).getBytes();
            String sourceFileNameLc = sourceFileName.toLowerCase(Locale.ENGLISH);
            if (sourceFileName.indexOf(47) != -1 || sourceFileName.indexOf(92) != -1 || sourceFileName.indexOf(58) != -1 || sourceFileNameLc.lastIndexOf(".java") == -1) {
                Pass2Verifier.this.addMessage("SourceFile attribute '" + Pass2Verifier.tostring(obj) + "' has a funny name: remember not to confuse certain parsers working on javap's output. Also, this name ('" + sourceFileName + "') is considered an unqualified (simple) file name only.");
            }
        }

        @Override
        public void visitSynthetic(Synthetic obj) {
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            String name = ((ConstantUtf8)this.cp.getConstant(obj.getNameIndex())).getBytes();
            if (!name.equals("Synthetic")) {
                throw new ClassConstraintException("The Synthetic attribute '" + Pass2Verifier.tostring(obj) + "' is not correctly named 'Synthetic' but '" + name + "'.");
            }
        }

        @Override
        public void visitUnknown(Unknown obj) {
            this.checkIndex(obj, obj.getNameIndex(), this.CONST_Utf8);
            Pass2Verifier.this.addMessage("Unknown attribute '" + Pass2Verifier.tostring(obj) + "'. This attribute is not known in any context!");
        }
    }
}

