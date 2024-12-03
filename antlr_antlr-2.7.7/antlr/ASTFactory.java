/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ASTPair;
import antlr.Token;
import antlr.Utils;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

public class ASTFactory {
    protected String theASTNodeType = null;
    protected Class theASTNodeTypeClass = null;
    protected Hashtable tokenTypeToASTClassMap = null;
    static /* synthetic */ Class class$antlr$CommonAST;
    static /* synthetic */ Class class$antlr$Token;

    public ASTFactory() {
    }

    public ASTFactory(Hashtable hashtable) {
        this.setTokenTypeToASTClassMap(hashtable);
    }

    public void setTokenTypeASTNodeType(int n, String string) throws IllegalArgumentException {
        if (this.tokenTypeToASTClassMap == null) {
            this.tokenTypeToASTClassMap = new Hashtable();
        }
        if (string == null) {
            this.tokenTypeToASTClassMap.remove(new Integer(n));
            return;
        }
        Class clazz = null;
        try {
            clazz = Utils.loadClass(string);
            this.tokenTypeToASTClassMap.put(new Integer(n), clazz);
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("Invalid class, " + string);
        }
    }

    public Class getASTNodeType(int n) {
        Class clazz;
        if (this.tokenTypeToASTClassMap != null && (clazz = (Class)this.tokenTypeToASTClassMap.get(new Integer(n))) != null) {
            return clazz;
        }
        if (this.theASTNodeTypeClass != null) {
            return this.theASTNodeTypeClass;
        }
        return class$antlr$CommonAST == null ? (class$antlr$CommonAST = ASTFactory.class$("antlr.CommonAST")) : class$antlr$CommonAST;
    }

    public void addASTChild(ASTPair aSTPair, AST aST) {
        if (aST != null) {
            if (aSTPair.root == null) {
                aSTPair.root = aST;
            } else if (aSTPair.child == null) {
                aSTPair.root.setFirstChild(aST);
            } else {
                aSTPair.child.setNextSibling(aST);
            }
            aSTPair.child = aST;
            aSTPair.advanceChildToEnd();
        }
    }

    public AST create() {
        return this.create(0);
    }

    public AST create(int n) {
        Class clazz = this.getASTNodeType(n);
        AST aST = this.create(clazz);
        if (aST != null) {
            aST.initialize(n, "");
        }
        return aST;
    }

    public AST create(int n, String string) {
        AST aST = this.create(n);
        if (aST != null) {
            aST.initialize(n, string);
        }
        return aST;
    }

    public AST create(int n, String string, String string2) {
        AST aST = this.create(string2);
        if (aST != null) {
            aST.initialize(n, string);
        }
        return aST;
    }

    public AST create(AST aST) {
        if (aST == null) {
            return null;
        }
        AST aST2 = this.create(aST.getType());
        if (aST2 != null) {
            aST2.initialize(aST);
        }
        return aST2;
    }

    public AST create(Token token) {
        AST aST = this.create(token.getType());
        if (aST != null) {
            aST.initialize(token);
        }
        return aST;
    }

    public AST create(Token token, String string) {
        AST aST = this.createUsingCtor(token, string);
        return aST;
    }

    public AST create(String string) {
        Class clazz = null;
        try {
            clazz = Utils.loadClass(string);
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("Invalid class, " + string);
        }
        return this.create(clazz);
    }

    protected AST createUsingCtor(Token token, String string) {
        Class clazz = null;
        AST aST = null;
        try {
            clazz = Utils.loadClass(string);
            Class[] classArray = new Class[]{class$antlr$Token == null ? (class$antlr$Token = ASTFactory.class$("antlr.Token")) : class$antlr$Token};
            try {
                Constructor constructor = clazz.getConstructor(classArray);
                aST = (AST)constructor.newInstance(token);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                aST = this.create(clazz);
                if (aST != null) {
                    aST.initialize(token);
                }
            }
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("Invalid class or can't make instance, " + string);
        }
        return aST;
    }

    protected AST create(Class clazz) {
        AST aST = null;
        try {
            aST = (AST)clazz.newInstance();
        }
        catch (Exception exception) {
            this.error("Can't create AST Node " + clazz.getName());
            return null;
        }
        return aST;
    }

    public AST dup(AST aST) {
        if (aST == null) {
            return null;
        }
        AST aST2 = this.create(aST.getClass());
        aST2.initialize(aST);
        return aST2;
    }

    public AST dupList(AST aST) {
        AST aST2;
        AST aST3 = aST2 = this.dupTree(aST);
        while (aST != null) {
            aST = aST.getNextSibling();
            aST3.setNextSibling(this.dupTree(aST));
            aST3 = aST3.getNextSibling();
        }
        return aST2;
    }

    public AST dupTree(AST aST) {
        AST aST2 = this.dup(aST);
        if (aST != null) {
            aST2.setFirstChild(this.dupList(aST.getFirstChild()));
        }
        return aST2;
    }

    public AST make(AST[] aSTArray) {
        if (aSTArray == null || aSTArray.length == 0) {
            return null;
        }
        AST aST = aSTArray[0];
        AST aST2 = null;
        if (aST != null) {
            aST.setFirstChild(null);
        }
        for (int i = 1; i < aSTArray.length; ++i) {
            if (aSTArray[i] == null) continue;
            if (aST == null) {
                aST = aST2 = aSTArray[i];
            } else if (aST2 == null) {
                aST.setFirstChild(aSTArray[i]);
                aST2 = aST.getFirstChild();
            } else {
                aST2.setNextSibling(aSTArray[i]);
                aST2 = aST2.getNextSibling();
            }
            while (aST2.getNextSibling() != null) {
                aST2 = aST2.getNextSibling();
            }
        }
        return aST;
    }

    public AST make(ASTArray aSTArray) {
        return this.make(aSTArray.array);
    }

    public void makeASTRoot(ASTPair aSTPair, AST aST) {
        if (aST != null) {
            aST.addChild(aSTPair.root);
            aSTPair.child = aSTPair.root;
            aSTPair.advanceChildToEnd();
            aSTPair.root = aST;
        }
    }

    public void setASTNodeClass(Class clazz) {
        if (clazz != null) {
            this.theASTNodeTypeClass = clazz;
            this.theASTNodeType = clazz.getName();
        }
    }

    public void setASTNodeClass(String string) {
        this.theASTNodeType = string;
        try {
            this.theASTNodeTypeClass = Utils.loadClass(string);
        }
        catch (Exception exception) {
            this.error("Can't find/access AST Node type" + string);
        }
    }

    public void setASTNodeType(String string) {
        this.setASTNodeClass(string);
    }

    public Hashtable getTokenTypeToASTClassMap() {
        return this.tokenTypeToASTClassMap;
    }

    public void setTokenTypeToASTClassMap(Hashtable hashtable) {
        this.tokenTypeToASTClassMap = hashtable;
    }

    public void error(String string) {
        System.err.println(string);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

