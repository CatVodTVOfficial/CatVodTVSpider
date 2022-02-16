package com.github.catvod.spider;

import android.util.Base64;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.crawler.SpiderReq;
import com.github.catvod.crawler.SpiderReqResult;
import com.github.catvod.crawler.SpiderUrl;
import com.github.catvod.utils.SSLSocketFactoryCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 需配合猫大的解密接口使用，没有接口不用尝试。
 */

public class XPathBde4 extends XPath {

    String tk = "";

    @Override
    protected void loadRuleExt(String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            tk = jsonObj.optString("decodeTk").trim();
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    @Override
    protected String categoryUrl(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        String url = rule.getCateUrl().replace("{catePg}", pg);
        String s = "all";
        if (extend != null) {
            if (extend.containsKey("s")) {
                s = extend.get("s");
            }
        }
        url = url.replace("{cateId}", s);
        url += "?";
        if (extend != null && extend.size() > 0) {
            for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                if (key.equals("s"))
                    continue;
                String value = extend.get(key);
                if (value.length() > 0) {
                    url += (key + "=" + URLEncoder.encode(value) + "&");
                }
            }
        }
        if (!tid.equals("*")) {
            url += ("type=" + tid);
        }
        return url;
    }

    static OkHttpClient respInterceptorClient = null;
    static String m3u8Data = null;

    static OkHttpClient respInterceptorClient() {
        if (respInterceptorClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            try {
                                m3u8Data = new String(Base64.encode(response.body().bytes(), Base64.DEFAULT));
                            } catch (Exception e) {
                                m3u8Data = null;
                            }
                            return response;
                        }
                    })
                    .retryOnConnectionFailure(true)
                    .sslSocketFactory(new SSLSocketFactoryCompat(SSLSocketFactoryCompat.trustAllCert), SSLSocketFactoryCompat.trustAllCert);
            respInterceptorClient = builder.build();
        }
        return respInterceptorClient;
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            fetchRule();
            String webUrl = rule.getPlayUrl().isEmpty() ? id : rule.getPlayUrl().replace("{playUrl}", id);
            SpiderDebug.log(webUrl);
            SpiderUrl su = new SpiderUrl(webUrl, getHeaders(webUrl));
            SpiderReqResult srr = SpiderReq.get(su);
            String content = srr.content;
            String startFlag = "var m3u8 = \"";
            int start = content.indexOf(startFlag);
            start = start + startFlag.length();
            int end = content.indexOf("\";", start);
            String m3u8 = content.substring(start, end).replace("\\", "").replace("https", "http");
            SpiderDebug.log(m3u8);
            HashMap<String, String> headers = getHeaders(m3u8);
            su = new SpiderUrl(m3u8, headers);
            SpiderReq.get(respInterceptorClient(), su);
            if (m3u8Data != null) {
                HashMap<String, String> json = new HashMap<>();
                json.put("data", m3u8Data);
                json.put("t", tk);
                SpiderReqResult srr1 = SpiderReq.postJson("https://cat.idontcare.top/ssr/bde4", json, new HashMap<>());
                String url = srr1.content;
                JSONObject result = new JSONObject();
                result.put("parse", 0);
                result.put("playUrl", "");
                if (!rule.getPlayUa().isEmpty()) {
                    result.put("ua", rule.getPlayUa());
                }
                result.put("url", url);
                return result.toString();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}
