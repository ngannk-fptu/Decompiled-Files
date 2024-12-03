/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.extractor.ArgumentParser;

public class DefaultArgumentParser
extends ArgumentParser<Object, Object> {
    @Override
    public Object parse(Object input) {
        return input;
    }
}

