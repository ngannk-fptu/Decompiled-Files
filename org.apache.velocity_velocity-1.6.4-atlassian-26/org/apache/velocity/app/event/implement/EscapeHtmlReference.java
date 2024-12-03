/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 */
package org.apache.velocity.app.event.implement;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.app.event.implement.EscapeReference;

public class EscapeHtmlReference
extends EscapeReference {
    @Override
    protected String escape(Object text) {
        return StringEscapeUtils.escapeHtml4((String)text.toString());
    }

    @Override
    protected String getMatchAttribute() {
        return "eventhandler.escape.html.match";
    }
}

