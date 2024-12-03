/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.text;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.awt.event.ActionEvent;
import java.lang.ref.SoftReference;
import java.util.regex.Matcher;
import javax.swing.AbstractAction;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class AutoIndentAction
extends AbstractAction
implements GroovyObject {
    private AttributeSet simpleAttributeSet;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AutoIndentAction() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AutoIndentAction.$getCallSiteArray();
        Object object = callSiteArray[0].callConstructor(SimpleAttributeSet.class);
        this.simpleAttributeSet = (AttributeSet)ScriptBytecodeAdapter.castToType(object, AttributeSet.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        CallSite[] callSiteArray = AutoIndentAction.$getCallSiteArray();
        Object inputArea = callSiteArray[1].callGetProperty(evt);
        Object rootElement = callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(inputArea));
        Object cursorPos = callSiteArray[4].call(inputArea);
        int rowNum = DefaultTypeTransformation.intUnbox(callSiteArray[5].call(rootElement, cursorPos));
        Object rowElement = callSiteArray[6].call(rootElement, rowNum);
        int startOffset = DefaultTypeTransformation.intUnbox(callSiteArray[7].call(rowElement));
        int endOffset = DefaultTypeTransformation.intUnbox(callSiteArray[8].call(rowElement));
        String rowContent = null;
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[9].call(callSiteArray[10].callGetProperty(inputArea), startOffset, callSiteArray[11].call((Object)endOffset, startOffset));
            rowContent = ShortTypeHandling.castToString(object);
        } else {
            Object object = callSiteArray[12].call(callSiteArray[13].callGetProperty(inputArea), startOffset, endOffset - startOffset);
            rowContent = ShortTypeHandling.castToString(object);
        }
        String contentBeforeCursor = ShortTypeHandling.castToString(callSiteArray[14].call(callSiteArray[15].callGetProperty(inputArea), startOffset, callSiteArray[16].call(cursorPos, startOffset)));
        Reference<String> whitespaceStr = new Reference<String>("");
        Matcher matcher = ScriptBytecodeAdapter.findRegex(rowContent, "(?m)^(\\s*).*\\n$");
        public class _actionPerformed_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference whitespaceStr;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _actionPerformed_closure1(Object _outerInstance, Object _thisObject, Reference whitespaceStr) {
                Reference reference;
                CallSite[] callSiteArray = _actionPerformed_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.whitespaceStr = reference = whitespaceStr;
            }

            public Object doCall(Object all, Object ws) {
                CallSite[] callSiteArray = _actionPerformed_closure1.$getCallSiteArray();
                Object object = ws;
                this.whitespaceStr.set(ShortTypeHandling.castToString(object));
                return object;
            }

            public Object call(Object all, Object ws) {
                CallSite[] callSiteArray = _actionPerformed_closure1.$getCallSiteArray();
                return callSiteArray[0].callCurrent(this, all, ws);
            }

            public String getWhitespaceStr() {
                CallSite[] callSiteArray = _actionPerformed_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.whitespaceStr.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _actionPerformed_closure1.class) {
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
                stringArray[0] = "doCall";
                return new CallSiteArray(_actionPerformed_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _actionPerformed_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[17].call((Object)matcher, new _actionPerformed_closure1(this, this, whitespaceStr));
        if (ScriptBytecodeAdapter.matchRegex(contentBeforeCursor, "(\\s)*")) {
            String string = contentBeforeCursor;
            whitespaceStr.set(string);
        }
        callSiteArray[18].call(callSiteArray[19].callGetProperty(inputArea), cursorPos, callSiteArray[20].call((Object)"\n", whitespaceStr.get()), this.simpleAttributeSet);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AutoIndentAction.class) {
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

    public AttributeSet getSimpleAttributeSet() {
        return this.simpleAttributeSet;
    }

    public void setSimpleAttributeSet(AttributeSet attributeSet) {
        this.simpleAttributeSet = attributeSet;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "source";
        stringArray[2] = "defaultRootElement";
        stringArray[3] = "document";
        stringArray[4] = "getCaretPosition";
        stringArray[5] = "getElementIndex";
        stringArray[6] = "getElement";
        stringArray[7] = "getStartOffset";
        stringArray[8] = "getEndOffset";
        stringArray[9] = "getText";
        stringArray[10] = "document";
        stringArray[11] = "minus";
        stringArray[12] = "getText";
        stringArray[13] = "document";
        stringArray[14] = "getText";
        stringArray[15] = "document";
        stringArray[16] = "minus";
        stringArray[17] = "each";
        stringArray[18] = "insertString";
        stringArray[19] = "document";
        stringArray[20] = "plus";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[21];
        AutoIndentAction.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AutoIndentAction.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AutoIndentAction.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

