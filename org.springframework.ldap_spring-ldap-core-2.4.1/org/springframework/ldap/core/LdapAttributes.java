/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.springframework.ldap.core;

import java.net.URI;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.ldap.support.LdapUtils;

public class LdapAttributes
extends BasicAttributes {
    private static final long serialVersionUID = 97903297123869138L;
    private static Logger log = LoggerFactory.getLogger(LdapAttributes.class);
    private static final String SAFE_CHAR = "[\\p{ASCII}&&[^\\x00\\x0A\\x0D]]";
    private static final String SAFE_INIT_CHAR = "[\\p{ASCII}&&[^ \\x00\\x0A\\x0D\\x3A\\x3C]]";
    protected LdapName dn = LdapUtils.emptyLdapName();

    public LdapAttributes() {
    }

    public LdapAttributes(boolean ignoreCase) {
        super(ignoreCase);
    }

    public DistinguishedName getDN() {
        return new DistinguishedName(this.dn);
    }

    public LdapName getName() {
        return LdapUtils.newLdapName(this.dn);
    }

    public void setDN(DistinguishedName dn) {
        this.dn = LdapUtils.newLdapName(dn);
    }

    public void setName(Name name) {
        this.dn = LdapUtils.newLdapName(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            LdapName dn = this.getName();
            if (!dn.toString().matches("[\\p{ASCII}&&[^ \\x00\\x0A\\x0D\\x3A\\x3C]][\\p{ASCII}&&[^\\x00\\x0A\\x0D]]*")) {
                sb.append("dn:: " + LdapEncoder.printBase64Binary(dn.toString().getBytes()) + "\n");
            } else {
                sb.append("dn: " + this.getDN() + "\n");
            }
            NamingEnumeration<Attribute> attributes = this.getAll();
            while (attributes.hasMore()) {
                Attribute attribute = attributes.next();
                NamingEnumeration<?> values = attribute.getAll();
                while (values.hasMore()) {
                    Object value = values.next();
                    if (value instanceof String) {
                        sb.append(attribute.getID() + ": " + (String)value + "\n");
                        continue;
                    }
                    if (value instanceof byte[]) {
                        sb.append(attribute.getID() + ":: " + LdapEncoder.printBase64Binary((byte[])value) + "\n");
                        continue;
                    }
                    if (value instanceof URI) {
                        sb.append(attribute.getID() + ":< " + (URI)value + "\n");
                        continue;
                    }
                    sb.append(attribute.getID() + ": " + value + "\n");
                }
            }
        }
        catch (NamingException e) {
            log.error("Error formating attributes for output.", (Throwable)e);
            sb = new StringBuilder();
        }
        return sb.toString();
    }
}

