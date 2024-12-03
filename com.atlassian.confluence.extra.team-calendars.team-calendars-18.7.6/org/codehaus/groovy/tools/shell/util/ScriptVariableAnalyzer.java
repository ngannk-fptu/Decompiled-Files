/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ScriptVariableAnalyzer
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ScriptVariableAnalyzer() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ScriptVariableAnalyzer.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Deprecated
    public static Set<String> getBoundVars(String scriptText) {
        CallSite[] callSiteArray = ScriptVariableAnalyzer.$getCallSiteArray();
        return (Set)ScriptBytecodeAdapter.castToType(callSiteArray[0].callStatic(ScriptVariableAnalyzer.class, scriptText, null), Set.class);
    }

    public static Set<String> getBoundVars(String scriptText, ClassLoader parent) {
        CallSite[] callSiteArray = ScriptVariableAnalyzer.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = scriptText;
            valueRecorder.record(string, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(string, null);
            valueRecorder.record(bl, 19);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert scriptText != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        GroovyClassVisitor visitor = (GroovyClassVisitor)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(VariableVisitor.class), GroovyClassVisitor.class);
        VisitorClassLoader myCL = (VisitorClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[2].callConstructor(VisitorClassLoader.class, visitor, parent), VisitorClassLoader.class);
        callSiteArray[3].call((Object)myCL, scriptText);
        return (Set)ScriptBytecodeAdapter.castToType(callSiteArray[4].callGetProperty(visitor), Set.class);
    }

    public /* synthetic */ Object this$dist$invoke$1(String name, Object args) {
        CallSite[] callSiteArray = ScriptVariableAnalyzer.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(ScriptVariableAnalyzer.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$1(String name, Object value) {
        CallSite[] callSiteArray = ScriptVariableAnalyzer.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, ScriptVariableAnalyzer.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$1(String name) {
        CallSite[] callSiteArray = ScriptVariableAnalyzer.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(ScriptVariableAnalyzer.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ScriptVariableAnalyzer.class) {
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
        stringArray[0] = "getBoundVars";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "parseClass";
        stringArray[4] = "bound";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        ScriptVariableAnalyzer.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ScriptVariableAnalyzer.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ScriptVariableAnalyzer.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    public static class VariableVisitor
    extends ClassCodeVisitorSupport
    implements GroovyClassVisitor,
    GroovyObject {
        private Set<String> bound;
        private Set<String> unbound;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public VariableVisitor() {
            MetaClass metaClass;
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            Object object = callSiteArray[0].callConstructor(HashSet.class);
            this.bound = (Set)ScriptBytecodeAdapter.castToType(object, Set.class);
            Object object2 = callSiteArray[1].callConstructor(HashSet.class);
            this.unbound = (Set)ScriptBytecodeAdapter.castToType(object2, Set.class);
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        @Override
        public void visitVariableExpression(VariableExpression expression) {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            if (!ScriptBytecodeAdapter.isCase(callSiteArray[2].callGetProperty(expression), ScriptBytecodeAdapter.createList(new Object[]{"args", "context", "this", "super"}))) {
                if (callSiteArray[3].callGetProperty(expression) instanceof DynamicVariable) {
                    callSiteArray[4].call(this.unbound, callSiteArray[5].callGetProperty(expression));
                } else {
                    callSiteArray[6].call(this.bound, callSiteArray[7].callGetProperty(expression));
                }
            }
            ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitVariableExpression", new Object[]{expression});
        }

        @Override
        protected SourceUnit getSourceUnit() {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            return (SourceUnit)ScriptBytecodeAdapter.castToType(null, SourceUnit.class);
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(VariableVisitor.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(VariableVisitor.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(VariableVisitor.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = VariableVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(VariableVisitor.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != VariableVisitor.class) {
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

        public Set<String> getBound() {
            return this.bound;
        }

        public void setBound(Set<String> set) {
            this.bound = set;
        }

        public Set<String> getUnbound() {
            return this.unbound;
        }

        public void setUnbound(Set<String> set) {
            this.unbound = set;
        }

        public /* synthetic */ void super$2$visitVariableExpression(VariableExpression variableExpression) {
            super.visitVariableExpression(variableExpression);
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "<$constructor$>";
            stringArray[2] = "variable";
            stringArray[3] = "accessedVariable";
            stringArray[4] = "leftShift";
            stringArray[5] = "variable";
            stringArray[6] = "leftShift";
            stringArray[7] = "variable";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[8];
            VariableVisitor.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(VariableVisitor.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = VariableVisitor.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public static class VisitorSourceOperation
    extends CompilationUnit.PrimaryClassNodeOperation
    implements GroovyObject {
        private final GroovyClassVisitor visitor;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public VisitorSourceOperation(GroovyClassVisitor visitor) {
            MetaClass metaClass;
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
            GroovyClassVisitor groovyClassVisitor = visitor;
            this.visitor = (GroovyClassVisitor)ScriptBytecodeAdapter.castToType(groovyClassVisitor, GroovyClassVisitor.class);
        }

        @Override
        public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            callSiteArray[0].call((Object)classNode, this.visitor);
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(VisitorSourceOperation.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(VisitorSourceOperation.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(VisitorSourceOperation.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = VisitorSourceOperation.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(VisitorSourceOperation.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != VisitorSourceOperation.class) {
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

        public final GroovyClassVisitor getVisitor() {
            return this.visitor;
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[1];
            stringArray[0] = "visitContents";
            return new CallSiteArray(VisitorSourceOperation.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = VisitorSourceOperation.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public static class VisitorClassLoader
    extends GroovyClassLoader
    implements GroovyObject {
        private final GroovyClassVisitor visitor;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        @Deprecated
        public VisitorClassLoader(GroovyClassVisitor visitor) {
            MetaClass metaClass;
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
            GroovyClassVisitor groovyClassVisitor = visitor;
            this.visitor = (GroovyClassVisitor)ScriptBytecodeAdapter.castToType(groovyClassVisitor, GroovyClassVisitor.class);
        }

        public VisitorClassLoader(GroovyClassVisitor visitor, ClassLoader parent) {
            MetaClass metaClass;
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            Object[] objectArray = new Object[]{ScriptBytecodeAdapter.compareEqual(parent, null) ? callSiteArray[0].call(callSiteArray[1].call(Thread.class)) : parent};
            VisitorClassLoader visitorClassLoader = this;
            switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, GroovyClassLoader.class)) {
                case -2044833963: {
                    Object[] objectArray2 = objectArray;
                    super((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class));
                    break;
                }
                case -1374445560: {
                    Object[] objectArray2 = objectArray;
                    super((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[1], CompilerConfiguration.class));
                    break;
                }
                case -991719697: {
                    Object[] objectArray2 = objectArray;
                    super((GroovyClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], GroovyClassLoader.class));
                    break;
                }
                case 39797: {
                    Object[] objectArray2 = objectArray;
                    super();
                    break;
                }
                case 341906380: {
                    Object[] objectArray2 = objectArray;
                    super((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[1], CompilerConfiguration.class), DefaultTypeTransformation.booleanUnbox(objectArray[2]));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
                }
            }
            this.metaClass = metaClass = this.$getStaticMetaClass();
            GroovyClassVisitor groovyClassVisitor = visitor;
            this.visitor = (GroovyClassVisitor)ScriptBytecodeAdapter.castToType(groovyClassVisitor, GroovyClassVisitor.class);
        }

        @Override
        protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            CompilationUnit cu = (CompilationUnit)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.invokeMethodOnSuperN(GroovyClassLoader.class, this, "createCompilationUnit", new Object[]{config, source}), CompilationUnit.class);
            callSiteArray[2].call(cu, callSiteArray[3].callConstructor(VisitorSourceOperation.class, this.visitor), callSiteArray[4].callGetProperty(Phases.class));
            return cu;
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(VisitorClassLoader.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(VisitorClassLoader.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(VisitorClassLoader.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = VisitorClassLoader.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(VisitorClassLoader.class, ScriptVariableAnalyzer.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != VisitorClassLoader.class) {
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

        public final GroovyClassVisitor getVisitor() {
            return this.visitor;
        }

        public /* synthetic */ CompilationUnit super$5$createCompilationUnit(CompilerConfiguration compilerConfiguration, CodeSource codeSource) {
            return super.createCompilationUnit(compilerConfiguration, codeSource);
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "getContextClassLoader";
            stringArray[1] = "currentThread";
            stringArray[2] = "addPhaseOperation";
            stringArray[3] = "<$constructor$>";
            stringArray[4] = "CLASS_GENERATION";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[5];
            VisitorClassLoader.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(VisitorClassLoader.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = VisitorClassLoader.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

