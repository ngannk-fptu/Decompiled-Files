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

public abstract class Variator {
    protected static final int ALL_VARIANTS = -1;
    protected int variants;
    protected boolean[] variantPassed;
    protected List<String> names;
    protected List<Class<? extends CSSProperty>> types;
    protected List<Term<?>> terms;

    public Variator(int variants) {
        this.variants = variants;
        this.variantPassed = new boolean[variants];
        for (int i = 0; i < variants; ++i) {
            this.variantPassed[i] = false;
        }
        this.names = new ArrayList<String>(variants);
        this.types = new ArrayList<Class<? extends CSSProperty>>(variants);
    }

    protected abstract boolean variant(int var1, IntegerRef var2, Map<String, CSSProperty> var3, Map<String, Term<?>> var4);

    protected boolean checkInherit(int variant, Term<?> term, Map<String, CSSProperty> properties) {
        if (!(term instanceof TermIdent) || !"INHERIT".equalsIgnoreCase((String)((TermIdent)term).getValue())) {
            return false;
        }
        if (variant == -1) {
            for (int i = 0; i < this.variants; ++i) {
                properties.put(this.names.get(i), this.createInherit(i));
            }
            return true;
        }
        properties.put(this.names.get(variant), this.createInherit(variant));
        return true;
    }

    private CSSProperty createInherit(int i) {
        try {
            Class<? extends CSSProperty> clazz = this.types.get(i);
            CSSProperty property = CSSProperty.Translator.createInherit(clazz);
            if (property != null) {
                return property;
            }
            throw new IllegalAccessException("No inherit value for: " + clazz.getName());
        }
        catch (Exception e) {
            throw new UnsupportedOperationException("Unable to create inherit value", e);
        }
    }

    protected boolean variantCondition(int variant, IntegerRef term) {
        return true;
    }

    public boolean vary(Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.terms.size() == 1 && this.checkInherit(-1, this.terms.get(0), properties)) {
            return true;
        }
        IntegerRef i = new IntegerRef(0);
        while (i.get() < this.terms.size()) {
            boolean passed = false;
            for (int v = 0; v < this.variants; ++v) {
                if (!this.variantCondition(v, i) || this.variantPassed[v] || !(passed = this.variant(v, i, properties, values))) continue;
                this.variantPassed[v] = true;
                break;
            }
            if (!passed) {
                return false;
            }
            i.inc();
        }
        return true;
    }

    public boolean tryOneTermVariant(int variant, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        if (this.checkInherit(variant, (Term)d.get(0), properties)) {
            return true;
        }
        this.terms = new ArrayList();
        this.terms.add((Term<?>)d.get(0));
        return this.variant(variant, new IntegerRef(0), properties, values);
    }

    public boolean tryMultiTermVariant(int variant, Map<String, CSSProperty> properties, Map<String, Term<?>> values, Term<?> ... terms) {
        this.terms = Arrays.asList(terms);
        if (this.terms.size() == 1 && this.checkInherit(variant, this.terms.get(0), properties)) {
            return true;
        }
        return this.variant(variant, new IntegerRef(0), properties, values);
    }

    public void assignVariantPropertyNames(String ... variantPropertyNames) {
        this.names = Arrays.asList(variantPropertyNames);
    }

    public void assignTerms(Term<?> ... terms) {
        this.terms = Arrays.asList(terms);
    }

    public void assignTermsFromDeclaration(Declaration d) {
        this.terms = d.asList();
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

    protected static class IntegerRef {
        private int i;

        public IntegerRef(int i) {
            this.i = i;
        }

        public int get() {
            return this.i;
        }

        public void set(int i) {
            this.i = i;
        }

        public void inc() {
            ++this.i;
        }
    }
}

