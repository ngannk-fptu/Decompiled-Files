/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.ent.ExtEntity;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

public class ParsedExtEntity
extends ExtEntity {
    public ParsedExtEntity(Location loc, String name, URL ctxt, String pubId, String sysId) {
        super(loc, name, ctxt, pubId, sysId);
    }

    @Override
    public String getNotationName() {
        return null;
    }

    @Override
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
        w.write("\">");
    }

    @Override
    public boolean isParsed() {
        return true;
    }

    @Override
    public WstxInputSource expand(WstxInputSource parent, XMLResolver res, ReaderConfig cfg, int xmlVersion) throws IOException, XMLStreamException {
        if (xmlVersion == 0) {
            xmlVersion = 256;
        }
        return DefaultInputResolver.resolveEntity(parent, this.mContext, this.mName, this.getPublicId(), this.getSystemId(), res, cfg, xmlVersion);
    }
}

