/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.TreeMap;
import org.egothor.stemmer.Cell;

public class Row {
    TreeMap<Character, Cell> cells = new TreeMap();
    int uniformCnt = 0;
    int uniformSkip = 0;

    public Row(DataInput is) throws IOException {
        for (int i = is.readInt(); i > 0; --i) {
            char ch = is.readChar();
            Cell c = new Cell();
            c.cmd = is.readInt();
            c.cnt = is.readInt();
            c.ref = is.readInt();
            c.skip = is.readInt();
            this.cells.put(Character.valueOf(ch), c);
        }
    }

    public Row() {
    }

    public Row(Row old) {
        this.cells = old.cells;
    }

    public void setCmd(Character way, int cmd) {
        Cell c = this.at(way);
        if (c == null) {
            c = new Cell();
            c.cmd = cmd;
            this.cells.put(way, c);
        } else {
            c.cmd = cmd;
        }
        c.cnt = cmd >= 0 ? 1 : 0;
    }

    public void setRef(Character way, int ref) {
        Cell c = this.at(way);
        if (c == null) {
            c = new Cell();
            c.ref = ref;
            this.cells.put(way, c);
        } else {
            c.ref = ref;
        }
    }

    public int getCells() {
        Iterator<Character> i = this.cells.keySet().iterator();
        int size = 0;
        while (i.hasNext()) {
            Character c = i.next();
            Cell e = this.at(c);
            if (e.cmd < 0 && e.ref < 0) continue;
            ++size;
        }
        return size;
    }

    public int getCellsPnt() {
        Iterator<Character> i = this.cells.keySet().iterator();
        int size = 0;
        while (i.hasNext()) {
            Character c = i.next();
            Cell e = this.at(c);
            if (e.ref < 0) continue;
            ++size;
        }
        return size;
    }

    public int getCellsVal() {
        Iterator<Character> i = this.cells.keySet().iterator();
        int size = 0;
        while (i.hasNext()) {
            Character c = i.next();
            Cell e = this.at(c);
            if (e.cmd < 0) continue;
            ++size;
        }
        return size;
    }

    public int getCmd(Character way) {
        Cell c = this.at(way);
        return c == null ? -1 : c.cmd;
    }

    public int getCnt(Character way) {
        Cell c = this.at(way);
        return c == null ? -1 : c.cnt;
    }

    public int getRef(Character way) {
        Cell c = this.at(way);
        return c == null ? -1 : c.ref;
    }

    public void store(DataOutput os) throws IOException {
        os.writeInt(this.cells.size());
        for (Character c : this.cells.keySet()) {
            Cell e = this.at(c);
            if (e.cmd < 0 && e.ref < 0) continue;
            os.writeChar(c.charValue());
            os.writeInt(e.cmd);
            os.writeInt(e.cnt);
            os.writeInt(e.ref);
            os.writeInt(e.skip);
        }
    }

    public int uniformCmd(boolean eqSkip) {
        Iterator<Cell> i = this.cells.values().iterator();
        int ret = -1;
        this.uniformCnt = 1;
        this.uniformSkip = 0;
        while (i.hasNext()) {
            Cell c = i.next();
            if (c.ref >= 0) {
                return -1;
            }
            if (c.cmd < 0) continue;
            if (ret < 0) {
                ret = c.cmd;
                this.uniformSkip = c.skip;
                continue;
            }
            if (ret == c.cmd) {
                if (eqSkip) {
                    if (this.uniformSkip == c.skip) {
                        ++this.uniformCnt;
                        continue;
                    }
                    return -1;
                }
                ++this.uniformCnt;
                continue;
            }
            return -1;
        }
        return ret;
    }

    public void print(PrintStream out) {
        for (Character ch : this.cells.keySet()) {
            Cell c = this.at(ch);
            out.print("[" + ch + ":" + c + "]");
        }
        out.println();
    }

    Cell at(Character index) {
        return this.cells.get(index);
    }
}

