/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.template;

import com.atlassian.plugin.notifications.spi.TemplateParams;
import java.io.File;

public interface TemplatePathResolver {
    public Iterable<TemplatePath> getCustomTemplatePaths(TemplateParams var1);

    public Iterable<TemplatePath> getTemplatePaths(File var1, TemplateParams var2);

    public static class TemplatePath {
        private File baseDir;
        private String templatePath;

        TemplatePath(File baseDir, String templatePath) {
            this.baseDir = baseDir;
            this.templatePath = templatePath;
        }

        public String getFullTemplatePath() {
            return this.baseDir.getPath() + File.separator + this.templatePath;
        }

        public File getBaseDir() {
            return this.baseDir;
        }

        public String getTemplatePath() {
            return this.templatePath;
        }
    }
}

