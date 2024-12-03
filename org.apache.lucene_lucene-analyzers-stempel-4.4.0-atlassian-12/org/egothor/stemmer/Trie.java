/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.egothor.stemmer.Cell;
import org.egothor.stemmer.Reduce;
import org.egothor.stemmer.Row;

public class Trie {
    List<Row> rows = new ArrayList<Row>();
    List<CharSequence> cmds = new ArrayList<CharSequence>();
    int root;
    boolean forward = false;

    public Trie(DataInput is) throws IOException {
        int i;
        this.forward = is.readBoolean();
        this.root = is.readInt();
        for (i = is.readInt(); i > 0; --i) {
            this.cmds.add(is.readUTF());
        }
        for (i = is.readInt(); i > 0; --i) {
            this.rows.add(new Row(is));
        }
    }

    public Trie(boolean forward) {
        this.rows.add(new Row());
        this.root = 0;
        this.forward = forward;
    }

    public Trie(boolean forward, int root, List<CharSequence> cmds, List<Row> rows) {
        this.rows = rows;
        this.cmds = cmds;
        this.root = root;
        this.forward = forward;
    }

    public CharSequence[] getAll(CharSequence key) {
        int w;
        int[] res = new int[key.length()];
        int resc = 0;
        Row now = this.getRow(this.root);
        StrEnum e = new StrEnum(key, this.forward);
        boolean br = false;
        for (int i = 0; i < key.length() - 1; ++i) {
            Character ch = new Character(e.next());
            w = now.getCmd(ch);
            if (w >= 0) {
                int n = w;
                for (int j = 0; j < resc; ++j) {
                    if (n != res[j]) continue;
                    n = -1;
                    break;
                }
                if (n >= 0) {
                    res[resc++] = n;
                }
            }
            if ((w = now.getRef(ch)) < 0) {
                br = true;
                break;
            }
            now = this.getRow(w);
        }
        if (!br && (w = now.getCmd(new Character(e.next()))) >= 0) {
            int n = w;
            for (int j = 0; j < resc; ++j) {
                if (n != res[j]) continue;
                n = -1;
                break;
            }
            if (n >= 0) {
                res[resc++] = n;
            }
        }
        if (resc < 1) {
            return null;
        }
        CharSequence[] R = new CharSequence[resc];
        for (int j = 0; j < resc; ++j) {
            R[j] = this.cmds.get(res[j]);
        }
        return R;
    }

    public int getCells() {
        int size = 0;
        for (Row row : this.rows) {
            size += row.getCells();
        }
        return size;
    }

    public int getCellsPnt() {
        int size = 0;
        for (Row row : this.rows) {
            size += row.getCellsPnt();
        }
        return size;
    }

    public int getCellsVal() {
        int size = 0;
        for (Row row : this.rows) {
            size += row.getCellsVal();
        }
        return size;
    }

    public CharSequence getFully(CharSequence key) {
        Row now = this.getRow(this.root);
        int cmd = -1;
        StrEnum e = new StrEnum(key, this.forward);
        Character ch = null;
        Character aux = null;
        int i = 0;
        while (i < key.length()) {
            ch = new Character(e.next());
            ++i;
            Cell c = now.at(ch);
            if (c == null) {
                return null;
            }
            cmd = c.cmd;
            for (int skip = c.skip; skip > 0; --skip) {
                if (i >= key.length()) {
                    return null;
                }
                aux = new Character(e.next());
                ++i;
            }
            int w = now.getRef(ch);
            if (w >= 0) {
                now = this.getRow(w);
                continue;
            }
            if (i >= key.length()) continue;
            return null;
        }
        return cmd == -1 ? null : this.cmds.get(cmd);
    }

    public CharSequence getLastOnPath(CharSequence key) {
        int w;
        Row now = this.getRow(this.root);
        CharSequence last = null;
        StrEnum e = new StrEnum(key, this.forward);
        for (int i = 0; i < key.length() - 1; ++i) {
            Character ch = new Character(e.next());
            w = now.getCmd(ch);
            if (w >= 0) {
                last = this.cmds.get(w);
            }
            if ((w = now.getRef(ch)) < 0) {
                return last;
            }
            now = this.getRow(w);
        }
        w = now.getCmd(new Character(e.next()));
        return w >= 0 ? this.cmds.get(w) : last;
    }

    private Row getRow(int index) {
        if (index < 0 || index >= this.rows.size()) {
            return null;
        }
        return this.rows.get(index);
    }

    public void store(DataOutput os) throws IOException {
        os.writeBoolean(this.forward);
        os.writeInt(this.root);
        os.writeInt(this.cmds.size());
        for (CharSequence cmd : this.cmds) {
            os.writeUTF(cmd.toString());
        }
        os.writeInt(this.rows.size());
        for (Row row : this.rows) {
            row.store(os);
        }
    }

    void add(CharSequence key, CharSequence cmd) {
        if (key == null || cmd == null) {
            return;
        }
        if (cmd.length() == 0) {
            return;
        }
        int id_cmd = this.cmds.indexOf(cmd);
        if (id_cmd == -1) {
            id_cmd = this.cmds.size();
            this.cmds.add(cmd);
        }
        int node = this.root;
        Row r = this.getRow(node);
        StrEnum e = new StrEnum(key, this.forward);
        for (int i = 0; i < e.length() - 1; ++i) {
            Character ch = new Character(e.next());
            node = r.getRef(ch);
            if (node >= 0) {
                r = this.getRow(node);
                continue;
            }
            node = this.rows.size();
            Row n = new Row();
            this.rows.add(n);
            r.setRef(ch, node);
            r = n;
        }
        r.setCmd(new Character(e.next()), id_cmd);
    }

    public Trie reduce(Reduce by) {
        return by.optimize(this);
    }

    public void printInfo(PrintStream out, CharSequence prefix) {
        out.println(prefix + "nds " + this.rows.size() + " cmds " + this.cmds.size() + " cells " + this.getCells() + " valcells " + this.getCellsVal() + " pntcells " + this.getCellsPnt());
    }

    class StrEnum {
        CharSequence s;
        int from;
        int by;

        StrEnum(CharSequence s, boolean up) {
            this.s = s;
            if (up) {
                this.from = 0;
                this.by = 1;
            } else {
                this.from = s.length() - 1;
                this.by = -1;
            }
        }

        int length() {
            return this.s.length();
        }

        char next() {
            char ch = this.s.charAt(this.from);
            this.from += this.by;
            return ch;
        }
    }
}

