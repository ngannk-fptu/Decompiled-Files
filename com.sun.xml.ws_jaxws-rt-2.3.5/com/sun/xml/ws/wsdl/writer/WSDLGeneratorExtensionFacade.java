/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.txw2.TypedXmlWriter
 */
package com.sun.xml.ws.wsdl.writer;

import com.sun.istack.NotNull;
import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.ws.api.model.CheckedException;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.ws.api.wsdl.writer.WSDLGeneratorExtension;

final class WSDLGeneratorExtensionFacade
extends WSDLGeneratorExtension {
    private final WSDLGeneratorExtension[] extensions;

    WSDLGeneratorExtensionFacade(WSDLGeneratorExtension ... extensions) {
        assert (extensions != null);
        this.extensions = extensions;
    }

    @Override
    public void start(WSDLGenExtnContext ctxt) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.start(ctxt);
        }
    }

    @Override
    public void end(@NotNull WSDLGenExtnContext ctxt) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.end(ctxt);
        }
    }

    @Override
    public void addDefinitionsExtension(TypedXmlWriter definitions) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addDefinitionsExtension(definitions);
        }
    }

    @Override
    public void addServiceExtension(TypedXmlWriter service) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addServiceExtension(service);
        }
    }

    @Override
    public void addPortExtension(TypedXmlWriter port) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addPortExtension(port);
        }
    }

    @Override
    public void addPortTypeExtension(TypedXmlWriter portType) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addPortTypeExtension(portType);
        }
    }

    @Override
    public void addBindingExtension(TypedXmlWriter binding) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addBindingExtension(binding);
        }
    }

    @Override
    public void addOperationExtension(TypedXmlWriter operation, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addOperationExtension(operation, method);
        }
    }

    @Override
    public void addBindingOperationExtension(TypedXmlWriter operation, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationExtension(operation, method);
        }
    }

    @Override
    public void addInputMessageExtension(TypedXmlWriter message, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addInputMessageExtension(message, method);
        }
    }

    @Override
    public void addOutputMessageExtension(TypedXmlWriter message, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addOutputMessageExtension(message, method);
        }
    }

    @Override
    public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addOperationInputExtension(input, method);
        }
    }

    @Override
    public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addOperationOutputExtension(output, method);
        }
    }

    @Override
    public void addBindingOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationInputExtension(input, method);
        }
    }

    @Override
    public void addBindingOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationOutputExtension(output, method);
        }
    }

    @Override
    public void addBindingOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addBindingOperationFaultExtension(fault, method, ce);
        }
    }

    @Override
    public void addFaultMessageExtension(TypedXmlWriter message, JavaMethod method, CheckedException ce) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addFaultMessageExtension(message, method, ce);
        }
    }

    @Override
    public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException ce) {
        for (WSDLGeneratorExtension e : this.extensions) {
            e.addOperationFaultExtension(fault, method, ce);
        }
    }
}

