/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.util.List;
import org.hibernate.boot.jaxb.mapping.spi.JaxbBasic;
import org.hibernate.boot.jaxb.mapping.spi.JaxbElementCollection;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbedded;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbManyToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToMany;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOneToOne;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTransient;

public interface AttributesContainer {
    public List<JaxbTransient> getTransient();

    public List<JaxbBasic> getBasic();

    public List<JaxbElementCollection> getElementCollection();

    public List<JaxbEmbedded> getEmbedded();

    public List<JaxbManyToMany> getManyToMany();

    public List<JaxbManyToOne> getManyToOne();

    public List<JaxbOneToMany> getOneToMany();

    public List<JaxbOneToOne> getOneToOne();
}

