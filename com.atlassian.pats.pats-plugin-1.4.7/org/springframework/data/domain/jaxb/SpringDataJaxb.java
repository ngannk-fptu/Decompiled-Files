/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.springframework.hateoas.RepresentationModel
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.domain.jaxb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class SpringDataJaxb {
    public static final String NAMESPACE = "http://www.springframework.org/schema/data/jaxb";

    private SpringDataJaxb() {
    }

    public static <T, S> List<T> unmarshal(Collection<S> source, XmlAdapter<S, T> adapter) {
        Assert.notNull(adapter, (String)"Adapter must not be null!");
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Object> result = new ArrayList<Object>(source.size());
        for (S element : source) {
            try {
                result.add(adapter.unmarshal(element));
            }
            catch (Exception o_O) {
                throw new RuntimeException(o_O);
            }
        }
        return result;
    }

    public static <T, S> List<S> marshal(@Nullable Iterable<T> source, XmlAdapter<S, T> adapter) {
        Assert.notNull(adapter, (String)"Adapter must not be null!");
        if (source == null) {
            return Collections.emptyList();
        }
        ArrayList<Object> result = new ArrayList<Object>();
        for (T element : source) {
            try {
                result.add(adapter.marshal(element));
            }
            catch (Exception o_O) {
                throw new RuntimeException(o_O);
            }
        }
        return result;
    }

    @XmlRootElement(name="page", namespace="http://www.springframework.org/schema/data/jaxb")
    @XmlAccessorType(value=XmlAccessType.FIELD)
    public static class PageDto
    extends RepresentationModel {
        @Nullable
        @XmlAnyElement
        @XmlElementWrapper(name="content")
        List<Object> content;
    }

    @XmlRootElement(name="order", namespace="http://www.springframework.org/schema/data/jaxb")
    @XmlAccessorType(value=XmlAccessType.FIELD)
    public static class OrderDto {
        @Nullable
        @XmlAttribute
        String property;
        @Nullable
        @XmlAttribute
        Sort.Direction direction;
    }

    @XmlRootElement(name="sort", namespace="http://www.springframework.org/schema/data/jaxb")
    @XmlAccessorType(value=XmlAccessType.FIELD)
    public static class SortDto {
        @XmlElement(name="order", namespace="http://www.springframework.org/schema/data/jaxb")
        List<OrderDto> orders = new ArrayList<OrderDto>();
    }

    @XmlRootElement(name="page-request", namespace="http://www.springframework.org/schema/data/jaxb")
    @XmlAccessorType(value=XmlAccessType.FIELD)
    public static class PageRequestDto {
        @XmlAttribute
        int page;
        @XmlAttribute
        int size;
        @XmlElement(name="order", namespace="http://www.springframework.org/schema/data/jaxb")
        List<OrderDto> orders = new ArrayList<OrderDto>();
    }
}

