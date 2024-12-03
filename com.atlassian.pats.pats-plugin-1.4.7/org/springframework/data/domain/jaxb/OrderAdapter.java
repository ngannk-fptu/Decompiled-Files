/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.domain.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.lang.Nullable;

public class OrderAdapter
extends XmlAdapter<SpringDataJaxb.OrderDto, Sort.Order> {
    public static final OrderAdapter INSTANCE = new OrderAdapter();

    @Nullable
    public SpringDataJaxb.OrderDto marshal(@Nullable Sort.Order order) {
        if (order == null) {
            return null;
        }
        SpringDataJaxb.OrderDto dto = new SpringDataJaxb.OrderDto();
        dto.direction = order.getDirection();
        dto.property = order.getProperty();
        return dto;
    }

    @Nullable
    public Sort.Order unmarshal(@Nullable SpringDataJaxb.OrderDto source) {
        if (source == null) {
            return null;
        }
        Sort.Direction direction = source.direction;
        String property = source.property;
        if (direction == null || property == null) {
            return null;
        }
        return new Sort.Order(direction, property);
    }
}

