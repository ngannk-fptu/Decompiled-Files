/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.SupportedCSS;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermIdent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Repeater {
    protected int times;
    protected List<Term<?>> terms;
    protected List<String> names;
    protected Class<? extends CSSProperty> type;

    public Repeater(int times) {
        this.times = times;
        this.terms = new ArrayList(times);
        this.names = new ArrayList<String>(times);
    }

    protected abstract boolean operation(int var1, Map<String, CSSProperty> var2, Map<String, Term<?>> var3);

    public boolean repeat(Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        for (int i = 0; i < this.times; ++i) {
            if (this.operation(i, properties, values)) continue;
            return false;
        }
        return true;
    }

    public boolean repeatOverFourTermDeclaration(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) throws IllegalArgumentException {
        switch (d.size()) {
            case 1: {
                Term term = (Term)d.get(0);
                if (term instanceof TermIdent && "INHERIT".equalsIgnoreCase((String)((TermIdent)term).getValue())) {
                    CSSProperty property = CSSProperty.Translator.createInherit(this.type);
                    for (int i = 0; i < this.times; ++i) {
                        properties.put(this.names.get(i), property);
                    }
                    return true;
                }
                this.assignTerms(term, term, term, term);
                return this.repeat(properties, values);
            }
            case 2: {
                Term term1 = (Term)d.get(0);
                Term term2 = (Term)d.get(1);
                this.assignTerms(term1, term2, term1, term2);
                return this.repeat(properties, values);
            }
            case 3: {
                Term term31 = (Term)d.get(0);
                Term term32 = (Term)d.get(1);
                Term term33 = (Term)d.get(2);
                this.assignTerms(term31, term32, term33, term32);
                return this.repeat(properties, values);
            }
            case 4: {
                Term term41 = (Term)d.get(0);
                Term term42 = (Term)d.get(1);
                Term term43 = (Term)d.get(2);
                Term term44 = (Term)d.get(3);
                this.assignTerms(term41, term42, term43, term44);
                return this.repeat(properties, values);
            }
        }
        throw new IllegalArgumentException("Invalid length of terms in Repeater.");
    }

    public void assignPropertyNames(String ... propertyNames) throws IllegalArgumentException {
        if (propertyNames.length != this.times) {
            throw new IllegalArgumentException("Invalid length of propertyNames in Repeater.");
        }
        this.names = Arrays.asList(propertyNames);
    }

    public void assignTerms(Term<?> ... terms) throws IllegalArgumentException {
        if (terms.length != this.times) {
            throw new IllegalArgumentException("Invalid length of terms in Repeater.");
        }
        this.terms = Arrays.asList(terms);
    }

    public void assignDefaults(Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        SupportedCSS css = CSSFactory.getSupportedCSS();
        for (String name : this.names) {
            Term<?> dv;
            CSSProperty dp = css.getDefaultProperty(name);
            if (dp != null) {
                properties.put(name, dp);
            }
            if ((dv = css.getDefaultValue(name)) == null) continue;
            values.put(name, dv);
        }
    }
}

