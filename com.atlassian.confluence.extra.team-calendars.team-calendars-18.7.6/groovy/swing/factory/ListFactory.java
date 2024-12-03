/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.binding.JListMetaMethods;
import groovy.swing.impl.ListWrapperListModel;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JList;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ListFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ListFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ListFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ListFactory.$getCallSiteArray();
        JList list = null;
        Object items = callSiteArray[0].call((Object)attributes, "items");
        if (value instanceof JList) {
            Object object = value;
            list = (JList)ScriptBytecodeAdapter.castToType(object, JList.class);
        } else if (value instanceof Vector || value instanceof Object[]) {
            Object object = callSiteArray[1].callConstructor(JList.class, value);
            list = (JList)ScriptBytecodeAdapter.castToType(object, JList.class);
        } else if (value instanceof List) {
            Object object = callSiteArray[2].callConstructor(JList.class, callSiteArray[3].callConstructor(ListWrapperListModel.class, items));
            list = (JList)ScriptBytecodeAdapter.castToType(object, JList.class);
        } else {
            Object object = callSiteArray[4].callConstructor(JList.class);
            list = (JList)ScriptBytecodeAdapter.castToType(object, JList.class);
        }
        if (items instanceof Vector) {
            callSiteArray[5].call((Object)list, ScriptBytecodeAdapter.createPojoWrapper((Vector)ScriptBytecodeAdapter.castToType(items, Vector.class), Vector.class));
        } else if (items instanceof Object[]) {
            callSiteArray[6].call((Object)list, ScriptBytecodeAdapter.createPojoWrapper((Object[])ScriptBytecodeAdapter.castToType(items, Object[].class), Object[].class));
        } else if (items instanceof List) {
            Object object = callSiteArray[7].callConstructor(ListWrapperListModel.class, items);
            ScriptBytecodeAdapter.setProperty(object, null, list, "model");
        }
        callSiteArray[8].call(JListMetaMethods.class, list);
        return list;
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        CallSite[] callSiteArray = ListFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call((Object)attributes, "listData"))) {
            Object listData = callSiteArray[10].call((Object)attributes, "listData");
            if (listData instanceof Vector || listData instanceof Object[]) {
                Object object = listData;
                ScriptBytecodeAdapter.setProperty(object, null, node, "listData");
            } else if (listData instanceof List) {
                Object object = callSiteArray[11].callConstructor(ListWrapperListModel.class, listData);
                ScriptBytecodeAdapter.setProperty(object, null, node, "model");
            } else if (listData instanceof Collection) {
                Object object = callSiteArray[12].call(listData);
                ScriptBytecodeAdapter.setProperty(object, null, node, "listData");
            } else {
                public class _onHandleNodeAttributes_closure1
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _onHandleNodeAttributes_closure1(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _onHandleNodeAttributes_closure1.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _onHandleNodeAttributes_closure1.$getCallSiteArray();
                        return it;
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _onHandleNodeAttributes_closure1.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _onHandleNodeAttributes_closure1.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[]{};
                        return new CallSiteArray(_onHandleNodeAttributes_closure1.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _onHandleNodeAttributes_closure1.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                Object[] objectArray = (Object[])ScriptBytecodeAdapter.asType(callSiteArray[13].call(listData, ScriptBytecodeAdapter.createList(new Object[0]), new _onHandleNodeAttributes_closure1(this, this)), Object[].class);
                ScriptBytecodeAdapter.setProperty(objectArray, null, node, "listData");
            }
        }
        return true;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ListFactory.class) {
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

    public /* synthetic */ boolean super$2$onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object object, Map map) {
        return super.onHandleNodeAttributes(factoryBuilderSupport, object, map);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "setListData";
        stringArray[6] = "setListData";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "enhanceMetaClass";
        stringArray[9] = "containsKey";
        stringArray[10] = "remove";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "toArray";
        stringArray[13] = "collect";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[14];
        ListFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ListFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ListFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

