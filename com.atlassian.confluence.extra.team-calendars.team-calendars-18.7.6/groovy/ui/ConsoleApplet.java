/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.ui.Console;
import java.lang.ref.SoftReference;
import javax.swing.JApplet;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ConsoleApplet
extends JApplet
implements GroovyObject {
    private Console console;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConsoleApplet() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ConsoleApplet.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void start() {
        CallSite[] callSiteArray = ConsoleApplet.$getCallSiteArray();
        Object object = callSiteArray[0].callConstructor(Console.class);
        this.console = (Console)ScriptBytecodeAdapter.castToType(object, Console.class);
        callSiteArray[1].call((Object)this.console, this);
    }

    @Override
    public void stop() {
        CallSite[] callSiteArray = ConsoleApplet.$getCallSiteArray();
        callSiteArray[2].call(this.console);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConsoleApplet.class) {
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

    public Console getConsole() {
        return this.console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }

    public /* synthetic */ void super$5$start() {
        super.start();
    }

    public /* synthetic */ void super$5$stop() {
        super.stop();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "run";
        stringArray[2] = "exit";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        ConsoleApplet.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ConsoleApplet.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConsoleApplet.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

