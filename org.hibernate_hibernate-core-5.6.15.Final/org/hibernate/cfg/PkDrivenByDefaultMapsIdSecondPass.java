/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.Map;
import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.FkSecondPass;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.SimpleValue;

public class PkDrivenByDefaultMapsIdSecondPass
extends FkSecondPass {
    private final String referencedEntityName;
    private final Ejb3JoinColumn[] columns;
    private final SimpleValue value;

    public PkDrivenByDefaultMapsIdSecondPass(String referencedEntityName, Ejb3JoinColumn[] columns, SimpleValue value) {
        super(value, columns);
        this.referencedEntityName = referencedEntityName;
        this.columns = columns;
        this.value = value;
    }

    @Override
    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    @Override
    public boolean isInPrimaryKey() {
        return true;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        PersistentClass referencedEntity = (PersistentClass)persistentClasses.get(this.referencedEntityName);
        if (referencedEntity == null) {
            throw new AnnotationException("Unknown entity name: " + this.referencedEntityName);
        }
        TableBinder.linkJoinColumnWithValueOverridingNameIfImplicit(referencedEntity, referencedEntity.getKey().getColumnIterator(), this.columns, this.value);
    }
}

