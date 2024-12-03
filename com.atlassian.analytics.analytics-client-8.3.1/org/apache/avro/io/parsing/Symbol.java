/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.avro.Schema;

public abstract class Symbol {
    public final Kind kind;
    public final Symbol[] production;
    public static final Symbol NULL = new Terminal("null");
    public static final Symbol BOOLEAN = new Terminal("boolean");
    public static final Symbol INT = new Terminal("int");
    public static final Symbol LONG = new Terminal("long");
    public static final Symbol FLOAT = new Terminal("float");
    public static final Symbol DOUBLE = new Terminal("double");
    public static final Symbol STRING = new Terminal("string");
    public static final Symbol BYTES = new Terminal("bytes");
    public static final Symbol FIXED = new Terminal("fixed");
    public static final Symbol ENUM = new Terminal("enum");
    public static final Symbol UNION = new Terminal("union");
    public static final Symbol ARRAY_START = new Terminal("array-start");
    public static final Symbol ARRAY_END = new Terminal("array-end");
    public static final Symbol MAP_START = new Terminal("map-start");
    public static final Symbol MAP_END = new Terminal("map-end");
    public static final Symbol ITEM_END = new Terminal("item-end");
    public static final Symbol WRITER_UNION_ACTION = Symbol.writerUnionAction();
    public static final Symbol FIELD_ACTION = new Terminal("field-action");
    public static final Symbol RECORD_START = new ImplicitAction(false);
    public static final Symbol RECORD_END = new ImplicitAction(true);
    public static final Symbol UNION_END = new ImplicitAction(true);
    public static final Symbol FIELD_END = new ImplicitAction(true);
    public static final Symbol DEFAULT_END_ACTION = new ImplicitAction(true);
    public static final Symbol MAP_KEY_MARKER = new Terminal("map-key-marker");

    protected Symbol(Kind kind) {
        this(kind, null);
    }

    protected Symbol(Kind kind, Symbol[] production) {
        this.production = production;
        this.kind = kind;
    }

    static Symbol root(Symbol ... symbols) {
        return new Root(symbols);
    }

    static Symbol seq(Symbol ... production) {
        return new Sequence(production);
    }

    static Symbol repeat(Symbol endSymbol, Symbol ... symsToRepeat) {
        return new Repeater(endSymbol, symsToRepeat);
    }

    static Symbol alt(Symbol[] symbols, String[] labels) {
        return new Alternative(symbols, labels);
    }

    static Symbol error(String e) {
        return new ErrorAction(e);
    }

    static Symbol resolve(Symbol w, Symbol r) {
        return new ResolvingAction(w, r);
    }

    public Symbol flatten(Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
        return this;
    }

    public int flattenedSize() {
        return 1;
    }

    static void flatten(Symbol[] in, int start, Symbol[] out, int skip, Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
        int j = skip;
        for (int i = start; i < in.length; ++i) {
            Symbol s = in[i].flatten(map, map2);
            if (s instanceof Sequence) {
                Symbol[] p = s.production;
                List<Fixup> l = map2.get(s);
                if (l == null) {
                    System.arraycopy(p, 0, out, j, p.length);
                    for (List<Fixup> fixups : map2.values()) {
                        Symbol.copyFixups(fixups, out, j, p);
                    }
                } else {
                    l.add(new Fixup(out, j));
                }
                j += p.length;
                continue;
            }
            out[j++] = s;
        }
    }

    private static void copyFixups(List<Fixup> fixups, Symbol[] out, int outPos, Symbol[] toCopy) {
        int n = fixups.size();
        for (int i = 0; i < n; ++i) {
            Fixup fixup = fixups.get(i);
            if (fixup.symbols != toCopy) continue;
            fixups.add(new Fixup(out, fixup.pos + outPos));
        }
    }

    protected static int flattenedSize(Symbol[] symbols, int start) {
        int result = 0;
        for (int i = start; i < symbols.length; ++i) {
            if (symbols[i] instanceof Sequence) {
                Sequence s = (Sequence)symbols[i];
                result += s.flattenedSize();
                continue;
            }
            ++result;
        }
        return result;
    }

    public static boolean hasErrors(Symbol symbol) {
        return Symbol.hasErrors(symbol, new HashSet<Symbol>());
    }

    private static boolean hasErrors(Symbol symbol, Set<Symbol> visited) {
        if (visited.contains(symbol)) {
            return false;
        }
        visited.add(symbol);
        switch (symbol.kind) {
            case ALTERNATIVE: {
                return Symbol.hasErrors(symbol, ((Alternative)symbol).symbols, visited);
            }
            case EXPLICIT_ACTION: {
                return false;
            }
            case IMPLICIT_ACTION: {
                if (symbol instanceof ErrorAction) {
                    return true;
                }
                if (symbol instanceof UnionAdjustAction) {
                    return Symbol.hasErrors(((UnionAdjustAction)symbol).symToParse, visited);
                }
                return false;
            }
            case REPEATER: {
                Repeater r = (Repeater)symbol;
                return Symbol.hasErrors(r.end, visited) || Symbol.hasErrors(symbol, r.production, visited);
            }
            case ROOT: 
            case SEQUENCE: {
                return Symbol.hasErrors(symbol, symbol.production, visited);
            }
            case TERMINAL: {
                return false;
            }
        }
        throw new RuntimeException("unknown symbol kind: " + (Object)((Object)symbol.kind));
    }

