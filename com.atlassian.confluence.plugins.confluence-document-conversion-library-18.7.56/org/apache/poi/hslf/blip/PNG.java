/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.blip;

import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hslf.blip.Bitmap;
import org.apache.poi.sl.image.ImageHeaderPNG;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;

public final class PNG
extends Bitmap {
    @Deprecated
    @Removal(version="5.3")
    public PNG() {
        this(new EscherContainerRecord(), new EscherBSERecord());
    }

    @Internal
    public PNG(EscherContainerRecord recordContainer, EscherBSERecord bse) {
        super(recordContainer, bse);
    }

    @Override
    public byte[] getData() {
        return new ImageHeaderPNG(super.getData()).extractPNG();
    }

    @Override
    public PictureData.PictureType getType() {
        return PictureData.PictureType.PNG;
    }

    @Override
    public int getSignature() {
        return this.getUIDInstanceCount() == 1 ? 28160 : 28176;
    }

    @Override
    public void setSignature(int signature) {
        switch (signature) {
            case 28160: {
                this.setUIDInstanceCount(1);
                break;
            }
            case 28176: {
                this.setUIDInstanceCount(2);
                break;
            }
            default: {
                throw new IllegalArgumentException(signature + " is not a valid instance/signature value for PNG");
            }
        }
    }
}

