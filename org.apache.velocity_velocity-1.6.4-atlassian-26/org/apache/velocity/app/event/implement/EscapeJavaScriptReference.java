/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 */
package org.apache.velocity.app.event.implement;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.app.event.implement.EscapeReference;

public class EscapeJavaScriptReference
extends EscapeReference {
    @Override
    protected String escape(Object text) {
        return StringEscapeUtils.escapeEcmaScript((String)text.toString());
    }

    @Override
    protected String getMatchAttribute() {
        return "eventhandler.escape.javascript.match";
    }
}

