/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2005-2005 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/

package mondrian.tui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.Principal;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

/** 
 * This is a partial implementation of the HttpServletRequest where just
 * enough is present to allow for communication between Mondrian's
 * XMLA code and other code in the same JVM.
 * Currently it is used in both the CmdRunner and in XMLA JUnit tests.
 * <p>
 * If you need to add to this implementation, please do so.
 * 
 * @author <a>Richard M. Emberson</a>
 * @version 
 */
public class MockHttpServletRequest implements HttpServletRequest {
    public static String AUTHORIZATION = "Authorization";
    public final static String DATE_FORMAT_HEADER = "EEE, d MMM yyyy HH:mm:ss Z";   

    public class MockRequestDispatcher implements RequestDispatcher {
        private ServletRequest forwardedRequest;
        private ServletResponse forwardedResponse;
        private ServletRequest includedRequest;
        private ServletResponse includedResponse;
        private String path;

        MockRequestDispatcher() {
        }
        public void setPath(String path) {
            this.path = path;
        }
        public String getPath() {
            return this.path;
        }
        public void forward(ServletRequest request, ServletResponse response) 
            throws ServletException, IOException {
            this.forwardedRequest = request;
            this.forwardedResponse = response;
        }
        public void include(ServletRequest request, ServletResponse response) 
                throws ServletException, IOException {
            this.includedRequest = request;
            this.includedResponse = response;
        }

        public ServletRequest getForwardedRequest() {
            return this.forwardedRequest;
        }

        public ServletResponse getForwardedResponse() {
            return this.forwardedResponse;
        }
        public ServletRequest getIncludedRequest() {   
            return this.includedRequest;
        }

        public ServletResponse getIncludedResponse() {   
            return this.includedResponse;
        }
    }
    static class MockServletInputStream extends ServletInputStream {   
        private ByteArrayInputStream stream;
        
        public MockServletInputStream(byte[] data) {   
            stream = new ByteArrayInputStream(data);
        }

        public int read() throws IOException {
            return stream.read();
        }
    }


    private HttpSession session;
    //private ByteArrayInputStream bin;
    private Map parameters;
    private Map requestDispatchers;
    private List locales;
    private String serverName;
    private String charEncoding;
    private String method;
    private String pathInfo;
    private String pathTranslated;
    private String contextPath;
    private String queryString;
    private String remoteUser;
    private String requestedSessionId;
    private String servletPath;
    private String scheme;
    private String localName;
    private String localAddr;
    private String authType;
    private String protocol;
    private String schema;
    private Principal principal;
    private List cookies;
    private boolean requestedSessionIdIsFromCookie;
    private int remotePort;
    private int localPort;
    private int serverPort;
    private String remoteAddr;
    private String remoteHost;
    private Map attributes;
    private final LinkedHashMap headers;
    private boolean sessionCreated;
    private String requestedURI;
    private StringBuffer requestUrl;
    private String bodyContent;
    private Map roles;                                                          

    public MockHttpServletRequest() {
        this(new byte[0]);
    }
    public MockHttpServletRequest(byte[] bytes) {
        this(new String(bytes));
    }
    public MockHttpServletRequest(String bodyContent) {
        this.bodyContent = bodyContent;
        this.attributes = Collections.EMPTY_MAP;
        //this.bin = new ByteArrayInputStream(bytes);
        this.headers = new LinkedHashMap();
        this.requestDispatchers = new HashMap();
        this.parameters = new HashMap();
        this.cookies = new ArrayList();
        this.locales = new ArrayList();
        this.roles = new HashMap();
        this.requestedSessionIdIsFromCookie = true;
        this.method = "GET";
        this.protocol = "HTTP/1.1";
        this.serverName = "localhost";
        this.serverPort = 8080;
        this.scheme = "http";
        this.remoteHost = "localhost";
        this.remoteAddr = "127.0.0.1";
        this.localAddr = "127.0.0.1";
        this.localName = "localhost";
        this.localPort = 8080;
        this.remotePort = 5000;

        this.sessionCreated = false;

    }


    /** 
     *  Returns the value of the named attribute as an Object, or null if no
     *  attribute of the given name exists.
     * 
     * @param name 
     * @return 
     */
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    /** 
     * Returns an Enumeration containing the names of the attributes available
     * to this request.
     * 
     * @return 
     */
    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    /** 
     * Returns the name of the character encoding used in the body of this
     * request.
     * 
     * @return 
     */
    public String getCharacterEncoding() {
        return charEncoding;
    }

