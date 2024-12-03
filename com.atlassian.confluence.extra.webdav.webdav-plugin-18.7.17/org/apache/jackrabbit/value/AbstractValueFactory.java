/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;
import org.apache.jackrabbit.value.BinaryImpl;
import org.apache.jackrabbit.value.BinaryValue;
import org.apache.jackrabbit.value.BooleanValue;
import org.apache.jackrabbit.value.DateValue;
import org.apache.jackrabbit.value.DecimalValue;
import org.apache.jackrabbit.value.DoubleValue;
import org.apache.jackrabbit.value.LongValue;
import org.apache.jackrabbit.value.NameValue;
import org.apache.jackrabbit.value.PathValue;
import org.apache.jackrabbit.value.ReferenceValue;
import org.apache.jackrabbit.value.StringValue;
import org.apache.jackrabbit.value.URIValue;
import org.apache.jackrabbit.value.WeakReferenceValue;

public abstract class AbstractValueFactory
implements ValueFactory {
    protected AbstractValueFactory() {
    }

    protected abstract void checkPathFormat(String var1) throws ValueFormatException;

    protected abstract void checkNameFormat(String var1) throws ValueFormatException;

    @Override
    public Value createValue(boolean value) {
        return new BooleanValue(value);
    }

    @Override
    public Value createValue(Calendar value) {
        return new DateValue(value);
    }

    @Override
    public Value createValue(double value) {
        return new DoubleValue(value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Value createValue(InputStream value) {
        try {
            BinaryValue binaryValue = new BinaryValue(value);
            return binaryValue;
        }
        finally {
            try {
                value.close();
            }
            catch (IOException iOException) {}
        }
    }

    @Override
    public Value createValue(long value) {
        return new LongValue(value);
    }

    @Override
    public Value createValue(Node value) throws RepositoryException {
        return this.createValue(value, false);
    }

    @Override
    public Value createValue(String value) {
        return new StringValue(value);
    }

    @Override
    public Value createValue(String value, int type) throws ValueFormatException {
        BaseValue val;
        switch (type) {
            case 1: {
                val = new StringValue(value);
                break;
            }
            case 6: {
                val = BooleanValue.valueOf(value);
                break;
            }
            case 4: {
                val = DoubleValue.valueOf(value);
                break;
            }
            case 3: {
                val = LongValue.valueOf(value);
                break;
            }
            case 12: {
                val = DecimalValue.valueOf(value);
                break;
            }
            case 5: {
                val = DateValue.valueOf(value);
                break;
            }
            case 7: {
                this.checkNameFormat(value);
                val = NameValue.valueOf(value);
                break;
            }
            case 8: {
                this.checkPathFormat(value);
                val = PathValue.valueOf(value);
                break;
            }
            case 11: {
                val = URIValue.valueOf(value);
                break;
            }
            case 9: {
                val = ReferenceValue.valueOf(value);
                break;
            }
            case 10: {
                val = WeakReferenceValue.valueOf(value);
                break;
            }
            case 2: {
                val = new BinaryValue(value);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid type constant: " + type);
            }
        }
        return val;
    }

    @Override
    public Binary createBinary(InputStream stream) throws RepositoryException {
        try {
            BinaryImpl binaryImpl = new BinaryImpl(stream);
            return binaryImpl;
        }
        catch (IOException e) {
            throw new RepositoryException("failed to create Binary instance", e);
        }
        finally {
            try {
                stream.close();
            }
            catch (IOException iOException) {}
        }
    }

    @Override
    public Value createValue(Binary value) {
        return new BinaryValue(value);
    }

    @Override
    public Value createValue(BigDecimal value) {
        return new DecimalValue(value);
    }

    @Override
    public Value createValue(Node node, boolean weak) throws RepositoryException {
        if (weak) {
            return new WeakReferenceValue(node);
        }
        return new ReferenceValue(node);
    }
}

