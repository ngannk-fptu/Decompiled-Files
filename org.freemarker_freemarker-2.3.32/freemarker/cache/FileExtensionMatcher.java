/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateSourceMatcher;
import java.io.IOException;

public class FileExtensionMatcher
extends TemplateSourceMatcher {
    private final String extension;
    private boolean caseInsensitive = true;

    public FileExtensionMatcher(String extension) {
        if (extension.indexOf(47) != -1) {
            throw new IllegalArgumentException("A file extension can't contain \"/\": " + extension);
        }
        if (extension.indexOf(42) != -1) {
            throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
        }
        if (extension.indexOf(63) != -1) {
            throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
        }
        if (extension.startsWith(".")) {
            throw new IllegalArgumentException("A file extension can't start with \".\": " + extension);
        }
        this.extension = extension;
    }

    @Override
    public boolean matches(String sourceName, Object templateSource) throws IOException {
        int extLn;
        int ln = sourceName.length();
        if (ln < (extLn = this.extension.length()) + 1 || sourceName.charAt(ln - extLn - 1) != '.') {
            return false;
        }
        return sourceName.regionMatches(this.caseInsensitive, ln - extLn, this.extension, 0, extLn);
    }

    public boolean isCaseInsensitive() {
        return this.caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public FileExtensionMatcher caseInsensitive(boolean caseInsensitive) {
        this.setCaseInsensitive(caseInsensitive);
        return this;
    }
}

