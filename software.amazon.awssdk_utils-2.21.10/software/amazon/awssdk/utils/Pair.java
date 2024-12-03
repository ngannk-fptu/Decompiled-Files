/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.function.BiFunction;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class Pair<LeftT, RightT> {
    private final LeftT left;
    private final RightT right;

    private Pair(LeftT left, RightT right) {
        this.left = Validate.paramNotNull(left, "left");
        this.right = Validate.paramNotNull(right, "right");
    }

    public LeftT left() {
        return this.left;
    }

    public RightT right() {
        return this.right;
    }

    public <ReturnT> ReturnT apply(BiFunction<LeftT, RightT, ReturnT> function) {
        return function.apply(this.left, this.right);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair)obj;
        return other.left.equals(this.left) && other.right.equals(this.right);
    }

    public int hashCode() {
        return this.getClass().hashCode() + this.left.hashCode() + this.right.hashCode();
    }

    public String toString() {
        return "Pair(left=" + this.left + ", right=" + this.right + ")";
    }

    public static <LeftT, RightT> Pair<LeftT, RightT> of(LeftT left, RightT right) {
        return new Pair<LeftT, RightT>(left, right);
    }
}

