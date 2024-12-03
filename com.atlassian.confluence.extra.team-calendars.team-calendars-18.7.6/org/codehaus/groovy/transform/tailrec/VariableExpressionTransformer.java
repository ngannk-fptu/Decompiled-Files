/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class VariableExpressionTransformer
implements ExpressionTransformer,
GroovyObject {
    private Closure<Boolean> when;
    private Closure<VariableExpression> replaceWith;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public VariableExpressionTransformer() {
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Expression transform(Expression expr) {
        if (expr instanceof VariableExpression && DefaultTypeTransformation.booleanUnbox(this.when.call(new Object[]{expr}))) {
            VariableExpression newExpr = (VariableExpression)ScriptBytecodeAdapter.castToType(this.replaceWith.call(new Object[]{expr}), VariableExpression.class);
            newExpr.setSourcePosition(expr);
            newExpr.copyNodeMetaData(expr);
            return newExpr;
        }
        return expr.transformExpression(this);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != VariableExpressionTransformer.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public Closure<Boolean> getWhen() {
        return this.when;
    }

    public void setWhen(Closure<Boolean> closure) {
        this.when = closure;
    }

    public Closure<VariableExpression> getReplaceWith() {
        return this.replaceWith;
    }

    public void setReplaceWith(Closure<VariableExpression> closure) {
        this.replaceWith = closure;
    }
}

