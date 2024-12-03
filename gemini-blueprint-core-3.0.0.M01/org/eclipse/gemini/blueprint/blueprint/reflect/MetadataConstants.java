/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import org.eclipse.gemini.blueprint.blueprint.reflect.internal.metadata.EnvironmentManagerFactoryBean;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

interface MetadataConstants {
    public static final Class<OsgiServiceFactoryBean> EXPORTER_CLASS = OsgiServiceFactoryBean.class;
    public static final Class<OsgiServiceProxyFactoryBean> SINGLE_SERVICE_IMPORTER_CLASS = OsgiServiceProxyFactoryBean.class;
    public static final Class<OsgiServiceCollectionProxyFactoryBean> MULTI_SERVICE_IMPORTER_CLASS = OsgiServiceCollectionProxyFactoryBean.class;
    public static final Class<EnvironmentManagerFactoryBean> ENV_FB_CLASS = EnvironmentManagerFactoryBean.class;
    public static final String SPRING_DM_PREFIX = "spring.osgi.";
    public static final String COMPONENT_METADATA_ATTRIBUTE = "spring.osgi." + ComponentMetadata.class.getName();
    public static final String COMPONENT_NAME = "spring.osgi.component.name";
    public static final String EXPORTER_RANKING_PROP = "ranking";
    public static final String EXPORTER_INTFS_PROP = "interfaces";
    public static final String EXPORTER_PROPS_PROP = "serviceProperties";
    public static final String EXPORTER_AUTO_EXPORT_PROP = "interfaceDetector";
    public static final String EXPORTER_TARGET_BEAN_PROP = "targetBean";
    public static final String EXPORTER_TARGET_BEAN_NAME_PROP = "targetBeanName";
    public static final String IMPORTER_INTFS_PROP = "interfaces";
    public static final String IMPORTER_FILTER_PROP = "filter";
    public static final String IMPORTER_CARDINALITY_PROP = "cardinality";
    public static final String IMPORTER_BEAN_NAME_PROP = "serviceBeanName";
    public static final String IMPORTER_TIMEOUT_PROP = "timeout";
    public static final String IMPORTER_COLLECTION_PROP = "collectionType";
}

