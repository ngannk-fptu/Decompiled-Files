/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import java.net.URL;
import javax.xml.stream.Location;
import org.codehaus.stax2.ri.evt.NotationDeclarationEventImpl;

public class WNotationDeclaration
extends NotationDeclarationEventImpl {
    final URL _baseURL;

    public WNotationDeclaration(Location loc, String name, String pubId, String sysId, URL baseURL) {
        super(loc, name, pubId, sysId);
        this._baseURL = baseURL;
    }

    public String getBaseURI() {
        if (this._baseURL == null) {
            return super.getBaseURI();
        }
        return this._baseURL.toExternalForm();
    }
}

