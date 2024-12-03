/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class MethodResolver {
    public static final int STATIC_ONLY = 1;
    public static final int INSTANCE_ONLY = 2;
    public static final int STATIC_AND_INSTANCE = 3;
    public static final int DYNAMIC = 4;
    private static final int SCOREBASE = 1;
    private static final ConversionInfo[] m_javaObjConversions = new ConversionInfo[]{new ConversionInfo(Double.TYPE, 11), new ConversionInfo(Float.TYPE, 12), new ConversionInfo(Long.TYPE, 13), new ConversionInfo(Integer.TYPE, 14), new ConversionInfo(Short.TYPE, 15), new ConversionInfo(Character.TYPE, 16), new ConversionInfo(Byte.TYPE, 17), new ConversionInfo(String.class, 18)};
    private static final ConversionInfo[] m_booleanConversions = new ConversionInfo[]{new ConversionInfo(Boolean.TYPE, 0), new ConversionInfo(Boolean.class, 1), new ConversionInfo(Object.class, 2), new ConversionInfo(String.class, 3)};
    private static final ConversionInfo[] m_numberConversions = new ConversionInfo[]{new ConversionInfo(Double.TYPE, 0), new ConversionInfo(Double.class, 1), new ConversionInfo(Float.TYPE, 3), new ConversionInfo(Long.TYPE, 4), new ConversionInfo(Integer.TYPE, 5), new ConversionInfo(Short.TYPE, 6), new ConversionInfo(Character.TYPE, 7), new ConversionInfo(Byte.TYPE, 8), new ConversionInfo(Boolean.TYPE, 9), new ConversionInfo(String.class, 10), new ConversionInfo(Object.class, 11)};
    private static final ConversionInfo[] m_stringConversions = new ConversionInfo[]{new ConversionInfo(String.class, 0), new ConversionInfo(Object.class, 1), new ConversionInfo(Character.TYPE, 2), new ConversionInfo(Double.TYPE, 3), new ConversionInfo(Float.TYPE, 3), new ConversionInfo(Long.TYPE, 3), new ConversionInfo(Integer.TYPE, 3), new ConversionInfo(Short.TYPE, 3), new ConversionInfo(Byte.TYPE, 3), new ConversionInfo(Boolean.TYPE, 4)};
    private static final ConversionInfo[] m_rtfConversions = new ConversionInfo[]{new ConversionInfo(NodeIterator.class, 0), new ConversionInfo(NodeList.class, 1), new ConversionInfo(Node.class, 2), new ConversionInfo(String.class, 3), new ConversionInfo(Object.class, 5), new ConversionInfo(Character.TYPE, 6), new ConversionInfo(Double.TYPE, 7), new ConversionInfo(Float.TYPE, 7), new ConversionInfo(Long.TYPE, 7), new ConversionInfo(Integer.TYPE, 7), new ConversionInfo(Short.TYPE, 7), new ConversionInfo(Byte.TYPE, 7), new ConversionInfo(Boolean.TYPE, 8)};
    private static final ConversionInfo[] m_nodesetConversions = new ConversionInfo[]{new ConversionInfo(NodeIterator.class, 0), new ConversionInfo(NodeList.class, 1), new ConversionInfo(Node.class, 2), new ConversionInfo(String.class, 3), new ConversionInfo(Object.class, 5), new ConversionInfo(Character.TYPE, 6), new ConversionInfo(Double.TYPE, 7), new ConversionInfo(Float.TYPE, 7), new ConversionInfo(Long.TYPE, 7), new ConversionInfo(Integer.TYPE, 7), new ConversionInfo(Short.TYPE, 7), new ConversionInfo(Byte.TYPE, 7), new ConversionInfo(Boolean.TYPE, 8)};
    private static final ConversionInfo[][] m_conversions = new ConversionInfo[][]{m_javaObjConversions, m_booleanConversions, m_numberConversions, m_stringConversions, m_nodesetConversions, m_rtfConversions};

    public static Constructor getConstructor(Class classObj, Object[] argsIn, Object[][] argsOut, ExpressionContext exprContext) throws NoSuchMethodException, SecurityException, TransformerException {
        Constructor<?> bestConstructor = null;
        Class[] bestParamTypes = null;
        Constructor<?>[] constructors = classObj.getConstructors();
        int nMethods = constructors.length;
        int bestScore = Integer.MAX_VALUE;
        int bestScoreCount = 0;
        for (int i = 0; i < nMethods; ++i) {
            int score;
            int scoreStart;
            Constructor<?> ctor = constructors[i];
            Class[] paramTypes = ctor.getParameterTypes();
            int numberMethodParams = paramTypes.length;
            int paramStart = 0;
            boolean isFirstExpressionContext = false;
            if (numberMethodParams == argsIn.length + 1) {
                Class<?> javaClass = paramTypes[0];
                if (!ExpressionContext.class.isAssignableFrom(javaClass)) continue;
                isFirstExpressionContext = true;
                scoreStart = 0;
                ++paramStart;
            } else {
                scoreStart = 1000;
            }
            if (argsIn.length != numberMethodParams - paramStart || -1 == (score = MethodResolver.scoreMatch(paramTypes, paramStart, argsIn, scoreStart))) continue;
            if (score < bestScore) {
                bestConstructor = ctor;
                bestParamTypes = paramTypes;
                bestScore = score;
                bestScoreCount = 1;
                continue;
            }
            if (score != bestScore) continue;
            ++bestScoreCount;
        }
        if (null == bestConstructor) {
            throw new NoSuchMethodException(MethodResolver.errString("function", "constructor", classObj, "", 0, argsIn));
        }
        MethodResolver.convertParams(argsIn, argsOut, bestParamTypes, exprContext);
        return bestConstructor;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static Method getMethod(Class classObj, String name, Object[] argsIn, Object[][] argsOut, ExpressionContext exprContext, int searchMethod) throws NoSuchMethodException, SecurityException, TransformerException {
        if (name.indexOf("-") > 0) {
            name = MethodResolver.replaceDash(name);
        }
        Method bestMethod = null;
        Class[] bestParamTypes = null;
        Method[] methods = classObj.getMethods();
        int nMethods = methods.length;
        int bestScore = Integer.MAX_VALUE;
        int bestScoreCount = 0;
        block6: for (int i = 0; i < nMethods; ++i) {
            int score;
            int scoreStart;
            int argsLen;
            Method method = methods[i];
            int xsltParamStart = 0;
            if (!method.getName().equals(name)) continue;
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            switch (searchMethod) {
                case 1: {
                    if (isStatic) break;
                    continue block6;
                }
                case 2: {
                    if (!isStatic) break;
                    continue block6;
                }
                case 3: {
                    break;
                }
                case 4: {
                    if (isStatic) break;
                    xsltParamStart = 1;
                }
            }
            int javaParamStart = 0;
            Class[] paramTypes = method.getParameterTypes();
            int numberMethodParams = paramTypes.length;
            boolean isFirstExpressionContext = false;
            int n = argsLen = null != argsIn ? argsIn.length : 0;
            if (numberMethodParams == argsLen - xsltParamStart + 1) {
                Class<?> javaClass = paramTypes[0];
                if (!ExpressionContext.class.isAssignableFrom(javaClass)) continue;
                isFirstExpressionContext = true;
                scoreStart = 0;
                ++javaParamStart;
            } else {
                scoreStart = 1000;
            }
            if (argsLen - xsltParamStart != numberMethodParams - javaParamStart || -1 == (score = MethodResolver.scoreMatch(paramTypes, javaParamStart, argsIn, scoreStart))) continue;
            if (score < bestScore) {
                bestMethod = method;
                bestParamTypes = paramTypes;
                bestScore = score;
                bestScoreCount = 1;
                continue;
            }
            if (score != bestScore) continue;
            ++bestScoreCount;
        }
        if (null == bestMethod) {
            throw new NoSuchMethodException(MethodResolver.errString("function", "method", classObj, name, searchMethod, argsIn));
        }
        MethodResolver.convertParams(argsIn, argsOut, bestParamTypes, exprContext);
        return bestMethod;
    }

    private static String replaceDash(String name) {
        char dash = '-';
        StringBuffer buff = new StringBuffer("");
        for (int i = 0; i < name.length(); ++i) {
            if (name.charAt(i) == dash) continue;
            if (i > 0 && name.charAt(i - 1) == dash) {
                buff.append(Character.toUpperCase(name.charAt(i)));
                continue;
            }
            buff.append(name.charAt(i));
        }
        return buff.toString();
    }

    public static Method getElementMethod(Class classObj, String name) throws NoSuchMethodException, SecurityException, TransformerException {
        Method bestMethod = null;
        Method[] methods = classObj.getMethods();
        int nMethods = methods.length;
        int bestScoreCount = 0;
        for (int i = 0; i < nMethods; ++i) {
            Class<?>[] paramTypes;
            Method method = methods[i];
            if (!method.getName().equals(name) || (paramTypes = method.getParameterTypes()).length != 2 || !paramTypes[1].isAssignableFrom(ElemExtensionCall.class) || !paramTypes[0].isAssignableFrom(XSLProcessorContext.class)) continue;
            if (++bestScoreCount != 1) break;
            bestMethod = method;
        }
        if (null == bestMethod) {
            throw new NoSuchMethodException(MethodResolver.errString("element", "method", classObj, name, 0, null));
        }
        if (bestScoreCount > 1) {
            throw new TransformerException(XSLMessages.createMessage("ER_MORE_MATCH_ELEMENT", new Object[]{name}));
        }
        return bestMethod;
    }

    public static void convertParams(Object[] argsIn, Object[][] argsOut, Class[] paramTypes, ExpressionContext exprContext) throws TransformerException {
        if (paramTypes == null) {
            argsOut[0] = null;
        } else {
            int nParams = paramTypes.length;
            argsOut[0] = new Object[nParams];
            int paramIndex = 0;
            if (nParams > 0 && ExpressionContext.class.isAssignableFrom(paramTypes[0])) {
                argsOut[0][0] = exprContext;
                ++paramIndex;
            }
            if (argsIn != null) {
                int i = argsIn.length - nParams + paramIndex;
                while (paramIndex < nParams) {
                    argsOut[0][paramIndex] = MethodResolver.convert(argsIn[i], paramTypes[paramIndex]);
                    ++i;
                    ++paramIndex;
                }
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static int scoreMatch(Class[] javaParamTypes, int javaParamsStart, Object[] xsltArgs, int score) {
        if (xsltArgs == null || javaParamTypes == null) {
            return score;
        }
        int nParams = xsltArgs.length;
        int i = nParams - javaParamTypes.length + javaParamsStart;
        int javaParamTypesIndex = javaParamsStart;
        while (i < nParams) {
            block4: {
                Class<?> realClass;
                Class javaClass;
                block7: {
                    Object xsltObj;
                    block5: {
                        block6: {
                            int k;
                            int xsltClassType;
                            block3: {
                                xsltObj = xsltArgs[i];
                                xsltClassType = xsltObj instanceof XObject ? ((XObject)xsltObj).getType() : 0;
                                javaClass = javaParamTypes[javaParamTypesIndex];
                                if (xsltClassType != -1) break block3;
                                if (javaClass.isPrimitive()) return -1;
                                score += 10;
                                break block4;
                            }
                            ConversionInfo[] convInfo = m_conversions[xsltClassType];
                            int nConversions = convInfo.length;
                            for (k = 0; k < nConversions; ++k) {
                                ConversionInfo cinfo = convInfo[k];
                                if (!javaClass.isAssignableFrom(cinfo.m_class)) continue;
                                score += cinfo.m_score;
                                break;
                            }
                            if (k != nConversions) break block4;
                            if (0 != xsltClassType) return -1;
                            realClass = null;
                            if (!(xsltObj instanceof XObject)) break block5;
                            Object realObj = ((XObject)xsltObj).object();
                            if (null == realObj) break block6;
                            realClass = realObj.getClass();
                            break block7;
                        }
                        score += 10;
                        break block4;
                    }
                    realClass = xsltObj.getClass();
                }
                if (!javaClass.isAssignableFrom(realClass)) return -1;
                score += 0;
            }
            ++i;
            ++javaParamTypesIndex;
        }
        return score;
    }

    static Object convert(Object xsltObj, Class javaClass) throws TransformerException {
        if (xsltObj instanceof XObject) {
            XObject xobj = (XObject)xsltObj;
            int xsltClassType = xobj.getType();
            switch (xsltClassType) {
                case -1: {
                    return null;
                }
                case 1: {
                    if (javaClass == String.class) {
                        return xobj.str();
                    }
                    return xobj.bool() ? Boolean.TRUE : Boolean.FALSE;
                }
                case 2: {
                    if (javaClass == String.class) {
                        return xobj.str();
                    }
                    if (javaClass == Boolean.TYPE) {
                        return xobj.bool() ? Boolean.TRUE : Boolean.FALSE;
                    }
                    return MethodResolver.convertDoubleToNumber(xobj.num(), javaClass);
                }
                case 3: {
                    if (javaClass == String.class || javaClass == Object.class) {
                        return xobj.str();
                    }
                    if (javaClass == Character.TYPE) {
                        String str = xobj.str();
                        if (str.length() > 0) {
                            return new Character(str.charAt(0));
                        }
                        return null;
                    }
                    if (javaClass == Boolean.TYPE) {
                        return xobj.bool() ? Boolean.TRUE : Boolean.FALSE;
                    }
                    return MethodResolver.convertDoubleToNumber(xobj.num(), javaClass);
                }
                case 5: {
                    int rootHandle;
                    if (javaClass == NodeIterator.class || javaClass == Object.class) {
                        DTMIterator dtmIter = ((XRTreeFrag)xobj).asNodeIterator();
                        return new DTMNodeIterator(dtmIter);
                    }
                    if (javaClass == NodeList.class) {
                        return ((XRTreeFrag)xobj).convertToNodeset();
                    }
                    if (javaClass == Node.class) {
                        DTMIterator iter = ((XRTreeFrag)xobj).asNodeIterator();
                        int rootHandle2 = iter.nextNode();
                        DTM dtm = iter.getDTM(rootHandle2);
                        return dtm.getNode(dtm.getFirstChild(rootHandle2));
                    }
                    if (javaClass == String.class) {
                        return xobj.str();
                    }
                    if (javaClass == Boolean.TYPE) {
                        return xobj.bool() ? Boolean.TRUE : Boolean.FALSE;
                    }
                    if (javaClass.isPrimitive()) {
                        return MethodResolver.convertDoubleToNumber(xobj.num(), javaClass);
                    }
                    DTMIterator iter = ((XRTreeFrag)xobj).asNodeIterator();
                    DTM dtm = iter.getDTM(rootHandle = iter.nextNode());
                    Node child = dtm.getNode(dtm.getFirstChild(rootHandle));
                    if (javaClass.isAssignableFrom(child.getClass())) {
                        return child;
                    }
                    return null;
                }
                case 4: {
                    int childHandle;
                    if (javaClass == NodeIterator.class || javaClass == Object.class) {
                        return xobj.nodeset();
                    }
                    if (javaClass == NodeList.class) {
                        return xobj.nodelist();
                    }
                    if (javaClass == Node.class) {
                        DTMIterator ni = xobj.iter();
                        int handle = ni.nextNode();
                        if (handle != -1) {
                            return ni.getDTM(handle).getNode(handle);
                        }
                        return null;
                    }
                    if (javaClass == String.class) {
                        return xobj.str();
                    }
                    if (javaClass == Boolean.TYPE) {
                        return xobj.bool() ? Boolean.TRUE : Boolean.FALSE;
                    }
                    if (javaClass.isPrimitive()) {
                        return MethodResolver.convertDoubleToNumber(xobj.num(), javaClass);
                    }
                    DTMIterator iter = xobj.iter();
                    DTM dtm = iter.getDTM(childHandle = iter.nextNode());
                    Node child = dtm.getNode(childHandle);
                    if (javaClass.isAssignableFrom(child.getClass())) {
                        return child;
                    }
                    return null;
                }
            }
            xsltObj = xobj.object();
        }
        if (null != xsltObj) {
            if (javaClass == String.class) {
                return xsltObj.toString();
            }
            if (javaClass.isPrimitive()) {
                XString xstr = new XString(xsltObj.toString());
                double num = xstr.num();
                return MethodResolver.convertDoubleToNumber(num, javaClass);
            }
            if (javaClass == Class.class) {
                return xsltObj.getClass();
            }
            return xsltObj;
        }
        return xsltObj;
    }

    static Object convertDoubleToNumber(double num, Class javaClass) {
        if (javaClass == Double.TYPE || javaClass == Double.class) {
            return new Double(num);
        }
        if (javaClass == Float.TYPE) {
            return new Float(num);
        }
        if (javaClass == Long.TYPE) {
            return new Long((long)num);
        }
        if (javaClass == Integer.TYPE) {
            return new Integer((int)num);
        }
        if (javaClass == Short.TYPE) {
            return new Short((short)num);
        }
        if (javaClass == Character.TYPE) {
            return new Character((char)num);
        }
        if (javaClass == Byte.TYPE) {
            return new Byte((byte)num);
        }
        return new Double(num);
    }

    private static String errString(String callType, String searchType, Class classObj, String funcName, int searchMethod, Object[] xsltArgs) {
        String resultString = "For extension " + callType + ", could not find " + searchType + " ";
        switch (searchMethod) {
            case 1: {
                return resultString + "static " + classObj.getName() + "." + funcName + "([ExpressionContext,] " + MethodResolver.errArgs(xsltArgs, 0) + ").";
            }
            case 2: {
                return resultString + classObj.getName() + "." + funcName + "([ExpressionContext,] " + MethodResolver.errArgs(xsltArgs, 0) + ").";
            }
            case 3: {
                return resultString + classObj.getName() + "." + funcName + "([ExpressionContext,] " + MethodResolver.errArgs(xsltArgs, 0) + ").\nChecked both static and instance methods.";
            }
            case 4: {
                return resultString + "static " + classObj.getName() + "." + funcName + "([ExpressionContext, ]" + MethodResolver.errArgs(xsltArgs, 0) + ") nor\n" + classObj + "." + funcName + "([ExpressionContext,] " + MethodResolver.errArgs(xsltArgs, 1) + ").";
            }
        }
        if (callType.equals("function")) {
            return resultString + classObj.getName() + "([ExpressionContext,] " + MethodResolver.errArgs(xsltArgs, 0) + ").";
        }
        return resultString + classObj.getName() + "." + funcName + "(org.apache.xalan.extensions.XSLProcessorContext, org.apache.xalan.templates.ElemExtensionCall).";
    }

    private static String errArgs(Object[] xsltArgs, int startingArg) {
        StringBuffer returnArgs = new StringBuffer();
        for (int i = startingArg; i < xsltArgs.length; ++i) {
            if (i != startingArg) {
                returnArgs.append(", ");
            }
            if (xsltArgs[i] instanceof XObject) {
                returnArgs.append(((XObject)xsltArgs[i]).getTypeString());
                continue;
            }
            returnArgs.append(xsltArgs[i].getClass().getName());
        }
        return returnArgs.toString();
    }

    static class ConversionInfo {
        Class m_class;
        int m_score;

        ConversionInfo(Class cl, int score) {
            this.m_class = cl;
            this.m_score = score;
        }
    }
}

