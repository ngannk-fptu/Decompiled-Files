/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.binding.AbstractSyntheticMetaMethods;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class JComboBoxMetaMethods
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JComboBoxMetaMethods() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JComboBoxMetaMethods.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static void enhanceMetaClass(JComboBox comboBox) {
        CallSite[] callSiteArray = JComboBoxMetaMethods.$getCallSiteArray();
        public class _enhanceMetaClass_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _enhanceMetaClass_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _enhanceMetaClass_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _enhanceMetaClass_closure1.$getCallSiteArray();
                ComboBoxModel model = (ComboBoxModel)ScriptBytecodeAdapter.castToType(callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this)), ComboBoxModel.class);
                List results = ScriptBytecodeAdapter.createList(new Object[0]);
                int size = DefaultTypeTransformation.intUnbox(callSiteArray[2].callGetProperty(model));
                if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    int i = 0;
                    while (i < size) {
                        callSiteArray[3].call((Object)results, callSiteArray[4].call((Object)model, i));
                        int n = i;
                        i = DefaultTypeTransformation.intUnbox(callSiteArray[5].call(n));
                    }
                } else {
                    int i = 0;
                    while (i < size) {
                        callSiteArray[6].call((Object)results, callSiteArray[7].call((Object)model, i));
                        int n = i;
                        int cfr_ignored_0 = n + 1;
                    }
                }
                return results;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _enhanceMetaClass_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "model";
                stringArray[1] = "delegate";
                stringArray[2] = "size";
                stringArray[3] = "plus";
                stringArray[4] = "getElementAt";
                stringArray[5] = "next";
                stringArray[6] = "plus";
                stringArray[7] = "getElementAt";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _enhanceMetaClass_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_enhanceMetaClass_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _enhanceMetaClass_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _enhanceMetaClass_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _enhanceMetaClass_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _enhanceMetaClass_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _enhanceMetaClass_closure2.$getCallSiteArray();
                return callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _enhanceMetaClass_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "selectedItem";
                stringArray[1] = "delegate";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _enhanceMetaClass_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_enhanceMetaClass_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _enhanceMetaClass_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _enhanceMetaClass_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _enhanceMetaClass_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _enhanceMetaClass_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object item) {
                CallSite[] callSiteArray = _enhanceMetaClass_closure3.$getCallSiteArray();
                Object object = item;
                ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[0].callGroovyObjectGetProperty(this), "selectedItem");
                return object;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _enhanceMetaClass_closure3.class) {
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
                stringArray[0] = "delegate";
                return new CallSiteArray(_enhanceMetaClass_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _enhanceMetaClass_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[0].call(AbstractSyntheticMetaMethods.class, comboBox, ScriptBytecodeAdapter.createMap(new Object[]{"getElements", new _enhanceMetaClass_closure1(JComboBoxMetaMethods.class, JComboBoxMetaMethods.class), "getSelectedElement", new _enhanceMetaClass_closure2(JComboBoxMetaMethods.class, JComboBoxMetaMethods.class), "setSelectedElement", new _enhanceMetaClass_closure3(JComboBoxMetaMethods.class, JComboBoxMetaMethods.class)}));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JComboBoxMetaMethods.class) {
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

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[1];
        stringArray[0] = "enhance";
        return new CallSiteArray(JComboBoxMetaMethods.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JComboBoxMetaMethods.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

