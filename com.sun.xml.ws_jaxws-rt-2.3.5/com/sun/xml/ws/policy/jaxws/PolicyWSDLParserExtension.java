/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapMutator
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  com.sun.xml.ws.policy.privateutil.PolicyUtils$IO
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModelContext
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion
 *  com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapMutator;
import com.sun.xml.ws.policy.jaxws.BuilderHandlerEndpointScope;
import com.sun.xml.ws.policy.jaxws.BuilderHandlerMessageScope;
import com.sun.xml.ws.policy.jaxws.BuilderHandlerOperationScope;
import com.sun.xml.ws.policy.jaxws.BuilderHandlerServiceScope;
import com.sun.xml.ws.policy.jaxws.PolicyMapBuilder;
import com.sun.xml.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.ws.policy.jaxws.SafePolicyReader;
import com.sun.xml.ws.policy.jaxws.WSDLBoundFaultContainer;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModelContext;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.ws.resources.PolicyMessages;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public final class PolicyWSDLParserExtension
extends WSDLParserExtension {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLParserExtension.class);
    private static final StringBuffer AnonymnousPolicyIdPrefix = new StringBuffer("#__anonymousPolicy__ID");
    private int anonymousPoliciesCount;
    private final SafePolicyReader policyReader = new SafePolicyReader();
    private SafePolicyReader.PolicyRecord expandQueueHead = null;
    private Map<String, SafePolicyReader.PolicyRecord> policyRecordsPassedBy = null;
    private Map<String, PolicySourceModel> anonymousPolicyModels = null;
    private List<String> unresolvedUris = null;
    private final LinkedList<String> urisNeeded = new LinkedList();
    private final Map<String, PolicySourceModel> modelsNeeded = new HashMap<String, PolicySourceModel>();
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4ServiceMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortTypeMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BoundOperationMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OperationMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4MessageMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4InputMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OutputMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4FaultMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingInputOpMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingOutputOpMap = null;
    private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingFaultOpMap = null;
    private PolicyMapBuilder policyBuilder = new PolicyMapBuilder();

    private boolean isPolicyProcessed(String policyUri) {
        return this.modelsNeeded.containsKey(policyUri);
    }

    private void addNewPolicyNeeded(String policyUri, PolicySourceModel policyModel) {
        if (!this.modelsNeeded.containsKey(policyUri)) {
            this.modelsNeeded.put(policyUri, policyModel);
            this.urisNeeded.addFirst(policyUri);
        }
    }

    private Map<String, PolicySourceModel> getPolicyModels() {
        return this.modelsNeeded;
    }

    private Map<String, SafePolicyReader.PolicyRecord> getPolicyRecordsPassedBy() {
        if (null == this.policyRecordsPassedBy) {
            this.policyRecordsPassedBy = new HashMap<String, SafePolicyReader.PolicyRecord>();
        }
        return this.policyRecordsPassedBy;
    }

    private Map<String, PolicySourceModel> getAnonymousPolicyModels() {
        if (null == this.anonymousPolicyModels) {
            this.anonymousPolicyModels = new HashMap<String, PolicySourceModel>();
        }
        return this.anonymousPolicyModels;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4ServiceMap() {
        if (null == this.handlers4ServiceMap) {
            this.handlers4ServiceMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4ServiceMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortMap() {
        if (null == this.handlers4PortMap) {
            this.handlers4PortMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4PortMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortTypeMap() {
        if (null == this.handlers4PortTypeMap) {
            this.handlers4PortTypeMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4PortTypeMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingMap() {
        if (null == this.handlers4BindingMap) {
            this.handlers4BindingMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OperationMap() {
        if (null == this.handlers4OperationMap) {
            this.handlers4OperationMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4OperationMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BoundOperationMap() {
        if (null == this.handlers4BoundOperationMap) {
            this.handlers4BoundOperationMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BoundOperationMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4MessageMap() {
        if (null == this.handlers4MessageMap) {
            this.handlers4MessageMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4MessageMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4InputMap() {
        if (null == this.handlers4InputMap) {
            this.handlers4InputMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4InputMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OutputMap() {
        if (null == this.handlers4OutputMap) {
            this.handlers4OutputMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4OutputMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4FaultMap() {
        if (null == this.handlers4FaultMap) {
            this.handlers4FaultMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4FaultMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingInputOpMap() {
        if (null == this.handlers4BindingInputOpMap) {
            this.handlers4BindingInputOpMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingInputOpMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingOutputOpMap() {
        if (null == this.handlers4BindingOutputOpMap) {
            this.handlers4BindingOutputOpMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingOutputOpMap;
    }

    private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingFaultOpMap() {
        if (null == this.handlers4BindingFaultOpMap) {
            this.handlers4BindingFaultOpMap = new HashMap<WSDLObject, Collection<PolicyRecordHandler>>();
        }
        return this.handlers4BindingFaultOpMap;
    }

    private List<String> getUnresolvedUris(boolean emptyListNeeded) {
        if (null == this.unresolvedUris || emptyListNeeded) {
            this.unresolvedUris = new LinkedList<String>();
        }
        return this.unresolvedUris;
    }

    private void policyRecToExpandQueue(SafePolicyReader.PolicyRecord policyRec) {
        this.expandQueueHead = null == this.expandQueueHead ? policyRec : this.expandQueueHead.insert(policyRec);
    }

    private PolicyRecordHandler readSinglePolicy(SafePolicyReader.PolicyRecord policyRec, boolean inner) {
        PolicyRecordHandler handler = null;
        String policyId = policyRec.policyModel.getPolicyId();
        if (policyId == null) {
            policyId = policyRec.policyModel.getPolicyName();
        }
        if (policyId != null) {
            handler = new PolicyRecordHandler(HandlerType.PolicyUri, policyRec.getUri());
            this.getPolicyRecordsPassedBy().put(policyRec.getUri(), policyRec);
            this.policyRecToExpandQueue(policyRec);
        } else if (inner) {
            String anonymousId = AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString();
            handler = new PolicyRecordHandler(HandlerType.AnonymousPolicyId, anonymousId);
            this.getAnonymousPolicyModels().put(anonymousId, policyRec.policyModel);
            if (null != policyRec.unresolvedURIs) {
                this.getUnresolvedUris(false).addAll(policyRec.unresolvedURIs);
            }
        }
        return handler;
    }

    private void addHandlerToMap(Map<WSDLObject, Collection<PolicyRecordHandler>> map, WSDLObject key, PolicyRecordHandler handler) {
        if (map.containsKey(key)) {
            map.get(key).add(handler);
        } else {
            LinkedList<PolicyRecordHandler> newSet = new LinkedList<PolicyRecordHandler>();
            newSet.add(handler);
            map.put(key, newSet);
        }
    }

    private String getBaseUrl(String policyUri) {
        if (null == policyUri) {
            return null;
        }
        int fragmentIdx = policyUri.indexOf(35);
        return fragmentIdx == -1 ? policyUri : policyUri.substring(0, fragmentIdx);
    }

    private void processReferenceUri(String policyUri, WSDLObject element, XMLStreamReader reader, Map<WSDLObject, Collection<PolicyRecordHandler>> map) {
        if (null == policyUri || policyUri.length() == 0) {
            return;
        }
        if ('#' != policyUri.charAt(0)) {
            this.getUnresolvedUris(false).add(policyUri);
        }
        this.addHandlerToMap(map, element, new PolicyRecordHandler(HandlerType.PolicyUri, SafePolicyReader.relativeToAbsoluteUrl(policyUri, reader.getLocation().getSystemId())));
    }

    private boolean processSubelement(WSDLObject element, XMLStreamReader reader, Map<WSDLObject, Collection<PolicyRecordHandler>> map) {
        if (NamespaceVersion.resolveAsToken((QName)reader.getName()) == XmlToken.PolicyReference) {
            this.processReferenceUri(this.policyReader.readPolicyReferenceElement(reader), element, reader, map);
            return true;
        }
        if (NamespaceVersion.resolveAsToken((QName)reader.getName()) == XmlToken.Policy) {
            PolicyRecordHandler handler = this.readSinglePolicy(this.policyReader.readPolicyElement(reader, null == reader.getLocation().getSystemId() ? "" : reader.getLocation().getSystemId()), true);
            if (null != handler) {
                this.addHandlerToMap(map, element, handler);
            }
            return true;
        }
        return false;
    }

    private void processAttributes(WSDLObject element, XMLStreamReader reader, Map<WSDLObject, Collection<PolicyRecordHandler>> map) {
        String[] uriArray = this.getPolicyURIsFromAttr(reader);
        if (null != uriArray) {
            for (String policyUri : uriArray) {
                this.processReferenceUri(policyUri, element, reader, map);
            }
        }
    }

    @Override
    public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(port, reader, this.getHandlers4PortMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void portAttributes(EditableWSDLPort port, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(port, reader, this.getHandlers4PortMap());
        LOGGER.exiting();
    }

    @Override
    public boolean serviceElements(EditableWSDLService service, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(service, reader, this.getHandlers4ServiceMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void serviceAttributes(EditableWSDLService service, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(service, reader, this.getHandlers4ServiceMap());
        LOGGER.exiting();
    }

    @Override
    public boolean definitionsElements(XMLStreamReader reader) {
        LOGGER.entering();
        if (NamespaceVersion.resolveAsToken((QName)reader.getName()) == XmlToken.Policy) {
            this.readSinglePolicy(this.policyReader.readPolicyElement(reader, null == reader.getLocation().getSystemId() ? "" : reader.getLocation().getSystemId()), false);
            LOGGER.exiting();
            return true;
        }
        LOGGER.exiting();
        return false;
    }

    @Override
    public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(binding, reader, this.getHandlers4BindingMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void bindingAttributes(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(binding, reader, this.getHandlers4BindingMap());
        LOGGER.exiting();
    }

    @Override
    public boolean portTypeElements(EditableWSDLPortType portType, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(portType, reader, this.getHandlers4PortTypeMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void portTypeAttributes(EditableWSDLPortType portType, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(portType, reader, this.getHandlers4PortTypeMap());
        LOGGER.exiting();
    }

    @Override
    public boolean portTypeOperationElements(EditableWSDLOperation operation, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(operation, reader, this.getHandlers4OperationMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void portTypeOperationAttributes(EditableWSDLOperation operation, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(operation, reader, this.getHandlers4OperationMap());
        LOGGER.exiting();
    }

    @Override
    public boolean bindingOperationElements(EditableWSDLBoundOperation boundOperation, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(boundOperation, reader, this.getHandlers4BoundOperationMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void bindingOperationAttributes(EditableWSDLBoundOperation boundOperation, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(boundOperation, reader, this.getHandlers4BoundOperationMap());
        LOGGER.exiting();
    }

    @Override
    public boolean messageElements(EditableWSDLMessage msg, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(msg, reader, this.getHandlers4MessageMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void messageAttributes(EditableWSDLMessage msg, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(msg, reader, this.getHandlers4MessageMap());
        LOGGER.exiting();
    }

    @Override
    public boolean portTypeOperationInputElements(EditableWSDLInput input, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(input, reader, this.getHandlers4InputMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(input, reader, this.getHandlers4InputMap());
        LOGGER.exiting();
    }

    @Override
    public boolean portTypeOperationOutputElements(EditableWSDLOutput output, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(output, reader, this.getHandlers4OutputMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(output, reader, this.getHandlers4OutputMap());
        LOGGER.exiting();
    }

    @Override
    public boolean portTypeOperationFaultElements(EditableWSDLFault fault, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(fault, reader, this.getHandlers4FaultMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(fault, reader, this.getHandlers4FaultMap());
        LOGGER.exiting();
    }

    @Override
    public boolean bindingOperationInputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(operation, reader, this.getHandlers4BindingInputOpMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void bindingOperationInputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(operation, reader, this.getHandlers4BindingInputOpMap());
        LOGGER.exiting();
    }

    @Override
    public boolean bindingOperationOutputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(operation, reader, this.getHandlers4BindingOutputOpMap());
        LOGGER.exiting();
        return result;
    }

    @Override
    public void bindingOperationOutputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(operation, reader, this.getHandlers4BindingOutputOpMap());
        LOGGER.exiting();
    }

    @Override
    public boolean bindingOperationFaultElements(EditableWSDLBoundFault fault, XMLStreamReader reader) {
        LOGGER.entering();
        boolean result = this.processSubelement(fault, reader, this.getHandlers4BindingFaultOpMap());
        LOGGER.exiting((Object)result);
        return result;
    }

    @Override
    public void bindingOperationFaultAttributes(EditableWSDLBoundFault fault, XMLStreamReader reader) {
        LOGGER.entering();
        this.processAttributes(fault, reader, this.getHandlers4BindingFaultOpMap());
        LOGGER.exiting();
    }

    private PolicyMapBuilder getPolicyMapBuilder() {
        if (null == this.policyBuilder) {
            this.policyBuilder = new PolicyMapBuilder();
        }
        return this.policyBuilder;
    }

    private Collection<String> getPolicyURIs(Collection<PolicyRecordHandler> handlers, PolicySourceModelContext modelContext) throws PolicyException {
        ArrayList<String> result = new ArrayList<String>(handlers.size());
        for (PolicyRecordHandler handler : handlers) {
            String policyUri = handler.handler;
            if (HandlerType.AnonymousPolicyId == handler.type) {
                PolicySourceModel policyModel = this.getAnonymousPolicyModels().get(policyUri);
                policyModel.expand(modelContext);
                while (this.getPolicyModels().containsKey(policyUri)) {
                    policyUri = AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString();
                }
                this.getPolicyModels().put(policyUri, policyModel);
            }
            result.add(policyUri);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private boolean readExternalFile(String fileUrl) {
        boolean bl;
        InputStream ios = null;
        XMLStreamReader reader = null;
        try {
            URL xmlURL = new URL(fileUrl);
            ios = xmlURL.openStream();
            reader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(ios);
            while (reader.hasNext()) {
                if (reader.isStartElement() && NamespaceVersion.resolveAsToken((QName)reader.getName()) == XmlToken.Policy) {
                    this.readSinglePolicy(this.policyReader.readPolicyElement(reader, fileUrl), false);
                }
                reader.next();
            }
            bl = true;
        }
        catch (IOException ioe) {
            boolean bl2 = false;
            PolicyUtils.IO.closeResource(reader);
            PolicyUtils.IO.closeResource((Closeable)ios);
            return bl2;
        }
        catch (XMLStreamException xmlse) {
            boolean bl3 = false;
            {
                catch (Throwable throwable) {
                    PolicyUtils.IO.closeResource(reader);
                    PolicyUtils.IO.closeResource(ios);
                    throw throwable;
                }
            }
            PolicyUtils.IO.closeResource(reader);
            PolicyUtils.IO.closeResource((Closeable)ios);
            return bl3;
        }
        PolicyUtils.IO.closeResource((XMLStreamReader)reader);
        PolicyUtils.IO.closeResource((Closeable)ios);
        return bl;
    }

    @Override
    public void finished(WSDLParserExtensionContext context) {
        LOGGER.entering(new Object[]{context});
        if (null != this.expandQueueHead) {
            List<String> externalUris = this.getUnresolvedUris(false);
            this.getUnresolvedUris(true);
            Iterator<String> baseUnresolvedUris = new LinkedList();
            SafePolicyReader.PolicyRecord currentRec = this.expandQueueHead;
            while (null != currentRec) {
                ((LinkedList)((Object)baseUnresolvedUris)).addFirst(currentRec.getUri());
                currentRec = currentRec.next;
            }
            this.getUnresolvedUris(false).addAll((Collection<String>)((Object)baseUnresolvedUris));
            this.expandQueueHead = null;
            this.getUnresolvedUris(false).addAll(externalUris);
        }
        while (!this.getUnresolvedUris(false).isEmpty()) {
            List<String> urisToBeSolvedList = this.getUnresolvedUris(false);
            this.getUnresolvedUris(true);
            for (String currentUri : urisToBeSolvedList) {
                if (this.isPolicyProcessed(currentUri)) continue;
                SafePolicyReader.PolicyRecord policyRecord = this.getPolicyRecordsPassedBy().get(currentUri);
                if (null == policyRecord) {
                    if (this.policyReader.getUrlsRead().contains(this.getBaseUrl(currentUri))) {
                        LOGGER.logSevereException((Throwable)new PolicyException(PolicyMessages.WSP_1014_CAN_NOT_FIND_POLICY(currentUri)));
                        continue;
                    }
                    if (!this.readExternalFile(this.getBaseUrl(currentUri))) continue;
                    this.getUnresolvedUris(false).add(currentUri);
                    continue;
                }
                if (null != policyRecord.unresolvedURIs) {
                    this.getUnresolvedUris(false).addAll(policyRecord.unresolvedURIs);
                }
                this.addNewPolicyNeeded(currentUri, policyRecord.policyModel);
            }
        }
        PolicySourceModelContext modelContext = PolicySourceModelContext.createContext();
        for (String policyUri : this.urisNeeded) {
            PolicySourceModel policySourceModel = this.modelsNeeded.get(policyUri);
            try {
                policySourceModel.expand(modelContext);
                modelContext.addModel(new URI(policyUri), policySourceModel);
            }
            catch (URISyntaxException e) {
                LOGGER.logSevereException((Throwable)e);
            }
            catch (PolicyException e) {
                LOGGER.logSevereException((Throwable)e);
            }
        }
        try {
            HashSet<BuilderHandlerMessageScope> messageSet = new HashSet<BuilderHandlerMessageScope>();
            for (EditableWSDLService editableWSDLService : context.getWSDLModel().getServices().values()) {
                if (this.getHandlers4ServiceMap().containsKey(editableWSDLService)) {
                    this.getPolicyMapBuilder().registerHandler(new BuilderHandlerServiceScope(this.getPolicyURIs(this.getHandlers4ServiceMap().get(editableWSDLService), modelContext), this.getPolicyModels(), editableWSDLService, editableWSDLService.getName()));
                }
                for (EditableWSDLPort editableWSDLPort : editableWSDLService.getPorts()) {
                    if (this.getHandlers4PortMap().containsKey(editableWSDLPort)) {
                        this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs(this.getHandlers4PortMap().get(editableWSDLPort), modelContext), this.getPolicyModels(), editableWSDLPort, editableWSDLPort.getOwner().getName(), editableWSDLPort.getName()));
                    }
                    if (null == editableWSDLPort.getBinding()) continue;
                    if (this.getHandlers4BindingMap().containsKey(editableWSDLPort.getBinding())) {
                        this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs(this.getHandlers4BindingMap().get(editableWSDLPort.getBinding()), modelContext), this.getPolicyModels(), editableWSDLPort.getBinding(), editableWSDLService.getName(), editableWSDLPort.getName()));
                    }
                    if (this.getHandlers4PortTypeMap().containsKey(editableWSDLPort.getBinding().getPortType())) {
                        this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs(this.getHandlers4PortTypeMap().get(editableWSDLPort.getBinding().getPortType()), modelContext), this.getPolicyModels(), editableWSDLPort.getBinding().getPortType(), editableWSDLService.getName(), editableWSDLPort.getName()));
                    }
                    for (EditableWSDLBoundOperation editableWSDLBoundOperation : editableWSDLPort.getBinding().getBindingOperations()) {
                        EditableWSDLMessage outputMsg;
                        EditableWSDLOutput output;
                        EditableWSDLMessage inputMsg;
                        EditableWSDLInput input;
                        EditableWSDLOperation operation = editableWSDLBoundOperation.getOperation();
                        QName operationName = new QName(editableWSDLBoundOperation.getBoundPortType().getName().getNamespaceURI(), editableWSDLBoundOperation.getName().getLocalPart());
                        if (this.getHandlers4BoundOperationMap().containsKey(editableWSDLBoundOperation)) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(this.getPolicyURIs(this.getHandlers4BoundOperationMap().get(editableWSDLBoundOperation), modelContext), this.getPolicyModels(), editableWSDLBoundOperation, editableWSDLService.getName(), editableWSDLPort.getName(), operationName));
                        }
                        if (this.getHandlers4OperationMap().containsKey(operation)) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(this.getPolicyURIs(this.getHandlers4OperationMap().get(operation), modelContext), this.getPolicyModels(), operation, editableWSDLService.getName(), editableWSDLPort.getName(), operationName));
                        }
                        if (null != (input = operation.getInput()) && (inputMsg = input.getMessage()) != null && this.getHandlers4MessageMap().containsKey(inputMsg)) {
                            messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4MessageMap().get(inputMsg), modelContext), this.getPolicyModels(), inputMsg, BuilderHandlerMessageScope.Scope.InputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, null));
                        }
                        if (this.getHandlers4BindingInputOpMap().containsKey(editableWSDLBoundOperation)) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4BindingInputOpMap().get(editableWSDLBoundOperation), modelContext), this.getPolicyModels(), editableWSDLBoundOperation, BuilderHandlerMessageScope.Scope.InputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, null));
                        }
                        if (null != input && this.getHandlers4InputMap().containsKey(input)) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4InputMap().get(input), modelContext), this.getPolicyModels(), input, BuilderHandlerMessageScope.Scope.InputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, null));
                        }
                        if (null != (output = operation.getOutput()) && (outputMsg = output.getMessage()) != null && this.getHandlers4MessageMap().containsKey(outputMsg)) {
                            messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4MessageMap().get(outputMsg), modelContext), this.getPolicyModels(), outputMsg, BuilderHandlerMessageScope.Scope.OutputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, null));
                        }
                        if (this.getHandlers4BindingOutputOpMap().containsKey(editableWSDLBoundOperation)) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4BindingOutputOpMap().get(editableWSDLBoundOperation), modelContext), this.getPolicyModels(), editableWSDLBoundOperation, BuilderHandlerMessageScope.Scope.OutputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, null));
                        }
                        if (null != output && this.getHandlers4OutputMap().containsKey(output)) {
                            this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4OutputMap().get(output), modelContext), this.getPolicyModels(), output, BuilderHandlerMessageScope.Scope.OutputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, null));
                        }
                        for (EditableWSDLBoundFault editableWSDLBoundFault : editableWSDLBoundOperation.getFaults()) {
                            EditableWSDLFault fault = editableWSDLBoundFault.getFault();
                            if (fault == null) {
                                LOGGER.warning(PolicyMessages.WSP_1021_FAULT_NOT_BOUND(editableWSDLBoundFault.getName()));
                                continue;
                            }
                            EditableWSDLMessage faultMessage = fault.getMessage();
                            QName faultName = new QName(editableWSDLBoundOperation.getBoundPortType().getName().getNamespaceURI(), editableWSDLBoundFault.getName());
                            if (faultMessage != null && this.getHandlers4MessageMap().containsKey(faultMessage)) {
                                messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4MessageMap().get(faultMessage), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(editableWSDLBoundFault, editableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, faultName));
                            }
                            if (this.getHandlers4FaultMap().containsKey(fault)) {
                                messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4FaultMap().get(fault), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(editableWSDLBoundFault, editableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, faultName));
                            }
                            if (!this.getHandlers4BindingFaultOpMap().containsKey(editableWSDLBoundFault)) continue;
                            messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs(this.getHandlers4BindingFaultOpMap().get(editableWSDLBoundFault), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(editableWSDLBoundFault, editableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), operationName, faultName));
                        }
                    }
                }
            }
            for (BuilderHandlerMessageScope builderHandlerMessageScope : messageSet) {
                this.getPolicyMapBuilder().registerHandler(builderHandlerMessageScope);
            }
        }
        catch (PolicyException e) {
            LOGGER.logSevereException((Throwable)e);
        }
        LOGGER.exiting();
    }

    @Override
    public void postFinished(WSDLParserExtensionContext context) {
        PolicyMap effectiveMap;
        EditableWSDLModel wsdlModel = context.getWSDLModel();
        try {
            effectiveMap = context.isClientSide() ? context.getPolicyResolver().resolve(new PolicyResolver.ClientContext(this.policyBuilder.getPolicyMap(new PolicyMapMutator[0]), context.getContainer())) : context.getPolicyResolver().resolve(new PolicyResolver.ServerContext(this.policyBuilder.getPolicyMap(new PolicyMapMutator[0]), context.getContainer(), null, new PolicyMapMutator[0]));
            wsdlModel.setPolicyMap(effectiveMap);
        }
        catch (PolicyException e) {
            LOGGER.logSevereException((Throwable)e);
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL(), (Throwable)e));
        }
        try {
            PolicyUtil.configureModel(wsdlModel, effectiveMap);
        }
        catch (PolicyException e) {
            LOGGER.logSevereException((Throwable)e);
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(PolicyMessages.WSP_1012_FAILED_CONFIGURE_WSDL_MODEL(), (Throwable)e));
        }
        LOGGER.exiting();
    }

    private String[] getPolicyURIsFromAttr(XMLStreamReader reader) {
        StringBuilder policyUriBuffer = new StringBuilder();
        for (NamespaceVersion version : NamespaceVersion.values()) {
            String value = reader.getAttributeValue(version.toString(), XmlToken.PolicyUris.toString());
            if (value == null) continue;
            policyUriBuffer.append(value).append(" ");
        }
        return policyUriBuffer.length() > 0 ? policyUriBuffer.toString().split("[\\n ]+") : null;
    }

    static final class PolicyRecordHandler {
        String handler;
        HandlerType type;

        PolicyRecordHandler(HandlerType type, String handler) {
            this.type = type;
            this.handler = handler;
        }

        HandlerType getType() {
            return this.type;
        }

        String getHandler() {
            return this.handler;
        }
    }

    static enum HandlerType {
        PolicyUri,
        AnonymousPolicyId;

    }
}

