/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.basetree;

import com.google.template.soy.basetree.Node;

public interface NodeVisitor<N extends Node, R> {
    public R exec(N var1);
}

