/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.privateutil;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.MethodUtil;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.privateutil.RuntimePolicyUtilsException;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class PolicyUtils {
    private PolicyUtils() {
    }

    public static class Rfc2396 {
        private static final PolicyLogger LOGGER = PolicyLogger.getLogger(Reflection.class);

        public static String unquote(String quoted) {
            if (null == quoted) {
                return null;
            }
            byte[] unquoted = new byte[quoted.length()];
            int newLength = 0;
            for (int i = 0; i < quoted.length(); ++i) {
                char c = quoted.charAt(i);
                if ('%' == c) {
                    if (i + 2 >= quoted.length()) {
                        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted)), false);
                    }
                    int hi = Character.digit(quoted.charAt(++i), 16);
                    int lo = Character.digit(quoted.charAt(++i), 16);
                    if (0 > hi || 0 > lo) {
                        throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted)), false);
                    }
                    unquoted[newLength++] = (byte)(hi * 16 + lo);
                    continue;
                }
                unquoted[newLength++] = (byte)c;
            }
            try {
                return new String(unquoted, 0, newLength, "utf-8");
            }
            catch (UnsupportedEncodingException uee) {
                throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted), uee));
            }
        }
    }

    public static class ConfigFile {
        public static String generateFullName(String configFileIdentifier) throws PolicyException {
            if (configFileIdentifier != null) {
                StringBuffer buffer = new StringBuffer("wsit-");
                buffer.append(configFileIdentifier).append(".xml");
                return buffer.toString();
            }
            throw new PolicyException(LocalizationMessages.WSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL());
        }

        public static URL loadFromContext(String configFileName, Object context) {
            return Reflection.invoke(context, "getResource", URL.class, configFileName);
        }

        public static URL loadFromClasspath(String configFileName) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                return ClassLoader.getSystemResource(configFileName);
            }
            return cl.getResource(configFileName);
        }
    }

    static class Reflection {
        private static final PolicyLogger LOGGER = PolicyLogger.getLogger(Reflection.class);

        Reflection() {
        }

        static <T> T invoke(Object target, String methodName, Class<T> resultClass, Object ... parameters) throws RuntimePolicyUtilsException {
            Class[] parameterTypes;
            if (parameters != null && parameters.length > 0) {
                parameterTypes = new Class[parameters.length];
                int i = 0;
                for (Object parameter : parameters) {
                    parameterTypes[i++] = parameter.getClass();
                }
            } else {
                parameterTypes = null;
            }
            return Reflection.invoke(target, methodName, resultClass, parameters, parameterTypes);
        }

        public static <T> T invoke(Object target, String methodName, Class<T> resultClass, Object[] parameters, Class[] parameterTypes) throws RuntimePolicyUtilsException {
            try {
                Method method = target.getClass().getMethod(methodName, parameterTypes);
                Object result = MethodUtil.invoke(target, method, parameters);
                return resultClass.cast(result);
            }
            catch (IllegalArgumentException e) {
                throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(Reflection.createExceptionMessage(target, parameters, methodName), e));
            }
            catch (InvocationTargetException e) {
                throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(Reflection.createExceptionMessage(target, parameters, methodName), e));
            }
            catch (IllegalAccessException e) {
                throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(Reflection.createExceptionMessage(target, parameters, methodName), e.getCause()));
            }
            catch (SecurityException e) {
                throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(Reflection.createExceptionMessage(target, parameters, methodName), e));
            }
            catch (NoSuchMethodException e) {
                throw (RuntimePolicyUtilsException)LOGGER.logSevereException(new RuntimePolicyUtilsException(Reflection.createExceptionMessage(target, parameters, methodName), e));
            }
        }

        private static String createExceptionMessage(Object target, Object[] parameters, String methodName) {
            return LocalizationMessages.WSP_0061_METHOD_INVOCATION_FAILED(target.getClass().getName(), methodName, parameters == null ? null : Arrays.asList(parameters).toString());
        }
    }

    public static class Collections {
        private static final PolicyLogger LOGGER = PolicyLogger.getLogger(Collections.class);

        public static <E, T extends Collection<? extends E>, U extends Collection<? extends E>> Collection<Collection<E>> combine(U initialBase, Collection<T> options, boolean ignoreEmptyOption) {
            ArrayList<Collection<AbstractList>> combinations = null;
            if (options == null || options.isEmpty()) {
                if (initialBase != null) {
                    combinations = new ArrayList<Collection<AbstractList>>(1);
                    combinations.add(new ArrayList(initialBase));
                }
                return combinations;
            }
            LinkedList base = new LinkedList();
            if (initialBase != null && !initialBase.isEmpty()) {
                base.addAll(initialBase);
            }
            int finalCombinationsSize = 1;
            LinkedList<Collection> optionProcessingQueue = new LinkedList<Collection>();
            for (Collection option : options) {
                int optionSize = option.size();
                if (optionSize == 0) {
                    if (ignoreEmptyOption) continue;
                    return null;
                }
                if (optionSize == 1) {
                    base.addAll(option);
                    continue;
                }
                boolean entered = optionProcessingQueue.offer(option);
                if (!entered) {
                    throw (RuntimePolicyUtilsException)LOGGER.logException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0096_ERROR_WHILE_COMBINE(option)), false, Level.WARNING);
                }
                finalCombinationsSize *= optionSize;
            }
            combinations = new ArrayList(finalCombinationsSize);
            combinations.add(base);
            if (finalCombinationsSize > 1) {
                Collection processedOption;
                while ((processedOption = (Collection)optionProcessingQueue.poll()) != null) {
                    int actualSemiCombinationCollectionSize = combinations.size();
                    int newSemiCombinationCollectionSize = actualSemiCombinationCollectionSize * processedOption.size();
                    int semiCombinationIndex = 0;
                    for (Object optionElement : processedOption) {
                        for (int i = 0; i < actualSemiCombinationCollectionSize; ++i) {
                            Collection semiCombination = (Collection)combinations.get(semiCombinationIndex);
                            if (semiCombinationIndex + actualSemiCombinationCollectionSize < newSemiCombinationCollectionSize) {
                                combinations.add(new LinkedList(semiCombination));
                            }
                            semiCombination.add(optionElement);
                            ++semiCombinationIndex;
                        }
                    }
                }
            }
            return combinations;
        }
    }

    public static class Comparison {
        public static final Comparator<QName> QNAME_COMPARATOR = new Comparator<QName>(){

            @Override
            public int compare(QName qn1, QName qn2) {
                if (qn1 == qn2 || qn1.equals(qn2)) {
                    return 0;
                }
                int result = qn1.getNamespaceURI().compareTo(qn2.getNamespaceURI());
                if (result != 0) {
                    return result;
                }
                return qn1.getLocalPart().compareTo(qn2.getLocalPart());
            }
        };

        public static int compareBoolean(boolean b1, boolean b2) {
            int i1 = b1 ? 1 : 0;
            int i2 = b2 ? 1 : 0;
            return i1 - i2;
        }

        public static int compareNullableStrings(String s1, String s2) {
            return s1 == null ? (s2 == null ? 0 : -1) : (s2 == null ? 1 : s1.compareTo(s2));
        }
    }

    public static class Text {
        public static final String NEW_LINE = System.getProperty("line.separator");

        public static String createIndent(int indentLevel) {
            char[] charData = new char[indentLevel * 4];
            Arrays.fill(charData, ' ');
            return String.valueOf(charData);
        }
    }

    public static class IO {
        private static final PolicyLogger LOGGER = PolicyLogger.getLogger(IO.class);

        public static void closeResource(Closeable resource) {
            if (resource != null) {
                try {
                    resource.close();
                }
                catch (IOException e) {
                    LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(resource.toString()), e);
                }
            }
        }

        public static void closeResource(XMLStreamReader reader) {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (XMLStreamException e) {
                    LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(reader.toString()), e);
                }
            }
        }
    }

    public static class Commons {
        public static String getStackMethodName(int methodIndexInStack) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String methodName = stack.length > methodIndexInStack + 1 ? stack[methodIndexInStack].getMethodName() : "UNKNOWN METHOD";
            return methodName;
        }

        public static String getCallerMethodName() {
            String result = Commons.getStackMethodName(5);
            if (result.equals("invoke0")) {
                result = Commons.getStackMethodName(4);
            }
            return result;
        }
    }
}

