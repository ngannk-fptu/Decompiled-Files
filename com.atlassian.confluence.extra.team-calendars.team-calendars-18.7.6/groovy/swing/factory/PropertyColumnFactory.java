/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.model.DefaultTableModel;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class PropertyColumnFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PropertyColumnFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = PropertyColumnFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = PropertyColumnFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name);
        Object current = callSiteArray[1].call(builder);
        if (current instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel)ScriptBytecodeAdapter.castToType(current, DefaultTableModel.class);
            String property = ShortTypeHandling.castToString(callSiteArray[2].call((Object)attributes, "propertyName"));
            if (ScriptBytecodeAdapter.compareEqual(property, null)) {
                throw (Throwable)callSiteArray[3].callConstructor(IllegalArgumentException.class, "Must specify a property for a propertyColumn");
            }
            Object header = callSiteArray[4].call((Object)attributes, "header");
            if (ScriptBytecodeAdapter.compareEqual(header, null)) {
                String string = "";
                header = string;
            }
            Class<Object> type = ShortTypeHandling.castToClass(callSiteArray[5].call((Object)attributes, "type"));
            if (ScriptBytecodeAdapter.compareEqual(type, null)) {
                Class<Object> clazz;
                type = clazz = Object.class;
            }
            Boolean editable = (Boolean)ScriptBytecodeAdapter.castToType(callSiteArray[6].call((Object)attributes, "editable"), Boolean.class);
            if (ScriptBytecodeAdapter.compareEqual(editable, null)) {
                Object object = callSiteArray[7].callGetProperty(Boolean.class);
                editable = (Boolean)ScriptBytecodeAdapter.castToType(object, Boolean.class);
            }
            return callSiteArray[8].call(model, header, property, type, callSiteArray[9].call(editable));
        }
        throw (Throwable)callSiteArray[10].callConstructor(RuntimeException.class, "propertyColumn must be a child of a tableModel");
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PropertyColumnFactory.class) {
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
        stringArray[0] = "checkValueIsNull";
        stringArray[1] = "getCurrent";
        stringArray[2] = "remove";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "remove";
        stringArray[5] = "remove";
        stringArray[6] = "remove";
        stringArray[7] = "TRUE";
        stringArray[8] = "addPropertyColumn";
        stringArray[9] = "booleanValue";
        stringArray[10] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[11];
        PropertyColumnFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(PropertyColumnFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = PropertyColumnFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

