/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.util.Logger;

public class CommandRegistry
implements GroovyObject {
    protected final Logger log;
    private final List<Command> commandList;
    private final Set<String> names;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CommandRegistry() {
        MetaClass metaClass;
        List list;
        CallSite[] callSiteArray = CommandRegistry.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, CommandRegistry.class);
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.commandList = list = ScriptBytecodeAdapter.createList(new Object[0]);
        Object object2 = callSiteArray[1].callConstructor(TreeSet.class);
        this.names = (Set)ScriptBytecodeAdapter.castToType(object2, Set.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public Command register(Command command) {
        CallSite[] callSiteArray = CommandRegistry.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Command command2 = command;
            valueRecorder.record(command2, 8);
            if (DefaultTypeTransformation.booleanUnbox(command2)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert command", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (!(!DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(this.names, callSiteArray[3].callGetProperty(command))))) {
            ScriptBytecodeAdapter.assertFailed("names.contains(command.name)", new GStringImpl(new Object[]{callSiteArray[4].callGetProperty(command)}, new String[]{"Duplicate command name: ", ""}));
        }
        callSiteArray[5].call(this.names, callSiteArray[6].callGetProperty(command));
        if (!(!DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(this.names, callSiteArray[8].callGetProperty(command))))) {
            ScriptBytecodeAdapter.assertFailed("names.contains(command.shortcut)", new GStringImpl(new Object[]{callSiteArray[9].callGetProperty(command)}, new String[]{"Duplicate command shortcut: ", ""}));
        }
        callSiteArray[10].call(this.names, callSiteArray[11].callGetProperty(command));
        callSiteArray[12].call(this.commandList, command);
        if (command instanceof CommandSupport) {
            CommandRegistry commandRegistry = this;
            ScriptBytecodeAdapter.setProperty(commandRegistry, null, (CommandSupport)ScriptBytecodeAdapter.castToType(command, CommandSupport.class), "registry");
        }
        public class _register_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _register_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _register_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Command it) {
                CallSite[] callSiteArray = _register_closure1.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this.getThisObject(), it);
            }

            public Object call(Command it) {
                CallSite[] callSiteArray = _register_closure1.$getCallSiteArray();
                return callSiteArray[1].callCurrent((GroovyObject)this, it);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _register_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "register";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _register_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_register_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _register_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[13].callSafe(callSiteArray[14].callGetProperty(command), new _register_closure1(this, this));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].callGetProperty(this.log))) {
            callSiteArray[16].call((Object)this.log, new GStringImpl(new Object[]{callSiteArray[17].callGetProperty(command)}, new String[]{"Registered command: ", ""}));
        }
        return command;
    }

    public Command find(String name) {
        CallSite[] callSiteArray = CommandRegistry.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = name;
            valueRecorder.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert name", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Object c = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[18].call(this.commandList), Iterator.class);
        while (iterator.hasNext()) {
            c = iterator.next();
            if (ScriptBytecodeAdapter.isCase(name, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[19].callGetProperty(c), callSiteArray[20].callGetProperty(c)}))) {
                return (Command)ScriptBytecodeAdapter.castToType(c, Command.class);
            }
            if (!(!DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call(callSiteArray[22].callGetProperty(c), ":")) && DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call((Object)name, callSiteArray[24].call((Object)":", callSiteArray[25].callGetProperty(c)))))) continue;
            return (Command)ScriptBytecodeAdapter.castToType(c, Command.class);
        }
        return (Command)ScriptBytecodeAdapter.castToType(null, Command.class);
    }

    public void remove(Command command) {
        CallSite[] callSiteArray = CommandRegistry.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Command command2 = command;
            valueRecorder.record(command2, 8);
            if (DefaultTypeTransformation.booleanUnbox(command2)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert command", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        callSiteArray[26].call(this.commandList, command);
        callSiteArray[27].call(this.names, callSiteArray[28].callGetProperty(command));
        callSiteArray[29].call(this.names, callSiteArray[30].callGetProperty(command));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[31].callGetProperty(this.log))) {
            callSiteArray[32].call((Object)this.log, new GStringImpl(new Object[]{callSiteArray[33].callGetProperty(command)}, new String[]{"Removed command: ", ""}));
        }
    }

    public List<Command> commands() {
        CallSite[] callSiteArray = CommandRegistry.$getCallSiteArray();
        return this.commandList;
    }

    @Override
    public Command getProperty(String name) {
        CallSite[] callSiteArray = CommandRegistry.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (Command)ScriptBytecodeAdapter.castToType(callSiteArray[34].callCurrent((GroovyObject)this, name), Command.class);
        }
        return this.find(name);
    }

    public Iterator iterator() {
        CallSite[] callSiteArray = CommandRegistry.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[35].call(callSiteArray[36].callCurrent(this)), Iterator.class);
        }
        return (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[37].call(this.commands()), Iterator.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CommandRegistry.class) {
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
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public final List<Command> getCommandList() {
        return this.commandList;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "contains";
        stringArray[3] = "name";
        stringArray[4] = "name";
        stringArray[5] = "leftShift";
        stringArray[6] = "name";
        stringArray[7] = "contains";
        stringArray[8] = "shortcut";
        stringArray[9] = "shortcut";
        stringArray[10] = "leftShift";
        stringArray[11] = "shortcut";
        stringArray[12] = "leftShift";
        stringArray[13] = "each";
        stringArray[14] = "aliases";
        stringArray[15] = "debugEnabled";
        stringArray[16] = "debug";
        stringArray[17] = "name";
        stringArray[18] = "iterator";
        stringArray[19] = "name";
        stringArray[20] = "shortcut";
        stringArray[21] = "startsWith";
        stringArray[22] = "name";
        stringArray[23] = "equals";
        stringArray[24] = "plus";
        stringArray[25] = "name";
        stringArray[26] = "remove";
        stringArray[27] = "remove";
        stringArray[28] = "name";
        stringArray[29] = "remove";
        stringArray[30] = "shortcut";
        stringArray[31] = "debugEnabled";
        stringArray[32] = "debug";
        stringArray[33] = "name";
        stringArray[34] = "find";
        stringArray[35] = "iterator";
        stringArray[36] = "commands";
        stringArray[37] = "iterator";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[38];
        CommandRegistry.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CommandRegistry.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CommandRegistry.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