    /** 
     *  
     * 
     * @param charEncoding 
     * @throws UnsupportedEncodingException 
     */
    public void setCharacterEncoding(String charEncoding) 
            throws UnsupportedEncodingException {
        this.charEncoding = charEncoding;
    }

    /** 
     *  Returns the length, in bytes, of the request body and made available by
     *  the input stream, or -1 if the length is not known.
     * 
     * @return 
     */
    public int getContentLength() {
        return getIntHeader("Content-Length");
    }

    /** 
     * Returns the MIME type of the body of the request, or null if the type is
     * not known.
     * 
     * @return 
     */
    public String getContentType() {
        return getHeader("Content-Type");
    }

    /** 
     * Retrieves the body of the request as binary data using a
     * ServletInputStream.
     * 
     * @return 
     * @throws IOException 
     */
    public ServletInputStream getInputStream() throws IOException {
        return new MockServletInputStream(bodyContent.getBytes());
    }

    /** 
     * Returns the value of a request parameter as a String, or null if the
     * parameter does not exist.
     * 
     * @param name 
     * @return 
     */
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        return (null != values && 0 < values.length)
            ? values[0] : null;
    }

    /** 
     * Returns an Enumeration of String objects containing the names of the
     * parameters contained in this request.
     * 
     * @return 
     */
    public Enumeration getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    /** 
     * Returns an array of String objects containing all of the values the given
     * request parameter has, or null if the parameter does not exist.
     * 
     * @param name 
     * @return 
     */
    public String[] getParameterValues(String name) {
        return (String[]) parameters.get(name);
    }


    /** 
     * Returns the name and version of the protocol the request uses in the form
     * protocol/majorVersion.minorVersion, for example, HTTP/1.1.
     * 
     * @return 
     */
    public String getProtocol() {
        return protocol;
    }

    /** 
     * Returns the name of the scheme used to make this request, for example,
     * http, https, or ftp.
     * 
     * @return 
     */
    public String getScheme() {
        return schema;
    }

    /** 
     * Returns the host name of the server that received the request.
     * 
     * @return 
     */
    public String getServerName() {
        return serverName;
    }

    /** 
     * Returns the port number on which this request was received.
     * 
     * @return 
     */
    public int getServerPort() {
        return serverPort;
    }

    /** 
     * Retrieves the body of the request as character data using a
     * BufferedReader.
     * 
     * @return 
     * @throws IOException 
     */
    public BufferedReader getReader() throws IOException {
        return (bodyContent == null) 
            ? null
            : new BufferedReader(new StringReader(bodyContent));
    }

    /** 
     * Returns the Internet Protocol (IP) address of the client that sent the
     * request.
     * 
     * @return 
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }

    /** 
     * Returns the fully qualified name of the client that sent the request, or
     * the IP address of the client if the name cannot be determined.
     * 
     * @return 
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /** 
     *  Stores an attribute in this request.
     * 
     * @param name 
     * @param obj 
     */
    public void setAttribute(String name, Object obj) {
        if (attributes  == Collections.EMPTY_MAP) {
            attributes = new HashMap();
        }
        this.attributes.put(name, obj);
    }

    /** 
     *  Removes an attribute from this request.
     * 
     * @param name 
     */
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    /** 
     * Returns the preferred Locale that the client will accept content in,
     * based on the Accept-Language header.
     * 
     * @return 
     */
    public Locale getLocale() {
        return (locales.size() < 1) 
            ? Locale.getDefault()
            : (Locale) locales.get(0);
    }

    /** 
     * Returns an Enumeration of Locale objects indicating, in decreasing order
     * starting with the preferred locale, the locales that are acceptable to
     * the client based on the Accept-Language header.
     * 
     * @return 
     */
    public Enumeration getLocales() {
        return Collections.enumeration(locales);
    }

    /** 
     *  Returns a boolean indicating whether this request was made using a
     *  secure channel, such as HTTPS.
     * 
     * @return 
     */
    public boolean isSecure() {
        String scheme = getScheme();
        return (scheme == null) 
            ? false
            : scheme.equals("https");
    }

    /** 
     * Returns a RequestDispatcher object that acts as a wrapper for the
     * resource located at the given path.
     * 
     * @param name 
     * @return 
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        RequestDispatcher dispatcher = 
            (RequestDispatcher) requestDispatchers.get(path);
        if (dispatcher == null) {
            dispatcher = new MockRequestDispatcher();
            setRequestDispatcher(path, dispatcher);
        }
        return dispatcher;
    }

    /**
     * Deprecated. As of Version 2.1 of the Java Servlet API, use
     * ServletContext.getRealPath(java.lang.String) instead.
     * @deprecated Method getRealPath is deprecated
     * 
     * @param path  
     * @return 
     */
    public String getRealPath(String path) {
        HttpSession session = getSession();
        return (session == null) 
            ? null
            : session.getServletContext().getRealPath(path);

    }

    /** 
     *  
     * 
     * @return 
     */
    public int getRemotePort() {
        return remotePort;
    }

    /** 
     *  
     * 
     * @return 
     */
    public String getLocalName() {
        return localName;
    }

    /** 
     *  
     * 
     * @return 
     */
    public String getLocalAddr() {
        return localAddr;
    }

    /** 
     *  
     * 
     * @return 
     */
    public int getLocalPort() {
        return localPort;
    }

    /** 
     * Returns the name of the authentication scheme used to protect the
     * servlet, for example, "BASIC" or "SSL," or null if the servlet was not
     * protected.
     * 
     * @return 
     */
    public String getAuthType() {
        return authType;
    }

    /** 
     * Returns an array containing all of the Cookie objects the client sent
     * with this request.
     * 
     * @return 
     */
    public Cookie[] getCookies() {
        return (Cookie[]) cookies.toArray(new Cookie[cookies.size()]);
    }

    /** 
     * Returns the value of the specified request header as a long value that
     * represents a Date object.
     * 
     * @param name 
     * @return 
     */
    public long getDateHeader(String name) {
        String header = getHeader(name);
        if (header == null) {
            return -1;
        }
        try {
            Date dateValue = new SimpleDateFormat(DATE_FORMAT_HEADER, Locale.US).parse(header);
            return dateValue.getTime();
        } catch (ParseException exc) {
            throw new IllegalArgumentException(exc.getMessage());
        }
    }

    /** 
     * Returns the value of the specified request header as a String.
     * 
     * @param name 
     * @return 
     */
    public String getHeader(String name) {

        List headerList = (List) headers.get(name);

        return ((headerList == null) || (headerList.size() ==0))
            ? null
            : (String) headerList.get(0);
    }

    /** 
     *  Returns all the values of the specified request header as an Enumeration
     *  of String objects.
     * 
     * @param name 
     * @return 
     */
    public Enumeration getHeaders(String name) {
        List headerList = (List) headers.get(name);
        return (headerList == null) 
            ? null
            : Collections.enumeration(headerList);
    }

    /** 
     * Returns an enumeration of all the header names this request contains.
     * 
     * @return 
     */
    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    /** 
     * Returns the value of the specified request header as an int.
     * 
     * @param name 
     * @return 
     */
    public int getIntHeader(String name) {
        String header = getHeader(name);
        return (header == null) 
            ? -1
            : new Integer(header).intValue();
    }

    /** 
     * Returns the name of the HTTP method with which this request was made, for
     * example, GET, POST, or PUT.
     * 
     * @return 
     */
    public String getMethod() {
        return this.method;
    }

    /** 
     * Returns any extra path information associated with the URL the client
     * sent when it made this request.
     * 
     * @return 
     */
    public String getPathInfo() {
        return pathInfo;
    }

    /** 
     * Returns any extra path information after the servlet name but before the
     * query string, and translates it to a real path.
     * 
     * @return 
     */
    public String getPathTranslated() {
        return pathTranslated;
    }

    /** 
     * Returns the portion of the request URI that indicates the context of the
     * request.
     * 
     * @return 
     */
    public String getContextPath() {
        return contextPath;
    }

    /** 
     * Returns the query string that is contained in the request URL after the
     * path.
     * 
     * @return 
     */
    public String getQueryString() {
        return queryString;
    }

    /** 
     * Returns the login of the user making this request, if the user has been
     * authenticated, or null if the user has not been authenticated.
     * 
     * @return 
     */
    public String getRemoteUser() {
        return remoteUser;
    }

    /** 
     *  Returns a boolean indicating whether the authenticated user is included
     *  in the specified logical "role".
     * 
     * @param role 
     * @return 
     */
    public boolean isUserInRole(String role) {
        return ((Boolean) roles.get(role)).booleanValue();
    }

    /** 
     *  Returns a java.security.Principal object containing the name of the
     *  current authenticated user.
     * 
     * @return 
     */
    public Principal getUserPrincipal() {
        return principal;
    }

    /** 
     * Returns the session ID specified by the client.
     * 
     * @return 
     */
    public String getRequestedSessionId() {
        HttpSession session = getSession();
        return (session == null) 
            ? null
            : session.getId();
    }

    /** 
     * Returns the part of this request's URL from the protocol name up to the
     * query string in the first line of the HTTP request.
     * 
     * @return 
     */
    public String getRequestURI() {
        return requestedURI;
    }

    /** 
     *  
     * 
     * @return 
     */
    public StringBuffer getRequestURL() {
        return requestUrl;
    }

    /** 
     * Returns the part of this request's URL that calls the servlet.
     * 
     * @return 
     */
    public String getServletPath() {
        return servletPath;
    }

    /** 
     * Returns the current HttpSession associated with this request or, if if
     * there is no current session and create is true, returns a new session.
     * 
     * @param flag 
     * @return 
     */
    public HttpSession getSession(boolean create) {
        if (! create && ! sessionCreated) {
            return null;
        }
        return getSession();
    }

    /** 
     *  Returns the current session associated with this request, or if the
     *  request does not have a session, creates one.
     * 
     * @return 
     */
    public HttpSession getSession() {
        sessionCreated = true;
        return session;
    }

    /** 
     * Checks whether the requested session ID is still valid.
     * 
     * @return 
     */
    public boolean isRequestedSessionIdValid() {
        HttpSession session = getSession();
        return (session != null);
    }

    /** 
     * Checks whether the requested session ID came in as a cookie.
     * 
     * @return 
     */
    public boolean isRequestedSessionIdFromCookie() {
        return requestedSessionIdIsFromCookie;
    }

    /** 
     *  Checks whether the requested session ID came in as part of the request
     *  URL.
     * 
     * @return 
     */
    public boolean isRequestedSessionIdFromURL() {
        return !requestedSessionIdIsFromCookie;
    }

    /**
     * Deprecated. As of Version 2.1 of the Java Servlet API, use
     * isRequestedSessionIdFromURL() instead.
     * @deprecated Method isRequestedSessionIdFromUrl is deprecated
     * 
     * @return  
     */
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    /////////////////////////////////////////////////////////////////////////
    //
    // implementation access
    //
    /////////////////////////////////////////////////////////////////////////
