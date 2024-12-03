/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public interface VariableReplacedListener {
    public static final VariableReplacedListener NULL;

    public void variableReplaced(VariableExpression var1, VariableExpression var2);

    static {
        1 var0 = new 1(2.$class$org$codehaus$groovy$transform$tailrec$VariableReplacedListener);
        NULL = var0;
    }

    public class 1
    implements VariableReplacedListener,
    GroovyObject {
        public /* synthetic */ Class this$0;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;

        /* synthetic */ 1(Class p0) {
            MetaClass metaClass;
            Class clazz;
            this.this$0 = clazz = p0;
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        @Override
        public void variableReplaced(VariableExpression oldVar, VariableExpression newVar) {
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            return ScriptBytecodeAdapter.invokeMethodN(1.class, VariableReplacedListener.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            return ScriptBytecodeAdapter.invokeMethodN(1.class, VariableReplacedListener.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, VariableReplacedListener.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, VariableReplacedListener.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            return ScriptBytecodeAdapter.getProperty(1.class, VariableReplacedListener.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            return ScriptBytecodeAdapter.getProperty(1.class, VariableReplacedListener.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != 1.class) {
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
    }

    static class 2
    implements GroovyObject {
        static /* synthetic */ Class $class$org$codehaus$groovy$transform$tailrec$VariableReplacedListener;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public 2() {
            MetaClass metaClass;
            CallSite[] callSiteArray = 2.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != 2.class) {
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
            Class<VariableReplacedListener> clazz;
            $class$org$codehaus$groovy$transform$tailrec$VariableReplacedListener = clazz = VariableReplacedListener.class;
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[]{};
            return new CallSiteArray(2.class, stringArray);
        }

        public static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = 2.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

