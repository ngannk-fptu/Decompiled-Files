/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanMetadataElement
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.List;
import org.eclipse.gemini.blueprint.blueprint.reflect.BeanMetadataElementFactory;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataConstants;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleComponentMetadata;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ReferenceListMetadata;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.ServiceReferenceMetadata;
import org.osgi.service.blueprint.reflect.Target;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;

class BeanDefinitionFactory
implements MetadataConstants {
    BeanDefinitionFactory() {
    }

    BeanDefinition buildBeanDefinitionFor(ComponentMetadata metadata) {
        if (metadata instanceof SimpleComponentMetadata) {
            return ((SimpleComponentMetadata)metadata).getBeanDefinition();
        }
        AbstractBeanDefinition definition = this.buildBeanDefinition(metadata);
        definition.setAttribute(MetadataConstants.COMPONENT_METADATA_ATTRIBUTE, (Object)metadata);
        definition.setAttribute("spring.osgi.component.name", (Object)metadata.getId());
        throw new UnsupportedOperationException("move depends on for BeanMetadata");
    }

    private AbstractBeanDefinition buildBeanDefinition(ComponentMetadata metadata) {
        if (metadata instanceof BeanMetadata) {
            return this.buildLocalComponent((BeanMetadata)metadata);
        }
        if (metadata instanceof ServiceMetadata) {
            return this.buildExporter((ServiceMetadata)metadata);
        }
        if (metadata instanceof ServiceReferenceMetadata) {
            if (metadata instanceof ReferenceListMetadata) {
                return this.buildReferenceCollection((ReferenceListMetadata)metadata);
            }
            if (metadata instanceof ReferenceMetadata) {
                return this.buildReferenceProxy((ReferenceMetadata)metadata);
            }
        }
        throw new IllegalArgumentException("Unknown metadata type" + metadata.getClass());
    }

    private AbstractBeanDefinition buildLocalComponent(BeanMetadata metadata) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((String)metadata.getClassName()).setInitMethodName(metadata.getInitMethod()).setDestroyMethodName(metadata.getDestroyMethod()).setLazyInit(this.getLazy(metadata)).setScope(metadata.getScope());
        String factoryMethod = metadata.getFactoryMethod();
        if (StringUtils.hasText((String)factoryMethod)) {
            builder.setFactoryMethod(factoryMethod);
            Target factory = metadata.getFactoryComponent();
            if (factory != null) {
                builder.getRawBeanDefinition().setFactoryBeanName(((ComponentMetadata)((Object)factory)).getId());
            }
        }
        List beanArguments = metadata.getArguments();
        ConstructorArgumentValues cargs = builder.getRawBeanDefinition().getConstructorArgumentValues();
        for (BeanArgument arg : beanArguments) {
            int index = arg.getIndex();
            BeanMetadataElement val = BeanMetadataElementFactory.buildBeanMetadata(arg.getValue());
            if (index > -1) {
                cargs.addGenericArgumentValue((Object)val, arg.getValueType());
                continue;
            }
            cargs.addIndexedArgumentValue(index, (Object)val, arg.getValueType());
        }
        List props = metadata.getProperties();
        MutablePropertyValues pvs = new MutablePropertyValues();
        for (BeanProperty injection : props) {
            pvs.addPropertyValue(injection.getName(), (Object)BeanMetadataElementFactory.buildBeanMetadata(injection.getValue()));
        }
        return builder.getBeanDefinition();
    }

    private boolean getLazy(ComponentMetadata metadata) {
        return metadata.getActivation() == 2;
    }

    private AbstractBeanDefinition buildExporter(ServiceMetadata metadata) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((Class)EXPORTER_CLASS);
        builder.addPropertyValue("ranking", (Object)metadata.getRanking());
        builder.addPropertyValue("interfaces", (Object)metadata.getInterfaces());
        builder.addPropertyValue("serviceProperties", (Object)metadata.getServiceProperties());
        builder.addPropertyValue("interfaceDetector", (Object)DefaultInterfaceDetector.values()[metadata.getAutoExport() - 1]);
        BeanMetadataElement beanMetadata = BeanMetadataElementFactory.buildBeanMetadata(metadata.getServiceComponent());
        if (beanMetadata instanceof RuntimeBeanReference) {
            builder.addPropertyValue("targetBeanName", (Object)beanMetadata);
        } else {
            builder.addPropertyValue("targetBean", (Object)beanMetadata);
        }
        return builder.getBeanDefinition();
    }

    private AbstractBeanDefinition buildReferenceCollection(ReferenceListMetadata metadata) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((Class)MULTI_SERVICE_IMPORTER_CLASS);
        this.addServiceReferenceProperties(metadata, builder);
        throw new UnsupportedOperationException("not implemented yet");
    }

    private AbstractBeanDefinition buildReferenceProxy(ReferenceMetadata metadata) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((Class)SINGLE_SERVICE_IMPORTER_CLASS);
        this.addServiceReferenceProperties(metadata, builder);
        throw new UnsupportedOperationException("not implemented yet");
    }

    private void addServiceReferenceProperties(ServiceReferenceMetadata referenceMetadata, BeanDefinitionBuilder builder) {
        builder.addPropertyValue("filter", (Object)referenceMetadata.getFilter());
        builder.addPropertyValue("interfaces", (Object)referenceMetadata.getInterface());
    }
}

