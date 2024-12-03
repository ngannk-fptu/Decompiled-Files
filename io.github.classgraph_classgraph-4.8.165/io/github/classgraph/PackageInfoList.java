/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.MappableInfoList;
import io.github.classgraph.PackageInfo;
import java.util.Collection;

public class PackageInfoList
extends MappableInfoList<PackageInfo> {
    private static final long serialVersionUID = 1L;
    static final PackageInfoList EMPTY_LIST = new PackageInfoList(){
        private static final long serialVersionUID = 1L;

        @Override
        public boolean add(PackageInfo e) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public void add(int index, PackageInfo element) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public boolean remove(Object o) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public PackageInfo remove(int index) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public boolean addAll(Collection<? extends PackageInfo> c) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public boolean addAll(int index, Collection<? extends PackageInfo> c) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public void clear() {
            throw new IllegalArgumentException("List is immutable");
        }

        @Override
        public PackageInfo set(int index, PackageInfo element) {
            throw new IllegalArgumentException("List is immutable");
        }
    };

    PackageInfoList() {
    }

    PackageInfoList(int sizeHint) {
        super(sizeHint);
    }

    PackageInfoList(Collection<PackageInfo> packageInfoCollection) {
        super(packageInfoCollection);
    }

    public PackageInfoList filter(PackageInfoFilter filter) {
        PackageInfoList packageInfoFiltered = new PackageInfoList();
        for (PackageInfo resource : this) {
            if (!filter.accept(resource)) continue;
            packageInfoFiltered.add(resource);
        }
        return packageInfoFiltered;
    }

    @FunctionalInterface
    public static interface PackageInfoFilter {
        public boolean accept(PackageInfo var1);
    }
}

