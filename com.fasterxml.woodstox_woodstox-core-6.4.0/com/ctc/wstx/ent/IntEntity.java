/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.InputSourceFactory;
import com.ctc.wstx.io.TextEscaper;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;

public class IntEntity
extends EntityDecl {
    protected final Location mContentLocation;
    final char[] mRepl;
    String mReplText = null;

    public IntEntity(Location loc, String name, URL ctxt, char[] repl, Location defLoc) {
        super(loc, name, ctxt);
        this.mRepl = repl;
        this.mContentLocation = defLoc;
    }

    public static IntEntity create(String id, String repl) {
        return IntEntity.create(id, repl.toCharArray());
    }

    public static IntEntity create(String id, char[] val) {
        WstxInputLocation loc = WstxInputLocation.getEmptyLocation();
        return new IntEntity((Location)((Object)loc), id, null, val, (Location)((Object)loc));
    }

    @Override
    public String getNotationName() {
        return null;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getReplacementText() {
        String repl = this.mReplText;
        if (repl == null) {
            this.mReplText = repl = this.mRepl.length == 0 ? "" : new String(this.mRepl);
        }
        return this.mReplText;
    }

    @Override
    public int getReplacementText(Writer w) throws IOException {
        w.write(this.mRepl);
        return this.mRepl.length;
    }

    @Override
    public String getSystemId() {
        return null;
    }

    @Override
    public void writeEnc(Writer w) throws IOException {
        w.write("<!ENTITY ");
        w.write(this.mName);
        w.write(" \"");
        TextEscaper.outputDTDText(w, this.mRepl, 0, this.mRepl.length);
        w.write("\">");
    }

    @Override
    public char[] getReplacementChars() {
        return this.mRepl;
    }

    @Override
    public boolean isExternal() {
        return false;
    }

    @Override
    public boolean isParsed() {
        return true;
    }

    @Override
    public WstxInputSource expand(WstxInputSource parent, XMLResolver res, ReaderConfig cfg, int xmlVersion) {
        return InputSourceFactory.constructCharArraySource(parent, this.mName, this.mRepl, 0, this.mRepl.length, this.mContentLocation, null);
    }
}

