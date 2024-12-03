/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SpringDataAnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class SortHandlerMethodArgumentResolverSupport {
    private static final String DEFAULT_PARAMETER = "sort";
    private static final String DEFAULT_PROPERTY_DELIMITER = ",";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final Sort DEFAULT_SORT = Sort.unsorted();
    private static final String SORT_DEFAULTS_NAME = SortDefault.SortDefaults.class.getSimpleName();
    private static final String SORT_DEFAULT_NAME = SortDefault.class.getSimpleName();
    private Sort fallbackSort = DEFAULT_SORT;
    private String sortParameter = "sort";
    private String propertyDelimiter = ",";
    private String qualifierDelimiter = "_";

    public void setSortParameter(String sortParameter) {
        Assert.hasText((String)sortParameter, (String)"SortParameter must not be null nor empty!");
        this.sortParameter = sortParameter;
    }

    public void setPropertyDelimiter(String propertyDelimiter) {
        Assert.hasText((String)propertyDelimiter, (String)"Property delimiter must not be null or empty!");
        this.propertyDelimiter = propertyDelimiter;
    }

    public String getPropertyDelimiter() {
        return this.propertyDelimiter;
    }

    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter == null ? DEFAULT_QUALIFIER_DELIMITER : qualifierDelimiter;
    }

    public void setFallbackSort(Sort fallbackSort) {
        this.fallbackSort = fallbackSort;
    }

    protected Sort getDefaultFromAnnotationOrFallback(MethodParameter parameter) {
        SortDefault.SortDefaults annotatedDefaults = (SortDefault.SortDefaults)parameter.getParameterAnnotation(SortDefault.SortDefaults.class);
        SortDefault annotatedDefault = (SortDefault)parameter.getParameterAnnotation(SortDefault.class);
        if (annotatedDefault != null && annotatedDefaults != null) {
            throw new IllegalArgumentException(String.format("Cannot use both @%s and @%s on parameter %s! Move %s into %s to define sorting order!", SORT_DEFAULTS_NAME, SORT_DEFAULT_NAME, parameter.toString(), SORT_DEFAULT_NAME, SORT_DEFAULTS_NAME));
        }
        if (annotatedDefault != null) {
            return this.appendOrCreateSortTo(annotatedDefault, Sort.unsorted());
        }
        if (annotatedDefaults != null) {
            Sort sort = Sort.unsorted();
            for (SortDefault currentAnnotatedDefault : annotatedDefaults.value()) {
                sort = this.appendOrCreateSortTo(currentAnnotatedDefault, sort);
            }
            return sort;
        }
        return this.fallbackSort;
    }

    private Sort appendOrCreateSortTo(SortDefault sortDefault, Sort sortOrNull) {
        String[] fields = (String[])SpringDataAnnotationUtils.getSpecificPropertyOrDefaultFromValue(sortDefault, DEFAULT_PARAMETER);
        if (fields.length == 0) {
            return Sort.unsorted();
        }
        ArrayList<Sort.Order> orders = new ArrayList<Sort.Order>(fields.length);
        for (String field : fields) {
            Sort.Order order = new Sort.Order(sortDefault.direction(), field);
            orders.add(sortDefault.caseSensitive() ? order : order.ignoreCase());
        }
        return sortOrNull.and(Sort.by(orders));
    }

    protected String getSortParameter(@Nullable MethodParameter parameter) {
        StringBuilder builder = new StringBuilder();
        String value = SpringDataAnnotationUtils.getQualifier(parameter);
        if (StringUtils.hasLength((String)value)) {
            builder.append(value);
            builder.append(this.qualifierDelimiter);
        }
        return builder.append(this.sortParameter).toString();
    }

    Sort parseParameterIntoSort(List<String> source, String delimiter) {
        ArrayList<Sort.Order> allOrders = new ArrayList<Sort.Order>();
        for (String part : source) {
            if (part == null) continue;
            SortOrderParser.parse(part, delimiter).parseIgnoreCase().parseDirection().forEachOrder(allOrders::add);
        }
        return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
    }

    protected List<String> foldIntoExpressions(Sort sort) {
        ArrayList<String> expressions = new ArrayList<String>();
        ExpressionBuilder builder = null;
        for (Sort.Order order : sort) {
            Sort.Direction direction = order.getDirection();
            if (builder == null) {
                builder = new ExpressionBuilder(direction);
            } else if (!builder.hasSameDirectionAs(order)) {
                builder.dumpExpressionIfPresentInto(expressions);
                builder = new ExpressionBuilder(direction);
            }
            builder.add(order.getProperty());
        }
        return builder == null ? Collections.emptyList() : builder.dumpExpressionIfPresentInto(expressions);
    }

    protected List<String> legacyFoldExpressions(Sort sort) {
        ArrayList<String> expressions = new ArrayList<String>();
        ExpressionBuilder builder = null;
        for (Sort.Order order : sort) {
            Sort.Direction direction = order.getDirection();
            if (builder == null) {
                builder = new ExpressionBuilder(direction);
            } else if (!builder.hasSameDirectionAs(order)) {
                throw new IllegalArgumentException(String.format("%s in legacy configuration only supports a single direction to sort by!", this.getClass().getSimpleName()));
            }
            builder.add(order.getProperty());
        }
        return builder == null ? Collections.emptyList() : builder.dumpExpressionIfPresentInto(expressions);
    }

    static boolean notOnlyDots(String source) {
        return StringUtils.hasText((String)source.replace(".", ""));
    }

    static class SortOrderParser {
        private static final String IGNORECASE = "ignorecase";
        private final String[] elements;
        private final int lastIndex;
        private final Optional<Sort.Direction> direction;
        private final Optional<Boolean> ignoreCase;

        private SortOrderParser(String[] elements) {
            this(elements, elements.length, Optional.empty(), Optional.empty());
        }

        private SortOrderParser(String[] elements, int lastIndex, Optional<Sort.Direction> direction, Optional<Boolean> ignoreCase) {
            this.elements = elements;
            this.lastIndex = Math.max(0, lastIndex);
            this.direction = direction;
            this.ignoreCase = ignoreCase;
        }

        public static SortOrderParser parse(String part, String delimiter) {
            String[] elements = (String[])Arrays.stream(part.split(delimiter)).filter(SortHandlerMethodArgumentResolverSupport::notOnlyDots).toArray(String[]::new);
            return new SortOrderParser(elements);
        }

        public SortOrderParser parseIgnoreCase() {
            Optional<Boolean> ignoreCase = this.lastIndex > 0 ? this.fromOptionalString(this.elements[this.lastIndex - 1]) : Optional.empty();
            return new SortOrderParser(this.elements, this.lastIndex - (ignoreCase.isPresent() ? 1 : 0), this.direction, ignoreCase);
        }

        public SortOrderParser parseDirection() {
            Optional<Sort.Direction> direction = this.lastIndex > 0 ? Sort.Direction.fromOptionalString(this.elements[this.lastIndex - 1]) : Optional.empty();
            return new SortOrderParser(this.elements, this.lastIndex - (direction.isPresent() ? 1 : 0), direction, this.ignoreCase);
        }

        public void forEachOrder(Consumer<? super Sort.Order> callback) {
            for (int i = 0; i < this.lastIndex; ++i) {
                this.toOrder(this.elements[i]).ifPresent(callback);
            }
        }

        private Optional<Boolean> fromOptionalString(String value) {
            return IGNORECASE.equalsIgnoreCase(value) ? Optional.of(true) : Optional.empty();
        }

        private Optional<Sort.Order> toOrder(String property) {
            if (!StringUtils.hasText((String)property)) {
                return Optional.empty();
            }
            Sort.Order order = this.direction.map(it -> new Sort.Order((Sort.Direction)((Object)it), property)).orElseGet(() -> Sort.Order.by(property));
            if (this.ignoreCase.isPresent()) {
                return Optional.of(order.ignoreCase());
            }
            return Optional.of(order);
        }
    }

    class ExpressionBuilder {
        private final List<String> elements = new ArrayList<String>();
        private final Sort.Direction direction;

        ExpressionBuilder(Sort.Direction direction) {
            Assert.notNull((Object)((Object)direction), (String)"Direction must not be null!");
            this.direction = direction;
        }

        boolean hasSameDirectionAs(Sort.Order order) {
            return this.direction == order.getDirection();
        }

        void add(String property) {
            this.elements.add(property);
        }

        List<String> dumpExpressionIfPresentInto(List<String> expressions) {
            if (this.elements.isEmpty()) {
                return expressions;
            }
            this.elements.add(this.direction.name().toLowerCase());
            expressions.add(StringUtils.collectionToDelimitedString(this.elements, (String)SortHandlerMethodArgumentResolverSupport.this.propertyDelimiter));
            return expressions;
        }
    }
}

