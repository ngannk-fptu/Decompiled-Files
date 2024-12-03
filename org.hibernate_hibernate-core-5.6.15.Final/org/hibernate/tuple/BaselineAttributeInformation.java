/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.FetchMode;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.tuple.ValueGeneration;

public class BaselineAttributeInformation {
    private final boolean lazy;
    private final boolean insertable;
    private final boolean updateable;
    private final ValueGeneration valueGenerationStrategy;
    private final boolean nullable;
    private final boolean dirtyCheckable;
    private final boolean versionable;
    private final CascadeStyle cascadeStyle;
    private final FetchMode fetchMode;
    private boolean checkable;

    public BaselineAttributeInformation(boolean lazy, boolean insertable, boolean updateable, ValueGeneration valueGenerationStrategy, boolean nullable, boolean dirtyCheckable, boolean versionable, CascadeStyle cascadeStyle, FetchMode fetchMode) {
        this.lazy = lazy;
        this.insertable = insertable;
        this.updateable = updateable;
        this.valueGenerationStrategy = valueGenerationStrategy;
        this.nullable = nullable;
        this.dirtyCheckable = dirtyCheckable;
        this.versionable = versionable;
        this.cascadeStyle = cascadeStyle;
        this.fetchMode = fetchMode;
    }

    public boolean isLazy() {
        return this.lazy;
    }

    public boolean isInsertable() {
        return this.insertable;
    }

    public boolean isUpdateable() {
        return this.updateable;
    }

    public ValueGeneration getValueGenerationStrategy() {
        return this.valueGenerationStrategy;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public boolean isDirtyCheckable() {
        return this.dirtyCheckable;
    }

    public boolean isVersionable() {
        return this.versionable;
    }

    public CascadeStyle getCascadeStyle() {
        return this.cascadeStyle;
    }

    public FetchMode getFetchMode() {
        return this.fetchMode;
    }

    public boolean isCheckable() {
        return this.checkable;
    }

    public static class Builder {
        private boolean lazy;
        private boolean insertable;
        private boolean updateable;
        private ValueGeneration valueGenerationStrategy;
        private boolean nullable;
        private boolean dirtyCheckable;
        private boolean versionable;
        private CascadeStyle cascadeStyle;
        private FetchMode fetchMode;

        public Builder setLazy(boolean lazy) {
            this.lazy = lazy;
            return this;
        }

        public Builder setInsertable(boolean insertable) {
            this.insertable = insertable;
            return this;
        }

        public Builder setUpdateable(boolean updateable) {
            this.updateable = updateable;
            return this;
        }

        public Builder setValueGenerationStrategy(ValueGeneration valueGenerationStrategy) {
            this.valueGenerationStrategy = valueGenerationStrategy;
            return this;
        }

        public Builder setNullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        public Builder setDirtyCheckable(boolean dirtyCheckable) {
            this.dirtyCheckable = dirtyCheckable;
            return this;
        }

        public Builder setVersionable(boolean versionable) {
            this.versionable = versionable;
            return this;
        }

        public Builder setCascadeStyle(CascadeStyle cascadeStyle) {
            this.cascadeStyle = cascadeStyle;
            return this;
        }

        public Builder setFetchMode(FetchMode fetchMode) {
            this.fetchMode = fetchMode;
            return this;
        }

        public BaselineAttributeInformation createInformation() {
            return new BaselineAttributeInformation(this.lazy, this.insertable, this.updateable, this.valueGenerationStrategy, this.nullable, this.dirtyCheckable, this.versionable, this.cascadeStyle, this.fetchMode);
        }
    }
}

