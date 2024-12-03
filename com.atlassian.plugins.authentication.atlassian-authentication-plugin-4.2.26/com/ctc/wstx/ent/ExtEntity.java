/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

public abstract class ExtEntity
extends EntityDecl {
    final String mPublicId;
    final String mSystemId;

    public ExtEntity(Location loc, String name, URL ctxt, String pubId, String sysId) {
        super(loc, name, ctxt);
        this.mPublicId = pubId;
        this.mSystemId = sysId;
    }

    @Override
    public abstract String getNotationName();

    @Override
    public String getPublicId() {
        return this.mPublicId;
    }

    @Override
    public String getReplacementText() {
        return null;
    }

    @Override
    public int getReplacementText(Writer w) {
        return 0;
    }

    @Override
    public String getSystemId() {
        return this.mSystemId;
    }

    @Override
    public abstract void writeEnc(Writer var1) throws IOException;

    @Override
    public char[] getReplacementChars() {
        return null;
    }

    @Override
    public boolean isExternal() {
        return true;
    }

    @Override
    public abstract boolean isParsed();

    @Override
    public abstract WstxInputSource expand(WstxInputSource var1, XMLResolver var2, ReaderConfig var3, int var4) throws IOException, XMLStreamException;
}

