/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.binding.AbstractSyntheticBinding;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.SoftReference;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class JListSelectedElementBinding
extends AbstractSyntheticBinding
implements PropertyChangeListener,
ListSelectionListener,
GroovyObject {
    private JList boundList;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    protected JListSelectedElementBinding(PropertyBinding source, TargetBinding target, String propertyName) {
        MetaClass metaClass;
        CallSite[] callSiteArray = JListSelectedElementBinding.$getCallSiteArray();
        super(source, target, JList.class, propertyName);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public synchronized void syntheticBind() {
        JList jList;
        CallSite[] callSiteArray = JListSelectedElementBinding.$getCallSiteArray();
        this.boundList = jList = (JList)ScriptBytecodeAdapter.castToType(callSiteArray[0].call((PropertyBinding)ScriptBytecodeAdapter.castToType(callSiteArray[1].callGroovyObjectGetProperty(this), PropertyBinding.class)), JList.class);
        callSiteArray[2].call(this.boundList, "selectionModel", this);
        callSiteArray[3].call((Object)this.boundList, this);
    }

    @Override
    public synchronized void syntheticUnbind() {
        CallSite[] callSiteArray = JListSelectedElementBinding.$getCallSiteArray();
        callSiteArray[4].call(this.boundList, "selectionModel", this);
        callSiteArray[5].call((Object)this.boundList, this);
        Object var2_2 = null;
        this.boundList = (JList)ScriptBytecodeAdapter.castToType(var2_2, JList.class);
    }

    @Override
    public void setTargetBinding(TargetBinding target) {
        CallSite[] callSiteArray = JListSelectedElementBinding.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractSyntheticBinding.class, this, "setTargetBinding", new Object[]{target});
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        CallSite[] callSiteArray = JListSelectedElementBinding.$getCallSiteArray();
        callSiteArray[6].callCurrent(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        CallSite[] callSiteArray = JListSelectedElementBinding.$getCallSiteArray();
        callSiteArray[7].callCurrent(this);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JListSelectedElementBinding.class) {
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

    public JList getBoundList() {
        return this.boundList;
    }

    public void setBoundList(JList jList) {
        this.boundList = jList;
    }

    public /* synthetic */ void super$3$setTargetBinding(TargetBinding targetBinding) {
        super.setTargetBinding(targetBinding);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getBean";
        stringArray[1] = "sourceBinding";
        stringArray[2] = "addPropertyChangeListener";
        stringArray[3] = "addListSelectionListener";
        stringArray[4] = "removePropertyChangeListener";
        stringArray[5] = "removeListSelectionListener";
        stringArray[6] = "update";
        stringArray[7] = "update";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        JListSelectedElementBinding.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JListSelectedElementBinding.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JListSelectedElementBinding.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

