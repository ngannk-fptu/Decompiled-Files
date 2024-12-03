/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.code;

import com.atlassian.renderer.v2.macro.code.SourceCodeFormatter;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatterRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimpleSourceCodeFormatterRepository
implements SourceCodeFormatterRepository {
    private Map<String, SourceCodeFormatter> formatters = new TreeMap<String, SourceCodeFormatter>();

    public SimpleSourceCodeFormatterRepository() {
    }

    public SimpleSourceCodeFormatterRepository(List<SourceCodeFormatter> formatters) {
        this.setCodeFormatters(formatters);
    }

    @Override
    public SourceCodeFormatter getSourceCodeFormatter(String language) {
        return this.formatters.get(language);
    }

    @Override
    public Collection getAvailableLanguages() {
        return Collections.unmodifiableSet(this.formatters.keySet());
    }

    private void setCodeFormatters(List<SourceCodeFormatter> codeFormatters) {
        this.formatters.clear();
        for (SourceCodeFormatter formatter : codeFormatters) {
            for (String language : formatter.getSupportedLanguages()) {
                this.formatters.put(language, formatter);
            }
        }
    }
}

