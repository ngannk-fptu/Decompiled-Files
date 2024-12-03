/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.text.Format;
import java.util.Map;
import javax.swing.JFormattedTextField;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class FormattedTextFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public FormattedTextFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = FormattedTextFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = FormattedTextFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name);
        JFormattedTextField ftf = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call((Object)attributes, "format"))) {
            Object object = callSiteArray[2].callConstructor(JFormattedTextField.class, ScriptBytecodeAdapter.createPojoWrapper((Format)ScriptBytecodeAdapter.castToType(callSiteArray[3].call((Object)attributes, "format"), Format.class), Format.class));
            ftf = (JFormattedTextField)ScriptBytecodeAdapter.castToType(object, JFormattedTextField.class);
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call((Object)attributes, "value"))) {
            Object object = callSiteArray[5].callConstructor(JFormattedTextField.class, callSiteArray[6].call((Object)attributes, "value"));
            ftf = (JFormattedTextField)ScriptBytecodeAdapter.castToType(object, JFormattedTextField.class);
        } else {
            Object object = callSiteArray[7].callConstructor(JFormattedTextField.class);
            ftf = (JFormattedTextField)ScriptBytecodeAdapter.castToType(object, JFormattedTextField.class);
        }
        return ftf;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != FormattedTextFactory.class) {
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
        stringArray[1] = "containsKey";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "remove";
        stringArray[4] = "containsKey";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "remove";
        stringArray[7] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        FormattedTextFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(FormattedTextFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = FormattedTextFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

