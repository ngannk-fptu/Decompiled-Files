/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.StringUtils;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.collections.ASTEnumeration;
import groovyjarjarantlr.collections.impl.ASTEnumerator;
import groovyjarjarantlr.collections.impl.Vector;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

public abstract class BaseAST
implements AST,
Serializable {
    protected BaseAST down;
    protected BaseAST right;
    private static boolean verboseStringConversion = false;
    private static String[] tokenNames = null;

    public void addChild(AST aST) {
        if (aST == null) {
            return;
        }
        BaseAST baseAST = this.down;
        if (baseAST != null) {
            while (baseAST.right != null) {
                baseAST = baseAST.right;
            }
            baseAST.right = (BaseAST)aST;
        } else {
            this.down = (BaseAST)aST;
        }
    }

    public int getNumberOfChildren() {
        BaseAST baseAST = this.down;
        int n = 0;
        if (baseAST != null) {
            n = 1;
            while (baseAST.right != null) {
                baseAST = baseAST.right;
                ++n;
            }
            return n;
        }
        return n;
    }

    private static void doWorkForFindAll(AST aST, Vector vector, AST aST2, boolean bl) {
        for (AST aST3 = aST; aST3 != null; aST3 = aST3.getNextSibling()) {
            if (bl && aST3.equalsTreePartial(aST2) || !bl && aST3.equalsTree(aST2)) {
                vector.appendElement(aST3);
            }
            if (aST3.getFirstChild() == null) continue;
            BaseAST.doWorkForFindAll(aST3.getFirstChild(), vector, aST2, bl);
        }
    }

    public boolean equals(AST aST) {
        if (aST == null) {
            return false;
        }
        if (this.getText() == null && aST.getText() != null || this.getText() != null && aST.getText() == null) {
            return false;
        }
        if (this.getText() == null && aST.getText() == null) {
            return this.getType() == aST.getType();
        }
        return this.getText().equals(aST.getText()) && this.getType() == aST.getType();
    }

    public boolean equalsList(AST aST) {
        AST aST2;
        if (aST == null) {
            return false;
        }
        for (aST2 = this; aST2 != null && aST != null; aST2 = aST2.getNextSibling(), aST = aST.getNextSibling()) {
            if (!aST2.equals(aST)) {
                return false;
            }
            if (!(aST2.getFirstChild() != null ? !aST2.getFirstChild().equalsList(aST.getFirstChild()) : aST.getFirstChild() != null)) continue;
            return false;
        }
        return aST2 == null && aST == null;
    }

    public boolean equalsListPartial(AST aST) {
        AST aST2;
        if (aST == null) {
            return true;
        }
        for (aST2 = this; aST2 != null && aST != null; aST2 = aST2.getNextSibling(), aST = aST.getNextSibling()) {
            if (!aST2.equals(aST)) {
                return false;
            }
            if (aST2.getFirstChild() == null || aST2.getFirstChild().equalsListPartial(aST.getFirstChild())) continue;
            return false;
        }
        return aST2 != null || aST == null;
    }

    public boolean equalsTree(AST aST) {
        if (!this.equals(aST)) {
            return false;
        }
        return !(this.getFirstChild() != null ? !this.getFirstChild().equalsList(aST.getFirstChild()) : aST.getFirstChild() != null);
    }

    public boolean equalsTreePartial(AST aST) {
        if (aST == null) {
            return true;
        }
        if (!this.equals(aST)) {
            return false;
        }
        return this.getFirstChild() == null || this.getFirstChild().equalsListPartial(aST.getFirstChild());
    }

    public ASTEnumeration findAll(AST aST) {
        Vector vector = new Vector(10);
        if (aST == null) {
            return null;
        }
        BaseAST.doWorkForFindAll(this, vector, aST, false);
        return new ASTEnumerator(vector);
    }

    public ASTEnumeration findAllPartial(AST aST) {
        Vector vector = new Vector(10);
        if (aST == null) {
            return null;
        }
        BaseAST.doWorkForFindAll(this, vector, aST, true);
        return new ASTEnumerator(vector);
    }

    public AST getFirstChild() {
        return this.down;
    }

    public AST getNextSibling() {
        return this.right;
    }

    public String getText() {
        return "";
    }

    public int getType() {
        return 0;
    }

    public int getLine() {
        return 0;
    }

    public int getColumn() {
        return 0;
    }

    public abstract void initialize(int var1, String var2);

    public abstract void initialize(AST var1);

    public abstract void initialize(Token var1);

    public void removeChildren() {
        this.down = null;
    }

    public void setFirstChild(AST aST) {
        this.down = (BaseAST)aST;
    }

    public void setNextSibling(AST aST) {
        this.right = (BaseAST)aST;
    }

    public void setText(String string) {
    }

    public void setType(int n) {
    }

    public static void setVerboseStringConversion(boolean bl, String[] stringArray) {
        verboseStringConversion = bl;
        tokenNames = stringArray;
    }

    public static String[] getTokenNames() {
        return tokenNames;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        if (verboseStringConversion && this.getText() != null && !this.getText().equalsIgnoreCase(tokenNames[this.getType()]) && !this.getText().equalsIgnoreCase(StringUtils.stripFrontBack(tokenNames[this.getType()], "\"", "\""))) {
            stringBuffer.append('[');
            stringBuffer.append(this.getText());
            stringBuffer.append(",<");
            stringBuffer.append(tokenNames[this.getType()]);
            stringBuffer.append(">]");
            return stringBuffer.toString();
        }
        return this.getText();
    }

    public String toStringList() {
        BaseAST baseAST = this;
        String string = "";
        if (baseAST.getFirstChild() != null) {
            string = string + " (";
        }
        string = string + " " + this.toString();
        if (baseAST.getFirstChild() != null) {
            string = string + ((BaseAST)baseAST.getFirstChild()).toStringList();
        }
        if (baseAST.getFirstChild() != null) {
            string = string + " )";
        }
        if (baseAST.getNextSibling() != null) {
            string = string + ((BaseAST)baseAST.getNextSibling()).toStringList();
        }
        return string;
    }

    public String toStringTree() {
        BaseAST baseAST = this;
        String string = "";
        if (baseAST.getFirstChild() != null) {
            string = string + " (";
        }
        string = string + " " + this.toString();
        if (baseAST.getFirstChild() != null) {
            string = string + ((BaseAST)baseAST.getFirstChild()).toStringList();
        }
        if (baseAST.getFirstChild() != null) {
            string = string + " )";
        }
        return string;
    }

    public static String decode(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '&') {
                char c2 = string.charAt(i + 1);
                char c3 = string.charAt(i + 2);
                char c4 = string.charAt(i + 3);
                char c5 = string.charAt(i + 4);
                char c6 = string.charAt(i + 5);
                if (c2 == 'a' && c3 == 'm' && c4 == 'p' && c5 == ';') {
                    stringBuffer.append("&");
                    i += 5;
                    continue;
                }
                if (c2 == 'l' && c3 == 't' && c4 == ';') {
                    stringBuffer.append("<");
                    i += 4;
                    continue;
                }
                if (c2 == 'g' && c3 == 't' && c4 == ';') {
                    stringBuffer.append(">");
                    i += 4;
                    continue;
                }
                if (c2 == 'q' && c3 == 'u' && c4 == 'o' && c5 == 't' && c6 == ';') {
                    stringBuffer.append("\"");
                    i += 6;
                    continue;
                }
                if (c2 == 'a' && c3 == 'p' && c4 == 'o' && c5 == 's' && c6 == ';') {
                    stringBuffer.append("'");
                    i += 6;
                    continue;
                }
                stringBuffer.append("&");
                continue;
            }
            stringBuffer.append(c);
        }
        return new String(stringBuffer);
    }

    public static String encode(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        block7: for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '&': {
                    stringBuffer.append("&amp;");
                    continue block7;
                }
                case '<': {
                    stringBuffer.append("&lt;");
                    continue block7;
                }
                case '>': {
                    stringBuffer.append("&gt;");
                    continue block7;
                }
                case '\"': {
                    stringBuffer.append("&quot;");
                    continue block7;
                }
                case '\'': {
                    stringBuffer.append("&apos;");
                    continue block7;
                }
                default: {
                    stringBuffer.append(c);
                }
            }
        }
        return new String(stringBuffer);
    }

    public void xmlSerializeNode(Writer writer) throws IOException {
        StringBuffer stringBuffer = new StringBuffer(100);
        stringBuffer.append("<");
        stringBuffer.append(this.getClass().getName() + " ");
        stringBuffer.append("text=\"" + BaseAST.encode(this.getText()) + "\" type=\"" + this.getType() + "\"/>");
        writer.write(stringBuffer.toString());
    }

    public void xmlSerializeRootOpen(Writer writer) throws IOException {
        StringBuffer stringBuffer = new StringBuffer(100);
        stringBuffer.append("<");
        stringBuffer.append(this.getClass().getName() + " ");
        stringBuffer.append("text=\"" + BaseAST.encode(this.getText()) + "\" type=\"" + this.getType() + "\">\n");
        writer.write(stringBuffer.toString());
    }

    public void xmlSerializeRootClose(Writer writer) throws IOException {
        writer.write("</" + this.getClass().getName() + ">\n");
    }

    public void xmlSerialize(Writer writer) throws IOException {
        for (AST aST = this; aST != null; aST = aST.getNextSibling()) {
            if (aST.getFirstChild() == null) {
                ((BaseAST)aST).xmlSerializeNode(writer);
                continue;
            }
            ((BaseAST)aST).xmlSerializeRootOpen(writer);
            ((BaseAST)aST.getFirstChild()).xmlSerialize(writer);
            ((BaseAST)aST).xmlSerializeRootClose(writer);
        }
    }
}

