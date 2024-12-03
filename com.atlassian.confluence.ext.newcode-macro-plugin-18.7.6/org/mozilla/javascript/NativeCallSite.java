/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class NativeCallSite
extends IdScriptableObject {
    private static final long serialVersionUID = 2688372752566593594L;
    private static final String CALLSITE_TAG = "CallSite";
    private ScriptStackElement element;
    private static final int Id_constructor = 1;
    private static final int Id_getThis = 2;
    private static final int Id_getTypeName = 3;
    private static final int Id_getFunction = 4;
    private static final int Id_getFunctionName = 5;
    private static final int Id_getMethodName = 6;
    private static final int Id_getFileName = 7;
    private static final int Id_getLineNumber = 8;
    private static final int Id_getColumnNumber = 9;
    private static final int Id_getEvalOrigin = 10;
    private static final int Id_isToplevel = 11;
    private static final int Id_isEval = 12;
    private static final int Id_isNative = 13;
    private static final int Id_isConstructor = 14;
    private static final int Id_toString = 15;
    private static final int MAX_PROTOTYPE_ID = 15;

    static void init(Scriptable scope, boolean sealed) {
        NativeCallSite cs = new NativeCallSite();
        cs.exportAsJSClass(15, scope, sealed);
    }

    static NativeCallSite make(Scriptable scope, Scriptable ctorObj) {
        NativeCallSite cs = new NativeCallSite();
        Scriptable proto = (Scriptable)ctorObj.get("prototype", ctorObj);
        cs.setParentScope(scope);
        cs.setPrototype(proto);
        return cs;
    }

    private NativeCallSite() {
    }

    void setElement(ScriptStackElement elt) {
        this.element = elt;
    }

    @Override
    public String getClassName() {
        return CALLSITE_TAG;
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 1: {
                arity = 0;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 0;
                s = "getThis";
                break;
            }
            case 3: {
                arity = 0;
                s = "getTypeName";
                break;
            }
            case 4: {
                arity = 0;
                s = "getFunction";
                break;
            }
            case 5: {
                arity = 0;
                s = "getFunctionName";
                break;
            }
            case 6: {
                arity = 0;
                s = "getMethodName";
                break;
            }
            case 7: {
                arity = 0;
                s = "getFileName";
                break;
            }
            case 8: {
                arity = 0;
                s = "getLineNumber";
                break;
            }
            case 9: {
                arity = 0;
                s = "getColumnNumber";
                break;
            }
            case 10: {
                arity = 0;
                s = "getEvalOrigin";
                break;
            }
            case 11: {
                arity = 0;
                s = "isToplevel";
                break;
            }
            case 12: {
                arity = 0;
                s = "isEval";
                break;
            }
            case 13: {
                arity = 0;
                s = "isNative";
                break;
            }
            case 14: {
                arity = 0;
                s = "isConstructor";
                break;
            }
            case 15: {
                arity = 0;
                s = "toString";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(CALLSITE_TAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(CALLSITE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1: {
                return NativeCallSite.make(scope, f);
            }
            case 5: {
                return NativeCallSite.getFunctionName(thisObj);
            }
            case 7: {
                return NativeCallSite.getFileName(thisObj);
            }
            case 8: {
                return NativeCallSite.getLineNumber(thisObj);
            }
            case 2: 
            case 3: 
            case 4: 
            case 9: {
                return Undefined.instance;
            }
            case 6: {
                return null;
            }
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: {
                return Boolean.FALSE;
            }
            case 15: {
                return NativeCallSite.js_toString(thisObj);
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    public String toString() {
        if (this.element == null) {
            return "";
        }
        return this.element.toString();
    }

    private static Object js_toString(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite)obj;
        StringBuilder sb = new StringBuilder();
        cs.element.renderJavaStyle(sb);
        return sb.toString();
    }

    private static Object getFunctionName(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite)obj;
        return cs.element == null ? null : cs.element.functionName;
    }

    private static Object getFileName(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite)obj;
        return cs.element == null ? null : cs.element.fileName;
    }

    private static Object getLineNumber(Scriptable obj) {
        while (obj != null && !(obj instanceof NativeCallSite)) {
            obj = obj.getPrototype();
        }
        if (obj == null) {
            return NOT_FOUND;
        }
        NativeCallSite cs = (NativeCallSite)obj;
        if (cs.element == null || cs.element.lineNumber < 0) {
            return Undefined.instance;
        }
        return cs.element.lineNumber;
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            case "getThis": {
                id = 2;
                break;
            }
            case "getTypeName": {
                id = 3;
                break;
            }
            case "getFunction": {
                id = 4;
                break;
            }
            case "getFunctionName": {
                id = 5;
                break;
            }
            case "getMethodName": {
                id = 6;
                break;
            }
            case "getFileName": {
                id = 7;
                break;
            }
            case "getLineNumber": {
                id = 8;
                break;
            }
            case "getColumnNumber": {
                id = 9;
                break;
            }
            case "getEvalOrigin": {
                id = 10;
                break;
            }
            case "isToplevel": {
                id = 11;
                break;
            }
            case "isEval": {
                id = 12;
                break;
            }
            case "isNative": {
                id = 13;
                break;
            }
            case "isConstructor": {
                id = 14;
                break;
            }
            case "toString": {
                id = 15;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }
}

