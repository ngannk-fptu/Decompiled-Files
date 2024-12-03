/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Collection;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.ParseStatus;
import org.codehaus.groovy.tools.shell.Parsing;
import org.codehaus.groovy.tools.shell.RelaxedParser;
import org.codehaus.groovy.tools.shell.RigidParser;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.tools.shell.util.Preferences;

public class Parser
implements GroovyObject {
    private static final String NEWLINE;
    private static final Logger log;
    private final Parsing delegate;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Parser() {
        MetaClass metaClass;
        CallSite[] callSiteArray = Parser.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        String flavor = ShortTypeHandling.castToString(callSiteArray[0].call(Preferences.class));
        callSiteArray[1].call((Object)log, new GStringImpl(new Object[]{flavor}, new String[]{"Using parser flavor: ", ""}));
        String string = flavor;
        if (ScriptBytecodeAdapter.isCase(string, callSiteArray[2].callGetProperty(Preferences.class))) {
            Object object = callSiteArray[3].callConstructor(RelaxedParser.class);
            this.delegate = (Parsing)ScriptBytecodeAdapter.castToType(object, Parsing.class);
        } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[4].callGetProperty(Preferences.class))) {
            Object object = callSiteArray[5].callConstructor(RigidParser.class);
            this.delegate = (Parsing)ScriptBytecodeAdapter.castToType(object, Parsing.class);
        } else {
            callSiteArray[6].call((Object)log, new GStringImpl(new Object[]{flavor, callSiteArray[7].callGetProperty(Preferences.class)}, new String[]{"Invalid parser flavor: ", "; using default: ", ""}));
            Object object = callSiteArray[8].callConstructor(RigidParser.class);
            this.delegate = (Parsing)ScriptBytecodeAdapter.castToType(object, Parsing.class);
        }
    }

    public ParseStatus parse(Collection<String> buffer) {
        CallSite[] callSiteArray = Parser.$getCallSiteArray();
        return (ParseStatus)ScriptBytecodeAdapter.castToType(callSiteArray[9].call((Object)this.delegate, buffer), ParseStatus.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Parser.class) {
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
        Object object = Parser.$getCallSiteArray()[10].call(System.class, "line.separator");
        NEWLINE = ShortTypeHandling.castToString(object);
        Object object2 = Parser.$getCallSiteArray()[11].call(Logger.class, Parser.class);
        log = (Logger)ScriptBytecodeAdapter.castToType(object2, Logger.class);
    }

    public static String getNEWLINE() {
        return NEWLINE;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getParserFlavor";
        stringArray[1] = "debug";
        stringArray[2] = "PARSER_RELAXED";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "PARSER_RIGID";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "error";
        stringArray[7] = "PARSER_RIGID";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "parse";
        stringArray[10] = "getProperty";
        stringArray[11] = "create";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[12];
        Parser.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Parser.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Parser.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

