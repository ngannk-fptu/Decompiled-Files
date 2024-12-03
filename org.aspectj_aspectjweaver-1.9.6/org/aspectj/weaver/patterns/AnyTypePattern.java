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

public class AnyTypePattern
extends TypePattern {
    public AnyTypePattern() {
        super(false, false, new TypePatternList());
    }

    @Override
    protected boolean couldEverMatchSameTypesAs(TypePattern other) {
        return true;
    }

    @Override
    protected boolean matchesExactly(ResolvedType type) {
        return true;
    }

    @Override
    protected boolean matchesExactly(ResolvedType type, ResolvedType annotatedType) {
        return true;
    }

    @Override
    public FuzzyBoolean matchesInstanceof(ResolvedType type) {
        return FuzzyBoolean.YES;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(5);
    }

    @Override
    protected boolean matchesSubtypes(ResolvedType type) {
        return true;
    }

    @Override
    public boolean isStar() {
        return true;
    }

    public String toString() {
        return "*";
    }

    public boolean equals(Object obj) {
        return obj instanceof AnyTypePattern;
    }

    public int hashCode() {
        return 37;
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

