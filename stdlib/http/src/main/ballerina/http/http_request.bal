// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/io;
import ballerina/mime;

# Represents an HTTP request.
#
# + rawPath - Resource path of the request URL
# + method - The HTTP request method
# + httpVersion - The HTTP version supported by the client
# + userAgent - The user-agent. This value is used when setting the `user-agent` header
# + extraPathInfo - Additional information associated with the URL provided by the client
# + cacheControl - The cache-control directives for the request. This needs to be explicitly initialized if intending
#                  on utilizing HTTP caching.
# + mutualSslHandshake - A record providing mutual ssl handshake results.
public type Request object {

    public string rawPath = "";
    public string method = "";
    public string httpVersion = "";
    public string userAgent = "";
    public string extraPathInfo = "";
    public RequestCacheControl? cacheControl = ();
    public MutualSslHandshake? mutualSslHandshake = ();

    private mime:Entity entity;
    private boolean dirtyRequest;
    boolean noEntityBody;

    public function __init() {
        self.dirtyRequest = false;
        self.noEntityBody = false;
        self.entity = self.createNewEntity();
    }

    # Create a new `Entity` and link it with the request.
    #
    # + return - Newly created `Entity` that has been set to the request
    function createNewEntity() returns mime:Entity = external;

    # Sets the provided `Entity` to the request.
    #
    # + e - The `Entity` to be set to the request
    public function setEntity(mime:Entity e) = external;

    # Gets the query parameters of the request, as a map.
    #
    # + return - String map of query params
    public function getQueryParams() returns map<string> = external;

    # Gets the matrix parameters of the request.
    #
    # + path - Path to the location of matrix parameters
    # + return - A map of matrix paramters which can be found for the given path
    public function getMatrixParams(string path) returns map<any> = external;

    # Gets the `Entity` associated with the request.
    #
    # + return - The `Entity` of the request. An `error` is returned, if entity construction fails
    public function getEntity() returns mime:Entity|error = external;

    //Gets the `Entity` from the request without the body. This function is exposed only to be used internally.
    function getEntityWithoutBody() returns mime:Entity = external;

    # Checks whether the requested header key exists in the header map.
    #
    # + headerName - The header name
    # + return - Returns true if the specified header key exists
    public function hasHeader(string headerName) returns boolean;

    # Returns the value of the specified header. If the specified header key maps to multiple values, the first of
    # these values is returned.
    #
    # + headerName - The header name
    # + return - The first header value for the specified header name. An exception is thrown if no header is found. Use
    #            `hasHeader()` beforehand to check the existence of header.
    public function getHeader(string headerName) returns string;

    # Gets all the header values to which the specified header key maps to.
    #
    # + headerName - The header name
    # + return - The header values the specified header key maps to. An exception is thrown if no header is found. Use
    #            `hasHeader()` beforehand to check the existence of header.
    public function getHeaders(string headerName) returns (string[]);

    # Sets the specified header to the request. If a mapping already exists for the specified header key, the existing
    # header value is replaced with the specfied header value.
    #
    # + headerName - The header name
    # + headerValue - The header value
    public function setHeader(string headerName, string headerValue);

    # Adds the specified header to the request. Existing header values are not replaced.
    #
    # + headerName - The header name
    # + headerValue - The header value
    public function addHeader(string headerName, string headerValue);

    # Removes the specified header from the request.
    #
    # + key - The header name
    public function removeHeader(string key);

    # Removes all the headers from the request.
    public function removeAllHeaders();

    # Gets all the names of the headers of the request.
    #
    # + return - An array of all the header names
    public function getHeaderNames() returns string[];

    # Checks whether the client expects a `100-continue` response.
    #
    # + return - Returns true if the client expects a `100-continue` response
    public function expects100Continue() returns boolean;

    # Sets the `content-type` header to the request.
    #
    # + contentType - Content type value to be set as the `content-type` header
    # + return - Nil if successful, error in case of invalid content-type
    public function setContentType(string contentType) returns error?;

    # Gets the type of the payload of the request (i.e: the `content-type` header value).
    #
    # + return - Returns the `content-type` header value as a string
    public function getContentType() returns string;

    # Extracts `json` payload from the request. If the content type is not JSON, an `error` is returned.
    #
    # + return - The `json` payload or `error` in case of errors
    public function getJsonPayload() returns json|error;

    # Extracts `xml` payload from the request. If the content type is not XML, an `error` is returned.
    #
    # + return - The `xml` payload or `error` in case of errors
    public function getXmlPayload() returns xml|error;

    # Extracts `text` payload from the request. If the content type is not of type text, an `error` is returned.
    #
    # + return - The `text` payload or `error` in case of errors
    public function getTextPayload() returns string|error;

    # Gets the request payload as a `ByteChannel` except in the case of multiparts. To retrieve multiparts, use
    # `getBodyParts()`.
    #
    # + return - A byte channel from which the message payload can be read or `error` in case of errors
    public function getByteChannel() returns io:ReadableByteChannel|error;

    # Gets the request payload as a `byte[]`.
    #
    # + return - The byte[] representation of the message payload or `error` in case of errors
    public function getBinaryPayload() returns byte[]|error;

    # Gets the form parameters from the HTTP request as a `map` when content type is application/x-www-form-urlencoded.
    #
    # + return - The map of form params or `error` in case of errors
    public function getFormParams() returns map<string>|error;

    # Extracts body parts from the request. If the content type is not a composite media type, an error
    # is returned.

    # + return - Returns the body parts as an array of entities or an `error` if there were any errors in
    #            constructing the body parts from the request
    public function getBodyParts() returns mime:Entity[]|error;

    # Sets a `json` as the payload.
    #
    # + payload - The `json` payload
    # + contentType - The content type of the payload. Set this to override the default `content-type` header value
    #                 for `json`
    public function setJsonPayload(json payload, string contentType = "application/json");

    # Sets an `xml` as the payload.
    #
    # + payload - The `xml` payload
    # + contentType - The content type of the payload. Set this to override the default `content-type` header value
    #                 for `xml`
    public function setXmlPayload(xml payload, string contentType = "application/xml");

    # Sets a `string` as the payload.
    #
    # + payload - The `string` payload
    # + contentType - The content type of the payload. Set this to override the default `content-type` header value
    #                 for `string`
    public function setTextPayload(string payload, string contentType = "text/plain");

    # Sets a `byte[]` as the payload.
    #
    # + payload - The `byte[]` payload
    # + contentType - The content type of the payload. Set this to override the default `content-type` header value
    #                 for `byte[]`
    public function setBinaryPayload(byte[] payload, string contentType = "application/octet-stream");

    # Set multiparts as the payload.
    #
    # + bodyParts - The entities which make up the message body
    # + contentType - The content type of the top level message. Set this to override the default
    #                 `content-type` header value
    public function setBodyParts(mime:Entity[] bodyParts, string contentType = "multipart/form-data");

    # Sets the content of the specified file as the entity body of the request.
    #
    # + filePath - Path to the file to be set as the payload
    # + contentType - The content type of the specified file. Set this to override the default `content-type`
    #                 header value
    public function setFileAsPayload(string filePath, string contentType = "application/octet-stream");

    # Sets a `ByteChannel` as the payload.
    #
    # + payload - A `ByteChannel` through which the message payload can be read
    # + contentType - The content type of the payload. Set this to override the default `content-type`
    #                 header value
    public function setByteChannel(io:ReadableByteChannel payload, string contentType = "application/octet-stream");

    # Sets the request payload.
    #
    # + payload - Payload can be of type `string`, `xml`, `json`, `byte[]`, `ByteChannel` or `Entity[]` (i.e: a set
    #             of body parts)
    public function setPayload(string|xml|json|byte[]|io:ReadableByteChannel|mime:Entity[] payload);

    // For use within the module. Takes the Cache-Control header and parses it to a RequestCacheControl object.
    function parseCacheControlHeader();

    # Check whether the entity body is present.
    #
    # + return - a boolean indicating entity body availability
    function checkEntityBodyAvailability() returns boolean = external;
};

