/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;

public class AnyWithAnnotationTypePattern
extends TypePattern {
    public AnyWithAnnotationTypePattern(AnnotationTypePattern atp) {
        super(false, false);
        this.annotationPattern = atp;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    protected boolean couldEverMatchSameTypesAs(TypePattern other) {
        return true;
    }

    @Override
    protected boolean matchesExactly(ResolvedType type) {
        this.annotationPattern.resolve(type.getWorld());
        boolean b = false;
        b = type.temporaryAnnotationTypes != null ? this.annotationPattern.matches(type, type.temporaryAnnotationTypes).alwaysTrue() : this.annotationPattern.matches(type).alwaysTrue();
        return b;
    }

    @Override
    public TypePattern resolveBindings(IScope scope, Bindings bindings, boolean allowBinding, boolean requireExactType) {
        if (requireExactType) {
            scope.getWorld().getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("wildcardTypePatternNotAllowed"), this.getSourceLocation()));
            return NO;
        }
        return super.resolveBindings(scope, bindings, allowBinding, requireExactType);
    }

    @Override
    protected boolean matchesExactly(ResolvedType type, ResolvedType annotatedType) {
        this.annotationPattern.resolve(type.getWorld());
        return this.annotationPattern.matches(annotatedType).alwaysTrue();
    }

    @Override
    public FuzzyBoolean matchesInstanceof(ResolvedType type) {
        if (Modifier.isFinal(type.getModifiers())) {
            return FuzzyBoolean.fromBoolean(this.matchesExactly(type));
        }
        return FuzzyBoolean.MAYBE;
    }

    @Override
    public TypePattern parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        AnyWithAnnotationTypePattern ret = new AnyWithAnnotationTypePattern(this.annotationPattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(10);
        this.annotationPattern.write(s);
        this.writeLocation(s);
    }

    public static TypePattern read(VersionedDataInputStream s, ISourceContext c) throws IOException {
        AnnotationTypePattern annPatt = AnnotationTypePattern.read(s, c);
        AnyWithAnnotationTypePattern ret = new AnyWithAnnotationTypePattern(annPatt);
        ret.readLocation(c, s);
        return ret;
    }

    @Override
    protected boolean matchesSubtypes(ResolvedType type) {
        return true;
    }

    @Override
    public boolean isStar() {
        return false;
    }

    public String toString() {
        return "(" + this.annotationPattern + " *)";
    }

    public AnnotationTypePattern getAnnotationTypePattern() {
        return this.annotationPattern;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AnyWithAnnotationTypePattern)) {
            return false;
        }
        AnyWithAnnotationTypePattern awatp = (AnyWithAnnotationTypePattern)obj;
        return this.annotationPattern.equals(awatp.annotationPattern);
    }

    public int hashCode() {
        return this.annotationPattern.hashCode();
    }
}

