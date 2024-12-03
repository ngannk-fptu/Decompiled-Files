/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.util.Map;

public class WildcardHelper {
    protected static final int MATCH_FILE = -1;
    protected static final int MATCH_PATH = -2;
    protected static final int MATCH_BEGIN = -4;
    protected static final int MATCH_THEEND = -5;
    protected static final int MATCH_END = -3;

    public int[] compilePattern(String data) {
        int[] expr = new int[data.length() + 2];
        char[] buff = data.toCharArray();
        int y = 0;
        boolean slash = false;
        expr[y++] = -4;
        if (buff.length > 0) {
            if (buff[0] == '\\') {
                slash = true;
            } else {
                expr[y++] = buff[0] == '*' ? -1 : buff[0];
            }
            for (int x = 1; x < buff.length; ++x) {
                if (slash) {
                    expr[y++] = buff[x];
                    slash = false;
                    continue;
                }
                if (buff[x] == '\\') {
                    slash = true;
                    continue;
                }
                if (buff[x] == '*') {
                    if (expr[y - 1] <= -1) {
                        expr[y - 1] = -2;
                        continue;
                    }
                    expr[y++] = -1;
                    continue;
                }
                expr[y++] = buff[x];
            }
        }
        expr[y] = -5;
        return expr;
    }

    public boolean match(Map map, String data, int[] expr) {
        if (map == null) {
            throw new NullPointerException("No map provided");
        }
        if (data == null) {
            throw new NullPointerException("No data provided");
        }
        if (expr == null) {
            throw new NullPointerException("No pattern expression provided");
        }
        char[] buff = data.toCharArray();
        char[] rslt = new char[expr.length + buff.length];
        int charpos = 0;
        int exprpos = 0;
        int buffpos = 0;
        int rsltpos = 0;
        int mcount = 0;
        map.put(Integer.toString(mcount), data);
        boolean matchBegin = false;
        if (expr[charpos] == -4) {
            matchBegin = true;
            exprpos = ++charpos;
        }
        while (expr[charpos] >= 0) {
            ++charpos;
        }
        int exprchr = expr[charpos];
        while (true) {
            int offset;
            if (matchBegin) {
                if (!this.matchArray(expr, exprpos, charpos, buff, buffpos)) {
                    return false;
                }
                matchBegin = false;
            } else {
                offset = this.indexOfArray(expr, exprpos, charpos, buff, buffpos);
                if (offset < 0) {
                    return false;
                }
            }
            buffpos += charpos - exprpos;
            if (exprchr == -3) {
                if (rsltpos > 0) {
                    map.put(Integer.toString(++mcount), new String(rslt, 0, rsltpos));
                }
                return true;
            }
            if (exprchr == -5) {
                if (rsltpos > 0) {
                    map.put(Integer.toString(++mcount), new String(rslt, 0, rsltpos));
                }
                return buffpos == buff.length;
            }
            exprpos = ++charpos;
            while (expr[charpos] >= 0) {
                ++charpos;
            }
            int prevchr = exprchr;
            exprchr = expr[charpos];
            int n = offset = prevchr == -1 ? this.indexOfArray(expr, exprpos, charpos, buff, buffpos) : this.lastIndexOfArray(expr, exprpos, charpos, buff, buffpos);
            if (offset < 0) {
                return false;
            }
            if (prevchr == -2) {
                while (buffpos < offset) {
                    rslt[rsltpos++] = buff[buffpos++];
                }
            } else {
                while (buffpos < offset) {
                    if (buff[buffpos] == '/') {
                        return false;
                    }
                    rslt[rsltpos++] = buff[buffpos++];
                }
            }
            map.put(Integer.toString(++mcount), new String(rslt, 0, rsltpos));
            rsltpos = 0;
        }
    }

    protected int indexOfArray(int[] r, int rpos, int rend, char[] d, int dpos) {
        if (rend < rpos) {
            throw new IllegalArgumentException("rend < rpos");
        }
        if (rend == rpos) {
            return d.length;
        }
        if (rend - rpos == 1) {
            for (int x = dpos; x < d.length; ++x) {
                if (r[rpos] != d[x]) continue;
                return x;
            }
        }
        while (dpos + rend - rpos <= d.length) {
            int y = dpos;
            for (int x = rpos; x <= rend; ++x) {
                if (x == rend) {
                    return dpos;
                }
                if (r[x] != d[y++]) break;
            }
            ++dpos;
        }
        return -1;
    }

    protected int lastIndexOfArray(int[] r, int rpos, int rend, char[] d, int dpos) {
        if (rend < rpos) {
            throw new IllegalArgumentException("rend < rpos");
        }
        if (rend == rpos) {
            return d.length;
        }
        if (rend - rpos == 1) {
            for (int x = d.length - 1; x > dpos; --x) {
                if (r[rpos] != d[x]) continue;
                return x;
            }
        }
        block1: for (int l = d.length - (rend - rpos); l >= dpos; --l) {
            int y = l;
            for (int x = rpos; x <= rend; ++x) {
                if (x == rend) {
                    return l;
                }
                if (r[x] != d[y++]) continue block1;
            }
        }
        return -1;
    }

    protected boolean matchArray(int[] r, int rpos, int rend, char[] d, int dpos) {
        if (d.length - dpos < rend - rpos) {
            return false;
        }
        for (int i = rpos; i < rend; ++i) {
            if (r[i] == d[dpos++]) continue;
            return false;
        }
        return true;
    }
}

