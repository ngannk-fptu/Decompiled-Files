/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.domain.jaxb;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.jaxb.OrderAdapter;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.lang.Nullable;

public class SortAdapter
extends XmlAdapter<SpringDataJaxb.SortDto, Sort> {
    public static final SortAdapter INSTANCE = new SortAdapter();

    @Nullable
    public SpringDataJaxb.SortDto marshal(@Nullable Sort source) {
        if (source == null) {
            return null;
        }
        SpringDataJaxb.SortDto dto = new SpringDataJaxb.SortDto();
        dto.orders = SpringDataJaxb.marshal(source, OrderAdapter.INSTANCE);
        return dto;
    }

    @Nonnull
    public Sort unmarshal(@Nullable SpringDataJaxb.SortDto source) {
        return source == null ? Sort.unsorted() : Sort.by(SpringDataJaxb.unmarshal(source.orders, OrderAdapter.INSTANCE));
    }
}

