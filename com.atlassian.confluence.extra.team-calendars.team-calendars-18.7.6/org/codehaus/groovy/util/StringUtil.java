/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class StringUtil
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StringUtil() {
        MetaClass metaClass;
        CallSite[] callSiteArray = StringUtil.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    /*
     * WARNING - void declaration
     */
    public static String tr(String text, String source, String replacement) {
        void var2_2;
        Reference<String> source2 = new Reference<String>(source);
        Reference<void> replacement2 = new Reference<void>(var2_2);
        CallSite[] callSiteArray = StringUtil.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? !DefaultTypeTransformation.booleanUnbox(text) || !DefaultTypeTransformation.booleanUnbox(source2.get()) : !DefaultTypeTransformation.booleanUnbox(text) || !DefaultTypeTransformation.booleanUnbox(source2.get())) {
            return text;
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[0].callStatic(StringUtil.class, source2.get());
            source2.set(ShortTypeHandling.castToString(object));
        } else {
            String string = StringUtil.expandHyphen(source2.get());
            source2.set(string);
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[1].callStatic(StringUtil.class, (String)replacement2.get());
            replacement2.set((void)ShortTypeHandling.castToString(object));
        } else {
            String string = StringUtil.expandHyphen((String)replacement2.get());
            replacement2.set((void)string);
        }
        Object object = callSiteArray[2].call((String)replacement2.get(), callSiteArray[3].call(source2.get()), callSiteArray[4].call((Object)((String)replacement2.get()), callSiteArray[5].call(callSiteArray[6].call((String)replacement2.get()), 1)));
        replacement2.set((void)ShortTypeHandling.castToString(object));
        public class _tr_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference source;
            private /* synthetic */ Reference replacement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _tr_closure1(Object _outerInstance, Object _thisObject, Reference source, Reference replacement) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _tr_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.source = reference2 = source;
                this.replacement = reference = replacement;
            }

            public Object doCall(Object original) {
                CallSite[] callSiteArray = _tr_closure1.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(this.source.get(), original))) {
                    return callSiteArray[1].call(this.replacement.get(), callSiteArray[2].call(this.source.get(), original));
                }
                return original;
            }

            public String getSource() {
                CallSite[] callSiteArray = _tr_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.source.get());
            }

            public String getReplacement() {
                CallSite[] callSiteArray = _tr_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.replacement.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _tr_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "contains";
                stringArray[1] = "getAt";
                stringArray[2] = "lastIndexOf";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _tr_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_tr_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _tr_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return ShortTypeHandling.castToString(callSiteArray[7].call(callSiteArray[8].call((Object)text, new _tr_closure1(StringUtil.class, StringUtil.class, source2, replacement2))));
    }

    private static String expandHyphen(String text) {
        CallSite[] callSiteArray = StringUtil.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call((Object)text, "-"))) {
            return text;
        }
        public class _expandHyphen_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _expandHyphen_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _expandHyphen_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object all, Object begin, Object end) {
                CallSite[] callSiteArray = _expandHyphen_closure2.$getCallSiteArray();
                return callSiteArray[0].call(ScriptBytecodeAdapter.createRange(begin, end, true));
            }

            public Object call(Object all, Object begin, Object end) {
                CallSite[] callSiteArray = _expandHyphen_closure2.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, all, begin, end);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _expandHyphen_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "join";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _expandHyphen_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_expandHyphen_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _expandHyphen_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return ShortTypeHandling.castToString(callSiteArray[10].call(text, "(.)-(.)", new _expandHyphen_closure2(StringUtil.class, StringUtil.class)));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StringUtil.class) {
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
        stringArray[0] = "expandHyphen";
        stringArray[1] = "expandHyphen";
        stringArray[2] = "padRight";
        stringArray[3] = "size";
        stringArray[4] = "getAt";
        stringArray[5] = "minus";
        stringArray[6] = "size";
        stringArray[7] = "join";
        stringArray[8] = "collect";
        stringArray[9] = "contains";
        stringArray[10] = "replaceAll";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[11];
        StringUtil.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StringUtil.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StringUtil.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

