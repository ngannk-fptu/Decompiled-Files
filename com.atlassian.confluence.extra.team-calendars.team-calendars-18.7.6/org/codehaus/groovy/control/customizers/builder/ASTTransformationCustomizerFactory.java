/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers.builder;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ASTTransformationCustomizerFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ASTTransformationCustomizerFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ASTTransformationCustomizerFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return false;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ASTTransformationCustomizerFactory.$getCallSiteArray();
        ASTTransformationCustomizer customizer = null;
        if (DefaultTypeTransformation.booleanUnbox(attributes)) {
            Object object = callSiteArray[0].callConstructor(ASTTransformationCustomizer.class, attributes, value);
            customizer = (ASTTransformationCustomizer)ScriptBytecodeAdapter.castToType(object, ASTTransformationCustomizer.class);
        } else {
            Object object = callSiteArray[1].callConstructor(ASTTransformationCustomizer.class, value);
            customizer = (ASTTransformationCustomizer)ScriptBytecodeAdapter.castToType(object, ASTTransformationCustomizer.class);
        }
        return customizer;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ASTTransformationCustomizerFactory.class) {
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

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    public /* synthetic */ boolean super$2$onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object object, Map map) {
        return super.onHandleNodeAttributes(factoryBuilderSupport, object, map);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[2];
        ASTTransformationCustomizerFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ASTTransformationCustomizerFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ASTTransformationCustomizerFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

