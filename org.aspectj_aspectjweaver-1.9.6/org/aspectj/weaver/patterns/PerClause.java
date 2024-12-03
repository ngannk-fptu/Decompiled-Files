/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import org.aspectj.util.TypeSafeEnum;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.patterns.PerCflow;
import org.aspectj.weaver.patterns.PerFromSuper;
import org.aspectj.weaver.patterns.PerObject;
import org.aspectj.weaver.patterns.PerSingleton;
import org.aspectj.weaver.patterns.PerTypeWithin;
import org.aspectj.weaver.patterns.Pointcut;

public abstract class PerClause
extends Pointcut {
    protected ResolvedType inAspect;
    public static final Kind SINGLETON = new Kind("issingleton", 1);
    public static final Kind PERCFLOW = new Kind("percflow", 2);
    public static final Kind PEROBJECT = new Kind("perobject", 3);
    public static final Kind FROMSUPER = new Kind("fromsuper", 4);
    public static final Kind PERTYPEWITHIN = new Kind("pertypewithin", 5);

    public static PerClause readPerClause(VersionedDataInputStream s, ISourceContext context) throws IOException {
        Kind kind = Kind.read(s);
        if (kind == SINGLETON) {
            return PerSingleton.readPerClause(s, context);
        }
        if (kind == PERCFLOW) {
            return PerCflow.readPerClause(s, context);
        }
        if (kind == PEROBJECT) {
            return PerObject.readPerClause(s, context);
        }
        if (kind == FROMSUPER) {
            return PerFromSuper.readPerClause(s, context);
        }
        if (kind == PERTYPEWITHIN) {
            return PerTypeWithin.readPerClause(s, context);
        }
        throw new BCException("unknown kind: " + kind);
    }

    @Override
    public final Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        throw new RuntimeException("unimplemented: wrong concretize");
    }

    public abstract PerClause concretize(ResolvedType var1);

    public abstract Kind getKind();

    public abstract String toDeclarationString();

    public static class KindAnnotationPrefix
    extends TypeSafeEnum {
        public static final KindAnnotationPrefix PERCFLOW = new KindAnnotationPrefix("percflow(", 1);
        public static final KindAnnotationPrefix PERCFLOWBELOW = new KindAnnotationPrefix("percflowbelow(", 2);
        public static final KindAnnotationPrefix PERTHIS = new KindAnnotationPrefix("perthis(", 3);
        public static final KindAnnotationPrefix PERTARGET = new KindAnnotationPrefix("pertarget(", 4);
        public static final KindAnnotationPrefix PERTYPEWITHIN = new KindAnnotationPrefix("pertypewithin(", 5);

        private KindAnnotationPrefix(String name, int key) {
            super(name, key);
        }

        public String extractPointcut(String perClause) {
            int from = this.getName().length();
            int to = perClause.length() - 1;
            if (!perClause.startsWith(this.getName()) || !perClause.endsWith(")") || from > perClause.length()) {
                throw new RuntimeException("cannot read perclause " + perClause);
            }
            return perClause.substring(from, to);
        }
    }

    public static class Kind
    extends TypeSafeEnum {
        public Kind(String name, int key) {
            super(name, key);
        }

        public static Kind read(VersionedDataInputStream s) throws IOException {
            byte key = s.readByte();
            switch (key) {
                case 1: {
                    return SINGLETON;
                }
                case 2: {
                    return PERCFLOW;
                }
                case 3: {
                    return PEROBJECT;
                }
                case 4: {
                    return FROMSUPER;
                }
                case 5: {
                    return PERTYPEWITHIN;
                }
            }
            throw new BCException("weird kind " + key);
        }
    }
}