    private static boolean hasErrors(Symbol root, Symbol[] symbols, Set<Symbol> visited) {
        if (null != symbols) {
            for (Symbol s : symbols) {
                if (s == root || !Symbol.hasErrors(s, visited)) continue;
                return true;
            }
        }
        return false;
    }

    public static IntCheckAction intCheckAction(int size) {
        return new IntCheckAction(size);
    }

    public static EnumAdjustAction enumAdjustAction(int rsymCount, Object[] adj) {
        return new EnumAdjustAction(rsymCount, adj);
    }

    public static WriterUnionAction writerUnionAction() {
        return new WriterUnionAction();
    }

    public static SkipAction skipAction(Symbol symToSkip) {
        return new SkipAction(symToSkip);
    }

    public static FieldAdjustAction fieldAdjustAction(int rindex, String fname, Set<String> aliases) {
        return new FieldAdjustAction(rindex, fname, aliases);
    }

    public static FieldOrderAction fieldOrderAction(Schema.Field[] fields) {
        return new FieldOrderAction(fields);
    }

    public static DefaultStartAction defaultStartAction(byte[] contents) {
        return new DefaultStartAction(contents);
    }

    public static UnionAdjustAction unionAdjustAction(int rindex, Symbol sym) {
        return new UnionAdjustAction(rindex, sym);
    }

    public static EnumLabelsAction enumLabelsAction(List<String> symbols) {
        return new EnumLabelsAction(symbols);
    }

    public static class EnumLabelsAction
    extends IntCheckAction {
        public final List<String> symbols;

        @Deprecated
        public EnumLabelsAction(List<String> symbols) {
            super(symbols.size());
            this.symbols = symbols;
        }

        public String getLabel(int n) {
            return this.symbols.get(n);
        }

        public int findLabel(String l) {
            if (l != null) {
                for (int i = 0; i < this.symbols.size(); ++i) {
                    if (!l.equals(this.symbols.get(i))) continue;
                    return i;
                }
            }
            return -1;
        }
    }

    public static class UnionAdjustAction
    extends ImplicitAction {
        public final int rindex;
        public final Symbol symToParse;

        @Deprecated
        public UnionAdjustAction(int rindex, Symbol symToParse) {
            this.rindex = rindex;
            this.symToParse = symToParse;
        }

        @Override
        public UnionAdjustAction flatten(Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
            return new UnionAdjustAction(this.rindex, this.symToParse.flatten(map, map2));
        }
    }

    public static class DefaultStartAction
    extends ImplicitAction {
        public final byte[] contents;

        @Deprecated
        public DefaultStartAction(byte[] contents) {
            this.contents = contents;
        }
    }

    public static final class FieldOrderAction
    extends ImplicitAction {
        public final boolean noReorder;
        public final Schema.Field[] fields;

        @Deprecated
        public FieldOrderAction(Schema.Field[] fields) {
            this.fields = fields;
            boolean noReorder = true;
            for (int i = 0; noReorder && i < fields.length; noReorder &= i == fields[i].pos(), ++i) {
            }
            this.noReorder = noReorder;
        }
    }

    public static class FieldAdjustAction
    extends ImplicitAction {
        public final int rindex;
        public final String fname;
        public final Set<String> aliases;

        @Deprecated
        public FieldAdjustAction(int rindex, String fname, Set<String> aliases) {
            this.rindex = rindex;
            this.fname = fname;
            this.aliases = aliases;
        }
    }

    public static class SkipAction
    extends ImplicitAction {
        public final Symbol symToSkip;

        @Deprecated
        public SkipAction(Symbol symToSkip) {
            super(true);
            this.symToSkip = symToSkip;
        }

        @Override
        public SkipAction flatten(Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
            return new SkipAction(this.symToSkip.flatten(map, map2));
        }
    }

    public static class ResolvingAction
    extends ImplicitAction {
        public final Symbol writer;
        public final Symbol reader;

        private ResolvingAction(Symbol writer, Symbol reader) {
            this.writer = writer;
            this.reader = reader;
        }

        @Override
        public ResolvingAction flatten(Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
            return new ResolvingAction(this.writer.flatten(map, map2), this.reader.flatten(map, map2));
        }
    }

    public static class WriterUnionAction
    extends ImplicitAction {
        private WriterUnionAction() {
        }
    }

    public static class EnumAdjustAction
    extends IntCheckAction {
        public final boolean noAdjustments;
        public final Object[] adjustments;

        @Deprecated
        public EnumAdjustAction(int rsymCount, Object[] adjustments) {
            super(rsymCount);
            this.adjustments = adjustments;
            boolean noAdj = true;
            if (adjustments != null) {
                int count = Math.min(rsymCount, adjustments.length);
                noAdj = adjustments.length <= rsymCount;
                for (int i = 0; noAdj && i < count; noAdj &= adjustments[i] instanceof Integer && i == (Integer)adjustments[i], ++i) {
                }
            }
            this.noAdjustments = noAdj;
        }
    }

