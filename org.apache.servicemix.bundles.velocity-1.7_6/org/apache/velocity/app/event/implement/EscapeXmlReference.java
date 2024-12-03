/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringEscapeUtils
 */
package org.apache.velocity.app.event.implement;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.app.event.implement.EscapeReference;

public class EscapeXmlReference
extends EscapeReference {
    protected String escape(Object text) {
        return StringEscapeUtils.escapeXml((String)text.toString());
    }

    protected String getMatchAttribute() {
        return "eventhandler.escape.xml.match";
    }
}

