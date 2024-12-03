/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason
 *  javax.websocket.DecodeException
 *  javax.websocket.Decoder
 *  javax.websocket.DeploymentException
 *  javax.websocket.EndpointConfig
 *  javax.websocket.MessageHandler
 *  javax.websocket.OnClose
 *  javax.websocket.OnError
 *  javax.websocket.OnMessage
 *  javax.websocket.OnOpen
 *  javax.websocket.PongMessage
 *  javax.websocket.Session
 *  javax.websocket.server.PathParam
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.pojo;

import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.DecoderEntry;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerBase;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerPartialBinary;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerPartialText;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBinary;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholePong;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeText;
import org.apache.tomcat.websocket.pojo.PojoPathParam;

public class PojoMethodMapping {
    private static final StringManager sm = StringManager.getManager(PojoMethodMapping.class);
    private final Method onOpen;
    private final Method onClose;
    private final Method onError;
    private final PojoPathParam[] onOpenParams;
    private final PojoPathParam[] onCloseParams;
    private final PojoPathParam[] onErrorParams;
    private final List<MessageHandlerInfo> onMessage = new ArrayList<MessageHandlerInfo>();
    private final String wsPath;

    @Deprecated
    public PojoMethodMapping(Class<?> clazzPojo, List<Class<? extends Decoder>> decoderClazzes, String wsPath) throws DeploymentException {
        this(clazzPojo, decoderClazzes, wsPath, null);
    }

    public PojoMethodMapping(Class<?> clazzPojo, List<Class<? extends Decoder>> decoderClazzes, String wsPath, InstanceManager instanceManager) throws DeploymentException {
        this.wsPath = wsPath;
        List<DecoderEntry> decoders = Util.getDecoders(decoderClazzes, instanceManager);
        Object open = null;
        Object close = null;
        Object error = null;
        Method[] clazzPojoMethods = null;
        Class<?> currentClazz = clazzPojo;
        while (!currentClazz.equals(Object.class)) {
            Method[] currentClazzMethods = currentClazz.getDeclaredMethods();
            if (currentClazz == clazzPojo) {
                clazzPojoMethods = currentClazzMethods;
            }
            for (Method method : currentClazzMethods) {
                if (method.isSynthetic()) continue;
                if (method.getAnnotation(OnOpen.class) != null) {
                    this.checkPublic(method);
                    if (open == null) {
                        open = method;
                        continue;
                    }
                    if (currentClazz != clazzPojo && this.isMethodOverride((Method)open, method)) continue;
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateAnnotation", new Object[]{OnOpen.class, currentClazz}));
                }
                if (method.getAnnotation(OnClose.class) != null) {
                    this.checkPublic(method);
                    if (close == null) {
                        close = method;
                        continue;
                    }
                    if (currentClazz != clazzPojo && this.isMethodOverride((Method)close, method)) continue;
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateAnnotation", new Object[]{OnClose.class, currentClazz}));
                }
                if (method.getAnnotation(OnError.class) != null) {
                    this.checkPublic(method);
                    if (error == null) {
                        error = method;
                        continue;
                    }
                    if (currentClazz != clazzPojo && this.isMethodOverride((Method)error, method)) continue;
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateAnnotation", new Object[]{OnError.class, currentClazz}));
                }
                if (method.getAnnotation(OnMessage.class) == null) continue;
                this.checkPublic(method);
                MessageHandlerInfo messageHandler = new MessageHandlerInfo(method, decoders);
                boolean found = false;
                for (MessageHandlerInfo otherMessageHandler : this.onMessage) {
                    if (!messageHandler.targetsSameWebSocketMessageType(otherMessageHandler)) continue;
                    found = true;
                    if (currentClazz != clazzPojo && this.isMethodOverride(messageHandler.m, otherMessageHandler.m)) continue;
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateAnnotation", new Object[]{OnMessage.class, currentClazz}));
                }
                if (found) continue;
                this.onMessage.add(messageHandler);
            }
            currentClazz = currentClazz.getSuperclass();
        }
        if (open != null && ((Method)open).getDeclaringClass() != clazzPojo && this.isOverridenWithoutAnnotation(clazzPojoMethods, (Method)open, OnOpen.class)) {
            open = null;
        }
        if (close != null && ((Method)close).getDeclaringClass() != clazzPojo && this.isOverridenWithoutAnnotation(clazzPojoMethods, (Method)close, OnClose.class)) {
            close = null;
        }
        if (error != null && ((Method)error).getDeclaringClass() != clazzPojo && this.isOverridenWithoutAnnotation(clazzPojoMethods, (Method)error, OnError.class)) {
            error = null;
        }
        ArrayList<MessageHandlerInfo> overriddenOnMessage = new ArrayList<MessageHandlerInfo>();
        for (MessageHandlerInfo messageHandler : this.onMessage) {
            if (messageHandler.m.getDeclaringClass() == clazzPojo || !this.isOverridenWithoutAnnotation(clazzPojoMethods, messageHandler.m, OnMessage.class)) continue;
            overriddenOnMessage.add(messageHandler);
        }
        for (MessageHandlerInfo messageHandler : overriddenOnMessage) {
            this.onMessage.remove(messageHandler);
        }
        this.onOpen = open;
        this.onClose = close;
        this.onError = error;
        this.onOpenParams = PojoMethodMapping.getPathParams(this.onOpen, MethodType.ON_OPEN);
        this.onCloseParams = PojoMethodMapping.getPathParams(this.onClose, MethodType.ON_CLOSE);
        this.onErrorParams = PojoMethodMapping.getPathParams(this.onError, MethodType.ON_ERROR);
    }