    public static class IntCheckAction
    extends Symbol {
        public final int size;

        @Deprecated
        public IntCheckAction(int size) {
            super(Kind.EXPLICIT_ACTION);
            this.size = size;
        }
    }

    public static class ErrorAction
    extends ImplicitAction {
        public final String msg;

        private ErrorAction(String msg) {
            this.msg = msg;
        }
    }

    public static class Alternative
    extends Symbol {
        public final Symbol[] symbols;
        public final String[] labels;

        private Alternative(Symbol[] symbols, String[] labels) {
            super(Kind.ALTERNATIVE);
            this.symbols = symbols;
            this.labels = labels;
        }

        public Symbol getSymbol(int index) {
            return this.symbols[index];
        }

        public String getLabel(int index) {
            return this.labels[index];
        }

        public int size() {
            return this.symbols.length;
        }

        public int findLabel(String label) {
            if (label != null) {
                for (int i = 0; i < this.labels.length; ++i) {
                    if (!label.equals(this.labels[i])) continue;
                    return i;
                }
            }
            return -1;
        }

        @Override
        public Alternative flatten(Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
            Symbol[] ss = new Symbol[this.symbols.length];
            for (int i = 0; i < ss.length; ++i) {
                ss[i] = this.symbols[i].flatten(map, map2);
            }
            return new Alternative(ss, this.labels);
        }
    }

    public static class Repeater
    extends Symbol {
        public final Symbol end;

        private Repeater(Symbol end, Symbol ... sequenceToRepeat) {
            super(Kind.REPEATER, Repeater.makeProduction(sequenceToRepeat));
            this.end = end;
            this.production[0] = this;
        }

        private static Symbol[] makeProduction(Symbol[] p) {
            Symbol[] result = new Symbol[p.length + 1];
            System.arraycopy(p, 0, result, 1, p.length);
            return result;
        }

        @Override
        public Repeater flatten(Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
            Repeater result = new Repeater(this.end, new Symbol[Repeater.flattenedSize(this.production, 1)]);
            Repeater.flatten(this.production, 1, result.production, 1, map, map2);
            return result;
        }
    }

    protected static class Sequence
    extends Symbol
    implements Iterable<Symbol> {
        private Sequence(Symbol[] productions) {
            super(Kind.SEQUENCE, productions);
        }

        public Symbol get(int index) {
            return this.production[index];
        }

        public int size() {
            return this.production.length;
        }

        @Override
        public Iterator<Symbol> iterator() {
            return new Iterator<Symbol>(){
                private int pos;
                {
                    this.pos = production.length;
                }

                @Override
                public boolean hasNext() {
                    return 0 < this.pos;
                }

                @Override
                public Symbol next() {
                    if (0 < this.pos) {
                        return production[--this.pos];
                    }
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public Sequence flatten(Map<Sequence, Sequence> map, Map<Sequence, List<Fixup>> map2) {
            Sequence result = map.get(this);
            if (result == null) {
                result = new Sequence(new Symbol[this.flattenedSize()]);
                map.put(this, result);
                ArrayList l = new ArrayList();
                map2.put(result, l);
                Sequence.flatten(this.production, 0, result.production, 0, map, map2);
                for (Fixup f : l) {
                    System.arraycopy(result.production, 0, f.symbols, f.pos, result.production.length);
                }
                map2.remove(result);
            }
            return result;
        }

        @Override
        public final int flattenedSize() {
            return Sequence.flattenedSize(this.production, 0);
        }
    }

    protected static class Root
    extends Symbol {
        private Root(Symbol ... symbols) {
            super(Kind.ROOT, Root.makeProduction(symbols));
            this.production[0] = this;
        }

        private static Symbol[] makeProduction(Symbol[] symbols) {
            Symbol[] result = new Symbol[Root.flattenedSize(symbols, 0) + 1];
            Root.flatten(symbols, 0, result, 1, new HashMap<Sequence, Sequence>(), new HashMap<Sequence, List<Fixup>>());
            return result;
        }
    }

    public static class ImplicitAction
    extends Symbol {
        public final boolean isTrailing;

        private ImplicitAction() {
            this(false);
        }

        private ImplicitAction(boolean isTrailing) {
            super(Kind.IMPLICIT_ACTION);
            this.isTrailing = isTrailing;
        }
    }

    private static class Terminal
    extends Symbol {
        private final String printName;

        public Terminal(String printName) {
            super(Kind.TERMINAL);
            this.printName = printName;
        }

        public String toString() {
            return this.printName;
        }
    }

    private static class Fixup {
        public final Symbol[] symbols;
        public final int pos;

        public Fixup(Symbol[] symbols, int pos) {
            this.symbols = symbols;
            this.pos = pos;
        }
    }

    public static enum Kind {
        TERMINAL,
        ROOT,
        SEQUENCE,
        REPEATER,
        ALTERNATIVE,
        IMPLICIT_ACTION,
        EXPLICIT_ACTION;

    }
}

