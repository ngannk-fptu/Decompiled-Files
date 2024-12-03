/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.FieldGen;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.bcel.BcelClassWeaver;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.LazyMethodGen;
import org.aspectj.weaver.bcel.Utility;

public class BcelCflowStackFieldAdder
extends BcelTypeMunger {
    private ResolvedMember cflowStackField;

    public BcelCflowStackFieldAdder(ResolvedMember cflowStackField) {
        super(null, (ResolvedType)cflowStackField.getDeclaringType());
        this.cflowStackField = cflowStackField;
    }

    @Override
    public boolean munge(BcelClassWeaver weaver) {
        LazyClassGen gen = weaver.getLazyClassGen();
        if (!gen.getType().equals(this.cflowStackField.getDeclaringType())) {
            return false;
        }
        FieldGen f = new FieldGen(this.cflowStackField.getModifiers(), BcelWorld.makeBcelType(this.cflowStackField.getReturnType()), this.cflowStackField.getName(), gen.getConstantPool());
        gen.addField(f, this.getSourceLocation());
        LazyMethodGen clinit = gen.getAjcPreClinit();
        InstructionList setup = new InstructionList();
        InstructionFactory fact = gen.getFactory();
        setup.append(fact.createNew("org.aspectj.runtime.internal.CFlowStack"));
        setup.append(InstructionFactory.createDup(1));
        setup.append(fact.createInvoke("org.aspectj.runtime.internal.CFlowStack", "<init>", Type.VOID, Type.NO_ARGS, (short)183));
        setup.append(Utility.createSet(fact, this.cflowStackField));
        clinit.getBody().insert(setup);
        return true;
    }

    @Override
    public ResolvedMember getMatchingSyntheticMember(Member member) {
        return null;
    }

    @Override
    public ResolvedMember getSignature() {
        return this.cflowStackField;
    }

    @Override
    public boolean matches(ResolvedType onType) {
        return onType.equals(this.cflowStackField.getDeclaringType());
    }

    @Override
    public boolean existsToSupportShadowMunging() {
        return true;
    }
}

