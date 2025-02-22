/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.RegExpProxy;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.regexp.GlobData;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.regexp.NativeRegExpInstantiator;
import org.mozilla.javascript.regexp.RECompiled;
import org.mozilla.javascript.regexp.SubString;

public class RegExpImpl
implements RegExpProxy {
    protected String input;
    protected boolean multiline;
    protected SubString[] parens;
    protected SubString lastMatch;
    protected SubString lastParen;
    protected SubString leftContext;
    protected SubString rightContext;

    @Override
    public boolean isRegExp(Scriptable obj) {
        return obj instanceof NativeRegExp;
    }

    @Override
    public Object compileRegExp(Context cx, String source, String flags) {
        return NativeRegExp.compileRE(cx, source, flags, false);
    }

    @Override
    public Scriptable wrapRegExp(Context cx, Scriptable scope, Object compiled) {
        return NativeRegExpInstantiator.withLanguageVersionScopeCompiled(cx.getLanguageVersion(), scope, (RECompiled)compiled);
    }

    @Override
    public Object action(Context cx, Scriptable scope, Scriptable thisObj, Object[] args, int actionType) {
        GlobData data = new GlobData();
        data.mode = actionType;
        data.str = ScriptRuntime.toString(thisObj);
        switch (actionType) {
            case 1: {
                int optarg = Integer.MAX_VALUE;
                if (cx.getLanguageVersion() < 160) {
                    optarg = 1;
                }
                NativeRegExp re = RegExpImpl.createRegExp(cx, scope, args, optarg, false);
                Object rval = RegExpImpl.matchOrReplace(cx, scope, thisObj, args, this, data, re);
                return data.arrayobj == null ? rval : data.arrayobj;
            }
            case 3: {
                int optarg = Integer.MAX_VALUE;
                if (cx.getLanguageVersion() < 160) {
                    optarg = 1;
                }
                NativeRegExp re = RegExpImpl.createRegExp(cx, scope, args, optarg, false);
                return RegExpImpl.matchOrReplace(cx, scope, thisObj, args, this, data, re);
            }
            case 2: {
                Object val;
                boolean useRE;
                boolean bl = useRE = args.length > 0 && args[0] instanceof NativeRegExp;
                if (cx.getLanguageVersion() < 160) {
                    useRE |= args.length > 2;
                }
                NativeRegExp re = null;
                String search = null;
                if (useRE) {
                    re = RegExpImpl.createRegExp(cx, scope, args, 2, true);
                } else {
                    Object arg0 = args.length < 1 ? Undefined.instance : args[0];
                    search = ScriptRuntime.toString(arg0);
                }
                Object arg1 = args.length < 2 ? Undefined.instance : args[1];
                String repstr = null;
                Function lambda = null;
                if (arg1 instanceof Function && (cx.getLanguageVersion() < 200 || !(arg1 instanceof NativeRegExp))) {
                    lambda = (Function)arg1;
                } else {
                    repstr = ScriptRuntime.toString(arg1);
                }
                data.lambda = lambda;
                data.repstr = repstr;
                data.dollar = repstr == null ? -1 : repstr.indexOf(36);
                data.charBuf = null;
                data.leftIndex = 0;
                if (useRE) {
                    val = RegExpImpl.matchOrReplace(cx, scope, thisObj, args, this, data, re);
                } else {
                    String str = data.str;
                    int index = str.indexOf(search);
                    if (index >= 0) {
                        int slen = search.length();
                        this.parens = null;
                        this.lastParen = null;
                        this.leftContext = new SubString(str, 0, index);
                        this.lastMatch = new SubString(str, index, slen);
                        this.rightContext = new SubString(str, index + slen, str.length() - index - slen);
                        val = Boolean.TRUE;
                    } else {
                        val = Boolean.FALSE;
                    }
                }
                if (data.charBuf == null) {
                    if (data.global || val == null || !val.equals(Boolean.TRUE)) {
                        return data.str;
                    }
                    SubString lc = this.leftContext;
                    RegExpImpl.replace_glob(data, cx, scope, this, lc.index, lc.length);
                }
                SubString rc = this.rightContext;
                data.charBuf.append(rc.str, rc.index, rc.index + rc.length);
                return data.charBuf.toString();
            }
        }
        throw Kit.codeBug();
    }

    private static NativeRegExp createRegExp(Context cx, Scriptable scope, Object[] args, int optarg, boolean forceFlat) {
        NativeRegExp re;
        Scriptable topScope = ScriptableObject.getTopLevelScope(scope);
        if (args.length == 0 || args[0] == Undefined.instance) {
            RECompiled compiled = NativeRegExp.compileRE(cx, "", "", false);
            re = NativeRegExpInstantiator.withLanguageVersionScopeCompiled(cx.getLanguageVersion(), topScope, compiled);
        } else if (args[0] instanceof NativeRegExp) {
            re = (NativeRegExp)args[0];
        } else {
            String opt;
            String src = ScriptRuntime.toString(args[0]);
            if (optarg < args.length) {
                args[0] = src;
                opt = ScriptRuntime.toString(args[optarg]);
            } else {
                opt = null;
            }
            RECompiled compiled = NativeRegExp.compileRE(cx, src, opt, forceFlat);
            re = NativeRegExpInstantiator.withLanguageVersionScopeCompiled(cx.getLanguageVersion(), topScope, compiled);
        }
        return re;
    }

    private static Object matchOrReplace(Context cx, Scriptable scope, Scriptable thisObj, Object[] args, RegExpImpl reImpl, GlobData data, NativeRegExp re) {
        String str = data.str;
        data.global = (re.getFlags() & 1) != 0;
        int[] indexp = new int[]{0};
        Object result = null;
        if (data.mode == 3) {
            result = re.executeRegExp(cx, scope, reImpl, str, indexp, 0);
            result = result != null && result.equals(Boolean.TRUE) ? Integer.valueOf(reImpl.leftContext.length) : Integer.valueOf(-1);
        } else if (data.global) {
            re.lastIndex = ScriptRuntime.zeroObj;
            int count = 0;
            while (indexp[0] <= str.length() && (result = re.executeRegExp(cx, scope, reImpl, str, indexp, 0)) != null && result.equals(Boolean.TRUE)) {
                if (data.mode == 1) {
                    RegExpImpl.match_glob(data, cx, scope, count, reImpl);
                } else {
                    if (data.mode != 2) {
                        Kit.codeBug();
                    }
                    SubString lastMatch = reImpl.lastMatch;
                    int leftIndex = data.leftIndex;
                    int leftlen = lastMatch.index - leftIndex;
                    data.leftIndex = lastMatch.index + lastMatch.length;
                    RegExpImpl.replace_glob(data, cx, scope, reImpl, leftIndex, leftlen);
                }
                if (reImpl.lastMatch.length == 0) {
                    if (indexp[0] == str.length()) break;
                    indexp[0] = indexp[0] + 1;
                }
                ++count;
            }
        } else {
            result = re.executeRegExp(cx, scope, reImpl, str, indexp, data.mode == 2 ? 0 : 1);
        }
        return result;
    }

    @Override
    public int find_split(Context cx, Scriptable scope, String target, String separator, Scriptable reObj, int[] ip, int[] matchlen, boolean[] matched, String[][] parensp) {
        int result;
        block5: {
            int i = ip[0];
            int length = target.length();
            int version = cx.getLanguageVersion();
            NativeRegExp re = (NativeRegExp)reObj;
            while (true) {
                int ipsave = ip[0];
                ip[0] = ++i;
                Object ret = re.executeRegExp(cx, scope, this, target, ip, 0);
                if (!Boolean.TRUE.equals(ret)) {
                    ip[0] = ipsave;
                    matchlen[0] = 1;
                    matched[0] = false;
                    return length;
                }
                i = ip[0];
                ip[0] = ipsave;
                matched[0] = true;
                SubString sep = this.lastMatch;
                matchlen[0] = sep.length;
                if (matchlen[0] != 0 || i != ip[0]) break;
                if (i != length) continue;
                if (version == 120) {
                    matchlen[0] = 1;
                    result = i;
                } else {
                    result = -1;
                }
                break block5;
            }
            result = i - matchlen[0];
        }
        int size = this.parens == null ? 0 : this.parens.length;
        parensp[0] = new String[size];
        for (int num = 0; num < size; ++num) {
            SubString parsub = this.getParenSubString(num);
            parensp[0][num] = parsub.toString();
        }
        return result;
    }

    SubString getParenSubString(int i) {
        SubString parsub;
        if (this.parens != null && i < this.parens.length && (parsub = this.parens[i]) != null) {
            return parsub;
        }
        return new SubString();
    }

    private static void match_glob(GlobData mdata, Context cx, Scriptable scope, int count, RegExpImpl reImpl) {
        if (mdata.arrayobj == null) {
            mdata.arrayobj = cx.newArray(scope, 0);
        }
        SubString matchsub = reImpl.lastMatch;
        String matchstr = matchsub.toString();
        mdata.arrayobj.put(count, mdata.arrayobj, (Object)matchstr);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void replace_glob(GlobData rdata, Context cx, Scriptable scope, RegExpImpl reImpl, int leftIndex, int leftlen) {
        int replen;
        String lambdaStr;
        if (rdata.lambda != null) {
            SubString[] parens = reImpl.parens;
            int parenCount = parens == null ? 0 : parens.length;
            Object[] args = new Object[parenCount + 3];
            args[0] = reImpl.lastMatch.toString();
            for (int i = 0; i < parenCount; ++i) {
                SubString sub = parens[i];
                args[i + 1] = sub != null ? sub.toString() : Undefined.instance;
            }
            args[parenCount + 1] = reImpl.leftContext.length;
            args[parenCount + 2] = rdata.str;
            if (reImpl != ScriptRuntime.getRegExpProxy(cx)) {
                Kit.codeBug();
            }
            RegExpImpl re2 = new RegExpImpl();
            re2.multiline = reImpl.multiline;
            re2.input = reImpl.input;
            ScriptRuntime.setRegExpProxy(cx, re2);
            try {
                Scriptable parent = ScriptableObject.getTopLevelScope(scope);
                Object result = rdata.lambda.call(cx, parent, parent, args);
                lambdaStr = ScriptRuntime.toString(result);
            }
            finally {
                ScriptRuntime.setRegExpProxy(cx, reImpl);
            }
            replen = lambdaStr.length();
        } else {
            lambdaStr = null;
            replen = rdata.repstr.length();
            if (rdata.dollar >= 0) {
                int[] skip = new int[1];
                int dp = rdata.dollar;
                do {
                    SubString sub;
                    if ((sub = RegExpImpl.interpretDollar(cx, reImpl, rdata.repstr, dp, skip)) != null) {
                        replen += sub.length - skip[0];
                        dp += skip[0];
                        continue;
                    }
                    ++dp;
                } while ((dp = rdata.repstr.indexOf(36, dp)) >= 0);
            }
        }
        int growth = leftlen + replen + reImpl.rightContext.length;
        StringBuilder charBuf = rdata.charBuf;
        if (charBuf == null) {
            rdata.charBuf = charBuf = new StringBuilder(growth);
        } else {
            charBuf.ensureCapacity(rdata.charBuf.length() + growth);
        }
        charBuf.append(reImpl.leftContext.str, leftIndex, leftIndex + leftlen);
        if (rdata.lambda != null) {
            charBuf.append(lambdaStr);
        } else {
            RegExpImpl.do_replace(rdata, cx, reImpl);
        }
    }

    private static SubString interpretDollar(Context cx, RegExpImpl res, String da, int dp, int[] skip) {
        int version;
        if (da.charAt(dp) != '$') {
            Kit.codeBug();
        }
        if ((version = cx.getLanguageVersion()) != 0 && version <= 140 && dp > 0 && da.charAt(dp - 1) == '\\') {
            return null;
        }
        int daL = da.length();
        if (dp + 1 >= daL) {
            return null;
        }
        char dc = da.charAt(dp + 1);
        if (NativeRegExp.isDigit(dc)) {
            int tmp;
            int cp;
            int num;
            if (version != 0 && version <= 140) {
                if (dc == '0') {
                    return null;
                }
                num = 0;
                cp = dp;
                while (++cp < daL && NativeRegExp.isDigit(dc = da.charAt(cp)) && (tmp = 10 * num + (dc - 48)) >= num) {
                    num = tmp;
                }
            } else {
                num = dc - 48;
                int parenCount = res.parens == null ? 0 : res.parens.length;
                if (num > parenCount) {
                    return null;
                }
                cp = dp + 2;
                if (dp + 2 < daL && NativeRegExp.isDigit(dc = da.charAt(dp + 2)) && (tmp = 10 * num + (dc - 48)) <= parenCount) {
                    ++cp;
                    num = tmp;
                }
                if (num == 0) {
                    return null;
                }
            }
            skip[0] = cp - dp;
            return res.getParenSubString(--num);
        }
        skip[0] = 2;
        switch (dc) {
            case '$': {
                return new SubString("$");
            }
            case '&': {
                return res.lastMatch;
            }
            case '+': {
                return res.lastParen;
            }
            case '`': {
                if (version == 120) {
                    res.leftContext.index = 0;
                    res.leftContext.length = res.lastMatch.index;
                }
                return res.leftContext;
            }
            case '\'': {
                return res.rightContext;
            }
        }
        return null;
    }

    private static void do_replace(GlobData rdata, Context cx, RegExpImpl regExpImpl) {
        int daL;
        StringBuilder charBuf = rdata.charBuf;
        int cp = 0;
        String da = rdata.repstr;
        int dp = rdata.dollar;
        if (dp != -1) {
            int[] skip = new int[1];
            do {
                int len = dp - cp;
                charBuf.append(da.substring(cp, dp));
                cp = dp;
                SubString sub = RegExpImpl.interpretDollar(cx, regExpImpl, da, dp, skip);
                if (sub != null) {
                    len = sub.length;
                    if (len > 0) {
                        charBuf.append(sub.str, sub.index, sub.index + len);
                    }
                    cp += skip[0];
                    dp += skip[0];
                    continue;
                }
                ++dp;
            } while ((dp = da.indexOf(36, dp)) >= 0);
        }
        if ((daL = da.length()) > cp) {
            charBuf.append(da.substring(cp, daL));
        }
    }

    @Override
    public Object js_split(Context cx, Scriptable scope, String target, Object[] args) {
        int match;
        Scriptable test;
        Scriptable result = cx.newArray(scope, 0);
        boolean limited = args.length > 1 && args[1] != Undefined.instance;
        long limit = 0L;
        if (limited) {
            limit = ScriptRuntime.toUint32(args[1]);
            if (limit == 0L) {
                return result;
            }
            if (limit > (long)target.length()) {
                limit = 1 + target.length();
            }
        }
        if (args.length < 1 || args[0] == Undefined.instance) {
            result.put(0, result, (Object)target);
            return result;
        }
        String separator = null;
        int[] matchlen = new int[1];
        Scriptable re = null;
        RegExpProxy reProxy = null;
        if (args[0] instanceof Scriptable && (reProxy = ScriptRuntime.getRegExpProxy(cx)) != null && reProxy.isRegExp(test = (Scriptable)args[0])) {
            re = test;
        }
        if (re == null) {
            separator = ScriptRuntime.toString(args[0]);
            matchlen[0] = separator.length();
        }
        int[] ip = new int[]{0};
        int len = 0;
        boolean[] matched = new boolean[]{false};
        String[][] parens = new String[][]{null};
        int version = cx.getLanguageVersion();
        while (!((match = RegExpImpl.find_split(cx, scope, target, separator, version, reProxy, re, ip, matchlen, matched, parens)) < 0 || limited && (long)len >= limit || match > target.length())) {
            String substr = target.length() == 0 ? target : target.substring(ip[0], match);
            result.put(len, result, (Object)substr);
            ++len;
            if (re != null && matched[0]) {
                int size = parens[0].length;
                for (int num = 0; !(num >= size || limited && (long)len >= limit); ++num) {
                    result.put(len, result, (Object)parens[0][num]);
                    ++len;
                }
                matched[0] = false;
            }
            ip[0] = match + matchlen[0];
            if (version >= 130 || version == 0 || limited || ip[0] != target.length()) continue;
            break;
        }
        return result;
    }

    private static int find_split(Context cx, Scriptable scope, String target, String separator, int version, RegExpProxy reProxy, Scriptable re, int[] ip, int[] matchlen, boolean[] matched, String[][] parensp) {
        int i;
        int length = target.length();
        if (version == 120 && re == null && separator.length() == 1 && separator.charAt(0) == ' ') {
            int j;
            if (i == 0) {
                for (i = ip[0]; i < length && Character.isWhitespace(target.charAt(i)); ++i) {
                }
                ip[0] = i;
            }
            if (i == length) {
                return -1;
            }
            while (i < length && !Character.isWhitespace(target.charAt(i))) {
                ++i;
            }
            for (j = i; j < length && Character.isWhitespace(target.charAt(j)); ++j) {
            }
            matchlen[0] = j - i;
            return i;
        }
        if (i > length) {
            return -1;
        }
        if (re != null) {
            return reProxy.find_split(cx, scope, target, separator, re, ip, matchlen, matched, parensp);
        }
        if (version != 0 && version < 130 && length == 0) {
            return -1;
        }
        if (separator.length() == 0) {
            if (version == 120) {
                if (i == length) {
                    matchlen[0] = 1;
                    return i;
                }
                return i + 1;
            }
            return i == length ? -1 : i + 1;
        }
        if (ip[0] >= length) {
            return length;
        }
        i = target.indexOf(separator, ip[0]);
        return i != -1 ? i : length;
    }
}

