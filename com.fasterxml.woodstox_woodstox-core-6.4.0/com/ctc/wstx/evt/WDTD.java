/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.ri.evt.DTDEventImpl
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.ent.EntityDecl;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.ri.evt.DTDEventImpl;

public class WDTD
extends DTDEventImpl {
    final DTDSubset mSubset;
    List<EntityDeclaration> mEntities = null;
    List<NotationDeclaration> mNotations = null;

    public WDTD(Location loc, String rootName, String sysId, String pubId, String intSubset, DTDSubset dtdSubset) {
        super(loc, rootName, sysId, pubId, intSubset, (Object)dtdSubset);
        this.mSubset = dtdSubset;
    }

    public WDTD(Location loc, String rootName, String sysId, String pubId, String intSubset) {
        this(loc, rootName, sysId, pubId, intSubset, null);
    }

    public WDTD(Location loc, String rootName, String intSubset) {
        this(loc, rootName, null, null, intSubset, null);
    }

    public WDTD(Location loc, String fullText) {
        super(loc, fullText);
        this.mSubset = null;
    }

    public List<EntityDeclaration> getEntities() {
        if (this.mEntities == null && this.mSubset != null) {
            this.mEntities = new ArrayList<EntityDecl>(this.mSubset.getGeneralEntityList());
        }
        return this.mEntities;
    }

    public List<NotationDeclaration> getNotations() {
        if (this.mNotations == null && this.mSubset != null) {
            this.mNotations = new ArrayList<NotationDeclaration>(this.mSubset.getNotationList());
        }
        return this.mNotations;
    }
}

