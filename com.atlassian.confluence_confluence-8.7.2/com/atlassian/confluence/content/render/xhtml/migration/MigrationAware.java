/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.google.common.base.Predicate;

public interface MigrationAware {
    public boolean wasMigrationPerformed(ConversionContext var1);

    public static class MigrationPerformedPredicate
    implements Predicate<ConversionContext> {
        private final MigrationAware migrationAware;

        public MigrationPerformedPredicate(MigrationAware migrationAware) {
            this.migrationAware = migrationAware;
        }

        public boolean apply(ConversionContext conversionContext) {
            return this.migrationAware.wasMigrationPerformed(conversionContext);
        }
    }
}

