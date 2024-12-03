/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.IFileNameFinder;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class FileNameByRegexFinder
implements IFileNameFinder,
GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public FileNameByRegexFinder() {
        MetaClass metaClass;
        CallSite[] callSiteArray = FileNameByRegexFinder.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public List<String> getFileNames(String basedir, String pattern) {
        CallSite[] callSiteArray = FileNameByRegexFinder.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (List)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent(this, basedir, pattern, ""), List.class);
        }
        return this.getFileNames(basedir, pattern, "");
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public List<String> getFileNames(String basedir, String pattern, String excludesPattern) {
        void var3_3;
        Reference<String> pattern2 = new Reference<String>(pattern);
        Reference<void> excludesPattern2 = new Reference<void>(var3_3);
        CallSite[] callSiteArray = FileNameByRegexFinder.$getCallSiteArray();
        Reference<List> result = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        public class _getFileNames_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference pattern;
            private /* synthetic */ Reference excludesPattern;
            private /* synthetic */ Reference result;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getFileNames_closure1(Object _outerInstance, Object _thisObject, Reference pattern, Reference excludesPattern, Reference result) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.pattern = reference3 = pattern;
                this.excludesPattern = reference2 = excludesPattern;
                this.result = reference = result;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.findRegex(callSiteArray[0].callGetProperty(it), this.pattern.get())) && (!DefaultTypeTransformation.booleanUnbox(this.excludesPattern.get()) || !DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.findRegex(callSiteArray[1].callGetProperty(it), this.excludesPattern.get())))) {
                        return callSiteArray[2].call(this.result.get(), callSiteArray[3].callGetProperty(it));
                    }
                    return null;
                }
                if (DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.findRegex(callSiteArray[4].callGetProperty(it), this.pattern.get())) && (!DefaultTypeTransformation.booleanUnbox(this.excludesPattern.get()) || !DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.findRegex(callSiteArray[5].callGetProperty(it), this.excludesPattern.get())))) {
                    return callSiteArray[6].call(this.result.get(), callSiteArray[7].callGetProperty(it));
                }
                return null;
            }

            public String getPattern() {
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.pattern.get());
            }

            public String getExcludesPattern() {
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.excludesPattern.get());
            }

            public Object getResult() {
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                return this.result.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getFileNames_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "path";
                stringArray[1] = "path";
                stringArray[2] = "leftShift";
                stringArray[3] = "absolutePath";
                stringArray[4] = "path";
                stringArray[5] = "path";
                stringArray[6] = "leftShift";
                stringArray[7] = "absolutePath";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _getFileNames_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getFileNames_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getFileNames_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[1].call(callSiteArray[2].callConstructor(File.class, basedir), new _getFileNames_closure1(this, this, pattern2, excludesPattern2, result));
        return (List)ScriptBytecodeAdapter.castToType(result.get(), List.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != FileNameByRegexFinder.class) {
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
        stringArray[0] = "getFileNames";
        stringArray[1] = "eachFileRecurse";
        stringArray[2] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        FileNameByRegexFinder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(FileNameByRegexFinder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = FileNameByRegexFinder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

