/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DataReEncryptor
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.crypto.PropertyEncryptor;
import com.atlassian.crowd.dao.property.PropertyDAO;
import com.atlassian.crowd.embedded.api.DataReEncryptor;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.property.Property;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class EncryptingPropertyDAO
implements PropertyDAO,
DataReEncryptor {
    private PropertyDAO delegate;
    private PropertyEncryptor encryptor;

    public void setDelegate(PropertyDAO delegate) {
        this.delegate = delegate;
    }

    public void setEncryptor(PropertyEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public Property find(String key, String name) throws ObjectNotFoundException {
        return this.encryptor.decrypt(this.delegate.find(key, name));
    }

    @Override
    public List<Property> findAll(String key) {
        return this.delegate.findAll(key).stream().map(this.encryptor::decrypt).collect(Collectors.toList());
    }

    @Override
    public Property add(Property property) {
        return this.addOrUpdate(this.delegate::add, property);
    }

    @Override
    public Property update(Property property) {
        return this.addOrUpdate(this.delegate::update, property);
    }

    private Property addOrUpdate(UnaryOperator<Property> operation, Property property) {
        Property encrypted = this.encryptor.encrypt(property);
        Property addedOrUpdated = (Property)operation.apply(encrypted);
        return this.encryptor.decrypt(addedOrUpdated);
    }

    @Override
    public void remove(String key, String name) {
        this.delegate.remove(key, name);
    }

    @Override
    public List<Property> findAll() {
        return this.delegate.findAll().stream().map(this.encryptor::decrypt).collect(Collectors.toList());
    }

    public void reEncrypt() {
        this.findAll().forEach(this::update);
    }
}

