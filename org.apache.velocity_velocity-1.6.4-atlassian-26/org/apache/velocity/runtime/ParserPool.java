/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.Parser;

public interface ParserPool {
    public void initialize(RuntimeServices var1);

    public Parser get();

    public void put(Parser var1);
}

