/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.cli.CommandLine
 */
package groovy.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class OptionAccessor
implements GroovyObject {
    private CommandLine inner;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public OptionAccessor(CommandLine inner) {
        MetaClass metaClass;
        CallSite[] callSiteArray = OptionAccessor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        CommandLine commandLine = inner;
        this.inner = (CommandLine)ScriptBytecodeAdapter.castToType(commandLine, CommandLine.class);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        CallSite[] callSiteArray = OptionAccessor.$getCallSiteArray();
        return callSiteArray[0].call(callSiteArray[1].call(InvokerHelper.class, this.inner), this.inner, name, args);
    }

    @Override
    public Object getProperty(String name) {
        CallSite[] callSiteArray = OptionAccessor.$getCallSiteArray();
        Object methodname = "getOptionValue";
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[2].call(name), 1) && DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call((Object)name, "s"))) {
            Object singularName = callSiteArray[4].call((Object)name, ScriptBytecodeAdapter.createRange(0, -2, true));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callCurrent((GroovyObject)this, singularName))) {
                Object object = singularName;
                name = ShortTypeHandling.castToString(object);
                methodname = callSiteArray[6].call(methodname, "s");
            }
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[7].call(name), 1)) {
            char c = DefaultTypeTransformation.charUnbox(ScriptBytecodeAdapter.asType(name, Character.TYPE));
            name = ShortTypeHandling.castToString(Character.valueOf(c));
        }
        Object result = callSiteArray[8].call(callSiteArray[9].call(InvokerHelper.class, this.inner), this.inner, methodname, name);
        if (ScriptBytecodeAdapter.compareEqual(null, result)) {
            Object object;
            result = object = callSiteArray[10].call((Object)this.inner, name);
        }
        if (result instanceof String[]) {
            Object object;
            result = object = callSiteArray[11].call(result);
        }
        return result;
    }

    public List<String> arguments() {
        CallSite[] callSiteArray = OptionAccessor.$getCallSiteArray();
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(callSiteArray[13].callGetProperty(this.inner)), List.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != OptionAccessor.class) {
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
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public CommandLine getInner() {
        return this.inner;
    }

    public void setInner(CommandLine commandLine) {
        this.inner = commandLine;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "invokeMethod";
        stringArray[1] = "getMetaClass";
        stringArray[2] = "size";
        stringArray[3] = "endsWith";
        stringArray[4] = "getAt";
        stringArray[5] = "hasOption";
        stringArray[6] = "plus";
        stringArray[7] = "size";
        stringArray[8] = "invokeMethod";
        stringArray[9] = "getMetaClass";
        stringArray[10] = "hasOption";
        stringArray[11] = "toList";
        stringArray[12] = "toList";
        stringArray[13] = "args";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[14];
        OptionAccessor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(OptionAccessor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = OptionAccessor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

