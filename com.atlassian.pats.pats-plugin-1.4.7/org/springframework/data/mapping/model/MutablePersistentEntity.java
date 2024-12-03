/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping.model;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.PersistentPropertyAccessorFactory;
import org.springframework.data.spel.EvaluationContextProvider;

public interface MutablePersistentEntity<T, P extends PersistentProperty<P>>
extends PersistentEntity<T, P> {
    public void addPersistentProperty(P var1);

    public void addAssociation(Association<P> var1);

    public void verify() throws MappingException;

    public void setPersistentPropertyAccessorFactory(PersistentPropertyAccessorFactory var1);

    public void setEvaluationContextProvider(EvaluationContextProvider var1);
}

