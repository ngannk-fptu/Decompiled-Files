/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.api;

import java.util.Set;
import java.util.TreeSet;

public interface EmptyElementHandler {
    public boolean allowEmptyElement(String var1, String var2, String var3, boolean var4);

    public static class HtmlEmptyElementHandler
    extends SetEmptyElementHandler {
        private static final HtmlEmptyElementHandler sInstance = new HtmlEmptyElementHandler();

        public static HtmlEmptyElementHandler getInstance() {
            return sInstance;
        }

        protected HtmlEmptyElementHandler() {
            super(new TreeSet(String.CASE_INSENSITIVE_ORDER));
            this.mEmptyElements.add("area");
            this.mEmptyElements.add("base");
            this.mEmptyElements.add("basefont");
            this.mEmptyElements.add("br");
            this.mEmptyElements.add("col");
            this.mEmptyElements.add("frame");
            this.mEmptyElements.add("hr");
            this.mEmptyElements.add("input");
            this.mEmptyElements.add("img");
            this.mEmptyElements.add("isindex");
            this.mEmptyElements.add("link");
            this.mEmptyElements.add("meta");
            this.mEmptyElements.add("param");
        }
    }

    public static class SetEmptyElementHandler
    implements EmptyElementHandler {
        protected final Set mEmptyElements;

        public SetEmptyElementHandler(Set emptyElements) {
            this.mEmptyElements = emptyElements;
        }

        public boolean allowEmptyElement(String prefix, String localName, String nsURI, boolean allowEmpty) {
            return this.mEmptyElements.contains(localName);
        }
    }
}

