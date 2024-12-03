/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.CommandRegistry;
import org.codehaus.groovy.tools.shell.util.SimpleCompletor;

public class CommandNameCompleter
extends SimpleCompletor {
    private final CommandRegistry registry;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CommandNameCompleter(CommandRegistry registry) {
        CallSite[] callSiteArray = CommandNameCompleter.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            CommandRegistry commandRegistry = registry;
            valueRecorder.record(commandRegistry, 8);
            if (DefaultTypeTransformation.booleanUnbox(commandRegistry)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert registry", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        CommandRegistry commandRegistry = registry;
        this.registry = (CommandRegistry)ScriptBytecodeAdapter.castToType(commandRegistry, CommandRegistry.class);
    }

    @Override
    public SortedSet<String> getCandidates() {
        CallSite[] callSiteArray = CommandNameCompleter.$getCallSiteArray();
        SortedSet set = (SortedSet)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(TreeSet.class), SortedSet.class);
        Command command = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[1].call(callSiteArray[2].call(this.registry)), Iterator.class);
        while (iterator.hasNext()) {
            command = (Command)ScriptBytecodeAdapter.castToType(iterator.next(), Command.class);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].callGetProperty(command))) continue;
            callSiteArray[4].call((Object)set, callSiteArray[5].callGetProperty(command));
            callSiteArray[6].call((Object)set, callSiteArray[7].callGetProperty(command));
        }
        return set;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CommandNameCompleter.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ SortedSet super$2$getCandidates() {
        return super.getCandidates();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "iterator";
        stringArray[2] = "commands";
        stringArray[3] = "hidden";
        stringArray[4] = "leftShift";
        stringArray[5] = "name";
        stringArray[6] = "leftShift";
        stringArray[7] = "shortcut";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        CommandNameCompleter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CommandNameCompleter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CommandNameCompleter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

