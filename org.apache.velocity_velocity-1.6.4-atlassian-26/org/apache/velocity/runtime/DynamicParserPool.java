/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ReflectionToStringBuilder
 *  org.apache.commons.pool.PoolableObjectFactory
 *  org.apache.commons.pool.impl.GenericObjectPool
 *  org.apache.commons.pool.impl.GenericObjectPool$Config
 */
package org.apache.velocity.runtime;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.velocity.runtime.ParserPool;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.Parser;

public class DynamicParserPool
implements ParserPool {
    private GenericObjectPool.Config config;
    private GenericObjectPool pool;

    @Override
    public void initialize(RuntimeServices rsvc) {
        this.config = new GenericObjectPool.Config();
        this.config.maxActive = rsvc.getInt("parser.pool.size", 20);
        this.config.maxIdle = rsvc.getInt("parser.pool.maxIdle", 5);
        this.config.maxWait = rsvc.getInt("parser.pool.maxWait", 30000);
        this.config.timeBetweenEvictionRunsMillis = -1L;
        this.pool = new GenericObjectPool((PoolableObjectFactory)new ParserFactory(rsvc), this.config);
        if (rsvc.getLog().isDebugEnabled()) {
            rsvc.getLog().debug("Created parser pool: " + this);
        }
    }

    @Override
    public Parser get() {
        try {
            return (Parser)this.pool.borrowObject();
        }
        catch (Exception e) {
            throw new RuntimeException("Error borrowing a parser from the pool", e);
        }
    }

    @Override
    public void put(Parser parser) {
        try {
            this.pool.returnObject((Object)parser);
        }
        catch (Exception e) {
            throw new RuntimeException("Error returning a parser to the pool", e);
        }
    }

    public String toString() {
        return "DynamicParserPool{config=" + ReflectionToStringBuilder.toString((Object)this.config) + '}';
    }

    private static class Props {
        static final String MAX_ACTIVE = "parser.pool.size";
        static final int MAX_ACTIVE_DEFAULT = 20;
        static final String MAX_IDLE = "parser.pool.maxIdle";
        static final int MAX_IDLE_DEFAULT = 5;
        static final String MAX_WAIT = "parser.pool.maxWait";
        static final int MAX_WAIT_DEFAULT = 30000;

        private Props() {
        }
    }

    private static class ParserFactory
    implements PoolableObjectFactory {
        private final RuntimeServices rsvc;

        public ParserFactory(RuntimeServices rsvc) {
            this.rsvc = rsvc;
        }

        public Object makeObject() throws Exception {
            Parser newParser = this.rsvc.createNewParser();
            this.rsvc.getLog().trace(String.format("Created parser: %s", newParser));
            return newParser;
        }

        public boolean validateObject(Object obj) {
            return true;
        }

        public void destroyObject(Object obj) throws Exception {
            this.rsvc.getLog().trace(String.format("Destroyed parser: %s", obj));
        }

        public void activateObject(Object obj) throws Exception {
        }

        public void passivateObject(Object obj) throws Exception {
        }
    }
}

