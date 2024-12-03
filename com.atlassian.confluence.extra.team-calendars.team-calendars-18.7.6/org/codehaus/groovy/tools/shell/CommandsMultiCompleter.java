/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.AggregateCompleter
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import jline.console.completer.AggregateCompleter;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.util.Logger;

public class CommandsMultiCompleter
extends AggregateCompleter
implements GroovyObject {
    protected final Logger log;
    private List list;
    private boolean dirty;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CommandsMultiCompleter() {
        MetaClass metaClass;
        boolean bl;
        List list;
        CallSite[] callSiteArray = CommandsMultiCompleter.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.list = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.dirty = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public Object add(Command command) {
        CallSite[] callSiteArray = CommandsMultiCompleter.$getCallSiteArray();
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
        Object c = callSiteArray[2].callGetProperty(command);
        if (DefaultTypeTransformation.booleanUnbox(c)) {
            boolean bl;
            callSiteArray[3].call((Object)this.list, c);
            callSiteArray[4].call((Object)this.log, new GStringImpl(new Object[]{callSiteArray[5].call(this.list), callSiteArray[6].callGetProperty(command)}, new String[]{"Added completer[", "] for command: ", ""}));
            this.dirty = bl = true;
            return bl;
        }
        return null;
    }

    public void refresh() {
        boolean bl;
        CallSite[] callSiteArray = CommandsMultiCompleter.$getCallSiteArray();
        callSiteArray[7].call((Object)this.log, "Refreshing the completer list");
        callSiteArray[8].call(callSiteArray[9].callGroovyObjectGetProperty(this));
        callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(this), this.list);
        this.dirty = bl = false;
    }

    public int complete(String buffer, int pos, List cand) {
        CallSite[] callSiteArray = CommandsMultiCompleter.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = buffer;
            valueRecorder.record(string, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(string, null);
            valueRecorder.record(bl, 15);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert buffer != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (this.dirty) {
                callSiteArray[12].callCurrent(this);
            }
        } else if (this.dirty) {
            this.refresh();
        }
        return DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.invokeMethodOnSuperN(AggregateCompleter.class, this, "complete", new Object[]{buffer, pos, cand}));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CommandsMultiCompleter.class) {
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

    public List getList() {
        return this.list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public /* synthetic */ int super$2$complete(String string, int n, List list) {
        return super.complete(string, n, list);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "completer";
        stringArray[3] = "leftShift";
        stringArray[4] = "debug";
        stringArray[5] = "size";
        stringArray[6] = "name";
        stringArray[7] = "debug";
        stringArray[8] = "clear";
        stringArray[9] = "completers";
        stringArray[10] = "addAll";
        stringArray[11] = "completers";
        stringArray[12] = "refresh";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[13];
        CommandsMultiCompleter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CommandsMultiCompleter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CommandsMultiCompleter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

