/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.blip;

import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.blip.Bitmap;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;

public final class JPEG
extends Bitmap {
    private ColorSpace colorSpace = ColorSpace.rgb;

    @Deprecated
    @Removal(version="5.3")
    public JPEG() {
        this(new EscherContainerRecord(), new EscherBSERecord());
    }

    @Internal
    public JPEG(EscherContainerRecord recordContainer, EscherBSERecord bse) {
        super(recordContainer, bse);
    }

    @Override
    public PictureData.PictureType getType() {
        return PictureData.PictureType.JPEG;
    }

    public ColorSpace getColorSpace() {
        return this.colorSpace;
    }

    public void setColorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }

    @Override
    public int getSignature() {
        return this.colorSpace == ColorSpace.rgb ? (this.getUIDInstanceCount() == 1 ? 18080 : 18096) : (this.getUIDInstanceCount() == 1 ? 28192 : 28208);
    }

    @Override
    public void setSignature(int signature) {
        switch (signature) {
            case 18080: {
                this.setUIDInstanceCount(1);
                this.colorSpace = ColorSpace.rgb;
                break;
            }
            case 18096: {
                this.setUIDInstanceCount(2);
                this.colorSpace = ColorSpace.rgb;
                break;
            }
            case 28192: {
                this.setUIDInstanceCount(1);
                this.colorSpace = ColorSpace.cymk;
                break;
            }
            case 28208: {
                this.setUIDInstanceCount(2);
                this.colorSpace = ColorSpace.cymk;
                break;
            }
            default: {
                throw new IllegalArgumentException(signature + " is not a valid instance/signature value for JPEG");
            }
        }
    }

    public static enum ColorSpace {
        rgb,
        cymk;

    }
}

