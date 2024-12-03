/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Objects;
import java.util.stream.Stream;
import org.apache.tools.ant.util.ContainerMapper;

public class ChainedMapper
extends ContainerMapper {
    @Override
    public String[] mapFileName(String sourceFileName) {
        String[] result = this.getMappers().stream().filter(Objects::nonNull).reduce(new String[]{sourceFileName}, (i, m) -> (String[])Stream.of(i).map(m::mapFileName).filter(Objects::nonNull).flatMap(Stream::of).toArray(String[]::new), (i, o) -> o);
        return result == null || result.length == 0 ? null : result;
    }
}

