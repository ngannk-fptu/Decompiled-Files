/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Version
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  net.oauth.OAuth
 *  net.oauth.OAuth$Parameter
 *  net.oauth.OAuthAccessor
 *  net.oauth.OAuthMessage
 *  net.oauth.OAuthProblemException
 *  net.oauth.OAuthValidator
 *  net.oauth.server.OAuthServlet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.internal.OAuthConverter;
import com.atlassian.oauth.serviceprovider.internal.TokenFactory;
import com.atlassian.oauth.serviceprovider.internal.servlet.OAuthProblemUtils;
import com.atlassian.oauth.serviceprovider.internal.servlet.TransactionalServlet;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthValidator;
import net.oauth.server.OAuthServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class RequestTokenServlet
extends TransactionalServlet {
    @VisibleForTesting
    static final String INVALID_CALLBACK_ADVICE = "As per OAuth spec version 1.0 Revision A Section 6.1 <http://oauth.net/core/1.0a#auth_step1>, the oauth_callback parameter is required and must be either a valid, absolute URI using the http or https scheme, or 'oob' if the callback has been established out of band. The following invalid URI was supplied '%s'";
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private final ApplicationProperties applicationProperties;
    private final TokenFactory factory;
    private final OAuthValidator validator;
    private final OAuthConverter converter;
    private final ServiceProviderConsumerStore consumerStore;
    private final ServiceProviderTokenStore tokenStore;

    public RequestTokenServlet(ServiceProviderConsumerStore consumerStore, @Qualifier(value="tokenStore") ServiceProviderTokenStore tokenStore, TokenFactory factory, OAuthValidator validator, OAuthConverter converter, ApplicationProperties applicationProperties, TransactionTemplate transactionTemplate) {
        super(transactionTemplate);
        this.consumerStore = Objects.requireNonNull(consumerStore, "consumerStore");
        this.tokenStore = Objects.requireNonNull(tokenStore, "tokenStore");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.converter = Objects.requireNonNull(converter, "converter");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
    }

    @Override
    public void doPostInTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            ServiceProviderToken.Version version;
            URI callback;
            OAuthMessage message = OAuthServlet.getMessage((HttpServletRequest)request, null);
            message.requireParameters(new String[]{"oauth_consumer_key"});
            Consumer consumer = this.consumerStore.get(message.getConsumerKey());
            if (consumer == null) {
                throw new OAuthProblemException("consumer_key_unknown");
            }
            if (!consumer.getThreeLOAllowed()) {
                throw new OAuthProblemException("permission_denied");
            }
            if (message.getParameter("oauth_callback") != null) {
                callback = this.callbackToUri(message.getParameter("oauth_callback"));
                version = ServiceProviderToken.Version.V_1_0_A;
            } else {
                callback = null;
                version = ServiceProviderToken.Version.V_1_0;
            }
            try {
                this.validator.validateMessage(message, new OAuthAccessor(this.converter.toOAuthConsumer(consumer)));
            }
            catch (OAuthProblemException ope) {
                OAuthProblemUtils.logOAuthProblem(message, ope, this.log);
                throw ope;
            }
            ServiceProviderToken token = this.tokenStore.put(this.factory.generateRequestToken(consumer, callback, message, version));
            response.setContentType("text/plain");
            ServletOutputStream out = response.getOutputStream();
            ArrayList<OAuth.Parameter> parameters = new ArrayList<OAuth.Parameter>();
            parameters.add(new OAuth.Parameter("oauth_token", token.getToken()));
            parameters.add(new OAuth.Parameter("oauth_token_secret", token.getTokenSecret()));
            if (ServiceProviderToken.Version.V_1_0_A.equals((Object)version)) {
                parameters.add(new OAuth.Parameter("oauth_callback_confirmed", "true"));
            }
            OAuth.formEncode(parameters, (OutputStream)out);
        }
        catch (Exception e) {
            OAuthServlet.handleException((HttpServletResponse)response, (Exception)e, (String)this.applicationProperties.getBaseUrl(), (boolean)true);
        }
    }

    private URI callbackToUri(String callbackParameter) throws IOException, OAuthProblemException {
        URI callback;
        if (callbackParameter.equals("oob")) {
            return null;
        }
        try {
            callback = new URI(callbackParameter);
        }
        catch (URISyntaxException e) {
            this.log.error("Unable to parse callback URI '{}'", (Object)callbackParameter);
            OAuthProblemException problem = new OAuthProblemException("parameter_rejected");
            problem.setParameter("oauth_parameters_rejected", (Object)"oauth_callback");
            problem.setParameter("oauth_problem_advice", (Object)String.format(INVALID_CALLBACK_ADVICE, callbackParameter));
            throw problem;
        }
        if (!ServiceProviderToken.isValidCallback((URI)callback)) {
            this.log.error("Invalid callback URI '{}'", (Object)callbackParameter);
            OAuthProblemException problem = new OAuthProblemException("parameter_rejected");
            problem.setParameter("oauth_parameters_rejected", (Object)"oauth_callback");
            problem.setParameter("oauth_problem_advice", (Object)String.format(INVALID_CALLBACK_ADVICE, callbackParameter));
            throw problem;
        }
        return callback;
    }
}

