/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import java.util.Hashtable;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;

public class DTDGrammarBucket {
    protected final Hashtable fGrammars = new Hashtable();
    protected DTDGrammar fActiveGrammar;
    protected boolean fIsStandalone;

    public void putGrammar(DTDGrammar dTDGrammar) {
        XMLDTDDescription xMLDTDDescription = (XMLDTDDescription)dTDGrammar.getGrammarDescription();
        this.fGrammars.put(xMLDTDDescription, dTDGrammar);
    }

    public DTDGrammar getGrammar(XMLGrammarDescription xMLGrammarDescription) {
        return (DTDGrammar)this.fGrammars.get((XMLDTDDescription)xMLGrammarDescription);
    }

    public void clear() {
        this.fGrammars.clear();
        this.fActiveGrammar = null;
        this.fIsStandalone = false;
    }

    void setStandalone(boolean bl) {
        this.fIsStandalone = bl;
    }

    boolean getStandalone() {
        return this.fIsStandalone;
    }

    void setActiveGrammar(DTDGrammar dTDGrammar) {
        this.fActiveGrammar = dTDGrammar;
    }

    DTDGrammar getActiveGrammar() {
        return this.fActiveGrammar;
    }
}

