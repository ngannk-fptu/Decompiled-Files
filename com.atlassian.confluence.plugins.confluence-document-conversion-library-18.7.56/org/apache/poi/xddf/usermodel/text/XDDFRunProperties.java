/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.text;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFEffectContainer;
import org.apache.poi.xddf.usermodel.XDDFEffectList;
import org.apache.poi.xddf.usermodel.XDDFExtensionList;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.apache.poi.xddf.usermodel.XDDFGradientFillProperties;
import org.apache.poi.xddf.usermodel.XDDFGroupFillProperties;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFNoFillProperties;
import org.apache.poi.xddf.usermodel.XDDFPatternFillProperties;
import org.apache.poi.xddf.usermodel.XDDFPictureFillProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.text.CapsType;
import org.apache.poi.xddf.usermodel.text.StrikeType;
import org.apache.poi.xddf.usermodel.text.UnderlineType;
import org.apache.poi.xddf.usermodel.text.XDDFFont;
import org.apache.poi.xddf.usermodel.text.XDDFHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;

public class XDDFRunProperties {
    private CTTextCharacterProperties props;

    public XDDFRunProperties() {
        this(CTTextCharacterProperties.Factory.newInstance());
    }

    @Internal
    public XDDFRunProperties(CTTextCharacterProperties properties) {
        this.props = properties;
    }

    @Internal
    protected CTTextCharacterProperties getXmlObject() {
        return this.props;
    }

    public void setBaseline(Integer value) {
        XDDFRunProperties.update(this.props::isSetBaseline, this.props::unsetBaseline, this.props::setBaseline, value);
    }

    public void setDirty(Boolean dirty) {
        XDDFRunProperties.update(this.props::isSetDirty, this.props::unsetDirty, this.props::setDirty, dirty);
    }

    public void setSpellError(Boolean error) {
        XDDFRunProperties.update(this.props::isSetErr, this.props::unsetErr, this.props::setErr, error);
    }

    public void setNoProof(Boolean noproof) {
        XDDFRunProperties.update(this.props::isSetNoProof, this.props::unsetNoProof, this.props::setNoProof, noproof);
    }

    public void setNormalizeHeights(Boolean normalize) {
        XDDFRunProperties.update(this.props::isSetNormalizeH, this.props::unsetNormalizeH, this.props::setNormalizeH, normalize);
    }

    public void setKumimoji(Boolean kumimoji) {
        XDDFRunProperties.update(this.props::isSetKumimoji, this.props::unsetKumimoji, this.props::setKumimoji, kumimoji);
    }

    public void setBold(Boolean bold) {
        XDDFRunProperties.update(this.props::isSetB, this.props::unsetB, this.props::setB, bold);
    }

    public void setItalic(Boolean italic) {
        XDDFRunProperties.update(this.props::isSetI, this.props::unsetI, this.props::setI, italic);
    }

    public void setFontSize(Double size) {
        if (size != null && (size < 1.0 || 400.0 < size)) {
            throw new IllegalArgumentException("Minimum inclusive = 1. Maximum inclusive = 400.");
        }
        XDDFRunProperties.update(this.props::isSetSz, this.props::unsetSz, this.props::setSz, size == null ? null : Integer.valueOf((int)(100.0 * size)));
    }

    public void setFillProperties(XDDFFillProperties properties) {
        if (this.props.isSetBlipFill()) {
            this.props.unsetBlipFill();
        }
        if (this.props.isSetGradFill()) {
            this.props.unsetGradFill();
        }
        if (this.props.isSetGrpFill()) {
            this.props.unsetGrpFill();
        }
        if (this.props.isSetNoFill()) {
            this.props.unsetNoFill();
        }
        if (this.props.isSetPattFill()) {
            this.props.unsetPattFill();
        }
        if (this.props.isSetSolidFill()) {
            this.props.unsetSolidFill();
        }
        if (properties == null) {
            return;
        }
        if (properties instanceof XDDFGradientFillProperties) {
            this.props.setGradFill(((XDDFGradientFillProperties)properties).getXmlObject());
        } else if (properties instanceof XDDFGroupFillProperties) {
            this.props.setGrpFill(((XDDFGroupFillProperties)properties).getXmlObject());
        } else if (properties instanceof XDDFNoFillProperties) {
            this.props.setNoFill(((XDDFNoFillProperties)properties).getXmlObject());
        } else if (properties instanceof XDDFPatternFillProperties) {
            this.props.setPattFill(((XDDFPatternFillProperties)properties).getXmlObject());
        } else if (properties instanceof XDDFPictureFillProperties) {
            this.props.setBlipFill(((XDDFPictureFillProperties)properties).getXmlObject());
        } else if (properties instanceof XDDFSolidFillProperties) {
            this.props.setSolidFill(((XDDFSolidFillProperties)properties).getXmlObject());
        }
    }

    public void setCharacterKerning(Double kerning) {
        if (kerning != null && (kerning < 0.0 || 4000.0 < kerning)) {
            throw new IllegalArgumentException("Minimum inclusive = 0. Maximum inclusive = 4000.");
        }
        XDDFRunProperties.update(this.props::isSetKern, this.props::unsetKern, this.props::setKern, kerning == null ? null : Integer.valueOf((int)(100.0 * kerning)));
    }

