/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.value.AbstractQValue;

public class DefaultQValue
extends AbstractQValue
implements Serializable {
    private static final long serialVersionUID = -3887529703765183611L;
    protected static final QValue TRUE = new DefaultQValue(Boolean.TRUE);
    protected static final QValue FALSE = new DefaultQValue(Boolean.FALSE);

    public DefaultQValue(String value, int type) {
        super(value, type);
    }

    public DefaultQValue(Long value) {
        super(value);
    }

    public DefaultQValue(Double value) {
        super(value);
    }

    public DefaultQValue(BigDecimal value) {
        super(value);
    }

    public DefaultQValue(Boolean value) {
        super(value);
    }

    public DefaultQValue(Name value) {
        super(value);
    }

    public DefaultQValue(Path value) {
        super(value);
    }

    public DefaultQValue(URI value) {
        super(value);
    }

    protected DefaultQValue(Calendar value) {
        super(value);
    }

    @Override
    public InputStream getStream() throws RepositoryException {
        try {
            return new ByteArrayInputStream(this.getString().getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RepositoryException("UTF-8 is not supported encoding on this platform", e);
        }
    }
}

