/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.model.types.PAPAbstractType;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.DropCapSpecifier;
import org.apache.poi.hwpf.usermodel.LineSpacingDescriptor;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;

public final class ParagraphProperties
extends PAPAbstractType
implements Duplicatable {
    private boolean jcLogical;

    public ParagraphProperties() {
        this.setAnld(new byte[84]);
        this.setPhe(new byte[12]);
    }

    public ParagraphProperties(ParagraphProperties other) {
        super(other);
        this.jcLogical = other.jcLogical;
    }

    @Override
    public ParagraphProperties copy() {
        return new ParagraphProperties(this);
    }

    public BorderCode getBarBorder() {
        return super.getBrcBar();
    }

    public BorderCode getBottomBorder() {
        return super.getBrcBottom();
    }

    public DropCapSpecifier getDropCap() {
        return super.getDcs();
    }

    public int getFirstLineIndent() {
        return super.getDxaLeft1();
    }

    public int getFontAlignment() {
        return super.getWAlignFont();
    }

    public int getIndentFromLeft() {
        return super.getDxaLeft();
    }

    public int getIndentFromRight() {
        return super.getDxaRight();
    }

    public int getJustification() {
        if (this.jcLogical) {
            if (!this.getFBiDi()) {
                return this.getJc();
            }
            switch (this.getJc()) {
                case 0: {
                    return 2;
                }
                case 2: {
                    return 0;
                }
            }
            return this.getJc();
        }
        return this.getJc();
    }

    public BorderCode getLeftBorder() {
        return super.getBrcLeft();
    }

    public LineSpacingDescriptor getLineSpacing() {
        return super.getLspd();
    }

    public BorderCode getRightBorder() {
        return super.getBrcRight();
    }

    public ShadingDescriptor getShading() {
        return super.getShd();
    }

    public int getSpacingAfter() {
        return super.getDyaAfter();
    }

    public int getSpacingBefore() {
        return super.getDyaBefore();
    }

    public BorderCode getTopBorder() {
        return super.getBrcTop();
    }

    public boolean isAutoHyphenated() {
        return !super.getFNoAutoHyph();
    }

    public boolean isBackward() {
        return super.isFBackward();
    }

    public boolean isKinsoku() {
        return super.getFKinsoku();
    }

    public boolean isLineNotNumbered() {
        return super.getFNoLnn();
    }

    public boolean isSideBySide() {
        return super.getFSideBySide();
    }

    public boolean isVertical() {
        return super.isFVertical();
    }

    public boolean isWidowControlled() {
        return super.getFWidowControl();
    }

    public boolean isWordWrapped() {
        return super.getFWordWrap();
    }

    public boolean keepOnPage() {
        return super.getFKeep();
    }

    public boolean keepWithNext() {
        return super.getFKeepFollow();
    }

    public boolean pageBreakBefore() {
        return super.getFPageBreakBefore();
    }

    public void setAutoHyphenated(boolean auto) {
        super.setFNoAutoHyph(!auto);
    }

    public void setBackward(boolean bward) {
        super.setFBackward(bward);
    }

    public void setBarBorder(BorderCode bar) {
        super.setBrcBar(bar);
    }

    public void setBottomBorder(BorderCode bottom) {
        super.setBrcBottom(bottom);
    }

    public void setDropCap(DropCapSpecifier dcs) {
        super.setDcs(dcs);
    }

    public void setFirstLineIndent(int first) {
        super.setDxaLeft1(first);
    }

    public void setFontAlignment(int align) {
        super.setWAlignFont(align);
    }

    public void setIndentFromLeft(int dxaLeft) {
        super.setDxaLeft(dxaLeft);
    }

    public void setIndentFromRight(int dxaRight) {
        super.setDxaRight(dxaRight);
    }

    public void setJustification(byte jc) {
        super.setJc(jc);
        this.jcLogical = false;
    }

    public void setJustificationLogical(byte jc) {
        super.setJc(jc);
        this.jcLogical = true;
    }

    public void setKeepOnPage(boolean fKeep) {
        super.setFKeep(fKeep);
    }

    public void setKeepWithNext(boolean fKeepFollow) {
        super.setFKeepFollow(fKeepFollow);
    }

    public void setKinsoku(boolean kinsoku) {
        super.setFKinsoku(kinsoku);
    }

    public void setLeftBorder(BorderCode left) {
        super.setBrcLeft(left);
    }

    public void setLineNotNumbered(boolean fNoLnn) {
        super.setFNoLnn(fNoLnn);
    }

    public void setLineSpacing(LineSpacingDescriptor lspd) {
        super.setLspd(lspd);
    }

    public void setPageBreakBefore(boolean fPageBreak) {
        super.setFPageBreakBefore(fPageBreak);
    }

    public void setRightBorder(BorderCode right) {
        super.setBrcRight(right);
    }

    public void setShading(ShadingDescriptor shd) {
        super.setShd(shd);
    }

    public void setSideBySide(boolean fSideBySide) {
        super.setFSideBySide(fSideBySide);
    }

    public void setSpacingAfter(int after) {
        super.setDyaAfter(after);
    }

    public void setSpacingBefore(int before) {
        super.setDyaBefore(before);
    }

    public void setTopBorder(BorderCode top) {
        super.setBrcTop(top);
    }

    public void setVertical(boolean vertical) {
        super.setFVertical(vertical);
    }

    public void setWidowControl(boolean widowControl) {
        super.setFWidowControl(widowControl);
    }

    public void setWordWrapped(boolean wrap) {
        super.setFWordWrap(wrap);
    }
}

