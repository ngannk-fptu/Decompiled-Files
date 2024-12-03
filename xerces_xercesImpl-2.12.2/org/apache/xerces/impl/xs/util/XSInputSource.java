/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.XSObject;

public final class XSInputSource
extends XMLInputSource {
    private SchemaGrammar[] fGrammars;
    private XSObject[] fComponents;

    public XSInputSource(SchemaGrammar[] schemaGrammarArray) {
        super(null, null, null);
        this.fGrammars = schemaGrammarArray;
        this.fComponents = null;
    }

    public XSInputSource(XSObject[] xSObjectArray) {
        super(null, null, null);
        this.fGrammars = null;
        this.fComponents = xSObjectArray;
    }

    public SchemaGrammar[] getGrammars() {
        return this.fGrammars;
    }

    public void setGrammars(SchemaGrammar[] schemaGrammarArray) {
        this.fGrammars = schemaGrammarArray;
    }

    public XSObject[] getComponents() {
        return this.fComponents;
    }

    public void setComponents(XSObject[] xSObjectArray) {
        this.fComponents = xSObjectArray;
    }
}

