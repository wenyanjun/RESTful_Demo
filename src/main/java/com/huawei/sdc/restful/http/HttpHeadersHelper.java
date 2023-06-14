package com.huawei.sdc.restful.http;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.auth.MalformedChallengeException;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class HttpHeadersHelper {
    /* HTTP消息头 */
    private List<Header> headers = new ArrayList<Header>();

    /* 设备端RESTful服务返回的响应消息中包含的鉴权头（WWW-Authenticate），可能有多条，根据支持的算法类型进行选择。
     **/
    private List<Header> authHeaders = new ArrayList<Header>();

    /* 发送给设备端RESTful服务的鉴权头(Authorization) */
    private Header authorizaitonHeader = null;

    /* 消息体长度 */
    public int contentLen = 0;

    public HttpHeadersHelper() {
    }

    public HttpHeadersHelper(Header[] headers) {
        for(Header header : headers)
            this.headers.add(header);
        parse();
    }

    public void addHeader(String name, String value) {
        this.headers.add(new Header(name, value));
    }

    public void addHeader(Header header) {
        this.headers.add(header);
    }

    private  void parse() {
        for(Header header: headers) {
            if (header.getName().equals("Content-Length")) {
                contentLen = Integer.parseInt(header.getValue());
            } else if (header.getName().equals("WWW-Authenticate")) {
                authHeaders.add(header);
            }
        }
    }

    /* 计算response，拼装Authorization头 */
    public Header getAuthorizationHeader(String authMethod, String userName, String password, String httpMethod, String url) throws NoSuchAlgorithmException, UnsupportedEncodingException, MalformedChallengeException, AuthenticationException {
        if (null != authorizaitonHeader || authHeaders.size() == 0)
            return authorizaitonHeader;

        Header selectAuthHeader = null;
        for(Header header : authHeaders)  {
            if (authMethod.isEmpty() || header.getValue().contains(authMethod)) {
                selectAuthHeader = header;
                break;
            }
        }

        /* TODO：DigestScheme只支持MD5、MD5-Sess算法，如果要支持SHA256、SHA256-Sess，可以基于此类进行扩展，或参考此类自行实现 */
        DigestScheme scheme = new DigestScheme();
        scheme.processChallenge(selectAuthHeader.getValue());  // 注意： 这里要求传入的是WWW-Authenticate头字段的value，不带key。
        String solution = scheme.authenticate(
                new UsernamePasswordCredentials(userName, password), new GetMethod(url));
        log.info("method---" + new GetMethod(url));
        authorizaitonHeader = new Header("Authorization", solution);
        return authorizaitonHeader;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Header header: headers) {
            builder.append(header.toString());
        }
        builder.append("\r\n");
        return builder.toString();
    }
}
