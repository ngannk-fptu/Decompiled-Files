/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class HistoryRecord
implements GroovyObject {
    private String allText;
    private int selectionStart;
    private int selectionEnd;
    private String scriptName;
    private Object result;
    private Throwable exception;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public HistoryRecord() {
        MetaClass metaClass;
        CallSite[] callSiteArray = HistoryRecord.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public String getTextToRun(boolean useSelection) {
        public class _getTextToRun_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getTextToRun_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _getTextToRun_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getTextToRun_closure1.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].call(it), "import");
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getTextToRun_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getTextToRun_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "startsWith";
                stringArray[1] = "trim";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _getTextToRun_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getTextToRun_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getTextToRun_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        CallSite[] callSiteArray = HistoryRecord.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (useSelection && this.selectionStart != this.selectionEnd) {
                Object before = callSiteArray[0].call(callSiteArray[1].call((Object)this.allText, ScriptBytecodeAdapter.createRange(0, this.selectionStart, false)), "\n");
                Object importLines = callSiteArray[2].call(before, new _getTextToRun_closure1(this, this));
                Object imports = callSiteArray[3].call(importLines, "\n");
                Object code = callSiteArray[4].call(callSiteArray[5].call(imports, "\n"), callSiteArray[6].call((Object)this.allText, ScriptBytecodeAdapter.createRange(this.selectionStart, this.selectionEnd, false)));
                return ShortTypeHandling.castToString(code);
            }
        } else if (useSelection && this.selectionStart != this.selectionEnd) {
            Object before = callSiteArray[7].call(callSiteArray[8].call((Object)this.allText, ScriptBytecodeAdapter.createRange(0, this.selectionStart, false)), "\n");
            Object importLines = callSiteArray[9].call(before, new _getTextToRun_closure1(this, this));
            Object imports = callSiteArray[10].call(importLines, "\n");
            Object code = callSiteArray[11].call(callSiteArray[12].call(imports, "\n"), callSiteArray[13].call((Object)this.allText, ScriptBytecodeAdapter.createRange(this.selectionStart, this.selectionEnd, false)));
            return ShortTypeHandling.castToString(code);
        }
        return this.allText;
    }

    public Object getValue() {
        CallSite[] callSiteArray = HistoryRecord.$getCallSiteArray();
        return DefaultTypeTransformation.booleanUnbox(this.exception) ? this.exception : this.result;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != HistoryRecord.class) {
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

    public String getAllText() {
        return this.allText;
    }

    public void setAllText(String string) {
        this.allText = string;
    }

    public int getSelectionStart() {
        return this.selectionStart;
    }

    public void setSelectionStart(int n) {
        this.selectionStart = n;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public void setSelectionEnd(int n) {
        this.selectionEnd = n;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public void setScriptName(String string) {
        this.scriptName = string;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object object) {
        this.result = object;
    }

    public Throwable getException() {
        return this.exception;
    }

    public void setException(Throwable throwable) {
        this.exception = throwable;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "split";
        stringArray[1] = "getAt";
        stringArray[2] = "findAll";
        stringArray[3] = "join";
        stringArray[4] = "plus";
        stringArray[5] = "plus";
        stringArray[6] = "getAt";
        stringArray[7] = "split";
        stringArray[8] = "getAt";
        stringArray[9] = "findAll";
        stringArray[10] = "join";
        stringArray[11] = "plus";
        stringArray[12] = "plus";
        stringArray[13] = "getAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[14];
        HistoryRecord.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(HistoryRecord.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = HistoryRecord.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

