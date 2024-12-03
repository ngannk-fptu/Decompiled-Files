/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
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
package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Component;
import org.apache.struts2.util.FastByteArrayOutputStream;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="include", tldTagClass="org.apache.struts2.views.jsp.IncludeTag", description="Include a servlet's output (result of servlet or a JSP page)")
public class Include
extends Component {
    private static final Logger LOG = LogManager.getLogger(Include.class);
    private static final String systemEncoding = System.getProperty("file.encoding");
    protected String value;
    private HttpServletRequest req;
    private HttpServletResponse res;
    private String defaultEncoding;
    private boolean useResponseEncoding = true;

    public Include(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    @Inject(value="struts.i18n.encoding")
    public void setDefaultEncoding(String encoding) {
        this.defaultEncoding = encoding;
    }

    @Inject(value="struts.tag.includetag.useResponseEncoding", required=false)
    public void setUseResponseEncoding(String useEncoding) {
        this.useResponseEncoding = Boolean.parseBoolean(useEncoding);
    }

    @Override
    public boolean end(Writer writer, String body) {
        String encodingForInclude;
        String page = this.findString(this.value, "value", "You must specify the URL to include. Example: /foo.jsp");
        StringBuilder urlBuf = new StringBuilder();
        if (this.useResponseEncoding) {
            encodingForInclude = this.res.getCharacterEncoding();
            if (encodingForInclude == null || encodingForInclude.length() == 0) {
                encodingForInclude = this.defaultEncoding;
            }
        } else {
            encodingForInclude = this.defaultEncoding;
        }
        urlBuf.append(page);
        if (this.parameters.size() > 0) {
            urlBuf.append('?');
            String concat = "";
            Iterator iterator = this.parameters.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry next;
                Map.Entry entry = next = iterator.next();
                Object name = entry.getKey();
                List values = (List)entry.getValue();
                for (Object value : values) {
                    urlBuf.append(concat);
                    urlBuf.append(name);
                    urlBuf.append('=');
                    try {
                        urlBuf.append(URLEncoder.encode(value.toString(), "UTF-8"));
                    }
                    catch (UnsupportedEncodingException e) {
                        LOG.warn("Unable to url-encode {}, it will be ignored", value);
                    }
                    concat = "&";
                }
            }
        }
        String result = urlBuf.toString();
        try {
            Include.include(result, writer, (ServletRequest)this.req, this.res, encodingForInclude);
        }
        catch (IOException | ServletException e) {
            LOG.warn("Exception thrown during include of {}", (Object)result, (Object)e);
        }
        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="The jsp/servlet output to include", required=true)
    public void setValue(String value) {
        this.value = value;
    }

    public static String getContextRelativePath(ServletRequest request, String relativePath) {
        String returnValue;
        if (relativePath.startsWith("/")) {
            returnValue = relativePath;
        } else if (!(request instanceof HttpServletRequest)) {
            returnValue = relativePath;
        } else {
            HttpServletRequest hrequest = (HttpServletRequest)request;
            String uri = (String)request.getAttribute("javax.servlet.include.servlet_path");
            if (uri == null) {
                uri = RequestUtils.getServletPath(hrequest);
            }
            returnValue = uri.substring(0, uri.lastIndexOf(47)) + '/' + relativePath;
        }
        if (returnValue.contains("..")) {
            Stack<String> stack = new Stack<String>();
            StringTokenizer pathParts = new StringTokenizer(returnValue.replace('\\', '/'), "/");
            while (pathParts.hasMoreTokens()) {
                String part = pathParts.nextToken();
                if (part.equals(".")) continue;
                if (part.equals("..")) {
                    stack.pop();
                    continue;
                }
                stack.push(part);
            }
            StringBuilder flatPathBuffer = new StringBuilder();
            for (int i = 0; i < stack.size(); ++i) {
                flatPathBuffer.append("/").append(stack.elementAt(i));
            }
            returnValue = flatPathBuffer.toString();
        }
        return returnValue;
    }

    @Override
    public void addParameter(String key, Object value) {
        if (value != null) {
            ArrayList<Object> currentValues = (ArrayList<Object>)this.parameters.get(key);
            if (currentValues == null) {
                currentValues = new ArrayList<Object>();
                this.parameters.put(key, currentValues);
            }
            currentValues.add(value);
        }
    }

    public static void include(String relativePath, Writer writer, ServletRequest request, HttpServletResponse response, String encoding) throws ServletException, IOException {
        String resourcePath = Include.getContextRelativePath(request, relativePath);
        RequestDispatcher rd = request.getRequestDispatcher(resourcePath);
        if (rd == null) {
            throw new ServletException("Not a valid resource path:" + resourcePath);
        }
        PageResponse pageResponse = new PageResponse(response);
        rd.include(request, (ServletResponse)pageResponse);
        if (encoding != null) {
            pageResponse.getContent().writeTo(writer, encoding);
        } else {
            pageResponse.getContent().writeTo(writer, systemEncoding);
        }
    }

    static final class PageResponse
    extends HttpServletResponseWrapper {
        protected PrintWriter pagePrintWriter;
        protected ServletOutputStream outputStream;
        private PageOutputStream pageOutputStream = null;

        public PageResponse(HttpServletResponse response) {
            super(response);
        }

        public FastByteArrayOutputStream getContent() throws IOException {
            if (this.pagePrintWriter != null) {
                this.pagePrintWriter.flush();
            }
            return ((PageOutputStream)this.getOutputStream()).getBuffer();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (this.pageOutputStream == null) {
                this.pageOutputStream = new PageOutputStream();
            }
            return this.pageOutputStream;
        }

        public PrintWriter getWriter() throws IOException {
            if (this.pagePrintWriter == null) {
                this.pagePrintWriter = new PrintWriter(new OutputStreamWriter((OutputStream)this.getOutputStream(), this.getCharacterEncoding()));
            }
            return this.pagePrintWriter;
        }
    }

    static final class PageOutputStream
    extends ServletOutputStream {
        private FastByteArrayOutputStream buffer = new FastByteArrayOutputStream();

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

        public FastByteArrayOutputStream getBuffer() throws IOException {
            this.flush();
            return this.buffer;
        }

        public void close() throws IOException {
            this.buffer.close();
        }

        public void flush() throws IOException {
            this.buffer.flush();
        }

        public void write(byte[] b, int o, int l) throws IOException {
            this.buffer.write(b, o, l);
        }

        public void write(int i) throws IOException {
            this.buffer.write(i);
        }

        public void write(byte[] b) throws IOException {
            this.buffer.write(b);
        }
    }
}

