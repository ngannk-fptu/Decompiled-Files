/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class RichActionWidgetFactory
extends AbstractFactory
implements GroovyObject {
    private static final Class[] ACTION_ARGS;
    private static final Class[] ICON_ARGS;
    private static final Class[] STRING_ARGS;
    private final Constructor actionCtor;
    private final Constructor iconCtor;
    private final Constructor stringCtor;
    private final Class klass;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RichActionWidgetFactory(Class klass) {
        MetaClass metaClass;
        CallSite[] callSiteArray = RichActionWidgetFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        try {
            Object object = callSiteArray[0].call((Object)klass, (Object)ACTION_ARGS);
            this.actionCtor = (Constructor)ScriptBytecodeAdapter.castToType(object, Constructor.class);
            Object object2 = callSiteArray[1].call((Object)klass, (Object)ICON_ARGS);
            this.iconCtor = (Constructor)ScriptBytecodeAdapter.castToType(object2, Constructor.class);
            Object object3 = callSiteArray[2].call((Object)klass, (Object)STRING_ARGS);
            this.stringCtor = (Constructor)ScriptBytecodeAdapter.castToType(object3, Constructor.class);
            Class clazz = klass;
            this.klass = ShortTypeHandling.castToClass(clazz);
        }
        catch (NoSuchMethodException ex) {
            callSiteArray[3].call(callSiteArray[4].call(Logger.class, "global"), callSiteArray[5].callGetProperty(Level.class), null, ex);
        }
        catch (SecurityException ex) {
            callSiteArray[6].call(callSiteArray[7].call(Logger.class, "global"), callSiteArray[8].callGetProperty(Level.class), null, ex);
        }
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        var5_5 = RichActionWidgetFactory.$getCallSiteArray();
        if (value instanceof GString) {
            var6_6 = (String)ScriptBytecodeAdapter.asType(value, String.class);
            value = var6_6;
        }
        if (!ScriptBytecodeAdapter.compareEqual(value, null)) ** GOTO lbl13
        var7_7 = var5_5[9].call(this.klass);
        return var7_7;
lbl13:
        // 1 sources

        if (!(value instanceof Action)) ** GOTO lbl19
        var8_8 = var5_5[10].call((Object)this.actionCtor, value);
        return var8_8;
lbl19:
        // 1 sources

        if (!(value instanceof Icon)) ** GOTO lbl25
        var9_9 = var5_5[11].call((Object)this.iconCtor, value);
        return var9_9;
lbl25:
        // 1 sources

        if (!(value instanceof String)) ** GOTO lbl31
        var10_10 = var5_5[12].call((Object)this.stringCtor, value);
        return var10_10;
lbl31:
        // 1 sources

        if (!DefaultTypeTransformation.booleanUnbox(var5_5[13].call((Object)this.klass, var5_5[14].call(value)))) ** GOTO lbl38
        var11_11 = value;
        try {
            return var11_11;
lbl38:
            // 1 sources

            throw (Throwable)var5_5[15].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name, var5_5[16].callGetProperty(this.klass)}, new String[]{"", " can only have a value argument of type javax.swing.Action, javax.swing.Icon, java.lang.String, or ", ""}));
        }
        catch (IllegalArgumentException e) {
            throw (Throwable)var5_5[17].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name, e}, new String[]{"Failed to create component for '", "' reason: ", ""}), e);
        }
        catch (InvocationTargetException e) {
            throw (Throwable)var5_5[18].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name, e}, new String[]{"Failed to create component for '", "' reason: ", ""}), e);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RichActionWidgetFactory.class) {
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
        Class[] classArray = new Class[]{Action.class};
        ACTION_ARGS = classArray;
        Class[] classArray2 = new Class[]{Icon.class};
        ICON_ARGS = classArray2;
        Class[] classArray3 = new Class[]{String.class};
        STRING_ARGS = classArray3;
    }

    public static Class[] getACTION_ARGS() {
        return ACTION_ARGS;
    }

    public static Class[] getICON_ARGS() {
        return ICON_ARGS;
    }

    public static Class[] getSTRING_ARGS() {
        return STRING_ARGS;
    }

    public final Constructor getActionCtor() {
        return this.actionCtor;
    }

    public final Constructor getIconCtor() {
        return this.iconCtor;
    }

    public final Constructor getStringCtor() {
        return this.stringCtor;
    }

    public final Class getKlass() {
        return this.klass;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getConstructor";
        stringArray[1] = "getConstructor";
        stringArray[2] = "getConstructor";
        stringArray[3] = "log";
        stringArray[4] = "getLogger";
        stringArray[5] = "INFO";
        stringArray[6] = "log";
        stringArray[7] = "getLogger";
        stringArray[8] = "SEVERE";
        stringArray[9] = "newInstance";
        stringArray[10] = "newInstance";
        stringArray[11] = "newInstance";
        stringArray[12] = "newInstance";
        stringArray[13] = "isAssignableFrom";
        stringArray[14] = "getClass";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "name";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[19];
        RichActionWidgetFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RichActionWidgetFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RichActionWidgetFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

