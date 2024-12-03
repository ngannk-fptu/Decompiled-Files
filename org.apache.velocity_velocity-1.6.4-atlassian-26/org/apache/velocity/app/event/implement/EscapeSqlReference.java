/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.app.event.implement;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.event.implement.EscapeReference;

public class EscapeSqlReference
extends EscapeReference {
    @Override
    protected String escape(Object text) {
        String str = text == null ? null : text.toString();
        return str == null ? null : StringUtils.replace((String)str, (String)"'", (String)"''");
    }

    @Override
    protected String getMatchAttribute() {
        return "eventhandler.escape.sql.match";
    }
}

