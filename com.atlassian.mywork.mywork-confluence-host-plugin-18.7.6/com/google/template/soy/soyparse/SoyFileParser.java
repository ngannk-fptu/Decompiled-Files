/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.soyparse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.base.internal.IncrementingIdGenerator;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soyparse.ParseException;
import com.google.template.soy.soyparse.SimpleCharStream;
import com.google.template.soy.soyparse.SoyFileParserConstants;
import com.google.template.soy.soyparse.SoyFileParserTokenManager;
import com.google.template.soy.soyparse.TemplateParser;
import com.google.template.soy.soyparse.Token;
import com.google.template.soy.soyparse.TokenMgrError;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateBasicNodeBuilder;
import com.google.template.soy.soytree.TemplateDelegateNodeBuilder;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateNodeBuilder;
import com.google.template.soy.types.SoyTypeRegistry;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoyFileParser
implements SoyFileParserConstants {
    private static final Pattern ERROR_MSG_LINE_NUM_PAT = Pattern.compile("(?<=line )\\d+(?=, column )");
    private SoyTypeRegistry typeRegistry;
    private IdGenerator nodeIdGen;
    private SoyFileKind soyFileKind;
    private String filePath = SourceLocation.UNKNOWN.getFilePath();
    public SoyFileParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[4];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public SoyFileParser(SoyTypeRegistry typeRegistry, IdGenerator nodeIdGen, Reader input, SoyFileKind soyFileKind, String filePath) {
        this(input);
        Preconditions.checkNotNull((Object)typeRegistry);
        Preconditions.checkNotNull((Object)nodeIdGen);
        this.typeRegistry = typeRegistry;
        this.nodeIdGen = nodeIdGen;
        this.soyFileKind = soyFileKind;
        this.filePath = filePath;
    }

    public SoyFileParser(SoyTypeRegistry typeRegistry, IdGenerator nodeIdGen, String input, SoyFileKind soyFileKind, String filePath) {
        this(typeRegistry, nodeIdGen, new StringReader(input), soyFileKind, filePath);
    }

    public static void main(String[] args) throws SoySyntaxException, TokenMgrError, ParseException {
        InputStreamReader stdinReader;
        try {
            stdinReader = new InputStreamReader(System.in, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
        new SoyFileParser(new SoyTypeRegistry(), (IdGenerator)new IncrementingIdGenerator(), stdinReader, SoyFileKind.SRC, "stdin").SoyFile();
        System.out.println("Valid input.");
    }

    public SoyFileNode parseSoyFile() throws SoySyntaxException, TokenMgrError, ParseException {
        Preconditions.checkNotNull((Object)this.typeRegistry);
        Preconditions.checkNotNull((Object)this.nodeIdGen);
        return this.SoyFile();
    }

    @VisibleForTesting
    static String adjustLineNumInErrorMsg(String errorMsg, int templateTagLineNum) {
        Matcher matcher = ERROR_MSG_LINE_NUM_PAT.matcher(errorMsg);
        if (!matcher.find()) {
            return errorMsg;
        }
        int newLineNum = templateTagLineNum + Integer.parseInt(matcher.group());
        return errorMsg.substring(0, matcher.start()) + newLineNum + errorMsg.substring(matcher.end());
    }

    private final SoyFileNode SoyFile() throws ParseException {
        int end;
        int start;
        String delpackageCmdText = null;
        String namespaceCmdText = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                Token delpackageTag = this.jj_consume_token(1);
                start = "{delpackage".length();
                end = delpackageTag.image.length() - 1;
                delpackageCmdText = delpackageTag.image.substring(start, end).trim();
                break;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 5: {
                Token namespaceTag = this.jj_consume_token(5);
                start = "{namespace".length();
                end = namespaceTag.image.length() - 1;
                namespaceCmdText = namespaceTag.image.substring(start, end).trim();
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
            }
        }
        ArrayList aliasCmdTexts = null;
        block12: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    break block12;
                }
            }
            Token aliasTag = this.jj_consume_token(9);
            int start2 = "{alias".length();
            int end2 = aliasTag.image.length() - 1;
            if (aliasCmdTexts == null) {
                aliasCmdTexts = Lists.newArrayList();
            }
            aliasCmdTexts.add(aliasTag.image.substring(start2, end2).trim());
        }
        SoyFileNode soyFileNode = new SoyFileNode(this.nodeIdGen.genId(), this.soyFileKind, delpackageCmdText, namespaceCmdText, aliasCmdTexts);
        soyFileNode.setFilePath(this.filePath);
        TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo = new TemplateNode.SoyFileHeaderInfo(soyFileNode);
        block13: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 19: {
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    break block13;
                }
            }
            TemplateNode template = this.Template(soyFileHeaderInfo);
            soyFileNode.addChild(template);
        }
        this.jj_consume_token(0);
        return soyFileNode;
    }

    private final TemplateNode Template(TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo) throws ParseException {
        List templateBodyNodes;
        List templateHeaderDecls;
        String soyDoc;
        Token templateTag = this.jj_consume_token(19);
        boolean isBasicTemplate = templateTag.image.startsWith("{template");
        TemplateNodeBuilder templateNodeBuilder = isBasicTemplate ? new TemplateBasicNodeBuilder(soyFileHeaderInfo, this.typeRegistry) : new TemplateDelegateNodeBuilder(soyFileHeaderInfo, this.typeRegistry);
        templateNodeBuilder.setId(this.nodeIdGen.genId());
        String cmdText = templateTag.image.substring(isBasicTemplate ? 10 : 13, templateTag.image.length() - 1).trim();
        templateNodeBuilder.setCmdText(cmdText);
        Token soyDocEnd = templateTag.specialToken;
        if (soyDocEnd != null && soyDocEnd.kind == 16) {
            ArrayList soyDocParts = Lists.newArrayList();
            soyDocParts.add(0, "*/");
            Token st = soyDocEnd.specialToken;
            while (st.kind != 13) {
                soyDocParts.add(0, st.image);
                st = st.specialToken;
            }
            soyDocParts.add(0, "/**");
            soyDoc = Joiner.on((String)"").join((Iterable)soyDocParts);
        } else {
            soyDoc = null;
        }
        templateNodeBuilder.setSoyDoc(soyDoc);
        Token templateContent = this.jj_consume_token(21);
        try {
            Pair<List<TemplateNodeBuilder.DeclInfo>, List<SoyNode.StandaloneNode>> templateHeaderAndBody = new TemplateParser(this.nodeIdGen, templateContent.image, this.filePath, templateContent.beginLine).parseTemplateContent();
            templateHeaderDecls = (List)templateHeaderAndBody.first;
            templateBodyNodes = (List)templateHeaderAndBody.second;
        }
        catch (TokenMgrError tme) {
            String adjustedErrorMsg = SoyFileParser.adjustLineNumInErrorMsg(tme.getMessage(), templateTag.beginLine);
            throw SoySyntaxException.createCausedWithMetaInfo(adjustedErrorMsg, tme, null, this.filePath, templateNodeBuilder.getTemplateNameForUserMsgs());
        }
        catch (ParseException pe) {
            String adjustedErrorMsg = SoyFileParser.adjustLineNumInErrorMsg(pe.getMessage(), templateTag.beginLine);
            throw SoySyntaxException.createCausedWithMetaInfo(adjustedErrorMsg, pe, null, this.filePath, templateNodeBuilder.getTemplateNameForUserMsgs());
        }
        catch (SoySyntaxException sse) {
            throw sse.associateMetaInfo(null, this.filePath, templateNodeBuilder.getTemplateNameForUserMsgs());
        }
        if (templateHeaderDecls != null) {
            templateNodeBuilder.setHeaderDecls(templateHeaderDecls);
        }
        TemplateNode templateNode = templateNodeBuilder.build();
        templateNode.setSourceLocation(new SourceLocation(this.filePath, templateTag.beginLine));
        templateNode.addChildren(templateBodyNodes);
        return templateNode;
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{2, 32, 512, 524288};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0};
    }

    public SoyFileParser(InputStream stream) {
        this(stream, null);
    }

    public SoyFileParser(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new SoyFileParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 4; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
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
        for (int i = 0; i < 4; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public SoyFileParser(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new SoyFileParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 4; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 4; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public SoyFileParser(SoyFileParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 4; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(SoyFileParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 4; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
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

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[40];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 4; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) == 0) continue;
                la1tokens[32 + j] = true;
            }
        }
        for (i = 0; i < 40; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
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

    static {
        SoyFileParser.jj_la1_init_0();
        SoyFileParser.jj_la1_init_1();
    }
}

