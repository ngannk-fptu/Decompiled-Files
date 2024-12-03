/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package org.apache.velocity.app.event.implement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.app.event.implement.EscapeReference;

public class EscapeXmlReference
extends EscapeReference {
    @Override
    protected String escape(Object text) {
        return StringEscapeUtils.escapeXml((String)text.toString());
    }

    @Override
    protected String getMatchAttribute() {
        return "eventhandler.escape.xml.match";
    }
}

