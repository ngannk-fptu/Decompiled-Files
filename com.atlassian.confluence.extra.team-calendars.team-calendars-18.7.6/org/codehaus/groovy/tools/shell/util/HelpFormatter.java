/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.Terminal
 *  org.apache.commons.cli.HelpFormatter
 *  org.apache.commons.cli.Option
 *  org.apache.commons.cli.Options
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import jline.Terminal;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
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
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class HelpFormatter
extends org.apache.commons.cli.HelpFormatter
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public HelpFormatter() {
        MetaClass metaClass;
        CallSite[] callSiteArray = HelpFormatter.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        int n = 2;
        ScriptBytecodeAdapter.setGroovyObjectProperty(n, HelpFormatter.class, this, "leftPadding");
        int n2 = 4;
        ScriptBytecodeAdapter.setGroovyObjectProperty(n2, HelpFormatter.class, this, "descPadding");
    }

    public int getDefaultWidth() {
        CallSite[] callSiteArray = HelpFormatter.$getCallSiteArray();
        return DefaultTypeTransformation.intUnbox(callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(Terminal.class)), 1));
    }

    protected StringBuffer renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad) {
        Reference<StringBuffer> sb2 = new Reference<StringBuffer>(sb);
        Reference<Integer> width2 = new Reference<Integer>(width);
        Reference<Integer> descPad2 = new Reference<Integer>(descPad);
        CallSite[] callSiteArray = HelpFormatter.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            StringBuffer stringBuffer = sb2.get();
            valueRecorder.record(stringBuffer, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(stringBuffer, null);
            valueRecorder.record(bl, 11);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert sb != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        ValueRecorder valueRecorder2 = new ValueRecorder();
        try {
            Options options2 = options;
            valueRecorder2.record(options2, 8);
            if (DefaultTypeTransformation.booleanUnbox(options2)) {
                valueRecorder2.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert options", valueRecorder2), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder2.clear();
            throw throwable;
        }
        Reference<List> prefixes = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        Reference<String> lpad = new Reference<String>(ShortTypeHandling.castToString(callSiteArray[3].call((Object)" ", leftPad)));
        public class _renderOptions_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _renderOptions_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _renderOptions_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Option a, Option b) {
                CallSite[] callSiteArray = _renderOptions_closure1.$getCallSiteArray();
                return ScriptBytecodeAdapter.compareTo(ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(a), " ") ? callSiteArray[1].callGetProperty(a) : callSiteArray[2].callGetProperty(a), ScriptBytecodeAdapter.compareEqual(callSiteArray[3].callGetProperty(b), " ") ? callSiteArray[4].callGetProperty(b) : callSiteArray[5].callGetProperty(b));
            }

            public Object call(Option a, Option b) {
                CallSite[] callSiteArray = _renderOptions_closure1.$getCallSiteArray();
                return callSiteArray[6].callCurrent(this, a, b);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _renderOptions_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "opt";
                stringArray[1] = "longOpt";
                stringArray[2] = "opt";
                stringArray[3] = "opt";
                stringArray[4] = "longOpt";
                stringArray[5] = "opt";
                stringArray[6] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _renderOptions_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_renderOptions_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _renderOptions_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<List> opts = new Reference<List>((List)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(callSiteArray[5].call(callSiteArray[6].callGetProperty(options)), new _renderOptions_closure1(this, this)), List.class));
        public class _renderOptions_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference lpad;
            private /* synthetic */ Reference prefixes;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _renderOptions_closure2(Object _outerInstance, Object _thisObject, Reference lpad, Reference prefixes) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _renderOptions_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.lpad = reference2 = lpad;
                this.prefixes = reference = prefixes;
            }

            public Object doCall(Option option) {
                CallSite[] callSiteArray = _renderOptions_closure2.$getCallSiteArray();
                StringBuffer buff = (StringBuffer)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(StringBuffer.class, 8), StringBuffer.class);
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[1].callGetProperty(option), " ")) {
                    callSiteArray[2].call((Object)buff, new GStringImpl(new Object[]{this.lpad.get(), callSiteArray[3].callGroovyObjectGetProperty(this), callSiteArray[4].callGetProperty(option)}, new String[]{"", "    ", "", ""}));
                } else {
                    callSiteArray[5].call((Object)buff, new GStringImpl(new Object[]{this.lpad.get(), callSiteArray[6].callGroovyObjectGetProperty(this), callSiteArray[7].callGetProperty(option)}, new String[]{"", "", "", ""}));
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].call(option))) {
                        callSiteArray[9].call((Object)buff, new GStringImpl(new Object[]{callSiteArray[10].callGroovyObjectGetProperty(this), callSiteArray[11].callGetProperty(option)}, new String[]{", ", "", ""}));
                    }
                }
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(option))) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[13].call(option))) {
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(option))) {
                            callSiteArray[15].call((Object)buff, new GStringImpl(new Object[]{callSiteArray[16].callGetProperty(option)}, new String[]{"[=", "]"}));
                        } else {
                            callSiteArray[17].call((Object)buff, new GStringImpl(new Object[]{callSiteArray[18].callGetProperty(option)}, new String[]{"=", ""}));
                        }
                    } else {
                        callSiteArray[19].call((Object)buff, " ");
                    }
                }
                return callSiteArray[20].call(this.prefixes.get(), buff);
            }

            public Object call(Option option) {
                CallSite[] callSiteArray = _renderOptions_closure2.$getCallSiteArray();
                return callSiteArray[21].callCurrent((GroovyObject)this, option);
            }

            public String getLpad() {
                CallSite[] callSiteArray = _renderOptions_closure2.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.lpad.get());
            }

            public List getPrefixes() {
                CallSite[] callSiteArray = _renderOptions_closure2.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.prefixes.get(), List.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _renderOptions_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "opt";
                stringArray[2] = "leftShift";
                stringArray[3] = "longOptPrefix";
                stringArray[4] = "longOpt";
                stringArray[5] = "leftShift";
                stringArray[6] = "optPrefix";
                stringArray[7] = "opt";
                stringArray[8] = "hasLongOpt";
                stringArray[9] = "leftShift";
                stringArray[10] = "longOptPrefix";
                stringArray[11] = "longOpt";
                stringArray[12] = "hasArg";
                stringArray[13] = "hasArgName";
                stringArray[14] = "hasOptionalArg";
                stringArray[15] = "leftShift";
                stringArray[16] = "argName";
                stringArray[17] = "leftShift";
                stringArray[18] = "argName";
                stringArray[19] = "leftShift";
                stringArray[20] = "leftShift";
                stringArray[21] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[22];
                _renderOptions_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_renderOptions_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _renderOptions_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[7].call((Object)opts.get(), new _renderOptions_closure2(this, this, lpad, prefixes));
        public class _renderOptions_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _renderOptions_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _renderOptions_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(StringBuffer a, StringBuffer b) {
                CallSite[] callSiteArray = _renderOptions_closure3.$getCallSiteArray();
                return ScriptBytecodeAdapter.compareTo(callSiteArray[0].call(a), callSiteArray[1].call(b));
            }

            public Object call(StringBuffer a, StringBuffer b) {
                CallSite[] callSiteArray = _renderOptions_closure3.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[2].callCurrent(this, a, b);
                }
                return this.doCall(a, b);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _renderOptions_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "size";
                stringArray[1] = "size";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _renderOptions_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_renderOptions_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _renderOptions_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<Integer> maxPrefix = new Reference<Integer>((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[8].call(callSiteArray[9].call((Object)prefixes.get(), new _renderOptions_closure3(this, this))), Integer.class));
        Reference<String> dpad = new Reference<String>(ShortTypeHandling.castToString(callSiteArray[10].call((Object)" ", DefaultTypeTransformation.intUnbox(descPad2.get()))));
        public class _renderOptions_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefixes;
            private /* synthetic */ Reference maxPrefix;
            private /* synthetic */ Reference dpad;
            private /* synthetic */ Reference descPad;
            private /* synthetic */ Reference sb;
            private /* synthetic */ Reference width;
            private /* synthetic */ Reference opts;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _renderOptions_closure4(Object _outerInstance, Object _thisObject, Reference prefixes, Reference maxPrefix, Reference dpad, Reference descPad, Reference sb, Reference width, Reference opts) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                Reference reference5;
                Reference reference6;
                Reference reference7;
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefixes = reference7 = prefixes;
                this.maxPrefix = reference6 = maxPrefix;
                this.dpad = reference5 = dpad;
                this.descPad = reference4 = descPad;
                this.sb = reference3 = sb;
                this.width = reference2 = width;
                this.opts = reference = opts;
            }

            public Object doCall(Option option, int i) {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                Object buff = callSiteArray[0].callConstructor(StringBuffer.class, callSiteArray[1].call(callSiteArray[2].call(this.prefixes.get(), i)));
                if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[3].call(buff), this.maxPrefix.get())) {
                    callSiteArray[4].call(buff, callSiteArray[5].call((Object)" ", callSiteArray[6].call(this.maxPrefix.get(), callSiteArray[7].call(buff))));
                }
                callSiteArray[8].call(buff, this.dpad.get());
                int nextLineTabStop = DefaultTypeTransformation.intUnbox(callSiteArray[9].call(this.maxPrefix.get(), this.descPad.get()));
                String text = ShortTypeHandling.castToString(callSiteArray[10].call(buff, callSiteArray[11].callGetProperty(option)));
                callSiteArray[12].callCurrent(this, this.sb.get(), this.width.get(), nextLineTabStop, text);
                if (ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[13].call(callSiteArray[14].call(this.opts.get()), 1))) {
                    return callSiteArray[15].call(this.sb.get(), callSiteArray[16].callGroovyObjectGetProperty(this));
                }
                return null;
            }

            public Object call(Option option, int i) {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return callSiteArray[17].callCurrent(this, option, i);
            }

            public List getPrefixes() {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.prefixes.get(), List.class);
            }

            public Integer getMaxPrefix() {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return (Integer)ScriptBytecodeAdapter.castToType(this.maxPrefix.get(), Integer.class);
            }

            public String getDpad() {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.dpad.get());
            }

            public int getDescPad() {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return DefaultTypeTransformation.intUnbox(this.descPad.get());
            }

            public StringBuffer getSb() {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return (StringBuffer)ScriptBytecodeAdapter.castToType(this.sb.get(), StringBuffer.class);
            }

            public int getWidth() {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return DefaultTypeTransformation.intUnbox(this.width.get());
            }

            public List getOpts() {
                CallSite[] callSiteArray = _renderOptions_closure4.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.opts.get(), List.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _renderOptions_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "toString";
                stringArray[2] = "getAt";
                stringArray[3] = "size";
                stringArray[4] = "leftShift";
                stringArray[5] = "multiply";
                stringArray[6] = "minus";
                stringArray[7] = "size";
                stringArray[8] = "leftShift";
                stringArray[9] = "plus";
                stringArray[10] = "leftShift";
                stringArray[11] = "description";
                stringArray[12] = "renderWrappedText";
                stringArray[13] = "minus";
                stringArray[14] = "size";
                stringArray[15] = "leftShift";
                stringArray[16] = "newLine";
                stringArray[17] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[18];
                _renderOptions_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_renderOptions_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _renderOptions_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[11].call((Object)opts.get(), new _renderOptions_closure4(this, this, prefixes, maxPrefix, dpad, descPad2, sb2, width2, opts));
        return sb2.get();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != HelpFormatter.class) {
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

    public /* synthetic */ StringBuffer super$2$renderOptions(StringBuffer stringBuffer, int n, Options options, int n2, int n3) {
        return super.renderOptions(stringBuffer, n, options, n2, n3);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "minus";
        stringArray[1] = "terminalWidth";
        stringArray[2] = "terminal";
        stringArray[3] = "multiply";
        stringArray[4] = "sort";
        stringArray[5] = "values";
        stringArray[6] = "shortOpts";
        stringArray[7] = "each";
        stringArray[8] = "size";
        stringArray[9] = "max";
        stringArray[10] = "multiply";
        stringArray[11] = "eachWithIndex";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[12];
        HelpFormatter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(HelpFormatter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = HelpFormatter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

