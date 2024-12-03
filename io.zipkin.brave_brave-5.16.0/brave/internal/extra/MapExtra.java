/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.extra;

import brave.internal.Nullable;
import brave.internal.Platform;
import brave.internal.collect.LongBitSet;
import brave.internal.collect.UnsafeArrayMap;
import brave.internal.extra.Extra;
import brave.internal.extra.MapExtraFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MapExtra<K, V, A extends MapExtra<K, V, A, F>, F extends MapExtraFactory<K, V, A, F>>
extends Extra<A, F> {
    protected MapExtra(F factory) {
        super(factory);
    }

    Object[] state() {
        return (Object[])this.state;
    }

    protected boolean isDynamic() {
        return ((MapExtraFactory)this.factory).maxDynamicEntries > 0;
    }

    protected boolean isEmpty() {
        Object[] state = this.state();
        for (int i = 0; i < state.length; i += 2) {
            if (state[i + 1] == null) continue;
            return false;
        }
        return true;
    }

    protected Set<K> keySet() {
        if (!this.isDynamic()) {
            return ((MapExtraFactory)this.factory).initialFieldIndices.keySet();
        }
        Object[] state = this.state();
        LinkedHashSet<Object> result = new LinkedHashSet<Object>(state.length / 2);
        for (int i = 0; i < state.length; i += 2) {
            result.add(state[i]);
        }
        return Collections.unmodifiableSet(result);
    }

    protected Map<K, V> asReadOnlyMap() {
        return UnsafeArrayMap.newBuilder().build(this.state());
    }

    @Nullable
    protected V get(K key) {
        if (key == null) {
            return null;
        }
        Object[] state = this.state();
        int i = this.indexOfExistingKey(state, key);
        return (V)(i != -1 ? state[i + 1] : null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean put(K key, @Nullable V value) {
        if (key == null) {
            return false;
        }
        int i = this.indexOfExistingKey(this.state(), key);
        if (i == -1 && ((MapExtraFactory)this.factory).maxDynamicEntries == 0) {
            Platform.get().log("Ignoring request to add a dynamic key", null);
            return false;
        }
        Object object = this.lock;
        synchronized (object) {
            Object[] prior = this.state();
            if (i == -1) {
                i = this.indexOfDynamicKey(prior, key);
            }
            if (i == -1) {
                return this.addNewEntry(prior, key, value);
            }
            if (MapExtra.equal(value, prior[i + 1])) {
                return false;
            }
            Object[] newState = Arrays.copyOf(prior, prior.length);
            newState[i + 1] = value;
            this.state = newState;
            return true;
        }
    }

    @Override
    protected void mergeStateKeepingOursOnConflict(A theirFields) {
        Object[] ourstate = this.state();
        Object[] theirstate = ((MapExtra)theirFields).state();
        long newToOurs = 0L;
        for (int i = 0; i < theirstate.length && theirstate[i] != null; i += 2) {
            int ourIndex = this.indexOfExistingKey(ourstate, theirstate[i]);
            if (ourIndex != -1) continue;
            newToOurs = LongBitSet.setBit(newToOurs, i / 2);
        }
        boolean growthAllowed = true;
        int newstateLength = ourstate.length + LongBitSet.size(newToOurs) * 2;
        if (newstateLength > ourstate.length && newstateLength / 2 > ((MapExtraFactory)this.factory).maxDynamicEntries) {
            Platform.get().log("Ignoring request to add > %s dynamic keys", 64, null);
            growthAllowed = false;
        }
        Object[] newState = null;
        int endOfOurs = ourstate.length;
        for (int i = 0; i < theirstate.length && theirstate[i] != null; i += 2) {
            Object theirValue = theirstate[i + 1];
            if (LongBitSet.isSet(newToOurs, i / 2)) {
                if (!growthAllowed) continue;
                if (newState == null) {
                    newState = Arrays.copyOf(ourstate, newstateLength);
                }
                newState[endOfOurs] = theirstate[i];
                newState[endOfOurs + 1] = theirValue;
                endOfOurs += 2;
                continue;
            }
            int ourIndex = this.indexOfExistingKey(ourstate, theirstate[i]);
            assert (ourIndex != -1);
            Object ourValue = ourstate[ourIndex + 1];
            if (ourValue != null || theirValue == null) continue;
            if (newState == null) {
                newState = Arrays.copyOf(ourstate, newstateLength);
            }
            newState[ourIndex + 1] = theirValue;
        }
        if (newState != null) {
            this.state = newState;
        }
    }

    int indexOfExistingKey(Object[] state, K key) {
        int i = this.indexOfInitialKey(key);
        if (i == -1 && ((MapExtraFactory)this.factory).maxDynamicEntries > 0) {
            i = this.indexOfDynamicKey(state, key);
        }
        return i;
    }

    int indexOfInitialKey(K key) {
        Integer index = ((MapExtraFactory)this.factory).initialFieldIndices.get(key);
        return index != null ? index : -1;
    }

    int indexOfDynamicKey(Object[] state, K key) {
        for (int i = ((MapExtraFactory)this.factory).initialArrayLength; i < state.length && state[i] != null; i += 2) {
            if (!key.equals(state[i])) continue;
            return i;
        }
        return -1;
    }

    boolean addNewEntry(Object[] prior, K key, @Nullable V value) {
        int newIndex = prior.length;
        int newstateLength = newIndex + 2;
        if (newstateLength / 2 > 64) {
            Platform.get().log("Ignoring request to add > %s dynamic entries", 64, null);
            return false;
        }
        Object[] newState = Arrays.copyOf(prior, newstateLength);
        newState[newIndex] = key;
        newState[newIndex + 1] = value;
        this.state = newState;
        return true;
    }

    @Override
    protected boolean stateEquals(Object thatState) {
        return Arrays.equals(this.state(), (Object[])thatState);
    }

    @Override
    protected int stateHashCode() {
        return Arrays.hashCode(this.state());
    }

    @Override
    protected String stateString() {
        return Arrays.toString(this.state());
    }
}

