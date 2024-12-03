/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Method;
import org.aspectj.apache.bcel.classfile.Synthetic;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.generic.BranchHandle;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.CodeExceptionGen;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InstructionSelect;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.LineNumberTag;
import org.aspectj.apache.bcel.generic.LocalVariableTag;
import org.aspectj.apache.bcel.generic.MethodGen;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.Tag;
import org.aspectj.apache.bcel.generic.TargetLostException;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.MemberImpl;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelConstantPoolReader;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelShadow;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.ExceptionRange;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.Range;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.tools.Traceable;

public final class LazyMethodGen
implements Traceable {
    private static final AnnotationAJ[] NO_ANNOTATIONAJ = new AnnotationAJ[0];
    private int modifiers;
    private Type returnType;
    private final String name;
    private Type[] argumentTypes;
    private String[] declaredExceptions;
    private InstructionList body;
    private List<Attribute> attributes;
    private List<AnnotationAJ> newAnnotations;
    private List<ResolvedType> annotationsForRemoval;
    private AnnotationAJ[][] newParameterAnnotations;
    private final LazyClassGen enclosingClass;
    private BcelMethod memberView;
    private AjAttribute.EffectiveSignatureAttribute effectiveSignature;
    int highestLineNumber = 0;
    boolean wasPackedOptimally = false;
    private Method savedMethod = null;
    private final boolean originalMethodHasLocalVariableTable;
    String fromFilename = null;
    private int maxLocals;
    private boolean canInline = true;
    private boolean isSynthetic = false;
    List<BcelShadow> matchedShadows;
    public ResolvedType definingType = null;

    public LazyMethodGen(int modifiers, Type returnType, String name, Type[] paramTypes, String[] declaredExceptions, LazyClassGen enclosingClass) {
        this.memberView = null;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.name = name;
        this.argumentTypes = paramTypes;
        this.declaredExceptions = declaredExceptions;
        if (!Modifier.isAbstract(modifiers)) {
            this.body = new InstructionList();
            this.setMaxLocals(this.calculateMaxLocals());
        } else {
            this.body = null;
        }
        this.attributes = new ArrayList<Attribute>();
        this.enclosingClass = enclosingClass;
        this.assertGoodBody();
        this.originalMethodHasLocalVariableTable = true;
        if (this.memberView != null && this.isAdviceMethod() && enclosingClass.getType().isAnnotationStyleAspect()) {
            this.canInline = false;
        }
    }

    private int calculateMaxLocals() {
        int ret = Modifier.isStatic(this.modifiers) ? 0 : 1;
        for (Type type : this.argumentTypes) {
            ret += type.getSize();
        }
        return ret;
    }

    public LazyMethodGen(Method m, LazyClassGen enclosingClass) {
        this.savedMethod = m;
        this.enclosingClass = enclosingClass;
        if (!m.isAbstract() && !m.isNative() && m.getCode() == null) {
            throw new RuntimeException("bad non-abstract method with no code: " + m + " on " + enclosingClass);
        }
        if ((m.isAbstract() || m.isNative()) && m.getCode() != null) {
            throw new RuntimeException("bad abstract method with code: " + m + " on " + enclosingClass);
        }
        this.memberView = new BcelMethod(enclosingClass.getBcelObjectType(), m);
        this.originalMethodHasLocalVariableTable = this.savedMethod.getLocalVariableTable() != null;
        this.modifiers = m.getModifiers();
        this.name = m.getName();
        if (this.memberView != null && this.isAdviceMethod() && enclosingClass.getType().isAnnotationStyleAspect()) {
            this.canInline = false;
        }
    }

    private boolean isAbstractOrNative(int modifiers) {
        return Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers);
    }

    public LazyMethodGen(BcelMethod m, LazyClassGen enclosingClass) {
        this.savedMethod = m.getMethod();
        this.enclosingClass = enclosingClass;
        if (!this.isAbstractOrNative(m.getModifiers()) && this.savedMethod.getCode() == null) {
            throw new RuntimeException("bad non-abstract method with no code: " + m + " on " + enclosingClass);
        }
        if (this.isAbstractOrNative(m.getModifiers()) && this.savedMethod.getCode() != null) {
            throw new RuntimeException("bad abstract method with code: " + m + " on " + enclosingClass);
        }
        this.memberView = m;
        this.modifiers = this.savedMethod.getModifiers();
        this.name = m.getName();
        boolean bl = this.originalMethodHasLocalVariableTable = this.savedMethod.getLocalVariableTable() != null;
        if (this.memberView != null && this.isAdviceMethod() && enclosingClass.getType().isAnnotationStyleAspect()) {
            this.canInline = false;
        }
    }

    public boolean hasDeclaredLineNumberInfo() {
        return this.memberView != null && this.memberView.hasDeclarationLineNumberInfo();
    }

    public int getDeclarationLineNumber() {
        if (this.hasDeclaredLineNumberInfo()) {
            return this.memberView.getDeclarationLineNumber();
        }
        return -1;
    }

    public int getDeclarationOffset() {
        if (this.hasDeclaredLineNumberInfo()) {
            return this.memberView.getDeclarationOffset();
        }
        return 0;
    }

    public void addAnnotation(AnnotationAJ ax) {
        this.initialize();
        if (this.memberView == null) {
            if (this.newAnnotations == null) {
                this.newAnnotations = new ArrayList<AnnotationAJ>();
            }
            this.newAnnotations.add(ax);
        } else {
            this.memberView.addAnnotation(ax);
        }
    }

    public void removeAnnotation(ResolvedType annotationType) {
        this.initialize();
        if (this.memberView == null) {
            if (this.annotationsForRemoval == null) {
                this.annotationsForRemoval = new ArrayList<ResolvedType>();
            }
            this.annotationsForRemoval.add(annotationType);
        } else {
            this.memberView.removeAnnotation(annotationType);
        }
    }

    public void addParameterAnnotation(int parameterNumber, AnnotationAJ anno) {
        this.initialize();
        if (this.memberView == null) {
            if (this.newParameterAnnotations == null) {
                int pcount = this.getArgumentTypes().length;
                this.newParameterAnnotations = new AnnotationAJ[pcount][];
                for (int i = 0; i < pcount; ++i) {
                    if (i == parameterNumber) {
                        this.newParameterAnnotations[i] = new AnnotationAJ[1];
                        this.newParameterAnnotations[i][0] = anno;
                        continue;
                    }
                    this.newParameterAnnotations[i] = NO_ANNOTATIONAJ;
                }
            } else {
                AnnotationAJ[] currentAnnoArray = this.newParameterAnnotations[parameterNumber];
                AnnotationAJ[] newAnnoArray = new AnnotationAJ[currentAnnoArray.length + 1];
                System.arraycopy(currentAnnoArray, 0, newAnnoArray, 0, currentAnnoArray.length);
                newAnnoArray[currentAnnoArray.length] = anno;
                this.newParameterAnnotations[parameterNumber] = newAnnoArray;
            }
        } else {
            this.memberView.addParameterAnnotation(parameterNumber, anno);
        }
    }

    public ResolvedType[] getAnnotationTypes() {
        this.initialize();
        if (this.memberView == null && this.newAnnotations != null && this.newAnnotations.size() != 0) {
            ResolvedType[] annotationTypes = new ResolvedType[this.newAnnotations.size()];
            int len = this.newAnnotations.size();
            for (int a = 0; a < len; ++a) {
                annotationTypes[a] = this.newAnnotations.get(a).getType();
            }
            return annotationTypes;
        }
        return null;
    }

    public AnnotationAJ[] getAnnotations() {
        this.initialize();
        if (this.memberView == null && this.newAnnotations != null && this.newAnnotations.size() != 0) {
            return this.newAnnotations.toArray(new AnnotationAJ[this.newAnnotations.size()]);
        }
        return null;
    }

    public boolean hasAnnotation(UnresolvedType annotationType) {
        this.initialize();
        if (this.memberView == null) {
            if (this.annotationsForRemoval != null) {
                for (ResolvedType at : this.annotationsForRemoval) {
                    if (!at.equals(annotationType)) continue;
                    return false;
                }
            }
            if (this.newAnnotations != null) {
                for (AnnotationAJ annotation : this.newAnnotations) {
                    if (!annotation.getTypeSignature().equals(annotationType.getSignature())) continue;
                    return true;
                }
            }
            this.memberView = new BcelMethod(this.getEnclosingClass().getBcelObjectType(), this.getMethod());
            return this.memberView.hasAnnotation(annotationType);
        }
        return this.memberView.hasAnnotation(annotationType);
    }

    private void initialize() {
        if (this.returnType != null) {
            return;
        }
        MethodGen gen = new MethodGen(this.savedMethod, this.enclosingClass.getName(), this.enclosingClass.getConstantPool(), true);
        this.returnType = gen.getReturnType();
        this.argumentTypes = gen.getArgumentTypes();
        this.declaredExceptions = gen.getExceptions();
        this.attributes = gen.getAttributes();
        this.maxLocals = gen.getMaxLocals();
        if (gen.isAbstract() || gen.isNative()) {
            this.body = null;
        } else {
            this.body = gen.getInstructionList();
            this.unpackHandlers(gen);
            this.ensureAllLineNumberSetup();
            this.highestLineNumber = gen.getHighestlinenumber();
        }
        this.assertGoodBody();
    }

    private void unpackHandlers(MethodGen gen) {
        CodeExceptionGen[] exns = gen.getExceptionHandlers();
        if (exns != null) {
            int len = exns.length;
            int priority = len - 1;
            int i = 0;
            while (i < len) {
                CodeExceptionGen exn = exns[i];
                InstructionHandle start = Range.genStart(this.body, this.getOutermostExceptionStart(exn.getStartPC()));
                InstructionHandle end = Range.genEnd(this.body, this.getOutermostExceptionEnd(exn.getEndPC()));
                ExceptionRange er = new ExceptionRange(this.body, exn.getCatchType() == null ? null : BcelWorld.fromBcel(exn.getCatchType()), priority);
                er.associateWithTargets(start, end, exn.getHandlerPC());
                exn.setStartPC(null);
                exn.setEndPC(null);
                exn.setHandlerPC(null);
                ++i;
                --priority;
            }
            gen.removeExceptionHandlers();
        }
    }

    private InstructionHandle getOutermostExceptionStart(InstructionHandle ih) {
        while (ExceptionRange.isExceptionStart(ih.getPrev())) {
            ih = ih.getPrev();
        }
        return ih;
    }

    private InstructionHandle getOutermostExceptionEnd(InstructionHandle ih) {
        while (ExceptionRange.isExceptionEnd(ih.getNext())) {
            ih = ih.getNext();
        }
        return ih;
    }

    public void ensureAllLineNumberSetup() {
        LineNumberTag lastKnownLineNumberTag = null;
        boolean skip = false;
        for (InstructionHandle ih = this.body.getStart(); ih != null; ih = ih.getNext()) {
            skip = false;
            for (InstructionTargeter targeter : ih.getTargeters()) {
                if (!(targeter instanceof LineNumberTag)) continue;
                lastKnownLineNumberTag = (LineNumberTag)targeter;
                skip = true;
            }
            if (lastKnownLineNumberTag == null || skip) continue;
            ih.addTargeter(lastKnownLineNumberTag);
        }
    }

    public int allocateLocal(Type type) {
        return this.allocateLocal(type.getSize());
    }

    public int allocateLocal(int slots) {
        int max = this.getMaxLocals();
        this.setMaxLocals(max + slots);
        return max;
    }

    public Method getMethod() {
        if (this.savedMethod != null) {
            return this.savedMethod;
        }
        try {
            MethodGen gen = this.pack();
            this.savedMethod = gen.getMethod();
            return this.savedMethod;
        }
        catch (ClassGenException e) {
            this.enclosingClass.getBcelObjectType().getResolvedTypeX().getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("problemGeneratingMethod", this.getClassName(), this.getName(), e.getMessage()), this.getMemberView() == null ? null : this.getMemberView().getSourceLocation(), null);
            this.body = null;
            MethodGen gen = this.pack();
            return gen.getMethod();
        }
        catch (RuntimeException re) {
            if (re.getCause() instanceof ClassGenException) {
                this.enclosingClass.getBcelObjectType().getResolvedTypeX().getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("problemGeneratingMethod", this.getClassName(), this.getName(), re.getCause().getMessage()), this.getMemberView() == null ? null : this.getMemberView().getSourceLocation(), null);
                this.body = null;
                MethodGen gen = this.pack();
                return gen.getMethod();
            }
            throw re;
        }
    }

    public void markAsChanged() {
        if (this.wasPackedOptimally) {
            throw new RuntimeException("Already packed method is being re-modified: " + this.getClassName() + " " + this.toShortString());
        }
        this.initialize();
        this.savedMethod = null;
    }

    public String toString() {
        BcelObjectType bot = this.enclosingClass.getBcelObjectType();
        AjAttribute.WeaverVersionInfo weaverVersion = bot == null ? AjAttribute.WeaverVersionInfo.CURRENT : bot.getWeaverVersionAttribute();
        return this.toLongString(weaverVersion);
    }

    public String toShortString() {
        int i;
        String access = org.aspectj.apache.bcel.classfile.Utility.accessToString(this.getAccessFlags());
        StringBuffer buf = new StringBuffer();
        if (!access.equals("")) {
            buf.append(access);
            buf.append(" ");
        }
        buf.append(org.aspectj.apache.bcel.classfile.Utility.signatureToString(this.getReturnType().getSignature(), true));
        buf.append(" ");
        buf.append(this.getName());
        buf.append("(");
        int len = this.argumentTypes.length;
        if (len > 0) {
            buf.append(org.aspectj.apache.bcel.classfile.Utility.signatureToString(this.argumentTypes[0].getSignature(), true));
            for (i = 1; i < this.argumentTypes.length; ++i) {
                buf.append(", ");
                buf.append(org.aspectj.apache.bcel.classfile.Utility.signatureToString(this.argumentTypes[i].getSignature(), true));
            }
        }
        buf.append(")");
        int n = len = this.declaredExceptions != null ? this.declaredExceptions.length : 0;
        if (len > 0) {
            buf.append(" throws ");
            buf.append(this.declaredExceptions[0]);
            for (i = 1; i < this.declaredExceptions.length; ++i) {
                buf.append(", ");
                buf.append(this.declaredExceptions[i]);
            }
        }
        return buf.toString();
    }

    public String toLongString(AjAttribute.WeaverVersionInfo weaverVersion) {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        this.print(new PrintStream(s), weaverVersion);
        return new String(s.toByteArray());
    }

    public void print(AjAttribute.WeaverVersionInfo weaverVersion) {
        this.print(System.out, weaverVersion);
    }

    public void print(PrintStream out, AjAttribute.WeaverVersionInfo weaverVersion) {
        out.print("  " + this.toShortString());
        this.printAspectAttributes(out, weaverVersion);
        InstructionList body = this.getBody();
        if (body == null) {
            out.println(";");
            return;
        }
        out.println(":");
        new BodyPrinter(out).run();
        out.println("  end " + this.toShortString());
    }

    private void printAspectAttributes(PrintStream out, AjAttribute.WeaverVersionInfo weaverVersion) {
        List<AjAttribute> as;
        ISourceContext context = null;
        if (this.enclosingClass != null && this.enclosingClass.getType() != null) {
            context = this.enclosingClass.getType().getSourceContext();
        }
        if (!(as = Utility.readAjAttributes(this.getClassName(), this.attributes.toArray(new Attribute[0]), context, null, weaverVersion, new BcelConstantPoolReader(this.enclosingClass.getConstantPool()))).isEmpty()) {
            out.println("    " + as.get(0));
        }
    }

    static LocalVariableTag getLocalVariableTag(InstructionHandle ih, int index) {
        for (InstructionTargeter t : ih.getTargeters()) {
            LocalVariableTag lvt;
            if (!(t instanceof LocalVariableTag) || (lvt = (LocalVariableTag)t).getSlot() != index) continue;
            return lvt;
        }
        return null;
    }

    static int getLineNumber(InstructionHandle ih, int prevLine) {
        for (InstructionTargeter t : ih.getTargeters()) {
            if (!(t instanceof LineNumberTag)) continue;
            return ((LineNumberTag)t).getLineNumber();
        }
        return prevLine;
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.getAccessFlags());
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.getAccessFlags());
    }

    public boolean isBridgeMethod() {
        return (this.getAccessFlags() & 0x40) != 0;
    }

    public void addExceptionHandler(InstructionHandle start, InstructionHandle end, InstructionHandle handlerStart, ObjectType catchType, boolean highPriority) {
        InstructionHandle start1 = Range.genStart(this.body, start);
        InstructionHandle end1 = Range.genEnd(this.body, end);
        ExceptionRange er = new ExceptionRange(this.body, catchType == null ? null : BcelWorld.fromBcel(catchType), highPriority);
        er.associateWithTargets(start1, end1, handlerStart);
    }

    public int getAccessFlags() {
        return this.modifiers;
    }

    public int getAccessFlagsWithoutSynchronized() {
        if (this.isSynchronized()) {
            return this.modifiers - 32;
        }
        return this.modifiers;
    }

    public boolean isSynchronized() {
        return (this.modifiers & 0x20) != 0;
    }

    public void setAccessFlags(int newFlags) {
        this.modifiers = newFlags;
    }

    public Type[] getArgumentTypes() {
        this.initialize();
        return this.argumentTypes;
    }

    public LazyClassGen getEnclosingClass() {
        return this.enclosingClass;
    }

    public int getMaxLocals() {
        return this.maxLocals;
    }

    public String getName() {
        return this.name;
    }

    public String getGenericReturnTypeSignature() {
        if (this.memberView == null) {
            return this.getReturnType().getSignature();
        }
        return this.memberView.getGenericReturnType().getSignature();
    }

    public Type getReturnType() {
        this.initialize();
        return this.returnType;
    }

    public void setMaxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
    }

    public InstructionList getBody() {
        this.markAsChanged();
        return this.body;
    }

    public InstructionList getBodyForPrint() {
        return this.body;
    }

    public boolean hasBody() {
        if (this.savedMethod != null) {
            return this.savedMethod.getCode() != null;
        }
        return this.body != null;
    }

    public List<Attribute> getAttributes() {
        return this.attributes;
    }

    public String[] getDeclaredExceptions() {
        return this.declaredExceptions;
    }

    public String getClassName() {
        return this.enclosingClass.getName();
    }

    public MethodGen pack() {
        this.forceSyntheticForAjcMagicMembers();
        int flags = this.getAccessFlags();
        if (this.enclosingClass.getWorld().isJoinpointSynchronizationEnabled() && this.enclosingClass.getWorld().areSynchronizationPointcutsInUse()) {
            flags = this.getAccessFlagsWithoutSynchronized();
        }
        MethodGen gen = new MethodGen(flags, this.getReturnType(), this.getArgumentTypes(), null, this.getName(), this.getEnclosingClass().getName(), new InstructionList(), this.getEnclosingClass().getConstantPool());
        int len = this.declaredExceptions.length;
        for (int i = 0; i < len; ++i) {
            gen.addException(this.declaredExceptions[i]);
        }
        for (Attribute attr : this.attributes) {
            gen.addAttribute(attr);
        }
        if (this.newAnnotations != null) {
            for (AnnotationAJ element : this.newAnnotations) {
                gen.addAnnotation(new AnnotationGen(((BcelAnnotation)element).getBcelAnnotation(), gen.getConstantPool(), true));
            }
        }
        if (this.newParameterAnnotations != null) {
            for (int i = 0; i < this.newParameterAnnotations.length; ++i) {
                AnnotationAJ[] annos = this.newParameterAnnotations[i];
                for (int j = 0; j < annos.length; ++j) {
                    gen.addParameterAnnotation(i, new AnnotationGen(((BcelAnnotation)annos[j]).getBcelAnnotation(), gen.getConstantPool(), true));
                }
            }
        }
        if (this.memberView != null && this.memberView.getAnnotations() != null && this.memberView.getAnnotations().length != 0) {
            AnnotationAJ[] ans = this.memberView.getAnnotations();
            int len2 = ans.length;
            for (int i = 0; i < len2; ++i) {
                AnnotationGen a = ((BcelAnnotation)ans[i]).getBcelAnnotation();
                gen.addAnnotation(new AnnotationGen(a, gen.getConstantPool(), true));
            }
        }
        if (this.isSynthetic) {
            if (this.enclosingClass.getWorld().isInJava5Mode()) {
                gen.setModifiers(gen.getModifiers() | 0x1000);
            }
            if (!this.hasAttribute("Synthetic")) {
                ConstantPool cpg = gen.getConstantPool();
                int index = cpg.addUtf8("Synthetic");
                gen.addAttribute(new Synthetic(index, 0, new byte[0], cpg));
            }
        }
        if (this.hasBody()) {
            if (this.enclosingClass.getWorld().shouldFastPackMethods()) {
                if (this.isAdviceMethod() || this.getName().equals("<clinit>")) {
                    this.packBody(gen);
                } else {
                    this.optimizedPackBody(gen);
                }
            } else {
                this.packBody(gen);
            }
            gen.setMaxLocals(true);
            gen.setMaxStack();
        } else {
            gen.setInstructionList(null);
        }
        return gen;
    }

    private boolean hasAttribute(String attributeName) {
        for (Attribute attr : this.attributes) {
            if (!attr.getName().equals(attributeName)) continue;
            return true;
        }
        return false;
    }

    private void forceSyntheticForAjcMagicMembers() {
        if (NameMangler.isSyntheticMethod(this.getName(), this.inAspect())) {
            this.makeSynthetic();
        }
    }

    private boolean inAspect() {
        BcelObjectType objectType = this.enclosingClass.getBcelObjectType();
        return objectType == null ? false : objectType.isAspect();
    }

    public void makeSynthetic() {
        this.isSynthetic = true;
    }

    public void packBody(MethodGen gen) {
        int lineNumberOffset;
        InstructionList fresh = gen.getInstructionList();
        Map<InstructionHandle, InstructionHandle> map = this.copyAllInstructionsExceptRangeInstructionsInto(fresh);
        InstructionHandle oldInstructionHandle = this.getBody().getStart();
        InstructionHandle newInstructionHandle = fresh.getStart();
        LinkedList<ExceptionRange> exceptionList = new LinkedList<ExceptionRange>();
        HashMap<LocalVariableTag, LVPosition> localVariables = new HashMap<LocalVariableTag, LVPosition>();
        int currLine = -1;
        int n = lineNumberOffset = this.fromFilename == null ? 0 : this.getEnclosingClass().getSourceDebugExtensionOffset(this.fromFilename);
        while (oldInstructionHandle != null) {
            if (map.get(oldInstructionHandle) == null) {
                this.handleRangeInstruction(oldInstructionHandle, exceptionList);
                oldInstructionHandle = oldInstructionHandle.getNext();
                continue;
            }
            Instruction oldInstruction = oldInstructionHandle.getInstruction();
            Instruction newInstruction = newInstructionHandle.getInstruction();
            if (oldInstruction instanceof InstructionBranch) {
                this.handleBranchInstruction(map, oldInstruction, newInstruction);
            }
            for (InstructionTargeter targeter : oldInstructionHandle.getTargeters()) {
                if (targeter instanceof LineNumberTag) {
                    int line = ((LineNumberTag)targeter).getLineNumber();
                    if (line == currLine) continue;
                    gen.addLineNumber(newInstructionHandle, line + lineNumberOffset);
                    currLine = line;
                    continue;
                }
                if (!(targeter instanceof LocalVariableTag)) continue;
                LocalVariableTag lvt = (LocalVariableTag)targeter;
                LVPosition p = (LVPosition)localVariables.get(lvt);
                if (p == null) {
                    LVPosition newp = new LVPosition();
                    newp.start = newp.end = newInstructionHandle;
                    localVariables.put(lvt, newp);
                    continue;
                }
                p.end = newInstructionHandle;
            }
            oldInstructionHandle = oldInstructionHandle.getNext();
            newInstructionHandle = newInstructionHandle.getNext();
        }
        this.addExceptionHandlers(gen, map, exceptionList);
        if (this.originalMethodHasLocalVariableTable || this.enclosingClass.getBcelObjectType().getResolvedTypeX().getWorld().generateNewLvts) {
            if (localVariables.size() == 0) {
                this.createNewLocalVariables(gen);
            } else {
                this.addLocalVariables(gen, localVariables);
            }
        }
        if (gen.getLineNumbers().length == 0) {
            gen.addLineNumber(gen.getInstructionList().getStart(), 1);
        }
    }

    private void createNewLocalVariables(MethodGen gen) {
        gen.removeLocalVariables();
        if (!this.getName().startsWith("<")) {
            String[] paramNames;
            int slot = 0;
            InstructionHandle start = gen.getInstructionList().getStart();
            InstructionHandle end = gen.getInstructionList().getEnd();
            if (!this.isStatic()) {
                String cname = this.enclosingClass.getClassName();
                if (cname == null) {
                    return;
                }
                Type enclosingType = BcelWorld.makeBcelType(UnresolvedType.forName(cname));
                gen.addLocalVariable("this", enclosingType, slot++, start, end);
            }
            String[] stringArray = paramNames = this.memberView == null ? null : this.memberView.getParameterNames();
            if (paramNames != null) {
                for (int i = 0; i < this.argumentTypes.length; ++i) {
                    String pname = paramNames[i];
                    if (pname == null) {
                        pname = "arg" + i;
                    }
                    gen.addLocalVariable(pname, this.argumentTypes[i], slot, start, end);
                    slot += this.argumentTypes[i].getSize();
                }
            }
        }
    }

    private World getWorld() {
        return this.enclosingClass.getBcelObjectType().getResolvedTypeX().getWorld();
    }

    public void optimizedPackBody(MethodGen gen) {
        InstructionList theBody = this.getBody();
        int currLine = -1;
        int lineNumberOffset = this.fromFilename == null ? 0 : this.getEnclosingClass().getSourceDebugExtensionOffset(this.fromFilename);
        HashMap<LocalVariableTag, LVPosition> localVariables = new HashMap<LocalVariableTag, LVPosition>();
        LinkedList<ExceptionRange> exceptionList = new LinkedList<ExceptionRange>();
        HashSet<InstructionHandle> forDeletion = new HashSet<InstructionHandle>();
        HashSet<BranchHandle> branchInstructions = new HashSet<BranchHandle>();
        for (InstructionHandle iHandle = theBody.getStart(); iHandle != null; iHandle = iHandle.getNext()) {
            Instruction inst = iHandle.getInstruction();
            if (inst == Range.RANGEINSTRUCTION) {
                ExceptionRange er;
                Range range = Range.getRange(iHandle);
                if (range instanceof ExceptionRange && (er = (ExceptionRange)range).getStart() == iHandle && !er.isEmpty()) {
                    LazyMethodGen.insertHandler(er, exceptionList);
                }
                forDeletion.add(iHandle);
                continue;
            }
            if (inst instanceof InstructionBranch) {
                branchInstructions.add((BranchHandle)iHandle);
            }
            for (InstructionTargeter targeter : iHandle.getTargetersCopy()) {
                if (targeter instanceof LineNumberTag) {
                    int line = ((LineNumberTag)targeter).getLineNumber();
                    if (line == currLine) continue;
                    gen.addLineNumber(iHandle, line + lineNumberOffset);
                    currLine = line;
                    continue;
                }
                if (!(targeter instanceof LocalVariableTag)) continue;
                LocalVariableTag lvt = (LocalVariableTag)targeter;
                LVPosition p = (LVPosition)localVariables.get(lvt);
                if (p == null) {
                    LVPosition newp = new LVPosition();
                    newp.start = newp.end = iHandle;
                    localVariables.put(lvt, newp);
                    continue;
                }
                p.end = iHandle;
            }
        }
        for (BranchHandle branchHandle : branchInstructions) {
            this.handleBranchInstruction(branchHandle, forDeletion);
        }
        for (ExceptionRange exceptionRange : exceptionList) {
            if (exceptionRange.isEmpty()) continue;
            gen.addExceptionHandler(this.jumpForward(exceptionRange.getRealStart(), forDeletion), this.jumpForward(exceptionRange.getRealEnd(), forDeletion), this.jumpForward(exceptionRange.getHandler(), forDeletion), exceptionRange.getCatchType() == null ? null : (ObjectType)BcelWorld.makeBcelType(exceptionRange.getCatchType()));
        }
        for (InstructionHandle instructionHandle : forDeletion) {
            try {
                theBody.delete(instructionHandle);
            }
            catch (TargetLostException e) {
                e.printStackTrace();
            }
        }
        gen.setInstructionList(theBody);
        if (this.originalMethodHasLocalVariableTable || this.getWorld().generateNewLvts) {
            if (localVariables.size() == 0) {
                this.createNewLocalVariables(gen);
            } else {
                this.addLocalVariables(gen, localVariables);
            }
        }
        if (gen.getLineNumbers().length == 0) {
            gen.addLineNumber(gen.getInstructionList().getStart(), 1);
        }
        this.wasPackedOptimally = true;
    }

    private void addLocalVariables(MethodGen gen, Map<LocalVariableTag, LVPosition> localVariables) {
        gen.removeLocalVariables();
        InstructionHandle methodStart = gen.getInstructionList().getStart();
        InstructionHandle methodEnd = gen.getInstructionList().getEnd();
        int paramSlots = gen.isStatic() ? 0 : 1;
        Type[] argTypes = gen.getArgumentTypes();
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; ++i) {
                if (argTypes[i].getSize() == 2) {
                    paramSlots += 2;
                    continue;
                }
                ++paramSlots;
            }
        }
        if (!this.enclosingClass.getWorld().generateNewLvts) {
            paramSlots = -1;
        }
        HashMap duplicatedLocalMap = new HashMap();
        for (LocalVariableTag tag : localVariables.keySet()) {
            LVPosition lvpos = localVariables.get(tag);
            InstructionHandle start = tag.getSlot() < paramSlots ? methodStart : lvpos.start;
            InstructionHandle end = tag.getSlot() < paramSlots ? methodEnd : lvpos.end;
            HashSet<Integer> slots = (HashSet<Integer>)duplicatedLocalMap.get(start);
            if (slots == null) {
                slots = new HashSet<Integer>();
                duplicatedLocalMap.put(start, slots);
            } else if (slots.contains(new Integer(tag.getSlot()))) continue;
            slots.add(tag.getSlot());
            Type t = tag.getRealType();
            if (t == null) {
                t = BcelWorld.makeBcelType(UnresolvedType.forSignature(tag.getType()));
            }
            gen.addLocalVariable(tag.getName(), t, tag.getSlot(), start, end);
        }
    }

    private void addExceptionHandlers(MethodGen gen, Map<InstructionHandle, InstructionHandle> map, LinkedList<ExceptionRange> exnList) {
        for (ExceptionRange r : exnList) {
            if (r.isEmpty()) continue;
            InstructionHandle rMappedStart = LazyMethodGen.remap(r.getRealStart(), map);
            InstructionHandle rMappedEnd = LazyMethodGen.remap(r.getRealEnd(), map);
            InstructionHandle rMappedHandler = LazyMethodGen.remap(r.getHandler(), map);
            gen.addExceptionHandler(rMappedStart, rMappedEnd, rMappedHandler, r.getCatchType() == null ? null : (ObjectType)BcelWorld.makeBcelType(r.getCatchType()));
        }
    }

    private void handleBranchInstruction(Map<InstructionHandle, InstructionHandle> map, Instruction oldInstruction, Instruction newInstruction) {
        InstructionBranch oldBranchInstruction = (InstructionBranch)oldInstruction;
        InstructionBranch newBranchInstruction = (InstructionBranch)newInstruction;
        InstructionHandle oldTarget = oldBranchInstruction.getTarget();
        newBranchInstruction.setTarget(LazyMethodGen.remap(oldTarget, map));
        if (oldBranchInstruction instanceof InstructionSelect) {
            InstructionHandle[] oldTargets = ((InstructionSelect)oldBranchInstruction).getTargets();
            InstructionHandle[] newTargets = ((InstructionSelect)newBranchInstruction).getTargets();
            for (int k = oldTargets.length - 1; k >= 0; --k) {
                newTargets[k] = LazyMethodGen.remap(oldTargets[k], map);
                newTargets[k].addTargeter(newBranchInstruction);
            }
        }
    }

    private InstructionHandle jumpForward(InstructionHandle t, Set<InstructionHandle> handlesForDeletion) {
        InstructionHandle target = t;
        if (handlesForDeletion.contains(target)) {
            while (handlesForDeletion.contains(target = target.getNext())) {
            }
        }
        return target;
    }

    private void handleBranchInstruction(BranchHandle branchHandle, Set<InstructionHandle> handlesForDeletion) {
        InstructionBranch branchInstruction = (InstructionBranch)branchHandle.getInstruction();
        InstructionHandle target = branchInstruction.getTarget();
        if (handlesForDeletion.contains(target)) {
            while (handlesForDeletion.contains(target = target.getNext())) {
            }
            branchInstruction.setTarget(target);
        }
        if (branchInstruction instanceof InstructionSelect) {
            InstructionSelect iSelect = (InstructionSelect)branchInstruction;
            InstructionHandle[] targets = iSelect.getTargets();
            for (int k = targets.length - 1; k >= 0; --k) {
                InstructionHandle oneTarget = targets[k];
                if (!handlesForDeletion.contains(oneTarget)) continue;
                while (handlesForDeletion.contains(oneTarget = oneTarget.getNext())) {
                }
                iSelect.setTarget(k, oneTarget);
                oneTarget.addTargeter(branchInstruction);
            }
        }
    }

    private void handleRangeInstruction(InstructionHandle ih, LinkedList<ExceptionRange> exnList) {
        ExceptionRange er;
        Range r = Range.getRange(ih);
        if (r instanceof ExceptionRange && (er = (ExceptionRange)r).getStart() == ih && !er.isEmpty()) {
            LazyMethodGen.insertHandler(er, exnList);
        }
    }

    private Map<InstructionHandle, InstructionHandle> copyAllInstructionsExceptRangeInstructionsInto(InstructionList intoList) {
        HashMap<InstructionHandle, InstructionHandle> map = new HashMap<InstructionHandle, InstructionHandle>();
        for (InstructionHandle ih = this.getBody().getStart(); ih != null; ih = ih.getNext()) {
            if (Range.isRangeHandle(ih)) continue;
            Instruction inst = ih.getInstruction();
            Instruction copy = Utility.copyInstruction(inst);
            if (copy instanceof InstructionBranch) {
                map.put(ih, intoList.append((InstructionBranch)copy));
                continue;
            }
            map.put(ih, intoList.append(copy));
        }
        return map;
    }

    private static InstructionHandle remap(InstructionHandle handle, Map<InstructionHandle, InstructionHandle> map) {
        InstructionHandle ret;
        while ((ret = map.get(handle)) == null) {
            handle = handle.getNext();
        }
        return ret;
    }

    static void insertHandler(ExceptionRange fresh, LinkedList<ExceptionRange> l) {
        ListIterator<ExceptionRange> iter = l.listIterator();
        while (iter.hasNext()) {
            ExceptionRange r = (ExceptionRange)iter.next();
            if (fresh.getPriority() < r.getPriority()) continue;
            iter.previous();
            iter.add(fresh);
            return;
        }
        l.add(fresh);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(this.getAccessFlags());
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.getAccessFlags());
    }

    public boolean isDefault() {
        return !this.isProtected() && !this.isPrivate() && !this.isPublic();
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.getAccessFlags());
    }

    public void assertGoodBody() {
    }

    public static void assertGoodBody(InstructionList il, String from) {
    }

    private static void assertTargetedBy(InstructionHandle target, InstructionTargeter targeter, String from) {
        Iterator<InstructionTargeter> tIter = target.getTargeters().iterator();
        while (tIter.hasNext()) {
            if (tIter.next() != targeter) continue;
            return;
        }
        throw new RuntimeException("bad targeting relationship in " + from);
    }

    private static void assertTargets(InstructionTargeter targeter, InstructionHandle target, String from) {
        if (targeter instanceof Range) {
            Range r = (Range)targeter;
            if (r.getStart() == target || r.getEnd() == target) {
                return;
            }
            if (r instanceof ExceptionRange && ((ExceptionRange)r).getHandler() == target) {
                return;
            }
        } else if (targeter instanceof InstructionBranch) {
            InstructionBranch bi = (InstructionBranch)targeter;
            if (bi.getTarget() == target) {
                return;
            }
            if (targeter instanceof InstructionSelect) {
                InstructionSelect sel = (InstructionSelect)targeter;
                InstructionHandle[] itargets = sel.getTargets();
                for (int k = itargets.length - 1; k >= 0; --k) {
                    if (itargets[k] != target) continue;
                    return;
                }
            }
        } else if (targeter instanceof Tag) {
            return;
        }
        throw new BCException(targeter + " doesn't target " + target + " in " + from);
    }

    private static Range getRangeAndAssertExactlyOne(InstructionHandle ih, String from) {
        Range ret = null;
        Iterator<InstructionTargeter> tIter = ih.getTargeters().iterator();
        if (!tIter.hasNext()) {
            throw new BCException("range handle with no range in " + from);
        }
        while (tIter.hasNext()) {
            InstructionTargeter ts = tIter.next();
            if (!(ts instanceof Range)) continue;
            if (ret != null) {
                throw new BCException("range handle with multiple ranges in " + from);
            }
            ret = (Range)ts;
        }
        if (ret == null) {
            throw new BCException("range handle with no range in " + from);
        }
        return ret;
    }

    boolean isAdviceMethod() {
        if (this.memberView == null) {
            return false;
        }
        return this.memberView.getAssociatedShadowMunger() != null;
    }

    boolean isAjSynthetic() {
        if (this.memberView == null) {
            return true;
        }
        return this.memberView.isAjSynthetic();
    }

    boolean isSynthetic() {
        if (this.memberView == null) {
            return false;
        }
        return this.memberView.isSynthetic();
    }

    public ISourceLocation getSourceLocation() {
        if (this.memberView != null) {
            return this.memberView.getSourceLocation();
        }
        return null;
    }

    public AjAttribute.EffectiveSignatureAttribute getEffectiveSignature() {
        if (this.effectiveSignature != null) {
            return this.effectiveSignature;
        }
        return this.memberView.getEffectiveSignature();
    }

    public void setEffectiveSignature(ResolvedMember member, Shadow.Kind kind, boolean shouldWeave) {
        this.effectiveSignature = new AjAttribute.EffectiveSignatureAttribute(member, kind, shouldWeave);
    }

    public String getSignature() {
        if (this.memberView != null) {
            return this.memberView.getSignature();
        }
        return MemberImpl.typesToSignature(BcelWorld.fromBcel(this.getReturnType()), BcelWorld.fromBcel(this.getArgumentTypes()), false);
    }

    public String getParameterSignature() {
        if (this.memberView != null) {
            return this.memberView.getParameterSignature();
        }
        return MemberImpl.typesToSignature(BcelWorld.fromBcel(this.getArgumentTypes()));
    }

    public BcelMethod getMemberView() {
        return this.memberView;
    }

    public void forcePublic() {
        this.markAsChanged();
        this.modifiers = Utility.makePublic(this.modifiers);
    }

    public boolean getCanInline() {
        return this.canInline;
    }

    public void setCanInline(boolean canInline) {
        this.canInline = canInline;
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    @Override
    public String toTraceString() {
        return this.toShortString();
    }

    public ConstantPool getConstantPool() {
        return this.enclosingClass.getConstantPool();
    }

    public static boolean isConstructor(LazyMethodGen aMethod) {
        return aMethod.getName().equals("<init>");
    }

    private static class LVPosition {
        InstructionHandle start = null;
        InstructionHandle end = null;

        private LVPosition() {
        }
    }

    private class BodyPrinter {
        Map<InstructionHandle, String> labelMap = new HashMap<InstructionHandle, String>();
        InstructionList body;
        PrintStream out;
        ConstantPool pool;
        static final int BODY_INDENT = 4;
        static final int CODE_INDENT = 16;

        BodyPrinter(PrintStream out) {
            this.pool = LazyMethodGen.this.enclosingClass.getConstantPool();
            this.body = LazyMethodGen.this.getBodyForPrint();
            this.out = out;
        }

        BodyPrinter(PrintStream out, InstructionList il) {
            this.pool = LazyMethodGen.this.enclosingClass.getConstantPool();
            this.body = il;
            this.out = out;
        }

        void run() {
            this.assignLabels();
            this.print();
        }

        void assignLabels() {
            LinkedList<ExceptionRange> exnTable = new LinkedList<ExceptionRange>();
            String pendingLabel = null;
            int lcounter = 0;
            for (InstructionHandle ih = this.body.getStart(); ih != null; ih = ih.getNext()) {
                for (InstructionTargeter t : ih.getTargeters()) {
                    if (t instanceof ExceptionRange) {
                        ExceptionRange r = (ExceptionRange)t;
                        if (r.getStart() != ih) continue;
                        LazyMethodGen.insertHandler(r, exnTable);
                        continue;
                    }
                    if (!(t instanceof InstructionBranch) || pendingLabel != null) continue;
                    pendingLabel = "L" + lcounter++;
                }
                if (pendingLabel == null) continue;
                this.labelMap.put(ih, pendingLabel);
                if (Range.isRangeHandle(ih)) continue;
                pendingLabel = null;
            }
            int ecounter = 0;
            for (ExceptionRange er : exnTable) {
                String exceptionLabel = "E" + ecounter++;
                this.labelMap.put(Range.getRealStart(er.getHandler()), exceptionLabel);
                this.labelMap.put(er.getHandler(), exceptionLabel);
            }
        }

        void print() {
            int depth = 0;
            int currLine = -1;
            block0: for (InstructionHandle ih = this.body.getStart(); ih != null; ih = ih.getNext()) {
                if (Range.isRangeHandle(ih)) {
                    Range r = Range.getRange(ih);
                    InstructionHandle xx = r.getStart();
                    while (Range.isRangeHandle(xx)) {
                        if (xx == r.getEnd()) continue block0;
                        xx = xx.getNext();
                    }
                    if (r.getStart() == ih) {
                        this.printRangeString(r, depth++);
                        continue;
                    }
                    if (r.getEnd() != ih) {
                        throw new RuntimeException("bad");
                    }
                    this.printRangeString(r, --depth);
                    continue;
                }
                this.printInstruction(ih, depth);
                int line = LazyMethodGen.getLineNumber(ih, currLine);
                if (line != currLine) {
                    currLine = line;
                    this.out.println("   (line " + line + ")");
                    continue;
                }
                this.out.println();
            }
        }

        void printRangeString(Range r, int depth) {
            this.printDepth(depth);
            this.out.println(this.getRangeString(r, this.labelMap));
        }

        String getRangeString(Range r, Map<InstructionHandle, String> labelMap) {
            if (r instanceof ExceptionRange) {
                ExceptionRange er = (ExceptionRange)r;
                return er.toString() + " -> " + labelMap.get(er.getHandler());
            }
            return r.toString();
        }

        void printDepth(int depth) {
            this.pad(4);
            while (depth > 0) {
                this.out.print("| ");
                --depth;
            }
        }

        void printLabel(String s, int depth) {
            int space = Math.max(16 - depth * 2, 0);
            if (s == null) {
                this.pad(space);
            } else {
                space = Math.max(space - (s.length() + 2), 0);
                this.pad(space);
                this.out.print(s);
                this.out.print(": ");
            }
        }

        void printInstruction(InstructionHandle h, int depth) {
            this.printDepth(depth);
            this.printLabel(this.labelMap.get(h), depth);
            Instruction inst = h.getInstruction();
            if (inst.isConstantPoolInstruction()) {
                this.out.print(Constants.OPCODE_NAMES[inst.opcode].toUpperCase());
                this.out.print(" ");
                this.out.print(this.pool.constantToString(this.pool.getConstant(inst.getIndex())));
            } else if (inst instanceof InstructionSelect) {
                InstructionSelect sinst = (InstructionSelect)inst;
                this.out.println(Constants.OPCODE_NAMES[sinst.opcode].toUpperCase());
                int[] matches = sinst.getMatchs();
                InstructionHandle[] targets = sinst.getTargets();
                InstructionHandle defaultTarget = sinst.getTarget();
                int len = matches.length;
                for (int i = 0; i < len; ++i) {
                    this.printDepth(depth);
                    this.printLabel(null, depth);
                    this.out.print("  ");
                    this.out.print(matches[i]);
                    this.out.print(": \t");
                    this.out.println(this.labelMap.get(targets[i]));
                }
                this.printDepth(depth);
                this.printLabel(null, depth);
                this.out.print("  ");
                this.out.print("default: \t");
                this.out.print(this.labelMap.get(defaultTarget));
            } else if (inst instanceof InstructionBranch) {
                InstructionBranch brinst = (InstructionBranch)inst;
                this.out.print(Constants.OPCODE_NAMES[brinst.getOpcode()].toUpperCase());
                this.out.print(" ");
                this.out.print(this.labelMap.get(brinst.getTarget()));
            } else if (inst.isLocalVariableInstruction()) {
                this.out.print(inst.toString(false).toUpperCase());
                int index = inst.getIndex();
                LocalVariableTag tag = LazyMethodGen.getLocalVariableTag(h, index);
                if (tag != null) {
                    this.out.print("     // ");
                    this.out.print(tag.getType());
                    this.out.print(" ");
                    this.out.print(tag.getName());
                }
            } else {
                this.out.print(inst.toString(false).toUpperCase());
            }
        }

        void pad(int size) {
            for (int i = 0; i < size; ++i) {
                this.out.print(" ");
            }
        }
    }

    static class LightweightBcelMethod
    extends BcelMethod {
        LightweightBcelMethod(BcelObjectType declaringType, Method method) {
            super(declaringType, method);
        }
    }
}

