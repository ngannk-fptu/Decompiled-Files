/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.renderer.RenderContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ExceptionTolerantMigrator {
    @Deprecated
    public String migrate(String var1, RenderContext var2, List<RuntimeException> var3);

    public MigrationResult migrate(String var1, ConversionContext var2);

    public static class MigrationResult {
        private final String content;
        private final List<RuntimeException> exceptions;
        private final boolean migrationPerformed;

        public MigrationResult(String content, boolean migrationPerformed, List<RuntimeException> exceptions) {
            this.content = content;
            this.migrationPerformed = migrationPerformed;
            this.exceptions = exceptions;
        }

        public MigrationResult(String content, boolean migrationPerformed) {
            this(content, migrationPerformed, new ArrayList<RuntimeException>());
        }

        public String getContent() {
            return this.content;
        }

        public List<RuntimeException> getExceptions() {
            return Collections.unmodifiableList(this.exceptions);
        }

        public boolean isMigrationPerformed() {
            return this.migrationPerformed;
        }
    }
}

