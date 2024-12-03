/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class RecursivenessTester
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RecursivenessTester() {
        MetaClass metaClass;
        CallSite[] callSiteArray = RecursivenessTester.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public boolean isRecursive(Object params) {
        CallSite[] callSiteArray = RecursivenessTester.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            CallSite callSite = callSiteArray[0];
            CallSite callSite2 = callSiteArray[1];
            Object object = params;
            valueRecorder.record(object, 8);
            Object object2 = callSite2.callGetProperty(object);
            valueRecorder.record(object2, 15);
            Object object3 = callSite.callGetProperty(object2);
            valueRecorder.record(object3, 22);
            boolean bl = ScriptBytecodeAdapter.compareEqual(object3, MethodNode.class);
            valueRecorder.record(bl, 28);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert params.method.class == MethodNode", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        ValueRecorder valueRecorder2 = new ValueRecorder();
        try {
            CallSite callSite = callSiteArray[2];
            CallSite callSite3 = callSiteArray[3];
            Object object = params;
            valueRecorder2.record(object, 8);
            Object object4 = callSite3.callGetProperty(object);
            valueRecorder2.record(object4, 15);
            Object object5 = callSite.callGetProperty(object4);
            valueRecorder2.record(object5, 20);
            boolean bl = ScriptBytecodeAdapter.compareEqual(object5, MethodCallExpression.class);
            valueRecorder2.record(bl, 26);
            boolean bl2 = bl || DefaultTypeTransformation.booleanUnbox(StaticMethodCallExpression.class);
            valueRecorder2.record(bl2, 50);
            if (bl2) {
                valueRecorder2.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert params.call.class == MethodCallExpression || StaticMethodCallExpression", valueRecorder2), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder2.clear();
            throw throwable;
        }
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[4].callCurrent(this, callSiteArray[5].callGetProperty(params), callSiteArray[6].callGetProperty(params)));
    }

    public boolean isRecursive(MethodNode method, MethodCallExpression call) {
        CallSite[] callSiteArray = RecursivenessTester.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callCurrent((GroovyObject)this, call))) {
            return false;
        }
        if (!(callSiteArray[8].callGetProperty(call) instanceof ConstantExpression)) {
            return false;
        }
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[9].callGetProperty(callSiteArray[10].callGetProperty(call)), callSiteArray[11].callGetProperty(method))) {
            return false;
        }
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[12].callCurrent(this, method, call));
    }

    public boolean isRecursive(MethodNode method, StaticMethodCallExpression call) {
        CallSite[] callSiteArray = RecursivenessTester.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[13].call(method))) {
            return false;
        }
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[14].callGetProperty(method), callSiteArray[15].callGetProperty(call))) {
            return false;
        }
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[16].callGetProperty(call), callSiteArray[17].callGetProperty(method))) {
            return false;
        }
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[18].callCurrent(this, method, call));
    }

    private boolean isCallToThis(MethodCallExpression call) {
        CallSite[] callSiteArray = RecursivenessTester.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[19].callGetProperty(call), null)) {
            return DefaultTypeTransformation.booleanUnbox(callSiteArray[20].call(call));
        }
        if (!(callSiteArray[21].callGetProperty(call) instanceof VariableExpression)) {
            return false;
        }
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call(callSiteArray[23].callGetProperty(call)));
    }

    private boolean methodParamsMatchCallArgs(Object method, Object call) {
        CallSite[] callSiteArray = RecursivenessTester.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[24].call(callSiteArray[25].callGetProperty(method)), callSiteArray[26].call(callSiteArray[27].callGetProperty(callSiteArray[28].callGetProperty(call))))) {
            return false;
        }
        Object classNodePairs = callSiteArray[29].call(ScriptBytecodeAdapter.createList(new Object[]{ScriptBytecodeAdapter.getPropertySpreadSafe(RecursivenessTester.class, callSiteArray[30].callGetProperty(method), "type"), ScriptBytecodeAdapter.getPropertySpreadSafe(RecursivenessTester.class, callSiteArray[31].callGetProperty(call), "type")}));
        public class _methodParamsMatchCallArgs_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _methodParamsMatchCallArgs_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _methodParamsMatchCallArgs_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(ClassNode paramType, ClassNode argType) {
                CallSite[] callSiteArray = _methodParamsMatchCallArgs_closure1.$getCallSiteArray();
                return callSiteArray[0].callCurrent(this, argType, paramType);
            }

            public Object call(ClassNode paramType, ClassNode argType) {
                CallSite[] callSiteArray = _methodParamsMatchCallArgs_closure1.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, paramType, argType);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _methodParamsMatchCallArgs_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "areTypesCallCompatible";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _methodParamsMatchCallArgs_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_methodParamsMatchCallArgs_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _methodParamsMatchCallArgs_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[32].call(classNodePairs, new _methodParamsMatchCallArgs_closure1(this, this)));
    }

    private Object areTypesCallCompatible(ClassNode argType, ClassNode paramType) {
        CallSite[] callSiteArray = RecursivenessTester.$getCallSiteArray();
        ClassNode boxedArg = (ClassNode)ScriptBytecodeAdapter.castToType(callSiteArray[33].call(ClassHelper.class, argType), ClassNode.class);
        ClassNode boxedParam = (ClassNode)ScriptBytecodeAdapter.castToType(callSiteArray[34].call(ClassHelper.class, paramType), ClassNode.class);
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[35].call((Object)boxedArg, boxedParam)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[36].call((Object)boxedParam, boxedArg));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RecursivenessTester.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "class";
        stringArray[1] = "method";
        stringArray[2] = "class";
        stringArray[3] = "call";
        stringArray[4] = "isRecursive";
        stringArray[5] = "method";
        stringArray[6] = "call";
        stringArray[7] = "isCallToThis";
        stringArray[8] = "method";
        stringArray[9] = "value";
        stringArray[10] = "method";
        stringArray[11] = "name";
        stringArray[12] = "methodParamsMatchCallArgs";
        stringArray[13] = "isStatic";
        stringArray[14] = "declaringClass";
        stringArray[15] = "ownerType";
        stringArray[16] = "method";
        stringArray[17] = "name";
        stringArray[18] = "methodParamsMatchCallArgs";
        stringArray[19] = "objectExpression";
        stringArray[20] = "isImplicitThis";
        stringArray[21] = "objectExpression";
        stringArray[22] = "isThisExpression";
        stringArray[23] = "objectExpression";
        stringArray[24] = "size";
        stringArray[25] = "parameters";
        stringArray[26] = "size";
        stringArray[27] = "expressions";
        stringArray[28] = "arguments";
        stringArray[29] = "transpose";
        stringArray[30] = "parameters";
        stringArray[31] = "arguments";
        stringArray[32] = "every";
        stringArray[33] = "getWrapper";
        stringArray[34] = "getWrapper";
        stringArray[35] = "isDerivedFrom";
        stringArray[36] = "isDerivedFrom";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[37];
        RecursivenessTester.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RecursivenessTester.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RecursivenessTester.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

