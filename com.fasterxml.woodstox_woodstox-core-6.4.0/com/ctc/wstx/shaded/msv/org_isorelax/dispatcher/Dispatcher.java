/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public interface Dispatcher {
    public void attachXMLReader(XMLReader var1);

    public void switchVerifier(IslandVerifier var1) throws SAXException;

    public void setErrorHandler(ErrorHandler var1);

    public ErrorHandler getErrorHandler();

    public SchemaProvider getSchemaProvider();

    public int countNotationDecls();

    public NotationDecl getNotationDecl(int var1);

    public int countUnparsedEntityDecls();

    public UnparsedEntityDecl getUnparsedEntityDecl(int var1);

    public static class UnparsedEntityDecl {
        public final String name;
        public final String publicId;
        public final String systemId;
        public final String notation;

        public UnparsedEntityDecl(String name, String publicId, String systemId, String notation) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
            this.notation = notation;
        }
    }

    public static class NotationDecl {
        public final String name;
        public final String publicId;
        public final String systemId;

        public NotationDecl(String name, String publicId, String systemId) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
        }
    }
}

