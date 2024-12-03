/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ColumnModelFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static final transient Logger log;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ColumnModelFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ColumnModelFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = ColumnModelFactory.$getCallSiteArray();
        if (value instanceof TableColumnModel) {
            return value;
        }
        Class<?> jxTableClass = null;
        try {
            Class<?> clazz;
            jxTableClass = clazz = Class.forName("org.jdesktop.swingx.JXTable");
        }
        catch (ClassNotFoundException ex) {
        }
        if (ScriptBytecodeAdapter.compareNotEqual(jxTableClass, null) && DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(jxTableClass, callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(builder))))) {
            return callSiteArray[3].call(Class.forName("org.jdesktop.swingx.table.DefaultTableColumnModelExt"));
        }
        return callSiteArray[4].callConstructor(DefaultTableColumnModel.class);
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        CallSite[] callSiteArray = ColumnModelFactory.$getCallSiteArray();
        if (parent instanceof JTable) {
            Object object = node;
            ScriptBytecodeAdapter.setProperty(object, null, parent, "columnModel");
        } else {
            Object object = DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call((Object)log, ScriptBytecodeAdapter.getField(ColumnModelFactory.class, Level.class, "WARNING"))) ? callSiteArray[6].call((Object)log, callSiteArray[7].call((Object)"ColumnModel must be a child of a table. Found: ", callSiteArray[8].call(parent))) : null;
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ColumnModelFactory.class) {
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

    static {
        Object object = ColumnModelFactory.$getCallSiteArray()[9].call(Logger.class, "groovy.swing.factory.ColumnModelFactory");
        log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
    }

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "isAssignableFrom";
        stringArray[1] = "getClass";
        stringArray[2] = "current";
        stringArray[3] = "newInstance";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "isLoggable";
        stringArray[6] = "warning";
        stringArray[7] = "plus";
        stringArray[8] = "getClass";
        stringArray[9] = "getLogger";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[10];
        ColumnModelFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ColumnModelFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ColumnModelFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

