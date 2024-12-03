/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import org.hibernate.bytecode.enhance.spi.interceptor.SessionAssociableInterceptor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public abstract class AbstractInterceptor
implements SessionAssociableInterceptor {
    private final String entityName;
    private transient SharedSessionContractImplementor session;
    private boolean allowLoadOutsideTransaction;
    private String sessionFactoryUuid;

    public AbstractInterceptor(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    @Override
    public SharedSessionContractImplementor getLinkedSession() {
        return this.session;
    }

    @Override
    public void setSession(SharedSessionContractImplementor session) {
        this.session = session;
        if (session != null && !this.allowLoadOutsideTransaction) {
            this.allowLoadOutsideTransaction = session.getFactory().getSessionFactoryOptions().isInitializeLazyStateOutsideTransactionsEnabled();
            if (this.allowLoadOutsideTransaction) {
                this.sessionFactoryUuid = session.getFactory().getUuid();
            }
        }
    }

    @Override
    public void unsetSession() {
        this.session = null;
    }

    @Override
    public boolean allowLoadOutsideTransaction() {
        return this.allowLoadOutsideTransaction;
    }

    @Override
    public String getSessionFactoryUuid() {
        return this.sessionFactoryUuid;
    }

    protected abstract Object handleRead(Object var1, String var2, Object var3);

    protected abstract Object handleWrite(Object var1, String var2, Object var3, Object var4);

    @Override
    public boolean readBoolean(Object obj, String name, boolean oldValue) {
        return (Boolean)this.handleRead(obj, name, oldValue);
    }

    @Override
    public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
        return (Boolean)this.handleWrite(obj, name, oldValue, newValue);
    }

    @Override
    public byte readByte(Object obj, String name, byte oldValue) {
        return (Byte)this.handleRead(obj, name, oldValue);
    }

    @Override
    public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
        return (Byte)this.handleWrite(obj, name, oldValue, newValue);
    }

    @Override
    public char readChar(Object obj, String name, char oldValue) {
        return ((Character)this.handleRead(obj, name, Character.valueOf(oldValue))).charValue();
    }

    @Override
    public char writeChar(Object obj, String name, char oldValue, char newValue) {
        return ((Character)this.handleWrite(obj, name, Character.valueOf(oldValue), Character.valueOf(newValue))).charValue();
    }

    @Override
    public short readShort(Object obj, String name, short oldValue) {
        return (Short)this.handleRead(obj, name, oldValue);
    }

    @Override
    public short writeShort(Object obj, String name, short oldValue, short newValue) {
        return (Short)this.handleWrite(obj, name, oldValue, newValue);
    }

    @Override
    public int readInt(Object obj, String name, int oldValue) {
        return (Integer)this.handleRead(obj, name, oldValue);
    }

    @Override
    public int writeInt(Object obj, String name, int oldValue, int newValue) {
        return (Integer)this.handleWrite(obj, name, oldValue, newValue);
    }

    @Override
    public float readFloat(Object obj, String name, float oldValue) {
        return ((Float)this.handleRead(obj, name, Float.valueOf(oldValue))).floatValue();
    }

    @Override
    public float writeFloat(Object obj, String name, float oldValue, float newValue) {
        return ((Float)this.handleWrite(obj, name, Float.valueOf(oldValue), Float.valueOf(newValue))).floatValue();
    }

    @Override
    public double readDouble(Object obj, String name, double oldValue) {
        return (Double)this.handleRead(obj, name, oldValue);
    }

    @Override
    public double writeDouble(Object obj, String name, double oldValue, double newValue) {
        return (Double)this.handleWrite(obj, name, oldValue, newValue);
    }

    @Override
    public long readLong(Object obj, String name, long oldValue) {
        return (Long)this.handleRead(obj, name, oldValue);
    }

    @Override
    public long writeLong(Object obj, String name, long oldValue, long newValue) {
        return (Long)this.handleWrite(obj, name, oldValue, newValue);
    }

    @Override
    public Object readObject(Object obj, String name, Object oldValue) {
        return this.handleRead(obj, name, oldValue);
    }

    @Override
    public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
        return this.handleWrite(obj, name, oldValue, newValue);
    }
}

