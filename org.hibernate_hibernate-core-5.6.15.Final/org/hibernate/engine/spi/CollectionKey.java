/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.hibernate.EntityMode;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;

public final class CollectionKey
implements Serializable {
    private final String role;
    private final Serializable key;
    private final Type keyType;
    private final SessionFactoryImplementor factory;
    private final int hashCode;

    public CollectionKey(CollectionPersister persister, Serializable key) {
        this(persister.getRole(), key, persister.getKeyType(), persister.getFactory());
    }

    @Deprecated
    public CollectionKey(CollectionPersister persister, Serializable key, EntityMode em) {
        this(persister.getRole(), key, persister.getKeyType(), persister.getFactory());
    }

    private CollectionKey(String role, Serializable key, Type keyType, SessionFactoryImplementor factory) {
        this.role = role;
        this.key = key;
        this.keyType = keyType;
        this.factory = factory;
        this.hashCode = this.generateHashCode();
    }

    private int generateHashCode() {
        int result = 17;
        result = 37 * result + this.role.hashCode();
        result = 37 * result + this.keyType.getHashCode(this.key, this.factory);
        return result;
    }

    public String getRole() {
        return this.role;
    }

    public Serializable getKey() {
        return this.key;
    }

    public String toString() {
        return "CollectionKey" + MessageHelper.collectionInfoString(this.factory.getCollectionPersister(this.role), this.key, this.factory);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        CollectionKey that = (CollectionKey)other;
        return that.role.equals(this.role) && this.keyType.isEqual(that.key, this.key, this.factory);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public void serialize(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.role);
        oos.writeObject(this.key);
        oos.writeObject(this.keyType);
    }

    public static CollectionKey deserialize(ObjectInputStream ois, SessionImplementor session) throws IOException, ClassNotFoundException {
        return new CollectionKey((String)ois.readObject(), (Serializable)ois.readObject(), (Type)ois.readObject(), session == null ? null : session.getFactory());
    }
}

