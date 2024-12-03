/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class AbstractSyntheticMetaMethods
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AbstractSyntheticMetaMethods() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AbstractSyntheticMetaMethods.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static void enhance(Object o, Map enhancedMethods) {
        boolean bl;
        CallSite[] callSiteArray = AbstractSyntheticMetaMethods.$getCallSiteArray();
        Class klass = ShortTypeHandling.castToClass(callSiteArray[0].call(o));
        MetaClassRegistry mcr = (MetaClassRegistry)ScriptBytecodeAdapter.castToType(callSiteArray[1].callGetProperty(GroovySystem.class), MetaClassRegistry.class);
        Reference<MetaClass> mc = new Reference<MetaClass>((MetaClass)ScriptBytecodeAdapter.castToType(callSiteArray[2].call((Object)mcr, klass), MetaClass.class));
        boolean init = false;
        callSiteArray[3].call((Object)mcr, klass);
        Object object = callSiteArray[4].callConstructor(ExpandoMetaClass.class, klass);
        mc.set((MetaClass)ScriptBytecodeAdapter.castToType(object, MetaClass.class));
        init = bl = true;
        public class _enhance_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference mc;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _enhance_closure1(Object _outerInstance, Object _thisObject, Reference mc) {
                Reference reference;
                CallSite[] callSiteArray = _enhance_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.mc = reference = mc;
            }

            public Object doCall(Object k, Object v) {
                CallSite[] callSiteArray = _enhance_closure1.$getCallSiteArray();
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].call(this.mc.get(), k), null)) {
                    return callSiteArray[1].call(this.mc.get(), k, v);
                }
                return null;
            }

            public Object call(Object k, Object v) {
                CallSite[] callSiteArray = _enhance_closure1.$getCallSiteArray();
                return callSiteArray[2].callCurrent(this, k, v);
            }

            public MetaClass getMc() {
                CallSite[] callSiteArray = _enhance_closure1.$getCallSiteArray();
                return (MetaClass)ScriptBytecodeAdapter.castToType(this.mc.get(), MetaClass.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _enhance_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getMetaMethod";
                stringArray[1] = "registerInstanceMethod";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _enhance_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_enhance_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _enhance_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[5].call((Object)enhancedMethods, new _enhance_closure1(AbstractSyntheticMetaMethods.class, AbstractSyntheticMetaMethods.class, mc));
        if (init) {
            callSiteArray[6].call(mc.get());
            callSiteArray[7].call(mcr, klass, mc.get());
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AbstractSyntheticMetaMethods.class) {
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
        stringArray[0] = "getClass";
        stringArray[1] = "metaClassRegistry";
        stringArray[2] = "getMetaClass";
        stringArray[3] = "removeMetaClass";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "each";
        stringArray[6] = "initialize";
        stringArray[7] = "setMetaClass";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        AbstractSyntheticMetaMethods.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AbstractSyntheticMetaMethods.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AbstractSyntheticMetaMethods.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

