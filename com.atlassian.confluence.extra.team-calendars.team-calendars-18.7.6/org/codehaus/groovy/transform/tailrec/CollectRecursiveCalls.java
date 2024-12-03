/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.util.List;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.transform.tailrec.RecursivenessTester;

public class CollectRecursiveCalls
extends CodeVisitorSupport
implements GroovyObject {
    private MethodNode method;
    private List<Expression> recursiveCalls;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public CollectRecursiveCalls() {
        MetaClass metaClass;
        List list;
        this.recursiveCalls = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        if (this.isRecursive(call)) {
            DefaultGroovyMethods.leftShift(this.recursiveCalls, call);
        }
        super.visitMethodCallExpression(call);
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        if (this.isRecursive(call)) {
            DefaultGroovyMethods.leftShift(this.recursiveCalls, call);
        }
        super.visitStaticMethodCallExpression(call);
    }

    private boolean isRecursive(Object call) {
        return new RecursivenessTester().isRecursive(ScriptBytecodeAdapter.createMap(new Object[]{"method", this.method, "call", call}));
    }

    public synchronized List<Expression> collect(MethodNode method) {
        MethodNode methodNode;
        this.recursiveCalls.clear();
        this.method = methodNode = method;
        this.method.getCode().visit(this);
        return this.recursiveCalls;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CollectRecursiveCalls.class) {
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

    public MethodNode getMethod() {
        return this.method;
    }

    public void setMethod(MethodNode methodNode) {
        this.method = methodNode;
    }

    public List<Expression> getRecursiveCalls() {
        return this.recursiveCalls;
    }

    public void setRecursiveCalls(List<Expression> list) {
        this.recursiveCalls = list;
    }
}

