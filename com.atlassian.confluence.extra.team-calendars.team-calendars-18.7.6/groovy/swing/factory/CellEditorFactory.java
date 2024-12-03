/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.impl.ClosureCellEditor;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class CellEditorFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CellEditorFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = CellEditorFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = CellEditorFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name);
        return callSiteArray[1].callConstructor(ClosureCellEditor.class, null, attributes);
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        CallSite[] callSiteArray = CellEditorFactory.$getCallSiteArray();
        if (child instanceof Component) {
            ScriptBytecodeAdapter.setProperty(callSiteArray[2].call(callSiteArray[3].callGetProperty(parent), child), null, parent, "children");
        }
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        CallSite[] callSiteArray = CellEditorFactory.$getCallSiteArray();
        Object object = callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(builder));
        ScriptBytecodeAdapter.setProperty(object, null, node, "editorValue");
        Object object2 = callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(builder));
        ScriptBytecodeAdapter.setProperty(object2, null, node, "prepareEditor");
        Object object3 = node;
        ScriptBytecodeAdapter.setProperty(object3, null, parent, "cellEditor");
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CellEditorFactory.class) {
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

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsNull";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "plus";
        stringArray[3] = "children";
        stringArray[4] = "editorValueClosure";
        stringArray[5] = "context";
        stringArray[6] = "prepareEditorClosure";
        stringArray[7] = "context";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        CellEditorFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CellEditorFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CellEditorFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