    private void checkPublic(Method m) throws DeploymentException {
        if (!Modifier.isPublic(m.getModifiers())) {
            throw new DeploymentException(sm.getString("pojoMethodMapping.methodNotPublic", new Object[]{m.getName()}));
        }
    }

    private boolean isMethodOverride(Method method1, Method method2) {
        return method1.getName().equals(method2.getName()) && method1.getReturnType().equals(method2.getReturnType()) && Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes());
    }

    private boolean isOverridenWithoutAnnotation(Method[] methods, Method superclazzMethod, Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            if (!this.isMethodOverride(method, superclazzMethod) || method.getAnnotation(annotation) != null) continue;
            return true;
        }
        return false;
    }

    public String getWsPath() {
        return this.wsPath;
    }

    public Method getOnOpen() {
        return this.onOpen;
    }

    public Object[] getOnOpenArgs(Map<String, String> pathParameters, Session session, EndpointConfig config) throws DecodeException {
        return PojoMethodMapping.buildArgs(this.onOpenParams, pathParameters, session, config, null, null);
    }

    public Method getOnClose() {
        return this.onClose;
    }

    public Object[] getOnCloseArgs(Map<String, String> pathParameters, Session session, CloseReason closeReason) throws DecodeException {
        return PojoMethodMapping.buildArgs(this.onCloseParams, pathParameters, session, null, null, closeReason);
    }

    public Method getOnError() {
        return this.onError;
    }

    public Object[] getOnErrorArgs(Map<String, String> pathParameters, Session session, Throwable throwable) throws DecodeException {
        return PojoMethodMapping.buildArgs(this.onErrorParams, pathParameters, session, null, throwable, null);
    }

    public boolean hasMessageHandlers() {
        return !this.onMessage.isEmpty();
    }

    public Set<MessageHandler> getMessageHandlers(Object pojo, Map<String, String> pathParameters, Session session, EndpointConfig config) {
        HashSet<MessageHandler> result = new HashSet<MessageHandler>();
        for (MessageHandlerInfo messageMethod : this.onMessage) {
            result.addAll(messageMethod.getMessageHandlers(pojo, pathParameters, session, config));
        }
        return result;
    }

    private static PojoPathParam[] getPathParams(Method m, MethodType methodType) throws DeploymentException {
        if (m == null) {
            return new PojoPathParam[0];
        }
        boolean foundThrowable = false;
        Class<?>[] types = m.getParameterTypes();
        Annotation[][] paramsAnnotations = m.getParameterAnnotations();
        PojoPathParam[] result = new PojoPathParam[types.length];
        for (int i = 0; i < types.length; ++i) {
            Annotation[] paramAnnotations;
            Class<?> type = types[i];
            if (type.equals(Session.class)) {
                result[i] = new PojoPathParam(type, null);
                continue;
            }
            if (methodType == MethodType.ON_OPEN && type.equals(EndpointConfig.class)) {
                result[i] = new PojoPathParam(type, null);
                continue;
            }
            if (methodType == MethodType.ON_ERROR && type.equals(Throwable.class)) {
                foundThrowable = true;
                result[i] = new PojoPathParam(type, null);
                continue;
            }
            if (methodType == MethodType.ON_CLOSE && type.equals(CloseReason.class)) {
                result[i] = new PojoPathParam(type, null);
                continue;
            }
            for (Annotation paramAnnotation : paramAnnotations = paramsAnnotations[i]) {
                if (!paramAnnotation.annotationType().equals(PathParam.class)) continue;
                result[i] = new PojoPathParam(type, ((PathParam)paramAnnotation).value());
                break;
            }
            if (result[i] != null) continue;
            throw new DeploymentException(sm.getString("pojoMethodMapping.paramWithoutAnnotation", new Object[]{type, m.getName(), m.getClass().getName()}));
        }
        if (methodType == MethodType.ON_ERROR && !foundThrowable) {
            throw new DeploymentException(sm.getString("pojoMethodMapping.onErrorNoThrowable", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
        }
        return result;
    }

    private static Object[] buildArgs(PojoPathParam[] pathParams, Map<String, String> pathParameters, Session session, EndpointConfig config, Throwable throwable, CloseReason closeReason) throws DecodeException {
        Object[] result = new Object[pathParams.length];
        for (int i = 0; i < pathParams.length; ++i) {
            Class<?> type = pathParams[i].getType();
            if (type.equals(Session.class)) {
                result[i] = session;
                continue;
            }
            if (type.equals(EndpointConfig.class)) {
                result[i] = config;
                continue;
            }
            if (type.equals(Throwable.class)) {
                result[i] = throwable;
                continue;
            }
            if (type.equals(CloseReason.class)) {
                result[i] = closeReason;
                continue;
            }
            String name = pathParams[i].getName();
            String value = pathParameters.get(name);
            try {
                result[i] = Util.coerceToType(type, value);
                continue;
            }
            catch (Exception e) {
                throw new DecodeException(value, sm.getString("pojoMethodMapping.decodePathParamFail", new Object[]{value, type}), (Throwable)e);
            }
        }
        return result;
    }

    private static class MessageHandlerInfo {
        private final Method m;
        private int indexString = -1;
        private int indexByteArray = -1;
        private int indexByteBuffer = -1;
        private int indexPong = -1;
        private int indexBoolean = -1;
        private int indexSession = -1;
        private int indexInputStream = -1;
        private int indexReader = -1;
        private int indexPrimitive = -1;
        private Map<Integer, PojoPathParam> indexPathParams = new HashMap<Integer, PojoPathParam>();
        private int indexPayload = -1;
        private Util.DecoderMatch decoderMatch = null;
        private long maxMessageSize = -1L;

        MessageHandlerInfo(Method m, List<DecoderEntry> decoderEntries) throws DeploymentException {
            this.m = m;
            Class<?>[] types = m.getParameterTypes();
            Annotation[][] paramsAnnotations = m.getParameterAnnotations();
            for (int i = 0; i < types.length; ++i) {
                Annotation[] paramAnnotations;
                boolean paramFound = false;
                for (Annotation paramAnnotation : paramAnnotations = paramsAnnotations[i]) {
                    if (!paramAnnotation.annotationType().equals(PathParam.class)) continue;
                    this.indexPathParams.put(i, new PojoPathParam(types[i], ((PathParam)paramAnnotation).value()));
                    paramFound = true;
                    break;
                }
                if (paramFound) continue;
                if (String.class.isAssignableFrom(types[i])) {
                    if (this.indexString == -1) {
                        this.indexString = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (Reader.class.isAssignableFrom(types[i])) {
                    if (this.indexReader == -1) {
                        this.indexReader = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (Boolean.TYPE == types[i]) {
                    if (this.indexBoolean == -1) {
                        this.indexBoolean = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateLastParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (ByteBuffer.class.isAssignableFrom(types[i])) {
                    if (this.indexByteBuffer == -1) {
                        this.indexByteBuffer = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (byte[].class == types[i]) {
                    if (this.indexByteArray == -1) {
                        this.indexByteArray = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (InputStream.class.isAssignableFrom(types[i])) {
                    if (this.indexInputStream == -1) {
                        this.indexInputStream = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (Util.isPrimitive(types[i])) {
                    if (this.indexPrimitive == -1) {
                        this.indexPrimitive = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (Session.class.isAssignableFrom(types[i])) {
                    if (this.indexSession == -1) {
                        this.indexSession = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateSessionParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (PongMessage.class.isAssignableFrom(types[i])) {
                    if (this.indexPong == -1) {
                        this.indexPong = i;
                        continue;
                    }
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicatePongMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                if (this.decoderMatch != null && this.decoderMatch.hasMatches()) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.decoderMatch = new Util.DecoderMatch(types[i], decoderEntries);
                if (this.decoderMatch.hasMatches()) {
                    this.indexPayload = i;
                    continue;
                }
                throw new DeploymentException(sm.getString("pojoMethodMapping.noDecoder", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
            }
            if (this.indexString != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.indexPayload = this.indexString;
            }
            if (this.indexReader != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.indexPayload = this.indexReader;
            }
            if (this.indexByteArray != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.indexPayload = this.indexByteArray;
            }
            if (this.indexByteBuffer != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.indexPayload = this.indexByteBuffer;
            }
            if (this.indexInputStream != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.indexPayload = this.indexInputStream;
            }
            if (this.indexPrimitive != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.duplicateMessageParam", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.indexPayload = this.indexPrimitive;
            }
            if (this.indexPong != -1) {
                if (this.indexPayload != -1) {
                    throw new DeploymentException(sm.getString("pojoMethodMapping.pongWithPayload", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
                }
                this.indexPayload = this.indexPong;
            }
            if (this.indexPayload == -1 && this.indexPrimitive == -1 && this.indexBoolean != -1) {
                this.indexPayload = this.indexBoolean;
                this.indexPrimitive = this.indexBoolean;
                this.indexBoolean = -1;
            }
            if (this.indexPayload == -1) {
                throw new DeploymentException(sm.getString("pojoMethodMapping.noPayload", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
            }
            if (this.indexPong != -1 && this.indexBoolean != -1) {
                throw new DeploymentException(sm.getString("pojoMethodMapping.partialPong", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
            }
            if (this.indexReader != -1 && this.indexBoolean != -1) {
                throw new DeploymentException(sm.getString("pojoMethodMapping.partialReader", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
            }
            if (this.indexInputStream != -1 && this.indexBoolean != -1) {
                throw new DeploymentException(sm.getString("pojoMethodMapping.partialInputStream", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
            }
            if (this.decoderMatch != null && this.decoderMatch.hasMatches() && this.indexBoolean != -1) {
                throw new DeploymentException(sm.getString("pojoMethodMapping.partialObject", new Object[]{m.getName(), m.getDeclaringClass().getName()}));
            }
            this.maxMessageSize = m.getAnnotation(OnMessage.class).maxMessageSize();
        }

        public boolean targetsSameWebSocketMessageType(MessageHandlerInfo otherHandler) {
            if (otherHandler == null) {
                return false;
            }
            return this.isPong() && otherHandler.isPong() || this.isBinary() && otherHandler.isBinary() || this.isText() && otherHandler.isText();
        }

        private boolean isPong() {
            return this.indexPong >= 0;
        }

        private boolean isText() {
            return this.indexString >= 0 || this.indexPrimitive >= 0 || this.indexReader >= 0 || this.decoderMatch != null && this.decoderMatch.getTextDecoders().size() > 0;
        }

        private boolean isBinary() {
            return this.indexByteArray >= 0 || this.indexByteBuffer >= 0 || this.indexInputStream >= 0 || this.decoderMatch != null && this.decoderMatch.getBinaryDecoders().size() > 0;
        }

        public Set<MessageHandler> getMessageHandlers(Object pojo, Map<String, String> pathParameters, Session session, EndpointConfig config) {
            PojoMessageHandlerBase mh;
            Object[] params = new Object[this.m.getParameterTypes().length];
            for (Map.Entry<Integer, PojoPathParam> entry : this.indexPathParams.entrySet()) {
                PojoPathParam pathParam = entry.getValue();
                String valueString = pathParameters.get(pathParam.getName());
                Object value = null;
                try {
                    value = Util.coerceToType(pathParam.getType(), valueString);
                }
                catch (Exception e) {
                    DecodeException de = new DecodeException(valueString, sm.getString("pojoMethodMapping.decodePathParamFail", new Object[]{valueString, pathParam.getType()}), (Throwable)e);
                    params = new Object[]{de};
                    break;
                }
                params[entry.getKey().intValue()] = value;
            }
            HashSet<MessageHandler> results = new HashSet<MessageHandler>(2);
            if (this.indexBoolean == -1) {
                if (this.indexString != -1 || this.indexPrimitive != -1) {
                    mh = new PojoMessageHandlerWholeText(pojo, this.m, session, config, null, params, this.indexPayload, false, this.indexSession, this.maxMessageSize);
                    results.add((MessageHandler)mh);
                } else if (this.indexReader != -1) {
                    mh = new PojoMessageHandlerWholeText(pojo, this.m, session, config, null, params, this.indexReader, true, this.indexSession, this.maxMessageSize);
                    results.add((MessageHandler)mh);
                } else if (this.indexByteArray != -1) {
                    mh = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, null, params, this.indexByteArray, true, this.indexSession, false, this.maxMessageSize);
                    results.add((MessageHandler)mh);
                } else if (this.indexByteBuffer != -1) {
                    mh = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, null, params, this.indexByteBuffer, false, this.indexSession, false, this.maxMessageSize);
                    results.add((MessageHandler)mh);
                } else if (this.indexInputStream != -1) {
                    mh = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, null, params, this.indexInputStream, true, this.indexSession, true, this.maxMessageSize);
                    results.add((MessageHandler)mh);
                } else if (this.decoderMatch != null && this.decoderMatch.hasMatches()) {
                    if (this.decoderMatch.getBinaryDecoders().size() > 0) {
                        mh = new PojoMessageHandlerWholeBinary(pojo, this.m, session, config, this.decoderMatch.getBinaryDecoders(), params, this.indexPayload, true, this.indexSession, true, this.maxMessageSize);
                        results.add((MessageHandler)mh);
                    }
                    if (this.decoderMatch.getTextDecoders().size() > 0) {
                        mh = new PojoMessageHandlerWholeText(pojo, this.m, session, config, this.decoderMatch.getTextDecoders(), params, this.indexPayload, true, this.indexSession, this.maxMessageSize);
                        results.add((MessageHandler)mh);
                    }
                } else {
                    mh = new PojoMessageHandlerWholePong(pojo, this.m, session, params, this.indexPong, false, this.indexSession);
                    results.add((MessageHandler)mh);
                }
            } else if (this.indexString != -1) {
                mh = new PojoMessageHandlerPartialText(pojo, this.m, session, params, this.indexString, false, this.indexBoolean, this.indexSession, this.maxMessageSize);
                results.add((MessageHandler)mh);
            } else if (this.indexByteArray != -1) {
                mh = new PojoMessageHandlerPartialBinary(pojo, this.m, session, params, this.indexByteArray, true, this.indexBoolean, this.indexSession, this.maxMessageSize);
                results.add((MessageHandler)mh);
            } else {
                mh = new PojoMessageHandlerPartialBinary(pojo, this.m, session, params, this.indexByteBuffer, false, this.indexBoolean, this.indexSession, this.maxMessageSize);
                results.add((MessageHandler)mh);
            }
            return results;
        }
    }

    private static enum MethodType {
        ON_OPEN,
        ON_CLOSE,
        ON_ERROR;

    }
}

