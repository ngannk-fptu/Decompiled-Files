/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.AttributeCondition
 *  org.w3c.css.sac.CombinatorCondition
 *  org.w3c.css.sac.Condition
 *  org.w3c.css.sac.ConditionalSelector
 *  org.w3c.css.sac.DescendantSelector
 *  org.w3c.css.sac.LexicalUnit
 *  org.w3c.css.sac.NegativeCondition
 *  org.w3c.css.sac.NegativeSelector
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SiblingSelector
 *  org.w3c.css.sac.SimpleSelector
 */
package org.owasp.validator.css;

import java.util.Iterator;
import java.util.regex.Pattern;
import org.owasp.validator.css.UnknownSelectorException;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.model.AntiSamyPattern;
import org.owasp.validator.html.model.Property;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;

public class CssValidator {
    private final Policy policy;

    public CssValidator(Policy policy) {
        this.policy = policy;
    }

    public boolean isValidProperty(String name, LexicalUnit lu) {
        boolean isValid = false;
        Property property = null;
        if (name != null) {
            property = this.policy.getPropertyByName(name.toLowerCase());
        }
        if (property != null) {
            isValid = true;
            while (lu != null) {
                String value = this.lexicalValueToString(lu);
                if (value == null || !this.validateValue(property, value)) {
                    isValid = false;
                    break;
                }
                lu = lu.getNextLexicalUnit();
            }
        }
        return isValid;
    }

    public boolean isValidSelector(String selectorName, Selector selector) throws ScanException {
        switch (selector.getSelectorType()) {
            case 1: 
            case 2: 
            case 4: 
            case 9: {
                return this.validateSimpleSelector((SimpleSelector)selector);
            }
            case 10: 
            case 11: {
                DescendantSelector descSelector = (DescendantSelector)selector;
                return this.isValidSelector(selectorName, (Selector)descSelector.getSimpleSelector()) && this.isValidSelector(selectorName, descSelector.getAncestorSelector());
            }
            case 0: {
                ConditionalSelector condSelector = (ConditionalSelector)selector;
                return this.isValidSelector(selectorName, (Selector)condSelector.getSimpleSelector()) && this.isValidCondition(selectorName, condSelector.getCondition());
            }
            case 12: {
                SiblingSelector sibSelector = (SiblingSelector)selector;
                return this.isValidSelector(selectorName, (Selector)sibSelector.getSiblingSelector()) && this.isValidSelector(selectorName, sibSelector.getSelector());
            }
            case 3: {
                return this.validateSimpleSelector((SimpleSelector)((NegativeSelector)selector));
            }
        }
        throw new UnknownSelectorException(HTMLEntityEncoder.htmlEntityEncode(selector.toString()));
    }

    private boolean validateSimpleSelector(SimpleSelector selector) {
        String selectorLowerCase = selector.toString().toLowerCase();
        return this.policy.getCommonRegularExpressions("cssElementSelector").matches(selectorLowerCase) && !this.policy.getCommonRegularExpressions("cssElementExclusion").matches(selectorLowerCase);
    }

    public boolean isValidCondition(String selectorName, Condition condition) throws ScanException {
        switch (condition.getConditionType()) {
            case 0: 
            case 1: {
                CombinatorCondition comboCondition = (CombinatorCondition)condition;
                return this.isValidCondition(selectorName, comboCondition.getFirstCondition()) && this.isValidCondition(selectorName, comboCondition.getSecondCondition());
            }
            case 9: {
                return this.validateCondition((AttributeCondition)condition, this.policy.getCommonRegularExpressions("cssClassSelector"), this.policy.getCommonRegularExpressions("cssClassExclusion"));
            }
            case 5: {
                return this.validateCondition((AttributeCondition)condition, this.policy.getCommonRegularExpressions("cssIDSelector"), this.policy.getCommonRegularExpressions("cssIDExclusion"));
            }
            case 10: {
                return this.validateCondition((AttributeCondition)condition, this.policy.getCommonRegularExpressions("cssPseudoElementSelector"), this.policy.getCommonRegularExpressions("cssPsuedoElementExclusion"));
            }
            case 4: 
            case 7: 
            case 8: {
                return this.validateCondition((AttributeCondition)condition, this.policy.getCommonRegularExpressions("cssAttributeSelector"), this.policy.getCommonRegularExpressions("cssAttributeExclusion"));
            }
            case 2: {
                return this.isValidCondition(selectorName, ((NegativeCondition)condition).getCondition());
            }
            case 11: 
            case 12: {
                return true;
            }
        }
        throw new UnknownSelectorException(HTMLEntityEncoder.htmlEntityEncode(selectorName));
    }

    private boolean validateCondition(AttributeCondition condition, AntiSamyPattern pattern, AntiSamyPattern exclusionPattern) {
        String otherLower = condition.toString().toLowerCase();
        return pattern.matches(otherLower) && !exclusionPattern.matches(otherLower);
    }

    private boolean validateValue(Property property, String value) {
        boolean isValid = false;
        value = value.toLowerCase();
        Iterator<String> allowedValues = property.getAllowedValues().iterator();
        while (allowedValues.hasNext() && !isValid) {
            String allowedValue = allowedValues.next();
            if (allowedValue == null || !allowedValue.equals(value)) continue;
            isValid = true;
        }
        Iterator<Pattern> allowedRegexps = property.getAllowedRegExp().iterator();
        while (allowedRegexps.hasNext() && !isValid) {
            Pattern pattern = allowedRegexps.next();
            if (pattern == null || !pattern.matcher(value).matches()) continue;
            isValid = true;
        }
        Iterator<String> shorthandRefs = property.getShorthandRefs().iterator();
        while (shorthandRefs.hasNext() && !isValid) {
            String shorthandRef = shorthandRefs.next();
            Property shorthand = this.policy.getPropertyByName(shorthandRef);
            if (shorthand == null) continue;
            isValid = this.validateValue(shorthand, value);
        }
        return isValid;
    }

    public String lexicalValueToString(LexicalUnit lu) {
        switch (lu.getLexicalUnitType()) {
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 28: 
            case 29: 
            case 30: 
            case 31: 
            case 32: 
            case 33: 
            case 34: 
            case 42: {
                return lu.getFloatValue() + lu.getDimensionUnitText();
            }
            case 13: {
                return String.valueOf(lu.getIntegerValue());
            }
            case 14: {
                return String.valueOf(lu.getFloatValue());
            }
            case 35: 
            case 36: {
                String stringValue = lu.getStringValue();
                if (stringValue.indexOf(" ") != -1) {
                    stringValue = "\"" + stringValue + "\"";
                }
                return stringValue;
            }
            case 24: {
                return "url(" + lu.getStringValue() + ")";
            }
            case 27: {
                StringBuffer sb = new StringBuffer("rgb(");
                LexicalUnit param = lu.getParameters();
                sb.append(param.getIntegerValue());
                sb.append(',');
                param = param.getNextLexicalUnit();
                param = param.getNextLexicalUnit();
                sb.append(param.getIntegerValue());
                sb.append(',');
                param = param.getNextLexicalUnit();
                param = param.getNextLexicalUnit();
                sb.append(param.getIntegerValue());
                sb.append(')');
                return sb.toString();
            }
            case 12: {
                return "inherit";
            }
            case 0: {
                return ",";
            }
        }
        return null;
    }
}

