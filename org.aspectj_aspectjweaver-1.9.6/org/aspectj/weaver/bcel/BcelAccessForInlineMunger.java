/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.FieldInstruction;
import org.aspectj.apache.bcel.generic.FieldOrMethod;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InvokeDynamic;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.bcel.BcelClassWeaver;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Utility;

public class BcelAccessForInlineMunger
extends BcelTypeMunger {
    private Map<String, ResolvedMember> inlineAccessors;
    private LazyClassGen aspectGen;
    private Set<LazyMethodGen> inlineAccessorMethodGens;

    public BcelAccessForInlineMunger(ResolvedType aspectType) {
        super(null, aspectType);
        if (aspectType.getWorld().isXnoInline()) {
            throw new Error("This should not happen");
        }
    }

    @Override
    public boolean munge(BcelClassWeaver weaver) {
        this.aspectGen = weaver.getLazyClassGen();
        this.inlineAccessors = new HashMap<String, ResolvedMember>(0);
        this.inlineAccessorMethodGens = new HashSet<LazyMethodGen>();
        for (LazyMethodGen methodGen : this.aspectGen.getMethodGens()) {
            if (!methodGen.hasAnnotation(UnresolvedType.forName("org/aspectj/lang/annotation/Around"))) continue;
            this.openAroundAdvice(methodGen);
        }
        for (LazyMethodGen lazyMethodGen : this.inlineAccessorMethodGens) {
            this.aspectGen.addMethodGen(lazyMethodGen);
        }
        this.inlineAccessorMethodGens = null;
        return true;
    }

    @Override
    public ResolvedMember getMatchingSyntheticMember(Member member) {
        ResolvedMember rm = this.inlineAccessors.get(member.getName());
        return rm;
    }

    @Override
    public ResolvedMember getSignature() {
        return null;
    }

    @Override
    public boolean matches(ResolvedType onType) {
        return this.aspectType.equals(onType);
    }

    private void openAroundAdvice(LazyMethodGen aroundAdvice) {
        InstructionHandle curr = aroundAdvice.getBody().getStart();
        InstructionHandle end = aroundAdvice.getBody().getEnd();
        ConstantPool cpg = aroundAdvice.getEnclosingClass().getConstantPool();
        InstructionFactory factory = aroundAdvice.getEnclosingClass().getFactory();
        boolean realizedCannotInline = false;
        while (curr != end && !realizedCannotInline) {
            ResolvedType callee;
            FieldOrMethod invoke;
            InstructionHandle next = curr.getNext();
            Instruction inst = curr.getInstruction();
            if (inst instanceof InvokeInstruction) {
                invoke = (InvokeInstruction)inst;
                if (invoke instanceof InvokeDynamic) {
                    realizedCannotInline = true;
                    break;
                }
                callee = this.aspectGen.getWorld().resolve(UnresolvedType.forName(invoke.getClassName(cpg)));
                List<ResolvedMember> methods = callee.getMethodsWithoutIterator(false, true, false);
                for (ResolvedMember resolvedMember : methods) {
                    InvokeInstruction newInst;
                    ResolvedMember accessor;
                    if (!invoke.getName(cpg).equals(resolvedMember.getName()) || !invoke.getSignature(cpg).equals(resolvedMember.getSignature()) || resolvedMember.isPublic()) continue;
                    if ("<init>".equals(invoke.getName(cpg))) {
                        if (!invoke.getClassName(cpg).equals(resolvedMember.getDeclaringType().getPackageName() + "." + resolvedMember.getDeclaringType().getClassName())) break;
                        aroundAdvice.setCanInline(false);
                        realizedCannotInline = true;
                        break;
                    }
                    ResolvedType memberType = this.aspectGen.getWorld().resolve(resolvedMember.getDeclaringType());
                    if (!this.aspectType.equals(memberType) && memberType.isAssignableFrom(this.aspectType)) {
                        accessor = this.createOrGetInlineAccessorForSuperDispatch(resolvedMember);
                        newInst = factory.createInvoke(this.aspectType.getName(), accessor.getName(), BcelWorld.makeBcelType(accessor.getReturnType()), BcelWorld.makeBcelTypes(accessor.getParameterTypes()), (short)182);
                        curr.setInstruction(newInst);
                    } else {
                        accessor = this.createOrGetInlineAccessorForMethod(resolvedMember);
                        newInst = factory.createInvoke(this.aspectType.getName(), accessor.getName(), BcelWorld.makeBcelType(accessor.getReturnType()), BcelWorld.makeBcelTypes(accessor.getParameterTypes()), (short)184);
                        curr.setInstruction(newInst);
                    }
                    break;
                }
            } else if (inst instanceof FieldInstruction) {
                invoke = (FieldInstruction)inst;
                callee = this.aspectGen.getWorld().resolve(UnresolvedType.forName(invoke.getClassName(cpg)));
                for (int i = 0; i < callee.getDeclaredJavaFields().length; ++i) {
                    ResolvedMember resolvedMember = callee.getDeclaredJavaFields()[i];
                    if (!invoke.getName(cpg).equals(resolvedMember.getName()) || !invoke.getSignature(cpg).equals(resolvedMember.getSignature()) || resolvedMember.isPublic()) continue;
                    ResolvedMember accessor = inst.opcode == 180 || inst.opcode == 178 ? this.createOrGetInlineAccessorForFieldGet(resolvedMember) : this.createOrGetInlineAccessorForFieldSet(resolvedMember);
                    InvokeInstruction newInst = factory.createInvoke(this.aspectType.getName(), accessor.getName(), BcelWorld.makeBcelType(accessor.getReturnType()), BcelWorld.makeBcelTypes(accessor.getParameterTypes()), (short)184);
                    curr.setInstruction(newInst);
                    break;
                }
            }
            curr = next;
        }
        if (!realizedCannotInline) {
            aroundAdvice.setCanInline(true);
        }
    }

    private ResolvedMember createOrGetInlineAccessorForMethod(ResolvedMember resolvedMember) {
        String accessorName = NameMangler.inlineAccessMethodForMethod(resolvedMember.getName(), resolvedMember.getDeclaringType(), this.aspectType);
        String key = accessorName;
        ResolvedMember inlineAccessor = this.inlineAccessors.get(key);
        if (inlineAccessor == null) {
            inlineAccessor = AjcMemberMaker.inlineAccessMethodForMethod(this.aspectType, resolvedMember);
            InstructionFactory factory = this.aspectGen.getFactory();
            LazyMethodGen method = this.makeMethodGen(this.aspectGen, inlineAccessor);
            method.makeSynthetic();
            ArrayList<AjAttribute> methodAttributes = new ArrayList<AjAttribute>();
            methodAttributes.add(new AjAttribute.AjSynthetic());
            methodAttributes.add(new AjAttribute.EffectiveSignatureAttribute(resolvedMember, Shadow.MethodCall, false));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(0), this.aspectGen.getConstantPool()));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(1), this.aspectGen.getConstantPool()));
            this.inlineAccessorMethodGens.add(method);
            InstructionList il = method.getBody();
            int register = 0;
            int max = inlineAccessor.getParameterTypes().length;
            for (int i = 0; i < max; ++i) {
                UnresolvedType ptype = inlineAccessor.getParameterTypes()[i];
                Type type = BcelWorld.makeBcelType(ptype);
                il.append(InstructionFactory.createLoad(type, register));
                register += type.getSize();
            }
            il.append(Utility.createInvoke(factory, Modifier.isStatic(resolvedMember.getModifiers()) ? (short)184 : 182, (Member)resolvedMember));
            il.append(InstructionFactory.createReturn(BcelWorld.makeBcelType(inlineAccessor.getReturnType())));
            this.inlineAccessors.put(key, new BcelMethod(this.aspectGen.getBcelObjectType(), method.getMethod(), methodAttributes));
        }
        return inlineAccessor;
    }

    private ResolvedMember createOrGetInlineAccessorForSuperDispatch(ResolvedMember resolvedMember) {
        String accessor = NameMangler.superDispatchMethod(this.aspectType, resolvedMember.getName());
        String key = accessor;
        ResolvedMember inlineAccessor = this.inlineAccessors.get(key);
        if (inlineAccessor == null) {
            inlineAccessor = AjcMemberMaker.superAccessMethod(this.aspectType, resolvedMember);
            InstructionFactory factory = this.aspectGen.getFactory();
            LazyMethodGen method = this.makeMethodGen(this.aspectGen, inlineAccessor);
            method.makeSynthetic();
            ArrayList<AjAttribute> methodAttributes = new ArrayList<AjAttribute>();
            methodAttributes.add(new AjAttribute.AjSynthetic());
            methodAttributes.add(new AjAttribute.EffectiveSignatureAttribute(resolvedMember, Shadow.MethodCall, false));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(0), this.aspectGen.getConstantPool()));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(1), this.aspectGen.getConstantPool()));
            this.inlineAccessorMethodGens.add(method);
            InstructionList il = method.getBody();
            il.append(InstructionConstants.ALOAD_0);
            int register = 1;
            for (int i = 0; i < inlineAccessor.getParameterTypes().length; ++i) {
                UnresolvedType typeX = inlineAccessor.getParameterTypes()[i];
                Type type = BcelWorld.makeBcelType(typeX);
                il.append(InstructionFactory.createLoad(type, register));
                register += type.getSize();
            }
            il.append(Utility.createInvoke(factory, (short)183, (Member)resolvedMember));
            il.append(InstructionFactory.createReturn(BcelWorld.makeBcelType(inlineAccessor.getReturnType())));
            this.inlineAccessors.put(key, new BcelMethod(this.aspectGen.getBcelObjectType(), method.getMethod(), methodAttributes));
        }
        return inlineAccessor;
    }

    private ResolvedMember createOrGetInlineAccessorForFieldGet(ResolvedMember resolvedMember) {
        String accessor = NameMangler.inlineAccessMethodForFieldGet(resolvedMember.getName(), resolvedMember.getDeclaringType(), this.aspectType);
        String key = accessor;
        ResolvedMember inlineAccessor = this.inlineAccessors.get(key);
        if (inlineAccessor == null) {
            inlineAccessor = AjcMemberMaker.inlineAccessMethodForFieldGet(this.aspectType, resolvedMember);
            InstructionFactory factory = this.aspectGen.getFactory();
            LazyMethodGen method = this.makeMethodGen(this.aspectGen, inlineAccessor);
            method.makeSynthetic();
            ArrayList<AjAttribute> methodAttributes = new ArrayList<AjAttribute>();
            methodAttributes.add(new AjAttribute.AjSynthetic());
            methodAttributes.add(new AjAttribute.EffectiveSignatureAttribute(resolvedMember, Shadow.FieldGet, false));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(0), this.aspectGen.getConstantPool()));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(1), this.aspectGen.getConstantPool()));
            this.inlineAccessorMethodGens.add(method);
            InstructionList il = method.getBody();
            if (!Modifier.isStatic(resolvedMember.getModifiers())) {
                il.append(InstructionConstants.ALOAD_0);
            }
            il.append(Utility.createGet(factory, resolvedMember));
            il.append(InstructionFactory.createReturn(BcelWorld.makeBcelType(inlineAccessor.getReturnType())));
            this.inlineAccessors.put(key, new BcelMethod(this.aspectGen.getBcelObjectType(), method.getMethod(), methodAttributes));
        }
        return inlineAccessor;
    }

    private ResolvedMember createOrGetInlineAccessorForFieldSet(ResolvedMember resolvedMember) {
        String accessor = NameMangler.inlineAccessMethodForFieldSet(resolvedMember.getName(), resolvedMember.getDeclaringType(), this.aspectType);
        String key = accessor;
        ResolvedMember inlineAccessor = this.inlineAccessors.get(key);
        if (inlineAccessor == null) {
            inlineAccessor = AjcMemberMaker.inlineAccessMethodForFieldSet(this.aspectType, resolvedMember);
            InstructionFactory factory = this.aspectGen.getFactory();
            LazyMethodGen method = this.makeMethodGen(this.aspectGen, inlineAccessor);
            method.makeSynthetic();
            ArrayList<AjAttribute> methodAttributes = new ArrayList<AjAttribute>();
            methodAttributes.add(new AjAttribute.AjSynthetic());
            methodAttributes.add(new AjAttribute.EffectiveSignatureAttribute(resolvedMember, Shadow.FieldSet, false));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(0), this.aspectGen.getConstantPool()));
            method.addAttribute(Utility.bcelAttribute((AjAttribute)methodAttributes.get(1), this.aspectGen.getConstantPool()));
            this.inlineAccessorMethodGens.add(method);
            InstructionList il = method.getBody();
            if (Modifier.isStatic(resolvedMember.getModifiers())) {
                il.append(InstructionFactory.createLoad(BcelWorld.makeBcelType(resolvedMember.getReturnType()), 0));
            } else {
                il.append(InstructionConstants.ALOAD_0);
                il.append(InstructionFactory.createLoad(BcelWorld.makeBcelType(resolvedMember.getReturnType()), 1));
            }
            il.append(Utility.createSet(factory, resolvedMember));
            il.append(InstructionConstants.RETURN);
            this.inlineAccessors.put(key, new BcelMethod(this.aspectGen.getBcelObjectType(), method.getMethod(), methodAttributes));
        }
        return inlineAccessor;
    }
}

