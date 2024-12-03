/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cz.vutbr.web.css.Declaration
 */
package com.atlassian.botocss;

import cz.vutbr.web.css.Declaration;

final class BotocssExpansion {
    private final String attributeName;
    private final DeclarationProcessor processor;

    BotocssExpansion(String attributeName, DeclarationProcessor processor) {
        this.attributeName = attributeName;
        this.processor = processor;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public DeclarationProcessor getProcessor() {
        return this.processor;
    }

    static interface DeclarationProcessor {
        public String parse(Declaration var1);
    }
}

