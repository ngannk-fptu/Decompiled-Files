/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function.postscript.operation;

import java.util.LinkedList;

public class Expression
extends LinkedList<Object> {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Expression;
    }
}

