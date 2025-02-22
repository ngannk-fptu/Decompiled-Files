/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason$CloseCode
 *  javax.websocket.CloseReason$CloseCodes
 *  javax.websocket.Decoder
 *  javax.websocket.Decoder$Binary
 *  javax.websocket.Decoder$BinaryStream
 *  javax.websocket.Decoder$Text
 *  javax.websocket.Decoder$TextStream
 *  javax.websocket.DeploymentException
 *  javax.websocket.Encoder
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Extension
 *  javax.websocket.MessageHandler
 *  javax.websocket.MessageHandler$Whole
 *  javax.websocket.PongMessage
 *  javax.websocket.Session
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.naming.NamingException;
import javax.websocket.CloseReason;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.DecoderEntry;
import org.apache.tomcat.websocket.MessageHandlerResult;
import org.apache.tomcat.websocket.MessageHandlerResultType;
import org.apache.tomcat.websocket.WsExtension;
import org.apache.tomcat.websocket.WsExtensionParameter;
import org.apache.tomcat.websocket.WsSession;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerPartialBinary;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBinary;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeText;

public class Util {
    private static final StringManager sm = StringManager.getManager(Util.class);
    private static final Queue<SecureRandom> randoms = new ConcurrentLinkedQueue<SecureRandom>();

    private Util() {
    }

    static boolean isControl(byte opCode) {
        return (opCode & 8) != 0;
    }

    static boolean isText(byte opCode) {
        return opCode == 1;
    }

    static boolean isContinuation(byte opCode) {
        return opCode == 0;
    }

    static CloseReason.CloseCode getCloseCode(int code) {
        if (code > 2999 && code < 5000) {
            return CloseReason.CloseCodes.getCloseCode((int)code);
        }
        switch (code) {
            case 1000: {
                return CloseReason.CloseCodes.NORMAL_CLOSURE;
            }
            case 1001: {
                return CloseReason.CloseCodes.GOING_AWAY;
            }
            case 1002: {
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            }
            case 1003: {
                return CloseReason.CloseCodes.CANNOT_ACCEPT;
            }
            case 1004: {
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            }
            case 1005: {
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            }
            case 1006: {
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            }
            case 1007: {
                return CloseReason.CloseCodes.NOT_CONSISTENT;
            }
            case 1008: {
                return CloseReason.CloseCodes.VIOLATED_POLICY;
            }
            case 1009: {
                return CloseReason.CloseCodes.TOO_BIG;
            }
            case 1010: {
                return CloseReason.CloseCodes.NO_EXTENSION;
            }
            case 1011: {
                return CloseReason.CloseCodes.UNEXPECTED_CONDITION;
            }
            case 1012: {
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            }
            case 1013: {
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            }
            case 1015: {
                return CloseReason.CloseCodes.PROTOCOL_ERROR;
            }
        }
        return CloseReason.CloseCodes.PROTOCOL_ERROR;
    }

    static byte[] generateMask() {
        SecureRandom sr = randoms.poll();
        if (sr == null) {
            try {
                sr = SecureRandom.getInstance("SHA1PRNG");
            }
            catch (NoSuchAlgorithmException e) {
                sr = new SecureRandom();
            }
        }
        byte[] result = new byte[4];
        sr.nextBytes(result);
        randoms.add(sr);
        return result;
    }

    static Class<?> getMessageType(MessageHandler listener) {
        return Util.getGenericType(MessageHandler.class, listener.getClass()).getClazz();
    }

    private static Class<?> getDecoderType(Class<? extends Decoder> decoder) {
        return Util.getGenericType(Decoder.class, decoder).getClazz();
    }

    static Class<?> getEncoderType(Class<? extends Encoder> encoder) {
        return Util.getGenericType(Encoder.class, encoder).getClazz();
    }

