/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonInclude$Value
 *  com.fasterxml.jackson.annotation.PropertyAccessor
 *  com.fasterxml.jackson.core.JsonFactory
 *  com.fasterxml.jackson.core.JsonGenerator$Feature
 *  com.fasterxml.jackson.core.JsonParser$Feature
 *  com.fasterxml.jackson.databind.AnnotationIntrospector
 *  com.fasterxml.jackson.databind.DeserializationFeature
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonSerializer
 *  com.fasterxml.jackson.databind.MapperFeature
 *  com.fasterxml.jackson.databind.Module
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.PropertyNamingStrategy
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.cfg.HandlerInstantiator
 *  com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
 *  com.fasterxml.jackson.databind.module.SimpleModule
 *  com.fasterxml.jackson.databind.ser.FilterProvider
 *  com.fasterxml.jackson.dataformat.cbor.CBORFactory
 *  com.fasterxml.jackson.dataformat.smile.SmileFactory
 *  com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
 *  com.fasterxml.jackson.dataformat.xml.XmlFactory
 *  com.fasterxml.jackson.dataformat.xml.XmlMapper
 */
package org.springframework.http.converter.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.KotlinDetector;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.StaxUtils;

public class Jackson2ObjectMapperBuilder {
    private final Map<Class<?>, Class<?>> mixIns = new LinkedHashMap();
    private final Map<Class<?>, JsonSerializer<?>> serializers = new LinkedHashMap();
    private final Map<Class<?>, JsonDeserializer<?>> deserializers = new LinkedHashMap();
    private final Map<PropertyAccessor, JsonAutoDetect.Visibility> visibilities = new LinkedHashMap<PropertyAccessor, JsonAutoDetect.Visibility>();
    private final Map<Object, Boolean> features = new LinkedHashMap<Object, Boolean>();
    private boolean createXmlMapper = false;
    @Nullable
    private JsonFactory factory;
    @Nullable
    private DateFormat dateFormat;
    @Nullable
    private Locale locale;
    @Nullable
    private TimeZone timeZone;
    @Nullable
    private AnnotationIntrospector annotationIntrospector;
    @Nullable
    private PropertyNamingStrategy propertyNamingStrategy;
    @Nullable
    private TypeResolverBuilder<?> defaultTyping;
    @Nullable
    private JsonInclude.Value serializationInclusion;
    @Nullable
    private FilterProvider filters;
    @Nullable
    private List<Module> modules;
    @Nullable
    private Class<? extends Module>[] moduleClasses;
    private boolean findModulesViaServiceLoader = false;
    private boolean findWellKnownModules = true;
    private ClassLoader moduleClassLoader = this.getClass().getClassLoader();
    @Nullable
    private HandlerInstantiator handlerInstantiator;
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private Boolean defaultUseWrapper;
    @Nullable
    private Consumer<ObjectMapper> configurer;

    public Jackson2ObjectMapperBuilder createXmlMapper(boolean createXmlMapper) {
        this.createXmlMapper = createXmlMapper;
        return this;
    }

    public Jackson2ObjectMapperBuilder factory(JsonFactory factory) {
        this.factory = factory;
        return this;
    }

    public Jackson2ObjectMapperBuilder dateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public Jackson2ObjectMapperBuilder simpleDateFormat(String format) {
        this.dateFormat = new SimpleDateFormat(format);
        return this;
    }

    public Jackson2ObjectMapperBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public Jackson2ObjectMapperBuilder locale(String localeString) {
        this.locale = StringUtils.parseLocale(localeString);
        return this;
    }

    public Jackson2ObjectMapperBuilder timeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Jackson2ObjectMapperBuilder timeZone(String timeZoneString) {
        this.timeZone = StringUtils.parseTimeZoneString(timeZoneString);
        return this;
    }

    public Jackson2ObjectMapperBuilder annotationIntrospector(AnnotationIntrospector annotationIntrospector) {
        this.annotationIntrospector = annotationIntrospector;
        return this;
    }

