/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.swing.impl.DefaultAction;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ActionFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ActionFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public boolean isHandlesNodeChildren() {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        Action action = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Action.class))) {
            Action action2;
            action = action2 = (Action)ScriptBytecodeAdapter.castToType(value, Action.class);
        } else if (callSiteArray[1].call((Object)attributes, name) instanceof Action) {
            Action action3;
            action = action3 = (Action)ScriptBytecodeAdapter.castToType(callSiteArray[2].call((Object)attributes, name), Action.class);
        } else {
            Object object = callSiteArray[3].callConstructor(DefaultAction.class);
            action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
        }
        return action;
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object action, Map attributes) {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        if (callSiteArray[4].call((Object)attributes, "closure") instanceof Closure && action instanceof DefaultAction) {
            Closure closure = (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[5].call((Object)attributes, "closure"), Closure.class);
            callSiteArray[6].call((Object)((DefaultAction)ScriptBytecodeAdapter.castToType(action, DefaultAction.class)), closure);
        }
        Object accel = callSiteArray[7].call((Object)attributes, "accelerator");
        if (ScriptBytecodeAdapter.compareNotEqual(accel, null)) {
            KeyStroke stroke = null;
            if (accel instanceof KeyStroke) {
                KeyStroke keyStroke;
                stroke = keyStroke = (KeyStroke)ScriptBytecodeAdapter.castToType(accel, KeyStroke.class);
            } else {
                Object object = callSiteArray[8].call(KeyStroke.class, callSiteArray[9].call(accel));
                stroke = (KeyStroke)ScriptBytecodeAdapter.castToType(object, KeyStroke.class);
            }
            callSiteArray[10].call(action, callSiteArray[11].callGetProperty(Action.class), stroke);
        }
        Object mnemonic = callSiteArray[12].call((Object)attributes, "mnemonic");
        if (ScriptBytecodeAdapter.compareNotEqual(mnemonic, null)) {
            if (!(mnemonic instanceof Number)) {
                Object object;
                mnemonic = object = callSiteArray[13].call(callSiteArray[14].call(mnemonic), 0);
            }
            callSiteArray[15].call(action, callSiteArray[16].callGetProperty(Action.class), ScriptBytecodeAdapter.createPojoWrapper((Integer)ScriptBytecodeAdapter.asType(mnemonic, Integer.class), Integer.class));
        }
        Object entry = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[17].call(callSiteArray[18].call(attributes)), Iterator.class);
        while (iterator.hasNext()) {
            entry = iterator.next();
            String propertyName = ShortTypeHandling.castToString(callSiteArray[19].call(entry));
            try {
                callSiteArray[20].call(InvokerHelper.class, action, propertyName, callSiteArray[21].call(entry));
            }
            catch (MissingPropertyException mpe) {
                Object object = callSiteArray[22].callCurrent((GroovyObject)this, propertyName);
                propertyName = ShortTypeHandling.castToString(object);
                callSiteArray[23].call(action, propertyName, callSiteArray[24].call(entry));
            }
        }
        return false;
    }

    @Override
    public boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        if (!(node instanceof DefaultAction)) {
            throw (Throwable)callSiteArray[25].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{callSiteArray[26].callGroovyObjectGetProperty(builder)}, new String[]{"", " only accepts a closure content when the action is generated by the node"}));
        }
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[27].callGetProperty(node), null)) {
            throw (Throwable)callSiteArray[28].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{callSiteArray[29].callGroovyObjectGetProperty(builder)}, new String[]{"", " already has an action set via the closure attribute, child content as action not allowed"}));
        }
        Closure closure = childContent;
        ScriptBytecodeAdapter.setProperty(closure, null, node, "closure");
        return false;
    }

    @Override
    public void setParent(FactoryBuilderSupport builder, Object parent, Object action) {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        try {
            callSiteArray[30].call(InvokerHelper.class, parent, "action", action);
        }
        catch (RuntimeException re) {
        }
        Object keyStroke = callSiteArray[31].call(action, "KeyStroke");
        if (parent instanceof JComponent) {
            JComponent component = (JComponent)ScriptBytecodeAdapter.castToType(parent, JComponent.class);
            KeyStroke stroke = null;
            if (keyStroke instanceof GString) {
                String string = (String)ScriptBytecodeAdapter.asType(keyStroke, String.class);
                keyStroke = string;
            }
            if (keyStroke instanceof String) {
                Object object = callSiteArray[32].call(KeyStroke.class, ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(keyStroke), String.class));
                stroke = (KeyStroke)ScriptBytecodeAdapter.castToType(object, KeyStroke.class);
            } else if (keyStroke instanceof KeyStroke) {
                KeyStroke keyStroke2;
                stroke = keyStroke2 = (KeyStroke)ScriptBytecodeAdapter.castToType(keyStroke, KeyStroke.class);
            }
            if (ScriptBytecodeAdapter.compareNotEqual(stroke, null)) {
                String key = ShortTypeHandling.castToString(callSiteArray[33].call(action));
                callSiteArray[34].call(callSiteArray[35].call(component), stroke, key);
                callSiteArray[36].call(callSiteArray[37].call(component), key, action);
            }
        }
    }

    public String capitalize(String text) {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        char ch = DefaultTypeTransformation.charUnbox(callSiteArray[38].call((Object)text, 0));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[39].call(Character.class, Character.valueOf(ch)))) {
            return text;
        }
        StringBuffer buffer = (StringBuffer)ScriptBytecodeAdapter.castToType(callSiteArray[40].callConstructor(StringBuffer.class, callSiteArray[41].call(text)), StringBuffer.class);
        callSiteArray[42].call((Object)buffer, callSiteArray[43].call(Character.class, Character.valueOf(ch)));
        callSiteArray[44].call((Object)buffer, callSiteArray[45].call((Object)text, 1));
        return ShortTypeHandling.castToString(callSiteArray[46].call(buffer));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ActionFactory.class) {
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

    public /* synthetic */ void super$2$setParent(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setParent(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ boolean super$2$onNodeChildren(FactoryBuilderSupport factoryBuilderSupport, Object object, Closure closure) {
        return super.onNodeChildren(factoryBuilderSupport, object, closure);
    }

    public /* synthetic */ boolean super$2$onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object object, Map map) {
        return super.onHandleNodeAttributes(factoryBuilderSupport, object, map);
    }

    public /* synthetic */ boolean super$2$isHandlesNodeChildren() {
        return super.isHandlesNodeChildren();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "get";
        stringArray[2] = "remove";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "get";
        stringArray[5] = "remove";
        stringArray[6] = "setClosure";
        stringArray[7] = "remove";
        stringArray[8] = "getKeyStroke";
        stringArray[9] = "toString";
        stringArray[10] = "putValue";
        stringArray[11] = "ACCELERATOR_KEY";
        stringArray[12] = "remove";
        stringArray[13] = "charAt";
        stringArray[14] = "toString";
        stringArray[15] = "putValue";
        stringArray[16] = "MNEMONIC_KEY";
        stringArray[17] = "iterator";
        stringArray[18] = "entrySet";
        stringArray[19] = "getKey";
        stringArray[20] = "setProperty";
        stringArray[21] = "getValue";
        stringArray[22] = "capitalize";
        stringArray[23] = "putValue";
        stringArray[24] = "getValue";
        stringArray[25] = "<$constructor$>";
        stringArray[26] = "currentName";
        stringArray[27] = "closure";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "currentName";
        stringArray[30] = "setProperty";
        stringArray[31] = "getValue";
        stringArray[32] = "getKeyStroke";
        stringArray[33] = "toString";
        stringArray[34] = "put";
        stringArray[35] = "getInputMap";
        stringArray[36] = "put";
        stringArray[37] = "getActionMap";
        stringArray[38] = "charAt";
        stringArray[39] = "isUpperCase";
        stringArray[40] = "<$constructor$>";
        stringArray[41] = "length";
        stringArray[42] = "append";
        stringArray[43] = "toUpperCase";
        stringArray[44] = "append";
        stringArray[45] = "substring";
        stringArray[46] = "toString";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[47];
        ActionFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ActionFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ActionFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

