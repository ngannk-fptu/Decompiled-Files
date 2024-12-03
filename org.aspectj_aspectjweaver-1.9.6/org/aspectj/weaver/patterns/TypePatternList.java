/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvableTypeList;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.EllipsisTypePattern;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.TypePattern;

public class TypePatternList
extends PatternNode {
    private TypePattern[] typePatterns;
    int ellipsisCount = 0;
    public static final TypePatternList EMPTY = new TypePatternList(new TypePattern[0]);
    public static final TypePatternList ANY = new TypePatternList(new TypePattern[]{new EllipsisTypePattern()});

    public TypePatternList() {
        this.typePatterns = new TypePattern[0];
        this.ellipsisCount = 0;
    }

    public TypePatternList(TypePattern[] arguments) {
        this.typePatterns = arguments;
        for (int i = 0; i < arguments.length; ++i) {
            if (arguments[i] != TypePattern.ELLIPSIS) continue;
            ++this.ellipsisCount;
        }
    }

    public TypePatternList(List<TypePattern> l) {
        this(l.toArray(new TypePattern[l.size()]));
    }

    public int size() {
        return this.typePatterns.length;
    }

    public TypePattern get(int index) {
        return this.typePatterns[index];
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(");
        int len = this.typePatterns.length;
        for (int i = 0; i < len; ++i) {
            TypePattern type = this.typePatterns[i];
            if (i > 0) {
                buf.append(", ");
            }
            if (type == TypePattern.ELLIPSIS) {
                buf.append("..");
                continue;
            }
            buf.append(type.toString());
        }
        buf.append(")");
        return buf.toString();
    }

    public boolean canMatchSignatureWithNParameters(int numParams) {
        if (this.ellipsisCount == 0) {
            return numParams == this.size();
        }
        return this.size() - this.ellipsisCount <= numParams;
    }

    public FuzzyBoolean matches(ResolvedType[] types, TypePattern.MatchKind kind) {
        return this.matches(types, kind, (ResolvedType[][])null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FuzzyBoolean matches(ResolvedType[] types, TypePattern.MatchKind kind, ResolvedType[][] parameterAnnotations) {
        int nameLength = types.length;
        int patternLength = this.typePatterns.length;
        int nameIndex = 0;
        int patternIndex = 0;
        if (this.ellipsisCount == 0) {
            if (nameLength != patternLength) {
                return FuzzyBoolean.NO;
            }
            FuzzyBoolean finalReturn = FuzzyBoolean.YES;
            while (patternIndex < patternLength) {
                ResolvedType t = types[nameIndex];
                FuzzyBoolean ret = null;
                try {
                    if (parameterAnnotations != null) {
                        t.temporaryAnnotationTypes = parameterAnnotations[nameIndex];
                    }
                    ret = this.typePatterns[patternIndex].matches(t, kind);
                }
                finally {
                    t.temporaryAnnotationTypes = null;
                }
                ++patternIndex;
                ++nameIndex;
                if (ret == FuzzyBoolean.NO) {
                    return ret;
                }
                if (ret != FuzzyBoolean.MAYBE) continue;
                finalReturn = ret;
            }
            return finalReturn;
        }
        if (this.ellipsisCount == 1) {
            if (nameLength < patternLength - 1) {
                return FuzzyBoolean.NO;
            }
            FuzzyBoolean finalReturn = FuzzyBoolean.YES;
            while (patternIndex < patternLength) {
                TypePattern p;
                if ((p = this.typePatterns[patternIndex++]) == TypePattern.ELLIPSIS) {
                    nameIndex = nameLength - (patternLength - patternIndex);
                    continue;
                }
                ResolvedType t = types[nameIndex];
                FuzzyBoolean ret = null;
                try {
                    if (parameterAnnotations != null) {
                        t.temporaryAnnotationTypes = parameterAnnotations[nameIndex];
                    }
                    ret = p.matches(t, kind);
                }
                finally {
                    t.temporaryAnnotationTypes = null;
                }
                ++nameIndex;
                if (ret == FuzzyBoolean.NO) {
                    return ret;
                }
                if (ret != FuzzyBoolean.MAYBE) continue;
                finalReturn = ret;
            }
            return finalReturn;
        }
        FuzzyBoolean b = TypePatternList.outOfStar(this.typePatterns, types, 0, 0, patternLength - this.ellipsisCount, nameLength, this.ellipsisCount, kind, parameterAnnotations);
        return b;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FuzzyBoolean outOfStar(TypePattern[] pattern, ResolvedType[] target, int pi, int ti, int pLeft, int tLeft, int starsLeft, TypePattern.MatchKind kind, ResolvedType[][] parameterAnnotations) {
        if (pLeft > tLeft) {
            return FuzzyBoolean.NO;
        }
        FuzzyBoolean finalReturn = FuzzyBoolean.YES;
        while (tLeft != 0) {
            if (pLeft == 0) {
                if (starsLeft > 0) {
                    return finalReturn;
                }
                return FuzzyBoolean.NO;
            }
            if (pattern[pi] == TypePattern.ELLIPSIS) {
                return TypePatternList.inStar(pattern, target, pi + 1, ti, pLeft, tLeft, starsLeft - 1, kind, parameterAnnotations);
            }
            FuzzyBoolean ret = null;
            try {
                if (parameterAnnotations != null) {
                    target[ti].temporaryAnnotationTypes = parameterAnnotations[ti];
                }
                ret = pattern[pi].matches(target[ti], kind);
            }
            finally {
                target[ti].temporaryAnnotationTypes = null;
            }
            if (ret == FuzzyBoolean.NO) {
                return ret;
            }
            if (ret == FuzzyBoolean.MAYBE) {
                finalReturn = ret;
            }
            ++pi;
            ++ti;
            --pLeft;
            --tLeft;
        }
        return finalReturn;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FuzzyBoolean inStar(TypePattern[] pattern, ResolvedType[] target, int pi, int ti, int pLeft, int tLeft, int starsLeft, TypePattern.MatchKind kind, ResolvedType[][] parameterAnnotations) {
        TypePattern patternChar = pattern[pi];
        while (patternChar == TypePattern.ELLIPSIS) {
            --starsLeft;
            patternChar = pattern[++pi];
        }
        while (pLeft <= tLeft) {
            FuzzyBoolean xx;
            FuzzyBoolean ff = null;
            try {
                if (parameterAnnotations != null) {
                    target[ti].temporaryAnnotationTypes = parameterAnnotations[ti];
                }
                ff = patternChar.matches(target[ti], kind);
            }
            finally {
                target[ti].temporaryAnnotationTypes = null;
            }
            if (ff.maybeTrue() && (xx = TypePatternList.outOfStar(pattern, target, pi + 1, ti + 1, pLeft - 1, tLeft - 1, starsLeft, kind, parameterAnnotations)).maybeTrue()) {
                return ff.and(xx);
            }
            ++ti;
            --tLeft;
        }
        return FuzzyBoolean.NO;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FuzzyBoolean matches(ResolvableTypeList types, TypePattern.MatchKind kind, ResolvedType[][] parameterAnnotations) {
        int nameLength = types.length;
        int patternLength = this.typePatterns.length;
        int nameIndex = 0;
        int patternIndex = 0;
        if (this.ellipsisCount == 0) {
            if (nameLength != patternLength) {
                return FuzzyBoolean.NO;
            }
            FuzzyBoolean finalReturn = FuzzyBoolean.YES;
            while (patternIndex < patternLength) {
                ResolvedType t = types.getResolved(nameIndex);
                FuzzyBoolean ret = null;
                try {
                    if (parameterAnnotations != null) {
                        t.temporaryAnnotationTypes = parameterAnnotations[nameIndex];
                    }
                    ret = this.typePatterns[patternIndex].matches(t, kind);
                }
                finally {
                    t.temporaryAnnotationTypes = null;
                }
                ++patternIndex;
                ++nameIndex;
                if (ret == FuzzyBoolean.NO) {
                    return ret;
                }
                if (ret != FuzzyBoolean.MAYBE) continue;
                finalReturn = ret;
            }
            return finalReturn;
        }
        if (this.ellipsisCount == 1) {
            if (nameLength < patternLength - 1) {
                return FuzzyBoolean.NO;
            }
            FuzzyBoolean finalReturn = FuzzyBoolean.YES;
            while (patternIndex < patternLength) {
                TypePattern p;
                if ((p = this.typePatterns[patternIndex++]) == TypePattern.ELLIPSIS) {
                    nameIndex = nameLength - (patternLength - patternIndex);
                    continue;
                }
                ResolvedType t = types.getResolved(nameIndex);
                FuzzyBoolean ret = null;
                try {
                    if (parameterAnnotations != null) {
                        t.temporaryAnnotationTypes = parameterAnnotations[nameIndex];
                    }
                    ret = p.matches(t, kind);
                }
                finally {
                    t.temporaryAnnotationTypes = null;
                }
                ++nameIndex;
                if (ret == FuzzyBoolean.NO) {
                    return ret;
                }
                if (ret != FuzzyBoolean.MAYBE) continue;
                finalReturn = ret;
            }
            return finalReturn;
        }
        FuzzyBoolean b = TypePatternList.outOfStar(this.typePatterns, types, 0, 0, patternLength - this.ellipsisCount, nameLength, this.ellipsisCount, kind, parameterAnnotations);
        return b;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FuzzyBoolean outOfStar(TypePattern[] pattern, ResolvableTypeList target, int pi, int ti, int pLeft, int tLeft, int starsLeft, TypePattern.MatchKind kind, ResolvedType[][] parameterAnnotations) {
        if (pLeft > tLeft) {
            return FuzzyBoolean.NO;
        }
        FuzzyBoolean finalReturn = FuzzyBoolean.YES;
        while (tLeft != 0) {
            if (pLeft == 0) {
                if (starsLeft > 0) {
                    return finalReturn;
                }
                return FuzzyBoolean.NO;
            }
            if (pattern[pi] == TypePattern.ELLIPSIS) {
                return TypePatternList.inStar(pattern, target, pi + 1, ti, pLeft, tLeft, starsLeft - 1, kind, parameterAnnotations);
            }
            FuzzyBoolean ret = null;
            ResolvedType type = target.getResolved(ti);
            try {
                if (parameterAnnotations != null) {
                    type.temporaryAnnotationTypes = parameterAnnotations[ti];
                }
                ret = pattern[pi].matches(type, kind);
            }
            finally {
                type.temporaryAnnotationTypes = null;
            }
            if (ret == FuzzyBoolean.NO) {
                return ret;
            }
            if (ret == FuzzyBoolean.MAYBE) {
                finalReturn = ret;
            }
            ++pi;
            ++ti;
            --pLeft;
            --tLeft;
        }
        return finalReturn;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FuzzyBoolean inStar(TypePattern[] pattern, ResolvableTypeList target, int pi, int ti, int pLeft, int tLeft, int starsLeft, TypePattern.MatchKind kind, ResolvedType[][] parameterAnnotations) {
        TypePattern patternChar = pattern[pi];
        while (patternChar == TypePattern.ELLIPSIS) {
            --starsLeft;
            patternChar = pattern[++pi];
        }
        while (pLeft <= tLeft) {
            FuzzyBoolean xx;
            ResolvedType type = target.getResolved(ti);
            FuzzyBoolean ff = null;
            try {
                if (parameterAnnotations != null) {
                    type.temporaryAnnotationTypes = parameterAnnotations[ti];
                }
                ff = patternChar.matches(type, kind);
            }
            finally {
                type.temporaryAnnotationTypes = null;
            }
            if (ff.maybeTrue() && (xx = TypePatternList.outOfStar(pattern, target, pi + 1, ti + 1, pLeft - 1, tLeft - 1, starsLeft, kind, parameterAnnotations)).maybeTrue()) {
                return ff.and(xx);
            }
            ++ti;
            --tLeft;
        }
        return FuzzyBoolean.NO;
    }

    public TypePatternList parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        TypePattern[] parameterizedPatterns = new TypePattern[this.typePatterns.length];
        for (int i = 0; i < parameterizedPatterns.length; ++i) {
            parameterizedPatterns[i] = this.typePatterns[i].parameterizeWith(typeVariableMap, w);
        }
        return new TypePatternList(parameterizedPatterns);
    }

    public TypePatternList resolveBindings(IScope scope, Bindings bindings, boolean allowBinding, boolean requireExactType) {
        for (int i = 0; i < this.typePatterns.length; ++i) {
            TypePattern p = this.typePatterns[i];
            if (p == null) continue;
            this.typePatterns[i] = this.typePatterns[i].resolveBindings(scope, bindings, allowBinding, requireExactType);
        }
        return this;
    }

    public TypePatternList resolveReferences(IntMap bindings) {
        int len = this.typePatterns.length;
        TypePattern[] ret = new TypePattern[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = this.typePatterns[i].remapAdviceFormals(bindings);
        }
        return new TypePatternList(ret);
    }

    public void postRead(ResolvedType enclosingType) {
        for (int i = 0; i < this.typePatterns.length; ++i) {
            TypePattern p = this.typePatterns[i];
            p.postRead(enclosingType);
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof TypePatternList)) {
            return false;
        }
        TypePatternList o = (TypePatternList)other;
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

    public static TypePatternList read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        int len = s.readShort();
        TypePattern[] arguments = new TypePattern[len];
        for (int i = 0; i < len; ++i) {
            arguments[i] = TypePattern.read(s, context);
        }
        TypePatternList ret = new TypePatternList(arguments);
        if (!s.isAtLeast169()) {
            ret.readLocation(context, s);
        }
        return ret;
    }

    @Override
    public int getEnd() {
        throw new IllegalStateException();
    }

    @Override
    public ISourceContext getSourceContext() {
        throw new IllegalStateException();
    }

    @Override
    public ISourceLocation getSourceLocation() {
        throw new IllegalStateException();
    }

    @Override
    public int getStart() {
        throw new IllegalStateException();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeShort(this.typePatterns.length);
        for (int i = 0; i < this.typePatterns.length; ++i) {
            this.typePatterns[i].write(s);
        }
    }

    public TypePattern[] getTypePatterns() {
        return this.typePatterns;
    }

    public List<UnresolvedType> getExactTypes() {
        ArrayList<UnresolvedType> ret = new ArrayList<UnresolvedType>();
        for (int i = 0; i < this.typePatterns.length; ++i) {
            UnresolvedType t = this.typePatterns[i].getExactType();
            if (ResolvedType.isMissing(t)) continue;
            ret.add(t);
        }
        return ret;
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

    public boolean areAllExactWithNoSubtypesAllowed() {
        for (int i = 0; i < this.typePatterns.length; ++i) {
            TypePattern array_element = this.typePatterns[i];
            if (!(array_element instanceof ExactTypePattern)) {
                return false;
            }
            ExactTypePattern etp = (ExactTypePattern)array_element;
            if (!etp.isIncludeSubtypes()) continue;
            return false;
        }
        return true;
    }

    public String[] maybeGetCleanNames() {
        String[] theParamNames = new String[this.typePatterns.length];
        for (int i = 0; i < this.typePatterns.length; ++i) {
            TypePattern string = this.typePatterns[i];
            if (!(string instanceof ExactTypePattern)) {
                return null;
            }
            theParamNames[i] = ((ExactTypePattern)string).getExactType().getName();
        }
        return theParamNames;
    }
}

