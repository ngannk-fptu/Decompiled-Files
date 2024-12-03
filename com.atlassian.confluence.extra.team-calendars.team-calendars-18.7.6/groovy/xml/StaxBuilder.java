/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.util.BuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class StaxBuilder
extends BuilderSupport {
    private Object writer;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StaxBuilder(Object xmlStreamWriter) {
        Object object;
        CallSite[] callSiteArray = StaxBuilder.$getCallSiteArray();
        this.writer = object = xmlStreamWriter;
        callSiteArray[0].call(this.writer);
    }

    @Override
    protected Object createNode(Object name) {
        CallSite[] callSiteArray = StaxBuilder.$getCallSiteArray();
        return callSiteArray[1].callCurrent(this, name, null, null);
    }

    @Override
    protected Object createNode(Object name, Object value) {
        CallSite[] callSiteArray = StaxBuilder.$getCallSiteArray();
        return callSiteArray[2].callCurrent(this, name, null, value);
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        CallSite[] callSiteArray = StaxBuilder.$getCallSiteArray();
        return callSiteArray[3].callCurrent(this, name, attributes, null);
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        CallSite[] callSiteArray = StaxBuilder.$getCallSiteArray();
        callSiteArray[4].call(this.writer, callSiteArray[5].call(name));
        if (DefaultTypeTransformation.booleanUnbox(attributes)) {
            public class _createNode_closure1
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _createNode_closure1(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _createNode_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object k, Object v) {
                    CallSite[] callSiteArray = _createNode_closure1.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].call(k), callSiteArray[3].call(v));
                }

                public Object call(Object k, Object v) {
                    CallSite[] callSiteArray = _createNode_closure1.$getCallSiteArray();
                    return callSiteArray[4].callCurrent(this, k, v);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _createNode_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "writeAttribute";
                    stringArray[1] = "writer";
                    stringArray[2] = "toString";
                    stringArray[3] = "toString";
                    stringArray[4] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _createNode_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_createNode_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _createNode_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[6].call((Object)attributes, new _createNode_closure1(this, this));
        }
        if (DefaultTypeTransformation.booleanUnbox(value)) {
            callSiteArray[7].call(this.writer, callSiteArray[8].call(value));
        }
        return name;
    }

    @Override
    protected void nodeCompleted(Object parent, Object node) {
        CallSite[] callSiteArray = StaxBuilder.$getCallSiteArray();
        callSiteArray[9].call(this.writer);
        if (!DefaultTypeTransformation.booleanUnbox(parent)) {
            callSiteArray[10].call(this.writer);
            callSiteArray[11].call(this.writer);
        }
    }

    @Override
    protected void setParent(Object parent, Object child) {
        CallSite[] callSiteArray = StaxBuilder.$getCallSiteArray();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StaxBuilder.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getWriter() {
        return this.writer;
    }

    public void setWriter(Object object) {
        this.writer = object;
    }

    public /* synthetic */ void super$3$nodeCompleted(Object object, Object object2) {
        super.nodeCompleted(object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "writeStartDocument";
        stringArray[1] = "createNode";
        stringArray[2] = "createNode";
        stringArray[3] = "createNode";
        stringArray[4] = "writeStartElement";
        stringArray[5] = "toString";
        stringArray[6] = "each";
        stringArray[7] = "writeCharacters";
        stringArray[8] = "toString";
        stringArray[9] = "writeEndElement";
        stringArray[10] = "writeEndDocument";
        stringArray[11] = "flush";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[12];
        StaxBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StaxBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StaxBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

