/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Objects;
import org.apache.tools.ant.util.ContainerMapper;

public class FirstMatchMapper
extends ContainerMapper {
    @Override
    public String[] mapFileName(String sourceFileName) {
        return this.getMappers().stream().filter(Objects::nonNull).map(m -> m.mapFileName(sourceFileName)).filter(Objects::nonNull).findFirst().orElse(null);
    }
}

