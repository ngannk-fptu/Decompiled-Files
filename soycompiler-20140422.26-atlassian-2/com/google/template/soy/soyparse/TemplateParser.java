/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.soyparse;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soyparse.ParseException;
import com.google.template.soy.soyparse.SimpleCharStream;
import com.google.template.soy.soyparse.TemplateParserConstants;
import com.google.template.soy.soyparse.TemplateParserTokenManager;
import com.google.template.soy.soyparse.Token;
import com.google.template.soy.soyparse.TokenMgrError;
import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.CallParamValueNode;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.DebuggerNode;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachIfemptyNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.LetNode;
import com.google.template.soy.soytree.LetValueNode;
import com.google.template.soy.soytree.LogNode;
import com.google.template.soy.soytree.MsgFallbackGroupNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.MsgPluralCaseNode;
import com.google.template.soy.soytree.MsgPluralDefaultNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgSelectCaseNode;
import com.google.template.soy.soytree.MsgSelectDefaultNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.SwitchCaseNode;
import com.google.template.soy.soytree.SwitchDefaultNode;
import com.google.template.soy.soytree.SwitchNode;
import com.google.template.soy.soytree.TemplateNodeBuilder;
import com.google.template.soy.soytree.XidNode;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParser
implements TemplateParserConstants {
    private static final String LINE_BOUNDARY_REGEX = "\\s*?(\\n|\\r)\\s*";
    private static final Pattern LINE_BOUNDARY_PATTERN = Pattern.compile("\\s*?(\\n|\\r)\\s*");
    private static final Pattern START_EDGE_LINE_BOUNDARY_PATTERN = Pattern.compile("^\\s*?(\\n|\\r)\\s*");
    private static final Pattern END_EDGE_LINE_BOUNDARY_PATTERN = Pattern.compile("\\s*?(\\n|\\r)\\s*$");
    private static final Pattern NONEDGE_LINE_BOUNDARY_PATTERN = Pattern.compile("(?<=\\S)\\s*?(\\n|\\r)\\s*(?=\\S)");
    private IdGenerator nodeIdGen;
    private String filePath;
    private int lineNumOffset;
    private static final Map<String, String> SPECIAL_CHAR_CMD_NAME_TO_RAW_TEXT = ImmutableMap.builder().put((Object)"sp", (Object)" ").put((Object)"nil", (Object)"").put((Object)"\\n", (Object)"\n").put((Object)"\\r", (Object)"\r").put((Object)"\\t", (Object)"\t").put((Object)"lb", (Object)"{").put((Object)"rb", (Object)"}").build();
    public TemplateParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[43];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private static int[] jj_la1_3;
    private final JJCalls[] jj_2_rtns = new JJCalls[47];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final StackTracelessAtlassianLookaheadSuccess jj_ls = new StackTracelessAtlassianLookaheadSuccess();
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public TemplateParser(IdGenerator nodeIdGen, Reader input, String filePath, int templateContentStartLine) {
        this(input);
        Preconditions.checkNotNull((Object)nodeIdGen);
        this.nodeIdGen = nodeIdGen;
        this.filePath = filePath;
        this.lineNumOffset = templateContentStartLine - 1;
    }

    public TemplateParser(IdGenerator nodeIdGen, String input, String filePath, int templateContentStartLine) {
        this(nodeIdGen, new StringReader(input), filePath, templateContentStartLine);
    }

    public Pair<List<TemplateNodeBuilder.DeclInfo>, List<SoyNode.StandaloneNode>> parseTemplateContent() throws SoySyntaxException, TokenMgrError, ParseException {
        Preconditions.checkNotNull((Object)this.nodeIdGen);
        return this.TemplateContentInput();
    }

    private SourceLocation createSrcLoc(int lineNum) {
        return new SourceLocation(this.filePath, this.lineNumOffset + lineNum);
    }

    private <T extends SoyNode> T setSrcLoc(T node, int lineNum) {
        node.setSourceLocation(this.createSrcLoc(lineNum));
        return node;
    }

    private final SourceItemInfo<Void> SoyTagOpen() throws ParseException {
        Token tagOpen;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: {
                tagOpen = this.jj_consume_token(8);
                break;
            }
            case 9: {
                tagOpen = this.jj_consume_token(9);
                break;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return new SourceItemInfo<Object>(null, tagOpen.beginLine);
    }

    private final void RegSoyTagClose() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 60: {
                this.jj_consume_token(60);
                break;
            }
            case 62: {
                this.jj_consume_token(62);
                break;
            }
            case 71: {
                this.jj_consume_token(71);
                break;
            }
            case 73: {
                this.jj_consume_token(73);
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    private final void SelfEndingSoyTagClose() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 61: {
                this.jj_consume_token(61);
                break;
            }
            case 63: {
                this.jj_consume_token(63);
                break;
            }
            case 72: {
                this.jj_consume_token(72);
                break;
            }
            case 74: {
                this.jj_consume_token(74);
                break;
            }
            default: {
                this.jj_la1[2] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    private final String CmdText() throws ParseException {
        List<String> cmdTextParts = this.CmdTextParts();
        return Joiner.on((String)"").join(cmdTextParts);
    }

    private final List<String> CmdTextParts() throws ParseException {
        StringBuilder currCmdTextPartSb;
        ArrayList cmdTextParts;
        block21: {
            cmdTextParts = Lists.newArrayList();
            currCmdTextPartSb = new StringBuilder();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 25: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    String freakCmdName = this.AnyCmdName();
                    currCmdTextPartSb.append(freakCmdName);
                    this.jj_consume_token(65);
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                }
            }
            block15: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 66: 
                    case 67: 
                    case 69: 
                    case 70: {
                        break;
                    }
                    default: {
                        this.jj_la1[4] = this.jj_gen;
                        break block21;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 69: {
                        Token cmdTextChar = this.jj_consume_token(69);
                        currCmdTextPartSb.append(cmdTextChar.image);
                        continue block15;
                    }
                    case 70: {
                        Token cmdTextChar = this.jj_consume_token(70);
                        currCmdTextPartSb.append(cmdTextChar.image);
                        continue block15;
                    }
                    case 66: 
                    case 67: {
                        Token cmdTextSpecialPart;
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 66: {
                                cmdTextSpecialPart = this.jj_consume_token(66);
                                break;
                            }
                            case 67: {
                                cmdTextSpecialPart = this.jj_consume_token(67);
                                break;
                            }
                            default: {
                                this.jj_la1[5] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        if (currCmdTextPartSb.length() > 0) {
                            cmdTextParts.add(currCmdTextPartSb.toString());
                            currCmdTextPartSb = new StringBuilder();
                        }
                        cmdTextParts.add(cmdTextSpecialPart.image);
                        continue block15;
                    }
                }
                break;
            }
            this.jj_la1[6] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        if (currCmdTextPartSb.length() > 0) {
            cmdTextParts.add(currCmdTextPartSb.toString());
            currCmdTextPartSb = new StringBuilder();
        }
        int n = cmdTextParts.size();
        for (int i = 0; i < n; ++i) {
            String cmdTextPart = (String)cmdTextParts.get(i);
            if (i == 0) {
                cmdTextPart = CharMatcher.whitespace().trimLeadingFrom((CharSequence)cmdTextPart);
            }
            if (i == n - 1) {
                cmdTextPart = CharMatcher.whitespace().trimTrailingFrom((CharSequence)cmdTextPart);
            }
            cmdTextPart = LINE_BOUNDARY_PATTERN.matcher(cmdTextPart).replaceAll(" ");
            cmdTextParts.set(i, cmdTextPart);
        }
        return cmdTextParts;
    }

    private final String AnyCmdName() throws ParseException {
        Token cmdName;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14: {
                cmdName = this.jj_consume_token(14);
                break;
            }
            case 15: {
                cmdName = this.jj_consume_token(15);
                break;
            }
            case 16: {
                cmdName = this.jj_consume_token(16);
                break;
            }
            case 17: {
                cmdName = this.jj_consume_token(17);
                break;
            }
            case 18: {
                cmdName = this.jj_consume_token(18);
                break;
            }
            case 19: {
                cmdName = this.jj_consume_token(19);
                break;
            }
            case 20: {
                cmdName = this.jj_consume_token(20);
                break;
            }
            case 21: {
                cmdName = this.jj_consume_token(21);
                break;
            }
            case 22: {
                cmdName = this.jj_consume_token(22);
                break;
            }
            case 23: {
                cmdName = this.jj_consume_token(23);
                break;
            }
            case 25: {
                cmdName = this.jj_consume_token(25);
                break;
            }
            case 30: {
                cmdName = this.jj_consume_token(30);
                break;
            }
            case 31: {
                cmdName = this.jj_consume_token(31);
                break;
            }
            case 32: {
                cmdName = this.jj_consume_token(32);
                break;
            }
            case 33: {
                cmdName = this.jj_consume_token(33);
                break;
            }
            case 34: {
                cmdName = this.jj_consume_token(34);
                break;
            }
            case 35: {
                cmdName = this.jj_consume_token(35);
                break;
            }
            case 36: {
                cmdName = this.jj_consume_token(36);
                break;
            }
            case 37: {
                cmdName = this.jj_consume_token(37);
                break;
            }
            case 38: {
                cmdName = this.jj_consume_token(38);
                break;
            }
            case 39: {
                cmdName = this.jj_consume_token(39);
                break;
            }
            case 40: {
                cmdName = this.jj_consume_token(40);
                break;
            }
            case 41: {
                cmdName = this.jj_consume_token(41);
                break;
            }
            case 42: {
                cmdName = this.jj_consume_token(42);
                break;
            }
            case 43: {
                cmdName = this.jj_consume_token(43);
                break;
            }
            case 44: {
                cmdName = this.jj_consume_token(44);
                break;
            }
            case 45: {
                cmdName = this.jj_consume_token(45);
                break;
            }
            case 46: {
                cmdName = this.jj_consume_token(46);
                break;
            }
            case 47: {
                cmdName = this.jj_consume_token(47);
                break;
            }
            case 48: {
                cmdName = this.jj_consume_token(48);
                break;
            }
            case 49: {
                cmdName = this.jj_consume_token(49);
                break;
            }
            case 50: {
                cmdName = this.jj_consume_token(50);
                break;
            }
            case 51: {
                cmdName = this.jj_consume_token(51);
                break;
            }
            case 52: {
                cmdName = this.jj_consume_token(52);
                break;
            }
            case 53: {
                cmdName = this.jj_consume_token(53);
                break;
            }
            case 54: {
                cmdName = this.jj_consume_token(54);
                break;
            }
            case 55: {
                cmdName = this.jj_consume_token(55);
                break;
            }
            case 56: {
                cmdName = this.jj_consume_token(56);
                break;
            }
            case 57: {
                cmdName = this.jj_consume_token(57);
                break;
            }
            default: {
                this.jj_la1[7] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return cmdName.image;
    }

    private final SourceItemInfo<String> ConsecWsNoNl() throws ParseException {
        StringBuilder sb = new StringBuilder();
        int lineNum = -1;
        block7: while (true) {
            Token token;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 86: {
                    token = this.jj_consume_token(86);
                    sb.append(token.image);
                    break;
                }
                case 7: {
                    token = this.jj_consume_token(7);
                    break;
                }
                default: {
                    this.jj_la1[8] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (lineNum == -1) {
                lineNum = token.beginLine;
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: 
                case 86: {
                    continue block7;
                }
            }
            break;
        }
        this.jj_la1[9] = this.jj_gen;
        return new SourceItemInfo<String>(sb.toString(), lineNum);
    }

    private final SourceItemInfo<String> ConsecWsWithNl() throws ParseException {
        int lineNum;
        StringBuilder sb;
        block12: {
            SourceItemInfo<String> consecWsNoNl;
            sb = new StringBuilder();
            lineNum = -1;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: 
                case 86: {
                    consecWsNoNl = this.ConsecWsNoNl();
                    sb.append((String)consecWsNoNl.parsedContent);
                    lineNum = consecWsNoNl.lineNum;
                    break;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                }
            }
            Token token = this.jj_consume_token(85);
            sb.append(token.image);
            if (lineNum == -1) {
                lineNum = token.beginLine;
            }
            block10: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 7: 
                    case 85: 
                    case 86: {
                        break;
                    }
                    default: {
                        this.jj_la1[11] = this.jj_gen;
                        break block12;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 85: {
                        token = this.jj_consume_token(85);
                        sb.append(token.image);
                        continue block10;
                    }
                    case 7: 
                    case 86: {
                        consecWsNoNl = this.ConsecWsNoNl();
                        sb.append((String)consecWsNoNl.parsedContent);
                        continue block10;
                    }
                }
                break;
            }
            this.jj_la1[12] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        return new SourceItemInfo<String>(sb.toString(), lineNum);
    }

    private final void ConsecWsWithNlLookaheadHelper() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 7: 
            case 86: {
                this.ConsecWsNoNl();
                break;
            }
            default: {
                this.jj_la1[13] = this.jj_gen;
            }
        }
        this.jj_consume_token(85);
    }

    private final SourceItemInfo<String> ConsecWs() throws ParseException {
        SourceItemInfo<String> consecWs;
        if (this.jj_2_1(Integer.MAX_VALUE)) {
            consecWs = this.ConsecWsWithNl();
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: 
                case 86: {
                    consecWs = this.ConsecWsNoNl();
                    break;
                }
                default: {
                    this.jj_la1[14] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        return consecWs;
    }

    private final Token BlockCommentToken() throws ParseException {
        Token token;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6: {
                token = this.jj_consume_token(6);
                break;
            }
            case 7: {
                token = this.jj_consume_token(7);
                break;
            }
            default: {
                this.jj_la1[15] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return token;
    }

    private final SourceItemInfo<String> WsBasicRawTextNoNl() throws ParseException {
        StringBuilder sb = new StringBuilder();
        int lineNum = -1;
        block7: while (true) {
            Token token;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 86: {
                    token = this.jj_consume_token(86);
                    sb.append(token.image);
                    break;
                }
                case 6: 
                case 7: {
                    token = this.BlockCommentToken();
                    break;
                }
                default: {
                    this.jj_la1[16] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (lineNum == -1) {
                lineNum = token.beginLine;
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6: 
                case 7: 
                case 86: {
                    continue block7;
                }
            }
            break;
        }
        this.jj_la1[17] = this.jj_gen;
        return new SourceItemInfo<String>(sb.toString(), lineNum);
    }

    private final Token BasicRawTextToken() throws ParseException {
        Token token;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 85: {
                token = this.jj_consume_token(85);
                break;
            }
            case 86: {
                token = this.jj_consume_token(86);
                break;
            }
            case 87: {
                token = this.jj_consume_token(87);
                break;
            }
            default: {
                this.jj_la1[18] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return token;
    }

    private final SourceItemInfo<String> BasicRawText() throws ParseException {
        StringBuilder basicRawTextSb = new StringBuilder();
        int lineNum = -1;
        block7: while (true) {
            Token token;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 85: 
                case 86: 
                case 87: {
                    token = this.BasicRawTextToken();
                    basicRawTextSb.append(token.image);
                    break;
                }
                case 6: 
                case 7: {
                    token = this.BlockCommentToken();
                    break;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (lineNum == -1) {
                lineNum = token.beginLine;
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6: 
                case 7: 
                case 85: 
                case 86: 
                case 87: {
                    continue block7;
                }
            }
            break;
        }
        this.jj_la1[20] = this.jj_gen;
        String basicRawText = basicRawTextSb.toString();
        basicRawText = START_EDGE_LINE_BOUNDARY_PATTERN.matcher(basicRawText).replaceFirst("");
        int numSpaceCharsRemovedFromFront = basicRawTextSb.length() - basicRawText.length();
        for (int i = 0; i < numSpaceCharsRemovedFromFront; ++i) {
            char ch = basicRawTextSb.charAt(i);
            if (ch == '\r') {
                if (i + 1 != numSpaceCharsRemovedFromFront && basicRawTextSb.charAt(i + 1) == '\n') continue;
                ++lineNum;
                continue;
            }
            if (ch != '\n') continue;
            ++lineNum;
        }
        basicRawText = END_EDGE_LINE_BOUNDARY_PATTERN.matcher(basicRawText).replaceFirst("");
        Matcher matcher = NONEDGE_LINE_BOUNDARY_PATTERN.matcher(basicRawText);
        StringBuffer basicRawTextWithoutNewlinesSb = new StringBuffer(basicRawText.length());
        while (matcher.find()) {
            char charBefore = basicRawText.charAt(matcher.start() - 1);
            char charAfter = basicRawText.charAt(matcher.end());
            matcher.appendReplacement(basicRawTextWithoutNewlinesSb, charBefore == '>' || charAfter == '<' ? "" : " ");
        }
        matcher.appendTail(basicRawTextWithoutNewlinesSb);
        return new SourceItemInfo<String>(basicRawTextWithoutNewlinesSb.toString(), lineNum);
    }

    public final void MaybeWhitespace(String errorMessage) throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 6: 
            case 7: 
            case 85: 
            case 86: 
            case 87: {
                SourceItemInfo<String> basicRawText = this.BasicRawText();
                if (((String)basicRawText.parsedContent).trim().length() == 0) break;
                throw new ParseException(errorMessage + " Found on line " + (this.lineNumOffset + basicRawText.lineNum));
            }
            default: {
                this.jj_la1[21] = this.jj_gen;
            }
        }
    }

    private final SourceItemInfo<String> LiteralRawText() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(21);
        this.RegSoyTagClose();
        Token literalRawTextContent = this.jj_consume_token(82);
        return new SourceItemInfo<String>(literalRawTextContent.image, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> SpecialCharRawText() throws ParseException {
        Token specialCharCmdName;
        this.SoyTagOpen();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 14: {
                specialCharCmdName = this.jj_consume_token(14);
                break;
            }
            case 15: {
                specialCharCmdName = this.jj_consume_token(15);
                break;
            }
            case 16: {
                specialCharCmdName = this.jj_consume_token(16);
                break;
            }
            case 17: {
                specialCharCmdName = this.jj_consume_token(17);
                break;
            }
            case 18: {
                specialCharCmdName = this.jj_consume_token(18);
                break;
            }
            case 19: {
                specialCharCmdName = this.jj_consume_token(19);
                break;
            }
            case 20: {
                specialCharCmdName = this.jj_consume_token(20);
                break;
            }
            default: {
                this.jj_la1[22] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(SPECIAL_CHAR_CMD_NAME_TO_RAW_TEXT.get(specialCharCmdName.image), specialCharCmdName.beginLine);
    }

    private final SourceItemInfo<String> ContiguousRawText() throws ParseException {
        StringBuilder sb = new StringBuilder();
        int lineNum = -1;
        block6: do {
            block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6: 
                case 7: 
                case 85: 
                case 86: 
                case 87: {
                    SourceItemInfo<String> basicRawText = this.BasicRawText();
                    if (((String)basicRawText.parsedContent).length() == 0) continue block6;
                    if (lineNum == -1) {
                        lineNum = basicRawText.lineNum;
                    }
                    sb.append((String)basicRawText.parsedContent);
                    break;
                }
                default: {
                    this.jj_la1[23] = this.jj_gen;
                    if (this.jj_2_2(Integer.MAX_VALUE)) {
                        SourceItemInfo<String> literalRawText = this.LiteralRawText();
                        if (lineNum == -1) {
                            lineNum = literalRawText.lineNum;
                        }
                        sb.append((String)literalRawText.parsedContent);
                        break;
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 8: 
                        case 9: {
                            SourceItemInfo<String> specialCharRawText = this.SpecialCharRawText();
                            if (lineNum == -1) {
                                lineNum = specialCharRawText.lineNum;
                            }
                            sb.append((String)specialCharRawText.parsedContent);
                            break block0;
                        }
                    }
                    this.jj_la1[24] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        } while (this.jj_2_3(Integer.MAX_VALUE));
        return new SourceItemInfo<String>(sb.toString(), lineNum);
    }

    private final void ContiguousRawTextLookaheadHelper() throws ParseException {
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 85: 
            case 86: 
            case 87: {
                this.BasicRawTextToken();
                break;
            }
            case 6: 
            case 7: {
                this.BlockCommentToken();
                break;
            }
            default: {
                this.jj_la1[25] = this.jj_gen;
                if (this.jj_2_4(Integer.MAX_VALUE)) {
                    this.SoyTagOpen();
                    this.jj_consume_token(21);
                    this.RegSoyTagClose();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 8: 
                    case 9: {
                        this.SpecialCharRawText();
                        break block0;
                    }
                }
                this.jj_la1[26] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    private final SourceItemInfo<String> ParamDeclTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(12);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> OptParamDeclTag() throws ParseException {
        String optText = "";
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(12);
        this.jj_consume_token(97);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> MsgTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(23);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> FallbackmsgTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(24);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final void EndMsgTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(25);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<String> PluralTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(26);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final void EndPluralTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(27);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<String> SelectTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(28);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final void EndSelectTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(29);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<List<String>> PrintTag() throws ParseException {
        ArrayList printTagParts = Lists.newArrayList();
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        if (this.jj_2_5(Integer.MAX_VALUE)) {
            this.jj_consume_token(30);
            this.jj_consume_token(64);
            printTagParts.add("print");
        }
        List<String> cmdTextParts = this.CmdTextParts();
        printTagParts.addAll(cmdTextParts);
        this.RegSoyTagClose();
        return new SourceItemInfo<List<String>>(printTagParts, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> XidTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(31);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> CssTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(32);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> LetTagSelfEnding() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(33);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.SelfEndingSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> LetTagNotSelfEnding() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(33);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final void EndLetTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(34);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<String> IfTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(35);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> ElseifTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(36);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<Void> ElseTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(37);
        this.RegSoyTagClose();
        return tagOpen;
    }

    private final void EndIfTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(38);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<String> SwitchTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(39);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final void EndSwitchTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(40);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<String> ForeachTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(41);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<Void> IfemptyTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(42);
        this.RegSoyTagClose();
        return tagOpen;
    }

    private final void EndForeachTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(43);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<String> ForTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(44);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final void EndForTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(45);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<List<String>> AnyCallTagSelfEnding() throws ParseException {
        ArrayList callTagParts = Lists.newArrayList();
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        Token cmdName = this.jj_consume_token(46);
        callTagParts.add(cmdName.image);
        this.jj_consume_token(64);
        List<String> cmdTextParts = this.CmdTextParts();
        callTagParts.addAll(cmdTextParts);
        this.SelfEndingSoyTagClose();
        return new SourceItemInfo<List<String>>(callTagParts, tagOpen.lineNum);
    }

    private final SourceItemInfo<List<String>> AnyCallTagNotSelfEnding() throws ParseException {
        ArrayList callTagParts = Lists.newArrayList();
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        Token cmdName = this.jj_consume_token(46);
        callTagParts.add(cmdName.image);
        this.jj_consume_token(64);
        List<String> cmdTextParts = this.CmdTextParts();
        callTagParts.addAll(cmdTextParts);
        this.RegSoyTagClose();
        return new SourceItemInfo<List<String>>(callTagParts, tagOpen.lineNum);
    }

    private final boolean EndAnyCallTag() throws ParseException {
        this.SoyTagOpen();
        Token cmdName = this.jj_consume_token(47);
        this.RegSoyTagClose();
        return cmdName.image.equals("/call");
    }

    private final SourceItemInfo<String> ParamTagSelfEnding() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(48);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.SelfEndingSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<String> ParamTagNotSelfEnding() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(48);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final void EndParamTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(49);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<Void> LogTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(50);
        this.RegSoyTagClose();
        return tagOpen;
    }

    private final void EndLogTag() throws ParseException {
        this.SoyTagOpen();
        this.jj_consume_token(51);
        this.RegSoyTagClose();
    }

    private final SourceItemInfo<Void> DebuggerTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(52);
        this.RegSoyTagClose();
        return tagOpen;
    }

    private final SourceItemInfo<String> CaseTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(53);
        this.jj_consume_token(64);
        String cmdText = this.CmdText();
        this.RegSoyTagClose();
        return new SourceItemInfo<String>(cmdText, tagOpen.lineNum);
    }

    private final SourceItemInfo<Void> DefaultTag() throws ParseException {
        SourceItemInfo<Void> tagOpen = this.SoyTagOpen();
        this.jj_consume_token(54);
        this.RegSoyTagClose();
        return tagOpen;
    }

    private final Pair<List<TemplateNodeBuilder.DeclInfo>, List<SoyNode.StandaloneNode>> TemplateContentInput() throws ParseException {
        List<TemplateNodeBuilder.DeclInfo> headerDecls;
        if (this.jj_2_6(Integer.MAX_VALUE)) {
            headerDecls = this.TemplateHeader();
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6: 
                case 7: 
                case 86: {
                    this.WsBasicRawTextNoNl();
                    break;
                }
                default: {
                    this.jj_la1[27] = this.jj_gen;
                }
            }
            headerDecls = null;
        }
        List<SoyNode.StandaloneNode> bodyNodes = this.TemplateBlock();
        this.jj_consume_token(0);
        if (this.token_source.curLexState == 4 || this.token_source.curLexState == 5) {
            throw new ParseException("At end of template, found comment block that is never closed.");
        }
        return Pair.of(headerDecls, bodyNodes);
    }

    private final List<TemplateNodeBuilder.DeclInfo> TemplateHeader() throws ParseException {
        ArrayList declInfos = Lists.newArrayList();
        do {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: 
                case 85: 
                case 86: {
                    this.ConsecWs();
                    break;
                }
                default: {
                    this.jj_la1[28] = this.jj_gen;
                }
            }
            TemplateNodeBuilder.DeclInfo declInfo = this.Decl();
            declInfos.add(declInfo);
        } while (this.jj_2_7(Integer.MAX_VALUE));
        this.ConsecWsWithNl();
        return declInfos;
    }

    private final void TemplateHeaderLookaheadHelper() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 7: 
            case 85: 
            case 86: {
                this.ConsecWs();
                break;
            }
            default: {
                this.jj_la1[29] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: 
            case 9: {
                this.SoyTagOpen();
                this.jj_consume_token(12);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 97: {
                        this.jj_consume_token(97);
                        break;
                    }
                    default: {
                        this.jj_la1[30] = this.jj_gen;
                    }
                }
                this.jj_consume_token(64);
                break;
            }
            case 6: {
                this.jj_consume_token(6);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 7: 
                    case 85: 
                    case 86: {
                        this.ConsecWs();
                        break;
                    }
                    default: {
                        this.jj_la1[31] = this.jj_gen;
                    }
                }
                this.SoyTagOpen();
                this.jj_consume_token(12);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 97: {
                        this.jj_consume_token(97);
                        break;
                    }
                    default: {
                        this.jj_la1[32] = this.jj_gen;
                    }
                }
                this.jj_consume_token(64);
                break;
            }
            default: {
                this.jj_la1[33] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    private final TemplateNodeBuilder.DeclInfo Decl() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: 
            case 9: {
                TemplateNodeBuilder.DeclInfo declInfo = this.ParamDecl();
                return declInfo;
            }
            case 6: {
                TemplateNodeBuilder.DeclInfo declInfo = this.ParamDeclWithDocPrefix();
                return declInfo;
            }
        }
        this.jj_la1[34] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    private final TemplateNodeBuilder.DeclInfo ParamDecl() throws ParseException {
        String desc;
        String tag;
        SourceItemInfo<String> cmdText;
        if (this.jj_2_8(Integer.MAX_VALUE)) {
            cmdText = this.ParamDeclTag();
            tag = "@param";
        } else if (this.jj_2_9(Integer.MAX_VALUE)) {
            cmdText = this.OptParamDeclTag();
            tag = "@param?";
        } else {
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        if (this.jj_2_10(Integer.MAX_VALUE)) {
            block3: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 86: {
                        break;
                    }
                    default: {
                        this.jj_la1[35] = this.jj_gen;
                        break block3;
                    }
                }
                this.jj_consume_token(86);
            }
            Token blockDocComment = this.jj_consume_token(6);
            desc = blockDocComment.image;
        } else {
            desc = null;
        }
        return new TemplateNodeBuilder.DeclInfo(tag, (String)cmdText.parsedContent, desc);
    }

    private final TemplateNodeBuilder.DeclInfo ParamDeclWithDocPrefix() throws ParseException {
        String tag;
        SourceItemInfo<String> cmdText;
        Token blockDocComment = this.jj_consume_token(6);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 7: 
            case 85: 
            case 86: {
                this.ConsecWs();
                break;
            }
            default: {
                this.jj_la1[36] = this.jj_gen;
            }
        }
        if (this.jj_2_11(Integer.MAX_VALUE)) {
            cmdText = this.ParamDeclTag();
            tag = "@param";
        } else if (this.jj_2_12(Integer.MAX_VALUE)) {
            cmdText = this.OptParamDeclTag();
            tag = "@param?";
        } else {
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        return new TemplateNodeBuilder.DeclInfo(tag, (String)cmdText.parsedContent, blockDocComment.image);
    }

    private final List<SoyNode.StandaloneNode> TemplateBlock() throws ParseException {
        ArrayList templateBlock = Lists.newArrayList();
        block3: while (this.jj_2_13(Integer.MAX_VALUE)) {
            if (this.jj_2_14(Integer.MAX_VALUE)) {
                RawTextNode contiguousRawTextAsNode = this.ContiguousRawTextAsNode();
                if (contiguousRawTextAsNode == null) continue;
                templateBlock.add(contiguousRawTextAsNode);
                continue;
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: 
                case 9: {
                    SoyNode.StatementNode stmt = this.Stmt();
                    templateBlock.add(stmt);
                    continue block3;
                }
            }
            this.jj_la1[37] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        return templateBlock;
    }

    private final RawTextNode ContiguousRawTextAsNode() throws ParseException {
        SourceItemInfo<String> contiguousRawText = this.ContiguousRawText();
        return ((String)contiguousRawText.parsedContent).length() > 0 ? this.setSrcLoc(new RawTextNode(this.nodeIdGen.genId(), (String)contiguousRawText.parsedContent), contiguousRawText.lineNum) : null;
    }

    private final SoyNode.StatementNode Stmt() throws ParseException {
        AbstractSoyNode stmt;
        if (this.jj_2_15(Integer.MAX_VALUE)) {
            stmt = this.MsgStmt();
        } else if (this.jj_2_16(Integer.MAX_VALUE)) {
            stmt = this.PrintStmt();
        } else if (this.jj_2_17(Integer.MAX_VALUE)) {
            stmt = this.XidStmt();
        } else if (this.jj_2_18(Integer.MAX_VALUE)) {
            stmt = this.CssStmt();
        } else if (this.jj_2_19(Integer.MAX_VALUE)) {
            stmt = this.LetStmt();
        } else if (this.jj_2_20(Integer.MAX_VALUE)) {
            stmt = this.IfStmt();
        } else if (this.jj_2_21(Integer.MAX_VALUE)) {
            stmt = this.SwitchStmt();
        } else if (this.jj_2_22(Integer.MAX_VALUE)) {
            stmt = this.ForeachStmt();
        } else if (this.jj_2_23(Integer.MAX_VALUE)) {
            stmt = this.ForStmt();
        } else if (this.jj_2_24(Integer.MAX_VALUE)) {
            stmt = this.CallStmt();
        } else if (this.jj_2_25(Integer.MAX_VALUE)) {
            stmt = this.LogStmt();
        } else if (this.jj_2_26(Integer.MAX_VALUE)) {
            stmt = this.DebuggerStmt();
        } else {
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        return stmt;
    }

    private final void StmtLookaheadHelper() throws ParseException {
        if (this.jj_2_27(Integer.MAX_VALUE)) {
            this.PrintStmtLookaheadHelper();
        } else {
            block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: 
                case 9: {
                    this.SoyTagOpen();
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 23: {
                            this.jj_consume_token(23);
                            break;
                        }
                        case 31: {
                            this.jj_consume_token(31);
                            break;
                        }
                        case 32: {
                            this.jj_consume_token(32);
                            break;
                        }
                        case 33: {
                            this.jj_consume_token(33);
                            break;
                        }
                        case 35: {
                            this.jj_consume_token(35);
                            break;
                        }
                        case 39: {
                            this.jj_consume_token(39);
                            break;
                        }
                        case 41: {
                            this.jj_consume_token(41);
                            break;
                        }
                        case 44: {
                            this.jj_consume_token(44);
                            break;
                        }
                        case 46: {
                            this.jj_consume_token(46);
                            break;
                        }
                        case 50: {
                            this.jj_consume_token(50);
                            break;
                        }
                        case 52: {
                            this.jj_consume_token(52);
                            break;
                        }
                        default: {
                            this.jj_la1[38] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 64: {
                            this.jj_consume_token(64);
                            break block0;
                        }
                        case 60: 
                        case 62: 
                        case 71: 
                        case 73: {
                            this.RegSoyTagClose();
                            break block0;
                        }
                    }
                    this.jj_la1[39] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                default: {
                    this.jj_la1[40] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
    }

    private final MsgFallbackGroupNode MsgStmt() throws ParseException {
        SourceItemInfo<String> cmdText = this.MsgTag();
        MsgFallbackGroupNode msgFbGrpNode = this.setSrcLoc(new MsgFallbackGroupNode(this.nodeIdGen.genId()), cmdText.lineNum);
        MsgNode msgNode = this.setSrcLoc(new MsgNode(this.nodeIdGen.genId(), "msg", (String)cmdText.parsedContent), cmdText.lineNum);
        msgFbGrpNode.addChild(msgNode);
        List<SoyNode.StandaloneNode> templateBlockForMsg = this.TemplateBlockForMsg();
        msgNode.addChildren(templateBlockForMsg);
        while (this.jj_2_28(Integer.MAX_VALUE)) {
            cmdText = this.FallbackmsgTag();
            msgNode = this.setSrcLoc(new MsgNode(this.nodeIdGen.genId(), "fallbackmsg", (String)cmdText.parsedContent), cmdText.lineNum);
            msgFbGrpNode.addChild(msgNode);
            templateBlockForMsg = this.TemplateBlockForMsg();
            msgNode.addChildren(templateBlockForMsg);
        }
        this.EndMsgTag();
        if (msgFbGrpNode.numChildren() > 2) {
            throw SoySyntaxExceptionUtils.createWithNode("Multiple 'fallbackmsg's is not allowed.", msgFbGrpNode);
        }
        return msgFbGrpNode;
    }

    private final List<SoyNode.StandaloneNode> TemplateBlockForMsg() throws ParseException {
        ArrayList templateBlock = Lists.newArrayList();
        if (this.jj_2_31(Integer.MAX_VALUE)) {
            this.MaybeWhitespace("No message content is allowed before a 'plural' block.");
            MsgPluralNode msgPlural = this.MsgPlural();
            templateBlock.add(msgPlural);
            this.MaybeWhitespace("No message content is allowed after a 'plural' block.");
        } else if (this.jj_2_32(Integer.MAX_VALUE)) {
            this.MaybeWhitespace("No message content is allowed before a 'select' block.");
            MsgSelectNode msgSelect = this.MsgSelect();
            templateBlock.add(msgSelect);
            this.MaybeWhitespace("No message content is allowed after a 'select' block.");
        } else {
            block4: while (this.jj_2_29(Integer.MAX_VALUE)) {
                if (this.jj_2_30(Integer.MAX_VALUE)) {
                    RawTextNode contiguousRawTextAsNode = this.ContiguousRawTextAsNode();
                    if (contiguousRawTextAsNode == null) continue;
                    templateBlock.add(contiguousRawTextAsNode);
                    continue;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 8: 
                    case 9: {
                        SoyNode.StatementNode stmt = this.Stmt();
                        if (!(stmt instanceof SoyNode.MsgPlaceholderInitialNode)) {
                            String commandName = stmt instanceof IfNode ? "if" : ((SoyNode.CommandNode)((Object)stmt)).getCommandName();
                            throw SoySyntaxExceptionUtils.createWithNode("Command '" + commandName + "' not allowed within a 'msg' block.", stmt);
                        }
                        templateBlock.add(new MsgPlaceholderNode(this.nodeIdGen.genId(), (SoyNode.MsgPlaceholderInitialNode)((Object)stmt)));
                        continue block4;
                    }
                    case 79: {
                        MsgHtmlTagNode msgHtmlTag = this.MsgHtmlTag();
                        templateBlock.add(new MsgPlaceholderNode(this.nodeIdGen.genId(), msgHtmlTag));
                        continue block4;
                    }
                }
                this.jj_la1[41] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return templateBlock;
    }

    private final MsgPluralNode MsgPlural() throws ParseException {
        List<SoyNode.StandaloneNode> templateBlockForMsg;
        SourceItemInfo<String> cmdText = this.PluralTag();
        MsgPluralNode msgPluralNode = this.setSrcLoc(new MsgPluralNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
        this.MaybeWhitespace("No content allowed between 'plural' and 'case' (whitespace and comments are okay).");
        while (this.jj_2_33(Integer.MAX_VALUE)) {
            cmdText = this.CaseTag();
            MsgPluralCaseNode msgPluralCaseNode = this.setSrcLoc(new MsgPluralCaseNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
            msgPluralNode.addChild(msgPluralCaseNode);
            templateBlockForMsg = this.TemplateBlockForMsg();
            if (templateBlockForMsg.size() == 1 && (templateBlockForMsg.get(0) instanceof MsgPluralNode || templateBlockForMsg.get(0) instanceof MsgSelectNode)) {
                throw SoySyntaxExceptionUtils.createWithNode("Tags 'plural' and 'select' are not allowed inside 'plural' blocks.", templateBlockForMsg.get(0));
            }
            msgPluralCaseNode.addChildren(templateBlockForMsg);
        }
        SourceItemInfo<Void> defaultTagInfo = this.DefaultTag();
        MsgPluralDefaultNode msgPluralDefaultNode = this.setSrcLoc(new MsgPluralDefaultNode(this.nodeIdGen.genId()), defaultTagInfo.lineNum);
        msgPluralNode.addChild(msgPluralDefaultNode);
        templateBlockForMsg = this.TemplateBlockForMsg();
        if (templateBlockForMsg.size() == 1 && (templateBlockForMsg.get(0) instanceof MsgPluralNode || templateBlockForMsg.get(0) instanceof MsgSelectNode)) {
            throw SoySyntaxExceptionUtils.createWithNode("Tags 'plural' and 'select' are not allowed inside 'plural' blocks.", templateBlockForMsg.get(0));
        }
        msgPluralDefaultNode.addChildren(templateBlockForMsg);
        this.EndPluralTag();
        return msgPluralNode;
    }

    private final MsgSelectNode MsgSelect() throws ParseException {
        List<SoyNode.StandaloneNode> templateBlockForMsg;
        SourceItemInfo<String> cmdText = this.SelectTag();
        MsgSelectNode msgSelectNode = this.setSrcLoc(new MsgSelectNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
        this.MaybeWhitespace("No content allowed between 'select' and 'case' (whitespace and comments are okay).");
        while (this.jj_2_34(Integer.MAX_VALUE)) {
            cmdText = this.CaseTag();
            MsgSelectCaseNode msgSelectCaseNode = this.setSrcLoc(new MsgSelectCaseNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
            msgSelectNode.addChild(msgSelectCaseNode);
            templateBlockForMsg = this.TemplateBlockForMsg();
            msgSelectCaseNode.addChildren(templateBlockForMsg);
        }
        SourceItemInfo<Void> defaultTagInfo = this.DefaultTag();
        MsgSelectDefaultNode msgSelectDefaultNode = this.setSrcLoc(new MsgSelectDefaultNode(this.nodeIdGen.genId()), defaultTagInfo.lineNum);
        msgSelectNode.addChild(msgSelectDefaultNode);
        templateBlockForMsg = this.TemplateBlockForMsg();
        msgSelectDefaultNode.addChildren(templateBlockForMsg);
        this.EndSelectTag();
        return msgSelectNode;
    }

    private final MsgHtmlTagNode MsgHtmlTag() throws ParseException {
        Token htmlTagOpen = this.jj_consume_token(79);
        List<SoyNode.StandaloneNode> templateBlock = this.TemplateBlock();
        this.jj_consume_token(80);
        int lineNum = htmlTagOpen.beginLine;
        if (templateBlock.get(0) instanceof RawTextNode) {
            RawTextNode firstNode = (RawTextNode)templateBlock.get(0);
            RawTextNode newNode = new RawTextNode(this.nodeIdGen.genId(), "<" + firstNode.getRawText());
            templateBlock.set(0, this.setSrcLoc(newNode, lineNum));
        } else {
            templateBlock.add(0, this.setSrcLoc(new RawTextNode(this.nodeIdGen.genId(), "<"), lineNum));
        }
        int lastNodeIndex = templateBlock.size() - 1;
        if (templateBlock.get(lastNodeIndex) instanceof RawTextNode) {
            RawTextNode lastNode = (RawTextNode)templateBlock.get(lastNodeIndex);
            RawTextNode newNode = new RawTextNode(this.nodeIdGen.genId(), lastNode.getRawText() + ">");
            templateBlock.set(lastNodeIndex, this.setSrcLoc(newNode, lineNum));
        } else {
            templateBlock.add(this.setSrcLoc(new RawTextNode(this.nodeIdGen.genId(), ">"), lineNum));
        }
        return this.setSrcLoc(new MsgHtmlTagNode(this.nodeIdGen.genId(), templateBlock), lineNum);
    }

    private final PrintNode PrintStmt() throws ParseException {
        String userSuppliedPhName;
        List cmdTextParts;
        boolean isImplicit;
        SourceItemInfo<List<String>> printTagInfo = this.PrintTag();
        List printTagParts = (List)printTagInfo.parsedContent;
        int lineNum = printTagInfo.lineNum;
        if (!printTagParts.isEmpty() && ((String)printTagParts.get(0)).equals("print")) {
            isImplicit = false;
            cmdTextParts = printTagParts.subList(1, printTagParts.size());
        } else {
            isImplicit = true;
            cmdTextParts = printTagParts;
        }
        String cmdText = Joiner.on((String)"").join((Iterable)cmdTextParts);
        String phnameAttr = null;
        for (String cmdTextPart : cmdTextParts) {
            if (!cmdTextPart.startsWith(" phname=\"") || !cmdTextPart.endsWith("\"")) continue;
            if (phnameAttr != null) {
                throw SoySyntaxException.createWithMetaInfo("Found multiple 'phname' attributes in 'print' command text \"" + cmdText + "\".", this.createSrcLoc(lineNum), null, null);
            }
            phnameAttr = cmdTextPart;
        }
        if (phnameAttr != null) {
            cmdTextParts.remove(phnameAttr);
            userSuppliedPhName = phnameAttr.substring(9, phnameAttr.length() - 1);
        } else {
            userSuppliedPhName = null;
        }
        if (cmdTextParts.isEmpty()) {
            throw SoySyntaxException.createWithMetaInfo("Found 'print' command with empty command text.", this.createSrcLoc(lineNum), null, null);
        }
        String exprText = ((String)cmdTextParts.get(0)).trim();
        PrintNode printNode = this.setSrcLoc(new PrintNode(this.nodeIdGen.genId(), isImplicit, exprText, userSuppliedPhName), lineNum);
        String directiveName = null;
        int n = cmdTextParts.size();
        for (int i = 1; i < n; ++i) {
            String cmdTextPart = (String)cmdTextParts.get(i);
            if (cmdTextPart.startsWith("|")) {
                if (directiveName != null) {
                    printNode.addChild(this.setSrcLoc(new PrintDirectiveNode(this.nodeIdGen.genId(), directiveName, ""), lineNum));
                }
                directiveName = cmdTextPart;
                continue;
            }
            if (cmdTextPart.startsWith(":")) {
                if (directiveName == null) {
                    throw new AssertionError();
                }
                String argsText = cmdTextPart.substring(1);
                printNode.addChild(this.setSrcLoc(new PrintDirectiveNode(this.nodeIdGen.genId(), directiveName, argsText), lineNum));
                directiveName = null;
                continue;
            }
            if (cmdTextPart.trim().length() == 0) continue;
            throw SoySyntaxExceptionUtils.createWithNode("Invalid 'print' command text \"" + cmdText + "\" (check the directives).", printNode);
        }
        if (directiveName != null) {
            printNode.addChild(this.setSrcLoc(new PrintDirectiveNode(this.nodeIdGen.genId(), directiveName, ""), lineNum));
        }
        return printNode;
    }

    private final void PrintStmtLookaheadHelper() throws ParseException {
        this.SoyTagOpen();
        if (this.jj_2_35(Integer.MAX_VALUE)) {
            this.jj_consume_token(30);
            this.jj_consume_token(64);
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 25: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    this.AnyCmdName();
                    this.jj_consume_token(65);
                    break;
                }
                case 69: {
                    this.jj_consume_token(69);
                    break;
                }
                case 70: {
                    this.jj_consume_token(70);
                    break;
                }
                default: {
                    this.jj_la1[42] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
    }

    private final XidNode XidStmt() throws ParseException {
        SourceItemInfo<String> cmdText = this.XidTag();
        return this.setSrcLoc(new XidNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
    }

    private final CssNode CssStmt() throws ParseException {
        SourceItemInfo<String> cmdText = this.CssTag();
        return this.setSrcLoc(new CssNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
    }

    private final LetNode LetStmt() throws ParseException {
        LetNode letNode;
        if (this.jj_2_36(Integer.MAX_VALUE)) {
            SourceItemInfo<String> cmdText = this.LetTagSelfEnding();
            letNode = this.setSrcLoc(new LetValueNode(this.nodeIdGen.genId(), false, (String)cmdText.parsedContent), cmdText.lineNum);
        } else if (this.jj_2_37(Integer.MAX_VALUE)) {
            SourceItemInfo<String> cmdText = this.LetTagNotSelfEnding();
            LetContentNode letContentNode = this.setSrcLoc(new LetContentNode(this.nodeIdGen.genId(), false, (String)cmdText.parsedContent), cmdText.lineNum);
            List<SoyNode.StandaloneNode> templateBlock = this.TemplateBlock();
            letContentNode.addChildren((List<? extends SoyNode.StandaloneNode>)templateBlock);
            letNode = letContentNode;
            this.EndLetTag();
        } else {
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        return letNode;
    }

    private final IfNode IfStmt() throws ParseException {
        SourceItemInfo<String> cmdText = this.IfTag();
        IfNode ifNode = this.setSrcLoc(new IfNode(this.nodeIdGen.genId()), cmdText.lineNum);
        IfCondNode ifCondNode = this.setSrcLoc(new IfCondNode(this.nodeIdGen.genId(), "if", (String)cmdText.parsedContent), cmdText.lineNum);
        ifNode.addChild(ifCondNode);
        List<SoyNode.StandaloneNode> templateBlock = this.TemplateBlock();
        ifCondNode.addChildren(templateBlock);
        while (this.jj_2_38(Integer.MAX_VALUE)) {
            cmdText = this.ElseifTag();
            ifCondNode = this.setSrcLoc(new IfCondNode(this.nodeIdGen.genId(), "elseif", (String)cmdText.parsedContent), cmdText.lineNum);
            ifNode.addChild(ifCondNode);
            templateBlock = this.TemplateBlock();
            ifCondNode.addChildren(templateBlock);
        }
        if (this.jj_2_39(Integer.MAX_VALUE)) {
            SourceItemInfo<Void> elseTagInfo = this.ElseTag();
            IfElseNode ifElseNode = this.setSrcLoc(new IfElseNode(this.nodeIdGen.genId()), elseTagInfo.lineNum);
            ifNode.addChild(ifElseNode);
            templateBlock = this.TemplateBlock();
            ifElseNode.addChildren(templateBlock);
        }
        this.EndIfTag();
        return ifNode;
    }

    private final SwitchNode SwitchStmt() throws ParseException {
        List<SoyNode.StandaloneNode> templateBlock;
        SourceItemInfo<String> cmdText = this.SwitchTag();
        SwitchNode switchNode = this.setSrcLoc(new SwitchNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
        this.MaybeWhitespace("No content allowed between 'switch' and 'case' (whitespace and comments are okay).");
        while (this.jj_2_40(Integer.MAX_VALUE)) {
            cmdText = this.CaseTag();
            SwitchCaseNode switchCaseNode = this.setSrcLoc(new SwitchCaseNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
            switchNode.addChild(switchCaseNode);
            templateBlock = this.TemplateBlock();
            switchCaseNode.addChildren(templateBlock);
        }
        if (this.jj_2_41(Integer.MAX_VALUE)) {
            SourceItemInfo<Void> defaultTagInfo = this.DefaultTag();
            SwitchDefaultNode switchDefaultNode = this.setSrcLoc(new SwitchDefaultNode(this.nodeIdGen.genId()), defaultTagInfo.lineNum);
            switchNode.addChild(switchDefaultNode);
            templateBlock = this.TemplateBlock();
            switchDefaultNode.addChildren(templateBlock);
        }
        this.EndSwitchTag();
        return switchNode;
    }

    private final ForeachNode ForeachStmt() throws ParseException {
        SourceItemInfo<String> cmdText = this.ForeachTag();
        ForeachNode foreachNode = this.setSrcLoc(new ForeachNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
        int lineNum = this.getToken((int)0).beginLine;
        List<SoyNode.StandaloneNode> templateBlock = this.TemplateBlock();
        ForeachNonemptyNode foreachNonemptyNode = this.setSrcLoc(new ForeachNonemptyNode(this.nodeIdGen.genId()), lineNum);
        foreachNode.addChild(foreachNonemptyNode);
        foreachNonemptyNode.addChildren(templateBlock);
        if (this.jj_2_42(Integer.MAX_VALUE)) {
            SourceItemInfo<Void> ifemptyTagInfo = this.IfemptyTag();
            templateBlock = this.TemplateBlock();
            ForeachIfemptyNode foreachIfemptyNode = this.setSrcLoc(new ForeachIfemptyNode(this.nodeIdGen.genId()), ifemptyTagInfo.lineNum);
            foreachNode.addChild(foreachIfemptyNode);
            foreachIfemptyNode.addChildren(templateBlock);
        }
        this.EndForeachTag();
        return foreachNode;
    }

    private final ForNode ForStmt() throws ParseException {
        SourceItemInfo<String> cmdText = this.ForTag();
        ForNode forNode = this.setSrcLoc(new ForNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
        List<SoyNode.StandaloneNode> templateBlock = this.TemplateBlock();
        forNode.addChildren(templateBlock);
        this.EndForTag();
        return forNode;
    }

    private final CallNode CallStmt() throws ParseException {
        Boolean isBasicEndCallTag;
        SourceItemInfo<List<String>> callTagInfo;
        ArrayList callParams = Lists.newArrayList();
        if (this.jj_2_44(Integer.MAX_VALUE)) {
            callTagInfo = this.AnyCallTagSelfEnding();
            isBasicEndCallTag = null;
        } else if (this.jj_2_45(Integer.MAX_VALUE)) {
            callTagInfo = this.AnyCallTagNotSelfEnding();
            this.MaybeWhitespace("No content allowed between 'call' and 'param' (whitespace and comments are okay).");
            while (this.jj_2_43(Integer.MAX_VALUE)) {
                CallParamNode callParam = this.CallParam();
                callParams.add(callParam);
                this.MaybeWhitespace("No content allowed between 'param' and 'param' (whitespace and comments are okay).");
            }
            isBasicEndCallTag = this.EndAnyCallTag();
        } else {
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        List callTagParts = (List)callTagInfo.parsedContent;
        String cmdName = (String)callTagParts.get(0);
        StringBuilder cmdTextSb = new StringBuilder();
        String phnameAttr = null;
        for (int i = 1; i < callTagParts.size(); ++i) {
            String cmdTextPart = (String)callTagParts.get(i);
            if (cmdTextPart.startsWith(" phname=\"") && cmdTextPart.endsWith("\"")) {
                if (phnameAttr != null) {
                    throw SoySyntaxException.createWithMetaInfo("Found multiple 'phname' attributes in '" + cmdName + "' command text \"" + Joiner.on((String)"").join(callTagParts.subList(1, callTagParts.size())) + "\".", this.createSrcLoc(callTagInfo.lineNum), null, null);
                }
                phnameAttr = cmdTextPart;
                continue;
            }
            cmdTextSb.append(cmdTextPart);
        }
        String cmdText = cmdTextSb.toString();
        boolean isBasicCallTag = ((String)callTagParts.get(0)).equals("call");
        if (isBasicEndCallTag != null && isBasicCallTag != isBasicEndCallTag) {
            if (isBasicCallTag) {
                throw new ParseException("Mismatched 'call' and '/delcall'.");
            }
            throw new ParseException("Mismatched 'delcall' and '/call'.");
        }
        String userSuppliedPhName = phnameAttr != null ? phnameAttr.substring(9, phnameAttr.length() - 1) : null;
        CallNode callNode = isBasicCallTag ? new CallBasicNode(this.nodeIdGen.genId(), cmdText, userSuppliedPhName) : new CallDelegateNode(this.nodeIdGen.genId(), cmdText, userSuppliedPhName);
        callNode = this.setSrcLoc(callNode, callTagInfo.lineNum);
        callNode.addChildren(callParams);
        return callNode;
    }

    private final CallParamNode CallParam() throws ParseException {
        CallParamNode callParamNode;
        if (this.jj_2_46(Integer.MAX_VALUE)) {
            SourceItemInfo<String> cmdText = this.ParamTagSelfEnding();
            callParamNode = this.setSrcLoc(new CallParamValueNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
        } else if (this.jj_2_47(Integer.MAX_VALUE)) {
            SourceItemInfo<String> cmdText = this.ParamTagNotSelfEnding();
            CallParamContentNode cpcn = this.setSrcLoc(new CallParamContentNode(this.nodeIdGen.genId(), (String)cmdText.parsedContent), cmdText.lineNum);
            List<SoyNode.StandaloneNode> templateBlock = this.TemplateBlock();
            cpcn.addChildren((List<? extends SoyNode.StandaloneNode>)templateBlock);
            callParamNode = cpcn;
            this.EndParamTag();
        } else {
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        return callParamNode;
    }

    private final LogNode LogStmt() throws ParseException {
        SourceItemInfo<Void> logTagInfo = this.LogTag();
        LogNode logNode = this.setSrcLoc(new LogNode(this.nodeIdGen.genId()), logTagInfo.lineNum);
        List<SoyNode.StandaloneNode> templateBlock = this.TemplateBlock();
        logNode.addChildren(templateBlock);
        this.EndLogTag();
        return logNode;
    }

    private final DebuggerNode DebuggerStmt() throws ParseException {
        SourceItemInfo<Void> debuggerTagInfo = this.DebuggerTag();
        return this.setSrcLoc(new DebuggerNode(this.nodeIdGen.genId()), debuggerTagInfo.lineNum);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_1();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(0, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_2();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(1, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_3(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_3();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(2, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_4(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_4();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(3, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_5(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_5();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(4, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_6(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_6();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(5, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_7(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_7();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(6, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_8(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_8();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(7, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_9(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_9();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(8, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_10(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_10();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(9, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_11(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_11();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(10, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_12(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_12();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(11, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_13(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_13();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(12, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_14(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_14();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(13, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_15(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_15();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(14, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_16(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_16();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(15, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_17(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_17();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(16, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_18(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_18();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(17, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_19(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_19();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(18, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_20(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_20();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(19, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_21(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_21();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(20, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_22(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_22();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(21, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_23(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_23();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(22, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_24(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_24();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(23, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_25(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_25();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(24, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_26(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_26();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(25, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_27(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_27();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(26, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_28(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_28();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(27, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_29(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_29();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(28, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_30(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_30();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(29, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_31(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_31();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(30, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_32(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_32();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(31, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_33(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_33();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(32, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_34(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_34();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(33, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_35(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_35();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(34, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_36(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_36();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(35, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_37(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_37();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(36, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_38(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_38();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(37, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_39(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_39();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(38, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_40(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_40();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(39, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_41(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_41();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(40, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_42(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_42();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(41, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_43(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_43();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(42, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_44(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_44();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(43, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_45(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_45();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(44, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_46(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_46();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(45, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_47(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_47();
            return bl;
        }
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(46, xla);
        }
    }

    private boolean jj_3R_56() {
        if (this.jj_3R_18()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(23)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(31)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(32)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(33)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(35)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(39)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(41)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(44)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(46)) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_scan_token(50)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(52)) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(64)) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_64()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_54() {
        return this.jj_3R_53();
    }

    private boolean jj_3R_55() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_42() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_55()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_56()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_24() {
        if (this.jj_3R_18()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_43()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_44()) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(69)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(70)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3_2() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3R_50() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(85)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(86)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(87)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3_26() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(52)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3_25() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(50)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3_47() {
        return this.jj_3R_33();
    }

    private boolean jj_3_3() {
        return this.jj_3R_20();
    }

    private boolean jj_3_24() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(46)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_41() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(54)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3R_41() {
        if (this.jj_scan_token(6)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_54()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(12)) {
            return true;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(97)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_23() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(44)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_57() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(14)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(15)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(16)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(17)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(18)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(19)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(20)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(21)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(22)) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_scan_token(23)) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_scan_token(25)) {
                                                    this.jj_scanpos = xsp;
                                                    if (this.jj_scan_token(30)) {
                                                        this.jj_scanpos = xsp;
                                                        if (this.jj_scan_token(31)) {
                                                            this.jj_scanpos = xsp;
                                                            if (this.jj_scan_token(32)) {
                                                                this.jj_scanpos = xsp;
                                                                if (this.jj_scan_token(33)) {
                                                                    this.jj_scanpos = xsp;
                                                                    if (this.jj_scan_token(34)) {
                                                                        this.jj_scanpos = xsp;
                                                                        if (this.jj_scan_token(35)) {
                                                                            this.jj_scanpos = xsp;
                                                                            if (this.jj_scan_token(36)) {
                                                                                this.jj_scanpos = xsp;
                                                                                if (this.jj_scan_token(37)) {
                                                                                    this.jj_scanpos = xsp;
                                                                                    if (this.jj_scan_token(38)) {
                                                                                        this.jj_scanpos = xsp;
                                                                                        if (this.jj_scan_token(39)) {
                                                                                            this.jj_scanpos = xsp;
                                                                                            if (this.jj_scan_token(40)) {
                                                                                                this.jj_scanpos = xsp;
                                                                                                if (this.jj_scan_token(41)) {
                                                                                                    this.jj_scanpos = xsp;
                                                                                                    if (this.jj_scan_token(42)) {
                                                                                                        this.jj_scanpos = xsp;
                                                                                                        if (this.jj_scan_token(43)) {
                                                                                                            this.jj_scanpos = xsp;
                                                                                                            if (this.jj_scan_token(44)) {
                                                                                                                this.jj_scanpos = xsp;
                                                                                                                if (this.jj_scan_token(45)) {
                                                                                                                    this.jj_scanpos = xsp;
                                                                                                                    if (this.jj_scan_token(46)) {
                                                                                                                        this.jj_scanpos = xsp;
                                                                                                                        if (this.jj_scan_token(47)) {
                                                                                                                            this.jj_scanpos = xsp;
                                                                                                                            if (this.jj_scan_token(48)) {
                                                                                                                                this.jj_scanpos = xsp;
                                                                                                                                if (this.jj_scan_token(49)) {
                                                                                                                                    this.jj_scanpos = xsp;
                                                                                                                                    if (this.jj_scan_token(50)) {
                                                                                                                                        this.jj_scanpos = xsp;
                                                                                                                                        if (this.jj_scan_token(51)) {
                                                                                                                                            this.jj_scanpos = xsp;
                                                                                                                                            if (this.jj_scan_token(52)) {
                                                                                                                                                this.jj_scanpos = xsp;
                                                                                                                                                if (this.jj_scan_token(53)) {
                                                                                                                                                    this.jj_scanpos = xsp;
                                                                                                                                                    if (this.jj_scan_token(54)) {
                                                                                                                                                        this.jj_scanpos = xsp;
                                                                                                                                                        if (this.jj_scan_token(55)) {
                                                                                                                                                            this.jj_scanpos = xsp;
                                                                                                                                                            if (this.jj_scan_token(56)) {
                                                                                                                                                                this.jj_scanpos = xsp;
                                                                                                                                                                if (this.jj_scan_token(57)) {
                                                                                                                                                                    return true;
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_40() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(12)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(97)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_46() {
        return this.jj_3R_32();
    }

    private boolean jj_3_22() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(41)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_39() {
        return this.jj_3R_53();
    }

    private boolean jj_3R_21() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_39()) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_40()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_41()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_21() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(39)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_33() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(53)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_33() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(48)) {
            return true;
        }
        if (this.jj_scan_token(64)) {
            return true;
        }
        if (this.jj_3R_46()) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3_20() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(35)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_40() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(53)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_19() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(33)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_18() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(32)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_26() {
        return this.jj_3R_42();
    }

    private boolean jj_3_17() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(31)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_7() {
        return this.jj_3R_21();
    }

    private boolean jj_3_16() {
        return this.jj_3R_24();
    }

    private boolean jj_3_15() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(23)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_32() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(48)) {
            return true;
        }
        if (this.jj_scan_token(64)) {
            return true;
        }
        if (this.jj_3R_46()) {
            return true;
        }
        return this.jj_3R_47();
    }

    private boolean jj_3R_51() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(6)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(7)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_23() {
        return this.jj_3R_42();
    }

    private boolean jj_3_1() {
        return this.jj_3R_17();
    }

    private boolean jj_3R_52() {
        if (this.jj_3R_18()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(14)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(15)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(16)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(17)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(18)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(19)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(20)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return this.jj_3R_19();
    }

    private boolean jj_3_39() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(37)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3_30() {
        return this.jj_3R_20();
    }

    private boolean jj_3R_63() {
        return this.jj_3R_49();
    }

    private boolean jj_3R_68() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(66)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(67)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_29() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_25()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_26()) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(79)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3R_25() {
        return this.jj_3R_20();
    }

    private boolean jj_3R_62() {
        return this.jj_3R_70();
    }

    private boolean jj_3R_67() {
        return this.jj_scan_token(70);
    }

    private boolean jj_3R_53() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_62()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_63()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_66() {
        return this.jj_scan_token(69);
    }

    private boolean jj_3_38() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(36)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_60() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_66()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_67()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_68()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3_32() {
        if (this.jj_3R_27()) {
            return true;
        }
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(28)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_6() {
        return this.jj_3R_21();
    }

    private boolean jj_3R_31() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(46)) {
            return true;
        }
        if (this.jj_scan_token(64)) {
            return true;
        }
        if (this.jj_3R_48()) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3R_59() {
        if (this.jj_3R_57()) {
            return true;
        }
        return this.jj_scan_token(65);
    }

    private boolean jj_3R_34() {
        return this.jj_3R_49();
    }

    private boolean jj_3R_17() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_34()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(85);
    }

    private boolean jj_3_14() {
        return this.jj_3R_20();
    }

    private boolean jj_3_31() {
        if (this.jj_3R_27()) {
            return true;
        }
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(26)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_48() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_59()) {
            this.jj_scanpos = xsp;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_60());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_13() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_22()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_23()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_22() {
        return this.jj_3R_20();
    }

    private boolean jj_3_43() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(48)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_45() {
        return this.jj_3R_58();
    }

    private boolean jj_3R_76() {
        return this.jj_3R_49();
    }

    private boolean jj_3R_27() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_45()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_75() {
        return this.jj_scan_token(85);
    }

    private boolean jj_3R_74() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_75()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_76()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_45() {
        return this.jj_3R_31();
    }

    private boolean jj_3R_30() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(46)) {
            return true;
        }
        if (this.jj_scan_token(64)) {
            return true;
        }
        if (this.jj_3R_48()) {
            return true;
        }
        return this.jj_3R_47();
    }

    private boolean jj_3_44() {
        return this.jj_3R_30();
    }

    private boolean jj_3R_46() {
        return this.jj_3R_48();
    }

    private boolean jj_3_37() {
        return this.jj_3R_29();
    }

    private boolean jj_3R_73() {
        return this.jj_3R_49();
    }

    private boolean jj_3_12() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(12)) {
            return true;
        }
        return this.jj_scan_token(97);
    }

    private boolean jj_3R_29() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(33)) {
            return true;
        }
        if (this.jj_scan_token(64)) {
            return true;
        }
        if (this.jj_3R_46()) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3_11() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(12)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_70() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_73()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(85)) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_74());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_36() {
        return this.jj_3R_28();
    }

    private boolean jj_3R_47() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(61)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(63)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(72)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(74)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3_28() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(24)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_28() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(33)) {
            return true;
        }
        if (this.jj_scan_token(64)) {
            return true;
        }
        if (this.jj_3R_46()) {
            return true;
        }
        return this.jj_3R_47();
    }

    private boolean jj_3R_69() {
        return this.jj_scan_token(86);
    }

    private boolean jj_3R_19() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(60)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(62)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(71)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(73)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_61() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_69()) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(7)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_49() {
        Token xsp;
        if (this.jj_3R_61()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_61());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_10() {
        Token xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_scan_token(86));
        this.jj_scanpos = xsp;
        return this.jj_scan_token(6);
    }

    private boolean jj_3_34() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(53)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_36() {
        return this.jj_3R_51();
    }

    private boolean jj_3R_72() {
        return this.jj_3R_51();
    }

    private boolean jj_3_9() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(12)) {
            return true;
        }
        return this.jj_scan_token(97);
    }

    private boolean jj_3R_18() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(8)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(9)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_71() {
        return this.jj_3R_50();
    }

    private boolean jj_3_8() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(12)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_65() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_71()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_72()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_64() {
        return this.jj_3R_19();
    }

    private boolean jj_3_4() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3_42() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(42)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3R_58() {
        Token xsp;
        if (this.jj_3R_65()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_65());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_38() {
        return this.jj_3R_52();
    }

    private boolean jj_3_35() {
        if (this.jj_scan_token(30)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_37() {
        if (this.jj_3R_18()) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        return this.jj_3R_19();
    }

    private boolean jj_3R_20() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_35()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_36()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_37()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_38()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_35() {
        return this.jj_3R_50();
    }

    private boolean jj_3_27() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_44() {
        if (this.jj_3R_57()) {
            return true;
        }
        return this.jj_scan_token(65);
    }

    private boolean jj_3_5() {
        if (this.jj_scan_token(30)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_43() {
        if (this.jj_scan_token(30)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{768, 0, 0, -1023426560, 0, 0, 0, -1023426560, 128, 128, 128, 128, 128, 128, 128, 192, 192, 192, 0, 192, 192, 192, 2080768, 192, 768, 192, 768, 192, 128, 128, 0, 128, 0, 832, 832, 0, 128, 768, -2139095040, 0, 768, 768, -1023426560};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0x50000000, -1610612736, 0x3FFFFFF, 0, 0, 0, 0x3FFFFFF, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1331851, 0x50000000, 0, 0, 0x3FFFFFF};
    }

    private static void jj_la1_init_2() {
        jj_la1_2 = new int[]{0, 640, 1280, 0, 108, 12, 108, 0, 0x400000, 0x400000, 0x400000, 0x600000, 0x600000, 0x400000, 0x400000, 0, 0x400000, 0x400000, 0xE00000, 0xE00000, 0xE00000, 0xE00000, 0, 0xE00000, 0, 0xE00000, 0, 0x400000, 0x600000, 0x600000, 0, 0x600000, 0, 0, 0, 0x400000, 0x600000, 0, 0, 641, 0, 32768, 96};
    }

    private static void jj_la1_init_3() {
        jj_la1_3 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public TemplateParser(InputStream stream) {
        this(stream, null);
    }

    public TemplateParser(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new TemplateParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 43; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 43; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public TemplateParser(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new TemplateParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 43; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(Reader stream) {
        int i;
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 43; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public TemplateParser(TemplateParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 43; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(TemplateParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 43; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    JJCalls c = this.jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                        c = c.next;
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    private boolean jj_scan_token(int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
                this.jj_lastpos = this.jj_scanpos.next;
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok = this.token;
            while (tok != null && tok != this.jj_scanpos) {
                ++i;
                tok = tok.next;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
            block1: for (int[] oldentry : this.jj_expentries) {
                if (oldentry.length != this.jj_expentry.length) continue;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] != this.jj_expentry[i]) continue block1;
                }
                this.jj_expentries.add(this.jj_expentry);
                break;
            }
            if (pos != 0) {
                this.jj_endpos = pos;
                this.jj_lasttokens[this.jj_endpos - 1] = kind;
            }
        }
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[99];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 43; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) != 0) {
                    la1tokens[32 + j] = true;
                }
                if ((jj_la1_2[i] & 1 << j) != 0) {
                    la1tokens[64 + j] = true;
                }
                if ((jj_la1_3[i] & 1 << j) == 0) continue;
                la1tokens[96 + j] = true;
            }
        }
        for (i = 0; i < 99; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 47; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen <= this.jj_gen) continue;
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
                            break;
                        }
                        case 1: {
                            this.jj_3_2();
                            break;
                        }
                        case 2: {
                            this.jj_3_3();
                            break;
                        }
                        case 3: {
                            this.jj_3_4();
                            break;
                        }
                        case 4: {
                            this.jj_3_5();
                            break;
                        }
                        case 5: {
                            this.jj_3_6();
                            break;
                        }
                        case 6: {
                            this.jj_3_7();
                            break;
                        }
                        case 7: {
                            this.jj_3_8();
                            break;
                        }
                        case 8: {
                            this.jj_3_9();
                            break;
                        }
                        case 9: {
                            this.jj_3_10();
                            break;
                        }
                        case 10: {
                            this.jj_3_11();
                            break;
                        }
                        case 11: {
                            this.jj_3_12();
                            break;
                        }
                        case 12: {
                            this.jj_3_13();
                            break;
                        }
                        case 13: {
                            this.jj_3_14();
                            break;
                        }
                        case 14: {
                            this.jj_3_15();
                            break;
                        }
                        case 15: {
                            this.jj_3_16();
                            break;
                        }
                        case 16: {
                            this.jj_3_17();
                            break;
                        }
                        case 17: {
                            this.jj_3_18();
                            break;
                        }
                        case 18: {
                            this.jj_3_19();
                            break;
                        }
                        case 19: {
                            this.jj_3_20();
                            break;
                        }
                        case 20: {
                            this.jj_3_21();
                            break;
                        }
                        case 21: {
                            this.jj_3_22();
                            break;
                        }
                        case 22: {
                            this.jj_3_23();
                            break;
                        }
                        case 23: {
                            this.jj_3_24();
                            break;
                        }
                        case 24: {
                            this.jj_3_25();
                            break;
                        }
                        case 25: {
                            this.jj_3_26();
                            break;
                        }
                        case 26: {
                            this.jj_3_27();
                            break;
                        }
                        case 27: {
                            this.jj_3_28();
                            break;
                        }
                        case 28: {
                            this.jj_3_29();
                            break;
                        }
                        case 29: {
                            this.jj_3_30();
                            break;
                        }
                        case 30: {
                            this.jj_3_31();
                            break;
                        }
                        case 31: {
                            this.jj_3_32();
                            break;
                        }
                        case 32: {
                            this.jj_3_33();
                            break;
                        }
                        case 33: {
                            this.jj_3_34();
                            break;
                        }
                        case 34: {
                            this.jj_3_35();
                            break;
                        }
                        case 35: {
                            this.jj_3_36();
                            break;
                        }
                        case 36: {
                            this.jj_3_37();
                            break;
                        }
                        case 37: {
                            this.jj_3_38();
                            break;
                        }
                        case 38: {
                            this.jj_3_39();
                            break;
                        }
                        case 39: {
                            this.jj_3_40();
                            break;
                        }
                        case 40: {
                            this.jj_3_41();
                            break;
                        }
                        case 41: {
                            this.jj_3_42();
                            break;
                        }
                        case 42: {
                            this.jj_3_43();
                            break;
                        }
                        case 43: {
                            this.jj_3_44();
                            break;
                        }
                        case 44: {
                            this.jj_3_45();
                            break;
                        }
                        case 45: {
                            this.jj_3_46();
                            break;
                        }
                        case 46: {
                            this.jj_3_47();
                        }
                    }
                } while ((p = p.next) != null);
                continue;
            }
            catch (StackTracelessAtlassianLookaheadSuccess stackTracelessAtlassianLookaheadSuccess) {
                // empty catch block
            }
        }
        this.jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        JJCalls p = this.jj_2_rtns[index];
        while (p.gen > this.jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    static {
        TemplateParser.jj_la1_init_0();
        TemplateParser.jj_la1_init_1();
        TemplateParser.jj_la1_init_2();
        TemplateParser.jj_la1_init_3();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }

    private static final class StackTracelessAtlassianLookaheadSuccess
    extends Error {
        private StackTracelessAtlassianLookaheadSuccess() {
        }
    }

    static final class SourceItemInfo<T> {
        final T parsedContent;
        final int lineNum;

        SourceItemInfo(T parsedContent, int lineNum) {
            this.parsedContent = parsedContent;
            this.lineNum = lineNum;
        }
    }
}

