/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WeightedEvictableList<T> {
    private List<WeightedItem<T>> list = new ArrayList<WeightedItem<T>>();
    private final int maxSize;
    private final int maxVotesBeforeReorganization;
    private int reorganizationCounter;
    private final Comparator<WeightedItem<T>> itemComparator = new Comparator<WeightedItem<T>>(){

        @Override
        public int compare(WeightedItem<T> o1, WeightedItem<T> o2) {
            return o2.weight - o1.weight;
        }
    };

    public WeightedEvictableList(int maxSize, int maxVotesBeforeReorganization) {
        this.maxSize = maxSize;
        this.maxVotesBeforeReorganization = maxVotesBeforeReorganization;
    }

    public List<WeightedItem<T>> getList() {
        return this.list;
    }

    public void voteFor(WeightedItem<T> weightedItem) {
        ++this.reorganizationCounter;
        ((WeightedItem)weightedItem).vote();
        if (this.reorganizationCounter == this.maxVotesBeforeReorganization) {
            this.reorganizationCounter = 0;
            this.organizeAndAdd(null);
        }
    }

    public WeightedItem<T> addOrVote(T item) {
        for (int i = 0; i < this.list.size(); ++i) {
            WeightedItem<T> weightedItem = this.list.get(i);
            if (!weightedItem.item.equals(item)) continue;
            this.voteFor(weightedItem);
            return weightedItem;
        }
        return this.organizeAndAdd(item);
    }

    public WeightedItem<T> getWeightedItem(int index) {
        return this.list.get(index);
    }

    public int size() {
        return this.list.size();
    }

    WeightedItem<T> organizeAndAdd(T item) {
        Collections.sort(this.list, this.itemComparator);
        if (this.list.size() == this.maxSize && item != null) {
            for (int i = this.list.size() - 1; i >= this.maxSize / 2; --i) {
                this.list.remove(i);
            }
            for (WeightedItem<T> it : this.list) {
                it.weight = 0;
            }
        }
        WeightedItem<T> returnValue = null;
        if (item != null) {
            returnValue = new WeightedItem<T>(item);
            returnValue.weight = 1;
            this.list.add(returnValue);
        }
        return returnValue;
    }

    public static class WeightedItem<T> {
        final T item;
        int weight;

        WeightedItem(T item) {
            this.item = item;
            this.weight = 0;
        }

        WeightedItem(WeightedItem<T> other) {
            this.item = other.item;
            this.weight = other.weight;
        }

        private void vote() {
            ++this.weight;
        }

        public T getItem() {
            return this.item;
        }
    }
}

