/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.tailrec.InWhileLoopWrapper;

public class AstHelper
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public AstHelper() {
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static ExpressionStatement createVariableDefinition(String variableName, ClassNode variableType, Expression value, boolean variableShouldBeFinal) {
        VariableExpression newVariable = GeneralUtils.varX(variableName, variableType);
        if (variableShouldBeFinal) {
            newVariable.setModifiers(Modifier.FINAL);
        }
        return (ExpressionStatement)ScriptBytecodeAdapter.castToType(GeneralUtils.declS(newVariable, value), ExpressionStatement.class);
    }

    public static ExpressionStatement createVariableAlias(String aliasName, ClassNode variableType, String variableName) {
        return AstHelper.createVariableDefinition(aliasName, variableType, GeneralUtils.varX(variableName, variableType), true);
    }

    public static VariableExpression createVariableReference(Map variableSpec) {
        return GeneralUtils.varX(ShortTypeHandling.castToString(variableSpec.get("name")), (ClassNode)ScriptBytecodeAdapter.castToType(variableSpec.get("type"), ClassNode.class));
    }

    public static Statement recurStatement() {
        return new ContinueStatement(InWhileLoopWrapper.getLOOP_LABEL());
    }

    public static Statement recurByThrowStatement() {
        return new ThrowStatement(GeneralUtils.propX((Expression)GeneralUtils.classX(InWhileLoopWrapper.class), "LOOP_EXCEPTION"));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AstHelper.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public static ExpressionStatement createVariableDefinition(String variableName, ClassNode variableType, Expression value) {
        return AstHelper.createVariableDefinition(variableName, variableType, value, false);
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
}

