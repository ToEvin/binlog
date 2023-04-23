package com.insistingon.binlog.event.handler;

import org.apache.http.client.methods.CloseableHttpResponse;

public interface IHttpCallback {
    void call(CloseableHttpResponse closeableHttpResponse);
}
