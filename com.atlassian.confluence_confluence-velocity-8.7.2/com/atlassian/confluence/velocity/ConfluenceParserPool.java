/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.apache.commons.pool2.PooledObject
 *  org.apache.commons.pool2.PooledObjectFactory
 *  org.apache.commons.pool2.impl.DefaultPooledObject
 *  org.apache.commons.pool2.impl.GenericObjectPool
 *  org.apache.commons.pool2.impl.GenericObjectPoolConfig
 *  org.apache.velocity.runtime.ParserPool
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.parser.Parser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.velocity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.velocity.runtime.ParserPool;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceParserPool
implements ParserPool {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceParserPool.class);
    private GenericObjectPoolConfig<Parser> config;
    private GenericObjectPool<Parser> pool;

    public void initialize(RuntimeServices rsvc) {
        this.config = new GenericObjectPoolConfig();
        this.config.setMaxTotal(rsvc.getInt("parser.pool.size", 20));
        this.config.setMaxIdle(rsvc.getInt("parser.pool.maxIdle", 5));
        this.config.setMaxWaitMillis((long)rsvc.getInt("parser.pool.maxWait", 30000));
        this.pool = new GenericObjectPool((PooledObjectFactory)new ParserFactory(rsvc), this.config);
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
        this.pool.returnObject((Object)parser);
    }

    public String toString() {
        return "ConfluenceParserPool{config=" + ToStringBuilder.reflectionToString(this.config, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE) + "}";
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
    implements PooledObjectFactory<Parser> {
        private final RuntimeServices rsvc;

        public ParserFactory(RuntimeServices rsvc) {
            this.rsvc = rsvc;
        }

        public PooledObject<Parser> makeObject() {
            Parser newParser = this.rsvc.createNewParser();
            log.trace("Created parser: {}", (Object)newParser);
            return new DefaultPooledObject((Object)newParser);
        }

        public boolean validateObject(PooledObject<Parser> obj) {
            return true;
        }

        public void destroyObject(PooledObject<Parser> obj) {
            log.trace("Destroyed parser: {}", obj);
        }

        public void activateObject(PooledObject<Parser> obj) {
        }

        public void passivateObject(PooledObject<Parser> obj) {
        }
    }
}

