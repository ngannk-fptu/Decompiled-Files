/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterFactory
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ConverterBuilder;
import org.springframework.data.convert.JMoleculesConverters;
import org.springframework.data.convert.JodaTimeConverters;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.ThreeTenBackPortConverters;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class CustomConversions {
    private static final Log logger = LogFactory.getLog(CustomConversions.class);
    private static final String READ_CONVERTER_NOT_SIMPLE = "Registering converter from %s to %s as reading converter although it doesn't convert from a store-supported type! You might want to check your annotation setup at the converter implementation.";
    private static final String WRITE_CONVERTER_NOT_SIMPLE = "Registering converter from %s to %s as writing converter although it doesn't convert to a store-supported type! You might want to check your annotation setup at the converter implementation.";
    private static final String NOT_A_CONVERTER = "Converter %s is neither a Spring Converter, GenericConverter or ConverterFactory!";
    private static final String CONVERTER_FILTER = "converter from %s to %s as %s converter.";
    private static final String ADD_CONVERTER = "Adding %sconverter from %s to %s as %s converter.";
    private static final String SKIP_CONVERTER = "Skipping converter from %s to %s as %s converter. %s is not a store supported simple type!";
    private static final List<Object> DEFAULT_CONVERTERS;
    private final SimpleTypeHolder simpleTypeHolder;
    private final List<Object> converters;
    private final Set<GenericConverter.ConvertiblePair> readingPairs = new LinkedHashSet<GenericConverter.ConvertiblePair>();
    private final Set<GenericConverter.ConvertiblePair> writingPairs = new LinkedHashSet<GenericConverter.ConvertiblePair>();
    private final Set<Class<?>> customSimpleTypes = new HashSet();
    private final ConversionTargetsCache customReadTargetTypes = new ConversionTargetsCache();
    private final ConversionTargetsCache customWriteTargetTypes = new ConversionTargetsCache();
    private final ConverterConfiguration converterConfiguration;
    private final Function<GenericConverter.ConvertiblePair, Class<?>> getReadTarget = convertiblePair -> this.getCustomTarget(convertiblePair.getSourceType(), convertiblePair.getTargetType(), this.readingPairs);
    private final Function<GenericConverter.ConvertiblePair, Class<?>> getWriteTarget = convertiblePair -> this.getCustomTarget(convertiblePair.getSourceType(), convertiblePair.getTargetType(), this.writingPairs);
    private final Function<GenericConverter.ConvertiblePair, Class<?>> getRawWriteTarget = convertiblePair -> this.getCustomTarget(convertiblePair.getSourceType(), null, this.writingPairs);

    public CustomConversions(ConverterConfiguration converterConfiguration) {
        this.converterConfiguration = converterConfiguration;
        List registeredConverters = this.collectPotentialConverterRegistrations(converterConfiguration.getStoreConversions(), converterConfiguration.getUserConverters()).stream().filter(this::isSupportedConverter).filter(this::shouldRegister).map(ConverterRegistrationIntent::getConverterRegistration).map(this::register).distinct().collect(Collectors.toList());
        Collections.reverse(registeredConverters);
        this.converters = Collections.unmodifiableList(registeredConverters);
        this.simpleTypeHolder = new SimpleTypeHolder(this.customSimpleTypes, converterConfiguration.getStoreConversions().getStoreTypeHolder());
    }

    public CustomConversions(StoreConversions storeConversions, Collection<?> converters) {
        this(new ConverterConfiguration(storeConversions, new ArrayList(converters)));
    }

    public SimpleTypeHolder getSimpleTypeHolder() {
        return this.simpleTypeHolder;
    }

    public boolean isSimpleType(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return this.simpleTypeHolder.isSimpleType(type);
    }

    public void registerConvertersIn(ConverterRegistry conversionService) {
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
        this.converters.forEach(it -> this.registerConverterIn(it, conversionService));
    }

    private List<ConverterRegistrationIntent> collectPotentialConverterRegistrations(StoreConversions storeConversions, Collection<?> converters) {
        ArrayList<ConverterRegistrationIntent> converterRegistrations = new ArrayList<ConverterRegistrationIntent>();
        converters.stream().map(storeConversions::getRegistrationsFor).flatMap(Streamable::stream).map(ConverterRegistrationIntent::userConverters).forEach(converterRegistrations::add);
        storeConversions.getStoreConverters().stream().map(storeConversions::getRegistrationsFor).flatMap(Streamable::stream).map(ConverterRegistrationIntent::storeConverters).forEach(converterRegistrations::add);
        DEFAULT_CONVERTERS.stream().map(storeConversions::getRegistrationsFor).flatMap(Streamable::stream).map(ConverterRegistrationIntent::defaultConverters).forEach(converterRegistrations::add);
        return converterRegistrations;
    }

    private void registerConverterIn(Object candidate, ConverterRegistry conversionService) {
        if (candidate instanceof Converter) {
            conversionService.addConverter((Converter)Converter.class.cast(candidate));
            return;
        }
        if (candidate instanceof ConverterFactory) {
            conversionService.addConverterFactory((ConverterFactory)ConverterFactory.class.cast(candidate));
            return;
        }
        if (candidate instanceof GenericConverter) {
            conversionService.addConverter((GenericConverter)GenericConverter.class.cast(candidate));
            return;
        }
        if (candidate instanceof ConverterBuilder.ConverterAware) {
            ((ConverterBuilder.ConverterAware)ConverterBuilder.ConverterAware.class.cast(candidate)).getConverters().forEach(it -> this.registerConverterIn(it, conversionService));
            return;
        }
        throw new IllegalArgumentException(String.format(NOT_A_CONVERTER, candidate));
    }

    private Object register(ConverterRegistration converterRegistration) {
        Assert.notNull((Object)converterRegistration, (String)"Converter registration must not be null!");
        GenericConverter.ConvertiblePair pair = converterRegistration.getConvertiblePair();
        if (converterRegistration.isReading()) {
            this.readingPairs.add(pair);
            if (logger.isWarnEnabled() && !converterRegistration.isSimpleSourceType()) {
                logger.warn((Object)String.format(READ_CONVERTER_NOT_SIMPLE, pair.getSourceType(), pair.getTargetType()));
            }
        }
        if (converterRegistration.isWriting()) {
            this.writingPairs.add(pair);
            this.customSimpleTypes.add(pair.getSourceType());
            if (logger.isWarnEnabled() && !converterRegistration.isSimpleTargetType()) {
                logger.warn((Object)String.format(WRITE_CONVERTER_NOT_SIMPLE, pair.getSourceType(), pair.getTargetType()));
            }
        }
        return converterRegistration.getConverter();
    }

    private boolean isSupportedConverter(ConverterRegistrationIntent registrationIntent) {
        boolean register;
        boolean bl = register = registrationIntent.isUserConverter() || registrationIntent.isStoreConverter() || registrationIntent.isReading() && registrationIntent.isSimpleSourceType() || registrationIntent.isWriting() && registrationIntent.isSimpleTargetType();
        if (logger.isDebugEnabled()) {
            if (register) {
                logger.debug((Object)String.format(ADD_CONVERTER, registrationIntent.isUserConverter() ? "user defined " : "", registrationIntent.getSourceType(), registrationIntent.getTargetType(), registrationIntent.isReading() ? "reading" : "writing"));
            } else {
                logger.debug((Object)String.format(SKIP_CONVERTER, registrationIntent.getSourceType(), registrationIntent.getTargetType(), registrationIntent.isReading() ? "reading" : "writing", registrationIntent.isReading() ? registrationIntent.getSourceType() : registrationIntent.getTargetType()));
            }
        }
        return register;
    }

    private boolean shouldRegister(ConverterRegistrationIntent intent) {
        return !intent.isDefaultConverter() || this.converterConfiguration.shouldRegister(intent.getConverterRegistration().getConvertiblePair());
    }

    public Optional<Class<?>> getCustomWriteTarget(Class<?> sourceType) {
        Assert.notNull(sourceType, (String)"Source type must not be null!");
        Class<?> target = this.customWriteTargetTypes.computeIfAbsent(sourceType, this.getRawWriteTarget);
        return Void.class.equals(target) || target == null ? Optional.empty() : Optional.of(target);
    }

    public Optional<Class<?>> getCustomWriteTarget(Class<?> sourceType, Class<?> requestedTargetType) {
        Assert.notNull(sourceType, (String)"Source type must not be null!");
        Assert.notNull(requestedTargetType, (String)"Target type must not be null!");
        Class<?> target = this.customWriteTargetTypes.computeIfAbsent(sourceType, requestedTargetType, this.getWriteTarget);
        return Void.class.equals(target) || target == null ? Optional.empty() : Optional.of(target);
    }

    public boolean hasCustomWriteTarget(Class<?> sourceType) {
        Assert.notNull(sourceType, (String)"Source type must not be null!");
        return this.getCustomWriteTarget(sourceType).isPresent();
    }

    public boolean hasCustomWriteTarget(Class<?> sourceType, Class<?> targetType) {
        Assert.notNull(sourceType, (String)"Source type must not be null!");
        Assert.notNull(targetType, (String)"Target type must not be null!");
        return this.getCustomWriteTarget(sourceType, targetType).isPresent();
    }

    public boolean hasCustomReadTarget(Class<?> sourceType, Class<?> targetType) {
        Assert.notNull(sourceType, (String)"Source type must not be null!");
        Assert.notNull(targetType, (String)"Target type must not be null!");
        return this.getCustomReadTarget(sourceType, targetType) != null;
    }

    @Nullable
    private Class<?> getCustomReadTarget(Class<?> sourceType, Class<?> targetType) {
        return this.customReadTargetTypes.computeIfAbsent(sourceType, targetType, this.getReadTarget);
    }

    @Nullable
    private Class<?> getCustomTarget(Class<?> sourceType, @Nullable Class<?> targetType, Collection<GenericConverter.ConvertiblePair> pairs) {
        if (targetType != null && pairs.contains(new GenericConverter.ConvertiblePair(sourceType, targetType))) {
            return targetType;
        }
        for (GenericConverter.ConvertiblePair pair : pairs) {
            Class candidate;
            if (!CustomConversions.hasAssignableSourceType(pair, sourceType) || !CustomConversions.requestedTargetTypeIsAssignable(targetType, candidate = pair.getTargetType())) continue;
            return candidate;
        }
        return null;
    }

    private static boolean hasAssignableSourceType(GenericConverter.ConvertiblePair pair, Class<?> sourceType) {
        return pair.getSourceType().isAssignableFrom(sourceType);
    }

    private static boolean requestedTargetTypeIsAssignable(@Nullable Class<?> requestedTargetType, Class<?> targetType) {
        return requestedTargetType == null || targetType.isAssignableFrom(requestedTargetType);
    }

    static {
        ArrayList<Object> defaults = new ArrayList<Object>();
        defaults.addAll(JodaTimeConverters.getConvertersToRegister());
        defaults.addAll(Jsr310Converters.getConvertersToRegister());
        defaults.addAll(ThreeTenBackPortConverters.getConvertersToRegister());
        defaults.addAll(JMoleculesConverters.getConvertersToRegister());
        DEFAULT_CONVERTERS = Collections.unmodifiableList(defaults);
    }

    protected static class ConverterConfiguration {
        private final StoreConversions storeConversions;
        private final List<?> userConverters;
        private final Predicate<GenericConverter.ConvertiblePair> converterRegistrationFilter;

        public ConverterConfiguration(StoreConversions storeConversions, List<?> userConverters) {
            this(storeConversions, userConverters, it -> true);
        }

        public ConverterConfiguration(StoreConversions storeConversions, List<?> userConverters, Predicate<GenericConverter.ConvertiblePair> converterRegistrationFilter) {
            this.storeConversions = storeConversions;
            this.userConverters = new ArrayList(userConverters);
            this.converterRegistrationFilter = converterRegistrationFilter;
        }

        StoreConversions getStoreConversions() {
            return this.storeConversions;
        }

        List<?> getUserConverters() {
            return this.userConverters;
        }

        boolean shouldRegister(GenericConverter.ConvertiblePair candidate) {
            return this.converterRegistrationFilter.test(candidate);
        }
    }

    public static class StoreConversions {
        public static final StoreConversions NONE = StoreConversions.of(SimpleTypeHolder.DEFAULT, Collections.emptyList());
        private final SimpleTypeHolder storeTypeHolder;
        private final Collection<?> storeConverters;

        private StoreConversions(SimpleTypeHolder storeTypeHolder, Collection<?> storeConverters) {
            this.storeTypeHolder = storeTypeHolder;
            this.storeConverters = storeConverters;
        }

        public static StoreConversions of(SimpleTypeHolder storeTypeHolder, Object ... converters) {
            Assert.notNull((Object)storeTypeHolder, (String)"SimpleTypeHolder must not be null!");
            Assert.notNull((Object)converters, (String)"Converters must not be null!");
            return new StoreConversions(storeTypeHolder, Arrays.asList(converters));
        }

        public static StoreConversions of(SimpleTypeHolder storeTypeHolder, Collection<?> converters) {
            Assert.notNull((Object)storeTypeHolder, (String)"SimpleTypeHolder must not be null!");
            Assert.notNull(converters, (String)"Converters must not be null!");
            return new StoreConversions(storeTypeHolder, converters);
        }

        public Streamable<ConverterRegistration> getRegistrationsFor(Object converter) {
            Assert.notNull((Object)converter, (String)"Converter must not be null!");
            Class<?> type = converter.getClass();
            boolean isWriting = type.isAnnotationPresent(WritingConverter.class);
            boolean isReading = type.isAnnotationPresent(ReadingConverter.class);
            if (converter instanceof ConverterBuilder.ConverterAware) {
                return Streamable.of(() -> ((ConverterBuilder.ConverterAware)ConverterBuilder.ConverterAware.class.cast(converter)).getConverters().stream().flatMap(it -> this.getRegistrationsFor(it).stream()));
            }
            if (converter instanceof GenericConverter) {
                Set convertibleTypes = ((GenericConverter)GenericConverter.class.cast(converter)).getConvertibleTypes();
                return convertibleTypes == null ? Streamable.empty() : Streamable.of(convertibleTypes).map(it -> this.register(converter, (GenericConverter.ConvertiblePair)it, isReading, isWriting));
            }
            if (converter instanceof ConverterFactory) {
                return this.getRegistrationFor(converter, ConverterFactory.class, isReading, isWriting);
            }
            if (converter instanceof Converter) {
                return this.getRegistrationFor(converter, Converter.class, isReading, isWriting);
            }
            throw new IllegalArgumentException(String.format("Unsupported converter type %s!", converter));
        }

        private Streamable<ConverterRegistration> getRegistrationFor(Object converter, Class<?> type, boolean isReading, boolean isWriting) {
            Class<?> converterType = converter.getClass();
            Class[] arguments = GenericTypeResolver.resolveTypeArguments(converterType, type);
            if (arguments == null) {
                throw new IllegalStateException(String.format("Couldn't resolve type arguments for %s!", converterType));
            }
            return Streamable.of(this.register(converter, arguments[0], arguments[1], isReading, isWriting));
        }

        private ConverterRegistration register(Object converter, Class<?> source, Class<?> target, boolean isReading, boolean isWriting) {
            return this.register(converter, new GenericConverter.ConvertiblePair(source, target), isReading, isWriting);
        }

        private ConverterRegistration register(Object converter, GenericConverter.ConvertiblePair pair, boolean isReading, boolean isWriting) {
            return new ConverterRegistration(converter, pair, this, isReading, isWriting);
        }

        private boolean isStoreSimpleType(Class<?> type) {
            return this.storeTypeHolder.isSimpleType(type);
        }

        SimpleTypeHolder getStoreTypeHolder() {
            return this.storeTypeHolder;
        }

        Collection<?> getStoreConverters() {
            return this.storeConverters;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StoreConversions)) {
                return false;
            }
            StoreConversions that = (StoreConversions)o;
            if (!ObjectUtils.nullSafeEquals((Object)this.storeTypeHolder, (Object)that.storeTypeHolder)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.storeConverters, that.storeConverters);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)this.storeTypeHolder);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.storeConverters);
            return result;
        }

        public String toString() {
            return "StoreConversions{storeTypeHolder=" + this.storeTypeHolder + ", storeConverters=" + this.storeConverters + '}';
        }
    }

    private static class ConverterRegistration {
        private final Object converter;
        private final GenericConverter.ConvertiblePair convertiblePair;
        private final StoreConversions storeConversions;
        private final boolean reading;
        private final boolean writing;

        private ConverterRegistration(Object converter, GenericConverter.ConvertiblePair convertiblePair, StoreConversions storeConversions, boolean reading, boolean writing) {
            this.converter = converter;
            this.convertiblePair = convertiblePair;
            this.storeConversions = storeConversions;
            this.reading = reading;
            this.writing = writing;
        }

        public boolean isWriting() {
            return this.writing || !this.reading && this.isSimpleTargetType();
        }

        public boolean isReading() {
            return this.reading || !this.writing && this.isSimpleSourceType();
        }

        public GenericConverter.ConvertiblePair getConvertiblePair() {
            return this.convertiblePair;
        }

        public boolean isSimpleSourceType() {
            return this.storeConversions.isStoreSimpleType(this.convertiblePair.getSourceType());
        }

        public boolean isSimpleTargetType() {
            return this.storeConversions.isStoreSimpleType(this.convertiblePair.getTargetType());
        }

        Object getConverter() {
            return this.converter;
        }
    }

    protected static class ConverterRegistrationIntent {
        private final ConverterRegistration delegate;
        private final ConverterOrigin origin;

        ConverterRegistrationIntent(ConverterRegistration delegate, ConverterOrigin origin) {
            this.delegate = delegate;
            this.origin = origin;
        }

        static ConverterRegistrationIntent userConverters(ConverterRegistration delegate) {
            return new ConverterRegistrationIntent(delegate, ConverterOrigin.USER_DEFINED);
        }

        static ConverterRegistrationIntent storeConverters(ConverterRegistration delegate) {
            return new ConverterRegistrationIntent(delegate, ConverterOrigin.STORE);
        }

        static ConverterRegistrationIntent defaultConverters(ConverterRegistration delegate) {
            return new ConverterRegistrationIntent(delegate, ConverterOrigin.DEFAULT);
        }

        Class<?> getSourceType() {
            return this.delegate.getConvertiblePair().getSourceType();
        }

        Class<?> getTargetType() {
            return this.delegate.getConvertiblePair().getTargetType();
        }

        public boolean isWriting() {
            return this.delegate.isWriting();
        }

        public boolean isReading() {
            return this.delegate.isReading();
        }

        public boolean isSimpleSourceType() {
            return this.delegate.isSimpleSourceType();
        }

        public boolean isSimpleTargetType() {
            return this.delegate.isSimpleTargetType();
        }

        public boolean isUserConverter() {
            return this.isConverterOfSource(ConverterOrigin.USER_DEFINED);
        }

        public boolean isStoreConverter() {
            return this.isConverterOfSource(ConverterOrigin.STORE);
        }

        public boolean isDefaultConverter() {
            return this.isConverterOfSource(ConverterOrigin.DEFAULT);
        }

        public ConverterRegistration getConverterRegistration() {
            return this.delegate;
        }

        private boolean isConverterOfSource(ConverterOrigin source) {
            return this.origin.equals((Object)source);
        }

        protected static enum ConverterOrigin {
            DEFAULT,
            USER_DEFINED,
            STORE;

        }
    }

    static class TargetTypes {
        private final Class<?> sourceType;
        private final Map<Class<?>, Class<?>> conversionTargets = new ConcurrentHashMap();

        TargetTypes(Class<?> sourceType) {
            this.sourceType = sourceType;
        }

        @Nullable
        public Class<?> computeIfAbsent(Class<?> targetType, Function<GenericConverter.ConvertiblePair, Class<?>> mappingFunction) {
            Class<?> optionalTarget = this.conversionTargets.get(targetType);
            if (optionalTarget == null) {
                optionalTarget = mappingFunction.apply(new GenericConverter.ConvertiblePair(this.sourceType, targetType));
                this.conversionTargets.put(targetType, optionalTarget == null ? Void.class : optionalTarget);
            }
            return Void.class.equals(optionalTarget) ? null : optionalTarget;
        }
    }

    static class ConversionTargetsCache {
        private final Map<Class<?>, TargetTypes> customReadTargetTypes = new ConcurrentHashMap();

        ConversionTargetsCache() {
        }

        @Nullable
        public Class<?> computeIfAbsent(Class<?> sourceType, Function<GenericConverter.ConvertiblePair, Class<?>> mappingFunction) {
            return this.computeIfAbsent(sourceType, AbsentTargetTypeMarker.class, mappingFunction);
        }

        @Nullable
        public Class<?> computeIfAbsent(Class<?> sourceType, Class<?> targetType, Function<GenericConverter.ConvertiblePair, Class<?>> mappingFunction) {
            TargetTypes targetTypes = this.customReadTargetTypes.get(sourceType);
            if (targetTypes == null) {
                targetTypes = this.customReadTargetTypes.computeIfAbsent(sourceType, TargetTypes::new);
            }
            return targetTypes.computeIfAbsent(targetType, mappingFunction);
        }

        static interface AbsentTargetTypeMarker {
        }
    }
}

