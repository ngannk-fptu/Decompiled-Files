/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Serializable;
import java.util.ArrayDeque;

public class ConsString
implements CharSequence,
Serializable {
    private static final long serialVersionUID = -8432806714471372570L;
    private CharSequence left;
    private CharSequence right;
    private final int length;
    private boolean isFlat;

    public ConsString(CharSequence str1, CharSequence str2) {
        this.left = str1;
        this.right = str2;
        this.length = this.left.length() + this.right.length();
        this.isFlat = false;
    }

    private Object writeReplace() {
        return this.toString();
    }

    @Override
    public String toString() {
        return this.isFlat ? (String)this.left : this.flatten();
    }

    private synchronized String flatten() {
        if (!this.isFlat) {
            char[] chars = new char[this.length];
            int charPos = this.length;
            ArrayDeque<CharSequence> stack = new ArrayDeque<CharSequence>();
            stack.addFirst(this.left);
            CharSequence next = this.right;
            do {
                if (next instanceof ConsString) {
                    ConsString casted = (ConsString)next;
                    if (casted.isFlat) {
                        next = casted.left;
                    } else {
                        stack.addFirst(casted.left);
                        next = casted.right;
                        continue;
                    }
                }
                String str = (String)next;
                str.getChars(0, str.length(), chars, charPos -= str.length());
                CharSequence charSequence = next = stack.isEmpty() ? null : (CharSequence)stack.removeFirst();
            } while (next != null);
            this.left = new String(chars);
            this.right = "";
            this.isFlat = true;
        }
        return (String)this.left;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public char charAt(int index) {
        String str = this.isFlat ? (String)this.left : this.flatten();
        return str.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        String str = this.isFlat ? (String)this.left : this.flatten();
        return str.substring(start, end);
    }
}

