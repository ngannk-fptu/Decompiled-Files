/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.FieldGen;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.ObjectType;
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

public class BcelCflowCounterFieldAdder
extends BcelTypeMunger {
    private ResolvedMember cflowCounterField;

    public BcelCflowCounterFieldAdder(ResolvedMember cflowCounterField) {
        super(null, (ResolvedType)cflowCounterField.getDeclaringType());
        this.cflowCounterField = cflowCounterField;
    }

    @Override
    public boolean munge(BcelClassWeaver weaver) {
        LazyClassGen gen = weaver.getLazyClassGen();
        if (!gen.getType().equals(this.cflowCounterField.getDeclaringType())) {
            return false;
        }
        FieldGen f = new FieldGen(this.cflowCounterField.getModifiers(), BcelWorld.makeBcelType(this.cflowCounterField.getReturnType()), this.cflowCounterField.getName(), gen.getConstantPool());
        gen.addField(f, this.getSourceLocation());
        LazyMethodGen clinit = gen.getAjcPreClinit();
        InstructionList setup = new InstructionList();
        InstructionFactory fact = gen.getFactory();
        setup.append(fact.createNew(new ObjectType("org.aspectj.runtime.internal.CFlowCounter")));
        setup.append(InstructionFactory.createDup(1));
        setup.append(fact.createInvoke("org.aspectj.runtime.internal.CFlowCounter", "<init>", Type.VOID, new Type[0], (short)183));
        setup.append(Utility.createSet(fact, this.cflowCounterField));
        clinit.getBody().insert(setup);
        return true;
    }

    @Override
    public ResolvedMember getMatchingSyntheticMember(Member member) {
        return null;
    }

    @Override
    public ResolvedMember getSignature() {
        return this.cflowCounterField;
    }

    @Override
    public boolean matches(ResolvedType onType) {
        return onType.equals(this.cflowCounterField.getDeclaringType());
    }

    @Override
    public boolean existsToSupportShadowMunging() {
        return true;
    }

    @Override
    public String toString() {
        return "(BcelTypeMunger: CflowField " + this.cflowCounterField.getDeclaringType().getName() + " " + this.cflowCounterField.getName() + ")";
    }
}

