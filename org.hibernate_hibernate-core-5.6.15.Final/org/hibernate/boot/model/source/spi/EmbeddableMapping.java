/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;

public interface EmbeddableMapping {
    public String getClazz();

    public List<JaxbHbmTuplizerType> getTuplizer();

    public String getParent();
}

