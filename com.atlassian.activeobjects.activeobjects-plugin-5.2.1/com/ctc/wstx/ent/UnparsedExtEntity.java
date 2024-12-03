/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.ent.ExtEntity;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;

public class UnparsedExtEntity
extends ExtEntity {
    final String mNotationId;

    public UnparsedExtEntity(Location loc, String name, URL ctxt, String pubId, String sysId, String notationId) {
        super(loc, name, ctxt, pubId, sysId);
        this.mNotationId = notationId;
    }

    public String getNotationName() {
        return this.mNotationId;
    }

    public void writeEnc(Writer w) throws IOException {
        w.write("<!ENTITY ");
        w.write(this.mName);
        String pubId = this.getPublicId();
        if (pubId != null) {
            w.write("PUBLIC \"");
            w.write(pubId);
            w.write("\" ");
        } else {
            w.write("SYSTEM ");
        }
        w.write(34);
        w.write(this.getSystemId());
        w.write("\" NDATA ");
        w.write(this.mNotationId);
        w.write(62);
    }

    public boolean isParsed() {
        return false;
    }

    public WstxInputSource expand(WstxInputSource parent, XMLResolver res, ReaderConfig cfg, int xmlVersion) {
        throw new IllegalStateException("Internal error: createInputSource() called for unparsed (external) entity.");
    }
}

