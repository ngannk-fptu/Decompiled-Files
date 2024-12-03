/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiRepositoryFactory
implements RepositoryFactory {
    public static final String JNDI_NAME = "org.apache.jackrabbit.repository.jndi.name";

    @Override
    public Repository getRepository(Map parameters) throws RepositoryException {
        if (parameters == null) {
            return null;
        }
        Hashtable environment = new Hashtable(parameters);
        if (environment.containsKey(JNDI_NAME)) {
            String name = environment.remove(JNDI_NAME).toString();
            return this.getRepository(name, environment);
        }
        if (environment.containsKey("org.apache.jackrabbit.repository.uri")) {
            Object parameter = environment.remove("org.apache.jackrabbit.repository.uri");
            try {
                URI uri = new URI(parameter.toString().trim());
                if ("jndi".equalsIgnoreCase(uri.getScheme())) {
                    return this.getRepository(uri, environment);
                }
                return null;
            }
            catch (URISyntaxException e) {
                return null;
            }
        }
        return null;
    }

    private Repository getRepository(URI uri, Hashtable environment) throws RepositoryException {
        String name;
        if (uri.isOpaque()) {
            name = uri.getSchemeSpecificPart();
        } else {
            name = uri.getPath();
            if (name == null) {
                name = "";
            } else if (name.startsWith("/")) {
                name = name.substring(1);
            }
            String authority = uri.getAuthority();
            if (authority != null && authority.length() > 0) {
                environment = new Hashtable<String, String>(environment);
                environment.put("java.naming.factory.initial", authority);
            }
        }
        return this.getRepository(name, (Hashtable)environment);
    }

    private Repository getRepository(String name, Hashtable environment) throws RepositoryException {
        try {
            Object value = new InitialContext(environment).lookup(name);
            if (value instanceof Repository) {
                return (Repository)value;
            }
            throw new RepositoryException("Invalid repository object " + value + " found at " + name + " in JNDI environment " + environment);
        }
        catch (NamingException e) {
            throw new RepositoryException("Failed to look up " + name + " from JNDI environment " + environment, e);
        }
    }
}

