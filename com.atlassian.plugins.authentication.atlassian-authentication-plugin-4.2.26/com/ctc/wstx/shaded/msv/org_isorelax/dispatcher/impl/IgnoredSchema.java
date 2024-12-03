/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.AttributesVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl.IgnoreVerifier;
import java.util.Iterator;
import java.util.Vector;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;

public class IgnoredSchema
implements IslandSchema {
    private static final ElementDecl[] theElemDecl = new ElementDecl[]{new ElementDecl(){

        public String getName() {
            return "$$any$$";
        }

        public Object getProperty(String propertyName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(propertyName);
        }

        public boolean getFeature(String featureName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(featureName);
        }
    }};
    private static final AttributesDecl[] theAttDecl = new AttributesDecl[]{new AttributesDecl(){

        public String getName() {
            return "$$any$$";
        }

        public Object getProperty(String propertyName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(propertyName);
        }

        public boolean getFeature(String featureName) throws SAXNotRecognizedException {
            throw new SAXNotRecognizedException(featureName);
        }
    }};

    public ElementDecl getElementDeclByName(String name) {
        return theElemDecl[0];
    }

    public ElementDecl[] getElementDecls() {
        return theElemDecl;
    }

    public Iterator iterateElementDecls() {
        Vector<ElementDecl> vec = new Vector<ElementDecl>();
        vec.add(theElemDecl[0]);
        return vec.iterator();
    }

    public IslandVerifier createNewVerifier(String namespaceURI, ElementDecl[] rules) {
        return new IgnoreVerifier(namespaceURI, rules);
    }

    public AttributesDecl getAttributesDeclByName(String name) {
        return theAttDecl[0];
    }

    public AttributesDecl[] getAttributesDecls() {
        return theAttDecl;
    }

    public Iterator iterateAttributesDecls() {
        Vector<AttributesDecl> vec = new Vector<AttributesDecl>();
        vec.add(theAttDecl[0]);
        return vec.iterator();
    }

    public AttributesVerifier createNewAttributesVerifier(String namespaceURI, AttributesDecl[] decls) {
        throw new Error("not implemented yet");
    }

    public void bind(SchemaProvider provider, ErrorHandler handler) {
    }
}

