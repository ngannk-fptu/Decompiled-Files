/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.classgen.ReturnAdder;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ReturnAdderForClosures
extends CodeVisitorSupport
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ReturnAdderForClosures() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ReturnAdderForClosures.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public synchronized void visitMethod(MethodNode method) {
        CallSite[] callSiteArray = ReturnAdderForClosures.$getCallSiteArray();
        callSiteArray[0].call(callSiteArray[1].callGetProperty(method), this);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        CallSite[] callSiteArray = ReturnAdderForClosures.$getCallSiteArray();
        MethodNode node = (MethodNode)ScriptBytecodeAdapter.castToType(callSiteArray[2].callConstructor((Object)MethodNode.class, ArrayUtil.createArray("dummy", 0, callSiteArray[3].callGetProperty(ClassHelper.class), callSiteArray[4].callGetProperty(Parameter.class), callSiteArray[5].callGetProperty(ClassNode.class), callSiteArray[6].callGetProperty(expression))), MethodNode.class);
        callSiteArray[7].call(callSiteArray[8].callConstructor(ReturnAdder.class), node);
        ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, this, "visitClosureExpression", new Object[]{expression});
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ReturnAdderForClosures.class) {
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

    public /* synthetic */ void super$2$visitClosureExpression(ClosureExpression closureExpression) {
        super.visitClosureExpression(closureExpression);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "visit";
        stringArray[1] = "code";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "OBJECT_TYPE";
        stringArray[4] = "EMPTY_ARRAY";
        stringArray[5] = "EMPTY_ARRAY";
        stringArray[6] = "code";
        stringArray[7] = "visitMethod";
        stringArray[8] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[9];
        ReturnAdderForClosures.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ReturnAdderForClosures.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ReturnAdderForClosures.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

