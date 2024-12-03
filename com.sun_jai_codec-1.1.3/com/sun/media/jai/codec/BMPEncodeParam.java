/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.JaiI18N;

public class BMPEncodeParam
implements ImageEncodeParam {
    public static final int VERSION_2 = 0;
    public static final int VERSION_3 = 1;
    public static final int VERSION_4 = 2;
    private int version = 1;
    private boolean compressed = false;
    private boolean topDown = false;

    public void setVersion(int versionNumber) {
        this.checkVersion(versionNumber);
        this.version = versionNumber;
    }

    public int getVersion() {
        return this.version;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public boolean isCompressed() {
        return this.compressed;
    }

    public void setTopDown(boolean topDown) {
        this.topDown = topDown;
    }

    public boolean isTopDown() {
        return this.topDown;
    }

    private void checkVersion(int versionNumber) {
        if (versionNumber != 0 && versionNumber != 1 && versionNumber != 2) {
            throw new RuntimeException(JaiI18N.getString("BMPEncodeParam0"));
        }
    }
}

