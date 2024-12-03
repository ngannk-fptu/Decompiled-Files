/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.ParseProperty;
import org.w3c.tidy.TidyUtils;

public final class ParsePropertyImpl {
    static final ParseProperty INT = new ParseInt();
    static final ParseProperty BOOL = new ParseBoolean();
    static final ParseProperty INVBOOL = new ParseInvBoolean();
    static final ParseProperty CHAR_ENCODING = new ParseCharEncoding();
    static final ParseProperty NAME = new ParseName();
    static final ParseProperty TAGNAMES = new ParseTagNames();
    static final ParseProperty DOCTYPE = new ParseDocType();
    static final ParseProperty REPEATED_ATTRIBUTES = new ParseRepeatedAttribute();
    static final ParseProperty STRING = new ParseString();
    static final ParseProperty INDENT = new ParseIndent();
    static final ParseProperty CSS1SELECTOR = new ParseCSS1Selector();
    static final ParseProperty NEWLINE = new ParseNewLine();

    private ParsePropertyImpl() {
    }

    static class ParseNewLine
    implements ParseProperty {
        ParseNewLine() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            if ("lf".equalsIgnoreCase(value)) {
                configuration.newline = new char[]{'\n'};
            } else if ("cr".equalsIgnoreCase(value)) {
                configuration.newline = new char[]{'\r'};
            } else if ("crlf".equalsIgnoreCase(value)) {
                configuration.newline = new char[]{'\r', '\n'};
            } else {
                configuration.report.badArgument(value, option);
            }
            return null;
        }

        public String getType() {
            return "Enum";
        }