/*
    public void setBytes(byte[] bytes) {
        this.bin = new ByteArrayInputStream(bytes);
    }
*/
    /** 
     *  
     * 
     * @return 
     */
    public Map getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }
    public void setPathTranslated(String pathTranslated) {
        this.pathTranslated = pathTranslated;
    }
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }
    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }
    public void setRequestURI(String requestedURI) {
        this.requestedURI = requestedURI;
    }
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }
    public void setLocalName(String localName) {
        this.localName = localName;
    }
    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }
    public void setAuthType(String authType) {
        this.authType = authType;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public void setScheme(String schema) {
        this.schema = schema;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setContentType(String contentType) {
        setHeader("Content-Type", contentType);
    }
    public void setHeader(String name, String value) {
        List valueList = (List) headers.get(name);
        if (valueList == null) {
            valueList = new ArrayList();
            headers.put(name, valueList);
        }
        valueList.add(value);
    }
    /////////////////////////////////////////////////////////////////////////
    //
    // helpers
    //
    /////////////////////////////////////////////////////////////////////////

    public void clearParameters() {   
        parameters.clear();
    }

    public void setupAddParameter(String key, String[] values) {
        parameters.put(key, values);
    }
    public void setupAddParameter(String key, String value) {
        setupAddParameter(key, new String[] { value });
    }

    public void clearAttributes() {
        attributes.clear();
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public Map getRequestDispatcherMap() {
        return Collections.unmodifiableMap(requestDispatchers);
    }   

    public void setRequestDispatcher(String path, RequestDispatcher dispatcher) {
        if(dispatcher instanceof MockRequestDispatcher) {
            ((MockRequestDispatcher)dispatcher).setPath(path);
        }
        requestDispatchers.put(path, dispatcher);
    }

    public void addLocale(Locale locale) {
        locales.add(locale);
    }
    
    public void addLocales(List localeList) {
        locales.addAll(localeList);
    }

    public void addHeader(String key, String value) {
        List valueList = (List) headers.get(key);
        if (valueList == null) {
            valueList = new ArrayList();
            headers.put(key, valueList);
        }
        valueList.add(value);
    }
    public void clearHeader(String key) {
        headers.remove(key);
    }

    public void setRequestURL(String requestUrl) {
        this.requestUrl = new StringBuffer(requestUrl);
    }
    public void setUserPrincipal(Principal principal) {
        this.principal = principal;
    }
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public void setRequestedSessionIdFromCookie(boolean requestedSessionIdIsFromCookie) {
        this.requestedSessionIdIsFromCookie = requestedSessionIdIsFromCookie;
    }
    public void setUserInRole(String role, boolean isInRole) {
        roles.put(role, new Boolean(isInRole));
    }

    public void setBodyContent(byte[] data) {
        setBodyContent(new String(data));
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }


}