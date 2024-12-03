/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.w3c.tidy.EncodingNameMapper;
import org.w3c.tidy.ParseProperty;
import org.w3c.tidy.ParsePropertyImpl;
import org.w3c.tidy.Report;
import org.w3c.tidy.TagTable;

public class Configuration
implements Serializable {
    public static final int RAW = 0;
    public static final int ASCII = 1;
    public static final int LATIN1 = 2;
    public static final int UTF8 = 3;
    public static final int ISO2022 = 4;
    public static final int MACROMAN = 5;
    public static final int UTF16LE = 6;
    public static final int UTF16BE = 7;
    public static final int UTF16 = 8;
    public static final int WIN1252 = 9;
    public static final int BIG5 = 10;
    public static final int SHIFTJIS = 11;
    private final String[] ENCODING_NAMES = new String[]{"raw", "ASCII", "ISO8859_1", "UTF8", "JIS", "MacRoman", "UnicodeLittle", "UnicodeBig", "Unicode", "Cp1252", "Big5", "SJIS"};
    public static final int DOCTYPE_OMIT = 0;
    public static final int DOCTYPE_AUTO = 1;
    public static final int DOCTYPE_STRICT = 2;
    public static final int DOCTYPE_LOOSE = 3;
    public static final int DOCTYPE_USER = 4;
    public static final int KEEP_LAST = 0;
    public static final int KEEP_FIRST = 1;
    private static final Map OPTIONS = new HashMap();
    private static final long serialVersionUID = -4955155037138560842L;
    protected int spaces = 2;
    protected int wraplen = 68;
    protected int tabsize = 8;
    protected int docTypeMode = 1;
    protected int duplicateAttrs = 0;
    protected String altText;
    protected String slidestyle;
    protected String language;
    protected String docTypeStr;
    protected String errfile;
    protected boolean writeback;
    protected boolean onlyErrors;
    protected boolean showWarnings = true;
    protected boolean quiet;
    protected boolean indentContent;
    protected boolean smartIndent;
    protected boolean hideEndTags;
    protected boolean xmlTags;
    protected boolean xmlOut;
    protected boolean xHTML;
    protected boolean htmlOut;
    protected boolean xmlPi;
    protected boolean upperCaseTags;
    protected boolean upperCaseAttrs;
    protected boolean makeClean;
    protected boolean makeBare;
    protected boolean logicalEmphasis;
    protected boolean dropFontTags;
    protected boolean dropProprietaryAttributes;
    protected boolean dropEmptyParas = true;
    protected boolean fixComments = true;
    protected boolean trimEmpty = true;
    protected boolean breakBeforeBR;
    protected boolean burstSlides;
    protected boolean numEntities;
    protected boolean quoteMarks;
    protected boolean quoteNbsp = true;
    protected boolean quoteAmpersand = true;
    protected boolean wrapAttVals;
    protected boolean wrapScriptlets;
    protected boolean wrapSection = true;
    protected boolean wrapAsp = true;
    protected boolean wrapJste = true;
    protected boolean wrapPhp = true;
    protected boolean fixBackslash = true;
    protected boolean indentAttributes;
    protected boolean xmlPIs;
    protected boolean xmlSpace;
    protected boolean encloseBodyText;
    protected boolean encloseBlockText;
    protected boolean keepFileTimes = true;
    protected boolean word2000;
    protected boolean tidyMark = true;
    protected boolean emacs;
    protected boolean literalAttribs;
    protected boolean bodyOnly;
    protected boolean fixUri = true;
    protected boolean lowerLiterals = true;
    protected boolean replaceColor;
    protected boolean hideComments;
    protected boolean indentCdata;
    protected boolean forceOutput;
    protected int showErrors = 6;
    protected boolean asciiChars = true;
    protected boolean joinClasses;
    protected boolean joinStyles = true;
    protected boolean escapeCdata = true;
    protected boolean ncr = true;
    protected String cssPrefix;
    protected String replacementCharEncoding = "WIN1252";
    protected TagTable tt;
    protected Report report;
    protected int definedTags;
    protected char[] newline = System.getProperty("line.separator").toCharArray();
    private String inCharEncoding = "ISO8859_1";
    private String outCharEncoding = "ASCII";
    protected boolean rawOut;
    private transient Properties properties = new Properties();
    static /* synthetic */ Class class$org$w3c$tidy$Configuration;

    protected Configuration(Report report) {
        this.report = report;
    }

    private static void addConfigOption(Flag flag) {
        OPTIONS.put(flag.getName(), flag);
    }

    public void addProps(Properties p) {
        Enumeration<?> propEnum = p.propertyNames();
        while (propEnum.hasMoreElements()) {
            String key = (String)propEnum.nextElement();
            String value = p.getProperty(key);
            this.properties.put(key, value);
        }
        this.parseProps();
    }

    public void parseFile(String filename) {
        try {
            this.properties.load(new FileInputStream(filename));
        }
        catch (IOException e) {
            System.err.println(filename + " " + e.toString());
            return;
        }
        this.parseProps();
    }

    public static boolean isKnownOption(String name) {
        return name != null && OPTIONS.containsKey(name);
    }

    private void parseProps() {
        Iterator<Object> iterator = this.properties.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String)iterator.next();
            Flag flag = (Flag)OPTIONS.get(key);
            if (flag == null) {
                this.report.unknownOption(key);
                continue;
            }
            String stringValue = this.properties.getProperty(key);
            Object value = flag.getParser().parse(stringValue, key, this);
            if (flag.getLocation() == null) continue;
            try {
                flag.getLocation().set(this, value);
            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException("IllegalArgumentException during config initialization for field " + key + "with value [" + value + "]: " + e.getMessage());
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("IllegalArgumentException during config initialization for field " + key + "with value [" + value + "]: " + e.getMessage());
            }
        }
    }

    public void adjust() {
        if (this.encloseBlockText) {
            this.encloseBodyText = true;
        }
        if (this.smartIndent) {
            this.indentContent = true;
        }
        if (this.wraplen == 0) {
            this.wraplen = Integer.MAX_VALUE;
        }
        if (this.word2000) {
            this.definedTags |= 2;
            this.tt.defineTag((short)2, "o:p");
        }
        if (this.xmlTags) {
            this.xHTML = false;
        }
        if (this.xHTML) {
            this.xmlOut = true;
            this.upperCaseTags = false;
            this.upperCaseAttrs = false;
        }
        if (this.xmlTags) {
            this.xmlOut = true;
            this.xmlPIs = true;
        }
        if (!"UTF8".equals(this.getOutCharEncodingName()) && !"ASCII".equals(this.getOutCharEncodingName()) && this.xmlOut) {
            this.xmlPi = true;
        }
        if (this.xmlOut) {
            this.quoteAmpersand = true;
            this.hideEndTags = false;
        }
    }

    void printConfigOptions(Writer errout, boolean showActualConfiguration) {
        String pad = "                                                                               ";
        try {
            errout.write("\nConfiguration File Settings:\n\n");
            if (showActualConfiguration) {
                errout.write("Name                        Type       Current Value\n");
            } else {
                errout.write("Name                        Type       Allowable values\n");
            }
            errout.write("=========================== =========  ========================================\n");
            ArrayList values = new ArrayList(OPTIONS.values());
            Collections.sort(values);
            Iterator iterator = values.iterator();
            while (iterator.hasNext()) {
                Flag configItem = (Flag)iterator.next();
                errout.write(configItem.getName());
                errout.write(pad, 0, 28 - configItem.getName().length());
                errout.write(configItem.getParser().getType());
                errout.write(pad, 0, 11 - configItem.getParser().getType().length());
                if (showActualConfiguration) {
                    Field field = configItem.getLocation();
                    Object actualValue = null;
                    if (field != null) {
                        try {
                            actualValue = field.get(this);
                        }
                        catch (IllegalArgumentException e1) {
                            throw new RuntimeException("IllegalArgument when reading field " + field.getName());
                        }
                        catch (IllegalAccessException e1) {
                            throw new RuntimeException("IllegalAccess when reading field " + field.getName());
                        }
                    }
                    errout.write(configItem.getParser().getFriendlyName(configItem.getName(), actualValue, this));
                } else {
                    errout.write(configItem.getParser().getOptionValues());
                }
                errout.write("\n");
            }
            errout.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected String getInCharEncodingName() {
        return this.inCharEncoding;
    }

    protected void setInCharEncodingName(String encoding) {
        String javaEncoding = EncodingNameMapper.toJava(encoding);
        if (javaEncoding != null) {
            this.inCharEncoding = javaEncoding;
        }
    }

    protected String getOutCharEncodingName() {
        return this.outCharEncoding;
    }

    protected void setOutCharEncodingName(String encoding) {
        String javaEncoding = EncodingNameMapper.toJava(encoding);
        if (javaEncoding != null) {
            this.outCharEncoding = javaEncoding;
        }
    }

    protected void setInOutEncodingName(String encoding) {
        this.setInCharEncodingName(encoding);
        this.setOutCharEncodingName(encoding);
    }

    protected void setOutCharEncoding(int encoding) {
        this.setOutCharEncodingName(this.convertCharEncoding(encoding));
    }

    protected void setInCharEncoding(int encoding) {
        this.setInCharEncodingName(this.convertCharEncoding(encoding));
    }

    protected String convertCharEncoding(int code) {
        if (code != 0 && code < this.ENCODING_NAMES.length) {
            return this.ENCODING_NAMES[code];
        }
        return null;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Configuration.addConfigOption(new Flag("indent-spaces", "spaces", ParsePropertyImpl.INT));
        Configuration.addConfigOption(new Flag("wrap", "wraplen", ParsePropertyImpl.INT));
        Configuration.addConfigOption(new Flag("show-errors", "showErrors", ParsePropertyImpl.INT));
        Configuration.addConfigOption(new Flag("tab-size", "tabsize", ParsePropertyImpl.INT));
        Configuration.addConfigOption(new Flag("wrap-attributes", "wrapAttVals", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("wrap-script-literals", "wrapScriptlets", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("wrap-sections", "wrapSection", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("wrap-asp", "wrapAsp", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("wrap-jste", "wrapJste", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("wrap-php", "wrapPhp", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("literal-attributes", "literalAttribs", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("show-body-only", "bodyOnly", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("fix-uri", "fixUri", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("lower-literals", "lowerLiterals", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("hide-comments", "hideComments", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("indent-cdata", "indentCdata", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("force-output", "forceOutput", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("ascii-chars", "asciiChars", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("join-classes", "joinClasses", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("join-styles", "joinStyles", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("escape-cdata", "escapeCdata", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("replace-color", "replaceColor", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("quiet", "quiet", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("tidy-mark", "tidyMark", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("indent-attributes", "indentAttributes", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("hide-endtags", "hideEndTags", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("input-xml", "xmlTags", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("output-xml", "xmlOut", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("output-html", "htmlOut", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("output-xhtml", "xHTML", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("add-xml-pi", "xmlPi", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("add-xml-decl", "xmlPi", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("assume-xml-procins", "xmlPIs", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("uppercase-tags", "upperCaseTags", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("uppercase-attributes", "upperCaseAttrs", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("bare", "makeBare", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("clean", "makeClean", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("logical-emphasis", "logicalEmphasis", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("word-2000", "word2000", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("drop-empty-paras", "dropEmptyParas", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("drop-font-tags", "dropFontTags", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("drop-proprietary-attributes", "dropProprietaryAttributes", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("enclose-text", "encloseBodyText", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("enclose-block-text", "encloseBlockText", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("add-xml-space", "xmlSpace", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("fix-bad-comments", "fixComments", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("split", "burstSlides", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("break-before-br", "breakBeforeBR", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("numeric-entities", "numEntities", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("quote-marks", "quoteMarks", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("quote-nbsp", "quoteNbsp", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("quote-ampersand", "quoteAmpersand", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("write-back", "writeback", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("keep-time", "keepFileTimes", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("show-warnings", "showWarnings", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("ncr", "ncr", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("fix-backslash", "fixBackslash", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("gnu-emacs", "emacs", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("only-errors", "onlyErrors", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("output-raw", "rawOut", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("trim-empty-elements", "trimEmpty", ParsePropertyImpl.BOOL));
        Configuration.addConfigOption(new Flag("markup", "onlyErrors", ParsePropertyImpl.INVBOOL));
        Configuration.addConfigOption(new Flag("char-encoding", null, ParsePropertyImpl.CHAR_ENCODING));
        Configuration.addConfigOption(new Flag("input-encoding", null, ParsePropertyImpl.CHAR_ENCODING));
        Configuration.addConfigOption(new Flag("output-encoding", null, ParsePropertyImpl.CHAR_ENCODING));
        Configuration.addConfigOption(new Flag("error-file", "errfile", ParsePropertyImpl.NAME));
        Configuration.addConfigOption(new Flag("slide-style", "slidestyle", ParsePropertyImpl.NAME));
        Configuration.addConfigOption(new Flag("language", "language", ParsePropertyImpl.NAME));
        Configuration.addConfigOption(new Flag("new-inline-tags", null, ParsePropertyImpl.TAGNAMES));
        Configuration.addConfigOption(new Flag("new-blocklevel-tags", null, ParsePropertyImpl.TAGNAMES));
        Configuration.addConfigOption(new Flag("new-empty-tags", null, ParsePropertyImpl.TAGNAMES));
        Configuration.addConfigOption(new Flag("new-pre-tags", null, ParsePropertyImpl.TAGNAMES));
        Configuration.addConfigOption(new Flag("doctype", "docTypeStr", ParsePropertyImpl.DOCTYPE));
        Configuration.addConfigOption(new Flag("repeated-attributes", "duplicateAttrs", ParsePropertyImpl.REPEATED_ATTRIBUTES));
        Configuration.addConfigOption(new Flag("alt-text", "altText", ParsePropertyImpl.STRING));
        Configuration.addConfigOption(new Flag("indent", "indentContent", ParsePropertyImpl.INDENT));
        Configuration.addConfigOption(new Flag("css-prefix", "cssPrefix", ParsePropertyImpl.CSS1SELECTOR));
        Configuration.addConfigOption(new Flag("newline", null, ParsePropertyImpl.NEWLINE));
    }

    static class Flag
    implements Comparable {
        private String name;
        private String fieldName;
        private Field location;
        private ParseProperty parser;

        Flag(String name, String fieldName, ParseProperty parser) {
            this.fieldName = fieldName;
            this.name = name;
            this.parser = parser;
        }

        public Field getLocation() {
            if (this.fieldName != null && this.location == null) {
                try {
                    this.location = (class$org$w3c$tidy$Configuration == null ? (class$org$w3c$tidy$Configuration = Configuration.class$("org.w3c.tidy.Configuration")) : class$org$w3c$tidy$Configuration).getDeclaredField(this.fieldName);
                }
                catch (NoSuchFieldException e) {
                    throw new RuntimeException("NoSuchField exception during config initialization for field " + this.fieldName);
                }
                catch (SecurityException e) {
                    throw new RuntimeException("Security exception during config initialization for field " + this.fieldName + ": " + e.getMessage());
                }
            }
            return this.location;
        }

        public String getName() {
            return this.name;
        }

        public ParseProperty getParser() {
            return this.parser;
        }

        public boolean equals(Object obj) {
            return this.name.equals(((Flag)obj).name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public int compareTo(Object o) {
            return this.name.compareTo(((Flag)o).name);
        }
    }
}

