/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.ThreadInterrupt;
import java.util.ArrayList;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.transform.AbstractInterruptibleASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class ThreadInterruptibleASTTransformation
extends AbstractInterruptibleASTTransformation
implements GroovyObject {
    private static final ClassNode MY_TYPE;
    private static final ClassNode THREAD_TYPE;
    private static final MethodNode CURRENTTHREAD_METHOD;
    private static final MethodNode ISINTERRUPTED_METHOD;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public ThreadInterruptibleASTTransformation() {
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    static {
        MethodNode methodNode;
        MethodNode methodNode2;
        ClassNode classNode;
        ClassNode classNode2;
        MY_TYPE = classNode2 = ClassHelper.make(ThreadInterrupt.class);
        THREAD_TYPE = classNode = ClassHelper.make(Thread.class);
        CURRENTTHREAD_METHOD = methodNode2 = THREAD_TYPE.getMethod("currentThread", Parameter.EMPTY_ARRAY);
        ISINTERRUPTED_METHOD = methodNode = THREAD_TYPE.getMethod("isInterrupted", Parameter.EMPTY_ARRAY);
    }

    @Override
    protected ClassNode type() {
        return MY_TYPE;
    }

    @Override
    protected String getErrorMessage() {
        return "Execution interrupted. The current thread has been interrupted.";
    }

    @Override
    protected Expression createCondition() {
        MethodCallExpression currentThread = new MethodCallExpression((Expression)new ClassExpression(THREAD_TYPE), "currentThread", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        MethodNode methodNode = CURRENTTHREAD_METHOD;
        currentThread.setMethodTarget(methodNode);
        MethodCallExpression isInterrupted = new MethodCallExpression((Expression)currentThread, "isInterrupted", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
        MethodNode methodNode2 = ISINTERRUPTED_METHOD;
        isInterrupted.setMethodTarget(methodNode2);
        ArrayList<Boolean> StaticTypesBinaryExpressionMultiTypeDispatcher$spreadresult1 = new ArrayList<Boolean>();
        if (ScriptBytecodeAdapter.createList(new Object[]{currentThread, isInterrupted}) != null) {
            Object for$it$12 = null;
            for (Object for$it$12 : ScriptBytecodeAdapter.createList(new Object[]{currentThread, isInterrupted})) {
                boolean bl = false;
                ScriptBytecodeAdapter.setPropertySafe(bl, null, for$it$12, "implicitThis");
                StaticTypesBinaryExpressionMultiTypeDispatcher$spreadresult1.add(bl);
            }
        }
        return isInterrupted;
    }

    @Override
    public void visitClosureExpression(ClosureExpression closureExpr) {
        Statement code = closureExpr.getCode();
        Statement statement = this.wrapBlock(code);
        closureExpr.setCode(statement);
        super.visitClosureExpression(closureExpr);
    }

    @Override
    public void visitMethod(MethodNode node) {
        if (this.checkOnMethodStart && !node.isSynthetic() && !node.isAbstract()) {
            Statement code = node.getCode();
            Statement statement = this.wrapBlock(code);
            node.setCode(statement);
        }
        super.visitMethod(node);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ThreadInterruptibleASTTransformation.class) {
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
}

