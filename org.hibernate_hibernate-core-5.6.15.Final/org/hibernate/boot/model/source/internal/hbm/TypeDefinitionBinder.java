/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeDefinitionType;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.source.internal.hbm.ConfigParameterHelper;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.jboss.logging.Logger;

public class TypeDefinitionBinder {
    private static final Logger log = Logger.getLogger(TypeDefinitionBinder.class);

    public static void processTypeDefinition(HbmLocalMetadataBuildingContext context, JaxbHbmTypeDefinitionType typeDefinitionBinding) {
        ClassLoaderService cls = context.getBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class);
        TypeDefinition definition = new TypeDefinition(typeDefinitionBinding.getName(), cls.classForName(typeDefinitionBinding.getClazz()), null, ConfigParameterHelper.extractConfigParameters(typeDefinitionBinding));
        log.debugf("Processed type-definition : %s -> %s", (Object)definition.getName(), (Object)definition.getTypeImplementorClass().getName());
        context.getMetadataCollector().addTypeDefinition(definition);
    }
}

