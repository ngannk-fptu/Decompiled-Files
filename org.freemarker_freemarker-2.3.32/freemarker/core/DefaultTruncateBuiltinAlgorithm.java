/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core.TruncateBuiltinAlgorithm;
import freemarker.core.XMLOutputFormat;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;

public class DefaultTruncateBuiltinAlgorithm
extends TruncateBuiltinAlgorithm {
    public static final String STANDARD_ASCII_TERMINATOR = "[...]";
    public static final String STANDARD_UNICODE_TERMINATOR = "[\u2026]";
    public static final TemplateHTMLOutputModel STANDARD_M_TERMINATOR;
    public static final double DEFAULT_WORD_BOUNDARY_MIN_LENGTH = 0.75;
    private static final int FALLBACK_M_TERMINATOR_LENGTH = 3;
    public static final DefaultTruncateBuiltinAlgorithm ASCII_INSTANCE;
    public static final DefaultTruncateBuiltinAlgorithm UNICODE_INSTANCE;
    private final TemplateScalarModel defaultTerminator;
    private final int defaultTerminatorLength;
    private final boolean defaultTerminatorRemovesDots;
    private final TemplateMarkupOutputModel<?> defaultMTerminator;
    private final Integer defaultMTerminatorLength;
    private final boolean defaultMTerminatorRemovesDots;
    private final double wordBoundaryMinLength;
    private final boolean addSpaceAtWordBoundary;

    public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, TemplateMarkupOutputModel<?> defaultMTerminator, boolean addSpaceAtWordBoundary) {
        this(defaultTerminator, null, null, defaultMTerminator, null, null, addSpaceAtWordBoundary, null);
    }

    public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, boolean addSpaceAtWordBoundary) {
        this(defaultTerminator, null, null, null, null, null, addSpaceAtWordBoundary, null);
    }

    public DefaultTruncateBuiltinAlgorithm(String defaultTerminator, Integer defaultTerminatorLength, Boolean defaultTerminatorRemovesDots, TemplateMarkupOutputModel<?> defaultMTerminator, Integer defaultMTerminatorLength, Boolean defaultMTerminatorRemovesDots, boolean addSpaceAtWordBoundary, Double wordBoundaryMinLength) {
        NullArgumentException.check("defaultTerminator", defaultTerminator);
        this.defaultTerminator = new SimpleScalar(defaultTerminator);
        try {
            this.defaultTerminatorLength = defaultTerminatorLength != null ? defaultTerminatorLength.intValue() : defaultTerminator.length();
            this.defaultTerminatorRemovesDots = defaultTerminatorRemovesDots != null ? defaultTerminatorRemovesDots.booleanValue() : this.getTerminatorRemovesDots(defaultTerminator);
        }
        catch (TemplateModelException e) {
            throw new IllegalArgumentException("Failed to examine defaultTerminator", e);
        }
        this.defaultMTerminator = defaultMTerminator;
        if (defaultMTerminator != null) {
            try {
                this.defaultMTerminatorLength = defaultMTerminatorLength != null ? defaultMTerminatorLength.intValue() : this.getMTerminatorLength(defaultMTerminator);
                this.defaultMTerminatorRemovesDots = defaultMTerminatorRemovesDots != null ? defaultMTerminatorRemovesDots.booleanValue() : this.getMTerminatorRemovesDots(defaultMTerminator);
            }
            catch (TemplateModelException e) {
                throw new IllegalArgumentException("Failed to examine defaultMTerminator", e);
            }
        } else {
            this.defaultMTerminatorLength = null;
            this.defaultMTerminatorRemovesDots = false;
        }
        if (wordBoundaryMinLength == null) {
            wordBoundaryMinLength = 0.75;
        } else if (wordBoundaryMinLength < 0.0 || wordBoundaryMinLength > 1.0) {
            throw new IllegalArgumentException("wordBoundaryMinLength must be between 0.0 and 1.0 (inclusive)");
        }
        this.wordBoundaryMinLength = wordBoundaryMinLength;
        this.addSpaceAtWordBoundary = addSpaceAtWordBoundary;
    }

    @Override
    public TemplateScalarModel truncate(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return (TemplateScalarModel)this.unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.AUTO, false);
    }

    @Override
    public TemplateScalarModel truncateW(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return (TemplateScalarModel)this.unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.WORD_BOUNDARY, false);
    }

    @Override
    public TemplateScalarModel truncateC(String s, int maxLength, TemplateScalarModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return (TemplateScalarModel)this.unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.CHAR_BOUNDARY, false);
    }

    @Override
    public TemplateModel truncateM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return this.unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.AUTO, true);
    }

    @Override
    public TemplateModel truncateWM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return this.unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.WORD_BOUNDARY, true);
    }

    @Override
    public TemplateModel truncateCM(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, Environment env) throws TemplateException {
        return this.unifiedTruncate(s, maxLength, terminator, terminatorLength, TruncationMode.CHAR_BOUNDARY, true);
    }

    public String getDefaultTerminator() {
        try {
            return this.defaultTerminator.getAsString();
        }
        catch (TemplateModelException e) {
            throw new IllegalStateException(e);
        }
    }

    public int getDefaultTerminatorLength() {
        return this.defaultTerminatorLength;
    }

    public boolean getDefaultTerminatorRemovesDots() {
        return this.defaultTerminatorRemovesDots;
    }

    public TemplateMarkupOutputModel<?> getDefaultMTerminator() {
        return this.defaultMTerminator;
    }

    public Integer getDefaultMTerminatorLength() {
        return this.defaultMTerminatorLength;
    }

    public boolean getDefaultMTerminatorRemovesDots() {
        return this.defaultMTerminatorRemovesDots;
    }

    public double getWordBoundaryMinLength() {
        return this.wordBoundaryMinLength;
    }

    public boolean getAddSpaceAtWordBoundary() {
        return this.addSpaceAtWordBoundary;
    }

    protected int getMTerminatorLength(TemplateMarkupOutputModel<?> mTerminator) throws TemplateModelException {
        MarkupOutputFormat<?> format = mTerminator.getOutputFormat();
        return this.isHTMLOrXML(format) ? DefaultTruncateBuiltinAlgorithm.getLengthWithoutTags(format.getMarkupString(mTerminator)) : 3;
    }

    protected boolean getTerminatorRemovesDots(String terminator) throws TemplateModelException {
        return terminator.startsWith(".") || terminator.startsWith("\u2026");
    }

    protected boolean getMTerminatorRemovesDots(TemplateMarkupOutputModel terminator) throws TemplateModelException {
        return this.isHTMLOrXML(terminator.getOutputFormat()) ? DefaultTruncateBuiltinAlgorithm.doesHtmlOrXmlStartWithDot(terminator.getOutputFormat().getMarkupString(terminator)) : true;
    }

    private TemplateModel unifiedTruncate(String s, int maxLength, TemplateModel terminator, Integer terminatorLength, TruncationMode mode, boolean allowMarkupResult) throws TemplateException {
        Boolean terminatorRemovesDots;
        if (s.length() <= maxLength) {
            return new SimpleScalar(s);
        }
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength can't be negative");
        }
        if (terminator == null) {
            if (allowMarkupResult && this.defaultMTerminator != null) {
                terminator = this.defaultMTerminator;
                terminatorLength = this.defaultMTerminatorLength;
                terminatorRemovesDots = this.defaultMTerminatorRemovesDots;
            } else {
                terminator = this.defaultTerminator;
                terminatorLength = this.defaultTerminatorLength;
                terminatorRemovesDots = this.defaultTerminatorRemovesDots;
            }
        } else {
            if (terminatorLength != null) {
                if (terminatorLength < 0) {
                    throw new IllegalArgumentException("terminatorLength can't be negative");
                }
            } else {
                terminatorLength = this.getTerminatorLength(terminator);
            }
            terminatorRemovesDots = null;
        }
        StringBuilder truncatedS = this.unifiedTruncateWithoutTerminatorAdded(s, maxLength, terminator, terminatorLength, terminatorRemovesDots, mode);
        if (truncatedS == null || truncatedS.length() == 0) {
            return terminator;
        }
        if (terminator instanceof TemplateScalarModel) {
            truncatedS.append(((TemplateScalarModel)((Object)terminator)).getAsString());
            return new SimpleScalar(truncatedS.toString());
        }
        if (terminator instanceof TemplateMarkupOutputModel) {
            TemplateMarkupOutputModel<?> markup = terminator;
            MarkupOutputFormat<?> outputFormat = markup.getOutputFormat();
            return outputFormat.concat(outputFormat.fromPlainTextByEscaping(truncatedS.toString()), markup);
        }
        throw new IllegalArgumentException("Unsupported terminator type: " + ClassUtil.getFTLTypeDescription(terminator));
    }

    private StringBuilder unifiedTruncateWithoutTerminatorAdded(String s, int maxLength, TemplateModel terminator, int terminatorLength, Boolean terminatorRemovesDots, TruncationMode mode) throws TemplateModelException {
        boolean skippedDots;
        int cbInitialLastCIdx;
        int cbLastCIdx = cbInitialLastCIdx = maxLength - terminatorLength - 1;
        if ((cbLastCIdx = this.skipTrailingWS(s, cbLastCIdx)) < 0) {
            return null;
        }
        if (mode == TruncationMode.AUTO && this.wordBoundaryMinLength < 1.0 || mode == TruncationMode.WORD_BOUNDARY) {
            int wbLastCIdx;
            boolean followingCIsWS;
            StringBuilder truncedS = null;
            int wordTerminatorLength = this.addSpaceAtWordBoundary ? terminatorLength + 1 : terminatorLength;
            int minIdx = mode == TruncationMode.AUTO ? Math.max((int)Math.ceil((double)maxLength * this.wordBoundaryMinLength) - wordTerminatorLength - 1, 0) : 0;
            boolean bl = followingCIsWS = s.length() > wbLastCIdx + 1 ? Character.isWhitespace(s.charAt(wbLastCIdx + 1)) : true;
            for (wbLastCIdx = Math.min(maxLength - wordTerminatorLength - 1, cbLastCIdx); wbLastCIdx >= minIdx; --wbLastCIdx) {
                char curC = s.charAt(wbLastCIdx);
                boolean curCIsWS = Character.isWhitespace(curC);
                if (!curCIsWS && followingCIsWS) {
                    if (!this.addSpaceAtWordBoundary && DefaultTruncateBuiltinAlgorithm.isDot(curC)) {
                        if (terminatorRemovesDots == null) {
                            terminatorRemovesDots = this.getTerminatorRemovesDots(terminator);
                        }
                        if (terminatorRemovesDots.booleanValue()) {
                            while (wbLastCIdx >= minIdx && DefaultTruncateBuiltinAlgorithm.isDotOrWS(s.charAt(wbLastCIdx))) {
                                --wbLastCIdx;
                            }
                            if (wbLastCIdx < minIdx) break;
                        }
                    }
                    truncedS = new StringBuilder(wbLastCIdx + 1 + wordTerminatorLength);
                    truncedS.append(s, 0, wbLastCIdx + 1);
                    if (!this.addSpaceAtWordBoundary) break;
                    truncedS.append(' ');
                    break;
                }
                followingCIsWS = curCIsWS;
            }
            if (truncedS != null || mode == TruncationMode.WORD_BOUNDARY || mode == TruncationMode.AUTO && this.wordBoundaryMinLength == 0.0) {
                return truncedS;
            }
        }
        if (cbLastCIdx == cbInitialLastCIdx && this.addSpaceAtWordBoundary && this.isWordEnd(s, cbLastCIdx) && --cbLastCIdx < 0) {
            return null;
        }
        do {
            skippedDots = false;
            if ((cbLastCIdx = this.skipTrailingWS(s, cbLastCIdx)) < 0) {
                return null;
            }
            if (!DefaultTruncateBuiltinAlgorithm.isDot(s.charAt(cbLastCIdx)) || this.addSpaceAtWordBoundary && this.isWordEnd(s, cbLastCIdx)) continue;
            if (terminatorRemovesDots == null) {
                terminatorRemovesDots = this.getTerminatorRemovesDots(terminator);
            }
            if (!terminatorRemovesDots.booleanValue()) continue;
            if ((cbLastCIdx = this.skipTrailingDots(s, cbLastCIdx)) < 0) {
                return null;
            }
            skippedDots = true;
        } while (skippedDots);
        boolean addWordBoundarySpace = this.addSpaceAtWordBoundary && this.isWordEnd(s, cbLastCIdx);
        StringBuilder truncatedS = new StringBuilder(cbLastCIdx + 1 + (addWordBoundarySpace ? 1 : 0) + terminatorLength);
        truncatedS.append(s, 0, cbLastCIdx + 1);
        if (addWordBoundarySpace) {
            truncatedS.append(' ');
        }
        return truncatedS;
    }

    private int getTerminatorLength(TemplateModel terminator) throws TemplateModelException {
        return terminator instanceof TemplateScalarModel ? ((TemplateScalarModel)terminator).getAsString().length() : this.getMTerminatorLength((TemplateMarkupOutputModel)terminator);
    }

    private boolean getTerminatorRemovesDots(TemplateModel terminator) throws TemplateModelException {
        return terminator instanceof TemplateScalarModel ? this.getTerminatorRemovesDots(((TemplateScalarModel)terminator).getAsString()) : this.getMTerminatorRemovesDots((TemplateMarkupOutputModel)terminator);
    }

    private int skipTrailingWS(String s, int lastCIdx) {
        while (lastCIdx >= 0 && Character.isWhitespace(s.charAt(lastCIdx))) {
            --lastCIdx;
        }
        return lastCIdx;
    }

    private int skipTrailingDots(String s, int lastCIdx) {
        while (lastCIdx >= 0 && DefaultTruncateBuiltinAlgorithm.isDot(s.charAt(lastCIdx))) {
            --lastCIdx;
        }
        return lastCIdx;
    }

    private boolean isWordEnd(String s, int lastCIdx) {
        return lastCIdx + 1 >= s.length() || Character.isWhitespace(s.charAt(lastCIdx + 1));
    }

    private static boolean isDot(char c) {
        return c == '.' || c == '\u2026';
    }

    private static boolean isDotOrWS(char c) {
        return DefaultTruncateBuiltinAlgorithm.isDot(c) || Character.isWhitespace(c);
    }

    private boolean isHTMLOrXML(MarkupOutputFormat<?> outputFormat) {
        return outputFormat instanceof HTMLOutputFormat || outputFormat instanceof XMLOutputFormat;
    }

    static int getLengthWithoutTags(String s) {
        int result = 0;
        int i = 0;
        int len = s.length();
        while (i < len) {
            char c;
            if ((c = s.charAt(i++)) == '<') {
                if (s.startsWith("!--", i)) {
                    i += 3;
                    while (i + 2 < len && (s.charAt(i) != '-' || s.charAt(i + 1) != '-' || s.charAt(i + 2) != '>')) {
                        ++i;
                    }
                    if ((i += 3) < len) continue;
                    break;
                }
                if (s.startsWith("![CDATA[", i)) {
                    i += 8;
                    while (i < len && (s.charAt(i) != ']' || i + 2 >= len || s.charAt(i + 1) != ']' || s.charAt(i + 2) != '>')) {
                        ++result;
                        ++i;
                    }
                    if ((i += 3) < len) continue;
                    break;
                }
                while (i < len && s.charAt(i) != '>') {
                    ++i;
                }
                if (++i < len) continue;
                break;
            }
            if (c == '&') {
                while (i < len && s.charAt(i) != ';') {
                    ++i;
                }
                ++result;
                if (++i < len) continue;
                break;
            }
            ++result;
        }
        return result;
    }

    static boolean doesHtmlOrXmlStartWithDot(String s) {
        int i = 0;
        int len = s.length();
        while (i < len) {
            char c;
            if ((c = s.charAt(i++)) == '<') {
                if (s.startsWith("!--", i)) {
                    i += 3;
                    while (i + 2 < len && ((c = s.charAt(i)) != '-' || s.charAt(i + 1) != '-' || s.charAt(i + 2) != '>')) {
                        ++i;
                    }
                    if ((i += 3) < len) continue;
                    break;
                }
                if (s.startsWith("![CDATA[", i)) {
                    if ((i += 8) < len && ((c = s.charAt(i)) != ']' || i + 2 >= len || s.charAt(i + 1) != ']' || s.charAt(i + 2) != '>')) {
                        return DefaultTruncateBuiltinAlgorithm.isDot(c);
                    }
                    if ((i += 3) < len) continue;
                    break;
                }
                while (i < len && s.charAt(i) != '>') {
                    ++i;
                }
                if (++i < len) continue;
                break;
            }
            if (c == '&') {
                int start = i;
                while (i < len && s.charAt(i) != ';') {
                    ++i;
                }
                return DefaultTruncateBuiltinAlgorithm.isDotCharReference(s.substring(start, i));
            }
            return DefaultTruncateBuiltinAlgorithm.isDot(c);
        }
        return false;
    }

    static boolean isDotCharReference(String name) {
        if (name.length() > 2 && name.charAt(0) == '#') {
            int charCode = DefaultTruncateBuiltinAlgorithm.getCodeFromNumericalCharReferenceName(name);
            return charCode == 8230 || charCode == 46;
        }
        return name.equals("hellip") || name.equals("period");
    }

    static int getCodeFromNumericalCharReferenceName(String name) {
        int pos;
        char c = name.charAt(1);
        boolean hex = c == 'x' || c == 'X';
        int code = 0;
        int n = pos = hex ? 2 : 1;
        while (pos < name.length()) {
            c = name.charAt(pos);
            code *= hex ? 16 : 10;
            if (c >= '0' && c <= '9') {
                code += c - 48;
            } else if (hex && c >= 'a' && c <= 'f') {
                code += c - 97 + 10;
            } else if (hex && c >= 'A' && c <= 'F') {
                code += c - 65 + 10;
            } else {
                return -1;
            }
            ++pos;
        }
        return code;
    }

    static {
        try {
            STANDARD_M_TERMINATOR = (TemplateHTMLOutputModel)HTMLOutputFormat.INSTANCE.fromMarkup("<span class='truncateTerminator'>[&#8230;]</span>");
        }
        catch (TemplateModelException e) {
            throw new IllegalStateException(e);
        }
        ASCII_INSTANCE = new DefaultTruncateBuiltinAlgorithm(STANDARD_ASCII_TERMINATOR, STANDARD_M_TERMINATOR, true);
        UNICODE_INSTANCE = new DefaultTruncateBuiltinAlgorithm(STANDARD_UNICODE_TERMINATOR, STANDARD_M_TERMINATOR, true);
    }

    private static enum TruncationMode {
        CHAR_BOUNDARY,
        WORD_BOUNDARY,
        AUTO;

    }
}

