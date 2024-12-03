/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParseException
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.voorhees;

import com.atlassian.voorhees.ApplicationException;
import com.atlassian.voorhees.DefaultErrorMapper;
import com.atlassian.voorhees.ErrorCode;
import com.atlassian.voorhees.ErrorMapper;
import com.atlassian.voorhees.I18nAdapter;
import com.atlassian.voorhees.JsonError;
import com.atlassian.voorhees.JsonErrorResponse;
import com.atlassian.voorhees.JsonSuccessResponse;
import com.atlassian.voorhees.MethodData;
import com.atlassian.voorhees.RpcMethodMapper;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonRpcHandler {
    public static final String JSON_CONTENT_TYPE = "application/json";
    private static final Logger log = LoggerFactory.getLogger(JsonRpcHandler.class);
    private final RpcMethodMapper methodMapper;
    private final I18nAdapter i18nAdapter;
    private final ErrorMapper errorMapper;

    public JsonRpcHandler(RpcMethodMapper methodMapper, I18nAdapter i18nAdapter) {
        this(methodMapper, i18nAdapter, new DefaultErrorMapper(i18nAdapter));
    }

    public JsonRpcHandler(RpcMethodMapper methodMapper, I18nAdapter i18nAdapter, ErrorMapper errorMapper) {
        this.methodMapper = methodMapper;
        this.i18nAdapter = i18nAdapter;
        this.errorMapper = errorMapper;
    }

    public void process(String methodName, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        Object jsonResponse;
        httpResponse.setCharacterEncoding("UTF-8");
        String strippedContentType = httpRequest.getContentType();
        if (strippedContentType.contains(";")) {
            strippedContentType = strippedContentType.substring(0, httpRequest.getContentType().indexOf(";"));
        }
        if (!JSON_CONTENT_TYPE.equals(strippedContentType)) {
            this.writeResponse(httpResponse, this.error(new JsonError(ErrorCode.INVALID_REQUEST, this.i18nAdapter.getText("voorhees.invalid.mime.type"))));
            return;
        }
        try {
            JsonNode parameters;
            try {
                parameters = this.readObject(httpRequest);
            }
            catch (EOFException e) {
                parameters = null;
            }
            try {
                jsonResponse = this.executeMethod(null, methodName, parameters);
            }
            catch (Exception e) {
                jsonResponse = this.error(new JsonError(ErrorCode.INTERNAL_RPC_ERROR, this.i18nAdapter.getText("voorhees.internal.server.error", new Serializable[]{e.getMessage()}), (Object)e));
            }
        }
        catch (IOException e) {
            jsonResponse = this.error(new JsonError(ErrorCode.PARSE_ERROR, this.i18nAdapter.getText("voorhees.invalid.json.request")));
        }
        if (jsonResponse == null) {
            this.writeEmptyResponse(httpResponse);
        } else {
            this.writeResponse(httpResponse, jsonResponse);
        }
    }

    public void process(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        Object jsonResponse;
        httpResponse.setCharacterEncoding("UTF-8");
        String strippedContentType = httpRequest.getContentType();
        if (strippedContentType.contains(";")) {
            strippedContentType = strippedContentType.substring(0, httpRequest.getContentType().indexOf(";"));
        }
        if (!JSON_CONTENT_TYPE.equals(strippedContentType)) {
            this.writeResponse(httpResponse, this.error(new JsonError(ErrorCode.INVALID_REQUEST, this.i18nAdapter.getText("voorhees.invalid.mime.type"))));
            return;
        }
        try {
            JsonNode jsonRequest = this.readObject(httpRequest);
            if (jsonRequest.isArray()) {
                ArrayList<Object> responses = new ArrayList<Object>(jsonRequest.size());
                for (JsonNode jsonNode : jsonRequest) {
                    Object retVal = this.processRequest(jsonNode);
                    if (retVal == null) continue;
                    responses.add(retVal);
                }
                jsonResponse = responses;
            } else {
                jsonResponse = this.processRequest(jsonRequest);
            }
        }
        catch (EOFException e) {
            jsonResponse = this.error(new JsonError(ErrorCode.PARSE_ERROR, this.i18nAdapter.getText("voorhees.empty.json.request")));
        }
        catch (JsonParseException e) {
            jsonResponse = this.error(new JsonError(ErrorCode.PARSE_ERROR, this.i18nAdapter.getText("voorhees.invalid.json.request"), (Object)e));
        }
        catch (IOException e) {
            jsonResponse = this.error(new JsonError(ErrorCode.INTERNAL_RPC_ERROR, this.i18nAdapter.getText("voorhees.internal.server.error", new Serializable[]{e.getMessage()}), (Object)e));
        }
        if (jsonResponse != null) {
            this.writeResponse(httpResponse, jsonResponse);
        } else {
            this.writeEmptyResponse(httpResponse);
        }
    }

    private Object processRequest(JsonNode rpcRequest) {
        JsonNode methodNode = rpcRequest.get("method");
        JsonNode jsonVersion = rpcRequest.get("jsonrpc");
        JsonNode idNode = rpcRequest.get("id");
        boolean idSet = rpcRequest.has("id");
        if (!this.isValidId(idNode)) {
            return this.error(new JsonError(ErrorCode.INVALID_REQUEST, this.i18nAdapter.getText("voorhees.illegal.id.type")));
        }
        Object id = this.extractId(rpcRequest.get("id"));
        if (methodNode == null || methodNode.getTextValue().equals("")) {
            return this.error(id, new JsonError(ErrorCode.INVALID_REQUEST, this.i18nAdapter.getText("voorhees.incomplete.request")));
        }
        if (jsonVersion != null && !jsonVersion.getValueAsText().equals("2.0")) {
            return this.error(id, new JsonError(ErrorCode.INVALID_REQUEST, this.i18nAdapter.getText("voorhees.unsupported.jsonrpc.version")));
        }
        String methodName = methodNode.getTextValue();
        Object retVal = this.executeMethod(id, methodName, rpcRequest.get("params"));
        if (!idSet || id == null && jsonVersion == null) {
            return null;
        }
        if (retVal instanceof JsonErrorResponse) {
            return retVal;
        }
        return this.success(id, retVal);
    }

    private Object executeMethod(Object requestId, String methodName, JsonNode params) {
        MethodData methodData = new MethodData(methodName, params);
        if (!this.methodMapper.methodExists(methodData.getMethodName())) {
            return this.error(requestId, new JsonError(ErrorCode.METHOD_NOT_FOUND, this.i18nAdapter.getText("voorhees.method.not.found", new Serializable[]{methodName})));
        }
        if (!this.methodMapper.methodExists(methodData.getMethodName(), methodData.getArity())) {
            return this.error(requestId, new JsonError(ErrorCode.METHOD_NOT_FOUND, this.i18nAdapter.getText("voorhees.method.not.found.with.arity", new Serializable[]{methodData.getMethodName(), Integer.valueOf(methodData.getArity())})));
        }
        List<Class[]> possibleArgumentTypes = this.methodMapper.getPossibleArgumentTypes(methodData.getMethodName(), methodData.getArity());
        Object[] args = new Object[]{};
        Class[] argumentTypes = new Class[]{};
        for (Class[] candidateArgumentTypes : possibleArgumentTypes) {
            argumentTypes = candidateArgumentTypes;
            try {
                args = methodData.getArguments(new ObjectMapper(), argumentTypes);
                break;
            }
            catch (JsonMappingException jsonMappingException) {
            }
            catch (IOException e) {
                return this.error(requestId, new JsonError(ErrorCode.PARSE_ERROR, this.i18nAdapter.getText("unable.to.parse.method.arguments")));
            }
        }
        if (args.length != argumentTypes.length) {
            return this.error(requestId, new JsonError(ErrorCode.INVALID_METHOD_PARAMETERS, this.i18nAdapter.getText("voorhees.method.argument.types.mismatch", new Serializable[]{methodData.getMethodName(), Integer.valueOf(methodData.getArity())})));
        }
        try {
            return this.methodMapper.call(methodData.getMethodName(), argumentTypes, args);
        }
        catch (ApplicationException e) {
            return this.error(requestId, this.errorMapper.mapError(methodName, e.getCause()));
        }
        catch (Exception e) {
            return this.error(requestId, new JsonError(ErrorCode.INTERNAL_RPC_ERROR, this.i18nAdapter.getText("voorhees.internal.server.error", new Serializable[]{e.getMessage()}), (Object)e));
        }
    }

    private void writeEmptyResponse(HttpServletResponse response) {
        this.setEmptyResponseHeaders(response);
    }

    private void writeResponse(HttpServletResponse response, Object responseObject) throws IOException {
        this.setResponseHeaders(response);
        new ObjectMapper().writeValue((Writer)response.getWriter(), responseObject);
    }

    private JsonNode readObject(HttpServletRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree((Reader)request.getReader());
    }

    private void setEmptyResponseHeaders(HttpServletResponse response) {
        response.setStatus(200);
    }

    private void setResponseHeaders(HttpServletResponse response) {
        response.setContentType(JSON_CONTENT_TYPE);
        response.setStatus(200);
    }

    private Object success(Object id, Object retVal) {
        return new JsonSuccessResponse(id, retVal);
    }

    private Object error(JsonError error) {
        return this.error(null, error);
    }

    private Object error(Object requestId, JsonError error) {
        return new JsonErrorResponse(requestId, error);
    }

    private boolean isValidId(JsonNode idNode) {
        return idNode == null || idNode.isNull() || idNode.isNumber() || idNode.isTextual();
    }

    private Object extractId(JsonNode idNode) {
        if (idNode == null) {
            return null;
        }
        if (idNode.isNumber()) {
            return idNode.getNumberValue();
        }
        if (idNode.isTextual()) {
            return idNode.getTextValue();
        }
        if (idNode.isNull()) {
            return null;
        }
        throw new IllegalArgumentException("Not a valid id type: " + idNode);
    }
}

