package com.github.catvod.spider;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.crawler.SpiderReq;
import com.github.catvod.crawler.SpiderReqResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class XPathEgg extends XPath {

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
        if (Integer.parseInt(pg) <= 1) {
            return rule.getCateUrl().replace("{cateId}", tid);
        }
        return (rule.getCateUrl() + "index_{catePg}.html").replace("{cateId}", tid).replace("{catePg}", pg);
    }

    @Override
    protected void detailContentExt(String content, JSONObject vod) {
        try {
            String startFlag = "infoid=";
            int start = content.indexOf(startFlag);
            start = start + startFlag.length();
            int end = content.indexOf(";", start);
            String infoid = content.substring(start, end);

            startFlag = "link5='";
            start = content.indexOf(startFlag);
            start = start + startFlag.length();
            end = content.indexOf("';", start);
            String link5 = content.substring(start, end);

            HashMap<String, String> json = new HashMap<>();
            json.put("infoid", infoid);
            json.put("link5", link5);
            json.put("t", tk);
            // Thanks 猫大
            SpiderReqResult srr = SpiderReq.postJson("https://cat.idontcare.top/ssr/dandan", json, new HashMap<>());
            JSONObject obj = new JSONObject(srr.content);
            vod.put("vod_play_from", obj.getString("vod_play_from"));
            vod.put("vod_play_url", obj.getString("vod_play_url"));
        } catch (Exception e) {

        }
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            fetchRule();
            HashMap<String, String> json = new HashMap<>();
            json.put("url", id);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("accept", "*/*");
            headers.put("x-requested-with", "XMLHttpRequest");
            headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
            headers.put("origin", "https://www.dandanzan.cc");
            headers.put("accept-language", "zh-CN,zh;q=0.9");
            SpiderReqResult srr = SpiderReq.postForm(rule.getPlayUrl(), json, headers);
            JSONObject result = new JSONObject();
            result.put("parse", 0);
            result.put("playUrl", "");
            if (!rule.getPlayUa().isEmpty()) {
                result.put("ua", rule.getPlayUa());
            }
            result.put("url", srr.content);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}
