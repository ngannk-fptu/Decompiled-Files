/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Objects;
import java.util.stream.Stream;
import org.apache.tools.ant.util.ContainerMapper;

public class CompositeMapper
extends ContainerMapper {
    @Override
    public String[] mapFileName(String sourceFileName) {
        String[] result = (String[])this.getMappers().stream().filter(Objects::nonNull).map(m -> m.mapFileName(sourceFileName)).filter(Objects::nonNull).flatMap(Stream::of).toArray(String[]::new);
        return result.length == 0 ? null : result;
    }
}

