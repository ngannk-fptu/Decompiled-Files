/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;

public interface EntityLinkBuilderFactory {
    public EntityLinkBuilder builder();

    public static interface EntityLinkBuilder {
        public EntityLinkBuilder key(String var1);

        public EntityLinkBuilder type(EntityType var1);

        public EntityLinkBuilder applicationLink(ApplicationLink var1);

        public EntityLinkBuilder primary(boolean var1);

        public EntityLinkBuilder name(String var1);

        public EntityLink build();
    }
}

