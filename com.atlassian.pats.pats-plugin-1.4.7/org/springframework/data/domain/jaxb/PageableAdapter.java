/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.domain.jaxb;

import java.util.Collections;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.SortAdapter;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.lang.Nullable;

class PageableAdapter
extends XmlAdapter<SpringDataJaxb.PageRequestDto, Pageable> {
    PageableAdapter() {
    }

    @Nullable
    public SpringDataJaxb.PageRequestDto marshal(@Nullable Pageable request) {
        if (request == null) {
            return null;
        }
        SpringDataJaxb.PageRequestDto dto = new SpringDataJaxb.PageRequestDto();
        SpringDataJaxb.SortDto sortDto = SortAdapter.INSTANCE.marshal(request.getSort());
        dto.orders = sortDto == null ? Collections.emptyList() : sortDto.orders;
        dto.page = request.getPageNumber();
        dto.size = request.getPageSize();
        return dto;
    }

    @Nonnull
    public Pageable unmarshal(@Nullable SpringDataJaxb.PageRequestDto v) {
        if (v == null) {
            return Pageable.unpaged();
        }
        if (v.orders.isEmpty()) {
            return PageRequest.of(v.page, v.size);
        }
        SpringDataJaxb.SortDto sortDto = new SpringDataJaxb.SortDto();
        sortDto.orders = v.orders;
        return PageRequest.of(v.page, v.size, SortAdapter.INSTANCE.unmarshal(sortDto));
    }
}

