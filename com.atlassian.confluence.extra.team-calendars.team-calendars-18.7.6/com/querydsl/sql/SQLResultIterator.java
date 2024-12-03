/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.QueryException;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLDetailedListener;
import com.querydsl.sql.SQLListenerContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public abstract class SQLResultIterator<T>
implements CloseableIterator<T> {
    @Nullable
    private Boolean next = null;
    private final Configuration configuration;
    private final ResultSet rs;
    private final Statement stmt;
    private final SQLDetailedListener listener;
    private final SQLListenerContext context;

    public SQLResultIterator(Configuration conf, Statement stmt, ResultSet rs) {
        this(conf, stmt, rs, null, null);
    }

    public SQLResultIterator(Configuration conf, Statement stmt, ResultSet rs, SQLDetailedListener listener, SQLListenerContext context) {
        this.configuration = conf;
        this.stmt = stmt;
        this.rs = rs;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void close() {
        try {
            try {
                if (this.rs != null) {
                    this.rs.close();
                }
            }
            finally {
                if (this.stmt != null) {
                    this.stmt.close();
                }
            }
        }
        catch (SQLException e) {
            throw this.configuration.translate(e);
        }
        finally {
            if (this.listener != null) {
                this.listener.end(this.context);
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (this.next == null) {
            try {
                this.next = this.rs.next();
            }
            catch (SQLException e) {
                this.close();
                throw this.configuration.translate(e);
            }
        }
        return this.next;
    }

    @Override
    public T next() {
        if (this.hasNext()) {
            this.next = null;
            try {
                return this.produceNext(this.rs);
            }
            catch (SQLException e) {
                this.close();
                throw this.configuration.translate(e);
            }
            catch (Exception e) {
                this.close();
                throw new QueryException(e);
            }
        }
        throw new NoSuchElementException();
    }

    protected abstract T produceNext(ResultSet var1) throws Exception;

    @Override
    public void remove() {
        try {
            this.rs.deleteRow();
        }
        catch (SQLException e) {
            this.close();
            throw this.configuration.translate(e);
        }
    }
}

