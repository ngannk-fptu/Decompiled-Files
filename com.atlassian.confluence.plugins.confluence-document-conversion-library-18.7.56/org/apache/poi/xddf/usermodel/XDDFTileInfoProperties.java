/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.RectangleAlignment;
import org.apache.poi.xddf.usermodel.TileFlipMode;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTileInfoProperties;

public class XDDFTileInfoProperties {
    private CTTileInfoProperties props;

    protected XDDFTileInfoProperties(CTTileInfoProperties properties) {
        this.props = properties;
    }

    @Internal
    protected CTTileInfoProperties getXmlObject() {
        return this.props;
    }

    public void setAlignment(RectangleAlignment alignment) {
        if (alignment == null) {
            if (this.props.isSetAlgn()) {
                this.props.unsetAlgn();
            }
        } else {
            this.props.setAlgn(alignment.underlying);
        }
    }

    public TileFlipMode getFlipMode() {
        if (this.props.isSetFlip()) {
            return TileFlipMode.valueOf(this.props.getFlip());
        }
        return null;
    }

    public void setFlipMode(TileFlipMode mode) {
        if (mode == null) {
            if (this.props.isSetFlip()) {
                this.props.unsetFlip();
            }
        } else {
            this.props.setFlip(mode.underlying);
        }
    }

    public Integer getSx() {
        if (this.props.isSetSx()) {
            return POIXMLUnits.parsePercent(this.props.xgetSx());
        }
        return null;
    }

    public void setSx(Integer value) {
        if (value == null) {
            if (this.props.isSetSx()) {
                this.props.unsetSx();
            }
        } else {
            this.props.setSx(value);
        }
    }

    public Integer getSy() {
        if (this.props.isSetSy()) {
            return POIXMLUnits.parsePercent(this.props.xgetSy());
        }
        return null;
    }

    public void setSy(Integer value) {
        if (value == null) {
            if (this.props.isSetSy()) {
                this.props.unsetSy();
            }
        } else {
            this.props.setSy(value);
        }
    }

    public Long getTx() {
        if (this.props.isSetTx()) {
            return POIXMLUnits.parseLength(this.props.xgetTx());
        }
        return null;
    }

    public void setTx(Long value) {
        if (value == null) {
            if (this.props.isSetTx()) {
                this.props.unsetTx();
            }
        } else {
            this.props.setTx(value);
        }
    }

    public Long getTy() {
        if (this.props.isSetTy()) {
            return POIXMLUnits.parseLength(this.props.xgetTy());
        }
        return null;
    }

    public void setTy(Long value) {
        if (value == null) {
            if (this.props.isSetTy()) {
                this.props.unsetTy();
            }
        } else {
            this.props.setTy(value);
        }
    }
}

