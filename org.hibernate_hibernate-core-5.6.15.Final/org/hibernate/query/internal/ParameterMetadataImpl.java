/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 */
package org.hibernate.query.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.persistence.Parameter;
import org.hibernate.QueryException;
import org.hibernate.engine.query.spi.NamedParameterDescriptor;
import org.hibernate.engine.query.spi.OrdinalParameterDescriptor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.compare.ComparableComparator;
import org.hibernate.query.ParameterMetadata;
import org.hibernate.query.QueryParameter;
import org.hibernate.type.Type;

public class ParameterMetadataImpl
implements ParameterMetadata {
    private final Map<Integer, OrdinalParameterDescriptor> ordinalDescriptorMap;
    private final Map<String, NamedParameterDescriptor> namedDescriptorMap;
    private final Set<QueryParameter> ordinalDescriptorValueCache;
    private final Set<QueryParameter> namedDescriptorValueCache;

    public ParameterMetadataImpl(Map<Integer, OrdinalParameterDescriptor> ordinalDescriptorMap, Map<String, NamedParameterDescriptor> namedDescriptorMap) {
        this.ordinalDescriptorMap = ordinalDescriptorMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(ordinalDescriptorMap);
        this.ordinalDescriptorValueCache = this.ordinalDescriptorMap.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<OrdinalParameterDescriptor>(this.ordinalDescriptorMap.values()));
        this.namedDescriptorMap = namedDescriptorMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(namedDescriptorMap);
        Set<Object> set = this.namedDescriptorValueCache = this.namedDescriptorMap.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<NamedParameterDescriptor>(this.namedDescriptorMap.values()));
        if (ordinalDescriptorMap != null && !ordinalDescriptorMap.isEmpty()) {
            ArrayList<Integer> sortedPositions = new ArrayList<Integer>(ordinalDescriptorMap.keySet());
            sortedPositions.sort(ComparableComparator.INSTANCE);
            int lastPosition = -1;
            for (Integer sortedPosition : sortedPositions) {
                if (lastPosition == -1) {
                    lastPosition = sortedPosition;
                    continue;
                }
                if (sortedPosition != lastPosition + 1) {
                    throw new QueryException(String.format(Locale.ROOT, "Unexpected gap in ordinal parameter labels [%s -> %s] : [%s]", lastPosition, sortedPosition, StringHelper.join(",", sortedPositions.iterator())));
                }
                lastPosition = sortedPosition;
            }
        }
    }

    @Override
    public Collection<QueryParameter> getPositionalParameters() {
        return this.ordinalDescriptorValueCache;
    }

    @Override
    public Collection<QueryParameter> getNamedParameters() {
        return this.namedDescriptorValueCache;
    }

    @Override
    public int getParameterCount() {
        return this.ordinalDescriptorMap.size() + this.namedDescriptorMap.size();
    }

    @Override
    public boolean containsReference(QueryParameter parameter) {
        return this.ordinalDescriptorValueCache.contains(parameter) || this.namedDescriptorValueCache.contains(parameter);
    }

    @Override
    public boolean hasNamedParameters() {
        return !this.namedDescriptorMap.isEmpty();
    }

    @Override
    public boolean hasPositionalParameters() {
        return this.getOrdinalParameterCount() > 0;
    }

    @Override
    public int getPositionalParameterCount() {
        return this.getOrdinalParameterCount();
    }

    public int getOrdinalParameterCount() {
        return this.ordinalDescriptorMap.size();
    }

    @Override
    public Set<String> getNamedParameterNames() {
        return this.namedDescriptorMap.keySet();
    }

    public Set<Integer> getOrdinalParameterLabels() {
        return this.ordinalDescriptorMap.keySet();
    }

    public OrdinalParameterDescriptor getOrdinalParameterDescriptor(int position) {
        OrdinalParameterDescriptor descriptor = this.ordinalDescriptorMap.get(position);
        if (descriptor == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Could not locate ordinal parameter [%s], expecting one of [%s]", position, StringHelper.join(", ", this.ordinalDescriptorMap.keySet().iterator())));
        }
        return descriptor;
    }

    @Deprecated
    public Type getOrdinalParameterExpectedType(int position) {
        return this.getOrdinalParameterDescriptor(position).getExpectedType();
    }

    @Deprecated
    public int getOrdinalParameterSourceLocation(int position) {
        return this.getOrdinalParameterDescriptor(position).getPosition();
    }

    @Override
    public <T> QueryParameter<T> getQueryParameter(String name) {
        return this.getNamedParameterDescriptor(name);
    }

    @Override
    public <T> QueryParameter<T> getQueryParameter(Integer position) {
        return this.getOrdinalParameterDescriptor(position);
    }

    @Override
    public <T> QueryParameter<T> resolve(Parameter<T> param) {
        if (param instanceof QueryParameter) {
            return (QueryParameter)param;
        }
        throw new IllegalArgumentException("Could not resolve javax.persistence.Parameter to org.hibernate.query.QueryParameter");
    }

    public NamedParameterDescriptor getNamedParameterDescriptor(String name) {
        NamedParameterDescriptor descriptor = this.namedDescriptorMap.get(name);
        if (descriptor == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Could not locate named parameter [%s], expecting one of [%s]", name, String.join((CharSequence)", ", this.namedDescriptorMap.keySet())));
        }
        return descriptor;
    }

    @Override
    public void visitRegistrations(Consumer<QueryParameter> action) {
        block3: {
            block2: {
                if (!this.hasPositionalParameters()) break block2;
                for (OrdinalParameterDescriptor descriptor : this.ordinalDescriptorMap.values()) {
                    action.accept(descriptor);
                }
                break block3;
            }
            if (!this.hasNamedParameters()) break block3;
            for (NamedParameterDescriptor descriptor : this.namedDescriptorMap.values()) {
                action.accept(descriptor);
            }
        }
    }

    @Deprecated
    public Type getNamedParameterExpectedType(String name) {
        return this.getNamedParameterDescriptor(name).getExpectedType();
    }

    @Deprecated
    public int[] getNamedParameterSourceLocations(String name) {
        return this.getNamedParameterDescriptor(name).getSourceLocations();
    }

    @Override
    public Set<QueryParameter<?>> collectAllParameters() {
        if (this.hasNamedParameters() || this.hasPositionalParameters()) {
            HashSet allParameters = new HashSet();
            allParameters.addAll(this.namedDescriptorMap.values());
            allParameters.addAll(this.ordinalDescriptorMap.values());
            return allParameters;
        }
        return Collections.emptySet();
    }

    @Override
    public Set<Parameter<?>> collectAllParametersJpa() {
        if (this.hasNamedParameters() || this.hasPositionalParameters()) {
            HashSet allParameters = new HashSet();
            allParameters.addAll(this.namedDescriptorMap.values());
            allParameters.addAll(this.ordinalDescriptorMap.values());
            return allParameters;
        }
        return Collections.emptySet();
    }
}

