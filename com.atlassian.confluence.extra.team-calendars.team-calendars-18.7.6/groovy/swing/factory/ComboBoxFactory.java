/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.binding.JComboBoxMetaMethods;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JComboBox;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ComboBoxFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ComboBoxFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ComboBoxFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ComboBoxFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name, JComboBox.class);
        Object items = callSiteArray[1].call((Object)attributes, "items");
        JComboBox comboBox = null;
        if (items instanceof Vector) {
            Object object = callSiteArray[2].callConstructor(JComboBox.class, callSiteArray[3].call((Object)attributes, "items"));
            comboBox = (JComboBox)ScriptBytecodeAdapter.castToType(object, JComboBox.class);
        } else if (items instanceof List) {
            List list = (List)ScriptBytecodeAdapter.castToType(callSiteArray[4].call((Object)attributes, "items"), List.class);
            Object object = callSiteArray[5].callConstructor(JComboBox.class, callSiteArray[6].call(list));
            comboBox = (JComboBox)ScriptBytecodeAdapter.castToType(object, JComboBox.class);
        } else if (items instanceof Object[]) {
            Object object = callSiteArray[7].callConstructor(JComboBox.class, callSiteArray[8].call((Object)attributes, "items"));
            comboBox = (JComboBox)ScriptBytecodeAdapter.castToType(object, JComboBox.class);
        } else if (value instanceof JComboBox) {
            Object object = value;
            comboBox = (JComboBox)ScriptBytecodeAdapter.castToType(object, JComboBox.class);
        } else {
            Object object = callSiteArray[9].callConstructor(JComboBox.class);
            comboBox = (JComboBox)ScriptBytecodeAdapter.castToType(object, JComboBox.class);
        }
        callSiteArray[10].call(JComboBoxMetaMethods.class, comboBox);
        return comboBox;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ComboBoxFactory.class) {
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
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "get";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "remove";
        stringArray[4] = "remove";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "toArray";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "remove";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "enhanceMetaClass";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[11];
        ComboBoxFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ComboBoxFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ComboBoxFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

