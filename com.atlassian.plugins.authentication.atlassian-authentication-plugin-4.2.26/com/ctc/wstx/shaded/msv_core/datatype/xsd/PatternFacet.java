/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithLexicalConstraintFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.Vector;

public final class PatternFacet
extends DataTypeWithLexicalConstraintFacet {
    private transient RegExp[] exps;
    public final String[] patterns;
    private static final long serialVersionUID = 1L;

    public RegExp[] getRegExps() {
        return this.exps;
    }

    public PatternFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        super(nsUri, typeName, baseType, "pattern", facets.isFixed("pattern"));
        Vector regExps = facets.getVector("pattern");
        this.patterns = regExps.toArray(new String[regExps.size()]);
        try {
            this.compileRegExps();
        }
        catch (ParseException pe) {
            throw new DatatypeException(PatternFacet.localize("PatternFacet.ParseError", pe.getMessage()));
        }
    }

    private void compileRegExps() throws ParseException {
        this.exps = new RegExp[this.patterns.length];
        RegExpFactory factory = RegExpFactory.createFactory();
        for (int i = 0; i < this.exps.length; ++i) {
            this.exps[i] = factory.compile(this.patterns[i]);
        }
    }

    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if (this.checkLexicalConstraint(content)) {
            return;
        }
        if (this.exps.length == 1) {
            throw new DatatypeException(-1, PatternFacet.localize("DataTypeErrorDiagnosis.Pattern.1", this.patterns[0]));
        }
        throw new DatatypeException(-1, PatternFacet.localize("DataTypeErrorDiagnosis.Pattern.Many"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final boolean checkLexicalConstraint(String literal) {
        PatternFacet patternFacet = this;
        synchronized (patternFacet) {
            for (int i = 0; i < this.exps.length; ++i) {
                if (!this.exps[i].matches(literal)) continue;
                return true;
            }
        }
        return false;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        try {
            this.compileRegExps();
        }
        catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }
}

