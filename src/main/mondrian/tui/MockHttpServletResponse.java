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

import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

/** 
 * This is a partial implementation of the HttpServletResponse where just
 * enough is present to allow for communication between Mondrian's
 * XMLA code and other code in the same JVM.
 * Currently it is used in both the CmdRunner and in XMLA JUnit tests.
 * <p>
 * If you need to add to this implementation, please do so.
 * 
 * @author <a>Richard M. Emberson</a>
 * @version 
 */
public class MockHttpServletResponse implements HttpServletResponse {

    public final static String DATE_FORMAT_HEADER = "EEE, d MMM yyyy HH:mm:ss Z";   


    static class MockServletOutputStream extends ServletOutputStream {       
        private ByteArrayOutputStream buffer;
        private String encoding;
        
        public MockServletOutputStream(int size) {
            this(size, "ISO-8859-1");
        }
        
        public MockServletOutputStream(int size, String encoding) {       
            buffer = new ByteArrayOutputStream(size);
            this.encoding = encoding;
        }   
        
        public void setEncoding(String encoding) {   
            this.encoding = encoding;
        }
    
        public void write(int value) throws IOException {
            buffer.write(value);
        }

        public String getContent() throws IOException {
            try {
                buffer.flush();
                return buffer.toString(encoding);
            } catch (IOException exc) {
                throw exc;
            }
        }

        public byte[] getBinaryContent() throws IOException {
            try {
                buffer.flush();
                return buffer.toByteArray();
            } catch (IOException exc) {
                throw exc;
            }
        }

        public void clearContent() {
            buffer = new ByteArrayOutputStream();
        }   
    }   


    private PrintWriter writer;
    private Locale locale;
    private String charEncoding;
    private List cookies;
    private MockServletOutputStream outputStream;
    private int statusCode;
    private boolean isCommited;
    private String location;
    private String errorMsg;
    private int errorCode;
    private boolean wasErrorSent;
    private boolean wasRedirectSent;
    private int bufferSize;
    private final Map headers;

    public MockHttpServletResponse() {
        this.isCommited = false;
        this.cookies = Collections.EMPTY_LIST;
        this.bufferSize = 8192;
        this.charEncoding = "ISO-8859-1";
        this.errorCode = SC_OK;
        this.statusCode = SC_OK;
        this.headers = new HashMap();
        this.outputStream = new MockServletOutputStream(bufferSize);
    }
    
    /** 
     * Returns the name of the charset used for the MIME body sent in this
     * response. 
     * 
     * @return 
     */
    public String getCharacterEncoding() {
        return charEncoding;
    }
    
    /** 
     * Returns a ServletOutputStream suitable for writing binary data in the
     * response. 
     * 
     * @return 
     * @throws IOException 
     */
    public ServletOutputStream getOutputStream() throws IOException {
        return outputStream;
    }
    
