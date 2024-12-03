/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.lang3.StringUtils;

public class JNDIConfiguration
extends AbstractConfiguration {
    private String prefix;
    private Context context;
    private Context baseContext;
    private final Set<String> clearedProperties = new HashSet<String>();

    public JNDIConfiguration() throws NamingException {
        this((String)null);
    }

    public JNDIConfiguration(String prefix) throws NamingException {
        this(new InitialContext(), prefix);
    }

    public JNDIConfiguration(Context context) {
        this(context, null);
    }

    public JNDIConfiguration(Context context, String prefix) {
        this.context = context;
        this.prefix = prefix;
        this.initLogger(new ConfigurationLogger(JNDIConfiguration.class));
        this.addErrorLogListener();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void recursiveGetKeys(Set<String> keys, Context context, String prefix, Set<Context> processedCtx) throws NamingException {
        processedCtx.add(context);
        try (NamingEnumeration<NameClassPair> elements = null;){
            elements = context.list("");
            while (elements.hasMore()) {
                NameClassPair nameClassPair = elements.next();
                String name = nameClassPair.getName();
                Object object = context.lookup(name);
                StringBuilder keyBuilder = new StringBuilder();
                keyBuilder.append(prefix);
                if (keyBuilder.length() > 0) {
                    keyBuilder.append(".");
                }
                keyBuilder.append(name);
                if (object instanceof Context) {
                    Context subcontext = (Context)object;
                    if (processedCtx.contains(subcontext)) continue;
                    this.recursiveGetKeys(keys, subcontext, keyBuilder.toString(), processedCtx);
                    continue;
                }
                keys.add(keyBuilder.toString());
            }
        }
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return this.getKeysInternal("");
    }

    @Override
    protected Iterator<String> getKeysInternal(String prefix) {
        String[] splitPath = StringUtils.split((String)prefix, (String)".");
        List<String> path = Arrays.asList(splitPath);
        try {
            Context context = this.getContext(path, this.getBaseContext());
            HashSet<String> keys = new HashSet<String>();
            if (context != null) {
                this.recursiveGetKeys(keys, context, prefix, new HashSet<Context>());
            } else if (this.containsKey(prefix)) {
                keys.add(prefix);
            }
            return keys.iterator();
        }
        catch (NameNotFoundException e) {
            return new ArrayList().iterator();
        }
        catch (NamingException e) {
            this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null, e);
            return new ArrayList().iterator();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Context getContext(List<String> path, Context context) throws NamingException {
        if (path == null || path.isEmpty()) {
            return context;
        }
        String key = path.get(0);
        try (NamingEnumeration<NameClassPair> elements = null;){
            elements = context.list("");
            while (elements.hasMore()) {
                NameClassPair nameClassPair = elements.next();
                String name = nameClassPair.getName();
                Object object = context.lookup(name);
                if (!(object instanceof Context) || !name.equals(key)) continue;
                Context subcontext = (Context)object;
                Context context2 = this.getContext(path.subList(1, path.size()), subcontext);
                return context2;
            }
        }
        return null;
    }

    @Override
    protected boolean isEmptyInternal() {
        boolean bl;
        block6: {
            NamingEnumeration<NameClassPair> enumeration = null;
            try {
                enumeration = this.getBaseContext().list("");
                boolean bl2 = bl = !enumeration.hasMore();
                if (enumeration == null) break block6;
            }
            catch (Throwable throwable) {
                try {
                    if (enumeration != null) {
                        enumeration.close();
                    }
                    throw throwable;
                }
                catch (NamingException e) {
                    this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null, e);
                    return true;
                }
            }
            enumeration.close();
        }
        return bl;
    }

    @Override
    protected void setPropertyInternal(String key, Object value) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.clearedProperties.add(key);
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        if (this.clearedProperties.contains(key)) {
            return false;
        }
        key = key.replace('.', '/');
        try {
            this.getBaseContext().lookup(key);
            return true;
        }
        catch (NameNotFoundException e) {
            return false;
        }
        catch (NamingException e) {
            this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null, e);
            return false;
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        this.baseContext = null;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        if (this.clearedProperties.contains(key)) {
            return null;
        }
        try {
            key = key.replace('.', '/');
            return this.getBaseContext().lookup(key);
        }
        catch (NameNotFoundException | NotContextException nctxex) {
            return null;
        }
        catch (NamingException e) {
            this.fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null, e);
            return null;
        }
    }

    @Override
    protected void addPropertyDirect(String key, Object obj) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    public Context getBaseContext() throws NamingException {
        if (this.baseContext == null) {
            this.baseContext = (Context)this.getContext().lookup(this.prefix == null ? "" : this.prefix);
        }
        return this.baseContext;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.clearedProperties.clear();
        this.context = context;
    }
}

