/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.regexp.NativeRegExpCallable;
import org.mozilla.javascript.regexp.RECompiled;

public class NativeRegExpInstantiator {
    private NativeRegExpInstantiator() {
    }

    static NativeRegExp withLanguageVersion(int languageVersion) {
        if (languageVersion < 200) {
            return new NativeRegExpCallable();
        }
        return new NativeRegExp();
    }

    static NativeRegExp withLanguageVersionScopeCompiled(int languageVersion, Scriptable scope, RECompiled compiled) {
        if (languageVersion < 200) {
            return new NativeRegExpCallable(scope, compiled);
        }
        return new NativeRegExp(scope, compiled);
    }
}

