/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.test.ElementReplacer;

public class DelegatingReplacedElementFactory
implements ReplacedElementFactory {
    private final List replacers = new ArrayList();
    private final Map byNameReplacers;
    private final List elementReplacements = new ArrayList();

    public DelegatingReplacedElementFactory() {
        this.byNameReplacers = new HashMap();
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext context, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        ElementReplacer nameReplacer = (ElementReplacer)this.byNameReplacers.get(box.getElement().getNodeName());
        if (nameReplacer != null) {
            return this.replaceUsing(context, box, uac, cssWidth, cssHeight, nameReplacer);
        }
        for (ElementReplacer replacer : this.replacers) {
            if (!replacer.accept(context, box.getElement())) continue;
            return this.replaceUsing(context, box, uac, cssWidth, cssHeight, replacer);
        }
        return null;
    }

    private ReplacedElement replaceUsing(LayoutContext context, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight, ElementReplacer replacer) {
        ReplacedElement re = replacer.replace(context, box, uac, cssWidth, cssHeight);
        this.elementReplacements.add(new ERItem(box.getElement(), re, replacer));
        return re;
    }

    @Override
    public void reset() {
        System.out.println("\n\n***Factory reset()");
        this.elementReplacements.clear();
        for (ElementReplacer elementReplacer : this.replacers) {
            elementReplacer.reset();
        }
        Iterator<Object> iterator = this.byNameReplacers.values().iterator();
        while (iterator.hasNext()) {
            ((ElementReplacer)iterator.next()).reset();
        }
    }

    @Override
    public void remove(Element element) {
        int idx = this.elementReplacements.indexOf(element);
        ERItem item = (ERItem)this.elementReplacements.get(idx);
        this.elementReplacements.remove(idx);
        item.elementReplacer.clear(element);
    }

    public ElementReplacer addReplacer(ElementReplacer replacer) {
        if (replacer.isElementNameMatch()) {
            this.byNameReplacers.put(replacer.getElementNameMatch(), replacer);
        } else {
            this.replacers.add(replacer);
        }
        return replacer;
    }

    public void removeReplacer(ElementReplacer replacer) {
        this.replacers.remove(replacer);
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
    }

    private static class ERItem {
        private final Element element;
        private final ReplacedElement replacedElement;
        private final ElementReplacer elementReplacer;

        private ERItem(Element e, ReplacedElement re, ElementReplacer er) {
            this.element = e;
            this.replacedElement = re;
            this.elementReplacer = er;
        }

        public int hashCode() {
            return this.element.hashCode();
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ERItem)) {
                return false;
            }
            ERItem other = (ERItem)o;
            return other.element == this.element;
        }
    }
}

