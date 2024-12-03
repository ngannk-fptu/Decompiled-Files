/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.GroovyTestCase;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class GroovyLogTestCase
extends GroovyTestCase
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GroovyLogTestCase() {
        MetaClass metaClass;
        CallSite[] callSiteArray = GroovyLogTestCase.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static String stringLog(Level level, String qualifier, Closure yield) {
        CallSite[] callSiteArray = GroovyLogTestCase.$getCallSiteArray();
        Logger logger = (Logger)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(Logger.class, qualifier), Logger.class);
        Object usesParentHandlers = callSiteArray[1].callGetProperty(logger);
        boolean bl = false;
        ScriptBytecodeAdapter.setProperty(bl, null, logger, "useParentHandlers");
        Object out = callSiteArray[2].callConstructor(ByteArrayOutputStream.class, 1024);
        Handler stringHandler = (Handler)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(StreamHandler.class, out, callSiteArray[4].callConstructor(SimpleFormatter.class)), Handler.class);
        Object object = callSiteArray[5].callGetProperty(Level.class);
        ScriptBytecodeAdapter.setProperty(object, null, stringHandler, "level");
        callSiteArray[6].call((Object)logger, stringHandler);
        callSiteArray[7].callStatic(GroovyLogTestCase.class, level, qualifier, yield);
        Object object2 = callSiteArray[8].callGetProperty(Level.class);
        ScriptBytecodeAdapter.setProperty(object2, null, logger, "level");
        callSiteArray[9].call(stringHandler);
        callSiteArray[10].call(out);
        callSiteArray[11].call((Object)logger, stringHandler);
        Object object3 = usesParentHandlers;
        ScriptBytecodeAdapter.setProperty(object3, null, logger, "useParentHandlers");
        return ShortTypeHandling.castToString(callSiteArray[12].call(out));
    }

    public static Object withLevel(Level level, String qualifier, Closure yield) {
        CallSite[] callSiteArray = GroovyLogTestCase.$getCallSiteArray();
        Logger logger = (Logger)ScriptBytecodeAdapter.castToType(callSiteArray[13].call(Logger.class, qualifier), Logger.class);
        Object loglevel = callSiteArray[14].callGetProperty(logger);
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call((Object)logger, level))) {
            Level level2 = level;
            ScriptBytecodeAdapter.setProperty(level2, null, logger, "level");
        }
        Object result = callSiteArray[16].call(yield);
        Object object = loglevel;
        ScriptBytecodeAdapter.setProperty(object, null, logger, "level");
        return result;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GroovyLogTestCase.class) {
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
        stringArray[0] = "getLogger";
        stringArray[1] = "useParentHandlers";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "ALL";
        stringArray[6] = "addHandler";
        stringArray[7] = "withLevel";
        stringArray[8] = "OFF";
        stringArray[9] = "flush";
        stringArray[10] = "close";
        stringArray[11] = "removeHandler";
        stringArray[12] = "toString";
        stringArray[13] = "getLogger";
        stringArray[14] = "level";
        stringArray[15] = "isLoggable";
        stringArray[16] = "call";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[17];
        GroovyLogTestCase.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GroovyLogTestCase.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GroovyLogTestCase.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

