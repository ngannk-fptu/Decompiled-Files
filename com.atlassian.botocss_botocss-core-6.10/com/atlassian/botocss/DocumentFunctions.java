/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jsoup.nodes.Document
 */
package com.atlassian.botocss;

import java.util.function.Function;
import org.jsoup.nodes.Document;

public enum DocumentFunctions implements Function<Document, Document>
{
    NOOP,
    PRETTY_PRINT(document -> {
        document.outputSettings().prettyPrint(true);
        document.outputSettings().indentAmount(4);
        return null;
    }),
    ZERO_INDENT(document -> {
        document.outputSettings().indentAmount(0);
        return null;
    });

    private Function<Document, Document> delegate = Function.identity();

    private DocumentFunctions() {
    }

    private DocumentFunctions(Function<Document, Document> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Document apply(Document document) {
        return this.delegate.apply(document);
    }
}

