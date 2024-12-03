/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.fault.SOAPFaultBuilder;
import com.sun.xml.ws.util.pipe.AbstractSchemaValidationTube;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public class ServerSchemaValidationTube
extends AbstractSchemaValidationTube {
    private static final Logger LOGGER = Logger.getLogger(ServerSchemaValidationTube.class.getName());
    private final Schema schema;
    private final Validator validator;
    private final boolean noValidation;
    private final SEIModel seiModel;
    private final WSDLPort wsdlPort;

    public ServerSchemaValidationTube(WSEndpoint endpoint, WSBinding binding, SEIModel seiModel, WSDLPort wsdlPort, Tube next) {
        super(binding, next);
        this.seiModel = seiModel;
        this.wsdlPort = wsdlPort;
        if (endpoint.getServiceDefinition() != null) {
            Source[] sources;
            AbstractSchemaValidationTube.MetadataResolverImpl mdresolver = new AbstractSchemaValidationTube.MetadataResolverImpl(endpoint.getServiceDefinition());
            for (Source source : sources = this.getSchemaSources(endpoint.getServiceDefinition(), mdresolver)) {
                LOGGER.fine("Constructing service validation schema from = " + source.getSystemId());
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

    @Override
    public NextAction processRequest(Packet request) {
        if (this.isNoValidation() || !this.feature.isInbound() || !request.getMessage().hasPayload() || request.getMessage().isFault()) {
            return super.processRequest(request);
        }
        try {
            this.doProcess(request);
        }
        catch (SAXException se) {
            LOGGER.log(Level.WARNING, "Client Request doesn't pass Service's Schema Validation", se);
            SOAPVersion soapVersion = this.binding.getSOAPVersion();
            Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, se, soapVersion.faultCodeClient);
            return this.doReturnWith(request.createServerResponse(faultMsg, this.wsdlPort, this.seiModel, this.binding));
        }
        return super.processRequest(request);
    }

    @Override
    public NextAction processResponse(Packet response) {
        if (this.isNoValidation() || !this.feature.isOutbound() || response.getMessage() == null || !response.getMessage().hasPayload() || response.getMessage().isFault()) {
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

    protected ServerSchemaValidationTube(ServerSchemaValidationTube that, TubeCloner cloner) {
        super(that, cloner);
        this.schema = that.schema;
        this.validator = this.schema.newValidator();
        this.noValidation = that.noValidation;
        this.seiModel = that.seiModel;
        this.wsdlPort = that.wsdlPort;
    }

    @Override
    public AbstractTubeImpl copy(TubeCloner cloner) {
        return new ServerSchemaValidationTube(this, cloner);
    }
}

