/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.tools.shell.ParseCode;

public final class ParseStatus
implements GroovyObject {
    private final ParseCode code;
    private final Throwable cause;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ParseStatus(ParseCode code, Throwable cause) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ParseStatus.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ParseCode parseCode = code;
        this.code = (ParseCode)ScriptBytecodeAdapter.castToType(parseCode, ParseCode.class);
        Throwable throwable = cause;
        this.cause = (Throwable)ScriptBytecodeAdapter.castToType(throwable, Throwable.class);
    }

    public ParseStatus(ParseCode code) {
        CallSite[] callSiteArray = ParseStatus.$getCallSiteArray();
        this(code, null);
    }

    public ParseStatus(Throwable cause) {
        CallSite[] callSiteArray = ParseStatus.$getCallSiteArray();
        this((ParseCode)ScriptBytecodeAdapter.castToType(callSiteArray[0].callGetProperty(ParseCode.class), ParseCode.class), cause);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ParseStatus.class) {
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

    public final ParseCode getCode() {
        return this.code;
    }

    public final Throwable getCause() {
        return this.cause;
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[1];
        stringArray[0] = "ERROR";
        return new CallSiteArray(ParseStatus.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ParseStatus.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

