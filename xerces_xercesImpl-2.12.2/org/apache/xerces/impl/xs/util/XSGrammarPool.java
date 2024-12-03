/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.util;

import java.util.ArrayList;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSModelImpl;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xs.XSModel;

public class XSGrammarPool
extends XMLGrammarPoolImpl {
    public XSModel toXSModel() {
        return this.toXSModel((short)1);
    }

    public XSModel toXSModel(short s) {
        Object object;
        int n;
        ArrayList<Grammar> arrayList = new ArrayList<Grammar>();
        for (n = 0; n < this.fGrammars.length; ++n) {
            object = this.fGrammars[n];
            while (object != null) {
                if (object.desc.getGrammarType().equals("http://www.w3.org/2001/XMLSchema")) {
                    arrayList.add(object.grammar);
                }
                object = object.next;
            }
        }
        n = arrayList.size();
        if (n == 0) {
            return this.toXSModel(new SchemaGrammar[0], s);
        }
        object = arrayList.toArray(new SchemaGrammar[n]);
        return this.toXSModel((SchemaGrammar[])object, s);
    }

    protected XSModel toXSModel(SchemaGrammar[] schemaGrammarArray, short s) {
        return new XSModelImpl(schemaGrammarArray, s);
    }
}

