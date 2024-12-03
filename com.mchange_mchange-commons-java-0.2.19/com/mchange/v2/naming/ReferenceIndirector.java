/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.naming;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.naming.ReferenceableUtils;
import com.mchange.v2.ser.IndirectlySerialized;
import com.mchange.v2.ser.Indirector;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

public class ReferenceIndirector
implements Indirector {
    static final MLogger logger = MLog.getLogger(ReferenceIndirector.class);
    Name name;
    Name contextName;
    Hashtable environmentProperties;

    public Name getName() {
        return this.name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Name getNameContextName() {
        return this.contextName;
    }

    public void setNameContextName(Name name) {
        this.contextName = name;
    }

    public Hashtable getEnvironmentProperties() {
        return this.environmentProperties;
    }

    public void setEnvironmentProperties(Hashtable hashtable) {
        this.environmentProperties = hashtable;
    }

    @Override
    public IndirectlySerialized indirectForm(Object object) throws Exception {
        Reference reference = ((Referenceable)object).getReference();
        return new ReferenceSerialized(reference, this.name, this.contextName, this.environmentProperties);
    }

    private static class ReferenceSerialized
    implements IndirectlySerialized {
        Reference reference;
        Name name;
        Name contextName;
        Hashtable env;

        ReferenceSerialized(Reference reference, Name name, Name name2, Hashtable hashtable) {
            this.reference = reference;
            this.name = name;
            this.contextName = name2;
            this.env = hashtable;
        }

        @Override
        public Object getObject() throws ClassNotFoundException, IOException {
            try {
                InitialContext initialContext = this.env == null ? new InitialContext() : new InitialContext(this.env);
                Context context = null;
                if (this.contextName != null) {
                    context = (Context)initialContext.lookup(this.contextName);
                }
                return ReferenceableUtils.referenceToObject(this.reference, this.name, context, this.env);
            }
            catch (NamingException namingException) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.log(MLevel.WARNING, "Failed to acquire the Context necessary to lookup an Object.", namingException);
                }
                throw new InvalidObjectException("Failed to acquire the Context necessary to lookup an Object: " + namingException.toString());
            }
        }
    }
}

