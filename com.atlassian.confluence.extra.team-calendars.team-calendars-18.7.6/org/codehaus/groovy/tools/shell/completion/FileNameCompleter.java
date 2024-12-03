/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 *  jline.internal.Configuration
 *  jline.internal.Preconditions
 */
package org.codehaus.groovy.tools.shell.completion;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import jline.console.completer.Completer;
import jline.internal.Configuration;
import jline.internal.Preconditions;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class FileNameCompleter
implements Completer,
GroovyObject {
    private static final boolean OS_IS_WINDOWS;
    private final boolean blankSuffix;
    private final Object handleLeadingHyphen;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public FileNameCompleter() {
        MetaClass metaClass;
        boolean bl;
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        this.blankSuffix = bl = true;
        boolean bl2 = false;
        this.handleLeadingHyphen = bl2;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public FileNameCompleter(boolean blankSuffix) {
        MetaClass metaClass;
        boolean bl;
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        this.blankSuffix = bl = true;
        boolean bl2 = false;
        this.handleLeadingHyphen = bl2;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        boolean bl3 = blankSuffix;
        this.blankSuffix = DefaultTypeTransformation.booleanUnbox(bl3);
    }

    public FileNameCompleter(boolean blankSuffix, boolean handleLeadingHyphen) {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        this(blankSuffix);
        boolean bl = handleLeadingHyphen;
        this.handleLeadingHyphen = bl;
    }

    static {
        String os = ShortTypeHandling.castToString(FileNameCompleter.$getCallSiteArray()[0].call(Configuration.class));
        Object object = FileNameCompleter.$getCallSiteArray()[1].call((Object)os, "windows");
        OS_IS_WINDOWS = DefaultTypeTransformation.booleanUnbox(object);
    }

    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        callSiteArray[2].callStatic(Preconditions.class, candidates);
        String hyphenChar = null;
        if (ScriptBytecodeAdapter.compareEqual(buffer, null)) {
            String string;
            buffer = string = "";
        }
        if (OS_IS_WINDOWS) {
            Object object = callSiteArray[3].call(buffer, "/", "\\");
            buffer = ShortTypeHandling.castToString(object);
        }
        String translated = buffer;
        if (DefaultTypeTransformation.booleanUnbox(this.handleLeadingHyphen) && (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call((Object)translated, "'")) || DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call((Object)translated, "\"")))) {
            Object object = callSiteArray[6].call((Object)translated, 0);
            hyphenChar = ShortTypeHandling.castToString(object);
            Object object2 = callSiteArray[7].call((Object)translated, 1);
            translated = ShortTypeHandling.castToString(object2);
        }
        File homeDir = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[8].callCurrent(this);
            homeDir = (File)ScriptBytecodeAdapter.castToType(object, File.class);
        } else {
            File file;
            homeDir = file = this.getUserHome();
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call((Object)translated, callSiteArray[10].call((Object)"~", callSiteArray[11].callCurrent(this))))) {
                Object object = callSiteArray[12].call(callSiteArray[13].call(homeDir), callSiteArray[14].call((Object)translated, 1));
                translated = ShortTypeHandling.castToString(object);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call((Object)translated, "~"))) {
                Object object = callSiteArray[16].call(callSiteArray[17].call(homeDir));
                translated = ShortTypeHandling.castToString(object);
            } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[18].call(callSiteArray[19].callConstructor(File.class, translated)))) {
                String cwd = ShortTypeHandling.castToString(callSiteArray[20].call(callSiteArray[21].callCurrent(this)));
                Object object = callSiteArray[22].call(callSiteArray[23].call((Object)cwd, callSiteArray[24].callCurrent(this)), translated);
                translated = ShortTypeHandling.castToString(object);
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[25].call((Object)translated, callSiteArray[26].call((Object)"~", this.separator())))) {
            Object object = callSiteArray[27].call(callSiteArray[28].call(homeDir), callSiteArray[29].call((Object)translated, 1));
            translated = ShortTypeHandling.castToString(object);
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[30].call((Object)translated, "~"))) {
            Object object = callSiteArray[31].call(callSiteArray[32].call(homeDir));
            translated = ShortTypeHandling.castToString(object);
        } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[33].call(callSiteArray[34].callConstructor(File.class, translated)))) {
            String cwd = ShortTypeHandling.castToString(callSiteArray[35].call(this.getUserDir()));
            Object object = callSiteArray[36].call(callSiteArray[37].call((Object)cwd, this.separator()), translated);
            translated = ShortTypeHandling.castToString(object);
        }
        File file = (File)ScriptBytecodeAdapter.castToType(callSiteArray[38].callConstructor(File.class, translated), File.class);
        File dir = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[39].call((Object)translated, callSiteArray[40].callCurrent(this)))) {
                File file2;
                dir = file2 = file;
            } else {
                Object object = callSiteArray[41].call(file);
                dir = (File)ScriptBytecodeAdapter.castToType(object, File.class);
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[42].call((Object)translated, this.separator()))) {
            File file3;
            dir = file3 = file;
        } else {
            Object object = callSiteArray[43].call(file);
            dir = (File)ScriptBytecodeAdapter.castToType(object, File.class);
        }
        File[] entries = (File[])ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.compareEqual(dir, null) ? new File[]{} : callSiteArray[44].call(dir), File[].class);
        return DefaultTypeTransformation.intUnbox(callSiteArray[45].callCurrent((GroovyObject)this, ArrayUtil.createArray(buffer, translated, entries, candidates, hyphenChar)));
    }

    protected String separator() {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[46].callGetProperty(File.class));
    }

    protected File getUserHome() {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        return (File)ScriptBytecodeAdapter.castToType(callSiteArray[47].call(Configuration.class), File.class);
    }

    protected File getUserDir() {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        return (File)ScriptBytecodeAdapter.castToType(callSiteArray[48].callConstructor(File.class, "."), File.class);
    }

    protected int matchFiles(String buffer, String translated, File[] files, List<CharSequence> candidates, Object hyphenChar) {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(files, null)) {
            return -1;
        }
        int matches = 0;
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            File file = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[49].call(files), Iterator.class);
            while (iterator.hasNext()) {
                file = (File)ScriptBytecodeAdapter.castToType(iterator.next(), File.class);
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[50].call(callSiteArray[51].call(file), translated))) continue;
                int n = matches;
                matches = DefaultTypeTransformation.intUnbox(callSiteArray[52].call(n));
            }
        } else {
            File file = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[53].call(files), Iterator.class);
            while (iterator.hasNext()) {
                file = (File)ScriptBytecodeAdapter.castToType(iterator.next(), File.class);
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[54].call(callSiteArray[55].call(file), translated))) continue;
                int n = matches;
                int cfr_ignored_0 = n + 1;
            }
        }
        File file = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[56].call(files), Iterator.class);
        while (iterator.hasNext()) {
            file = (File)ScriptBytecodeAdapter.castToType(iterator.next(), File.class);
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[57].call(callSiteArray[58].call(file), translated))) continue;
            CharSequence name = (CharSequence)ScriptBytecodeAdapter.castToType(callSiteArray[59].call(file), CharSequence.class);
            if (matches == 1) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[60].call(file))) {
                    name = (CharSequence)ScriptBytecodeAdapter.castToType(callSiteArray[61].call((Object)name, callSiteArray[62].callCurrent(this)), CharSequence.class);
                } else if (this.blankSuffix && !DefaultTypeTransformation.booleanUnbox(hyphenChar)) {
                    name = (CharSequence)ScriptBytecodeAdapter.castToType(callSiteArray[63].call((Object)name, " "), CharSequence.class);
                }
            }
            callSiteArray[64].call(candidates, callSiteArray[65].call(callSiteArray[66].callCurrent(this, name, hyphenChar)));
        }
        int index = 0;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[67].call((Object)buffer, callSiteArray[68].callCurrent(this));
            index = DefaultTypeTransformation.intUnbox(object);
        } else {
            Object object = callSiteArray[69].call((Object)buffer, this.separator());
            index = DefaultTypeTransformation.intUnbox(object);
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return DefaultTypeTransformation.intUnbox(callSiteArray[70].call((Object)index, callSiteArray[71].call(callSiteArray[72].callCurrent(this))));
        }
        return DefaultTypeTransformation.intUnbox(callSiteArray[73].call((Object)index, callSiteArray[74].call(this.separator())));
    }

    protected CharSequence render(CharSequence name, String hyphenChar) {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(hyphenChar, null)) {
            return (CharSequence)ScriptBytecodeAdapter.castToType(callSiteArray[75].callCurrent(this, name, hyphenChar), CharSequence.class);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[76].call((Object)name, " "))) {
            return (CharSequence)ScriptBytecodeAdapter.castToType(callSiteArray[77].callCurrent(this, name, "'"), CharSequence.class);
        }
        return name;
    }

    private String escapedNameInHyphens(String name, String hyphen) {
        CallSite[] callSiteArray = FileNameCompleter.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[78].call(callSiteArray[79].call((Object)hyphen, callSiteArray[80].call(callSiteArray[81].call(name, "\\", "\\\\"), hyphen, callSiteArray[82].call((Object)"\\", hyphen))), hyphen));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != FileNameCompleter.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getOsName";
        stringArray[1] = "contains";
        stringArray[2] = "checkNotNull";
        stringArray[3] = "replace";
        stringArray[4] = "startsWith";
        stringArray[5] = "startsWith";
        stringArray[6] = "getAt";
        stringArray[7] = "substring";
        stringArray[8] = "getUserHome";
        stringArray[9] = "startsWith";
        stringArray[10] = "plus";
        stringArray[11] = "separator";
        stringArray[12] = "plus";
        stringArray[13] = "getPath";
        stringArray[14] = "substring";
        stringArray[15] = "startsWith";
        stringArray[16] = "getAbsolutePath";
        stringArray[17] = "getParentFile";
        stringArray[18] = "isAbsolute";
        stringArray[19] = "<$constructor$>";
        stringArray[20] = "getAbsolutePath";
        stringArray[21] = "getUserDir";
        stringArray[22] = "plus";
        stringArray[23] = "plus";
        stringArray[24] = "separator";
        stringArray[25] = "startsWith";
        stringArray[26] = "plus";
        stringArray[27] = "plus";
        stringArray[28] = "getPath";
        stringArray[29] = "substring";
        stringArray[30] = "startsWith";
        stringArray[31] = "getAbsolutePath";
        stringArray[32] = "getParentFile";
        stringArray[33] = "isAbsolute";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "getAbsolutePath";
        stringArray[36] = "plus";
        stringArray[37] = "plus";
        stringArray[38] = "<$constructor$>";
        stringArray[39] = "endsWith";
        stringArray[40] = "separator";
        stringArray[41] = "getParentFile";
        stringArray[42] = "endsWith";
        stringArray[43] = "getParentFile";
        stringArray[44] = "listFiles";
        stringArray[45] = "matchFiles";
        stringArray[46] = "separator";
        stringArray[47] = "getUserHome";
        stringArray[48] = "<$constructor$>";
        stringArray[49] = "iterator";
        stringArray[50] = "startsWith";
        stringArray[51] = "getAbsolutePath";
        stringArray[52] = "next";
        stringArray[53] = "iterator";
        stringArray[54] = "startsWith";
        stringArray[55] = "getAbsolutePath";
        stringArray[56] = "iterator";
        stringArray[57] = "startsWith";
        stringArray[58] = "getAbsolutePath";
        stringArray[59] = "getName";
        stringArray[60] = "isDirectory";
        stringArray[61] = "plus";
        stringArray[62] = "separator";
        stringArray[63] = "plus";
        stringArray[64] = "add";
        stringArray[65] = "toString";
        stringArray[66] = "render";
        stringArray[67] = "lastIndexOf";
        stringArray[68] = "separator";
        stringArray[69] = "lastIndexOf";
        stringArray[70] = "plus";
        stringArray[71] = "length";
        stringArray[72] = "separator";
        stringArray[73] = "plus";
        stringArray[74] = "length";
        stringArray[75] = "escapedNameInHyphens";
        stringArray[76] = "contains";
        stringArray[77] = "escapedNameInHyphens";
        stringArray[78] = "plus";
        stringArray[79] = "plus";
        stringArray[80] = "replace";
        stringArray[81] = "replace";
        stringArray[82] = "plus";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[83];
        FileNameCompleter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(FileNameCompleter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = FileNameCompleter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

