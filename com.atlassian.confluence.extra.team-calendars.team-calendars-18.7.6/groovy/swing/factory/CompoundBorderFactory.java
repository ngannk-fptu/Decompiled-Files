/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.swing.factory.SwingBorderFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CompoundBorderFactory
extends SwingBorderFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CompoundBorderFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = CompoundBorderFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = CompoundBorderFactory.$getCallSiteArray();
        Object object = callSiteArray[0].call((Object)attributes, "parent");
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGroovyObjectGetProperty(builder), "applyBorderToParent");
        Border border = null;
        if (value instanceof List) {
            Object object2 = callSiteArray[2].call(value);
            if (ScriptBytecodeAdapter.isCase(object2, 0)) {
                throw (Throwable)callSiteArray[3].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " does not accept an empty array as an value argument"}));
            }
            if (ScriptBytecodeAdapter.isCase(object2, 1)) {
                Object object3 = callSiteArray[4].call(value, 0);
                border = (Border)ScriptBytecodeAdapter.castToType(object3, Border.class);
            } else if (ScriptBytecodeAdapter.isCase(object2, 2)) {
                Object object4 = callSiteArray[5].callConstructor(CompoundBorder.class, callSiteArray[6].call(value, 0), callSiteArray[7].call(value, 1));
                border = (Border)ScriptBytecodeAdapter.castToType(object4, Border.class);
            } else {
                if (ScriptBytecodeAdapter.isCase(object2, 3)) {
                    // empty if block
                }
                Object object5 = callSiteArray[8].callConstructor(CompoundBorder.class, callSiteArray[9].call(value, 0), callSiteArray[10].call(value, 1));
                border = (Border)ScriptBytecodeAdapter.castToType(object5, Border.class);
                public class _newInstance_closure1
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _newInstance_closure1(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object that, Object it) {
                        CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                        return callSiteArray[0].callConstructor(CompoundBorder.class, that, it);
                    }

                    public Object call(Object that, Object it) {
                        CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                        return callSiteArray[1].callCurrent(this, that, it);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _newInstance_closure1.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "<$constructor$>";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _newInstance_closure1.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_newInstance_closure1.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _newInstance_closure1.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                Object object6 = callSiteArray[11].call(callSiteArray[12].call(value, ScriptBytecodeAdapter.createRange(2, -1, true)), border, new _newInstance_closure1(this, this));
                border = (Border)ScriptBytecodeAdapter.castToType(object6, Border.class);
            }
        }
        if (!DefaultTypeTransformation.booleanUnbox(border) && DefaultTypeTransformation.booleanUnbox(attributes)) {
            if (DefaultTypeTransformation.booleanUnbox(value)) {
                throw (Throwable)callSiteArray[13].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " only accepts an array of borders as a value argument"}));
            }
            Object inner = callSiteArray[14].call((Object)attributes, "inner");
            Object outer = callSiteArray[15].call((Object)attributes, "outer");
            if (inner instanceof Border && outer instanceof Border) {
                Object object7 = callSiteArray[16].callConstructor(CompoundBorder.class, outer, inner);
                border = (Border)ScriptBytecodeAdapter.castToType(object7, Border.class);
            }
        }
        if (!DefaultTypeTransformation.booleanUnbox(border)) {
            throw (Throwable)callSiteArray[17].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " only accepts an array of javax.swing.border.Border or an inner: and outer: attribute"}));
        }
        return border;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CompoundBorderFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "context";
        stringArray[2] = "size";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "getAt";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "getAt";
        stringArray[7] = "getAt";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "getAt";
        stringArray[10] = "getAt";
        stringArray[11] = "inject";
        stringArray[12] = "getAt";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "remove";
        stringArray[15] = "remove";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[18];
        CompoundBorderFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CompoundBorderFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CompoundBorderFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

