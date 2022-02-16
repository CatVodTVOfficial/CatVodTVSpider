package com.github.catvod.parser;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.crawler.SpiderReq;
import com.github.catvod.crawler.SpiderUrl;
import com.github.catvod.utils.Misc;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * 依次解析，失败尝试下一个
 * <p>
 * 默认解析超时时间为15秒，如果需要请自定义SpiderReq的HttpClient
 * <p>
 * Author: CatVod
 */
public class JsonSequence {
    public static JSONObject parse(LinkedHashMap<String, String> jx, String url) {
        try {
            if (jx.size() > 0) {
                Set<String> jxNames = jx.keySet();
                for (String jxName : jxNames) {
                    String parseUrl = jx.get(jxName) + url;
                    SpiderDebug.log(parseUrl);
                    try {
                        String json = SpiderReq.get(new SpiderUrl(parseUrl, null)).content;
                        JSONObject taskResult = Misc.jsonParse(url, json);
                        if (taskResult == null)
                            continue;
                        taskResult.put("jxFrom", jxName);
                        return taskResult;
                    } catch (Throwable th) {
                        SpiderDebug.log(th);
                    }
                }
            }
        } catch (Throwable th) {
            SpiderDebug.log(th);
        }
        return new JSONObject();
    }
}
