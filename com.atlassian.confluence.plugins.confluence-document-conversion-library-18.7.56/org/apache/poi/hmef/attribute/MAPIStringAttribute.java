/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.attribute;

import java.nio.charset.Charset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hmef.attribute.MAPIRtfAttribute;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.StringUtil;

public final class MAPIStringAttribute
extends MAPIAttribute {
    private static final Logger LOG = LogManager.getLogger(MAPIStringAttribute.class);
    private static final String CODEPAGE = "CP1252";
    private final String data;

    public MAPIStringAttribute(MAPIProperty property, int type, byte[] data) {
        super(property, type, data);
        String tmpData = null;
        if (type == Types.ASCII_STRING.getId()) {
            tmpData = new String(data, Charset.forName(CODEPAGE));
        } else if (type == Types.UNICODE_STRING.getId()) {
            tmpData = StringUtil.getFromUnicodeLE(data);
        } else {
            throw new IllegalArgumentException("Not a string type " + type);
        }
        if (tmpData.endsWith("\u0000")) {
            tmpData = tmpData.substring(0, tmpData.length() - 1);
        }
        this.data = tmpData;
    }

    public String getDataString() {
        return this.data;
    }

    @Override
    public String toString() {
        return this.getProperty() + " " + this.data;
    }

    public static String getAsString(MAPIAttribute attr) {
        if (attr == null) {
            return null;
        }
        if (attr instanceof MAPIStringAttribute) {
            return ((MAPIStringAttribute)attr).getDataString();
        }
        if (attr instanceof MAPIRtfAttribute) {
            return ((MAPIRtfAttribute)attr).getDataString();
        }
        LOG.atWarn().log("Warning, non string property found: {}", (Object)attr);
        return null;
    }
}

