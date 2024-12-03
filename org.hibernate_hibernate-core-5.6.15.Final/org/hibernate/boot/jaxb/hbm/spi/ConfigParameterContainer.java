/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmConfigParameterType;

public interface ConfigParameterContainer {
    public List<JaxbHbmConfigParameterType> getConfigParameters();
}

