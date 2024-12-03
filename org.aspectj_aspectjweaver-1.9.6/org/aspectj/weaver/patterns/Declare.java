/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.World;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclareParentsMixin;
import org.aspectj.weaver.patterns.DeclarePrecedence;
import org.aspectj.weaver.patterns.DeclareSoft;
import org.aspectj.weaver.patterns.DeclareTypeErrorOrWarning;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNode;

public abstract class Declare
extends PatternNode {
    public static final byte ERROR_OR_WARNING = 1;
    public static final byte PARENTS = 2;
    public static final byte SOFT = 3;
    public static final byte DOMINATES = 4;
    public static final byte ANNOTATION = 5;
    public static final byte PARENTSMIXIN = 6;
    public static final byte TYPE_ERROR_OR_WARNING = 7;
    private ResolvedType declaringType;

    public static Declare read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        byte kind = s.readByte();
        switch (kind) {
            case 1: {
                return DeclareErrorOrWarning.read(s, context);
            }
            case 4: {
                return DeclarePrecedence.read(s, context);
            }
            case 2: {
                return DeclareParents.read(s, context);
            }
            case 3: {
                return DeclareSoft.read(s, context);
            }
            case 5: {
                return DeclareAnnotation.read(s, context);
            }
            case 6: {
                return DeclareParentsMixin.read(s, context);
            }
            case 7: {
                return DeclareTypeErrorOrWarning.read(s, context);
            }
        }
        throw new RuntimeException("unimplemented");
    }

    public abstract void resolve(IScope var1);

    public abstract Declare parameterizeWith(Map<String, UnresolvedType> var1, World var2);

    public abstract boolean isAdviceLike();

    public abstract String getNameSuffix();

    public void setDeclaringType(ResolvedType aType) {
        this.declaringType = aType;
    }

    public ResolvedType getDeclaringType() {
        return this.declaringType;
    }
}

