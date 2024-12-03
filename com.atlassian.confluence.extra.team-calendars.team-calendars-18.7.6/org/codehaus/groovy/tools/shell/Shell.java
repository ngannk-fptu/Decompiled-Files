/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.fusesource.jansi.Ansi
 *  org.fusesource.jansi.Ansi$Attribute
 *  org.fusesource.jansi.Ansi$Color
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.CommandException;
import org.codehaus.groovy.tools.shell.CommandRegistry;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.util.CommandArgumentParser;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.fusesource.jansi.Ansi;

public class Shell
implements GroovyObject {
    protected final Logger log;
    private final CommandRegistry registry;
    private final IO io;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Shell(IO io) {
        MetaClass metaClass;
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        Object object2 = callSiteArray[2].callConstructor(CommandRegistry.class);
        this.registry = (CommandRegistry)ScriptBytecodeAdapter.castToType(object2, CommandRegistry.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            IO iO = io;
            valueRecorder.record(iO, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(iO, null);
            valueRecorder.record(bl, 11);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert(io != null)", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        IO iO = io;
        this.io = (IO)ScriptBytecodeAdapter.castToType(iO, IO.class);
    }

    public Shell() {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        this((IO)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(IO.class), IO.class));
    }

    public Command findCommand(String line, List<String> parsedArgs) {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = line;
            valueRecorder.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert line", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Command command = null;
        List linetokens = (List)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(callSiteArray[5].call(line)), List.class);
        ValueRecorder valueRecorder2 = new ValueRecorder();
        try {
            CallSite callSite = callSiteArray[6];
            List list = linetokens;
            valueRecorder2.record(list, 8);
            Object object = callSite.call(list);
            valueRecorder2.record(object, 19);
            boolean bl = ScriptBytecodeAdapter.compareGreaterThan(object, 0);
            valueRecorder2.record(bl, 26);
            if (bl) {
                valueRecorder2.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert linetokens.size() > 0", valueRecorder2), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder2.clear();
            throw throwable;
        }
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[7].call(callSiteArray[8].call((Object)linetokens, 0)), 0)) {
            Object object = callSiteArray[9].call((Object)this.registry, callSiteArray[10].call((Object)linetokens, 0));
            command = (Command)ScriptBytecodeAdapter.castToType(object, Command.class);
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareNotEqual(command, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[11].call(linetokens), 1) && ScriptBytecodeAdapter.compareNotEqual(parsedArgs, null)) {
                    List args = (List)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(CommandArgumentParser.class, line, ScriptBytecodeAdapter.compareEqual(parsedArgs, null) ? Integer.valueOf(1) : Integer.valueOf(-1)), List.class);
                    callSiteArray[13].call(parsedArgs, callSiteArray[14].call((Object)args, ScriptBytecodeAdapter.createRange(1, -1, true)));
                }
            } else if (ScriptBytecodeAdapter.compareNotEqual(command, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[15].call(linetokens), 1) && ScriptBytecodeAdapter.compareNotEqual(parsedArgs, null)) {
                List args = (List)ScriptBytecodeAdapter.castToType(callSiteArray[16].call(CommandArgumentParser.class, line, ScriptBytecodeAdapter.compareEqual(parsedArgs, null) ? Integer.valueOf(1) : Integer.valueOf(-1)), List.class);
                callSiteArray[17].call(parsedArgs, callSiteArray[18].call((Object)args, ScriptBytecodeAdapter.createRange(1, -1, true)));
            }
        }
        return command;
    }

    public boolean isExecutable(String line) {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ScriptBytecodeAdapter.compareNotEqual(callSiteArray[19].callCurrent((GroovyObject)this, line), null);
        }
        return ScriptBytecodeAdapter.compareNotEqual(this.findCommand(line), null);
    }

    public Object execute(String line) {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = line;
            valueRecorder.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert line", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        List args = ScriptBytecodeAdapter.createList(new Object[0]);
        Command command = (Command)ScriptBytecodeAdapter.castToType(callSiteArray[20].callCurrent(this, line, args), Command.class);
        Object result = null;
        if (DefaultTypeTransformation.booleanUnbox(command)) {
            callSiteArray[21].call((Object)this.log, new GStringImpl(new Object[]{callSiteArray[22].callGetProperty(command), command, args}, new String[]{"Executing command(", "): ", "; w/args: ", ""}));
            try {
                Object object;
                result = object = callSiteArray[23].call((Object)command, args);
            }
            catch (CommandException e) {
                callSiteArray[24].call(callSiteArray[25].callGetProperty(this.io), callSiteArray[26].call(callSiteArray[27].call(callSiteArray[28].call(callSiteArray[29].call(callSiteArray[30].callStatic(Ansi.class), callSiteArray[31].callGetProperty(Ansi.Attribute.class)), callSiteArray[32].callGetProperty(Ansi.Color.class)), callSiteArray[33].callGetProperty(e))));
            }
            callSiteArray[34].call((Object)this.log, new GStringImpl(new Object[]{callSiteArray[35].call(InvokerHelper.class, result)}, new String[]{"Result: ", ""}));
        }
        return result;
    }

    public Command register(Command command) {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        return (Command)ScriptBytecodeAdapter.castToType(callSiteArray[36].call((Object)this.registry, command), Command.class);
    }

    public Object leftShift(String line) {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return callSiteArray[37].callCurrent((GroovyObject)this, line);
        }
        return this.execute(line);
    }

    public Command leftShift(Command command) {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        return (Command)ScriptBytecodeAdapter.castToType(callSiteArray[38].callCurrent((GroovyObject)this, command), Command.class);
    }

    public Command findCommand(String line) {
        CallSite[] callSiteArray = Shell.$getCallSiteArray();
        return this.findCommand(line, null);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Shell.class) {
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

    public final CommandRegistry getRegistry() {
        return this.registry;
    }

    public final IO getIo() {
        return this.io;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "tokenize";
        stringArray[5] = "trim";
        stringArray[6] = "size";
        stringArray[7] = "length";
        stringArray[8] = "getAt";
        stringArray[9] = "find";
        stringArray[10] = "getAt";
        stringArray[11] = "size";
        stringArray[12] = "parseLine";
        stringArray[13] = "addAll";
        stringArray[14] = "getAt";
        stringArray[15] = "size";
        stringArray[16] = "parseLine";
        stringArray[17] = "addAll";
        stringArray[18] = "getAt";
        stringArray[19] = "findCommand";
        stringArray[20] = "findCommand";
        stringArray[21] = "debug";
        stringArray[22] = "name";
        stringArray[23] = "execute";
        stringArray[24] = "println";
        stringArray[25] = "err";
        stringArray[26] = "reset";
        stringArray[27] = "a";
        stringArray[28] = "fg";
        stringArray[29] = "a";
        stringArray[30] = "ansi";
        stringArray[31] = "INTENSITY_BOLD";
        stringArray[32] = "RED";
        stringArray[33] = "message";
        stringArray[34] = "debug";
        stringArray[35] = "toString";
        stringArray[36] = "register";
        stringArray[37] = "execute";
        stringArray[38] = "register";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[39];
        Shell.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Shell.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Shell.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

