/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.transform.PassThroughResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.TupleSubsetResultTransformer;
import org.hibernate.type.Type;

public class CacheableResultTransformer
implements ResultTransformer {
    private static final PassThroughResultTransformer ACTUAL_TRANSFORMER = PassThroughResultTransformer.INSTANCE;
    private final int tupleLength;
    private final int tupleSubsetLength;
    private final boolean[] includeInTuple;
    private final int[] includeInTransformIndex;

    public static CacheableResultTransformer create(ResultTransformer transformer, String[] aliases, boolean[] includeInTuple) {
        return transformer instanceof TupleSubsetResultTransformer ? CacheableResultTransformer.create((TupleSubsetResultTransformer)transformer, aliases, includeInTuple) : CacheableResultTransformer.create(includeInTuple);
    }

    private static CacheableResultTransformer create(TupleSubsetResultTransformer transformer, String[] aliases, boolean[] includeInTuple) {
        if (transformer == null) {
            throw new IllegalArgumentException("transformer cannot be null");
        }
        int tupleLength = ArrayHelper.countTrue(includeInTuple);
        if (aliases != null && aliases.length != tupleLength) {
            throw new IllegalArgumentException("if aliases is not null, then the length of aliases[] must equal the number of true elements in includeInTuple; aliases.length=" + aliases.length + "tupleLength=" + tupleLength);
        }
        return new CacheableResultTransformer(includeInTuple, transformer.includeInTransform(aliases, tupleLength));
    }

    private static CacheableResultTransformer create(boolean[] includeInTuple) {
        return new CacheableResultTransformer(includeInTuple, null);
    }

    private CacheableResultTransformer(boolean[] includeInTuple, boolean[] includeInTransform) {
        if (includeInTuple == null) {
            throw new IllegalArgumentException("includeInTuple cannot be null");
        }
        this.includeInTuple = includeInTuple;
        this.tupleLength = ArrayHelper.countTrue(includeInTuple);
        int n = this.tupleSubsetLength = includeInTransform == null ? this.tupleLength : ArrayHelper.countTrue(includeInTransform);
        if (this.tupleSubsetLength == this.tupleLength) {
            this.includeInTransformIndex = null;
        } else {
            this.includeInTransformIndex = new int[this.tupleSubsetLength];
            int j = 0;
            for (int i = 0; i < includeInTransform.length; ++i) {
                if (!includeInTransform[i]) continue;
                this.includeInTransformIndex[j] = i;
                ++j;
            }
        }
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (aliases != null && aliases.length != this.tupleLength) {
            throw new IllegalStateException("aliases expected length is " + this.tupleLength + "; actual length is " + aliases.length);
        }
        return ACTUAL_TRANSFORMER.transformTuple(this.index(tuple.getClass(), tuple), null);
    }

    public List retransformResults(List transformedResults, String[] aliases, ResultTransformer transformer, boolean[] includeInTuple) {
        String[] aliasesToUse;
        if (transformer == null) {
            throw new IllegalArgumentException("transformer cannot be null");
        }
        if (!this.equals(CacheableResultTransformer.create(transformer, aliases, includeInTuple))) {
            throw new IllegalStateException("this CacheableResultTransformer is inconsistent with specified arguments; cannot re-transform");
        }
        boolean requiresRetransform = true;
        String[] stringArray = aliasesToUse = aliases == null ? null : this.index(aliases.getClass(), aliases);
        if (transformer == ACTUAL_TRANSFORMER) {
            requiresRetransform = false;
        } else if (transformer instanceof TupleSubsetResultTransformer) {
            boolean bl = requiresRetransform = !((TupleSubsetResultTransformer)transformer).isTransformedValueATupleElement(aliasesToUse, this.tupleLength);
        }
        if (requiresRetransform) {
            for (int i = 0; i < transformedResults.size(); ++i) {
                Object[] tuple = ACTUAL_TRANSFORMER.untransformToTuple(transformedResults.get(i), this.tupleSubsetLength == 1);
                transformedResults.set(i, transformer.transformTuple(tuple, aliasesToUse));
            }
        }
        return transformedResults;
    }

    public List untransformToTuples(List results) {
        if (this.includeInTransformIndex == null) {
            results = ACTUAL_TRANSFORMER.untransformToTuples(results, this.tupleSubsetLength == 1);
        } else {
            for (int i = 0; i < results.size(); ++i) {
                Object[] tuple = ACTUAL_TRANSFORMER.untransformToTuple(results.get(i), this.tupleSubsetLength == 1);
                results.set(i, this.unindex(tuple.getClass(), tuple));
            }
        }
        return results;
    }

    public Type[] getCachedResultTypes(Type[] tupleResultTypes) {
        return this.tupleLength != this.tupleSubsetLength ? this.index(tupleResultTypes.getClass(), tupleResultTypes) : tupleResultTypes;
    }

    @Override
    public List transformList(List list) {
        return list;
    }

    private <T> T[] index(Class<? extends T[]> clazz, T[] objects) {
        T[] objectsIndexed = objects;
        if (objects != null && this.includeInTransformIndex != null && objects.length != this.tupleSubsetLength) {
            objectsIndexed = clazz.cast(Array.newInstance(clazz.getComponentType(), this.tupleSubsetLength));
            for (int i = 0; i < this.tupleSubsetLength; ++i) {
                objectsIndexed[i] = objects[this.includeInTransformIndex[i]];
            }
        }
        return objectsIndexed;
    }

    private <T> T[] unindex(Class<? extends T[]> clazz, T[] objects) {
        T[] objectsUnindexed = objects;
        if (objects != null && this.includeInTransformIndex != null && objects.length != this.tupleLength) {
            objectsUnindexed = clazz.cast(Array.newInstance(clazz.getComponentType(), this.tupleLength));
            for (int i = 0; i < this.tupleSubsetLength; ++i) {
                objectsUnindexed[this.includeInTransformIndex[i]] = objects[i];
            }
        }
        return objectsUnindexed;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CacheableResultTransformer that = (CacheableResultTransformer)o;
        return this.tupleLength == that.tupleLength && this.tupleSubsetLength == that.tupleSubsetLength && Arrays.equals(this.includeInTuple, that.includeInTuple) && Arrays.equals(this.includeInTransformIndex, that.includeInTransformIndex);
    }

    public int hashCode() {
        int result = this.tupleLength;
        result = 31 * result + this.tupleSubsetLength;
        result = 31 * result + (this.includeInTuple != null ? Arrays.hashCode(this.includeInTuple) : 0);
        result = 31 * result + (this.includeInTransformIndex != null ? Arrays.hashCode(this.includeInTransformIndex) : 0);
        return result;
    }
}

