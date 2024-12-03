/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.util.MethodInvocationRecorder;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class Sort
implements Streamable<Order>,
Serializable {
    private static final long serialVersionUID = 5737186511678863905L;
    private static final Sort UNSORTED = Sort.by(new Order[0]);
    public static final Direction DEFAULT_DIRECTION = Direction.ASC;
    private final List<Order> orders;

    protected Sort(List<Order> orders) {
        this.orders = orders;
    }

    private Sort(Direction direction, List<String> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }
        this.orders = properties.stream().map(it -> new Order(direction, (String)it)).collect(Collectors.toList());
    }

    public static Sort by(String ... properties) {
        Assert.notNull((Object)properties, (String)"Properties must not be null!");
        return properties.length == 0 ? Sort.unsorted() : new Sort(DEFAULT_DIRECTION, Arrays.asList(properties));
    }

    public static Sort by(List<Order> orders) {
        Assert.notNull(orders, (String)"Orders must not be null!");
        return orders.isEmpty() ? Sort.unsorted() : new Sort(orders);
    }

    public static Sort by(Order ... orders) {
        Assert.notNull((Object)orders, (String)"Orders must not be null!");
        return new Sort(Arrays.asList(orders));
    }

    public static Sort by(Direction direction, String ... properties) {
        Assert.notNull((Object)((Object)direction), (String)"Direction must not be null!");
        Assert.notNull((Object)properties, (String)"Properties must not be null!");
        Assert.isTrue((properties.length > 0 ? 1 : 0) != 0, (String)"At least one property must be given!");
        return Sort.by(Arrays.stream(properties).map(it -> new Order(direction, (String)it)).collect(Collectors.toList()));
    }

    public static <T> TypedSort<T> sort(Class<T> type) {
        return new TypedSort(type);
    }

    public static Sort unsorted() {
        return UNSORTED;
    }

    public Sort descending() {
        return this.withDirection(Direction.DESC);
    }

    public Sort ascending() {
        return this.withDirection(Direction.ASC);
    }

    public boolean isSorted() {
        return !this.isEmpty();
    }

    @Override
    public boolean isEmpty() {
        return this.orders.isEmpty();
    }

    public boolean isUnsorted() {
        return !this.isSorted();
    }

    public Sort and(Sort sort) {
        Assert.notNull((Object)sort, (String)"Sort must not be null!");
        ArrayList<Order> these = new ArrayList<Order>(this.toList());
        for (Order order : sort) {
            these.add(order);
        }
        return Sort.by(these);
    }

    @Nullable
    public Order getOrderFor(String property) {
        for (Order order : this) {
            if (!order.getProperty().equals(property)) continue;
            return order;
        }
        return null;
    }

    @Override
    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Sort)) {
            return false;
        }
        Sort that = (Sort)obj;
        return this.toList().equals(that.toList());
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + this.orders.hashCode();
        return result;
    }

    public String toString() {
        return this.isEmpty() ? "UNSORTED" : StringUtils.collectionToCommaDelimitedString(this.orders);
    }

    private Sort withDirection(Direction direction) {
        return Sort.by(this.stream().map(it -> new Order(direction, it.getProperty())).collect(Collectors.toList()));
    }

    public static class TypedSort<T>
    extends Sort {
        private static final long serialVersionUID = -3550403511206745880L;
        private final MethodInvocationRecorder.Recorded<T> recorded;

        private TypedSort(Class<T> type) {
            this(MethodInvocationRecorder.forProxyOf(type));
        }

        private TypedSort(MethodInvocationRecorder.Recorded<T> recorded) {
            super(Collections.emptyList());
            this.recorded = recorded;
        }

        public <S> TypedSort<S> by(Function<T, S> property) {
            return new TypedSort<S>(this.recorded.record(property));
        }

        public <S> TypedSort<S> by(MethodInvocationRecorder.Recorded.ToCollectionConverter<T, S> collectionProperty) {
            return new TypedSort<S>(this.recorded.record(collectionProperty));
        }

        public <S> TypedSort<S> by(MethodInvocationRecorder.Recorded.ToMapConverter<T, S> mapProperty) {
            return new TypedSort<S>(this.recorded.record(mapProperty));
        }

        @Override
        public Sort ascending() {
            return this.withDirection(Sort::ascending);
        }

        @Override
        public Sort descending() {
            return this.withDirection(Sort::descending);
        }

        private Sort withDirection(Function<Sort, Sort> direction) {
            return this.recorded.getPropertyPath().map((? super T xva$0) -> Sort.by(xva$0)).map(direction).orElseGet(Sort::unsorted);
        }

        @Override
        public Iterator<Order> iterator() {
            return this.recorded.getPropertyPath().map(Order::by).map(Collections::singleton).orElseGet(Collections::emptySet).iterator();
        }

        @Override
        public boolean isEmpty() {
            return !this.recorded.getPropertyPath().isPresent();
        }

        @Override
        public String toString() {
            return this.recorded.getPropertyPath().map((? super T xva$0) -> Sort.by(xva$0)).orElseGet(Sort::unsorted).toString();
        }
    }

    public static class Order
    implements Serializable {
        private static final long serialVersionUID = 1522511010900108987L;
        private static final boolean DEFAULT_IGNORE_CASE = false;
        private static final NullHandling DEFAULT_NULL_HANDLING = NullHandling.NATIVE;
        private final Direction direction;
        private final String property;
        private final boolean ignoreCase;
        private final NullHandling nullHandling;

        public Order(@Nullable Direction direction, String property) {
            this(direction, property, false, DEFAULT_NULL_HANDLING);
        }

        public Order(@Nullable Direction direction, String property, NullHandling nullHandlingHint) {
            this(direction, property, false, nullHandlingHint);
        }

        public static Order by(String property) {
            return new Order(DEFAULT_DIRECTION, property);
        }

        public static Order asc(String property) {
            return new Order(Direction.ASC, property, DEFAULT_NULL_HANDLING);
        }

        public static Order desc(String property) {
            return new Order(Direction.DESC, property, DEFAULT_NULL_HANDLING);
        }

        private Order(@Nullable Direction direction, String property, boolean ignoreCase, NullHandling nullHandling) {
            if (!StringUtils.hasText((String)property)) {
                throw new IllegalArgumentException("Property must not be null or empty!");
            }
            this.direction = direction == null ? DEFAULT_DIRECTION : direction;
            this.property = property;
            this.ignoreCase = ignoreCase;
            this.nullHandling = nullHandling;
        }

        public Direction getDirection() {
            return this.direction;
        }

        public String getProperty() {
            return this.property;
        }

        public boolean isAscending() {
            return this.direction.isAscending();
        }

        public boolean isDescending() {
            return this.direction.isDescending();
        }

        public boolean isIgnoreCase() {
            return this.ignoreCase;
        }

        public Order with(Direction direction) {
            return new Order(direction, this.property, this.ignoreCase, this.nullHandling);
        }

        public Order withProperty(String property) {
            return new Order(this.direction, property, this.ignoreCase, this.nullHandling);
        }

        public Sort withProperties(String ... properties) {
            return Sort.by(this.direction, properties);
        }

        public Order ignoreCase() {
            return new Order(this.direction, this.property, true, this.nullHandling);
        }

        public Order with(NullHandling nullHandling) {
            return new Order(this.direction, this.property, this.ignoreCase, nullHandling);
        }

        public Order nullsFirst() {
            return this.with(NullHandling.NULLS_FIRST);
        }

        public Order nullsLast() {
            return this.with(NullHandling.NULLS_LAST);
        }

        public Order nullsNative() {
            return this.with(NullHandling.NATIVE);
        }

        public NullHandling getNullHandling() {
            return this.nullHandling;
        }

        public int hashCode() {
            int result = 17;
            result = 31 * result + this.direction.hashCode();
            result = 31 * result + this.property.hashCode();
            result = 31 * result + (this.ignoreCase ? 1 : 0);
            result = 31 * result + this.nullHandling.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Order)) {
                return false;
            }
            Order that = (Order)obj;
            return this.direction.equals((Object)that.direction) && this.property.equals(that.property) && this.ignoreCase == that.ignoreCase && this.nullHandling.equals((Object)that.nullHandling);
        }

        public String toString() {
            String result = String.format("%s: %s", new Object[]{this.property, this.direction});
            if (!NullHandling.NATIVE.equals((Object)this.nullHandling)) {
                result = result + ", " + (Object)((Object)this.nullHandling);
            }
            if (this.ignoreCase) {
                result = result + ", ignoring case";
            }
            return result;
        }
    }

    public static enum NullHandling {
        NATIVE,
        NULLS_FIRST,
        NULLS_LAST;

    }

    public static enum Direction {
        ASC,
        DESC;


        public boolean isAscending() {
            return this.equals((Object)ASC);
        }

        public boolean isDescending() {
            return this.equals((Object)DESC);
        }

        public static Direction fromString(String value) {
            try {
                return Direction.valueOf(value.toUpperCase(Locale.US));
            }
            catch (Exception e) {
                throw new IllegalArgumentException(String.format("Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), e);
            }
        }

        public static Optional<Direction> fromOptionalString(String value) {
            try {
                return Optional.of(Direction.fromString(value));
            }
            catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
    }
}

