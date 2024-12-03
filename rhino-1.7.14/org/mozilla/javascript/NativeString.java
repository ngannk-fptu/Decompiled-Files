/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.text.Collator;
import java.text.Normalizer;
import java.util.Locale;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeStringIterator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ScriptRuntimeES6;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.regexp.NativeRegExp;

final class NativeString
extends IdScriptableObject {
    private static final long serialVersionUID = 920268368584188687L;
    private static final Object STRING_TAG = "String";
    private static final int Id_length = 1;
    private static final int MAX_INSTANCE_ID = 1;
    private static final int ConstructorId_fromCharCode = -1;
    private static final int ConstructorId_fromCodePoint = -2;
    private static final int ConstructorId_raw = -3;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toSource = 3;
    private static final int Id_valueOf = 4;
    private static final int Id_charAt = 5;
    private static final int Id_charCodeAt = 6;
    private static final int Id_indexOf = 7;
    private static final int Id_lastIndexOf = 8;
    private static final int Id_split = 9;
    private static final int Id_substring = 10;
    private static final int Id_toLowerCase = 11;
    private static final int Id_toUpperCase = 12;
    private static final int Id_substr = 13;
    private static final int Id_concat = 14;
    private static final int Id_slice = 15;
    private static final int Id_bold = 16;
    private static final int Id_italics = 17;
    private static final int Id_fixed = 18;
    private static final int Id_strike = 19;
    private static final int Id_small = 20;
    private static final int Id_big = 21;
    private static final int Id_blink = 22;
    private static final int Id_sup = 23;
    private static final int Id_sub = 24;
    private static final int Id_fontsize = 25;
    private static final int Id_fontcolor = 26;
    private static final int Id_link = 27;
    private static final int Id_anchor = 28;
    private static final int Id_equals = 29;
    private static final int Id_equalsIgnoreCase = 30;
    private static final int Id_match = 31;
    private static final int Id_search = 32;
    private static final int Id_replace = 33;
    private static final int Id_localeCompare = 34;
    private static final int Id_toLocaleLowerCase = 35;
    private static final int Id_toLocaleUpperCase = 36;
    private static final int Id_trim = 37;
    private static final int Id_trimLeft = 38;
    private static final int Id_trimRight = 39;
    private static final int Id_includes = 40;
    private static final int Id_startsWith = 41;
    private static final int Id_endsWith = 42;
    private static final int Id_normalize = 43;
    private static final int Id_repeat = 44;
    private static final int Id_codePointAt = 45;
    private static final int Id_padStart = 46;
    private static final int Id_padEnd = 47;
    private static final int SymbolId_iterator = 48;
    private static final int Id_trimStart = 49;
    private static final int Id_trimEnd = 50;
    private static final int MAX_PROTOTYPE_ID = 50;
    private static final int ConstructorId_charAt = -5;
    private static final int ConstructorId_charCodeAt = -6;
    private static final int ConstructorId_indexOf = -7;
    private static final int ConstructorId_lastIndexOf = -8;
    private static final int ConstructorId_split = -9;
    private static final int ConstructorId_substring = -10;
    private static final int ConstructorId_toLowerCase = -11;
    private static final int ConstructorId_toUpperCase = -12;
    private static final int ConstructorId_substr = -13;
    private static final int ConstructorId_concat = -14;
    private static final int ConstructorId_slice = -15;
    private static final int ConstructorId_equalsIgnoreCase = -30;
    private static final int ConstructorId_match = -31;
    private static final int ConstructorId_search = -32;
    private static final int ConstructorId_replace = -33;
    private static final int ConstructorId_localeCompare = -34;
    private static final int ConstructorId_toLocaleLowerCase = -35;
    private CharSequence string;

    static void init(Scriptable scope, boolean sealed) {
        NativeString obj = new NativeString("");
        obj.exportAsJSClass(50, scope, sealed);
    }

    NativeString(CharSequence s) {
        this.string = s;
    }

    @Override
    public String getClassName() {
        return "String";
    }

    @Override
    protected int getMaxInstanceId() {
        return 1;
    }

    @Override
    protected int findInstanceIdInfo(String s) {
        if (s.equals("length")) {
            return NativeString.instanceIdInfo(7, 1);
        }
        return super.findInstanceIdInfo(s);
    }

    @Override
    protected String getInstanceIdName(int id) {
        if (id == 1) {
            return "length";
        }
        return super.getInstanceIdName(id);
    }

    @Override
    protected Object getInstanceIdValue(int id) {
        if (id == 1) {
            return ScriptRuntime.wrapInt(this.string.length());
        }
        return super.getInstanceIdValue(id);
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        this.addIdFunctionProperty(ctor, STRING_TAG, -1, "fromCharCode", 1);
        this.addIdFunctionProperty(ctor, STRING_TAG, -2, "fromCodePoint", 1);
        this.addIdFunctionProperty(ctor, STRING_TAG, -3, "raw", 1);
        this.addIdFunctionProperty(ctor, STRING_TAG, -5, "charAt", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -6, "charCodeAt", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -7, "indexOf", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -8, "lastIndexOf", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -9, "split", 3);
        this.addIdFunctionProperty(ctor, STRING_TAG, -10, "substring", 3);
        this.addIdFunctionProperty(ctor, STRING_TAG, -11, "toLowerCase", 1);
        this.addIdFunctionProperty(ctor, STRING_TAG, -12, "toUpperCase", 1);
        this.addIdFunctionProperty(ctor, STRING_TAG, -13, "substr", 3);
        this.addIdFunctionProperty(ctor, STRING_TAG, -14, "concat", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -15, "slice", 3);
        this.addIdFunctionProperty(ctor, STRING_TAG, -30, "equalsIgnoreCase", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -31, "match", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -32, "search", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -33, "replace", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -34, "localeCompare", 2);
        this.addIdFunctionProperty(ctor, STRING_TAG, -35, "toLocaleLowerCase", 1);
        super.fillConstructorProperties(ctor);
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        if (id == 48) {
            this.initPrototypeMethod(STRING_TAG, id, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
            return;
        }
        String fnName = null;
        switch (id) {
            case 1: {
                arity = 1;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 0;
                s = "toString";
                break;
            }
            case 3: {
                arity = 0;
                s = "toSource";
                break;
            }
            case 4: {
                arity = 0;
                s = "valueOf";
                break;
            }
            case 5: {
                arity = 1;
                s = "charAt";
                break;
            }
            case 6: {
                arity = 1;
                s = "charCodeAt";
                break;
            }
            case 7: {
                arity = 1;
                s = "indexOf";
                break;
            }
            case 8: {
                arity = 1;
                s = "lastIndexOf";
                break;
            }
            case 9: {
                arity = 2;
                s = "split";
                break;
            }
            case 10: {
                arity = 2;
                s = "substring";
                break;
            }
            case 11: {
                arity = 0;
                s = "toLowerCase";
                break;
            }
            case 12: {
                arity = 0;
                s = "toUpperCase";
                break;
            }
            case 13: {
                arity = 2;
                s = "substr";
                break;
            }
            case 14: {
                arity = 1;
                s = "concat";
                break;
            }
            case 15: {
                arity = 2;
                s = "slice";
                break;
            }
            case 16: {
                arity = 0;
                s = "bold";
                break;
            }
            case 17: {
                arity = 0;
                s = "italics";
                break;
            }
            case 18: {
                arity = 0;
                s = "fixed";
                break;
            }
            case 19: {
                arity = 0;
                s = "strike";
                break;
            }
            case 20: {
                arity = 0;
                s = "small";
                break;
            }
            case 21: {
                arity = 0;
                s = "big";
                break;
            }
            case 22: {
                arity = 0;
                s = "blink";
                break;
            }
            case 23: {
                arity = 0;
                s = "sup";
                break;
            }
            case 24: {
                arity = 0;
                s = "sub";
                break;
            }
            case 25: {
                arity = 0;
                s = "fontsize";
                break;
            }
            case 26: {
                arity = 0;
                s = "fontcolor";
                break;
            }
            case 27: {
                arity = 0;
                s = "link";
                break;
            }
            case 28: {
                arity = 0;
                s = "anchor";
                break;
            }
            case 29: {
                arity = 1;
                s = "equals";
                break;
            }
            case 30: {
                arity = 1;
                s = "equalsIgnoreCase";
                break;
            }
            case 31: {
                arity = 1;
                s = "match";
                break;
            }
            case 32: {
                arity = 1;
                s = "search";
                break;
            }
            case 33: {
                arity = 2;
                s = "replace";
                break;
            }
            case 34: {
                arity = 1;
                s = "localeCompare";
                break;
            }
            case 35: {
                arity = 0;
                s = "toLocaleLowerCase";
                break;
            }
            case 36: {
                arity = 0;
                s = "toLocaleUpperCase";
                break;
            }
            case 37: {
                arity = 0;
                s = "trim";
                break;
            }
            case 38: {
                arity = 0;
                s = "trimLeft";
                break;
            }
            case 39: {
                arity = 0;
                s = "trimRight";
                break;
            }
            case 40: {
                arity = 1;
                s = "includes";
                break;
            }
            case 41: {
                arity = 1;
                s = "startsWith";
                break;
            }
            case 42: {
                arity = 1;
                s = "endsWith";
                break;
            }
            case 43: {
                arity = 0;
                s = "normalize";
                break;
            }
            case 44: {
                arity = 1;
                s = "repeat";
                break;
            }
            case 45: {
                arity = 1;
                s = "codePointAt";
                break;
            }
            case 46: {
                arity = 1;
                s = "padStart";
                break;
            }
            case 47: {
                arity = 1;
                s = "padEnd";
                break;
            }
            case 49: {
                arity = 0;
                s = "trimStart";
                break;
            }
            case 50: {
                arity = 0;
                s = "trimEnd";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(STRING_TAG, id, s, fnName, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(STRING_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        block46: while (true) {
            switch (id) {
                case -35: 
                case -34: 
                case -33: 
                case -32: 
                case -31: 
                case -30: 
                case -15: 
                case -14: 
                case -13: 
                case -12: 
                case -11: 
                case -10: 
                case -9: 
                case -8: 
                case -7: 
                case -6: 
                case -5: {
                    if (args.length > 0) {
                        thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(args[0]));
                        Object[] newArgs = new Object[args.length - 1];
                        for (int i = 0; i < newArgs.length; ++i) {
                            newArgs[i] = args[i + 1];
                        }
                        args = newArgs;
                    } else {
                        thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(thisObj));
                    }
                    id = -id;
                    continue block46;
                }
                case -2: {
                    int n = args.length;
                    if (n < 1) {
                        return "";
                    }
                    int[] codePoints = new int[n];
                    for (int i = 0; i != n; ++i) {
                        Object arg = args[i];
                        int codePoint = ScriptRuntime.toInt32(arg);
                        double num = ScriptRuntime.toNumber(arg);
                        if (!ScriptRuntime.eqNumber(num, codePoint) || !Character.isValidCodePoint(codePoint)) {
                            throw ScriptRuntime.rangeError("Invalid code point " + ScriptRuntime.toString(arg));
                        }
                        codePoints[i] = codePoint;
                    }
                    return new String(codePoints, 0, n);
                }
                case -1: {
                    int n = args.length;
                    if (n < 1) {
                        return "";
                    }
                    char[] chars = new char[n];
                    for (int i = 0; i != n; ++i) {
                        chars[i] = ScriptRuntime.toUint16(args[i]);
                    }
                    return new String(chars);
                }
                case -3: {
                    return NativeString.js_raw(cx, scope, args);
                }
                case 1: {
                    CharSequence s = args.length == 0 ? "" : (ScriptRuntime.isSymbol(args[0]) && thisObj != null ? args[0].toString() : ScriptRuntime.toCharSequence(args[0]));
                    if (thisObj == null) {
                        return new NativeString(s);
                    }
                    return s instanceof String ? s : s.toString();
                }
                case 2: 
                case 4: {
                    CharSequence cs = NativeString.realThis((Scriptable)thisObj, (IdFunctionObject)f).string;
                    return cs instanceof String ? cs : cs.toString();
                }
                case 3: {
                    CharSequence s = NativeString.realThis((Scriptable)thisObj, (IdFunctionObject)f).string;
                    return "(new String(\"" + ScriptRuntime.escapeString(s.toString()) + "\"))";
                }
                case 5: 
                case 6: {
                    CharSequence target = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    double pos = ScriptRuntime.toInteger(args, 0);
                    if (pos < 0.0 || pos >= (double)target.length()) {
                        if (id == 5) {
                            return "";
                        }
                        return ScriptRuntime.NaNobj;
                    }
                    char c = target.charAt((int)pos);
                    if (id == 5) {
                        return String.valueOf(c);
                    }
                    return ScriptRuntime.wrapInt(c);
                }
                case 7: {
                    String thisString = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return ScriptRuntime.wrapInt(NativeString.js_indexOf(7, thisString, args));
                }
                case 40: 
                case 41: 
                case 42: {
                    String thisString = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    if (args.length > 0 && args[0] instanceof NativeRegExp) {
                        throw ScriptRuntime.typeErrorById("msg.first.arg.not.regexp", String.class.getSimpleName(), f.getFunctionName());
                    }
                    int idx = NativeString.js_indexOf(id, thisString, args);
                    if (id == 40) {
                        return idx != -1;
                    }
                    if (id == 41) {
                        return idx == 0;
                    }
                    if (id == 42) {
                        return idx != -1;
                    }
                }
                case 46: 
                case 47: {
                    return NativeString.js_pad(cx, thisObj, f, args, id == 46);
                }
                case 8: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return ScriptRuntime.wrapInt(NativeString.js_lastIndexOf(thisStr, args));
                }
                case 9: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return ScriptRuntime.checkRegExpProxy(cx).js_split(cx, scope, thisStr, args);
                }
                case 10: {
                    CharSequence target = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return NativeString.js_substring(cx, target, args);
                }
                case 11: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr.toLowerCase(Locale.ROOT);
                }
                case 12: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr.toUpperCase(Locale.ROOT);
                }
                case 13: {
                    CharSequence target = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return NativeString.js_substr(target, args);
                }
                case 14: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return NativeString.js_concat(thisStr, args);
                }
                case 15: {
                    CharSequence target = ScriptRuntime.toCharSequence(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return NativeString.js_slice(target, args);
                }
                case 16: {
                    return NativeString.tagify(cx, thisObj, f, "b", null, null);
                }
                case 17: {
                    return NativeString.tagify(cx, thisObj, f, "i", null, null);
                }
                case 18: {
                    return NativeString.tagify(cx, thisObj, f, "tt", null, null);
                }
                case 19: {
                    return NativeString.tagify(cx, thisObj, f, "strike", null, null);
                }
                case 20: {
                    return NativeString.tagify(cx, thisObj, f, "small", null, null);
                }
                case 21: {
                    return NativeString.tagify(cx, thisObj, f, "big", null, null);
                }
                case 22: {
                    return NativeString.tagify(cx, thisObj, f, "blink", null, null);
                }
                case 23: {
                    return NativeString.tagify(cx, thisObj, f, "sup", null, null);
                }
                case 24: {
                    return NativeString.tagify(cx, thisObj, f, "sub", null, null);
                }
                case 25: {
                    return NativeString.tagify(cx, thisObj, f, "font", "size", args);
                }
                case 26: {
                    return NativeString.tagify(cx, thisObj, f, "font", "color", args);
                }
                case 27: {
                    return NativeString.tagify(cx, thisObj, f, "a", "href", args);
                }
                case 28: {
                    return NativeString.tagify(cx, thisObj, f, "a", "name", args);
                }
                case 29: 
                case 30: {
                    String s1 = ScriptRuntime.toString(thisObj);
                    String s2 = ScriptRuntime.toString(args, 0);
                    return ScriptRuntime.wrapBoolean(id == 29 ? s1.equals(s2) : s1.equalsIgnoreCase(s2));
                }
                case 31: 
                case 32: 
                case 33: {
                    int actionType = id == 31 ? 1 : (id == 32 ? 3 : 2);
                    ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f);
                    return ScriptRuntime.checkRegExpProxy(cx).action(cx, scope, thisObj, args, actionType);
                }
                case 34: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    Collator collator = Collator.getInstance(cx.getLocale());
                    collator.setStrength(3);
                    collator.setDecomposition(1);
                    return ScriptRuntime.wrapNumber(collator.compare(thisStr, ScriptRuntime.toString(args, 0)));
                }
                case 35: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr.toLowerCase(cx.getLocale());
                }
                case 36: {
                    String thisStr = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    return thisStr.toUpperCase(cx.getLocale());
                }
                case 37: {
                    int end;
                    int start;
                    String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    char[] chars = str.toCharArray();
                    for (start = 0; start < chars.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[start]); ++start) {
                    }
                    for (end = chars.length; end > start && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[end - 1]); --end) {
                    }
                    return str.substring(start, end);
                }
                case 38: 
                case 49: {
                    int start;
                    String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    char[] chars = str.toCharArray();
                    for (start = 0; start < chars.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[start]); ++start) {
                    }
                    int end = chars.length;
                    return str.substring(start, end);
                }
                case 39: 
                case 50: {
                    int end;
                    String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    char[] chars = str.toCharArray();
                    int start = 0;
                    for (end = chars.length; end > start && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[end - 1]); --end) {
                    }
                    return str.substring(start, end);
                }
                case 43: {
                    Normalizer.Form form;
                    if (args.length == 0 || Undefined.isUndefined(args[0])) {
                        return Normalizer.normalize(ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f)), Normalizer.Form.NFC);
                    }
                    String formStr = ScriptRuntime.toString(args, 0);
                    if (Normalizer.Form.NFD.name().equals(formStr)) {
                        form = Normalizer.Form.NFD;
                    } else if (Normalizer.Form.NFKC.name().equals(formStr)) {
                        form = Normalizer.Form.NFKC;
                    } else if (Normalizer.Form.NFKD.name().equals(formStr)) {
                        form = Normalizer.Form.NFKD;
                    } else if (Normalizer.Form.NFC.name().equals(formStr)) {
                        form = Normalizer.Form.NFC;
                    } else {
                        throw ScriptRuntime.rangeError("The normalization form should be one of 'NFC', 'NFD', 'NFKC', 'NFKD'.");
                    }
                    return Normalizer.normalize(ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f)), form);
                }
                case 44: {
                    return NativeString.js_repeat(cx, thisObj, f, args);
                }
                case 45: {
                    String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                    double cnt = ScriptRuntime.toInteger(args, 0);
                    return cnt < 0.0 || cnt >= (double)str.length() ? Undefined.instance : Integer.valueOf(str.codePointAt((int)cnt));
                }
                case 48: {
                    return new NativeStringIterator(scope, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
                }
            }
            break;
        }
        throw new IllegalArgumentException("String.prototype has no method: " + f.getFunctionName());
    }

    private static NativeString realThis(Scriptable thisObj, IdFunctionObject f) {
        return NativeString.ensureType(thisObj, NativeString.class, f);
    }

    private static String tagify(Context cx, Scriptable thisObj, IdFunctionObject f, String tag, String attribute, Object[] args) {
        String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
        StringBuilder result = new StringBuilder();
        result.append('<').append(tag);
        if (attribute != null && attribute.length() > 0) {
            String attributeValue = ScriptRuntime.toString(args, 0);
            attributeValue = attributeValue.replace("\"", "&quot;");
            result.append(' ').append(attribute).append("=\"").append(attributeValue).append('\"');
        }
        result.append('>').append(str).append("</").append(tag).append('>');
        return result.toString();
    }

    public CharSequence toCharSequence() {
        return this.string;
    }

    public String toString() {
        return this.string instanceof String ? (String)this.string : this.string.toString();
    }

    @Override
    public Object get(int index, Scriptable start) {
        if (0 <= index && index < this.string.length()) {
            return String.valueOf(this.string.charAt(index));
        }
        return super.get(index, start);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        if (0 <= index && index < this.string.length()) {
            return;
        }
        super.put(index, start, value);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        if (0 <= index && index < this.string.length()) {
            return true;
        }
        return super.has(index, start);
    }

    @Override
    public int getAttributes(int index) {
        if (0 <= index && index < this.string.length()) {
            int attribs = 5;
            if (Context.getContext().getLanguageVersion() < 200) {
                attribs |= 2;
            }
            return attribs;
        }
        return super.getAttributes(index);
    }

    @Override
    protected Object[] getIds(boolean nonEnumerable, boolean getSymbols) {
        Context cx = Context.getCurrentContext();
        if (cx != null && cx.getLanguageVersion() >= 200) {
            int i;
            Object[] sids = super.getIds(nonEnumerable, getSymbols);
            Object[] a = new Object[sids.length + this.string.length()];
            for (i = 0; i < this.string.length(); ++i) {
                a[i] = i;
            }
            System.arraycopy(sids, 0, a, i, sids.length);
            return a;
        }
        return super.getIds(nonEnumerable, getSymbols);
    }

    @Override
    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        if (!(id instanceof Symbol) && cx != null && cx.getLanguageVersion() >= 200) {
            ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
            if (s.stringId == null && 0 <= s.index && s.index < this.string.length()) {
                String value = String.valueOf(this.string.charAt(s.index));
                return this.defaultIndexPropertyDescriptor(value);
            }
        }
        return super.getOwnPropertyDescriptor(cx, id);
    }

    private ScriptableObject defaultIndexPropertyDescriptor(Object value) {
        Scriptable scope = this.getParentScope();
        if (scope == null) {
            scope = this;
        }
        NativeObject desc = new NativeObject();
        ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
        desc.defineProperty("value", value, 0);
        desc.defineProperty("writable", (Object)Boolean.FALSE, 0);
        desc.defineProperty("enumerable", (Object)Boolean.TRUE, 0);
        desc.defineProperty("configurable", (Object)Boolean.FALSE, 0);
        return desc;
    }

    private static int js_indexOf(int methodId, String target, Object[] args) {
        String searchStr = ScriptRuntime.toString(args, 0);
        double position = ScriptRuntime.toInteger(args, 1);
        if (methodId != 41 && methodId != 42 && searchStr.length() == 0) {
            return position > (double)target.length() ? target.length() : (int)position;
        }
        if (methodId != 41 && methodId != 42 && position > (double)target.length()) {
            return -1;
        }
        if (position < 0.0) {
            position = 0.0;
        } else if (position > (double)target.length()) {
            position = target.length();
        } else if (methodId == 42 && (Double.isNaN(position) || position > (double)target.length())) {
            position = target.length();
        }
        if (42 == methodId) {
            if (args.length == 0 || args.length == 1 || args.length == 2 && args[1] == Undefined.instance) {
                position = target.length();
            }
            return target.substring(0, (int)position).endsWith(searchStr) ? 0 : -1;
        }
        return methodId == 41 ? (target.startsWith(searchStr, (int)position) ? 0 : -1) : target.indexOf(searchStr, (int)position);
    }

    private static int js_lastIndexOf(String target, Object[] args) {
        String search = ScriptRuntime.toString(args, 0);
        double end = ScriptRuntime.toNumber(args, 1);
        if (Double.isNaN(end) || end > (double)target.length()) {
            end = target.length();
        } else if (end < 0.0) {
            end = 0.0;
        }
        return target.lastIndexOf(search, (int)end);
    }

    private static CharSequence js_substring(Context cx, CharSequence target, Object[] args) {
        double end;
        int length = target.length();
        double start = ScriptRuntime.toInteger(args, 0);
        if (start < 0.0) {
            start = 0.0;
        } else if (start > (double)length) {
            start = length;
        }
        if (args.length <= 1 || args[1] == Undefined.instance) {
            end = length;
        } else {
            end = ScriptRuntime.toInteger(args[1]);
            if (end < 0.0) {
                end = 0.0;
            } else if (end > (double)length) {
                end = length;
            }
            if (end < start) {
                if (cx.getLanguageVersion() != 120) {
                    double temp = start;
                    start = end;
                    end = temp;
                } else {
                    end = start;
                }
            }
        }
        return target.subSequence((int)start, (int)end);
    }

    int getLength() {
        return this.string.length();
    }

    private static CharSequence js_substr(CharSequence target, Object[] args) {
        Object lengthArg;
        if (args.length < 1) {
            return target;
        }
        double begin = ScriptRuntime.toInteger(args[0]);
        int length = target.length();
        if (begin < 0.0) {
            if ((begin += (double)length) < 0.0) {
                begin = 0.0;
            }
        } else if (begin > (double)length) {
            begin = length;
        }
        double end = length;
        if (args.length > 1 && !Undefined.isUndefined(lengthArg = args[1])) {
            end = ScriptRuntime.toInteger(lengthArg);
            if (end < 0.0) {
                end = 0.0;
            }
            if ((end += begin) > (double)length) {
                end = length;
            }
        }
        return target.subSequence((int)begin, (int)end);
    }

    private static String js_concat(String target, Object[] args) {
        int N = args.length;
        if (N == 0) {
            return target;
        }
        if (N == 1) {
            String arg = ScriptRuntime.toString(args[0]);
            return target.concat(arg);
        }
        int size = target.length();
        String[] argsAsStrings = new String[N];
        for (int i = 0; i != N; ++i) {
            String s;
            argsAsStrings[i] = s = ScriptRuntime.toString(args[i]);
            size += s.length();
        }
        StringBuilder result = new StringBuilder(size);
        result.append(target);
        for (int i = 0; i != N; ++i) {
            result.append(argsAsStrings[i]);
        }
        return result.toString();
    }

    private static CharSequence js_slice(CharSequence target, Object[] args) {
        double end;
        double begin = args.length < 1 ? 0.0 : ScriptRuntime.toInteger(args[0]);
        int length = target.length();
        if (begin < 0.0) {
            if ((begin += (double)length) < 0.0) {
                begin = 0.0;
            }
        } else if (begin > (double)length) {
            begin = length;
        }
        if (args.length < 2 || args[1] == Undefined.instance) {
            end = length;
        } else {
            end = ScriptRuntime.toInteger(args[1]);
            if (end < 0.0) {
                if ((end += (double)length) < 0.0) {
                    end = 0.0;
                }
            } else if (end > (double)length) {
                end = length;
            }
            if (end < begin) {
                end = begin;
            }
        }
        return target.subSequence((int)begin, (int)end);
    }

    private static String js_repeat(Context cx, Scriptable thisObj, IdFunctionObject f, Object[] args) {
        int i;
        String str = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
        double cnt = ScriptRuntime.toInteger(args, 0);
        if (cnt < 0.0 || cnt == Double.POSITIVE_INFINITY) {
            throw ScriptRuntime.rangeError("Invalid count value");
        }
        if (cnt == 0.0 || str.length() == 0) {
            return "";
        }
        long size = (long)str.length() * (long)cnt;
        if (cnt > 2.147483647E9 || size > Integer.MAX_VALUE) {
            throw ScriptRuntime.rangeError("Invalid size or count value");
        }
        StringBuilder retval = new StringBuilder((int)size);
        retval.append(str);
        int icnt = (int)cnt;
        for (i = 1; i <= icnt / 2; i *= 2) {
            retval.append((CharSequence)retval);
        }
        if (i < icnt) {
            retval.append(retval.substring(0, str.length() * (icnt - i)));
        }
        return retval.toString();
    }

    private static String js_pad(Context cx, Scriptable thisObj, IdFunctionObject f, Object[] args, boolean atStart) {
        String pad = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, f));
        long intMaxLength = ScriptRuntime.toLength(args, 0);
        if (intMaxLength <= (long)pad.length()) {
            return pad;
        }
        String filler = " ";
        if (args.length >= 2 && !Undefined.isUndefined(args[1]) && (filler = ScriptRuntime.toString(args[1])).length() < 1) {
            return pad;
        }
        int fillLen = (int)(intMaxLength - (long)pad.length());
        StringBuilder concat = new StringBuilder();
        do {
            concat.append(filler);
        } while (concat.length() < fillLen);
        concat.setLength(fillLen);
        if (atStart) {
            return concat.append(pad).toString();
        }
        return concat.insert(0, pad).toString();
    }

    @Override
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 48;
        }
        return 0;
    }

    private static CharSequence js_raw(Context cx, Scriptable scope, Object[] args) {
        Object arg0 = args.length > 0 ? args[0] : Undefined.instance;
        Scriptable cooked = ScriptRuntime.toObject(cx, scope, arg0);
        Object rawValue = ScriptRuntime.getObjectProp(cooked, "raw", cx);
        Scriptable raw = ScriptRuntime.toObject(cx, scope, rawValue);
        long rawLength = NativeArray.getLengthProperty(cx, raw);
        if (rawLength > Integer.MAX_VALUE) {
            throw ScriptRuntime.rangeError("raw.length > " + Integer.toString(Integer.MAX_VALUE));
        }
        int literalSegments = (int)rawLength;
        if (literalSegments <= 0) {
            return "";
        }
        StringBuilder elements = new StringBuilder();
        int nextIndex = 0;
        while (true) {
            Object next = ScriptRuntime.getObjectIndex(raw, nextIndex, cx);
            String nextSeg = ScriptRuntime.toString(next);
            elements.append(nextSeg);
            if (++nextIndex == literalSegments) break;
            if (args.length <= nextIndex) continue;
            next = args[nextIndex];
            String nextSub = ScriptRuntime.toString(next);
            elements.append(nextSub);
        }
        return elements;
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            case "toString": {
                id = 2;
                break;
            }
            case "toSource": {
                id = 3;
                break;
            }
            case "valueOf": {
                id = 4;
                break;
            }
            case "charAt": {
                id = 5;
                break;
            }
            case "charCodeAt": {
                id = 6;
                break;
            }
            case "indexOf": {
                id = 7;
                break;
            }
            case "lastIndexOf": {
                id = 8;
                break;
            }
            case "split": {
                id = 9;
                break;
            }
            case "substring": {
                id = 10;
                break;
            }
            case "toLowerCase": {
                id = 11;
                break;
            }
            case "toUpperCase": {
                id = 12;
                break;
            }
            case "substr": {
                id = 13;
                break;
            }
            case "concat": {
                id = 14;
                break;
            }
            case "slice": {
                id = 15;
                break;
            }
            case "bold": {
                id = 16;
                break;
            }
            case "italics": {
                id = 17;
                break;
            }
            case "fixed": {
                id = 18;
                break;
            }
            case "strike": {
                id = 19;
                break;
            }
            case "small": {
                id = 20;
                break;
            }
            case "big": {
                id = 21;
                break;
            }
            case "blink": {
                id = 22;
                break;
            }
            case "sup": {
                id = 23;
                break;
            }
            case "sub": {
                id = 24;
                break;
            }
            case "fontsize": {
                id = 25;
                break;
            }
            case "fontcolor": {
                id = 26;
                break;
            }
            case "link": {
                id = 27;
                break;
            }
            case "anchor": {
                id = 28;
                break;
            }
            case "equals": {
                id = 29;
                break;
            }
            case "equalsIgnoreCase": {
                id = 30;
                break;
            }
            case "match": {
                id = 31;
                break;
            }
            case "search": {
                id = 32;
                break;
            }
            case "replace": {
                id = 33;
                break;
            }
            case "localeCompare": {
                id = 34;
                break;
            }
            case "toLocaleLowerCase": {
                id = 35;
                break;
            }
            case "toLocaleUpperCase": {
                id = 36;
                break;
            }
            case "trim": {
                id = 37;
                break;
            }
            case "trimLeft": {
                id = 38;
                break;
            }
            case "trimRight": {
                id = 39;
                break;
            }
            case "includes": {
                id = 40;
                break;
            }
            case "startsWith": {
                id = 41;
                break;
            }
            case "endsWith": {
                id = 42;
                break;
            }
            case "normalize": {
                id = 43;
                break;
            }
            case "repeat": {
                id = 44;
                break;
            }
            case "codePointAt": {
                id = 45;
                break;
            }
            case "padStart": {
                id = 46;
                break;
            }
            case "padEnd": {
                id = 47;
                break;
            }
            case "trimStart": {
                id = 49;
                break;
            }
            case "trimEnd": {
                id = 50;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }
}

