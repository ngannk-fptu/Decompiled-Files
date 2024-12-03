/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.AntBuilder;
import groovy.util.IFileNameFinder;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class FileNameFinder
implements IFileNameFinder,
GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public FileNameFinder() {
        MetaClass metaClass;
        CallSite[] callSiteArray = FileNameFinder.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public List<String> getFileNames(String basedir, String pattern) {
        CallSite[] callSiteArray = FileNameFinder.$getCallSiteArray();
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"dir", basedir, "includes", pattern})), List.class);
    }

    @Override
    public List<String> getFileNames(String basedir, String pattern, String excludesPattern) {
        CallSite[] callSiteArray = FileNameFinder.$getCallSiteArray();
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"dir", basedir, "includes", pattern, "excludes", excludesPattern})), List.class);
    }

    public List<String> getFileNames(Map args) {
        Reference<Map> args2 = new Reference<Map>(args);
        CallSite[] callSiteArray = FileNameFinder.$getCallSiteArray();
        Object ant = callSiteArray[2].callConstructor(AntBuilder.class);
        public class _getFileNames_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference args;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getFileNames_closure1(Object _outerInstance, Object _thisObject, Reference args) {
                Reference reference;
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.args = reference = args;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, this.args.get());
            }

            public Map getArgs() {
                CallSite[] callSiteArray = _getFileNames_closure1.$getCallSiteArray();
                return (Map)ScriptBytecodeAdapter.castToType(this.args.get(), Map.class);
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "fileset";
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
        Object scanner = callSiteArray[3].call(ant, new _getFileNames_closure1(this, this, args2));
        List fls = ScriptBytecodeAdapter.createList(new Object[0]);
        Object f = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(scanner), Iterator.class);
        while (iterator.hasNext()) {
            f = iterator.next();
            callSiteArray[5].call((Object)fls, callSiteArray[6].call(f));
        }
        return (List)ScriptBytecodeAdapter.castToType(fls, List.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != FileNameFinder.class) {
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
        stringArray[1] = "getFileNames";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "fileScanner";
        stringArray[4] = "iterator";
        stringArray[5] = "leftShift";
        stringArray[6] = "getAbsolutePath";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[7];
        FileNameFinder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(FileNameFinder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = FileNameFinder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

