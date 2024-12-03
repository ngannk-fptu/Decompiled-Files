/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import com.sun.pdfview.function.postscript.operation.Abs;
import com.sun.pdfview.function.postscript.operation.Add;
import com.sun.pdfview.function.postscript.operation.And;
import com.sun.pdfview.function.postscript.operation.Atan;
import com.sun.pdfview.function.postscript.operation.Bitshift;
import com.sun.pdfview.function.postscript.operation.Ceiling;
import com.sun.pdfview.function.postscript.operation.Copy;
import com.sun.pdfview.function.postscript.operation.Cvi;
import com.sun.pdfview.function.postscript.operation.Cvr;
import com.sun.pdfview.function.postscript.operation.Div;
import com.sun.pdfview.function.postscript.operation.Dup;
import com.sun.pdfview.function.postscript.operation.Eq;
import com.sun.pdfview.function.postscript.operation.Exch;
import com.sun.pdfview.function.postscript.operation.Exp;
import com.sun.pdfview.function.postscript.operation.False;
import com.sun.pdfview.function.postscript.operation.Floor;
import com.sun.pdfview.function.postscript.operation.Ge;
import com.sun.pdfview.function.postscript.operation.Gt;
import com.sun.pdfview.function.postscript.operation.Idiv;
import com.sun.pdfview.function.postscript.operation.If;
import com.sun.pdfview.function.postscript.operation.IfElse;
import com.sun.pdfview.function.postscript.operation.Index;
import com.sun.pdfview.function.postscript.operation.Le;
import com.sun.pdfview.function.postscript.operation.Ln;
import com.sun.pdfview.function.postscript.operation.Log;
import com.sun.pdfview.function.postscript.operation.Lt;
import com.sun.pdfview.function.postscript.operation.Mod;
import com.sun.pdfview.function.postscript.operation.Mul;
import com.sun.pdfview.function.postscript.operation.Ne;
import com.sun.pdfview.function.postscript.operation.Neg;
import com.sun.pdfview.function.postscript.operation.Not;
import com.sun.pdfview.function.postscript.operation.Or;
import com.sun.pdfview.function.postscript.operation.Pop;
import com.sun.pdfview.function.postscript.operation.PostScriptOperation;
import com.sun.pdfview.function.postscript.operation.PushAsNumber;
import com.sun.pdfview.function.postscript.operation.Roll;
import com.sun.pdfview.function.postscript.operation.Round;
import com.sun.pdfview.function.postscript.operation.Sin;
import com.sun.pdfview.function.postscript.operation.Sqrt;
import com.sun.pdfview.function.postscript.operation.Sub;
import com.sun.pdfview.function.postscript.operation.True;
import com.sun.pdfview.function.postscript.operation.Truncate;
import com.sun.pdfview.function.postscript.operation.Xor;
import java.util.HashMap;
import java.util.Map;

public class OperationSet {
    private Map<String, PostScriptOperation> operationSet = null;
    private static OperationSet instance;

    private OperationSet() {
        this.initOperations();
    }

    public static synchronized OperationSet getInstance() {
        if (instance == null) {
            instance = new OperationSet();
        }
        return instance;
    }

    public PostScriptOperation getOperation(String token) {
        PostScriptOperation result = this.operationSet.get(token.trim().toLowerCase());
        if (result == null) {
            result = new PushAsNumber(token);
        }
        return result;
    }

    private void initOperations() {
        if (this.operationSet == null) {
            this.operationSet = new HashMap<String, PostScriptOperation>();
            this.operationSet.put("abs", new Abs());
            this.operationSet.put("add", new Add());
            this.operationSet.put("atan", new Atan());
            this.operationSet.put("ceiling", new Ceiling());
            this.operationSet.put("cvi", new Cvi());
            this.operationSet.put("cvr", new Cvr());
            this.operationSet.put("div", new Div());
            this.operationSet.put("exp", new Exp());
            this.operationSet.put("floor", new Floor());
            this.operationSet.put("idiv", new Idiv());
            this.operationSet.put("ln", new Ln());
            this.operationSet.put("log", new Log());
            this.operationSet.put("mod", new Mod());
            this.operationSet.put("mul", new Mul());
            this.operationSet.put("neg", new Neg());
            this.operationSet.put("round", new Round());
            this.operationSet.put("sin", new Sin());
            this.operationSet.put("sqrt", new Sqrt());
            this.operationSet.put("sub", new Sub());
            this.operationSet.put("truncate", new Truncate());
            this.operationSet.put("and", new And());
            this.operationSet.put("bitshift", new Bitshift());
            this.operationSet.put("eq", new Eq());
            this.operationSet.put("false", new False());
            this.operationSet.put("ge", new Ge());
            this.operationSet.put("gt", new Gt());
            this.operationSet.put("le", new Le());
            this.operationSet.put("lt", new Lt());
            this.operationSet.put("ne", new Ne());
            this.operationSet.put("not", new Not());
            this.operationSet.put("or", new Or());
            this.operationSet.put("true", new True());
            this.operationSet.put("xor", new Xor());
            this.operationSet.put("if", new If());
            this.operationSet.put("ifelse", new IfElse());
            this.operationSet.put("copy", new Copy());
            this.operationSet.put("dup", new Dup());
            this.operationSet.put("exch", new Exch());
            this.operationSet.put("index", new Index());
            this.operationSet.put("pop", new Pop());
            this.operationSet.put("roll", new Roll());
        }
    }
}