    public void setCharacterSpacing(Double spacing) {
        if (spacing != null && (spacing < -4000.0 || 4000.0 < spacing)) {
            throw new IllegalArgumentException("Minimum inclusive = -4000. Maximum inclusive = 4000.");
        }
        XDDFRunProperties.update(this.props::isSetSpc, this.props::unsetSpc, this.props::setSpc, spacing == null ? null : Integer.valueOf((int)(100.0 * spacing)));
    }

    public void setFonts(XDDFFont[] fonts) {
        block6: for (XDDFFont font : fonts) {
            CTTextFont xml = font.getXmlObject();
            switch (font.getGroup()) {
                case COMPLEX_SCRIPT: {
                    XDDFRunProperties.update(this.props::isSetCs, this.props::unsetCs, this.props::setCs, xml);
                    continue block6;
                }
                case EAST_ASIAN: {
                    XDDFRunProperties.update(this.props::isSetEa, this.props::unsetEa, this.props::setEa, xml);
                    continue block6;
                }
                case LATIN: {
                    XDDFRunProperties.update(this.props::isSetLatin, this.props::unsetLatin, this.props::setLatin, xml);
                    continue block6;
                }
                case SYMBOL: {
                    XDDFRunProperties.update(this.props::isSetSym, this.props::unsetSym, this.props::setSym, xml);
                }
            }
        }
    }

    public void setUnderline(UnderlineType underline) {
        XDDFRunProperties.update(this.props::isSetU, this.props::unsetU, this.props::setU, underline == null ? null : underline.underlying);
    }

    public void setStrikeThrough(StrikeType strike) {
        XDDFRunProperties.update(this.props::isSetStrike, this.props::unsetStrike, this.props::setStrike, strike == null ? null : strike.underlying);
    }

    public void setCapitals(CapsType caps) {
        XDDFRunProperties.update(this.props::isSetCap, this.props::unsetCap, this.props::setCap, caps == null ? null : caps.underlying);
    }

    public void setHyperlink(XDDFHyperlink link) {
        XDDFRunProperties.update(this.props::isSetHlinkClick, this.props::unsetHlinkClick, this.props::setHlinkClick, link == null ? null : link.getXmlObject());
    }

    public void setMouseOver(XDDFHyperlink link) {
        XDDFRunProperties.update(this.props::isSetHlinkMouseOver, this.props::unsetHlinkMouseOver, this.props::setHlinkMouseOver, link == null ? null : link.getXmlObject());
    }

    public void setLanguage(Locale lang) {
        XDDFRunProperties.update(this.props::isSetLang, this.props::unsetLang, this.props::setLang, lang == null ? null : lang.toLanguageTag());
    }

    public void setAlternativeLanguage(Locale lang) {
        XDDFRunProperties.update(this.props::isSetAltLang, this.props::unsetAltLang, this.props::setAltLang, lang == null ? null : lang.toLanguageTag());
    }

    public void setHighlight(XDDFColor color) {
        XDDFRunProperties.update(this.props::isSetHighlight, this.props::unsetHighlight, this.props::setHighlight, color == null ? null : color.getColorContainer());
    }

    public void setLineProperties(XDDFLineProperties properties) {
        XDDFRunProperties.update(this.props::isSetLn, this.props::unsetLn, this.props::setLn, properties == null ? null : properties.getXmlObject());
    }

    public void setBookmark(String bookmark) {
        XDDFRunProperties.update(this.props::isSetBmk, this.props::unsetBmk, this.props::setBmk, bookmark);
    }

    public XDDFExtensionList getExtensionList() {
        if (this.props.isSetExtLst()) {
            return new XDDFExtensionList(this.props.getExtLst());
        }
        return null;
    }

    public void setExtensionList(XDDFExtensionList list) {
        XDDFRunProperties.update(this.props::isSetExtLst, this.props::unsetExtLst, this.props::setExtLst, list == null ? null : list.getXmlObject());
    }

    public XDDFEffectContainer getEffectContainer() {
        if (this.props.isSetEffectDag()) {
            return new XDDFEffectContainer(this.props.getEffectDag());
        }
        return null;
    }

    public void setEffectContainer(XDDFEffectContainer container) {
        XDDFRunProperties.update(this.props::isSetEffectDag, this.props::unsetEffectDag, this.props::setEffectDag, container == null ? null : container.getXmlObject());
    }

    public XDDFEffectList getEffectList() {
        if (this.props.isSetEffectLst()) {
            return new XDDFEffectList(this.props.getEffectLst());
        }
        return null;
    }

    public void setEffectList(XDDFEffectList list) {
        XDDFRunProperties.update(this.props::isSetEffectLst, this.props::unsetEffectLst, this.props::setEffectLst, list == null ? null : list.getXmlObject());
    }

    private static <T> void update(Supplier<Boolean> isSet, Runnable unset, Consumer<T> setter, T val) {
        if (val != null) {
            setter.accept(val);
        } else if (isSet.get().booleanValue()) {
            unset.run();
        }
    }
}

