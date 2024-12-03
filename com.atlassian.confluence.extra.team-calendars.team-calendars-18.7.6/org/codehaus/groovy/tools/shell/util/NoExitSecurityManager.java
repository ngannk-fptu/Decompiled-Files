/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.security.Permission;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class NoExitSecurityManager
extends SecurityManager
implements GroovyObject {
    private final SecurityManager parent;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public NoExitSecurityManager(SecurityManager parent) {
        MetaClass metaClass;
        CallSite[] callSiteArray = NoExitSecurityManager.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        SecurityManager securityManager = parent;
        this.parent = (SecurityManager)ScriptBytecodeAdapter.castToType(securityManager, SecurityManager.class);
    }

    public NoExitSecurityManager() {
        CallSite[] callSiteArray = NoExitSecurityManager.$getCallSiteArray();
        this((SecurityManager)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(System.class), SecurityManager.class));
    }

    @Override
    public void checkPermission(Permission perm) {
        CallSite[] callSiteArray = NoExitSecurityManager.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(this.parent, null)) {
            callSiteArray[1].call((Object)this.parent, perm);
        }
    }

    @Override
    public void checkExit(int code) {
        CallSite[] callSiteArray = NoExitSecurityManager.$getCallSiteArray();
        throw (Throwable)callSiteArray[2].callConstructor(SecurityException.class, "Use of System.exit() is forbidden!");
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != NoExitSecurityManager.class) {
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

    public /* synthetic */ void super$2$checkPermission(Permission permission) {
        super.checkPermission(permission);
    }

    public /* synthetic */ void super$2$checkExit(int n) {
        super.checkExit(n);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getSecurityManager";
        stringArray[1] = "checkPermission";
        stringArray[2] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        NoExitSecurityManager.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(NoExitSecurityManager.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = NoExitSecurityManager.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

