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
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public final class ParseCode
implements GroovyObject {
    private static final ParseCode COMPLETE;
    private static final ParseCode INCOMPLETE;
    private static final ParseCode ERROR;
    private final int code;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    private ParseCode(int code) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ParseCode.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        int n = code;
        this.code = DefaultTypeTransformation.intUnbox(n);
    }

    public String toString() {
        CallSite[] callSiteArray = ParseCode.$getCallSiteArray();
        return ShortTypeHandling.castToString(this.code);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ParseCode.class) {
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
        Object object = ParseCode.$getCallSiteArray()[0].callConstructor(ParseCode.class, 0);
        COMPLETE = (ParseCode)ScriptBytecodeAdapter.castToType(object, ParseCode.class);
        Object object2 = ParseCode.$getCallSiteArray()[1].callConstructor(ParseCode.class, 1);
        INCOMPLETE = (ParseCode)ScriptBytecodeAdapter.castToType(object2, ParseCode.class);
        Object object3 = ParseCode.$getCallSiteArray()[2].callConstructor(ParseCode.class, 2);
        ERROR = (ParseCode)ScriptBytecodeAdapter.castToType(object3, ParseCode.class);
    }

    public static ParseCode getCOMPLETE() {
        return COMPLETE;
    }

    public static ParseCode getINCOMPLETE() {
        return INCOMPLETE;
    }

    public static ParseCode getERROR() {
        return ERROR;
    }

    public final int getCode() {
        return this.code;
    }

    public /* synthetic */ String super$1$toString() {
        return super.toString();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        ParseCode.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ParseCode.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ParseCode.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

