/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.CompilationUnitAware;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

public class ASTTransformationCustomizer
extends CompilationCustomizer
implements CompilationUnitAware,
GroovyObject {
    private final AnnotationNode annotationNode;
    private final ASTTransformation transformation;
    protected CompilationUnit compilationUnit;
    private boolean applied;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ASTTransformationCustomizer(Class<? extends Annotation> transformationAnnotation, String astTransformationClassName, ClassLoader transformationClassLoader) {
        MetaClass metaClass;
        boolean bl;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        super((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[0].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, astTransformationClassName, transformationClassLoader), CompilePhase.class));
        this.applied = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[1].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, astTransformationClassName, transformationClassLoader));
        Object object = callSiteArray[2].call(clazz);
        this.transformation = (ASTTransformation)ScriptBytecodeAdapter.castToType(object, ASTTransformation.class);
        Object object2 = callSiteArray[3].callConstructor(AnnotationNode.class, callSiteArray[4].call(ClassHelper.class, transformationAnnotation));
        this.annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(object2, AnnotationNode.class);
    }

    public ASTTransformationCustomizer(Class<? extends Annotation> transformationAnnotation, String astTransformationClassName) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        Object[] objectArray = new Object[]{transformationAnnotation, astTransformationClassName, callSiteArray[5].callGetProperty(transformationAnnotation)};
        ASTTransformationCustomizer aSTTransformationCustomizer = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, ASTTransformationCustomizer.class)) {
            case -1185825229: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[3], ClassLoader.class));
                break;
            }
            case -1120136712: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            case -455969688: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]));
                break;
            }
            case -232370331: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[0], ASTTransformation.class));
                break;
            }
            case 481615309: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]));
                break;
            }
            case 575475287: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]));
                break;
            }
            case 1145169098: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[1], ASTTransformation.class));
                break;
            }
            case 1278481666: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[1], ClassLoader.class));
                break;
            }
            case 1486509106: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]));
                break;
            }
            case 1716054525: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
    }

    public ASTTransformationCustomizer(Map annotationParams, Class<? extends Annotation> transformationAnnotation, String astTransformationClassName, ClassLoader transformationClassLoader) {
        MetaClass metaClass;
        boolean bl;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        super((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[6].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, astTransformationClassName, transformationClassLoader), CompilePhase.class));
        this.applied = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[7].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, astTransformationClassName, transformationClassLoader));
        Object object = callSiteArray[8].call(clazz);
        this.transformation = (ASTTransformation)ScriptBytecodeAdapter.castToType(object, ASTTransformation.class);
        Object object2 = callSiteArray[9].callConstructor(AnnotationNode.class, callSiteArray[10].call(ClassHelper.class, transformationAnnotation));
        this.annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(object2, AnnotationNode.class);
        callSiteArray[11].callCurrent((GroovyObject)this, annotationParams);
    }

    public ASTTransformationCustomizer(Map annotationParams, Class<? extends Annotation> transformationAnnotation, String astTransformationClassName) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        Object[] objectArray = new Object[]{annotationParams, transformationAnnotation, callSiteArray[12].callGetProperty(transformationAnnotation)};
        ASTTransformationCustomizer aSTTransformationCustomizer = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, ASTTransformationCustomizer.class)) {
            case -1185825229: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[3], ClassLoader.class));
                break;
            }
            case -1120136712: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            case -455969688: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]));
                break;
            }
            case -232370331: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[0], ASTTransformation.class));
                break;
            }
            case 481615309: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]));
                break;
            }
            case 575475287: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]));
                break;
            }
            case 1145169098: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[1], ASTTransformation.class));
                break;
            }
            case 1278481666: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[1], ClassLoader.class));
                break;
            }
            case 1486509106: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]));
                break;
            }
            case 1716054525: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
    }

    public ASTTransformationCustomizer(Class<? extends Annotation> transformationAnnotation, ClassLoader transformationClassLoader) {
        MetaClass metaClass;
        boolean bl;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        super((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[13].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, transformationClassLoader), CompilePhase.class));
        this.applied = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[14].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, transformationClassLoader));
        Object object = callSiteArray[15].call(clazz);
        this.transformation = (ASTTransformation)ScriptBytecodeAdapter.castToType(object, ASTTransformation.class);
        Object object2 = callSiteArray[16].callConstructor(AnnotationNode.class, callSiteArray[17].call(ClassHelper.class, transformationAnnotation));
        this.annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(object2, AnnotationNode.class);
    }

    public ASTTransformationCustomizer(Class<? extends Annotation> transformationAnnotation) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        Object[] objectArray = new Object[]{transformationAnnotation, callSiteArray[18].callGetProperty(transformationAnnotation)};
        ASTTransformationCustomizer aSTTransformationCustomizer = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, ASTTransformationCustomizer.class)) {
            case -1185825229: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[3], ClassLoader.class));
                break;
            }
            case -1120136712: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            case -455969688: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]));
                break;
            }
            case -232370331: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[0], ASTTransformation.class));
                break;
            }
            case 481615309: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]));
                break;
            }
            case 575475287: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]));
                break;
            }
            case 1145169098: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[1], ASTTransformation.class));
                break;
            }
            case 1278481666: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[1], ClassLoader.class));
                break;
            }
            case 1486509106: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]));
                break;
            }
            case 1716054525: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
    }

    public ASTTransformationCustomizer(ASTTransformation transformation) {
        MetaClass metaClass;
        boolean bl;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        super((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[19].callStatic(ASTTransformationCustomizer.class, transformation), CompilePhase.class));
        this.applied = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ASTTransformation aSTTransformation = transformation;
        this.transformation = (ASTTransformation)ScriptBytecodeAdapter.castToType(aSTTransformation, ASTTransformation.class);
        Object var6_6 = null;
        this.annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(var6_6, AnnotationNode.class);
    }

    public ASTTransformationCustomizer(Map annotationParams, Class<? extends Annotation> transformationAnnotation, ClassLoader transformationClassLoader) {
        MetaClass metaClass;
        boolean bl;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        super((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[20].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, transformationClassLoader), CompilePhase.class));
        this.applied = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[21].callStatic(ASTTransformationCustomizer.class, transformationAnnotation, transformationClassLoader));
        Object object = callSiteArray[22].call(clazz);
        this.transformation = (ASTTransformation)ScriptBytecodeAdapter.castToType(object, ASTTransformation.class);
        Object object2 = callSiteArray[23].callConstructor(AnnotationNode.class, callSiteArray[24].call(ClassHelper.class, transformationAnnotation));
        this.annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(object2, AnnotationNode.class);
        callSiteArray[25].callCurrent((GroovyObject)this, annotationParams);
    }

    public ASTTransformationCustomizer(Map annotationParams, Class<? extends Annotation> transformationAnnotation) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        Object[] objectArray = new Object[]{annotationParams, transformationAnnotation, callSiteArray[26].callGetProperty(transformationAnnotation)};
        ASTTransformationCustomizer aSTTransformationCustomizer = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, ASTTransformationCustomizer.class)) {
            case -1185825229: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[3], ClassLoader.class));
                break;
            }
            case -1120136712: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            case -455969688: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]));
                break;
            }
            case -232370331: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[0], ASTTransformation.class));
                break;
            }
            case 481615309: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]));
                break;
            }
            case 575475287: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]));
                break;
            }
            case 1145169098: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[1], ASTTransformation.class));
                break;
            }
            case 1278481666: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[1], ClassLoader.class));
                break;
            }
            case 1486509106: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]));
                break;
            }
            case 1716054525: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
    }

    public ASTTransformationCustomizer(Map annotationParams, ASTTransformation transformation) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        Object[] objectArray = new Object[]{transformation};
        ASTTransformationCustomizer aSTTransformationCustomizer = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, ASTTransformationCustomizer.class)) {
            case -1185825229: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[3], ClassLoader.class));
                break;
            }
            case -1120136712: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            case -455969688: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer(ShortTypeHandling.castToClass(objectArray[0]));
                break;
            }
            case -232370331: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[0], ASTTransformation.class));
                break;
            }
            case 481615309: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), ShortTypeHandling.castToClass(objectArray[1]));
                break;
            }
            case 575475287: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), ShortTypeHandling.castToString(objectArray[2]));
                break;
            }
            case 1145169098: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (ASTTransformation)ScriptBytecodeAdapter.castToType(objectArray[1], ASTTransformation.class));
                break;
            }
            case 1278481666: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[1], ClassLoader.class));
                break;
            }
            case 1486509106: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]));
                break;
            }
            case 1716054525: {
                Object[] objectArray2 = objectArray;
                aSTTransformationCustomizer((Map)ScriptBytecodeAdapter.castToType(objectArray[0], Map.class), (Class<? extends Annotation>)ShortTypeHandling.castToClass(objectArray[1]), (ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], ClassLoader.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
        callSiteArray[27].callCurrent((GroovyObject)this, annotationParams);
    }

    @Override
    public void setCompilationUnit(CompilationUnit unit) {
        CompilationUnit compilationUnit;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        this.compilationUnit = compilationUnit = unit;
    }

    private static Class<ASTTransformation> findASTTranformationClass(Class<? extends Annotation> anAnnotationClass, ClassLoader transformationClassLoader) {
        ClassLoader classLoader;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        GroovyASTTransformationClass annotation = (GroovyASTTransformationClass)ScriptBytecodeAdapter.castToType(callSiteArray[28].call(anAnnotationClass, GroovyASTTransformationClass.class), GroovyASTTransformationClass.class);
        if (ScriptBytecodeAdapter.compareEqual(annotation, null)) {
            throw (Throwable)callSiteArray[29].callConstructor(IllegalArgumentException.class, "Provided class doesn't look like an AST @interface");
        }
        Object[] classes = (Class[])ScriptBytecodeAdapter.castToType(callSiteArray[30].call(annotation), Class[].class);
        Object[] classesAsStrings = (String[])ScriptBytecodeAdapter.castToType(callSiteArray[31].call(annotation), String[].class);
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[32].call(callSiteArray[33].callGetProperty(classes), callSiteArray[34].callGetProperty(classesAsStrings)), 1)) {
            throw (Throwable)callSiteArray[35].callConstructor(IllegalArgumentException.class, "AST transformation customizer doesn't support AST transforms with multiple classes");
        }
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            ClassLoader classLoader2;
            return ShortTypeHandling.castToClass(DefaultTypeTransformation.booleanUnbox(classes) ? callSiteArray[36].call((Object)classes, 0) : callSiteArray[37].call(Class.class, callSiteArray[38].call((Object)classesAsStrings, 0), true, DefaultTypeTransformation.booleanUnbox(classLoader2 = transformationClassLoader) ? classLoader2 : callSiteArray[39].callGetProperty(anAnnotationClass)));
        }
        return ShortTypeHandling.castToClass(DefaultTypeTransformation.booleanUnbox(classes) ? BytecodeInterface8.objectArrayGet(classes, 0) : callSiteArray[40].call(Class.class, BytecodeInterface8.objectArrayGet(classesAsStrings, 0), true, DefaultTypeTransformation.booleanUnbox(classLoader = transformationClassLoader) ? classLoader : callSiteArray[41].callGetProperty(anAnnotationClass)));
    }

    private static Class<ASTTransformation> findASTTranformationClass(Class<? extends Annotation> anAnnotationClass, String astTransformationClassName, ClassLoader transformationClassLoader) {
        ClassLoader classLoader;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        return (Class)ScriptBytecodeAdapter.asType(callSiteArray[42].call(Class.class, astTransformationClassName, true, DefaultTypeTransformation.booleanUnbox(classLoader = transformationClassLoader) ? classLoader : callSiteArray[43].callGetProperty(anAnnotationClass)), Class.class);
    }

    private static CompilePhase findPhase(ASTTransformation transformation) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(transformation, null)) {
            throw (Throwable)callSiteArray[44].callConstructor(IllegalArgumentException.class, "Provided transformation must not be null");
        }
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[45].callGetProperty(transformation));
        GroovyASTTransformation annotation = (GroovyASTTransformation)ScriptBytecodeAdapter.castToType(callSiteArray[46].call((Object)clazz, GroovyASTTransformation.class), GroovyASTTransformation.class);
        if (ScriptBytecodeAdapter.compareEqual(annotation, null)) {
            throw (Throwable)callSiteArray[47].callConstructor(IllegalArgumentException.class, callSiteArray[48].call((Object)"Provided ast transformation is not annotated with ", callSiteArray[49].callGetProperty(GroovyASTTransformation.class)));
        }
        return (CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[50].call(annotation), CompilePhase.class);
    }

    private static CompilePhase findPhase(Class<? extends Annotation> annotationClass, ClassLoader transformationClassLoader) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[51].callStatic(ASTTransformationCustomizer.class, annotationClass, transformationClassLoader));
        return (CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[52].callStatic(ASTTransformationCustomizer.class, callSiteArray[53].call(clazz)), CompilePhase.class);
    }

    private static CompilePhase findPhase(Class<? extends Annotation> annotationClass, String astTransformationClassName, ClassLoader transformationClassLoader) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        Class clazz = ShortTypeHandling.castToClass(callSiteArray[54].callStatic(ASTTransformationCustomizer.class, annotationClass, astTransformationClassName, transformationClassLoader));
        return (CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[55].callStatic(ASTTransformationCustomizer.class, callSiteArray[56].call(clazz)), CompilePhase.class);
    }

    public void setAnnotationParameters(Map<String, Object> params) {
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? ScriptBytecodeAdapter.compareEqual(params, null) || ScriptBytecodeAdapter.compareEqual(this.annotationNode, null) : ScriptBytecodeAdapter.compareEqual(params, null) || ScriptBytecodeAdapter.compareEqual(this.annotationNode, null)) {
            return;
        }
        public class _setAnnotationParameters_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _setAnnotationParameters_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _setAnnotationParameters_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object key, Object value) {
                CallSite[] callSiteArray = _setAnnotationParameters_closure1.$getCallSiteArray();
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), key))) {
                    throw (Throwable)callSiteArray[3].callConstructor(IllegalArgumentException.class, new GStringImpl(new Object[]{callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this))), key}, new String[]{"", " does not accept any [", "] parameter"}));
                }
                if (value instanceof Closure) {
                    throw (Throwable)callSiteArray[7].callConstructor(IllegalArgumentException.class, callSiteArray[8].call((Object)"Direct usage of closure is not supported by the AST ", "compilation customizer. Please use ClosureExpression instead."));
                }
                if (value instanceof Expression) {
                    callSiteArray[9].call(value, 0);
                    callSiteArray[10].call(value, 0);
                    return callSiteArray[11].call(callSiteArray[12].callGroovyObjectGetProperty(this), key, value);
                }
                if (value instanceof Class) {
                    return callSiteArray[13].call(callSiteArray[14].callGroovyObjectGetProperty(this), key, callSiteArray[15].callConstructor(ClassExpression.class, callSiteArray[16].call(ClassHelper.class, value)));
                }
                if (value instanceof List) {
                    public class _closure2
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure2(Object _outerInstance, Object _thisObject) {
                            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                            return it instanceof Class ? (Expression)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(ClassExpression.class, callSiteArray[1].call(ClassHelper.class, it)), Expression.class) : (Expression)ScriptBytecodeAdapter.castToType(callSiteArray[2].callConstructor(ConstantExpression.class, it), Expression.class);
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure2.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "<$constructor$>";
                            stringArray[1] = "make";
                            stringArray[2] = "<$constructor$>";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[3];
                            _closure2.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure2.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure2.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[17].call(callSiteArray[18].callGroovyObjectGetProperty(this), key, callSiteArray[19].callConstructor(ListExpression.class, callSiteArray[20].call(value, new _closure2(this, this.getThisObject()))));
                }
                return callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(this), key, callSiteArray[23].callConstructor(ConstantExpression.class, value));
            }

            public Object call(Object key, Object value) {
                CallSite[] callSiteArray = _setAnnotationParameters_closure1.$getCallSiteArray();
                return callSiteArray[24].callCurrent(this, key, value);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _setAnnotationParameters_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getMethod";
                stringArray[1] = "classNode";
                stringArray[2] = "annotationNode";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "name";
                stringArray[5] = "classNode";
                stringArray[6] = "annotationNode";
                stringArray[7] = "<$constructor$>";
                stringArray[8] = "plus";
                stringArray[9] = "setLineNumber";
                stringArray[10] = "setLastLineNumber";
                stringArray[11] = "addMember";
                stringArray[12] = "annotationNode";
                stringArray[13] = "addMember";
                stringArray[14] = "annotationNode";
                stringArray[15] = "<$constructor$>";
                stringArray[16] = "make";
                stringArray[17] = "addMember";
                stringArray[18] = "annotationNode";
                stringArray[19] = "<$constructor$>";
                stringArray[20] = "collect";
                stringArray[21] = "addMember";
                stringArray[22] = "annotationNode";
                stringArray[23] = "<$constructor$>";
                stringArray[24] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[25];
                _setAnnotationParameters_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_setAnnotationParameters_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _setAnnotationParameters_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[57].call(params, new _setAnnotationParameters_closure1(this, this));
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        boolean bl;
        CallSite[] callSiteArray = ASTTransformationCustomizer.$getCallSiteArray();
        if (this.transformation instanceof CompilationUnitAware) {
            CompilationUnit compilationUnit = this.compilationUnit;
            ScriptBytecodeAdapter.setProperty(compilationUnit, null, this.transformation, "compilationUnit");
        }
        if (ScriptBytecodeAdapter.compareNotEqual(this.annotationNode, null)) {
            ClassNode classNode2 = classNode;
            ScriptBytecodeAdapter.setProperty(classNode2, null, this.annotationNode, "sourcePosition");
            callSiteArray[58].call(this.transformation, ScriptBytecodeAdapter.createPojoWrapper((ASTNode[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{this.annotationNode, classNode}), ASTNode[].class), ASTNode[].class), source);
        } else if (!this.applied) {
            callSiteArray[59].call(this.transformation, null, source);
        }
        this.applied = bl = true;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ASTTransformationCustomizer.class) {
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
        stringArray[0] = "findPhase";
        stringArray[1] = "findASTTranformationClass";
        stringArray[2] = "newInstance";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "make";
        stringArray[5] = "classLoader";
        stringArray[6] = "findPhase";
        stringArray[7] = "findASTTranformationClass";
        stringArray[8] = "newInstance";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "make";
        stringArray[11] = "setAnnotationParameters";
        stringArray[12] = "classLoader";
        stringArray[13] = "findPhase";
        stringArray[14] = "findASTTranformationClass";
        stringArray[15] = "newInstance";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "make";
        stringArray[18] = "classLoader";
        stringArray[19] = "findPhase";
        stringArray[20] = "findPhase";
        stringArray[21] = "findASTTranformationClass";
        stringArray[22] = "newInstance";
        stringArray[23] = "<$constructor$>";
        stringArray[24] = "make";
        stringArray[25] = "setAnnotationParameters";
        stringArray[26] = "classLoader";
        stringArray[27] = "setAnnotationParameters";
        stringArray[28] = "getAnnotation";
        stringArray[29] = "<$constructor$>";
        stringArray[30] = "classes";
        stringArray[31] = "value";
        stringArray[32] = "plus";
        stringArray[33] = "length";
        stringArray[34] = "length";
        stringArray[35] = "<$constructor$>";
        stringArray[36] = "getAt";
        stringArray[37] = "forName";
        stringArray[38] = "getAt";
        stringArray[39] = "classLoader";
        stringArray[40] = "forName";
        stringArray[41] = "classLoader";
        stringArray[42] = "forName";
        stringArray[43] = "classLoader";
        stringArray[44] = "<$constructor$>";
        stringArray[45] = "class";
        stringArray[46] = "getAnnotation";
        stringArray[47] = "<$constructor$>";
        stringArray[48] = "plus";
        stringArray[49] = "name";
        stringArray[50] = "phase";
        stringArray[51] = "findASTTranformationClass";
        stringArray[52] = "findPhase";
        stringArray[53] = "newInstance";
        stringArray[54] = "findASTTranformationClass";
        stringArray[55] = "findPhase";
        stringArray[56] = "newInstance";
        stringArray[57] = "each";
        stringArray[58] = "visit";
        stringArray[59] = "visit";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[60];
        ASTTransformationCustomizer.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ASTTransformationCustomizer.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ASTTransformationCustomizer.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

