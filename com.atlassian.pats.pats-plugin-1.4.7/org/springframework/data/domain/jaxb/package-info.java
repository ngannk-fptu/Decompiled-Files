/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlNs
 *  javax.xml.bind.annotation.XmlSchema
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters
 *  org.springframework.lang.NonNullApi
 */
@XmlSchema(xmlns={@XmlNs(prefix="sd", namespaceURI="http://www.springframework.org/schema/data/jaxb")})
@XmlJavaTypeAdapters(value={@XmlJavaTypeAdapter(value=PageableAdapter.class, type=Pageable.class), @XmlJavaTypeAdapter(value=SortAdapter.class, type=Sort.class), @XmlJavaTypeAdapter(value=PageAdapter.class, type=Page.class)})
@NonNullApi
package org.springframework.data.domain.jaxb;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.jaxb.PageAdapter;
import org.springframework.data.domain.jaxb.PageableAdapter;
import org.springframework.data.domain.jaxb.SortAdapter;
import org.springframework.lang.NonNullApi;


