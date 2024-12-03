/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
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

public class ClosureColumnFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ClosureColumnFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ClosureColumnFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ClosureColumnFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name);
        Object current = callSiteArray[1].call(builder);
        if (current instanceof DefaultTableModel) {
            DefaultTableModel model = (DefaultTableModel)ScriptBytecodeAdapter.castToType(current, DefaultTableModel.class);
            Object header = callSiteArray[2].call((Object)attributes, "header");
            if (ScriptBytecodeAdapter.compareEqual(header, null)) {
                String string = "";
                header = string;
            }
            Closure readClosure = (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[3].call((Object)attributes, "read"), Closure.class);
            if (ScriptBytecodeAdapter.compareEqual(readClosure, null)) {
                throw (Throwable)callSiteArray[4].callConstructor(IllegalArgumentException.class, "Must specify 'read' Closure property for a closureColumn");
            }
            Closure writeClosure = (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[5].call((Object)attributes, "write"), Closure.class);
            Class<Object> type = ShortTypeHandling.castToClass(callSiteArray[6].call((Object)attributes, "type"));
            if (ScriptBytecodeAdapter.compareEqual(type, null)) {
                Class<Object> clazz;
                type = clazz = Object.class;
            }
            return callSiteArray[7].call(model, header, readClosure, writeClosure, type);
        }
        throw (Throwable)callSiteArray[8].callConstructor(RuntimeException.class, "closureColumn must be a child of a tableModel");
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ClosureColumnFactory.class) {
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
        stringArray[3] = "remove";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "remove";
        stringArray[6] = "remove";
        stringArray[7] = "addClosureColumn";
        stringArray[8] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[9];
        ClosureColumnFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ClosureColumnFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ClosureColumnFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

