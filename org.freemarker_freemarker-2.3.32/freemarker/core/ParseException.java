/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.FMParserConstants;
import freemarker.core.TemplateObject;
import freemarker.core.Token;
import freemarker.core._MessageUtil;
import freemarker.template.Template;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ParseException
extends IOException
implements FMParserConstants {
    private static final String END_TAG_SYNTAX_HINT = "(Note that FreeMarker end-tags must have # or @ after the / character.)";
    public Token currentToken;
    private static volatile Boolean jbossToolsMode;
    private boolean messageAndDescriptionRendered;
    private String message;
    private String description;
    public int columnNumber;
    public int lineNumber;
    public int endColumnNumber;
    public int endLineNumber;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    protected String eol = SecurityUtilities.getSystemProperty("line.separator", "\n");
    @Deprecated
    protected boolean specialConstructor;
    private String templateName;

    public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
        super("");
        this.currentToken = currentTokenVal;
        this.specialConstructor = true;
        this.expectedTokenSequences = expectedTokenSequencesVal;
        this.tokenImage = tokenImageVal;
        this.lineNumber = this.currentToken.next.beginLine;
        this.columnNumber = this.currentToken.next.beginColumn;
        this.endLineNumber = this.currentToken.next.endLine;
        this.endColumnNumber = this.currentToken.next.endColumn;
    }

    @Deprecated
    protected ParseException() {
    }

    @Deprecated
    public ParseException(String description, int lineNumber, int columnNumber) {
        this(description, null, lineNumber, columnNumber, null);
    }

    public ParseException(String description, Template template, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber) {
        this(description, template, lineNumber, columnNumber, endLineNumber, endColumnNumber, null);
    }

    public ParseException(String description, Template template, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber, Throwable cause) {
        this(description, template == null ? null : template.getSourceName(), lineNumber, columnNumber, endLineNumber, endColumnNumber, cause);
    }

    @Deprecated
    public ParseException(String description, Template template, int lineNumber, int columnNumber) {
        this(description, template, lineNumber, columnNumber, null);
    }

    @Deprecated
    public ParseException(String description, Template template, int lineNumber, int columnNumber, Throwable cause) {
        this(description, template == null ? null : template.getSourceName(), lineNumber, columnNumber, 0, 0, cause);
    }

    public ParseException(String description, Template template, Token tk) {
        this(description, template, tk, null);
    }

    public ParseException(String description, Template template, Token tk, Throwable cause) {
        this(description, template == null ? null : template.getSourceName(), tk.beginLine, tk.beginColumn, tk.endLine, tk.endColumn, cause);
    }

    public ParseException(String description, TemplateObject tobj) {
        this(description, tobj, null);
    }

    public ParseException(String description, TemplateObject tobj, Throwable cause) {
        this(description, tobj.getTemplate() == null ? null : tobj.getTemplate().getSourceName(), tobj.beginLine, tobj.beginColumn, tobj.endLine, tobj.endColumn, cause);
    }

    private ParseException(String description, String templateName, int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber, Throwable cause) {
        super(description);
        try {
            this.initCause(cause);
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.description = description;
        this.templateName = templateName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.endLineNumber = endLineNumber;
        this.endColumnNumber = endColumnNumber;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
        ParseException parseException = this;
        synchronized (parseException) {
            this.messageAndDescriptionRendered = false;
            this.message = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getMessage() {
        ParseException parseException = this;
        synchronized (parseException) {
            if (this.messageAndDescriptionRendered) {
                return this.message;
            }
        }
        this.renderMessageAndDescription();
        parseException = this;
        synchronized (parseException) {
            return this.message;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getDescription() {
        ParseException parseException = this;
        synchronized (parseException) {
            if (this.messageAndDescriptionRendered) {
                return this.description;
            }
        }
        this.renderMessageAndDescription();
        parseException = this;
        synchronized (parseException) {
            return this.description;
        }
    }

    public String getEditorMessage() {
        return this.getDescription();
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public int getEndLineNumber() {
        return this.endLineNumber;
    }

    public int getEndColumnNumber() {
        return this.endColumnNumber;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderMessageAndDescription() {
        String desc = this.getOrRenderDescription();
        String prefix = !this.isInJBossToolsMode() ? "Syntax error " + _MessageUtil.formatLocationForSimpleParsingError(this.templateName, this.lineNumber, this.columnNumber) + ":\n" : "[col. " + this.columnNumber + "] ";
        String msg = prefix + desc;
        desc = msg.substring(prefix.length());
        ParseException parseException = this;
        synchronized (parseException) {
            this.message = msg;
            this.description = desc;
            this.messageAndDescriptionRendered = true;
        }
    }

    private boolean isInJBossToolsMode() {
        if (jbossToolsMode == null) {
            try {
                jbossToolsMode = ParseException.class.getClassLoader().toString().indexOf("[org.jboss.ide.eclipse.freemarker:") != -1;
            }
            catch (Throwable e) {
                jbossToolsMode = Boolean.FALSE;
            }
        }
        return jbossToolsMode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getOrRenderDescription() {
        Set<String> expectedEndTokenDescs;
        int unexpTokKind;
        ParseException parseException = this;
        synchronized (parseException) {
            if (this.description != null) {
                return this.description;
            }
        }
        if (this.currentToken == null) {
            return null;
        }
        Token unexpectedTok = this.currentToken.next;
        if (unexpectedTok.kind == 0) {
            Set<String> endTokenDescs = this.getExpectedEndTokenDescs();
            return "Unexpected end of file reached." + (endTokenDescs.size() == 0 ? "" : " You have an unclosed " + this.joinWithAnds(endTokenDescs) + ". Check if the FreeMarker end-tags are present, and aren't malformed. " + END_TAG_SYNTAX_HINT);
        }
        int maxExpectedTokenSequenceLength = 0;
        for (int i = 0; i < this.expectedTokenSequences.length; ++i) {
            int[] expectedTokenSequence = this.expectedTokenSequences[i];
            if (maxExpectedTokenSequenceLength >= expectedTokenSequence.length) continue;
            maxExpectedTokenSequenceLength = expectedTokenSequence.length;
        }
        StringBuilder tokenErrDesc = new StringBuilder();
        tokenErrDesc.append("Encountered ");
        boolean encounteredEndTag = false;
        for (int i = 0; i < maxExpectedTokenSequenceLength; ++i) {
            if (i != 0) {
                tokenErrDesc.append(" ");
            }
            if (unexpectedTok.kind == 0) {
                tokenErrDesc.append(this.tokenImage[0]);
                break;
            }
            String image = unexpectedTok.image;
            if (i == 0 && (image.startsWith("</") || image.startsWith("[/"))) {
                encounteredEndTag = true;
            }
            tokenErrDesc.append(StringUtil.jQuote(image));
            unexpectedTok = unexpectedTok.next;
        }
        if (this.getIsEndToken(unexpTokKind = this.currentToken.next.kind) || unexpTokKind == 54 || unexpTokKind == 9) {
            expectedEndTokenDescs = new LinkedHashSet<String>(this.getExpectedEndTokenDescs());
            if (unexpTokKind == 54 || unexpTokKind == 9) {
                expectedEndTokenDescs.remove(this.getEndTokenDescIfIsEndToken(36));
            } else {
                expectedEndTokenDescs.remove(this.getEndTokenDescIfIsEndToken(unexpTokKind));
            }
        } else {
            expectedEndTokenDescs = Collections.emptySet();
        }
        if (!expectedEndTokenDescs.isEmpty()) {
            if (unexpTokKind == 54 || unexpTokKind == 9) {
                tokenErrDesc.append(", which can only be used where an #if");
                if (unexpTokKind == 54) {
                    tokenErrDesc.append(" or #list");
                }
                tokenErrDesc.append(" could be closed");
            }
            tokenErrDesc.append(", but at this place only ");
            tokenErrDesc.append(expectedEndTokenDescs.size() > 1 ? "these" : "this");
            tokenErrDesc.append(" can be closed: ");
            boolean first = true;
            for (String expectedEndTokenDesc : expectedEndTokenDescs) {
                if (!first) {
                    tokenErrDesc.append(", ");
                } else {
                    first = false;
                }
                tokenErrDesc.append(!expectedEndTokenDesc.startsWith("\"") ? StringUtil.jQuote(expectedEndTokenDesc) : expectedEndTokenDesc);
            }
            tokenErrDesc.append(".");
            if (encounteredEndTag) {
                tokenErrDesc.append(" This usually because of wrong nesting of FreeMarker directives, like a missed or malformed end-tag somewhere. (Note that FreeMarker end-tags must have # or @ after the / character.)");
            }
            tokenErrDesc.append(this.eol);
            tokenErrDesc.append("Was ");
        } else {
            tokenErrDesc.append(", but was ");
        }
        if (this.expectedTokenSequences.length == 1) {
            tokenErrDesc.append("expecting pattern:");
        } else {
            tokenErrDesc.append("expecting one of these patterns:");
        }
        tokenErrDesc.append(this.eol);
        for (int i = 0; i < this.expectedTokenSequences.length; ++i) {
            if (i != 0) {
                tokenErrDesc.append(this.eol);
            }
            tokenErrDesc.append("    ");
            int[] expectedTokenSequence = this.expectedTokenSequences[i];
            for (int j = 0; j < expectedTokenSequence.length; ++j) {
                if (j != 0) {
                    tokenErrDesc.append(' ');
                }
                tokenErrDesc.append(this.tokenImage[expectedTokenSequence[j]]);
            }
        }
        return tokenErrDesc.toString();
    }

    private Set<String> getExpectedEndTokenDescs() {
        LinkedHashSet<String> endTokenDescs = new LinkedHashSet<String>();
        for (int i = 0; i < this.expectedTokenSequences.length; ++i) {
            int[] sequence = this.expectedTokenSequences[i];
            for (int j = 0; j < sequence.length; ++j) {
                int token = sequence[j];
                String endTokenDesc = this.getEndTokenDescIfIsEndToken(token);
                if (endTokenDesc == null) continue;
                endTokenDescs.add(endTokenDesc);
            }
        }
        return endTokenDescs;
    }

    private boolean getIsEndToken(int token) {
        return this.getEndTokenDescIfIsEndToken(token) != null;
    }

    private String getEndTokenDescIfIsEndToken(int token) {
        String endTokenDesc = null;
        switch (token) {
            case 42: {
                endTokenDesc = "#foreach";
                break;
            }
            case 37: {
                endTokenDesc = "#list";
                break;
            }
            case 39: {
                endTokenDesc = "#sep";
                break;
            }
            case 38: {
                endTokenDesc = "#items";
                break;
            }
            case 53: {
                endTokenDesc = "#switch";
                break;
            }
            case 36: {
                endTokenDesc = "#if";
                break;
            }
            case 51: {
                endTokenDesc = "#compress";
                break;
            }
            case 46: 
            case 47: {
                endTokenDesc = "#macro or #function";
                break;
            }
            case 52: {
                endTokenDesc = "#transform";
                break;
            }
            case 71: {
                endTokenDesc = "#escape";
                break;
            }
            case 73: {
                endTokenDesc = "#noescape";
                break;
            }
            case 43: 
            case 44: 
            case 45: {
                endTokenDesc = "#assign or #local or #global";
                break;
            }
            case 41: {
                endTokenDesc = "#attempt";
                break;
            }
            case 138: {
                endTokenDesc = "\"{\"";
                break;
            }
            case 134: {
                endTokenDesc = "\"[\"";
                break;
            }
            case 136: {
                endTokenDesc = "\"(\"";
                break;
            }
            case 75: {
                endTokenDesc = "@...";
            }
        }
        return endTokenDesc;
    }

    private String joinWithAnds(Collection<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String s : strings) {
            if (sb.length() != 0) {
                sb.append(" and ");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    protected String add_escapes(String str) {
        StringBuilder retval = new StringBuilder();
        block11: for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '\u0000': {
                    continue block11;
                }
                case '\b': {
                    retval.append("\\b");
                    continue block11;
                }
                case '\t': {
                    retval.append("\\t");
                    continue block11;
                }
                case '\n': {
                    retval.append("\\n");
                    continue block11;
                }
                case '\f': {
                    retval.append("\\f");
                    continue block11;
                }
                case '\r': {
                    retval.append("\\r");
                    continue block11;
                }
                case '\"': {
                    retval.append("\\\"");
                    continue block11;
                }
                case '\'': {
                    retval.append("\\'");
                    continue block11;
                }
                case '\\': {
                    retval.append("\\\\");
                    continue block11;
                }
                default: {
                    char ch = str.charAt(i);
                    if (ch < ' ' || ch > '~') {
                        String s = "0000" + Integer.toString(ch, 16);
                        retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                        continue block11;
                    }
                    retval.append(ch);
                }
            }
        }
        return retval.toString();
    }
}

