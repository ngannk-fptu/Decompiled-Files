/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.support;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.AttributeInUseException;
import org.springframework.ldap.AttributeModificationException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.AuthenticationNotSupportedException;
import org.springframework.ldap.CannotProceedException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.ConfigurationException;
import org.springframework.ldap.ContextNotEmptyException;
import org.springframework.ldap.InsufficientResourcesException;
import org.springframework.ldap.InterruptedNamingException;
import org.springframework.ldap.InvalidAttributeIdentifierException;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.ldap.InvalidAttributesException;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.InvalidSearchControlsException;
import org.springframework.ldap.InvalidSearchFilterException;
import org.springframework.ldap.LdapReferralException;
import org.springframework.ldap.LimitExceededException;
import org.springframework.ldap.LinkException;
import org.springframework.ldap.LinkLoopException;
import org.springframework.ldap.MalformedLinkException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.NamingSecurityException;
import org.springframework.ldap.NoInitialContextException;
import org.springframework.ldap.NoPermissionException;
import org.springframework.ldap.NoSuchAttributeException;
import org.springframework.ldap.NotContextException;
import org.springframework.ldap.OperationNotSupportedException;
import org.springframework.ldap.PartialResultException;
import org.springframework.ldap.ReferralException;
import org.springframework.ldap.SchemaViolationException;
import org.springframework.ldap.ServiceUnavailableException;
import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.TimeLimitExceededException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.support.AttributeValueCallbackHandler;
import org.springframework.util.Assert;

