/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.io.EndPoint
 *  org.eclipse.jetty.util.Callback
 */
package org.eclipse.jetty.client;

import java.util.List;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;

public class UpgradeProtocolHandler
implements ProtocolHandler {
    private final List<String> protocols = List.of("websocket", "h2c");

    @Override
    public String getName() {
        return "upgrade";
    }

    @Override
    public boolean accept(Request request, Response response) {
        boolean upgraded = 101 == response.getStatus();
        boolean accepted = false;
        if (upgraded) {
            accepted = this.acceptHeaders(request, response);
        }
        return upgraded && accepted;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected boolean acceptHeaders(Request request, Response response) {
        HttpField requestUpgrade;
        HttpField responseUpgrade = response.getHeaders().getField(HttpHeader.UPGRADE);
        if (responseUpgrade != null) {
            if (this.protocols.stream().anyMatch(arg_0 -> ((HttpField)responseUpgrade).contains(arg_0))) {
                return true;
            }
        }
        if ((requestUpgrade = request.getHeaders().getField(HttpHeader.UPGRADE)) == null) return false;
        if (!this.protocols.stream().anyMatch(arg_0 -> ((HttpField)requestUpgrade).contains(arg_0))) return false;
        return true;
    }

    @Override
    public Response.Listener getResponseListener() {
        return new Response.Listener.Adapter(){

            @Override
            public void onComplete(Result result) {
                HttpResponse response = (HttpResponse)result.getResponse();
                HttpRequest request = (HttpRequest)response.getRequest();
                if (result.isSucceeded()) {
                    try {
                        HttpConversation conversation = request.getConversation();
                        HttpUpgrader upgrader = (HttpUpgrader)conversation.getAttribute(HttpUpgrader.class.getName());
                        if (upgrader == null) {
                            throw new HttpResponseException("101 response without " + HttpUpgrader.class.getSimpleName(), response);
                        }
                        EndPoint endPoint = (EndPoint)conversation.getAttribute(EndPoint.class.getName());
                        if (endPoint == null) {
                            throw new HttpResponseException("Upgrade without " + EndPoint.class.getSimpleName(), response);
                        }
                        upgrader.upgrade(response, endPoint, Callback.from(() -> ((Callback)Callback.NOOP).succeeded(), x -> UpgradeProtocolHandler.this.forwardFailureComplete(request, null, response, (Throwable)x)));
                    }
                    catch (Throwable x2) {
                        UpgradeProtocolHandler.this.forwardFailureComplete(request, null, response, x2);
                    }
                } else {
                    UpgradeProtocolHandler.this.forwardFailureComplete(request, result.getRequestFailure(), response, result.getResponseFailure());
                }
            }
        };
    }

    private void forwardFailureComplete(HttpRequest request, Throwable requestFailure, Response response, Throwable responseFailure) {
        HttpConversation conversation = request.getConversation();
        conversation.updateResponseListeners(null);
        List<Response.ResponseListener> responseListeners = conversation.getResponseListeners();
        ResponseNotifier notifier = new ResponseNotifier();
        notifier.forwardFailure(responseListeners, response, responseFailure);
        notifier.notifyComplete(responseListeners, new Result(request, requestFailure, response, responseFailure));
    }
}

