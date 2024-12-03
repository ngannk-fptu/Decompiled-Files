/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.apache.commons.pool.PoolableObjectFactory
 *  org.apache.commons.pool.impl.GenericObjectPool
 *  org.apache.commons.pool.impl.GenericObjectPool$Config
 *  org.apache.velocity.runtime.ParserPool
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.parser.Parser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.templaterenderer.velocity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.velocity.runtime.ParserPool;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicParserPool
implements ParserPool {
    private static final Logger log = LoggerFactory.getLogger(DynamicParserPool.class);
    private GenericObjectPool.Config config;
    private GenericObjectPool pool;

    public void initialize(RuntimeServices rsvc) {
        this.config = new GenericObjectPool.Config();
        this.config.maxActive = rsvc.getInt("parser.pool.size", 20);
        this.config.maxIdle = rsvc.getInt("parser.pool.maxIdle", 5);
        this.config.maxWait = rsvc.getInt("parser.pool.maxWait", 30000);
        this.config.timeBetweenEvictionRunsMillis = -1L;
        this.pool = new GenericObjectPool((PoolableObjectFactory)new ParserFactory(rsvc), this.config);
        if (rsvc.getLog().isDebugEnabled()) {
            rsvc.getLog().debug((Object)("Created parser pool: " + this));
        }
    }

    public Parser get() {
        try {
            return (Parser)this.pool.borrowObject();
        }
        catch (Exception e) {
            throw new RuntimeException("Error borrowing a parser from the pool", e);
        }
    }

    public void put(Parser parser) {
        try {
            this.pool.returnObject((Object)parser);
        }
        catch (Exception e) {
            throw new RuntimeException("Error returning a parser to the pool", e);
        }
    }

    public String toString() {
        return "DynamicParserPool{config=" + ToStringBuilder.reflectionToString((Object)this.config, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE) + '}';
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
            log.trace("Created parser: {}", (Object)newParser);
            return newParser;
        }

        public boolean validateObject(Object obj) {
            return true;
        }

        public void destroyObject(Object obj) throws Exception {
            log.trace("Destroyed parser: {}", obj);
        }

        public void activateObject(Object obj) throws Exception {
        }

        public void passivateObject(Object obj) throws Exception {
        }
    }
}

