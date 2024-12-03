/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.Macro;
import org.apache.velocity.runtime.directive.MacroParseException;
import org.apache.velocity.runtime.parser.CharStream;
import org.apache.velocity.runtime.parser.JJTParserState;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.ParserConstants;
import org.apache.velocity.runtime.parser.ParserTokenManager;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.TemplateParseException;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.TokenMgrError;
import org.apache.velocity.runtime.parser.VelocityCharStream;
import org.apache.velocity.runtime.parser.node.ASTAddNode;
import org.apache.velocity.runtime.parser.node.ASTAndNode;
import org.apache.velocity.runtime.parser.node.ASTAssignment;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTComment;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTDivNode;
import org.apache.velocity.runtime.parser.node.ASTEQNode;
import org.apache.velocity.runtime.parser.node.ASTElseIfStatement;
import org.apache.velocity.runtime.parser.node.ASTElseStatement;
import org.apache.velocity.runtime.parser.node.ASTEscape;
import org.apache.velocity.runtime.parser.node.ASTEscapedDirective;
import org.apache.velocity.runtime.parser.node.ASTExpression;
import org.apache.velocity.runtime.parser.node.ASTFalse;
import org.apache.velocity.runtime.parser.node.ASTFloatingPointLiteral;
import org.apache.velocity.runtime.parser.node.ASTGENode;
import org.apache.velocity.runtime.parser.node.ASTGTNode;
import org.apache.velocity.runtime.parser.node.ASTIdentifier;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTIndex;
import org.apache.velocity.runtime.parser.node.ASTIntegerLiteral;
import org.apache.velocity.runtime.parser.node.ASTIntegerRange;
import org.apache.velocity.runtime.parser.node.ASTLENode;
import org.apache.velocity.runtime.parser.node.ASTLTNode;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.ASTModNode;
import org.apache.velocity.runtime.parser.node.ASTMulNode;
import org.apache.velocity.runtime.parser.node.ASTNENode;
import org.apache.velocity.runtime.parser.node.ASTNotNode;
import org.apache.velocity.runtime.parser.node.ASTObjectArray;
import org.apache.velocity.runtime.parser.node.ASTOrNode;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.ASTSubtractNode;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTTextblock;
import org.apache.velocity.runtime.parser.node.ASTTrue;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class Parser
implements ParserTreeConstants,
ParserConstants {
    protected JJTParserState jjtree = new JJTParserState();
    private Map macroNames = new HashMap();
    public String currentTemplateName = "";
    public boolean strictEscape = false;
    VelocityCharStream velcharstream = null;
    private RuntimeServices rsvc = null;
    public ParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[69];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private final JJCalls[] jj_2_rtns = new JJCalls[12];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private List jj_expentries = new ArrayList();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public Parser(RuntimeServices rs) {
        this(new VelocityCharStream(new ByteArrayInputStream("\n".getBytes()), 1, 1));
        this.velcharstream = new VelocityCharStream(new ByteArrayInputStream("\n".getBytes()), 1, 1);
        this.strictEscape = rs.getBoolean("runtime.references.strict.escape", false);
        this.rsvc = rs;
    }

    public SimpleNode parse(Reader reader, String templateName) throws ParseException {
        SimpleNode sn = null;
        this.currentTemplateName = templateName;
        try {
            this.token_source.clearStateVars();
            this.velcharstream.ReInit(reader, 1, 1);
            this.ReInit(this.velcharstream);
            sn = this.process();
        }
        catch (MacroParseException mee) {
            this.rsvc.getLog().error("Parser Error: " + templateName, mee);
            throw mee;
        }
        catch (ParseException pe) {
            this.rsvc.getLog().error("Parser Exception: " + templateName, pe);
            throw new TemplateParseException(pe.currentToken, pe.expectedTokenSequences, pe.tokenImage, this.currentTemplateName);
        }
        catch (TokenMgrError tme) {
            throw new ParseException("Lexical error: " + tme.toString());
        }
        catch (Exception e) {
            String msg = "Parser Error: " + templateName;
            this.rsvc.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
        this.currentTemplateName = "";
        return sn;
    }

    public Directive getDirective(String directive) {
        return this.rsvc.getDirective(directive);
    }

    public boolean isDirective(String directive) {
        return this.rsvc.getDirective(directive) != null;
    }

    private String escapedDirective(String strImage) {
        int iLast = strImage.lastIndexOf("\\");
        String strDirective = strImage.substring(iLast + 1);
        boolean bRecognizedDirective = false;
        String dirTag = strDirective.substring(1);
        if (dirTag.charAt(0) == '{') {
            dirTag = dirTag.substring(1, dirTag.length() - 1);
        }
        if (this.strictEscape || this.isDirective(dirTag) || this.macroNames.containsKey(dirTag) || this.rsvc.isVelocimacro(dirTag, this.currentTemplateName)) {
            bRecognizedDirective = true;
        } else if (dirTag.equals("if") || dirTag.equals("end") || dirTag.equals("set") || dirTag.equals("else") || dirTag.equals("elseif")) {
            bRecognizedDirective = true;
        }
        if (bRecognizedDirective) {
            return strImage.substring(0, iLast / 2) + strDirective;
        }
        return strImage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isLeftParenthesis() {
        int no = 0;
        try {
            char c;
            do {
                c = this.velcharstream.readChar();
                ++no;
                if (c != '(') continue;
                boolean bl = true;
                return bl;
            } while (c == ' ' || c == '\n' || c == '\r' || c == '\t');
            boolean bl = false;
            return bl;
        }
        catch (IOException iOException) {
        }
        finally {
            this.velcharstream.backup(no);
        }
        return false;
    }

    public final SimpleNode process() throws ParseException {
        ASTprocess jjtn000 = new ASTprocess(this, 0);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block8: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 10: 
                    case 11: 
                    case 13: 
                    case 14: 
                    case 21: 
                    case 22: 
                    case 23: 
                    case 24: 
                    case 26: 
                    case 27: 
                    case 28: 
                    case 32: 
                    case 52: 
                    case 56: 
                    case 57: 
                    case 61: 
                    case 62: 
                    case 66: 
                    case 67: 
                    case 68: 
                    case 69: 
                    case 72: {
                        break;
                    }
                    default: {
                        this.jj_la1[0] = this.jj_gen;
                        break block8;
                    }
                }
                this.Statement();
            }
            this.jj_consume_token(0);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            ASTprocess aSTprocess = jjtn000;
            return aSTprocess;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Statement() throws ParseException {
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 52: {
                this.IfStatement();
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
                if (this.jj_2_1(2)) {
                    this.Reference();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 21: 
                    case 26: 
                    case 27: {
                        this.Comment();
                        break block0;
                    }
                    case 28: {
                        this.Textblock();
                        break block0;
                    }
                    case 14: {
                        this.SetDirective();
                        break block0;
                    }
                    case 13: {
                        this.EscapedDirective();
                        break block0;
                    }
                    case 22: {
                        this.Escape();
                        break block0;
                    }
                    case 61: 
                    case 62: {
                        this.Directive();
                        break block0;
                    }
                    case 10: 
                    case 11: 
                    case 23: 
                    case 24: 
                    case 32: 
                    case 56: 
                    case 57: 
                    case 67: 
                    case 68: 
                    case 69: 
                    case 72: {
                        this.Text();
                        break block0;
                    }
                }
                this.jj_la1[2] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void EscapedDirective() throws ParseException {
        ASTEscapedDirective jjtn000 = new ASTEscapedDirective(this, 2);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = null;
            t = this.jj_consume_token(13);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            t.image = this.escapedDirective(t.image);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void Escape() throws ParseException {
        ASTEscape jjtn000 = new ASTEscape(this, 3);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = null;
            int count = 0;
            boolean control = false;
            do {
                t = this.jj_consume_token(22);
                ++count;
            } while (this.jj_2_2(2));
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            switch (t.next.kind) {
                case 51: 
                case 52: 
                case 53: 
                case 54: {
                    control = true;
                }
            }
            String nTag = t.next.image.substring(1);
            if (this.strictEscape || this.isDirective(nTag) || this.macroNames.containsKey(nTag) || this.rsvc.isVelocimacro(nTag, this.currentTemplateName)) {
                control = true;
            }
            jjtn000.val = "";
            for (int i = 0; i < count; ++i) {
                jjtn000.val = jjtn000.val + (control ? "\\" : "\\\\");
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Comment() throws ParseException {
        ASTComment jjtn000 = new ASTComment(this, 4);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 21: {
                    this.jj_consume_token(21);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 25: {
                            this.jj_consume_token(25);
                            return;
                        }
                    }
                    this.jj_la1[3] = this.jj_gen;
                    return;
                }
                case 27: {
                    this.jj_consume_token(27);
                    return;
                }
                case 26: {
                    this.jj_consume_token(26);
                    return;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void Textblock() throws ParseException {
        ASTTextblock jjtn000 = new ASTTextblock(this, 5);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(28);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void FloatingPointLiteral() throws ParseException {
        ASTFloatingPointLiteral jjtn000 = new ASTFloatingPointLiteral(this, 6);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(57);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void IntegerLiteral() throws ParseException {
        ASTIntegerLiteral jjtn000 = new ASTIntegerLiteral(this, 7);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(56);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void StringLiteral() throws ParseException {
        ASTStringLiteral jjtn000 = new ASTStringLiteral(this, 8);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(32);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void Identifier() throws ParseException {
        ASTIdentifier jjtn000 = new ASTIdentifier(this, 9);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(66);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void Word() throws ParseException {
        ASTWord jjtn000 = new ASTWord(this, 10);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(61);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final int DirectiveArg() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 66: 
            case 68: {
                this.Reference();
                return 18;
            }
            case 61: {
                this.Word();
                return 10;
            }
            case 32: {
                this.StringLiteral();
                return 8;
            }
            case 56: {
                this.IntegerLiteral();
                return 7;
            }
        }
        this.jj_la1[5] = this.jj_gen;
        if (this.jj_2_3(Integer.MAX_VALUE)) {
            this.IntegerRange();
            return 15;
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 57: {
                this.FloatingPointLiteral();
                return 6;
            }
            case 8: {
                this.Map();
                return 13;
            }
            case 3: {
                this.ObjectArray();
                return 14;
            }
            case 33: {
                this.True();
                return 19;
            }
            case 34: {
                this.False();
                return 20;
            }
        }
        this.jj_la1[6] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final SimpleNode Directive() throws ParseException {
        ASTDirective jjtn000 = new ASTDirective(this, 11);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        int argPos = 0;
        boolean isVM = false;
        boolean doItNow = false;
        try {
            block62: {
                int directiveType;
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 61: {
                        t = this.jj_consume_token(61);
                        break;
                    }
                    case 62: {
                        t = this.jj_consume_token(62);
                        break;
                    }
                    default: {
                        this.jj_la1[7] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                String directiveName = t.kind == 62 ? t.image.substring(2, t.image.length() - 1) : t.image.substring(1);
                Directive d = this.getDirective(directiveName);
                if (directiveName.equals("macro")) {
                    doItNow = true;
                }
                jjtn000.setDirectiveName(directiveName);
                if (d == null) {
                    if (directiveName.startsWith("@")) {
                        directiveType = 1;
                    } else {
                        isVM = this.rsvc.isVelocimacro(directiveName, this.currentTemplateName);
                        directiveType = 2;
                    }
                } else {
                    directiveType = d.getType();
                }
                this.token_source.SwitchTo(3);
                argPos = 0;
                if (this.isLeftParenthesis()) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 31: {
                            this.jj_consume_token(31);
                            break;
                        }
                        default: {
                            this.jj_la1[8] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(10);
                    while (this.jj_2_4(2)) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 31: {
                                this.jj_consume_token(31);
                                break;
                            }
                            default: {
                                this.jj_la1[9] = this.jj_gen;
                            }
                        }
                        block16 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 5: {
                                this.jj_consume_token(5);
                                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                    case 31: {
                                        this.jj_consume_token(31);
                                        break block16;
                                    }
                                }
                                this.jj_la1[10] = this.jj_gen;
                                break;
                            }
                            default: {
                                this.jj_la1[11] = this.jj_gen;
                            }
                        }
                        int argType = this.DirectiveArg();
                        if (argType == 10) {
                            if (!doItNow || argPos != 0) {
                                if (isVM) {
                                    throw new MacroParseException("Invalid arg #" + argPos + " in VM " + t.image, this.currentTemplateName, t);
                                }
                                if (!(d == null || directiveName.equals("foreach") && argPos == 1)) {
                                    throw new MacroParseException("Invalid arg #" + argPos + " in directive " + t.image, this.currentTemplateName, t);
                                }
                            }
                        } else if (doItNow && argPos == 0) {
                            throw new MacroParseException("Invalid first arg in #macro() directive - must be a word token (no ' or \" surrounding)", this.currentTemplateName, t);
                        }
                        ++argPos;
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 31: {
                            this.jj_consume_token(31);
                            break;
                        }
                        default: {
                            this.jj_la1[12] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(11);
                    if (directiveType == 2) {
                        ASTDirective aSTDirective = jjtn000;
                        return aSTDirective;
                    }
                } else {
                    if (doItNow) {
                        throw new MacroParseException("A macro declaration requires at least a name argument", this.currentTemplateName, t);
                    }
                    this.token_source.stateStackPop();
                    this.token_source.inDirective = false;
                    ASTDirective aSTDirective = jjtn000;
                    return aSTDirective;
                }
                ASTBlock jjtn001 = new ASTBlock(this, 12);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 10: 
                            case 11: 
                            case 13: 
                            case 14: 
                            case 21: 
                            case 22: 
                            case 23: 
                            case 24: 
                            case 26: 
                            case 27: 
                            case 28: 
                            case 32: 
                            case 52: 
                            case 56: 
                            case 57: 
                            case 61: 
                            case 62: 
                            case 66: 
                            case 67: 
                            case 68: 
                            case 69: 
                            case 72: {
                                break;
                            }
                            default: {
                                this.jj_la1[13] = this.jj_gen;
                                break block62;
                            }
                        }
                        this.Statement();
                    }
                }
                catch (Throwable jjte001) {
                    if (jjtc001) {
                        this.jjtree.clearNodeScope(jjtn001);
                        jjtc001 = false;
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte001 instanceof RuntimeException) {
                        throw (RuntimeException)jjte001;
                    }
                    if (jjte001 instanceof ParseException) {
                        throw (ParseException)jjte001;
                    }
                    throw (Error)jjte001;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            this.jj_consume_token(51);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            if (doItNow) {
                Macro.checkArgs(this.rsvc, t, jjtn000, this.currentTemplateName);
                String macroName = jjtn000.jjtGetChild((int)0).getFirstToken().image;
                this.macroNames.put(macroName, macroName);
            }
            ASTDirective aSTDirective = jjtn000;
            return aSTDirective;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Map() throws ParseException {
        ASTMap jjtn000 = new ASTMap(this, 13);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block21: {
                this.jj_consume_token(8);
                if (this.jj_2_5(2)) {
                    this.Parameter();
                    this.jj_consume_token(7);
                    this.Parameter();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 5: {
                                break;
                            }
                            default: {
                                this.jj_la1[14] = this.jj_gen;
                                break block21;
                            }
                        }
                        this.jj_consume_token(5);
                        this.Parameter();
                        this.jj_consume_token(7);
                        this.Parameter();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 31: {
                        this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                    }
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    return;
                }
                case 69: {
                    this.jj_consume_token(69);
                    return;
                }
                default: {
                    this.jj_la1[16] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (!(jjte000 instanceof ParseException)) throw (Error)jjte000;
            throw (ParseException)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void ObjectArray() throws ParseException {
        ASTObjectArray jjtn000 = new ASTObjectArray(this, 14);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(3);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: 
                case 8: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 56: 
                case 57: 
                case 66: 
                case 68: {
                    this.Parameter();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 5: {
                                break;
                            }
                            default: {
                                this.jj_la1[17] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(5);
                        this.Parameter();
                    }
                }
                default: {
                    this.jj_la1[18] = this.jj_gen;
                }
            }
            this.jj_consume_token(4);
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void IntegerRange() throws ParseException {
        ASTIntegerRange jjtn000 = new ASTIntegerRange(this, 15);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(3);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 31: {
                    this.jj_consume_token(31);
                    break;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 66: 
                case 68: {
                    this.Reference();
                    break;
                }
                case 56: {
                    this.IntegerLiteral();
                    break;
                }
                default: {
                    this.jj_la1[20] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 31: {
                    this.jj_consume_token(31);
                    break;
                }
                default: {
                    this.jj_la1[21] = this.jj_gen;
                }
            }
            this.jj_consume_token(6);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 31: {
                    this.jj_consume_token(31);
                    break;
                }
                default: {
                    this.jj_la1[22] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 66: 
                case 68: {
                    this.Reference();
                    break;
                }
                case 56: {
                    this.IntegerLiteral();
                    break;
                }
                default: {
                    this.jj_la1[23] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 31: {
                    this.jj_consume_token(31);
                    break;
                }
                default: {
                    this.jj_la1[24] = this.jj_gen;
                }
            }
            this.jj_consume_token(4);
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void IndexParameter() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 31: {
                this.jj_consume_token(31);
                break;
            }
            default: {
                this.jj_la1[25] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 32: {
                this.StringLiteral();
                break;
            }
            case 56: {
                this.IntegerLiteral();
                break;
            }
            case 33: {
                this.True();
                break;
            }
            case 34: {
                this.False();
                break;
            }
            case 66: 
            case 68: {
                this.Reference();
                break;
            }
            default: {
                this.jj_la1[26] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 31: {
                this.jj_consume_token(31);
                break;
            }
            default: {
                this.jj_la1[27] = this.jj_gen;
            }
        }
    }

    public final void Parameter() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 31: {
                this.jj_consume_token(31);
                break;
            }
            default: {
                this.jj_la1[28] = this.jj_gen;
            }
        }
        block3 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 32: {
                this.StringLiteral();
                break;
            }
            case 56: {
                this.IntegerLiteral();
                break;
            }
            default: {
                this.jj_la1[29] = this.jj_gen;
                if (this.jj_2_6(Integer.MAX_VALUE)) {
                    this.IntegerRange();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 8: {
                        this.Map();
                        break block3;
                    }
                    case 3: {
                        this.ObjectArray();
                        break block3;
                    }
                    case 33: {
                        this.True();
                        break block3;
                    }
                    case 34: {
                        this.False();
                        break block3;
                    }
                    case 66: 
                    case 68: {
                        this.Reference();
                        break block3;
                    }
                    case 57: {
                        this.FloatingPointLiteral();
                        break block3;
                    }
                }
                this.jj_la1[30] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 31: {
                this.jj_consume_token(31);
                break;
            }
            default: {
                this.jj_la1[31] = this.jj_gen;
            }
        }
    }

    public final void Method() throws ParseException {
        ASTMethod jjtn000 = new ASTMethod(this, 16);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Identifier();
            this.jj_consume_token(10);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: 
                case 8: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 56: 
                case 57: 
                case 66: 
                case 68: {
                    this.Parameter();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 5: {
                                break;
                            }
                            default: {
                                this.jj_la1[32] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(5);
                        this.Parameter();
                    }
                }
                default: {
                    this.jj_la1[33] = this.jj_gen;
                }
            }
            this.jj_consume_token(12);
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Index() throws ParseException {
        ASTIndex jjtn000 = new ASTIndex(this, 17);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(1);
            this.IndexParameter();
            this.jj_consume_token(2);
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Reference() throws ParseException {
        ASTReference jjtn000 = new ASTReference(this, 18);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 66: {
                    this.jj_consume_token(66);
                    block27: while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 1: {
                                break;
                            }
                            default: {
                                this.jj_la1[34] = this.jj_gen;
                                break block27;
                            }
                        }
                        this.Index();
                    }
                    block28: while (this.jj_2_7(2)) {
                        this.jj_consume_token(67);
                        if (this.jj_2_8(3)) {
                            this.Method();
                        } else {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 66: {
                                    this.Identifier();
                                    break;
                                }
                                default: {
                                    this.jj_la1[35] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                        }
                        while (true) {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 1: {
                                    break;
                                }
                                default: {
                                    this.jj_la1[36] = this.jj_gen;
                                    continue block28;
                                }
                            }
                            this.Index();
                        }
                    }
                    return;
                }
                case 68: {
                    this.jj_consume_token(68);
                    this.jj_consume_token(66);
                    block30: while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 1: {
                                break;
                            }
                            default: {
                                this.jj_la1[37] = this.jj_gen;
                                break block30;
                            }
                        }
                        this.Index();
                    }
                    block31: while (this.jj_2_9(2)) {
                        this.jj_consume_token(67);
                        if (this.jj_2_10(3)) {
                            this.Method();
                        } else {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 66: {
                                    this.Identifier();
                                    break;
                                }
                                default: {
                                    this.jj_la1[38] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                        }
                        while (true) {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 1: {
                                    break;
                                }
                                default: {
                                    this.jj_la1[39] = this.jj_gen;
                                    continue block31;
                                }
                            }
                            this.Index();
                        }
                    }
                    this.jj_consume_token(69);
                    return;
                }
                default: {
                    this.jj_la1[40] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (!(jjte000 instanceof ParseException)) throw (Error)jjte000;
            throw (ParseException)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void True() throws ParseException {
        ASTTrue jjtn000 = new ASTTrue(this, 19);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(33);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void False() throws ParseException {
        ASTFalse jjtn000 = new ASTFalse(this, 20);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(34);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Text() throws ParseException {
        ASTText jjtn000 = new ASTText(this, 21);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 24: {
                    this.jj_consume_token(24);
                    return;
                }
                case 67: {
                    this.jj_consume_token(67);
                    return;
                }
                case 11: {
                    this.jj_consume_token(11);
                    return;
                }
                case 10: {
                    this.jj_consume_token(10);
                    return;
                }
                case 56: {
                    this.jj_consume_token(56);
                    return;
                }
                case 57: {
                    this.jj_consume_token(57);
                    return;
                }
                case 32: {
                    this.jj_consume_token(32);
                    return;
                }
                case 23: {
                    this.jj_consume_token(23);
                    return;
                }
                case 68: {
                    this.jj_consume_token(68);
                    return;
                }
                case 69: {
                    this.jj_consume_token(69);
                    return;
                }
                case 72: {
                    this.jj_consume_token(72);
                    return;
                }
                default: {
                    this.jj_la1[41] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void IfStatement() throws ParseException {
        ASTIfStatement jjtn000 = new ASTIfStatement(this, 22);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block37: {
                this.jj_consume_token(52);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 31: {
                        this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[42] = this.jj_gen;
                    }
                }
                this.jj_consume_token(10);
                this.Expression();
                this.jj_consume_token(11);
                ASTBlock jjtn001 = new ASTBlock(this, 12);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 10: 
                            case 11: 
                            case 13: 
                            case 14: 
                            case 21: 
                            case 22: 
                            case 23: 
                            case 24: 
                            case 26: 
                            case 27: 
                            case 28: 
                            case 32: 
                            case 52: 
                            case 56: 
                            case 57: 
                            case 61: 
                            case 62: 
                            case 66: 
                            case 67: 
                            case 68: 
                            case 69: 
                            case 72: {
                                break;
                            }
                            default: {
                                this.jj_la1[43] = this.jj_gen;
                                break block37;
                            }
                        }
                        this.Statement();
                    }
                }
                catch (Throwable jjte001) {
                    if (jjtc001) {
                        this.jjtree.clearNodeScope(jjtn001);
                        jjtc001 = false;
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte001 instanceof RuntimeException) {
                        throw (RuntimeException)jjte001;
                    }
                    if (jjte001 instanceof ParseException) {
                        throw (ParseException)jjte001;
                    }
                    throw (Error)jjte001;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 53: {
                    block26: while (true) {
                        this.ElseIfStatement();
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 53: {
                                continue block26;
                            }
                        }
                        break;
                    }
                    this.jj_la1[44] = this.jj_gen;
                    break;
                }
                default: {
                    this.jj_la1[45] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 54: {
                    this.ElseStatement();
                    break;
                }
                default: {
                    this.jj_la1[46] = this.jj_gen;
                }
            }
            this.jj_consume_token(51);
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void ElseStatement() throws ParseException {
        block24: {
            ASTElseStatement jjtn000 = new ASTElseStatement(this, 23);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(54);
                ASTBlock jjtn001 = new ASTBlock(this, 12);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 10: 
                            case 11: 
                            case 13: 
                            case 14: 
                            case 21: 
                            case 22: 
                            case 23: 
                            case 24: 
                            case 26: 
                            case 27: 
                            case 28: 
                            case 32: 
                            case 52: 
                            case 56: 
                            case 57: 
                            case 61: 
                            case 62: 
                            case 66: 
                            case 67: 
                            case 68: 
                            case 69: 
                            case 72: {
                                break;
                            }
                            default: {
                                this.jj_la1[47] = this.jj_gen;
                                break block24;
                            }
                        }
                        this.Statement();
                    }
                }
                catch (Throwable jjte001) {
                    if (jjtc001) {
                        this.jjtree.clearNodeScope(jjtn001);
                        jjtc001 = false;
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte001 instanceof RuntimeException) {
                        throw (RuntimeException)jjte001;
                    }
                    if (jjte001 instanceof ParseException) {
                        throw (ParseException)jjte001;
                    }
                    throw (Error)jjte001;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            catch (Throwable jjte000) {
                if (jjtc000) {
                    this.jjtree.clearNodeScope(jjtn000);
                    jjtc000 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw (RuntimeException)jjte000;
                }
                if (jjte000 instanceof ParseException) {
                    throw (ParseException)jjte000;
                }
                throw (Error)jjte000;
            }
            finally {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                }
            }
        }
    }

    public final void ElseIfStatement() throws ParseException {
        block27: {
            ASTElseIfStatement jjtn000 = new ASTElseIfStatement(this, 24);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(53);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 31: {
                        this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[48] = this.jj_gen;
                    }
                }
                this.jj_consume_token(10);
                this.Expression();
                this.jj_consume_token(11);
                ASTBlock jjtn001 = new ASTBlock(this, 12);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 10: 
                            case 11: 
                            case 13: 
                            case 14: 
                            case 21: 
                            case 22: 
                            case 23: 
                            case 24: 
                            case 26: 
                            case 27: 
                            case 28: 
                            case 32: 
                            case 52: 
                            case 56: 
                            case 57: 
                            case 61: 
                            case 62: 
                            case 66: 
                            case 67: 
                            case 68: 
                            case 69: 
                            case 72: {
                                break;
                            }
                            default: {
                                this.jj_la1[49] = this.jj_gen;
                                break block27;
                            }
                        }
                        this.Statement();
                    }
                }
                catch (Throwable jjte001) {
                    if (jjtc001) {
                        this.jjtree.clearNodeScope(jjtn001);
                        jjtc001 = false;
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte001 instanceof RuntimeException) {
                        throw (RuntimeException)jjte001;
                    }
                    if (jjte001 instanceof ParseException) {
                        throw (ParseException)jjte001;
                    }
                    throw (Error)jjte001;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            catch (Throwable jjte000) {
                if (jjtc000) {
                    this.jjtree.clearNodeScope(jjtn000);
                    jjtc000 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw (RuntimeException)jjte000;
                }
                if (jjte000 instanceof ParseException) {
                    throw (ParseException)jjte000;
                }
                throw (Error)jjte000;
            }
            finally {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                }
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void SetDirective() throws ParseException {
        ASTSetDirective jjtn000 = new ASTSetDirective(this, 25);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(14);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 31: {
                    this.jj_consume_token(31);
                    break;
                }
                default: {
                    this.jj_la1[50] = this.jj_gen;
                }
            }
            this.Reference();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 31: {
                    this.jj_consume_token(31);
                    break;
                }
                default: {
                    this.jj_la1[51] = this.jj_gen;
                }
            }
            this.jj_consume_token(50);
            this.Expression();
            this.jj_consume_token(11);
            this.token_source.inSet = false;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 35: {
                    this.jj_consume_token(35);
                    return;
                }
                default: {
                    this.jj_la1[52] = this.jj_gen;
                    return;
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (!(jjte000 instanceof ParseException)) throw (Error)jjte000;
            throw (ParseException)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Expression() throws ParseException {
        ASTExpression jjtn000 = new ASTExpression(this, 26);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.ConditionalOrExpression();
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Assignment() throws ParseException {
        ASTAssignment jjtn000 = new ASTAssignment(this, 27);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.PrimaryExpression();
            this.jj_consume_token(50);
            this.Expression();
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, 2);
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void ConditionalOrExpression() throws ParseException {
        this.ConditionalAndExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 42: {
                    break;
                }
                default: {
                    this.jj_la1[53] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(42);
            ASTOrNode jjtn001 = new ASTOrNode(this, 28);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.ConditionalAndExpression();
                continue;
            }
            catch (Throwable jjte001) {
                if (jjtc001) {
                    this.jjtree.clearNodeScope(jjtn001);
                    jjtc001 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte001 instanceof RuntimeException) {
                    throw (RuntimeException)jjte001;
                }
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void ConditionalAndExpression() throws ParseException {
        this.EqualityExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 41: {
                    break;
                }
                default: {
                    this.jj_la1[54] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(41);
            ASTAndNode jjtn001 = new ASTAndNode(this, 29);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.EqualityExpression();
                continue;
            }
            catch (Throwable jjte001) {
                if (jjtc001) {
                    this.jjtree.clearNodeScope(jjtn001);
                    jjtc001 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte001 instanceof RuntimeException) {
                    throw (RuntimeException)jjte001;
                }
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    public final void EqualityExpression() throws ParseException {
        block26: {
            this.RelationalExpression();
            block17: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 47: 
                    case 48: {
                        break;
                    }
                    default: {
                        this.jj_la1[55] = this.jj_gen;
                        break block26;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 47: {
                        this.jj_consume_token(47);
                        ASTEQNode jjtn001 = new ASTEQNode(this, 30);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.RelationalExpression();
                            continue block17;
                        }
                        catch (Throwable jjte001) {
                            if (jjtc001) {
                                this.jjtree.clearNodeScope(jjtn001);
                                jjtc001 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw (RuntimeException)jjte001;
                            }
                            if (jjte001 instanceof ParseException) {
                                throw (ParseException)jjte001;
                            }
                            throw (Error)jjte001;
                        }
                        finally {
                            if (!jjtc001) continue block17;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block17;
                        }
                    }
                    case 48: {
                        this.jj_consume_token(48);
                        ASTNENode jjtn002 = new ASTNENode(this, 31);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.RelationalExpression();
                            continue block17;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block17;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block17;
                        }
                    }
                }
                break;
            }
            this.jj_la1[56] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void RelationalExpression() throws ParseException {
        block46: {
            this.AdditiveExpression();
            block29: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 43: 
                    case 44: 
                    case 45: 
                    case 46: {
                        break;
                    }
                    default: {
                        this.jj_la1[57] = this.jj_gen;
                        break block46;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 43: {
                        this.jj_consume_token(43);
                        ASTLTNode jjtn001 = new ASTLTNode(this, 32);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.AdditiveExpression();
                            continue block29;
                        }
                        catch (Throwable jjte001) {
                            if (jjtc001) {
                                this.jjtree.clearNodeScope(jjtn001);
                                jjtc001 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw (RuntimeException)jjte001;
                            }
                            if (jjte001 instanceof ParseException) {
                                throw (ParseException)jjte001;
                            }
                            throw (Error)jjte001;
                        }
                        finally {
                            if (!jjtc001) continue block29;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block29;
                        }
                    }
                    case 45: {
                        this.jj_consume_token(45);
                        ASTGTNode jjtn002 = new ASTGTNode(this, 33);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.AdditiveExpression();
                            continue block29;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block29;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block29;
                        }
                    }
                    case 44: {
                        this.jj_consume_token(44);
                        ASTLENode jjtn003 = new ASTLENode(this, 34);
                        boolean jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.AdditiveExpression();
                            continue block29;
                        }
                        catch (Throwable jjte003) {
                            if (jjtc003) {
                                this.jjtree.clearNodeScope(jjtn003);
                                jjtc003 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte003 instanceof RuntimeException) {
                                throw (RuntimeException)jjte003;
                            }
                            if (jjte003 instanceof ParseException) {
                                throw (ParseException)jjte003;
                            }
                            throw (Error)jjte003;
                        }
                        finally {
                            if (!jjtc003) continue block29;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block29;
                        }
                    }
                    case 46: {
                        this.jj_consume_token(46);
                        ASTGENode jjtn004 = new ASTGENode(this, 35);
                        boolean jjtc004 = true;
                        this.jjtree.openNodeScope(jjtn004);
                        try {
                            this.AdditiveExpression();
                            continue block29;
                        }
                        catch (Throwable jjte004) {
                            if (jjtc004) {
                                this.jjtree.clearNodeScope(jjtn004);
                                jjtc004 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte004 instanceof RuntimeException) {
                                throw (RuntimeException)jjte004;
                            }
                            if (jjte004 instanceof ParseException) {
                                throw (ParseException)jjte004;
                            }
                            throw (Error)jjte004;
                        }
                        finally {
                            if (!jjtc004) continue block29;
                            this.jjtree.closeNodeScope((Node)jjtn004, 2);
                            continue block29;
                        }
                    }
                }
                break;
            }
            this.jj_la1[58] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void AdditiveExpression() throws ParseException {
        block26: {
            this.MultiplicativeExpression();
            block17: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 36: 
                    case 37: {
                        break;
                    }
                    default: {
                        this.jj_la1[59] = this.jj_gen;
                        break block26;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 37: {
                        this.jj_consume_token(37);
                        ASTAddNode jjtn001 = new ASTAddNode(this, 36);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.MultiplicativeExpression();
                            continue block17;
                        }
                        catch (Throwable jjte001) {
                            if (jjtc001) {
                                this.jjtree.clearNodeScope(jjtn001);
                                jjtc001 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw (RuntimeException)jjte001;
                            }
                            if (jjte001 instanceof ParseException) {
                                throw (ParseException)jjte001;
                            }
                            throw (Error)jjte001;
                        }
                        finally {
                            if (!jjtc001) continue block17;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block17;
                        }
                    }
                    case 36: {
                        this.jj_consume_token(36);
                        ASTSubtractNode jjtn002 = new ASTSubtractNode(this, 37);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.MultiplicativeExpression();
                            continue block17;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block17;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block17;
                        }
                    }
                }
                break;
            }
            this.jj_la1[60] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void MultiplicativeExpression() throws ParseException {
        block36: {
            this.UnaryExpression();
            block23: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 38: 
                    case 39: 
                    case 40: {
                        break;
                    }
                    default: {
                        this.jj_la1[61] = this.jj_gen;
                        break block36;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 38: {
                        this.jj_consume_token(38);
                        ASTMulNode jjtn001 = new ASTMulNode(this, 38);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.UnaryExpression();
                            continue block23;
                        }
                        catch (Throwable jjte001) {
                            if (jjtc001) {
                                this.jjtree.clearNodeScope(jjtn001);
                                jjtc001 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte001 instanceof RuntimeException) {
                                throw (RuntimeException)jjte001;
                            }
                            if (jjte001 instanceof ParseException) {
                                throw (ParseException)jjte001;
                            }
                            throw (Error)jjte001;
                        }
                        finally {
                            if (!jjtc001) continue block23;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block23;
                        }
                    }
                    case 39: {
                        this.jj_consume_token(39);
                        ASTDivNode jjtn002 = new ASTDivNode(this, 39);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.UnaryExpression();
                            continue block23;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block23;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block23;
                        }
                    }
                    case 40: {
                        this.jj_consume_token(40);
                        ASTModNode jjtn003 = new ASTModNode(this, 40);
                        boolean jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.UnaryExpression();
                            continue block23;
                        }
                        catch (Throwable jjte003) {
                            if (jjtc003) {
                                this.jjtree.clearNodeScope(jjtn003);
                                jjtc003 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte003 instanceof RuntimeException) {
                                throw (RuntimeException)jjte003;
                            }
                            if (jjte003 instanceof ParseException) {
                                throw (ParseException)jjte003;
                            }
                            throw (Error)jjte003;
                        }
                        finally {
                            if (!jjtc003) continue block23;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block23;
                        }
                    }
                }
                break;
            }
            this.jj_la1[62] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void UnaryExpression() throws ParseException {
        if (this.jj_2_11(2)) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 31: {
                    this.jj_consume_token(31);
                    break;
                }
                default: {
                    this.jj_la1[63] = this.jj_gen;
                }
            }
            this.jj_consume_token(49);
            ASTNotNode jjtn001 = new ASTNotNode(this, 41);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.UnaryExpression();
            }
            catch (Throwable jjte001) {
                if (jjtc001) {
                    this.jjtree.clearNodeScope(jjtn001);
                    jjtc001 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte001 instanceof RuntimeException) {
                    throw (RuntimeException)jjte001;
                }
                if (jjte001 instanceof ParseException) {
                    throw (ParseException)jjte001;
                }
                throw (Error)jjte001;
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, 1);
                }
            }
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 3: 
                case 8: 
                case 10: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 56: 
                case 57: 
                case 66: 
                case 68: {
                    this.PrimaryExpression();
                    break;
                }
                default: {
                    this.jj_la1[64] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
    }

    public final void PrimaryExpression() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 31: {
                this.jj_consume_token(31);
                break;
            }
            default: {
                this.jj_la1[65] = this.jj_gen;
            }
        }
        block3 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 32: {
                this.StringLiteral();
                break;
            }
            case 66: 
            case 68: {
                this.Reference();
                break;
            }
            case 56: {
                this.IntegerLiteral();
                break;
            }
            default: {
                this.jj_la1[66] = this.jj_gen;
                if (this.jj_2_12(Integer.MAX_VALUE)) {
                    this.IntegerRange();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 57: {
                        this.FloatingPointLiteral();
                        break block3;
                    }
                    case 8: {
                        this.Map();
                        break block3;
                    }
                    case 3: {
                        this.ObjectArray();
                        break block3;
                    }
                    case 33: {
                        this.True();
                        break block3;
                    }
                    case 34: {
                        this.False();
                        break block3;
                    }
                    case 10: {
                        this.jj_consume_token(10);
                        this.Expression();
                        this.jj_consume_token(11);
                        break block3;
                    }
                }
                this.jj_la1[67] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 31: {
                this.jj_consume_token(31);
                break;
            }
            default: {
                this.jj_la1[68] = this.jj_gen;
            }
        }
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
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
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(11, xla);
        }
    }

    private boolean jj_3_7() {
        if (this.jj_scan_token(67)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_8()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_32()) {
                return true;
            }
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_89());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_61() {
        Token xsp;
        if (this.jj_3R_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_97());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_39() {
        Token xsp;
        if (this.jj_scan_token(68)) {
            return true;
        }
        if (this.jj_scan_token(66)) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_74());
        this.jj_scanpos = xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3_9());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(69);
    }

    private boolean jj_3R_40() {
        return this.jj_scan_token(56);
    }

    private boolean jj_3R_88() {
        return this.jj_scan_token(10);
    }

    private boolean jj_3R_87() {
        return this.jj_3R_71();
    }

    private boolean jj_3R_86() {
        return this.jj_3R_70();
    }

    private boolean jj_3R_38() {
        Token xsp;
        if (this.jj_scan_token(66)) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_63());
        this.jj_scanpos = xsp;
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3_7());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_85() {
        return this.jj_3R_69();
    }

    private boolean jj_3R_84() {
        return this.jj_3R_68();
    }

    private boolean jj_3R_83() {
        return this.jj_3R_67();
    }

    private boolean jj_3R_67() {
        return this.jj_scan_token(57);
    }

    private boolean jj_3R_24() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_38()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_39()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_82() {
        return this.jj_3R_66();
    }

    private boolean jj_3R_81() {
        return this.jj_3R_40();
    }

    private boolean jj_3R_80() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_79() {
        return this.jj_3R_65();
    }

    private boolean jj_3R_72() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_79()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_80()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_81()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_82()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_83()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_84()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_85()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_86()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_87()) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_3R_88()) {
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
        return false;
    }

    private boolean jj_3R_73() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        if (this.jj_3R_91()) {
            return true;
        }
        return this.jj_scan_token(2);
    }

    private boolean jj_3R_35() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_11()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_62()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_11() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(49)) {
            return true;
        }
        return this.jj_3R_35();
    }

    private boolean jj_3R_62() {
        return this.jj_3R_72();
    }

    private boolean jj_3_6() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_30()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_31()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(6);
    }

    private boolean jj_3R_33() {
        if (this.jj_3R_60()) {
            return true;
        }
        if (this.jj_scan_token(10)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_61()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(12);
    }

    private boolean jj_3R_59() {
        return this.jj_3R_67();
    }

    private boolean jj_3R_58() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_57() {
        return this.jj_3R_71();
    }

    private boolean jj_3R_56() {
        return this.jj_3R_70();
    }

    private boolean jj_3R_55() {
        return this.jj_3R_69();
    }

    private boolean jj_3R_54() {
        return this.jj_3R_68();
    }

    private boolean jj_3R_53() {
        return this.jj_3R_66();
    }

    private boolean jj_3R_52() {
        return this.jj_3R_40();
    }

    private boolean jj_3R_51() {
        return this.jj_3R_65();
    }

    private boolean jj_3R_29() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_51()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_52()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_53()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_54()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_55()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_56()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_57()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_58()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_59()) {
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
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_100() {
        if (this.jj_scan_token(5)) {
            return true;
        }
        if (this.jj_3R_29()) {
            return true;
        }
        if (this.jj_scan_token(7)) {
            return true;
        }
        return this.jj_3R_29();
    }

    private boolean jj_3R_96() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_95() {
        return this.jj_3R_71();
    }

    private boolean jj_3R_94() {
        return this.jj_3R_70();
    }

    private boolean jj_3R_93() {
        return this.jj_3R_40();
    }

    private boolean jj_3R_92() {
        return this.jj_3R_65();
    }

    private boolean jj_3R_99() {
        return this.jj_3R_40();
    }

    private boolean jj_3_2() {
        return this.jj_scan_token(22);
    }

    private boolean jj_3R_76() {
        return this.jj_3R_40();
    }

    private boolean jj_3R_101() {
        if (this.jj_scan_token(5)) {
            return true;
        }
        return this.jj_3R_29();
    }

    private boolean jj_3R_91() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_92()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_93()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_94()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_95()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_96()) {
                            return true;
                        }
                    }
                }
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_98() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_75() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_78() {
        Token xsp;
        if (this.jj_3R_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_101());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_66() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_75()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_76()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(6)) {
            return true;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_98()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_99()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(4);
    }

    private boolean jj_3R_26() {
        return this.jj_3R_40();
    }

    private boolean jj_3R_69() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_78()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(4);
    }

    private boolean jj_3R_77() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3_5() {
        Token xsp;
        if (this.jj_3R_29()) {
            return true;
        }
        if (this.jj_scan_token(7)) {
            return true;
        }
        if (this.jj_3R_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_100());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_25() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_68() {
        if (this.jj_scan_token(8)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_5()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_77()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(9)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(69)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_1() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_50() {
        return this.jj_3R_71();
    }

    private boolean jj_3R_90() {
        return this.jj_3R_73();
    }

    private boolean jj_3R_49() {
        return this.jj_3R_70();
    }

    private boolean jj_3_3() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_25()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_26()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(6);
    }

    private boolean jj_3R_48() {
        return this.jj_3R_69();
    }

    private boolean jj_3R_89() {
        return this.jj_3R_73();
    }

    private boolean jj_3R_47() {
        return this.jj_3R_68();
    }

    private boolean jj_3R_46() {
        return this.jj_3R_67();
    }

    private boolean jj_3R_45() {
        return this.jj_3R_66();
    }

    private boolean jj_3R_34() {
        return this.jj_3R_60();
    }

    private boolean jj_3R_37() {
        return this.jj_3R_40();
    }

    private boolean jj_3R_32() {
        return this.jj_3R_60();
    }

    private boolean jj_3R_44() {
        return this.jj_3R_40();
    }

    private boolean jj_3R_27() {
        if (this.jj_scan_token(5)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_43() {
        return this.jj_3R_65();
    }

    private boolean jj_3R_42() {
        return this.jj_3R_64();
    }

    private boolean jj_3R_36() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_41() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_28() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_41()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_42()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_43()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_44()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_45()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_46()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_47()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_48()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_49()) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_3R_50()) {
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
        return false;
    }

    private boolean jj_3_10() {
        return this.jj_3R_33();
    }

    private boolean jj_3R_64() {
        return this.jj_scan_token(61);
    }

    private boolean jj_3R_31() {
        return this.jj_3R_40();
    }

    private boolean jj_3_8() {
        return this.jj_3R_33();
    }

    private boolean jj_3R_74() {
        return this.jj_3R_73();
    }

    private boolean jj_3R_60() {
        return this.jj_scan_token(66);
    }

    private boolean jj_3R_97() {
        if (this.jj_scan_token(5)) {
            return true;
        }
        return this.jj_3R_29();
    }

    private boolean jj_3R_71() {
        return this.jj_scan_token(34);
    }

    private boolean jj_3_4() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_27()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_3R_28();
    }

    private boolean jj_3R_63() {
        return this.jj_3R_73();
    }

    private boolean jj_3R_30() {
        return this.jj_3R_24();
    }

    private boolean jj_3R_70() {
        return this.jj_scan_token(33);
    }

    private boolean jj_3_9() {
        if (this.jj_scan_token(67)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_10()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_34()) {
                return true;
            }
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_90());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_12() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_36()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_37()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(6);
    }

    private boolean jj_3R_65() {
        return this.jj_scan_token(32);
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{501246976, 0, 501246976, 0x2000000, 0xC200000, 0, 264, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 32, Integer.MIN_VALUE, 501246976, 32, Integer.MIN_VALUE, 512, 32, -2147483384, Integer.MIN_VALUE, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 264, Integer.MIN_VALUE, 32, -2147483384, 2, 0, 2, 2, 0, 2, 0, 25168896, Integer.MIN_VALUE, 501246976, 0, 0, 0, 501246976, Integer.MIN_VALUE, 501246976, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Integer.MIN_VALUE, -2147482360, Integer.MIN_VALUE, 0, 1288, Integer.MIN_VALUE};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{1661992961, 0x100000, 1660944385, 0, 0, 0x21000001, 0x2000006, 0x60000000, 0, 0, 0, 0, 0, 1661992961, 0, 0, 0, 0, 0x3000007, 0, 0x1000000, 0, 0, 0x1000000, 0, 0, 0x1000007, 0, 0, 0x1000001, 0x2000006, 0, 0, 0x3000007, 0, 0, 0, 0, 0, 0, 0, 0x3000001, 0, 1661992961, 0x200000, 0x200000, 0x400000, 1661992961, 0, 1661992961, 0, 0, 8, 1024, 512, 98304, 98304, 30720, 30720, 48, 48, 448, 448, 0, 0x3000007, 0, 0x1000001, 0x2000006, 0};
    }

    private static void jj_la1_init_2() {
        jj_la1_2 = new int[]{316, 0, 312, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 316, 0, 0, 32, 0, 20, 0, 20, 0, 0, 20, 0, 0, 20, 0, 0, 0, 20, 0, 0, 20, 0, 4, 0, 0, 4, 0, 20, 312, 0, 316, 0, 0, 0, 316, 0, 316, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 20, 0, 0};
    }

    public Parser(CharStream stream) {
        int i;
        this.token_source = new ParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 69; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(CharStream stream) {
        int i;
        this.token_source.ReInit(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (i = 0; i < 69; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public Parser(ParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 69; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(ParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (i = 0; i < 69; ++i) {
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
            Iterator it = this.jj_expentries.iterator();
            block1: while (it.hasNext()) {
                int[] oldentry = (int[])it.next();
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
        boolean[] la1tokens = new boolean[73];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 69; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) != 0) {
                    la1tokens[32 + j] = true;
                }
                if ((jj_la1_2[i] & 1 << j) == 0) continue;
                la1tokens[64 + j] = true;
            }
        }
        for (i = 0; i < 73; ++i) {
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
            exptokseq[i2] = (int[])this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 12; ++i) {
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
                        }
                    }
                } while ((p = p.next) != null);
                continue;
            }
            catch (LookaheadSuccess lookaheadSuccess) {
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
        Parser.jj_la1_init_0();
        Parser.jj_la1_init_1();
        Parser.jj_la1_init_2();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }

    private static final class LookaheadSuccess
    extends Error {
        private LookaheadSuccess() {
        }
    }
}

