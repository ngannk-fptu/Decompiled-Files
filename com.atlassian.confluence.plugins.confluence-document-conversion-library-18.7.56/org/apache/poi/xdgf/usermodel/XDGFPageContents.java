/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.PageContentsDocument;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.xdgf.usermodel.XDGFBaseContents;
import org.apache.poi.xdgf.usermodel.XDGFMaster;
import org.apache.poi.xdgf.usermodel.XDGFMasterContents;
import org.apache.poi.xdgf.usermodel.XDGFPage;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.xmlbeans.XmlException;

public class XDGFPageContents
extends XDGFBaseContents {
    protected Map<Long, XDGFMaster> _masters = new HashMap<Long, XDGFMaster>();
    protected XDGFPage _page;

    public XDGFPageContents(PackagePart part) {
        super(part);
    }

    @Override
    protected void onDocumentRead() {
        try {
            try (InputStream stream = this.getPackagePart().getInputStream();){
                this._pageContents = ((PageContentsDocument)PageContentsDocument.Factory.parse(stream)).getPageContents();
            }
            catch (IOException | XmlException e) {
                throw new POIXMLException(e);
            }
            for (POIXMLDocumentPart part : this.getRelations()) {
                if (!(part instanceof XDGFMasterContents)) continue;
                XDGFMaster master = ((XDGFMasterContents)part).getMaster();
                if (master == null) {
                    throw new POIXMLException("Master entry is missing in XDGFPageContents");
                }
                this._masters.put(master.getID(), master);
            }
            super.onDocumentRead();
            for (XDGFShape shape : this._shapes.values()) {
                if (!shape.isTopmost()) continue;
                shape.setupMaster(this, null);
            }
        }
        catch (POIXMLException e) {
            throw XDGFException.wrap(this, e);
        }
    }

    public XDGFPage getPage() {
        return this._page;
    }

    protected void setPage(XDGFPage page) {
        this._page = page;
    }

    public XDGFMaster getMasterById(long id) {
        return this._masters.get(id);
    }
}

