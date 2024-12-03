/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.AssertionFailure;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;

public final class EntityKey
implements Serializable {
    private final Serializable identifier;
    private final int hashCode;
    private final EntityPersister persister;

    public EntityKey(Serializable id, EntityPersister persister) {
        this.persister = persister;
        if (id == null) {
            throw new AssertionFailure("null identifier");
        }
        this.identifier = id;
        this.hashCode = this.generateHashCode();
    }

    private int generateHashCode() {
        int result = 17;
        String rootEntityName = this.persister.getRootEntityName();
        result = 37 * result + (rootEntityName != null ? rootEntityName.hashCode() : 0);
        result = 37 * result + this.persister.getIdentifierType().getHashCode(this.identifier, this.persister.getFactory());
        return result;
    }

    public boolean isBatchLoadable() {
        return this.persister.isBatchLoadable();
    }

    public Serializable getIdentifier() {
        return this.identifier;
    }

    public String getEntityName() {
        return this.persister.getEntityName();
    }

    public EntityPersister getPersister() {
        return this.persister;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || EntityKey.class != other.getClass()) {
            return false;
        }
        EntityKey otherKey = (EntityKey)other;
        return this.samePersistentType(otherKey) && this.sameIdentifier(otherKey);
    }

    private boolean sameIdentifier(EntityKey otherKey) {
        return this.persister.getIdentifierType().isEqual(otherKey.identifier, this.identifier, this.persister.getFactory());
    }

    private boolean samePersistentType(EntityKey otherKey) {
        if (otherKey.persister == this.persister) {
            return true;
        }
        return Objects.equals(otherKey.persister.getRootEntityName(), this.persister.getRootEntityName());
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return "EntityKey" + MessageHelper.infoString(this.persister, this.identifier, this.persister.getFactory());
    }

    public void serialize(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.identifier);
        oos.writeObject(this.persister.getEntityName());
    }

    public static EntityKey deserialize(ObjectInputStream ois, SessionFactoryImplementor sessionFactory) throws IOException, ClassNotFoundException {
        Serializable id = (Serializable)ois.readObject();
        String entityName = (String)ois.readObject();
        EntityPersister entityPersister = sessionFactory.getEntityPersister(entityName);
        return new EntityKey(id, entityPersister);
    }
}