    public Jackson2ObjectMapperBuilder annotationIntrospector(Function<AnnotationIntrospector, AnnotationIntrospector> pairingFunction) {
        this.annotationIntrospector = pairingFunction.apply(this.annotationIntrospector);
        return this;
    }

    public Jackson2ObjectMapperBuilder propertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
        this.propertyNamingStrategy = propertyNamingStrategy;
        return this;
    }

    public Jackson2ObjectMapperBuilder defaultTyping(TypeResolverBuilder<?> typeResolverBuilder) {
        this.defaultTyping = typeResolverBuilder;
        return this;
    }

    public Jackson2ObjectMapperBuilder serializationInclusion(JsonInclude.Include inclusion) {
        return this.serializationInclusion(JsonInclude.Value.construct((JsonInclude.Include)inclusion, (JsonInclude.Include)inclusion));
    }

    public Jackson2ObjectMapperBuilder serializationInclusion(JsonInclude.Value serializationInclusion) {
        this.serializationInclusion = serializationInclusion;
        return this;
    }

    public Jackson2ObjectMapperBuilder filters(FilterProvider filters) {
        this.filters = filters;
        return this;
    }

    public Jackson2ObjectMapperBuilder mixIn(Class<?> target, Class<?> mixinSource) {
        this.mixIns.put(target, mixinSource);
        return this;
    }

    public Jackson2ObjectMapperBuilder mixIns(Map<Class<?>, Class<?>> mixIns) {
        this.mixIns.putAll(mixIns);
        return this;
    }

    public Jackson2ObjectMapperBuilder serializers(JsonSerializer<?> ... serializers) {
        for (JsonSerializer<?> serializer : serializers) {
            Class handledType = serializer.handledType();
            if (handledType == null || handledType == Object.class) {
                throw new IllegalArgumentException("Unknown handled type in " + serializer.getClass().getName());
            }
            this.serializers.put(serializer.handledType(), serializer);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder serializerByType(Class<?> type, JsonSerializer<?> serializer) {
        this.serializers.put(type, serializer);
        return this;
    }

    public Jackson2ObjectMapperBuilder serializersByType(Map<Class<?>, JsonSerializer<?>> serializers) {
        this.serializers.putAll(serializers);
        return this;
    }

    public Jackson2ObjectMapperBuilder deserializers(JsonDeserializer<?> ... deserializers) {
        for (JsonDeserializer<?> deserializer : deserializers) {
            Class handledType = deserializer.handledType();
            if (handledType == null || handledType == Object.class) {
                throw new IllegalArgumentException("Unknown handled type in " + deserializer.getClass().getName());
            }
            this.deserializers.put(deserializer.handledType(), deserializer);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder deserializerByType(Class<?> type, JsonDeserializer<?> deserializer) {
        this.deserializers.put(type, deserializer);
        return this;
    }

    public Jackson2ObjectMapperBuilder deserializersByType(Map<Class<?>, JsonDeserializer<?>> deserializers) {
        this.deserializers.putAll(deserializers);
        return this;
    }

    public Jackson2ObjectMapperBuilder autoDetectFields(boolean autoDetectFields) {
        this.features.put(MapperFeature.AUTO_DETECT_FIELDS, autoDetectFields);
        return this;
    }

    public Jackson2ObjectMapperBuilder autoDetectGettersSetters(boolean autoDetectGettersSetters) {
        this.features.put(MapperFeature.AUTO_DETECT_GETTERS, autoDetectGettersSetters);
        this.features.put(MapperFeature.AUTO_DETECT_SETTERS, autoDetectGettersSetters);
        this.features.put(MapperFeature.AUTO_DETECT_IS_GETTERS, autoDetectGettersSetters);
        return this;
    }

    public Jackson2ObjectMapperBuilder defaultViewInclusion(boolean defaultViewInclusion) {
        this.features.put(MapperFeature.DEFAULT_VIEW_INCLUSION, defaultViewInclusion);
        return this;
    }

    public Jackson2ObjectMapperBuilder failOnUnknownProperties(boolean failOnUnknownProperties) {
        this.features.put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        return this;
    }

    public Jackson2ObjectMapperBuilder failOnEmptyBeans(boolean failOnEmptyBeans) {
        this.features.put(SerializationFeature.FAIL_ON_EMPTY_BEANS, failOnEmptyBeans);
        return this;
    }

    public Jackson2ObjectMapperBuilder indentOutput(boolean indentOutput) {
        this.features.put(SerializationFeature.INDENT_OUTPUT, indentOutput);
        return this;
    }

    public Jackson2ObjectMapperBuilder defaultUseWrapper(boolean defaultUseWrapper) {
        this.defaultUseWrapper = defaultUseWrapper;
        return this;
    }

    public Jackson2ObjectMapperBuilder visibility(PropertyAccessor accessor, JsonAutoDetect.Visibility visibility) {
        this.visibilities.put(accessor, visibility);
        return this;
    }

    public Jackson2ObjectMapperBuilder featuresToEnable(Object ... featuresToEnable) {
        for (Object feature : featuresToEnable) {
            this.features.put(feature, Boolean.TRUE);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder featuresToDisable(Object ... featuresToDisable) {
        for (Object feature : featuresToDisable) {
            this.features.put(feature, Boolean.FALSE);
        }
        return this;
    }

    public Jackson2ObjectMapperBuilder modules(Module ... modules) {
        return this.modules(Arrays.asList(modules));
    }

    public Jackson2ObjectMapperBuilder modules(List<Module> modules) {
        this.modules = new ArrayList<Module>(modules);
        this.findModulesViaServiceLoader = false;
        this.findWellKnownModules = false;
        return this;
    }

    public Jackson2ObjectMapperBuilder modules(Consumer<List<Module>> consumer) {
        this.modules = this.modules != null ? this.modules : new ArrayList<Module>();
        this.findModulesViaServiceLoader = false;
        this.findWellKnownModules = false;
        consumer.accept(this.modules);
        return this;
    }

    public Jackson2ObjectMapperBuilder modulesToInstall(Module ... modules) {
        this.modules = new ArrayList<Module>(Arrays.asList(modules));
        this.findWellKnownModules = true;
        return this;
    }

    public Jackson2ObjectMapperBuilder modulesToInstall(Consumer<List<Module>> consumer) {
        this.modules = this.modules != null ? this.modules : new ArrayList<Module>();
        this.findWellKnownModules = true;
        consumer.accept(this.modules);
        return this;
    }

    @SafeVarargs
    public final Jackson2ObjectMapperBuilder modulesToInstall(Class<? extends Module> ... modules) {
        this.moduleClasses = modules;
        this.findWellKnownModules = true;
        return this;
    }

    public Jackson2ObjectMapperBuilder findModulesViaServiceLoader(boolean findModules) {
        this.findModulesViaServiceLoader = findModules;
        return this;
    }

    public Jackson2ObjectMapperBuilder moduleClassLoader(ClassLoader moduleClassLoader) {
        this.moduleClassLoader = moduleClassLoader;
        return this;
    }

    public Jackson2ObjectMapperBuilder handlerInstantiator(HandlerInstantiator handlerInstantiator) {
        this.handlerInstantiator = handlerInstantiator;
        return this;
    }

    public Jackson2ObjectMapperBuilder applicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public Jackson2ObjectMapperBuilder postConfigurer(Consumer<ObjectMapper> configurer) {
        this.configurer = this.configurer != null ? this.configurer.andThen(configurer) : configurer;
        return this;
    }

    public <T extends ObjectMapper> T build() {
        ObjectMapper mapper = this.createXmlMapper ? (this.defaultUseWrapper != null ? new XmlObjectMapperInitializer().create(this.defaultUseWrapper, this.factory) : new XmlObjectMapperInitializer().create(this.factory)) : (this.factory != null ? new ObjectMapper(this.factory) : new ObjectMapper());
        this.configure(mapper);
        return (T)mapper;
    }

    public void configure(ObjectMapper objectMapper) {
        Assert.notNull((Object)objectMapper, "ObjectMapper must not be null");
        LinkedMultiValueMap<Object, Module> modulesToRegister = new LinkedMultiValueMap<Object, Module>();
        if (this.findModulesViaServiceLoader) {
            ObjectMapper.findModules((ClassLoader)this.moduleClassLoader).forEach(module -> this.registerModule((Module)module, (MultiValueMap<Object, Module>)modulesToRegister));
        } else if (this.findWellKnownModules) {
            this.registerWellKnownModulesIfAvailable(modulesToRegister);
        }
        if (this.modules != null) {
            this.modules.forEach(module -> this.registerModule((Module)module, (MultiValueMap<Object, Module>)modulesToRegister));
        }
        if (this.moduleClasses != null) {
            for (Class<? extends Module> moduleClass : this.moduleClasses) {
                this.registerModule(BeanUtils.instantiateClass(moduleClass), modulesToRegister);
            }
        }
        ArrayList modules = new ArrayList();
        for (List nestedModules : modulesToRegister.values()) {
            modules.addAll(nestedModules);
        }
        objectMapper.registerModules(modules);
        if (this.dateFormat != null) {
            objectMapper.setDateFormat(this.dateFormat);
        }
        if (this.locale != null) {
            objectMapper.setLocale(this.locale);
        }
        if (this.timeZone != null) {
            objectMapper.setTimeZone(this.timeZone);
        }
        if (this.annotationIntrospector != null) {
            objectMapper.setAnnotationIntrospector(this.annotationIntrospector);
        }
        if (this.propertyNamingStrategy != null) {
            objectMapper.setPropertyNamingStrategy(this.propertyNamingStrategy);
        }
        if (this.defaultTyping != null) {
            objectMapper.setDefaultTyping(this.defaultTyping);
        }
        if (this.serializationInclusion != null) {
            objectMapper.setDefaultPropertyInclusion(this.serializationInclusion);
        }
        if (this.filters != null) {
            objectMapper.setFilterProvider(this.filters);
        }
        this.mixIns.forEach((arg_0, arg_1) -> ((ObjectMapper)objectMapper).addMixIn(arg_0, arg_1));
        if (!this.serializers.isEmpty() || !this.deserializers.isEmpty()) {
            SimpleModule module2 = new SimpleModule();
            this.addSerializers(module2);
            this.addDeserializers(module2);
            objectMapper.registerModule((Module)module2);
        }
        this.visibilities.forEach((arg_0, arg_1) -> ((ObjectMapper)objectMapper).setVisibility(arg_0, arg_1));
        this.customizeDefaultFeatures(objectMapper);
        this.features.forEach((feature, enabled) -> this.configureFeature(objectMapper, feature, (boolean)enabled));
        if (this.handlerInstantiator != null) {
            objectMapper.setHandlerInstantiator(this.handlerInstantiator);
        } else if (this.applicationContext != null) {
            objectMapper.setHandlerInstantiator((HandlerInstantiator)new SpringHandlerInstantiator(this.applicationContext.getAutowireCapableBeanFactory()));
        }
        if (this.configurer != null) {
            this.configurer.accept(objectMapper);
        }
    }

    private void registerModule(Module module, MultiValueMap<Object, Module> modulesToRegister) {
        if (module.getTypeId() == null) {
            modulesToRegister.add(SimpleModule.class.getName(), module);
        } else {
            modulesToRegister.set(module.getTypeId(), module);
        }
    }

    private void customizeDefaultFeatures(ObjectMapper objectMapper) {
        if (!this.features.containsKey(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
            this.configureFeature(objectMapper, MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        }
        if (!this.features.containsKey(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            this.configureFeature(objectMapper, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    private <T> void addSerializers(SimpleModule module) {
        this.serializers.forEach((type, serializer) -> module.addSerializer(type, serializer));
    }

    private <T> void addDeserializers(SimpleModule module) {
        this.deserializers.forEach((type, deserializer) -> module.addDeserializer(type, deserializer));
    }

    private void configureFeature(ObjectMapper objectMapper, Object feature, boolean enabled) {
        if (feature instanceof JsonParser.Feature) {
            objectMapper.configure((JsonParser.Feature)feature, enabled);
        } else if (feature instanceof JsonGenerator.Feature) {
            objectMapper.configure((JsonGenerator.Feature)feature, enabled);
        } else if (feature instanceof SerializationFeature) {
            objectMapper.configure((SerializationFeature)feature, enabled);
        } else if (feature instanceof DeserializationFeature) {
            objectMapper.configure((DeserializationFeature)feature, enabled);
        } else if (feature instanceof MapperFeature) {
            objectMapper.configure((MapperFeature)feature, enabled);
        } else {
            throw new IllegalArgumentException("Unknown feature class: " + feature.getClass().getName());
        }
    }

    private void registerWellKnownModulesIfAvailable(MultiValueMap<Object, Module> modulesToRegister) {
        try {
            Class<?> jdk8ModuleClass = ClassUtils.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", this.moduleClassLoader);
            Module jdk8Module = (Module)BeanUtils.instantiateClass(jdk8ModuleClass);
            modulesToRegister.set(jdk8Module.getTypeId(), jdk8Module);
        }
        catch (ClassNotFoundException jdk8ModuleClass) {
            // empty catch block
        }
        try {
            Class<?> javaTimeModuleClass = ClassUtils.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", this.moduleClassLoader);
            Module javaTimeModule = (Module)BeanUtils.instantiateClass(javaTimeModuleClass);
            modulesToRegister.set(javaTimeModule.getTypeId(), javaTimeModule);
        }
        catch (ClassNotFoundException javaTimeModuleClass) {
            // empty catch block
        }
        if (ClassUtils.isPresent("org.joda.time.YearMonth", this.moduleClassLoader)) {
            try {
                Class<?> jodaModuleClass = ClassUtils.forName("com.fasterxml.jackson.datatype.joda.JodaModule", this.moduleClassLoader);
                Module jodaModule = (Module)BeanUtils.instantiateClass(jodaModuleClass);
                modulesToRegister.set(jodaModule.getTypeId(), jodaModule);
            }
            catch (ClassNotFoundException jodaModuleClass) {
                // empty catch block
            }
        }
        if (KotlinDetector.isKotlinPresent()) {
            try {
                Class<?> kotlinModuleClass = ClassUtils.forName("com.fasterxml.jackson.module.kotlin.KotlinModule", this.moduleClassLoader);
                Module kotlinModule = (Module)BeanUtils.instantiateClass(kotlinModuleClass);
                modulesToRegister.set(kotlinModule.getTypeId(), kotlinModule);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
    }

    public static Jackson2ObjectMapperBuilder json() {
        return new Jackson2ObjectMapperBuilder();
    }

    public static Jackson2ObjectMapperBuilder xml() {
        return new Jackson2ObjectMapperBuilder().createXmlMapper(true);
    }

    public static Jackson2ObjectMapperBuilder smile() {
        return new Jackson2ObjectMapperBuilder().factory(new SmileFactoryInitializer().create());
    }

    public static Jackson2ObjectMapperBuilder cbor() {
        return new Jackson2ObjectMapperBuilder().factory(new CborFactoryInitializer().create());
    }

    private static class CborFactoryInitializer {
        private CborFactoryInitializer() {
        }

        public JsonFactory create() {
            return new CBORFactory();
        }
    }

    private static class SmileFactoryInitializer {
        private SmileFactoryInitializer() {
        }

        public JsonFactory create() {
            return new SmileFactory();
        }
    }

    private static class XmlObjectMapperInitializer {
        private XmlObjectMapperInitializer() {
        }

        public ObjectMapper create(@Nullable JsonFactory factory) {
            if (factory != null) {
                return new XmlMapper((XmlFactory)factory);
            }
            return new XmlMapper(StaxUtils.createDefensiveInputFactory());
        }

        public ObjectMapper create(boolean defaultUseWrapper, @Nullable JsonFactory factory) {
            JacksonXmlModule module = new JacksonXmlModule();
            module.setDefaultUseWrapper(defaultUseWrapper);
            if (factory != null) {
                return new XmlMapper((XmlFactory)factory, module);
            }
            return new XmlMapper(new XmlFactory(StaxUtils.createDefensiveInputFactory()), module);
        }
    }
}

