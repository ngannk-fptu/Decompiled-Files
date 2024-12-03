/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMappingDelegate;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.Base64DeserializerFactory;
import org.apache.axis.encoding.ser.Base64SerializerFactory;

public class DefaultSOAPEncodingTypeMappingImpl
extends DefaultTypeMappingImpl {
    private static DefaultSOAPEncodingTypeMappingImpl tm = null;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$java$lang$Double;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$math$BigInteger;
    static /* synthetic */ Class class$java$math$BigDecimal;
    static /* synthetic */ Class class$java$lang$Long;
    static /* synthetic */ Class class$java$lang$Short;
    static /* synthetic */ Class class$java$lang$Byte;
    static /* synthetic */ Class array$B;
    static /* synthetic */ Class class$java$util$Collection;
    static /* synthetic */ Class class$java$util$ArrayList;
    static /* synthetic */ Class array$Ljava$lang$Object;

    public static synchronized TypeMappingImpl getSingleton() {
        if (tm == null) {
            tm = new DefaultSOAPEncodingTypeMappingImpl();
        }
        return tm;
    }

    public static TypeMappingDelegate createWithDelegate() {
        TypeMappingDelegate ret = new TypeMappingDelegate(new DefaultSOAPEncodingTypeMappingImpl());
        MessageContext mc = MessageContext.getCurrentContext();
        TypeMappingDelegate tm = null;
        tm = mc != null ? (TypeMappingDelegate)mc.getTypeMappingRegistry().getDefaultTypeMapping() : DefaultTypeMappingImpl.getSingletonDelegate();
        ret.setNext(tm);
        return ret;
    }

    protected DefaultSOAPEncodingTypeMappingImpl() {
        super(true);
        this.registerSOAPTypes();
    }

    private void registerSOAPTypes() {
        this.myRegisterSimple(Constants.SOAP_STRING, class$java$lang$String == null ? (class$java$lang$String = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.String")) : class$java$lang$String);
        this.myRegisterSimple(Constants.SOAP_BOOLEAN, class$java$lang$Boolean == null ? (class$java$lang$Boolean = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.Boolean")) : class$java$lang$Boolean);
        this.myRegisterSimple(Constants.SOAP_DOUBLE, class$java$lang$Double == null ? (class$java$lang$Double = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.Double")) : class$java$lang$Double);
        this.myRegisterSimple(Constants.SOAP_FLOAT, class$java$lang$Float == null ? (class$java$lang$Float = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.Float")) : class$java$lang$Float);
        this.myRegisterSimple(Constants.SOAP_INT, class$java$lang$Integer == null ? (class$java$lang$Integer = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.Integer")) : class$java$lang$Integer);
        this.myRegisterSimple(Constants.SOAP_INTEGER, class$java$math$BigInteger == null ? (class$java$math$BigInteger = DefaultSOAPEncodingTypeMappingImpl.class$("java.math.BigInteger")) : class$java$math$BigInteger);
        this.myRegisterSimple(Constants.SOAP_DECIMAL, class$java$math$BigDecimal == null ? (class$java$math$BigDecimal = DefaultSOAPEncodingTypeMappingImpl.class$("java.math.BigDecimal")) : class$java$math$BigDecimal);
        this.myRegisterSimple(Constants.SOAP_LONG, class$java$lang$Long == null ? (class$java$lang$Long = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.Long")) : class$java$lang$Long);
        this.myRegisterSimple(Constants.SOAP_SHORT, class$java$lang$Short == null ? (class$java$lang$Short = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.Short")) : class$java$lang$Short);
        this.myRegisterSimple(Constants.SOAP_BYTE, class$java$lang$Byte == null ? (class$java$lang$Byte = DefaultSOAPEncodingTypeMappingImpl.class$("java.lang.Byte")) : class$java$lang$Byte);
        this.myRegister(Constants.SOAP_BASE64, array$B == null ? (array$B = DefaultSOAPEncodingTypeMappingImpl.class$("[B")) : array$B, new Base64SerializerFactory(array$B == null ? (array$B = DefaultSOAPEncodingTypeMappingImpl.class$("[B")) : array$B, Constants.SOAP_BASE64), new Base64DeserializerFactory(array$B == null ? (array$B = DefaultSOAPEncodingTypeMappingImpl.class$("[B")) : array$B, Constants.SOAP_BASE64));
        this.myRegister(Constants.SOAP_BASE64BINARY, array$B == null ? (array$B = DefaultSOAPEncodingTypeMappingImpl.class$("[B")) : array$B, new Base64SerializerFactory(array$B == null ? (array$B = DefaultSOAPEncodingTypeMappingImpl.class$("[B")) : array$B, Constants.SOAP_BASE64BINARY), new Base64DeserializerFactory(array$B == null ? (array$B = DefaultSOAPEncodingTypeMappingImpl.class$("[B")) : array$B, Constants.SOAP_BASE64BINARY));
        this.myRegister(Constants.SOAP_ARRAY12, class$java$util$Collection == null ? (class$java$util$Collection = DefaultSOAPEncodingTypeMappingImpl.class$("java.util.Collection")) : class$java$util$Collection, new ArraySerializerFactory(), new ArrayDeserializerFactory());
        this.myRegister(Constants.SOAP_ARRAY12, class$java$util$ArrayList == null ? (class$java$util$ArrayList = DefaultSOAPEncodingTypeMappingImpl.class$("java.util.ArrayList")) : class$java$util$ArrayList, new ArraySerializerFactory(), new ArrayDeserializerFactory());
        this.myRegister(Constants.SOAP_ARRAY12, array$Ljava$lang$Object == null ? (array$Ljava$lang$Object = DefaultSOAPEncodingTypeMappingImpl.class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, new ArraySerializerFactory(), new ArrayDeserializerFactory());
        this.myRegister(Constants.SOAP_ARRAY, class$java$util$ArrayList == null ? (class$java$util$ArrayList = DefaultSOAPEncodingTypeMappingImpl.class$("java.util.ArrayList")) : class$java$util$ArrayList, new ArraySerializerFactory(), new ArrayDeserializerFactory());
        this.myRegister(Constants.SOAP_ARRAY, class$java$util$Collection == null ? (class$java$util$Collection = DefaultSOAPEncodingTypeMappingImpl.class$("java.util.Collection")) : class$java$util$Collection, new ArraySerializerFactory(), new ArrayDeserializerFactory());
        this.myRegister(Constants.SOAP_ARRAY, array$Ljava$lang$Object == null ? (array$Ljava$lang$Object = DefaultSOAPEncodingTypeMappingImpl.class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, new ArraySerializerFactory(), new ArrayDeserializerFactory());
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

