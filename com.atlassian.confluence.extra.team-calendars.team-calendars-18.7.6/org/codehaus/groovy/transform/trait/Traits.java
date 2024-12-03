/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.trait;

import groovy.lang.GeneratedGroovyProxy;
import groovy.transform.SelfType;
import groovy.transform.Trait;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.transform.trait.TraitHelpersTuple;

public abstract class Traits {
    public static final ClassNode IMPLEMENTED_CLASSNODE = ClassHelper.make(Implemented.class);
    public static final ClassNode TRAITBRIDGE_CLASSNODE = ClassHelper.make(TraitBridge.class);
    public static final Class TRAIT_CLASS = Trait.class;
    public static final ClassNode TRAIT_CLASSNODE = ClassHelper.make(TRAIT_CLASS);
    public static final ClassNode GENERATED_PROXY_CLASSNODE = ClassHelper.make(GeneratedGroovyProxy.class);
    public static final ClassNode SELFTYPE_CLASSNODE = ClassHelper.make(SelfType.class);
    static final String TRAIT_TYPE_NAME = "@" + TRAIT_CLASSNODE.getNameWithoutPackage();
    static final String TRAIT_HELPER = "$Trait$Helper";
    static final String FIELD_HELPER = "$Trait$FieldHelper";
    static final String DIRECT_SETTER_SUFFIX = "$set";
    static final String DIRECT_GETTER_SUFFIX = "$get";
    static final String INIT_METHOD = "$init$";
    static final String STATIC_INIT_METHOD = "$static$init$";
    public static final String THIS_OBJECT = "$self";
    public static final String STATIC_THIS_OBJECT = "$static$self";
    static final String STATIC_FIELD_PREFIX = "$static";
    static final String FIELD_PREFIX = "$ins";
    static final String PUBLIC_FIELD_PREFIX = "$0";
    static final String PRIVATE_FIELD_PREFIX = "$1";
    static final List<Integer> FIELD_PREFIXES = Arrays.asList(1, 2, 9, 10, 17, 18, 25, 26, 129, 130, 137, 138, 145, 146, 153, 154);
    static final int FIELD_PREFIX_MASK = 155;
    static final String SUPER_TRAIT_METHOD_PREFIX = "trait$super$";

    static String fieldHelperClassName(ClassNode traitNode) {
        return traitNode.getName() + FIELD_HELPER;
    }

    static String helperGetterName(FieldNode field) {
        return Traits.remappedFieldName(Traits.unwrapOwner(field.getOwner()), field.getName()) + DIRECT_GETTER_SUFFIX;
    }

    static String helperSetterName(FieldNode field) {
        return Traits.remappedFieldName(Traits.unwrapOwner(field.getOwner()), field.getName()) + DIRECT_SETTER_SUFFIX;
    }

    static String helperClassName(ClassNode traitNode) {
        return traitNode.getName() + TRAIT_HELPER;
    }

    static String remappedFieldName(ClassNode traitNode, String name) {
        return traitNode.getName().replace('.', '_') + "__" + name;
    }

    private static ClassNode unwrapOwner(ClassNode owner) {
        if (ClassHelper.CLASS_Type.equals(owner) && owner.getGenericsTypes() != null && owner.getGenericsTypes().length == 1) {
            return owner.getGenericsTypes()[0].getType();
        }
        return owner;
    }

    public static ClassNode findHelper(ClassNode trait) {
        return Traits.findHelpers(trait).getHelper();
    }

    public static ClassNode findFieldHelper(ClassNode trait) {
        return Traits.findHelpers(trait).getFieldHelper();
    }

    static TraitHelpersTuple findHelpers(ClassNode trait) {
        ClassNode helperClassNode = null;
        ClassNode fieldHelperClassNode = null;
        Iterator<InnerClassNode> innerClasses = trait.redirect().getInnerClasses();
        if (innerClasses != null && innerClasses.hasNext()) {
            while (innerClasses.hasNext()) {
                ClassNode icn = innerClasses.next();
                if (icn.getName().endsWith(FIELD_HELPER)) {
                    fieldHelperClassNode = icn;
                    continue;
                }
                if (!icn.getName().endsWith(TRAIT_HELPER)) continue;
                helperClassNode = icn;
            }
        } else {
            try {
                ClassLoader classLoader = trait.getTypeClass().getClassLoader();
                String helperClassName = Traits.helperClassName(trait);
                helperClassNode = ClassHelper.make(Class.forName(helperClassName, false, classLoader));
                try {
                    fieldHelperClassNode = ClassHelper.make(classLoader.loadClass(Traits.fieldHelperClassName(trait)));
                }
                catch (ClassNotFoundException classNotFoundException) {}
            }
            catch (ClassNotFoundException e) {
                throw new GroovyBugError("Couldn't find trait helper classes on compile classpath!", e);
            }
        }
        return new TraitHelpersTuple(helperClassNode, fieldHelperClassNode);
    }

