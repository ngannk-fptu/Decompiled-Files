/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.internal.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.AbstractPatternNodeVisitor;
import org.aspectj.weaver.patterns.AnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.NotAnnotationTypePattern;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.WithinAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinCodeAnnotationPointcut;
import org.aspectj.weaver.reflect.ReflectionFastMatchInfo;
import org.aspectj.weaver.reflect.ReflectionShadow;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.reflect.ShadowMatchImpl;
import org.aspectj.weaver.tools.DefaultMatchingContext;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.ShadowMatch;

public class PointcutExpressionImpl
implements PointcutExpression {
    private static final boolean MATCH_INFO = false;
    private World world;
    private Pointcut pointcut;
    private String expression;
    private PointcutParameter[] parameters;
    private MatchingContext matchContext = new DefaultMatchingContext();

    public PointcutExpressionImpl(Pointcut pointcut, String expression, PointcutParameter[] params, World inWorld) {
        this.pointcut = pointcut;
        this.expression = expression;
        this.world = inWorld;
        this.parameters = params;
        if (this.parameters == null) {
            this.parameters = new PointcutParameter[0];
        }
    }

    public Pointcut getUnderlyingPointcut() {
        return this.pointcut;
    }

    @Override
    public void setMatchingContext(MatchingContext aMatchContext) {
        this.matchContext = aMatchContext;
    }

    @Override
    public boolean couldMatchJoinPointsInType(Class aClass) {
        ResolvedType matchType = this.world.resolve(aClass.getName());
        if (matchType.isMissing() && this.world instanceof ReflectionWorld) {
            matchType = ((ReflectionWorld)this.world).resolveUsingClass(aClass);
        }
        ReflectionFastMatchInfo info = new ReflectionFastMatchInfo(matchType, null, this.matchContext, this.world);
        boolean couldMatch = this.pointcut.fastMatch(info).maybeTrue();
        return couldMatch;
    }

    @Override
    public boolean mayNeedDynamicTest() {
        HasPossibleDynamicContentVisitor visitor = new HasPossibleDynamicContentVisitor();
        this.pointcut.traverse(visitor, null);
        return visitor.hasDynamicContent();
    }

    private ExposedState getExposedState() {
        return new ExposedState(this.parameters.length);
    }

    @Override
    public ShadowMatch matchesMethodExecution(Method aMethod) {
        ShadowMatch match = this.matchesExecution(aMethod);
        return match;
    }

    @Override
    public ShadowMatch matchesConstructorExecution(Constructor aConstructor) {
        ShadowMatch match = this.matchesExecution(aConstructor);
        return match;
    }

    private ShadowMatch matchesExecution(Member aMember) {
        Shadow s = ReflectionShadow.makeExecutionShadow(this.world, aMember, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aMember);
        sm.setWithinCode(null);
        sm.setWithinType(aMember.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesStaticInitialization(Class aClass) {
        Shadow s = ReflectionShadow.makeStaticInitializationShadow(this.world, aClass, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(null);
        sm.setWithinCode(null);
        sm.setWithinType(aClass);
        return sm;
    }

    @Override
    public ShadowMatch matchesAdviceExecution(Method aMethod) {
        Shadow s = ReflectionShadow.makeAdviceExecutionShadow(this.world, aMethod, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aMethod);
        sm.setWithinCode(null);
        sm.setWithinType(aMethod.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesInitialization(Constructor aConstructor) {
        Shadow s = ReflectionShadow.makeInitializationShadow(this.world, aConstructor, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aConstructor);
        sm.setWithinCode(null);
        sm.setWithinType(aConstructor.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesPreInitialization(Constructor aConstructor) {
        Shadow s = ReflectionShadow.makePreInitializationShadow(this.world, aConstructor, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aConstructor);
        sm.setWithinCode(null);
        sm.setWithinType(aConstructor.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesMethodCall(Method aMethod, Member withinCode) {
        Shadow s = ReflectionShadow.makeCallShadow(this.world, (Member)aMethod, withinCode, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aMethod);
        sm.setWithinCode(withinCode);
        sm.setWithinType(withinCode.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesMethodCall(Method aMethod, Class callerType) {
        Shadow s = ReflectionShadow.makeCallShadow(this.world, (Member)aMethod, callerType, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aMethod);
        sm.setWithinCode(null);
        sm.setWithinType(callerType);
        return sm;
    }

    @Override
    public ShadowMatch matchesConstructorCall(Constructor aConstructor, Class callerType) {
        Shadow s = ReflectionShadow.makeCallShadow(this.world, (Member)aConstructor, callerType, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aConstructor);
        sm.setWithinCode(null);
        sm.setWithinType(callerType);
        return sm;
    }

    @Override
    public ShadowMatch matchesConstructorCall(Constructor aConstructor, Member withinCode) {
        Shadow s = ReflectionShadow.makeCallShadow(this.world, (Member)aConstructor, withinCode, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aConstructor);
        sm.setWithinCode(withinCode);
        sm.setWithinType(withinCode.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesHandler(Class exceptionType, Class handlingType) {
        Shadow s = ReflectionShadow.makeHandlerShadow(this.world, exceptionType, handlingType, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(null);
        sm.setWithinCode(null);
        sm.setWithinType(handlingType);
        return sm;
    }

    @Override
    public ShadowMatch matchesHandler(Class exceptionType, Member withinCode) {
        Shadow s = ReflectionShadow.makeHandlerShadow(this.world, exceptionType, withinCode, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(null);
        sm.setWithinCode(withinCode);
        sm.setWithinType(withinCode.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesFieldGet(Field aField, Class withinType) {
        Shadow s = ReflectionShadow.makeFieldGetShadow(this.world, aField, withinType, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aField);
        sm.setWithinCode(null);
        sm.setWithinType(withinType);
        return sm;
    }

    @Override
    public ShadowMatch matchesFieldGet(Field aField, Member withinCode) {
        Shadow s = ReflectionShadow.makeFieldGetShadow(this.world, aField, withinCode, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aField);
        sm.setWithinCode(withinCode);
        sm.setWithinType(withinCode.getDeclaringClass());
        return sm;
    }

    @Override
    public ShadowMatch matchesFieldSet(Field aField, Class withinType) {
        Shadow s = ReflectionShadow.makeFieldSetShadow(this.world, aField, withinType, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aField);
        sm.setWithinCode(null);
        sm.setWithinType(withinType);
        return sm;
    }

    @Override
    public ShadowMatch matchesFieldSet(Field aField, Member withinCode) {
        Shadow s = ReflectionShadow.makeFieldSetShadow(this.world, aField, withinCode, this.matchContext);
        ShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aField);
        sm.setWithinCode(withinCode);
        sm.setWithinType(withinCode.getDeclaringClass());
        return sm;
    }

    private ShadowMatchImpl getShadowMatch(Shadow forShadow) {
        FuzzyBoolean match = this.pointcut.match(forShadow);
        Test residueTest = Literal.TRUE;
        ExposedState state = this.getExposedState();
        if (match.maybeTrue()) {
            residueTest = this.pointcut.findResidue(forShadow, state);
        }
        ShadowMatchImpl sm = new ShadowMatchImpl(match, residueTest, state, this.parameters);
        sm.setMatchingContext(this.matchContext);
        return sm;
    }

    @Override
    public String getPointcutExpression() {
        return this.expression;
    }

    public static class Handler
    implements Member {
        private Class decClass;
        private Class exType;

        public Handler(Class decClass, Class exType) {
            this.decClass = decClass;
            this.exType = exType;
        }

        @Override
        public int getModifiers() {
            return 0;
        }

        public Class getDeclaringClass() {
            return this.decClass;
        }

        @Override
        public String getName() {
            return null;
        }

        public Class getHandledExceptionType() {
            return this.exType;
        }

        @Override
        public boolean isSynthetic() {
            return false;
        }
    }

    private static class HasPossibleDynamicContentVisitor
    extends AbstractPatternNodeVisitor {
        private boolean hasDynamicContent = false;

        private HasPossibleDynamicContentVisitor() {
        }

        public boolean hasDynamicContent() {
            return this.hasDynamicContent;
        }

        @Override
        public Object visit(WithinAnnotationPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(WithinCodeAnnotationPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(AnnotationPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(ArgsAnnotationPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(ArgsPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(CflowPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(IfPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(NotAnnotationTypePattern node, Object data) {
            return node.getNegatedPattern().accept(this, data);
        }

        @Override
        public Object visit(NotPointcut node, Object data) {
            return node.getNegatedPointcut().accept(this, data);
        }

        @Override
        public Object visit(ThisOrTargetAnnotationPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }

        @Override
        public Object visit(ThisOrTargetPointcut node, Object data) {
            this.hasDynamicContent = true;
            return null;
        }
    }
}

