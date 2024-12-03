/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import java.util.Map;
import lombok.Generated;

public class MigrationMappingPage {
    private final MigrationMappingMeta meta;
    private final Map<String, String> items;

    @Generated
    MigrationMappingPage(MigrationMappingMeta meta, Map<String, String> items) {
        this.meta = meta;
        this.items = items;
    }

    @Generated
    public static MigrationMappingPageBuilder builder() {
        return new MigrationMappingPageBuilder();
    }

    @Generated
    public MigrationMappingMeta getMeta() {
        return this.meta;
    }

    @Generated
    public Map<String, String> getItems() {
        return this.items;
    }

    @Generated
    public static class MigrationMappingPageBuilder {
        @Generated
        private MigrationMappingMeta meta;
        @Generated
        private Map<String, String> items;

        @Generated
        MigrationMappingPageBuilder() {
        }

        @Generated
        public MigrationMappingPageBuilder meta(MigrationMappingMeta meta) {
            this.meta = meta;
            return this;
        }

        @Generated
        public MigrationMappingPageBuilder items(Map<String, String> items) {
            this.items = items;
            return this;
        }

        @Generated
        public MigrationMappingPage build() {
            return new MigrationMappingPage(this.meta, this.items);
        }

        @Generated
        public String toString() {
            return "MigrationMappingPage.MigrationMappingPageBuilder(meta=" + this.meta + ", items=" + this.items + ")";
        }
    }

    public static class MigrationMappingMeta {
        private final int pageSize;
        private final boolean hasNext;
        private final String lastEntity;

        @Generated
        MigrationMappingMeta(int pageSize, boolean hasNext, String lastEntity2) {
            this.pageSize = pageSize;
            this.hasNext = hasNext;
            this.lastEntity = lastEntity2;
        }

        @Generated
        public static MigrationMappingMetaBuilder builder() {
            return new MigrationMappingMetaBuilder();
        }

        @Generated
        public int getPageSize() {
            return this.pageSize;
        }

        @Generated
        public boolean isHasNext() {
            return this.hasNext;
        }

        @Generated
        public String getLastEntity() {
            return this.lastEntity;
        }

        @Generated
        public static class MigrationMappingMetaBuilder {
            @Generated
            private int pageSize;
            @Generated
            private boolean hasNext;
            @Generated
            private String lastEntity;

            @Generated
            MigrationMappingMetaBuilder() {
            }

            @Generated
            public MigrationMappingMetaBuilder pageSize(int pageSize) {
                this.pageSize = pageSize;
                return this;
            }

            @Generated
            public MigrationMappingMetaBuilder hasNext(boolean hasNext) {
                this.hasNext = hasNext;
                return this;
            }

            @Generated
            public MigrationMappingMetaBuilder lastEntity(String lastEntity2) {
                this.lastEntity = lastEntity2;
                return this;
            }

            @Generated
            public MigrationMappingMeta build() {
                return new MigrationMappingMeta(this.pageSize, this.hasNext, this.lastEntity);
            }

            @Generated
            public String toString() {
                return "MigrationMappingPage.MigrationMappingMeta.MigrationMappingMetaBuilder(pageSize=" + this.pageSize + ", hasNext=" + this.hasNext + ", lastEntity=" + this.lastEntity + ")";
            }
        }
    }
}

