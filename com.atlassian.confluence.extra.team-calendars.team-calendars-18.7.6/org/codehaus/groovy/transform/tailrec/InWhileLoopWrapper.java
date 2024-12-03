/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.transform.tailrec.GotoRecurHereException;

public class InWhileLoopWrapper
implements GroovyObject {
    private static final String LOOP_LABEL = "_RECUR_HERE_";
    private static final GotoRecurHereException LOOP_EXCEPTION;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public InWhileLoopWrapper() {
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public void wrap(MethodNode method) {
        BlockStatement oldBody = (BlockStatement)ScriptBytecodeAdapter.asType(method.getCode(), BlockStatement.class);
        TryCatchStatement tryCatchStatement = new TryCatchStatement(oldBody, new EmptyStatement());
        tryCatchStatement.addCatch(new CatchStatement(new Parameter(ClassHelper.make(GotoRecurHereException.class), "ignore"), new ContinueStatement(InWhileLoopWrapper.getLOOP_LABEL())));
        WhileStatement whileLoop = new WhileStatement(new BooleanExpression(new ConstantExpression(true)), new BlockStatement(ScriptBytecodeAdapter.createList(new Object[]{tryCatchStatement}), new VariableScope(method.getVariableScope())));
        List<Statement> whileLoopStatements = ((BlockStatement)ScriptBytecodeAdapter.castToType(whileLoop.getLoopBlock(), BlockStatement.class)).getStatements();
        if (whileLoopStatements.size() > 0) {
            String string = LOOP_LABEL;
            DefaultGroovyMethods.getAt(whileLoopStatements, 0).setStatementLabel(string);
        }
        BlockStatement newBody = new BlockStatement(ScriptBytecodeAdapter.createList(new Object[0]), new VariableScope(method.getVariableScope()));
        newBody.addStatement(whileLoop);
        BlockStatement blockStatement = newBody;
        method.setCode(blockStatement);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != InWhileLoopWrapper.class) {
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

    static {
        GotoRecurHereException gotoRecurHereException;
        LOOP_EXCEPTION = gotoRecurHereException = new GotoRecurHereException();
    }

    public static String getLOOP_LABEL() {
        return LOOP_LABEL;
    }

    public static GotoRecurHereException getLOOP_EXCEPTION() {
        return LOOP_EXCEPTION;
    }
}

