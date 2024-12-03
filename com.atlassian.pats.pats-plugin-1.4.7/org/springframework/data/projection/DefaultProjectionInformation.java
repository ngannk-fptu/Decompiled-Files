/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.core.log.LogMessage
 *  org.springframework.core.type.MethodMetadata
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.projection;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.log.LogMessage;
import org.springframework.core.type.MethodMetadata;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.data.type.MethodsMetadata;
import org.springframework.data.type.classreading.MethodsMetadataReader;
import org.springframework.data.type.classreading.MethodsMetadataReaderFactory;
import org.springframework.data.util.StreamUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

class DefaultProjectionInformation
implements ProjectionInformation {
    private final Class<?> projectionType;
    private final List<PropertyDescriptor> properties;

    DefaultProjectionInformation(Class<?> type) {
        Assert.notNull(type, (String)"Projection type must not be null!");
        this.projectionType = type;
        this.properties = new PropertyDescriptorSource(type).getDescriptors();
    }

    @Override
    public Class<?> getType() {
        return this.projectionType;
    }

    @Override
    public List<PropertyDescriptor> getInputProperties() {
        return this.properties.stream().filter(this::isInputProperty).distinct().collect(Collectors.toList());
    }

    @Override
    public boolean isClosed() {
        return this.properties.equals(this.getInputProperties());
    }

    protected boolean isInputProperty(PropertyDescriptor descriptor) {
        return true;
    }

    private static boolean hasDefaultGetter(PropertyDescriptor descriptor) {
        Method method = descriptor.getReadMethod();
        return method != null && method.isDefault();
    }

    private static class PropertyDescriptorSource {
        private static final Log logger = LogFactory.getLog(PropertyDescriptorSource.class);
        private final Class<?> type;
        private final Optional<MethodsMetadata> metadata;

        PropertyDescriptorSource(Class<?> type) {
            Assert.notNull(type, (String)"Type must not be null!");
            this.type = type;
            this.metadata = PropertyDescriptorSource.getMetadata(type);
        }

        List<PropertyDescriptor> getDescriptors() {
            return this.collectDescriptors().distinct().collect(StreamUtils.toUnmodifiableList());
        }

        private Stream<PropertyDescriptor> collectDescriptors() {
            Stream<PropertyDescriptor> allButDefaultGetters = Arrays.stream(BeanUtils.getPropertyDescriptors(this.type)).filter(it -> !DefaultProjectionInformation.hasDefaultGetter(it));
            Stream<PropertyDescriptor> ownDescriptors = this.metadata.map(it -> PropertyDescriptorSource.filterAndOrder(allButDefaultGetters, it)).orElse(allButDefaultGetters);
            Stream superTypeDescriptors = this.metadata.map(this::fromMetadata).orElseGet(this::fromType).flatMap(it -> new PropertyDescriptorSource((Class<?>)it).collectDescriptors());
            return Stream.concat(ownDescriptors, superTypeDescriptors);
        }

        private static Stream<PropertyDescriptor> filterAndOrder(Stream<PropertyDescriptor> source, MethodsMetadata metadata) {
            Map<String, Integer> orderedMethods = PropertyDescriptorSource.getMethodOrder(metadata);
            if (orderedMethods.isEmpty()) {
                return source;
            }
            return source.filter(descriptor -> descriptor.getReadMethod() != null).filter(descriptor -> orderedMethods.containsKey(descriptor.getReadMethod().getName())).sorted(Comparator.comparingInt(left -> (Integer)orderedMethods.get(left.getReadMethod().getName())));
        }

        private Stream<Class<?>> fromMetadata(MethodsMetadata metadata) {
            return Arrays.stream(metadata.getInterfaceNames()).map(it -> PropertyDescriptorSource.findType(it, this.type.getInterfaces()));
        }

        private Stream<Class<?>> fromType() {
            return Arrays.stream(this.type.getInterfaces());
        }

        private static Optional<MethodsMetadata> getMetadata(Class<?> type) {
            try {
                MethodsMetadataReaderFactory factory = new MethodsMetadataReaderFactory(type.getClassLoader());
                MethodsMetadataReader metadataReader = factory.getMetadataReader(ClassUtils.getQualifiedName(type));
                return Optional.of(metadataReader.getMethodsMetadata());
            }
            catch (IOException e) {
                logger.info((Object)LogMessage.format((String)"Couldn't read class metadata for %s. Input property calculation might fail!", type));
                return Optional.empty();
            }
        }

        private static Class<?> findType(String name, Class<?>[] types) {
            return Arrays.stream(types).filter(it -> name.equals(it.getName())).findFirst().orElseThrow(() -> new IllegalStateException(String.format("Did not find type %s in %s!", name, Arrays.toString(types))));
        }

        private static Map<String, Integer> getMethodOrder(MethodsMetadata metadata) {
            List methods = metadata.getMethods().stream().map(MethodMetadata::getMethodName).distinct().collect(Collectors.toList());
            return IntStream.range(0, methods.size()).boxed().collect(Collectors.toMap(methods::get, i -> i));
        }
    }
}

