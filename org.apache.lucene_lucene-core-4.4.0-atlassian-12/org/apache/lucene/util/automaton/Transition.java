/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.Comparator;
import org.apache.lucene.util.automaton.State;

public class Transition
implements Cloneable {
    final int min;
    final int max;
    final State to;
    public static final Comparator<Transition> CompareByDestThenMinMax = new CompareByDestThenMinMaxSingle();
    public static final Comparator<Transition> CompareByMinMaxThenDest = new CompareByMinMaxThenDestSingle();

    public Transition(int c, State to) {
        assert (c >= 0);
        this.min = this.max = c;
        this.to = to;
    }

    public Transition(int min, int max, State to) {
        assert (min >= 0);
        assert (max >= 0);
        if (max < min) {
            int t = max;
            max = min;
            min = t;
        }
        this.min = min;
        this.max = max;
        this.to = to;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public State getDest() {
        return this.to;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Transition) {
            Transition t = (Transition)obj;
            return t.min == this.min && t.max == this.max && t.to == this.to;
        }
        return false;
    }

    public int hashCode() {
        return this.min * 2 + this.max * 3;
    }

    public Transition clone() {
        try {
            return (Transition)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    static void appendCharString(int c, StringBuilder b) {
        if (c >= 33 && c <= 126 && c != 92 && c != 34) {
            b.appendCodePoint(c);
        } else {
            b.append("\\\\U");
            String s = Integer.toHexString(c);
            if (c < 16) {
                b.append("0000000").append(s);
            } else if (c < 256) {
                b.append("000000").append(s);
            } else if (c < 4096) {
                b.append("00000").append(s);
            } else if (c < 65536) {
                b.append("0000").append(s);
            } else if (c < 0x100000) {
                b.append("000").append(s);
            } else if (c < 0x1000000) {
                b.append("00").append(s);
            } else if (c < 0x10000000) {
                b.append("0").append(s);
            } else {
                b.append(s);
            }
        }
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        Transition.appendCharString(this.min, b);
        if (this.min != this.max) {
            b.append("-");
            Transition.appendCharString(this.max, b);
        }
        b.append(" -> ").append(this.to.number);
        return b.toString();
    }

    void appendDot(StringBuilder b) {
        b.append(" -> ").append(this.to.number).append(" [label=\"");
        Transition.appendCharString(this.min, b);
        if (this.min != this.max) {
            b.append("-");
            Transition.appendCharString(this.max, b);
        }
        b.append("\"]\n");
    }

    private static final class CompareByMinMaxThenDestSingle
    implements Comparator<Transition> {
        private CompareByMinMaxThenDestSingle() {
        }

        @Override
        public int compare(Transition t1, Transition t2) {
            if (t1.min < t2.min) {
                return -1;
            }
            if (t1.min > t2.min) {
                return 1;
            }
            if (t1.max > t2.max) {
                return -1;
            }
            if (t1.max < t2.max) {
                return 1;
            }
            if (t1.to != t2.to) {
                if (t1.to.number < t2.to.number) {
                    return -1;
                }
                if (t1.to.number > t2.to.number) {
                    return 1;
                }
            }
            return 0;
        }
    }

    private static final class CompareByDestThenMinMaxSingle
    implements Comparator<Transition> {
        private CompareByDestThenMinMaxSingle() {
        }

        @Override
        public int compare(Transition t1, Transition t2) {
            if (t1.to != t2.to) {
                if (t1.to.number < t2.to.number) {
                    return -1;
                }
                if (t1.to.number > t2.to.number) {
                    return 1;
                }
            }
            if (t1.min < t2.min) {
                return -1;
            }
            if (t1.min > t2.min) {
                return 1;
            }
            if (t1.max > t2.max) {
                return -1;
            }
            if (t1.max < t2.max) {
                return 1;
            }
            return 0;
        }
    }
}

