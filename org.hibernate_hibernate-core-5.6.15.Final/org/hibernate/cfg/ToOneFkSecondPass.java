/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.FkSecondPass;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;

public class ToOneFkSecondPass
extends FkSecondPass {
    private MetadataBuildingContext buildingContext;
    private boolean unique;
    private String path;
    private String entityClassName;

    public ToOneFkSecondPass(ToOne value, Ejb3JoinColumn[] columns, boolean unique, String entityClassName, String path, MetadataBuildingContext buildingContext) {
        super(value, columns);
        this.buildingContext = buildingContext;
        this.unique = unique;
        this.entityClassName = entityClassName;
        this.path = entityClassName != null ? path.substring(entityClassName.length() + 1) : path;
    }

    @Override
    public String getReferencedEntityName() {
        return ((ToOne)this.value).getReferencedEntityName();
    }

    @Override
    public boolean isInPrimaryKey() {
        if (this.entityClassName == null) {
            return false;
        }
        PersistentClass persistentClass = this.buildingContext.getMetadataCollector().getEntityBinding(this.entityClassName);
        Property property = persistentClass.getIdentifierProperty();
        if (this.path == null) {
            return false;
        }
        if (property != null) {
            return this.path.startsWith(property.getName() + ".");
        }
        KeyValue valueIdentifier = persistentClass.getIdentifier();
        if (valueIdentifier instanceof Component) {
            String localPath = this.path;
            if (this.path.startsWith("id.")) {
                localPath = this.path.substring(3);
            }
            Iterator it = ((Component)valueIdentifier).getPropertyIterator();
            while (it.hasNext()) {
                Property idProperty = (Property)it.next();
                if (!localPath.equals(idProperty.getName()) && !localPath.startsWith(idProperty.getName() + ".")) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        if (this.value instanceof ManyToOne) {
            ManyToOne manyToOne = (ManyToOne)this.value;
            PersistentClass ref = (PersistentClass)persistentClasses.get(manyToOne.getReferencedEntityName());
            if (ref == null) {
                throw new AnnotationException("@OneToOne or @ManyToOne on " + StringHelper.qualify(this.entityClassName, this.path) + " references an unknown entity: " + manyToOne.getReferencedEntityName());
            }
            manyToOne.setPropertyName(this.path);
            BinderHelper.createSyntheticPropertyReference(this.columns, ref, null, manyToOne, false, this.buildingContext);
            TableBinder.bindFk(ref, null, this.columns, manyToOne, this.unique, this.buildingContext);
            if (manyToOne.getNotFoundAction() == null) {
                manyToOne.createPropertyRefConstraints(persistentClasses);
            }
        } else if (this.value instanceof OneToOne) {
            this.value.createForeignKey();
        } else {
            throw new AssertionFailure("FkSecondPass for a wrong value type: " + this.value.getClass().getName());
        }
    }
}

