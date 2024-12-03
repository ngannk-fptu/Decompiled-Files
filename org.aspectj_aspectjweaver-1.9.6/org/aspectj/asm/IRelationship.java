/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;

public interface IRelationship
extends Serializable {
    public String getName();

    public Kind getKind();

    public void addTarget(String var1);

    public List<String> getTargets();

    public String getSourceHandle();

    public boolean hasRuntimeTest();

    public boolean isAffects();

    public static class Kind
    implements Serializable {
        private static final long serialVersionUID = -2691351740214705220L;
        public static final Kind DECLARE_WARNING = new Kind("declare warning");
        public static final Kind DECLARE_ERROR = new Kind("declare error");
        public static final Kind ADVICE_AROUND = new Kind("around advice");
        public static final Kind ADVICE_AFTERRETURNING = new Kind("after returning advice");
        public static final Kind ADVICE_AFTERTHROWING = new Kind("after throwing advice");
        public static final Kind ADVICE_AFTER = new Kind("after advice");
        public static final Kind ADVICE_BEFORE = new Kind("before advice");
        public static final Kind ADVICE = new Kind("advice");
        public static final Kind DECLARE = new Kind("declare");
        public static final Kind DECLARE_INTER_TYPE = new Kind("inter-type declaration");
        public static final Kind USES_POINTCUT = new Kind("uses pointcut");
        public static final Kind DECLARE_SOFT = new Kind("declare soft");
        public static final Kind[] ALL = new Kind[]{DECLARE_WARNING, DECLARE_ERROR, ADVICE_AROUND, ADVICE_AFTERRETURNING, ADVICE_AFTERTHROWING, ADVICE_AFTER, ADVICE_BEFORE, ADVICE, DECLARE, DECLARE_INTER_TYPE, USES_POINTCUT, DECLARE_SOFT};
        private final String name;
        private static int nextOrdinal = 0;
        private final int ordinal = nextOrdinal++;

        public boolean isDeclareKind() {
            return this == DECLARE_WARNING || this == DECLARE_ERROR || this == DECLARE || this == DECLARE_INTER_TYPE || this == DECLARE_SOFT;
        }

        public String getName() {
            return this.name;
        }

        public static Kind getKindFor(String stringFormOfRelationshipKind) {
            for (int i = 0; i < ALL.length; ++i) {
                if (!Kind.ALL[i].name.equals(stringFormOfRelationshipKind)) continue;
                return ALL[i];
            }
            return null;
        }

        private Kind(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        private Object readResolve() throws ObjectStreamException {
            return ALL[this.ordinal];
        }
    }
}

