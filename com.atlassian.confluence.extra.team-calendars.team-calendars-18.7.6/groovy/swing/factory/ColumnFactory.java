/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ColumnFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static final transient Logger log;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ColumnFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ColumnFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = ColumnFactory.$getCallSiteArray();
        if (value instanceof TableColumn) {
            return value;
        }
        TableColumn node = null;
        Class<?> jxTableClass = null;
        try {
            Class<?> clazz;
            jxTableClass = clazz = Class.forName("org.jdesktop.swingx.JXTable");
        }
        catch (ClassNotFoundException ex) {
        }
        if (ScriptBytecodeAdapter.compareNotEqual(jxTableClass, null) && callSiteArray[0].callGroovyObjectGetProperty(builder) instanceof TableColumnModel) {
            Object object = callSiteArray[1].call(Class.forName("org.jdesktop.swingx.table.TableColumnExt"));
            node = (TableColumn)ScriptBytecodeAdapter.castToType(object, TableColumn.class);
        } else {
            Object object = callSiteArray[2].callConstructor(TableColumn.class);
            node = (TableColumn)ScriptBytecodeAdapter.castToType(object, TableColumn.class);
        }
        if (ScriptBytecodeAdapter.compareNotEqual(value, null)) {
            Object object = callSiteArray[3].call(value);
            ScriptBytecodeAdapter.setProperty(object, null, node, "identifier");
            callSiteArray[4].call((Object)attributes, "identifier");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callGetProperty(attributes))) {
            if (callSiteArray[6].callGetProperty(attributes) instanceof Collection) {
                Object object = callSiteArray[7].callGetProperty(attributes);
                Object min = callSiteArray[8].call(object, 0);
                Object pref = callSiteArray[9].call(object, 1);
                Object max = callSiteArray[10].call(object, 2);
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (!DefaultTypeTransformation.booleanUnbox(pref) && !DefaultTypeTransformation.booleanUnbox(max)) {
                        int n = 0;
                        ScriptBytecodeAdapter.setProperty(n, null, node, "minWidth");
                        Integer n2 = (Integer)ScriptBytecodeAdapter.asType(min, Integer.class);
                        ScriptBytecodeAdapter.setProperty(n2, null, node, "preferredWidth");
                        Object object2 = callSiteArray[11].callGetProperty(Integer.class);
                        ScriptBytecodeAdapter.setProperty(object2, null, node, "maxWidth");
                    } else {
                        if (DefaultTypeTransformation.booleanUnbox(min)) {
                            Integer n = (Integer)ScriptBytecodeAdapter.asType(min, Integer.class);
                            ScriptBytecodeAdapter.setProperty(n, null, node, "minWidth");
                        }
                        if (DefaultTypeTransformation.booleanUnbox(pref)) {
                            Integer n = (Integer)ScriptBytecodeAdapter.asType(pref, Integer.class);
                            ScriptBytecodeAdapter.setProperty(n, null, node, "preferredWidth");
                        }
                        if (DefaultTypeTransformation.booleanUnbox(max)) {
                            Integer n = (Integer)ScriptBytecodeAdapter.asType(max, Integer.class);
                            ScriptBytecodeAdapter.setProperty(n, null, node, "maxWidth");
                        }
                    }
                } else if (!DefaultTypeTransformation.booleanUnbox(pref) && !DefaultTypeTransformation.booleanUnbox(max)) {
                    int n = 0;
                    ScriptBytecodeAdapter.setProperty(n, null, node, "minWidth");
                    Integer n3 = (Integer)ScriptBytecodeAdapter.asType(min, Integer.class);
                    ScriptBytecodeAdapter.setProperty(n3, null, node, "preferredWidth");
                    Object object3 = callSiteArray[12].callGetProperty(Integer.class);
                    ScriptBytecodeAdapter.setProperty(object3, null, node, "maxWidth");
                } else {
                    if (DefaultTypeTransformation.booleanUnbox(min)) {
                        Integer n = (Integer)ScriptBytecodeAdapter.asType(min, Integer.class);
                        ScriptBytecodeAdapter.setProperty(n, null, node, "minWidth");
                    }
                    if (DefaultTypeTransformation.booleanUnbox(pref)) {
                        Integer n = (Integer)ScriptBytecodeAdapter.asType(pref, Integer.class);
                        ScriptBytecodeAdapter.setProperty(n, null, node, "preferredWidth");
                    }
                    if (DefaultTypeTransformation.booleanUnbox(max)) {
                        Integer n = (Integer)ScriptBytecodeAdapter.asType(max, Integer.class);
                        ScriptBytecodeAdapter.setProperty(n, null, node, "maxWidth");
                    }
                }
            } else if (callSiteArray[13].callGetProperty(attributes) instanceof Number) {
                Object object = callSiteArray[14].call(callSiteArray[15].callGetProperty(attributes));
                ScriptBytecodeAdapter.setProperty(object, null, node, "minWidth");
                Object object4 = callSiteArray[16].call(callSiteArray[17].callGetProperty(attributes));
                ScriptBytecodeAdapter.setProperty(object4, null, node, "preferredWidth");
                Object object5 = callSiteArray[18].call(callSiteArray[19].callGetProperty(attributes));
                ScriptBytecodeAdapter.setProperty(object5, null, node, "maxWidth");
            }
            callSiteArray[20].call((Object)attributes, "width");
        }
        return node;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        CallSite[] callSiteArray = ColumnFactory.$getCallSiteArray();
        if (!(parent instanceof TableColumnModel)) {
            Object object = DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call((Object)log, ScriptBytecodeAdapter.getField(ColumnFactory.class, Level.class, "WARNING"))) ? callSiteArray[22].call((Object)log, callSiteArray[23].call((Object)"Column must be a child of a columnModel. Found ", callSiteArray[24].call(parent))) : null;
        }
        callSiteArray[25].call(parent, node);
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        block1: {
            Object object;
            block2: {
                CallSite[] callSiteArray = ColumnFactory.$getCallSiteArray();
                if (!(parent instanceof TableColumn)) {
                    Object object2 = DefaultTypeTransformation.booleanUnbox(callSiteArray[26].call((Object)log, ScriptBytecodeAdapter.getField(ColumnFactory.class, Level.class, "WARNING"))) ? callSiteArray[27].call((Object)log, callSiteArray[28].call((Object)"Renderer must be a child of a tableColumn. Found ", callSiteArray[29].call(parent))) : null;
                }
                if (!(child instanceof TableCellRenderer)) break block1;
                object = callSiteArray[30].call(builder);
                if (!ScriptBytecodeAdapter.isCase(object, "headerRenderer")) break block2;
                boolean bl = true;
                ScriptBytecodeAdapter.setProperty(bl, null, child, "tableHeader");
                Object object3 = child;
                ScriptBytecodeAdapter.setProperty(object3, null, parent, "headerRenderer");
                break block1;
            }
            if (!ScriptBytecodeAdapter.isCase(object, "cellRenderer")) break block1;
            boolean bl = false;
            ScriptBytecodeAdapter.setProperty(bl, null, child, "tableHeader");
            Object object4 = child;
            ScriptBytecodeAdapter.setProperty(object4, null, parent, "cellRenderer");
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ColumnFactory.class) {
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
        Object object = ColumnFactory.$getCallSiteArray()[31].call(Logger.class, "groovy.swing.factory.ColumnFactory");
        log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
    }

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "current";
        stringArray[1] = "newInstance";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "toString";
        stringArray[4] = "remove";
        stringArray[5] = "width";
        stringArray[6] = "width";
        stringArray[7] = "width";
        stringArray[8] = "getAt";
        stringArray[9] = "getAt";
        stringArray[10] = "getAt";
        stringArray[11] = "MAX_VALUE";
        stringArray[12] = "MAX_VALUE";
        stringArray[13] = "width";
        stringArray[14] = "intValue";
        stringArray[15] = "width";
        stringArray[16] = "intValue";
        stringArray[17] = "width";
        stringArray[18] = "intValue";
        stringArray[19] = "width";
        stringArray[20] = "remove";
        stringArray[21] = "isLoggable";
        stringArray[22] = "warning";
        stringArray[23] = "plus";
        stringArray[24] = "getClass";
        stringArray[25] = "addColumn";
        stringArray[26] = "isLoggable";
        stringArray[27] = "warning";
        stringArray[28] = "plus";
        stringArray[29] = "getClass";
        stringArray[30] = "getCurrentName";
        stringArray[31] = "getLogger";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[32];
        ColumnFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ColumnFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ColumnFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

