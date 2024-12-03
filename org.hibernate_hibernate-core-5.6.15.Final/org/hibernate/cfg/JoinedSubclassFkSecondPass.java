/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.FkSecondPass;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.SimpleValue;

public class JoinedSubclassFkSecondPass
extends FkSecondPass {
    private JoinedSubclass entity;
    private MetadataBuildingContext buildingContext;

    public JoinedSubclassFkSecondPass(JoinedSubclass entity, Ejb3JoinColumn[] inheritanceJoinedColumns, SimpleValue key, MetadataBuildingContext buildingContext) {
        super(key, inheritanceJoinedColumns);
        this.entity = entity;
        this.buildingContext = buildingContext;
    }

    @Override
    public String getReferencedEntityName() {
        return this.entity.getSuperclass().getEntityName();
    }

    @Override
    public boolean isInPrimaryKey() {
        return true;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        TableBinder.bindFk(this.entity.getSuperclass(), this.entity, this.columns, this.value, false, this.buildingContext);
    }
}

