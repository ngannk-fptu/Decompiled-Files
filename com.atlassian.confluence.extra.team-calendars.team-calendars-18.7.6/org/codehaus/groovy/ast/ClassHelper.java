/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;
import groovy.lang.Range;
import groovy.lang.Reference;
import groovy.lang.Script;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.ref.SoftReference;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.trait.Traits;
import org.codehaus.groovy.util.ManagedConcurrentMap;
import org.codehaus.groovy.util.ReferenceBundle;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public class ClassHelper {
    private static final Class[] classes = new Class[]{Object.class, Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Double.TYPE, Float.TYPE, Void.TYPE, Closure.class, GString.class, List.class, Map.class, Range.class, Pattern.class, Script.class, String.class, Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, Double.class, Float.class, BigDecimal.class, BigInteger.class, Number.class, Void.class, Reference.class, Class.class, MetaClass.class, Iterator.class, GeneratedClosure.class, GroovyObjectSupport.class};
    private static final String[] primitiveClassNames = new String[]{"", "boolean", "char", "byte", "short", "int", "long", "double", "float", "void"};
    public static final ClassNode DYNAMIC_TYPE;
    public static final ClassNode OBJECT_TYPE;
    public static final ClassNode VOID_TYPE;
    public static final ClassNode CLOSURE_TYPE;
    public static final ClassNode GSTRING_TYPE;
    public static final ClassNode LIST_TYPE;
    public static final ClassNode MAP_TYPE;
    public static final ClassNode RANGE_TYPE;
    public static final ClassNode PATTERN_TYPE;
    public static final ClassNode STRING_TYPE;
    public static final ClassNode SCRIPT_TYPE;
    public static final ClassNode REFERENCE_TYPE;
    public static final ClassNode BINDING_TYPE;
    public static final ClassNode boolean_TYPE;
    public static final ClassNode char_TYPE;
    public static final ClassNode byte_TYPE;
    public static final ClassNode int_TYPE;
    public static final ClassNode long_TYPE;
    public static final ClassNode short_TYPE;
    public static final ClassNode double_TYPE;
    public static final ClassNode float_TYPE;
    public static final ClassNode Byte_TYPE;
    public static final ClassNode Short_TYPE;
    public static final ClassNode Integer_TYPE;
    public static final ClassNode Long_TYPE;
    public static final ClassNode Character_TYPE;
    public static final ClassNode Float_TYPE;
    public static final ClassNode Double_TYPE;
    public static final ClassNode Boolean_TYPE;
    public static final ClassNode BigInteger_TYPE;
    public static final ClassNode BigDecimal_TYPE;
    public static final ClassNode Number_TYPE;
    public static final ClassNode void_WRAPPER_TYPE;
    public static final ClassNode METACLASS_TYPE;
    public static final ClassNode Iterator_TYPE;
    public static final ClassNode Enum_Type;
    public static final ClassNode Annotation_TYPE;
    public static final ClassNode ELEMENT_TYPE_TYPE;
    public static final ClassNode CLASS_Type;
    public static final ClassNode COMPARABLE_TYPE;
    public static final ClassNode GENERATED_CLOSURE_Type;
    public static final ClassNode GROOVY_OBJECT_SUPPORT_TYPE;
    public static final ClassNode GROOVY_OBJECT_TYPE;
    public static final ClassNode GROOVY_INTERCEPTABLE_TYPE;
    private static final ClassNode[] types;
    private static final int ABSTRACT_STATIC_PRIVATE = 1034;
    private static final int VISIBILITY = 5;
    protected static final ClassNode[] EMPTY_TYPE_ARRAY;
    public static final String OBJECT = "java.lang.Object";

    public static ClassNode makeCached(Class c) {
        ClassNode classNode;
        SoftReference classNodeSoftReference = (SoftReference)ClassHelperCache.classCache.get(c);
        if (classNodeSoftReference == null || (classNode = (ClassNode)classNodeSoftReference.get()) == null) {
            classNode = new ClassNode(c);
            ClassHelperCache.classCache.put(c, new SoftReference<ClassNode>(classNode));
            VMPluginFactory.getPlugin().setAdditionalClassInformation(classNode);
        }
        return classNode;
    }

    public static ClassNode[] make(Class[] classes) {
        ClassNode[] cns = new ClassNode[classes.length];
        for (int i = 0; i < cns.length; ++i) {
            cns[i] = ClassHelper.make(classes[i]);
        }
        return cns;
    }

    public static ClassNode make(Class c) {
        return ClassHelper.make(c, true);
    }

    public static ClassNode make(Class c, boolean includeGenerics) {
        for (int i = 0; i < classes.length; ++i) {
            if (c != classes[i]) continue;
            return types[i];
        }
        if (c.isArray()) {
            ClassNode cn = ClassHelper.make(c.getComponentType(), includeGenerics);
            return cn.makeArray();
        }
        return ClassHelper.makeWithoutCaching(c, includeGenerics);
    }

    public static ClassNode makeWithoutCaching(Class c) {
        return ClassHelper.makeWithoutCaching(c, true);
    }

    public static ClassNode makeWithoutCaching(Class c, boolean includeGenerics) {
        if (c.isArray()) {
            ClassNode cn = ClassHelper.makeWithoutCaching(c.getComponentType(), includeGenerics);
            return cn.makeArray();
        }
        ClassNode cached = ClassHelper.makeCached(c);
        if (includeGenerics) {
            return cached;
        }
        ClassNode t = ClassHelper.makeWithoutCaching(c.getName());
        t.setRedirect(cached);
        return t;
    }

    public static ClassNode makeWithoutCaching(String name) {
        ClassNode cn = new ClassNode(name, 1, OBJECT_TYPE);
        cn.isPrimaryNode = false;
        return cn;
    }

    public static ClassNode make(String name) {
        int i;
        if (name == null || name.length() == 0) {
            return DYNAMIC_TYPE;
        }
        for (i = 0; i < primitiveClassNames.length; ++i) {
            if (!primitiveClassNames[i].equals(name)) continue;
            return types[i];
        }
        for (i = 0; i < classes.length; ++i) {
            String cname = classes[i].getName();
            if (!name.equals(cname)) continue;
            return types[i];
        }
        return ClassHelper.makeWithoutCaching(name);
    }

    public static ClassNode getWrapper(ClassNode cn) {
        if (!ClassHelper.isPrimitiveType(cn = cn.redirect())) {
            return cn;
        }
        if (cn == boolean_TYPE) {
            return Boolean_TYPE;
        }
        if (cn == byte_TYPE) {
            return Byte_TYPE;
        }
        if (cn == char_TYPE) {
            return Character_TYPE;
        }
        if (cn == short_TYPE) {
            return Short_TYPE;
        }
        if (cn == int_TYPE) {
            return Integer_TYPE;
        }
        if (cn == long_TYPE) {
            return Long_TYPE;
        }
        if (cn == float_TYPE) {
            return Float_TYPE;
        }
        if (cn == double_TYPE) {
            return Double_TYPE;
        }
        if (cn == VOID_TYPE) {
            return void_WRAPPER_TYPE;
        }
        return cn;
    }

    public static ClassNode getUnwrapper(ClassNode cn) {
        if (ClassHelper.isPrimitiveType(cn = cn.redirect())) {
            return cn;
        }
        if (cn == Boolean_TYPE) {
            return boolean_TYPE;
        }
        if (cn == Byte_TYPE) {
            return byte_TYPE;
        }
        if (cn == Character_TYPE) {
            return char_TYPE;
        }
        if (cn == Short_TYPE) {
            return short_TYPE;
        }
        if (cn == Integer_TYPE) {
            return int_TYPE;
        }
        if (cn == Long_TYPE) {
            return long_TYPE;
        }
        if (cn == Float_TYPE) {
            return float_TYPE;
        }
        if (cn == Double_TYPE) {
            return double_TYPE;
        }
        return cn;
    }

    public static boolean isPrimitiveType(ClassNode cn) {
        return cn == boolean_TYPE || cn == char_TYPE || cn == byte_TYPE || cn == short_TYPE || cn == int_TYPE || cn == long_TYPE || cn == float_TYPE || cn == double_TYPE || cn == VOID_TYPE;
    }

    public static boolean isStaticConstantInitializerType(ClassNode cn) {
        return cn == int_TYPE || cn == float_TYPE || cn == long_TYPE || cn == double_TYPE || cn == STRING_TYPE || cn == byte_TYPE || cn == char_TYPE || cn == short_TYPE;
    }

    public static boolean isNumberType(ClassNode cn) {
        return cn == Byte_TYPE || cn == Short_TYPE || cn == Integer_TYPE || cn == Long_TYPE || cn == Float_TYPE || cn == Double_TYPE || cn == byte_TYPE || cn == short_TYPE || cn == int_TYPE || cn == long_TYPE || cn == float_TYPE || cn == double_TYPE;
    }

    public static ClassNode makeReference() {
        return REFERENCE_TYPE.getPlainNodeReference();
    }

    public static boolean isCachedType(ClassNode type) {
        for (ClassNode cachedType : types) {
            if (cachedType != type) continue;
            return true;
        }
        return false;
    }

    public static boolean isSAMType(ClassNode type) {
        return ClassHelper.findSAM(type) != null;
    }

    public static MethodNode findSAM(ClassNode type) {
        if (!Modifier.isAbstract(type.getModifiers())) {
            return null;
        }
        if (type.isInterface()) {
            List<MethodNode> methods = type.isInterface() ? type.redirect().getAllDeclaredMethods() : type.getMethods();
            MethodNode found = null;
            for (MethodNode mi : methods) {
                if (!Modifier.isAbstract(mi.getModifiers()) || Traits.hasDefaultImplementation(mi) || mi.getDeclaringClass().equals(OBJECT_TYPE) || OBJECT_TYPE.getDeclaredMethod(mi.getName(), mi.getParameters()) != null) continue;
                if (found != null) {
                    return null;
                }
                found = mi;
            }
            return found;
        }
        List<MethodNode> methods = type.getAbstractMethods();
        MethodNode found = null;
        if (methods != null) {
            for (MethodNode mi : methods) {
                if (ClassHelper.hasUsableImplementation(type, mi)) continue;
                if (found != null) {
                    return null;
                }
                found = mi;
            }
        }
        return found;
    }

    private static boolean hasUsableImplementation(ClassNode c, MethodNode m) {
        if (c == m.getDeclaringClass()) {
            return false;
        }
        MethodNode found = c.getDeclaredMethod(m.getName(), m.getParameters());
        if (found == null) {
            return false;
        }
        int asp = found.getModifiers() & 0x40A;
        int visible = found.getModifiers() & 5;
        if (visible != 0 && asp == 0) {
            return true;
        }
        if (c.equals(OBJECT_TYPE)) {
            return false;
        }
        return ClassHelper.hasUsableImplementation(c.getSuperClass(), m);
    }

    public static ClassNode getNextSuperClass(ClassNode clazz, ClassNode goalClazz) {
        ClassNode[] interfaces;
        if (clazz.isArray()) {
            if (!goalClazz.isArray()) {
                return null;
            }
            ClassNode cn = ClassHelper.getNextSuperClass(clazz.getComponentType(), goalClazz.getComponentType());
            if (cn != null) {
                cn = cn.makeArray();
            }
            return cn;
        }
        if (!goalClazz.isInterface()) {
            if (clazz.isInterface()) {
                if (OBJECT_TYPE.equals(clazz)) {
                    return null;
                }
                return OBJECT_TYPE;
            }
            return clazz.getUnresolvedSuperClass();
        }
        for (ClassNode anInterface : interfaces = clazz.getUnresolvedInterfaces()) {
            if (!StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(anInterface, goalClazz)) continue;
            return anInterface;
        }
        return clazz.getUnresolvedSuperClass();
    }

    static {
        OBJECT_TYPE = DYNAMIC_TYPE = ClassHelper.makeCached(Object.class);
        VOID_TYPE = ClassHelper.makeCached(Void.TYPE);
        CLOSURE_TYPE = ClassHelper.makeCached(Closure.class);
        GSTRING_TYPE = ClassHelper.makeCached(GString.class);
        LIST_TYPE = ClassHelper.makeWithoutCaching(List.class);
        MAP_TYPE = ClassHelper.makeWithoutCaching(Map.class);
        RANGE_TYPE = ClassHelper.makeCached(Range.class);
        PATTERN_TYPE = ClassHelper.makeCached(Pattern.class);
        STRING_TYPE = ClassHelper.makeCached(String.class);
        SCRIPT_TYPE = ClassHelper.makeCached(Script.class);
        REFERENCE_TYPE = ClassHelper.makeWithoutCaching(Reference.class);
        BINDING_TYPE = ClassHelper.makeCached(Binding.class);
        boolean_TYPE = ClassHelper.makeCached(Boolean.TYPE);
        char_TYPE = ClassHelper.makeCached(Character.TYPE);
        byte_TYPE = ClassHelper.makeCached(Byte.TYPE);
        int_TYPE = ClassHelper.makeCached(Integer.TYPE);
        long_TYPE = ClassHelper.makeCached(Long.TYPE);
        short_TYPE = ClassHelper.makeCached(Short.TYPE);
        double_TYPE = ClassHelper.makeCached(Double.TYPE);
        float_TYPE = ClassHelper.makeCached(Float.TYPE);
        Byte_TYPE = ClassHelper.makeCached(Byte.class);
        Short_TYPE = ClassHelper.makeCached(Short.class);
        Integer_TYPE = ClassHelper.makeCached(Integer.class);
        Long_TYPE = ClassHelper.makeCached(Long.class);
        Character_TYPE = ClassHelper.makeCached(Character.class);
        Float_TYPE = ClassHelper.makeCached(Float.class);
        Double_TYPE = ClassHelper.makeCached(Double.class);
        Boolean_TYPE = ClassHelper.makeCached(Boolean.class);
        BigInteger_TYPE = ClassHelper.makeCached(BigInteger.class);
        BigDecimal_TYPE = ClassHelper.makeCached(BigDecimal.class);
        Number_TYPE = ClassHelper.makeCached(Number.class);
        void_WRAPPER_TYPE = ClassHelper.makeCached(Void.class);
        METACLASS_TYPE = ClassHelper.makeCached(MetaClass.class);
        Iterator_TYPE = ClassHelper.makeCached(Iterator.class);
        Enum_Type = ClassHelper.makeWithoutCaching(Enum.class);
        Annotation_TYPE = ClassHelper.makeCached(Annotation.class);
        ELEMENT_TYPE_TYPE = ClassHelper.makeCached(ElementType.class);
        CLASS_Type = ClassHelper.makeWithoutCaching(Class.class);
        COMPARABLE_TYPE = ClassHelper.makeWithoutCaching(Comparable.class);
        GENERATED_CLOSURE_Type = ClassHelper.makeWithoutCaching(GeneratedClosure.class);
        GROOVY_OBJECT_SUPPORT_TYPE = ClassHelper.makeWithoutCaching(GroovyObjectSupport.class);
        GROOVY_OBJECT_TYPE = ClassHelper.makeWithoutCaching(GroovyObject.class);
        GROOVY_INTERCEPTABLE_TYPE = ClassHelper.makeWithoutCaching(GroovyInterceptable.class);
        types = new ClassNode[]{OBJECT_TYPE, boolean_TYPE, char_TYPE, byte_TYPE, short_TYPE, int_TYPE, long_TYPE, double_TYPE, float_TYPE, VOID_TYPE, CLOSURE_TYPE, GSTRING_TYPE, LIST_TYPE, MAP_TYPE, RANGE_TYPE, PATTERN_TYPE, SCRIPT_TYPE, STRING_TYPE, Boolean_TYPE, Character_TYPE, Byte_TYPE, Short_TYPE, Integer_TYPE, Long_TYPE, Double_TYPE, Float_TYPE, BigDecimal_TYPE, BigInteger_TYPE, Number_TYPE, void_WRAPPER_TYPE, REFERENCE_TYPE, CLASS_Type, METACLASS_TYPE, Iterator_TYPE, GENERATED_CLOSURE_Type, GROOVY_OBJECT_SUPPORT_TYPE, GROOVY_OBJECT_TYPE, GROOVY_INTERCEPTABLE_TYPE, Enum_Type, Annotation_TYPE};
        EMPTY_TYPE_ARRAY = new ClassNode[0];
    }

    static class ClassHelperCache {
        static ManagedConcurrentMap<Class, SoftReference<ClassNode>> classCache = new ManagedConcurrentMap(ReferenceBundle.getWeakBundle());

        ClassHelperCache() {
        }
    }
}

