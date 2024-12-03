/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class CellEditorPrepareFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CellEditorPrepareFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = CellEditorPrepareFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = CellEditorPrepareFactory.$getCallSiteArray();
        return callSiteArray[0].call(Collections.class);
    }

    @Override
    public boolean isHandlesNodeChildren() {
        CallSite[] callSiteArray = CellEditorPrepareFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        CallSite[] callSiteArray = CellEditorPrepareFactory.$getCallSiteArray();
        Closure closure = childContent;
        ScriptBytecodeAdapter.setProperty(closure, null, callSiteArray[1].callGroovyObjectGetProperty(builder), "prepareEditorClosure");
        return false;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CellEditorPrepareFactory.class) {
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

    public /* synthetic */ boolean super$2$onNodeChildren(FactoryBuilderSupport factoryBuilderSupport, Object object, Closure closure) {
        return super.onNodeChildren(factoryBuilderSupport, object, closure);
    }

    public /* synthetic */ boolean super$2$isHandlesNodeChildren() {
        return super.isHandlesNodeChildren();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "emptyMap";
        stringArray[1] = "parentContext";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[2];
        CellEditorPrepareFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CellEditorPrepareFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CellEditorPrepareFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

