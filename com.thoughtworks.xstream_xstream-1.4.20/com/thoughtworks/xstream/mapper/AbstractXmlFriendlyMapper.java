/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class AbstractXmlFriendlyMapper
extends MapperWrapper {
    private char dollarReplacementInClass = (char)45;
    private String dollarReplacementInField = "_DOLLAR_";
    private String underscoreReplacementInField = "__";
    private String noPackagePrefix = "default";

    protected AbstractXmlFriendlyMapper(Mapper wrapped) {
        super(wrapped);
    }

    protected String escapeClassName(String className) {
        if ((className = className.replace('$', this.dollarReplacementInClass)).charAt(0) == this.dollarReplacementInClass) {
            className = this.noPackagePrefix + className;
        }
        return className;
    }

    protected String unescapeClassName(String className) {
        if (className.startsWith(this.noPackagePrefix + this.dollarReplacementInClass)) {
            className = className.substring(this.noPackagePrefix.length());
        }
        className = className.replace(this.dollarReplacementInClass, '$');
        return className;
    }

    protected String escapeFieldName(String fieldName) {
        StringBuffer result = new StringBuffer();
        int length = fieldName.length();
        for (int i = 0; i < length; ++i) {
            char c = fieldName.charAt(i);
            if (c == '$') {
                result.append(this.dollarReplacementInField);
                continue;
            }
            if (c == '_') {
                result.append(this.underscoreReplacementInField);
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    protected String unescapeFieldName(String xmlName) {
        StringBuffer result = new StringBuffer();
        int length = xmlName.length();
        for (int i = 0; i < length; ++i) {
            char c = xmlName.charAt(i);
            if (this.stringFoundAt(xmlName, i, this.underscoreReplacementInField)) {
                i += this.underscoreReplacementInField.length() - 1;
                result.append('_');
                continue;
            }
            if (this.stringFoundAt(xmlName, i, this.dollarReplacementInField)) {
                i += this.dollarReplacementInField.length() - 1;
                result.append('$');
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    private boolean stringFoundAt(String name, int i, String replacement) {
        return name.length() >= i + replacement.length() && name.substring(i, i + replacement.length()).equals(replacement);
    }
}

