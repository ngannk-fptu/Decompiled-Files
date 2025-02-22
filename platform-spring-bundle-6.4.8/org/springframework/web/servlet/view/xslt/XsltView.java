/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view.xslt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.SimpleTransformErrorListener;
import org.springframework.util.xml.TransformerUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XsltView
extends AbstractUrlBasedView {
    @Nullable
    private Class<? extends TransformerFactory> transformerFactoryClass;
    @Nullable
    private String sourceKey;
    @Nullable
    private URIResolver uriResolver;
    private ErrorListener errorListener;
    private boolean indent;
    @Nullable
    private Properties outputProperties;
    private boolean cacheTemplates;
    @Nullable
    private TransformerFactory transformerFactory;
    @Nullable
    private Templates cachedTemplates;

    public XsltView() {
        this.errorListener = new SimpleTransformErrorListener(this.logger);
        this.indent = true;
        this.cacheTemplates = true;
    }

    public void setTransformerFactoryClass(Class<? extends TransformerFactory> transformerFactoryClass) {
        this.transformerFactoryClass = transformerFactoryClass;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public void setUriResolver(URIResolver uriResolver) {
        this.uriResolver = uriResolver;
    }

    public void setErrorListener(@Nullable ErrorListener errorListener) {
        this.errorListener = errorListener != null ? errorListener : new SimpleTransformErrorListener(this.logger);
    }

    public void setIndent(boolean indent) {
        this.indent = indent;
    }

    public void setOutputProperties(Properties outputProperties) {
        this.outputProperties = outputProperties;
    }

    public void setCacheTemplates(boolean cacheTemplates) {
        this.cacheTemplates = cacheTemplates;
    }

    @Override
    protected void initApplicationContext() throws BeansException {
        this.transformerFactory = this.newTransformerFactory(this.transformerFactoryClass);
        this.transformerFactory.setErrorListener(this.errorListener);
        if (this.uriResolver != null) {
            this.transformerFactory.setURIResolver(this.uriResolver);
        }
        if (this.cacheTemplates) {
            this.cachedTemplates = this.loadTemplates();
        }
    }

    protected TransformerFactory newTransformerFactory(@Nullable Class<? extends TransformerFactory> transformerFactoryClass) {
        if (transformerFactoryClass != null) {
            try {
                return ReflectionUtils.accessibleConstructor(transformerFactoryClass, new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception ex) {
                throw new TransformerFactoryConfigurationError(ex, "Could not instantiate TransformerFactory");
            }
        }
        return TransformerFactory.newInstance();
    }

    protected final TransformerFactory getTransformerFactory() {
        Assert.state(this.transformerFactory != null, "No TransformerFactory available");
        return this.transformerFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Templates templates = this.cachedTemplates;
        if (templates == null) {
            templates = this.loadTemplates();
        }
        Transformer transformer = this.createTransformer(templates);
        this.configureTransformer(model, response, transformer);
        this.configureResponse(model, response, transformer);
        Source source = null;
        try {
            source = this.locateSource(model);
            if (source == null) {
                throw new IllegalArgumentException("Unable to locate Source object in model: " + model);
            }
            transformer.transform(source, this.createResult(response));
        }
        finally {
            this.closeSourceIfNecessary(source);
        }
    }

    protected Result createResult(HttpServletResponse response) throws Exception {
        return new StreamResult((OutputStream)response.getOutputStream());
    }

    @Nullable
    protected Source locateSource(Map<String, Object> model) throws Exception {
        if (this.sourceKey != null) {
            return this.convertSource(model.get(this.sourceKey));
        }
        Object source = CollectionUtils.findValueOfType(model.values(), this.getSourceTypes());
        return source != null ? this.convertSource(source) : null;
    }

    protected Class<?>[] getSourceTypes() {
        return new Class[]{Source.class, Document.class, Node.class, Reader.class, InputStream.class, Resource.class};
    }

    protected Source convertSource(Object source) throws Exception {
        if (source instanceof Source) {
            return (Source)source;
        }
        if (source instanceof Document) {
            return new DOMSource(((Document)source).getDocumentElement());
        }
        if (source instanceof Node) {
            return new DOMSource((Node)source);
        }
        if (source instanceof Reader) {
            return new StreamSource((Reader)source);
        }
        if (source instanceof InputStream) {
            return new StreamSource((InputStream)source);
        }
        if (source instanceof Resource) {
            Resource resource = (Resource)source;
            return new StreamSource(resource.getInputStream(), resource.getURI().toASCIIString());
        }
        throw new IllegalArgumentException("Value '" + source + "' cannot be converted to XSLT Source");
    }

    protected void configureTransformer(Map<String, Object> model, HttpServletResponse response, Transformer transformer) {
        this.copyModelParameters(model, transformer);
        this.copyOutputProperties(transformer);
        this.configureIndentation(transformer);
    }

    protected final void configureIndentation(Transformer transformer) {
        if (this.indent) {
            TransformerUtils.enableIndenting(transformer);
        } else {
            TransformerUtils.disableIndenting(transformer);
        }
    }

    protected final void copyOutputProperties(Transformer transformer) {
        if (this.outputProperties != null) {
            Enumeration<?> en = this.outputProperties.propertyNames();
            while (en.hasMoreElements()) {
                String name = (String)en.nextElement();
                transformer.setOutputProperty(name, this.outputProperties.getProperty(name));
            }
        }
    }

    protected final void copyModelParameters(Map<String, Object> model, Transformer transformer) {
        model.forEach(transformer::setParameter);
    }

    protected void configureResponse(Map<String, Object> model, HttpServletResponse response, Transformer transformer) {
        String contentType = this.getContentType();
        String mediaType = transformer.getOutputProperty("media-type");
        String encoding = transformer.getOutputProperty("encoding");
        if (StringUtils.hasText(mediaType)) {
            contentType = mediaType;
        }
        if (StringUtils.hasText(encoding) && contentType != null && !contentType.toLowerCase().contains(";charset=")) {
            contentType = contentType + ";charset=" + encoding;
        }
        response.setContentType(contentType);
    }

    private Templates loadTemplates() throws ApplicationContextException {
        Source stylesheetSource = this.getStylesheetSource();
        try {
            Templates templates;
            Templates templates2 = templates = this.getTransformerFactory().newTemplates(stylesheetSource);
            return templates2;
        }
        catch (TransformerConfigurationException ex) {
            throw new ApplicationContextException("Can't load stylesheet from '" + this.getUrl() + "'", ex);
        }
        finally {
            this.closeSourceIfNecessary(stylesheetSource);
        }
    }

    protected Transformer createTransformer(Templates templates) throws TransformerConfigurationException {
        Transformer transformer = templates.newTransformer();
        if (this.uriResolver != null) {
            transformer.setURIResolver(this.uriResolver);
        }
        return transformer;
    }

    protected Source getStylesheetSource() {
        String url = this.getUrl();
        Assert.state(url != null, "'url' not set");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Applying stylesheet [" + url + "]"));
        }
        try {
            Resource resource = this.obtainApplicationContext().getResource(url);
            return new StreamSource(resource.getInputStream(), resource.getURI().toASCIIString());
        }
        catch (IOException ex) {
            throw new ApplicationContextException("Can't load XSLT stylesheet from '" + url + "'", ex);
        }
    }

    private void closeSourceIfNecessary(@Nullable Source source) {
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource)source;
            if (streamSource.getReader() != null) {
                try {
                    streamSource.getReader().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            if (streamSource.getInputStream() != null) {
                try {
                    streamSource.getInputStream().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
    }
}

