/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ManyToOne
 *  javax.persistence.OneToOne
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.hibernate.AssertionFailure;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.PropertyData;

public class ToOneBinder {
    public static String getReferenceEntityName(PropertyData propertyData, XClass targetEntity, MetadataBuildingContext buildingContext) {
        if (AnnotationBinder.isDefault(targetEntity, buildingContext)) {
            return propertyData.getClassOrElementName();
        }
        return targetEntity.getName();
    }

    public static String getReferenceEntityName(PropertyData propertyData, MetadataBuildingContext buildingContext) {
        XClass targetEntity = ToOneBinder.getTargetEntity(propertyData, buildingContext);
        if (AnnotationBinder.isDefault(targetEntity, buildingContext)) {
            return propertyData.getClassOrElementName();
        }
        return targetEntity.getName();
    }

    public static XClass getTargetEntity(PropertyData propertyData, MetadataBuildingContext buildingContext) {
        XProperty property = propertyData.getProperty();
        return buildingContext.getBootstrapContext().getReflectionManager().toXClass(ToOneBinder.getTargetEntityClass(property));
    }

    private static Class<?> getTargetEntityClass(XProperty property) {
        ManyToOne mTo = (ManyToOne)property.getAnnotation(ManyToOne.class);
        if (mTo != null) {
            return mTo.targetEntity();
        }
        OneToOne oTo = (OneToOne)property.getAnnotation(OneToOne.class);
        if (oTo != null) {
            return oTo.targetEntity();
        }
        throw new AssertionFailure("Unexpected discovery of a targetEntity: " + property.getName());
    }
}

