/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.api.model.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.permissions.Operation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.codehaus.jackson.annotate.JsonCreator;

@ExperimentalApi
public final class OperationKey
extends BaseApiEnum
implements Operation {
    public static final OperationKey USE = new OperationKey("use");
    public static final OperationKey CREATE = new OperationKey("create");
    public static final OperationKey READ = new OperationKey("read");
    public static final OperationKey UPDATE = new OperationKey("update");
    public static final OperationKey DELETE = new OperationKey("delete");
    public static final OperationKey COPY = new OperationKey("copy");
    public static final OperationKey MOVE = new OperationKey("move");
    public static final OperationKey EXPORT = new OperationKey("export");
    public static final OperationKey PURGE = new OperationKey("purge");
    public static final OperationKey PURGE_VERSION = new OperationKey("purge_version");
    public static final OperationKey ADMINISTER = new OperationKey("administer");
    public static final OperationKey RESTORE = new OperationKey("restore");
    public static final List<OperationKey> BUILT_IN = Collections.unmodifiableList(Arrays.asList(USE, CREATE, READ, UPDATE, DELETE, COPY, MOVE, EXPORT, PURGE, PURGE_VERSION, ADMINISTER, RESTORE));
    public static final List<OperationKey> READ_ONLY_WHITELIST = Collections.unmodifiableList(Arrays.asList(USE, READ, EXPORT, ADMINISTER));

    @JsonCreator
    public static OperationKey valueOf(String name) {
        for (OperationKey operationKey : BUILT_IN) {
            if (!operationKey.value.equals(name)) continue;
            return operationKey;
        }
        return new OperationKey(name);
    }

    private OperationKey(String name) {
        super(Objects.requireNonNull(name));
    }

    @Override
    public @NonNull OperationKey getOperationKey() {
        return this;
    }
}

