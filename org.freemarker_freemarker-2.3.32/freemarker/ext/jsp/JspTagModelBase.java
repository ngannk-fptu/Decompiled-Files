/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.jsp;

import freemarker.core._DelayedJQuote;
import freemarker.core._DelayedShortClassName;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jsp.SimpleTagDirectiveModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class JspTagModelBase {
    protected final String tagName;
    private final Class tagClass;
    private final Method dynaSetter;
    private final Map propertySetters = new HashMap();

    protected JspTagModelBase(String tagName, Class tagClass) throws IntrospectionException {
        Method dynaSetter;
        this.tagName = tagName;
        this.tagClass = tagClass;
        BeanInfo bi = Introspector.getBeanInfo(tagClass);
        PropertyDescriptor[] pda = bi.getPropertyDescriptors();
        for (int i = 0; i < pda.length; ++i) {
            PropertyDescriptor pd = pda[i];
            Method m = pd.getWriteMethod();
            if (m == null) continue;
            this.propertySetters.put(pd.getName(), m);
        }
        try {
            dynaSetter = tagClass.getMethod("setDynamicAttribute", String.class, String.class, Object.class);
        }
        catch (NoSuchMethodException nsme) {
            dynaSetter = null;
        }
        this.dynaSetter = dynaSetter;
    }

    Object getTagInstance() throws IllegalAccessException, InstantiationException {
        return this.tagClass.newInstance();
    }

    void setupTag(Object tag, Map args, ObjectWrapper wrapper) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
        if (args != null && !args.isEmpty()) {
            ObjectWrapperAndUnwrapper unwrapper = wrapper instanceof ObjectWrapperAndUnwrapper ? (ObjectWrapperAndUnwrapper)wrapper : BeansWrapper.getDefaultInstance();
            Object[] argArray = new Object[1];
            for (Map.Entry entry : args.entrySet()) {
                Object arg;
                argArray[0] = arg = unwrapper.unwrap((TemplateModel)entry.getValue());
                Object paramName = entry.getKey();
                Method setterMethod = (Method)this.propertySetters.get(paramName);
                if (setterMethod == null) {
                    if (this.dynaSetter == null) {
                        throw new TemplateModelException("Unknown property " + StringUtil.jQuote(paramName.toString()) + " on instance of " + this.tagClass.getName());
                    }
                    this.dynaSetter.invoke(tag, null, paramName, argArray[0]);
                    continue;
                }
                if (arg instanceof BigDecimal) {
                    argArray[0] = BeansWrapper.coerceBigDecimal((BigDecimal)arg, setterMethod.getParameterTypes()[0]);
                }
                try {
                    setterMethod.invoke(tag, argArray);
                }
                catch (Exception e) {
                    Class<String> setterType = setterMethod.getParameterTypes()[0];
                    _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Failed to set JSP tag parameter ", new _DelayedJQuote(paramName), " (declared type: ", new _DelayedShortClassName(setterType) + ", actual value's type: ", argArray[0] != null ? new _DelayedShortClassName(argArray[0].getClass()) : "Null", "). See cause exception for the more specific cause...");
                    if (e instanceof IllegalArgumentException && !setterType.isAssignableFrom(String.class) && argArray[0] != null && argArray[0] instanceof String) {
                        desc.tip("This problem is often caused by unnecessary parameter quotation. Parameters aren't quoted in FTL, similarly as they aren't quoted in most languages. For example, these parameter assignments are wrong: ", "<@my.tag p1=\"true\" p2=\"10\" p3=\"${someVariable}\" p4=\"${x+1}\" />", ". The correct form is: ", "<@my.tag p1=true p2=10 p3=someVariable p4=x+1 />", ". Only string literals are quoted (regardless of where they occur): ", "<@my.box style=\"info\" message=\"Hello ${name}!\" width=200 />", ".");
                    }
                    throw new _TemplateModelException((Throwable)e, null, desc);
                }
            }
        }
    }

    protected final TemplateModelException toTemplateModelExceptionOrRethrow(Exception e) throws TemplateModelException {
        if (e instanceof RuntimeException && !this.isCommonRuntimeException((RuntimeException)e)) {
            throw (RuntimeException)e;
        }
        if (e instanceof TemplateModelException) {
            throw (TemplateModelException)e;
        }
        if (e instanceof SimpleTagDirectiveModel.TemplateExceptionWrapperJspException) {
            return this.toTemplateModelExceptionOrRethrow(((SimpleTagDirectiveModel.TemplateExceptionWrapperJspException)((Object)e)).getCause());
        }
        return new TemplateModelException("Error while invoking the " + StringUtil.jQuote(this.tagName) + " JSP custom tag; see cause exception", e instanceof TemplateException, e);
    }

    private boolean isCommonRuntimeException(RuntimeException e) {
        Class<?> eClass = e.getClass();
        return eClass == NullPointerException.class || eClass == IllegalArgumentException.class || eClass == ClassCastException.class || eClass == IndexOutOfBoundsException.class;
    }
}

