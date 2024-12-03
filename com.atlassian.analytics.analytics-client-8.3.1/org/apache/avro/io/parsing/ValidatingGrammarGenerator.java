/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.io.parsing.Symbol;

public class ValidatingGrammarGenerator {
    public Symbol generate(Schema schema) {
        return Symbol.root(this.generate(schema, new HashMap<LitS, Symbol>()));
    }

    public Symbol generate(Schema sc, Map<LitS, Symbol> seen) {
        switch (sc.getType()) {
            case NULL: {
                return Symbol.NULL;
            }
            case BOOLEAN: {
                return Symbol.BOOLEAN;
            }
            case INT: {
                return Symbol.INT;
            }
            case LONG: {
                return Symbol.LONG;
            }
            case FLOAT: {
                return Symbol.FLOAT;
            }
            case DOUBLE: {
                return Symbol.DOUBLE;
            }
            case STRING: {
                return Symbol.STRING;
            }
            case BYTES: {
                return Symbol.BYTES;
            }
            case FIXED: {
                return Symbol.seq(Symbol.intCheckAction(sc.getFixedSize()), Symbol.FIXED);
            }
            case ENUM: {
                return Symbol.seq(Symbol.intCheckAction(sc.getEnumSymbols().size()), Symbol.ENUM);
            }
            case ARRAY: {
                return Symbol.seq(Symbol.repeat(Symbol.ARRAY_END, this.generate(sc.getElementType(), seen)), Symbol.ARRAY_START);
            }
            case MAP: {
                return Symbol.seq(Symbol.repeat(Symbol.MAP_END, this.generate(sc.getValueType(), seen), Symbol.STRING), Symbol.MAP_START);
            }
            case RECORD: {
                LitS wsc = new LitS(sc);
                Symbol rresult = seen.get(wsc);
                if (rresult == null) {
                    Symbol[] production = new Symbol[sc.getFields().size()];
                    rresult = Symbol.seq(production);
                    seen.put(wsc, rresult);
                    int i = production.length;
                    for (Schema.Field f : sc.getFields()) {
                        production[--i] = this.generate(f.schema(), seen);
                    }
                }
                return rresult;
            }
            case UNION: {
                List<Schema> subs = sc.getTypes();
                Symbol[] symbols = new Symbol[subs.size()];
                String[] labels = new String[subs.size()];
                int i = 0;
                for (Schema b : sc.getTypes()) {
                    symbols[i] = this.generate(b, seen);
                    labels[i] = b.getFullName();
                    ++i;
                }
                return Symbol.seq(Symbol.alt(symbols, labels), Symbol.UNION);
            }
        }
        throw new RuntimeException("Unexpected schema type");
    }

    static class LitS {
        public final Schema actual;

        public LitS(Schema actual) {
            this.actual = actual;
        }

        public boolean equals(Object o) {
            if (!(o instanceof LitS)) {
                return false;
            }
            return this.actual.equals(((LitS)o).actual);
        }

        public int hashCode() {
            return this.actual.hashCode();
        }
    }
}

