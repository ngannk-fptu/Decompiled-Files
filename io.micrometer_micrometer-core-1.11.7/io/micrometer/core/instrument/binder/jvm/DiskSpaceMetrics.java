/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.jvm;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.io.File;
import java.util.Collections;

@NonNullApi
@NonNullFields
@Deprecated
@Incubating(since="1.1.0")
public class DiskSpaceMetrics
implements MeterBinder {
    private final Iterable<Tag> tags;
    private final File path;
    private final String absolutePath;

    public DiskSpaceMetrics(File path) {
        this(path, Collections.emptyList());
    }

    public DiskSpaceMetrics(File path, Iterable<Tag> tags) {
        this.path = path;
        this.absolutePath = path.getAbsolutePath();
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Tags tagsWithPath = Tags.concat(this.tags, "path", this.absolutePath);
        Gauge.builder("disk.free", this.path, File::getUsableSpace).tags(tagsWithPath).description("Usable space for path").baseUnit("bytes").strongReference(true).register(registry);
        Gauge.builder("disk.total", this.path, File::getTotalSpace).tags(tagsWithPath).description("Total space for path").baseUnit("bytes").strongReference(true).register(registry);
    }
}

