/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateElement;
import java.util.Collection;
import java.util.Collections;

class TemplateElementsToVisit {
    private final Collection<TemplateElement> templateElements;

    TemplateElementsToVisit(Collection<TemplateElement> templateElements) {
        this.templateElements = null != templateElements ? templateElements : Collections.emptyList();
    }

    TemplateElementsToVisit(TemplateElement nestedBlock) {
        this(Collections.singleton(nestedBlock));
    }

    Collection<TemplateElement> getTemplateElements() {
        return this.templateElements;
    }
}