    private static <T> TypeResult getGenericType(Class<T> type, Class<? extends T> clazz) {
        Type[] interfaces;
        for (Type iface : interfaces = clazz.getGenericInterfaces()) {
            ParameterizedType pi;
            if (!(iface instanceof ParameterizedType) || !((pi = (ParameterizedType)iface).getRawType() instanceof Class) || !type.isAssignableFrom((Class)pi.getRawType())) continue;
            return Util.getTypeParameter(clazz, pi.getActualTypeArguments()[0]);
        }
        Class<? extends T> superClazz = clazz.getSuperclass();
        if (superClazz == null) {
            return null;
        }
        TypeResult superClassTypeResult = Util.getGenericType(type, superClazz);
        int dimension = superClassTypeResult.getDimension();
        if (superClassTypeResult.getIndex() == -1 && dimension == 0) {
            return superClassTypeResult;
        }
        if (superClassTypeResult.getIndex() > -1) {
            ParameterizedType superClassType = (ParameterizedType)clazz.getGenericSuperclass();
            TypeResult result = Util.getTypeParameter(clazz, superClassType.getActualTypeArguments()[superClassTypeResult.getIndex()]);
            result.incrementDimension(superClassTypeResult.getDimension());
            if (result.getClazz() != null && result.getDimension() > 0) {
                superClassTypeResult = result;
            } else {
                return result;
            }
        }
        if (superClassTypeResult.getDimension() > 0) {
            Class<?> arrayClazz;
            StringBuilder className = new StringBuilder();
            for (int i = 0; i < dimension; ++i) {
                className.append('[');
            }
            className.append('L');
            className.append(superClassTypeResult.getClazz().getCanonicalName());
            className.append(';');
            try {
                arrayClazz = Class.forName(className.toString());
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
            return new TypeResult(arrayClazz, -1, 0);
        }
        return null;
    }

    private static TypeResult getTypeParameter(Class<?> clazz, Type argType) {
        if (argType instanceof Class) {
            return new TypeResult((Class)argType, -1, 0);
        }
        if (argType instanceof ParameterizedType) {
            return new TypeResult((Class)((ParameterizedType)argType).getRawType(), -1, 0);
        }
        if (argType instanceof GenericArrayType) {
            Type arrayElementType = ((GenericArrayType)argType).getGenericComponentType();
            TypeResult result = Util.getTypeParameter(clazz, arrayElementType);
            result.incrementDimension(1);
            return result;
        }
        TypeVariable<Class<?>>[] tvs = clazz.getTypeParameters();
        for (int i = 0; i < tvs.length; ++i) {
            if (!tvs[i].equals(argType)) continue;
            return new TypeResult(null, i, 0);
        }
        return null;
    }

    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        return clazz.equals(Boolean.class) || clazz.equals(Byte.class) || clazz.equals(Character.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Short.class);
    }

