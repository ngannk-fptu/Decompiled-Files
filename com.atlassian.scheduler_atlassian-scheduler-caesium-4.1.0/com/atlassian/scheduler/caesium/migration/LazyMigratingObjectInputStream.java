/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.core.util.ClassLoaderAwareObjectInputStream
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.scheduler.caesium.migration;

import com.atlassian.scheduler.caesium.migration.DirtyFlagMap;
import com.atlassian.scheduler.caesium.migration.JobDataMap;
import com.atlassian.scheduler.caesium.migration.StringKeyDirtyFlagMap;
import com.atlassian.scheduler.core.util.ClassLoaderAwareObjectInputStream;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.util.Map;

public class LazyMigratingObjectInputStream
extends ClassLoaderAwareObjectInputStream {
    private static final Map<String, Class<?>> CLASS_MAPPER = ImmutableMap.builder().put((Object)"org.quartz.JobDataMap", JobDataMap.class).put((Object)"org.quartz.utils.StringKeyDirtyFlagMap", StringKeyDirtyFlagMap.class).put((Object)"org.quartz.utils.DirtyFlagMap", DirtyFlagMap.class).build();

    public LazyMigratingObjectInputStream(ClassLoader classLoader, byte[] parameters) throws IOException {
        super(classLoader, parameters);
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        Class killed = CLASS_MAPPER.get(desc.getName());
        return killed != null ? killed : super.resolveClass(desc);
    }
}