/////////////////////////////////
/// Ballerina Implementations ///
/////////////////////////////////

public function Request.hasHeader(string headerName) returns boolean {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.hasHeader(headerName);
}

public function Request.getHeader(string headerName) returns string {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.getHeader(headerName);
}

public function Request.getHeaders(string headerName) returns string[] {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.getHeaders(headerName);
}

public function Request.setHeader(string headerName, string headerValue) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setHeader(headerName, headerValue);
}

public function Request.addHeader(string headerName, string headerValue) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.addHeader(headerName, headerValue);
}

public function Request.removeHeader(string key) {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.removeHeader(key);
}

public function Request.removeAllHeaders() {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.removeAllHeaders();
}

public function Request.getHeaderNames() returns string[] {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.getHeaderNames();
}

public function Request.expects100Continue() returns boolean {
    return self.hasHeader(EXPECT) ? self.getHeader(EXPECT) == "100-continue" : false;
}

public function Request.setContentType(string contentType) returns error? {
    mime:Entity entity = self.getEntityWithoutBody();
    check entity.setContentType(contentType);
    return;
}

public function Request.getContentType() returns string {
    mime:Entity entity = self.getEntityWithoutBody();
    return entity.getContentType();
}

public function Request.getJsonPayload() returns json|error {
    return self.getEntity()!getJson();
}

