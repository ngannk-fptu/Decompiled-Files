/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.plist;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.plist.ParseException;
import org.apache.commons.configuration2.plist.PropertyListParser;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.lang3.StringUtils;

public class PropertyListConfiguration
extends BaseHierarchicalConfiguration
implements FileBasedConfiguration {
    private static final DateComponentParser DATE_SEPARATOR_PARSER = new DateSeparatorParser("-");
    private static final DateComponentParser TIME_SEPARATOR_PARSER = new DateSeparatorParser(":");
    private static final DateComponentParser BLANK_SEPARATOR_PARSER = new DateSeparatorParser(" ");
    private static final DateComponentParser[] DATE_PARSERS = new DateComponentParser[]{new DateSeparatorParser("<*D"), new DateFieldParser(1, 4), DATE_SEPARATOR_PARSER, new DateFieldParser(2, 2, 1), DATE_SEPARATOR_PARSER, new DateFieldParser(5, 2), BLANK_SEPARATOR_PARSER, new DateFieldParser(11, 2), TIME_SEPARATOR_PARSER, new DateFieldParser(12, 2), TIME_SEPARATOR_PARSER, new DateFieldParser(13, 2), BLANK_SEPARATOR_PARSER, new DateTimeZoneParser(), new DateSeparatorParser(">")};
    private static final String TIME_ZONE_PREFIX = "GMT";
    private static final int MILLIS_PER_MINUTE = 60000;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int INDENT_SIZE = 4;
    private static final int TIME_ZONE_LENGTH = 5;
    private static final char PAD_CHAR = '0';

    public PropertyListConfiguration() {
    }

    public PropertyListConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
    }

    PropertyListConfiguration(ImmutableNode root) {
        super(new InMemoryNodeModel(root));
    }

    @Override
    protected void setPropertyInternal(String key, Object value) {
        if (value instanceof byte[]) {
            this.setDetailEvents(false);
            try {
                this.clearProperty(key);
                this.addPropertyDirect(key, value);
            }
            finally {
                this.setDetailEvents(true);
            }
        } else {
            super.setPropertyInternal(key, value);
        }
    }

    @Override
    protected void addPropertyInternal(String key, Object value) {
        if (value instanceof byte[]) {
            this.addPropertyDirect(key, value);
        } else {
            super.addPropertyInternal(key, value);
        }
    }

    @Override
    public void read(Reader in) throws ConfigurationException {
        PropertyListParser parser = new PropertyListParser(in);
        try {
            PropertyListConfiguration config = parser.parse();
            this.getModel().setRootNode(config.getNodeModel().getNodeHandler().getRootNode());
        }
        catch (ParseException e) {
            throw new ConfigurationException(e);
        }
    }

    @Override
    public void write(Writer out) throws ConfigurationException {
        PrintWriter writer = new PrintWriter(out);
        NodeHandler<ImmutableNode> handler = this.getModel().getNodeHandler();
        this.printNode(writer, 0, (ImmutableNode)handler.getRootNode(), handler);
        writer.flush();
    }

    private void printNode(PrintWriter out, int indentLevel, ImmutableNode node, NodeHandler<ImmutableNode> handler) {
        ArrayList<ImmutableNode> children;
        String padding = StringUtils.repeat((String)" ", (int)(indentLevel * 4));
        if (node.getNodeName() != null) {
            out.print(padding + this.quoteString(node.getNodeName()) + " = ");
        }
        if (!(children = new ArrayList<ImmutableNode>(node.getChildren())).isEmpty()) {
            if (indentLevel > 0) {
                out.println();
            }
            out.println(padding + "{");
            Iterator it = children.iterator();
            while (it.hasNext()) {
                ImmutableNode child = (ImmutableNode)it.next();
                this.printNode(out, indentLevel + 1, child, handler);
                Object value = child.getValue();
                if (value != null && !(value instanceof Map) && !(value instanceof Configuration)) {
                    out.println(";");
                }
                if (!it.hasNext() || value != null && !(value instanceof List)) continue;
                out.println();
            }
            out.print(padding + "}");
            if (handler.getParent(node) != null) {
                out.println();
            }
        } else if (node.getValue() == null) {
            out.println();
            out.print(padding + "{ };");
            if (handler.getParent(node) != null) {
                out.println();
            }
        } else {
            Object value = node.getValue();
            this.printValue(out, indentLevel, value);
        }
    }

    private void printValue(PrintWriter out, int indentLevel, Object value) {
        String padding = StringUtils.repeat((String)" ", (int)(indentLevel * 4));
        if (value instanceof List) {
            out.print("( ");
            Iterator it = ((List)value).iterator();
            while (it.hasNext()) {
                this.printValue(out, indentLevel + 1, it.next());
                if (!it.hasNext()) continue;
                out.print(", ");
            }
            out.print(" )");
        } else if (value instanceof PropertyListConfiguration) {
            NodeHandler<ImmutableNode> handler = ((PropertyListConfiguration)value).getModel().getNodeHandler();
            this.printNode(out, indentLevel, (ImmutableNode)handler.getRootNode(), handler);
        } else if (value instanceof ImmutableConfiguration) {
            out.println();
            out.println(padding + "{");
            ImmutableConfiguration config = (ImmutableConfiguration)value;
            Iterator<String> it = config.getKeys();
            while (it.hasNext()) {
                String key = it.next();
                ImmutableNode node = new ImmutableNode.Builder().name(key).value(config.getProperty(key)).create();
                InMemoryNodeModel tempModel = new InMemoryNodeModel(node);
                this.printNode(out, indentLevel + 1, node, tempModel.getNodeHandler());
                out.println(";");
            }
            out.println(padding + "}");
        } else if (value instanceof Map) {
            Map<String, Object> map = PropertyListConfiguration.transformMap((Map)value);
            this.printValue(out, indentLevel, new MapConfiguration(map));
        } else if (value instanceof byte[]) {
            out.print("<" + new String(Hex.encodeHex((byte[])((byte[])value))) + ">");
        } else if (value instanceof Date) {
            out.print(PropertyListConfiguration.formatDate((Date)value));
        } else if (value != null) {
            out.print(this.quoteString(String.valueOf(value)));
        }
    }

    String quoteString(String s) {
        if (s == null) {
            return null;
        }
        if (s.indexOf(32) != -1 || s.indexOf(9) != -1 || s.indexOf(13) != -1 || s.indexOf(10) != -1 || s.indexOf(34) != -1 || s.indexOf(40) != -1 || s.indexOf(41) != -1 || s.indexOf(123) != -1 || s.indexOf(125) != -1 || s.indexOf(61) != -1 || s.indexOf(44) != -1 || s.indexOf(59) != -1) {
            s = s.replace("\"", "\\\"");
            s = "\"" + s + "\"";
        }
        return s;
    }

    static Date parseDate(String s) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        int index = 0;
        for (DateComponentParser parser : DATE_PARSERS) {
            index += parser.parseComponent(s, index, cal);
        }
        return cal.getTime();
    }

    static String formatDate(Calendar cal) {
        StringBuilder buf = new StringBuilder();
        for (DateComponentParser element : DATE_PARSERS) {
            element.formatComponent(buf, cal);
        }
        return buf.toString();
    }

    static String formatDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return PropertyListConfiguration.formatDate(cal);
    }

    private static Map<String, Object> transformMap(Map<?, ?> src) {
        HashMap<String, Object> dest = new HashMap<String, Object>();
        src.forEach((k, v) -> {
            if (k instanceof String) {
                dest.put((String)k, v);
            }
        });
        return dest;
    }

    private static class DateTimeZoneParser
    extends DateComponentParser {
        private DateTimeZoneParser() {
        }

        @Override
        public void formatComponent(StringBuilder buf, Calendar cal) {
            TimeZone tz = cal.getTimeZone();
            int ofs = tz.getRawOffset() / 60000;
            if (ofs < 0) {
                buf.append('-');
                ofs = -ofs;
            } else {
                buf.append('+');
            }
            int hour = ofs / 60;
            int min = ofs % 60;
            this.padNum(buf, hour, 2);
            this.padNum(buf, min, 2);
        }

        @Override
        public int parseComponent(String s, int index, Calendar cal) throws ParseException {
            this.checkLength(s, index, 5);
            TimeZone tz = TimeZone.getTimeZone(PropertyListConfiguration.TIME_ZONE_PREFIX + s.substring(index, index + 5));
            cal.setTimeZone(tz);
            return 5;
        }
    }

    private static class DateSeparatorParser
    extends DateComponentParser {
        private final String separator;

        public DateSeparatorParser(String sep) {
            this.separator = sep;
        }

        @Override
        public void formatComponent(StringBuilder buf, Calendar cal) {
            buf.append(this.separator);
        }

        @Override
        public int parseComponent(String s, int index, Calendar cal) throws ParseException {
            this.checkLength(s, index, this.separator.length());
            if (!s.startsWith(this.separator, index)) {
                throw new ParseException("Invalid input: " + s + ", index " + index + ", expected " + this.separator);
            }
            return this.separator.length();
        }
    }

    private static class DateFieldParser
    extends DateComponentParser {
        private final int calendarField;
        private final int length;
        private final int offset;

        public DateFieldParser(int calFld, int len) {
            this(calFld, len, 0);
        }

        public DateFieldParser(int calFld, int len, int ofs) {
            this.calendarField = calFld;
            this.length = len;
            this.offset = ofs;
        }

        @Override
        public void formatComponent(StringBuilder buf, Calendar cal) {
            this.padNum(buf, cal.get(this.calendarField) + this.offset, this.length);
        }

        @Override
        public int parseComponent(String s, int index, Calendar cal) throws ParseException {
            this.checkLength(s, index, this.length);
            try {
                cal.set(this.calendarField, Integer.parseInt(s.substring(index, index + this.length)) - this.offset);
                return this.length;
            }
            catch (NumberFormatException nfex) {
                throw new ParseException("Invalid number: " + s + ", index " + index);
            }
        }
    }

    private static abstract class DateComponentParser {
        private DateComponentParser() {
        }

        public abstract int parseComponent(String var1, int var2, Calendar var3) throws ParseException;

        public abstract void formatComponent(StringBuilder var1, Calendar var2);

        protected void checkLength(String s, int index, int length) throws ParseException {
            int len;
            int n = len = s == null ? 0 : s.length();
            if (index + length > len) {
                throw new ParseException("Input string too short: " + s + ", index: " + index);
            }
        }

        protected void padNum(StringBuilder buf, int num, int length) {
            buf.append(StringUtils.leftPad((String)String.valueOf(num), (int)length, (char)'0'));
        }
    }
}

