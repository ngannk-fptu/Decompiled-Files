/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@Internal
final class CollapsedReference<T>
extends Reference<T>
implements Collapsed {
    private final Map<Object, Object> idProperties;
    private final Class<? extends T> referentClass;
    private final boolean exists;

    CollapsedReference(Class<? extends T> referentClass, Map<Object, Object> idProperties) {
        this(referentClass, idProperties, !idProperties.isEmpty());
    }

    CollapsedReference(Class<? extends T> referentClass, boolean exists) {
        this(referentClass, Collections.EMPTY_MAP, exists);
    }

    CollapsedReference(T obj) {
        this(obj.getClass(), CollapsedReference.resolveIdProps(obj));
    }

    private CollapsedReference(Class<? extends T> referentClass, Map<Object, Object> idProperties, boolean exists) {
        super(true);
        this.referentClass = referentClass;
        this.idProperties = Collections.unmodifiableMap(idProperties);
        this.exists = exists;
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return navigationService.createNavigation().fromReference(this);
    }

    @Override
    public boolean exists() {
        return this.exists;
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public T get() {
        throw Collapsed.Exceptions.throwCollapsedException("get()");
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Map<Object, Object> getIdProperties() {
        return this.idProperties;
    }

    @Override
    public Class<? extends T> referentClass() {
        return this.referentClass;
    }

    public String toString() {
        return "CollapsedReference{idProperties=" + this.idProperties + ", referentClass=" + this.referentClass + '}';
    }
}

