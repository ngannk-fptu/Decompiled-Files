/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;

public class NoTypePattern
extends TypePattern {
    public NoTypePattern() {
        super(false, false, new TypePatternList());
    }

    @Override
    protected boolean couldEverMatchSameTypesAs(TypePattern other) {
        return false;
    }

    @Override
    protected boolean matchesExactly(ResolvedType type) {
        return false;
    }

    @Override
    protected boolean matchesExactly(ResolvedType type, ResolvedType annotatedType) {
        return false;
    }

    @Override
    public FuzzyBoolean matchesInstanceof(ResolvedType type) {
        return FuzzyBoolean.NO;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(9);
    }

    @Override
    protected boolean matchesSubtypes(ResolvedType type) {
        return false;
    }

    @Override
    public boolean isStar() {
        return false;
    }

    public String toString() {
        return "<nothing>";
    }

    public boolean equals(Object obj) {
        return obj instanceof NoTypePattern;
    }

    public int hashCode() {
        return 23273;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public TypePattern parameterizeWith(Map<String, UnresolvedType> arg0, World w) {
        return this;
    }
}

