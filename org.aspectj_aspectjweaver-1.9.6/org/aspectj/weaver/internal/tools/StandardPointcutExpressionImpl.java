/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.internal.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.ResolvedMember;
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
import org.aspectj.weaver.reflect.StandardShadow;
import org.aspectj.weaver.reflect.StandardShadowMatchImpl;
import org.aspectj.weaver.tools.DefaultMatchingContext;
import org.aspectj.weaver.tools.MatchingContext;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.ShadowMatch;
import org.aspectj.weaver.tools.StandardPointcutExpression;

public class StandardPointcutExpressionImpl
implements StandardPointcutExpression {
    private World world;
    private Pointcut pointcut;
    private String expression;
    private PointcutParameter[] parameters;
    private MatchingContext matchContext = new DefaultMatchingContext();

    public StandardPointcutExpressionImpl(Pointcut pointcut, String expression, PointcutParameter[] params, World inWorld) {
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
        ReflectionFastMatchInfo info = new ReflectionFastMatchInfo(matchType, null, this.matchContext, this.world);
        return this.pointcut.fastMatch(info).maybeTrue();
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
    public ShadowMatch matchesMethodExecution(ResolvedMember aMethod) {
        return this.matchesExecution(aMethod);
    }

    public ShadowMatch matchesConstructorExecution(Constructor aConstructor) {
        return null;
    }

    private ShadowMatch matchesExecution(ResolvedMember aMember) {
        Shadow s = StandardShadow.makeExecutionShadow(this.world, aMember, this.matchContext);
        StandardShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aMember);
        sm.setWithinCode(null);
        sm.setWithinType((ResolvedType)aMember.getDeclaringType());
        return sm;
    }

    @Override
    public ShadowMatch matchesStaticInitialization(ResolvedType aType) {
        Shadow s = StandardShadow.makeStaticInitializationShadow(this.world, aType, this.matchContext);
        StandardShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(null);
        sm.setWithinCode(null);
        sm.setWithinType(aType);
        return sm;
    }

    @Override
    public ShadowMatch matchesMethodCall(ResolvedMember aMethod, ResolvedMember withinCode) {
        Shadow s = StandardShadow.makeCallShadow(this.world, aMethod, withinCode, this.matchContext);
        StandardShadowMatchImpl sm = this.getShadowMatch(s);
        sm.setSubject(aMethod);
        sm.setWithinCode(withinCode);
        sm.setWithinType((ResolvedType)withinCode.getDeclaringType());
        return sm;
    }

    private StandardShadowMatchImpl getShadowMatch(Shadow forShadow) {
        FuzzyBoolean match = this.pointcut.match(forShadow);
        Test residueTest = Literal.TRUE;
        ExposedState state = this.getExposedState();
        if (match.maybeTrue()) {
            residueTest = this.pointcut.findResidue(forShadow, state);
        }
        StandardShadowMatchImpl sm = new StandardShadowMatchImpl(match, residueTest, state, this.parameters);
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

