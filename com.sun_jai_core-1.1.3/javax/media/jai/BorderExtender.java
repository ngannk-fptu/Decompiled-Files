/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.image.WritableRaster;
import java.io.Serializable;
import javax.media.jai.BorderExtenderCopy;
import javax.media.jai.BorderExtenderReflect;
import javax.media.jai.BorderExtenderWrap;
import javax.media.jai.BorderExtenderZero;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;

public abstract class BorderExtender
implements Serializable {
    public static final int BORDER_ZERO = 0;
    public static final int BORDER_COPY = 1;
    public static final int BORDER_REFLECT = 2;
    public static final int BORDER_WRAP = 3;
    private static BorderExtender borderExtenderZero = null;
    private static BorderExtender borderExtenderCopy = null;
    private static BorderExtender borderExtenderReflect = null;
    private static BorderExtender borderExtenderWrap = null;

    public abstract void extend(WritableRaster var1, PlanarImage var2);

    public static BorderExtender createInstance(int extenderType) {
        switch (extenderType) {
            case 0: {
                if (borderExtenderZero == null) {
                    borderExtenderZero = new BorderExtenderZero();
                }
                return borderExtenderZero;
            }
            case 1: {
                if (borderExtenderCopy == null) {
                    borderExtenderCopy = new BorderExtenderCopy();
                }
                return borderExtenderCopy;
            }
            case 2: {
                if (borderExtenderReflect == null) {
                    borderExtenderReflect = new BorderExtenderReflect();
                }
                return borderExtenderReflect;
            }
            case 3: {
                if (borderExtenderWrap == null) {
                    borderExtenderWrap = new BorderExtenderWrap();
                }
                return borderExtenderWrap;
            }
        }
        throw new IllegalArgumentException(JaiI18N.getString("BorderExtender0"));
    }
}

