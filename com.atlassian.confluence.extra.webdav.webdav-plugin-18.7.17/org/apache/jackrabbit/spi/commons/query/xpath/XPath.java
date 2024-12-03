/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.xpath;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;
import org.apache.jackrabbit.spi.commons.query.xpath.JJTXPathState;
import org.apache.jackrabbit.spi.commons.query.xpath.Node;
import org.apache.jackrabbit.spi.commons.query.xpath.ParseException;
import org.apache.jackrabbit.spi.commons.query.xpath.SimpleCharStream;
import org.apache.jackrabbit.spi.commons.query.xpath.SimpleNode;
import org.apache.jackrabbit.spi.commons.query.xpath.Token;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathConstants;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathTokenManager;
import org.apache.jackrabbit.spi.commons.query.xpath.XPathTreeConstants;

public class XPath
implements XPathTreeConstants,
XPathConstants {
    protected JJTXPathState jjtree = new JJTXPathState();
    boolean m_isMatchPattern = false;
    boolean isStep = false;
    Stack binaryTokenStack = new Stack();
    public XPathTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[166];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private static int[] jj_la1_3;
    private static int[] jj_la1_4;
    private static int[] jj_la1_5;
    private static int[] jj_la1_6;
    private static int[] jj_la1_7;
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public Node createNode(int id) {
        return null;
    }

    public static void main(String[] args) throws Exception {
        int numberArgsLeft = args.length;
        int argsStart = 0;
        boolean isMatchParser = false;
        if (numberArgsLeft > 0 && args[argsStart].equals("-match")) {
            isMatchParser = true;
            System.out.println("Match Pattern Parser");
            ++argsStart;
            --numberArgsLeft;
        }
        if (numberArgsLeft > 0) {
            try {
                boolean dumpTree = true;
                if (args[0].endsWith(".xquery")) {
                    System.out.println("Running test for: " + args[0]);
                    File file = new File(args[0]);
                    FileInputStream fis = new FileInputStream(file);
                    XPath parser = new XPath(fis);
                    SimpleNode tree = parser.XPath2();
                    tree.dump("|");
                } else {
                    for (int i = argsStart; i < args.length; ++i) {
                        System.out.println();
                        System.out.println("Test[" + i + "]: " + args[i]);
                        XPath parser = new XPath(new StringBufferInputStream(args[i]));
                        SimpleNode tree = isMatchParser ? parser.XPath2() : parser.XPath2();
                        ((SimpleNode)tree.jjtGetChild(0)).dump("|");
                    }
                    System.out.println("Success!!!!");
                }
            }
            catch (ParseException pe) {
                System.err.println(pe.getMessage());
            }
            return;
        }
        DataInputStream dinput = new DataInputStream(System.in);
        block6: while (true) {
            try {
                while (true) {
                    System.err.println("Type Expression: ");
                    String input = dinput.readLine();
                    if (null == input || input.trim().length() == 0) break block6;
                    XPath parser = new XPath(new StringBufferInputStream(input));
                    SimpleNode tree = isMatchParser ? parser.XPath2() : parser.XPath2();
                    ((SimpleNode)tree.jjtGetChild(0)).dump("|");
                }
            }
            catch (ParseException pe) {
                System.err.println(pe.getMessage());
                continue;
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
            }
            break;
        }
    }

    public final SimpleNode XPath2() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 0);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.QueryList();
            this.jj_consume_token(0);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            SimpleNode simpleNode = jjtn000;
            return simpleNode;
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

    public final void QueryList() throws ParseException {
        block17: {
            SimpleNode jjtn000 = new SimpleNode(this, 1);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.Module();
                block11: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 171: {
                            break;
                        }
                        default: {
                            this.jj_la1[0] = this.jj_gen;
                            break block17;
                        }
                    }
                    this.jj_consume_token(171);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 1: 
                        case 2: 
                        case 3: 
                        case 4: 
                        case 6: 
                        case 11: 
                        case 15: 
                        case 16: 
                        case 18: 
                        case 19: 
                        case 20: 
                        case 21: 
                        case 22: 
                        case 23: 
                        case 24: 
                        case 25: 
                        case 26: 
                        case 27: 
                        case 28: 
                        case 29: 
                        case 30: 
                        case 31: 
                        case 34: 
                        case 35: 
                        case 49: 
                        case 54: 
                        case 60: 
                        case 61: 
                        case 65: 
                        case 78: 
                        case 79: 
                        case 80: 
                        case 81: 
                        case 82: 
                        case 83: 
                        case 84: 
                        case 85: 
                        case 86: 
                        case 87: 
                        case 88: 
                        case 89: 
                        case 90: 
                        case 91: 
                        case 92: 
                        case 94: 
                        case 95: 
                        case 97: 
                        case 98: 
                        case 101: 
                        case 103: 
                        case 104: 
                        case 105: 
                        case 106: 
                        case 128: 
                        case 129: 
                        case 134: 
                        case 135: 
                        case 140: 
                        case 141: 
                        case 142: 
                        case 143: 
                        case 146: 
                        case 147: 
                        case 149: 
                        case 150: 
                        case 151: 
                        case 152: 
                        case 153: 
                        case 154: 
                        case 155: 
                        case 156: 
                        case 157: 
                        case 158: 
                        case 159: 
                        case 160: 
                        case 161: 
                        case 162: 
                        case 163: 
                        case 164: 
                        case 165: 
                        case 166: 
                        case 167: 
                        case 174: 
                        case 175: 
                        case 182: 
                        case 187: 
                        case 196: 
                        case 197: 
                        case 232: 
                        case 233: 
                        case 235: {
                            this.Module();
                            continue block11;
                        }
                    }
                    this.jj_la1[1] = this.jj_gen;
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
    public final void Module() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 2);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 6: {
                    this.VersionDecl();
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 15: 
                case 16: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 34: 
                case 35: 
                case 49: 
                case 54: 
                case 60: 
                case 61: 
                case 65: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 94: 
                case 95: 
                case 97: 
                case 98: 
                case 101: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 128: 
                case 129: 
                case 134: 
                case 135: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 146: 
                case 147: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 174: 
                case 175: 
                case 182: 
                case 187: 
                case 196: 
                case 197: 
                case 232: 
                case 233: 
                case 235: {
                    this.MainModule();
                    return;
                }
                case 11: {
                    this.LibraryModule();
                    return;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
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
    public final void VersionDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 3);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(6);
            SimpleNode jjtn001 = new SimpleNode(this, 4);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(7);
            SimpleNode jjtn002 = new SimpleNode(this, 5);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    this.jj_consume_token(8);
                    SimpleNode jjtn003 = new SimpleNode(this, 6);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                    this.jj_consume_token(7);
                    SimpleNode jjtn004 = new SimpleNode(this, 5);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                }
            }
            this.Separator();
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

    public final void MainModule() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 7);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Prolog();
            this.QueryBody();
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

    public final void LibraryModule() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 8);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.ModuleDecl();
            this.Prolog();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ModuleDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 9);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(11);
            SimpleNode jjtn001 = new SimpleNode(this, 10);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(188);
            SimpleNode jjtn002 = new SimpleNode(this, 11);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                }
            }
            this.jj_consume_token(110);
            SimpleNode jjtn003 = new SimpleNode(this, 12);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                }
            }
            this.jj_consume_token(10);
            SimpleNode jjtn004 = new SimpleNode(this, 13);
            boolean jjtc004 = true;
            this.jjtree.openNodeScope(jjtn004);
            try {
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                jjtc004 = false;
                jjtn004.processToken(this.token);
            }
            finally {
                if (jjtc004) {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                }
            }
            this.Separator();
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

    public final void Prolog() throws ParseException {
        block31: {
            SimpleNode jjtn000 = new SimpleNode(this, 14);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                block23: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 31: 
                        case 34: 
                        case 35: 
                        case 54: 
                        case 60: 
                        case 61: 
                        case 92: {
                            break;
                        }
                        default: {
                            this.jj_la1[5] = this.jj_gen;
                            break block23;
                        }
                    }
                    this.Setter();
                    this.Separator();
                }
                block24: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 65: 
                        case 94: 
                        case 95: 
                        case 97: 
                        case 98: {
                            break;
                        }
                        default: {
                            this.jj_la1[6] = this.jj_gen;
                            break block24;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 97: 
                        case 98: {
                            this.Import();
                            break;
                        }
                        case 65: {
                            this.NamespaceDecl();
                            break;
                        }
                        case 94: 
                        case 95: {
                            this.DefaultNamespaceDecl();
                            break;
                        }
                        default: {
                            this.jj_la1[7] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    this.Separator();
                }
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 30: 
                        case 182: {
                            break;
                        }
                        default: {
                            this.jj_la1[8] = this.jj_gen;
                            break block31;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 182: {
                            this.VarDecl();
                            break;
                        }
                        case 30: {
                            this.FunctionDecl();
                            break;
                        }
                        default: {
                            this.jj_la1[9] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    this.Separator();
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
    public final void Setter() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 15);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 60: {
                    this.XMLSpaceDecl();
                    return;
                }
                case 92: {
                    this.DefaultCollationDecl();
                    return;
                }
                case 61: {
                    this.BaseURIDecl();
                    return;
                }
                case 54: {
                    this.ConstructionDecl();
                    return;
                }
                case 31: {
                    this.OrderingModeDecl();
                    return;
                }
                case 34: {
                    this.EmptyOrderingDecl();
                    return;
                }
                case 35: {
                    this.InheritNamespacesDecl();
                    return;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Import() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 16);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 97: {
                    this.SchemaImport();
                    return;
                }
                case 98: {
                    this.ModuleImport();
                    return;
                }
                default: {
                    this.jj_la1[11] = this.jj_gen;
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

    public final void Separator() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 17);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(170);
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
    public final void NamespaceDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 18);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(65);
            SimpleNode jjtn001 = new SimpleNode(this, 19);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(188);
            SimpleNode jjtn002 = new SimpleNode(this, 11);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                }
            }
            this.jj_consume_token(110);
            SimpleNode jjtn003 = new SimpleNode(this, 12);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                }
            }
            this.jj_consume_token(10);
            SimpleNode jjtn004 = new SimpleNode(this, 13);
            boolean jjtc004 = true;
            this.jjtree.openNodeScope(jjtn004);
            try {
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                jjtc004 = false;
                jjtn004.processToken(this.token);
            }
            finally {
                if (jjtc004) {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
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
    public final void XMLSpaceDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 20);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(60);
            SimpleNode jjtn001 = new SimpleNode(this, 21);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 62: {
                    this.jj_consume_token(62);
                    SimpleNode jjtn002 = new SimpleNode(this, 22);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                case 63: {
                    this.jj_consume_token(63);
                    SimpleNode jjtn003 = new SimpleNode(this, 23);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[12] = this.jj_gen;
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
    public final void DefaultNamespaceDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 24);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 94: {
                    this.jj_consume_token(94);
                    SimpleNode jjtn001 = new SimpleNode(this, 25);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 95: {
                    this.jj_consume_token(95);
                    SimpleNode jjtn002 = new SimpleNode(this, 26);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[13] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(64);
            SimpleNode jjtn003 = new SimpleNode(this, 27);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                }
            }
            this.jj_consume_token(10);
            SimpleNode jjtn004 = new SimpleNode(this, 13);
            boolean jjtc004 = true;
            this.jjtree.openNodeScope(jjtn004);
            try {
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                jjtc004 = false;
                jjtn004.processToken(this.token);
            }
            finally {
                if (jjtc004) {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
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
    public final void OrderingModeDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 28);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(31);
            SimpleNode jjtn001 = new SimpleNode(this, 29);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 32: {
                    this.jj_consume_token(32);
                    SimpleNode jjtn002 = new SimpleNode(this, 30);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                case 33: {
                    this.jj_consume_token(33);
                    SimpleNode jjtn003 = new SimpleNode(this, 31);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[14] = this.jj_gen;
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
    public final void EmptyOrderingDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 32);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(34);
            SimpleNode jjtn001 = new SimpleNode(this, 33);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 180: {
                    this.jj_consume_token(180);
                    SimpleNode jjtn002 = new SimpleNode(this, 34);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                case 181: {
                    this.jj_consume_token(181);
                    SimpleNode jjtn003 = new SimpleNode(this, 35);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[15] = this.jj_gen;
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
    public final void InheritNamespacesDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 36);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(35);
            SimpleNode jjtn001 = new SimpleNode(this, 37);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 36: {
                    this.jj_consume_token(36);
                    SimpleNode jjtn002 = new SimpleNode(this, 38);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                case 37: {
                    this.jj_consume_token(37);
                    SimpleNode jjtn003 = new SimpleNode(this, 39);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[16] = this.jj_gen;
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
    public final void DefaultCollationDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 40);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(92);
            SimpleNode jjtn001 = new SimpleNode(this, 41);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(10);
            SimpleNode jjtn002 = new SimpleNode(this, 13);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
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
    public final void BaseURIDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 42);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(61);
            SimpleNode jjtn001 = new SimpleNode(this, 43);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(10);
            SimpleNode jjtn002 = new SimpleNode(this, 13);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void SchemaImport() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 44);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(97);
            SimpleNode jjtn001 = new SimpleNode(this, 45);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 64: 
                case 93: {
                    this.SchemaPrefix();
                    break;
                }
                default: {
                    this.jj_la1[17] = this.jj_gen;
                }
            }
            this.jj_consume_token(10);
            SimpleNode jjtn002 = new SimpleNode(this, 13);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    SimpleNode jjtn003 = new SimpleNode(this, 46);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
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
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 168: {
                    break;
                }
                default: {
                    this.jj_la1[18] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(168);
            this.jj_consume_token(4);
            SimpleNode jjtn004 = new SimpleNode(this, 47);
            boolean jjtc004 = true;
            this.jjtree.openNodeScope(jjtn004);
            try {
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                jjtc004 = false;
                jjtn004.processToken(this.token);
                continue;
            }
            finally {
                if (!jjtc004) continue;
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                continue;
            }
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void SchemaPrefix() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 48);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 64: {
                    this.jj_consume_token(64);
                    SimpleNode jjtn001 = new SimpleNode(this, 27);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                    this.jj_consume_token(188);
                    SimpleNode jjtn002 = new SimpleNode(this, 11);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.jj_consume_token(110);
                    SimpleNode jjtn003 = new SimpleNode(this, 12);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                case 93: {
                    this.jj_consume_token(93);
                    SimpleNode jjtn004 = new SimpleNode(this, 49);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                    this.jj_consume_token(64);
                    SimpleNode jjtn005 = new SimpleNode(this, 27);
                    boolean jjtc005 = true;
                    this.jjtree.openNodeScope(jjtn005);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                        jjtc005 = false;
                        jjtn005.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc005) {
                            this.jjtree.closeNodeScope((Node)jjtn005, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[20] = this.jj_gen;
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void ModuleImport() throws ParseException {
        block43: {
            SimpleNode jjtn000 = new SimpleNode(this, 50);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            this.jj_consume_token(98);
            SimpleNode jjtn001 = new SimpleNode(this, 51);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 64: {
                    this.jj_consume_token(64);
                    SimpleNode jjtn002 = new SimpleNode(this, 27);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.jj_consume_token(188);
                    SimpleNode jjtn003 = new SimpleNode(this, 11);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                    this.jj_consume_token(110);
                    SimpleNode jjtn004 = new SimpleNode(this, 12);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[21] = this.jj_gen;
                }
            }
            this.jj_consume_token(10);
            SimpleNode jjtn005 = new SimpleNode(this, 13);
            boolean jjtc005 = true;
            this.jjtree.openNodeScope(jjtn005);
            try {
                this.jjtree.closeNodeScope((Node)jjtn005, true);
                jjtc005 = false;
                jjtn005.processToken(this.token);
            }
            finally {
                if (jjtc005) {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    SimpleNode jjtn006 = new SimpleNode(this, 46);
                    boolean jjtc006 = true;
                    this.jjtree.openNodeScope(jjtn006);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn006, true);
                        jjtc006 = false;
                        jjtn006.processToken(this.token);
                        break block43;
                    }
                    finally {
                        if (jjtc006) {
                            this.jjtree.closeNodeScope((Node)jjtn006, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[23] = this.jj_gen;
                    return;
                }
            }
            finally {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                }
            }
        }
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 168: {
                    break;
                }
                default: {
                    this.jj_la1[22] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(168);
            this.jj_consume_token(4);
            SimpleNode jjtn007 = new SimpleNode(this, 47);
            boolean jjtc007 = true;
            this.jjtree.openNodeScope(jjtn007);
            try {
                this.jjtree.closeNodeScope((Node)jjtn007, true);
                jjtc007 = false;
                jjtn007.processToken(this.token);
                continue;
            }
            finally {
                if (!jjtc007) continue;
                this.jjtree.closeNodeScope((Node)jjtn007, true);
                continue;
            }
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void VarDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 52);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(182);
            SimpleNode jjtn001 = new SimpleNode(this, 53);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(50);
            SimpleNode jjtn002 = new SimpleNode(this, 54);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 72: {
                    this.TypeDeclaration();
                    break;
                }
                default: {
                    this.jj_la1[24] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 123: {
                    this.jj_consume_token(123);
                    SimpleNode jjtn003 = new SimpleNode(this, 55);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                    this.ExprSingle();
                    return;
                }
                case 38: {
                    this.jj_consume_token(38);
                    SimpleNode jjtn004 = new SimpleNode(this, 56);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[25] = this.jj_gen;
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
    public final void ConstructionDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 57);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(54);
            SimpleNode jjtn001 = new SimpleNode(this, 58);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(52);
            SimpleNode jjtn002 = new SimpleNode(this, 59);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void FunctionDecl() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 60);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(30);
            SimpleNode jjtn001 = new SimpleNode(this, 61);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(187);
            SimpleNode jjtn002 = new SimpleNode(this, 62);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 49: {
                    this.ParamList();
                    break;
                }
                default: {
                    this.jj_la1[26] = this.jj_gen;
                }
            }
            this.jj_consume_token(138);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 72: {
                    this.jj_consume_token(72);
                    SimpleNode jjtn003 = new SimpleNode(this, 63);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                    this.SequenceType();
                    break;
                }
                default: {
                    this.jj_la1[27] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 204: 
                case 205: {
                    this.EnclosedExpr();
                    return;
                }
                case 38: {
                    this.jj_consume_token(38);
                    SimpleNode jjtn004 = new SimpleNode(this, 56);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[28] = this.jj_gen;
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

    public final void ParamList() throws ParseException {
        block14: {
            SimpleNode jjtn000 = new SimpleNode(this, 64);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.Param();
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 168: {
                            break;
                        }
                        default: {
                            this.jj_la1[29] = this.jj_gen;
                            break block14;
                        }
                    }
                    this.jj_consume_token(168);
                    this.Param();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Param() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 65);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(49);
            this.jj_consume_token(50);
            SimpleNode jjtn001 = new SimpleNode(this, 54);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 72: {
                    this.TypeDeclaration();
                    return;
                }
                default: {
                    this.jj_la1[30] = this.jj_gen;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void EnclosedExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 66);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 204: {
                    this.jj_consume_token(204);
                    SimpleNode jjtn001 = new SimpleNode(this, 67);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 205: {
                    this.jj_consume_token(205);
                    SimpleNode jjtn002 = new SimpleNode(this, 68);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[31] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.Expr();
            this.jj_consume_token(241);
            SimpleNode jjtn003 = new SimpleNode(this, 69);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
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

    public final void QueryBody() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 70);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Expr();
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

    public final void Expr() throws ParseException {
        block14: {
            SimpleNode jjtn000 = new SimpleNode(this, 71);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.ExprSingle();
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 168: {
                            break;
                        }
                        default: {
                            this.jj_la1[32] = this.jj_gen;
                            break block14;
                        }
                    }
                    this.jj_consume_token(168);
                    this.ExprSingle();
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

    public final void ExprSingle() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 142: 
            case 143: {
                this.FLWORExpr();
                break;
            }
            case 140: 
            case 141: {
                this.QuantifiedExpr();
                break;
            }
            case 167: {
                this.TypeswitchExpr();
                break;
            }
            case 166: {
                this.IfExpr();
                break;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 15: 
            case 16: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 49: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 101: 
            case 103: 
            case 104: 
            case 105: 
            case 106: 
            case 128: 
            case 129: 
            case 134: 
            case 135: 
            case 146: 
            case 147: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 174: 
            case 175: 
            case 187: 
            case 196: 
            case 197: 
            case 232: 
            case 233: 
            case 235: {
                this.OrExpr();
                break;
            }
            default: {
                this.jj_la1[33] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void FLWORExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 73);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block18: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 142: {
                        this.ForClause();
                        break;
                    }
                    case 143: {
                        this.LetClause();
                        break;
                    }
                    default: {
                        this.jj_la1[34] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 142: 
                    case 143: {
                        continue block18;
                    }
                }
                break;
            }
            this.jj_la1[35] = this.jj_gen;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 67: {
                    this.WhereClause();
                    break;
                }
                default: {
                    this.jj_la1[36] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 176: 
                case 177: {
                    this.OrderByClause();
                    break;
                }
                default: {
                    this.jj_la1[37] = this.jj_gen;
                }
            }
            this.jj_consume_token(56);
            this.ExprSingle();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ForClause() throws ParseException {
        this.jj_consume_token(142);
        this.jj_consume_token(50);
        SimpleNode jjtn001 = new SimpleNode(this, 54);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.jjtree.closeNodeScope((Node)jjtn001, true);
            jjtc001 = false;
            jjtn001.processToken(this.token);
        }
        finally {
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 72: {
                this.TypeDeclaration();
                break;
            }
            default: {
                this.jj_la1[38] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 73: {
                this.PositionalVar();
                break;
            }
            default: {
                this.jj_la1[39] = this.jj_gen;
            }
        }
        this.jj_consume_token(45);
        SimpleNode jjtn002 = new SimpleNode(this, 74);
        boolean jjtc002 = true;
        this.jjtree.openNodeScope(jjtn002);
        try {
            this.jjtree.closeNodeScope((Node)jjtn002, true);
            jjtc002 = false;
            jjtn002.processToken(this.token);
        }
        finally {
            if (jjtc002) {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
            }
        }
        this.ExprSingle();
        block27: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 168: {
                    break;
                }
                default: {
                    this.jj_la1[40] = this.jj_gen;
                    break block27;
                }
            }
            this.jj_consume_token(168);
            this.jj_consume_token(49);
            this.jj_consume_token(50);
            SimpleNode jjtn003 = new SimpleNode(this, 54);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 72: {
                    this.TypeDeclaration();
                    break;
                }
                default: {
                    this.jj_la1[41] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 73: {
                    this.PositionalVar();
                    break;
                }
                default: {
                    this.jj_la1[42] = this.jj_gen;
                }
            }
            this.jj_consume_token(45);
            SimpleNode jjtn004 = new SimpleNode(this, 74);
            boolean jjtc004 = true;
            this.jjtree.openNodeScope(jjtn004);
            try {
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                jjtc004 = false;
                jjtn004.processToken(this.token);
            }
            finally {
                if (jjtc004) {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                }
            }
            this.ExprSingle();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void PositionalVar() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 75);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(73);
            SimpleNode jjtn001 = new SimpleNode(this, 76);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.jj_consume_token(49);
            this.jj_consume_token(50);
            SimpleNode jjtn002 = new SimpleNode(this, 54);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
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
    public final void LetClause() throws ParseException {
        block43: {
            SimpleNode jjtn000 = new SimpleNode(this, 77);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(143);
                SimpleNode jjtn001 = new SimpleNode(this, 78);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
                this.jj_consume_token(50);
                SimpleNode jjtn002 = new SimpleNode(this, 54);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                    jjtc002 = false;
                    jjtn002.processToken(this.token);
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 72: {
                        this.TypeDeclaration();
                        break;
                    }
                    default: {
                        this.jj_la1[43] = this.jj_gen;
                    }
                }
                this.jj_consume_token(123);
                SimpleNode jjtn003 = new SimpleNode(this, 55);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                    jjtc003 = false;
                    jjtn003.processToken(this.token);
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
                this.ExprSingle();
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 168: {
                            break;
                        }
                        default: {
                            this.jj_la1[44] = this.jj_gen;
                            break block43;
                        }
                    }
                    this.jj_consume_token(168);
                    this.jj_consume_token(49);
                    this.jj_consume_token(50);
                    SimpleNode jjtn004 = new SimpleNode(this, 54);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 72: {
                            this.TypeDeclaration();
                            break;
                        }
                        default: {
                            this.jj_la1[45] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(123);
                    SimpleNode jjtn005 = new SimpleNode(this, 55);
                    boolean jjtc005 = true;
                    this.jjtree.openNodeScope(jjtn005);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                        jjtc005 = false;
                        jjtn005.processToken(this.token);
                    }
                    finally {
                        if (jjtc005) {
                            this.jjtree.closeNodeScope((Node)jjtn005, true);
                        }
                    }
                    this.ExprSingle();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void WhereClause() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 79);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(67);
            SimpleNode jjtn001 = new SimpleNode(this, 80);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.ExprSingle();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void OrderByClause() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 81);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 176: {
                    this.jj_consume_token(176);
                    SimpleNode jjtn001 = new SimpleNode(this, 82);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 177: {
                    this.jj_consume_token(177);
                    SimpleNode jjtn002 = new SimpleNode(this, 83);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[46] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.OrderSpecList();
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

    public final void OrderSpecList() throws ParseException {
        block14: {
            SimpleNode jjtn000 = new SimpleNode(this, 84);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.OrderSpec();
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 168: {
                            break;
                        }
                        default: {
                            this.jj_la1[47] = this.jj_gen;
                            break block14;
                        }
                    }
                    this.jj_consume_token(168);
                    this.OrderSpec();
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

    public final void OrderSpec() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 85);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.ExprSingle();
            this.OrderModifier();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void OrderModifier() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 86);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block7 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 178: 
                case 179: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 178: {
                            this.jj_consume_token(178);
                            SimpleNode jjtn001 = new SimpleNode(this, 87);
                            boolean jjtc001 = true;
                            this.jjtree.openNodeScope(jjtn001);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn001, true);
                                jjtc001 = false;
                                jjtn001.processToken(this.token);
                                break block7;
                            }
                            finally {
                                if (jjtc001) {
                                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                                }
                            }
                        }
                        case 179: {
                            this.jj_consume_token(179);
                            SimpleNode jjtn002 = new SimpleNode(this, 88);
                            boolean jjtc002 = true;
                            this.jjtree.openNodeScope(jjtn002);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn002, true);
                                jjtc002 = false;
                                jjtn002.processToken(this.token);
                                break block7;
                            }
                            finally {
                                if (jjtc002) {
                                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                                }
                            }
                        }
                        default: {
                            this.jj_la1[48] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
                default: {
                    this.jj_la1[49] = this.jj_gen;
                }
            }
            block14 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 180: 
                case 181: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 180: {
                            this.jj_consume_token(180);
                            SimpleNode jjtn003 = new SimpleNode(this, 34);
                            boolean jjtc003 = true;
                            this.jjtree.openNodeScope(jjtn003);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn003, true);
                                jjtc003 = false;
                                jjtn003.processToken(this.token);
                                break block14;
                            }
                            finally {
                                if (jjtc003) {
                                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                                }
                            }
                        }
                        case 181: {
                            this.jj_consume_token(181);
                            SimpleNode jjtn004 = new SimpleNode(this, 35);
                            boolean jjtc004 = true;
                            this.jjtree.openNodeScope(jjtn004);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn004, true);
                                jjtc004 = false;
                                jjtn004.processToken(this.token);
                                break block14;
                            }
                            finally {
                                if (jjtc004) {
                                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                                }
                            }
                        }
                        default: {
                            this.jj_la1[50] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
                default: {
                    this.jj_la1[51] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 68: {
                    this.jj_consume_token(68);
                    SimpleNode jjtn005 = new SimpleNode(this, 89);
                    boolean jjtc005 = true;
                    this.jjtree.openNodeScope(jjtn005);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                        jjtc005 = false;
                        jjtn005.processToken(this.token);
                    }
                    finally {
                        if (jjtc005) {
                            this.jjtree.closeNodeScope((Node)jjtn005, true);
                        }
                    }
                    this.jj_consume_token(4);
                    SimpleNode jjtn006 = new SimpleNode(this, 47);
                    boolean jjtc006 = true;
                    this.jjtree.openNodeScope(jjtn006);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn006, true);
                        jjtc006 = false;
                        jjtn006.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc006) {
                            this.jjtree.closeNodeScope((Node)jjtn006, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[52] = this.jj_gen;
                    break;
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
    public final void QuantifiedExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 90);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 140: {
                    this.jj_consume_token(140);
                    SimpleNode jjtn001 = new SimpleNode(this, 91);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 141: {
                    this.jj_consume_token(141);
                    SimpleNode jjtn002 = new SimpleNode(this, 92);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[53] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(50);
            SimpleNode jjtn003 = new SimpleNode(this, 54);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 72: {
                    this.TypeDeclaration();
                    break;
                }
                default: {
                    this.jj_la1[54] = this.jj_gen;
                }
            }
            this.jj_consume_token(45);
            SimpleNode jjtn004 = new SimpleNode(this, 74);
            boolean jjtc004 = true;
            this.jjtree.openNodeScope(jjtn004);
            try {
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                jjtc004 = false;
                jjtn004.processToken(this.token);
            }
            finally {
                if (jjtc004) {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                }
            }
            this.ExprSingle();
            block39: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 168: {
                        break;
                    }
                    default: {
                        this.jj_la1[55] = this.jj_gen;
                        break block39;
                    }
                }
                this.jj_consume_token(168);
                this.jj_consume_token(49);
                this.jj_consume_token(50);
                SimpleNode jjtn005 = new SimpleNode(this, 54);
                boolean jjtc005 = true;
                this.jjtree.openNodeScope(jjtn005);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
                    jjtc005 = false;
                    jjtn005.processToken(this.token);
                }
                finally {
                    if (jjtc005) {
                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 72: {
                        this.TypeDeclaration();
                        break;
                    }
                    default: {
                        this.jj_la1[56] = this.jj_gen;
                    }
                }
                this.jj_consume_token(45);
                SimpleNode jjtn006 = new SimpleNode(this, 74);
                boolean jjtc006 = true;
                this.jjtree.openNodeScope(jjtn006);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn006, true);
                    jjtc006 = false;
                    jjtn006.processToken(this.token);
                }
                finally {
                    if (jjtc006) {
                        this.jjtree.closeNodeScope((Node)jjtn006, true);
                    }
                }
                this.ExprSingle();
            }
            this.jj_consume_token(55);
            SimpleNode jjtn007 = new SimpleNode(this, 93);
            boolean jjtc007 = true;
            this.jjtree.openNodeScope(jjtn007);
            try {
                this.jjtree.closeNodeScope((Node)jjtn007, true);
                jjtc007 = false;
                jjtn007.processToken(this.token);
            }
            finally {
                if (jjtc007) {
                    this.jjtree.closeNodeScope((Node)jjtn007, true);
                }
            }
            this.ExprSingle();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void TypeswitchExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 94);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(167);
            this.Expr();
            this.jj_consume_token(138);
            block17: while (true) {
                this.CaseClause();
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 74: {
                        continue block17;
                    }
                }
                break;
            }
            this.jj_la1[57] = this.jj_gen;
            this.jj_consume_token(59);
            SimpleNode jjtn001 = new SimpleNode(this, 95);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 49: {
                    this.jj_consume_token(49);
                    this.jj_consume_token(50);
                    SimpleNode jjtn002 = new SimpleNode(this, 54);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[58] = this.jj_gen;
                }
            }
            this.jj_consume_token(56);
            this.ExprSingle();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CaseClause() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 96);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(74);
            SimpleNode jjtn001 = new SimpleNode(this, 97);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 49: {
                    this.jj_consume_token(49);
                    this.jj_consume_token(50);
                    SimpleNode jjtn002 = new SimpleNode(this, 54);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.jj_consume_token(72);
                    SimpleNode jjtn003 = new SimpleNode(this, 63);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[59] = this.jj_gen;
                }
            }
            this.SequenceType();
            this.jj_consume_token(56);
            this.ExprSingle();
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

    public final void IfExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 98);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(166);
            this.Expr();
            this.jj_consume_token(138);
            this.jj_consume_token(57);
            this.ExprSingle();
            this.jj_consume_token(58);
            this.ExprSingle();
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

    public final void OperatorExpr() throws ParseException {
        this.OrExpr();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void OrExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 99);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.AndExpr();
            block12: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 39: {
                        break;
                    }
                    default: {
                        this.jj_la1[60] = this.jj_gen;
                        break block12;
                    }
                }
                this.jj_consume_token(39);
                this.binaryTokenStack.push(this.token);
                this.AndExpr();
                SimpleNode jjtn001 = new SimpleNode(this, 99);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    jjtc001 = false;
                    try {
                        jjtn001.processToken((Token)this.binaryTokenStack.pop());
                        continue;
                    }
                    catch (EmptyStackException e) {
                        this.token_source.printLinePos();
                        e.printStackTrace();
                        throw e;
                    }
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    continue;
                }
                break;
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void AndExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 100);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.ComparisonExpr();
            block12: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 40: {
                        break;
                    }
                    default: {
                        this.jj_la1[61] = this.jj_gen;
                        break block12;
                    }
                }
                this.jj_consume_token(40);
                this.binaryTokenStack.push(this.token);
                this.ComparisonExpr();
                SimpleNode jjtn001 = new SimpleNode(this, 100);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    jjtc001 = false;
                    try {
                        jjtn001.processToken((Token)this.binaryTokenStack.pop());
                        continue;
                    }
                    catch (EmptyStackException e) {
                        this.token_source.printLinePos();
                        e.printStackTrace();
                        throw e;
                    }
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    continue;
                }
                break;
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ComparisonExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 101);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.RangeExpr();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 109: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 124: 
                case 125: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 117: 
                        case 118: 
                        case 119: 
                        case 120: 
                        case 121: 
                        case 122: {
                            this.ValueComp();
                            break;
                        }
                        case 109: 
                        case 112: 
                        case 113: 
                        case 115: 
                        case 124: 
                        case 125: {
                            this.GeneralComp();
                            break;
                        }
                        case 111: 
                        case 114: 
                        case 116: {
                            this.NodeComp();
                            break;
                        }
                        default: {
                            this.jj_la1[62] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    this.RangeExpr();
                    SimpleNode jjtn001 = new SimpleNode(this, 101);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, 2);
                        jjtc001 = false;
                        try {
                            jjtn001.processToken((Token)this.binaryTokenStack.pop());
                            break;
                        }
                        catch (EmptyStackException e) {
                            this.token_source.printLinePos();
                            e.printStackTrace();
                            throw e;
                        }
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                        }
                    }
                }
                default: {
                    this.jj_la1[63] = this.jj_gen;
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void RangeExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 102);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.AdditiveExpr();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 66: {
                    this.jj_consume_token(66);
                    this.binaryTokenStack.push(this.token);
                    this.AdditiveExpr();
                    SimpleNode jjtn001 = new SimpleNode(this, 102);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, 2);
                        jjtc001 = false;
                        try {
                            jjtn001.processToken((Token)this.binaryTokenStack.pop());
                            break;
                        }
                        catch (EmptyStackException e) {
                            this.token_source.printLinePos();
                            e.printStackTrace();
                            throw e;
                        }
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                        }
                    }
                }
                default: {
                    this.jj_la1[64] = this.jj_gen;
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void AdditiveExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 103);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.MultiplicativeExpr();
            block16: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 126: 
                    case 127: {
                        break;
                    }
                    default: {
                        this.jj_la1[65] = this.jj_gen;
                        break block16;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 127: {
                        this.jj_consume_token(127);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    case 126: {
                        this.jj_consume_token(126);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    default: {
                        this.jj_la1[66] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.MultiplicativeExpr();
                SimpleNode jjtn001 = new SimpleNode(this, 103);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    jjtc001 = false;
                    try {
                        jjtn001.processToken((Token)this.binaryTokenStack.pop());
                        continue;
                    }
                    catch (EmptyStackException e) {
                        this.token_source.printLinePos();
                        e.printStackTrace();
                        throw e;
                    }
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    continue;
                }
                break;
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void MultiplicativeExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 104);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.UnionExpr();
            block18: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 41: 
                    case 42: 
                    case 43: 
                    case 44: {
                        break;
                    }
                    default: {
                        this.jj_la1[67] = this.jj_gen;
                        break block18;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 44: {
                        this.jj_consume_token(44);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    case 41: {
                        this.jj_consume_token(41);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    case 42: {
                        this.jj_consume_token(42);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    case 43: {
                        this.jj_consume_token(43);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    default: {
                        this.jj_la1[68] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.UnionExpr();
                SimpleNode jjtn001 = new SimpleNode(this, 104);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    jjtc001 = false;
                    try {
                        jjtn001.processToken((Token)this.binaryTokenStack.pop());
                        continue;
                    }
                    catch (EmptyStackException e) {
                        this.token_source.printLinePos();
                        e.printStackTrace();
                        throw e;
                    }
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    continue;
                }
                break;
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void UnionExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 105);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.IntersectExceptExpr();
            block16: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 70: 
                    case 133: {
                        break;
                    }
                    default: {
                        this.jj_la1[69] = this.jj_gen;
                        break block16;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 70: {
                        this.jj_consume_token(70);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    case 133: {
                        this.jj_consume_token(133);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    default: {
                        this.jj_la1[70] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.IntersectExceptExpr();
                SimpleNode jjtn001 = new SimpleNode(this, 105);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    jjtc001 = false;
                    try {
                        jjtn001.processToken((Token)this.binaryTokenStack.pop());
                        continue;
                    }
                    catch (EmptyStackException e) {
                        this.token_source.printLinePos();
                        e.printStackTrace();
                        throw e;
                    }
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    continue;
                }
                break;
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void IntersectExceptExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 106);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.InstanceofExpr();
            block16: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 69: 
                    case 71: {
                        break;
                    }
                    default: {
                        this.jj_la1[71] = this.jj_gen;
                        break block16;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 69: {
                        this.jj_consume_token(69);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    case 71: {
                        this.jj_consume_token(71);
                        this.binaryTokenStack.push(this.token);
                        break;
                    }
                    default: {
                        this.jj_la1[72] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.InstanceofExpr();
                SimpleNode jjtn001 = new SimpleNode(this, 106);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    jjtc001 = false;
                    try {
                        jjtn001.processToken((Token)this.binaryTokenStack.pop());
                        continue;
                    }
                    catch (EmptyStackException e) {
                        this.token_source.printLinePos();
                        e.printStackTrace();
                        throw e;
                    }
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    continue;
                }
                break;
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    public final void InstanceofExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 107);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.TreatExpr();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 75: {
                    this.jj_consume_token(75);
                    this.SequenceType();
                    break;
                }
                default: {
                    this.jj_la1[73] = this.jj_gen;
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    public final void TreatExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 108);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.CastableExpr();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 145: {
                    this.jj_consume_token(145);
                    this.SequenceType();
                    break;
                }
                default: {
                    this.jj_la1[74] = this.jj_gen;
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    public final void CastableExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 109);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.CastExpr();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 76: {
                    this.jj_consume_token(76);
                    this.SingleType();
                    break;
                }
                default: {
                    this.jj_la1[75] = this.jj_gen;
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CastExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 110);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.UnaryExpr();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 144: {
                    this.jj_consume_token(144);
                    SimpleNode jjtn001 = new SimpleNode(this, 111);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                    this.SingleType();
                    break;
                }
                default: {
                    this.jj_la1[76] = this.jj_gen;
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void UnaryExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 112);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        boolean keepUnary = false;
        try {
            block23: {
                block18: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 128: 
                        case 129: {
                            break;
                        }
                        default: {
                            this.jj_la1[77] = this.jj_gen;
                            break block23;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 128: {
                            this.jj_consume_token(128);
                            SimpleNode jjtn001 = new SimpleNode(this, 113);
                            boolean jjtc001 = true;
                            this.jjtree.openNodeScope(jjtn001);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn001, true);
                                jjtc001 = false;
                                keepUnary = true;
                                jjtn001.processToken(this.token);
                                continue block18;
                            }
                            finally {
                                if (!jjtc001) continue block18;
                                this.jjtree.closeNodeScope((Node)jjtn001, true);
                                continue block18;
                            }
                        }
                        case 129: {
                            this.jj_consume_token(129);
                            SimpleNode jjtn002 = new SimpleNode(this, 114);
                            boolean jjtc002 = true;
                            this.jjtree.openNodeScope(jjtn002);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn002, true);
                                jjtc002 = false;
                                keepUnary = true;
                                jjtn002.processToken(this.token);
                                continue block18;
                            }
                            finally {
                                if (!jjtc002) continue block18;
                                this.jjtree.closeNodeScope((Node)jjtn002, true);
                                continue block18;
                            }
                        }
                    }
                    break;
                }
                this.jj_la1[78] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            this.ValueExpr();
            return;
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
                this.jjtree.closeNodeScope((Node)jjtn000, keepUnary);
            }
        }
    }

    public final void ValueExpr() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 146: 
            case 147: {
                this.ValidateExpr();
                break;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 15: 
            case 16: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 49: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 101: 
            case 103: 
            case 104: 
            case 105: 
            case 106: 
            case 134: 
            case 135: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 174: 
            case 175: 
            case 187: 
            case 196: 
            case 197: 
            case 232: 
            case 233: 
            case 235: {
                this.PathExpr();
                break;
            }
            default: {
                this.jj_la1[79] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void GeneralComp() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 109: {
                this.jj_consume_token(109);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 112: {
                this.jj_consume_token(112);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 124: {
                this.jj_consume_token(124);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 113: {
                this.jj_consume_token(113);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 125: {
                this.jj_consume_token(125);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 115: {
                this.jj_consume_token(115);
                this.binaryTokenStack.push(this.token);
                break;
            }
            default: {
                this.jj_la1[80] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void ValueComp() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 117: {
                this.jj_consume_token(117);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 118: {
                this.jj_consume_token(118);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 121: {
                this.jj_consume_token(121);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 122: {
                this.jj_consume_token(122);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 119: {
                this.jj_consume_token(119);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 120: {
                this.jj_consume_token(120);
                this.binaryTokenStack.push(this.token);
                break;
            }
            default: {
                this.jj_la1[81] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void NodeComp() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 111: {
                this.jj_consume_token(111);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 114: {
                this.jj_consume_token(114);
                this.binaryTokenStack.push(this.token);
                break;
            }
            case 116: {
                this.jj_consume_token(116);
                this.binaryTokenStack.push(this.token);
                break;
            }
            default: {
                this.jj_la1[82] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ValidateExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 115);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 146: {
                    this.jj_consume_token(146);
                    SimpleNode jjtn001 = new SimpleNode(this, 116);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 147: {
                    this.jj_consume_token(147);
                    SimpleNode jjtn002 = new SimpleNode(this, 117);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.jj_consume_token(205);
                    SimpleNode jjtn003 = new SimpleNode(this, 68);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[83] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.Expr();
            this.jj_consume_token(241);
            SimpleNode jjtn004 = new SimpleNode(this, 69);
            boolean jjtc004 = true;
            this.jjtree.openNodeScope(jjtn004);
            try {
                this.jjtree.closeNodeScope((Node)jjtn004, true);
                jjtc004 = false;
                jjtn004.processToken(this.token);
            }
            finally {
                if (jjtc004) {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void PathExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 118);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block4 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 105: {
                    this.jj_consume_token(105);
                    SimpleNode jjtn001 = new SimpleNode(this, 119);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 1: 
                        case 2: 
                        case 3: 
                        case 4: 
                        case 15: 
                        case 16: 
                        case 18: 
                        case 19: 
                        case 20: 
                        case 21: 
                        case 22: 
                        case 23: 
                        case 24: 
                        case 25: 
                        case 26: 
                        case 27: 
                        case 28: 
                        case 29: 
                        case 49: 
                        case 78: 
                        case 79: 
                        case 80: 
                        case 81: 
                        case 82: 
                        case 83: 
                        case 84: 
                        case 85: 
                        case 86: 
                        case 87: 
                        case 88: 
                        case 89: 
                        case 90: 
                        case 91: 
                        case 101: 
                        case 103: 
                        case 104: 
                        case 134: 
                        case 135: 
                        case 149: 
                        case 150: 
                        case 151: 
                        case 152: 
                        case 153: 
                        case 154: 
                        case 155: 
                        case 156: 
                        case 157: 
                        case 158: 
                        case 159: 
                        case 160: 
                        case 161: 
                        case 162: 
                        case 163: 
                        case 164: 
                        case 165: 
                        case 174: 
                        case 175: 
                        case 187: 
                        case 196: 
                        case 197: 
                        case 232: 
                        case 233: 
                        case 235: {
                            this.RelativePathExpr();
                            break block4;
                        }
                    }
                    this.jj_la1[84] = this.jj_gen;
                    break;
                }
                case 106: {
                    this.jj_consume_token(106);
                    SimpleNode jjtn002 = new SimpleNode(this, 120);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.RelativePathExpr();
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 15: 
                case 16: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 49: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 101: 
                case 103: 
                case 104: 
                case 134: 
                case 135: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 174: 
                case 175: 
                case 187: 
                case 196: 
                case 197: 
                case 232: 
                case 233: 
                case 235: {
                    this.RelativePathExpr();
                    break;
                }
                default: {
                    this.jj_la1[85] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 0);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 0);
                }
                throw throwable;
            }
        }
    }

    public final void RelativePathExpr() throws ParseException {
        this.StepExpr();
        block10: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 107: 
                case 108: {
                    break;
                }
                default: {
                    this.jj_la1[86] = this.jj_gen;
                    break block10;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 107: {
                    this.jj_consume_token(107);
                    break;
                }
                case 108: {
                    this.jj_consume_token(108);
                    SimpleNode jjtn001 = new SimpleNode(this, 121);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[87] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.StepExpr();
        }
    }

    public final void StepExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 122);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        boolean savedIsStep = this.isStep;
        this.isStep = false;
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 101: 
                case 103: 
                case 104: 
                case 135: 
                case 149: 
                case 150: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 175: 
                case 235: {
                    this.isStep = true;
                    this.AxisStep();
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1 || this.isStep);
                    jjtc000 = false;
                    this.isStep = savedIsStep;
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 15: 
                case 16: 
                case 49: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 134: 
                case 151: 
                case 174: 
                case 187: 
                case 196: 
                case 197: 
                case 232: 
                case 233: {
                    this.FilterExpr();
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1 || this.isStep);
                    jjtc000 = false;
                    this.isStep = savedIsStep;
                    break;
                }
                default: {
                    this.jj_la1[88] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1 || this.isStep);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 1 || this.isStep);
                }
                throw throwable;
            }
        }
    }

    public final void AxisStep() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 18: 
            case 19: 
            case 21: 
            case 22: 
            case 23: 
            case 25: 
            case 27: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 101: 
            case 103: 
            case 104: 
            case 135: 
            case 149: 
            case 150: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 235: {
                this.ForwardStep();
                break;
            }
            case 20: 
            case 24: 
            case 26: 
            case 28: 
            case 29: 
            case 175: {
                this.ReverseStep();
                break;
            }
            default: {
                this.jj_la1[89] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.PredicateList();
    }

    public final void ForwardStep() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 18: 
            case 19: 
            case 21: 
            case 22: 
            case 23: 
            case 25: 
            case 27: {
                this.ForwardAxis();
                this.NodeTest();
                break;
            }
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 101: 
            case 103: 
            case 104: 
            case 135: 
            case 149: 
            case 150: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 235: {
                this.AbbrevForwardStep();
                break;
            }
            default: {
                this.jj_la1[90] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ForwardAxis() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 18: {
                this.jj_consume_token(18);
                SimpleNode jjtn001 = new SimpleNode(this, 123);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            case 19: {
                this.jj_consume_token(19);
                SimpleNode jjtn002 = new SimpleNode(this, 124);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                    jjtc002 = false;
                    jjtn002.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
            }
            case 21: {
                this.jj_consume_token(21);
                SimpleNode jjtn003 = new SimpleNode(this, 125);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                    jjtc003 = false;
                    jjtn003.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
            }
            case 22: {
                this.jj_consume_token(22);
                SimpleNode jjtn004 = new SimpleNode(this, 126);
                boolean jjtc004 = true;
                this.jjtree.openNodeScope(jjtn004);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                    jjtc004 = false;
                    jjtn004.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc004) {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                    }
                }
            }
            case 23: {
                this.jj_consume_token(23);
                SimpleNode jjtn005 = new SimpleNode(this, 127);
                boolean jjtc005 = true;
                this.jjtree.openNodeScope(jjtn005);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
                    jjtc005 = false;
                    jjtn005.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc005) {
                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                    }
                }
            }
            case 25: {
                this.jj_consume_token(25);
                SimpleNode jjtn006 = new SimpleNode(this, 128);
                boolean jjtc006 = true;
                this.jjtree.openNodeScope(jjtn006);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn006, true);
                    jjtc006 = false;
                    jjtn006.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc006) {
                        this.jjtree.closeNodeScope((Node)jjtn006, true);
                    }
                }
            }
            case 27: {
                this.jj_consume_token(27);
                SimpleNode jjtn007 = new SimpleNode(this, 129);
                boolean jjtc007 = true;
                this.jjtree.openNodeScope(jjtn007);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn007, true);
                    jjtc007 = false;
                    jjtn007.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc007) {
                        this.jjtree.closeNodeScope((Node)jjtn007, true);
                    }
                }
            }
            default: {
                this.jj_la1[91] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void AbbrevForwardStep() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 135: {
                this.jj_consume_token(135);
                SimpleNode jjtn001 = new SimpleNode(this, 130);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            default: {
                this.jj_la1[92] = this.jj_gen;
            }
        }
        this.NodeTest();
    }

    public final void ReverseStep() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 20: 
            case 24: 
            case 26: 
            case 28: 
            case 29: {
                this.ReverseAxis();
                this.NodeTest();
                break;
            }
            case 175: {
                this.AbbrevReverseStep();
                break;
            }
            default: {
                this.jj_la1[93] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ReverseAxis() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 20: {
                this.jj_consume_token(20);
                SimpleNode jjtn001 = new SimpleNode(this, 131);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            case 24: {
                this.jj_consume_token(24);
                SimpleNode jjtn002 = new SimpleNode(this, 132);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                    jjtc002 = false;
                    jjtn002.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
            }
            case 26: {
                this.jj_consume_token(26);
                SimpleNode jjtn003 = new SimpleNode(this, 133);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                    jjtc003 = false;
                    jjtn003.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
            }
            case 28: {
                this.jj_consume_token(28);
                SimpleNode jjtn004 = new SimpleNode(this, 134);
                boolean jjtc004 = true;
                this.jjtree.openNodeScope(jjtn004);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                    jjtc004 = false;
                    jjtn004.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc004) {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                    }
                }
            }
            case 29: {
                this.jj_consume_token(29);
                SimpleNode jjtn005 = new SimpleNode(this, 135);
                boolean jjtc005 = true;
                this.jjtree.openNodeScope(jjtn005);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
                    jjtc005 = false;
                    jjtn005.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc005) {
                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                    }
                }
            }
            default: {
                this.jj_la1[94] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void AbbrevReverseStep() throws ParseException {
        this.jj_consume_token(175);
        SimpleNode jjtn001 = new SimpleNode(this, 136);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.jjtree.closeNodeScope((Node)jjtn001, true);
            jjtc001 = false;
            jjtn001.processToken(this.token);
        }
        finally {
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void NodeTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 137);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 149: 
                case 150: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: {
                    this.KindTest();
                    return;
                }
                case 101: 
                case 103: 
                case 104: 
                case 235: {
                    this.NameTest();
                    return;
                }
                default: {
                    this.jj_la1[95] = this.jj_gen;
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void NameTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 138);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 235: {
                    this.jj_consume_token(235);
                    SimpleNode jjtn001 = new SimpleNode(this, 139);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 101: 
                case 103: 
                case 104: {
                    this.Wildcard();
                    return;
                }
                default: {
                    this.jj_la1[96] = this.jj_gen;
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
    public final void Wildcard() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 101: {
                this.jj_consume_token(101);
                SimpleNode jjtn001 = new SimpleNode(this, 140);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            case 103: {
                this.jj_consume_token(103);
                SimpleNode jjtn002 = new SimpleNode(this, 141);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                    jjtc002 = false;
                    jjtn002.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
            }
            case 104: {
                this.jj_consume_token(104);
                SimpleNode jjtn003 = new SimpleNode(this, 142);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                    jjtc003 = false;
                    jjtn003.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
            }
            default: {
                this.jj_la1[97] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void FilterExpr() throws ParseException {
        this.PrimaryExpr();
        this.PredicateList();
    }

    public final void PredicateList() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 143);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block7: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 136: {
                        break;
                    }
                    default: {
                        this.jj_la1[98] = this.jj_gen;
                        break block7;
                    }
                }
                this.Predicate();
            }
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 0);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 0);
                }
                throw throwable;
            }
        }
    }

    public final void Predicate() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 144);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(136);
            this.Expr();
            this.jj_consume_token(137);
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 0);
            }
        }
        catch (Throwable jjte000) {
            try {
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
            catch (Throwable throwable) {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, this.jjtree.nodeArity() > 0);
                }
                throw throwable;
            }
        }
    }

    public final void PrimaryExpr() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 3: 
            case 4: {
                this.Literal();
                break;
            }
            case 49: {
                this.VarRef();
                break;
            }
            case 134: {
                this.ParenthesizedExpr();
                break;
            }
            case 174: {
                this.isStep = true;
                this.ContextItemExpr();
                break;
            }
            case 187: {
                this.FunctionCall();
                break;
            }
            case 15: 
            case 16: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 151: 
            case 196: 
            case 197: 
            case 232: 
            case 233: {
                this.Constructor();
                break;
            }
            case 82: {
                this.OrderedExpr();
                break;
            }
            case 83: {
                this.UnorderedExpr();
                break;
            }
            default: {
                this.jj_la1[99] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void Literal() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 3: {
                this.NumericLiteral();
                break;
            }
            case 4: {
                this.jj_consume_token(4);
                SimpleNode jjtn001 = new SimpleNode(this, 47);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            default: {
                this.jj_la1[100] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void NumericLiteral() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                this.jj_consume_token(1);
                SimpleNode jjtn001 = new SimpleNode(this, 145);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            case 2: {
                this.jj_consume_token(2);
                SimpleNode jjtn002 = new SimpleNode(this, 146);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                    jjtc002 = false;
                    jjtn002.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
            }
            case 3: {
                this.jj_consume_token(3);
                SimpleNode jjtn003 = new SimpleNode(this, 147);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                    jjtc003 = false;
                    jjtn003.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
            }
            default: {
                this.jj_la1[101] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void VarRef() throws ParseException {
        this.jj_consume_token(49);
        this.jj_consume_token(50);
        SimpleNode jjtn001 = new SimpleNode(this, 54);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.jjtree.closeNodeScope((Node)jjtn001, true);
            jjtc001 = false;
            jjtn001.processToken(this.token);
        }
        finally {
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
            }
        }
    }

    public final void ParenthesizedExpr() throws ParseException {
        this.jj_consume_token(134);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 15: 
            case 16: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 49: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 101: 
            case 103: 
            case 104: 
            case 105: 
            case 106: 
            case 128: 
            case 129: 
            case 134: 
            case 135: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 146: 
            case 147: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 174: 
            case 175: 
            case 187: 
            case 196: 
            case 197: 
            case 232: 
            case 233: 
            case 235: {
                this.Expr();
                break;
            }
            default: {
                this.jj_la1[102] = this.jj_gen;
            }
        }
        this.jj_consume_token(138);
    }

    public final void ContextItemExpr() throws ParseException {
        this.jj_consume_token(174);
        SimpleNode jjtn001 = new SimpleNode(this, 148);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.jjtree.closeNodeScope((Node)jjtn001, true);
            jjtc001 = false;
            jjtn001.processToken(this.token);
        }
        finally {
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void OrderedExpr() throws ParseException {
        this.jj_consume_token(82);
        SimpleNode jjtn001 = new SimpleNode(this, 149);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.jjtree.closeNodeScope((Node)jjtn001, true);
            jjtc001 = false;
            jjtn001.processToken(this.token);
        }
        finally {
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
            }
        }
        this.Expr();
        this.jj_consume_token(241);
        SimpleNode jjtn002 = new SimpleNode(this, 69);
        boolean jjtc002 = true;
        this.jjtree.openNodeScope(jjtn002);
        try {
            this.jjtree.closeNodeScope((Node)jjtn002, true);
            jjtc002 = false;
            jjtn002.processToken(this.token);
        }
        finally {
            if (jjtc002) {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void UnorderedExpr() throws ParseException {
        this.jj_consume_token(83);
        SimpleNode jjtn001 = new SimpleNode(this, 150);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.jjtree.closeNodeScope((Node)jjtn001, true);
            jjtc001 = false;
            jjtn001.processToken(this.token);
        }
        finally {
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
            }
        }
        this.Expr();
        this.jj_consume_token(241);
        SimpleNode jjtn002 = new SimpleNode(this, 69);
        boolean jjtc002 = true;
        this.jjtree.openNodeScope(jjtn002);
        try {
            this.jjtree.closeNodeScope((Node)jjtn002, true);
            jjtc002 = false;
            jjtn002.processToken(this.token);
        }
        finally {
            if (jjtc002) {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void FunctionCall() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 151);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(187);
            SimpleNode jjtn001 = new SimpleNode(this, 62);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            block3 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 15: 
                case 16: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 49: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 101: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 128: 
                case 129: 
                case 134: 
                case 135: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 146: 
                case 147: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 174: 
                case 175: 
                case 187: 
                case 196: 
                case 197: 
                case 232: 
                case 233: 
                case 235: {
                    this.ExprSingle();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 168: {
                                break;
                            }
                            default: {
                                this.jj_la1[103] = this.jj_gen;
                                break block3;
                            }
                        }
                        this.jj_consume_token(168);
                        this.ExprSingle();
                    }
                }
                default: {
                    this.jj_la1[104] = this.jj_gen;
                }
            }
            this.jj_consume_token(138);
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
    public final void Constructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 152);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 15: 
                case 16: 
                case 196: 
                case 197: 
                case 232: 
                case 233: {
                    this.DirectConstructor();
                    return;
                }
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 151: {
                    this.ComputedConstructor();
                    return;
                }
                default: {
                    this.jj_la1[105] = this.jj_gen;
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void DirectConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 153);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 196: 
                case 197: {
                    this.DirElemConstructor();
                    return;
                }
                case 232: 
                case 233: {
                    this.DirCommentConstructor();
                    return;
                }
                case 15: 
                case 16: {
                    this.DirPIConstructor();
                    return;
                }
                default: {
                    this.jj_la1[106] = this.jj_gen;
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
    public final void DirElemConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 154);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 197: {
                    this.jj_consume_token(197);
                    SimpleNode jjtn001 = new SimpleNode(this, 155);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 196: {
                    this.jj_consume_token(196);
                    SimpleNode jjtn002 = new SimpleNode(this, 156);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[107] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(203);
            SimpleNode jjtn003 = new SimpleNode(this, 157);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                }
            }
            this.DirAttributeList();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 199: {
                    this.jj_consume_token(199);
                    SimpleNode jjtn004 = new SimpleNode(this, 158);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                case 198: {
                    this.jj_consume_token(198);
                    SimpleNode jjtn005 = new SimpleNode(this, 159);
                    boolean jjtc005 = true;
                    this.jjtree.openNodeScope(jjtn005);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                        jjtc005 = false;
                        jjtn005.processToken(this.token);
                    }
                    finally {
                        if (jjtc005) {
                            this.jjtree.closeNodeScope((Node)jjtn005, true);
                        }
                    }
                    block46: while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 15: 
                            case 16: 
                            case 190: 
                            case 191: 
                            case 193: 
                            case 194: 
                            case 196: 
                            case 197: 
                            case 204: 
                            case 205: 
                            case 206: 
                            case 207: 
                            case 210: 
                            case 232: 
                            case 233: {
                                break;
                            }
                            default: {
                                this.jj_la1[108] = this.jj_gen;
                                break block46;
                            }
                        }
                        this.DirElemContent();
                    }
                    this.jj_consume_token(200);
                    SimpleNode jjtn006 = new SimpleNode(this, 160);
                    boolean jjtc006 = true;
                    this.jjtree.openNodeScope(jjtn006);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn006, true);
                        jjtc006 = false;
                        jjtn006.processToken(this.token);
                    }
                    finally {
                        if (jjtc006) {
                            this.jjtree.closeNodeScope((Node)jjtn006, true);
                        }
                    }
                    this.jj_consume_token(203);
                    SimpleNode jjtn007 = new SimpleNode(this, 157);
                    boolean jjtc007 = true;
                    this.jjtree.openNodeScope(jjtn007);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn007, true);
                        jjtc007 = false;
                        jjtn007.processToken(this.token);
                    }
                    finally {
                        if (jjtc007) {
                            this.jjtree.closeNodeScope((Node)jjtn007, true);
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 237: {
                            this.jj_consume_token(237);
                            SimpleNode jjtn008 = new SimpleNode(this, 161);
                            boolean jjtc008 = true;
                            this.jjtree.openNodeScope(jjtn008);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn008, true);
                                jjtc008 = false;
                                jjtn008.processToken(this.token);
                                break;
                            }
                            finally {
                                if (jjtc008) {
                                    this.jjtree.closeNodeScope((Node)jjtn008, true);
                                }
                            }
                        }
                        default: {
                            this.jj_la1[109] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(201);
                    SimpleNode jjtn009 = new SimpleNode(this, 162);
                    boolean jjtc009 = true;
                    this.jjtree.openNodeScope(jjtn009);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn009, true);
                        jjtc009 = false;
                        jjtn009.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc009) {
                            this.jjtree.closeNodeScope((Node)jjtn009, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[110] = this.jj_gen;
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void DirAttributeList() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 163);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block32: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 237: {
                        break;
                    }
                    default: {
                        this.jj_la1[111] = this.jj_gen;
                        return;
                    }
                }
                this.jj_consume_token(237);
                SimpleNode jjtn001 = new SimpleNode(this, 161);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 203: {
                        this.jj_consume_token(203);
                        SimpleNode jjtn002 = new SimpleNode(this, 157);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                            jjtc002 = false;
                            jjtn002.processToken(this.token);
                        }
                        finally {
                            if (jjtc002) {
                                this.jjtree.closeNodeScope((Node)jjtn002, true);
                            }
                        }
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 237: {
                                this.jj_consume_token(237);
                                SimpleNode jjtn003 = new SimpleNode(this, 161);
                                boolean jjtc003 = true;
                                this.jjtree.openNodeScope(jjtn003);
                                try {
                                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                                    jjtc003 = false;
                                    jjtn003.processToken(this.token);
                                    break;
                                }
                                finally {
                                    if (jjtc003) {
                                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                                    }
                                }
                            }
                            default: {
                                this.jj_la1[112] = this.jj_gen;
                            }
                        }
                        this.jj_consume_token(202);
                        SimpleNode jjtn004 = new SimpleNode(this, 164);
                        boolean jjtc004 = true;
                        this.jjtree.openNodeScope(jjtn004);
                        try {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                            jjtc004 = false;
                            jjtn004.processToken(this.token);
                        }
                        finally {
                            if (jjtc004) {
                                this.jjtree.closeNodeScope((Node)jjtn004, true);
                            }
                        }
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 237: {
                                this.jj_consume_token(237);
                                SimpleNode jjtn005 = new SimpleNode(this, 161);
                                boolean jjtc005 = true;
                                this.jjtree.openNodeScope(jjtn005);
                                try {
                                    this.jjtree.closeNodeScope((Node)jjtn005, true);
                                    jjtc005 = false;
                                    jjtn005.processToken(this.token);
                                    break;
                                }
                                finally {
                                    if (jjtc005) {
                                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                                    }
                                }
                            }
                            default: {
                                this.jj_la1[113] = this.jj_gen;
                            }
                        }
                        this.DirAttributeValue();
                        continue block32;
                    }
                }
                this.jj_la1[114] = this.jj_gen;
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
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void DirAttributeValue() throws ParseException {
        block54: {
            block52: {
                block53: {
                    SimpleNode jjtn000 = new SimpleNode(this, 165);
                    boolean jjtc000 = true;
                    this.jjtree.openNodeScope(jjtn000);
                    try {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 172: {
                                this.jj_consume_token(172);
                                SimpleNode jjtn001 = new SimpleNode(this, 166);
                                boolean jjtc001 = true;
                                this.jjtree.openNodeScope(jjtn001);
                                try {
                                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                                    jjtc001 = false;
                                    jjtn001.processToken(this.token);
                                    break;
                                }
                                finally {
                                    if (jjtc001) {
                                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                                    }
                                }
                            }
                            case 217: {
                                this.jj_consume_token(217);
                                SimpleNode jjtn004 = new SimpleNode(this, 169);
                                boolean jjtc004 = true;
                                this.jjtree.openNodeScope(jjtn004);
                                try {
                                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                                    jjtc004 = false;
                                    jjtn004.processToken(this.token);
                                    break block52;
                                }
                                finally {
                                    if (jjtc004) {
                                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                                    }
                                }
                            }
                            default: {
                                this.jj_la1[119] = this.jj_gen;
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
                    block41: while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 193: 
                            case 194: 
                            case 204: 
                            case 205: 
                            case 206: 
                            case 207: 
                            case 208: 
                            case 211: {
                                break;
                            }
                            default: {
                                this.jj_la1[115] = this.jj_gen;
                                break block53;
                            }
                        }
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 208: {
                                this.jj_consume_token(208);
                                SimpleNode jjtn002 = new SimpleNode(this, 167);
                                boolean jjtc002 = true;
                                this.jjtree.openNodeScope(jjtn002);
                                try {
                                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                                    jjtc002 = false;
                                    jjtn002.processToken(this.token);
                                    continue block41;
                                }
                                finally {
                                    if (!jjtc002) continue block41;
                                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                                    continue block41;
                                }
                            }
                            case 193: 
                            case 194: 
                            case 204: 
                            case 205: 
                            case 206: 
                            case 207: 
                            case 211: {
                                this.QuotAttrValueContent();
                                continue block41;
                            }
                        }
                        break;
                    }
                    this.jj_la1[116] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                this.jj_consume_token(173);
                SimpleNode jjtn003 = new SimpleNode(this, 168);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                    jjtc003 = false;
                    jjtn003.processToken(this.token);
                    return;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
            }
            block42: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 193: 
                    case 194: 
                    case 204: 
                    case 205: 
                    case 206: 
                    case 207: 
                    case 209: 
                    case 212: {
                        break;
                    }
                    default: {
                        this.jj_la1[117] = this.jj_gen;
                        break block54;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 209: {
                        this.jj_consume_token(209);
                        SimpleNode jjtn005 = new SimpleNode(this, 170);
                        boolean jjtc005 = true;
                        this.jjtree.openNodeScope(jjtn005);
                        try {
                            this.jjtree.closeNodeScope((Node)jjtn005, true);
                            jjtc005 = false;
                            jjtn005.processToken(this.token);
                            continue block42;
                        }
                        finally {
                            if (!jjtc005) continue block42;
                            this.jjtree.closeNodeScope((Node)jjtn005, true);
                            continue block42;
                        }
                    }
                    case 193: 
                    case 194: 
                    case 204: 
                    case 205: 
                    case 206: 
                    case 207: 
                    case 212: {
                        this.AposAttrValueContent();
                        continue block42;
                    }
                }
                break;
            }
            this.jj_la1[118] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        this.jj_consume_token(218);
        SimpleNode jjtn006 = new SimpleNode(this, 171);
        boolean jjtc006 = true;
        this.jjtree.openNodeScope(jjtn006);
        try {
            this.jjtree.closeNodeScope((Node)jjtn006, true);
            jjtc006 = false;
            jjtn006.processToken(this.token);
            return;
        }
        finally {
            if (jjtc006) {
                this.jjtree.closeNodeScope((Node)jjtn006, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void QuotAttrValueContent() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 172);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 211: {
                    this.jj_consume_token(211);
                    SimpleNode jjtn001 = new SimpleNode(this, 173);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 193: 
                case 194: 
                case 204: 
                case 205: 
                case 206: 
                case 207: {
                    this.CommonContent();
                    return;
                }
                default: {
                    this.jj_la1[120] = this.jj_gen;
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void AposAttrValueContent() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 174);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 212: {
                    this.jj_consume_token(212);
                    SimpleNode jjtn001 = new SimpleNode(this, 175);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 193: 
                case 194: 
                case 204: 
                case 205: 
                case 206: 
                case 207: {
                    this.CommonContent();
                    return;
                }
                default: {
                    this.jj_la1[121] = this.jj_gen;
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void DirElemContent() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 176);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 15: 
                case 16: 
                case 196: 
                case 197: 
                case 232: 
                case 233: {
                    this.DirectConstructor();
                    return;
                }
                case 210: {
                    this.jj_consume_token(210);
                    SimpleNode jjtn001 = new SimpleNode(this, 177);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 190: 
                case 191: {
                    this.CDataSection();
                    return;
                }
                case 193: 
                case 194: 
                case 204: 
                case 205: 
                case 206: 
                case 207: {
                    this.CommonContent();
                    return;
                }
                default: {
                    this.jj_la1[122] = this.jj_gen;
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void CommonContent() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 178);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 193: {
                    this.jj_consume_token(193);
                    SimpleNode jjtn001 = new SimpleNode(this, 179);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 194: {
                    this.jj_consume_token(194);
                    SimpleNode jjtn002 = new SimpleNode(this, 180);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                case 206: {
                    this.jj_consume_token(206);
                    SimpleNode jjtn003 = new SimpleNode(this, 181);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                case 207: {
                    this.jj_consume_token(207);
                    SimpleNode jjtn004 = new SimpleNode(this, 182);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                case 204: 
                case 205: {
                    this.EnclosedExpr();
                    return;
                }
                default: {
                    this.jj_la1[123] = this.jj_gen;
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
    public final void DirCommentConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 183);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 233: {
                    this.jj_consume_token(233);
                    SimpleNode jjtn001 = new SimpleNode(this, 184);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 232: {
                    this.jj_consume_token(232);
                    SimpleNode jjtn002 = new SimpleNode(this, 185);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[124] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.DirCommentContents();
            this.jj_consume_token(234);
            SimpleNode jjtn003 = new SimpleNode(this, 186);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void DirCommentContents() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 187);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block16: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 213: 
                    case 214: {
                        break;
                    }
                    default: {
                        this.jj_la1[125] = this.jj_gen;
                        return;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 213: {
                        this.jj_consume_token(213);
                        SimpleNode jjtn001 = new SimpleNode(this, 188);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                            jjtc001 = false;
                            jjtn001.processToken(this.token);
                            continue block16;
                        }
                        finally {
                            if (!jjtc001) continue block16;
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                            continue block16;
                        }
                    }
                    case 214: {
                        this.jj_consume_token(214);
                        SimpleNode jjtn002 = new SimpleNode(this, 189);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                            jjtc002 = false;
                            jjtn002.processToken(this.token);
                            continue block16;
                        }
                        finally {
                            if (!jjtc002) continue block16;
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                            continue block16;
                        }
                    }
                }
                break;
            }
            this.jj_la1[126] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
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
    public final void DirPIConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 190);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 16: {
                    this.jj_consume_token(16);
                    SimpleNode jjtn001 = new SimpleNode(this, 191);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 15: {
                    this.jj_consume_token(15);
                    SimpleNode jjtn002 = new SimpleNode(this, 192);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[127] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(46);
            SimpleNode jjtn003 = new SimpleNode(this, 193);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 238: {
                    this.jj_consume_token(238);
                    SimpleNode jjtn004 = new SimpleNode(this, 194);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                    this.DirPIContents();
                    break;
                }
                default: {
                    this.jj_la1[128] = this.jj_gen;
                }
            }
            this.jj_consume_token(17);
            SimpleNode jjtn005 = new SimpleNode(this, 195);
            boolean jjtc005 = true;
            this.jjtree.openNodeScope(jjtn005);
            try {
                this.jjtree.closeNodeScope((Node)jjtn005, true);
                jjtc005 = false;
                jjtn005.processToken(this.token);
            }
            finally {
                if (jjtc005) {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public final void DirPIContents() throws ParseException {
        block11: {
            jjtn000 = new SimpleNode(this, 196);
            jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            while (true) lbl-1000:
            // 3 sources

            {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 215: {
                        break;
                    }
                    default: {
                        this.jj_la1[129] = this.jj_gen;
                        break block11;
                    }
                }
                this.jj_consume_token(215);
                jjtn001 = new SimpleNode(this, 197);
                jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    continue;
                }
                break;
            }
            ** GOTO lbl-1000
            finally {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CDataSection() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 198);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 191: {
                    this.jj_consume_token(191);
                    SimpleNode jjtn001 = new SimpleNode(this, 199);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 190: {
                    this.jj_consume_token(190);
                    SimpleNode jjtn002 = new SimpleNode(this, 200);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[130] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.CDataSectionContents();
            this.jj_consume_token(192);
            SimpleNode jjtn003 = new SimpleNode(this, 201);
            boolean jjtc003 = true;
            this.jjtree.openNodeScope(jjtn003);
            try {
                this.jjtree.closeNodeScope((Node)jjtn003, true);
                jjtc003 = false;
                jjtn003.processToken(this.token);
            }
            finally {
                if (jjtc003) {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public final void CDataSectionContents() throws ParseException {
        block11: {
            jjtn000 = new SimpleNode(this, 202);
            jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            while (true) lbl-1000:
            // 3 sources

            {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 216: {
                        break;
                    }
                    default: {
                        this.jj_la1[131] = this.jj_gen;
                        break block11;
                    }
                }
                this.jj_consume_token(216);
                jjtn001 = new SimpleNode(this, 203);
                jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                }
                finally {
                    if (!jjtc001) continue;
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    continue;
                }
                break;
            }
            ** GOTO lbl-1000
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
    public final void ComputedConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 204);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 151: {
                    this.CompDocConstructor();
                    return;
                }
                case 84: 
                case 89: {
                    this.CompElemConstructor();
                    return;
                }
                case 85: 
                case 90: {
                    this.CompAttrConstructor();
                    return;
                }
                case 91: {
                    this.CompTextConstructor();
                    return;
                }
                case 88: {
                    this.CompCommentConstructor();
                    return;
                }
                case 86: 
                case 87: {
                    this.CompPIConstructor();
                    return;
                }
                default: {
                    this.jj_la1[132] = this.jj_gen;
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
    public final void CompDocConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 205);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(151);
            SimpleNode jjtn001 = new SimpleNode(this, 206);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.Expr();
            this.jj_consume_token(241);
            SimpleNode jjtn002 = new SimpleNode(this, 69);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CompElemConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 207);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 84: {
                    this.jj_consume_token(84);
                    SimpleNode jjtn001 = new SimpleNode(this, 208);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 89: {
                    this.jj_consume_token(89);
                    SimpleNode jjtn002 = new SimpleNode(this, 209);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.Expr();
                    this.jj_consume_token(241);
                    SimpleNode jjtn003 = new SimpleNode(this, 69);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                    this.jj_consume_token(205);
                    SimpleNode jjtn004 = new SimpleNode(this, 68);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[133] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 15: 
                case 16: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 49: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 101: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 128: 
                case 129: 
                case 134: 
                case 135: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 146: 
                case 147: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 174: 
                case 175: 
                case 187: 
                case 196: 
                case 197: 
                case 232: 
                case 233: 
                case 235: {
                    this.ContentExpr();
                    break;
                }
                default: {
                    this.jj_la1[134] = this.jj_gen;
                }
            }
            this.jj_consume_token(241);
            SimpleNode jjtn005 = new SimpleNode(this, 69);
            boolean jjtc005 = true;
            this.jjtree.openNodeScope(jjtn005);
            try {
                this.jjtree.closeNodeScope((Node)jjtn005, true);
                jjtc005 = false;
                jjtn005.processToken(this.token);
            }
            finally {
                if (jjtc005) {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
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

    public final void ContentExpr() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 210);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Expr();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CompAttrConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 211);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 85: {
                    this.jj_consume_token(85);
                    SimpleNode jjtn001 = new SimpleNode(this, 212);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 90: {
                    this.jj_consume_token(90);
                    SimpleNode jjtn002 = new SimpleNode(this, 213);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.Expr();
                    this.jj_consume_token(241);
                    SimpleNode jjtn003 = new SimpleNode(this, 69);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                    this.jj_consume_token(205);
                    SimpleNode jjtn004 = new SimpleNode(this, 68);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[135] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 15: 
                case 16: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 49: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 101: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 128: 
                case 129: 
                case 134: 
                case 135: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 146: 
                case 147: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 174: 
                case 175: 
                case 187: 
                case 196: 
                case 197: 
                case 232: 
                case 233: 
                case 235: {
                    this.Expr();
                    break;
                }
                default: {
                    this.jj_la1[136] = this.jj_gen;
                }
            }
            this.jj_consume_token(241);
            SimpleNode jjtn005 = new SimpleNode(this, 69);
            boolean jjtc005 = true;
            this.jjtree.openNodeScope(jjtn005);
            try {
                this.jjtree.closeNodeScope((Node)jjtn005, true);
                jjtc005 = false;
                jjtn005.processToken(this.token);
            }
            finally {
                if (jjtc005) {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CompTextConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 214);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(91);
            SimpleNode jjtn001 = new SimpleNode(this, 215);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.Expr();
            this.jj_consume_token(241);
            SimpleNode jjtn002 = new SimpleNode(this, 69);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CompCommentConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 216);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(88);
            SimpleNode jjtn001 = new SimpleNode(this, 217);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.Expr();
            this.jj_consume_token(241);
            SimpleNode jjtn002 = new SimpleNode(this, 69);
            boolean jjtc002 = true;
            this.jjtree.openNodeScope(jjtn002);
            try {
                this.jjtree.closeNodeScope((Node)jjtn002, true);
                jjtc002 = false;
                jjtn002.processToken(this.token);
            }
            finally {
                if (jjtc002) {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void CompPIConstructor() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 218);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 86: {
                    this.jj_consume_token(86);
                    SimpleNode jjtn001 = new SimpleNode(this, 219);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 87: {
                    this.jj_consume_token(87);
                    SimpleNode jjtn002 = new SimpleNode(this, 220);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                    this.Expr();
                    this.jj_consume_token(241);
                    SimpleNode jjtn003 = new SimpleNode(this, 69);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                    this.jj_consume_token(205);
                    SimpleNode jjtn004 = new SimpleNode(this, 68);
                    boolean jjtc004 = true;
                    this.jjtree.openNodeScope(jjtn004);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn004, true);
                        jjtc004 = false;
                        jjtn004.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc004) {
                            this.jjtree.closeNodeScope((Node)jjtn004, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[137] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 15: 
                case 16: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 49: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 101: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 128: 
                case 129: 
                case 134: 
                case 135: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 146: 
                case 147: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 174: 
                case 175: 
                case 187: 
                case 196: 
                case 197: 
                case 232: 
                case 233: 
                case 235: {
                    this.Expr();
                    break;
                }
                default: {
                    this.jj_la1[138] = this.jj_gen;
                }
            }
            this.jj_consume_token(241);
            SimpleNode jjtn005 = new SimpleNode(this, 69);
            boolean jjtc005 = true;
            this.jjtree.openNodeScope(jjtn005);
            try {
                this.jjtree.closeNodeScope((Node)jjtn005, true);
                jjtc005 = false;
                jjtn005.processToken(this.token);
            }
            finally {
                if (jjtc005) {
                    this.jjtree.closeNodeScope((Node)jjtn005, true);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void SingleType() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 221);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.AtomicType();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 130: {
                    this.jj_consume_token(130);
                    SimpleNode jjtn001 = new SimpleNode(this, 222);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[139] = this.jj_gen;
                    break;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void TypeDeclaration() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 223);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(72);
            SimpleNode jjtn001 = new SimpleNode(this, 63);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
            this.SequenceType();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void SequenceType() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 224);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 149: 
                case 150: 
                case 152: 
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 183: 
                case 184: {
                    this.ItemType();
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 130: 
                        case 131: 
                        case 132: {
                            this.OccurrenceIndicator();
                            return;
                        }
                    }
                    this.jj_la1[140] = this.jj_gen;
                    return;
                }
                case 96: {
                    this.jj_consume_token(96);
                    SimpleNode jjtn001 = new SimpleNode(this, 225);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[141] = this.jj_gen;
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
    public final void OccurrenceIndicator() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 130: {
                this.jj_consume_token(130);
                SimpleNode jjtn001 = new SimpleNode(this, 222);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            case 131: {
                this.jj_consume_token(131);
                SimpleNode jjtn002 = new SimpleNode(this, 226);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                    jjtc002 = false;
                    jjtn002.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
            }
            case 132: {
                this.jj_consume_token(132);
                SimpleNode jjtn003 = new SimpleNode(this, 227);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                    jjtc003 = false;
                    jjtn003.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
            }
            default: {
                this.jj_la1[142] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void ItemType() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 183: 
            case 184: {
                this.AtomicType();
                break;
            }
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 149: 
            case 150: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: {
                this.KindTest();
                break;
            }
            case 77: {
                this.jj_consume_token(77);
                SimpleNode jjtn001 = new SimpleNode(this, 228);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                    jjtc001 = false;
                    jjtn001.processToken(this.token);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            default: {
                this.jj_la1[143] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void AtomicType() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 229);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 184: {
                    this.jj_consume_token(184);
                    SimpleNode jjtn001 = new SimpleNode(this, 230);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 183: {
                    this.jj_consume_token(183);
                    SimpleNode jjtn002 = new SimpleNode(this, 231);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[144] = this.jj_gen;
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

    public final void KindTest() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 149: 
            case 150: {
                this.DocumentTest();
                break;
            }
            case 78: 
            case 156: 
            case 157: {
                this.ElementTest();
                break;
            }
            case 79: 
            case 158: {
                this.AttributeTest();
                break;
            }
            case 80: 
            case 159: 
            case 160: {
                this.SchemaElementTest();
                break;
            }
            case 81: 
            case 161: {
                this.SchemaAttributeTest();
                break;
            }
            case 155: 
            case 162: {
                this.PITest();
                break;
            }
            case 153: 
            case 164: {
                this.CommentTest();
                break;
            }
            case 154: 
            case 163: {
                this.TextTest();
                break;
            }
            case 152: 
            case 165: {
                this.AnyKindTest();
                break;
            }
            default: {
                this.jj_la1[145] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void AnyKindTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 232);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 152: {
                    this.jj_consume_token(152);
                    break;
                }
                case 165: {
                    this.jj_consume_token(165);
                    SimpleNode jjtn001 = new SimpleNode(this, 233);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[146] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(139);
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
    public final void DocumentTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 234);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 149: {
                    this.jj_consume_token(149);
                    SimpleNode jjtn001 = new SimpleNode(this, 235);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 150: {
                    this.jj_consume_token(150);
                    SimpleNode jjtn002 = new SimpleNode(this, 236);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[147] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            block8 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 78: 
                case 80: 
                case 156: 
                case 157: 
                case 159: 
                case 160: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 78: 
                        case 156: 
                        case 157: {
                            this.ElementTest();
                            break block8;
                        }
                        case 80: 
                        case 159: 
                        case 160: {
                            this.SchemaElementTest();
                            break block8;
                        }
                    }
                    this.jj_la1[148] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                default: {
                    this.jj_la1[149] = this.jj_gen;
                }
            }
            this.jj_consume_token(139);
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void TextTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 237);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 154: {
                    this.jj_consume_token(154);
                    break;
                }
                case 163: {
                    this.jj_consume_token(163);
                    SimpleNode jjtn001 = new SimpleNode(this, 238);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[150] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(139);
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
    public final void CommentTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 239);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 153: {
                    this.jj_consume_token(153);
                    break;
                }
                case 164: {
                    this.jj_consume_token(164);
                    SimpleNode jjtn001 = new SimpleNode(this, 240);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[151] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(139);
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
    public final void PITest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 241);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 155: {
                    this.jj_consume_token(155);
                    break;
                }
                case 162: {
                    this.jj_consume_token(162);
                    SimpleNode jjtn001 = new SimpleNode(this, 242);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[152] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            block8 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: 
                case 189: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 189: {
                            this.jj_consume_token(189);
                            SimpleNode jjtn002 = new SimpleNode(this, 243);
                            boolean jjtc002 = true;
                            this.jjtree.openNodeScope(jjtn002);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn002, true);
                                jjtc002 = false;
                                jjtn002.processToken(this.token);
                                break block8;
                            }
                            finally {
                                if (jjtc002) {
                                    this.jjtree.closeNodeScope((Node)jjtn002, true);
                                }
                            }
                        }
                        case 5: {
                            this.jj_consume_token(5);
                            SimpleNode jjtn003 = new SimpleNode(this, 244);
                            boolean jjtc003 = true;
                            this.jjtree.openNodeScope(jjtn003);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn003, true);
                                jjtc003 = false;
                                jjtn003.processToken(this.token);
                                break block8;
                            }
                            finally {
                                if (jjtc003) {
                                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                                }
                            }
                        }
                        default: {
                            this.jj_la1[153] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
                default: {
                    this.jj_la1[154] = this.jj_gen;
                }
            }
            this.jj_consume_token(139);
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
    public final void AttributeTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 245);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 79: {
                    this.jj_consume_token(79);
                    SimpleNode jjtn001 = new SimpleNode(this, 246);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 158: {
                    this.jj_consume_token(158);
                    SimpleNode jjtn002 = new SimpleNode(this, 247);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[155] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            block9 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 102: 
                case 185: {
                    this.AttribNameOrWildcard();
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 169: {
                            this.jj_consume_token(169);
                            SimpleNode jjtn003 = new SimpleNode(this, 248);
                            boolean jjtc003 = true;
                            this.jjtree.openNodeScope(jjtn003);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn003, true);
                                jjtc003 = false;
                                jjtn003.processToken(this.token);
                            }
                            finally {
                                if (jjtc003) {
                                    this.jjtree.closeNodeScope((Node)jjtn003, true);
                                }
                            }
                            this.TypeName();
                            break block9;
                        }
                    }
                    this.jj_la1[156] = this.jj_gen;
                    break;
                }
                default: {
                    this.jj_la1[157] = this.jj_gen;
                }
            }
            this.jj_consume_token(139);
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void AttribNameOrWildcard() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 249);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 185: {
                    this.AttributeName();
                    return;
                }
                case 102: {
                    this.jj_consume_token(102);
                    SimpleNode jjtn001 = new SimpleNode(this, 250);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[158] = this.jj_gen;
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
    public final void SchemaAttributeTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 251);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 81: {
                    this.jj_consume_token(81);
                    SimpleNode jjtn001 = new SimpleNode(this, 252);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 161: {
                    this.jj_consume_token(161);
                    SimpleNode jjtn002 = new SimpleNode(this, 253);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[159] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.AttributeDeclaration();
            this.jj_consume_token(139);
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

    public final void AttributeDeclaration() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 254);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.AttributeName();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ElementTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 255);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 78: {
                    this.jj_consume_token(78);
                    SimpleNode jjtn001 = new SimpleNode(this, 256);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 156: {
                    this.jj_consume_token(156);
                    SimpleNode jjtn002 = new SimpleNode(this, 257);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                case 157: {
                    this.jj_consume_token(157);
                    SimpleNode jjtn003 = new SimpleNode(this, 258);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[160] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            block12 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 102: 
                case 185: {
                    this.ElementNameOrWildcard();
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 169: {
                            this.jj_consume_token(169);
                            SimpleNode jjtn004 = new SimpleNode(this, 248);
                            boolean jjtc004 = true;
                            this.jjtree.openNodeScope(jjtn004);
                            try {
                                this.jjtree.closeNodeScope((Node)jjtn004, true);
                                jjtc004 = false;
                                jjtn004.processToken(this.token);
                            }
                            finally {
                                if (jjtc004) {
                                    this.jjtree.closeNodeScope((Node)jjtn004, true);
                                }
                            }
                            this.TypeName();
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 53: {
                                    this.jj_consume_token(53);
                                    SimpleNode jjtn005 = new SimpleNode(this, 259);
                                    boolean jjtc005 = true;
                                    this.jjtree.openNodeScope(jjtn005);
                                    try {
                                        this.jjtree.closeNodeScope((Node)jjtn005, true);
                                        jjtc005 = false;
                                        jjtn005.processToken(this.token);
                                        break block12;
                                    }
                                    finally {
                                        if (jjtc005) {
                                            this.jjtree.closeNodeScope((Node)jjtn005, true);
                                        }
                                    }
                                }
                                default: {
                                    this.jj_la1[161] = this.jj_gen;
                                    break;
                                }
                            }
                            break block12;
                        }
                        default: {
                            this.jj_la1[162] = this.jj_gen;
                            break;
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[163] = this.jj_gen;
                }
            }
            this.jj_consume_token(139);
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void ElementNameOrWildcard() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 260);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 185: {
                    this.ElementName();
                    return;
                }
                case 102: {
                    this.jj_consume_token(102);
                    SimpleNode jjtn001 = new SimpleNode(this, 250);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        return;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[164] = this.jj_gen;
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
    public final void SchemaElementTest() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 261);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 80: {
                    this.jj_consume_token(80);
                    SimpleNode jjtn001 = new SimpleNode(this, 262);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                }
                case 159: {
                    this.jj_consume_token(159);
                    SimpleNode jjtn002 = new SimpleNode(this, 263);
                    boolean jjtc002 = true;
                    this.jjtree.openNodeScope(jjtn002);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                        jjtc002 = false;
                        jjtn002.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc002) {
                            this.jjtree.closeNodeScope((Node)jjtn002, true);
                        }
                    }
                }
                case 160: {
                    this.jj_consume_token(160);
                    SimpleNode jjtn003 = new SimpleNode(this, 264);
                    boolean jjtc003 = true;
                    this.jjtree.openNodeScope(jjtn003);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                        jjtc003 = false;
                        jjtn003.processToken(this.token);
                        break;
                    }
                    finally {
                        if (jjtc003) {
                            this.jjtree.closeNodeScope((Node)jjtn003, true);
                        }
                    }
                }
                default: {
                    this.jj_la1[165] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.ElementDeclaration();
            this.jj_consume_token(139);
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

    public final void ElementDeclaration() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 265);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.ElementName();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void AttributeName() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 266);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(185);
            SimpleNode jjtn001 = new SimpleNode(this, 267);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
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
    public final void ElementName() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 268);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(185);
            SimpleNode jjtn001 = new SimpleNode(this, 267);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
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
    public final void TypeName() throws ParseException {
        SimpleNode jjtn000 = new SimpleNode(this, 269);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(185);
            SimpleNode jjtn001 = new SimpleNode(this, 267);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.processToken(this.token);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{0, -161698, 64, -161762, 256, Integer.MIN_VALUE, 0, 0, 0x40000000, 0x40000000, Integer.MIN_VALUE, 0, 0, 0, 0, 0, 0, 0, 0, 512, 0, 0, 0, 512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1073578014, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1073578014, 0, 0, 0, 0, 1073578014, 1073578014, 0, 0, 1073578014, 1073479680, 183238656, 183238656, 0, 890241024, 890241024, 0, 0, 0, 0, 98334, 30, 14, 1073578014, 0, 1073578014, 98304, 98304, 0, 98304, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 98304, 0, 0, 0, 0, 98304, 0, 0, 0, 0, 0, 0, 1073578014, 0, 1073578014, 0, 1073578014, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_1() {
        jj_la1_1 = new int[]{0, 809631756, 0, 809631756, 0, 809500684, 0, 0, 0, 0, 809500684, 0, -1073741824, 0, 3, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 64, 131072, 0, 64, 0, 0, 0, 0, 131072, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 131072, 131072, 128, 256, 0, 0, 0, 0, 0, 7680, 7680, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 131072, 0, 0, 0, 0, 131072, 131072, 0, 0, 131072, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 131072, 0, 0, 131072, 0, 131072, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 131072, 0, 131072, 0, 131072, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x200000, 0, 0, 0, 0};
    }

    private static void jj_la1_2() {
        jj_la1_2 = new int[]{0, -536887294, 0, -536887294, 0, 0x10000000, -1073741822, -1073741822, 0, 0, 0x10000000, 0, 0, -1073741824, 0, 0, 0, 0x20000001, 0, 0, 0x20000001, 1, 0, 0, 256, 0, 0, 256, 0, 0, 256, 0, 0, 0xFFFC000, 0, 0, 8, 0, 256, 512, 0, 256, 512, 256, 0, 256, 0, 0, 0, 0, 0, 0, 16, 0, 256, 0, 256, 1024, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 64, 64, 160, 160, 2048, 0, 4096, 0, 0, 0, 0xFFFC000, 0, 0, 0, 0, 0xFFFC000, 0xFFFC000, 0, 0, 0xFFFC000, 245760, 245760, 0, 0, 0, 0, 245760, 0, 0, 0, 0xFFC0000, 0, 0, 0xFFFC000, 0, 0xFFFC000, 0xFF00000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xFF00000, 0x2100000, 0xFFFC000, 0x4200000, 0xFFFC000, 0xC00000, 0xFFFC000, 0, 0, 253952, 0, 253952, 0, 245760, 0, 0, 81920, 81920, 0, 0, 0, 0, 0, 32768, 0, 0, 0, 131072, 16384, 0, 0, 0, 0, 65536};
    }

    private static void jj_la1_3() {
        jj_la1_3 = new int[]{0, 1958, 0, 1958, 0, 0, 6, 6, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x8000000, 0, 0, 0, 0, 0, 0, 0, 1952, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 939499520, 939499520, 0, -1073741824, -1073741824, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1952, 806035456, 0x7E00000, 1343488, 0, 416, 1952, 6144, 6144, 416, 416, 416, 0, 0, 0, 0, 416, 416, 416, 0, 0, 0, 0, 1952, 0, 1952, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1952, 0, 1952, 0, 1952, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 64, 0, 0, 0, 0, 64, 64, 0};
    }

    private static void jj_la1_4() {
        jj_la1_4 = new int[]{0, -1249085, 0, -1249085, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1249085, 49152, 49152, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12288, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 32, 0, 0, 0, 131072, 0, 65536, 3, 3, -1310528, 0, 0, 0, 786432, -2096960, -2096960, 0, 0, -2096960, -10485632, -10485632, 0, 128, 0, 0, -10485760, 0, 0, 256, 0x800040, 0, 0, -1249085, 0, -1249085, 0x800000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x800000, 0, -1249085, 0, -1249085, 0, -1249085, 4, 28, -10485760, 28, -10485760, 0, -10485760, 0x1000000, 0x600000, -1342177280, -1342177280, 0x4000000, 0x2000000, 0x8000000, 0, 0, 0x40000000, 0, 0, 0, 0, 0x30000000, 0, 0, 0, 0, Integer.MIN_VALUE};
    }

    private static void jj_la1_5() {
        jj_la1_5 = new int[]{2048, 138461439, 0, 138461439, 0, 0, 0, 0, 0x400000, 0x400000, 0, 0, 0, 0, 0, 0x300000, 0, 0, 256, 0, 0, 0, 256, 0, 0, 0, 0, 0, 0, 256, 0, 0, 256, 134267135, 0, 0, 0, 196608, 0, 0, 256, 0, 0, 0, 256, 0, 196608, 256, 786432, 786432, 0x300000, 0x300000, 0, 0, 0, 256, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 134266943, 0, 0, 0, 0, 134266943, 134266943, 0, 0, 134266943, 32831, 63, 0, 0, 32768, 0, 63, 0, 0, 0, 0x8004000, 0, 0, 134267135, 256, 134267135, 0, 0, 0, -1073741824, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4096, 0, 0, -1073741824, 0, 0, 0, 0, 0, 0, 0, -1073741824, 0, 0, 0, 134267135, 0, 134267135, 0, 134267135, 0, 0, 25165887, 0, 25165887, 0x1800000, 63, 32, 0, 1, 1, 8, 16, 4, 0x20000000, 0x20000000, 0, 512, 0x2000000, 0x2000000, 2, 0, 0, 512, 0x2000000, 0x2000000, 1};
    }

    private static void jj_la1_6() {
        jj_la1_6 = new int[]{0, 48, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12288, 0, 0, 12288, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 0, 0, 0, 0, 48, 48, 0, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 0, 0, 48, 0, 48, 48, 48, 48, 323638, 0, 192, 0, 0, 0, 2048, 651270, 651270, 1241094, 1241094, 0x2000000, 585734, 1110022, 323638, 61446, 0, 0x600000, 0x600000, 0, 0, 0x800000, 0, 0x1000000, 0, 0, 48, 0, 48, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_7() {
        jj_la1_7 = new int[]{0, 2816, 0, 2816, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2816, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2816, 0, 0, 0, 0, 2816, 2816, 0, 0, 2816, 2048, 2048, 0, 0, 0, 0, 2048, 2048, 0, 0, 768, 0, 0, 2816, 0, 2816, 768, 768, 0, 768, 8192, 0, 8192, 8192, 8192, 0, 0, 0, 0, 0, 0, 0, 0, 768, 0, 768, 0, 0, 0, 16384, 0, 0, 0, 0, 0, 2816, 0, 2816, 0, 2816, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public XPath(InputStream stream) {
        this(stream, null);
    }

    public XPath(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new XPathTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 166; ++i) {
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
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 166; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public XPath(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new XPathTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 166; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 166; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public XPath(XPathTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 166; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(XPathTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 166; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private final Token jj_consume_token(int kind) throws ParseException {
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

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.removeAllElements();
        boolean[] la1tokens = new boolean[251];
        for (i = 0; i < 251; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 166; ++i) {
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
                if ((jj_la1_3[i] & 1 << j) != 0) {
                    la1tokens[96 + j] = true;
                }
                if ((jj_la1_4[i] & 1 << j) != 0) {
                    la1tokens[128 + j] = true;
                }
                if ((jj_la1_5[i] & 1 << j) != 0) {
                    la1tokens[160 + j] = true;
                }
                if ((jj_la1_6[i] & 1 << j) != 0) {
                    la1tokens[192 + j] = true;
                }
                if ((jj_la1_7[i] & 1 << j) == 0) continue;
                la1tokens[224 + j] = true;
            }
        }
        for (i = 0; i < 251; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.addElement(this.jj_expentry);
        }
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

    static {
        XPath.jj_la1_0();
        XPath.jj_la1_1();
        XPath.jj_la1_2();
        XPath.jj_la1_3();
        XPath.jj_la1_4();
        XPath.jj_la1_5();
        XPath.jj_la1_6();
        XPath.jj_la1_7();
    }
}

