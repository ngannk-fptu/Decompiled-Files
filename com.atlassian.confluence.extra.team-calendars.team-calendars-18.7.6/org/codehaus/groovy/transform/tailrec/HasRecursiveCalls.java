/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.transform.tailrec.RecursivenessTester;

public class HasRecursiveCalls
extends CodeVisitorSupport
implements GroovyObject {
    private MethodNode method;
    private boolean hasRecursiveCalls;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public HasRecursiveCalls() {
        MetaClass metaClass;
        boolean bl;
        this.hasRecursiveCalls = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        if (this.isRecursive(call)) {
            boolean bl;
            this.hasRecursiveCalls = bl = true;
        } else {
            super.visitMethodCallExpression(call);
        }
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        if (this.isRecursive(call)) {
            boolean bl;
            this.hasRecursiveCalls = bl = true;
        } else {
            super.visitStaticMethodCallExpression(call);
        }
    }

    private boolean isRecursive(Object call) {
        return new RecursivenessTester().isRecursive(ScriptBytecodeAdapter.createMap(new Object[]{"method", this.method, "call", call}));
    }

    public synchronized boolean test(MethodNode method) {
        MethodNode methodNode;
        boolean bl;
        this.hasRecursiveCalls = bl = false;
        this.method = methodNode = method;
        this.method.getCode().visit(this);
        return this.hasRecursiveCalls;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != HasRecursiveCalls.class) {
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

    public boolean getHasRecursiveCalls() {
        return this.hasRecursiveCalls;
    }

    public boolean isHasRecursiveCalls() {
        return this.hasRecursiveCalls;
    }

    public void setHasRecursiveCalls(boolean bl) {
        this.hasRecursiveCalls = bl;
    }
}

