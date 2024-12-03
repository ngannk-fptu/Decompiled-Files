/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.syntax;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.syntax.Reduction;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

public abstract class CSTNode {
    public int getMeaning() {
        return this.getRoot(true).getMeaning();
    }

    public CSTNode setMeaning(int meaning) {
        this.getRoot().setMeaning(meaning);
        return this;
    }

    public int getType() {
        return this.getRoot(true).getType();
    }

    public boolean canMean(int type) {
        return Types.canMean(this.getMeaning(), type);
    }

    public boolean isA(int type) {
        return Types.ofType(this.getMeaning(), type);
    }

    public boolean isOneOf(int[] types) {
        int meaning = this.getMeaning();
        for (int i = 0; i < types.length; ++i) {
            if (!Types.ofType(meaning, types[i])) continue;
            return true;
        }
        return false;
    }

    public boolean isAllOf(int[] types) {
        int meaning = this.getMeaning();
        for (int i = 0; i < types.length; ++i) {
            if (Types.ofType(meaning, types[i])) continue;
            return false;
        }
        return true;
    }

    public int getMeaningAs(int[] types) {
        for (int i = 0; i < types.length; ++i) {
            if (!this.isA(types[i])) continue;
            return types[i];
        }
        return 0;
    }

    boolean matches(int type) {
        return this.isA(type);
    }

    boolean matches(int type, int child1) {
        return this.isA(type) && this.get(1, true).isA(child1);
    }

    boolean matches(int type, int child1, int child2) {
        return this.matches(type, child1) && this.get(2, true).isA(child2);
    }

    boolean matches(int type, int child1, int child2, int child3) {
        return this.matches(type, child1, child2) && this.get(3, true).isA(child3);
    }

    boolean matches(int type, int child1, int child2, int child3, int child4) {
        return this.matches(type, child1, child2, child3) && this.get(4, true).isA(child4);
    }

    public boolean isEmpty() {
        return false;
    }

    public abstract int size();

    public boolean hasChildren() {
        return this.children() > 0;
    }

    public int children() {
        int size = this.size();
        if (size > 1) {
            return size - 1;
        }
        return 0;
    }

    public abstract CSTNode get(int var1);

    public CSTNode get(int index, boolean safe) {
        CSTNode element = this.get(index);
        if (element == null && safe) {
            element = Token.NULL;
        }
        return element;
    }

    public abstract Token getRoot();

    public Token getRoot(boolean safe) {
        Token root = this.getRoot();
        if (root == null && safe) {
            root = Token.NULL;
        }
        return root;
    }

    public String getRootText() {
        Token root = this.getRoot(true);
        return root.getText();
    }

    public String getDescription() {
        return Types.getDescription(this.getMeaning());
    }

    public int getStartLine() {
        return this.getRoot(true).getStartLine();
    }

    public int getStartColumn() {
        return this.getRoot(true).getStartColumn();
    }

    public void markAsExpression() {
        throw new GroovyBugError("markAsExpression() not supported for this CSTNode type");
    }

    public boolean isAnExpression() {
        return this.isA(1910);
    }

    public CSTNode add(CSTNode element) {
        throw new GroovyBugError("add() not supported for this CSTNode type");
    }

    public void addChildrenOf(CSTNode of) {
        for (int i = 1; i < of.size(); ++i) {
            this.add(of.get(i));
        }
    }

    public CSTNode set(int index, CSTNode element) {
        throw new GroovyBugError("set() not supported for this CSTNode type");
    }

    public abstract Reduction asReduction();

    public String toString() {
        StringWriter string = new StringWriter();
        this.write(new PrintWriter(string));
        string.flush();
        return string.toString();
    }

    public void write(PrintWriter writer) {
        this.write(writer, "");
    }

    protected void write(PrintWriter writer, String indent) {
        writer.print("(");
        if (!this.isEmpty()) {
            String text;
            int length;
            Token root = this.getRoot(true);
            int type = root.getType();
            int meaning = root.getMeaning();
            writer.print(Types.getDescription(type));
            if (meaning != type) {
                writer.print(" as ");
                writer.print(Types.getDescription(meaning));
            }
            if (this.getStartLine() > -1) {
                writer.print(" at " + this.getStartLine() + ":" + this.getStartColumn());
            }
            if ((length = (text = root.getText()).length()) > 0) {
                writer.print(": ");
                if (length > 40) {
                    text = text.substring(0, 17) + "..." + text.substring(length - 17, length);
                }
                writer.print(" \"");
                writer.print(text);
                writer.print("\" ");
            } else if (this.children() > 0) {
                writer.print(": ");
            }
            int count = this.size();
            if (count > 1) {
                writer.println("");
                String indent1 = indent + "  ";
                String indent2 = indent + "   ";
                for (int i = 1; i < count; ++i) {
                    writer.print(indent1);
                    writer.print(i);
                    writer.print(": ");
                    this.get(i, true).write(writer, indent2);
                }
                writer.print(indent);
            }
        }
        if (indent.length() > 0) {
            writer.println(")");
        } else {
            writer.print(")");
        }
    }
}

