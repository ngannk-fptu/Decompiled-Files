/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.hslf.record.AnimationInfo;
import org.apache.poi.hslf.record.AnimationInfoAtom;
import org.apache.poi.hslf.record.ExMCIMovie;
import org.apache.poi.hslf.record.ExObjList;
import org.apache.poi.hslf.record.ExObjRefAtom;
import org.apache.poi.hslf.record.ExVideoContainer;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.InteractiveInfo;
import org.apache.poi.hslf.record.InteractiveInfoAtom;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.ShapeContainer;

public final class MovieShape
extends HSLFPictureShape {
    public static final int DEFAULT_MOVIE_THUMBNAIL = -1;
    public static final int MOVIE_MPEG = 1;
    public static final int MOVIE_AVI = 2;

    public MovieShape(int movieIdx, HSLFPictureData pictureData) {
        super(pictureData, null);
        this.setMovieIndex(movieIdx);
        this.setAutoPlay(true);
    }

    public MovieShape(int movieIdx, HSLFPictureData pictureData, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(pictureData, parent);
        this.setMovieIndex(movieIdx);
    }

    public MovieShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    @Override
    protected EscherContainerRecord createSpContainer(int idx, boolean isChild) {
        EscherContainerRecord ecr = super.createSpContainer(idx, isChild);
        this.setEscherProperty(EscherPropertyTypes.PROTECTION__LOCKAGAINSTGROUPING, 0x1000100);
        this.setEscherProperty(EscherPropertyTypes.FILL__NOFILLHITTEST, 65537);
        ExObjRefAtom oe = new ExObjRefAtom();
        InteractiveInfo info = new InteractiveInfo();
        InteractiveInfoAtom infoAtom = info.getInteractiveInfoAtom();
        infoAtom.setAction((byte)6);
        infoAtom.setHyperlinkType((byte)-1);
        AnimationInfo an = new AnimationInfo();
        AnimationInfoAtom anAtom = an.getAnimationInfoAtom();
        anAtom.setFlag(4, true);
        HSLFEscherClientDataRecord cldata = this.getClientData(true);
        cldata.addChild(oe);
        cldata.addChild(an);
        cldata.addChild(info);
        return ecr;
    }

    public void setMovieIndex(int idx) {
        ExObjRefAtom oe = (ExObjRefAtom)this.getClientDataRecord(RecordTypes.ExObjRefAtom.typeID);
        oe.setExObjIdRef(idx);
        AnimationInfo an = (AnimationInfo)this.getClientDataRecord(RecordTypes.AnimationInfo.typeID);
        if (an != null) {
            AnimationInfoAtom ai = an.getAnimationInfoAtom();
            ai.setDimColor(0x7000000);
            ai.setFlag(4, true);
            ai.setFlag(256, true);
            ai.setFlag(1024, true);
            ai.setOrderID(idx + 1);
        }
    }

    public void setAutoPlay(boolean flag) {
        AnimationInfo an = (AnimationInfo)this.getClientDataRecord(RecordTypes.AnimationInfo.typeID);
        if (an != null) {
            an.getAnimationInfoAtom().setFlag(4, flag);
        }
    }

    public boolean isAutoPlay() {
        AnimationInfo an = (AnimationInfo)this.getClientDataRecord(RecordTypes.AnimationInfo.typeID);
        if (an != null) {
            return an.getAnimationInfoAtom().getFlag(4);
        }
        return false;
    }

    public String getPath() {
        Record[] r;
        ExObjRefAtom oe = (ExObjRefAtom)this.getClientDataRecord(RecordTypes.ExObjRefAtom.typeID);
        int idx = oe.getExObjIdRef();
        HSLFSlideShow ppt = this.getSheet().getSlideShow();
        ExObjList lst = (ExObjList)ppt.getDocumentRecord().findFirstOfType(RecordTypes.ExObjList.typeID);
        if (lst == null) {
            return null;
        }
        for (Record record : r = lst.getChildRecords()) {
            ExMCIMovie mci;
            ExVideoContainer exVideo;
            int objectId;
            if (!(record instanceof ExMCIMovie) || (objectId = (exVideo = (mci = (ExMCIMovie)record).getExVideo()).getExMediaAtom().getObjectId()) != idx) continue;
            return exVideo.getPathAtom().getText();
        }
        return null;
    }
}

