/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.testUtil;

import ch.qos.logback.core.testUtil.MockInitialContext;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class MockInitialContextFactory
implements InitialContextFactory {
    static MockInitialContext mic;

    public static void initialize() {
        try {
            mic = new MockInitialContext();
        }
        catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return mic;
    }

    public static MockInitialContext getContext() {
        return mic;
    }

    static {
        System.out.println("MockInitialContextFactory static called");
        MockInitialContextFactory.initialize();
    }
}

