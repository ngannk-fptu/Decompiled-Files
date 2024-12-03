/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;

public class AttributeState
extends com.ctc.wstx.shaded.msv_core.reader.trex.AttributeState {
    private static final String infosetURI = "http://www.w3.org/2000/xmlns";

    protected void endSelf() {
        super.endSelf();
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        reader.restrictionChecker.checkNameClass(this.nameClass);
        this.nameClass.visit(new NameClassVisitor(){

            public Object onAnyName(AnyNameClass nc) {
                return null;
            }

            public Object onSimple(SimpleNameClass nc) {
                if (nc.namespaceURI.equals(AttributeState.infosetURI)) {
                    reader.reportError("RELAXNGReader.InfosetUriAttribute");
                }
                if (nc.namespaceURI.length() == 0 && nc.localName.equals("xmlns")) {
                    reader.reportError("RELAXNGReader.XmlnsAttribute");
                }
                return null;
            }

            public Object onNsName(NamespaceNameClass nc) {
                if (nc.namespaceURI.equals(AttributeState.infosetURI)) {
                    reader.reportError("RELAXNGReader.InfosetUriAttribute");
                }
                return null;
            }

            public Object onNot(NotNameClass nc) {
                nc.child.visit(this);
                return null;
            }

            public Object onDifference(DifferenceNameClass nc) {
                nc.nc1.visit(this);
                nc.nc2.visit(this);
                return null;
            }

            public Object onChoice(ChoiceNameClass nc) {
                nc.nc1.visit(this);
                nc.nc2.visit(this);
                return null;
            }
        });
    }
}

