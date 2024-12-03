/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import java.io.Serializable;

public class CommandResult<T extends Serializable>
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final CommandResult<?> NOT_FOUND = new CommandResult<Object>(null, true);
    private T data;
    private boolean bucketNotFound;

    public CommandResult(T data, boolean bucketNotFound) {
        this.data = data;
        this.bucketNotFound = bucketNotFound;
    }

    public static <R extends Serializable> CommandResult<R> success(R data) {
        return new CommandResult<R>(data, false);
    }

    public static <R extends Serializable> CommandResult<R> bucketNotFound() {
        return NOT_FOUND;
    }

    public T getData() {
        return this.data;
    }

    public boolean isBucketNotFound() {
        return this.bucketNotFound;
    }
}

