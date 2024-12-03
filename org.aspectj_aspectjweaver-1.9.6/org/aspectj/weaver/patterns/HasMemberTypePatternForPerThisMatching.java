/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.List;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.patterns.HasMemberTypePattern;
import org.aspectj.weaver.patterns.SignaturePattern;

public class HasMemberTypePatternForPerThisMatching
extends HasMemberTypePattern {
    public HasMemberTypePatternForPerThisMatching(SignaturePattern aSignaturePattern) {
        super(aSignaturePattern);
    }

    @Override
    protected boolean hasMethod(ResolvedType type) {
        boolean b = super.hasMethod(type);
        if (b) {
            return true;
        }
        List<ConcreteTypeMunger> mungers = type.getInterTypeMungersIncludingSupers();
        return mungers.size() != 0;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new IllegalAccessError("Should never be called, these are transient and don't get serialized");
    }
}

