/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.query.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.StringUtils;

class OrderBySource {
    static OrderBySource EMPTY = new OrderBySource("");
    private static final String BLOCK_SPLIT = "(?<=Asc|Desc)(?=\\p{Lu})";
    private static final Pattern DIRECTION_SPLIT = Pattern.compile("(.+?)(Asc|Desc)?$");
    private static final String INVALID_ORDER_SYNTAX = "Invalid order syntax for part %s!";
    private static final Set<String> DIRECTION_KEYWORDS = new HashSet<String>(Arrays.asList("Asc", "Desc"));
    private final List<Sort.Order> orders = new ArrayList<Sort.Order>();

    OrderBySource(String clause) {
        this(clause, Optional.empty());
    }

    OrderBySource(String clause, Optional<Class<?>> domainClass) {
        if (!StringUtils.hasText((String)clause)) {
            return;
        }
        for (String part : clause.split(BLOCK_SPLIT)) {
            Matcher matcher = DIRECTION_SPLIT.matcher(part);
            if (!matcher.find()) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }
            String propertyString = matcher.group(1);
            String directionString = matcher.group(2);
            if (DIRECTION_KEYWORDS.contains(propertyString) && directionString == null) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }
            this.orders.add(this.createOrder(propertyString, Sort.Direction.fromOptionalString(directionString), domainClass));
        }
    }

    private Sort.Order createOrder(String propertySource, Optional<Sort.Direction> direction, Optional<Class<?>> domainClass) {
        return domainClass.map(type -> {
            PropertyPath propertyPath = PropertyPath.from(propertySource, type);
            return direction.map(it -> new Sort.Order((Sort.Direction)((Object)((Object)it)), propertyPath.toDotPath())).orElseGet(() -> Sort.Order.by(propertyPath.toDotPath()));
        }).orElseGet(() -> direction.map(it -> new Sort.Order((Sort.Direction)((Object)((Object)it)), StringUtils.uncapitalize((String)propertySource))).orElseGet(() -> Sort.Order.by(StringUtils.uncapitalize((String)propertySource))));
    }

    Sort toSort() {
        return Sort.by(this.orders);
    }

    public String toString() {
        return "Order By " + StringUtils.collectionToDelimitedString(this.orders, (String)", ");
    }
}