    public static Object coerceToType(Class<?> type, String value) {
        if (type.equals(String.class)) {
            return value;
        }
        if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return Boolean.valueOf(value);
        }
        if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
            return Byte.valueOf(value);
        }
        if (type.equals(Character.TYPE) || type.equals(Character.class)) {
            return Character.valueOf(value.charAt(0));
        }
        if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return Double.valueOf(value);
        }
        if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return Float.valueOf(value);
        }
        if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return Integer.valueOf(value);
        }
        if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return Long.valueOf(value);
        }
        if (type.equals(Short.TYPE) || type.equals(Short.class)) {
            return Short.valueOf(value);
        }
        throw new IllegalArgumentException(sm.getString("util.invalidType", new Object[]{value, type.getName()}));
    }

    @Deprecated
    public static List<DecoderEntry> getDecoders(List<Class<? extends Decoder>> decoderClazzes) throws DeploymentException {
        return Util.getDecoders(decoderClazzes, null);
    }

    public static List<DecoderEntry> getDecoders(List<Class<? extends Decoder>> decoderClazzes, InstanceManager instanceManager) throws DeploymentException {
        ArrayList<DecoderEntry> result = new ArrayList<DecoderEntry>();
        if (decoderClazzes != null) {
            for (Class<? extends Decoder> decoderClazz : decoderClazzes) {
                try {
                    Decoder instance;
                    if (instanceManager == null) {
                        instance = decoderClazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    } else {
                        instance = (Decoder)instanceManager.newInstance(decoderClazz);
                        instanceManager.destroyInstance((Object)instance);
                    }
                }
                catch (IllegalArgumentException | ReflectiveOperationException | SecurityException | NamingException e) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.invalidDecoder", new Object[]{decoderClazz.getName()}), (Throwable)e);
                }
                DecoderEntry entry = new DecoderEntry(Util.getDecoderType(decoderClazz), decoderClazz);
                result.add(entry);
            }
        }
        return result;
    }

    static Set<MessageHandlerResult> getMessageHandlers(Class<?> target, MessageHandler listener, EndpointConfig endpointConfig, Session session) {
        HashSet<MessageHandlerResult> results = new HashSet<MessageHandlerResult>(2);
        if (String.class.isAssignableFrom(target)) {
            MessageHandlerResult result = new MessageHandlerResult(listener, MessageHandlerResultType.TEXT);
            results.add(result);
        } else if (ByteBuffer.class.isAssignableFrom(target)) {
            MessageHandlerResult result = new MessageHandlerResult(listener, MessageHandlerResultType.BINARY);
            results.add(result);
        } else if (PongMessage.class.isAssignableFrom(target)) {
            MessageHandlerResult result = new MessageHandlerResult(listener, MessageHandlerResultType.PONG);
            results.add(result);
        } else if (byte[].class.isAssignableFrom(target)) {
            boolean whole = MessageHandler.Whole.class.isAssignableFrom(listener.getClass());
            MessageHandlerResult result = new MessageHandlerResult((MessageHandler)(whole ? new PojoMessageHandlerWholeBinary(listener, Util.getOnMessageMethod(listener), session, endpointConfig, Util.matchDecoders(target, endpointConfig, true, ((WsSession)session).getInstanceManager()), new Object[1], 0, true, -1, false, -1L) : new PojoMessageHandlerPartialBinary(listener, Util.getOnMessagePartialMethod(listener), session, new Object[2], 0, true, 1, -1, -1L)), MessageHandlerResultType.BINARY);
            results.add(result);
        } else if (InputStream.class.isAssignableFrom(target)) {
            MessageHandlerResult result = new MessageHandlerResult((MessageHandler)new PojoMessageHandlerWholeBinary(listener, Util.getOnMessageMethod(listener), session, endpointConfig, Util.matchDecoders(target, endpointConfig, true, ((WsSession)session).getInstanceManager()), new Object[1], 0, true, -1, true, -1L), MessageHandlerResultType.BINARY);
            results.add(result);
        } else if (Reader.class.isAssignableFrom(target)) {
            MessageHandlerResult result = new MessageHandlerResult((MessageHandler)new PojoMessageHandlerWholeText(listener, Util.getOnMessageMethod(listener), session, endpointConfig, Util.matchDecoders(target, endpointConfig, false, ((WsSession)session).getInstanceManager()), new Object[1], 0, true, -1, -1L), MessageHandlerResultType.TEXT);
            results.add(result);
        } else {
            MessageHandlerResult result;
            DecoderMatch decoderMatch = Util.matchDecoders(target, endpointConfig, ((WsSession)session).getInstanceManager());
            Method m = Util.getOnMessageMethod(listener);
            if (decoderMatch.getBinaryDecoders().size() > 0) {
                result = new MessageHandlerResult((MessageHandler)new PojoMessageHandlerWholeBinary(listener, m, session, endpointConfig, decoderMatch.getBinaryDecoders(), new Object[1], 0, false, -1, false, -1L), MessageHandlerResultType.BINARY);
                results.add(result);
            }
            if (decoderMatch.getTextDecoders().size() > 0) {
                result = new MessageHandlerResult((MessageHandler)new PojoMessageHandlerWholeText(listener, m, session, endpointConfig, decoderMatch.getTextDecoders(), new Object[1], 0, false, -1, -1L), MessageHandlerResultType.TEXT);
                results.add(result);
            }
        }
        if (results.size() == 0) {
            throw new IllegalArgumentException(sm.getString("wsSession.unknownHandler", new Object[]{listener, target}));
        }
        return results;
    }

    private static List<Class<? extends Decoder>> matchDecoders(Class<?> target, EndpointConfig endpointConfig, boolean binary, InstanceManager instanceManager) {
        DecoderMatch decoderMatch = Util.matchDecoders(target, endpointConfig, instanceManager);
        if (binary) {
            if (decoderMatch.getBinaryDecoders().size() > 0) {
                return decoderMatch.getBinaryDecoders();
            }
        } else if (decoderMatch.getTextDecoders().size() > 0) {
            return decoderMatch.getTextDecoders();
        }
        return null;
    }

    private static DecoderMatch matchDecoders(Class<?> target, EndpointConfig endpointConfig, InstanceManager instanceManager) {
        DecoderMatch decoderMatch;
        try {
            List decoders = endpointConfig.getDecoders();
            List<DecoderEntry> decoderEntries = Util.getDecoders(decoders, instanceManager);
            decoderMatch = new DecoderMatch(target, decoderEntries);
        }
        catch (DeploymentException e) {
            throw new IllegalArgumentException(e);
        }
        return decoderMatch;
    }

    public static void parseExtensionHeader(List<Extension> extensions, String header) {
        String[] unparsedExtensions;
        for (String unparsedExtension : unparsedExtensions = header.split(",")) {
            String[] unparsedParameters = unparsedExtension.split(";");
            WsExtension extension = new WsExtension(unparsedParameters[0].trim());
            for (int i = 1; i < unparsedParameters.length; ++i) {
                String value;
                String name;
                int equalsPos = unparsedParameters[i].indexOf(61);
                if (equalsPos == -1) {
                    name = unparsedParameters[i].trim();
                    value = null;
                } else {
                    name = unparsedParameters[i].substring(0, equalsPos).trim();
                    value = unparsedParameters[i].substring(equalsPos + 1).trim();
                    int len = value.length();
                    if (len > 1 && value.charAt(0) == '\"' && value.charAt(len - 1) == '\"') {
                        value = value.substring(1, value.length() - 1);
                    }
                }
                if (Util.containsDelims(name) || Util.containsDelims(value)) {
                    throw new IllegalArgumentException(sm.getString("util.notToken", new Object[]{name, value}));
                }
                if (value != null && (value.indexOf(44) > -1 || value.indexOf(59) > -1 || value.indexOf(34) > -1 || value.indexOf(61) > -1)) {
                    throw new IllegalArgumentException(sm.getString("", new Object[]{value}));
                }
                extension.addParameter(new WsExtensionParameter(name, value));
            }
            extensions.add(extension);
        }
    }

    private static boolean containsDelims(String input) {
        if (input == null || input.length() == 0) {
            return false;
        }
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\"': 
                case ',': 
                case ';': 
                case '=': {
                    return true;
                }
            }
        }
        return false;
    }

    private static Method getOnMessageMethod(MessageHandler listener) {
        try {
            return listener.getClass().getMethod("onMessage", Object.class);
        }
        catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("util.invalidMessageHandler"), e);
        }
    }

    private static Method getOnMessagePartialMethod(MessageHandler listener) {
        try {
            return listener.getClass().getMethod("onMessage", Object.class, Boolean.TYPE);
        }
        catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("util.invalidMessageHandler"), e);
        }
    }

    private static class TypeResult {
        private final Class<?> clazz;
        private final int index;
        private int dimension;

        TypeResult(Class<?> clazz, int index, int dimension) {
            this.clazz = clazz;
            this.index = index;
            this.dimension = dimension;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public int getIndex() {
            return this.index;
        }

        public int getDimension() {
            return this.dimension;
        }

        public void incrementDimension(int inc) {
            this.dimension += inc;
        }
    }

    public static class DecoderMatch {
        private final List<Class<? extends Decoder>> textDecoders = new ArrayList<Class<? extends Decoder>>();
        private final List<Class<? extends Decoder>> binaryDecoders = new ArrayList<Class<? extends Decoder>>();
        private final Class<?> target;

        public DecoderMatch(Class<?> target, List<DecoderEntry> decoderEntries) {
            this.target = target;
            for (DecoderEntry decoderEntry : decoderEntries) {
                if (!decoderEntry.getClazz().isAssignableFrom(target)) continue;
                if (Decoder.Binary.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                    this.binaryDecoders.add(decoderEntry.getDecoderClazz());
                    continue;
                }
                if (Decoder.BinaryStream.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                    this.binaryDecoders.add(decoderEntry.getDecoderClazz());
                    break;
                }
                if (Decoder.Text.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                    this.textDecoders.add(decoderEntry.getDecoderClazz());
                    continue;
                }
                if (Decoder.TextStream.class.isAssignableFrom(decoderEntry.getDecoderClazz())) {
                    this.textDecoders.add(decoderEntry.getDecoderClazz());
                    break;
                }
                throw new IllegalArgumentException(sm.getString("util.unknownDecoderType"));
            }
        }

        public List<Class<? extends Decoder>> getTextDecoders() {
            return this.textDecoders;
        }

        public List<Class<? extends Decoder>> getBinaryDecoders() {
            return this.binaryDecoders;
        }

        public Class<?> getTarget() {
            return this.target;
        }

        public boolean hasMatches() {
            return this.textDecoders.size() > 0 || this.binaryDecoders.size() > 0;
        }
    }
}

