/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.jndi;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.hibernate.engine.jndi.internal.JndiServiceImpl;

@Deprecated
public final class JndiHelper {
    private JndiHelper() {
    }

    public static Properties extractJndiProperties(Map configurationValues) {
        return JndiServiceImpl.extractJndiProperties(configurationValues);
    }

    public static InitialContext getInitialContext(Properties props) throws NamingException {
        Properties hash = JndiHelper.extractJndiProperties(props);
        return ((Hashtable)hash).size() == 0 ? new InitialContext() : new InitialContext(hash);
    }

    public static void bind(Context ctx, String name, Object val) throws NamingException {
        try {
            ctx.rebind(name, val);
        }
        catch (Exception e) {
            Name n = ctx.getNameParser("").parse(name);
            while (n.size() > 1) {
                String ctxName = n.get(0);
                Context subctx = null;
                try {
                    subctx = (Context)ctx.lookup(ctxName);
                }
                catch (NameNotFoundException nameNotFoundException) {
                    // empty catch block
                }
                ctx = subctx != null ? subctx : ctx.createSubcontext(ctxName);
                n = n.getSuffix(1);
            }
            ctx.rebind(n, val);
        }
    }
}

