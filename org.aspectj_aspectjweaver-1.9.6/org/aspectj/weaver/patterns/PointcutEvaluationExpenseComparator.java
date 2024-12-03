/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.Comparator;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.AnnotationPointcut;
import org.aspectj.weaver.patterns.AnyTypePattern;
import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.WithinAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinCodeAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;

public class PointcutEvaluationExpenseComparator
implements Comparator<Pointcut> {
    private static final int MATCHES_NOTHING = -1;
    private static final int WITHIN = 1;
    private static final int ATWITHIN = 2;
    private static final int STATICINIT = 3;
    private static final int ADVICEEXECUTION = 4;
    private static final int HANDLER = 5;
    private static final int GET_OR_SET = 6;
    private static final int WITHINCODE = 7;
    private static final int ATWITHINCODE = 8;
    private static final int EXE_INIT_PREINIT = 9;
    private static final int CALL_WITH_DECLARING_TYPE = 10;
    private static final int THIS_OR_TARGET = 11;
    private static final int CALL_WITHOUT_DECLARING_TYPE = 12;
    private static final int ANNOTATION = 13;
    private static final int AT_THIS_OR_TARGET = 14;
    private static final int ARGS = 15;
    private static final int AT_ARGS = 16;
    private static final int CFLOW = 17;
    private static final int IF = 18;
    private static final int OTHER = 20;

    @Override
    public int compare(Pointcut p1, Pointcut p2) {
        if (p1.equals(p2)) {
            return 0;
        }
        int result = this.getScore(p1) - this.getScore(p2);
        if (result == 0) {
            int p2code;
            int p1code = p1.hashCode();
            if (p1code == (p2code = p2.hashCode())) {
                return 0;
            }
            if (p1code < p2code) {
                return -1;
            }
            return 1;
        }
        return result;
    }

    private int getScore(Pointcut p) {
        if (p.couldMatchKinds() == Shadow.NO_SHADOW_KINDS_BITS) {
            return -1;
        }
        if (p instanceof WithinPointcut) {
            return 1;
        }
        if (p instanceof WithinAnnotationPointcut) {
            return 2;
        }
        if (p instanceof KindedPointcut) {
            KindedPointcut kp = (KindedPointcut)p;
            Shadow.Kind kind = kp.getKind();
            if (kind == Shadow.AdviceExecution) {
                return 4;
            }
            if (kind == Shadow.ConstructorCall || kind == Shadow.MethodCall) {
                TypePattern declaringTypePattern = kp.getSignature().getDeclaringType();
                if (declaringTypePattern instanceof AnyTypePattern) {
                    return 12;
                }
                return 10;
            }
            if (kind == Shadow.ConstructorExecution || kind == Shadow.MethodExecution || kind == Shadow.Initialization || kind == Shadow.PreInitialization) {
                return 9;
            }
            if (kind == Shadow.ExceptionHandler) {
                return 5;
            }
            if (kind == Shadow.FieldGet || kind == Shadow.FieldSet) {
                return 6;
            }
            if (kind == Shadow.StaticInitialization) {
                return 3;
            }
            return 20;
        }
        if (p instanceof AnnotationPointcut) {
            return 13;
        }
        if (p instanceof ArgsPointcut) {
            return 15;
        }
        if (p instanceof ArgsAnnotationPointcut) {
            return 16;
        }
        if (p instanceof CflowPointcut || p instanceof ConcreteCflowPointcut) {
            return 17;
        }
        if (p instanceof HandlerPointcut) {
            return 5;
        }
        if (p instanceof IfPointcut) {
            return 18;
        }
        if (p instanceof ThisOrTargetPointcut) {
            return 11;
        }
        if (p instanceof ThisOrTargetAnnotationPointcut) {
            return 14;
        }
        if (p instanceof WithincodePointcut) {
            return 7;
        }
        if (p instanceof WithinCodeAnnotationPointcut) {
            return 8;
        }
        if (p instanceof NotPointcut) {
            return this.getScore(((NotPointcut)p).getNegatedPointcut());
        }
        if (p instanceof AndPointcut) {
            int rightScore;
            int leftScore = this.getScore(((AndPointcut)p).getLeft());
            if (leftScore < (rightScore = this.getScore(((AndPointcut)p).getRight()))) {
                return leftScore;
            }
            return rightScore;
        }
        if (p instanceof OrPointcut) {
            int rightScore;
            int leftScore = this.getScore(((OrPointcut)p).getLeft());
            if (leftScore > (rightScore = this.getScore(((OrPointcut)p).getRight()))) {
                return leftScore;
            }
            return rightScore;
        }
        return 20;
    }
}

