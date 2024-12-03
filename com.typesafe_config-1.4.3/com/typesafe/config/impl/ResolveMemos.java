/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.BadMap;
import com.typesafe.config.impl.MemoKey;

final class ResolveMemos {
    private final BadMap<MemoKey, AbstractConfigValue> memos;

    private ResolveMemos(BadMap<MemoKey, AbstractConfigValue> memos) {
        this.memos = memos;
    }

    ResolveMemos() {
        this(new BadMap<MemoKey, AbstractConfigValue>());
    }

    AbstractConfigValue get(MemoKey key) {
        return this.memos.get(key);
    }

    ResolveMemos put(MemoKey key, AbstractConfigValue value) {
        return new ResolveMemos(this.memos.copyingPut(key, value));
    }
}

