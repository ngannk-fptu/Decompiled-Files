/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.core.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.StringUtils;

public class DefaultDirObjectFactory
implements DirObjectFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDirObjectFactory.class);
    public static final String JNDI_ENV_BASE_PATH_KEY = "org.springframework.ldap.base.path";
    private static final String LDAP_PROTOCOL_PREFIX = "ldap://";
    private static final String LDAPS_PROTOCOL_PREFIX = "ldaps://";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment, Attributes attrs) throws Exception {
        try {
            String nameInNamespace = nameCtx != null ? nameCtx.getNameInNamespace() : "";
            DirContextAdapter dirContextAdapter = this.constructAdapterFromName(attrs, name, nameInNamespace);
            return dirContextAdapter;
        }
        finally {
            if (obj instanceof Context) {
                Context ctx = (Context)obj;
                try {
                    ctx.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    DirContextAdapter constructAdapterFromName(Attributes attrs, Name name, String nameInNamespace) {
        String nameString;
        String referralUrl = "";
        if (name instanceof CompositeName) {
            nameString = LdapUtils.convertCompositeNameToString((CompositeName)name);
        } else {
            LOG.warn("Expecting a CompositeName as input to getObjectInstance but received a '" + name.getClass().toString() + "' - using toString and proceeding with undefined results");
            nameString = name.toString();
        }
        if (nameString.startsWith(LDAP_PROTOCOL_PREFIX) || nameString.startsWith(LDAPS_PROTOCOL_PREFIX)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Received name '" + nameString + "' contains protocol delimiter; indicating a referral.Stripping protocol and address info to enable construction of a proper LdapName");
            }
            try {
                URI url = new URI(nameString);
                String pathString = url.getPath();
                referralUrl = nameString.substring(0, nameString.length() - pathString.length());
                if (StringUtils.hasLength((String)pathString) && pathString.startsWith("/")) {
                    pathString = pathString.substring(1);
                }
                nameString = pathString;
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException("Supplied name starts with protocol prefix indicating a referral, but is not possible to parse to an URI", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resulting name after removal of referral information: '" + nameString + "'");
            }
        }
        DirContextAdapter dirContextAdapter = new DirContextAdapter(attrs, LdapUtils.newLdapName(nameString), LdapUtils.newLdapName(nameInNamespace), referralUrl);
        dirContextAdapter.setUpdateMode(true);
        return dirContextAdapter;
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        return null;
    }
}

