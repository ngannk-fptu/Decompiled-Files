/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CompoundList {
    private CompoundList() {
        throw new UnsupportedOperationException("This class is a utility class and not supposed to be instantiated");
    }

    public static <S> List<S> of(S left, List<? extends S> right) {
        if (right.isEmpty()) {
            return Collections.singletonList(left);
        }
        ArrayList<S> list = new ArrayList<S>(1 + right.size());
        list.add(left);
        list.addAll(right);
        return list;
    }

    public static <S> List<S> of(List<? extends S> left, S right) {
        if (left.isEmpty()) {
            return Collections.singletonList(right);
        }
        ArrayList<S> list = new ArrayList<S>(left.size() + 1);
        list.addAll(left);
        list.add(right);
        return list;
    }

    public static <S> List<S> of(List<? extends S> left, List<? extends S> right) {
        ArrayList<S> list = new ArrayList<S>(left.size() + right.size());
        list.addAll(left);
        list.addAll(right);
        return list;
    }

    public static <S> List<S> of(List<? extends S> left, List<? extends S> middle, List<? extends S> right) {
        ArrayList<S> list = new ArrayList<S>(left.size() + middle.size() + right.size());
        list.addAll(left);
        list.addAll(middle);
        list.addAll(right);
        return list;
    }
}

