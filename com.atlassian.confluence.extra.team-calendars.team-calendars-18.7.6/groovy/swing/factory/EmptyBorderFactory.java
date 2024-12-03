/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.swing.factory.SwingBorderFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class EmptyBorderFactory
extends SwingBorderFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public EmptyBorderFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = EmptyBorderFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = EmptyBorderFactory.$getCallSiteArray();
        Object object = callSiteArray[0].call((Object)attributes, "parent");
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGroovyObjectGetProperty(builder), "applyBorderToParent");
        if (!DefaultTypeTransformation.booleanUnbox(attributes)) {
            if (value instanceof Integer) {
                return callSiteArray[2].call(BorderFactory.class, value, value, value, value);
            }
            if (value instanceof List && ScriptBytecodeAdapter.compareEqual(callSiteArray[3].call(value), 4)) {
                Reference<Boolean> ints = new Reference<Boolean>(true);
                public class _newInstance_closure1
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference ints;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _newInstance_closure1(Object _outerInstance, Object _thisObject, Reference ints) {
                        Reference reference;
                        CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.ints = reference = ints;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                        Object object = callSiteArray[0].call(this.ints.get(), it instanceof Integer);
                        this.ints.set((Boolean)ScriptBytecodeAdapter.castToType(object, Boolean.class));
                        return object;
                    }

                    public Boolean getInts() {
                        CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                        return (Boolean)ScriptBytecodeAdapter.castToType(this.ints.get(), Boolean.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                        return this.doCall(null);
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

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "and";
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
                callSiteArray[4].call(value, new _newInstance_closure1(this, this, ints));
                if (DefaultTypeTransformation.booleanUnbox(ints.get())) {
                    return callSiteArray[5].call((Object)BorderFactory.class, ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{value}, new int[]{0}));
                }
            }
            throw (Throwable)callSiteArray[6].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " only accepts a single integer or an array of four integers as a value argument"}));
        }
        if (ScriptBytecodeAdapter.compareEqual(value, null)) {
            int top = DefaultTypeTransformation.intUnbox(callSiteArray[7].call((Object)attributes, "top"));
            int left = DefaultTypeTransformation.intUnbox(callSiteArray[8].call((Object)attributes, "left"));
            int bottom = DefaultTypeTransformation.intUnbox(callSiteArray[9].call((Object)attributes, "bottom"));
            int right = DefaultTypeTransformation.intUnbox(callSiteArray[10].call((Object)attributes, "right"));
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareEqual(top, null) || ScriptBytecodeAdapter.compareEqual(left, null) || ScriptBytecodeAdapter.compareEqual(bottom, null) || ScriptBytecodeAdapter.compareEqual(right, null) || DefaultTypeTransformation.booleanUnbox(attributes)) {
                    throw (Throwable)callSiteArray[11].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"When ", " is called it must be call with top:, left:, bottom:, right:, and no other attributes"}));
                }
            } else if (ScriptBytecodeAdapter.compareEqual(top, null) || ScriptBytecodeAdapter.compareEqual(left, null) || ScriptBytecodeAdapter.compareEqual(bottom, null) || ScriptBytecodeAdapter.compareEqual(right, null) || DefaultTypeTransformation.booleanUnbox(attributes)) {
                throw (Throwable)callSiteArray[12].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"When ", " is called it must be call with top:, left:, bottom:, right:, and no other attributes"}));
            }
            return callSiteArray[13].call(BorderFactory.class, top, left, bottom, right);
        }
        throw (Throwable)callSiteArray[14].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " cannot be called with both an argulent value and attributes"}));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != EmptyBorderFactory.class) {
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
        stringArray[2] = "createEmptyBorder";
        stringArray[3] = "size";
        stringArray[4] = "each";
        stringArray[5] = "createEmptyBorder";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "remove";
        stringArray[8] = "remove";
        stringArray[9] = "remove";
        stringArray[10] = "remove";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "createEmptyBorder";
        stringArray[14] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[15];
        EmptyBorderFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(EmptyBorderFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = EmptyBorderFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

