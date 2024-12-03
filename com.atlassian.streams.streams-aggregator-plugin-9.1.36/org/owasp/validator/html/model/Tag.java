/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.owasp.validator.html.model.Attribute;

public class Tag {
    private final Map<String, Attribute> allowedAttributes;
    private final String name;
    private final String action;
    private Set<String> allowCData;
    static final String ANY_NORMAL_WHITESPACES = "(\\s)*";
    static final String OPEN_ATTRIBUTE = "(";
    static final String ATTRIBUTE_DIVIDER = "|";
    static final String CLOSE_ATTRIBUTE = ")";
    private static final String OPEN_TAG_ATTRIBUTES = "(\\s)*(";
    private static final String CLOSE_TAG_ATTRIBUTES = ")*";
    private static final String REGEXP_CHARACTERS = "\\(){}.*?$^-+";

    public Tag(String name, Map<String, Attribute> tagAttributes, String action) {
        this.name = name;
        this.allowedAttributes = Collections.unmodifiableMap(tagAttributes);
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public boolean isAction(String action) {
        return action.equals(this.action);
    }

    public Tag mutateAction(String action) {
        return new Tag(this.name, this.allowedAttributes, action);
    }

    public Set<String> getAllowCData() {
        return this.allowCData;
    }

    public void setAllowCData(Set<String> allowCData) {
        this.allowCData = allowCData;
    }

    public String getRegularExpression() {
        if (this.allowedAttributes.size() == 0) {
            return "^<" + this.name + ">$";
        }
        StringBuilder regExp = new StringBuilder("<(\\s)*" + this.name + OPEN_TAG_ATTRIBUTES);
        ArrayList<Attribute> values = new ArrayList<Attribute>(this.allowedAttributes.values());
        Collections.sort(values, new Comparator<Attribute>(){

            @Override
            public int compare(Attribute o1, Attribute o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Iterator attributes = values.iterator();
        while (attributes.hasNext()) {
            Attribute attr = (Attribute)attributes.next();
            regExp.append(attr.matcherRegEx(attributes.hasNext()));
        }
        regExp.append(")*(\\s)*>");
        return regExp.toString();
    }

    static String escapeRegularExpressionCharacters(String allowedValue) {
        String toReturn = allowedValue;
        if (toReturn == null) {
            return null;
        }
        for (int i = 0; i < REGEXP_CHARACTERS.length(); ++i) {
            toReturn = toReturn.replaceAll("\\" + String.valueOf(REGEXP_CHARACTERS.charAt(i)), "\\" + REGEXP_CHARACTERS.charAt(i));
        }
        return toReturn;
    }

    public String getName() {
        return this.name;
    }

    public Attribute getAttributeByName(String name) {
        return this.allowedAttributes.get(name);
    }
}