public final class LdapUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUtils.class);
    private static final int HEX = 16;

    private LdapUtils() {
    }

    public static void closeContext(DirContext context) {
        if (context != null) {
            try {
                context.close();
            }
            catch (NamingException ex) {
                LOGGER.debug("Could not close JNDI DirContext", (Throwable)((Object)ex));
            }
            catch (Throwable ex) {
                LOGGER.debug("Unexpected exception on closing JNDI DirContext", ex);
            }
        }
    }

    public static NamingException convertLdapException(javax.naming.NamingException ex) {
        Assert.notNull((Object)ex, (String)"NamingException must not be null");
        if (javax.naming.directory.AttributeInUseException.class.isAssignableFrom(ex.getClass())) {
            return new AttributeInUseException((javax.naming.directory.AttributeInUseException)ex);
        }
        if (javax.naming.directory.AttributeModificationException.class.isAssignableFrom(ex.getClass())) {
            return new AttributeModificationException((javax.naming.directory.AttributeModificationException)ex);
        }
        if (javax.naming.CannotProceedException.class.isAssignableFrom(ex.getClass())) {
            return new CannotProceedException((javax.naming.CannotProceedException)ex);
        }
        if (javax.naming.CommunicationException.class.isAssignableFrom(ex.getClass())) {
            return new CommunicationException((javax.naming.CommunicationException)ex);
        }
        if (javax.naming.ConfigurationException.class.isAssignableFrom(ex.getClass())) {
            return new ConfigurationException((javax.naming.ConfigurationException)ex);
        }
        if (javax.naming.ContextNotEmptyException.class.isAssignableFrom(ex.getClass())) {
            return new ContextNotEmptyException((javax.naming.ContextNotEmptyException)ex);
        }
        if (javax.naming.InsufficientResourcesException.class.isAssignableFrom(ex.getClass())) {
            return new InsufficientResourcesException((javax.naming.InsufficientResourcesException)ex);
        }
        if (javax.naming.InterruptedNamingException.class.isAssignableFrom(ex.getClass())) {
            return new InterruptedNamingException((javax.naming.InterruptedNamingException)ex);
        }
        if (javax.naming.directory.InvalidAttributeIdentifierException.class.isAssignableFrom(ex.getClass())) {
            return new InvalidAttributeIdentifierException((javax.naming.directory.InvalidAttributeIdentifierException)ex);
        }
        if (javax.naming.directory.InvalidAttributesException.class.isAssignableFrom(ex.getClass())) {
            return new InvalidAttributesException((javax.naming.directory.InvalidAttributesException)ex);
        }
        if (javax.naming.directory.InvalidAttributeValueException.class.isAssignableFrom(ex.getClass())) {
            return new InvalidAttributeValueException((javax.naming.directory.InvalidAttributeValueException)ex);
        }
        if (javax.naming.InvalidNameException.class.isAssignableFrom(ex.getClass())) {
            return new InvalidNameException((javax.naming.InvalidNameException)ex);
        }
        if (javax.naming.directory.InvalidSearchControlsException.class.isAssignableFrom(ex.getClass())) {
            return new InvalidSearchControlsException((javax.naming.directory.InvalidSearchControlsException)ex);
        }
        if (javax.naming.directory.InvalidSearchFilterException.class.isAssignableFrom(ex.getClass())) {
            return new InvalidSearchFilterException((javax.naming.directory.InvalidSearchFilterException)ex);
        }
        if (javax.naming.ldap.LdapReferralException.class.isAssignableFrom(ex.getClass())) {
            return new LdapReferralException((javax.naming.ldap.LdapReferralException)ex);
        }
        if (javax.naming.ReferralException.class.isAssignableFrom(ex.getClass())) {
            return new ReferralException((javax.naming.ReferralException)ex);
        }
        if (javax.naming.SizeLimitExceededException.class.isAssignableFrom(ex.getClass())) {
            return new SizeLimitExceededException((javax.naming.SizeLimitExceededException)ex);
        }
        if (javax.naming.TimeLimitExceededException.class.isAssignableFrom(ex.getClass())) {
            return new TimeLimitExceededException((javax.naming.TimeLimitExceededException)ex);
        }
        if (javax.naming.LimitExceededException.class.isAssignableFrom(ex.getClass())) {
            return new LimitExceededException((javax.naming.LimitExceededException)ex);
        }
        if (javax.naming.LinkLoopException.class.isAssignableFrom(ex.getClass())) {
            return new LinkLoopException((javax.naming.LinkLoopException)ex);
        }
        if (javax.naming.MalformedLinkException.class.isAssignableFrom(ex.getClass())) {
            return new MalformedLinkException((javax.naming.MalformedLinkException)ex);
        }
        if (javax.naming.LinkException.class.isAssignableFrom(ex.getClass())) {
            return new LinkException((javax.naming.LinkException)ex);
        }
        if (javax.naming.NameAlreadyBoundException.class.isAssignableFrom(ex.getClass())) {
            return new NameAlreadyBoundException((javax.naming.NameAlreadyBoundException)ex);
        }
        if (javax.naming.NameNotFoundException.class.isAssignableFrom(ex.getClass())) {
            return new NameNotFoundException((javax.naming.NameNotFoundException)ex);
        }
        if (javax.naming.NoPermissionException.class.isAssignableFrom(ex.getClass())) {
            return new NoPermissionException((javax.naming.NoPermissionException)ex);
        }
        if (javax.naming.AuthenticationException.class.isAssignableFrom(ex.getClass())) {
            return new AuthenticationException((javax.naming.AuthenticationException)ex);
        }
        if (javax.naming.AuthenticationNotSupportedException.class.isAssignableFrom(ex.getClass())) {
            return new AuthenticationNotSupportedException((javax.naming.AuthenticationNotSupportedException)ex);
        }
        if (javax.naming.NamingSecurityException.class.isAssignableFrom(ex.getClass())) {
            return new NamingSecurityException((javax.naming.NamingSecurityException)ex);
        }
        if (javax.naming.NoInitialContextException.class.isAssignableFrom(ex.getClass())) {
            return new NoInitialContextException((javax.naming.NoInitialContextException)ex);
        }
        if (javax.naming.directory.NoSuchAttributeException.class.isAssignableFrom(ex.getClass())) {
            return new NoSuchAttributeException((javax.naming.directory.NoSuchAttributeException)ex);
        }
        if (javax.naming.NotContextException.class.isAssignableFrom(ex.getClass())) {
            return new NotContextException((javax.naming.NotContextException)ex);
        }
        if (javax.naming.OperationNotSupportedException.class.isAssignableFrom(ex.getClass())) {
            return new OperationNotSupportedException((javax.naming.OperationNotSupportedException)ex);
        }
        if (javax.naming.PartialResultException.class.isAssignableFrom(ex.getClass())) {
            return new PartialResultException((javax.naming.PartialResultException)ex);
        }
        if (javax.naming.directory.SchemaViolationException.class.isAssignableFrom(ex.getClass())) {
            return new SchemaViolationException((javax.naming.directory.SchemaViolationException)ex);
        }
        if (javax.naming.ServiceUnavailableException.class.isAssignableFrom(ex.getClass())) {
            return new ServiceUnavailableException((javax.naming.ServiceUnavailableException)ex);
        }
        return new UncategorizedLdapException(ex);
    }

    public static Class getActualTargetClass(DirContext context) {
        if (context instanceof LdapContext) {
            return LdapContext.class;
        }
        return DirContext.class;
    }

    public static void collectAttributeValues(Attributes attributes, String name, Collection<Object> collection) {
        LdapUtils.collectAttributeValues(attributes, name, collection, Object.class);
    }

    public static <T> void collectAttributeValues(Attributes attributes, String name, Collection<T> collection, Class<T> clazz) {
        Assert.notNull((Object)attributes, (String)"Attributes must not be null");
        Assert.hasText((String)name, (String)"Name must not be empty");
        Assert.notNull(collection, (String)"Collection must not be null");
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            throw new NoSuchAttributeException("No attribute with name '" + name + "'");
        }
        LdapUtils.iterateAttributeValues(attribute, new CollectingAttributeValueCallbackHandler<T>(collection, clazz));
    }

    public static void iterateAttributeValues(Attribute attribute, AttributeValueCallbackHandler callbackHandler) {
        Assert.notNull((Object)attribute, (String)"Attribute must not be null");
        Assert.notNull((Object)callbackHandler, (String)"callbackHandler must not be null");
        if (attribute instanceof Iterable) {
            int i = 0;
            for (Object obj : (Iterable)((Object)attribute)) {
                LdapUtils.handleAttributeValue(attribute.getID(), obj, i, callbackHandler);
                ++i;
            }
        } else {
            for (int i = 0; i < attribute.size(); ++i) {
                try {
                    LdapUtils.handleAttributeValue(attribute.getID(), attribute.get(i), i, callbackHandler);
                    continue;
                }
                catch (javax.naming.NamingException e) {
                    throw LdapUtils.convertLdapException(e);
                }
            }
        }
    }

    private static void handleAttributeValue(String attributeID, Object value, int i, AttributeValueCallbackHandler callbackHandler) {
        callbackHandler.handleAttributeValue(attributeID, value, i);
    }

    public static String convertCompositeNameToString(CompositeName compositeName) {
        if (compositeName.size() > 0) {
            return compositeName.get(0);
        }
        return "";
    }

    public static LdapName newLdapName(Name name) {
        Assert.notNull((Object)name, (String)"name must not be null");
        if (name instanceof LdapName) {
            return (LdapName)name.clone();
        }
        if (name instanceof CompositeName) {
            CompositeName compositeName = (CompositeName)name;
            try {
                return new LdapName(LdapUtils.convertCompositeNameToString(compositeName));
            }
            catch (javax.naming.InvalidNameException e) {
                throw LdapUtils.convertLdapException(e);
            }
        }
        LdapName result = LdapUtils.emptyLdapName();
        try {
            result.addAll(0, name);
        }
        catch (javax.naming.InvalidNameException e) {
            throw LdapUtils.convertLdapException(e);
        }
        return result;
    }

    public static LdapName newLdapName(String distinguishedName) {
        Assert.notNull((Object)distinguishedName, (String)"distinguishedName must not be null");
        try {
            return new LdapName(distinguishedName);
        }
        catch (javax.naming.InvalidNameException e) {
            throw LdapUtils.convertLdapException(e);
        }
    }

    private static LdapName returnOrConstructLdapNameFromName(Name name) {
        if (name instanceof LdapName) {
            return (LdapName)name;
        }
        return LdapUtils.newLdapName(name);
    }

    public static LdapName removeFirst(Name dn, Name pathToRemove) {
        Assert.notNull((Object)dn, (String)"dn must not be null");
        Assert.notNull((Object)pathToRemove, (String)"pathToRemove must not be null");
        LdapName result = LdapUtils.newLdapName(dn);
        LdapName path = LdapUtils.returnOrConstructLdapNameFromName(pathToRemove);
        if (path.size() == 0 || !dn.startsWith(path)) {
            return result;
        }
        for (int i = 0; i < path.size(); ++i) {
            try {
                result.remove(0);
                continue;
            }
            catch (javax.naming.InvalidNameException e) {
                throw LdapUtils.convertLdapException(e);
            }
        }
        return result;
    }

    public static LdapName prepend(Name dn, Name pathToPrepend) {
        Assert.notNull((Object)dn, (String)"dn must not be null");
        Assert.notNull((Object)pathToPrepend, (String)"pathToRemove must not be null");
        LdapName result = LdapUtils.newLdapName(dn);
        try {
            result.addAll(0, pathToPrepend);
        }
        catch (javax.naming.InvalidNameException e) {
            throw LdapUtils.convertLdapException(e);
        }
        return result;
    }

    public static LdapName emptyLdapName() {
        return LdapUtils.newLdapName("");
    }

    public static Rdn getRdn(Name name, String key) {
        Assert.notNull((Object)name, (String)"name must not be null");
        Assert.hasText((String)key, (String)"key must not be blank");
        LdapName ldapName = LdapUtils.returnOrConstructLdapNameFromName(name);
        List<Rdn> rdns = ldapName.getRdns();
        for (Rdn rdn : rdns) {
            NamingEnumeration<String> ids = rdn.toAttributes().getIDs();
            while (ids.hasMoreElements()) {
                String id = (String)ids.nextElement();
                if (!key.equalsIgnoreCase(id)) continue;
                return rdn;
            }
        }
        throw new NoSuchElementException("No Rdn with the requested key: '" + key + "'");
    }

    public static Object getValue(Name name, String key) {
        NamingEnumeration<? extends Attribute> allAttributes = LdapUtils.getRdn(name, key).toAttributes().getAll();
        while (allAttributes.hasMoreElements()) {
            Attribute oneAttribute = (Attribute)allAttributes.nextElement();
            if (!key.equalsIgnoreCase(oneAttribute.getID())) continue;
            try {
                return oneAttribute.get();
            }
            catch (javax.naming.NamingException e) {
                throw LdapUtils.convertLdapException(e);
            }
        }
        throw new NoSuchElementException("No Rdn with the requested key: '" + key + "'");
    }

    public static Object getValue(Name name, int index) {
        Assert.notNull((Object)name, (String)"name must not be null");
        LdapName ldapName = LdapUtils.returnOrConstructLdapNameFromName(name);
        Rdn rdn = ldapName.getRdn(index);
        if (rdn.size() > 1) {
            LOGGER.warn("Rdn at position " + index + " of dn '" + name + "' is multi-value - returned value is not to be trusted. Consider using name-based getValue method instead");
        }
        return rdn.getValue();
    }

    public static String getStringValue(Name name, int index) {
        return (String)LdapUtils.getValue(name, index);
    }

    public static String getStringValue(Name name, String key) {
        return (String)LdapUtils.getValue(name, key);
    }

    public static String convertBinarySidToString(byte[] sid) {
        StringBuffer sidAsString = new StringBuffer("S-");
        sidAsString.append(sid[0]).append('-');
        StringBuffer sb = new StringBuffer();
        for (int t = 2; t <= 7; ++t) {
            String hexString = Integer.toHexString(sid[t] & 0xFF);
            sb.append(hexString);
        }
        sidAsString.append(Long.parseLong(sb.toString(), 16));
        int count = sid[1];
        for (int i = 0; i < count; ++i) {
            int currSubAuthOffset = i * 4;
            sb.setLength(0);
            sb.append(LdapUtils.toHexString((byte)(sid[11 + currSubAuthOffset] & 0xFF)));
            sb.append(LdapUtils.toHexString((byte)(sid[10 + currSubAuthOffset] & 0xFF)));
            sb.append(LdapUtils.toHexString((byte)(sid[9 + currSubAuthOffset] & 0xFF)));
            sb.append(LdapUtils.toHexString((byte)(sid[8 + currSubAuthOffset] & 0xFF)));
            sidAsString.append('-').append(Long.parseLong(sb.toString(), 16));
        }
        return sidAsString.toString();
    }

    public static byte[] convertStringSidToBinary(String string) {
        String[] parts = string.split("-");
        byte sidRevision = (byte)Integer.parseInt(parts[1]);
        int subAuthCount = parts.length - 3;
        byte[] sid = new byte[]{sidRevision, (byte)subAuthCount};
        sid = LdapUtils.addAll(sid, LdapUtils.numberToBytes(parts[2], 6, true));
        for (int i = 0; i < subAuthCount; ++i) {
            sid = LdapUtils.addAll(sid, LdapUtils.numberToBytes(parts[3 + i], 4, false));
        }
        return sid;
    }

    private static byte[] addAll(byte[] array1, byte[] array2) {
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    static byte[] numberToBytes(String number, int length, boolean bigEndian) {
        BigInteger bi = new BigInteger(number);
        byte[] bytes = bi.toByteArray();
        int remaining = length - bytes.length;
        if (remaining < 0) {
            bytes = Arrays.copyOfRange(bytes, -remaining, bytes.length);
        } else {
            byte[] fill = new byte[remaining];
            bytes = LdapUtils.addAll(fill, bytes);
        }
        if (!bigEndian) {
            LdapUtils.reverse(bytes);
        }
        return bytes;
    }

    private static void reverse(byte[] array) {
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            byte tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    static String toHexString(byte b) {
        String hexString = Integer.toHexString(b & 0xFF);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        return hexString;
    }

    static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer("{");
        for (int i = 0; i < b.length; ++i) {
            sb.append(LdapUtils.toHexString(b[i]));
            if (i >= b.length - 1) continue;
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    private static final class CollectingAttributeValueCallbackHandler<T>
    implements AttributeValueCallbackHandler {
        private final Collection<T> collection;
        private final Class<T> clazz;

        public CollectingAttributeValueCallbackHandler(Collection<T> collection, Class<T> clazz) {
            Assert.notNull(collection, (String)"Collection must not be null");
            Assert.notNull(clazz, (String)"Clazz parameter must not be null");
            this.collection = collection;
            this.clazz = clazz;
        }

        @Override
        public void handleAttributeValue(String attributeName, Object attributeValue, int index) {
            Assert.isTrue((attributeName == null || this.clazz.isAssignableFrom(attributeValue.getClass()) ? 1 : 0) != 0);
            this.collection.add(this.clazz.cast(attributeValue));
        }
    }
}

