/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessOrder
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorOrder
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElement$DEFAULT
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlEnumValue
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.XmlValue
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter$DEFAULT
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters
 */
package org.codehaus.jackson.xc;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.Versioned;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.annotate.JsonCachable;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.map.util.BeanUtil;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.util.VersionUtil;
import org.codehaus.jackson.xc.XmlAdapterJsonDeserializer;
import org.codehaus.jackson.xc.XmlAdapterJsonSerializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JaxbAnnotationIntrospector
extends AnnotationIntrospector
implements Versioned {
    protected static final String MARKER_FOR_DEFAULT = "##default";
    protected final String _jaxbPackageName = XmlElement.class.getPackage().getName();
    protected final JsonSerializer<?> _dataHandlerSerializer;
    protected final JsonDeserializer<?> _dataHandlerDeserializer;

    public JaxbAnnotationIntrospector() {
        JsonSerializer dataHandlerSerializer = null;
        JsonDeserializer dataHandlerDeserializer = null;
        try {
            dataHandlerSerializer = (JsonSerializer)Class.forName("org.codehaus.jackson.xc.DataHandlerJsonSerializer").newInstance();
            dataHandlerDeserializer = (JsonDeserializer)Class.forName("org.codehaus.jackson.xc.DataHandlerJsonDeserializer").newInstance();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this._dataHandlerSerializer = dataHandlerSerializer;
        this._dataHandlerDeserializer = dataHandlerDeserializer;
    }

    @Override
    public Version version() {
        return VersionUtil.versionFor(this.getClass());
    }

    @Override
    public boolean isHandled(Annotation ann) {
        String pkgName;
        Class<? extends Annotation> cls = ann.annotationType();
        Package pkg = cls.getPackage();
        String string = pkgName = pkg != null ? pkg.getName() : cls.getName();
        if (pkgName.startsWith(this._jaxbPackageName)) {
            return true;
        }
        return cls == JsonCachable.class;
    }

    @Override
    public Boolean findCachability(AnnotatedClass ac) {
        JsonCachable ann = ac.getAnnotation(JsonCachable.class);
        if (ann != null) {
            return ann.value() ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }

    @Override
    public String findRootName(AnnotatedClass ac) {
        XmlRootElement elem = this.findRootElementAnnotation(ac);
        if (elem != null) {
            String name = elem.name();
            return MARKER_FOR_DEFAULT.equals(name) ? "" : name;
        }
        return null;
    }

    @Override
    public String[] findPropertiesToIgnore(AnnotatedClass ac) {
        return null;
    }

    @Override
    public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
        return null;
    }

    @Override
    public Boolean isIgnorableType(AnnotatedClass ac) {
        return null;
    }

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return m.getAnnotation(XmlTransient.class) != null;
    }

    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        XmlAccessType at = this.findAccessType(ac);
        if (at == null) {
            return checker;
        }
        switch (at) {
            case FIELD: {
                return checker.withFieldVisibility(JsonAutoDetect.Visibility.ANY).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            case NONE: {
                return checker.withFieldVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            case PROPERTY: {
                return checker.withFieldVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY);
            }
            case PUBLIC_MEMBER: {
                return checker.withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY).withIsGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY);
            }
        }
        return checker;
    }

    protected XmlAccessType findAccessType(Annotated ac) {
        XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, ac, true, true, true);
        return at == null ? null : at.value();
    }

    @Override
    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
        return null;
    }

    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
        if (baseType.isContainerType()) {
            return null;
        }
        return this._typeResolverFromXmlElements(am);
    }

    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
        if (!containerType.isContainerType()) {
            throw new IllegalArgumentException("Must call method with a container type (got " + containerType + ")");
        }
        return this._typeResolverFromXmlElements(am);
    }

    protected TypeResolverBuilder<?> _typeResolverFromXmlElements(AnnotatedMember am) {
        XmlElements elems = this.findAnnotation(XmlElements.class, am, false, false, false);
        XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, am, false, false, false);
        if (elems == null && elemRefs == null) {
            return null;
        }
        StdTypeResolverBuilder b = new StdTypeResolverBuilder();
        b = b.init(JsonTypeInfo.Id.NAME, null);
        b = b.inclusion(JsonTypeInfo.As.WRAPPER_OBJECT);
        return b;
    }

    @Override
    public List<NamedType> findSubtypes(Annotated a) {
        XmlElements elems = this.findAnnotation(XmlElements.class, a, false, false, false);
        if (elems != null) {
            ArrayList<NamedType> result = new ArrayList<NamedType>();
            for (XmlElement elem : elems.value()) {
                String name = elem.name();
                if (MARKER_FOR_DEFAULT.equals(name)) {
                    name = null;
                }
                result.add(new NamedType(elem.type(), name));
            }
            return result;
        }
        XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, a, false, false, false);
        if (elemRefs != null) {
            ArrayList<NamedType> result = new ArrayList<NamedType>();
            for (XmlElementRef elemRef : elemRefs.value()) {
                XmlRootElement rootElement;
                Class refType = elemRef.type();
                if (JAXBElement.class.isAssignableFrom(refType)) continue;
                String name = elemRef.name();
                if ((name == null || MARKER_FOR_DEFAULT.equals(name)) && (rootElement = refType.getAnnotation(XmlRootElement.class)) != null) {
                    name = rootElement.name();
                }
                if (name == null || MARKER_FOR_DEFAULT.equals(name)) {
                    name = Introspector.decapitalize(refType.getSimpleName());
                }
                result.add(new NamedType(refType, name));
            }
            return result;
        }
        return null;
    }

    @Override
    public String findTypeName(AnnotatedClass ac) {
        String name;
        XmlType type = this.findAnnotation(XmlType.class, ac, false, false, false);
        if (type != null && !MARKER_FOR_DEFAULT.equals(name = type.name())) {
            return name;
        }
        return null;
    }

    @Override
    public boolean isIgnorableMethod(AnnotatedMethod m) {
        return m.getAnnotation(XmlTransient.class) != null;
    }

    @Override
    public boolean isIgnorableConstructor(AnnotatedConstructor c) {
        return false;
    }

    @Override
    public boolean isIgnorableField(AnnotatedField f) {
        return f.getAnnotation(XmlTransient.class) != null;
    }

    @Override
    public JsonSerializer<?> findSerializer(Annotated am) {
        XmlAdapter<Object, Object> adapter = this.findAdapter(am, true);
        if (adapter != null) {
            return new XmlAdapterJsonSerializer(adapter);
        }
        Class<?> type = am.getRawType();
        if (type != null && this._dataHandlerSerializer != null && this.isDataHandler(type)) {
            return this._dataHandlerSerializer;
        }
        return null;
    }

    private boolean isDataHandler(Class<?> type) {
        return type != null && Object.class != type && ("javax.activation.DataHandler".equals(type.getName()) || this.isDataHandler(type.getSuperclass()));
    }

    @Override
    public Class<?> findSerializationType(Annotated a) {
        XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
        if (annotation == null || annotation.type() == XmlElement.DEFAULT.class) {
            return null;
        }
        Class<?> rawPropType = a.getRawType();
        if (this.isIndexedType(rawPropType)) {
            return null;
        }
        Class allegedType = annotation.type();
        if (a.getAnnotation(XmlJavaTypeAdapter.class) != null) {
            return null;
        }
        return allegedType;
    }

    @Override
    public JsonSerialize.Inclusion findSerializationInclusion(Annotated a, JsonSerialize.Inclusion defValue) {
        XmlElementWrapper w = a.getAnnotation(XmlElementWrapper.class);
        if (w != null) {
            return w.nillable() ? JsonSerialize.Inclusion.ALWAYS : JsonSerialize.Inclusion.NON_NULL;
        }
        XmlElement e = a.getAnnotation(XmlElement.class);
        if (e != null) {
            return e.nillable() ? JsonSerialize.Inclusion.ALWAYS : JsonSerialize.Inclusion.NON_NULL;
        }
        return defValue;
    }

    @Override
    public JsonSerialize.Typing findSerializationTyping(Annotated a) {
        return null;
    }

    @Override
    public Class<?>[] findSerializationViews(Annotated a) {
        return null;
    }

    @Override
    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        XmlType type = this.findAnnotation(XmlType.class, ac, true, true, true);
        if (type == null) {
            return null;
        }
        String[] order = type.propOrder();
        if (order == null || order.length == 0) {
            return null;
        }
        return order;
    }

    @Override
    public Boolean findSerializationSortAlphabetically(AnnotatedClass ac) {
        XmlAccessorOrder order = this.findAnnotation(XmlAccessorOrder.class, ac, true, true, true);
        return order == null ? null : Boolean.valueOf(order.value() == XmlAccessOrder.ALPHABETICAL);
    }

    @Override
    public String findGettablePropertyName(AnnotatedMethod am) {
        if (!this.isVisible(am)) {
            return null;
        }
        String name = JaxbAnnotationIntrospector.findJaxbPropertyName(am, am.getRawType(), BeanUtil.okNameForGetter(am));
        if (name == null) {
            // empty if block
        }
        return name;
    }

    @Override
    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        return false;
    }

    @Override
    public String findEnumValue(Enum<?> e) {
        Class<?> enumClass = e.getDeclaringClass();
        String enumValue = e.name();
        try {
            XmlEnumValue xmlEnumValue = enumClass.getDeclaredField(enumValue).getAnnotation(XmlEnumValue.class);
            return xmlEnumValue != null ? xmlEnumValue.value() : enumValue;
        }
        catch (NoSuchFieldException e1) {
            throw new IllegalStateException("Could not locate Enum entry '" + enumValue + "' (Enum class " + enumClass.getName() + ")", e1);
        }
    }

    @Override
    public String findSerializablePropertyName(AnnotatedField af) {
        if (!this.isVisible(af)) {
            return null;
        }
        String name = JaxbAnnotationIntrospector.findJaxbPropertyName(af, af.getRawType(), null);
        return name == null ? af.getName() : name;
    }

    @Override
    public JsonDeserializer<?> findDeserializer(Annotated am) {
        XmlAdapter<Object, Object> adapter = this.findAdapter(am, false);
        if (adapter != null) {
            return new XmlAdapterJsonDeserializer(adapter);
        }
        Class<?> type = am.getRawType();
        if (type != null && this._dataHandlerDeserializer != null && this.isDataHandler(type)) {
            return this._dataHandlerDeserializer;
        }
        return null;
    }

    public Class<KeyDeserializer> findKeyDeserializer(Annotated am) {
        return null;
    }

    public Class<JsonDeserializer<?>> findContentDeserializer(Annotated am) {
        return null;
    }

    @Override
    public Class<?> findDeserializationType(Annotated a, JavaType baseType, String propName) {
        if (!baseType.isContainerType()) {
            return this._doFindDeserializationType(a, baseType, propName);
        }
        return null;
    }

    @Override
    public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType, String propName) {
        return null;
    }

    @Override
    public Class<?> findDeserializationContentType(Annotated a, JavaType baseContentType, String propName) {
        return this._doFindDeserializationType(a, baseContentType, propName);
    }

    protected Class<?> _doFindDeserializationType(Annotated a, JavaType baseType, String propName) {
        AnnotatedMethod am;
        Class type;
        if (a.hasAnnotation(XmlJavaTypeAdapter.class)) {
            return null;
        }
        XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
        if (annotation != null && (type = annotation.type()) != XmlElement.DEFAULT.class) {
            return type;
        }
        if (a instanceof AnnotatedMethod && propName != null && (annotation = this.findFieldAnnotation(XmlElement.class, (am = (AnnotatedMethod)a).getDeclaringClass(), propName)) != null && annotation.type() != XmlElement.DEFAULT.class) {
            return annotation.type();
        }
        return null;
    }

    @Override
    public String findSettablePropertyName(AnnotatedMethod am) {
        if (!this.isVisible(am)) {
            return null;
        }
        Class<?> rawType = am.getParameterClass(0);
        String name = JaxbAnnotationIntrospector.findJaxbPropertyName(am, rawType, BeanUtil.okNameForSetter(am));
        return name;
    }

    @Override
    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return false;
    }

    @Override
    public boolean hasCreatorAnnotation(Annotated am) {
        return false;
    }

    @Override
    public String findDeserializablePropertyName(AnnotatedField af) {
        if (!this.isVisible(af)) {
            return null;
        }
        String name = JaxbAnnotationIntrospector.findJaxbPropertyName(af, af.getRawType(), null);
        return name == null ? af.getName() : name;
    }

    @Override
    public String findPropertyNameForParam(AnnotatedParameter param) {
        return null;
    }

    private boolean isVisible(AnnotatedField f) {
        for (Annotation annotation : f.getAnnotated().getDeclaredAnnotations()) {
            if (!this.isHandled(annotation)) continue;
            return true;
        }
        XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
        XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, f, true, true, true);
        if (at != null) {
            accessType = at.value();
        }
        if (accessType == XmlAccessType.FIELD) {
            return true;
        }
        if (accessType == XmlAccessType.PUBLIC_MEMBER) {
            return Modifier.isPublic(f.getAnnotated().getModifiers());
        }
        return false;
    }

    private boolean isVisible(AnnotatedMethod m) {
        for (Annotation annotation : m.getAnnotated().getDeclaredAnnotations()) {
            if (!this.isHandled(annotation)) continue;
            return true;
        }
        XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
        XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, m, true, true, true);
        if (at != null) {
            accessType = at.value();
        }
        if (accessType == XmlAccessType.PROPERTY || accessType == XmlAccessType.PUBLIC_MEMBER) {
            return Modifier.isPublic(m.getModifiers());
        }
        return false;
    }

    protected <A extends Annotation> A findAnnotation(Class<A> annotationClass, Annotated annotated, boolean includePackage, boolean includeClass, boolean includeSuperclasses) {
        A annotation = annotated.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Class memberClass = null;
        if (annotated instanceof AnnotatedParameter) {
            memberClass = ((AnnotatedParameter)annotated).getDeclaringClass();
        } else {
            AnnotatedElement annType = annotated.getAnnotated();
            if (annType instanceof Member) {
                memberClass = ((Member)((Object)annType)).getDeclaringClass();
                if (includeClass && (annotation = memberClass.getAnnotation(annotationClass)) != null) {
                    return annotation;
                }
            } else if (annType instanceof Class) {
                memberClass = (Class)annType;
            } else {
                throw new IllegalStateException("Unsupported annotated member: " + annotated.getClass().getName());
            }
        }
        if (memberClass != null) {
            Package pkg;
            if (includeSuperclasses) {
                for (Class<?> superclass = memberClass.getSuperclass(); superclass != null && superclass != Object.class; superclass = superclass.getSuperclass()) {
                    annotation = superclass.getAnnotation(annotationClass);
                    if (annotation == null) continue;
                    return annotation;
                }
            }
            if (includePackage && (pkg = memberClass.getPackage()) != null) {
                return memberClass.getPackage().getAnnotation(annotationClass);
            }
        }
        return null;
    }

    private <A extends Annotation> A findFieldAnnotation(Class<A> annotationType, Class<?> cls, String fieldName) {
        do {
            for (Field f : cls.getDeclaredFields()) {
                if (!fieldName.equals(f.getName())) continue;
                return f.getAnnotation(annotationType);
            }
        } while (!cls.isInterface() && cls != Object.class && (cls = cls.getSuperclass()) != null);
        return null;
    }

    private static String findJaxbPropertyName(Annotated ae, Class<?> aeType, String defaultName) {
        XmlValue valueInfo;
        XmlElementWrapper elementWrapper = ae.getAnnotation(XmlElementWrapper.class);
        if (elementWrapper != null) {
            String name = elementWrapper.name();
            if (!MARKER_FOR_DEFAULT.equals(name)) {
                return name;
            }
            return defaultName;
        }
        XmlAttribute attribute = ae.getAnnotation(XmlAttribute.class);
        if (attribute != null) {
            String name = attribute.name();
            if (!MARKER_FOR_DEFAULT.equals(name)) {
                return name;
            }
            return defaultName;
        }
        XmlElement element = ae.getAnnotation(XmlElement.class);
        if (element != null) {
            String name = element.name();
            if (!MARKER_FOR_DEFAULT.equals(name)) {
                return name;
            }
            return defaultName;
        }
        XmlElementRef elementRef = ae.getAnnotation(XmlElementRef.class);
        if (elementRef != null) {
            XmlRootElement rootElement;
            String name = elementRef.name();
            if (!MARKER_FOR_DEFAULT.equals(name)) {
                return name;
            }
            if (aeType != null && (rootElement = aeType.getAnnotation(XmlRootElement.class)) != null) {
                name = rootElement.name();
                if (!MARKER_FOR_DEFAULT.equals(name)) {
                    return name;
                }
                return Introspector.decapitalize(aeType.getSimpleName());
            }
        }
        if ((valueInfo = ae.getAnnotation(XmlValue.class)) != null) {
            return "value";
        }
        return null;
    }

    private XmlRootElement findRootElementAnnotation(AnnotatedClass ac) {
        return this.findAnnotation(XmlRootElement.class, ac, true, false, true);
    }

    private XmlAdapter<Object, Object> findAdapter(Annotated am, boolean forSerialization) {
        XmlAdapter<Object, Object> adapter;
        XmlAdapter<Object, Object> adapter2;
        XmlJavaTypeAdapter adapterInfo;
        Class<?> potentialAdaptee;
        Member member;
        if (am instanceof AnnotatedClass) {
            return this.findAdapterForClass((AnnotatedClass)am, forSerialization);
        }
        Class<?> memberType = am.getRawType();
        if (memberType == Void.TYPE && am instanceof AnnotatedMethod) {
            memberType = ((AnnotatedMethod)am).getParameterClass(0);
        }
        if ((member = (Member)((Object)am.getAnnotated())) != null && (potentialAdaptee = member.getDeclaringClass()) != null && (adapterInfo = potentialAdaptee.getAnnotation(XmlJavaTypeAdapter.class)) != null && (adapter2 = this.checkAdapter(adapterInfo, memberType)) != null) {
            return adapter2;
        }
        XmlJavaTypeAdapter adapterInfo2 = this.findAnnotation(XmlJavaTypeAdapter.class, am, true, false, false);
        if (adapterInfo2 != null && (adapter = this.checkAdapter(adapterInfo2, memberType)) != null) {
            return adapter;
        }
        XmlJavaTypeAdapters adapters = this.findAnnotation(XmlJavaTypeAdapters.class, am, true, false, false);
        if (adapters != null) {
            for (XmlJavaTypeAdapter info : adapters.value()) {
                XmlAdapter<Object, Object> adapter3 = this.checkAdapter(info, memberType);
                if (adapter3 == null) continue;
                return adapter3;
            }
        }
        return null;
    }

    private final XmlAdapter<Object, Object> checkAdapter(XmlJavaTypeAdapter adapterInfo, Class<?> typeNeeded) {
        Class adaptedType = adapterInfo.type();
        if (adaptedType == XmlJavaTypeAdapter.DEFAULT.class || adaptedType.isAssignableFrom(typeNeeded)) {
            Class cls = adapterInfo.value();
            return (XmlAdapter)ClassUtil.createInstance(cls, false);
        }
        return null;
    }

    private XmlAdapter<Object, Object> findAdapterForClass(AnnotatedClass ac, boolean forSerialization) {
        XmlJavaTypeAdapter adapterInfo = ((Class)ac.getAnnotated()).getAnnotation(XmlJavaTypeAdapter.class);
        if (adapterInfo != null) {
            Class cls = adapterInfo.value();
            return (XmlAdapter)ClassUtil.createInstance(cls, false);
        }
        return null;
    }

    private boolean isIndexedType(Class<?> raw) {
        return raw.isArray() || Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw);
    }
}

