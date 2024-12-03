/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.util.MetadataUtil;
import com.sun.xml.ws.util.pipe.AbstractSchemaValidationTube;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public class ClientSchemaValidationTube
extends AbstractSchemaValidationTube {
    private static final Logger LOGGER = Logger.getLogger(ClientSchemaValidationTube.class.getName());
    private final Schema schema;
    private final Validator validator;
    private final boolean noValidation;
    private final WSDLPort port;

    public ClientSchemaValidationTube(WSBinding binding, WSDLPort port, Tube next) {
        super(binding, next);
        this.port = port;
        if (port != null) {
            Source[] sources;
            String primaryWsdl = port.getOwner().getParent().getLocation().getSystemId();
            AbstractSchemaValidationTube.MetadataResolverImpl mdresolver = new AbstractSchemaValidationTube.MetadataResolverImpl();
            Map<String, SDDocument> docs = MetadataUtil.getMetadataClosure(primaryWsdl, mdresolver, true);
            mdresolver = new AbstractSchemaValidationTube.MetadataResolverImpl(docs.values());
            for (Source source : sources = this.getSchemaSources(docs.values(), mdresolver)) {
                LOGGER.fine("Constructing client validation schema from = " + source.getSystemId());
            }
            if (sources.length != 0) {
                this.noValidation = false;
                this.sf.setResourceResolver(mdresolver);
                try {
                    this.schema = this.sf.newSchema(sources);
                }
                catch (SAXException e) {
                    throw new WebServiceException((Throwable)e);
                }
                this.validator = this.schema.newValidator();
                return;
            }
        }
        this.noValidation = true;
        this.schema = null;
        this.validator = null;
    }

    @Override
    protected Validator getValidator() {
        return this.validator;
    }

    @Override
    protected boolean isNoValidation() {
        return this.noValidation;
    }

    protected ClientSchemaValidationTube(ClientSchemaValidationTube that, TubeCloner cloner) {
        super(that, cloner);
        this.port = that.port;
        this.schema = that.schema;
        this.validator = this.schema.newValidator();
        this.noValidation = that.noValidation;
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new ClientSchemaValidationTube(this, cloner);
    }

    @Override
    public NextAction processRequest(Packet request) {
        if (this.isNoValidation() || !this.feature.isOutbound() || !request.getMessage().hasPayload() || request.getMessage().isFault()) {
            return super.processRequest(request);
        }
        try {
            this.doProcess(request);
        }
        catch (SAXException se) {
            throw new WebServiceException((Throwable)se);
        }
        return super.processRequest(request);
    }

    @Override
    public NextAction processResponse(Packet response) {
        if (this.isNoValidation() || !this.feature.isInbound() || response.getMessage() == null || !response.getMessage().hasPayload() || response.getMessage().isFault()) {
            return super.processResponse(response);
        }
        try {
            this.doProcess(response);
        }
        catch (SAXException se) {
            throw new WebServiceException((Throwable)se);
        }
        return super.processResponse(response);
    }
}

