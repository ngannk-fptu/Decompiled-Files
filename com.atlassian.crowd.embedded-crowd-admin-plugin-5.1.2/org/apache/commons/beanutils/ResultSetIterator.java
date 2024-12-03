/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.ResultSetDynaClass;

public class ResultSetIterator
implements DynaBean,
Iterator<DynaBean> {
    protected boolean current = false;
    protected ResultSetDynaClass dynaClass = null;
    protected boolean eof = false;

    ResultSetIterator(ResultSetDynaClass dynaClass) {
        this.dynaClass = dynaClass;
    }

    @Override
    public boolean contains(String name, String key) {
        throw new UnsupportedOperationException("FIXME - mapped properties not currently supported");
    }

    @Override
    public Object get(String name) {
        if (this.dynaClass.getDynaProperty(name) == null) {
            throw new IllegalArgumentException(name);
        }
        try {
            return this.dynaClass.getObjectFromResultSet(name);
        }
        catch (SQLException e) {
            throw new RuntimeException("get(" + name + "): SQLException: " + e);
        }
    }

    @Override
    public Object get(String name, int index) {
        throw new UnsupportedOperationException("FIXME - indexed properties not currently supported");
    }

    @Override
    public Object get(String name, String key) {
        throw new UnsupportedOperationException("FIXME - mapped properties not currently supported");
    }

    @Override
    public DynaClass getDynaClass() {
        return this.dynaClass;
    }

    @Override
    public void remove(String name, String key) {
        throw new UnsupportedOperationException("FIXME - mapped operations not currently supported");
    }

    @Override
    public void set(String name, Object value) {
        if (this.dynaClass.getDynaProperty(name) == null) {
            throw new IllegalArgumentException(name);
        }
        try {
            this.dynaClass.getResultSet().updateObject(name, value);
        }
        catch (SQLException e) {
            throw new RuntimeException("set(" + name + "): SQLException: " + e);
        }
    }

    @Override
    public void set(String name, int index, Object value) {
        throw new UnsupportedOperationException("FIXME - indexed properties not currently supported");
    }

    @Override
    public void set(String name, String key, Object value) {
        throw new UnsupportedOperationException("FIXME - mapped properties not currently supported");
    }

    @Override
    public boolean hasNext() {
        try {
            this.advance();
            return !this.eof;
        }
        catch (SQLException e) {
            throw new RuntimeException("hasNext():  SQLException:  " + e);
        }
    }

    @Override
    public DynaBean next() {
        try {
            this.advance();
            if (this.eof) {
                throw new NoSuchElementException();
            }
            this.current = false;
            return this;
        }
        catch (SQLException e) {
            throw new RuntimeException("next():  SQLException:  " + e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove()");
    }

    protected void advance() throws SQLException {
        if (!this.current && !this.eof) {
            if (this.dynaClass.getResultSet().next()) {
                this.current = true;
                this.eof = false;
            } else {
                this.current = false;
                this.eof = true;
            }
        }
    }
}

