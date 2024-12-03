/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleFontFace;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermString;
import cz.vutbr.web.css.TermURI;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.ArrayList;
import java.util.List;

public class RuleFontFaceImpl
extends AbstractRuleBlock<Declaration>
implements RuleFontFace {
    private static final String PROPERTY_FONT_FAMILY_NAME = "font-family";
    private static final String PROPERTY_SOURCE = "src";
    private static final String PROPERTY_FONT_STYLE = "font-style";
    private static final String PROPERTY_FONT_WEIGHT = "font-weight";
    private static final String PROPERTY_UNICODE_RANGE = "unicode-range";

    protected RuleFontFaceImpl() {
    }

    @Override
    public String getFontFamily() {
        return this.getStringValue(PROPERTY_FONT_FAMILY_NAME);
    }

    @Override
    public List<RuleFontFace.Source> getSources() {
        Declaration decl = this.getDeclaration(PROPERTY_SOURCE);
        if (decl != null) {
            ArrayList<RuleFontFace.Source> ret = new ArrayList<RuleFontFace.Source>(decl.size());
            boolean invalid = false;
            for (int i = 0; i < decl.size() && !invalid; ++i) {
                RuleFontFace.Source src;
                Term val = (Term)decl.get(i);
                if (val instanceof TermURI) {
                    String format;
                    final TermURI uri = (TermURI)val;
                    String string = format = i + 1 < decl.size() ? this.checkForFormat((Term)decl.get(i + 1)) : null;
                    if (format != null) {
                        ++i;
                    }
                    src = new RuleFontFace.SourceURL(){

                        @Override
                        public TermURI getURI() {
                            return uri;
                        }

                        @Override
                        public String getFormat() {
                            return format;
                        }
                    };
                    ret.add(src);
                } else if (val instanceof TermFunction) {
                    TermFunction fn = (TermFunction)val;
                    if (fn.getFunctionName().equalsIgnoreCase("local") && fn.size() == 1 && fn.get(0) instanceof TermString) {
                        final String fontname = (String)((TermString)fn.get(0)).getValue();
                        src = new RuleFontFace.SourceLocal(){

                            @Override
                            public String getName() {
                                return fontname;
                            }
                        };
                        ret.add(src);
                    } else {
                        invalid = true;
                    }
                } else {
                    invalid = true;
                }
                if (i + 1 >= decl.size() || ((Term)decl.get(i + 1)).getOperator() == Term.Operator.COMMA) continue;
                invalid = true;
            }
            if (!invalid) {
                return ret;
            }
            return null;
        }
        return null;
    }

    private String checkForFormat(Term<?> term) {
        if (term instanceof TermFunction && term.getOperator() == Term.Operator.SPACE) {
            TermFunction fn = (TermFunction)term;
            if (fn.getFunctionName().equalsIgnoreCase("format") && fn.size() == 1 && fn.get(0) instanceof TermString) {
                return (String)((TermString)fn.get(0)).getValue();
            }
            return null;
        }
        return null;
    }

    @Override
    public CSSProperty.FontStyle getFontStyle() {
        String strValue = this.getStringValue(PROPERTY_FONT_STYLE);
        if (strValue == null) {
            return null;
        }
        try {
            return CSSProperty.FontStyle.valueOf(strValue.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public CSSProperty.FontWeight getFontWeight() {
        String strValue = this.getStringValue(PROPERTY_FONT_WEIGHT);
        if (strValue == null) {
            return null;
        }
        try {
            return CSSProperty.FontWeight.valueOf(strValue.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<String> getUnicodeRanges() {
        Declaration decl = this.getDeclaration(PROPERTY_UNICODE_RANGE);
        if (decl != null) {
            ArrayList<String> ret = new ArrayList<String>(decl.size());
            for (Term term : decl) {
                ret.add(term.getValue().toString());
            }
            return ret;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("@font-face").append(" ");
        sb.append(" {\n");
        sb = OutputUtil.appendList(sb, this.list, "", depth + 1);
        sb.append("}\n");
        return sb.toString();
    }

    private String getStringValue(String propertyName) {
        Declaration decl = this.getDeclaration(propertyName);
        if (decl == null) {
            return null;
        }
        Term term = (Term)decl.get(0);
        if (term == null) {
            return null;
        }
        Object value = term.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        return (String)value;
    }

    private Declaration getDeclaration(String property) {
        for (Declaration decl : this.list) {
            if (!property.equals(decl.getProperty())) continue;
            return decl;
        }
        return null;
    }
}

