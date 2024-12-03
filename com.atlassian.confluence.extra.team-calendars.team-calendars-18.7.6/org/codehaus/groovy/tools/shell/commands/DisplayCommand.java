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

public class DisplayCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":display";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public DisplayCommand(Groovysh shell) {
        CallSite[] callSiteArray = DisplayCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":d");
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = DisplayCommand.$getCallSiteArray();
        callSiteArray[0].callCurrent((GroovyObject)this, args);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this)))) {
            return callSiteArray[3].call(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), "Buffer is empty");
        }
        return callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this), callSiteArray[8].callGroovyObjectGetProperty(this));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != DisplayCommand.class) {
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
        stringArray[1] = "isEmpty";
        stringArray[2] = "buffer";
        stringArray[3] = "println";
        stringArray[4] = "out";
        stringArray[5] = "io";
        stringArray[6] = "displayBuffer";
        stringArray[7] = "shell";
        stringArray[8] = "buffer";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[9];
        DisplayCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(DisplayCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = DisplayCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

