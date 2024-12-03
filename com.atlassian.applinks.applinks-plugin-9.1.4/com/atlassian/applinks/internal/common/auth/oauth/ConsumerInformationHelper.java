/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.util.RSAKeys
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.internal.common.net.Uris;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.security.xml.SecureXmlParserFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConsumerInformationHelper {
    private static final String CONSUMER_INFO_PATH = "/plugins/servlet/oauth/consumer-info";

    @Nonnull
    public static Consumer fetchConsumerInformation(@Nonnull ApplicationLink applicationLink) throws ResponseException {
        Request request = Anonymous.createAnonymousRequest((ApplicationLink)applicationLink, (Request.MethodType)Request.MethodType.GET, (String)Uris.uncheckedConcatenate(applicationLink.getRpcUrl(), CONSUMER_INFO_PATH).toString());
        request.setHeader("Accept", "application/xml");
        ConsumerInformationResponseHandler handler = new ConsumerInformationResponseHandler();
        request.execute((ResponseHandler)handler);
        return handler.getConsumer();
    }

    private static class ConsumerInformationResponseHandler
    implements ResponseHandler<Response> {
        private Consumer consumer;

        private ConsumerInformationResponseHandler() {
        }

        public void handle(Response response) throws ResponseException {
            if (response.getStatusCode() != Response.Status.OK.getStatusCode()) {
                throw new ResponseException("Server responded with an error");
            }
            String contentTypeHeader = response.getHeader("Content-Type");
            if (contentTypeHeader != null && !contentTypeHeader.toLowerCase().startsWith("application/xml")) {
                throw new ResponseException("Server sent an invalid response");
            }
            try {
                DocumentBuilder docBuilder = SecureXmlParserFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(response.getResponseBodyAsStream());
                String consumerKey = doc.getElementsByTagName("key").item(0).getTextContent();
                String name = doc.getElementsByTagName("name").item(0).getTextContent();
                PublicKey publicKey = RSAKeys.fromPemEncodingToPublicKey((String)doc.getElementsByTagName("publicKey").item(0).getTextContent());
                String description = null;
                if (doc.getElementsByTagName("description").getLength() > 0) {
                    description = doc.getElementsByTagName("description").item(0).getTextContent();
                }
                URI callback = null;
                if (doc.getElementsByTagName("callback").getLength() > 0) {
                    callback = new URI(doc.getElementsByTagName("callback").item(0).getTextContent());
                }
                this.consumer = Consumer.key((String)consumerKey).name(name).publicKey(publicKey).description(description).callback(callback).build();
            }
            catch (IOException | DOMException | SAXException e) {
                throw new ResponseException("Unable to parse consumer information", (Throwable)e);
            }
            catch (URISyntaxException e) {
                throw new ResponseException("Unable to parse consumer information, callback is not a valid URL", (Throwable)e);
            }
            catch (NoSuchAlgorithmException e) {
                throw new ResponseException("Unable to parse consumer information, no RSA providers are installed", (Throwable)e);
            }
            catch (InvalidKeySpecException e) {
                throw new ResponseException("Unable to parse consumer information, the public key is not a validly encoded RSA public key", (Throwable)e);
            }
        }

        public Consumer getConsumer() {
            return this.consumer;
        }
    }
}

