/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.Option;
import groovyjarjarcommonscli.OptionGroup;
import groovyjarjarcommonscli.Options;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class HelpFormatter {
    public static final int DEFAULT_WIDTH = 74;
    public static final int DEFAULT_LEFT_PAD = 1;
    public static final int DEFAULT_DESC_PAD = 3;
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";
    public static final String DEFAULT_OPT_PREFIX = "-";
    public static final String DEFAULT_LONG_OPT_PREFIX = "--";
    public static final String DEFAULT_ARG_NAME = "arg";
    public int defaultWidth = 74;
    public int defaultLeftPad = 1;
    public int defaultDescPad = 3;
    public String defaultSyntaxPrefix = "usage: ";
    public String defaultNewLine = System.getProperty("line.separator");
    public String defaultOptPrefix = "-";
    public String defaultLongOptPrefix = "--";
    public String defaultArgName = "arg";
    protected Comparator optionComparator = new OptionComparator();

    public void setWidth(int width) {
        this.defaultWidth = width;
    }

    public int getWidth() {
        return this.defaultWidth;
    }

    public void setLeftPadding(int padding) {
        this.defaultLeftPad = padding;
    }

    public int getLeftPadding() {
        return this.defaultLeftPad;
    }

    public void setDescPadding(int padding) {
        this.defaultDescPad = padding;
    }

    public int getDescPadding() {
        return this.defaultDescPad;
    }

    public void setSyntaxPrefix(String prefix) {
        this.defaultSyntaxPrefix = prefix;
    }

    public String getSyntaxPrefix() {
        return this.defaultSyntaxPrefix;
    }

    public void setNewLine(String newline) {
        this.defaultNewLine = newline;
    }

    public String getNewLine() {
        return this.defaultNewLine;
    }

    public void setOptPrefix(String prefix) {
        this.defaultOptPrefix = prefix;
    }

    public String getOptPrefix() {
        return this.defaultOptPrefix;
    }

    public void setLongOptPrefix(String prefix) {
        this.defaultLongOptPrefix = prefix;
    }

    public String getLongOptPrefix() {
        return this.defaultLongOptPrefix;
    }

    public void setArgName(String name) {
        this.defaultArgName = name;
    }

    public String getArgName() {
        return this.defaultArgName;
    }

    public Comparator getOptionComparator() {
        return this.optionComparator;
    }

    public void setOptionComparator(Comparator comparator) {
        this.optionComparator = comparator == null ? new OptionComparator() : comparator;
    }

    public void printHelp(String cmdLineSyntax, Options options) {
        this.printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, false);
    }

    public void printHelp(String cmdLineSyntax, Options options, boolean autoUsage) {
        this.printHelp(this.defaultWidth, cmdLineSyntax, null, options, null, autoUsage);
    }

    public void printHelp(String cmdLineSyntax, String header, Options options, String footer) {
        this.printHelp(cmdLineSyntax, header, options, footer, false);
    }

    public void printHelp(String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
        this.printHelp(this.defaultWidth, cmdLineSyntax, header, options, footer, autoUsage);
    }

    public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer) {
        this.printHelp(width, cmdLineSyntax, header, options, footer, false);
    }

    public void printHelp(int width, String cmdLineSyntax, String header, Options options, String footer, boolean autoUsage) {
        PrintWriter pw = new PrintWriter(System.out);
        this.printHelp(pw, width, cmdLineSyntax, header, options, this.defaultLeftPad, this.defaultDescPad, footer, autoUsage);
        pw.flush();
    }

    public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer) {
        this.printHelp(pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
    }

    public void printHelp(PrintWriter pw, int width, String cmdLineSyntax, String header, Options options, int leftPad, int descPad, String footer, boolean autoUsage) {
        if (cmdLineSyntax == null || cmdLineSyntax.length() == 0) {
            throw new IllegalArgumentException("cmdLineSyntax not provided");
        }
        if (autoUsage) {
            this.printUsage(pw, width, cmdLineSyntax, options);
        } else {
            this.printUsage(pw, width, cmdLineSyntax);
        }
        if (header != null && header.trim().length() > 0) {
            this.printWrapped(pw, width, header);
        }
        this.printOptions(pw, width, options, leftPad, descPad);
        if (footer != null && footer.trim().length() > 0) {
            this.printWrapped(pw, width, footer);
        }
    }

    public void printUsage(PrintWriter pw, int width, String app, Options options) {
        StringBuffer buff = new StringBuffer(this.defaultSyntaxPrefix).append(app).append(" ");
        ArrayList<OptionGroup> processedGroups = new ArrayList<OptionGroup>();
        ArrayList optList = new ArrayList(options.getOptions());
        Collections.sort(optList, this.getOptionComparator());
        Iterator i = optList.iterator();
        while (i.hasNext()) {
            Option option = (Option)i.next();
            OptionGroup group = options.getOptionGroup(option);
            if (group != null) {
                if (!processedGroups.contains(group)) {
                    processedGroups.add(group);
                    this.appendOptionGroup(buff, group);
                }
            } else {
                HelpFormatter.appendOption(buff, option, option.isRequired());
            }
            if (!i.hasNext()) continue;
            buff.append(" ");
        }
        this.printWrapped(pw, width, buff.toString().indexOf(32) + 1, buff.toString());
    }

    private void appendOptionGroup(StringBuffer buff, OptionGroup group) {
        if (!group.isRequired()) {
            buff.append("[");
        }
        ArrayList optList = new ArrayList(group.getOptions());
        Collections.sort(optList, this.getOptionComparator());
        Iterator i = optList.iterator();
        while (i.hasNext()) {
            HelpFormatter.appendOption(buff, (Option)i.next(), true);
            if (!i.hasNext()) continue;
            buff.append(" | ");
        }
        if (!group.isRequired()) {
            buff.append("]");
        }
    }

    private static void appendOption(StringBuffer buff, Option option, boolean required) {
        if (!required) {
            buff.append("[");
        }
        if (option.getOpt() != null) {
            buff.append(DEFAULT_OPT_PREFIX).append(option.getOpt());
        } else {
            buff.append(DEFAULT_LONG_OPT_PREFIX).append(option.getLongOpt());
        }
        if (option.hasArg() && option.hasArgName()) {
            buff.append(" <").append(option.getArgName()).append(">");
        }
        if (!required) {
            buff.append("]");
        }
    }

    public void printUsage(PrintWriter pw, int width, String cmdLineSyntax) {
        int argPos = cmdLineSyntax.indexOf(32) + 1;
        this.printWrapped(pw, width, this.defaultSyntaxPrefix.length() + argPos, this.defaultSyntaxPrefix + cmdLineSyntax);
    }

    public void printOptions(PrintWriter pw, int width, Options options, int leftPad, int descPad) {
        StringBuffer sb = new StringBuffer();
        this.renderOptions(sb, width, options, leftPad, descPad);
        pw.println(sb.toString());
    }

    public void printWrapped(PrintWriter pw, int width, String text) {
        this.printWrapped(pw, width, 0, text);
    }

    public void printWrapped(PrintWriter pw, int width, int nextLineTabStop, String text) {
        StringBuffer sb = new StringBuffer(text.length());
        this.renderWrappedText(sb, width, nextLineTabStop, text);
        pw.println(sb.toString());
    }

    protected StringBuffer renderOptions(StringBuffer sb, int width, Options options, int leftPad, int descPad) {
        StringBuffer optBuf;
        String lpad = this.createPadding(leftPad);
        String dpad = this.createPadding(descPad);
        int max = 0;
        ArrayList<StringBuffer> prefixList = new ArrayList<StringBuffer>();
        List optList = options.helpOptions();
        Collections.sort(optList, this.getOptionComparator());
        Iterator i = optList.iterator();
        while (i.hasNext()) {
            Option option = (Option)i.next();
            optBuf = new StringBuffer(8);
            if (option.getOpt() == null) {
                optBuf.append(lpad).append("   " + this.defaultLongOptPrefix).append(option.getLongOpt());
            } else {
                optBuf.append(lpad).append(this.defaultOptPrefix).append(option.getOpt());
                if (option.hasLongOpt()) {
                    optBuf.append(',').append(this.defaultLongOptPrefix).append(option.getLongOpt());
                }
            }
            if (option.hasArg()) {
                if (option.hasArgName()) {
                    optBuf.append(" <").append(option.getArgName()).append(">");
                } else {
                    optBuf.append(' ');
                }
            }
            prefixList.add(optBuf);
            max = optBuf.length() > max ? optBuf.length() : max;
        }
        int x = 0;
        Iterator i2 = optList.iterator();
        while (i2.hasNext()) {
            Option option = (Option)i2.next();
            if ((optBuf = new StringBuffer(prefixList.get(x++).toString())).length() < max) {
                optBuf.append(this.createPadding(max - optBuf.length()));
            }
            optBuf.append(dpad);
            int nextLineTabStop = max + descPad;
            if (option.getDescription() != null) {
                optBuf.append(option.getDescription());
            }
            this.renderWrappedText(sb, width, nextLineTabStop, optBuf.toString());
            if (!i2.hasNext()) continue;
            sb.append(this.defaultNewLine);
        }
        return sb;
    }

    protected StringBuffer renderWrappedText(StringBuffer sb, int width, int nextLineTabStop, String text) {
        int pos = this.findWrapPos(text, width, 0);
        if (pos == -1) {
            sb.append(this.rtrim(text));
            return sb;
        }
        sb.append(this.rtrim(text.substring(0, pos))).append(this.defaultNewLine);
        if (nextLineTabStop >= width) {
            nextLineTabStop = 1;
        }
        String padding = this.createPadding(nextLineTabStop);
        while (true) {
            if ((pos = this.findWrapPos(text = padding + text.substring(pos).trim(), width, 0)) == -1) {
                sb.append(text);
                return sb;
            }
            if (text.length() > width && pos == nextLineTabStop - 1) {
                pos = width;
            }
            sb.append(this.rtrim(text.substring(0, pos))).append(this.defaultNewLine);
        }
    }

    protected int findWrapPos(String text, int width, int startPos) {
        char c;
        int pos = -1;
        pos = text.indexOf(10, startPos);
        if (pos != -1 && pos <= width || (pos = text.indexOf(9, startPos)) != -1 && pos <= width) {
            return pos + 1;
        }
        if (startPos + width >= text.length()) {
            return -1;
        }
        for (pos = startPos + width; pos >= startPos && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r'; --pos) {
        }
        if (pos > startPos) {
            return pos;
        }
        for (pos = startPos + width; pos <= text.length() && (c = text.charAt(pos)) != ' ' && c != '\n' && c != '\r'; ++pos) {
        }
        return pos == text.length() ? -1 : pos;
    }

    protected String createPadding(int len) {
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; ++i) {
            sb.append(' ');
        }
        return sb.toString();
    }

    protected String rtrim(String s) {
        int pos;
        if (s == null || s.length() == 0) {
            return s;
        }
        for (pos = s.length(); pos > 0 && Character.isWhitespace(s.charAt(pos - 1)); --pos) {
        }
        return s.substring(0, pos);
    }

    private static class OptionComparator
    implements Comparator {
        private OptionComparator() {
        }

        public int compare(Object o1, Object o2) {
            Option opt1 = (Option)o1;
            Option opt2 = (Option)o2;
            return opt1.getKey().compareToIgnoreCase(opt2.getKey());
        }
    }
}

