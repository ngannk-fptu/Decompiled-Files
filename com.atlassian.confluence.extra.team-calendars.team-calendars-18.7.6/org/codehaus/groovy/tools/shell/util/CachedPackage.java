/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class CachedPackage
implements GroovyObject {
    private String name;
    private boolean containsClasses;
    private boolean checked;
    private Map<String, CachedPackage> childPackages;
    private Set<URL> sources;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CachedPackage(String name, Set<URL> sources) {
        MetaClass metaClass;
        CallSite[] callSiteArray = CachedPackage.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Set<URL> set = sources;
        this.sources = (Set)ScriptBytecodeAdapter.castToType(set, Set.class);
        String string = name;
        this.name = ShortTypeHandling.castToString(string);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CachedPackage.class) {
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

    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public boolean getContainsClasses() {
        return this.containsClasses;
    }

    public boolean isContainsClasses() {
        return this.containsClasses;
    }

    public void setContainsClasses(boolean bl) {
        this.containsClasses = bl;
    }

    public boolean getChecked() {
        return this.checked;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean bl) {
        this.checked = bl;
    }

    public Map<String, CachedPackage> getChildPackages() {
        return this.childPackages;
    }

    public void setChildPackages(Map<String, CachedPackage> map) {
        this.childPackages = map;
    }

    public Set<URL> getSources() {
        return this.sources;
    }

    public void setSources(Set<URL> set) {
        this.sources = set;
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[]{};
        return new CallSiteArray(CachedPackage.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CachedPackage.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

