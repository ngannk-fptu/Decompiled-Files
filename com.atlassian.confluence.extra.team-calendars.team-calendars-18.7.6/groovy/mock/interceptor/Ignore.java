/*
 * Decompiled with CFR 0.152.
 */
package groovy.mock.interceptor;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class Ignore
implements GroovyObject {
    private Object parent;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Ignore() {
        MetaClass metaClass;
        CallSite[] callSiteArray = Ignore.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object invokeMethod(String methodName, Object args) {
        CallSite[] callSiteArray = Ignore.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(args) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[0].call(args), 1)) {
            throw (Throwable)callSiteArray[1].callConstructor(IllegalArgumentException.class, new GStringImpl(new Object[]{methodName}, new String[]{"Ranges/repetitions not supported for ignored method '", "'."}));
        }
        if (DefaultTypeTransformation.booleanUnbox(args) && ScriptBytecodeAdapter.compareEqual(callSiteArray[2].call(args), 1)) {
            if (callSiteArray[3].call(args, 0) instanceof Closure) {
                return callSiteArray[4].call(this.parent, methodName, ScriptBytecodeAdapter.createGroovyObjectWrapper((Closure)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(args, 0), Closure.class), Closure.class));
            }
            throw (Throwable)callSiteArray[6].callConstructor(IllegalArgumentException.class, new GStringImpl(new Object[]{methodName, callSiteArray[7].callGetProperty(callSiteArray[8].call(callSiteArray[9].call(args, 0)))}, new String[]{"Optional parameter to ignored method '", "' must be a Closure but instead found a ", "."}));
        }
        return callSiteArray[10].call(this.parent, methodName, null);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Ignore.class) {
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
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public Object getParent() {
        return this.parent;
    }

    public void setParent(Object object) {
        this.parent = object;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "size";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "size";
        stringArray[3] = "getAt";
        stringArray[4] = "ignore";
        stringArray[5] = "getAt";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "simpleName";
        stringArray[8] = "getClass";
        stringArray[9] = "getAt";
        stringArray[10] = "ignore";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[11];
        Ignore.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Ignore.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Ignore.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

