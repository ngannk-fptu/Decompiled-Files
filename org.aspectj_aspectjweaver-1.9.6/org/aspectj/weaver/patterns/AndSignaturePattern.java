/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AbstractSignaturePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.ISignaturePattern;

public class AndSignaturePattern
extends AbstractSignaturePattern {
    private ISignaturePattern leftSp;
    private ISignaturePattern rightSp;
    private List<ExactTypePattern> exactDeclaringTypes;

    public AndSignaturePattern(ISignaturePattern leftSp, ISignaturePattern rightSp) {
        this.leftSp = leftSp;
        this.rightSp = rightSp;
    }

    @Override
    public boolean couldEverMatch(ResolvedType type) {
        return this.leftSp.couldEverMatch(type) || this.rightSp.couldEverMatch(type);
    }

    @Override
    public List<ExactTypePattern> getExactDeclaringTypes() {
        if (this.exactDeclaringTypes == null) {
            this.exactDeclaringTypes = new ArrayList<ExactTypePattern>();
            this.exactDeclaringTypes.addAll(this.leftSp.getExactDeclaringTypes());
            this.exactDeclaringTypes.addAll(this.rightSp.getExactDeclaringTypes());
        }
        return this.exactDeclaringTypes;
    }

    @Override
    public boolean isMatchOnAnyName() {
        return this.leftSp.isMatchOnAnyName() || this.rightSp.isMatchOnAnyName();
    }

    @Override
    public boolean isStarAnnotation() {
        return this.leftSp.isStarAnnotation() || this.rightSp.isStarAnnotation();
    }

    @Override
    public boolean matches(Member member, World world, boolean b) {
        return this.leftSp.matches(member, world, b) && this.rightSp.matches(member, world, b);
    }

    @Override
    public ISignaturePattern parameterizeWith(Map<String, UnresolvedType> typeVariableBindingMap, World world) {
        return new AndSignaturePattern(this.leftSp.parameterizeWith(typeVariableBindingMap, world), this.rightSp.parameterizeWith(typeVariableBindingMap, world));
    }

    @Override
    public ISignaturePattern resolveBindings(IScope scope, Bindings bindings) {
        this.leftSp.resolveBindings(scope, bindings);
        this.rightSp.resolveBindings(scope, bindings);
        return this;
    }

    public static ISignaturePattern readAndSignaturePattern(VersionedDataInputStream s, ISourceContext context) throws IOException {
        AndSignaturePattern ret = new AndSignaturePattern(AndSignaturePattern.readCompoundSignaturePattern(s, context), AndSignaturePattern.readCompoundSignaturePattern(s, context));
        s.readInt();
        s.readInt();
        return ret;
    }

    public ISignaturePattern getLeft() {
        return this.leftSp;
    }

    public ISignaturePattern getRight() {
        return this.rightSp;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.leftSp.toString()).append(" && ").append(this.rightSp.toString());
        return sb.toString();
    }
}

