/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.image;

import java.awt.Dimension;
import java.awt.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

@Internal
public class ImageHeaderEMF {
    private static final Logger LOG = LogManager.getLogger(ImageHeaderEMF.class);
    private static final String EMF_SIGNATURE = " EMF";
    private final Rectangle deviceBounds;

    public ImageHeaderEMF(byte[] data, int off) {
        int offset = off;
        int type = (int)LittleEndian.getUInt(data, offset);
        offset += 4;
        if (type != 1) {
            LOG.atWarn().log("Invalid EMF picture - invalid type");
            this.deviceBounds = new Rectangle(0, 0, 200, 200);
            return;
        }
        int left = LittleEndian.getInt(data, offset += 4);
        int top = LittleEndian.getInt(data, offset += 4);
        int right = LittleEndian.getInt(data, offset += 4);
        int bottom = LittleEndian.getInt(data, offset += 4);
        offset += 4;
        this.deviceBounds = new Rectangle(left, top, right - left == -1 ? 0 : right - left, bottom - top == -1 ? 0 : bottom - top);
        String signature = new String(data, offset += 16, EMF_SIGNATURE.length(), LocaleUtil.CHARSET_1252);
        if (!EMF_SIGNATURE.equals(signature)) {
            LOG.atWarn().log("Invalid EMF picture - invalid signature");
        }
    }

    public Dimension getSize() {
        return this.deviceBounds.getSize();
    }

    public Rectangle getBounds() {
        return this.deviceBounds;
    }
}

