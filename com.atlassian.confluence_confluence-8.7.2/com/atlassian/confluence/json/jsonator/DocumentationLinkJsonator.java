/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonString;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationLink;

public class DocumentationLinkJsonator
implements Jsonator<DocumentationLink> {
    private final DocumentationBean documentationBean;

    public DocumentationLinkJsonator(DocumentationBean documentationBean) {
        this.documentationBean = documentationBean;
    }

    @Override
    public Json convert(DocumentationLink m) {
        String value = this.documentationBean.getLink(m.getKey());
        return new JsonString(value);
    }
}

