/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamConverters;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;
import com.thoughtworks.xstream.core.util.TypedNull;
import com.thoughtworks.xstream.mapper.AnnotationConfiguration;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.ElementIgnoringMapper;
import com.thoughtworks.xstream.mapper.FieldAliasingMapper;
import com.thoughtworks.xstream.mapper.ImplicitCollectionMapper;
import com.thoughtworks.xstream.mapper.LocalConversionMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AnnotationMapper
extends MapperWrapper
implements AnnotationConfiguration {
    private boolean locked;
    private transient Object[] arguments;
    private final ConverterRegistry converterRegistry;
    private transient ClassAliasingMapper classAliasingMapper;
    private transient DefaultImplementationsMapper defaultImplementationsMapper;
    private transient ImplicitCollectionMapper implicitCollectionMapper;
    private transient FieldAliasingMapper fieldAliasingMapper;
    private transient ElementIgnoringMapper elementIgnoringMapper;
    private transient AttributeMapper attributeMapper;
    private transient LocalConversionMapper localConversionMapper;
    private final Map<Class<?>, Map<List<Object>, Converter>> converterCache = new HashMap();
    private final Set<Class<?>> annotatedTypes = Collections.synchronizedSet(new HashSet());

    public AnnotationMapper(Mapper wrapped, ConverterRegistry converterRegistry, ConverterLookup converterLookup, ClassLoaderReference classLoaderReference, ReflectionProvider reflectionProvider) {
        super(wrapped);
        this.converterRegistry = converterRegistry;
        this.annotatedTypes.add(Object.class);
        this.setupMappers();
        this.locked = true;
        ClassLoader classLoader = classLoaderReference.getReference();
        this.arguments = new Object[]{this, classLoaderReference, reflectionProvider, converterLookup, new JVM(), classLoader != null ? classLoader : new TypedNull(ClassLoader.class)};
    }

    public AnnotationMapper(Mapper wrapped, ConverterRegistry converterRegistry, ConverterLookup converterLookup, ClassLoader classLoader, ReflectionProvider reflectionProvider, JVM jvm) {
        this(wrapped, converterRegistry, converterLookup, new ClassLoaderReference(classLoader), reflectionProvider);
    }

    @Override
    public String realMember(Class type, String serialized) {
        if (!this.locked) {
            this.processAnnotations(type);
        }
        return super.realMember(type, serialized);
    }

    @Override
    public String serializedClass(Class type) {
        if (!this.locked) {
            this.processAnnotations(type);
        }
        return super.serializedClass(type);
    }

    @Override
    public Class defaultImplementationOf(Class type) {
        if (!this.locked) {
            this.processAnnotations(type);
        }
        Class defaultImplementation = super.defaultImplementationOf(type);
        if (!this.locked) {
            this.processAnnotations(defaultImplementation);
        }
        return defaultImplementation;
    }

    @Override
    public Converter getLocalConverter(Class definedIn, String fieldName) {
        if (!this.locked) {
            this.processAnnotations(definedIn);
        }
        return super.getLocalConverter(definedIn, fieldName);
    }

    @Override
    public void autodetectAnnotations(boolean mode) {
        this.locked = !mode;
    }

    @Override
    public void processAnnotations(Class[] initialTypes) {
        if (initialTypes == null || initialTypes.length == 0) {
            return;
        }
        this.locked = true;
        UnprocessedTypesSet types = new UnprocessedTypesSet();
        for (Class initialType : initialTypes) {
            types.add(initialType);
        }
        this.processTypes(types);
    }

    private void processAnnotations(Class initialType) {
        if (initialType == null) {
            return;
        }
        UnprocessedTypesSet types = new UnprocessedTypesSet();
        types.add(initialType);
        this.processTypes(types);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void processTypes(Set<Class<?>> types) {
        while (!types.isEmpty()) {
            Iterator<Class<?>> iter = types.iterator();
            Class<?> type = iter.next();
            iter.remove();
            Class<?> clazz = type;
            synchronized (clazz) {
                if (this.annotatedTypes.contains(type)) {
                    continue;
                }
                try {
                    if (type.isPrimitive()) {
                        continue;
                    }
                    this.addParametrizedTypes(type, types);
                    this.processConverterAnnotations(type);
                    this.processAliasAnnotation(type, types);
                    this.processAliasTypeAnnotation(type);
                    if (type.isInterface()) {
                        continue;
                    }
                    this.processImplicitCollectionAnnotation(type);
                    Field[] fields = type.getDeclaredFields();
                    for (int i = 0; i < fields.length; ++i) {
                        Field field = fields[i];
                        if (field.isEnumConstant() || (field.getModifiers() & 0x88) > 0) continue;
                        this.addParametrizedTypes(field.getGenericType(), types);
                        if (field.isSynthetic()) continue;
                        this.processFieldAliasAnnotation(field);
                        this.processAsAttributeAnnotation(field);
                        this.processImplicitAnnotation(field);
                        this.processOmitFieldAnnotation(field);
                        this.processLocalConverterAnnotation(field);
                    }
                }
                finally {
                    this.annotatedTypes.add(type);
                }
            }
        }
        return;
    }

    private void addParametrizedTypes(Type type, final Set<Class<?>> types) {
        final HashSet<Type> processedTypes = new HashSet<Type>();
        LinkedHashSet<Type> localTypes = new LinkedHashSet<Type>(){

            @Override
            public boolean add(Type o) {
                if (o instanceof Class) {
                    return types.add((Class)o);
                }
                return o == null || processedTypes.contains(o) ? false : super.add(o);
            }
        };
        while (type != null) {
            processedTypes.add(type);
            if (type instanceof Class) {
                Class clazz = (Class)type;
                types.add(clazz);
                if (!clazz.isPrimitive()) {
                    TypeVariable<Class<T>>[] typeParameters = clazz.getTypeParameters();
                    for (Type type2 : typeParameters) {
                        localTypes.add(type2);
                    }
                    localTypes.add(clazz.getGenericSuperclass());
                    for (Type type3 : clazz.getGenericInterfaces()) {
                        localTypes.add(type3);
                    }
                }
            } else if (type instanceof TypeVariable) {
                Type[] bounds;
                TypeVariable typeVariable = (TypeVariable)type;
                for (Type type4 : bounds = typeVariable.getBounds()) {
                    localTypes.add(type4);
                }
            } else if (type instanceof ParameterizedType) {
                ParameterizedType parametrizedType = (ParameterizedType)type;
                localTypes.add(parametrizedType.getRawType());
                Type[] actualArguments = parametrizedType.getActualTypeArguments();
                for (Type type5 : actualArguments) {
                    localTypes.add(type5);
                }
            } else if (type instanceof GenericArrayType) {
                GenericArrayType arrayType = (GenericArrayType)type;
                localTypes.add(arrayType.getGenericComponentType());
            }
            if (!localTypes.isEmpty()) {
                Iterator iter = localTypes.iterator();
                type = (Type)iter.next();
                iter.remove();
                continue;
            }
            type = null;
        }
    }

    private void processConverterAnnotations(Class<?> type) {
        if (this.converterRegistry != null) {
            ArrayList<XStreamConverter> annotations;
            XStreamConverters convertersAnnotation = type.getAnnotation(XStreamConverters.class);
            XStreamConverter converterAnnotation = type.getAnnotation(XStreamConverter.class);
            ArrayList<XStreamConverter> arrayList = annotations = convertersAnnotation != null ? new ArrayList<XStreamConverter>(Arrays.asList(convertersAnnotation.value())) : new ArrayList();
            if (converterAnnotation != null) {
                annotations.add(converterAnnotation);
            }
            for (XStreamConverter annotation : annotations) {
                Converter converter = this.cacheConverter(annotation, converterAnnotation != null ? type : null);
                if (converter == null) continue;
                if (converterAnnotation != null || converter.canConvert(type)) {
                    this.converterRegistry.registerConverter(converter, annotation.priority());
                    continue;
                }
                throw new InitializationException("Converter " + annotation.value().getName() + " cannot handle annotated class " + type.getName());
            }
        }
    }

    private void processAliasAnnotation(Class<?> type, Set<Class<?>> types) {
        XStreamAlias aliasAnnotation = type.getAnnotation(XStreamAlias.class);
        if (aliasAnnotation != null) {
            if (this.classAliasingMapper == null) {
                throw new InitializationException("No " + ClassAliasingMapper.class.getName() + " available");
            }
            this.classAliasingMapper.addClassAlias(aliasAnnotation.value(), type);
            if (aliasAnnotation.impl() != Void.class) {
                this.defaultImplementationsMapper.addDefaultImplementation(aliasAnnotation.impl(), type);
                if (type.isInterface()) {
                    types.add(aliasAnnotation.impl());
                }
            }
        }
    }

    private void processAliasTypeAnnotation(Class<?> type) {
        XStreamAliasType aliasAnnotation = type.getAnnotation(XStreamAliasType.class);
        if (aliasAnnotation != null) {
            if (this.classAliasingMapper == null) {
                throw new InitializationException("No " + ClassAliasingMapper.class.getName() + " available");
            }
            this.classAliasingMapper.addTypeAlias(aliasAnnotation.value(), type);
        }
    }

    @Deprecated
    private void processImplicitCollectionAnnotation(Class<?> type) {
        XStreamImplicitCollection implicitColAnnotation = type.getAnnotation(XStreamImplicitCollection.class);
        if (implicitColAnnotation != null) {
            Field field;
            if (this.implicitCollectionMapper == null) {
                throw new InitializationException("No " + ImplicitCollectionMapper.class.getName() + " available");
            }
            String fieldName = implicitColAnnotation.value();
            String itemFieldName = implicitColAnnotation.item();
            try {
                field = type.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e) {
                throw new InitializationException(type.getName() + " does not have a field named '" + fieldName + "' as required by " + XStreamImplicitCollection.class.getName());
            }
            Class<?> itemType = null;
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type typeArgument = ((ParameterizedType)genericType).getActualTypeArguments()[0];
                itemType = this.getClass(typeArgument);
            }
            if (itemType == null) {
                this.implicitCollectionMapper.add(type, fieldName, null, Object.class);
            } else if (itemFieldName.equals("")) {
                this.implicitCollectionMapper.add(type, fieldName, null, itemType);
            } else {
                this.implicitCollectionMapper.add(type, fieldName, itemFieldName, itemType);
            }
        }
    }

    private void processFieldAliasAnnotation(Field field) {
        XStreamAlias aliasAnnotation = field.getAnnotation(XStreamAlias.class);
        if (aliasAnnotation != null) {
            if (this.fieldAliasingMapper == null) {
                throw new InitializationException("No " + FieldAliasingMapper.class.getName() + " available");
            }
            this.fieldAliasingMapper.addFieldAlias(aliasAnnotation.value(), field.getDeclaringClass(), field.getName());
        }
    }

    private void processAsAttributeAnnotation(Field field) {
        XStreamAsAttribute asAttributeAnnotation = field.getAnnotation(XStreamAsAttribute.class);
        if (asAttributeAnnotation != null) {
            if (this.attributeMapper == null) {
                throw new InitializationException("No " + AttributeMapper.class.getName() + " available");
            }
            this.attributeMapper.addAttributeFor(field);
        }
    }

    private void processImplicitAnnotation(Field field) {
        XStreamImplicit implicitAnnotation = field.getAnnotation(XStreamImplicit.class);
        if (implicitAnnotation != null) {
            Type genericType;
            if (this.implicitCollectionMapper == null) {
                throw new InitializationException("No " + ImplicitCollectionMapper.class.getName() + " available");
            }
            String fieldName = field.getName();
            String itemFieldName = implicitAnnotation.itemFieldName();
            String keyFieldName = implicitAnnotation.keyFieldName();
            boolean isMap = Map.class.isAssignableFrom(field.getType());
            Class<?> itemType = null;
            if (!field.getType().isArray() && (genericType = field.getGenericType()) instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType)genericType).getActualTypeArguments();
                Type typeArgument = actualTypeArguments[isMap ? 1 : 0];
                itemType = this.getClass(typeArgument);
            }
            if (isMap) {
                this.implicitCollectionMapper.add(field.getDeclaringClass(), fieldName, itemFieldName != null && !"".equals(itemFieldName) ? itemFieldName : null, itemType, keyFieldName != null && !"".equals(keyFieldName) ? keyFieldName : null);
            } else if (itemFieldName != null && !"".equals(itemFieldName)) {
                this.implicitCollectionMapper.add(field.getDeclaringClass(), fieldName, itemFieldName, itemType);
            } else {
                this.implicitCollectionMapper.add(field.getDeclaringClass(), fieldName, itemType);
            }
        }
    }

    private void processOmitFieldAnnotation(Field field) {
        XStreamOmitField omitFieldAnnotation = field.getAnnotation(XStreamOmitField.class);
        if (omitFieldAnnotation != null) {
            if (this.elementIgnoringMapper == null) {
                throw new InitializationException("No " + ElementIgnoringMapper.class.getName() + " available");
            }
            this.elementIgnoringMapper.omitField(field.getDeclaringClass(), field.getName());
        }
    }

    private void processLocalConverterAnnotation(Field field) {
        Converter converter;
        XStreamConverter annotation = field.getAnnotation(XStreamConverter.class);
        if (annotation != null && (converter = this.cacheConverter(annotation, field.getType())) != null) {
            if (this.localConversionMapper == null) {
                throw new InitializationException("No " + LocalConversionMapper.class.getName() + " available");
            }
            this.localConversionMapper.registerLocalConverter(field.getDeclaringClass(), field.getName(), converter);
        }
    }

    /*
     * WARNING - void declaration
     */
    private Converter cacheConverter(XStreamConverter annotation, Class targetType) {
        Converter result = null;
        ArrayList<Object> parameter = new ArrayList<Object>();
        if (targetType != null && annotation.useImplicitType()) {
            parameter.add(targetType);
        }
        ArrayList<Object[]> arrays = new ArrayList<Object[]>();
        arrays.add(annotation.booleans());
        arrays.add(annotation.bytes());
        arrays.add(annotation.chars());
        arrays.add(annotation.doubles());
        arrays.add(annotation.floats());
        arrays.add(annotation.ints());
        arrays.add(annotation.longs());
        arrays.add(annotation.shorts());
        arrays.add(annotation.strings());
        arrays.add(annotation.types());
        for (Object e : arrays) {
            if (e == null) continue;
            int length = Array.getLength(e);
            for (int i = 0; i < length; ++i) {
                parameter.add(Array.get(e, i));
            }
        }
        for (Class<?> type : annotation.nulls()) {
            TypedNull nullType = new TypedNull(type);
            parameter.add(nullType);
        }
        Class<? extends ConverterMatcher> converterType = annotation.value();
        Map<List<Object>, Converter> map = this.converterCache.get(converterType);
        if (map != null) {
            result = map.get(parameter);
        }
        if (result == null) {
            void var8_11;
            Converter converter;
            Object[] args;
            int size = parameter.size();
            if (size > 0) {
                args = new Object[this.arguments.length + size];
                System.arraycopy(this.arguments, 0, args, size, this.arguments.length);
                System.arraycopy(parameter.toArray(new Object[size]), 0, args, 0, size);
            } else {
                args = this.arguments;
            }
            try {
                if (SingleValueConverter.class.isAssignableFrom(converterType) && !Converter.class.isAssignableFrom(converterType)) {
                    SingleValueConverter svc = (SingleValueConverter)DependencyInjectionFactory.newInstance(converterType, args);
                    converter = new SingleValueConverterWrapper(svc);
                } else {
                    converter = (Converter)DependencyInjectionFactory.newInstance(converterType, args);
                }
            }
            catch (Exception e) {
                throw new InitializationException("Cannot instantiate converter " + converterType.getName() + (targetType != null ? " for type " + targetType.getName() : ""), e);
            }
            if (map == null) {
                HashMap hashMap = new HashMap();
                this.converterCache.put(converterType, hashMap);
            }
            var8_11.put(parameter, converter);
            result = converter;
        }
        return result;
    }

    private Class<?> getClass(Type typeArgument) {
        Class type = null;
        if (typeArgument instanceof ParameterizedType) {
            type = (Class)((ParameterizedType)typeArgument).getRawType();
        } else if (typeArgument instanceof Class) {
            type = (Class)typeArgument;
        }
        return type;
    }

    private void setupMappers() {
        this.classAliasingMapper = (ClassAliasingMapper)this.lookupMapperOfType(ClassAliasingMapper.class);
        this.defaultImplementationsMapper = (DefaultImplementationsMapper)this.lookupMapperOfType(DefaultImplementationsMapper.class);
        this.implicitCollectionMapper = (ImplicitCollectionMapper)this.lookupMapperOfType(ImplicitCollectionMapper.class);
        this.fieldAliasingMapper = (FieldAliasingMapper)this.lookupMapperOfType(FieldAliasingMapper.class);
        this.elementIgnoringMapper = (ElementIgnoringMapper)this.lookupMapperOfType(ElementIgnoringMapper.class);
        this.attributeMapper = (AttributeMapper)this.lookupMapperOfType(AttributeMapper.class);
        this.localConversionMapper = (LocalConversionMapper)this.lookupMapperOfType(LocalConversionMapper.class);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        int max = this.arguments.length - 2;
        out.writeInt(max);
        for (int i = 0; i < max; ++i) {
            out.writeObject(this.arguments[i]);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupMappers();
        int max = in.readInt();
        this.arguments = new Object[max + 2];
        for (int i = 0; i < max; ++i) {
            this.arguments[i] = in.readObject();
            if (!(this.arguments[i] instanceof ClassLoaderReference)) continue;
            this.arguments[max + 1] = ((ClassLoaderReference)this.arguments[i]).getReference();
        }
        this.arguments[max] = new JVM();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class UnprocessedTypesSet
    extends LinkedHashSet<Class<?>> {
        private UnprocessedTypesSet() {
        }

        @Override
        public boolean add(Class<?> type) {
            Class<?>[] incTypes;
            XStreamInclude inc;
            boolean ret;
            if (type == null) {
                return false;
            }
            while (type.isArray()) {
                type = type.getComponentType();
            }
            String name = type.getName();
            if (name.startsWith("java.") || name.startsWith("javax.")) {
                return false;
            }
            boolean bl = ret = AnnotationMapper.this.annotatedTypes.contains(type) ? false : super.add(type);
            if (ret && (inc = type.getAnnotation(XStreamInclude.class)) != null && (incTypes = inc.value()) != null) {
                for (Class<?> incType : incTypes) {
                    this.add(incType);
                }
            }
            return ret;
        }
    }
}

