/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.ArrayUtil
 */
package org.tartarus.snowball;

import java.lang.reflect.InvocationTargetException;
import org.apache.lucene.util.ArrayUtil;
import org.tartarus.snowball.Among;

public abstract class SnowballProgram {
    private static final Object[] EMPTY_ARGS = new Object[0];
    private char[] current = new char[8];
    protected int cursor;
    protected int limit;
    protected int limit_backward;
    protected int bra;
    protected int ket;

    protected SnowballProgram() {
        this.setCurrent("");
    }

    public abstract boolean stem();

    public void setCurrent(String value) {
        this.current = value.toCharArray();
        this.cursor = 0;
        this.limit = value.length();
        this.limit_backward = 0;
        this.bra = this.cursor;
        this.ket = this.limit;
    }

    public String getCurrent() {
        return new String(this.current, 0, this.limit);
    }

    public void setCurrent(char[] text, int length) {
        this.current = text;
        this.cursor = 0;
        this.limit = length;
        this.limit_backward = 0;
        this.bra = this.cursor;
        this.ket = this.limit;
    }

    public char[] getCurrentBuffer() {
        return this.current;
    }

    public int getCurrentBufferLength() {
        return this.limit;
    }

    protected void copy_from(SnowballProgram other) {
        this.current = other.current;
        this.cursor = other.cursor;
        this.limit = other.limit;
        this.limit_backward = other.limit_backward;
        this.bra = other.bra;
        this.ket = other.ket;
    }

    protected boolean in_grouping(char[] s, int min, int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        char ch = this.current[this.cursor];
        if (ch > max || ch < min) {
            return false;
        }
        if ((s[(ch = (char)(ch - min)) >> 3] & 1 << (ch & 7)) == 0) {
            return false;
        }
        ++this.cursor;
        return true;
    }

    protected boolean in_grouping_b(char[] s, int min, int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        char ch = this.current[this.cursor - 1];
        if (ch > max || ch < min) {
            return false;
        }
        if ((s[(ch = (char)(ch - min)) >> 3] & 1 << (ch & 7)) == 0) {
            return false;
        }
        --this.cursor;
        return true;
    }

    protected boolean out_grouping(char[] s, int min, int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        char ch = this.current[this.cursor];
        if (ch > max || ch < min) {
            ++this.cursor;
            return true;
        }
        if ((s[(ch = (char)(ch - min)) >> 3] & 1 << (ch & 7)) == 0) {
            ++this.cursor;
            return true;
        }
        return false;
    }

    protected boolean out_grouping_b(char[] s, int min, int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        char ch = this.current[this.cursor - 1];
        if (ch > max || ch < min) {
            --this.cursor;
            return true;
        }
        if ((s[(ch = (char)(ch - min)) >> 3] & 1 << (ch & 7)) == 0) {
            --this.cursor;
            return true;
        }
        return false;
    }

    protected boolean in_range(int min, int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        char ch = this.current[this.cursor];
        if (ch > max || ch < min) {
            return false;
        }
        ++this.cursor;
        return true;
    }

    protected boolean in_range_b(int min, int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        char ch = this.current[this.cursor - 1];
        if (ch > max || ch < min) {
            return false;
        }
        --this.cursor;
        return true;
    }

    protected boolean out_range(int min, int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        char ch = this.current[this.cursor];
        if (ch <= max && ch >= min) {
            return false;
        }
        ++this.cursor;
        return true;
    }

    protected boolean out_range_b(int min, int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        char ch = this.current[this.cursor - 1];
        if (ch <= max && ch >= min) {
            return false;
        }
        --this.cursor;
        return true;
    }

    protected boolean eq_s(int s_size, CharSequence s) {
        if (this.limit - this.cursor < s_size) {
            return false;
        }
        for (int i = 0; i != s_size; ++i) {
            if (this.current[this.cursor + i] == s.charAt(i)) continue;
            return false;
        }
        this.cursor += s_size;
        return true;
    }

    protected boolean eq_s_b(int s_size, CharSequence s) {
        if (this.cursor - this.limit_backward < s_size) {
            return false;
        }
        for (int i = 0; i != s_size; ++i) {
            if (this.current[this.cursor - s_size + i] == s.charAt(i)) continue;
            return false;
        }
        this.cursor -= s_size;
        return true;
    }

    protected boolean eq_v(CharSequence s) {
        return this.eq_s(s.length(), s);
    }

    protected boolean eq_v_b(CharSequence s) {
        return this.eq_s_b(s.length(), s);
    }

