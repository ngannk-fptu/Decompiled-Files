/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import com.google.inject.internal.asm.$Label;

class $Handler {
    $Label a;
    $Label b;
    $Label c;
    String d;
    int e;
    $Handler f;

    $Handler() {
    }

    static $Handler a($Handler $Handler, $Label $Label, $Label $Label2) {
        int n;
        if ($Handler == null) {
            return null;
        }
        $Handler.f = $Handler.a($Handler.f, $Label, $Label2);
        int n2 = $Handler.a.c;
        int n3 = $Handler.b.c;
        int n4 = $Label.c;
        int n5 = n = $Label2 == null ? Integer.MAX_VALUE : $Label2.c;
        if (n4 < n3 && n > n2) {
            if (n4 <= n2) {
                if (n >= n3) {
                    $Handler = $Handler.f;
                } else {
                    $Handler.a = $Label2;
                }
            } else if (n >= n3) {
                $Handler.b = $Label;
            } else {
                $Handler $Handler2 = new $Handler();
                $Handler2.a = $Label2;
                $Handler2.b = $Handler.b;
                $Handler2.c = $Handler.c;
                $Handler2.d = $Handler.d;
                $Handler2.e = $Handler.e;
                $Handler2.f = $Handler.f;
                $Handler.b = $Label;
                $Handler.f = $Handler2;
            }
        }
        return $Handler;
    }
}

