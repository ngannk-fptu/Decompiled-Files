/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serialize.HTMLSerializer
 *  org.apache.xml.serialize.OutputFormat
 */
package org.owasp.validator.html.scan;

import com.atlassian.xhtml.serialize.AllowCDataSectionXHTMLSerializer;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.scan.ASHTMLSerializer;
import org.owasp.validator.html.util.ErrorMessageUtil;

public abstract class AbstractAntiSamyScanner {
    protected final InternalPolicy policy;
    protected final List<String> errorMessages = new ArrayList<String>();
    protected static final ResourceBundle messages = AbstractAntiSamyScanner.getResourceBundle();
    protected final Locale locale = Locale.getDefault();
    protected boolean isNofollowAnchors = false;
    protected boolean isNoopenerAndNoreferrerAnchors = false;
    protected boolean isValidateParamAsEmbed = false;

    public abstract CleanResults scan(String var1) throws ScanException;

    public abstract CleanResults getResults();

    public AbstractAntiSamyScanner(Policy policy) {
        assert (policy instanceof InternalPolicy) : policy.getClass();
        this.policy = (InternalPolicy)policy;
    }

    public AbstractAntiSamyScanner() throws PolicyException {
        this.policy = (InternalPolicy)Policy.getInstance();
    }

    private static ResourceBundle getResourceBundle() {
        try {
            return ResourceBundle.getBundle("AntiSamy", Locale.getDefault());
        }
        catch (MissingResourceException mre) {
            return ResourceBundle.getBundle("AntiSamy", new Locale("en", "US"));
        }
    }

    protected void addError(String errorKey, Object[] objs) {
        this.errorMessages.add(ErrorMessageUtil.getMessage(messages, errorKey, objs));
    }

    protected OutputFormat getOutputFormat() {
        OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(this.policy.isOmitXmlDeclaration());
        format.setOmitDocumentType(this.policy.isOmitDoctypeDeclaration());
        format.setPreserveEmptyAttributes(true);
        format.setPreserveSpace(this.policy.isPreserveSpace());
        if (this.policy.isFormatOutput()) {
            format.setLineWidth(80);
            format.setIndenting(true);
            format.setIndent(2);
        }
        return format;
    }

    protected HTMLSerializer getHTMLSerializer(Writer w, OutputFormat format) {
        Object serializer = this.policy.isUseXhtml() ? new AllowCDataSectionXHTMLSerializer(w, format, this.policy) : new ASHTMLSerializer(w, format, this.policy);
        return serializer;
    }

    protected String trim(String original, String cleaned) {
        if (cleaned.endsWith("\n") && !original.endsWith("\n")) {
            if (cleaned.endsWith("\r\n")) {
                cleaned = cleaned.substring(0, cleaned.length() - 2);
            } else if (cleaned.endsWith("\n")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
        }
        return cleaned;
    }
}

