/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime;

import org.apache.velocity.runtime.ParserPool;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.CharStream;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.util.SimplePool;

public class ParserPoolImpl
implements ParserPool {
    SimplePool pool = null;
    int max = 20;

    @Override
    public void initialize(RuntimeServices rsvc) {
        this.max = rsvc.getInt("parser.pool.size", 20);
        this.pool = new SimplePool(this.max);
        for (int i = 0; i < this.max; ++i) {
            this.pool.put(rsvc.createNewParser());
        }
        if (rsvc.getLog().isDebugEnabled()) {
            rsvc.getLog().debug("Created '" + this.max + "' parsers.");
        }
    }

    @Override
    public Parser get() {
        return (Parser)this.pool.get();
    }

    @Override
    public void put(Parser parser) {
        parser.ReInit((CharStream)null);
        this.pool.put(parser);
    }
}

