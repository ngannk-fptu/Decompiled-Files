/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface TemplateHashModelEx2
extends TemplateHashModelEx {
    public KeyValuePairIterator keyValuePairIterator() throws TemplateModelException;

    public static interface KeyValuePairIterator {
        public boolean hasNext() throws TemplateModelException;

        public KeyValuePair next() throws TemplateModelException;
    }

    public static interface KeyValuePair {
        public TemplateModel getKey() throws TemplateModelException;

        public TemplateModel getValue() throws TemplateModelException;
    }
}