        public String getOptionValues() {
            return "lf, crlf, cr";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            if (configuration.newline.length == 1) {
                return configuration.newline[0] == '\n' ? "lf" : "cr";
            }
            return "crlf";
        }
    }

    static class ParseCSS1Selector
    implements ParseProperty {
        ParseCSS1Selector() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            StringTokenizer t = new StringTokenizer(value);
            String buf = null;
            if (t.countTokens() >= 1) {
                buf = t.nextToken() + "-";
            } else {
                configuration.report.badArgument(value, option);
            }
            if (!Lexer.isCSS1Selector(value)) {
                configuration.report.badArgument(value, option);
            }
            return buf;
        }

        public String getType() {
            return "Name";
        }

        public String getOptionValues() {
            return "CSS1 selector";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            return value == null ? "" : (String)value;
        }
    }

    static class ParseIndent
    implements ParseProperty {
        ParseIndent() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            boolean b = configuration.indentContent;
            if ("yes".equalsIgnoreCase(value)) {
                b = true;
                configuration.smartIndent = false;
            } else if ("true".equalsIgnoreCase(value)) {
                b = true;
                configuration.smartIndent = false;
            } else if ("no".equalsIgnoreCase(value)) {
                b = false;
                configuration.smartIndent = false;
            } else if ("false".equalsIgnoreCase(value)) {
                b = false;
                configuration.smartIndent = false;
            } else if ("auto".equalsIgnoreCase(value)) {
                b = true;
                configuration.smartIndent = true;
            } else {
                configuration.report.badArgument(value, option);
            }
            return b ? Boolean.TRUE : Boolean.FALSE;
        }

        public String getType() {
            return "Indent";
        }

        public String getOptionValues() {
            return "auto, y/n, yes/no, t/f, true/false, 1/0";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            return value == null ? "" : value.toString();
        }
    }

    static class ParseString
    implements ParseProperty {
        ParseString() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            return value;
        }

        public String getType() {
            return "String";
        }

        public String getOptionValues() {
            return "-";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            return value == null ? "" : (String)value;
        }
    }

    static class ParseRepeatedAttribute
    implements ParseProperty {
        ParseRepeatedAttribute() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            int dupAttr;
            if ("keep-first".equalsIgnoreCase(value)) {
                dupAttr = 1;
            } else if ("keep-last".equalsIgnoreCase(value)) {
                dupAttr = 0;
            } else {
                configuration.report.badArgument(value, option);
                dupAttr = -1;
            }
            return new Integer(dupAttr);
        }

        public String getType() {
            return "Enum";
        }

        public String getOptionValues() {
            return "keep-first, keep-last";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            String stringValue;
            if (value == null) {
                return "";
            }
            int intValue = (Integer)value;
            switch (intValue) {
                case 1: {
                    stringValue = "keep-first";
                    break;
                }
                case 0: {
                    stringValue = "keep-last";
                    break;
                }
                default: {
                    stringValue = "unknown";
                }
            }
            return stringValue;
        }
    }

    static class ParseDocType
    implements ParseProperty {
        ParseDocType() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            if ((value = value.trim()).startsWith("\"")) {
                configuration.docTypeMode = 4;
                return value;
            }
            String word = "";
            StringTokenizer t = new StringTokenizer(value, " \t\n\r,");
            if (t.hasMoreTokens()) {
                word = t.nextToken();
            }
            if ("auto".equalsIgnoreCase(word)) {
                configuration.docTypeMode = 1;
            } else if ("omit".equalsIgnoreCase(word)) {
                configuration.docTypeMode = 0;
            } else if ("strict".equalsIgnoreCase(word)) {
                configuration.docTypeMode = 2;
            } else if ("loose".equalsIgnoreCase(word) || "transitional".equalsIgnoreCase(word)) {
                configuration.docTypeMode = 3;
            } else {
                configuration.report.badArgument(value, option);
            }
            return null;
        }

        public String getType() {
            return "DocType";
        }

        public String getOptionValues() {
            return "omit | auto | strict | loose | [fpi]";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            String stringValue;
            switch (configuration.docTypeMode) {
                case 1: {
                    stringValue = "auto";
                    break;
                }
                case 0: {
                    stringValue = "omit";
                    break;
                }
                case 2: {
                    stringValue = "strict";
                    break;
                }
                case 3: {
                    stringValue = "transitional";
                    break;
                }
                case 4: {
                    stringValue = configuration.docTypeStr;
                    break;
                }
                default: {
                    stringValue = "unknown";
                }
            }
            return stringValue;
        }
    }

    static class ParseTagNames
    implements ParseProperty {
        ParseTagNames() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            short tagType = 2;
            if ("new-inline-tags".equals(option)) {
                tagType = 2;
            } else if ("new-blocklevel-tags".equals(option)) {
                tagType = 4;
            } else if ("new-empty-tags".equals(option)) {
                tagType = 1;
            } else if ("new-pre-tags".equals(option)) {
                tagType = 8;
            }
            StringTokenizer t = new StringTokenizer(value, " \t\n\r,");
            while (t.hasMoreTokens()) {
                configuration.definedTags |= tagType;
                configuration.tt.defineTag(tagType, t.nextToken());
            }
            return null;
        }

        public String getType() {
            return "Tag names";
        }

        public String getOptionValues() {
            return "tagX, tagY, ...";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            int tagType;
            if ("new-inline-tags".equals(option)) {
                tagType = 2;
            } else if ("new-blocklevel-tags".equals(option)) {
                tagType = 4;
            } else if ("new-empty-tags".equals(option)) {
                tagType = 1;
            } else if ("new-pre-tags".equals(option)) {
                tagType = 8;
            } else {
                return "";
            }
            List tagList = configuration.tt.findAllDefinedTag((short)tagType);
            if (tagList.isEmpty()) {
                return "";
            }
            StringBuffer buffer = new StringBuffer();
            Iterator iterator = tagList.iterator();
            while (iterator.hasNext()) {
                buffer.append(iterator.next());
                buffer.append(" ");
            }
            return buffer.toString();
        }
    }

    static class ParseName
    implements ParseProperty {
        ParseName() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            StringTokenizer t = new StringTokenizer(value);
            String rs = null;
            if (t.countTokens() >= 1) {
                rs = t.nextToken();
            } else {
                configuration.report.badArgument(value, option);
            }
            return rs;
        }

        public String getType() {
            return "Name";
        }

        public String getOptionValues() {
            return "-";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            return value == null ? "" : value.toString();
        }
    }

    static class ParseCharEncoding
    implements ParseProperty {
        ParseCharEncoding() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            if ("raw".equalsIgnoreCase(value)) {
                configuration.rawOut = true;
            } else if (!TidyUtils.isCharEncodingSupported(value)) {
                configuration.report.badArgument(value, option);
            } else if ("input-encoding".equalsIgnoreCase(option)) {
                configuration.setInCharEncodingName(value);
            } else if ("output-encoding".equalsIgnoreCase(option)) {
                configuration.setOutCharEncodingName(value);
            } else if ("char-encoding".equalsIgnoreCase(option)) {
                configuration.setInCharEncodingName(value);
                configuration.setOutCharEncodingName(value);
            }
            return null;
        }

        public String getType() {
            return "Encoding";
        }

        public String getOptionValues() {
            return "Any valid java char encoding name";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            if ("output-encoding".equalsIgnoreCase(option)) {
                return configuration.getOutCharEncodingName();
            }
            return configuration.getInCharEncodingName();
        }
    }

    static class ParseInvBoolean
    implements ParseProperty {
        ParseInvBoolean() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            return (Boolean)BOOL.parse(value, option, configuration) != false ? Boolean.FALSE : Boolean.TRUE;
        }

        public String getType() {
            return "Boolean";
        }

        public String getOptionValues() {
            return "yes, no, true, false";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            if (value == null) {
                return "";
            }
            return (Boolean)value != false ? "no" : "yes";
        }
    }

    static class ParseBoolean
    implements ParseProperty {
        ParseBoolean() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            Boolean b = Boolean.TRUE;
            if (value != null && value.length() > 0) {
                char c = value.charAt(0);
                if (c == 't' || c == 'T' || c == 'Y' || c == 'y' || c == '1') {
                    b = Boolean.TRUE;
                } else if (c == 'f' || c == 'F' || c == 'N' || c == 'n' || c == '0') {
                    b = Boolean.FALSE;
                } else {
                    configuration.report.badArgument(value, option);
                }
            }
            return b;
        }

        public String getType() {
            return "Boolean";
        }

        public String getOptionValues() {
            return "y/n, yes/no, t/f, true/false, 1/0";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            if (value == null) {
                return "";
            }
            return (Boolean)value != false ? "yes" : "no";
        }
    }

    static class ParseInt
    implements ParseProperty {
        ParseInt() {
        }

        public Object parse(String value, String option, Configuration configuration) {
            int i = 0;
            try {
                i = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                configuration.report.badArgument(value, option);
                i = -1;
            }
            return new Integer(i);
        }

        public String getType() {
            return "Integer";
        }

        public String getOptionValues() {
            return "0, 1, 2, ...";
        }

        public String getFriendlyName(String option, Object value, Configuration configuration) {
            return value == null ? "" : value.toString();
        }
    }
}