    protected int find_among(Among[] v, int v_size) {
        Among w;
        int i = 0;
        int j = v_size;
        int c = this.cursor;
        int l = this.limit;
        int common_i = 0;
        int common_j = 0;
        boolean first_key_inspected = false;
        while (true) {
            int k = i + (j - i >> 1);
            int diff = 0;
            int common = common_i < common_j ? common_i : common_j;
            Among w2 = v[k];
            for (int i2 = common; i2 < w2.s_size; ++i2) {
                if (c + common == l) {
                    diff = -1;
                    break;
                }
                diff = this.current[c + common] - w2.s[i2];
                if (diff != 0) break;
                ++common;
            }
            if (diff < 0) {
                j = k;
                common_j = common;
            } else {
                i = k;
                common_i = common;
            }
            if (j - i > 1) continue;
            if (i > 0 || j == i || first_key_inspected) break;
            first_key_inspected = true;
        }
        do {
            boolean res;
            w = v[i];
            if (common_i < w.s_size) continue;
            this.cursor = c + w.s_size;
            if (w.method == null) {
                return w.result;
            }
            try {
                Object resobj = w.method.invoke((Object)w.methodobject, EMPTY_ARGS);
                res = resobj.toString().equals("true");
            }
            catch (InvocationTargetException e) {
                res = false;
            }
            catch (IllegalAccessException e) {
                res = false;
            }
            this.cursor = c + w.s_size;
            if (!res) continue;
            return w.result;
        } while ((i = w.substring_i) >= 0);
        return 0;
    }

    protected int find_among_b(Among[] v, int v_size) {
        Among w;
        int i = 0;
        int j = v_size;
        int c = this.cursor;
        int lb = this.limit_backward;
        int common_i = 0;
        int common_j = 0;
        boolean first_key_inspected = false;
        while (true) {
            int k = i + (j - i >> 1);
            int diff = 0;
            int common = common_i < common_j ? common_i : common_j;
            Among w2 = v[k];
            for (int i2 = w2.s_size - 1 - common; i2 >= 0; --i2) {
                if (c - common == lb) {
                    diff = -1;
                    break;
                }
                diff = this.current[c - 1 - common] - w2.s[i2];
                if (diff != 0) break;
                ++common;
            }
            if (diff < 0) {
                j = k;
                common_j = common;
            } else {
                i = k;
                common_i = common;
            }
            if (j - i > 1) continue;
            if (i > 0 || j == i || first_key_inspected) break;
            first_key_inspected = true;
        }
        do {
            boolean res;
            w = v[i];
            if (common_i < w.s_size) continue;
            this.cursor = c - w.s_size;
            if (w.method == null) {
                return w.result;
            }
            try {
                Object resobj = w.method.invoke((Object)w.methodobject, EMPTY_ARGS);
                res = resobj.toString().equals("true");
            }
            catch (InvocationTargetException e) {
                res = false;
            }
            catch (IllegalAccessException e) {
                res = false;
            }
            this.cursor = c - w.s_size;
            if (!res) continue;
            return w.result;
        } while ((i = w.substring_i) >= 0);
        return 0;
    }

    protected int replace_s(int c_bra, int c_ket, CharSequence s) {
        int adjustment = s.length() - (c_ket - c_bra);
        int newLength = this.limit + adjustment;
        if (newLength > this.current.length) {
            char[] newBuffer = new char[ArrayUtil.oversize((int)newLength, (int)2)];
            System.arraycopy(this.current, 0, newBuffer, 0, this.limit);
            this.current = newBuffer;
        }
        if (adjustment != 0 && c_ket < this.limit) {
            System.arraycopy(this.current, c_ket, this.current, c_bra + s.length(), this.limit - c_ket);
        }
        for (int i = 0; i < s.length(); ++i) {
            this.current[c_bra + i] = s.charAt(i);
        }
        this.limit += adjustment;
        if (this.cursor >= c_ket) {
            this.cursor += adjustment;
        } else if (this.cursor > c_bra) {
            this.cursor = c_bra;
        }
        return adjustment;
    }

    protected void slice_check() {
        if (this.bra < 0 || this.bra > this.ket || this.ket > this.limit) {
            throw new IllegalArgumentException("faulty slice operation: bra=" + this.bra + ",ket=" + this.ket + ",limit=" + this.limit);
        }
    }

    protected void slice_from(CharSequence s) {
        this.slice_check();
        this.replace_s(this.bra, this.ket, s);
    }

    protected void slice_del() {
        this.slice_from("");
    }

    protected void insert(int c_bra, int c_ket, CharSequence s) {
        int adjustment = this.replace_s(c_bra, c_ket, s);
        if (c_bra <= this.bra) {
            this.bra += adjustment;
        }
        if (c_bra <= this.ket) {
            this.ket += adjustment;
        }
    }

    protected StringBuilder slice_to(StringBuilder s) {
        this.slice_check();
        int len = this.ket - this.bra;
        s.setLength(0);
        s.append(this.current, this.bra, len);
        return s;
    }

    protected StringBuilder assign_to(StringBuilder s) {
        s.setLength(0);
        s.append(this.current, 0, this.limit);
        return s;
    }
}

