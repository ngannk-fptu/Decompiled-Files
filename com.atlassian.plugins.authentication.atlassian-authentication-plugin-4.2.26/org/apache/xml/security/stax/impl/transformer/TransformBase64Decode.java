/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.transformer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.stax.impl.transformer.TransformIdentity;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;

public class TransformBase64Decode
extends TransformIdentity {
    private TransformIdentity.ChildOutputMethod childOutputMethod;

    @Override
    public void setOutputStream(OutputStream outputStream) throws XMLSecurityException {
        super.setOutputStream(new Base64OutputStream(new FilterOutputStream(outputStream){

            @Override
            public void close() throws IOException {
                super.flush();
            }
        }, false));
    }

    @Override
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent: {
                return XMLSecurityConstants.TransformMethod.InputStream;
            }
            case InputStream: {
                return XMLSecurityConstants.TransformMethod.InputStream;
            }
        }
        throw new IllegalArgumentException("Unsupported class " + forInput.name());
    }

    @Override
    public void transform(XMLSecEvent xmlSecEvent) throws XMLStreamException {
        int eventType = xmlSecEvent.getEventType();
        if (4 == eventType) {
            if (this.getOutputStream() != null) {
                try {
                    this.getOutputStream().write(xmlSecEvent.asCharacters().getData().getBytes());
                }
                catch (IOException e) {
                    throw new XMLStreamException(e);
                }
            } else if (this.childOutputMethod == null) {
                XMLSecurityConstants.TransformMethod preferredChildTransformMethod = this.getTransformer().getPreferredTransformMethod(XMLSecurityConstants.TransformMethod.XMLSecEvent);
                if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.XMLSecEvent) {
                    this.childOutputMethod = new TransformIdentity.ChildOutputMethod(){
                        private UnsyncByteArrayOutputStream byteArrayOutputStream;
                        private Base64OutputStream base64OutputStream;

                        @Override
                        public void transform(Object object) throws XMLStreamException {
                            if (this.base64OutputStream == null) {
                                this.byteArrayOutputStream = new UnsyncByteArrayOutputStream();
                                this.base64OutputStream = new Base64OutputStream(this.byteArrayOutputStream, false);
                            }
                            try {
                                this.base64OutputStream.write((byte[])object);
                            }
                            catch (IOException e) {
                                throw new XMLStreamException(e);
                            }
                        }

                        @Override
                        public void doFinal() throws XMLStreamException {
                            try {
                                this.base64OutputStream.close();
                            }
                            catch (IOException e) {
                                throw new XMLStreamException(e);
                            }
                            try (UnsyncByteArrayInputStream is = new UnsyncByteArrayInputStream(this.byteArrayOutputStream.toByteArray());){
                                XMLSecEvent xmlSecEvent;
                                XMLEventReaderInputProcessor xmlEventReaderInputProcessor = new XMLEventReaderInputProcessor(null, TransformIdentity.getXmlInputFactory().createXMLStreamReader(is));
                                do {
                                    xmlSecEvent = xmlEventReaderInputProcessor.processEvent(null);
                                    TransformBase64Decode.this.getTransformer().transform(xmlSecEvent);
                                } while (xmlSecEvent.getEventType() != 8);
                            }
                            catch (IOException | XMLSecurityException e) {
                                throw new XMLStreamException(e);
                            }
                            TransformBase64Decode.this.getTransformer().doFinal();
                        }
                    };
                } else if (preferredChildTransformMethod == XMLSecurityConstants.TransformMethod.InputStream) {
                    this.childOutputMethod = new TransformIdentity.ChildOutputMethod(){
                        private UnsyncByteArrayOutputStream byteArrayOutputStream;
                        private Base64OutputStream base64OutputStream;

                        @Override
                        public void transform(Object object) throws XMLStreamException {
                            if (this.base64OutputStream == null) {
                                this.byteArrayOutputStream = new UnsyncByteArrayOutputStream();
                                this.base64OutputStream = new Base64OutputStream(this.byteArrayOutputStream, false);
                            }
                            try {
                                this.base64OutputStream.write((byte[])object);
                            }
                            catch (IOException e) {
                                throw new XMLStreamException(e);
                            }
                        }

                        @Override
                        public void doFinal() throws XMLStreamException {
                            try {
                                this.base64OutputStream.close();
                            }
                            catch (IOException e) {
                                throw new XMLStreamException(e);
                            }
                            try (UnsyncByteArrayInputStream is = new UnsyncByteArrayInputStream(this.byteArrayOutputStream.toByteArray());){
                                TransformBase64Decode.this.getTransformer().transform(is);
                                TransformBase64Decode.this.getTransformer().doFinal();
                            }
                            catch (IOException ex) {
                                throw new XMLStreamException(ex);
                            }
                        }
                    };
                }
                if (this.childOutputMethod != null) {
                    this.childOutputMethod.transform(xmlSecEvent.asCharacters().getData().getBytes());
                }
            }
        }
    }

    @Override
    public void transform(InputStream inputStream) throws XMLStreamException {
        if (this.getOutputStream() != null) {
            super.transform(inputStream);
        } else {
            super.transform(new Base64InputStream(inputStream, false));
        }
    }

    @Override
    public void doFinal() throws XMLStreamException {
        if (this.getOutputStream() != null) {
            try {
                this.getOutputStream().close();
            }
            catch (IOException e) {
                throw new XMLStreamException(e);
            }
        }
        if (this.childOutputMethod != null) {
            this.childOutputMethod.doFinal();
        } else if (this.getTransformer() != null) {
            this.getTransformer().doFinal();
        }
    }
}

