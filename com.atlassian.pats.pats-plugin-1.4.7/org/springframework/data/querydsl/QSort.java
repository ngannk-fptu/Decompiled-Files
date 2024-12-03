/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class QSort
extends Sort
implements Serializable {
    private static final long serialVersionUID = -6701117396842171930L;
    private static final QSort UNSORTED = new QSort(new OrderSpecifier[0]);
    private final List<OrderSpecifier<?>> orderSpecifiers;

    public QSort(OrderSpecifier<?> ... orderSpecifiers) {
        this(Arrays.asList(orderSpecifiers));
    }

    public QSort(List<OrderSpecifier<?>> orderSpecifiers) {
        super(QSort.toOrders(orderSpecifiers));
        this.orderSpecifiers = orderSpecifiers;
    }

    public static QSort by(OrderSpecifier<?> ... orderSpecifiers) {
        return new QSort(orderSpecifiers);
    }

    public static QSort unsorted() {
        return UNSORTED;
    }

    private static List<Sort.Order> toOrders(List<OrderSpecifier<?>> orderSpecifiers) {
        Assert.notNull(orderSpecifiers, (String)"Order specifiers must not be null!");
        return orderSpecifiers.stream().map(QSort::toOrder).collect(Collectors.toList());
    }

    private static Sort.Order toOrder(OrderSpecifier<?> orderSpecifier) {
        Assert.notNull(orderSpecifier, (String)"Order specifier must not be null!");
        Expression<?> target = orderSpecifier.getTarget();
        Expression<?> targetElement = target instanceof Path ? QSort.preparePropertyPath((Path)target) : target;
        Assert.notNull(targetElement, (String)"Target element must not be null!");
        return Sort.Order.by(targetElement.toString()).with(orderSpecifier.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC);
    }

    @Override
    public boolean isEmpty() {
        return this.orderSpecifiers.isEmpty();
    }

    public List<OrderSpecifier<?>> getOrderSpecifiers() {
        return this.orderSpecifiers;
    }

    public QSort and(QSort sort) {
        return sort == null ? this : this.and(sort.getOrderSpecifiers());
    }

    public QSort and(List<OrderSpecifier<?>> orderSpecifiers) {
        Assert.notEmpty(orderSpecifiers, (String)"OrderSpecifiers must not be null or empty!");
        ArrayList newOrderSpecifiers = new ArrayList(this.orderSpecifiers);
        newOrderSpecifiers.addAll(orderSpecifiers);
        return new QSort(newOrderSpecifiers);
    }

    public QSort and(OrderSpecifier<?> ... orderSpecifiers) {
        Assert.notEmpty((Object[])orderSpecifiers, (String)"OrderSpecifiers must not be null or empty!");
        return this.and(Arrays.asList(orderSpecifiers));
    }

    private static String preparePropertyPath(Path<?> path) {
        Path<?> root = path.getRoot();
        return root == null || path.equals(root) ? path.toString() : path.toString().substring(root.toString().length() + 1);
    }
}

