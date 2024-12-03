/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.swing.binding.AbstractSyntheticMetaMethods;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class JTableMetaMethods
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JTableMetaMethods() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JTableMetaMethods.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static void enhanceMetaClass(Object table) {
        CallSite[] callSiteArray = JTableMetaMethods.$getCallSiteArray();
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
                Object model = callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this));
                if (model instanceof DefaultTableModel) {
                    return callSiteArray[2].call(Collections.class, callSiteArray[3].call(model));
                }
                if (model instanceof groovy.model.DefaultTableModel) {
                    return callSiteArray[4].call(Collections.class, callSiteArray[5].callGetProperty(model));
                }
                return null;
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
                stringArray[2] = "unmodifiableList";
                stringArray[3] = "getDataVector";
                stringArray[4] = "unmodifiableList";
                stringArray[5] = "rows";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
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
                return callSiteArray[0].callCurrent(this, callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)));
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
                stringArray[0] = "getElement";
                stringArray[1] = "delegate";
                stringArray[2] = "selectedRow";
                stringArray[3] = "delegate";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
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

            public Object doCall() {
                CallSite[] callSiteArray = _enhanceMetaClass_closure3.$getCallSiteArray();
                Reference<Object> myTable = new Reference<Object>(callSiteArray[0].callGroovyObjectGetProperty(this));
                public class _closure4
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference myTable;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure4(Object _outerInstance, Object _thisObject, Reference myTable) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.myTable = reference = myTable;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return callSiteArray[0].callCurrent(this, this.myTable.get(), it);
                    }

                    public Object getMyTable() {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return this.myTable.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure4.class) {
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
                        stringArray[0] = "getElement";
                        return new CallSiteArray(_closure4.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure4.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[1].call(callSiteArray[2].call(myTable.get()), new _closure4(this, this.getThisObject(), myTable));
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

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "delegate";
                stringArray[1] = "collect";
                stringArray[2] = "getSelectedRows";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _enhanceMetaClass_closure3.$createCallSiteArray_1(stringArray);
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
        callSiteArray[0].call(AbstractSyntheticMetaMethods.class, table, ScriptBytecodeAdapter.createMap(new Object[]{"getElements", new _enhanceMetaClass_closure1(JTableMetaMethods.class, JTableMetaMethods.class), "getSelectedElement", new _enhanceMetaClass_closure2(JTableMetaMethods.class, JTableMetaMethods.class), "getSelectedElements", new _enhanceMetaClass_closure3(JTableMetaMethods.class, JTableMetaMethods.class)}));
    }

    public static Object getElement(JTable table, int row) {
        CallSite[] callSiteArray = JTableMetaMethods.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(row, -1)) {
            return null;
        }
        TableModel model = (TableModel)ScriptBytecodeAdapter.castToType(callSiteArray[1].callGetProperty(table), TableModel.class);
        if (model instanceof DefaultTableModel) {
            Map value = ScriptBytecodeAdapter.createMap(new Object[0]);
            TableColumnModel cmodel = (TableColumnModel)ScriptBytecodeAdapter.castToType(callSiteArray[2].callGetProperty(table), TableColumnModel.class);
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                int i = 0;
                while (ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[3].call(cmodel))) {
                    TableColumn c = (TableColumn)ScriptBytecodeAdapter.castToType(callSiteArray[4].call((Object)cmodel, i), TableColumn.class);
                    callSiteArray[5].call(value, callSiteArray[6].call(c), callSiteArray[7].call(table, row, callSiteArray[8].call(c)));
                    int n = i;
                    i = DefaultTypeTransformation.intUnbox(callSiteArray[9].call(n));
                }
            } else {
                int i = 0;
                while (ScriptBytecodeAdapter.compareLessThan(i, callSiteArray[10].call(cmodel))) {
                    TableColumn c = (TableColumn)ScriptBytecodeAdapter.castToType(callSiteArray[11].call((Object)cmodel, i), TableColumn.class);
                    callSiteArray[12].call(value, callSiteArray[13].call(c), callSiteArray[14].call(table, row, callSiteArray[15].call(c)));
                    int n = i;
                    int cfr_ignored_0 = n + 1;
                }
            }
            return value;
        }
        if (model instanceof groovy.model.DefaultTableModel) {
            Object rowValue = callSiteArray[16].callGetProperty(callSiteArray[17].call(model));
            if (ScriptBytecodeAdapter.compareEqual(rowValue, null)) {
                return null;
            }
            return callSiteArray[18].call(callSiteArray[19].call(InvokerHelper.class, rowValue), row);
        }
        return null;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JTableMetaMethods.class) {
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
        stringArray[0] = "enhance";
        stringArray[1] = "model";
        stringArray[2] = "columnModel";
        stringArray[3] = "getColumnCount";
        stringArray[4] = "getColumn";
        stringArray[5] = "put";
        stringArray[6] = "getIdentifier";
        stringArray[7] = "getValueAt";
        stringArray[8] = "getModelIndex";
        stringArray[9] = "next";
        stringArray[10] = "getColumnCount";
        stringArray[11] = "getColumn";
        stringArray[12] = "put";
        stringArray[13] = "getIdentifier";
        stringArray[14] = "getValueAt";
        stringArray[15] = "getModelIndex";
        stringArray[16] = "value";
        stringArray[17] = "getRowsModel";
        stringArray[18] = "getAt";
        stringArray[19] = "asList";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[20];
        JTableMetaMethods.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JTableMetaMethods.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JTableMetaMethods.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

