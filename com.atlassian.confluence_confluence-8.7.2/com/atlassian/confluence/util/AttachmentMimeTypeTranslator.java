/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.servlet.ConfluenceContentTypeResolver;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public final class AttachmentMimeTypeTranslator {
    private static final Pattern VALID_MIME_TYPE = Pattern.compile("([a-z0-9_-]+/[^;]+)+.*", 42);
    private static final List<? extends TranslationStrategy> STRATEGIES = List.of(new InternetExplorerImageMimeTypeTranslationStrategy(), new CSVMimeTypeTranslationStrategy(), new GuessMimeTypeTranslationStrategy());

    public String resolveMimeType(String filename, String originalMimeType) {
        if (StringUtils.isEmpty((CharSequence)originalMimeType)) {
            return "application/octet-stream";
        }
        String mimeType = this.sanitizeMimeType(originalMimeType);
        for (TranslationStrategy translationStrategy : STRATEGIES) {
            String translated;
            if (!translationStrategy.handles(filename, mimeType) || null == (translated = translationStrategy.translate(filename, mimeType))) continue;
            return translated;
        }
        return mimeType;
    }

    private String sanitizeMimeType(String input) {
        String mimeType = input.toLowerCase();
        Matcher m = VALID_MIME_TYPE.matcher(mimeType);
        if (m.matches()) {
            return m.group(1);
        }
        return mimeType;
    }

    private static class CSVMimeTypeTranslationStrategy
    implements TranslationStrategy {
        private CSVMimeTypeTranslationStrategy() {
        }

        @Override
        public boolean handles(String filename, String mimeType) {
            return FilenameUtils.isExtension((String)filename, (String)"csv");
        }

        @Override
        public String translate(String filename, String mimeType) {
            return "text/csv";
        }
    }

    private static class GuessMimeTypeTranslationStrategy
    implements TranslationStrategy {
        private static final Set<String> ZIP_CONTAINER_MIME_TYPES = Set.of("application/zip", "application/x-zip", "application/x-zip-compressed", "application/x-compress", "application/x-compressed", "multipart/x-zip", "application/octet-stream");
        private final ConfluenceContentTypeResolver resolver = new ConfluenceContentTypeResolver();

        GuessMimeTypeTranslationStrategy() {
        }

        @Override
        public boolean handles(String filename, String mimeType) {
            if (filename == null) {
                return false;
            }
            return ZIP_CONTAINER_MIME_TYPES.contains(mimeType) && StringUtils.isNotEmpty((CharSequence)FilenameUtils.getExtension((String)filename)) && !FilenameUtils.isExtension((String)filename, (String)"csv");
        }

        @Override
        public String translate(String filename, String mimeType) {
            return this.resolver.getContentType(filename);
        }
    }

    private static class InternetExplorerImageMimeTypeTranslationStrategy
    implements TranslationStrategy {
        private InternetExplorerImageMimeTypeTranslationStrategy() {
        }

        @Override
        public boolean handles(String filename, String mimeType) {
            return "image/pjpeg".equals(mimeType) || "image/x-png".equals(mimeType);
        }

        @Override
        public String translate(String filename, String mimeType) {
            if ("image/pjpeg".equals(mimeType)) {
                return "image/jpeg";
            }
            if ("image/x-png".equals(mimeType)) {
                return "image/png";
            }
            return mimeType;
        }
    }

    private static interface TranslationStrategy {
        public boolean handles(String var1, String var2);

        public String translate(String var1, String var2);
    }
}

