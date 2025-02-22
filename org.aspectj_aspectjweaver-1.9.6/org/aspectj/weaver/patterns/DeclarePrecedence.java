/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.IMessage;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.Declare;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.TypePatternList;

public class DeclarePrecedence
extends Declare {
    private TypePatternList patterns;
    private IScope scope = null;

    public DeclarePrecedence(List patterns) {
        this(new TypePatternList(patterns));
    }

    private DeclarePrecedence(TypePatternList patterns) {
        this.patterns = patterns;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Declare parameterizeWith(Map typeVariableBindingMap, World w) {
        DeclarePrecedence ret = new DeclarePrecedence(this.patterns.parameterizeWith(typeVariableBindingMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("declare precedence: ");
        buf.append(this.patterns);
        buf.append(";");
        return buf.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof DeclarePrecedence)) {
            return false;
        }
        DeclarePrecedence o = (DeclarePrecedence)other;
        return o.patterns.equals(this.patterns);
    }

    public int hashCode() {
        return this.patterns.hashCode();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(4);
        this.patterns.write(s);
        this.writeLocation(s);
    }

    public static Declare read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        DeclarePrecedence ret = new DeclarePrecedence(TypePatternList.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    public void setScopeForResolution(IScope scope) {
        this.scope = scope;
    }

    public void ensureResolved() {
        if (this.scope != null) {
            try {
                this.resolve(this.scope);
            }
            finally {
                this.scope = null;
            }
        }
    }

    @Override
    public void resolve(IScope scope) {
        this.patterns = this.patterns.resolveBindings(scope, Bindings.NONE, false, false);
        boolean seenStar = false;
        for (int i = 0; i < this.patterns.size(); ++i) {
            TypePattern pi = this.patterns.get(i);
            if (pi.isStar()) {
                if (seenStar) {
                    scope.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("circularityInPrecedenceStar"), pi.getSourceLocation(), null);
                }
                seenStar = true;
                continue;
            }
            ResolvedType exactType = pi.getExactType().resolve(scope.getWorld());
            if (exactType.isMissing()) continue;
            if (!(exactType.isAspect() || exactType.isAnnotationStyleAspect() || pi.isIncludeSubtypes() || exactType.isTypeVariableReference())) {
                scope.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("nonAspectTypesInPrecedence", exactType.getName()), pi.getSourceLocation(), null);
            }
            for (int j = 0; j < this.patterns.size(); ++j) {
                TypePattern pj;
                if (j == i || (pj = this.patterns.get(j)).isStar() || !pj.matchesStatically(exactType)) continue;
                scope.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("circularityInPrecedenceTwo", exactType.getName()), pi.getSourceLocation(), pj.getSourceLocation());
            }
        }
    }

    public TypePatternList getPatterns() {
        this.ensureResolved();
        return this.patterns;
    }

    private int matchingIndex(ResolvedType a) {
        this.ensureResolved();
        int knownMatch = -1;
        int starMatch = -1;
        int len = this.patterns.size();
        for (int i = 0; i < len; ++i) {
            TypePattern p = this.patterns.get(i);
            if (p.isStar()) {
                starMatch = i;
                continue;
            }
            if (!p.matchesStatically(a)) continue;
            if (knownMatch != -1) {
                a.getWorld().showMessage(IMessage.ERROR, WeaverMessages.format("multipleMatchesInPrecedence", a, this.patterns.get(knownMatch), p), this.patterns.get(knownMatch).getSourceLocation(), p.getSourceLocation());
                return -1;
            }
            knownMatch = i;
        }
        if (knownMatch == -1) {
            return starMatch;
        }
        return knownMatch;
    }

    public int compare(ResolvedType aspect1, ResolvedType aspect2) {
        this.ensureResolved();
        int index1 = this.matchingIndex(aspect1);
        int index2 = this.matchingIndex(aspect2);
        if (index1 == -1 || index2 == -1) {
            return 0;
        }
        if (index1 == index2) {
            return 0;
        }
        if (index1 > index2) {
            return -1;
        }
        return 1;
    }

    @Override
    public boolean isAdviceLike() {
        return false;
    }

    @Override
    public String getNameSuffix() {
        return "precedence";
    }
}

