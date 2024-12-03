/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.AbstractByteArrayOutputStream
 *  org.apache.commons.io.output.ByteArrayOutputStream
 */
package org.apache.poi.hslf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.output.AbstractByteArrayOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hslf.record.ExEmbed;
import org.apache.poi.hslf.record.ExObjList;
import org.apache.poi.hslf.record.ExObjRefAtom;
import org.apache.poi.hslf.record.HSLFEscherClientDataRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.usermodel.HSLFObjectData;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.ObjectMetaData;
import org.apache.poi.sl.usermodel.ObjectShape;
import org.apache.poi.sl.usermodel.ShapeContainer;

public final class HSLFObjectShape
extends HSLFPictureShape
implements ObjectShape<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFObjectShape.class);
    private ExEmbed _exEmbed;

    public HSLFObjectShape(HSLFPictureData data) {
        super(data);
    }

    public HSLFObjectShape(HSLFPictureData data, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(data, parent);
    }

    public HSLFObjectShape(EscherContainerRecord escherRecord, ShapeContainer<HSLFShape, HSLFTextParagraph> parent) {
        super(escherRecord, parent);
    }

    public int getObjectID() {
        return this.getEscherProperty(EscherPropertyTypes.BLIP__PICTUREID);
    }

    public void setObjectID(int objectId) {
        this.setEscherProperty(EscherPropertyTypes.BLIP__PICTUREID, objectId);
        EscherContainerRecord ecr = this.getSpContainer();
        EscherSpRecord spRecord = (EscherSpRecord)ecr.getChildById(EscherSpRecord.RECORD_ID);
        if (spRecord != null) {
            spRecord.setFlags(spRecord.getFlags() | 0x10);
        } else {
            LOG.atWarn().log("Ole shape record not found.");
        }
        HSLFEscherClientDataRecord cldata = this.getClientData(true);
        ExObjRefAtom uer = null;
        for (Record record : cldata.getHSLFChildRecords()) {
            if (record.getRecordType() != (long)RecordTypes.ExObjRefAtom.typeID) continue;
            uer = (ExObjRefAtom)record;
            break;
        }
        if (uer == null) {
            uer = new ExObjRefAtom();
            cldata.addChild(uer);
        }
        uer.setExObjIdRef(objectId);
    }

    @Override
    public HSLFObjectData getObjectData() {
        HSLFSlideShow ppt = this.getSheet().getSlideShow();
        HSLFObjectData[] ole = ppt.getEmbeddedObjects();
        ExEmbed exEmbed = this.getExEmbed();
        HSLFObjectData data = null;
        if (exEmbed != null) {
            int ref = exEmbed.getExOleObjAtom().getObjStgDataRef();
            for (HSLFObjectData hod : ole) {
                if (hod.getExOleObjStg().getPersistId() != ref) continue;
                data = hod;
            }
        }
        if (data == null) {
            LOG.atWarn().log("OLE data not found");
        }
        return data;
    }

    public ExEmbed getExEmbed() {
        return this.getExEmbed(false);
    }

    private ExEmbed getExEmbed(boolean create) {
        if (this._exEmbed == null) {
            HSLFSlideShow ppt = this.getSheet().getSlideShow();
            ExObjList lst = ppt.getDocumentRecord().getExObjList(create);
            if (lst == null) {
                LOG.atWarn().log("ExObjList not found");
                return null;
            }
            int id = this.getObjectID();
            for (Record ch : lst.getChildRecords()) {
                ExEmbed embd;
                if (!(ch instanceof ExEmbed) || (embd = (ExEmbed)ch).getExOleObjAtom().getObjID() != id) continue;
                this._exEmbed = embd;
            }
            if (this._exEmbed == null && create) {
                this._exEmbed = new ExEmbed();
                this._exEmbed.getExOleObjAtom().setObjID(id);
                lst.appendChildRecord(this._exEmbed);
            }
        }
        return this._exEmbed;
    }

    public String getInstanceName() {
        ExEmbed ee = this.getExEmbed();
        return ee == null ? null : ee.getMenuName();
    }

    @Override
    public String getFullName() {
        ExEmbed ee = this.getExEmbed();
        return ee == null ? null : ee.getClipboardName();
    }

    public void setFullName(String fullName) {
        ExEmbed ex = this.getExEmbed(true);
        if (ex != null) {
            ex.setClipboardName(fullName);
        }
    }

    @Override
    public String getProgId() {
        ExEmbed ee = this.getExEmbed();
        return ee == null ? null : ee.getProgId();
    }

    public void setProgId(String progId) {
        ExEmbed ex = this.getExEmbed(true);
        if (ex != null) {
            ex.setProgId(progId);
        }
    }

    @Override
    public OutputStream updateObjectData(ObjectMetaData.Application application, ObjectMetaData metaData) {
        ObjectMetaData md;
        ObjectMetaData objectMetaData = md = application != null ? application.getMetaData() : metaData;
        if (md == null) {
            throw new RuntimeException("either application or metaData needs to be set");
        }
        return new ByteArrayOutputStream(){

            public void close() throws IOException {
                HSLFObjectShape.this.addUpdatedData(md, (AbstractByteArrayOutputStream)this);
            }
        };
    }

    private void addUpdatedData(ObjectMetaData md, AbstractByteArrayOutputStream baos) throws IOException {
        try (InputStream bis = FileMagic.prepareToCheckMagic(baos.toInputStream());){
            FileMagic fm = FileMagic.valueOf(bis);
            try (POIFSFileSystem poifs = fm == FileMagic.OLE2 ? new POIFSFileSystem(bis) : new POIFSFileSystem();){
                if (fm != FileMagic.OLE2) {
                    poifs.createDocument(bis, md.getOleEntry());
                }
                baos.reset();
                Ole10Native.createOleMarkerEntry(poifs);
                poifs.getRoot().setStorageClsid(md.getClassID());
                int oid = this.getObjectID();
                if (oid == 0) {
                    oid = this.getSheet().getSlideShow().addEmbed(poifs);
                    this.setObjectID(oid);
                } else {
                    HSLFObjectData od = this.getObjectData();
                    if (od != null) {
                        poifs.writeFilesystem((OutputStream)baos);
                        od.setData(baos.toByteArray());
                    }
                }
                this.setProgId(md.getProgId());
                this.setFullName(md.getObjectName());
            }
        }
    }
}

