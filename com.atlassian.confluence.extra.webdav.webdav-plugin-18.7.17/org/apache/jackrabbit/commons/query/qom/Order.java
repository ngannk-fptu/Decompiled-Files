/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.query.qom;

public enum Order {
    ASCENDING("jcr.order.ascending"),
    DESCENDING("jcr.order.descending");

    private final String name;

    private Order(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static Order getOrderByName(String name) {
        for (Order order : Order.values()) {
            if (!order.name.equals(name)) continue;
            return order;
        }
        throw new IllegalArgumentException("Unknown order name: " + name);
    }
}

