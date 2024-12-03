/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;

public class ClearCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":clear";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ClearCommand(Groovysh shell) {
        CallSite[] callSiteArray = ClearCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":c");
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = ClearCommand.$getCallSiteArray();
        callSiteArray[0].callCurrent((GroovyObject)this, args);
        callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)))) {
            return callSiteArray[5].call(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), "Buffer cleared");
        }
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ClearCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "assertNoArguments";
        stringArray[1] = "clear";
        stringArray[2] = "buffer";
        stringArray[3] = "verbose";
        stringArray[4] = "io";
        stringArray[5] = "println";
        stringArray[6] = "out";
        stringArray[7] = "io";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        ClearCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ClearCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ClearCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

