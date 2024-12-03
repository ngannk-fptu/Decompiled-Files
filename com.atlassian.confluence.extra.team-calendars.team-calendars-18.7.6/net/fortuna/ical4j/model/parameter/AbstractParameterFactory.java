/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.transform.Generated
 *  groovy.transform.Internal
 */
package net.fortuna.ical4j.model.parameter;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.Generated;
import groovy.transform.Internal;
import groovy.util.AbstractFactory;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public abstract class AbstractParameterFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AbstractParameterFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AbstractParameterFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = AbstractParameterFactory.$getCallSiteArray();
        return true;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AbstractParameterFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    @Generated
    @Internal
    public MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    @Generated
    @Internal
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    @Generated
    @Internal
    public Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    @Generated
    @Internal
    public Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    @Generated
    @Internal
    public void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[]{};
        return new CallSiteArray(AbstractParameterFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AbstractParameterFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

