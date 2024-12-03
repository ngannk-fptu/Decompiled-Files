/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.internal;

import java.util.Iterator;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.cfg.SecondPass;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Selectable;

public class NullableDiscriminatorColumnSecondPass
implements SecondPass {
    private final String rootEntityName;

    public NullableDiscriminatorColumnSecondPass(String rootEntityName) {
        this.rootEntityName = rootEntityName;
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        PersistentClass rootPersistenceClass = (PersistentClass)persistentClasses.get(this.rootEntityName);
        if (this.hasNullDiscriminatorValue(rootPersistenceClass)) {
            Iterator<Selectable> iterator = rootPersistenceClass.getDiscriminator().getColumnIterator();
            while (iterator.hasNext()) {
                Selectable selectable = iterator.next();
                if (!(selectable instanceof Column)) continue;
                ((Column)selectable).setNullable(true);
            }
        }
    }

    private boolean hasNullDiscriminatorValue(PersistentClass rootPersistenceClass) {
        if (rootPersistenceClass.isDiscriminatorValueNull()) {
            return true;
        }
        Iterator subclassIterator = rootPersistenceClass.getSubclassIterator();
        while (subclassIterator.hasNext()) {
            if (!((PersistentClass)subclassIterator.next()).isDiscriminatorValueNull()) continue;
            return true;
        }
        return false;
    }
}

