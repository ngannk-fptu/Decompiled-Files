/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.core.Macro;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._CoreAPI;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BooleanModel;
import freemarker.ext.beans.CollectionModel;
import freemarker.ext.beans.DateModel;
import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.IteratorModel;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.NumberModel;
import freemarker.ext.beans.OverloadedMethodsModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ClassUtil {
    private static final Map<String, Class<?>> PRIMITIVE_CLASSES_BY_NAME = new HashMap();

    private ClassUtil() {
    }

    public static Class forName(String className) throws ClassNotFoundException {
        try {
            ClassLoader ctcl = Thread.currentThread().getContextClassLoader();
            if (ctcl != null) {
                return Class.forName(className, true, ctcl);
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return Class.forName(className);
    }

    public static Class<?> resolveIfPrimitiveTypeName(String typeName) {
        return PRIMITIVE_CLASSES_BY_NAME.get(typeName);
    }

    public static Class<?> getArrayClass(Class<?> elementType, int dimensions) {
        return dimensions == 0 ? elementType : Array.newInstance(elementType, new int[dimensions]).getClass();
    }

    public static String getShortClassName(Class pClass) {
        return ClassUtil.getShortClassName(pClass, false);
    }

    public static String getShortClassName(Class pClass, boolean shortenFreeMarkerClasses) {
        if (pClass == null) {
            return null;
        }
        if (pClass.isArray()) {
            return ClassUtil.getShortClassName(pClass.getComponentType()) + "[]";
        }
        String cn = pClass.getName();
        if (cn.startsWith("java.lang.") || cn.startsWith("java.util.")) {
            return cn.substring(10);
        }
        if (shortenFreeMarkerClasses) {
            if (cn.startsWith("freemarker.template.")) {
                return "f.t" + cn.substring(19);
            }
            if (cn.startsWith("freemarker.ext.beans.")) {
                return "f.e.b" + cn.substring(20);
            }
            if (cn.startsWith("freemarker.core.")) {
                return "f.c" + cn.substring(15);
            }
            if (cn.startsWith("freemarker.ext.")) {
                return "f.e" + cn.substring(14);
            }
            if (cn.startsWith("freemarker.")) {
                return "f" + cn.substring(10);
            }
        }
        return cn;
    }

    public static String getShortClassNameOfObject(Object obj) {
        return ClassUtil.getShortClassNameOfObject(obj, false);
    }

    public static String getShortClassNameOfObject(Object obj, boolean shortenFreeMarkerClasses) {
        if (obj == null) {
            return "Null";
        }
        return ClassUtil.getShortClassName(obj.getClass(), shortenFreeMarkerClasses);
    }

    private static Class getPrimaryTemplateModelInterface(TemplateModel tm) {
        if (tm instanceof BeanModel) {
            if (tm instanceof CollectionModel) {
                return TemplateSequenceModel.class;
            }
            if (tm instanceof IteratorModel || tm instanceof EnumerationModel) {
                return TemplateCollectionModel.class;
            }
            if (tm instanceof MapModel) {
                return TemplateHashModelEx.class;
            }
            if (tm instanceof NumberModel) {
                return TemplateNumberModel.class;
            }
            if (tm instanceof BooleanModel) {
                return TemplateBooleanModel.class;
            }
            if (tm instanceof DateModel) {
                return TemplateDateModel.class;
            }
            if (tm instanceof StringModel) {
                Object wrapped = ((BeanModel)tm).getWrappedObject();
                return wrapped instanceof String ? TemplateScalarModel.class : (tm instanceof TemplateHashModelEx ? TemplateHashModelEx.class : null);
            }
            return null;
        }
        if (tm instanceof SimpleMethodModel || tm instanceof OverloadedMethodsModel) {
            return TemplateMethodModelEx.class;
        }
        if (tm instanceof TemplateCollectionModel && _CoreAPI.isLazilyGeneratedSequenceModel((TemplateCollectionModel)tm)) {
            return TemplateSequenceModel.class;
        }
        return null;
    }

    private static void appendTemplateModelTypeName(StringBuilder sb, Set typeNamesAppended, Class cl) {
        int initalLength = sb.length();
        if (TemplateNodeModelEx.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "extended node");
        } else if (TemplateNodeModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "node");
        }
        if (TemplateDirectiveModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "directive");
        } else if (TemplateTransformModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "transform");
        }
        if (TemplateSequenceModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "sequence");
        } else if (TemplateCollectionModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, TemplateCollectionModelEx.class.isAssignableFrom(cl) ? "extended_collection" : "collection");
        } else if (TemplateModelIterator.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "iterator");
        }
        if (TemplateMethodModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "method");
        }
        if (Environment.Namespace.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "namespace");
        } else if (TemplateHashModelEx.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "extended_hash");
        } else if (TemplateHashModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "hash");
        }
        if (TemplateNumberModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "number");
        }
        if (TemplateDateModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "date_or_time_or_datetime");
        }
        if (TemplateBooleanModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "boolean");
        }
        if (TemplateScalarModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "string");
        }
        if (TemplateMarkupOutputModel.class.isAssignableFrom(cl)) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "markup_output");
        }
        if (sb.length() == initalLength) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, "misc_template_model");
        }
    }

    private static Class getUnwrappedClass(TemplateModel tm) {
        Object unwrapped;
        try {
            unwrapped = tm instanceof WrapperTemplateModel ? ((WrapperTemplateModel)tm).getWrappedObject() : (tm instanceof AdapterTemplateModel ? ((AdapterTemplateModel)tm).getAdaptedObject(Object.class) : null);
        }
        catch (Throwable e) {
            unwrapped = null;
        }
        return unwrapped != null ? unwrapped.getClass() : null;
    }

    private static void appendTypeName(StringBuilder sb, Set typeNamesAppended, String name) {
        if (!typeNamesAppended.contains(name)) {
            if (sb.length() != 0) {
                sb.append("+");
            }
            sb.append(name);
            typeNamesAppended.add(name);
        }
    }

    public static String getFTLTypeDescription(TemplateModel tm) {
        if (tm == null) {
            return "Null";
        }
        HashSet typeNamesAppended = new HashSet();
        StringBuilder sb = new StringBuilder();
        Class primaryInterface = ClassUtil.getPrimaryTemplateModelInterface(tm);
        if (primaryInterface != null) {
            ClassUtil.appendTemplateModelTypeName(sb, typeNamesAppended, primaryInterface);
        }
        if (tm instanceof Macro) {
            ClassUtil.appendTypeName(sb, typeNamesAppended, ((Macro)tm).isFunction() ? "function" : "macro");
        }
        ClassUtil.appendTemplateModelTypeName(sb, typeNamesAppended, tm.getClass());
        Class unwrappedClass = ClassUtil.getUnwrappedClass(tm);
        String javaClassName = unwrappedClass != null ? ClassUtil.getShortClassName(unwrappedClass, true) : null;
        sb.append(" (");
        String modelClassName = ClassUtil.getShortClassName(tm.getClass(), true);
        if (javaClassName == null) {
            sb.append("wrapper: ");
            sb.append(modelClassName);
        } else {
            sb.append(javaClassName);
            sb.append(" wrapped into ");
            sb.append(modelClassName);
        }
        sb.append(")");
        return sb.toString();
    }

    public static Class primitiveClassToBoxingClass(Class primitiveClass) {
        if (primitiveClass == Integer.TYPE) {
            return Integer.class;
        }
        if (primitiveClass == Boolean.TYPE) {
            return Boolean.class;
        }
        if (primitiveClass == Long.TYPE) {
            return Long.class;
        }
        if (primitiveClass == Double.TYPE) {
            return Double.class;
        }
        if (primitiveClass == Character.TYPE) {
            return Character.class;
        }
        if (primitiveClass == Float.TYPE) {
            return Float.class;
        }
        if (primitiveClass == Byte.TYPE) {
            return Byte.class;
        }
        if (primitiveClass == Short.TYPE) {
            return Short.class;
        }
        if (primitiveClass == Void.TYPE) {
            return Void.class;
        }
        return primitiveClass;
    }

    public static Class boxingClassToPrimitiveClass(Class boxingClass) {
        if (boxingClass == Integer.class) {
            return Integer.TYPE;
        }
        if (boxingClass == Boolean.class) {
            return Boolean.TYPE;
        }
        if (boxingClass == Long.class) {
            return Long.TYPE;
        }
        if (boxingClass == Double.class) {
            return Double.TYPE;
        }
        if (boxingClass == Character.class) {
            return Character.TYPE;
        }
        if (boxingClass == Float.class) {
            return Float.TYPE;
        }
        if (boxingClass == Byte.class) {
            return Byte.TYPE;
        }
        if (boxingClass == Short.class) {
            return Short.TYPE;
        }
        if (boxingClass == Void.class) {
            return Void.TYPE;
        }
        return boxingClass;
    }

    public static boolean isNumerical(Class type) {
        return Number.class.isAssignableFrom(type) || type.isPrimitive() && type != Boolean.TYPE && type != Character.TYPE && type != Void.TYPE;
    }

    public static InputStream getReasourceAsStream(Class<?> baseClass, String resource, boolean optional) throws IOException {
        InputStream ins;
        try {
            ins = baseClass.getResourceAsStream(resource);
        }
        catch (Exception e) {
            URL url = baseClass.getResource(resource);
            InputStream inputStream = ins = url != null ? url.openStream() : null;
        }
        if (!optional) {
            ClassUtil.checkInputStreamNotNull(ins, baseClass, resource);
        }
        return ins;
    }

    public static InputStream getReasourceAsStream(ClassLoader classLoader, String resource, boolean optional) throws IOException {
        InputStream ins;
        try {
            ins = classLoader.getResourceAsStream(resource);
        }
        catch (Exception e) {
            URL url = classLoader.getResource(resource);
            InputStream inputStream = ins = url != null ? url.openStream() : null;
        }
        if (ins == null && !optional) {
            throw new IOException("Class-loader resource not found (shown quoted): " + StringUtil.jQuote(resource) + ". The base ClassLoader was: " + classLoader);
        }
        return ins;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties loadProperties(Class<?> baseClass, String resource) throws IOException {
        Properties props = new Properties();
        InputStream ins = null;
        try {
            try {
                ins = baseClass.getResourceAsStream(resource);
            }
            catch (Exception e) {
                throw new MaybeZipFileClosedException();
            }
            ClassUtil.checkInputStreamNotNull(ins, baseClass, resource);
            try {
                props.load(ins);
            }
            catch (Exception e) {
                throw new MaybeZipFileClosedException();
            }
            finally {
                try {
                    ins.close();
                }
                catch (Exception exception) {}
                ins = null;
            }
        }
        catch (MaybeZipFileClosedException e) {
            URL url = baseClass.getResource(resource);
            ins = url != null ? url.openStream() : null;
            ClassUtil.checkInputStreamNotNull(ins, baseClass, resource);
            props.load(ins);
        }
        finally {
            if (ins != null) {
                try {
                    ins.close();
                }
                catch (Exception exception) {}
            }
        }
        return props;
    }

    private static void checkInputStreamNotNull(InputStream ins, Class<?> baseClass, String resource) throws IOException {
        if (ins == null) {
            throw new IOException("Class-loader resource not found (shown quoted): " + StringUtil.jQuote(resource) + ". The base class was " + baseClass.getName() + ".");
        }
    }

    static {
        PRIMITIVE_CLASSES_BY_NAME.put("boolean", Boolean.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("byte", Byte.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("char", Character.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("short", Short.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("int", Integer.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("long", Long.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("float", Float.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("double", Double.TYPE);
    }

    private static class MaybeZipFileClosedException
    extends Exception {
        private MaybeZipFileClosedException() {
        }
    }
}

