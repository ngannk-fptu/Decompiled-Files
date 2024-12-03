/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
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
import org.apache.velocity.runtime.parser.node.ASTStop;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.ASTSubtractNode;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTTrue;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class Parser
implements ParserTreeConstants,
ParserConstants {
    protected JJTParserState jjtree = new JJTParserState();
    private Hashtable directives = new Hashtable(0);
    public String currentTemplateName = "";
    VelocityCharStream velcharstream = null;
    private RuntimeServices rsvc = null;
    public ParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    public boolean lookingAhead = false;
    private boolean jj_semLA;
    private int jj_gen;
    private final int[] jj_la1 = new int[62];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private final JJCalls[] jj_2_rtns = new JJCalls[12];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public Parser(RuntimeServices rs) {
        this(new VelocityCharStream(new ByteArrayInputStream("\n".getBytes()), 1, 1));
        this.velcharstream = new VelocityCharStream(new ByteArrayInputStream("\n".getBytes()), 1, 1);
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

    public void setDirectives(Hashtable directives) {
        this.directives = directives;
    }

    public Directive getDirective(String directive) {
        return (Directive)this.directives.get(directive);
    }

    public boolean isDirective(String directive) {
        return this.directives.containsKey(directive);
    }

    private String escapedDirective(String strImage) {
        int iLast = strImage.lastIndexOf("\\");
        String strDirective = strImage.substring(iLast + 1);
        boolean bRecognizedDirective = false;
        String dirTag = strDirective.substring(1);
        if (dirTag.charAt(0) == '{') {
            dirTag = dirTag.substring(1, dirTag.length() - 1);
        }
        if (this.isDirective(dirTag)) {
            bRecognizedDirective = true;
        } else if (this.rsvc.isVelocimacro(dirTag, this.currentTemplateName)) {
            bRecognizedDirective = true;
        } else if (dirTag.equals("if") || dirTag.equals("end") || dirTag.equals("set") || dirTag.equals("else") || dirTag.equals("elseif") || dirTag.equals("stop")) {
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
                    case 8: 
                    case 9: 
                    case 11: 
                    case 12: 
                    case 18: 
                    case 19: 
                    case 20: 
                    case 21: 
                    case 23: 
                    case 24: 
                    case 27: 
                    case 47: 
                    case 50: 
                    case 52: 
                    case 53: 
                    case 57: 
                    case 58: 
                    case 62: 
                    case 63: 
                    case 64: 
                    case 65: {
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
            case 47: {
                this.IfStatement();
                break;
            }
            case 50: {
                this.StopStatement();
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
                if (this.jj_2_1(2)) {
                    this.Reference();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: 
                    case 23: 
                    case 24: {
                        this.Comment();
                        break block0;
                    }
                    case 12: {
                        this.SetDirective();
                        break block0;
                    }
                    case 11: {
                        this.EscapedDirective();
                        break block0;
                    }
                    case 19: {
                        this.Escape();
                        break block0;
                    }
                    case 57: 
                    case 58: {
                        this.Directive();
                        break block0;
                    }
                    case 8: 
                    case 9: 
                    case 20: 
                    case 21: 
                    case 27: 
                    case 52: 
                    case 53: 
                    case 63: 
                    case 64: 
                    case 65: {
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
            t = this.jj_consume_token(11);
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
                t = this.jj_consume_token(19);
                ++count;
            } while (this.jj_2_2(2));
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            switch (t.next.kind) {
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: {
                    control = true;
                }
            }
            String nTag = t.next.image.substring(1);
            if (this.isDirective(nTag)) {
                control = true;
            } else if (this.rsvc.isVelocimacro(nTag, this.currentTemplateName)) {
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Comment() throws ParseException {
        ASTComment jjtn000 = new ASTComment(this, 4);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: {
                    this.jj_consume_token(18);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 22: {
                            this.jj_consume_token(22);
                            return;
                        }
                    }
                    this.jj_la1[3] = this.jj_gen;
                    return;
                }
                case 24: {
                    this.jj_consume_token(24);
                    return;
                }
                case 23: {
                    this.jj_consume_token(23);
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

    public final void FloatingPointLiteral() throws ParseException {
        ASTFloatingPointLiteral jjtn000 = new ASTFloatingPointLiteral(this, 5);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(53);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void IntegerLiteral() throws ParseException {
        ASTIntegerLiteral jjtn000 = new ASTIntegerLiteral(this, 6);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(52);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void StringLiteral() throws ParseException {
        ASTStringLiteral jjtn000 = new ASTStringLiteral(this, 7);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(27);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Identifier() throws ParseException {
        ASTIdentifier jjtn000 = new ASTIdentifier(this, 8);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(62);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Word() throws ParseException {
        ASTWord jjtn000 = new ASTWord(this, 9);
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

    public final int DirectiveArg() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 62: 
            case 64: {
                this.Reference();
                return 16;
            }
            case 57: {
                this.Word();
                return 9;
            }
            case 27: {
                this.StringLiteral();
                return 7;
            }
            case 52: {
                this.IntegerLiteral();
                return 6;
            }
        }
        this.jj_la1[5] = this.jj_gen;
        if (this.jj_2_3(Integer.MAX_VALUE)) {
            this.IntegerRange();
            return 14;
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 53: {
                this.FloatingPointLiteral();
                return 5;
            }
            case 6: {
                this.Map();
                return 12;
            }
            case 1: {
                this.ObjectArray();
                return 13;
            }
            case 28: {
                this.True();
                return 17;
            }
            case 29: {
                this.False();
                return 18;
            }
        }
        this.jj_la1[6] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final SimpleNode Directive() throws ParseException {
        ASTDirective jjtn000 = new ASTDirective(this, 10);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        int argPos = 0;
        boolean isVM = false;
        boolean doItNow = false;
        try {
            block60: {
                int directiveType;
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 57: {
                        t = this.jj_consume_token(57);
                        break;
                    }
                    case 58: {
                        t = this.jj_consume_token(58);
                        break;
                    }
                    default: {
                        this.jj_la1[7] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                String directiveName = t.kind == 58 ? t.image.substring(2, t.image.length() - 1) : t.image.substring(1);
                Directive d = (Directive)this.directives.get(directiveName);
                if (directiveName.equals("macro")) {
                    doItNow = true;
                }
                jjtn000.setDirectiveName(directiveName);
                if (d == null) {
                    isVM = this.rsvc.isVelocimacro(directiveName, this.currentTemplateName);
                    directiveType = 2;
                } else {
                    directiveType = d.getType();
                }
                this.token_source.SwitchTo(0);
                argPos = 0;
                if (this.isLeftParenthesis()) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 26: {
                            this.jj_consume_token(26);
                            break;
                        }
                        default: {
                            this.jj_la1[8] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(8);
                    while (this.jj_2_4(2)) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 26: {
                                this.jj_consume_token(26);
                                break;
                            }
                            default: {
                                this.jj_la1[9] = this.jj_gen;
                            }
                        }
                        block16 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 3: {
                                this.jj_consume_token(3);
                                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                    case 26: {
                                        this.jj_consume_token(26);
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
                        if (argType == 9) {
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
                        case 26: {
                            this.jj_consume_token(26);
                            break;
                        }
                        default: {
                            this.jj_la1[12] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(9);
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
                ASTBlock jjtn001 = new ASTBlock(this, 11);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 8: 
                            case 9: 
                            case 11: 
                            case 12: 
                            case 18: 
                            case 19: 
                            case 20: 
                            case 21: 
                            case 23: 
                            case 24: 
                            case 27: 
                            case 47: 
                            case 50: 
                            case 52: 
                            case 53: 
                            case 57: 
                            case 58: 
                            case 62: 
                            case 63: 
                            case 64: 
                            case 65: {
                                break;
                            }
                            default: {
                                this.jj_la1[13] = this.jj_gen;
                                break block60;
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
            this.jj_consume_token(46);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            if (doItNow) {
                Macro.processAndRegister(this.rsvc, t, jjtn000, this.currentTemplateName);
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
        ASTMap jjtn000 = new ASTMap(this, 12);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block21: {
                this.jj_consume_token(6);
                if (this.jj_2_5(2)) {
                    this.Parameter();
                    this.jj_consume_token(5);
                    this.Parameter();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 3: {
                                break;
                            }
                            default: {
                                this.jj_la1[14] = this.jj_gen;
                                break block21;
                            }
                        }
                        this.jj_consume_token(3);
                        this.Parameter();
                        this.jj_consume_token(5);
                        this.Parameter();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 26: {
                        this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                    }
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: {
                    this.jj_consume_token(7);
                    return;
                }
                case 65: {
                    this.jj_consume_token(65);
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
        ASTObjectArray jjtn000 = new ASTObjectArray(this, 13);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(1);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 6: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 52: 
                case 53: 
                case 62: 
                case 64: {
                    this.Parameter();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 3: {
                                break;
                            }
                            default: {
                                this.jj_la1[17] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(3);
                        this.Parameter();
                    }
                }
                default: {
                    this.jj_la1[18] = this.jj_gen;
                }
            }
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

    public final void IntegerRange() throws ParseException {
        ASTIntegerRange jjtn000 = new ASTIntegerRange(this, 14);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(1);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 26: {
                    this.jj_consume_token(26);
                    break;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 62: 
                case 64: {
                    this.Reference();
                    break;
                }
                case 52: {
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
                case 26: {
                    this.jj_consume_token(26);
                    break;
                }
                default: {
                    this.jj_la1[21] = this.jj_gen;
                }
            }
            this.jj_consume_token(4);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 26: {
                    this.jj_consume_token(26);
                    break;
                }
                default: {
                    this.jj_la1[22] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 62: 
                case 64: {
                    this.Reference();
                    break;
                }
                case 52: {
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
                case 26: {
                    this.jj_consume_token(26);
                    break;
                }
                default: {
                    this.jj_la1[24] = this.jj_gen;
                }
            }
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

    public final void Parameter() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 26: {
                this.jj_consume_token(26);
                break;
            }
            default: {
                this.jj_la1[25] = this.jj_gen;
            }
        }
        block3 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 27: {
                this.StringLiteral();
                break;
            }
            case 52: {
                this.IntegerLiteral();
                break;
            }
            default: {
                this.jj_la1[26] = this.jj_gen;
                if (this.jj_2_6(Integer.MAX_VALUE)) {
                    this.IntegerRange();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 6: {
                        this.Map();
                        break block3;
                    }
                    case 1: {
                        this.ObjectArray();
                        break block3;
                    }
                    case 28: {
                        this.True();
                        break block3;
                    }
                    case 29: {
                        this.False();
                        break block3;
                    }
                    case 62: 
                    case 64: {
                        this.Reference();
                        break block3;
                    }
                    case 53: {
                        this.FloatingPointLiteral();
                        break block3;
                    }
                }
                this.jj_la1[27] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 26: {
                this.jj_consume_token(26);
                break;
            }
            default: {
                this.jj_la1[28] = this.jj_gen;
            }
        }
    }

    public final void Method() throws ParseException {
        ASTMethod jjtn000 = new ASTMethod(this, 15);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Identifier();
            this.jj_consume_token(8);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 6: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 52: 
                case 53: 
                case 62: 
                case 64: {
                    this.Parameter();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 3: {
                                break;
                            }
                            default: {
                                this.jj_la1[29] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(3);
                        this.Parameter();
                    }
                }
                default: {
                    this.jj_la1[30] = this.jj_gen;
                }
            }
            this.jj_consume_token(10);
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
        ASTReference jjtn000 = new ASTReference(this, 16);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 62: {
                    this.jj_consume_token(62);
                    block15: while (this.jj_2_7(2)) {
                        this.jj_consume_token(63);
                        if (this.jj_2_8(3)) {
                            this.Method();
                            continue;
                        }
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 62: {
                                this.Identifier();
                                continue block15;
                            }
                        }
                        this.jj_la1[31] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                    return;
                }
                case 64: {
                    this.jj_consume_token(64);
                    this.jj_consume_token(62);
                    block16: while (this.jj_2_9(2)) {
                        this.jj_consume_token(63);
                        if (this.jj_2_10(3)) {
                            this.Method();
                            continue;
                        }
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 62: {
                                this.Identifier();
                                continue block16;
                            }
                        }
                        this.jj_la1[32] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                    this.jj_consume_token(65);
                    return;
                }
                default: {
                    this.jj_la1[33] = this.jj_gen;
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

    public final void True() throws ParseException {
        ASTTrue jjtn000 = new ASTTrue(this, 17);
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

    public final void False() throws ParseException {
        ASTFalse jjtn000 = new ASTFalse(this, 18);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(29);
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
    public final void Text() throws ParseException {
        ASTText jjtn000 = new ASTText(this, 19);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 21: {
                    this.jj_consume_token(21);
                    return;
                }
                case 63: {
                    this.jj_consume_token(63);
                    return;
                }
                case 9: {
                    this.jj_consume_token(9);
                    return;
                }
                case 8: {
                    this.jj_consume_token(8);
                    return;
                }
                case 52: {
                    this.jj_consume_token(52);
                    return;
                }
                case 53: {
                    this.jj_consume_token(53);
                    return;
                }
                case 27: {
                    this.jj_consume_token(27);
                    return;
                }
                case 20: {
                    this.jj_consume_token(20);
                    return;
                }
                case 64: {
                    this.jj_consume_token(64);
                    return;
                }
                case 65: {
                    this.jj_consume_token(65);
                    return;
                }
                default: {
                    this.jj_la1[34] = this.jj_gen;
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
        ASTIfStatement jjtn000 = new ASTIfStatement(this, 20);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block37: {
                this.jj_consume_token(47);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 26: {
                        this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[35] = this.jj_gen;
                    }
                }
                this.jj_consume_token(8);
                this.Expression();
                this.jj_consume_token(9);
                ASTBlock jjtn001 = new ASTBlock(this, 11);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 8: 
                            case 9: 
                            case 11: 
                            case 12: 
                            case 18: 
                            case 19: 
                            case 20: 
                            case 21: 
                            case 23: 
                            case 24: 
                            case 27: 
                            case 47: 
                            case 50: 
                            case 52: 
                            case 53: 
                            case 57: 
                            case 58: 
                            case 62: 
                            case 63: 
                            case 64: 
                            case 65: {
                                break;
                            }
                            default: {
                                this.jj_la1[36] = this.jj_gen;
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
                case 48: {
                    block26: while (true) {
                        this.ElseIfStatement();
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 48: {
                                continue block26;
                            }
                        }
                        break;
                    }
                    this.jj_la1[37] = this.jj_gen;
                    break;
                }
                default: {
                    this.jj_la1[38] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 49: {
                    this.ElseStatement();
                    break;
                }
                default: {
                    this.jj_la1[39] = this.jj_gen;
                }
            }
            this.jj_consume_token(46);
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
            ASTElseStatement jjtn000 = new ASTElseStatement(this, 21);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(49);
                ASTBlock jjtn001 = new ASTBlock(this, 11);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 8: 
                            case 9: 
                            case 11: 
                            case 12: 
                            case 18: 
                            case 19: 
                            case 20: 
                            case 21: 
                            case 23: 
                            case 24: 
                            case 27: 
                            case 47: 
                            case 50: 
                            case 52: 
                            case 53: 
                            case 57: 
                            case 58: 
                            case 62: 
                            case 63: 
                            case 64: 
                            case 65: {
                                break;
                            }
                            default: {
                                this.jj_la1[40] = this.jj_gen;
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
            ASTElseIfStatement jjtn000 = new ASTElseIfStatement(this, 22);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(48);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 26: {
                        this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[41] = this.jj_gen;
                    }
                }
                this.jj_consume_token(8);
                this.Expression();
                this.jj_consume_token(9);
                ASTBlock jjtn001 = new ASTBlock(this, 11);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 8: 
                            case 9: 
                            case 11: 
                            case 12: 
                            case 18: 
                            case 19: 
                            case 20: 
                            case 21: 
                            case 23: 
                            case 24: 
                            case 27: 
                            case 47: 
                            case 50: 
                            case 52: 
                            case 53: 
                            case 57: 
                            case 58: 
                            case 62: 
                            case 63: 
                            case 64: 
                            case 65: {
                                break;
                            }
                            default: {
                                this.jj_la1[42] = this.jj_gen;
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
        ASTSetDirective jjtn000 = new ASTSetDirective(this, 23);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(12);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 26: {
                    this.jj_consume_token(26);
                    break;
                }
                default: {
                    this.jj_la1[43] = this.jj_gen;
                }
            }
            this.Reference();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 26: {
                    this.jj_consume_token(26);
                    break;
                }
                default: {
                    this.jj_la1[44] = this.jj_gen;
                }
            }
            this.jj_consume_token(45);
            this.Expression();
            this.jj_consume_token(9);
            this.token_source.inSet = false;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 30: {
                    this.jj_consume_token(30);
                    return;
                }
                default: {
                    this.jj_la1[45] = this.jj_gen;
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

    public final void StopStatement() throws ParseException {
        ASTStop jjtn000 = new ASTStop(this, 24);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(50);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, 0);
            }
        }
    }

    public final void Expression() throws ParseException {
        ASTExpression jjtn000 = new ASTExpression(this, 25);
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
        ASTAssignment jjtn000 = new ASTAssignment(this, 26);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.PrimaryExpression();
            this.jj_consume_token(45);
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
                case 37: {
                    break;
                }
                default: {
                    this.jj_la1[46] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(37);
            ASTOrNode jjtn001 = new ASTOrNode(this, 27);
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
                case 36: {
                    break;
                }
                default: {
                    this.jj_la1[47] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(36);
            ASTAndNode jjtn001 = new ASTAndNode(this, 28);
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
                    case 42: 
                    case 43: {
                        break;
                    }
                    default: {
                        this.jj_la1[48] = this.jj_gen;
                        break block26;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 42: {
                        this.jj_consume_token(42);
                        ASTEQNode jjtn001 = new ASTEQNode(this, 29);
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
                    case 43: {
                        this.jj_consume_token(43);
                        ASTNENode jjtn002 = new ASTNENode(this, 30);
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
            this.jj_la1[49] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void RelationalExpression() throws ParseException {
        block46: {
            this.AdditiveExpression();
            block29: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: {
                        break;
                    }
                    default: {
                        this.jj_la1[50] = this.jj_gen;
                        break block46;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 38: {
                        this.jj_consume_token(38);
                        ASTLTNode jjtn001 = new ASTLTNode(this, 31);
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
                    case 40: {
                        this.jj_consume_token(40);
                        ASTGTNode jjtn002 = new ASTGTNode(this, 32);
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
                    case 39: {
                        this.jj_consume_token(39);
                        ASTLENode jjtn003 = new ASTLENode(this, 33);
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
                    case 41: {
                        this.jj_consume_token(41);
                        ASTGENode jjtn004 = new ASTGENode(this, 34);
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
            this.jj_la1[51] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void AdditiveExpression() throws ParseException {
        block26: {
            this.MultiplicativeExpression();
            block17: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 31: 
                    case 32: {
                        break;
                    }
                    default: {
                        this.jj_la1[52] = this.jj_gen;
                        break block26;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 32: {
                        this.jj_consume_token(32);
                        ASTAddNode jjtn001 = new ASTAddNode(this, 35);
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
                    case 31: {
                        this.jj_consume_token(31);
                        ASTSubtractNode jjtn002 = new ASTSubtractNode(this, 36);
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
            this.jj_la1[53] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void MultiplicativeExpression() throws ParseException {
        block36: {
            this.UnaryExpression();
            block23: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 33: 
                    case 34: 
                    case 35: {
                        break;
                    }
                    default: {
                        this.jj_la1[54] = this.jj_gen;
                        break block36;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 33: {
                        this.jj_consume_token(33);
                        ASTMulNode jjtn001 = new ASTMulNode(this, 37);
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
                    case 34: {
                        this.jj_consume_token(34);
                        ASTDivNode jjtn002 = new ASTDivNode(this, 38);
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
                    case 35: {
                        this.jj_consume_token(35);
                        ASTModNode jjtn003 = new ASTModNode(this, 39);
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
            this.jj_la1[55] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void UnaryExpression() throws ParseException {
        if (this.jj_2_11(2)) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 26: {
                    this.jj_consume_token(26);
                    break;
                }
                default: {
                    this.jj_la1[56] = this.jj_gen;
                }
            }
            this.jj_consume_token(44);
            ASTNotNode jjtn001 = new ASTNotNode(this, 40);
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
                case 1: 
                case 6: 
                case 8: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 52: 
                case 53: 
                case 62: 
                case 64: {
                    this.PrimaryExpression();
                    break;
                }
                default: {
                    this.jj_la1[57] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
    }

    public final void PrimaryExpression() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 26: {
                this.jj_consume_token(26);
                break;
            }
            default: {
                this.jj_la1[58] = this.jj_gen;
            }
        }
        block3 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 27: {
                this.StringLiteral();
                break;
            }
            case 62: 
            case 64: {
                this.Reference();
                break;
            }
            case 52: {
                this.IntegerLiteral();
                break;
            }
            default: {
                this.jj_la1[59] = this.jj_gen;
                if (this.jj_2_12(Integer.MAX_VALUE)) {
                    this.IntegerRange();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 53: {
                        this.FloatingPointLiteral();
                        break block3;
                    }
                    case 6: {
                        this.Map();
                        break block3;
                    }
                    case 1: {
                        this.ObjectArray();
                        break block3;
                    }
                    case 28: {
                        this.True();
                        break block3;
                    }
                    case 29: {
                        this.False();
                        break block3;
                    }
                    case 8: {
                        this.jj_consume_token(8);
                        this.Expression();
                        this.jj_consume_token(9);
                        break block3;
                    }
                }
                this.jj_la1[60] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 26: {
                this.jj_consume_token(26);
                break;
            }
            default: {
                this.jj_la1[61] = this.jj_gen;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final boolean jj_2_1(int xla) {
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
    private final boolean jj_2_2(int xla) {
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
    private final boolean jj_2_3(int xla) {
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
    private final boolean jj_2_4(int xla) {
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
    private final boolean jj_2_5(int xla) {
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
    private final boolean jj_2_6(int xla) {
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
    private final boolean jj_2_7(int xla) {
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
    private final boolean jj_2_8(int xla) {
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
    private final boolean jj_2_9(int xla) {
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
    private final boolean jj_2_10(int xla) {
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
    private final boolean jj_2_11(int xla) {
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
    private final boolean jj_2_12(int xla) {
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

    private final boolean jj_3_1() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_21() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_64() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_71()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(2);
    }

    private final boolean jj_3R_70() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private final boolean jj_3_5() {
        Token xsp;
        if (this.jj_3R_25()) {
            return true;
        }
        if (this.jj_scan_token(5)) {
            return true;
        }
        if (this.jj_3R_25()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_85());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_63() {
        if (this.jj_scan_token(6)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_5()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_70()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(7)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(65)) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_46() {
        return this.jj_3R_66();
    }

    private final boolean jj_3R_45() {
        return this.jj_3R_65();
    }

    private final boolean jj_3_3() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_21()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_22()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(4);
    }

    private final boolean jj_3R_44() {
        return this.jj_3R_64();
    }

    private final boolean jj_3R_43() {
        return this.jj_3R_63();
    }

    private final boolean jj_3R_42() {
        return this.jj_3R_62();
    }

    private final boolean jj_3R_41() {
        return this.jj_3R_61();
    }

    private final boolean jj_3R_40() {
        return this.jj_3R_36();
    }

    private final boolean jj_3R_39() {
        return this.jj_3R_60();
    }

    private final boolean jj_3R_38() {
        return this.jj_3R_59();
    }

    private final boolean jj_3R_23() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private final boolean jj_3R_37() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_24() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_37()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_38()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_39()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_40()) {
                        this.jj_scanpos = xsp;
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

    private final boolean jj_3R_59() {
        return this.jj_scan_token(57);
    }

    private final boolean jj_3R_56() {
        return this.jj_scan_token(62);
    }

    private final boolean jj_3R_30() {
        return this.jj_3R_56();
    }

    private final boolean jj_3R_28() {
        return this.jj_3R_56();
    }

    private final boolean jj_3R_33() {
        return this.jj_3R_36();
    }

    private final boolean jj_3_4() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_23()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_3R_24();
    }

    private final boolean jj_3R_60() {
        return this.jj_scan_token(27);
    }

    private final boolean jj_3R_36() {
        return this.jj_scan_token(52);
    }

    private final boolean jj_3R_32() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_27() {
        return this.jj_3R_36();
    }

    private final boolean jj_3R_62() {
        return this.jj_scan_token(53);
    }

    private final boolean jj_3_10() {
        return this.jj_3R_29();
    }

    private final boolean jj_3R_82() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        return this.jj_3R_25();
    }

    private final boolean jj_3_8() {
        return this.jj_3R_29();
    }

    private final boolean jj_3R_26() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_66() {
        return this.jj_scan_token(29);
    }

    private final boolean jj_3R_65() {
        return this.jj_scan_token(28);
    }

    private final boolean jj_3_9() {
        if (this.jj_scan_token(63)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_10()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_30()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_57() {
        Token xsp;
        if (this.jj_3R_25()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_82());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3_7() {
        if (this.jj_scan_token(63)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_8()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_28()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_35() {
        Token xsp;
        if (this.jj_scan_token(64)) {
            return true;
        }
        if (this.jj_scan_token(62)) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3_9());
        this.jj_scanpos = xsp;
        return this.jj_scan_token(65);
    }

    private final boolean jj_3_12() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_32()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_33()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(4);
    }

    private final boolean jj_3R_34() {
        Token xsp;
        if (this.jj_scan_token(62)) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3_7());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_81() {
        return this.jj_scan_token(8);
    }

    private final boolean jj_3R_80() {
        return this.jj_3R_66();
    }

    private final boolean jj_3R_79() {
        return this.jj_3R_65();
    }

    private final boolean jj_3R_20() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_34()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_35()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3R_78() {
        return this.jj_3R_64();
    }

    private final boolean jj_3R_77() {
        return this.jj_3R_63();
    }

    private final boolean jj_3R_76() {
        return this.jj_3R_62();
    }

    private final boolean jj_3R_75() {
        return this.jj_3R_61();
    }

    private final boolean jj_3R_74() {
        return this.jj_3R_36();
    }

    private final boolean jj_3R_73() {
        return this.jj_3R_20();
    }

    private final boolean jj_3_6() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_26()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_27()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(4);
    }

    private final boolean jj_3_2() {
        return this.jj_scan_token(19);
    }

    private final boolean jj_3R_29() {
        if (this.jj_3R_56()) {
            return true;
        }
        if (this.jj_scan_token(8)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_57()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(10);
    }

    private final boolean jj_3R_72() {
        return this.jj_3R_60();
    }

    private final boolean jj_3R_67() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_72()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_73()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_74()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_75()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_76()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_77()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_78()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_79()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_80()) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_3R_81()) {
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

    private final boolean jj_3R_55() {
        return this.jj_3R_62();
    }

    private final boolean jj_3R_54() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_53() {
        return this.jj_3R_66();
    }

    private final boolean jj_3R_52() {
        return this.jj_3R_65();
    }

    private final boolean jj_3R_31() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_11()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_58()) {
                return true;
            }
        }
        return false;
    }

    private final boolean jj_3_11() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(44)) {
            return true;
        }
        return this.jj_3R_31();
    }

    private final boolean jj_3R_58() {
        return this.jj_3R_67();
    }

    private final boolean jj_3R_51() {
        return this.jj_3R_64();
    }

    private final boolean jj_3R_85() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        if (this.jj_3R_25()) {
            return true;
        }
        if (this.jj_scan_token(5)) {
            return true;
        }
        return this.jj_3R_25();
    }

    private final boolean jj_3R_50() {
        return this.jj_3R_63();
    }

    private final boolean jj_3R_49() {
        return this.jj_3R_61();
    }

    private final boolean jj_3R_48() {
        return this.jj_3R_36();
    }

    private final boolean jj_3R_47() {
        return this.jj_3R_60();
    }

    private final boolean jj_3R_84() {
        return this.jj_3R_36();
    }

    private final boolean jj_3R_69() {
        return this.jj_3R_36();
    }

    private final boolean jj_3R_86() {
        if (this.jj_scan_token(3)) {
            return true;
        }
        return this.jj_3R_25();
    }

    private final boolean jj_3R_25() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_47()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_48()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_49()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_50()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_51()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_52()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_53()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_54()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_55()) {
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
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private final boolean jj_3R_83() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_22() {
        return this.jj_3R_36();
    }

    private final boolean jj_3R_68() {
        return this.jj_3R_20();
    }

    private final boolean jj_3R_71() {
        Token xsp;
        if (this.jj_3R_25()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_86());
        this.jj_scanpos = xsp;
        return false;
    }

    private final boolean jj_3R_61() {
        if (this.jj_scan_token(1)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_68()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_69()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(4)) {
            return true;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_83()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_84()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(26)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(2);
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{163322624, 0, 163322624, 0x400000, 25427968, 0x8000000, 805306434, 0, 0x4000000, 0x4000000, 0x4000000, 8, 0x4000000, 163322624, 8, 0x4000000, 128, 8, 1006633026, 0x4000000, 0, 0x4000000, 0x4000000, 0, 0x4000000, 0x4000000, 0x8000000, 805306434, 0x4000000, 8, 1006633026, 0, 0, 0, 0x8300300, 0x4000000, 163322624, 0, 0, 0, 163322624, 0x4000000, 163322624, 0x4000000, 0x4000000, 0x40000000, 0, 0, 0, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, 0x4000000, 1006633282, 0x4000000, 0x8000000, 805306690, 0x4000000};
    }

    private static void jj_la1_1() {
        jj_la1_1 = new int[]{-969637888, 294912, -2043674624, 0, 0, 1108344832, 0x200000, 0x6000000, 0, 0, 0, 0, 0, -969637888, 0, 0, 0, 0, 0x40300000, 0, 0x40100000, 0, 0, 0x40100000, 0, 0, 0x100000, 0x40200000, 0, 0, 0x40300000, 0x40000000, 0x40000000, 0x40000000, -2144337920, 0, -969637888, 65536, 65536, 131072, -969637888, 0, -969637888, 0, 0, 0, 32, 16, 3072, 3072, 960, 960, 1, 1, 14, 14, 0, 0x40300000, 0, 0x40100000, 0x200000, 0};
    }

    private static void jj_la1_2() {
        jj_la1_2 = new int[]{3, 0, 3, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 2, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 3, 0, 3, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0};
    }

    public Parser(CharStream stream) {
        int i;
        this.token_source = new ParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 62; ++i) {
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
        for (i = 0; i < 62; ++i) {
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
        for (i = 0; i < 62; ++i) {
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
        for (i = 0; i < 62; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    private final Token jj_consume_token(int kind) throws ParseException {
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

    private final boolean jj_scan_token(int kind) {
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
        Token t = this.lookingAhead ? this.jj_scanpos : this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private final int jj_ntk() {
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
            boolean exists = false;
            Enumeration e = this.jj_expentries.elements();
            while (e.hasMoreElements()) {
                int[] oldentry = (int[])e.nextElement();
                if (oldentry.length != this.jj_expentry.length) continue;
                exists = true;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] == this.jj_expentry[i]) continue;
                    exists = false;
                    break;
                }
                if (!exists) continue;
                break;
            }
            if (!exists) {
                this.jj_expentries.addElement(this.jj_expentry);
            }
            if (pos != 0) {
                this.jj_endpos = pos;
                this.jj_lasttokens[this.jj_endpos - 1] = kind;
            }
        }
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.removeAllElements();
        boolean[] la1tokens = new boolean[68];
        for (i = 0; i < 68; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 62; ++i) {
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
        for (i = 0; i < 68; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.addElement(this.jj_expentry);
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = (int[])this.jj_expentries.elementAt(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private final void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 12; ++i) {
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
        }
        this.jj_rescan = false;
    }

    private final void jj_save(int index, int xla) {
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
        Parser.jj_la1_0();
        Parser.jj_la1_1();
        Parser.jj_la1_2();
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

