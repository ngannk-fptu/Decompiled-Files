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
import org.codehaus.groovy.tools.shell.ExitNotification;
import org.codehaus.groovy.tools.shell.Groovysh;

public class ExitCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":exit";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ExitCommand(Groovysh shell) {
        CallSite[] callSiteArray = ExitCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":x");
        callSiteArray[0].callCurrent(this, ":quit", ":q");
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = ExitCommand.$getCallSiteArray();
        callSiteArray[1].callCurrent((GroovyObject)this, args);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)))) {
            callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)), callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty(this), "info.bye"));
        }
        throw (Throwable)callSiteArray[9].callConstructor(ExitNotification.class, 0);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ExitCommand.class) {
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
        stringArray[0] = "alias";
        stringArray[1] = "assertNoArguments";
        stringArray[2] = "verbose";
        stringArray[3] = "io";
        stringArray[4] = "println";
        stringArray[5] = "out";
        stringArray[6] = "io";
        stringArray[7] = "getAt";
        stringArray[8] = "messages";
        stringArray[9] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[10];
        ExitCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ExitCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ExitCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

