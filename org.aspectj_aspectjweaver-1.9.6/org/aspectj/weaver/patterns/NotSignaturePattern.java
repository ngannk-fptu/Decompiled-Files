/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
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

public class NotSignaturePattern
extends AbstractSignaturePattern {
    private ISignaturePattern negatedSp;

    public NotSignaturePattern(ISignaturePattern negatedSp) {
        this.negatedSp = negatedSp;
    }

    @Override
    public boolean couldEverMatch(ResolvedType type) {
        if (this.negatedSp.getExactDeclaringTypes().size() == 0) {
            return true;
        }
        return !this.negatedSp.couldEverMatch(type);
    }

    @Override
    public List<ExactTypePattern> getExactDeclaringTypes() {
        return this.negatedSp.getExactDeclaringTypes();
    }

    @Override
    public boolean isMatchOnAnyName() {
        return this.negatedSp.isMatchOnAnyName();
    }

    @Override
    public boolean isStarAnnotation() {
        return this.negatedSp.isStarAnnotation();
    }

    @Override
    public boolean matches(Member member, World world, boolean b) {
        return !this.negatedSp.matches(member, world, b);
    }

    @Override
    public ISignaturePattern parameterizeWith(Map<String, UnresolvedType> typeVariableBindingMap, World world) {
        return new NotSignaturePattern(this.negatedSp.parameterizeWith(typeVariableBindingMap, world));
    }

    @Override
    public ISignaturePattern resolveBindings(IScope scope, Bindings bindings) {
        this.negatedSp.resolveBindings(scope, bindings);
        return this;
    }

    public static ISignaturePattern readNotSignaturePattern(VersionedDataInputStream s, ISourceContext context) throws IOException {
        NotSignaturePattern ret = new NotSignaturePattern(NotSignaturePattern.readCompoundSignaturePattern(s, context));
        s.readInt();
        s.readInt();
        return ret;
    }

    public ISignaturePattern getNegated() {
        return this.negatedSp;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("!").append(this.negatedSp.toString());
        return sb.toString();
    }
}

