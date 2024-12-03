/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.util.List;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.ReferenceType;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.bcel.BcelClassWeaver;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.patterns.PerClause;

public class BcelPerClauseAspectAdder
extends BcelTypeMunger {
    private final PerClause.Kind kind;
    private boolean hasGeneratedInner = false;

    public BcelPerClauseAspectAdder(ResolvedType aspect, PerClause.Kind kind) {
        super(null, aspect);
        this.kind = kind;
        if (kind == PerClause.SINGLETON || kind == PerClause.PERTYPEWITHIN || kind == PerClause.PERCFLOW) {
            this.hasGeneratedInner = true;
        }
    }

    @Override
    public boolean munge(BcelClassWeaver weaver) {
        LazyClassGen gen = weaver.getLazyClassGen();
        this.doAggressiveInner(gen);
        if (!gen.getType().equals(this.aspectType)) {
            return false;
        }
        return this.doMunge(gen, true);
    }

    public boolean forceMunge(LazyClassGen gen, boolean checkAlreadyThere) {
        this.doAggressiveInner(gen);
        return this.doMunge(gen, checkAlreadyThere);
    }

    private void doAggressiveInner(LazyClassGen gen) {
        if (!this.hasGeneratedInner) {
            if (this.kind == PerClause.PEROBJECT) {
                UnresolvedType interfaceTypeX = AjcMemberMaker.perObjectInterfaceType(this.aspectType);
                LazyClassGen interfaceGen = new LazyClassGen(interfaceTypeX.getName(), "java.lang.Object", null, 1537, new String[0], this.getWorld());
                interfaceGen.addMethodGen(this.makeMethodGen(interfaceGen, AjcMemberMaker.perObjectInterfaceGet(this.aspectType)));
                interfaceGen.addMethodGen(this.makeMethodGen(interfaceGen, AjcMemberMaker.perObjectInterfaceSet(this.aspectType)));
                gen.addGeneratedInner(interfaceGen);
            }
            this.hasGeneratedInner = true;
        }
    }

    private boolean doMunge(LazyClassGen gen, boolean checkAlreadyThere) {
        if (checkAlreadyThere && this.hasPerClauseMembersAlready(gen)) {
            return false;
        }
        this.generatePerClauseMembers(gen);
        if (this.kind == PerClause.SINGLETON) {
            this.generatePerSingletonAspectOfMethod(gen);
            this.generatePerSingletonHasAspectMethod(gen);
            this.generatePerSingletonAjcClinitMethod(gen);
        } else if (this.kind == PerClause.PEROBJECT) {
            this.generatePerObjectAspectOfMethod(gen);
            this.generatePerObjectHasAspectMethod(gen);
            this.generatePerObjectBindMethod(gen);
        } else if (this.kind == PerClause.PERCFLOW) {
            this.generatePerCflowAspectOfMethod(gen);
            this.generatePerCflowHasAspectMethod(gen);
            this.generatePerCflowPushMethod(gen);
            this.generatePerCflowAjcClinitMethod(gen);
        } else if (this.kind == PerClause.PERTYPEWITHIN) {
            this.generatePerTWAspectOfMethod(gen);
            this.generatePerTWHasAspectMethod(gen);
            this.generatePerTWGetInstanceMethod(gen);
            this.generatePerTWCreateAspectInstanceMethod(gen);
            this.generatePerTWGetWithinTypeNameMethod(gen);
        } else {
            throw new Error("should not happen - not such kind " + this.kind.getName());
        }
        return true;
    }

    @Override
    public ResolvedMember getMatchingSyntheticMember(Member member) {
        return null;
    }

    @Override
    public ResolvedMember getSignature() {
        return null;
    }

    @Override
    public boolean matches(ResolvedType onType) {
        if (this.hasGeneratedInner) {
            return this.aspectType.equals(onType);
        }
        return true;
    }

    private boolean hasPerClauseMembersAlready(LazyClassGen classGen) {
        List<LazyMethodGen> methodGens = classGen.getMethodGens();
        for (LazyMethodGen method : methodGens) {
            if (!"aspectOf".equals(method.getName())) continue;
            if ("()".equals(method.getParameterSignature()) && (this.kind == PerClause.SINGLETON || this.kind == PerClause.PERCFLOW)) {
                return true;
            }
            if ("(Ljava/lang/Object;)".equals(method.getParameterSignature()) && this.kind == PerClause.PEROBJECT) {
                return true;
            }
            if (!"(Ljava/lang/Class;)".equals(method.getParameterSignature()) || this.kind != PerClause.PERTYPEWITHIN) continue;
            return true;
        }
        return false;
    }

    private void generatePerClauseMembers(LazyClassGen classGen) {
        ResolvedMember failureFieldInfo = AjcMemberMaker.initFailureCauseField(this.aspectType);
        if (this.kind == PerClause.SINGLETON) {
            classGen.addField(this.makeFieldGen(classGen, failureFieldInfo), null);
        }
        if (this.kind == PerClause.SINGLETON) {
            ResolvedMember perSingletonFieldInfo = AjcMemberMaker.perSingletonField(this.aspectType);
            classGen.addField(this.makeFieldGen(classGen, perSingletonFieldInfo), null);
        } else if (this.kind == PerClause.PERCFLOW) {
            ResolvedMember perCflowFieldInfo = AjcMemberMaker.perCflowField(this.aspectType);
            classGen.addField(this.makeFieldGen(classGen, perCflowFieldInfo), null);
        } else if (this.kind == PerClause.PERTYPEWITHIN) {
            ResolvedMember perTypeWithinForField = AjcMemberMaker.perTypeWithinWithinTypeField(this.aspectType, this.aspectType);
            classGen.addField(this.makeFieldGen(classGen, perTypeWithinForField), null);
        }
    }

    private void generatePerSingletonAspectOfMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perSingletonAspectOfMethod(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(Utility.createGet(factory, AjcMemberMaker.perSingletonField(this.aspectType)));
        InstructionBranch ifNotNull = InstructionFactory.createBranchInstruction((short)199, null);
        il.append(ifNotNull);
        il.append(factory.createNew(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName()));
        il.append(InstructionConstants.DUP);
        il.append(InstructionFactory.PUSH(classGen.getConstantPool(), this.aspectType.getName()));
        il.append(Utility.createGet(factory, AjcMemberMaker.initFailureCauseField(this.aspectType)));
        il.append(factory.createInvoke(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName(), "<init>", Type.VOID, new Type[]{Type.STRING, new ObjectType("java.lang.Throwable")}, (short)183));
        il.append(InstructionConstants.ATHROW);
        InstructionHandle ifElse = il.append(Utility.createGet(factory, AjcMemberMaker.perSingletonField(this.aspectType)));
        il.append(InstructionFactory.createReturn(Type.OBJECT));
        ifNotNull.setTarget(ifElse);
    }

    private void generatePerSingletonHasAspectMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perSingletonHasAspectMethod(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(Utility.createGet(factory, AjcMemberMaker.perSingletonField(this.aspectType)));
        InstructionBranch ifNull = InstructionFactory.createBranchInstruction((short)198, null);
        il.append(ifNull);
        il.append(InstructionFactory.PUSH(classGen.getConstantPool(), true));
        il.append(InstructionFactory.createReturn(Type.INT));
        InstructionHandle ifElse = il.append(InstructionFactory.PUSH(classGen.getConstantPool(), false));
        il.append(InstructionFactory.createReturn(Type.INT));
        ifNull.setTarget(ifElse);
    }

    private void generatePerSingletonAjcClinitMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.ajcPostClinitMethod(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, true);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(factory.createNew(this.aspectType.getName()));
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke(this.aspectType.getName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        il.append(Utility.createSet(factory, AjcMemberMaker.perSingletonField(this.aspectType)));
        il.append(InstructionFactory.createReturn(Type.VOID));
        LazyMethodGen clinit = classGen.getStaticInitializer();
        il = new InstructionList();
        InstructionHandle tryStart = il.append(factory.createInvoke(this.aspectType.getName(), "ajc$postClinit", Type.VOID, Type.NO_ARGS, (short)184));
        InstructionBranch tryEnd = InstructionFactory.createBranchInstruction((short)167, null);
        il.append(tryEnd);
        InstructionHandle handler = il.append(InstructionConstants.ASTORE_0);
        il.append(InstructionConstants.ALOAD_0);
        il.append(Utility.createSet(factory, AjcMemberMaker.initFailureCauseField(this.aspectType)));
        il.append(InstructionFactory.createReturn(Type.VOID));
        tryEnd.setTarget(il.getEnd());
        if (clinit.getBody().getEnd().getInstruction().opcode == 254) {
            clinit.getBody().getEnd().getPrev().setInstruction(InstructionConstants.NOP);
        }
        clinit.getBody().getEnd().setInstruction(InstructionConstants.NOP);
        clinit.getBody().append(il);
        clinit.addExceptionHandler(tryStart, handler.getPrev(), handler, new ObjectType("java.lang.Throwable"), false);
    }

    private void generatePerObjectAspectOfMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        ReferenceType interfaceType = (ReferenceType)BcelWorld.makeBcelType(AjcMemberMaker.perObjectInterfaceType(this.aspectType));
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perObjectAspectOfMethod(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(InstructionConstants.ALOAD_0);
        il.append(factory.createInstanceOf(interfaceType));
        InstructionBranch ifEq = InstructionFactory.createBranchInstruction((short)153, null);
        il.append(ifEq);
        il.append(InstructionConstants.ALOAD_0);
        il.append(factory.createCheckCast(interfaceType));
        il.append(Utility.createInvoke(factory, (short)185, (Member)AjcMemberMaker.perObjectInterfaceGet(this.aspectType)));
        il.append(InstructionConstants.DUP);
        InstructionBranch ifNull = InstructionFactory.createBranchInstruction((short)198, null);
        il.append(ifNull);
        il.append(InstructionFactory.createReturn(BcelWorld.makeBcelType(this.aspectType)));
        InstructionHandle ifNullElse = il.append(InstructionConstants.POP);
        ifNull.setTarget(ifNullElse);
        InstructionHandle ifEqElse = il.append(factory.createNew(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName()));
        ifEq.setTarget(ifEqElse);
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        il.append(InstructionConstants.ATHROW);
    }

    private void generatePerObjectHasAspectMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        ReferenceType interfaceType = (ReferenceType)BcelWorld.makeBcelType(AjcMemberMaker.perObjectInterfaceType(this.aspectType));
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perObjectHasAspectMethod(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(InstructionConstants.ALOAD_0);
        il.append(factory.createInstanceOf(interfaceType));
        InstructionBranch ifEq = InstructionFactory.createBranchInstruction((short)153, null);
        il.append(ifEq);
        il.append(InstructionConstants.ALOAD_0);
        il.append(factory.createCheckCast(interfaceType));
        il.append(Utility.createInvoke(factory, (short)185, (Member)AjcMemberMaker.perObjectInterfaceGet(this.aspectType)));
        InstructionBranch ifNull = InstructionFactory.createBranchInstruction((short)198, null);
        il.append(ifNull);
        il.append(InstructionConstants.ICONST_1);
        il.append(InstructionFactory.createReturn(Type.INT));
        InstructionHandle ifEqElse = il.append(InstructionConstants.ICONST_0);
        ifEq.setTarget(ifEqElse);
        ifNull.setTarget(ifEqElse);
        il.append(InstructionFactory.createReturn(Type.INT));
    }

    private void generatePerObjectBindMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        ReferenceType interfaceType = (ReferenceType)BcelWorld.makeBcelType(AjcMemberMaker.perObjectInterfaceType(this.aspectType));
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perObjectBind(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, true);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(InstructionConstants.ALOAD_0);
        il.append(factory.createInstanceOf(interfaceType));
        InstructionBranch ifEq = InstructionFactory.createBranchInstruction((short)153, null);
        il.append(ifEq);
        il.append(InstructionConstants.ALOAD_0);
        il.append(factory.createCheckCast(interfaceType));
        il.append(Utility.createInvoke(factory, (short)185, (Member)AjcMemberMaker.perObjectInterfaceGet(this.aspectType)));
        InstructionBranch ifNonNull = InstructionFactory.createBranchInstruction((short)199, null);
        il.append(ifNonNull);
        il.append(InstructionConstants.ALOAD_0);
        il.append(factory.createCheckCast(interfaceType));
        il.append(factory.createNew(this.aspectType.getName()));
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke(this.aspectType.getName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        il.append(Utility.createInvoke(factory, (short)185, (Member)AjcMemberMaker.perObjectInterfaceSet(this.aspectType)));
        InstructionHandle end = il.append(InstructionFactory.createReturn(Type.VOID));
        ifEq.setTarget(end);
        ifNonNull.setTarget(end);
    }

    private void generatePerCflowAspectOfMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perCflowAspectOfMethod(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(Utility.createGet(factory, AjcMemberMaker.perCflowField(this.aspectType)));
        il.append(Utility.createInvoke(factory, (short)182, AjcMemberMaker.cflowStackPeekInstance()));
        il.append(factory.createCheckCast((ReferenceType)BcelWorld.makeBcelType(this.aspectType)));
        il.append(InstructionFactory.createReturn(Type.OBJECT));
    }

    private void generatePerCflowHasAspectMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perCflowHasAspectMethod(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(Utility.createGet(factory, AjcMemberMaker.perCflowField(this.aspectType)));
        il.append(Utility.createInvoke(factory, (short)182, AjcMemberMaker.cflowStackIsValid()));
        il.append(InstructionFactory.createReturn(Type.INT));
    }

    private void generatePerCflowPushMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perCflowPush(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, true);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(Utility.createGet(factory, AjcMemberMaker.perCflowField(this.aspectType)));
        il.append(factory.createNew(this.aspectType.getName()));
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke(this.aspectType.getName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        il.append(Utility.createInvoke(factory, (short)182, AjcMemberMaker.cflowStackPushInstance()));
        il.append(InstructionFactory.createReturn(Type.VOID));
    }

    private void generatePerCflowAjcClinitMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = classGen.getAjcPreClinit();
        InstructionList il = new InstructionList();
        il.append(factory.createNew(AjcMemberMaker.CFLOW_STACK_TYPE.getName()));
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke(AjcMemberMaker.CFLOW_STACK_TYPE.getName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        il.append(Utility.createSet(factory, AjcMemberMaker.perCflowField(this.aspectType)));
        method.getBody().insert(il);
    }

    private void generatePerTWAspectOfMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perTypeWithinAspectOfMethod(this.aspectType, classGen.getWorld().isInJava5Mode()));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        InstructionHandle tryStart = il.append(InstructionConstants.ALOAD_0);
        il.append(Utility.createInvoke(factory, (short)184, (Member)AjcMemberMaker.perTypeWithinGetInstance(this.aspectType)));
        il.append(InstructionConstants.ASTORE_1);
        il.append(InstructionConstants.ALOAD_1);
        InstructionBranch ifNonNull = InstructionFactory.createBranchInstruction((short)199, null);
        il.append(ifNonNull);
        il.append(factory.createNew(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName()));
        il.append(InstructionConstants.DUP);
        il.append(InstructionFactory.PUSH(classGen.getConstantPool(), this.aspectType.getName()));
        il.append(InstructionConstants.ACONST_NULL);
        il.append(factory.createInvoke(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName(), "<init>", Type.VOID, new Type[]{Type.STRING, new ObjectType("java.lang.Throwable")}, (short)183));
        il.append(InstructionConstants.ATHROW);
        InstructionHandle ifElse = il.append(InstructionConstants.ALOAD_1);
        ifNonNull.setTarget(ifElse);
        il.append(InstructionFactory.createReturn(Type.OBJECT));
        InstructionHandle handler = il.append(InstructionConstants.ASTORE_1);
        il.append(factory.createNew(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName()));
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke(AjcMemberMaker.NO_ASPECT_BOUND_EXCEPTION.getName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        il.append(InstructionConstants.ATHROW);
        method.addExceptionHandler(tryStart, handler.getPrev(), handler, new ObjectType("java.lang.Exception"), false);
    }

    private void generatePerTWGetWithinTypeNameMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perTypeWithinGetWithinTypeNameMethod(this.aspectType, classGen.getWorld().isInJava5Mode()));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(InstructionConstants.ALOAD_0);
        il.append(Utility.createGet(factory, AjcMemberMaker.perTypeWithinWithinTypeField(this.aspectType, this.aspectType)));
        il.append(InstructionConstants.ARETURN);
    }

    private void generatePerTWHasAspectMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perTypeWithinHasAspectMethod(this.aspectType, classGen.getWorld().isInJava5Mode()));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, false);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        InstructionHandle tryStart = il.append(InstructionConstants.ALOAD_0);
        il.append(Utility.createInvoke(factory, (short)184, (Member)AjcMemberMaker.perTypeWithinGetInstance(this.aspectType)));
        InstructionBranch ifNull = InstructionFactory.createBranchInstruction((short)198, null);
        il.append(ifNull);
        il.append(InstructionConstants.ICONST_1);
        il.append(InstructionConstants.IRETURN);
        InstructionHandle ifElse = il.append(InstructionConstants.ICONST_0);
        ifNull.setTarget(ifElse);
        il.append(InstructionConstants.IRETURN);
        InstructionHandle handler = il.append(InstructionConstants.ASTORE_1);
        il.append(InstructionConstants.ICONST_0);
        il.append(InstructionConstants.IRETURN);
        method.addExceptionHandler(tryStart, handler.getPrev(), handler, new ObjectType("java.lang.Exception"), false);
    }

    private void generatePerTWGetInstanceMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perTypeWithinGetInstance(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, true);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        InstructionHandle tryStart = il.append(InstructionConstants.ALOAD_0);
        il.append(InstructionFactory.PUSH(factory.getConstantPool(), NameMangler.perTypeWithinLocalAspectOf(this.aspectType)));
        il.append(InstructionConstants.ACONST_NULL);
        il.append(factory.createInvoke("java/lang/Class", "getDeclaredMethod", Type.getType("Ljava/lang/reflect/Method;"), new Type[]{Type.getType("Ljava/lang/String;"), Type.getType("[Ljava/lang/Class;")}, (short)182));
        il.append(InstructionConstants.ACONST_NULL);
        il.append(InstructionConstants.ACONST_NULL);
        il.append(factory.createInvoke("java/lang/reflect/Method", "invoke", Type.OBJECT, new Type[]{Type.getType("Ljava/lang/Object;"), Type.getType("[Ljava/lang/Object;")}, (short)182));
        il.append(factory.createCheckCast((ReferenceType)BcelWorld.makeBcelType(this.aspectType)));
        il.append(InstructionConstants.ARETURN);
        InstructionHandle handler = il.append(InstructionConstants.ASTORE_1);
        il.append(InstructionConstants.ACONST_NULL);
        il.append(InstructionConstants.ARETURN);
        method.addExceptionHandler(tryStart, handler.getPrev(), handler, new ObjectType("java.lang.Exception"), false);
    }

    private void generatePerTWCreateAspectInstanceMethod(LazyClassGen classGen) {
        InstructionFactory factory = classGen.getFactory();
        LazyMethodGen method = this.makeMethodGen(classGen, AjcMemberMaker.perTypeWithinCreateAspectInstance(this.aspectType));
        BcelPerClauseAspectAdder.flagAsSynthetic(method, true);
        classGen.addMethodGen(method);
        InstructionList il = method.getBody();
        il.append(factory.createNew(this.aspectType.getName()));
        il.append(InstructionConstants.DUP);
        il.append(factory.createInvoke(this.aspectType.getName(), "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        il.append(InstructionConstants.ASTORE_1);
        il.append(InstructionConstants.ALOAD_1);
        il.append(InstructionConstants.ALOAD_0);
        il.append(Utility.createSet(factory, AjcMemberMaker.perTypeWithinWithinTypeField(this.aspectType, this.aspectType)));
        il.append(InstructionConstants.ALOAD_1);
        il.append(InstructionConstants.ARETURN);
    }

    private static void flagAsSynthetic(LazyMethodGen methodGen, boolean makeJavaSynthetic) {
        if (makeJavaSynthetic) {
            methodGen.makeSynthetic();
        }
        methodGen.addAttribute(Utility.bcelAttribute(new AjAttribute.AjSynthetic(), methodGen.getEnclosingClass().getConstantPool()));
    }
}

