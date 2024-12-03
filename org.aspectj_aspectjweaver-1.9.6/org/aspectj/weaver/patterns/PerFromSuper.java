/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.PerSingleton;
import org.aspectj.weaver.patterns.Pointcut;

public class PerFromSuper
extends PerClause {
    private PerClause.Kind kind;

    public PerFromSuper(PerClause.Kind kind) {
        this.kind = kind;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public PerClause concretize(ResolvedType inAspect) {
        PerClause p = this.lookupConcretePerClause(inAspect.getSuperclass());
        if (p == null) {
            inAspect.getWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("missingPerClause", inAspect.getSuperclass()), this.getSourceLocation()));
            return new PerSingleton().concretize(inAspect);
        }
        if (p.getKind() != this.kind) {
            inAspect.getWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("wrongPerClause", this.kind, p.getKind()), this.getSourceLocation()));
        }
        return p.concretize(inAspect);
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        return this;
    }

    public PerClause lookupConcretePerClause(ResolvedType lookupType) {
        PerClause ret = lookupType.getPerClause();
        if (ret == null) {
            return null;
        }
        if (ret instanceof PerFromSuper) {
            return this.lookupConcretePerClause(lookupType.getSuperclass());
        }
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        FROMSUPER.write(s);
        this.kind.write(s);
        this.writeLocation(s);
    }

    public static PerClause readPerClause(VersionedDataInputStream s, ISourceContext context) throws IOException {
        PerFromSuper ret = new PerFromSuper(PerClause.Kind.read(s));
        ret.readLocation(context, s);
        return ret;
    }

    public String toString() {
        return "perFromSuper(" + this.kind + ", " + this.inAspect + ")";
    }

    @Override
    public String toDeclarationString() {
        return "";
    }

    @Override
    public PerClause.Kind getKind() {
        return this.kind;
    }

    public boolean equals(Object other) {
        if (!(other instanceof PerFromSuper)) {
            return false;
        }
        PerFromSuper pc = (PerFromSuper)other;
        return pc.kind.equals(this.kind) && (pc.inAspect == null ? this.inAspect == null : pc.inAspect.equals(this.inAspect));
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.kind.hashCode();
        result = 37 * result + (this.inAspect == null ? 0 : this.inAspect.hashCode());
        return result;
    }
}

