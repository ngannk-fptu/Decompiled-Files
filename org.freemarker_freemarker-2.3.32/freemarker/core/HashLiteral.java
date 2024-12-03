/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CollectionAndSequence;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template._ObjectWrappers;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

final class HashLiteral
extends Expression {
    private final List<? extends Expression> keys;
    private final List<? extends Expression> values;
    private final int size;

    HashLiteral(List<? extends Expression> keys, List<? extends Expression> values) {
        this.keys = keys;
        this.values = values;
        this.size = keys.size();
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return new SequenceHash(env);
    }

    @Override
    public String getCanonicalForm() {
        StringBuilder buf = new StringBuilder("{");
        for (int i = 0; i < this.size; ++i) {
            Expression key = this.keys.get(i);
            Expression value = this.values.get(i);
            buf.append(key.getCanonicalForm());
            buf.append(": ");
            buf.append(value.getCanonicalForm());
            if (i == this.size - 1) continue;
            buf.append(", ");
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "{...}";
    }

    @Override
    boolean isLiteral() {
        if (this.constantValue != null) {
            return true;
        }
        for (int i = 0; i < this.size; ++i) {
            Expression key = this.keys.get(i);
            Expression value = this.values.get(i);
            if (key.isLiteral() && value.isLiteral()) continue;
            return false;
        }
        return true;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        ArrayList<Expression> clonedKeys = new ArrayList<Expression>(this.keys.size());
        for (Expression expression : this.keys) {
            clonedKeys.add(expression.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        }
        ArrayList<Expression> clonedValues = new ArrayList<Expression>(this.values.size());
        for (Expression expression : this.values) {
            clonedValues.add(expression.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
        }
        return new HashLiteral(clonedKeys, clonedValues);
    }

    @Override
    int getParameterCount() {
        return this.size * 2;
    }

    @Override
    Object getParameterValue(int idx) {
        this.checkIndex(idx);
        return idx % 2 == 0 ? this.keys.get(idx / 2) : this.values.get(idx / 2);
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        this.checkIndex(idx);
        return idx % 2 == 0 ? ParameterRole.ITEM_KEY : ParameterRole.ITEM_VALUE;
    }

    private void checkIndex(int idx) {
        if (idx >= this.size * 2) {
            throw new IndexOutOfBoundsException();
        }
    }

    private class SequenceHash
    implements TemplateHashModelEx2 {
        private HashMap<String, TemplateModel> map;
        private TemplateCollectionModel keyCollection;
        private TemplateCollectionModel valueCollection;

        SequenceHash(Environment env) throws TemplateException {
            if (_TemplateAPI.getTemplateLanguageVersionAsInt(HashLiteral.this) >= _VersionInts.V_2_3_21) {
                this.map = new LinkedHashMap<String, TemplateModel>();
                for (int i = 0; i < HashLiteral.this.size; ++i) {
                    Expression keyExp = (Expression)HashLiteral.this.keys.get(i);
                    Expression valExp = (Expression)HashLiteral.this.values.get(i);
                    String key = keyExp.evalAndCoerceToPlainText(env);
                    TemplateModel value = valExp.eval(env);
                    if (env == null || !env.isClassicCompatible()) {
                        valExp.assertNonNull(value, env);
                    }
                    this.map.put(key, value);
                }
            } else {
                this.map = new HashMap();
                SimpleSequence keyList = new SimpleSequence(HashLiteral.this.size, (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                SimpleSequence valueList = new SimpleSequence(HashLiteral.this.size, (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                for (int i = 0; i < HashLiteral.this.size; ++i) {
                    Expression keyExp = (Expression)HashLiteral.this.keys.get(i);
                    Expression valExp = (Expression)HashLiteral.this.values.get(i);
                    String key = keyExp.evalAndCoerceToPlainText(env);
                    TemplateModel value = valExp.eval(env);
                    if (env == null || !env.isClassicCompatible()) {
                        valExp.assertNonNull(value, env);
                    }
                    this.map.put(key, value);
                    keyList.add(key);
                    valueList.add(value);
                }
                this.keyCollection = new CollectionAndSequence(keyList);
                this.valueCollection = new CollectionAndSequence(valueList);
            }
        }

        @Override
        public int size() {
            return HashLiteral.this.size;
        }

        @Override
        public TemplateCollectionModel keys() {
            if (this.keyCollection == null) {
                this.keyCollection = new CollectionAndSequence(new SimpleSequence(this.map.keySet(), (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER));
            }
            return this.keyCollection;
        }

        @Override
        public TemplateCollectionModel values() {
            if (this.valueCollection == null) {
                this.valueCollection = new CollectionAndSequence(new SimpleSequence(this.map.values(), (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER));
            }
            return this.valueCollection;
        }

        @Override
        public TemplateModel get(String key) {
            return this.map.get(key);
        }

        @Override
        public boolean isEmpty() {
            return HashLiteral.this.size == 0;
        }

        public String toString() {
            return HashLiteral.this.getCanonicalForm();
        }

        @Override
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
            return new TemplateHashModelEx2.KeyValuePairIterator(){
                private final TemplateModelIterator keyIterator;
                private final TemplateModelIterator valueIterator;
                {
                    this.keyIterator = SequenceHash.this.keys().iterator();
                    this.valueIterator = SequenceHash.this.values().iterator();
                }

                @Override
                public boolean hasNext() throws TemplateModelException {
                    return this.keyIterator.hasNext();
                }

                @Override
                public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
                    return new TemplateHashModelEx2.KeyValuePair(){
                        private final TemplateModel key;
                        private final TemplateModel value;
                        {
                            this.key = keyIterator.next();
                            this.value = valueIterator.next();
                        }

                        @Override
                        public TemplateModel getKey() {
                            return this.key;
                        }

                        @Override
                        public TemplateModel getValue() {
                            return this.value;
                        }
                    };
                }
            };
        }
    }
}

