/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

public interface ValueFactory {
    public Value createValue(String var1);

    public Value createValue(String var1, int var2) throws ValueFormatException;

    public Value createValue(long var1);

    public Value createValue(double var1);

    public Value createValue(BigDecimal var1);

    public Value createValue(boolean var1);

    public Value createValue(Calendar var1);

    public Value createValue(InputStream var1);

    public Value createValue(Binary var1);

    public Value createValue(Node var1) throws RepositoryException;

    public Value createValue(Node var1, boolean var2) throws RepositoryException;

    public Binary createBinary(InputStream var1) throws RepositoryException;
}