public function Request.getXmlPayload() returns xml|error {
    return self.getEntity()!getXml();
}

public function Request.getTextPayload() returns string|error {
    return self.getEntity()!getText();
}

public function Request.getBinaryPayload() returns byte[]|error {
    return self.getEntity()!getByteArray();
}

public function Request.getByteChannel() returns io:ReadableByteChannel|error {
    return self.getEntity()!getByteChannel();
}

public function Request.getBodyParts() returns mime:Entity[]|error {
    return self.getEntity()!getBodyParts();
}

public function Request.getFormParams() returns map<string>|error {
    var mimeEntity = self.getEntity();
    if (mimeEntity is mime:Entity) {
        if (!mimeEntity.hasHeader(mime:CONTENT_TYPE)) {
            string errorMessage = "Content type header is not available";
            error typeError = error(mime:MIME_ERROR_CODE, { message : errorMessage });
            return typeError;
        }
        if (!mime:APPLICATION_FORM_URLENCODED.equalsIgnoreCase(mimeEntity.getHeader(mime:CONTENT_TYPE))) {
            string errorMessage = "Invalid content type : expected 'application/x-www-form-urlencoded'";
            error typeError = error(mime:MIME_ERROR_CODE, { message : errorMessage });
            return typeError;
        }
    } else {
        return mimeEntity;
    }
    var formData = self.getEntity()!getText();
    map<string> parameters = {};
    if (formData is string) {
        if (formData != "") {
            string[] entries = formData.split("&");
            int entryIndex = 0;
            while (entryIndex < entries.length()) {
                int index = entries[entryIndex].indexOf("=");
                if (index != -1) {
                    string name = entries[entryIndex].substring(0, index).trim();
                    int size = entries[entryIndex].length();
                    string value = entries[entryIndex].substring(index + 1, size).trim();
                    if (value != "") {
                        parameters[name] = value;
                    }
                }
                entryIndex = entryIndex + 1;
            }
        }
    } else {
        return formData;
    }
    return parameters;
}

public function Request.setJsonPayload(json payload, string contentType = "application/json") {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setJson(payload, contentType = contentType);
    self.setEntity(entity);
}

public function Request.setXmlPayload(xml payload, string contentType = "application/xml") {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setXml(payload, contentType = contentType);
    self.setEntity(entity);
}

public function Request.setTextPayload(string payload, string contentType = "text/plain") {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setText(payload, contentType = contentType);
    self.setEntity(entity);
}

public function Request.setBinaryPayload(byte[] payload, string contentType = "application/octet-stream") {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setByteArray(payload, contentType = contentType);
    self.setEntity(entity);
}

public function Request.setBodyParts(mime:Entity[] bodyParts, string contentType = "multipart/form-data") {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setBodyParts(bodyParts, contentType = contentType);
    self.setEntity(entity);
}

public function Request.setFileAsPayload(string filePath, @sensitive string contentType = "application/octet-stream") {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setFileAsEntityBody(filePath, contentType = contentType);
    self.setEntity(entity);
}

public function Request.setByteChannel(io:ReadableByteChannel payload, string contentType = "application/octet-stream") {
    mime:Entity entity = self.getEntityWithoutBody();
    entity.setByteChannel(payload, contentType = contentType);
    self.setEntity(entity);
}

public function Request.setPayload(string|xml|json|byte[]|io:ReadableByteChannel|mime:Entity[] payload) {
    if (payload is string) {
        self.setTextPayload(payload);
    } else if (payload is xml) {
        self.setXmlPayload(payload);
    } else if (payload is json) {
        self.setJsonPayload(payload);
    } else if (payload is byte[]) {
        self.setBinaryPayload(payload);
    } else if (payload is io:ReadableByteChannel) {
        self.setByteChannel(payload);
    } else {
        self.setBodyParts(payload);
    }
}

# A record for providing mutual ssl handshake results.
#
# + status - Status of the handshake.
public type MutualSslHandshake record {|
    MutualSslStatus status = ();
|};

# Defines the possible values for the mutual ssl status.
#
# `passed`: Mutual SSL handshake is succesful.
# `failed`: Mutual SSL handshake has failed.
public type MutualSslStatus PASSED | FAILED | ();

# Mutual SSL handshake is succesful.
public const PASSED = "passed";

# Mutual SSL handshake has failed.
public const FAILED = "failed";

# Not a mutual ssl connection.
public const NONE = ();
