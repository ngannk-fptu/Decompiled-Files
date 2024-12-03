/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v5;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.vmplugin.VMPlugin;
import org.codehaus.groovy.vmplugin.v5.PluginDefaultGroovyMethods;

public class Java5
implements VMPlugin {
    private static Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Class[] PLUGIN_DGM = new Class[]{PluginDefaultGroovyMethods.class};

    @Override
    public void setAdditionalClassInformation(ClassNode cn) {
        this.setGenericsTypes(cn);
    }

    private void setGenericsTypes(ClassNode cn) {
        TypeVariable[] tvs = cn.getTypeClass().getTypeParameters();
        GenericsType[] gts = this.configureTypeVariable(tvs);
        cn.setGenericsTypes(gts);
    }

    private GenericsType[] configureTypeVariable(TypeVariable[] tvs) {
        if (tvs.length == 0) {
            return null;
        }
        GenericsType[] gts = new GenericsType[tvs.length];
        for (int i = 0; i < tvs.length; ++i) {
            gts[i] = this.configureTypeVariableDefinition(tvs[i]);
        }
        return gts;
    }

    private GenericsType configureTypeVariableDefinition(TypeVariable tv) {
        GenericsType gt;
        ClassNode base = this.configureTypeVariableReference(tv);
        ClassNode redirect = base.redirect();
        base.setRedirect(null);
        Type[] tBounds = tv.getBounds();
        if (tBounds.length == 0) {
            gt = new GenericsType(base);
        } else {
            ClassNode[] cBounds = this.configureTypes(tBounds);
            gt = new GenericsType(base, cBounds, null);
            gt.setName(base.getName());
            gt.setPlaceholder(true);
        }
        base.setRedirect(redirect);
        return gt;
    }

    private ClassNode[] configureTypes(Type[] types) {
        if (types.length == 0) {
            return null;
        }
        ClassNode[] nodes = new ClassNode[types.length];
        for (int i = 0; i < types.length; ++i) {
            nodes[i] = this.configureType(types[i]);
        }
        return nodes;
    }

    private ClassNode configureType(Type type) {
        if (type instanceof WildcardType) {
            return this.configureWildcardType((WildcardType)type);
        }
        if (type instanceof ParameterizedType) {
            return this.configureParameterizedType((ParameterizedType)type);
        }
        if (type instanceof GenericArrayType) {
            return this.configureGenericArray((GenericArrayType)type);
        }
        if (type instanceof TypeVariable) {
            return this.configureTypeVariableReference((TypeVariable)type);
        }
        if (type instanceof Class) {
            return Java5.configureClass((Class)type);
        }
        if (type == null) {
            throw new GroovyBugError("Type is null. Most probably you let a transform reuse existing ClassNodes with generics information, that is now used in a wrong context.");
        }
        throw new GroovyBugError("unknown type: " + type + " := " + type.getClass());
    }

    private static ClassNode configureClass(Class c) {
        if (c.isPrimitive()) {
            return ClassHelper.make(c);
        }
        return ClassHelper.makeWithoutCaching(c, false);
    }

    private ClassNode configureGenericArray(GenericArrayType genericArrayType) {
        Type component = genericArrayType.getGenericComponentType();
        ClassNode node = this.configureType(component);
        return node.makeArray();
    }

    private ClassNode configureWildcardType(WildcardType wildcardType) {
        ClassNode base = ClassHelper.makeWithoutCaching("?");
        base.setRedirect(ClassHelper.OBJECT_TYPE);
        ClassNode[] lowers = this.configureTypes(wildcardType.getLowerBounds());
        ClassNode lower = null;
        if (lowers != null) {
            lower = lowers[0];
        }
        ClassNode[] upper = this.configureTypes(wildcardType.getUpperBounds());
        GenericsType t = new GenericsType(base, upper, lower);
        t.setWildcard(true);
        ClassNode ref = ClassHelper.makeWithoutCaching(Object.class, false);
        ref.setGenericsTypes(new GenericsType[]{t});
        return ref;
    }

    private ClassNode configureParameterizedType(ParameterizedType parameterizedType) {
        ClassNode base = this.configureType(parameterizedType.getRawType());
        GenericsType[] gts = this.configureTypeArguments(parameterizedType.getActualTypeArguments());
        base.setGenericsTypes(gts);
        return base;
    }

    private ClassNode configureTypeVariableReference(TypeVariable tv) {
        ClassNode cn = ClassHelper.makeWithoutCaching(tv.getName());
        cn.setGenericsPlaceHolder(true);
        ClassNode cn2 = ClassHelper.makeWithoutCaching(tv.getName());
        cn2.setGenericsPlaceHolder(true);
        GenericsType[] gts = new GenericsType[]{new GenericsType(cn2)};
        cn.setGenericsTypes(gts);
        cn.setRedirect(ClassHelper.OBJECT_TYPE);
        return cn;
    }

    private GenericsType[] configureTypeArguments(Type[] ta) {
        if (ta.length == 0) {
            return null;
        }
        GenericsType[] gts = new GenericsType[ta.length];
        for (int i = 0; i < ta.length; ++i) {
            ClassNode t = this.configureType(ta[i]);
            if (ta[i] instanceof WildcardType) {
                GenericsType[] gen = t.getGenericsTypes();
                gts[i] = gen[0];
                continue;
            }
            gts[i] = new GenericsType(t);
        }
        return gts;
    }

    @Override
    public Class[] getPluginDefaultGroovyMethods() {
        return PLUGIN_DGM;
    }

    @Override
    public Class[] getPluginStaticGroovyMethods() {
        return EMPTY_CLASS_ARRAY;
    }

    private void setAnnotationMetaData(Annotation[] annotations, AnnotatedNode an) {
        for (Annotation annotation : annotations) {
            AnnotationNode node = new AnnotationNode(ClassHelper.make(annotation.annotationType()));
            this.configureAnnotation(node, annotation);
            an.addAnnotation(node);
        }
    }

    private void configureAnnotationFromDefinition(AnnotationNode definition, AnnotationNode root) {
        ClassNode type = definition.getClassNode();
        if (!type.isResolved()) {
            return;
        }
        Class clazz = type.getTypeClass();
        if (clazz == Retention.class) {
            Expression exp = definition.getMember("value");
            if (!(exp instanceof PropertyExpression)) {
                return;
            }
            PropertyExpression pe = (PropertyExpression)exp;
            String name = pe.getPropertyAsString();
            RetentionPolicy policy = RetentionPolicy.valueOf(name);
            this.setRetentionPolicy(policy, root);
        } else if (clazz == Target.class) {
            Expression exp = definition.getMember("value");
            if (!(exp instanceof ListExpression)) {
                return;
            }
            ListExpression le = (ListExpression)exp;
            int bitmap = 0;
            for (Expression e : le.getExpressions()) {
                if (!(e instanceof PropertyExpression)) {
                    return;
                }
                PropertyExpression element = (PropertyExpression)e;
                String name = element.getPropertyAsString();
                ElementType value = ElementType.valueOf(name);
                bitmap |= this.getElementCode(value);
            }
            root.setAllowedTargets(bitmap);
        }
    }

    @Override
    public void configureAnnotation(AnnotationNode node) {
        ClassNode type = node.getClassNode();
        List<AnnotationNode> annotations = type.getAnnotations();
        for (AnnotationNode an : annotations) {
            this.configureAnnotationFromDefinition(an, node);
        }
        this.configureAnnotationFromDefinition(node, node);
    }

    private void configureAnnotation(AnnotationNode node, Annotation annotation) {
        Class<? extends Annotation> type = annotation.annotationType();
        if (type == Retention.class) {
            Retention r = (Retention)annotation;
            RetentionPolicy value = r.value();
            this.setRetentionPolicy(value, node);
            node.setMember("value", new PropertyExpression((Expression)new ClassExpression(ClassHelper.makeWithoutCaching(RetentionPolicy.class, false)), value.toString()));
        } else if (type == Target.class) {
            Target t = (Target)annotation;
            ElementType[] elements = t.value();
            ListExpression elementExprs = new ListExpression();
            for (ElementType element : elements) {
                elementExprs.addExpression(new PropertyExpression((Expression)new ClassExpression(ClassHelper.ELEMENT_TYPE_TYPE), element.name()));
            }
            node.setMember("value", elementExprs);
        } else {
            Method[] declaredMethods;
            try {
                declaredMethods = type.getDeclaredMethods();
            }
            catch (SecurityException se) {
                declaredMethods = new Method[]{};
            }
            for (Method declaredMethod : declaredMethods) {
                try {
                    Object value = declaredMethod.invoke((Object)annotation, new Object[0]);
                    Expression valueExpression = this.annotationValueToExpression(value);
                    if (valueExpression == null) continue;
                    node.setMember(declaredMethod.getName(), valueExpression);
                }
                catch (IllegalAccessException illegalAccessException) {
                }
                catch (InvocationTargetException invocationTargetException) {
                    // empty catch block
                }
            }
        }
    }

    private Expression annotationValueToExpression(Object value) {
        if (value == null || value instanceof String || value instanceof Number || value instanceof Character || value instanceof Boolean) {
            return new ConstantExpression(value);
        }
        if (value instanceof Class) {
            return new ClassExpression(ClassHelper.makeWithoutCaching((Class)value));
        }
        if (value.getClass().isArray()) {
            ListExpression elementExprs = new ListExpression();
            int len = Array.getLength(value);
            for (int i = 0; i != len; ++i) {
                elementExprs.addExpression(this.annotationValueToExpression(Array.get(value, i)));
            }
            return elementExprs;
        }
        return null;
    }

    private void setRetentionPolicy(RetentionPolicy value, AnnotationNode node) {
        switch (value) {
            case RUNTIME: {
                node.setRuntimeRetention(true);
                break;
            }
            case SOURCE: {
                node.setSourceRetention(true);
                break;
            }
            case CLASS: {
                node.setClassRetention(true);
                break;
            }
            default: {
                throw new GroovyBugError("unsupported Retention " + (Object)((Object)value));
            }
        }
    }

    private int getElementCode(ElementType value) {
        switch (value) {
            case TYPE: {
                return 1;
            }
            case CONSTRUCTOR: {
                return 2;
            }
            case METHOD: {
                return 4;
            }
            case FIELD: {
                return 8;
            }
            case PARAMETER: {
                return 16;
            }
            case LOCAL_VARIABLE: {
                return 32;
            }
            case ANNOTATION_TYPE: {
                return 64;
            }
            case PACKAGE: {
                return 128;
            }
        }
        if ("TYPE_USE".equals(value.name()) || "TYPE_PARAMETER".equals(value.name()) || "MODULE".equals(value.name())) {
            return 0;
        }
        throw new GroovyBugError("unsupported Target " + (Object)((Object)value));
    }

    private static void setMethodDefaultValue(MethodNode mn, Method m) {
        Object defaultValue = m.getDefaultValue();
        ConstantExpression cExp = ConstantExpression.NULL;
        if (defaultValue != null) {
            cExp = new ConstantExpression(defaultValue);
        }
        mn.setCode(new ReturnStatement(cExp));
        mn.setAnnotationDefault(true);
    }

    @Override
    public void configureClassNode(CompileUnit compileUnit, ClassNode classNode) {
        try {
            Constructor<?>[] constructors;
            ClassNode[] exceptions;
            Parameter[] params;
            Method[] methods;
            Field[] fields;
            Class clazz = classNode.getTypeClass();
            for (Field f : fields = clazz.getDeclaredFields()) {
                ClassNode ret = this.makeClassNode(compileUnit, f.getGenericType(), f.getType());
                FieldNode fn = new FieldNode(f.getName(), f.getModifiers(), ret, classNode, null);
                this.setAnnotationMetaData(f.getAnnotations(), fn);
                classNode.addField(fn);
            }
            for (Method m : methods = clazz.getDeclaredMethods()) {
                ClassNode ret = this.makeClassNode(compileUnit, m.getGenericReturnType(), m.getReturnType());
                params = this.makeParameters(compileUnit, m.getGenericParameterTypes(), m.getParameterTypes(), m.getParameterAnnotations());
                exceptions = this.makeClassNodes(compileUnit, m.getGenericExceptionTypes(), m.getExceptionTypes());
                MethodNode mn = new MethodNode(m.getName(), m.getModifiers(), ret, params, exceptions, null);
                mn.setSynthetic(m.isSynthetic());
                Java5.setMethodDefaultValue(mn, m);
                this.setAnnotationMetaData(m.getAnnotations(), mn);
                mn.setGenericsTypes(this.configureTypeVariable(m.getTypeParameters()));
                classNode.addMethod(mn);
            }
            for (Constructor<?> ctor : constructors = clazz.getDeclaredConstructors()) {
                params = this.makeParameters(compileUnit, ctor.getGenericParameterTypes(), ctor.getParameterTypes(), ctor.getParameterAnnotations());
                exceptions = this.makeClassNodes(compileUnit, ctor.getGenericExceptionTypes(), ctor.getExceptionTypes());
                classNode.addConstructor(ctor.getModifiers(), params, exceptions, null);
            }
            Class sc = clazz.getSuperclass();
            if (sc != null) {
                classNode.setUnresolvedSuperClass(this.makeClassNode(compileUnit, clazz.getGenericSuperclass(), sc));
            }
            this.makeInterfaceTypes(compileUnit, classNode, clazz);
            this.setAnnotationMetaData(classNode.getTypeClass().getAnnotations(), classNode);
            PackageNode packageNode = classNode.getPackage();
            if (packageNode != null) {
                this.setAnnotationMetaData(classNode.getTypeClass().getPackage().getAnnotations(), packageNode);
            }
        }
        catch (NoClassDefFoundError e) {
            throw new NoClassDefFoundError("Unable to load class " + classNode.toString(false) + " due to missing dependency " + e.getMessage());
        }
        catch (MalformedParameterizedTypeException e) {
            throw new RuntimeException("Unable to configure class node for class " + classNode.toString(false) + " due to malformed parameterized types", e);
        }
    }

    private void makeInterfaceTypes(CompileUnit cu, ClassNode classNode, Class clazz) {
        Type[] interfaceTypes = clazz.getGenericInterfaces();
        if (interfaceTypes.length == 0) {
            classNode.setInterfaces(ClassNode.EMPTY_ARRAY);
        } else {
            ClassNode[] ret = new ClassNode[interfaceTypes.length];
            for (int i = 0; i < interfaceTypes.length; ++i) {
                Type type = interfaceTypes[i];
                while (!(type instanceof Class)) {
                    ParameterizedType pt = (ParameterizedType)type;
                    Type t2 = pt.getRawType();
                    if (t2 == type) {
                        throw new GroovyBugError("Cannot transform generic signature of " + clazz + " with generic interface " + interfaceTypes[i] + " to a class.");
                    }
                    type = t2;
                }
                ret[i] = this.makeClassNode(cu, interfaceTypes[i], (Class)type);
            }
            classNode.setInterfaces(ret);
        }
    }

    private ClassNode[] makeClassNodes(CompileUnit cu, Type[] types, Class[] cls) {
        ClassNode[] nodes = new ClassNode[types.length];
        for (int i = 0; i < nodes.length; ++i) {
            nodes[i] = this.makeClassNode(cu, types[i], cls[i]);
        }
        return nodes;
    }

    private ClassNode makeClassNode(CompileUnit cu, Type t, Class c) {
        ClassNode back = null;
        if (cu != null) {
            back = cu.getClass(c.getName());
        }
        if (back == null) {
            back = ClassHelper.make(c);
        }
        if (!(t instanceof Class)) {
            ClassNode front = this.configureType(t);
            front.setRedirect(back);
            return front;
        }
        return back.getPlainNodeReference();
    }

    private Parameter[] makeParameters(CompileUnit cu, Type[] types, Class[] cls, Annotation[][] parameterAnnotations) {
        Parameter[] params = Parameter.EMPTY_ARRAY;
        if (types.length > 0) {
            params = new Parameter[types.length];
            for (int i = 0; i < params.length; ++i) {
                params[i] = this.makeParameter(cu, types[i], cls[i], parameterAnnotations[i], i);
            }
        }
        return params;
    }

    private Parameter makeParameter(CompileUnit cu, Type type, Class cl, Annotation[] annotations, int idx) {
        ClassNode cn = this.makeClassNode(cu, type, cl);
        Parameter parameter = new Parameter(cn, "param" + idx);
        this.setAnnotationMetaData(annotations, parameter);
        return parameter;
    }

    @Override
    public void invalidateCallSites() {
    }

    @Override
    public Object getInvokeSpecialHandle(Method m, Object receiver) {
        throw new GroovyBugError("getInvokeSpecialHandle requires at least JDK 7 wot private access to Lookup");
    }

    @Override
    public int getVersion() {
        return 5;
    }

    @Override
    public Object invokeHandle(Object handle, Object[] args) throws Throwable {
        throw new GroovyBugError("invokeHandle requires at least JDK 7");
    }
}

