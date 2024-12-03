/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.dtd.DTDSubset;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.Location;
import org.codehaus.stax2.ri.evt.DTDEventImpl;

public class WDTD
extends DTDEventImpl {
    final DTDSubset mSubset;
    List mEntities = null;
    List mNotations = null;

    public WDTD(Location loc, String rootName, String sysId, String pubId, String intSubset, DTDSubset dtdSubset) {
        super(loc, rootName, sysId, pubId, intSubset, dtdSubset);
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

    public List getEntities() {
        if (this.mEntities == null && this.mSubset != null) {
            this.mEntities = new ArrayList(this.mSubset.getGeneralEntityList());
        }
        return this.mEntities;
    }

    public List getNotations() {
        if (this.mNotations == null && this.mSubset != null) {
            this.mNotations = new ArrayList(this.mSubset.getNotationList());
        }
        return this.mNotations;
    }
}

