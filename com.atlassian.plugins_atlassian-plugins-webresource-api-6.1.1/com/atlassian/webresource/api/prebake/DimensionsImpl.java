/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.api.prebake;

import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.CoordinateImpl;
import com.atlassian.webresource.api.prebake.Dimensions;
import com.atlassian.webresource.api.prebake.QueryParam;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

class DimensionsImpl
implements Dimensions {
    private final SortedMap<String, Set<Optional<String>>> queryParams = new TreeMap<String, Set<Optional<String>>>();

    private DimensionsImpl() {
    }

    private DimensionsImpl(SortedMap<String, Set<Optional<String>>> a, SortedMap<String, Set<Optional<String>>> b) {
        this.queryParams.putAll(a);
        for (Map.Entry<String, Set<Optional<String>>> entry : b.entrySet()) {
            String key = entry.getKey();
            if (!this.queryParams.containsKey(key)) {
                this.queryParams.put(key, new LinkedHashSet());
            }
            ((Set)this.queryParams.get(key)).addAll((Collection)entry.getValue());
        }
    }

    private DimensionsImpl(SortedMap<String, Set<Optional<String>>> orig, String key, Collection<Optional<String>> more) {
        this.queryParams.putAll(orig);
        if (!this.queryParams.containsKey(key)) {
            this.queryParams.put(key, new LinkedHashSet());
        }
        ((Set)this.queryParams.get(key)).addAll(more);
    }

    public static Dimensions empty() {
        return new DimensionsImpl();
    }

    @Override
    public Dimensions andExactly(String key, String ... values) {
        return this.andExactly(key, Arrays.asList(values));
    }

    @Override
    public Dimensions andExactly(String key, Collection<String> values) {
        List<Optional<String>> optionalValues = values.stream().map(Optional::ofNullable).collect(Collectors.toList());
        return new DimensionsImpl(this.queryParams, key, optionalValues);
    }

    @Override
    public Dimensions andAbsent(String key) {
        return new DimensionsImpl(this.queryParams, key, Collections.singleton(Optional.empty()));
    }

    @Override
    public Dimensions product(Dimensions rhs) {
        return new DimensionsImpl(this.queryParams, ((DimensionsImpl)rhs).queryParams);
    }

    @Override
    @Nonnull
    public Dimensions whitelistValues(@Nonnull Dimensions whitelist) {
        Preconditions.checkNotNull((Object)whitelist, (Object)"whitelist cannot be null");
        return this.filterValues(whitelist, true);
    }

    @Override
    @Nonnull
    public Dimensions blacklistValues(@Nonnull Dimensions blacklist) {
        Preconditions.checkNotNull((Object)blacklist, (Object)"blacklist cannot be null");
        return this.filterValues(blacklist, false);
    }

    private Dimensions filterValues(Dimensions filter, boolean allow) {
        DimensionsImpl filterImpl = (DimensionsImpl)filter;
        if (filterImpl.queryParams.isEmpty()) {
            return this;
        }
        DimensionsImpl dims = new DimensionsImpl();
        for (Map.Entry<String, Set<Optional<String>>> e : this.queryParams.entrySet()) {
            String key = e.getKey();
            Set<Optional<String>> values = e.getValue();
            if (filterImpl.contains(key)) {
                Set<Optional<String>> filterValues = filterImpl.getValues(key);
                values = values.stream().filter(v -> filterValues.contains(v) == allow).collect(Collectors.toSet());
            }
            dims = dims.addAll(key, values);
        }
        return dims;
    }

    private DimensionsImpl addAll(String key, Set<Optional<String>> values) {
        return new DimensionsImpl(this.queryParams, key, values);
    }

    private boolean contains(String key) {
        return this.queryParams.containsKey(key);
    }

    private Set<Optional<String>> getValues(String key) {
        if (!this.queryParams.containsKey(key)) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet((Set)this.queryParams.get(key));
    }

    @Override
    public Stream<Coordinate> cartesianProduct() {
        ArrayList cartesianInput = new ArrayList();
        for (Map.Entry<String, Set<Optional<String>>> entry : this.queryParams.entrySet()) {
            LinkedHashSet<QueryParam> axis = new LinkedHashSet<QueryParam>();
            for (Optional<String> value : entry.getValue()) {
                axis.add(new QueryParam(entry.getKey(), value));
            }
            cartesianInput.add(axis);
        }
        Set cartesianProduct = Sets.cartesianProduct(cartesianInput);
        return cartesianProduct.stream().map(CoordinateImpl::new);
    }

    @Override
    public long cartesianProductSize() {
        if (this.queryParams.isEmpty()) {
            return 0L;
        }
        return this.queryParams.values().stream().map(Collection::size).reduce(1, (a, b) -> a * b).intValue();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DimensionsImpl that = (DimensionsImpl)o;
        return this.queryParams.equals(that.queryParams);
    }

    public int hashCode() {
        return this.queryParams.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Dimensions: {\n");
        for (Map.Entry<String, Set<Optional<String>>> e : this.queryParams.entrySet()) {
            builder.append(e.getKey());
            builder.append(" = ");
            builder.append(e.getValue());
            builder.append(",\n");
        }
        builder.append("}");
        return builder.toString();
    }
}

