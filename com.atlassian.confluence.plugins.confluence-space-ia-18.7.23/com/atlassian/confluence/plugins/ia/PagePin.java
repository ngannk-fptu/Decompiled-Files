/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 */
package com.atlassian.confluence.plugins.ia;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface PagePin
extends Entity {
    public long getPageId();

    public void setPageId(long var1);

    public long getSpaceId();

    public void setSpaceId(long var1);

    public ConcreteType getConcreteType();

    public void setConcreteType(ConcreteType var1);

    public static enum ConcreteType {
        BLOG_POST("BLOG_POST"),
        PAGE("PAGE");

        String value;

        private ConcreteType(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }
}

