/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.patterns.PatternNodeVisitor;

public class AnnotationPatternList
extends PatternNode {
    private AnnotationTypePattern[] typePatterns;
    int ellipsisCount = 0;
    public static final AnnotationPatternList EMPTY = new AnnotationPatternList(new AnnotationTypePattern[0]);
    public static final AnnotationPatternList ANY = new AnnotationPatternList(new AnnotationTypePattern[]{AnnotationTypePattern.ELLIPSIS});

    public AnnotationPatternList() {
        this.typePatterns = new AnnotationTypePattern[0];
        this.ellipsisCount = 0;
    }

    public AnnotationPatternList(AnnotationTypePattern[] arguments) {
        this.typePatterns = arguments;
        for (int i = 0; i < arguments.length; ++i) {
            if (arguments[i] != AnnotationTypePattern.ELLIPSIS) continue;
            ++this.ellipsisCount;
        }
    }

    public AnnotationPatternList(List<AnnotationTypePattern> l) {
        this(l.toArray(new AnnotationTypePattern[l.size()]));
    }

    protected AnnotationTypePattern[] getAnnotationPatterns() {
        return this.typePatterns;
    }

    public AnnotationPatternList parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        AnnotationTypePattern[] parameterizedPatterns = new AnnotationTypePattern[this.typePatterns.length];
        for (int i = 0; i < parameterizedPatterns.length; ++i) {
            parameterizedPatterns[i] = this.typePatterns[i].parameterizeWith(typeVariableMap, w);
        }
        AnnotationPatternList ret = new AnnotationPatternList(parameterizedPatterns);
        ret.copyLocationFrom(this);
        return ret;
    }

    public void resolve(World inWorld) {
        for (int i = 0; i < this.typePatterns.length; ++i) {
            this.typePatterns[i].resolve(inWorld);
        }
    }

    public FuzzyBoolean matches(ResolvedType[] someArgs) {
        int numArgsMatchedByEllipsis = someArgs.length + this.ellipsisCount - this.typePatterns.length;
        if (numArgsMatchedByEllipsis < 0) {
            return FuzzyBoolean.NO;
        }
        if (numArgsMatchedByEllipsis > 0 && this.ellipsisCount == 0) {
            return FuzzyBoolean.NO;
        }
        FuzzyBoolean ret = FuzzyBoolean.YES;
        int argsIndex = 0;
        for (int i = 0; i < this.typePatterns.length; ++i) {
            if (this.typePatterns[i] == AnnotationTypePattern.ELLIPSIS) {
                argsIndex += numArgsMatchedByEllipsis;
                continue;
            }
            if (this.typePatterns[i] == AnnotationTypePattern.ANY) {
                ++argsIndex;
                continue;
            }
            if (someArgs[argsIndex].isPrimitiveType()) {
                return FuzzyBoolean.NO;
            }
            ExactAnnotationTypePattern ap = (ExactAnnotationTypePattern)this.typePatterns[i];
            FuzzyBoolean matches = ap.matchesRuntimeType(someArgs[argsIndex]);
            if (matches == FuzzyBoolean.NO) {
                return FuzzyBoolean.MAYBE;
            }
            ++argsIndex;
            ret = ret.and(matches);
        }
        return ret;
    }

    public int size() {
        return this.typePatterns.length;
    }

    public AnnotationTypePattern get(int index) {
        return this.typePatterns[index];
    }

    public AnnotationPatternList resolveBindings(IScope scope, Bindings bindings, boolean allowBinding) {
        for (int i = 0; i < this.typePatterns.length; ++i) {
            AnnotationTypePattern p = this.typePatterns[i];
            if (p == null) continue;
            this.typePatterns[i] = this.typePatterns[i].resolveBindings(scope, bindings, allowBinding);
        }
        return this;
    }

    public AnnotationPatternList resolveReferences(IntMap bindings) {
        int len = this.typePatterns.length;
        AnnotationTypePattern[] ret = new AnnotationTypePattern[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = this.typePatterns[i].remapAdviceFormals(bindings);
        }
        return new AnnotationPatternList(ret);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        int len = this.typePatterns.length;
        for (int i = 0; i < len; ++i) {
            AnnotationTypePattern type = this.typePatterns[i];
            if (i > 0) {
                buf.append(", ");
            }
            if (type == AnnotationTypePattern.ELLIPSIS) {
                buf.append("..");
                continue;
            }
            String annPatt = type.toString();
            buf.append(annPatt.startsWith("@") ? annPatt.substring(1) : annPatt);
        }
        buf.append(")");
        return buf.toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof AnnotationPatternList)) {
            return false;
        }
        AnnotationPatternList o = (AnnotationPatternList)other;
        int len = o.typePatterns.length;
        if (len != this.typePatterns.length) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (this.typePatterns[i].equals(o.typePatterns[i])) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = 41;
        int len = this.typePatterns.length;
        for (int i = 0; i < len; ++i) {
            result = 37 * result + this.typePatterns[i].hashCode();
        }
        return result;
    }

    public static AnnotationPatternList read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        int len = s.readShort();
        AnnotationTypePattern[] arguments = new AnnotationTypePattern[len];
        for (int i = 0; i < len; ++i) {
            arguments[i] = AnnotationTypePattern.read(s, context);
        }
        AnnotationPatternList ret = new AnnotationPatternList(arguments);
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeShort(this.typePatterns.length);
        for (int i = 0; i < this.typePatterns.length; ++i) {
            this.typePatterns[i].write(s);
        }
        this.writeLocation(s);
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object traverse(PatternNodeVisitor visitor, Object data) {
        Object ret = this.accept(visitor, data);
        for (int i = 0; i < this.typePatterns.length; ++i) {
            this.typePatterns[i].traverse(visitor, ret);
        }
        return ret;
    }
}