    /** 
     * Returns a PrintWriter object that can send character text to the client. 
     * 
     * @return 
     * @throws IOException 
     */
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(
                                outputStream, charEncoding), true);
        }

        return writer;
    }
    
    public void setCharacterEncoding(String charEncoding) {
        this.charEncoding = charEncoding;
        this.outputStream.setEncoding(charEncoding);
    }

    /** 
     * Sets the length of the content body in the response In HTTP servlets,
     * this method sets the HTTP Content-Length header. 
     * 
     * @param i 
     */
    public void setContentLength(int len) {
        setIntHeader("Content-Length", len);
    }

    /** 
     * Sets the content type of the response being sent to the client. 
     * 
     * @param s 
     */
    public void setContentType(String contentType) {
        setHeader("Content-Type", contentType);
    }

    /** 
     * Sets the preferred buffer size for the body of the response. 
     * 
     * @param i 
     */
    public void setBufferSize(int size) {
        this.bufferSize = size;
    }

    /** 
     * Returns the actual buffer size used for the response. 
     * 
     * @return 
     */
    public int getBufferSize() {
        return this.bufferSize;
    }

    /** 
     * Forces any content in the buffer to be written to the client.
     * 
     * @throws IOException 
     */
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        outputStream.flush();
    }

    public void resetBuffer() {
        outputStream.clearContent();
    }

    /** 
     * Returns a boolean indicating if the response has been committed. 
     * 
     * @return 
     */
    public boolean isCommitted() {
        return isCommited;
    }

    /** 
     * Clears any data that exists in the buffer as well as the status code and
     * headers.
     */
    public void reset() {
        headers.clear();
        resetBuffer();
    }

    /** 
     *  Sets the locale of the response, setting the headers (including the
     *  Content-Type's charset) as appropriate. 
     * 
     * @param locale 
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /** 
     * Returns the locale assigned to the response. 
     * 
     * @return 
     */
    public Locale getLocale() {
        return locale;
    }

    /** 
     * Adds the specified cookie to the response. 
     * 
     * @param cookie 
     */
    public void addCookie(Cookie cookie) {
        if (cookies == Collections.EMPTY_LIST) {
            cookies = new ArrayList();
        }
        cookies.add(cookies);
    }

    /** 
     * Returns a boolean indicating whether the named response header has
     * already been set. 
     * 
     * @param s 
     * @return 
     */
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    /** 
     * Encodes the specified URL by including the session ID in it, or, if
     * encoding is not needed, returns the URL unchanged. 
     * 
     * @param s 
     * @return 
     */
    public String encodeURL(String url) {
        return encode(url);
    }

    /** 
     * Encodes the specified URL for use in the sendRedirect method or, if
     * encoding is not needed, returns the URL unchanged. 
     * 
     * @param s 
     * @return 
     */
    public String encodeRedirectURL(String url) {
        return encode(url);
    }

    /**
     * @deprecated Method encodeUrl is deprecated
     */

    public String encodeUrl(String s) {
        return encodeURL(s);
    }

    /**
     * @deprecated Method encodeRedirectUrl is deprecated
     */

    public String encodeRedirectUrl(String s) {
        return encodeRedirectURL(s);
    }

    /** 
     *  Sends an error response to the client using the specified status code
     *  and descriptive message. 
     * 
     * @param i 
     * @param s 
     * @throws IOException 
     */
    public void sendError(int code, String msg) throws IOException {
        this.errorCode = code;
        this.wasErrorSent = true;
        this.errorMsg = msg;
    }

    /** 
     * Sends an error response to the client using the specified status. 
     * 
     * @param i 
     * @throws IOException 
     */
    public void sendError(int code) throws IOException {
        this.errorCode = code;
        this.wasErrorSent = true;
    }

    /** 
     * Sends a temporary redirect response to the client using the specified
     * redirect location URL. 
     * 
     * @param s 
     * @throws IOException 
     */
    public void sendRedirect(String location) throws IOException {
        setHeader("Location", location);
        wasRedirectSent = true;
    }

    /** 
     * Sets a response header with the given name and date-value. 
     * 
     * @param s 
     * @param l 
     */
    public void setDateHeader(String name, long date) {
        Date dateValue = new Date(date);
        String dateString = DateFormat.getDateInstance().format(dateValue);
        setHeader(name, dateString);
    }

    /** 
     * Adds a response header with the given name and date-value. 
     * 
     * @param s 
     * @param l 
     */
    public void addDateHeader(String name, long date) {
        Date dateValue = new Date(date);
        String dateString = new SimpleDateFormat(DATE_FORMAT_HEADER, Locale.US).format(dateValue);
        addHeader(name, dateString);
    }

    /** 
     * Sets a response header with the given name and value. 
     * 
     * @param s 
     * @param s1 
     */
    public void setHeader(String name, String value) {
        List valueList = (List) headers.get(name);
        if (valueList == null) {
            valueList = new ArrayList();
            headers.put(name, valueList);
        }
        valueList.add(value);

    }

    /** 
     * Adds a response header with the given name and value. 
     * 
     * @param s 
     * @param s1 
     */
    public void addHeader(String name, String value) {
        List valueList = (List) headers.get(name);
        if (null == valueList) {
            valueList = new ArrayList();
            headers.put(name, valueList);
        }
        valueList.add(value);
    }

    /** 
     *  Sets a response header with the given name and integer value. 
     * 
     * @param s 
     * @param i 
     */
    public void setIntHeader(String name, int value) {
        String stringValue = new Integer(value).toString();
        addHeader(name, stringValue);
    }

    /** 
     * Adds a response header with the given name and integer value. 
     * 
     * @param s 
     * @param i 
     */
    public void addIntHeader(String name, int value) {
        String stringValue = new Integer(value).toString();
        addHeader(name, stringValue);
    }

    /** 
     *  Sets the status code for this response. 
     * 
     * @param i 
     */
    public void setStatus(int status) {
        this.statusCode = status;
    }
    
    /**
     * @deprecated Method setStatus is deprecated
     * Deprecated. As of version 2.1, due to ambiguous meaning of the message
     * parameter. To set a status code use setStatus(int), to send an error with
     * a description use sendError(int, String). Sets the status code and
     * message for this response.
     */
    public void setStatus(int status, String s) {
        setStatus(status);
    }

    /////////////////////////////////////////////////////////////////////////
    //
    // implementation access
    //
    /////////////////////////////////////////////////////////////////////////
    public byte[] toByteArray() throws IOException {
        return outputStream.getBinaryContent();
    }

    public String getHeader(String name) {
        List list = getHeaderList(name);

        return ((list == null) || (list.size() == 0))
            ? null
            : (String) list.get(0);

    }

    public String getContentType() {
        return getHeader("Content-Type");
    }
    

    /////////////////////////////////////////////////////////////////////////
    //
    // helpers
    //
    /////////////////////////////////////////////////////////////////////////
    public List getHeaderList(String name) {
        return (List) headers.get(name);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List getCookies() {
        return cookies;
    }

    public boolean wasErrorSent() {
        return wasErrorSent;
    }

    public boolean wasRedirectSent() {
        return wasRedirectSent;
    }

/*
    protected void clearHeaders() {
        this.headers.clear();
    }
*/

    protected String encode(String s) {
        // TODO
        return s;
    }


}