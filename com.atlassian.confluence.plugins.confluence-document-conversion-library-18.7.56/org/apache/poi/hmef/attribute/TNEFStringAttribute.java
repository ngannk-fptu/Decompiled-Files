/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.attribute;

import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hmef.attribute.TNEFAttribute;
import org.apache.poi.util.StringUtil;

public final class TNEFStringAttribute
extends TNEFAttribute {
    private static final Logger LOG = LogManager.getLogger(TNEFStringAttribute.class);
    private final String data;

    protected TNEFStringAttribute(int id, int type, InputStream inp) throws IOException {
        super(id, type, inp);
        String tmpData = null;
        byte[] data = this.getData();
        tmpData = this.getType() == 2 ? StringUtil.getFromUnicodeLE(data) : StringUtil.getFromCompressedUnicode(data, 0, data.length);
        if (tmpData.endsWith("\u0000")) {
            tmpData = tmpData.substring(0, tmpData.length() - 1);
        }
        this.data = tmpData;
    }

    public String getString() {
        return this.data;
    }

    @Override
    public String toString() {
        return "Attribute " + this.getProperty() + ", type=" + this.getType() + ", data=" + this.getString();
    }

    public static String getAsString(TNEFAttribute attr) {
        if (attr == null) {
            return null;
        }
        if (attr instanceof TNEFStringAttribute) {
            return ((TNEFStringAttribute)attr).getString();
        }
        LOG.atWarn().log("Warning, non string property found: {}", (Object)attr);
        return null;
    }
}

