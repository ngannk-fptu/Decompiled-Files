/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.evt.WEntityDeclaration;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

public abstract class EntityDecl
extends WEntityDeclaration {
    final String mName;
    final URL mContext;
    protected boolean mDeclaredExternally = false;

    public EntityDecl(Location loc, String name, URL ctxt) {
        super(loc);
        this.mName = name;
        this.mContext = ctxt;
    }

    public void markAsExternallyDeclared() {
        this.mDeclaredExternally = true;
    }

    @Override
    public final String getBaseURI() {
        return this.mContext.toExternalForm();
    }

    @Override
    public final String getName() {
        return this.mName;
    }

    @Override
    public abstract String getNotationName();

    @Override
    public abstract String getPublicId();

    @Override
    public abstract String getReplacementText();

    public abstract int getReplacementText(Writer var1) throws IOException;

    @Override
    public abstract String getSystemId();

    public boolean wasDeclaredExternally() {
        return this.mDeclaredExternally;
    }

    @Override
    public abstract void writeEnc(Writer var1) throws IOException;

    public abstract char[] getReplacementChars();

    public final int getReplacementTextLength() {
        String str = this.getReplacementText();
        return str == null ? 0 : str.length();
    }

    public abstract boolean isExternal();

    public abstract boolean isParsed();

    public abstract WstxInputSource expand(WstxInputSource var1, XMLResolver var2, ReaderConfig var3, int var4) throws IOException, XMLStreamException;
}

