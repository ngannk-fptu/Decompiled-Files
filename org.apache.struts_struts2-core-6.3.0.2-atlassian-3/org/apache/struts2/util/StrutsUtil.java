/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.util.ListEntry;
import org.apache.struts2.views.util.UrlHelper;

public class StrutsUtil {
    protected static final Logger LOG = LogManager.getLogger(StrutsUtil.class);
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Map<String, Class<?>> classes = new HashMap();
    protected ValueStack stack;
    private final UrlHelper urlHelper;
    private final ObjectFactory objectFactory;

    public StrutsUtil(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        this.stack = stack;
        this.request = request;
        this.response = response;
        this.urlHelper = stack.getActionContext().getContainer().getInstance(UrlHelper.class);
        this.objectFactory = stack.getActionContext().getContainer().getInstance(ObjectFactory.class);
    }

    public Object bean(Object name) throws Exception {
        String className = name.toString();
        Class clazz = this.classes.get(className);
        if (clazz == null) {
            clazz = ClassLoaderUtil.loadClass(className, StrutsUtil.class);
            this.classes.put(className, clazz);
        }
        return this.objectFactory.buildBean(clazz, this.stack.getContext());
    }

    public boolean isTrue(String expression) {
        Boolean retVal = (Boolean)this.stack.findValue(expression, Boolean.class);
        return retVal != null && retVal != false;
    }

    public Object findString(String name) {
        return this.stack.findValue(name, String.class);
    }

    public String include(Object aName) throws Exception {
        RequestDispatcher dispatcher = this.request.getRequestDispatcher(aName.toString());
        if (dispatcher == null) {
            throw new IllegalArgumentException("Cannot find included file " + aName);
        }
        ResponseWrapper responseWrapper = new ResponseWrapper(this.response);
        dispatcher.include((ServletRequest)this.request, (ServletResponse)responseWrapper);
        return responseWrapper.getData();
    }

    public String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            LOG.debug(MessageFormat.format("Cannot encode URL [{0}]", s), (Throwable)e);
            return s;
        }
    }

    public String buildUrl(String url) {
        return this.urlHelper.buildUrl(url, this.request, this.response, null);
    }

    public Object findValue(String expression, String className) throws ClassNotFoundException {
        return this.stack.findValue(expression, Class.forName(className));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object findValue(String expr, Object context) {
        this.stack.push(context);
        try {
            Object object = this.stack.findValue(expr, true);
            return object;
        }
        finally {
            this.stack.pop();
        }
    }

    public String getText(String text) {
        return (String)this.stack.findValue("getText('" + text.replace('\'', '\"') + "')");
    }

    public String getContext() {
        return this.request == null ? "" : this.request.getContextPath();
    }

    public String translateVariables(String expression) {
        return TextParseUtil.translateVariables('%', expression, this.stack);
    }

    public List<ListEntry> makeSelectList(String selectedList, String list, String listKey, String listValue) {
        ArrayList<ListEntry> selectList = new ArrayList<ListEntry>();
        Collection items = (Collection)this.stack.findValue(list);
        if (items == null) {
            return selectList;
        }
        Collection selectedItems = this.getSelectedItems(selectedList);
        for (Object element : items) {
            Object key = this.computeKey(listKey, element);
            Object value = this.computeValue(listValue, element);
            boolean isSelected = value != null && selectedItems.contains(value);
            selectList.add(new ListEntry(key, value, isSelected));
        }
        return selectList;
    }

    private Collection getSelectedItems(String selectedListName) {
        Object i = this.stack.findValue(selectedListName);
        if (i == null) {
            return Collections.emptyList();
        }
        if (i.getClass().isArray()) {
            return Arrays.asList((Object[])i);
        }
        if (i instanceof Collection) {
            return (Collection)i;
        }
        return Collections.singletonList(i);
    }

    private Object computeKey(String listKey, Object element) {
        if (listKey == null || listKey.isEmpty()) {
            return element;
        }
        return this.findValue(listKey, element);
    }

    private Object computeValue(String listValue, Object element) {
        if (listValue == null || listValue.isEmpty()) {
            return element;
        }
        return this.findValue(listValue, element);
    }

    public int toInt(long aLong) {
        return (int)aLong;
    }

    public long toLong(int anInt) {
        return anInt;
    }

    public long toLong(String aLong) {
        if (aLong == null || aLong.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(aLong);
    }

    public String toString(long aLong) {
        return Long.toString(aLong);
    }

    public String toString(int anInt) {
        return Integer.toString(anInt);
    }

    public String toStringSafe(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    static class ServletOutputStreamWrapper
    extends ServletOutputStream {
        StringWriter writer;

        ServletOutputStreamWrapper(StringWriter aWriter) {
            this.writer = aWriter;
        }

        public void write(int aByte) {
            this.writer.write(aByte);
        }

        public boolean isReady() {
            return true;
        }

        public void setWriteListener(WriteListener writeListener) {
            try {
                writeListener.onWritePossible();
            }
            catch (IOException e) {
                throw new StrutsException(e);
            }
        }
    }

    static class ResponseWrapper
    extends HttpServletResponseWrapper {
        StringWriter strout = new StringWriter();
        PrintWriter writer;
        ServletOutputStream sout = new ServletOutputStreamWrapper(this.strout);

        ResponseWrapper(HttpServletResponse aResponse) {
            super(aResponse);
            this.writer = new PrintWriter(this.strout);
        }

        public String getData() {
            this.writer.flush();
            return this.strout.toString();
        }

        public ServletOutputStream getOutputStream() {
            return this.sout;
        }

        public PrintWriter getWriter() throws IOException {
            return this.writer;
        }
    }
}

