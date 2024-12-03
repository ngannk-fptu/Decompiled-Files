/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.header;

import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.header.Header;
import org.apache.jackrabbit.webdav.util.EncodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LabelHeader
implements Header {
    private static Logger log = LoggerFactory.getLogger(LabelHeader.class);
    private final String label;

    public LabelHeader(String label) {
        if (label == null) {
            throw new IllegalArgumentException("null is not a valid label.");
        }
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String getHeaderName() {
        return "Label";
    }

    @Override
    public String getHeaderValue() {
        return EncodeUtil.escape(this.label);
    }

    public static LabelHeader parse(WebdavRequest request) {
        String hv = request.getHeader("Label");
        if (hv == null) {
            return null;
        }
        return new LabelHeader(EncodeUtil.unescape(hv));
    }
}

