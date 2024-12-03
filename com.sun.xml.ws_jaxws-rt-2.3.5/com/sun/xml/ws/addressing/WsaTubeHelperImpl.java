/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.addressing;

import com.sun.xml.ws.addressing.ProblemAction;
import com.sun.xml.ws.addressing.ProblemHeaderQName;
import com.sun.xml.ws.addressing.WsaTubeHelper;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WsaTubeHelperImpl
extends WsaTubeHelper {
    static final JAXBContext jc;

    public WsaTubeHelperImpl(WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
        super(binding, seiModel, wsdlPort);
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty("jaxb.fragment", (Object)Boolean.TRUE);
        return marshaller;
    }

    @Override
    public final void getProblemActionDetail(String action, Element element) {
        ProblemAction pa = new ProblemAction(action);
        try {
            this.createMarshaller().marshal((Object)pa, (Node)element);
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public final void getInvalidMapDetail(QName name, Element element) {
        ProblemHeaderQName phq = new ProblemHeaderQName(name);
        try {
            this.createMarshaller().marshal((Object)phq, (Node)element);
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public final void getMapRequiredDetail(QName name, Element element) {
        this.getInvalidMapDetail(name, element);
    }

    static {
        try {
            jc = JAXBContext.newInstance((Class[])new Class[]{ProblemAction.class, ProblemHeaderQName.class});
        }
        catch (JAXBException e) {
            throw new WebServiceException((Throwable)e);
        }
    }
}

