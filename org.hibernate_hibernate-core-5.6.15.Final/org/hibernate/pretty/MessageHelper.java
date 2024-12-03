/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.pretty;

import java.io.Serializable;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

public final class MessageHelper {
    private MessageHelper() {
    }

    public static String infoString(String entityName, Serializable id) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (entityName == null) {
            s.append("<null entity name>");
        } else {
            s.append(entityName);
        }
        s.append('#');
        if (id == null) {
            s.append("<null>");
        } else {
            s.append(id);
        }
        s.append(']');
        return s.toString();
    }

    public static String infoString(EntityPersister persister, Object id, SessionFactoryImplementor factory) {
        Type idType;
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (persister == null) {
            s.append("<null EntityPersister>");
            idType = null;
        } else {
            s.append(persister.getEntityName());
            idType = persister.getIdentifierType();
        }
        s.append('#');
        if (id == null) {
            s.append("<null>");
        } else if (idType == null) {
            s.append(id);
        } else if (factory != null) {
            s.append(idType.toLoggableString(id, factory));
        } else {
            s.append("<not loggable>");
        }
        s.append(']');
        return s.toString();
    }

    public static String infoString(EntityPersister persister, Object id, Type identifierType, SessionFactoryImplementor factory) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (persister == null) {
            s.append("<null EntityPersister>");
        } else {
            s.append(persister.getEntityName());
        }
        s.append('#');
        if (id == null) {
            s.append("<null>");
        } else {
            s.append(identifierType.toLoggableString(id, factory));
        }
        s.append(']');
        return s.toString();
    }

    public static String infoString(EntityPersister persister, Serializable[] ids, SessionFactoryImplementor factory) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (persister == null) {
            s.append("<null EntityPersister>");
        } else {
            s.append(persister.getEntityName());
            s.append("#<");
            for (int i = 0; i < ids.length; ++i) {
                s.append(persister.getIdentifierType().toLoggableString(ids[i], factory));
                if (i >= ids.length - 1) continue;
                s.append(", ");
            }
            s.append('>');
        }
        s.append(']');
        return s.toString();
    }

    public static String infoString(EntityPersister persister) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (persister == null) {
            s.append("<null EntityPersister>");
        } else {
            s.append(persister.getEntityName());
        }
        s.append(']');
        return s.toString();
    }

    public static String infoString(String entityName, String propertyName, Object key) {
        StringBuilder s = new StringBuilder().append('[').append(entityName).append('.').append(propertyName).append('#');
        if (key == null) {
            s.append("<null>");
        } else {
            s.append(key);
        }
        s.append(']');
        return s.toString();
    }

    public static String collectionInfoString(CollectionPersister persister, PersistentCollection collection, Serializable collectionKey, SharedSessionContractImplementor session) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (persister == null) {
            s.append("<unreferenced>");
        } else {
            Serializable ownerKey;
            s.append(persister.getRole());
            s.append('#');
            Type ownerIdentifierType = persister.getOwnerEntityPersister().getIdentifierType();
            if (collectionKey.getClass().isAssignableFrom(ownerIdentifierType.getReturnedClass())) {
                ownerKey = collectionKey;
            } else {
                Object collectionOwner = collection == null ? null : collection.getOwner();
                EntityEntry entry = collectionOwner == null ? null : session.getPersistenceContextInternal().getEntry(collectionOwner);
                ownerKey = entry == null ? null : entry.getId();
            }
            s.append(ownerIdentifierType.toLoggableString(ownerKey, session.getFactory()));
        }
        s.append(']');
        return s.toString();
    }

    public static String collectionInfoString(CollectionPersister persister, Serializable[] ids, SessionFactoryImplementor factory) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (persister == null) {
            s.append("<unreferenced>");
        } else {
            s.append(persister.getRole());
            s.append("#<");
            for (int i = 0; i < ids.length; ++i) {
                MessageHelper.addIdToCollectionInfoString(persister, ids[i], factory, s);
                if (i >= ids.length - 1) continue;
                s.append(", ");
            }
            s.append('>');
        }
        s.append(']');
        return s.toString();
    }

    public static String collectionInfoString(CollectionPersister persister, Serializable id, SessionFactoryImplementor factory) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (persister == null) {
            s.append("<unreferenced>");
        } else {
            s.append(persister.getRole());
            s.append('#');
            if (id == null) {
                s.append("<null>");
            } else {
                MessageHelper.addIdToCollectionInfoString(persister, id, factory, s);
            }
        }
        s.append(']');
        return s.toString();
    }

    private static void addIdToCollectionInfoString(CollectionPersister persister, Serializable id, SessionFactoryImplementor factory, StringBuilder s) {
        Type ownerIdentifierType = persister.getOwnerEntityPersister().getIdentifierType();
        if (id.getClass().isAssignableFrom(ownerIdentifierType.getReturnedClass())) {
            s.append(ownerIdentifierType.toLoggableString(id, factory));
        } else {
            s.append(id.toString());
        }
    }

    public static String collectionInfoString(String role, Serializable id) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (role == null) {
            s.append("<unreferenced>");
        } else {
            s.append(role);
            s.append('#');
            if (id == null) {
                s.append("<null>");
            } else {
                s.append(id);
            }
        }
        s.append(']');
        return s.toString();
    }
}

