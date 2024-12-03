/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class LazyTypeIncubator
implements XSTypeIncubator {
    private final XSDatatypeExp baseType;
    private final GrammarReader reader;
    private final List facets = new LinkedList();

    public LazyTypeIncubator(XSDatatypeExp base, GrammarReader reader) {
        this.baseType = base;
        this.reader = reader;
    }

    public void addFacet(String name, String strValue, boolean fixed, ValidationContext context) {
        this.facets.add(new Facet(name, strValue, fixed, context));
    }

    public XSDatatypeExp derive(final String nsUri, final String localName) throws DatatypeException {
        final int facetSize = this.facets.size();
        if (facetSize == 0) {
            return this.baseType;
        }
        return new XSDatatypeExp(nsUri, localName, this.reader, new XSDatatypeExp.Renderer(){

            public XSDatatype render(XSDatatypeExp.RenderingContext context) throws DatatypeException {
                TypeIncubator ti = new TypeIncubator(LazyTypeIncubator.this.baseType.getType(context));
                Iterator itr = LazyTypeIncubator.this.facets.iterator();
                for (int i = 0; i < facetSize; ++i) {
                    Facet f = (Facet)itr.next();
                    ti.addFacet(f.name, f.value, f.fixed, f.context);
                }
                return ti.derive(nsUri, localName);
            }
        });
    }

    private class Facet {
        String name;
        String value;
        boolean fixed;
        ValidationContext context;

        public Facet(String name, String value, boolean fixed, ValidationContext context) {
            this.name = name;
            this.value = value;
            this.fixed = fixed;
            this.context = context;
        }
    }
}