    public static boolean isTrait(ClassNode cNode) {
        return cNode != null && Traits.isAnnotatedWithTrait(cNode);
    }

    public static boolean isTrait(Class clazz) {
        return clazz != null && clazz.getAnnotation(Trait.class) != null;
    }

    public static boolean isAnnotatedWithTrait(ClassNode cNode) {
        List<AnnotationNode> traitAnn = cNode.getAnnotations(TRAIT_CLASSNODE);
        return traitAnn != null && !traitAnn.isEmpty();
    }

    public static boolean hasDefaultImplementation(MethodNode method) {
        return !method.getAnnotations(IMPLEMENTED_CLASSNODE).isEmpty();
    }

    public static boolean hasDefaultImplementation(Method method) {
        return method.getAnnotation(Implemented.class) != null;
    }

    public static boolean isBridgeMethod(Method someMethod) {
        TraitBridge annotation = someMethod.getAnnotation(TraitBridge.class);
        return annotation != null;
    }

    public static Method getBridgeMethodTarget(Method someMethod) {
        TraitBridge annotation = someMethod.getAnnotation(TraitBridge.class);
        if (annotation == null) {
            return null;
        }
        Class aClass = annotation.traitClass();
        String desc = annotation.desc();
        for (Method method : aClass.getDeclaredMethods()) {
            String methodDescriptor = BytecodeHelper.getMethodDescriptor(method.getReturnType(), method.getParameterTypes());
            if (!desc.equals(methodDescriptor)) continue;
            return method;
        }
        return null;
    }

    public static <T> T getAsType(Object self, Class<T> clazz) {
        Object proxyTarget;
        if (self instanceof GeneratedGroovyProxy && clazz.isAssignableFrom((proxyTarget = ((GeneratedGroovyProxy)self).getProxyTarget()).getClass())) {
            return (T)proxyTarget;
        }
        return DefaultGroovyMethods.asType(self, clazz);
    }

    public static String[] decomposeSuperCallName(String origName) {
        if (origName.contains(SUPER_TRAIT_METHOD_PREFIX)) {
            int endIndex = origName.indexOf(SUPER_TRAIT_METHOD_PREFIX);
            String tName = origName.substring(0, endIndex).replace('_', '.').replace("..", "_");
            String fName = origName.substring(endIndex + SUPER_TRAIT_METHOD_PREFIX.length());
            return new String[]{tName, fName};
        }
        return null;
    }

    public static LinkedHashSet<ClassNode> collectAllInterfacesReverseOrder(ClassNode cNode, LinkedHashSet<ClassNode> interfaces) {
        if (cNode.isInterface()) {
            interfaces.add(cNode);
        }
        ClassNode[] directInterfaces = cNode.getInterfaces();
        for (int i = directInterfaces.length - 1; i >= 0; --i) {
            ClassNode anInterface = directInterfaces[i];
            interfaces.add(GenericsUtils.parameterizeType(cNode, anInterface));
            Traits.collectAllInterfacesReverseOrder(anInterface, interfaces);
        }
        return interfaces;
    }

    public static LinkedHashSet<ClassNode> collectSelfTypes(ClassNode receiver, LinkedHashSet<ClassNode> selfTypes) {
        return Traits.collectSelfTypes(receiver, selfTypes, true, true);
    }

    public static LinkedHashSet<ClassNode> collectSelfTypes(ClassNode receiver, LinkedHashSet<ClassNode> selfTypes, boolean checkInterfaces, boolean checkSuper) {
        ClassNode superClass;
        if (Traits.isTrait(receiver)) {
            List<AnnotationNode> annotations = receiver.getAnnotations(SELFTYPE_CLASSNODE);
            for (AnnotationNode annotation : annotations) {
                Expression value = annotation.getMember("value");
                if (value instanceof ClassExpression) {
                    selfTypes.add(value.getType());
                    continue;
                }
                if (!(value instanceof ListExpression)) continue;
                List<Expression> expressions = ((ListExpression)value).getExpressions();
                for (Expression expression : expressions) {
                    if (!(expression instanceof ClassExpression)) continue;
                    selfTypes.add(expression.getType());
                }
            }
        }
        if (checkInterfaces) {
            ClassNode[] interfaces;
            for (ClassNode anInterface : interfaces = receiver.getInterfaces()) {
                Traits.collectSelfTypes(anInterface, selfTypes, true, checkSuper);
            }
        }
        if (checkSuper && (superClass = receiver.getSuperClass()) != null) {
            Traits.collectSelfTypes(superClass, selfTypes, checkInterfaces, true);
        }
        return selfTypes;
    }

    static String getSuperTraitMethodName(ClassNode trait, String method) {
        return trait.getName().replace("_", "__").replace('.', '_') + SUPER_TRAIT_METHOD_PREFIX + method;
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    public static @interface TraitBridge {
        public Class traitClass();

        public String desc();
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.METHOD})
    public static @interface Implemented {
    }
}

