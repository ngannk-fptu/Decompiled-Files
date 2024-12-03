/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintType;

public interface ToolingHintContainer {
    public List<JaxbHbmToolingHintType> getToolingHints();
}

