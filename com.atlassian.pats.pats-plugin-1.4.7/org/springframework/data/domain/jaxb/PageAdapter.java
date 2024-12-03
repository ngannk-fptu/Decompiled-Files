/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.springframework.hateoas.Link
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.domain.jaxb;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.hateoas.Link;
import org.springframework.lang.Nullable;

public class PageAdapter
extends XmlAdapter<SpringDataJaxb.PageDto, Page<Object>> {
    @Nullable
    public SpringDataJaxb.PageDto marshal(@Nullable Page<Object> source) {
        if (source == null) {
            return null;
        }
        SpringDataJaxb.PageDto dto = new SpringDataJaxb.PageDto();
        dto.content = source.getContent();
        dto.add(this.getLinks(source));
        return dto;
    }

    @Nullable
    public Page<Object> unmarshal(@Nullable SpringDataJaxb.PageDto v) {
        return null;
    }

    protected List<Link> getLinks(Page<?> source) {
        return Collections.emptyList();
    }
}

