/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.SecondPass;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Value;
import org.jboss.logging.Logger;

public abstract class CollectionSecondPass
implements SecondPass {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)CollectionSecondPass.class.getName());
    MetadataBuildingContext buildingContext;
    Collection collection;
    private Map localInheritedMetas;

    public CollectionSecondPass(MetadataBuildingContext buildingContext, Collection collection, Map inheritedMetas) {
        this.collection = collection;
        this.buildingContext = buildingContext;
        this.localInheritedMetas = inheritedMetas;
    }

    public CollectionSecondPass(MetadataBuildingContext buildingContext, Collection collection) {
        this(buildingContext, collection, Collections.EMPTY_MAP);
    }

    @Override
    public void doSecondPass(Map persistentClasses) throws MappingException {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Second pass for collection: %s", this.collection.getRole());
        }
        this.secondPass(persistentClasses, this.localInheritedMetas);
        this.collection.createAllKeys();
        if (LOG.isDebugEnabled()) {
            String msg = "Mapped collection key: " + CollectionSecondPass.columns(this.collection.getKey());
            if (this.collection.isIndexed()) {
                msg = msg + ", index: " + CollectionSecondPass.columns(((IndexedCollection)this.collection).getIndex());
            }
            msg = this.collection.isOneToMany() ? msg + ", one-to-many: " + ((OneToMany)this.collection.getElement()).getReferencedEntityName() : msg + ", element: " + CollectionSecondPass.columns(this.collection.getElement());
            LOG.debug(msg);
        }
    }

    public abstract void secondPass(Map var1, Map var2);

    private static String columns(Value val) {
        StringBuilder columns = new StringBuilder();
        Iterator<Selectable> iter = val.getColumnIterator();
        while (iter.hasNext()) {
            columns.append(iter.next().getText());
            if (!iter.hasNext()) continue;
            columns.append(", ");
        }
        return columns.toString();
    }
}

