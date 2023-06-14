package com.huawei.sdc.restful.http;

import org.apache.commons.httpclient.HttpParser;

import java.io.IOException;
import java.io.InputStream;

public class HttpReqLineHelper {
    private String reqLine = "";
    public String method = "";
    public String url = "";
    public String httpVersion = "";
    public int statusCode = 200;

    private final static int SDC_PRIVATE_PREFIX_LEN = 8;

    public HttpReqLineHelper(InputStream inputStream) throws IOException {
        String line = "";
        do {
            line = HttpParser.readLine(inputStream, "UTF-8");
        } while (null == line || line.isEmpty());

        if (hasPrivatePrefix(line) && line.length() > SDC_PRIVATE_PREFIX_LEN) {
            this.reqLine = line.substring(SDC_PRIVATE_PREFIX_LEN);
        } else {
            this.reqLine = line.trim();
        }
        parse();
    }

    public HttpReqLineHelper(String reqLine) {
        this.reqLine = reqLine.trim();
        parse();
    }

    private void parse() {
        //System.out.println("begin to parse http req line: " + reqLine);
        String[] segments = reqLine.split(" ");
        if (reqLine.startsWith("HTTP/")) {
            // response
            if (segments.length < 2) {
                System.out.println("error resposne format: " + reqLine);
                return;
            }
            httpVersion = segments[0].split("/")[1];
            statusCode = Integer.parseInt(segments[1]);
        } else {
            // request
            if (segments.length < 3) {
                System.out.println("error request format: " + reqLine);
                return;
            }
            method = segments[0];
            url = segments[1];
            httpVersion = segments[2].split("/")[1];
        }
    }

    private boolean hasPrivatePrefix(String line) {
        if (line.startsWith("HTTP") ||
                line.startsWith("POST") ||
                line.startsWith("PUT") ||
                line.startsWith("GET")) {
            return false;
        } else {
            return true;
        }
    }

    public String toString() {
        if (!reqLine.endsWith("\r\n"))
            return reqLine + "\r\n";
        return reqLine;
    }
}
