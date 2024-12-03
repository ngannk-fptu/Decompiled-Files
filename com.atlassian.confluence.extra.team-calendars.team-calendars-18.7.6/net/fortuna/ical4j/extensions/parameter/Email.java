/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 */
package net.fortuna.ical4j.extensions.parameter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

public class Email
extends Parameter {
    private static final long serialVersionUID = 1L;
    private static final String PARAMETER_NAME = "EMAIL";
    private final InternetAddress address;

    public Email(String address) throws AddressException {
        super(PARAMETER_NAME, new Factory());
        this.address = InternetAddress.parse((String)address)[0];
    }

    @Override
    public String getValue() {
        return this.address.getAddress();
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(Email.PARAMETER_NAME);
        }

        public Parameter createParameter(String value) {
            try {
                return new Email(value);
            }
            catch (AddressException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}

