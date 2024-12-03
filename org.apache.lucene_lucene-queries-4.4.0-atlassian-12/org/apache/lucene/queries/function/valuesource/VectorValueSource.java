/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.IndexSearcher
 */
package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.MultiValueSource;
import org.apache.lucene.search.IndexSearcher;

public class VectorValueSource
extends MultiValueSource {
    protected final List<ValueSource> sources;

    public VectorValueSource(List<ValueSource> sources) {
        this.sources = sources;
    }

    public List<ValueSource> getSources() {
        return this.sources;
    }

    @Override
    public int dimension() {
        return this.sources.size();
    }

    public String name() {
        return "vector";
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext readerContext) throws IOException {
        int size = this.sources.size();
        if (size == 2) {
            final FunctionValues x = this.sources.get(0).getValues(context, readerContext);
            final FunctionValues y = this.sources.get(1).getValues(context, readerContext);
            return new FunctionValues(){

                @Override
                public void byteVal(int doc, byte[] vals) {
                    vals[0] = x.byteVal(doc);
                    vals[1] = y.byteVal(doc);
                }

                @Override
                public void shortVal(int doc, short[] vals) {
                    vals[0] = x.shortVal(doc);
                    vals[1] = y.shortVal(doc);
                }

                @Override
                public void intVal(int doc, int[] vals) {
                    vals[0] = x.intVal(doc);
                    vals[1] = y.intVal(doc);
                }

                @Override
                public void longVal(int doc, long[] vals) {
                    vals[0] = x.longVal(doc);
                    vals[1] = y.longVal(doc);
                }

                @Override
                public void floatVal(int doc, float[] vals) {
                    vals[0] = x.floatVal(doc);
                    vals[1] = y.floatVal(doc);
                }

                @Override
                public void doubleVal(int doc, double[] vals) {
                    vals[0] = x.doubleVal(doc);
                    vals[1] = y.doubleVal(doc);
                }

                @Override
                public void strVal(int doc, String[] vals) {
                    vals[0] = x.strVal(doc);
                    vals[1] = y.strVal(doc);
                }

                @Override
                public String toString(int doc) {
                    return VectorValueSource.this.name() + "(" + x.toString(doc) + "," + y.toString(doc) + ")";
                }
            };
        }
        final FunctionValues[] valsArr = new FunctionValues[size];
        for (int i = 0; i < size; ++i) {
            valsArr[i] = this.sources.get(i).getValues(context, readerContext);
        }
        return new FunctionValues(){

            @Override
            public void byteVal(int doc, byte[] vals) {
                for (int i = 0; i < valsArr.length; ++i) {
                    vals[i] = valsArr[i].byteVal(doc);
                }
            }

            @Override
            public void shortVal(int doc, short[] vals) {
                for (int i = 0; i < valsArr.length; ++i) {
                    vals[i] = valsArr[i].shortVal(doc);
                }
            }

            @Override
            public void floatVal(int doc, float[] vals) {
                for (int i = 0; i < valsArr.length; ++i) {
                    vals[i] = valsArr[i].floatVal(doc);
                }
            }

            @Override
            public void intVal(int doc, int[] vals) {
                for (int i = 0; i < valsArr.length; ++i) {
                    vals[i] = valsArr[i].intVal(doc);
                }
            }

            @Override
            public void longVal(int doc, long[] vals) {
                for (int i = 0; i < valsArr.length; ++i) {
                    vals[i] = valsArr[i].longVal(doc);
                }
            }

            @Override
            public void doubleVal(int doc, double[] vals) {
                for (int i = 0; i < valsArr.length; ++i) {
                    vals[i] = valsArr[i].doubleVal(doc);
                }
            }

            @Override
            public void strVal(int doc, String[] vals) {
                for (int i = 0; i < valsArr.length; ++i) {
                    vals[i] = valsArr[i].strVal(doc);
                }
            }

            @Override
            public String toString(int doc) {
                StringBuilder sb = new StringBuilder();
                sb.append(VectorValueSource.this.name()).append('(');
                boolean firstTime = true;
                for (FunctionValues vals : valsArr) {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        sb.append(',');
                    }
                    sb.append(vals.toString(doc));
                }
                sb.append(')');
                return sb.toString();
            }
        };
    }

    @Override
    public void createWeight(Map context, IndexSearcher searcher) throws IOException {
        for (ValueSource source : this.sources) {
            source.createWeight(context, searcher);
        }
    }

    @Override
    public String description() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name()).append('(');
        boolean firstTime = true;
        for (ValueSource source : this.sources) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(',');
            }
            sb.append(source);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VectorValueSource)) {
            return false;
        }
        VectorValueSource that = (VectorValueSource)o;
        return this.sources.equals(that.sources);
    }

    @Override
    public int hashCode() {
        return this.sources.hashCode();
    }
}

