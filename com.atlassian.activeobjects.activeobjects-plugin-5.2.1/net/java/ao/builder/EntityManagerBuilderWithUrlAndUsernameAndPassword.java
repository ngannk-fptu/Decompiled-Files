/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.java.ao.builder;

import java.util.Objects;
import net.java.ao.ActiveObjectsException;
import net.java.ao.builder.BuilderDatabaseProperties;
import net.java.ao.builder.ConnectionPool;
import net.java.ao.builder.EntityManagerBuilder;
import net.java.ao.builder.EntityManagerBuilderWithDatabaseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EntityManagerBuilderWithUrlAndUsernameAndPassword {
    private final Logger logger = LoggerFactory.getLogger(EntityManagerBuilder.class);
    private final String url;
    private final String username;
    private final String password;
    private String schema;

    public EntityManagerBuilderWithUrlAndUsernameAndPassword(String url, String username, String password) {
        this.url = Objects.requireNonNull(url, "url can't be null");
        this.username = Objects.requireNonNull(username, "username can't be null");
        this.password = Objects.requireNonNull(password, "password can't be null");
    }

    public EntityManagerBuilderWithUrlAndUsernameAndPassword schema(String schema) {
        this.schema = schema;
        return this;
    }

    public EntityManagerBuilderWithDatabaseProperties none() {
        return this.getEntityManagerBuilderWithDatabaseProperties(ConnectionPool.NONE);
    }

    public EntityManagerBuilderWithDatabaseProperties dbcp() {
        return this.getEntityManagerBuilderWithDatabaseProperties(ConnectionPool.DBCP);
    }

    public EntityManagerBuilderWithDatabaseProperties proxool() {
        return this.getEntityManagerBuilderWithDatabaseProperties(ConnectionPool.PROXOOL);
    }

    public EntityManagerBuilderWithDatabaseProperties dbPool() {
        return this.getEntityManagerBuilderWithDatabaseProperties(ConnectionPool.DBPOOL);
    }

    public EntityManagerBuilderWithDatabaseProperties c3po() {
        return this.getEntityManagerBuilderWithDatabaseProperties(ConnectionPool.C3PO);
    }

    public EntityManagerBuilderWithDatabaseProperties auto() {
        for (ConnectionPool pool : ConnectionPool.values()) {
            if (!pool.isAvailable()) continue;
            return this.getEntityManagerBuilderWithDatabaseProperties(pool);
        }
        throw new ActiveObjectsException("Could not find any connection pool! Impossible, " + ConnectionPool.NONE + " should always be an option...");
    }

    private EntityManagerBuilderWithDatabaseProperties getEntityManagerBuilderWithDatabaseProperties(ConnectionPool pool) {
        if (pool.isAvailable()) {
            this.logger.debug("Entity manager will be using connection pool '{}'.", (Object)pool);
            return new EntityManagerBuilderWithDatabaseProperties(this.getDatabaseProperties(pool));
        }
        throw new ActiveObjectsException("Connection pool " + pool + " is not available on the classpath");
    }

    private BuilderDatabaseProperties getDatabaseProperties(ConnectionPool connectionPool) {
        BuilderDatabaseProperties properties = new BuilderDatabaseProperties(this.url, this.username, this.password, connectionPool);
        properties.setSchema(this.schema);
        return properties;
    }
}

