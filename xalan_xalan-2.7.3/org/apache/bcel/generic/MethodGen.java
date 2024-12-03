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
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.LocalVariableTypeTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.classfile.ParameterAnnotations;
import org.apache.bcel.classfile.RuntimeVisibleParameterAnnotations;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGenOrMethodGen;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LineNumberGen;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MethodObserver;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.util.BCELComparator;
import org.apache.commons.lang3.ArrayUtils;

public class MethodGen
extends FieldGenOrMethodGen {
    private static BCELComparator bcelComparator = new BCELComparator(){

        @Override
        public boolean equals(Object o1, Object o2) {
            FieldGenOrMethodGen THIS = (FieldGenOrMethodGen)o1;
            FieldGenOrMethodGen THAT = (FieldGenOrMethodGen)o2;
            return Objects.equals(THIS.getName(), THAT.getName()) && Objects.equals(THIS.getSignature(), THAT.getSignature());
        }

        @Override
        public int hashCode(Object o) {
            FieldGenOrMethodGen THIS = (FieldGenOrMethodGen)o;
            return THIS.getSignature().hashCode() ^ THIS.getName().hashCode();
        }
    };
    private String className;
    private Type[] argTypes;
    private String[] argNames;
    private int maxLocals;
    private int maxStack;
    private InstructionList il;
    private boolean stripAttributes;
    private LocalVariableTypeTable localVariableTypeTable;
    private final List<LocalVariableGen> variableList = new ArrayList<LocalVariableGen>();
    private final List<LineNumberGen> lineNumberList = new ArrayList<LineNumberGen>();
    private final List<CodeExceptionGen> exceptionList = new ArrayList<CodeExceptionGen>();
    private final List<String> throwsList = new ArrayList<String>();
    private final List<Attribute> codeAttrsList = new ArrayList<Attribute>();
    private List<AnnotationEntryGen>[] paramAnnotations;
    private boolean hasParameterAnnotations;
    private boolean haveUnpackedParameterAnnotations;
    private List<MethodObserver> observers;

    private static byte[] getByteCodes(Method method) {
        Code code = method.getCode();
        if (code == null) {
            throw new IllegalStateException(String.format("The method '%s' has no code.", method));
        }
        return code.getCode();
    }

    public static BCELComparator getComparator() {
        return bcelComparator;
    }

    public static int getMaxStack(ConstantPoolGen cp, InstructionList il, CodeExceptionGen[] et) {
        BranchStack branchTargets = new BranchStack();
        for (CodeExceptionGen element : et) {
            InstructionHandle handlerPc = element.getHandlerPC();
            if (handlerPc == null) continue;
            branchTargets.push(handlerPc, 1);
        }
        int stackDepth = 0;
        int maxStackDepth = 0;
        InstructionHandle ih = il.getStart();
        while (ih != null) {
            BranchTarget bt;
            Instruction instruction = ih.getInstruction();
            short opcode = instruction.getOpcode();
            int delta = instruction.produceStack(cp) - instruction.consumeStack(cp);
            if ((stackDepth += delta) > maxStackDepth) {
                maxStackDepth = stackDepth;
            }
            if (instruction instanceof BranchInstruction) {
                BranchInstruction branch = (BranchInstruction)instruction;
                if (instruction instanceof Select) {
                    InstructionHandle[] targets;
                    Select select = (Select)branch;
                    for (InstructionHandle target : targets = select.getTargets()) {
                        branchTargets.push(target, stackDepth);
                    }
                    ih = null;
                } else if (!(branch instanceof IfInstruction)) {
                    if (opcode == 168 || opcode == 201) {
                        branchTargets.push(ih.getNext(), stackDepth - 1);
                    }
                    ih = null;
                }
                branchTargets.push(branch.getTarget(), stackDepth);
            } else if (opcode == 191 || opcode == 169 || opcode >= 172 && opcode <= 177) {
                ih = null;
            }
            if (ih != null) {
                ih = ih.getNext();
            }
            if (ih != null || (bt = branchTargets.pop()) == null) continue;
            ih = bt.target;
            stackDepth = bt.stackDepth;
        }
        return maxStackDepth;
    }

    public static void setComparator(BCELComparator comparator) {
        bcelComparator = comparator;
    }

    public MethodGen(int accessFlags, Type returnType, Type[] argTypes, String[] argNames, String methodName, String className, InstructionList il, ConstantPoolGen cp) {
        super(accessFlags);
        this.setType(returnType);
        this.setArgumentTypes(argTypes);
        this.setArgumentNames(argNames);
        this.setName(methodName);
        this.setClassName(className);
        this.setInstructionList(il);
        this.setConstantPool(cp);
        boolean abstract_ = this.isAbstract() || this.isNative();
        InstructionHandle start = null;
        InstructionHandle end = null;
        if (!abstract_) {
            start = il.getStart();
            if (!this.isStatic() && className != null) {
                this.addLocalVariable("this", ObjectType.getInstance(className), start, end);
            }
        }
        if (argTypes != null) {
            int size = argTypes.length;
            for (Type argType : argTypes) {
                if (Type.VOID != argType) continue;
                throw new ClassGenException("'void' is an illegal argument type for a method");
            }
            if (argNames != null) {
                if (size != argNames.length) {
                    throw new ClassGenException("Mismatch in argument array lengths: " + size + " vs. " + argNames.length);
                }
            } else {
                argNames = new String[size];
                for (int i = 0; i < size; ++i) {
                    argNames[i] = "arg" + i;
                }
                this.setArgumentNames(argNames);
            }
            if (!abstract_) {
                for (int i = 0; i < size; ++i) {
                    this.addLocalVariable(argNames[i], argTypes[i], start, end);
                }
            }
        }
    }

    public MethodGen(Method method, String className, ConstantPoolGen cp) {
        this(method.getAccessFlags(), Type.getReturnType(method.getSignature()), Type.getArgumentTypes(method.getSignature()), null, method.getName(), className, (method.getAccessFlags() & 0x500) == 0 ? new InstructionList(MethodGen.getByteCodes(method)) : null, cp);
        Attribute[] attributes;
        for (Attribute attribute : attributes = method.getAttributes()) {
            Attribute a = attribute;
            if (a instanceof Code) {
                Attribute[] cAttributes;
                Code c = (Code)a;
                this.setMaxStack(c.getMaxStack());
                this.setMaxLocals(c.getMaxLocals());
                CodeException[] ces = c.getExceptionTable();
                if (ces != null) {
                    for (CodeException ce : ces) {
                        InstructionHandle end;
                        int type = ce.getCatchType();
                        ObjectType cType = null;
                        if (type > 0) {
                            String cen = method.getConstantPool().getConstantString(type, (byte)7);
                            cType = ObjectType.getInstance(cen);
                        }
                        int endPc = ce.getEndPC();
                        int length = MethodGen.getByteCodes(method).length;
                        if (length == endPc) {
                            end = this.il.getEnd();
                        } else {
                            end = this.il.findHandle(endPc);
                            end = end.getPrev();
                        }
                        this.addExceptionHandler(this.il.findHandle(ce.getStartPC()), end, this.il.findHandle(ce.getHandlerPC()), cType);
                    }
                }
                for (Attribute cAttribute : cAttributes = c.getAttributes()) {
                    a = cAttribute;
                    if (a instanceof LineNumberTable) {
                        ((LineNumberTable)a).forEach(l -> {
                            InstructionHandle ih = this.il.findHandle(l.getStartPC());
                            if (ih != null) {
                                this.addLineNumber(ih, l.getLineNumber());
                            }
                        });
                        continue;
                    }
                    if (a instanceof LocalVariableTable) {
                        this.updateLocalVariableTable((LocalVariableTable)a);
                        continue;
                    }
                    if (a instanceof LocalVariableTypeTable) {
                        this.localVariableTypeTable = (LocalVariableTypeTable)a.copy(cp.getConstantPool());
                        continue;
                    }
                    this.addCodeAttribute(a);
                }
                continue;
            }
            if (a instanceof ExceptionTable) {
                Collections.addAll(this.throwsList, ((ExceptionTable)a).getExceptionNames());
                continue;
            }
            if (a instanceof Annotations) {
                Annotations runtimeAnnotations = (Annotations)a;
                runtimeAnnotations.forEach(element -> this.addAnnotationEntry(new AnnotationEntryGen((AnnotationEntry)element, cp, false)));
                continue;
            }
            this.addAttribute(a);
        }
    }

    public void addAnnotationsAsAttribute(ConstantPoolGen cp) {
        this.addAll(AnnotationEntryGen.getAnnotationAttributes(cp, super.getAnnotationEntries()));
    }

    public void addCodeAttribute(Attribute a) {
        this.codeAttrsList.add(a);
    }

    public void addException(String className) {
        this.throwsList.add(className);
    }

    public CodeExceptionGen addExceptionHandler(InstructionHandle startPc, InstructionHandle endPc, InstructionHandle handlerPc, ObjectType catchType) {
        if (startPc == null || endPc == null || handlerPc == null) {
            throw new ClassGenException("Exception handler target is null instruction");
        }
        CodeExceptionGen c = new CodeExceptionGen(startPc, endPc, handlerPc, catchType);
        this.exceptionList.add(c);
        return c;
    }

    public LineNumberGen addLineNumber(InstructionHandle ih, int srcLine) {
        LineNumberGen l = new LineNumberGen(ih, srcLine);
        this.lineNumberList.add(l);
        return l;
    }

    public LocalVariableGen addLocalVariable(String name, Type type, InstructionHandle start, InstructionHandle end) {
        return this.addLocalVariable(name, type, this.maxLocals, start, end);
    }

    public LocalVariableGen addLocalVariable(String name, Type type, int slot, InstructionHandle start, InstructionHandle end) {
        return this.addLocalVariable(name, type, slot, start, end, slot);
    }

    public LocalVariableGen addLocalVariable(String name, Type type, int slot, InstructionHandle start, InstructionHandle end, int origIndex) {
        byte t = type.getType();
        if (t != 16) {
            LocalVariableGen l;
            int i;
            int add = type.getSize();
            if (slot + add > this.maxLocals) {
                this.maxLocals = slot + add;
            }
            if ((i = this.variableList.indexOf(l = new LocalVariableGen(slot, name, type, start, end, origIndex))) >= 0) {
                this.variableList.set(i, l);
            } else {
                this.variableList.add(l);
            }
            return l;
        }
        throw new IllegalArgumentException("Can not use " + type + " as type for local variable");
    }

    public void addObserver(MethodObserver o) {
        if (this.observers == null) {
            this.observers = new ArrayList<MethodObserver>();
        }
        this.observers.add(o);
    }

    public void addParameterAnnotation(int parameterIndex, AnnotationEntryGen annotation) {
        List<AnnotationEntryGen> existingAnnotations;
        this.ensureExistingParameterAnnotationsUnpacked();
        if (!this.hasParameterAnnotations) {
            List[] parmList = new List[this.argTypes.length];
            this.paramAnnotations = parmList;
            this.hasParameterAnnotations = true;
        }
        if ((existingAnnotations = this.paramAnnotations[parameterIndex]) != null) {
            existingAnnotations.add(annotation);
        } else {
            ArrayList<AnnotationEntryGen> l = new ArrayList<AnnotationEntryGen>();
            l.add(annotation);
            this.paramAnnotations[parameterIndex] = l;
        }
    }

    public void addParameterAnnotationsAsAttribute(ConstantPoolGen cp) {
        if (!this.hasParameterAnnotations) {
            return;
        }
        Attribute[] attrs = AnnotationEntryGen.getParameterAnnotationAttributes(cp, this.paramAnnotations);
        if (attrs != null) {
            this.addAll(attrs);
        }
    }

    private Attribute[] addRuntimeAnnotationsAsAttribute(ConstantPoolGen cp) {
        Attribute[] attrs = AnnotationEntryGen.getAnnotationAttributes(cp, super.getAnnotationEntries());
        this.addAll(attrs);
        return attrs;
    }

    private Attribute[] addRuntimeParameterAnnotationsAsAttribute(ConstantPoolGen cp) {
        if (!this.hasParameterAnnotations) {
            return Attribute.EMPTY_ARRAY;
        }
        Attribute[] attrs = AnnotationEntryGen.getParameterAnnotationAttributes(cp, this.paramAnnotations);
        this.addAll(attrs);
        return attrs;
    }

    private void adjustLocalVariableTypeTable(LocalVariableTable lvt) {
        LocalVariable[] lv = lvt.getLocalVariableTable();
        block0: for (LocalVariable element : this.localVariableTypeTable.getLocalVariableTypeTable()) {
            for (LocalVariable l : lv) {
                if (!element.getName().equals(l.getName()) || element.getIndex() != l.getOrigIndex()) continue;
                element.setLength(l.getLength());
                element.setStartPC(l.getStartPC());
                element.setIndex(l.getIndex());
                continue block0;
            }
        }
    }

    public MethodGen copy(String className, ConstantPoolGen cp) {
        Method m = ((MethodGen)this.clone()).getMethod();
        MethodGen mg = new MethodGen(m, className, super.getConstantPool());
        if (super.getConstantPool() != cp) {
            mg.setConstantPool(cp);
            mg.getInstructionList().replaceConstantPool(super.getConstantPool(), cp);
        }
        return mg;
    }

    private void ensureExistingParameterAnnotationsUnpacked() {
        if (this.haveUnpackedParameterAnnotations) {
            return;
        }
        Attribute[] attrs = this.getAttributes();
        ParameterAnnotations paramAnnVisAttr = null;
        ParameterAnnotations paramAnnInvisAttr = null;
        for (Attribute attribute : attrs) {
            if (!(attribute instanceof ParameterAnnotations)) continue;
            if (!this.hasParameterAnnotations) {
                List[] parmList = new List[this.argTypes.length];
                this.paramAnnotations = parmList;
                Arrays.setAll(this.paramAnnotations, i -> new ArrayList());
            }
            this.hasParameterAnnotations = true;
            ParameterAnnotations rpa = (ParameterAnnotations)attribute;
            if (rpa instanceof RuntimeVisibleParameterAnnotations) {
                paramAnnVisAttr = rpa;
            } else {
                paramAnnInvisAttr = rpa;
            }
            ParameterAnnotationEntry[] parameterAnnotationEntries = rpa.getParameterAnnotationEntries();
            for (int j = 0; j < parameterAnnotationEntries.length; ++j) {
                ParameterAnnotationEntry immutableArray = rpa.getParameterAnnotationEntries()[j];
                List<AnnotationEntryGen> mutable = this.makeMutableVersion(immutableArray.getAnnotationEntries());
                this.paramAnnotations[j].addAll(mutable);
            }
        }
        if (paramAnnVisAttr != null) {
            this.removeAttribute(paramAnnVisAttr);
        }
        if (paramAnnInvisAttr != null) {
            this.removeAttribute(paramAnnInvisAttr);
        }
        this.haveUnpackedParameterAnnotations = true;
    }

    public boolean equals(Object obj) {
        return bcelComparator.equals(this, obj);
    }

    public List<AnnotationEntryGen> getAnnotationsOnParameter(int i) {
        this.ensureExistingParameterAnnotationsUnpacked();
        if (!this.hasParameterAnnotations || i > this.argTypes.length) {
            return null;
        }
        return this.paramAnnotations[i];
    }

    public String getArgumentName(int i) {
        return this.argNames[i];
    }

    public String[] getArgumentNames() {
        return (String[])this.argNames.clone();
    }

    public Type getArgumentType(int i) {
        return this.argTypes[i];
    }

    public Type[] getArgumentTypes() {
        return (Type[])this.argTypes.clone();
    }

    public String getClassName() {
        return this.className;
    }

    public Attribute[] getCodeAttributes() {
        return this.codeAttrsList.toArray(Attribute.EMPTY_ARRAY);
    }

    private CodeException[] getCodeExceptions() {
        int size = this.exceptionList.size();
        CodeException[] cExc = new CodeException[size];
        Arrays.setAll(cExc, i -> this.exceptionList.get(i).getCodeException(super.getConstantPool()));
        return cExc;
    }

    public CodeExceptionGen[] getExceptionHandlers() {
        return this.exceptionList.toArray(CodeExceptionGen.EMPTY_ARRAY);
    }

    public String[] getExceptions() {
        return this.throwsList.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    private ExceptionTable getExceptionTable(ConstantPoolGen cp) {
        int size = this.throwsList.size();
        int[] ex = new int[size];
        Arrays.setAll(ex, i -> cp.addClass(this.throwsList.get(i)));
        return new ExceptionTable(cp.addUtf8("Exceptions"), 2 + 2 * size, ex, cp.getConstantPool());
    }

    public InstructionList getInstructionList() {
        return this.il;
    }

    public LineNumberGen[] getLineNumbers() {
        return this.lineNumberList.toArray(LineNumberGen.EMPTY_ARRAY);
    }

    public LineNumberTable getLineNumberTable(ConstantPoolGen cp) {
        int size = this.lineNumberList.size();
        LineNumber[] ln = new LineNumber[size];
        Arrays.setAll(ln, i -> this.lineNumberList.get(i).getLineNumber());
        return new LineNumberTable(cp.addUtf8("LineNumberTable"), 2 + ln.length * 4, ln, cp.getConstantPool());
    }

    public LocalVariableGen[] getLocalVariables() {
        int size = this.variableList.size();
        LocalVariableGen[] lg = new LocalVariableGen[size];
        this.variableList.toArray(lg);
        for (int i = 0; i < size; ++i) {
            if (lg[i].getStart() == null && this.il != null) {
                lg[i].setStart(this.il.getStart());
            }
            if (lg[i].getEnd() != null || this.il == null) continue;
            lg[i].setEnd(this.il.getEnd());
        }
        if (size > 1) {
            Arrays.sort(lg, Comparator.comparingInt(LocalVariableGen::getIndex));
        }
        return lg;
    }

    public LocalVariableTable getLocalVariableTable(ConstantPoolGen cp) {
        LocalVariableGen[] lg = this.getLocalVariables();
        int size = lg.length;
        LocalVariable[] lv = new LocalVariable[size];
        Arrays.setAll(lv, i -> lg[i].getLocalVariable(cp));
        return new LocalVariableTable(cp.addUtf8("LocalVariableTable"), 2 + lv.length * 10, lv, cp.getConstantPool());
    }

    public LocalVariableTypeTable getLocalVariableTypeTable() {
        return this.localVariableTypeTable;
    }

    public int getMaxLocals() {
        return this.maxLocals;
    }

    public int getMaxStack() {
        return this.maxStack;
    }

    public Method getMethod() {
        String signature = this.getSignature();
        ConstantPoolGen cp = super.getConstantPool();
        int nameIndex = cp.addUtf8(super.getName());
        int signatureIndex = cp.addUtf8(signature);
        byte[] byteCode = this.il != null ? this.il.getByteCode() : null;
        LineNumberTable lnt = null;
        LocalVariableTable lvt = null;
        if (!this.variableList.isEmpty() && !this.stripAttributes) {
            this.updateLocalVariableTable(this.getLocalVariableTable(cp));
            lvt = this.getLocalVariableTable(cp);
            this.addCodeAttribute(lvt);
        }
        if (this.localVariableTypeTable != null) {
            if (lvt != null) {
                this.adjustLocalVariableTypeTable(lvt);
            }
            this.addCodeAttribute(this.localVariableTypeTable);
        }
        if (!this.lineNumberList.isEmpty() && !this.stripAttributes) {
            lnt = this.getLineNumberTable(cp);
            this.addCodeAttribute(lnt);
        }
        Attribute[] codeAttrs = this.getCodeAttributes();
        int attrsLen = 0;
        for (Attribute codeAttr : codeAttrs) {
            attrsLen += codeAttr.getLength() + 6;
        }
        CodeException[] cExc = this.getCodeExceptions();
        int excLen = cExc.length * 8;
        Code code = null;
        if (byteCode != null && !this.isAbstract() && !this.isNative()) {
            Attribute[] attributes;
            for (Attribute a : attributes = this.getAttributes()) {
                if (!(a instanceof Code)) continue;
                this.removeAttribute(a);
            }
            code = new Code(cp.addUtf8("Code"), 8 + byteCode.length + 2 + excLen + 2 + attrsLen, this.maxStack, this.maxLocals, byteCode, cExc, codeAttrs, cp.getConstantPool());
            this.addAttribute(code);
        }
        Attribute[] annotations = this.addRuntimeAnnotationsAsAttribute(cp);
        Attribute[] parameterAnnotations = this.addRuntimeParameterAnnotationsAsAttribute(cp);
        ExceptionTable et = null;
        if (!this.throwsList.isEmpty()) {
            et = this.getExceptionTable(cp);
            this.addAttribute(et);
        }
        Method m = new Method(super.getAccessFlags(), nameIndex, signatureIndex, this.getAttributes(), cp.getConstantPool());
        if (lvt != null) {
            this.removeCodeAttribute(lvt);
        }
        if (this.localVariableTypeTable != null) {
            this.removeCodeAttribute(this.localVariableTypeTable);
        }
        if (lnt != null) {
            this.removeCodeAttribute(lnt);
        }
        if (code != null) {
            this.removeAttribute(code);
        }
        if (et != null) {
            this.removeAttribute(et);
        }
        this.removeRuntimeAttributes(annotations);
        this.removeRuntimeAttributes(parameterAnnotations);
        return m;
    }

    public Type getReturnType() {
        return this.getType();
    }

    @Override
    public String getSignature() {
        return Type.getMethodSignature(super.getType(), this.argTypes);
    }

    public int hashCode() {
        return bcelComparator.hashCode(this);
    }

    private List<AnnotationEntryGen> makeMutableVersion(AnnotationEntry[] mutableArray) {
        ArrayList<AnnotationEntryGen> result = new ArrayList<AnnotationEntryGen>();
        for (AnnotationEntry element : mutableArray) {
            result.add(new AnnotationEntryGen(element, this.getConstantPool(), false));
        }
        return result;
    }

    public void removeCodeAttribute(Attribute a) {
        this.codeAttrsList.remove(a);
    }

    public void removeCodeAttributes() {
        this.localVariableTypeTable = null;
        this.codeAttrsList.clear();
    }

    public void removeException(String c) {
        this.throwsList.remove(c);
    }

    public void removeExceptionHandler(CodeExceptionGen c) {
        this.exceptionList.remove(c);
    }

    public void removeExceptionHandlers() {
        this.exceptionList.clear();
    }

    public void removeExceptions() {
        this.throwsList.clear();
    }

    public void removeLineNumber(LineNumberGen l) {
        this.lineNumberList.remove(l);
    }

    public void removeLineNumbers() {
        this.lineNumberList.clear();
    }

    public void removeLocalVariable(LocalVariableGen l) {
        l.dispose();
        this.variableList.remove(l);
    }

    public void removeLocalVariables() {
        this.variableList.forEach(LocalVariableGen::dispose);
        this.variableList.clear();
    }

    public void removeLocalVariableTypeTable() {
        this.localVariableTypeTable = null;
    }

    public void removeNOPs() {
        if (this.il != null) {
            InstructionHandle ih = this.il.getStart();
            while (ih != null) {
                InstructionHandle next = ih.getNext();
                if (next != null && ih.getInstruction() instanceof NOP) {
                    try {
                        this.il.delete(ih);
                    }
                    catch (TargetLostException e) {
                        for (InstructionHandle target : e.getTargets()) {
                            for (InstructionTargeter targeter : target.getTargeters()) {
                                targeter.updateTarget(target, next);
                            }
                        }
                    }
                }
                ih = next;
            }
        }
    }

    public void removeObserver(MethodObserver o) {
        if (this.observers != null) {
            this.observers.remove(o);
        }
    }

    public void removeRuntimeAttributes(Attribute[] attrs) {
        for (Attribute attr : attrs) {
            this.removeAttribute(attr);
        }
    }

    public void setArgumentName(int i, String name) {
        this.argNames[i] = name;
    }

    public void setArgumentNames(String[] argNames) {
        this.argNames = argNames;
    }

    public void setArgumentType(int i, Type type) {
        this.argTypes[i] = type;
    }

    public void setArgumentTypes(Type[] argTypes) {
        this.argTypes = argTypes;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setInstructionList(InstructionList il) {
        this.il = il;
    }

    public void setMaxLocals() {
        if (this.il != null) {
            int max;
            int n = max = this.isStatic() ? 0 : 1;
            if (this.argTypes != null) {
                for (Type argType : this.argTypes) {
                    max += argType.getSize();
                }
            }
            for (InstructionHandle ih = this.il.getStart(); ih != null; ih = ih.getNext()) {
                int index;
                Instruction ins = ih.getInstruction();
                if (!(ins instanceof LocalVariableInstruction) && !(ins instanceof RET) && !(ins instanceof IINC) || (index = ((IndexedInstruction)((Object)ins)).getIndex() + ((TypedInstruction)((Object)ins)).getType(super.getConstantPool()).getSize()) <= max) continue;
                max = index;
            }
            this.maxLocals = max;
        } else {
            this.maxLocals = 0;
        }
    }

    public void setMaxLocals(int m) {
        this.maxLocals = m;
    }

    public void setMaxStack() {
        this.maxStack = this.il != null ? MethodGen.getMaxStack(super.getConstantPool(), this.il, this.getExceptionHandlers()) : 0;
    }

    public void setMaxStack(int m) {
        this.maxStack = m;
    }

    public void setReturnType(Type returnType) {
        this.setType(returnType);
    }

    public void stripAttributes(boolean flag) {
        this.stripAttributes = flag;
    }

    public final String toString() {
        String access = Utility.accessToString(super.getAccessFlags());
        String signature = Type.getMethodSignature(super.getType(), this.argTypes);
        signature = Utility.methodSignatureToString(signature, super.getName(), access, true, this.getLocalVariableTable(super.getConstantPool()));
        StringBuilder buf = new StringBuilder(signature);
        for (Attribute a : this.getAttributes()) {
            if (a instanceof Code || a instanceof ExceptionTable) continue;
            buf.append(" [").append(a).append("]");
        }
        if (!this.throwsList.isEmpty()) {
            for (String throwsDescriptor : this.throwsList) {
                buf.append("\n\t\tthrows ").append(throwsDescriptor);
            }
        }
        return buf.toString();
    }

    public void update() {
        if (this.observers != null) {
            for (MethodObserver observer : this.observers) {
                observer.notify(this);
            }
        }
    }

    private void updateLocalVariableTable(LocalVariableTable a) {
        this.removeLocalVariables();
        for (LocalVariable l : a.getLocalVariableTable()) {
            InstructionHandle start = this.il.findHandle(l.getStartPC());
            InstructionHandle end = this.il.findHandle(l.getStartPC() + l.getLength());
            if (null == start) {
                start = this.il.getStart();
            }
            this.addLocalVariable(l.getName(), Type.getType(l.getSignature()), l.getIndex(), start, end, l.getOrigIndex());
        }
    }

    static final class BranchTarget {
        final InstructionHandle target;
        final int stackDepth;

        BranchTarget(InstructionHandle target, int stackDepth) {
            this.target = target;
            this.stackDepth = stackDepth;
        }
    }

    static final class BranchStack {
        private final Stack<BranchTarget> branchTargets = new Stack();
        private final Hashtable<InstructionHandle, BranchTarget> visitedTargets = new Hashtable();

        BranchStack() {
        }

        public BranchTarget pop() {
            if (!this.branchTargets.empty()) {
                return this.branchTargets.pop();
            }
            return null;
        }

        public void push(InstructionHandle target, int stackDepth) {
            if (this.visited(target)) {
                return;
            }
            this.branchTargets.push(this.visit(target, stackDepth));
        }

        private BranchTarget visit(InstructionHandle target, int stackDepth) {
            BranchTarget bt = new BranchTarget(target, stackDepth);
            this.visitedTargets.put(target, bt);
            return bt;
        }

        private boolean visited(InstructionHandle target) {
            return this.visitedTargets.get(target) != null;
        }
    }
}

