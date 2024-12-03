/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.util.Properties;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.AbstractUUIDGenerator;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class UUIDHexGenerator
extends AbstractUUIDGenerator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(UUIDHexGenerator.class);
    private static boolean WARNED;
    private String sep = "";

    public UUIDHexGenerator() {
        if (!WARNED) {
            WARNED = true;
            LOG.usingUuidHexGenerator(this.getClass().getName(), UUIDGenerator.class.getName());
        }
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.sep = ConfigurationHelper.getString("separator", params, "");
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return this.format(this.getIP()) + this.sep + this.format(this.getJVM()) + this.sep + this.format(this.getHiTime()) + this.sep + this.format(this.getLoTime()) + this.sep + this.format(this.getCount());
    }

    protected String format(int intValue) {
        String formatted = Integer.toHexString(intValue);
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    protected String format(short shortValue) {
        String formatted = Integer.toHexString(shortValue);
        StringBuilder buf = new StringBuilder("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }
}

