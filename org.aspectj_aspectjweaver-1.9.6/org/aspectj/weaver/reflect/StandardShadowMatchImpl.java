/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.And;
import org.aspectj.weaver.ast.Call;
import org.aspectj.weaver.ast.FieldGetCall;
import org.aspectj.weaver.ast.HasAnnotation;
import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Instanceof;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Not;
import org.aspectj.weaver.ast.Or;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.internal.tools.MatchingContextBasedTest;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.reflect.JoinPointMatchImpl;
import org.aspectj.weaver.reflect.ReflectionVar;
import org.aspectj.weaver.tools.DefaultMatchingContext;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.ShadowMatch;

public class StandardShadowMatchImpl
implements ShadowMatch {
    private FuzzyBoolean match;
    private ExposedState state;
    private Test residualTest;
    private PointcutParameter[] params;
    private ResolvedMember withinCode;
    private ResolvedMember subject;
    private ResolvedType withinType;
    private MatchingContext matchContext = new DefaultMatchingContext();

    public StandardShadowMatchImpl(FuzzyBoolean match, Test test, ExposedState state, PointcutParameter[] params) {
        this.match = match;
        this.residualTest = test;
        this.state = state;
        this.params = params;
    }

    public void setWithinCode(ResolvedMember aMember) {
        this.withinCode = aMember;
    }

    public void setSubject(ResolvedMember aMember) {
        this.subject = aMember;
    }

    public void setWithinType(ResolvedType aClass) {
        this.withinType = aClass;
    }

    @Override
    public boolean alwaysMatches() {
        return this.match.alwaysTrue();
    }

    @Override
    public boolean maybeMatches() {
        return this.match.maybeTrue();
    }

    @Override
    public boolean neverMatches() {
        return this.match.alwaysFalse();
    }

    @Override
    public JoinPointMatch matchesJoinPoint(Object thisObject, Object targetObject, Object[] args) {
        if (this.neverMatches()) {
            return JoinPointMatchImpl.NO_MATCH;
        }
        if (new RuntimeTestEvaluator(this.residualTest, thisObject, targetObject, args, this.matchContext).matches()) {
            return new JoinPointMatchImpl(this.getPointcutParameters(thisObject, targetObject, args));
        }
        return JoinPointMatchImpl.NO_MATCH;
    }

    @Override
    public void setMatchingContext(MatchingContext aMatchContext) {
        this.matchContext = aMatchContext;
    }

    private PointcutParameter[] getPointcutParameters(Object thisObject, Object targetObject, Object[] args) {
        return null;
    }

    private static class RuntimeTestEvaluator
    implements ITestVisitor {
        private boolean matches = true;
        private final Test test;
        private final Object thisObject;
        private final Object targetObject;
        private final Object[] args;
        private final MatchingContext matchContext;

        public RuntimeTestEvaluator(Test aTest, Object thisObject, Object targetObject, Object[] args, MatchingContext context) {
            this.test = aTest;
            this.thisObject = thisObject;
            this.targetObject = targetObject;
            this.args = args;
            this.matchContext = context;
        }

        public boolean matches() {
            this.test.accept(this);
            return this.matches;
        }

        @Override
        public void visit(And e) {
            boolean leftMatches = new RuntimeTestEvaluator(e.getLeft(), this.thisObject, this.targetObject, this.args, this.matchContext).matches();
            this.matches = !leftMatches ? false : new RuntimeTestEvaluator(e.getRight(), this.thisObject, this.targetObject, this.args, this.matchContext).matches();
        }

        @Override
        public void visit(Instanceof i) {
            ReflectionVar v = (ReflectionVar)i.getVar();
            Object value = v.getBindingAtJoinPoint(this.thisObject, this.targetObject, this.args);
            World world = v.getType().getWorld();
            ResolvedType desiredType = i.getType().resolve(world);
            ResolvedType actualType = world.resolve(value.getClass().getName());
            this.matches = desiredType.isAssignableFrom(actualType);
        }

        @Override
        public void visit(MatchingContextBasedTest matchingContextTest) {
            this.matches = matchingContextTest.matches(this.matchContext);
        }

        @Override
        public void visit(Not not) {
            this.matches = !new RuntimeTestEvaluator(not.getBody(), this.thisObject, this.targetObject, this.args, this.matchContext).matches();
        }

        @Override
        public void visit(Or or) {
            boolean leftMatches = new RuntimeTestEvaluator(or.getLeft(), this.thisObject, this.targetObject, this.args, this.matchContext).matches();
            this.matches = leftMatches ? true : new RuntimeTestEvaluator(or.getRight(), this.thisObject, this.targetObject, this.args, this.matchContext).matches();
        }

        @Override
        public void visit(Literal literal) {
            this.matches = literal != Literal.FALSE;
        }

        @Override
        public void visit(Call call) {
            throw new UnsupportedOperationException("Can't evaluate call test at runtime");
        }

        @Override
        public void visit(FieldGetCall fieldGetCall) {
            throw new UnsupportedOperationException("Can't evaluate fieldGetCall test at runtime");
        }

        @Override
        public void visit(HasAnnotation hasAnnotation) {
            ReflectionVar v = (ReflectionVar)hasAnnotation.getVar();
            Object value = v.getBindingAtJoinPoint(this.thisObject, this.targetObject, this.args);
            World world = v.getType().getWorld();
            ResolvedType actualVarType = world.resolve(value.getClass().getName());
            ResolvedType requiredAnnotationType = hasAnnotation.getAnnotationType().resolve(world);
            this.matches = actualVarType.hasAnnotation(requiredAnnotationType);
        }
    }
}

