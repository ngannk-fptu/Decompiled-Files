/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.util.FileNameMapper;

public abstract class ContainerMapper
implements FileNameMapper {
    private List<FileNameMapper> mappers = new ArrayList<FileNameMapper>();

    public void addConfiguredMapper(Mapper mapper) {
        this.add(mapper.getImplementation());
    }

    public void addConfigured(FileNameMapper fileNameMapper) {
        this.add(fileNameMapper);
    }

    public synchronized void add(FileNameMapper fileNameMapper) {
        if (this == fileNameMapper || fileNameMapper instanceof ContainerMapper && ((ContainerMapper)fileNameMapper).contains(this)) {
            throw new IllegalArgumentException("Circular mapper containment condition detected");
        }
        this.mappers.add(fileNameMapper);
    }

    protected synchronized boolean contains(FileNameMapper fileNameMapper) {
        for (FileNameMapper m : this.mappers) {
            if (m == fileNameMapper) {
                return true;
            }
            if (!(m instanceof ContainerMapper) || !((ContainerMapper)m).contains(fileNameMapper)) continue;
            return true;
        }
        return false;
    }

    public synchronized List<FileNameMapper> getMappers() {
        return Collections.unmodifiableList(this.mappers);
    }

    @Override
    public void setFrom(String ignore) {
    }

    @Override
    public void setTo(String ignore) {
    }
}

