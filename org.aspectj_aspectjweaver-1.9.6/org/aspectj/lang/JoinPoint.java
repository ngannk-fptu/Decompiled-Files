/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

public interface JoinPoint {
    public static final String METHOD_EXECUTION = "method-execution";
    public static final String METHOD_CALL = "method-call";
    public static final String CONSTRUCTOR_EXECUTION = "constructor-execution";
    public static final String CONSTRUCTOR_CALL = "constructor-call";
    public static final String FIELD_GET = "field-get";
    public static final String FIELD_SET = "field-set";
    public static final String STATICINITIALIZATION = "staticinitialization";
    public static final String PREINITIALIZATION = "preinitialization";
    public static final String INITIALIZATION = "initialization";
    public static final String EXCEPTION_HANDLER = "exception-handler";
    public static final String SYNCHRONIZATION_LOCK = "lock";
    public static final String SYNCHRONIZATION_UNLOCK = "unlock";
    public static final String ADVICE_EXECUTION = "adviceexecution";

    public String toString();

    public String toShortString();

    public String toLongString();

    public Object getThis();

    public Object getTarget();

    public Object[] getArgs();

    public Signature getSignature();

    public SourceLocation getSourceLocation();

    public String getKind();

    public StaticPart getStaticPart();

    public static interface EnclosingStaticPart
    extends StaticPart {
    }

    public static interface StaticPart {
        public Signature getSignature();

        public SourceLocation getSourceLocation();

        public String getKind();

        public int getId();

        public String toString();

        public String toShortString();

        public String toLongString();
    }
}

