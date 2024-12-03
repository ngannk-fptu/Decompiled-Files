/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAuxiliaryDatabaseObjectType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDialectScopeType;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.SimpleAuxiliaryDatabaseObject;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;

public class AuxiliaryDatabaseObjectBinder {
    public static void processAuxiliaryDatabaseObject(HbmLocalMetadataBuildingContext context, JaxbHbmAuxiliaryDatabaseObjectType auxDbObjectMapping) {
        AuxiliaryDatabaseObject auxDbObject;
        if (auxDbObjectMapping.getDefinition() != null) {
            String auxDbObjectImplClass = auxDbObjectMapping.getDefinition().getClazz();
            try {
                auxDbObject = (AuxiliaryDatabaseObject)context.getBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class).classForName(auxDbObjectImplClass).newInstance();
            }
            catch (ClassLoadingException cle) {
                throw cle;
            }
            catch (Exception e) {
                throw new MappingException(String.format("Unable to instantiate custom AuxiliaryDatabaseObject class [%s]", auxDbObjectImplClass), context.getOrigin());
            }
        } else {
            auxDbObject = new SimpleAuxiliaryDatabaseObject(context.getMetadataCollector().getDatabase().getDefaultNamespace(), auxDbObjectMapping.getCreate(), auxDbObjectMapping.getDrop(), null);
        }
        if (!auxDbObjectMapping.getDialectScope().isEmpty() && AuxiliaryDatabaseObject.Expandable.class.isInstance(auxDbObject)) {
            AuxiliaryDatabaseObject.Expandable expandable = auxDbObject;
            for (JaxbHbmDialectScopeType dialectScopeBinding : auxDbObjectMapping.getDialectScope()) {
                expandable.addDialectScope(dialectScopeBinding.getName());
            }
        }
        context.getMetadataCollector().getDatabase().addAuxiliaryDatabaseObject(auxDbObject);
    }
}

