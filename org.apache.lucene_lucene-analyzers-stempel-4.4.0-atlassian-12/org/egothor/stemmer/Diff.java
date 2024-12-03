/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

public class Diff {
    int sizex = 0;
    int sizey = 0;
    int[][] net;
    int[][] way;
    int INSERT;
    int DELETE;
    int REPLACE;
    int NOOP;

    public Diff() {
        this(1, 1, 1, 0);
    }

    public Diff(int ins, int del, int rep, int noop) {
        this.INSERT = ins;
        this.DELETE = del;
        this.REPLACE = rep;
        this.NOOP = noop;
    }

    public static void apply(StringBuilder dest, CharSequence diff) {
        try {
            if (diff == null) {
                return;
            }
            int pos = dest.length() - 1;
            if (pos < 0) {
                return;
            }
            for (int i = 0; i < diff.length() / 2; ++i) {
                char cmd = diff.charAt(2 * i);
                char param = diff.charAt(2 * i + 1);
                int par_num = param - 97 + 1;
                switch (cmd) {
                    case '-': {
                        pos = pos - par_num + 1;
                        break;
                    }
                    case 'R': {
                        dest.setCharAt(pos, param);
                        break;
                    }
                    case 'D': {
                        int o = pos;
                        dest.delete(pos -= par_num - 1, o + 1);
                        break;
                    }
                    case 'I': {
                        dest.insert(++pos, param);
                    }
                }
                --pos;
            }
        }
        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            // empty catch block
        }
    }

    public synchronized String exec(String a, String b) {
        int y;
        int x;
        if (a == null || b == null) {
            return null;
        }
        int[] go = new int[4];
        boolean X = true;
        int Y = 2;
        int R = 3;
        boolean D = false;
        int maxx = a.length() + 1;
        int maxy = b.length() + 1;
        if (maxx >= this.sizex || maxy >= this.sizey) {
            this.sizex = maxx + 8;
            this.sizey = maxy + 8;
            this.net = new int[this.sizex][this.sizey];
            this.way = new int[this.sizex][this.sizey];
        }
        for (x = 0; x < maxx; ++x) {
            for (y = 0; y < maxy; ++y) {
                this.net[x][y] = 0;
            }
        }
        for (x = 1; x < maxx; ++x) {
            this.net[x][0] = x;
            this.way[x][0] = 1;
        }
        for (y = 1; y < maxy; ++y) {
            this.net[0][y] = y;
            this.way[0][y] = 2;
        }
        for (x = 1; x < maxx; ++x) {
            for (y = 1; y < maxy; ++y) {
                go[1] = this.net[x - 1][y] + this.DELETE;
                go[2] = this.net[x][y - 1] + this.INSERT;
                go[3] = this.net[x - 1][y - 1] + this.REPLACE;
                go[0] = this.net[x - 1][y - 1] + (a.charAt(x - 1) == b.charAt(y - 1) ? this.NOOP : 100);
                int min = 0;
                if (go[min] >= go[1]) {
                    min = 1;
                }
                if (go[min] > go[2]) {
                    min = 2;
                }
                if (go[min] > go[3]) {
                    min = 3;
                }
                this.way[x][y] = min;
                this.net[x][y] = (short)go[min];
            }
        }
        StringBuffer result = new StringBuffer();
        int base = 96;
        int deletes = 96;
        int equals = 96;
        x = maxx - 1;
        y = maxy - 1;
        while (x + y != 0) {
            switch (this.way[x][y]) {
                case 1: {
                    if (equals != 96) {
                        result.append("-" + (char)equals);
                        equals = 96;
                    }
                    deletes = (char)(deletes + '\u0001');
                    --x;
                    break;
                }
                case 2: {
                    if (deletes != 96) {
                        result.append("D" + (char)deletes);
                        deletes = 96;
                    }
                    if (equals != 96) {
                        result.append("-" + (char)equals);
                        equals = 96;
                    }
                    result.append('I');
                    result.append(b.charAt(--y));
                    break;
                }
                case 3: {
                    if (deletes != 96) {
                        result.append("D" + (char)deletes);
                        deletes = 96;
                    }
                    if (equals != 96) {
                        result.append("-" + (char)equals);
                        equals = 96;
                    }
                    result.append('R');
                    result.append(b.charAt(--y));
                    --x;
                    break;
                }
                case 0: {
                    if (deletes != 96) {
                        result.append("D" + (char)deletes);
                        deletes = 96;
                    }
                    equals = (char)(equals + 1);
                    --x;
                    --y;
                }
            }
        }
        if (deletes != 96) {
            result.append("D" + (char)deletes);
            deletes = 96;
        }
        return result.toString();
    }
}

