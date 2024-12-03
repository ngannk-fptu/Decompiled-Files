/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.result.StrutsResultSupport;

public class StreamResult
extends StrutsResultSupport {
    private static final long serialVersionUID = -1468409635999059850L;
    protected static final Logger LOG = LogManager.getLogger(StreamResult.class);
    public static final String DEFAULT_PARAM = "inputName";
    protected String contentType = "text/plain";
    protected String contentLength;
    protected String contentDisposition = "inline";
    protected String contentCharSet;
    protected String inputName = "inputStream";
    protected InputStream inputStream;
    protected int bufferSize = 1024;
    protected boolean allowCaching = true;
    private NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns;

    public StreamResult() {
    }

    public StreamResult(InputStream in) {
        this.inputStream = in;
    }

    @Inject
    public void setNotExcludedAcceptedPatterns(NotExcludedAcceptedPatternsChecker notExcludedAcceptedPatterns) {
        this.notExcludedAcceptedPatterns = notExcludedAcceptedPatterns;
    }

    public boolean getAllowCaching() {
        return this.allowCaching;
    }

    public void setAllowCaching(boolean allowCaching) {
        this.allowCaching = allowCaching;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentDisposition() {
        return this.contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public String getContentCharSet() {
        return this.contentCharSet;
    }

    public void setContentCharSet(String contentCharSet) {
        this.contentCharSet = contentCharSet;
    }

    public String getInputName() {
        return this.inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        LOG.debug("Find the Response in context");
        OutputStream oOutput = null;
        try {
            int iSize;
            boolean reevaluate;
            String parsedInputName = this.conditionalParse(this.inputName, invocation);
            boolean evaluated = parsedInputName != null && !parsedInputName.equals(this.inputName);
            boolean bl = reevaluate = !evaluated || this.isAcceptableExpression(parsedInputName);
            if (this.inputStream == null && reevaluate) {
                LOG.debug("Find the inputstream from the invocation variable stack");
                this.inputStream = (InputStream)invocation.getStack().findValue(parsedInputName);
            }
            if (this.inputStream == null) {
                String msg = "Can not find a java.io.InputStream with the name [" + parsedInputName + "] in the invocation stack. Check the <param name=\"inputName\"> tag specified for this action is correct, not excluded and accepted.";
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }
            HttpServletResponse oResponse = invocation.getInvocationContext().getServletResponse();
            LOG.debug("Set the content type: {};charset{}", (Object)this.contentType, (Object)this.contentCharSet);
            if (this.contentCharSet != null && !this.contentCharSet.equals("")) {
                oResponse.setContentType(this.conditionalParse(this.contentType, invocation) + ";charset=" + this.conditionalParse(this.contentCharSet, invocation));
            } else {
                oResponse.setContentType(this.conditionalParse(this.contentType, invocation));
            }
            LOG.debug("Set the content length: {}", (Object)this.contentLength);
            if (this.contentLength != null) {
                String translatedContentLength = this.conditionalParse(this.contentLength, invocation);
                try {
                    int contentLengthAsInt = Integer.parseInt(translatedContentLength);
                    if (contentLengthAsInt >= 0) {
                        oResponse.setContentLength(contentLengthAsInt);
                    }
                }
                catch (NumberFormatException e) {
                    LOG.warn("failed to recognize {} as a number, contentLength header will not be set", (Object)translatedContentLength, (Object)e);
                }
            }
            LOG.debug("Set the content-disposition: {}", (Object)this.contentDisposition);
            if (this.contentDisposition != null) {
                oResponse.addHeader("Content-Disposition", this.conditionalParse(this.contentDisposition, invocation));
            }
            LOG.debug("Set the cache control headers if necessary: {}", (Object)this.allowCaching);
            if (!this.allowCaching) {
                oResponse.addHeader("Pragma", "no-cache");
                oResponse.addHeader("Cache-Control", "no-cache");
            }
            oOutput = oResponse.getOutputStream();
            LOG.debug("Streaming result [{}] type=[{}] length=[{}] content-disposition=[{}] charset=[{}]", (Object)this.inputName, (Object)this.contentType, (Object)this.contentLength, (Object)this.contentDisposition, (Object)this.contentCharSet);
            LOG.debug("Streaming to output buffer +++ START +++");
            byte[] oBuff = new byte[this.bufferSize];
            while (-1 != (iSize = this.inputStream.read(oBuff))) {
                LOG.debug("Sending stream ... {}", (Object)iSize);
                oOutput.write(oBuff, 0, iSize);
            }
            LOG.debug("Streaming to output buffer +++ END +++");
            oOutput.flush();
        }
        finally {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (oOutput != null) {
                oOutput.close();
            }
        }
    }

    protected boolean isAcceptableExpression(String expression) {
        NotExcludedAcceptedPatternsChecker.IsAllowed isAllowed = this.notExcludedAcceptedPatterns.isAllowed(expression);
        if (isAllowed.isAllowed()) {
            return true;
        }
        LOG.warn("Expression [{}] isn't allowed by pattern [{}]! See Accepted / Excluded patterns at\nhttps://struts.apache.org/security/", (Object)expression, (Object)isAllowed.getAllowedPattern());
        return false;
    }
}

