/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.AggregateCompleter
 *  jline.console.completer.ArgumentCompleter
 *  jline.console.completer.Completer
 *  jline.console.completer.StringsCompleter
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.regex.Pattern;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.Interpreter;
import org.codehaus.groovy.tools.shell.commands.ImportCompleter;
import org.codehaus.groovy.tools.shell.util.PackageHelper;

public class ImportCommand
extends CommandSupport {
    private static final Pattern IMPORTED_ITEM_PATTERN;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ImportCommand(Groovysh shell) {
        CallSite[] callSiteArray = ImportCommand.$getCallSiteArray();
        super(shell, "import", ":i");
    }

    @Override
    public Completer getCompleter() {
        CallSite[] callSiteArray = ImportCommand.$getCallSiteArray();
        Completer impCompleter = (Completer)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(StringsCompleter.class, callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].callGroovyObjectGetProperty(this)), Completer.class);
        Completer asCompleter = (Completer)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(StringsCompleter.class, "as"), Completer.class);
        PackageHelper packageHelper = (PackageHelper)ScriptBytecodeAdapter.castToType(callSiteArray[4].callGroovyObjectGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), PackageHelper.class);
        Interpreter interp = (Interpreter)ScriptBytecodeAdapter.castToType(callSiteArray[6].callGroovyObjectGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), Interpreter.class);
        List argCompleters = ScriptBytecodeAdapter.createList(new Object[]{(Completer)callSiteArray[8].callConstructor(ArgumentCompleter.class, ScriptBytecodeAdapter.createList(new Object[]{impCompleter, callSiteArray[9].callConstructor(ImportCompleter.class, packageHelper, interp, false), asCompleter, null})), (Completer)callSiteArray[10].callConstructor(ArgumentCompleter.class, ScriptBytecodeAdapter.createList(new Object[]{impCompleter, callSiteArray[11].callConstructor(StringsCompleter.class, "static"), callSiteArray[12].callConstructor(ImportCompleter.class, packageHelper, interp, true), asCompleter, null}))});
        return (Completer)ScriptBytecodeAdapter.castToType(callSiteArray[13].callConstructor(AggregateCompleter.class, argCompleters), Completer.class);
    }

    @Override
    public Object execute(List<String> args) {
        Object object;
        Object object2;
        CallSite[] callSiteArray = ImportCommand.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            List<String> list = args;
            valueRecorder.record(list, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(list, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert args != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(args))) {
            callSiteArray[15].callCurrent((GroovyObject)this, "Command 'import' requires one or more arguments");
        }
        Object importSpec = callSiteArray[16].call(args, " ");
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[17].call(importSpec, IMPORTED_ITEM_PATTERN))) {
            GStringImpl msg = new GStringImpl(new Object[]{importSpec}, new String[]{"Invalid import definition: '", "'"});
            callSiteArray[18].call(callSiteArray[19].callGroovyObjectGetProperty(this), msg);
            callSiteArray[20].callCurrent((GroovyObject)this, msg);
        }
        importSpec = object2 = callSiteArray[21].call(importSpec, ";", "");
        List buff = ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[22].call((Object)"import ", callSiteArray[23].call(args, " "))});
        callSiteArray[24].call((Object)buff, "def dummp = false");
        Object type = null;
        type = object = callSiteArray[25].call(callSiteArray[26].callGroovyObjectGetProperty(this), callSiteArray[27].call((Object)buff, callSiteArray[28].callGroovyObjectGetProperty(this)));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[29].call(callSiteArray[30].callGroovyObjectGetProperty(this), importSpec))) {
            callSiteArray[31].call(callSiteArray[32].callGroovyObjectGetProperty(this), "Removed duplicate import from list");
        }
        callSiteArray[33].call(callSiteArray[34].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{importSpec}, new String[]{"Adding import: ", ""}));
        callSiteArray[35].call(callSiteArray[36].callGroovyObjectGetProperty(this), importSpec);
        Object object3 = callSiteArray[37].call(callSiteArray[38].callGroovyObjectGetProperty(this), ", ");
        callSiteArray[39].call(callSiteArray[40].callGroovyObjectGetProperty(this), callSiteArray[41].callGetPropertySafe(type));
        try {
            return object3;
        }
        catch (CompilationFailedException e) {
            GStringImpl msg = new GStringImpl(new Object[]{importSpec, callSiteArray[42].callGetProperty(e)}, new String[]{"Invalid import definition: '", "'; reason: ", ""});
            callSiteArray[43].call(callSiteArray[44].callGroovyObjectGetProperty(this), msg, e);
            Object object4 = callSiteArray[45].callCurrent((GroovyObject)this, msg);
            callSiteArray[46].call(callSiteArray[47].callGroovyObjectGetProperty(this), callSiteArray[48].callGetPropertySafe(type));
            try {
                return object4;
            }
            catch (Throwable throwable) {
                callSiteArray[52].call(callSiteArray[53].callGroovyObjectGetProperty(this), callSiteArray[54].callGetPropertySafe(type));
                throw throwable;
            }
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ImportCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    static {
        Object object = ScriptBytecodeAdapter.bitwiseNegate("[a-zA-Z0-9_. *]+;?$");
        IMPORTED_ITEM_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object, Pattern.class);
    }

    public /* synthetic */ Completer super$2$getCompleter() {
        return super.getCompleter();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "name";
        stringArray[2] = "shortcut";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "packageHelper";
        stringArray[5] = "shell";
        stringArray[6] = "interp";
        stringArray[7] = "shell";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "isEmpty";
        stringArray[15] = "fail";
        stringArray[16] = "join";
        stringArray[17] = "matches";
        stringArray[18] = "debug";
        stringArray[19] = "log";
        stringArray[20] = "fail";
        stringArray[21] = "replaceAll";
        stringArray[22] = "plus";
        stringArray[23] = "join";
        stringArray[24] = "leftShift";
        stringArray[25] = "parseClass";
        stringArray[26] = "classLoader";
        stringArray[27] = "join";
        stringArray[28] = "NEWLINE";
        stringArray[29] = "remove";
        stringArray[30] = "imports";
        stringArray[31] = "debug";
        stringArray[32] = "log";
        stringArray[33] = "debug";
        stringArray[34] = "log";
        stringArray[35] = "add";
        stringArray[36] = "imports";
        stringArray[37] = "join";
        stringArray[38] = "imports";
        stringArray[39] = "removeClassCacheEntry";
        stringArray[40] = "classLoader";
        stringArray[41] = "name";
        stringArray[42] = "message";
        stringArray[43] = "debug";
        stringArray[44] = "log";
        stringArray[45] = "fail";
        stringArray[46] = "removeClassCacheEntry";
        stringArray[47] = "classLoader";
        stringArray[48] = "name";
        stringArray[49] = "removeClassCacheEntry";
        stringArray[50] = "classLoader";
        stringArray[51] = "name";
        stringArray[52] = "removeClassCacheEntry";
        stringArray[53] = "classLoader";
        stringArray[54] = "name";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[55];
        ImportCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ImportCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ImportCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

