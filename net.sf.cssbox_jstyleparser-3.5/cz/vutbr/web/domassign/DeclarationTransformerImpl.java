/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleFactory;
import cz.vutbr.web.css.SupportedCSS;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermBracketedIdents;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.TermFloatValue;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermNumber;
import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.css.TermRect;
import cz.vutbr.web.css.TermString;
import cz.vutbr.web.css.TermTime;
import cz.vutbr.web.css.TermURI;
import cz.vutbr.web.css.TermUnicodeRange;
import cz.vutbr.web.csskit.DeclarationTransformer;
import cz.vutbr.web.domassign.Repeater;
import cz.vutbr.web.domassign.ValidationUtils;
import cz.vutbr.web.domassign.Variator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeclarationTransformerImpl
implements DeclarationTransformer {
    private static final Logger log = LoggerFactory.getLogger(DeclarationTransformerImpl.class);
    private static final boolean AVOID_INH = true;
    private static final boolean ALLOW_INH = false;
    private Map<String, Method> methods = this.parsingMethods();
    private static final DeclarationTransformerImpl instance;
    private static final RuleFactory rf;
    private static final TermFactory tf;
    private static final SupportedCSS css;

    public static final DeclarationTransformerImpl getInstance() {
        return instance;
    }

    public static final String camelCase(String string) {
        StringBuilder sb = new StringBuilder();
        boolean upperFlag = false;
        for (int i = 0; i < string.length(); ++i) {
            char ch = string.charAt(i);
            if (ch == '-') {
                upperFlag = true;
                continue;
            }
            if (upperFlag && Character.isLetter(ch)) {
                sb.append(Character.toUpperCase(ch));
                upperFlag = false;
                continue;
            }
            if (!upperFlag && Character.isLetter(ch)) {
                sb.append(ch);
                continue;
            }
            if (ch != '_') continue;
            sb.append(ch);
        }
        return sb.toString();
    }

    @Override
    public boolean parseDeclaration(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        String propertyName = d.getProperty();
        if (!css.isSupportedCSSProperty(propertyName) || d.isEmpty()) {
            return false;
        }
        try {
            Method m = this.methods.get(propertyName);
            if (m != null) {
                boolean result = (Boolean)m.invoke((Object)this, d, properties, values);
                log.debug("Parsing /{}/ {}", (Object)result, (Object)d);
                return result;
            }
            boolean result = this.processAdditionalCSSGenericProperty(d, properties, values);
            log.debug("Parsing with proxy /{}/ {}", (Object)result, (Object)d);
            return result;
        }
        catch (IllegalArgumentException e) {
            log.warn("Illegal argument", (Throwable)e);
        }
        catch (IllegalAccessException e) {
            log.warn("Illegal access", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            log.warn("Invocation target", (Throwable)e);
            log.warn("Invotation target cause", e.getCause());
        }
        return false;
    }

    private DeclarationTransformerImpl() {
    }

    protected Map<String, Method> parsingMethods() {
        HashMap<String, Method> map = new HashMap<String, Method>(css.getTotalProperties(), 1.0f);
        for (String key : css.getDefinedPropertyNames()) {
            try {
                Method m = DeclarationTransformerImpl.class.getDeclaredMethod(DeclarationTransformerImpl.camelCase("process-" + key), Declaration.class, Map.class, Map.class);
                map.put(key, m);
            }
            catch (Exception e) {
                log.warn("Unable to find method for property {}.", (Object)key);
            }
        }
        log.info("Totally found {} parsing methods", (Object)map.size());
        return map;
    }

    public <T extends CSSProperty> T genericPropertyRaw(Class<T> type, Set<T> intersection, TermIdent term) {
        try {
            String name = ((String)term.getValue()).replace("-", "_").toUpperCase();
            T property = CSSProperty.Translator.valueOf(type, name);
            if (intersection != null && intersection.contains(property)) {
                return property;
            }
            return property;
        }
        catch (Exception e) {
            return null;
        }
    }

    protected <T extends CSSProperty> boolean genericProperty(Class<T> type, TermIdent term, boolean avoidInherit, Map<String, CSSProperty> properties, String propertyName) {
        T property = this.genericPropertyRaw(type, null, term);
        if (property == null || avoidInherit && property.equalsInherit()) {
            return false;
        }
        properties.put(propertyName, (CSSProperty)property);
        return true;
    }

    protected <T extends CSSProperty> boolean genericTermIdent(Class<T> type, Term<?> term, boolean avoidInherit, String propertyName, Map<String, CSSProperty> properties) {
        if (term instanceof TermIdent) {
            return this.genericProperty(type, (TermIdent)term, avoidInherit, properties, propertyName);
        }
        return false;
    }

    protected <T extends CSSProperty> boolean genericTermColor(Term<?> term, String propertyName, T colorIdentification, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (term instanceof TermColor) {
            properties.put(propertyName, colorIdentification);
            values.put(propertyName, term);
            return true;
        }
        return false;
    }

    protected <T extends CSSProperty> boolean genericTermLength(Term<?> term, String propertyName, T lengthIdentification, ValueRange range, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (term instanceof TermInteger && ((TermInteger)term).getUnit().equals((Object)TermNumeric.Unit.none)) {
            if (CSSFactory.getImplyPixelLength() || ((Float)((TermInteger)term).getValue()).floatValue() == 0.0f) {
                TermLength tl = tf.createLength((Float)((TermInteger)term).getValue(), TermNumeric.Unit.px);
                return this.genericTerm(TermLength.class, tl, propertyName, lengthIdentification, range, properties, values);
            }
            return false;
        }
        if (term instanceof TermLength) {
            return this.genericTerm(TermLength.class, term, propertyName, lengthIdentification, range, properties, values);
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected <T extends CSSProperty> boolean genericTerm(Class<? extends Term<?>> termType, Term<?> term, String propertyName, T typeIdentification, ValueRange range, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (!termType.isInstance(term)) return false;
        if (range != ValueRange.ALLOW_ALL) {
            if (term.getValue() instanceof Integer) {
                Integer zero = 0;
                int result = zero.compareTo((Integer)term.getValue());
                if (result > 0) {
                    if (range != ValueRange.TRUNCATE_NEGATIVE) return false;
                    ((TermInteger)term).setZero();
                } else if (result == 0 && range == ValueRange.DISALLOW_ZERO) {
                    return false;
                }
            } else if (term.getValue() instanceof Float) {
                Float zero = Float.valueOf(0.0f);
                int result = zero.compareTo((Float)term.getValue());
                if (result > 0) {
                    if (range != ValueRange.TRUNCATE_NEGATIVE) return false;
                    ((TermFloatValue)term).setZero();
                } else if (result == 0 && range == ValueRange.DISALLOW_ZERO) {
                    return false;
                }
            }
        }
        properties.put(propertyName, typeIdentification);
        values.put(propertyName, term);
        return true;
    }

    protected <T extends CSSProperty> boolean genericOneIdent(Class<T> type, Declaration d, Map<String, CSSProperty> properties) {
        if (d.size() != 1) {
            return false;
        }
        return this.genericTermIdent(type, (Term)d.get(0), false, d.getProperty(), properties);
    }

    protected <T extends CSSProperty> boolean genericOneIdentOrColor(Class<T> type, T colorIdentification, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        return this.genericTermIdent(type, (Term)d.get(0), false, d.getProperty(), properties) || this.genericTermColor((Term)d.get(0), d.getProperty(), colorIdentification, properties, values);
    }

    protected <T extends CSSProperty> boolean genericOneIdentOrInteger(Class<T> type, T integerIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        return this.genericTermIdent(type, (Term)d.get(0), false, d.getProperty(), properties) || this.genericTerm(TermInteger.class, (Term)d.get(0), d.getProperty(), integerIdentification, range, properties, values);
    }

    protected <T extends CSSProperty> boolean genericOneIdentOrIntegerOrNumber(Class<T> type, T integerIdentification, T numberIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        return this.genericTermIdent(type, (Term)d.get(0), false, d.getProperty(), properties) || this.genericTerm(TermInteger.class, (Term)d.get(0), d.getProperty(), integerIdentification, range, properties, values) || this.genericTerm(TermNumber.class, (Term)d.get(0), d.getProperty(), numberIdentification, range, properties, values);
    }

    protected <T extends CSSProperty> boolean genericOneIdentOrLength(Class<T> type, T lengthIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        return this.genericTermIdent(type, (Term)d.get(0), false, d.getProperty(), properties) || this.genericTermLength((Term)d.get(0), d.getProperty(), lengthIdentification, range, properties, values);
    }

    protected <T extends CSSProperty> boolean genericTime(Class<T> type, T integerIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        Term term = (Term)d.get(0);
        if (term instanceof TermIdent) {
            T property = this.genericPropertyRaw(type, null, (TermIdent)term);
            if (!property.equalsInherit()) {
                return false;
            }
            properties.put(d.getProperty(), (CSSProperty)property);
            return true;
        }
        return this.genericTerm(TermTime.class, term, d.getProperty(), integerIdentification, range, properties, values);
    }

    protected <T extends CSSProperty> boolean genericInteger(Class<T> type, T integerIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        Term term = (Term)d.get(0);
        if (term instanceof TermIdent) {
            T property = this.genericPropertyRaw(type, null, (TermIdent)term);
            if (!property.equalsInherit()) {
                return false;
            }
            properties.put(d.getProperty(), (CSSProperty)property);
            return true;
        }
        return this.genericTerm(TermInteger.class, term, d.getProperty(), integerIdentification, range, properties, values);
    }

    protected <T extends CSSProperty> boolean genericIntegerOrLength(Class<T> type, T integerIdentification, T lengthIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        Term term = (Term)d.get(0);
        if (term instanceof TermIdent) {
            T property = this.genericPropertyRaw(type, null, (TermIdent)term);
            if (!property.equalsInherit()) {
                return false;
            }
            properties.put(d.getProperty(), (CSSProperty)property);
            return true;
        }
        return this.genericTerm(TermInteger.class, term, d.getProperty(), integerIdentification, range, properties, values) || this.genericTermLength(term, d.getProperty(), lengthIdentification, range, properties, values);
    }

    protected <T extends Enum<T>> boolean genericOneIdentOrLengthOrPercent(Class<T> type, T lengthIdentification, T percentIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        return this.genericTermIdent(type, (Term)d.get(0), false, d.getProperty(), properties) || this.genericTermLength((Term)d.get(0), d.getProperty(), (CSSProperty)((Object)lengthIdentification), range, properties, values) || this.genericTerm(TermPercent.class, (Term)d.get(0), d.getProperty(), (CSSProperty)((Object)percentIdentification), range, properties, values);
    }

    protected <T extends Enum<T>> boolean genericTwoIdentsOrLengthsOrPercents(Class<T> type, T listIdentification, ValueRange range, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1) {
            String propertyName;
            Term term = (Term)d.get(0);
            if (this.genericTermIdent(type, term, false, propertyName = d.getProperty(), properties) || this.genericTermLength(term, propertyName, (CSSProperty)((Object)listIdentification), range, properties, values) || this.genericTerm(TermPercent.class, term, propertyName, (CSSProperty)((Object)listIdentification), range, properties, values)) {
                if (properties.get(propertyName) == listIdentification) {
                    TermList terms = tf.createList(2);
                    terms.add(term);
                    terms.add(term);
                    values.put(propertyName, terms);
                }
                return true;
            }
            return false;
        }
        if (d.size() == 2) {
            Term term1 = (Term)d.get(0);
            Term term2 = (Term)d.get(1);
            String propertyName = d.getProperty();
            if ((this.genericTermLength(term1, propertyName, (CSSProperty)((Object)listIdentification), range, properties, values) || this.genericTerm(TermPercent.class, term1, propertyName, (CSSProperty)((Object)listIdentification), range, properties, values)) && (this.genericTermLength(term2, propertyName, (CSSProperty)((Object)listIdentification), range, properties, values) || this.genericTerm(TermPercent.class, term2, propertyName, (CSSProperty)((Object)listIdentification), range, properties, values))) {
                TermList terms = tf.createList(2);
                terms.add(term1);
                terms.add(term2);
                values.put(propertyName, terms);
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean processColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrColor(CSSProperty.Color.class, CSSProperty.Color.color, d, properties, values);
    }

    private boolean processBackground(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BackgroundVariator background = new BackgroundVariator();
        background.assignTermsFromDeclaration(d);
        background.assignDefaults(properties, values);
        return background.vary(properties, values);
    }

    private boolean processBackgroundAttachment(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BackgroundVariator background = new BackgroundVariator();
        return background.tryOneTermVariant(3, d, properties, values);
    }

    private boolean processBackgroundColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BackgroundVariator background = new BackgroundVariator();
        return background.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processBackgroundImage(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BackgroundVariator background = new BackgroundVariator();
        return background.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processBackgroundRepeat(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BackgroundVariator background = new BackgroundVariator();
        return background.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processBackgroundPosition(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BackgroundVariator background = new BackgroundVariator();
        return background.tryMultiTermVariant(4, properties, values, d.toArray(new Term[0]));
    }

    private boolean processBackgroundSize(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BackgroundVariator background = new BackgroundVariator();
        return background.tryMultiTermVariant(5, properties, values, d.toArray(new Term[0]));
    }

    private boolean processBorder(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderVariator border = new BorderVariator();
        border.assignTermsFromDeclaration(d);
        ((Variator)border).assignDefaults(properties, values);
        return border.vary(properties, values);
    }

    private boolean processBorderCollapse(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.BorderCollapse.class, d, properties);
    }

    private boolean processBorderTopColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("top");
        return borderSide.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processBorderRightColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("right");
        return borderSide.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processBorderBottomColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("bottom");
        return borderSide.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processBorderLeftColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("left");
        return borderSide.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processBorderTopStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("top");
        return borderSide.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processBorderRightStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("right");
        return borderSide.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processBorderBottomStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("bottom");
        return borderSide.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processBorderLeftStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("left");
        return borderSide.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processBorderSpacing(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1) {
            String propertyName;
            Term term = (Term)d.get(0);
            if (this.genericTermIdent(CSSProperty.BorderSpacing.class, term, false, propertyName = d.getProperty(), properties) || this.genericTermLength(term, propertyName, CSSProperty.BorderSpacing.list_values, ValueRange.DISALLOW_NEGATIVE, properties, values)) {
                if (properties.get(propertyName) == CSSProperty.BorderSpacing.list_values) {
                    TermList terms = tf.createList(2);
                    terms.add(term);
                    terms.add(term);
                    values.put(propertyName, terms);
                }
                return true;
            }
        } else if (d.size() == 2) {
            Term term1 = (Term)d.get(0);
            Term term2 = (Term)d.get(1);
            String propertyName = d.getProperty();
            if (this.genericTermLength(term1, propertyName, CSSProperty.BorderSpacing.list_values, ValueRange.DISALLOW_NEGATIVE, properties, values) && this.genericTermLength(term2, propertyName, CSSProperty.BorderSpacing.list_values, ValueRange.DISALLOW_NEGATIVE, properties, values)) {
                TermList terms = tf.createList(2);
                terms.add(term1);
                terms.add(term2);
                values.put(propertyName, terms);
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean processBorderColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderColorRepeater borderColor = new BorderColorRepeater();
        return borderColor.repeatOverFourTermDeclaration(d, properties, values);
    }

    private boolean processBorderStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderStyleRepeater borderStyle = new BorderStyleRepeater();
        return borderStyle.repeatOverFourTermDeclaration(d, properties, values);
    }

    private boolean processBorderTopWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("top");
        return borderSide.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processBorderRightWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("right");
        return borderSide.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processBorderBottomWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("bottom");
        return borderSide.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processBorderLeftWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("left");
        return borderSide.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processBorderWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderWidthRepeater borderWidth = new BorderWidthRepeater();
        return borderWidth.repeatOverFourTermDeclaration(d, properties, values);
    }

    private boolean processBorderTop(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("top");
        borderSide.assignTermsFromDeclaration(d);
        borderSide.assignDefaults(properties, values);
        return borderSide.vary(properties, values);
    }

    private boolean processBorderRight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("right");
        borderSide.assignTermsFromDeclaration(d);
        borderSide.assignDefaults(properties, values);
        return borderSide.vary(properties, values);
    }

    private boolean processBorderBottom(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("bottom");
        borderSide.assignTermsFromDeclaration(d);
        borderSide.assignDefaults(properties, values);
        return borderSide.vary(properties, values);
    }

    private boolean processBorderLeft(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderSideVariator borderSide = new BorderSideVariator("left");
        borderSide.assignTermsFromDeclaration(d);
        borderSide.assignDefaults(properties, values);
        return borderSide.vary(properties, values);
    }

    private boolean processBorderTopLeftRadius(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericTwoIdentsOrLengthsOrPercents(CSSProperty.BorderRadius.class, CSSProperty.BorderRadius.list_values, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processBorderTopRightRadius(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericTwoIdentsOrLengthsOrPercents(CSSProperty.BorderRadius.class, CSSProperty.BorderRadius.list_values, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processBorderBottomRightRadius(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericTwoIdentsOrLengthsOrPercents(CSSProperty.BorderRadius.class, CSSProperty.BorderRadius.list_values, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processBorderBottomLeftRadius(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericTwoIdentsOrLengthsOrPercents(CSSProperty.BorderRadius.class, CSSProperty.BorderRadius.list_values, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processBorderRadius(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        BorderRadiusRepeater radius = new BorderRadiusRepeater();
        return radius.repeatOverMultiTermDeclaration(d, properties, values);
    }

    private boolean processBoxShadow(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.BoxShadow.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        int lengthCount = 0;
        int lastLengthIndex = -1;
        int insetIndex = -1;
        int colorIndex = -1;
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (t.getOperator() == Term.Operator.COMMA) {
                if (lengthCount < 2) {
                    return false;
                }
                lengthCount = 0;
                lastLengthIndex = -1;
                insetIndex = -1;
                colorIndex = -1;
            }
            if (t instanceof TermColor && colorIndex < 0) {
                colorIndex = i;
            } else if (t instanceof TermIdent && ((String)((TermIdent)t).getValue()).equalsIgnoreCase("inset") && insetIndex < 0) {
                insetIndex = i;
            } else if (t instanceof TermLength && lastLengthIndex < 0 || lastLengthIndex > insetIndex && lastLengthIndex > colorIndex) {
                if (lengthCount >= 4) {
                    return false;
                }
                lastLengthIndex = i;
                ++lengthCount;
            } else {
                return false;
            }
            list.add(t);
        }
        if (lengthCount < 2) {
            return false;
        }
        properties.put(d.getProperty(), CSSProperty.BoxShadow.component_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processBoxSizing(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.BoxSizing.class, d, properties);
    }

    private boolean processFontFamily(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FontVariator font = new FontVariator();
        return font.tryMultiTermVariant(5, properties, values, d.toArray(new Term[0]));
    }

    private boolean processFontSize(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FontVariator font = new FontVariator();
        return font.tryOneTermVariant(3, d, properties, values);
    }

    private boolean processFontStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FontVariator font = new FontVariator();
        return font.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processFontVariant(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FontVariator font = new FontVariator();
        return font.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processFontWeight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FontVariator font = new FontVariator();
        return font.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processFont(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FontVariator font = new FontVariator();
        font.assignTermsFromDeclaration(d);
        font.assignDefaults(properties, values);
        return ((Variator)font).vary(properties, values);
    }

    private boolean processLineHeight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FontVariator font = new FontVariator();
        return font.tryOneTermVariant(4, d, properties, values);
    }

    private boolean processTabSize(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericIntegerOrLength(CSSProperty.TabSize.class, CSSProperty.TabSize.integer, CSSProperty.TabSize.length, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processTop(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Top.class, CSSProperty.Top.length, CSSProperty.Top.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processRight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Right.class, CSSProperty.Right.length, CSSProperty.Right.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processBottom(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Bottom.class, CSSProperty.Bottom.length, CSSProperty.Bottom.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processLeft(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Left.class, CSSProperty.Left.length, CSSProperty.Left.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processTransform(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.Transform.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (Term t : d.asList()) {
            if (t instanceof TermFunction.TransformFunction) {
                list.add(t);
                continue;
            }
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        properties.put("transform", CSSProperty.Transform.list_values);
        values.put("transform", list);
        return true;
    }

    private boolean processTransformOrigin(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericTermIdent(CSSProperty.BorderSpacing.class, (Term)d.get(0), false, d.getProperty(), properties)) {
            return true;
        }
        if (d.size() >= 1 && d.size() <= 3) {
            int i;
            TermLengthOrPercent hpos = null;
            TermLengthOrPercent vpos = null;
            TermLength zpos = null;
            for (i = 0; i < d.size(); ++i) {
                Term term = (Term)d.get(i);
                if (term instanceof TermIdent) {
                    String value = (String)((TermIdent)term).getValue();
                    if ("top".equals(value)) {
                        if (vpos == null) {
                            vpos = tf.createPercent(Float.valueOf(0.0f));
                            continue;
                        }
                        return false;
                    }
                    if ("bottom".equals(value)) {
                        if (vpos == null) {
                            vpos = tf.createPercent(Float.valueOf(100.0f));
                            continue;
                        }
                        return false;
                    }
                    if ("left".equals(value)) {
                        if (hpos == null) {
                            hpos = tf.createPercent(Float.valueOf(0.0f));
                            continue;
                        }
                        return false;
                    }
                    if ("right".equals(value)) {
                        if (hpos == null) {
                            hpos = tf.createPercent(Float.valueOf(100.0f));
                            continue;
                        }
                        return false;
                    }
                    if ("center".equals(value)) continue;
                    return false;
                }
                if (term instanceof TermLengthOrPercent) {
                    if (i <= 1 || !((TermLengthOrPercent)term).isPercentage()) continue;
                    return false;
                }
                return false;
            }
            for (i = 0; i < d.size(); ++i) {
                TermLengthOrPercent value = null;
                Term term = (Term)d.get(i);
                if (i < 2) {
                    if (term instanceof TermIdent) {
                        if ("center".equals(((TermIdent)term).getValue())) {
                            value = tf.createPercent(Float.valueOf(50.0f));
                        }
                    } else {
                        value = (TermLengthOrPercent)term;
                    }
                    if (value == null) continue;
                    if (hpos == null) {
                        hpos = value;
                        continue;
                    }
                    if (vpos == null) {
                        vpos = value;
                        continue;
                    }
                    return false;
                }
                zpos = (TermLength)term;
            }
            if (hpos == null) {
                hpos = tf.createPercent(Float.valueOf(50.0f));
            }
            if (vpos == null) {
                vpos = tf.createPercent(Float.valueOf(50.0f));
            }
            if (zpos == null) {
                zpos = tf.createLength(Float.valueOf(0.0f));
            }
            TermList list = tf.createList();
            list.add(hpos);
            list.add(vpos);
            list.add(zpos);
            properties.put("transform-origin", CSSProperty.TransformOrigin.list_values);
            values.put("transform-origin", list);
            return true;
        }
        return false;
    }

    private boolean processWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Width.class, CSSProperty.Width.length, CSSProperty.Width.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processHeight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Height.class, CSSProperty.Height.length, CSSProperty.Height.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processCaptionSide(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.CaptionSide.class, d, properties);
    }

    private boolean processClear(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Clear.class, d, properties);
    }

    private boolean processClip(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() != 1) {
            return false;
        }
        Term term = (Term)d.get(0);
        if (term instanceof TermIdent) {
            EnumSet<CSSProperty.Clip> allowedClips = EnumSet.allOf(CSSProperty.Clip.class);
            CSSProperty.Clip clip = this.genericPropertyRaw(CSSProperty.Clip.class, allowedClips, (TermIdent)term);
            if (clip != null) {
                properties.put("clip", clip);
                return true;
            }
            return false;
        }
        if (term instanceof TermRect) {
            return this.genericTerm(TermRect.class, term, "clip", CSSProperty.Clip.shape, ValueRange.ALLOW_ALL, properties, values);
        }
        return false;
    }

    private boolean processCounterIncrement(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.CounterIncrement.class, d, properties)) {
            return true;
        }
        List<Term<?>> termList = this.decodeCounterList(d.asList(), 1);
        if (termList != null && !termList.isEmpty()) {
            TermList list = tf.createList(termList.size());
            list.addAll(termList);
            properties.put("counter-increment", CSSProperty.CounterIncrement.list_values);
            values.put("counter-increment", list);
            return true;
        }
        return false;
    }

    private boolean processCounterReset(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.CounterReset.class, d, properties)) {
            return true;
        }
        List<Term<?>> termList = this.decodeCounterList(d.asList(), 0);
        if (termList != null && !termList.isEmpty()) {
            TermList list = tf.createList(termList.size());
            list.addAll(termList);
            properties.put("counter-reset", CSSProperty.CounterReset.list_values);
            values.put("counter-reset", list);
            return true;
        }
        return false;
    }

    private List<Term<?>> decodeCounterList(List<Term<?>> terms, int defaultValue) {
        ArrayList ret = new ArrayList();
        int i = 0;
        while (i < terms.size()) {
            Term<?> term = terms.get(i);
            if (term instanceof TermIdent) {
                String counterName = (String)((TermIdent)term).getValue();
                if (i + 1 < terms.size() && terms.get(i + 1) instanceof TermInteger) {
                    int counterValue = ((TermInteger)terms.get(i + 1)).getIntValue();
                    ret.add(tf.createPair(counterName, counterValue));
                    i += 2;
                    continue;
                }
                ret.add(tf.createPair(counterName, defaultValue));
                ++i;
                continue;
            }
            return null;
        }
        return ret;
    }

    private boolean processCursor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.Cursor.class, d, properties)) {
            return true;
        }
        EnumSet<CSSProperty.Cursor> allowedCursors = EnumSet.complementOf(EnumSet.of(CSSProperty.Cursor.INHERIT));
        TermList list = tf.createList();
        CSSProperty.Cursor cur = null;
        for (Term term : d.asList()) {
            if (term instanceof TermURI) {
                list.add(term);
                continue;
            }
            if (term instanceof TermIdent && (cur = this.genericPropertyRaw(CSSProperty.Cursor.class, allowedCursors, (TermIdent)term)) != null) {
                if (d.indexOf(term) != d.size() - 1) {
                    return false;
                }
                properties.put("cursor", cur);
                if (!list.isEmpty()) {
                    values.put("cursor", list);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean processDirection(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Direction.class, d, properties);
    }

    private boolean processDisplay(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Display.class, d, properties);
    }

    private boolean processEmptyCells(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.EmptyCells.class, d, properties);
    }

    private boolean processFloat(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Float.class, d, properties);
    }

    private boolean processListStyleImage(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        ListStyleVariator listStyle = new ListStyleVariator();
        return listStyle.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processListStylePosition(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        ListStyleVariator listStyle = new ListStyleVariator();
        return listStyle.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processListStyleType(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        ListStyleVariator listStyle = new ListStyleVariator();
        return listStyle.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processListStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        ListStyleVariator listStyle = new ListStyleVariator();
        listStyle.assignTermsFromDeclaration(d);
        listStyle.assignDefaults(properties, values);
        return listStyle.vary(properties, values);
    }

    private boolean processMarginTop(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Margin.class, CSSProperty.Margin.length, CSSProperty.Margin.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processMarginRight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Margin.class, CSSProperty.Margin.length, CSSProperty.Margin.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processMarginBottom(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Margin.class, CSSProperty.Margin.length, CSSProperty.Margin.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processMarginLeft(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Margin.class, CSSProperty.Margin.length, CSSProperty.Margin.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processMargin(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        MarginRepeater margin = new MarginRepeater();
        return margin.repeatOverFourTermDeclaration(d, properties, values);
    }

    private boolean processMaxHeight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.MaxHeight.class, CSSProperty.MaxHeight.length, CSSProperty.MaxHeight.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processMaxWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.MaxWidth.class, CSSProperty.MaxWidth.length, CSSProperty.MaxWidth.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processMinHeight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.MinHeight.class, CSSProperty.MinHeight.length, CSSProperty.MinHeight.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processMinWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.MinWidth.class, CSSProperty.MinWidth.length, CSSProperty.MinWidth.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processOpacity(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrIntegerOrNumber(CSSProperty.Opacity.class, CSSProperty.Opacity.number, CSSProperty.Opacity.number, ValueRange.TRUNCATE_NEGATIVE, d, properties, values);
    }

    private boolean processOrphans(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrInteger(CSSProperty.Orphans.class, CSSProperty.Orphans.integer, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processOutlineColor(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        OutlineVariator outline = new OutlineVariator();
        return outline.tryOneTermVariant(0, d, properties, values);
    }

    private boolean processOutlineStyle(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        OutlineVariator outline = new OutlineVariator();
        return outline.tryOneTermVariant(1, d, properties, values);
    }

    private boolean processOutlineWidth(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        OutlineVariator outline = new OutlineVariator();
        return outline.tryOneTermVariant(2, d, properties, values);
    }

    private boolean processOutline(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        OutlineVariator outline = new OutlineVariator();
        outline.assignTermsFromDeclaration(d);
        outline.assignDefaults(properties, values);
        return outline.vary(properties, values);
    }

    private boolean processOverflow(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1) {
            Term term = (Term)d.get(0);
            if (term instanceof TermIdent) {
                return this.genericProperty(CSSProperty.Overflow.class, (TermIdent)term, false, properties, "overflow-x") && this.genericProperty(CSSProperty.Overflow.class, (TermIdent)term, false, properties, "overflow-y");
            }
            return false;
        }
        return false;
    }

    private boolean processOverflowX(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Overflow.class, d, properties);
    }

    private boolean processOverflowY(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Overflow.class, d, properties);
    }

    private boolean processPaddingTop(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Padding.class, CSSProperty.Padding.length, CSSProperty.Padding.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processPaddingRight(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Padding.class, CSSProperty.Padding.length, CSSProperty.Padding.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processPaddingBottom(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Padding.class, CSSProperty.Padding.length, CSSProperty.Padding.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processPaddingLeft(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.Padding.class, CSSProperty.Padding.length, CSSProperty.Padding.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processPadding(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        PaddingRepeater padding = new PaddingRepeater();
        return padding.repeatOverFourTermDeclaration(d, properties, values);
    }

    private boolean processPageBreakAfter(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.PageBreak.class, d, properties);
    }

    private boolean processPageBreakBefore(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.PageBreak.class, d, properties);
    }

    private boolean processPageBreakInside(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.PageBreakInside.class, d, properties);
    }

    private boolean processPosition(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Position.class, d, properties);
    }

    private boolean processQuotes(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericTermIdent(CSSProperty.Quotes.class, (Term)d.get(0), false, "quotes", properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (Term term : d.asList()) {
            if (term instanceof TermString) {
                list.add(term);
                continue;
            }
            return false;
        }
        if (!list.isEmpty() && list.size() % 2 == 0) {
            properties.put("quotes", CSSProperty.Quotes.list_values);
            values.put("quotes", list);
            return true;
        }
        return false;
    }

    private boolean processTableLayout(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.TableLayout.class, d, properties);
    }

    private boolean processTextAlign(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.TextAlign.class, d, properties);
    }

    private boolean processTextDecoration(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        EnumSet<CSSProperty.TextDecoration> availableDecorations = EnumSet.of(CSSProperty.TextDecoration.BLINK, CSSProperty.TextDecoration.LINE_THROUGH, CSSProperty.TextDecoration.OVERLINE, CSSProperty.TextDecoration.UNDERLINE);
        if (d.size() == 1) {
            return this.genericOneIdent(CSSProperty.TextDecoration.class, d, properties);
        }
        TermList list = tf.createList();
        CSSProperty.TextDecoration dec = null;
        for (Term term : d.asList()) {
            if (term instanceof TermIdent && (dec = this.genericPropertyRaw(CSSProperty.TextDecoration.class, availableDecorations, (TermIdent)term)) != null) {
                list.add(tf.createTerm(dec));
                continue;
            }
            return false;
        }
        if (!list.isEmpty()) {
            properties.put("text-decoration", CSSProperty.TextDecoration.list_values);
            values.put("text-decoration", list);
            return true;
        }
        return false;
    }

    private boolean processTextIndent(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.TextIndent.class, CSSProperty.TextIndent.length, CSSProperty.TextIndent.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processTextTransform(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.TextTransform.class, d, properties);
    }

    private boolean processUnicodeBidi(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.UnicodeBidi.class, d, properties);
    }

    private boolean processUnicodeRange(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() > 0) {
            TermList list = tf.createList();
            for (int i = 0; i < d.size(); ++i) {
                Term term = (Term)d.get(i);
                if (!(term instanceof TermUnicodeRange && (i == 0 && term.getOperator() == null || i != 0 && term.getOperator() == Term.Operator.COMMA))) {
                    return false;
                }
                list.add(term);
            }
            properties.put("unicode-range", CSSProperty.UnicodeRange.list_values);
            values.put("unicode-range", list);
            return true;
        }
        return false;
    }

    private boolean processVerticalAlign(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.VerticalAlign.class, CSSProperty.VerticalAlign.length, CSSProperty.VerticalAlign.percentage, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processVisibility(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.Visibility.class, d, properties);
    }

    private boolean processWhiteSpace(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.WhiteSpace.class, d, properties);
    }

    private boolean processWidows(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrInteger(CSSProperty.Widows.class, CSSProperty.Widows.integer, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processWordSpacing(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLength(CSSProperty.WordSpacing.class, CSSProperty.WordSpacing.length, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processLetterSpacing(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLength(CSSProperty.LetterSpacing.class, CSSProperty.LetterSpacing.length, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processZIndex(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrInteger(CSSProperty.ZIndex.class, CSSProperty.ZIndex.integer, ValueRange.ALLOW_ALL, d, properties, values);
    }

    private boolean processAdditionalCSSGenericProperty(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1) {
            Term term = (Term)d.get(0);
            if (term instanceof TermIdent) {
                return this.genericProperty(CSSProperty.GenericCSSPropertyProxy.class, (TermIdent)term, true, properties, d.getProperty());
            }
            return this.genericTerm(TermLength.class, term, d.getProperty(), null, ValueRange.ALLOW_ALL, properties, values) || this.genericTerm(TermPercent.class, term, d.getProperty(), null, ValueRange.ALLOW_ALL, properties, values) || this.genericTerm(TermInteger.class, term, d.getProperty(), null, ValueRange.ALLOW_ALL, properties, values) || this.genericTermColor(term, d.getProperty(), null, properties, values);
        }
        log.warn("Ignoring unsupported property " + d.getProperty() + " with multiple values");
        return false;
    }

    private boolean processFlex(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FlexVariator variator = new FlexVariator();
        variator.assignTermsFromDeclaration(d);
        variator.assignDefaults(properties, values);
        return ((Variator)variator).vary(properties, values);
    }

    private boolean processFlexFlow(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        FlexFlowVariator variator = new FlexFlowVariator();
        variator.assignTermsFromDeclaration(d);
        variator.assignDefaults(properties, values);
        return variator.vary(properties, values);
    }

    private boolean processFlexBasis(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.FlexBasis.class, CSSProperty.FlexBasis.length, CSSProperty.FlexBasis.percentage, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processFlexDirection(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.FlexDirection.class, d, properties);
    }

    private boolean processFlexWrap(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.FlexWrap.class, d, properties);
    }

    private boolean processFlexGrow(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrIntegerOrNumber(CSSProperty.FlexGrow.class, CSSProperty.FlexGrow.number, CSSProperty.FlexGrow.number, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processFlexShrink(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrIntegerOrNumber(CSSProperty.FlexShrink.class, CSSProperty.FlexShrink.number, CSSProperty.FlexShrink.number, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processJustifyContent(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.JustifyContent.class, d, properties);
    }

    private boolean processAlignContent(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.AlignContent.class, d, properties);
    }

    private boolean processAlignItems(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.AlignItems.class, d, properties);
    }

    private boolean processAlignSelf(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdent(CSSProperty.AlignSelf.class, d, properties);
    }

    private boolean processOrder(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericInteger(CSSProperty.Order.class, CSSProperty.Order.integer, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processContent(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.Content.class, d, properties)) {
            return true;
        }
        HashSet<String> validTermIdents = new HashSet<String>(Arrays.asList("open-quote", "close-quote", "no-open-quote", "no-close-quote"));
        TermList list = tf.createList();
        for (Term t : d.asList()) {
            if (t instanceof TermIdent && validTermIdents.contains(((String)((TermIdent)t).getValue()).toLowerCase())) {
                list.add(t);
                continue;
            }
            if (t instanceof TermString) {
                list.add(t);
                continue;
            }
            if (t instanceof TermURI) {
                list.add(t);
                continue;
            }
            if (t instanceof TermFunction.CounterFunction || t instanceof TermFunction.Attr) {
                list.add(t);
                continue;
            }
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        properties.put("content", CSSProperty.Content.list_values);
        values.put("content", list);
        return true;
    }

    private boolean processFilter(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.Filter.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (Term t : d.asList()) {
            if (t instanceof TermFunction.FilterFunction) {
                list.add(t);
                continue;
            }
            if (t instanceof TermURI) {
                list.add(t);
                continue;
            }
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        properties.put("filter", CSSProperty.Filter.list_values);
        values.put("filter", list);
        return true;
    }

    private boolean processBackdropFilter(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.size() == 1 && this.genericOneIdent(CSSProperty.BackdropFilter.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (Term t : d.asList()) {
            if (t instanceof TermFunction.FilterFunction) {
                list.add(t);
                continue;
            }
            if (t instanceof TermURI) {
                list.add(t);
                continue;
            }
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        properties.put("backdrop-filter", CSSProperty.BackdropFilter.list_values);
        values.put("backdrop-filter", list);
        return true;
    }

    private boolean processGrid(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        Declaration templateDecl = rf.createDeclaration(d);
        templateDecl.setProperty("grid-template");
        if (this.processGridTemplate(templateDecl, properties, values)) {
            return true;
        }
        boolean beforeSlash = true;
        boolean autoFlowBeforeSlash = false;
        Declaration autoFlowDecl = (Declaration)rf.createDeclaration().unlock();
        autoFlowDecl.setProperty("grid-auto-flow");
        Declaration templateRowsDecl = (Declaration)rf.createDeclaration().unlock();
        templateRowsDecl.setProperty("grid-template-rows");
        Declaration autoRowsDecl = (Declaration)rf.createDeclaration().unlock();
        autoRowsDecl.setProperty("grid-auto-rows");
        Declaration templateColumnsDecl = (Declaration)rf.createDeclaration().unlock();
        templateColumnsDecl.setProperty("grid-template-columns");
        Declaration autoColumnsDecl = (Declaration)rf.createDeclaration().unlock();
        autoColumnsDecl.setProperty("grid-auto-columns");
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (t.getOperator() == Term.Operator.SLASH) {
                beforeSlash = false;
            }
            if (t instanceof TermIdent) {
                Enum property = this.genericPropertyRaw(CSSProperty.Grid.class, null, (TermIdent)t);
                if (CSSProperty.Grid.AUTO_FLOW.equals(property)) {
                    if (beforeSlash) {
                        autoFlowDecl.add(tf.createIdent("row"));
                    } else {
                        autoFlowDecl.add(tf.createIdent("column"));
                    }
                    autoFlowBeforeSlash = beforeSlash;
                    continue;
                }
                property = this.genericPropertyRaw(CSSProperty.GridAutoFlow.class, null, (TermIdent)t);
                if (CSSProperty.GridAutoFlow.DENSE.equals(property)) {
                    autoFlowDecl.add(t);
                    continue;
                }
            }
            if (autoFlowDecl.isEmpty()) {
                if (!beforeSlash) continue;
                templateRowsDecl.add(t);
                continue;
            }
            if (beforeSlash) {
                autoRowsDecl.add(t);
                continue;
            }
            if (autoFlowBeforeSlash) {
                templateColumnsDecl.add(t);
                continue;
            }
            autoColumnsDecl.add(t);
        }
        this.processGridAutoRows(autoRowsDecl, properties, values);
        this.processGridAutoColumns(autoColumnsDecl, properties, values);
        return this.processGridAutoFlow(autoFlowDecl, properties, values) && (this.processGridTemplateRows(templateRowsDecl, properties, values) || this.processGridTemplateColumns(templateColumnsDecl, properties, values));
    }

    private boolean processGridGap(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        Term rowGapTerm;
        Term columnGapTerm;
        switch (d.size()) {
            case 1: {
                rowGapTerm = columnGapTerm = (Term)d.get(0);
                break;
            }
            case 2: {
                rowGapTerm = (Term)d.get(0);
                columnGapTerm = (Term)d.get(1);
                break;
            }
            default: {
                return false;
            }
        }
        return !(!this.genericTermIdent(CSSProperty.GridGap.class, rowGapTerm, false, "grid-row-gap", properties) && !this.genericTermLength(rowGapTerm, "grid-row-gap", CSSProperty.GridGap.length, ValueRange.DISALLOW_NEGATIVE, properties, values) && !this.genericTerm(TermPercent.class, rowGapTerm, "grid-row-gap", CSSProperty.GridGap.length, ValueRange.DISALLOW_NEGATIVE, properties, values) || !this.genericTermIdent(CSSProperty.GridGap.class, columnGapTerm, false, "grid-column-gap", properties) && !this.genericTermLength(columnGapTerm, "grid-column-gap", CSSProperty.GridGap.length, ValueRange.DISALLOW_NEGATIVE, properties, values) && !this.genericTerm(TermPercent.class, columnGapTerm, "grid-column-gap", CSSProperty.GridGap.length, ValueRange.DISALLOW_NEGATIVE, properties, values));
    }

    private boolean processGridRowGap(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.GridGap.class, CSSProperty.GridGap.length, CSSProperty.GridGap.length, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processGridColumnGap(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.genericOneIdentOrLengthOrPercent(CSSProperty.GridGap.class, CSSProperty.GridGap.length, CSSProperty.GridGap.length, ValueRange.DISALLOW_NEGATIVE, d, properties, values);
    }

    private boolean processGridArea(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processNStartEnds(4, new String[]{"grid-row-start", "grid-column-start", "grid-row-end", "grid-column-end"}, d, properties, values);
    }

    private boolean processGridRow(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processNStartEnds(2, new String[]{"grid-row-start", "grid-row-end"}, d, properties, values);
    }

    private boolean processGridColumn(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processNStartEnds(2, new String[]{"grid-column-start", "grid-column-end"}, d, properties, values);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean processNStartEnds(int n, String[] propertyNames, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        int i;
        if (n != propertyNames.length) {
            return false;
        }
        TermList[] lists = new TermList[n];
        for (int i2 = 0; i2 < n; ++i2) {
            lists[i2] = tf.createList();
        }
        HashMap<String, TermList> identOnly = new HashMap<String, TermList>();
        int listIndex = 0;
        int valueValue = 0;
        int valueIndex = -1;
        int spanIndex = -1;
        int identIndex = -1;
        boolean autoSet = false;
        for (i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (t.getOperator() == Term.Operator.SLASH) {
                if (!autoSet && spanIndex < 0 && valueIndex < 0) {
                    identOnly.put(propertyNames[listIndex], lists[listIndex]);
                }
                valueIndex = -1;
                spanIndex = -1;
                identIndex = -1;
                autoSet = false;
                if (++listIndex >= n) {
                    return false;
                }
            }
            if (t instanceof TermIdent) {
                CSSProperty.GridStartEnd property = this.genericPropertyRaw(CSSProperty.GridStartEnd.class, null, (TermIdent)t);
                if (CSSProperty.GridStartEnd.AUTO.equals(property) && lists[listIndex].isEmpty()) {
                    autoSet = true;
                } else if (CSSProperty.GridStartEnd.SPAN.equals(property) && spanIndex < 0 && !autoSet && (valueIndex < 0 || valueValue > 0)) {
                    spanIndex = i;
                } else {
                    if (property != null || identIndex >= 0 || spanIndex >= 0 && valueIndex >= 0 && spanIndex >= valueIndex || autoSet) return false;
                    identIndex = i;
                }
            } else {
                if (!(t instanceof TermInteger) || ((TermInteger)t).getIntValue() == 0 || spanIndex >= 0 && ((TermInteger)t).getIntValue() <= 0 || valueIndex >= 0 || identIndex >= 0 && identIndex <= spanIndex || autoSet) return false;
                valueValue = ((TermInteger)t).getIntValue();
                valueIndex = i;
            }
            lists[listIndex].add(t);
        }
        if (!autoSet && spanIndex < 0 && valueIndex < 0) {
            identOnly.put(propertyNames[listIndex], lists[listIndex]);
        }
        block12: for (i = 1; i < n; ++i) {
            if (i <= listIndex) {
                this.setStartEndProperties(propertyNames[i], lists[i], properties, values);
                continue;
            }
            switch (propertyNames[i]) {
                case "grid-column-start": {
                    if (!identOnly.containsKey("grid-row-start")) continue block12;
                    this.setStartEndProperties(propertyNames[i], (TermList)identOnly.get("grid-row-start"), properties, values);
                    continue block12;
                }
                case "grid-row-end": {
                    if (!identOnly.containsKey("grid-row-start")) continue block12;
                    this.setStartEndProperties(propertyNames[i], (TermList)identOnly.get("grid-row-start"), properties, values);
                    continue block12;
                }
                case "grid-column-end": {
                    if (identOnly.containsKey("grid-column-start")) {
                        this.setStartEndProperties(propertyNames[i], (TermList)identOnly.get("grid-column-start"), properties, values);
                        continue block12;
                    }
                    if (!identOnly.containsKey("grid-row-start")) continue block12;
                    this.setStartEndProperties(propertyNames[i], (TermList)identOnly.get("grid-row-start"), properties, values);
                }
            }
        }
        return this.setStartEndProperties(propertyNames[0], lists[0], properties, values);
    }

    private boolean processGridRowStart(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridStartEnd(d, properties, values);
    }

    private boolean processGridRowEnd(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridStartEnd(d, properties, values);
    }

    private boolean processGridColumnStart(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridStartEnd(d, properties, values);
    }

    private boolean processGridColumnEnd(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridStartEnd(d, properties, values);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean processGridStartEnd(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.isEmpty()) {
            return false;
        }
        if (this.genericOneIdentOrInteger(CSSProperty.GridStartEnd.class, CSSProperty.GridStartEnd.number, ValueRange.DISALLOW_ZERO, d, properties, values)) {
            if (CSSProperty.GridStartEnd.SPAN.equals(properties.get(d.getProperty()))) return false;
            return true;
        }
        int valueValue = 0;
        int valueIndex = -1;
        int spanIndex = -1;
        int identIndex = -1;
        TermList list = tf.createList();
        int i = 0;
        while (i < d.size()) {
            Term t = (Term)d.get(i);
            if (t instanceof TermIdent) {
                CSSProperty.GridStartEnd property = this.genericPropertyRaw(CSSProperty.GridStartEnd.class, null, (TermIdent)t);
                if (CSSProperty.GridStartEnd.SPAN.equals(property) && spanIndex < 0 && (valueIndex < 0 || valueValue > 0)) {
                    spanIndex = i;
                } else {
                    if (property != null) return false;
                    if (identIndex >= 0) return false;
                    if (spanIndex >= 0 && valueIndex >= 0) {
                        if (spanIndex >= valueIndex) return false;
                    }
                    identIndex = i;
                }
            } else {
                if (!(t instanceof TermInteger)) return false;
                if (((TermInteger)t).getIntValue() == 0) return false;
                if (spanIndex >= 0) {
                    if (((TermInteger)t).getIntValue() <= 0) return false;
                }
                if (valueIndex >= 0) return false;
                if (identIndex >= 0) {
                    if (identIndex <= spanIndex) return false;
                }
                valueValue = ((TermInteger)t).getIntValue();
                valueIndex = i;
            }
            list.add(t);
            ++i;
        }
        return this.setStartEndProperties(d.getProperty(), list, properties, values);
    }

    private boolean setStartEndProperties(String propertyName, TermList list, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        switch (list.size()) {
            case 0: {
                return false;
            }
            case 1: {
                CSSProperty.GridStartEnd property;
                Term single = (Term)list.get(0);
                if (single instanceof TermIdent) {
                    CSSProperty.GridStartEnd identProperty = this.genericPropertyRaw(CSSProperty.GridStartEnd.class, null, (TermIdent)single);
                    if (CSSProperty.GridStartEnd.SPAN.equals(identProperty)) {
                        return false;
                    }
                    property = identProperty == CSSProperty.GridStartEnd.AUTO ? identProperty : CSSProperty.GridStartEnd.identificator;
                } else if (single instanceof TermInteger) {
                    property = CSSProperty.GridStartEnd.number;
                } else {
                    return false;
                }
                properties.put(propertyName, property);
                values.put(propertyName, single);
                break;
            }
            default: {
                properties.put(propertyName, CSSProperty.GridStartEnd.component_values);
                values.put(propertyName, list);
            }
        }
        return true;
    }

    private boolean processGridTemplate(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        d.setProperty("grid-template-areas");
        if (this.genericOneIdent(CSSProperty.GridTemplateAreas.class, d, properties)) {
            return true;
        }
        Declaration rowsDecl = (Declaration)rf.createDeclaration().unlock();
        rowsDecl.setProperty("grid-template-rows");
        Declaration columnsDecl = (Declaration)rf.createDeclaration().unlock();
        columnsDecl.setProperty("grid-template-columns");
        boolean beforeSlash = true;
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (t.getOperator() == Term.Operator.SLASH) {
                beforeSlash = false;
            }
            if (beforeSlash) {
                rowsDecl.add(t);
                continue;
            }
            columnsDecl.add(t);
        }
        if (this.processGridTemplateRows(rowsDecl, properties, values) && this.processGridTemplateColumns(columnsDecl, properties, values)) {
            return true;
        }
        TermList areasTerms = tf.createList();
        TermList rowsTerms = tf.createList();
        TermList columnsTerms = tf.createList();
        beforeSlash = true;
        boolean bracketedIdentUsed = false;
        boolean rowLengthSet = false;
        int areasInRow = 0;
        ArrayList<String[]> map = new ArrayList<String[]>();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (t.getOperator() == Term.Operator.SLASH) {
                bracketedIdentUsed = false;
                beforeSlash = false;
            }
            if (t instanceof TermString) {
                String[] rowAreas = ValidationUtils.getAreas((String)((TermString)t).getValue());
                if (rowAreas.length == 0 || !map.isEmpty() && rowAreas.length != areasInRow || !beforeSlash) {
                    return false;
                }
                areasInRow = rowAreas.length;
                map.add(rowAreas);
                rowLengthSet = false;
                areasTerms.add(t);
                continue;
            }
            if (t instanceof TermBracketedIdents) {
                if (bracketedIdentUsed) {
                    return false;
                }
                bracketedIdentUsed = true;
                if (beforeSlash) {
                    rowsTerms.add(t);
                    continue;
                }
                columnsTerms.add(t);
                continue;
            }
            if (this.isTermTrackBreadth(t)) {
                bracketedIdentUsed = false;
                if (beforeSlash) {
                    if (rowLengthSet) {
                        return false;
                    }
                    rowLengthSet = true;
                    rowsTerms.add(t);
                    continue;
                }
                columnsTerms.add(t);
                continue;
            }
            return false;
        }
        if (!ValidationUtils.containsRectangles((String[][])map.toArray((T[])new String[0][]))) {
            return false;
        }
        properties.put("grid-template-areas", CSSProperty.GridTemplateAreas.list_values);
        values.put("grid-template-areas", areasTerms);
        if (!rowsTerms.isEmpty()) {
            properties.put("grid-template-rows", CSSProperty.GridTemplateRowsColumns.list_values);
            values.put("grid-template-rows", rowsTerms);
        }
        if (!columnsTerms.isEmpty()) {
            properties.put("grid-template-columns", CSSProperty.GridTemplateRowsColumns.list_values);
            values.put("grid-template-columns", columnsTerms);
        }
        return true;
    }

    private boolean isTermTrackBreadth(Term<?> t) {
        if (t instanceof TermLengthOrPercent) {
            return true;
        }
        if (t instanceof TermIdent) {
            CSSProperty.GridTemplateRowsColumns property = this.genericPropertyRaw(CSSProperty.GridTemplateRowsColumns.class, null, (TermIdent)t);
            return property == CSSProperty.GridTemplateRowsColumns.AUTO || property == CSSProperty.GridTemplateRowsColumns.MIN_CONTENT || property == CSSProperty.GridTemplateRowsColumns.MAX_CONTENT;
        }
        return false;
    }

    private boolean processGridTemplateAreas(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.GridTemplateAreas.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        int areasInRow = 0;
        String[][] map = new String[d.size()][];
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (t instanceof TermString) {
                map[i] = ValidationUtils.getAreas((String)((TermString)t).getValue());
                if (map[i].length == 0 || i > 0 && map[i].length != areasInRow) {
                    return false;
                }
            } else {
                return false;
            }
            areasInRow = map[i].length;
            list.add(t);
        }
        if (!ValidationUtils.containsRectangles(map)) {
            return false;
        }
        properties.put(d.getProperty(), CSSProperty.GridTemplateAreas.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processGridTemplateRows(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridTemplateRowsColumns(d, properties, values);
    }

    private boolean processGridTemplateColumns(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridTemplateRowsColumns(d, properties, values);
    }

    private boolean processGridTemplateRowsColumns(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.isEmpty()) {
            return false;
        }
        if (this.genericOneIdent(CSSProperty.GridTemplateRowsColumns.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        boolean bracketedIdentUsed = false;
        boolean repeatUsed = false;
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (t instanceof TermIdent) {
                CSSProperty.GridTemplateRowsColumns property = this.genericPropertyRaw(CSSProperty.GridTemplateRowsColumns.class, null, (TermIdent)t);
                if (property == null || property == CSSProperty.GridTemplateRowsColumns.NONE) {
                    return false;
                }
            } else {
                if (t instanceof TermBracketedIdents) {
                    if (bracketedIdentUsed) {
                        return false;
                    }
                    bracketedIdentUsed = true;
                    list.add(t);
                    continue;
                }
                if (t instanceof TermFunction.Repeat && !repeatUsed) {
                    repeatUsed = true;
                } else if (!(t instanceof TermLengthOrPercent || t instanceof TermFunction.MinMax || t instanceof TermFunction.FitContent)) {
                    return false;
                }
            }
            list.add(t);
            bracketedIdentUsed = false;
        }
        properties.put(d.getProperty(), CSSProperty.GridTemplateRowsColumns.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean processGridAutoFlow(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.GridAutoFlow.class, d, properties)) {
            if (CSSProperty.GridAutoFlow.DENSE.equals(properties.get(d.getProperty()))) return false;
            return true;
        }
        boolean autoFlowSet = false;
        boolean denseSet = false;
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (!(t instanceof TermIdent)) return false;
            CSSProperty.GridAutoFlow property = this.genericPropertyRaw(CSSProperty.GridAutoFlow.class, null, (TermIdent)t);
            if ((CSSProperty.GridAutoFlow.ROW.equals(property) || CSSProperty.GridAutoFlow.COLUMN.equals(property)) && !autoFlowSet) {
                autoFlowSet = true;
            } else {
                if (!CSSProperty.GridAutoFlow.DENSE.equals(property)) return false;
                if (denseSet) return false;
                denseSet = true;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.GridAutoFlow.component_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processGridAutoRows(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridAutoRowsOrColumns(d, properties, values);
    }

    private boolean processGridAutoColumns(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processGridAutoRowsOrColumns(d, properties, values);
    }

    private boolean processGridAutoRowsOrColumns(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (d.isEmpty()) {
            return false;
        }
        if (this.genericOneIdentOrLengthOrPercent(CSSProperty.GridAutoRowsColumns.class, CSSProperty.GridAutoRowsColumns.length, CSSProperty.GridAutoRowsColumns.length, ValueRange.DISALLOW_NEGATIVE, d, properties, values)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            TermFunction.GridFunction f;
            Term t = (Term)d.get(i);
            if (t instanceof TermIdent) {
                CSSProperty.GridAutoRowsColumns property = this.genericPropertyRaw(CSSProperty.GridAutoRowsColumns.class, null, (TermIdent)t);
                if (property == null) {
                    return false;
                }
            } else if (t instanceof TermLengthOrPercent) {
                if (!DeclarationTransformerImpl.isPositive(t)) {
                    return false;
                }
            } else if (t instanceof TermFunction.MinMax) {
                f = (TermFunction.MinMax)t;
                if (f.getMin().getLenght() != null && !DeclarationTransformerImpl.isPositive(f.getMin().getLenght())) {
                    return false;
                }
                if (f.getMax().getLenght() != null && !DeclarationTransformerImpl.isPositive(f.getMax().getLenght())) {
                    return false;
                }
            } else if (t instanceof TermFunction.FitContent) {
                f = (TermFunction.FitContent)t;
                if (!DeclarationTransformerImpl.isPositive(f.getMaximum())) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.GridAutoRowsColumns.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private static boolean isPositive(Term<?> t) {
        if (t instanceof TermLengthOrPercent) {
            if (((Float)((TermLengthOrPercent)t).getValue()).floatValue() < 0.0f) {
                return false;
            }
        } else if (t instanceof TermFloatValue) {
            if (((Float)((TermFloatValue)t).getValue()).floatValue() < 0.0f) {
                return false;
            }
        } else if (t instanceof TermTime) {
            if (((Float)((TermTime)t).getValue()).floatValue() < 0.0f) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean processAnimation(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processPropertiesInList(new String[]{"animation-duration", "animation-timing-function", "animation-delay", "animation-iteration-count", "animation-direction", "animation-fill-mode", "animation-play-state", "animation-name"}, d, properties, values);
    }

    private boolean processAnimationDelay(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericTime(CSSProperty.AnimationDelay.class, CSSProperty.AnimationDelay.time, ValueRange.DISALLOW_NEGATIVE, d, properties, values)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermTime) {
                if (!DeclarationTransformerImpl.isPositive(t)) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.AnimationDelay.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processAnimationDirection(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.AnimationDirection.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermIdent) {
                CSSProperty.AnimationDirection property = this.genericPropertyRaw(CSSProperty.AnimationDirection.class, null, (TermIdent)t);
                if (property == null) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.AnimationDirection.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processAnimationDuration(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericTime(CSSProperty.AnimationDuration.class, CSSProperty.AnimationDuration.time, ValueRange.DISALLOW_NEGATIVE, d, properties, values)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermTime) {
                if (!DeclarationTransformerImpl.isPositive(t)) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.AnimationDuration.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processAnimationFillMode(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.AnimationFillMode.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermIdent) {
                CSSProperty.AnimationFillMode property = this.genericPropertyRaw(CSSProperty.AnimationFillMode.class, null, (TermIdent)t);
                if (property == null) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.AnimationFillMode.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processAnimationIterationCount(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdentOrInteger(CSSProperty.AnimationIterationCount.class, CSSProperty.AnimationIterationCount.number, ValueRange.DISALLOW_NEGATIVE, d, properties, values)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (i > 0 && t.getOperator() != Term.Operator.COMMA) {
                return false;
            }
            if (t instanceof TermIdent) {
                CSSProperty.AnimationIterationCount property = this.genericPropertyRaw(CSSProperty.AnimationIterationCount.class, null, (TermIdent)t);
                if (property == null) {
                    return false;
                }
            } else if (t instanceof TermFloatValue) {
                if (!DeclarationTransformerImpl.isPositive(t)) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.AnimationIterationCount.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processAnimationName(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.AnimationName.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if (i > 0 && t.getOperator() != Term.Operator.COMMA || !(t instanceof TermIdent)) {
                return false;
            }
            list.add(t);
        }
        if (list.size() == 1) {
            properties.put(d.getProperty(), CSSProperty.AnimationName.custom_ident);
            values.put(d.getProperty(), (Term<?>)list.get(0));
        } else {
            properties.put(d.getProperty(), CSSProperty.AnimationName.list_values);
            values.put(d.getProperty(), list);
        }
        return true;
    }

    private boolean processAnimationPlayState(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.AnimationPlayState.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermIdent) {
                CSSProperty.AnimationPlayState property = this.genericPropertyRaw(CSSProperty.AnimationPlayState.class, null, (TermIdent)t);
                if (property == null) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.AnimationPlayState.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processAnimationTimingFunction(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.AnimationTimingFunction.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            CSSProperty.AnimationTimingFunction property;
            Term t = (Term)d.get(i);
            if (i > 0 && t.getOperator() != Term.Operator.COMMA) {
                return false;
            }
            if (t instanceof TermIdent ? (property = this.genericPropertyRaw(CSSProperty.AnimationTimingFunction.class, null, (TermIdent)t)) == null : !(t instanceof TermFunction.TimingFunction)) {
                return false;
            }
            list.add(t);
        }
        if (list.size() == 1) {
            properties.put(d.getProperty(), CSSProperty.AnimationTimingFunction.timing_function);
            values.put(d.getProperty(), (Term<?>)list.get(0));
        } else {
            properties.put(d.getProperty(), CSSProperty.AnimationTimingFunction.list_values);
            values.put(d.getProperty(), list);
        }
        return true;
    }

    private boolean processPropertiesInList(String[] propertyList, Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        Declaration subDeclaration = (Declaration)rf.createDeclaration().unlock();
        TermList[] termLists = new TermList[propertyList.length];
        for (int i = 0; i < termLists.length; ++i) {
            termLists[i] = tf.createList();
        }
        boolean[] propertySet = new boolean[propertyList.length];
        Arrays.fill(propertySet, false);
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            subDeclaration.add(t);
            if (t.getOperator() == Term.Operator.COMMA) {
                Arrays.fill(propertySet, false);
            }
            for (int propertyIndex = 0; propertyIndex <= propertyList.length; ++propertyIndex) {
                if (propertyIndex == propertyList.length) {
                    return false;
                }
                if (propertySet[propertyIndex]) continue;
                subDeclaration.setProperty(propertyList[propertyIndex]);
                if (!this.parseDeclaration(subDeclaration, properties, values)) continue;
                propertySet[propertyIndex] = true;
                t.setOperator(termLists[propertyIndex].isEmpty() ? null : Term.Operator.COMMA);
                termLists[propertyIndex].add(t);
                break;
            }
            subDeclaration.clear();
        }
        for (int propertyIndex = 0; propertyIndex < propertyList.length; ++propertyIndex) {
            subDeclaration.setProperty(propertyList[propertyIndex]);
            subDeclaration.addAll(termLists[propertyIndex]);
            if (!subDeclaration.isEmpty() && !this.parseDeclaration(subDeclaration, properties, values)) {
                return false;
            }
            subDeclaration.clear();
        }
        return true;
    }

    private boolean processTransition(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        return this.processPropertiesInList(new String[]{"transition-duration", "transition-delay", "transition-timing-function", "transition-property"}, d, properties, values);
    }

    private boolean processTransitionDelay(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericTime(CSSProperty.TransitionDelay.class, CSSProperty.TransitionDelay.time, ValueRange.DISALLOW_NEGATIVE, d, properties, values)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermTime) {
                if (!DeclarationTransformerImpl.isPositive(t)) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.TransitionDelay.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processTransitionDuration(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericTime(CSSProperty.TransitionDuration.class, CSSProperty.TransitionDuration.time, ValueRange.DISALLOW_NEGATIVE, d, properties, values)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermTime) {
                if (!DeclarationTransformerImpl.isPositive(t)) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        properties.put(d.getProperty(), CSSProperty.TransitionDuration.list_values);
        values.put(d.getProperty(), list);
        return true;
    }

    private boolean processTransitionProperty(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.TransitionProperty.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            Term t = (Term)d.get(i);
            if ((i == 0 || t.getOperator() == Term.Operator.COMMA) && t instanceof TermIdent) {
                CSSProperty.TransitionProperty property = this.genericPropertyRaw(CSSProperty.TransitionProperty.class, null, (TermIdent)t);
                if (property == CSSProperty.TransitionProperty.NONE) {
                    return false;
                }
            } else {
                return false;
            }
            list.add(t);
        }
        if (list.size() == 1) {
            properties.put(d.getProperty(), CSSProperty.TransitionProperty.custom_ident);
            values.put(d.getProperty(), (Term<?>)list.get(0));
        } else {
            properties.put(d.getProperty(), CSSProperty.TransitionProperty.list_values);
            values.put(d.getProperty(), list);
        }
        return true;
    }

    private boolean processTransitionTimingFunction(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
        if (this.genericOneIdent(CSSProperty.TransitionTimingFunction.class, d, properties)) {
            return true;
        }
        TermList list = tf.createList();
        for (int i = 0; i < d.size(); ++i) {
            CSSProperty.TransitionTimingFunction property;
            Term t = (Term)d.get(i);
            if (i > 0 && t.getOperator() != Term.Operator.COMMA) {
                return false;
            }
            if (t instanceof TermIdent ? (property = this.genericPropertyRaw(CSSProperty.TransitionTimingFunction.class, null, (TermIdent)t)) == null : !(t instanceof TermFunction.TimingFunction)) {
                return false;
            }
            list.add(t);
        }
        if (list.size() == 1) {
            properties.put(d.getProperty(), CSSProperty.TransitionTimingFunction.timing_function);
            values.put(d.getProperty(), (Term<?>)list.get(0));
        } else {
            properties.put(d.getProperty(), CSSProperty.TransitionTimingFunction.list_values);
            values.put(d.getProperty(), list);
        }
        return true;
    }

    static {
        rf = CSSFactory.getRuleFactory();
        tf = CSSFactory.getTermFactory();
        css = CSSFactory.getSupportedCSS();
        instance = new DeclarationTransformerImpl();
    }

    private final class PaddingRepeater
    extends Repeater {
        public PaddingRepeater() {
            super(4);
            this.names.add("padding-top");
            this.names.add("padding-right");
            this.names.add("padding-bottom");
            this.names.add("padding-left");
            this.type = CSSProperty.Padding.class;
        }

        @Override
        protected boolean operation(int i, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            return DeclarationTransformerImpl.this.genericTermIdent(this.type, (Term)this.terms.get(i), true, (String)this.names.get(i), properties) || DeclarationTransformerImpl.this.genericTermLength((Term)this.terms.get(i), (String)this.names.get(i), CSSProperty.Padding.length, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermPercent.class, (Term)this.terms.get(i), (String)this.names.get(i), CSSProperty.Padding.percentage, ValueRange.DISALLOW_NEGATIVE, properties, values);
        }
    }

    private final class MarginRepeater
    extends Repeater {
        public MarginRepeater() {
            super(4);
            this.type = CSSProperty.Margin.class;
            this.names.add("margin-top");
            this.names.add("margin-right");
            this.names.add("margin-bottom");
            this.names.add("margin-left");
        }

        @Override
        protected boolean operation(int i, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            return DeclarationTransformerImpl.this.genericTermIdent(this.type, (Term)this.terms.get(i), true, (String)this.names.get(i), properties) || DeclarationTransformerImpl.this.genericTermLength((Term)this.terms.get(i), (String)this.names.get(i), CSSProperty.Margin.length, ValueRange.ALLOW_ALL, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermPercent.class, (Term)this.terms.get(i), (String)this.names.get(i), CSSProperty.Margin.percentage, ValueRange.ALLOW_ALL, properties, values);
        }
    }

    private final class BorderRadiusRepeater
    extends Repeater {
        public BorderRadiusRepeater() {
            super(4);
            this.type = CSSProperty.BorderRadius.class;
            this.names.add("border-top-left-radius");
            this.names.add("border-top-right-radius");
            this.names.add("border-bottom-right-radius");
            this.names.add("border-bottom-left-radius");
        }

        @Override
        protected boolean operation(int i, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            Term term = (Term)this.terms.get(i);
            String name = (String)this.names.get(i);
            if (DeclarationTransformerImpl.this.genericTermIdent(this.type, (Term)this.terms.get(i), true, (String)this.names.get(i), properties)) {
                return true;
            }
            if (term instanceof TermList) {
                properties.put(name, CSSProperty.BorderRadius.list_values);
                values.put(name, term);
                return true;
            }
            return false;
        }

        public boolean repeatOverMultiTermDeclaration(Declaration d, Map<String, CSSProperty> properties, Map<String, Term<?>> values) throws IllegalArgumentException {
            Term term;
            if (d.size() == 1 && (term = (Term)d.get(0)) instanceof TermIdent && "INHERIT".equalsIgnoreCase((String)((TermIdent)term).getValue())) {
                Object property = CSSProperty.Translator.createInherit(this.type);
                for (int i = 0; i < this.times; ++i) {
                    properties.put((String)this.names.get(i), (CSSProperty)property);
                }
                return true;
            }
            int slash = -1;
            for (int i = 0; i < d.size(); ++i) {
                Term term2 = (Term)d.get(i);
                if (term2.getOperator() != Term.Operator.SLASH) continue;
                slash = i;
                break;
            }
            if (slash == -1) {
                Term<?>[] sterms = this.createFourTerms(d, 0, d.size());
                for (int i = 0; i < 4; ++i) {
                    TermList list = tf.createList(2);
                    list.add(sterms[i]);
                    list.add(sterms[i]);
                    this.terms.add(list);
                }
            } else {
                Term<?>[] sterms1 = this.createFourTerms(d, 0, slash);
                Term<?>[] sterms2 = this.createFourTerms(d, slash, d.size());
                for (int i = 0; i < 4; ++i) {
                    TermList list = tf.createList(2);
                    list.add(sterms1[i]);
                    list.add(sterms2[i]);
                    this.terms.add(list);
                }
            }
            return this.repeat(properties, values);
        }

        private Term<?>[] createFourTerms(Declaration d, int fromIndex, int toIndex) throws IllegalArgumentException {
            int i;
            int size = toIndex - fromIndex;
            Term[] ret = new Term[4];
            switch (size) {
                case 1: {
                    ret[2] = ret[3] = (Term)d.get(fromIndex);
                    ret[1] = ret[3];
                    ret[0] = ret[3];
                    break;
                }
                case 2: {
                    ret[0] = ret[2] = (Term)d.get(fromIndex);
                    ret[1] = ret[3] = (Term)d.get(fromIndex + 1);
                    break;
                }
                case 3: {
                    ret[0] = (Term)d.get(fromIndex);
                    ret[1] = ret[3] = (Term)d.get(fromIndex + 1);
                    ret[2] = (Term)d.get(fromIndex + 2);
                    break;
                }
                case 4: {
                    for (i = 0; i < 4; ++i) {
                        ret[i] = (Term)d.get(fromIndex + i);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid length of terms in Repeater.");
                }
            }
            if (fromIndex != 0) {
                for (i = 0; i < 4; ++i) {
                    if (ret[i].getOperator() != Term.Operator.SLASH) continue;
                    ret[i] = this.stripSlash(ret[i]);
                }
            }
            return ret;
        }

        private Term<?> stripSlash(Term<?> src) {
            if (src.getOperator() == Term.Operator.SLASH) {
                if (src instanceof TermLength) {
                    return tf.createLength((Float)src.getValue(), ((TermLength)src).getUnit());
                }
                if (src instanceof TermPercent) {
                    return tf.createPercent((Float)src.getValue());
                }
                return src;
            }
            return src;
        }
    }

    private final class BorderWidthRepeater
    extends Repeater {
        public BorderWidthRepeater() {
            super(4);
            this.type = CSSProperty.BorderWidth.class;
            this.names.add("border-top-width");
            this.names.add("border-right-width");
            this.names.add("border-bottom-width");
            this.names.add("border-left-width");
        }

        @Override
        protected boolean operation(int i, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            return DeclarationTransformerImpl.this.genericTermIdent(this.type, (Term)this.terms.get(i), false, (String)this.names.get(i), properties) || DeclarationTransformerImpl.this.genericTermLength((Term)this.terms.get(i), (String)this.names.get(i), CSSProperty.BorderWidth.length, ValueRange.DISALLOW_NEGATIVE, properties, values);
        }
    }

    private final class BorderColorRepeater
    extends Repeater {
        public BorderColorRepeater() {
            super(4);
            this.type = CSSProperty.BorderColor.class;
            this.names.add("border-top-color");
            this.names.add("border-right-color");
            this.names.add("border-bottom-color");
            this.names.add("border-left-color");
        }

        @Override
        protected boolean operation(int i, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            return DeclarationTransformerImpl.this.genericTermIdent(this.type, (Term)this.terms.get(i), false, (String)this.names.get(i), properties) || DeclarationTransformerImpl.this.genericTerm(TermColor.class, (Term)this.terms.get(i), (String)this.names.get(i), CSSProperty.BorderColor.color, ValueRange.ALLOW_ALL, properties, values);
        }
    }

    private final class BorderStyleRepeater
    extends Repeater {
        public BorderStyleRepeater() {
            super(4);
            this.type = CSSProperty.BorderStyle.class;
            this.names.add("border-top-style");
            this.names.add("border-right-style");
            this.names.add("border-bottom-style");
            this.names.add("border-left-style");
        }

        @Override
        protected boolean operation(int i, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            return DeclarationTransformerImpl.this.genericTermIdent(CSSProperty.BorderStyle.class, (Term)this.terms.get(i), false, (String)this.names.get(i), properties);
        }
    }

    private final class BorderVariator
    extends Variator {
        public static final int WIDTH = 0;
        public static final int STYLE = 1;
        public static final int COLOR = 2;
        private List<Repeater> repeaters;

        public BorderVariator() {
            super(3);
            this.types.add(CSSProperty.BorderWidth.class);
            this.types.add(CSSProperty.BorderStyle.class);
            this.types.add(CSSProperty.BorderColor.class);
            this.repeaters = new ArrayList<Repeater>(this.variants);
            this.repeaters.add(new BorderWidthRepeater());
            this.repeaters.add(new BorderStyleRepeater());
            this.repeaters.add(new BorderColorRepeater());
        }

        @Override
        protected boolean variant(int variant, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            int i = iteration.get();
            Term term = (Term)this.terms.get(i);
            switch (variant) {
                case 0: {
                    Repeater r = this.repeaters.get(0);
                    r.assignTerms(term, term, term, term);
                    return r.repeat(properties, values);
                }
                case 1: {
                    Repeater r = this.repeaters.get(1);
                    r.assignTerms(term, term, term, term);
                    return r.repeat(properties, values);
                }
                case 2: {
                    Repeater r = this.repeaters.get(2);
                    r.assignTerms(term, term, term, term);
                    return r.repeat(properties, values);
                }
            }
            return false;
        }

        @Override
        protected boolean checkInherit(int variant, Term<?> term, Map<String, CSSProperty> properties) {
            if (!(term instanceof TermIdent) || !"INHERIT".equalsIgnoreCase((String)((TermIdent)term).getValue())) {
                return false;
            }
            if (variant == -1) {
                for (int i = 0; i < this.variants; ++i) {
                    Repeater r = this.repeaters.get(i);
                    r.assignTerms(term, term, term, term);
                    r.repeat(properties, null);
                }
                return true;
            }
            Repeater r = this.repeaters.get(variant);
            r.assignTerms(term, term, term, term);
            r.repeat(properties, null);
            return true;
        }

        @Override
        public void assignDefaults(Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            for (Repeater r : this.repeaters) {
                r.assignDefaults(properties, values);
            }
        }
    }

    private final class FlexFlowVariator
    extends Variator {
        public static final int DIRECTION = 0;
        public static final int WRAP = 1;

        public FlexFlowVariator() {
            super(2);
            this.names.add("flex-direction");
            this.types.add(CSSProperty.FlexDirection.class);
            this.names.add("flex-wrap");
            this.types.add(CSSProperty.FlexWrap.class);
        }

        @Override
        protected boolean variant(int v, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            int i = iteration.get();
            switch (v) {
                case 0: {
                    return DeclarationTransformerImpl.this.genericTermIdent(CSSProperty.FlexDirection.class, (Term)this.terms.get(i), true, (String)this.names.get(0), properties);
                }
                case 1: {
                    return DeclarationTransformerImpl.this.genericTermIdent(CSSProperty.FlexWrap.class, (Term)this.terms.get(i), true, (String)this.names.get(1), properties);
                }
            }
            return false;
        }
    }

    private final class FlexVariator
    extends Variator {
        public static final int GROW = 0;
        public static final int SHRINK = 1;
        public static final int BASIS = 2;

        public FlexVariator() {
            super(3);
            this.names.add("flex-grow");
            this.types.add(CSSProperty.FlexGrow.class);
            this.names.add("flex-shrink");
            this.types.add(CSSProperty.FlexShrink.class);
            this.names.add("flex-basis");
            this.types.add(CSSProperty.FlexBasis.class);
        }

        @Override
        protected boolean variant(int v, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            int i = iteration.get();
            switch (v) {
                case 0: {
                    return DeclarationTransformerImpl.this.genericTerm(TermNumber.class, (Term)this.terms.get(i), (String)this.names.get(0), CSSProperty.FlexGrow.number, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermInteger.class, (Term)this.terms.get(i), (String)this.names.get(0), CSSProperty.FlexGrow.number, ValueRange.DISALLOW_NEGATIVE, properties, values);
                }
                case 1: {
                    return DeclarationTransformerImpl.this.genericTerm(TermNumber.class, (Term)this.terms.get(i), (String)this.names.get(1), CSSProperty.FlexShrink.number, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermInteger.class, (Term)this.terms.get(i), (String)this.names.get(1), CSSProperty.FlexShrink.number, ValueRange.DISALLOW_NEGATIVE, properties, values);
                }
                case 2: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(2), (Term)this.terms.get(i), true, (String)this.names.get(2), properties) || DeclarationTransformerImpl.this.genericTerm(TermPercent.class, (Term)this.terms.get(i), (String)this.names.get(2), CSSProperty.FlexBasis.percentage, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermLength.class, (Term)this.terms.get(i), (String)this.names.get(2), CSSProperty.FlexBasis.length, ValueRange.DISALLOW_NEGATIVE, properties, values);
                }
            }
            return false;
        }

        @Override
        protected boolean variantCondition(int variant, Variator.IntegerRef iteration) {
            switch (variant) {
                case 1: {
                    return this.variantPassed[0];
                }
            }
            return true;
        }

        @Override
        public boolean vary(Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            if (this.terms.size() == 1 && this.terms.get(0) instanceof TermIdent) {
                if (this.checkInherit(-1, (Term)this.terms.get(0), properties)) {
                    return true;
                }
                if (((Term)this.terms.get(0)).equals(tf.createIdent("none"))) {
                    values.put((String)this.names.get(1), tf.createNumber(Float.valueOf(0.0f)));
                    return true;
                }
            }
            boolean ret = super.vary(properties, values);
            if (this.variantPassed[2] && !this.variantPassed[0] && properties.get(this.names.get(2)) == CSSProperty.FlexBasis.AUTO) {
                values.put((String)this.names.get(0), tf.createNumber(Float.valueOf(1.0f)));
            }
            if (this.variantPassed[0] && !this.variantPassed[2]) {
                properties.put((String)this.names.get(2), CSSProperty.FlexBasis.length);
                values.put((String)this.names.get(2), tf.createLength(Float.valueOf(0.0f)));
            }
            return ret;
        }
    }

    private final class BackgroundVariator
    extends Variator {
        public static final int COLOR = 0;
        public static final int IMAGE = 1;
        public static final int REPEAT = 2;
        public static final int ATTACHMENT = 3;
        public static final int POSITION = 4;
        public static final int SIZE = 5;

        public BackgroundVariator() {
            super(6);
            this.names.add("background-color");
            this.types.add(CSSProperty.BackgroundColor.class);
            this.names.add("background-image");
            this.types.add(CSSProperty.BackgroundImage.class);
            this.names.add("background-repeat");
            this.types.add(CSSProperty.BackgroundRepeat.class);
            this.names.add("background-attachment");
            this.types.add(CSSProperty.BackgroundAttachment.class);
            this.names.add("background-position");
            this.types.add(CSSProperty.BackgroundPosition.class);
            this.names.add("background-size");
            this.types.add(CSSProperty.BackgroundSize.class);
        }

        @Override
        protected boolean variant(int v, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            switch (v) {
                case 0: {
                    int i;
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(0), (Term)this.terms.get(i), true, (String)this.names.get(0), properties) || DeclarationTransformerImpl.this.genericTermColor((Term)this.terms.get(i), (String)this.names.get(0), CSSProperty.BackgroundColor.color, properties, values);
                }
                case 1: {
                    int i;
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(1), (Term)this.terms.get(i), true, (String)this.names.get(1), properties) || DeclarationTransformerImpl.this.genericTerm(TermURI.class, (Term)this.terms.get(i), (String)this.names.get(1), CSSProperty.BackgroundImage.uri, ValueRange.ALLOW_ALL, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermFunction.Gradient.class, (Term)this.terms.get(i), (String)this.names.get(1), CSSProperty.BackgroundImage.gradient, ValueRange.ALLOW_ALL, properties, values);
                }
                case 2: {
                    int i;
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(2), (Term)this.terms.get(i), true, (String)this.names.get(2), properties);
                }
                case 3: {
                    int i;
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(3), (Term)this.terms.get(i), true, (String)this.names.get(3), properties);
                }
                case 4: {
                    Term term;
                    int i;
                    EnumSet<CSSProperty.BackgroundPosition> allowedBackground = EnumSet.complementOf(EnumSet.of(CSSProperty.BackgroundPosition.list_values, CSSProperty.BackgroundPosition.INHERIT));
                    CSSProperty.BackgroundPosition bp = null;
                    Term[] vv = new Term[]{null, null};
                    for (i = iteration.get(); i < this.terms.size() && (term = (Term)this.terms.get(i)).getOperator() != Term.Operator.SLASH; ++i) {
                        if (term instanceof TermIdent) {
                            bp = DeclarationTransformerImpl.this.genericPropertyRaw(CSSProperty.BackgroundPosition.class, allowedBackground, (TermIdent)term);
                            if (bp == null) continue;
                            this.storeBackgroundPosition(vv, bp, term);
                            continue;
                        }
                        if (term instanceof TermPercent) {
                            this.storeBackgroundPosition(vv, null, term);
                            continue;
                        }
                        if (!(term instanceof TermLength)) break;
                        this.storeBackgroundPosition(vv, null, term);
                    }
                    int assigned = 0;
                    TermList list = tf.createList(2);
                    for (int j = 0; j < 2; ++j) {
                        if (vv[j] == null) {
                            list.add(tf.createPercent(Float.valueOf(50.0f)));
                            continue;
                        }
                        list.add(vv[j]);
                        ++assigned;
                    }
                    if (assigned == 0) {
                        return false;
                    }
                    if (assigned == 2) {
                        iteration.inc();
                    }
                    properties.put((String)this.names.get(4), CSSProperty.BackgroundPosition.list_values);
                    values.put((String)this.names.get(4), list);
                    return true;
                }
                case 5: {
                    int i;
                    EnumSet<CSSProperty.BackgroundSize> allowedSize = EnumSet.complementOf(EnumSet.of(CSSProperty.BackgroundSize.list_values, CSSProperty.BackgroundSize.INHERIT));
                    CSSProperty.BackgroundSize bs = null;
                    Term[] sz = new Term[]{null, null};
                    int vi = 0;
                    while (i < this.terms.size() && vi < 2) {
                        Term term = (Term)this.terms.get(i);
                        if (term instanceof TermIdent) {
                            bs = DeclarationTransformerImpl.this.genericPropertyRaw(CSSProperty.BackgroundSize.class, allowedSize, (TermIdent)term);
                            if (bs != null) {
                                properties.put((String)this.names.get(5), bs);
                                values.remove(this.names.get(5));
                                return true;
                            }
                            if (term.getValue().equals("auto")) {
                                sz[vi++] = term;
                            }
                        } else {
                            if (!(term instanceof TermPercent) && !(term instanceof TermLength)) break;
                            sz[vi++] = term;
                        }
                        ++i;
                    }
                    if (sz[0] == null) {
                        return false;
                    }
                    if (sz[1] == null) {
                        sz[1] = tf.createIdent("auto");
                    } else {
                        iteration.inc();
                    }
                    TermList szlist = tf.createList(2);
                    szlist.add(sz[0]);
                    szlist.add(sz[1]);
                    properties.put((String)this.names.get(5), CSSProperty.BackgroundSize.list_values);
                    values.put((String)this.names.get(5), szlist);
                    return true;
                }
            }
            return false;
        }

        private void storeBackgroundPosition(Term<?>[] storage, CSSProperty.BackgroundPosition bp, Term<?> term) {
            if (bp == CSSProperty.BackgroundPosition.LEFT) {
                this.setPositionValue(storage, 0, tf.createPercent(Float.valueOf(0.0f)));
            } else if (bp == CSSProperty.BackgroundPosition.RIGHT) {
                this.setPositionValue(storage, 0, tf.createPercent(Float.valueOf(100.0f)));
            } else if (bp == CSSProperty.BackgroundPosition.TOP) {
                this.setPositionValue(storage, 1, tf.createPercent(Float.valueOf(0.0f)));
            } else if (bp == CSSProperty.BackgroundPosition.BOTTOM) {
                this.setPositionValue(storage, 1, tf.createPercent(Float.valueOf(100.0f)));
            } else if (bp == CSSProperty.BackgroundPosition.CENTER) {
                this.setPositionValue(storage, -1, tf.createPercent(Float.valueOf(50.0f)));
            } else {
                this.setPositionValue(storage, -1, term);
            }
        }

        private void setPositionValue(Term<?>[] s, int index, Term<?> term) {
            switch (index) {
                case -1: {
                    if (s[0] == null) {
                        s[0] = term;
                        break;
                    }
                    s[1] = term;
                    break;
                }
                case 0: {
                    if (s[0] != null) {
                        s[1] = s[0];
                    }
                    s[0] = term;
                    break;
                }
                case 1: {
                    if (s[1] != null) {
                        s[0] = s[1];
                    }
                    s[1] = term;
                }
            }
        }

        @Override
        protected boolean variantCondition(int variant, Variator.IntegerRef iteration) {
            switch (variant) {
                case 4: {
                    if (this.variantPassed[5]) {
                        return false;
                    }
                    return ((Term)this.terms.get(iteration.get())).getOperator() != Term.Operator.SLASH;
                }
                case 5: {
                    if (!this.variantPassed[4]) {
                        return false;
                    }
                    return ((Term)this.terms.get(iteration.get())).getOperator() == Term.Operator.SLASH;
                }
            }
            return true;
        }
    }

    private final class FontVariator
    extends Variator {
        public static final int STYLE = 0;
        public static final int VARIANT = 1;
        public static final int WEIGHT = 2;
        public static final int SIZE = 3;
        public static final int LINE_HEIGHT = 4;
        public static final int FAMILY = 5;

        public FontVariator() {
            super(6);
            this.names.add("font-style");
            this.types.add(CSSProperty.FontStyle.class);
            this.names.add("font-variant");
            this.types.add(CSSProperty.FontVariant.class);
            this.names.add("font-weight");
            this.types.add(CSSProperty.FontWeight.class);
            this.names.add("font-size");
            this.types.add(CSSProperty.FontSize.class);
            this.names.add("line-height");
            this.types.add(CSSProperty.LineHeight.class);
            this.names.add("font-family");
            this.types.add(CSSProperty.FontFamily.class);
        }

        @Override
        protected boolean variant(int v, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            int i = iteration.get();
            switch (v) {
                case 0: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(0), (Term)this.terms.get(i), true, (String)this.names.get(0), properties);
                }
                case 1: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(1), (Term)this.terms.get(i), true, (String)this.names.get(1), properties);
                }
                case 2: {
                    Integer[] fontWeight = new Integer[]{100, 200, 300, 400, 500, 600, 700, 800, 900};
                    Term term = (Term)this.terms.get(i);
                    if (term instanceof TermIdent) {
                        return DeclarationTransformerImpl.this.genericProperty((Class)this.types.get(2), (TermIdent)term, true, properties, (String)this.names.get(2));
                    }
                    if (term instanceof TermInteger) {
                        Integer test;
                        int result;
                        Integer value = ((TermInteger)term).getIntValue();
                        Integer[] integerArray = fontWeight;
                        int n = integerArray.length;
                        for (int j = 0; j < n && (result = value.compareTo(test = integerArray[j])) >= 0; ++j) {
                            if (result != 0) continue;
                            Object property = CSSProperty.Translator.valueOf((Class)this.types.get(2), "numeric_" + value);
                            if (property == null) {
                                log.warn("Not found numeric values for FontWeight: numeric_ " + value);
                                return false;
                            }
                            properties.put((String)this.names.get(2), (CSSProperty)property);
                            return true;
                        }
                    }
                    return false;
                }
                case 3: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(3), (Term)this.terms.get(i), true, (String)this.names.get(3), properties) || DeclarationTransformerImpl.this.genericTermLength((Term)this.terms.get(i), (String)this.names.get(3), CSSProperty.FontSize.length, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermPercent.class, (Term)this.terms.get(i), (String)this.names.get(3), CSSProperty.FontSize.percentage, ValueRange.DISALLOW_NEGATIVE, properties, values);
                }
                case 4: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(4), (Term)this.terms.get(i), true, (String)this.names.get(4), properties) || DeclarationTransformerImpl.this.genericTerm(TermNumber.class, (Term)this.terms.get(i), (String)this.names.get(4), CSSProperty.LineHeight.number, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermInteger.class, (Term)this.terms.get(i), (String)this.names.get(4), CSSProperty.LineHeight.number, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermPercent.class, (Term)this.terms.get(i), (String)this.names.get(4), CSSProperty.LineHeight.percentage, ValueRange.DISALLOW_NEGATIVE, properties, values) || DeclarationTransformerImpl.this.genericTerm(TermLength.class, (Term)this.terms.get(i), (String)this.names.get(4), CSSProperty.LineHeight.length, ValueRange.DISALLOW_NEGATIVE, properties, values);
                }
                case 5: {
                    TermList list = tf.createList();
                    StringBuffer sb = new StringBuffer();
                    boolean composed = false;
                    for (Term t : this.terms.subList(i, this.terms.size())) {
                        if (t instanceof TermIdent && sb.length() == 0) {
                            sb.append(t.getValue());
                            composed = false;
                            continue;
                        }
                        if (t instanceof TermIdent && sb.length() != 0 && t.getOperator() != Term.Operator.COMMA && t.getOperator() != Term.Operator.SLASH) {
                            sb.append(" ").append(t.getValue());
                            composed = true;
                            continue;
                        }
                        if (t instanceof TermString || t instanceof TermIdent && t.getOperator() == Term.Operator.COMMA) {
                            this.storeFamilyName(list, sb.toString(), composed);
                            sb = new StringBuffer();
                            composed = false;
                            if (t instanceof TermString) {
                                this.storeFamilyName(list, (String)t.getValue(), true);
                                continue;
                            }
                            sb.append(t.getValue());
                            continue;
                        }
                        return false;
                    }
                    this.storeFamilyName(list, sb.toString(), composed);
                    if (list.isEmpty()) {
                        return false;
                    }
                    if (list.size() == 1 && !(list.toArray(new Term[0])[0] instanceof TermString)) {
                        properties.put((String)this.names.get(5), (CSSProperty.FontFamily)list.toArray(new Term[0])[0].getValue());
                        return true;
                    }
                    properties.put((String)this.names.get(5), CSSProperty.FontFamily.list_values);
                    values.put((String)this.names.get(5), list);
                    iteration.set(this.terms.size());
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean variantCondition(int variant, Variator.IntegerRef iteration) {
            switch (variant) {
                case 0: 
                case 1: 
                case 2: {
                    return iteration.get() < 3;
                }
                case 3: {
                    return true;
                }
                case 4: {
                    if (!this.variantPassed[3]) {
                        return false;
                    }
                    return ((Term)this.terms.get(iteration.get())).getOperator() == Term.Operator.SLASH;
                }
                case 5: {
                    return this.variantPassed[3];
                }
            }
            return false;
        }

        @Override
        public boolean vary(Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            if (this.terms.size() == 1 && this.terms.get(0) instanceof TermIdent) {
                if (this.checkInherit(-1, (Term)this.terms.get(0), properties)) {
                    return true;
                }
                return DeclarationTransformerImpl.this.genericTermIdent(CSSProperty.Font.class, (Term)this.terms.get(0), true, "font", properties);
            }
            return super.vary(properties, values);
        }

        private void storeFamilyName(TermList storage, String name, boolean composed) {
            EnumSet<CSSProperty.FontFamily> allowedFamilies = EnumSet.complementOf(EnumSet.of(CSSProperty.FontFamily.INHERIT, CSSProperty.FontFamily.list_values));
            if (name == null || "".equals(name) || name.length() == 0) {
                return;
            }
            name = name.trim();
            if (composed) {
                TermString term = tf.createString(name);
                if (!storage.isEmpty()) {
                    term.setOperator(Term.Operator.COMMA);
                }
                storage.add(term);
            } else {
                CSSProperty.FontFamily generic = DeclarationTransformerImpl.this.genericPropertyRaw(CSSProperty.FontFamily.class, allowedFamilies, tf.createIdent(name));
                if (generic != null) {
                    Term<CSSProperty.FontFamily> term = tf.createTerm(generic);
                    if (!storage.isEmpty()) {
                        term.setOperator(Term.Operator.COMMA);
                    }
                    storage.add(term);
                } else {
                    TermString term = tf.createString(name);
                    if (!storage.isEmpty()) {
                        term.setOperator(Term.Operator.COMMA);
                    }
                    storage.add(term);
                }
            }
        }
    }

    private final class OutlineVariator
    extends Variator {
        public static final int COLOR = 0;
        public static final int STYLE = 1;
        public static final int WIDTH = 2;

        public OutlineVariator() {
            super(3);
            this.names.add("outline-color");
            this.types.add(CSSProperty.OutlineColor.class);
            this.names.add("outline-style");
            this.types.add(CSSProperty.OutlineStyle.class);
            this.names.add("outline-width");
            this.types.add(CSSProperty.OutlineWidth.class);
        }

        @Override
        protected boolean variant(int v, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            int i = iteration.get();
            switch (v) {
                case 0: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(0), (Term)this.terms.get(i), true, (String)this.names.get(0), properties) || DeclarationTransformerImpl.this.genericTermColor((Term)this.terms.get(i), (String)this.names.get(0), CSSProperty.OutlineColor.color, properties, values);
                }
                case 1: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(1), (Term)this.terms.get(i), true, (String)this.names.get(1), properties);
                }
                case 2: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(2), (Term)this.terms.get(i), true, (String)this.names.get(2), properties) || DeclarationTransformerImpl.this.genericTermLength((Term)this.terms.get(i), (String)this.names.get(2), CSSProperty.OutlineWidth.length, ValueRange.DISALLOW_NEGATIVE, properties, values);
                }
            }
            return false;
        }
    }

    private final class BorderSideVariator
    extends Variator {
        public static final int COLOR = 0;
        public static final int STYLE = 1;
        public static final int WIDTH = 2;

        public BorderSideVariator(String side) {
            super(3);
            this.names.add("border-" + side + "-color");
            this.types.add(CSSProperty.BorderColor.class);
            this.names.add("border-" + side + "-style");
            this.types.add(CSSProperty.BorderStyle.class);
            this.names.add("border-" + side + "-width");
            this.types.add(CSSProperty.BorderWidth.class);
        }

        @Override
        protected boolean variant(int v, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            int i = iteration.get();
            switch (v) {
                case 0: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(0), (Term)this.terms.get(i), true, (String)this.names.get(0), properties) || DeclarationTransformerImpl.this.genericTermColor((Term)this.terms.get(i), (String)this.names.get(0), CSSProperty.BorderColor.color, properties, values);
                }
                case 1: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(1), (Term)this.terms.get(i), true, (String)this.names.get(1), properties);
                }
                case 2: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(2), (Term)this.terms.get(i), true, (String)this.names.get(2), properties) || DeclarationTransformerImpl.this.genericTermLength((Term)this.terms.get(i), (String)this.names.get(2), CSSProperty.BorderWidth.length, ValueRange.DISALLOW_NEGATIVE, properties, values);
                }
            }
            return false;
        }
    }

    private final class ListStyleVariator
    extends Variator {
        public static final int TYPE = 0;
        public static final int POSITION = 1;
        public static final int IMAGE = 2;

        public ListStyleVariator() {
            super(3);
            this.names.add("list-style-type");
            this.types.add(CSSProperty.ListStyleType.class);
            this.names.add("list-style-position");
            this.types.add(CSSProperty.ListStylePosition.class);
            this.names.add("list-style-image");
            this.types.add(CSSProperty.ListStyleImage.class);
        }

        @Override
        protected boolean variant(int v, Variator.IntegerRef iteration, Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
            int i = iteration.get();
            switch (v) {
                case 0: {
                    return DeclarationTransformerImpl.this.genericTermIdent(CSSProperty.ListStyleType.class, (Term)this.terms.get(i), true, (String)this.names.get(0), properties);
                }
                case 1: {
                    return DeclarationTransformerImpl.this.genericTermIdent(CSSProperty.ListStylePosition.class, (Term)this.terms.get(i), true, (String)this.names.get(1), properties);
                }
                case 2: {
                    return DeclarationTransformerImpl.this.genericTermIdent((Class)this.types.get(2), (Term)this.terms.get(i), true, (String)this.names.get(2), properties) || DeclarationTransformerImpl.this.genericTerm(TermURI.class, (Term)this.terms.get(i), (String)this.names.get(2), CSSProperty.ListStyleImage.uri, ValueRange.ALLOW_ALL, properties, values);
                }
            }
            return false;
        }
    }

    private static enum ValueRange {
        ALLOW_ALL,
        DISALLOW_NEGATIVE,
        TRUNCATE_NEGATIVE,
        DISALLOW_ZERO;

    }
}

