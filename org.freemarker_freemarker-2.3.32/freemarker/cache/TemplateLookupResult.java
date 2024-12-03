/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.template.utility.NullArgumentException;

public abstract class TemplateLookupResult {
    static TemplateLookupResult createNegativeResult() {
        return NegativeTemplateLookupResult.INSTANCE;
    }

    static TemplateLookupResult from(String templateSourceName, Object templateSource) {
        return templateSource != null ? new PositiveTemplateLookupResult(templateSourceName, templateSource) : TemplateLookupResult.createNegativeResult();
    }

    private TemplateLookupResult() {
    }

    public abstract String getTemplateSourceName();

    public abstract boolean isPositive();

    abstract Object getTemplateSource();

    private static final class NegativeTemplateLookupResult
    extends TemplateLookupResult {
        private static final NegativeTemplateLookupResult INSTANCE = new NegativeTemplateLookupResult();

        private NegativeTemplateLookupResult() {
        }

        @Override
        public String getTemplateSourceName() {
            return null;
        }

        @Override
        Object getTemplateSource() {
            return null;
        }

        @Override
        public boolean isPositive() {
            return false;
        }
    }

    private static final class PositiveTemplateLookupResult
    extends TemplateLookupResult {
        private final String templateSourceName;
        private final Object templateSource;

        private PositiveTemplateLookupResult(String templateSourceName, Object templateSource) {
            NullArgumentException.check("templateName", templateSourceName);
            NullArgumentException.check("templateSource", templateSource);
            if (templateSource instanceof TemplateLookupResult) {
                throw new IllegalArgumentException();
            }
            this.templateSourceName = templateSourceName;
            this.templateSource = templateSource;
        }

        @Override
        public String getTemplateSourceName() {
            return this.templateSourceName;
        }

        @Override
        Object getTemplateSource() {
            return this.templateSource;
        }

        @Override
        public boolean isPositive() {
            return true;
        }
    }
}

