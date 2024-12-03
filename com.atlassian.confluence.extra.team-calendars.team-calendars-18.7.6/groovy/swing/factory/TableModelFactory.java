/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.model.DefaultTableModel;
import groovy.model.ValueHolder;
import groovy.model.ValueModel;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class TableModelFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TableModelFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = TableModelFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = TableModelFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, TableModel.class))) {
            return value;
        }
        if (callSiteArray[1].call((Object)attributes, name) instanceof TableModel) {
            return callSiteArray[2].call((Object)attributes, name);
        }
        ValueModel model = (ValueModel)ScriptBytecodeAdapter.castToType(callSiteArray[3].call((Object)attributes, "model"), ValueModel.class);
        if (ScriptBytecodeAdapter.compareEqual(model, null)) {
            Object list = callSiteArray[4].call((Object)attributes, "list");
            if (ScriptBytecodeAdapter.compareEqual(list, null)) {
                Object object;
                list = object = callSiteArray[5].callConstructor(ArrayList.class);
            }
            Object object = callSiteArray[6].callConstructor(ValueHolder.class, list);
            model = (ValueModel)ScriptBytecodeAdapter.castToType(object, ValueModel.class);
        }
        return callSiteArray[7].callConstructor(DefaultTableModel.class, model);
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        Reference<Object> parent2 = new Reference<Object>(parent);
        CallSite[] callSiteArray = TableModelFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[8].callGetProperty(node), 0) && parent2.get() instanceof JTable) {
            boolean bl = false;
            ScriptBytecodeAdapter.setProperty(bl, null, parent2.get(), "autoCreateColumnsFromModel");
            public class _onNodeCompleted_closure1
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _onNodeCompleted_closure1(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _onNodeCompleted_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object e) {
                    CallSite[] callSiteArray = _onNodeCompleted_closure1.$getCallSiteArray();
                    if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(e), "model") && callSiteArray[1].callGetProperty(e) instanceof DefaultTableModel) {
                        Object object = callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(e));
                        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[4].callGetProperty(e), "columnModel");
                        callSiteArray[5].call(callSiteArray[6].callGetProperty(e));
                        return callSiteArray[7].call(callSiteArray[8].callGetProperty(e));
                    }
                    return null;
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _onNodeCompleted_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "propertyName";
                    stringArray[1] = "newValue";
                    stringArray[2] = "columnModel";
                    stringArray[3] = "newValue";
                    stringArray[4] = "source";
                    stringArray[5] = "revalidate";
                    stringArray[6] = "source";
                    stringArray[7] = "repaint";
                    stringArray[8] = "source";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[9];
                    _onNodeCompleted_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_onNodeCompleted_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _onNodeCompleted_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            Reference<PropertyChangeListener> listener = new Reference<PropertyChangeListener>((PropertyChangeListener)ScriptBytecodeAdapter.asType(new _onNodeCompleted_closure1(this, this), PropertyChangeListener.class));
            callSiteArray[9].call(parent2.get(), "model", listener.get());
            public class _onNodeCompleted_closure2
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference parent;
                private /* synthetic */ Reference listener;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _onNodeCompleted_closure2(Object _outerInstance, Object _thisObject, Reference parent, Reference listener) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.parent = reference2 = parent;
                    this.listener = reference = listener;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                    return callSiteArray[0].call(this.parent.get(), "model", this.listener.get());
                }

                public Object getParent() {
                    CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                    return this.parent.get();
                }

                public PropertyChangeListener getListener() {
                    CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                    return (PropertyChangeListener)ScriptBytecodeAdapter.castToType(this.listener.get(), PropertyChangeListener.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _onNodeCompleted_closure2.class) {
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
                    stringArray[0] = "removePropertyChangeListener";
                    return new CallSiteArray(_onNodeCompleted_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _onNodeCompleted_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[10].call((Object)builder, new _onNodeCompleted_closure2(this, this, parent2, listener));
            callSiteArray[11].call((Object)listener.get(), callSiteArray[12].callConstructor(PropertyChangeEvent.class, parent2.get(), "model", null, node));
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TableModelFactory.class) {
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

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "get";
        stringArray[2] = "remove";
        stringArray[3] = "remove";
        stringArray[4] = "remove";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "columnCount";
        stringArray[9] = "addPropertyChangeListener";
        stringArray[10] = "addDisposalClosure";
        stringArray[11] = "propertyChange";
        stringArray[12] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[13];
        TableModelFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TableModelFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TableModelFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

