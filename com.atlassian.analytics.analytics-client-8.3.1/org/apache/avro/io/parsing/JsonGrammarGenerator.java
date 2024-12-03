/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io.parsing;

import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.io.parsing.ValidatingGrammarGenerator;

public class JsonGrammarGenerator
extends ValidatingGrammarGenerator {
    @Override
    public Symbol generate(Schema schema) {
        return Symbol.root(this.generate(schema, new HashMap<ValidatingGrammarGenerator.LitS, Symbol>()));
    }

    @Override
    public Symbol generate(Schema sc, Map<ValidatingGrammarGenerator.LitS, Symbol> seen) {
        switch (sc.getType()) {
            case NULL: 
            case BOOLEAN: 
            case INT: 
            case LONG: 
            case FLOAT: 
            case DOUBLE: 
            case STRING: 
            case BYTES: 
            case FIXED: 
            case UNION: {
                return super.generate(sc, seen);
            }
            case ENUM: {
                return Symbol.seq(Symbol.enumLabelsAction(sc.getEnumSymbols()), Symbol.ENUM);
            }
            case ARRAY: {
                return Symbol.seq(Symbol.repeat(Symbol.ARRAY_END, Symbol.ITEM_END, this.generate(sc.getElementType(), seen)), Symbol.ARRAY_START);
            }
            case MAP: {
                return Symbol.seq(Symbol.repeat(Symbol.MAP_END, Symbol.ITEM_END, this.generate(sc.getValueType(), seen), Symbol.MAP_KEY_MARKER, Symbol.STRING), Symbol.MAP_START);
            }
            case RECORD: {
                ValidatingGrammarGenerator.LitS wsc = new ValidatingGrammarGenerator.LitS(sc);
                Symbol rresult = seen.get(wsc);
                if (rresult == null) {
                    Symbol[] production = new Symbol[sc.getFields().size() * 3 + 2];
                    rresult = Symbol.seq(production);
                    seen.put(wsc, rresult);
                    int i = production.length;
                    int n = 0;
                    production[--i] = Symbol.RECORD_START;
                    for (Schema.Field f : sc.getFields()) {
                        production[--i] = Symbol.fieldAdjustAction(n, f.name(), f.aliases());
                        production[--i] = this.generate(f.schema(), seen);
                        production[--i] = Symbol.FIELD_END;
                        ++n;
                    }
                    production[--i] = Symbol.RECORD_END;
                }
                return rresult;
            }
        }
        throw new RuntimeException("Unexpected schema type");
    }
}

