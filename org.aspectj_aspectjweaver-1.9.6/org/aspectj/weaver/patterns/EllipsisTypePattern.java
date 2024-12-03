/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;

public class EllipsisTypePattern
extends TypePattern {
    public EllipsisTypePattern() {
        super(false, false, new TypePatternList());
    }

    @Override
    protected boolean couldEverMatchSameTypesAs(TypePattern other) {
        return true;
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
        s.writeByte(4);
    }

    @Override
    public boolean isEllipsis() {
        return true;
    }

    public String toString() {
        return "..";
    }

    public boolean equals(Object obj) {
        return obj instanceof EllipsisTypePattern;
    }

    public int hashCode() {
        return 629;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public TypePattern parameterizeWith(Map typeVariableMap, World w) {
        return this;
    }
}

