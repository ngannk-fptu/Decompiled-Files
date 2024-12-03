/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ConfigBinding
extends Binding {
    private Object callable;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConfigBinding(Closure c) {
        CallSite[] callSiteArray = ConfigBinding.$getCallSiteArray();
        Closure closure = c;
        this.callable = closure;
    }

    @Override
    public void setVariable(String name, Object value) {
        CallSite[] callSiteArray = ConfigBinding.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeClosure(this.callable, new Object[]{name, value});
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConfigBinding.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getCallable() {
        return this.callable;
    }

    public void setCallable(Object object) {
        this.callable = object;
    }

    public /* synthetic */ void super$3$setVariable(String string, Object object) {
        super.setVariable(string, object);
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[]{};
        return new CallSiteArray(ConfigBinding.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConfigBinding.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

