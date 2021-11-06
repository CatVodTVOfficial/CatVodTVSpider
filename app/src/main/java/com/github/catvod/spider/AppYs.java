package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.crawler.SpiderReq;
import com.github.catvod.crawler.SpiderReqResult;
import com.github.catvod.crawler.SpiderUrl;
import com.github.catvod.okhttp.SpiderOKClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * M浏览器中的App影视
 * <p>
 * Author: 群友 不负此生
 */
public class AppYs extends Spider {

    private String sourceName = "";

    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
        this.sourceName = extend;
    }

    @Override
    public String homeContent(boolean filter) {
        try {
            fetchRule();
            JSONObject site = getJson();
            String url = getCateUrl(site.getString("url"));
            JSONArray jsonArray = null;
            if (!url.isEmpty()) {
                SpiderDebug.log(url);
                String json = SpiderReq.get(new SpiderUrl(url, getHeaders(url))).content;
                JSONObject obj = new JSONObject(json);
                if (obj.has("list") && obj.get("list") instanceof JSONArray) {
                    jsonArray = obj.getJSONArray("list");
                } else if (obj.has("data") && obj.get("data") instanceof JSONObject && obj.getJSONObject("data").has("list") && obj.getJSONObject("data").get("list") instanceof JSONArray) {
                    jsonArray = obj.getJSONObject("data").getJSONArray("list");
                } else if (obj.has("data") && obj.get("data") instanceof JSONArray) {
                    jsonArray = obj.getJSONArray("data");
                }
            } else { // 通过filter列表读分类
                String filterStr = getFilterTypes(url);
                String[] classes = filterStr.split("\n")[0].split("\\+");
                jsonArray = new JSONArray();
                for (int i = 1; i < classes.length; i++) {
                    String[] kv = classes[i].trim().split("=");
                    JSONObject newCls = new JSONObject();
                    newCls.put("type_name", kv[0].trim());
                    newCls.put("type_id", kv[1].trim());
                    jsonArray.put(newCls);
                }
            }
            JSONObject result = new JSONObject();
            JSONArray classes = new JSONArray();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObj = jsonArray.getJSONObject(i);
                    String typeName = jObj.getString("type_name");
                    if (isBan(typeName))
                        continue;
                    String typeId = jObj.getString("type_id");
                    JSONObject newCls = new JSONObject();
                    newCls.put("type_id", typeId);
                    newCls.put("type_name", typeName);
                    if (filter) {
                        String filterStr = getFilterTypes(url);
                        String[] filters = filterStr.split("\n");
                        JSONArray filterArr = new JSONArray();
                        for (int k = url.isEmpty() ? 1 : 0; k < filters.length; k++) {
                            String[] oneLine = filters[k].trim().split("\\+");
                            String type = oneLine[0];
                            JSONObject jOne = new JSONObject();
                            jOne.put("key", type.trim());
                            jOne.put("name", type.trim());
                            JSONArray valueArr = new JSONArray();
                            for (int j = 1; j < oneLine.length; j++) {
                                JSONObject kvo = new JSONObject();
                                String kv = oneLine[j].trim();
                                int sp = kv.indexOf("=");
                                if (sp == -1) {
                                    if (isBan(kv))
                                        continue;
                                    kvo.put("n", kv);
                                    kvo.put("v", kv);
                                } else {
                                    String n = kv.substring(0, sp);
                                    if (isBan(n))
                                        continue;
                                    kvo.put("n", n.trim());
                                    kvo.put("v", kv.substring(sp + 1).trim());
                                }
                                valueArr.put(kvo);
                            }
                            jOne.put("value", valueArr);
                            filterArr.put(jOne);
                        }
                        if (!result.has("filters")) {
                            result.put("filters", new JSONObject());
                        }
                        result.getJSONObject("filters").put(typeId, filterArr);
                    }
                    classes.put(newCls);
                }
            }
            result.put("class", classes);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String homeVideoContent() {
        try {
            fetchRule();
            JSONObject site = getJson();
            String apiUrl = site.getString("url");
            String url = getRecommendUrl(apiUrl);
            boolean isTV = false;
            if (url.isEmpty()) {
                url = getCateFilterUrlPrefix(apiUrl) + "movie&page=1&area=&type=&start=";
                isTV = true;
            }
            SpiderDebug.log(url);
            String json = SpiderReq.get(new SpiderUrl(url, getHeaders(url))).content;
            JSONObject obj = new JSONObject(json);
            JSONArray videos = new JSONArray();
            if (isTV) {
                JSONArray jsonArray = obj.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject vObj = jsonArray.getJSONObject(i);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", vObj.getString("nextlink"));
                    v.put("vod_name", vObj.getString("title"));
                    v.put("vod_pic", vObj.getString("pic"));
                    v.put("vod_remarks", vObj.getString("state"));
                    videos.put(v);
                }
            } else {
                ArrayList<JSONArray> arrays = new ArrayList<>();
                findJsonArray(obj, "vlist", arrays);
                if (arrays.isEmpty()) {
                    findJsonArray(obj, "vod_list", arrays);
                }
                List<String> ids = new ArrayList<>();
                for (JSONArray jsonArray : arrays) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject vObj = jsonArray.getJSONObject(i);
                        String vid = vObj.getString("vod_id");
                        if (ids.contains(vid))
                            continue;
                        ids.add(vid);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", vid);
                        v.put("vod_name", vObj.getString("vod_name"));
                        v.put("vod_pic", vObj.getString("vod_pic"));
                        v.put("vod_remarks", vObj.getString("vod_remarks"));
                        videos.put(v);
                    }
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            fetchRule();
            JSONObject site = getJson();
            String apiUrl = site.getString("url");
            String url = getCateFilterUrlPrefix(apiUrl) + tid + getCateFilterUrlSuffix(apiUrl);
            url = url.replace("#PN#", pg);
            url = url.replace("类型", (extend != null && extend.containsKey("类型")) ? extend.get("类型") : "");
            url = url.replace("地区", (extend != null && extend.containsKey("地区")) ? extend.get("地区") : "");
            url = url.replace("语种", (extend != null && extend.containsKey("语种")) ? extend.get("语种") : "");
            url = url.replace("年份", (extend != null && extend.containsKey("年份")) ? extend.get("年份") : "");
            url = url.replace("排序", (extend != null && extend.containsKey("排序")) ? extend.get("排序") : "");
            SpiderDebug.log(url);
            String json = SpiderReq.get(new SpiderUrl(url, getHeaders(url))).content;
            JSONObject obj = new JSONObject(json);
            int totalPg = Integer.MAX_VALUE;
            try {
                if (obj.has("totalpage") && obj.get("totalpage") instanceof Integer) {
                    totalPg = obj.getInt("totalpage");
                } else if (obj.has("pagecount") && obj.get("pagecount") instanceof Integer) {
                    totalPg = obj.getInt("pagecount");
                } else if (obj.has("data") && obj.get("data") instanceof JSONObject &&
                        (obj.getJSONObject("data").has("total") && obj.getJSONObject("data").get("total") instanceof Integer &&
                                obj.getJSONObject("data").has("limit") && obj.getJSONObject("data").get("limit") instanceof Integer)) {
                    int limit = obj.getJSONObject("data").getInt("limit");
                    int total = obj.getJSONObject("data").getInt("total");
                    totalPg = total % limit == 0 ? (total / limit) : (total / limit + 1);
                }
            } catch (Exception e) {
                SpiderDebug.log(e);
            }

            JSONArray jsonArray = null;
            JSONArray videos = new JSONArray();
            if (obj.has("list") && obj.get("list") instanceof JSONArray) {
                jsonArray = obj.getJSONArray("list");
            } else if (obj.has("data") && obj.get("data") instanceof JSONObject && obj.getJSONObject("data").has("list") && obj.getJSONObject("data").get("list") instanceof JSONArray) {
                jsonArray = obj.getJSONObject("data").getJSONArray("list");
            } else if (obj.has("data") && obj.get("data") instanceof JSONArray) {
                jsonArray = obj.getJSONArray("data");
            }
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject vObj = jsonArray.getJSONObject(i);
                    if (vObj.has("vod_id")) {
                        JSONObject v = new JSONObject();
                        v.put("vod_id", vObj.getString("vod_id"));
                        v.put("vod_name", vObj.getString("vod_name"));
                        v.put("vod_pic", vObj.getString("vod_pic"));
                        v.put("vod_remarks", vObj.getString("vod_remarks"));
                        videos.put(v);
                    } else {
                        JSONObject v = new JSONObject();
                        v.put("vod_id", vObj.getString("nextlink"));
                        v.put("vod_name", vObj.getString("title"));
                        v.put("vod_pic", vObj.getString("pic"));
                        v.put("vod_remarks", vObj.getString("state"));
                        videos.put(v);
                    }
                }
            }
            JSONObject result = new JSONObject();
            result.put("page", pg);
            result.put("pagecount", totalPg);
            result.put("limit", 90);
            result.put("total", Integer.MAX_VALUE);
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String detailContent(List<String> ids) {
        try {
            fetchRule();
            JSONObject site = getJson();
            String apiUrl = site.getString("url");
            String url = getPlayUrlPrefix(apiUrl) + ids.get(0);
            SpiderDebug.log(url);
            String json = SpiderReq.get(new SpiderUrl(url, getHeaders(url))).content;
            JSONObject obj = new JSONObject(json);
            JSONObject result = new JSONObject();
            JSONObject vod = new JSONObject();
            genPlayList(apiUrl, obj, json, vod, ids.get(0));
            JSONArray list = new JSONArray();
            list.put(vod);
            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) {
        try {
            fetchRule();
            JSONObject site = getJson();
            String apiUrl = site.getString("url");
            String url = getSearchUrl(apiUrl, URLEncoder.encode(key));
            String json = SpiderReq.get(new SpiderUrl(url, getHeaders(url))).content;
            JSONObject obj = new JSONObject(json);
            JSONArray jsonArray = null;
            JSONArray videos = new JSONArray();
            if (obj.has("list") && obj.get("list") instanceof JSONArray) {
                jsonArray = obj.getJSONArray("list");
            } else if (obj.has("data") && obj.get("data") instanceof JSONObject && obj.getJSONObject("data").has("list") && obj.getJSONObject("data").get("list") instanceof JSONArray) {
                jsonArray = obj.getJSONObject("data").getJSONArray("list");
            } else if (obj.has("data") && obj.get("data") instanceof JSONArray) {
                jsonArray = obj.getJSONArray("data");
            }
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject vObj = jsonArray.getJSONObject(i);
                    if (vObj.has("vod_id")) {
                        JSONObject v = new JSONObject();
                        v.put("vod_id", vObj.getString("vod_id"));
                        v.put("vod_name", vObj.getString("vod_name"));
                        v.put("vod_pic", vObj.getString("vod_pic"));
                        v.put("vod_remarks", vObj.getString("vod_remarks"));
                        videos.put(v);
                    } else {
                        JSONObject v = new JSONObject();
                        v.put("vod_id", vObj.getString("nextlink"));
                        v.put("vod_name", vObj.getString("title"));
                        v.put("vod_pic", vObj.getString("pic"));
                        v.put("vod_remarks", vObj.getString("state"));
                        videos.put(v);
                    }
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            fetchRule();
            JSONObject site = getJson();
            String apiUrl = site.getString("url");
            String parseUrl = getParseUrl(apiUrl, flag);
            String playerUrl = getPlayerUrl(apiUrl, parseUrl, id);
            JSONObject result = new JSONObject();
            getFinalVideo(playerUrl, result);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    private void findJsonArray(JSONObject obj, String match, ArrayList<JSONArray> result) {
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String k = keys.next();
            try {
                Object o = obj.get(k);
                if (k.equals(match) && o instanceof JSONArray)
                    result.add((JSONArray) o);
                if (o instanceof JSONObject) {
                    findJsonArray((JSONObject) o, match, result);
                } else if (o instanceof JSONArray) {
                    JSONArray array = (JSONArray) o;
                    for (int i = 0; i < array.length(); i++) {
                        findJsonArray(array.getJSONObject(i), match, result);
                    }
                }
            } catch (JSONException e) {
                SpiderDebug.log(e);
            }
        }
    }

    private String jsonArr2Str(JSONArray array) {
        try {
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                strings.add(array.getString(i));
            }
            return TextUtils.join(",", strings);
        } catch (JSONException e) {
        }
        return "";
    }

    private static HashMap<String, JSONObject> sites = new HashMap<>();

    protected void fetchRule() {
        if (sites.size() == 0) {
            try {
                SpiderUrl su = new SpiderUrl("https://inmemory.coding.net/p/InMemory/d/MBrowser/git/raw/master/AppFile/APP%E5%BD%B1%E8%A7%86%E5%88%97%E8%A1%A8", null);
                String json = SpiderReq.get(su).content.replaceAll("\\s", "");
                JSONArray sources = new JSONObject(json).optJSONArray("data");
                for (int i = 0; i < sources.length(); i++) {
                    JSONArray list = sources.getJSONObject(i).getJSONArray("list");
                    String title = sources.getJSONObject(i).getString("title");
                    Matcher matcher = Pattern.compile(".+\\((.+)\\)").matcher(title);
                    if (matcher.find()) {
                        title = matcher.group(1);
                    }
                    for (int j = 0; j < list.length(); j++) {
                        JSONObject obj = list.getJSONObject(j);
                        String scName = obj.optString("title");
                        sites.put(title + "_" + scName, obj);
                        SpiderDebug.log("{\"key\":\"csp_appys_" + title + "_" + scName + "\", \"name\":\"" + scName + "(M)\", \"type\":3, \"api\":\"csp_AppYs\",\"searchable\":1,\"quickSearch\":0,\"filterable\":1,\"ext\":\"" + title + "_" + scName + "\"},");
                    }
                }
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
        }
    }

    private JSONObject getJson() {
        if (sites.containsKey(sourceName)) {
            return sites.get(sourceName);
        }
        return null;
    }

    private HashMap<String, String> getHeaders(String URL) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", UA(URL));
        return headers;
    }

    private boolean isBan(String key) {
        return key.equals("伦理") || key.equals("情色") || key.equals("福利");
    }

    // M 扩展方法

    // ######重组搜索
    private String getSearchUrl(String URL, String KEY) {
        if (URL.contains(".vod")) {
            if (URL.contains("iopenyun.com")) {
                return URL + "/list?wd=" + KEY + "&page=";
            } else {
                return URL + "?wd=" + KEY + "&page=";
            }
        } else if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return URL + "search?text=" + KEY + "&pg=";
        } else if (urlPattern1.matcher(URL).find()) {
            if (URL.contains("fit:8")
                    || URL.contains("zjj.life")
                    || URL.contains("love9989")
                    || URL.contains("8d8q")
                    || URL.contains("lk.pxun")
                    || URL.contains("hgyx")
                    || URL.contains("521x5")
                    || URL.contains("lxyyy")
                    || URL.contains("0818tv")
                    || URL.contains("diyoui")
                    || URL.contains("diliktv")
                    || URL.contains("ppzhu")
                    || URL.contains("aitesucai")
                    || URL.contains("zz.ci")
                    || URL.contains("chxjon")
                    || URL.contains("watchmi")
                    || URL.contains("vipbp")
                    || URL.contains("wsba")
                    || URL.contains("xfykl")) {
                return URL + "?ac=list&" + "wd=" + KEY + "&page=";
            } else {
                return URL + "?ac=list&" + "zm=" + KEY + "&page=";
            }
        }
        return "";
    }

    // ######UA
    private static Pattern snifferMatch = Pattern.compile("http((?!http).){26,}?\\.(m3u8|mp4)\\?.*|http((?!http).){26,}\\.(m3u8|mp4)|http((?!http).){26,}?/m3u8\\?pt=m3u8.*|http((?!http).)*?default\\.ixigua\\.com/.*|http((?!http).)*?cdn-tos[^\\?]*|http((?!http).)*?/obj/tos[^\\?]*|http.*?/player/m3u8play\\.php\\?url=.*|http.*?/player/.*?[pP]lay\\.php\\?url=.*|http.*?/playlist/m3u8/\\?vid=.*|http.*?\\.php\\?type=m3u8&.*|http.*?/download.aspx\\?.*|http.*?/api/up_api.php\\?.*|https.*?\\.66yk\\.cn.*");
    private static Pattern urlPattern1 = Pattern.compile("api\\.php/.*?/vod");
    private static Pattern parsePattern = Pattern.compile("/.+\\?.+=");
    private static Pattern parsePattern1 = Pattern.compile(".*(url|v|vid|php\\?id)=");
    private static Pattern parsePattern2 = Pattern.compile("https?://[^/]*");
    private static Pattern[] parseMatchForDotVod = new Pattern[]{
            Pattern.compile("jx\\.+huimaojia\\.+com/player"),
            Pattern.compile("py\\.+789pan\\.+cn/player/tm\\.php\\?url="),
            Pattern.compile("ztys\\.+waruanzy\\.+com/player/\\?url="),
            Pattern.compile("yingshi\\.+waruanzy\\.+com/789pan/\\?url="),
            Pattern.compile("vip\\.+parwix\\.+com:4433/player/\\?url="),
            Pattern.compile("api\\.+cxitco\\.+cn"),
            Pattern.compile("/vip\\.+renrenmi.cc"),
            Pattern.compile("yanbing\\.+parwix\\.+com:4433/player"),
            Pattern.compile("json\\.+cantin\\.+cc/apijson\\.php"),
            Pattern.compile("ffdm\\.+miaoletv\\.+com/\\?url="),
            Pattern.compile("vip\\.+sylwl\\.+cn/api/\\?key="),
            Pattern.compile("jx\\.+dikotv\\.+com/\\?url="),
            Pattern.compile("zly\\.+xjqxz\\.+top/player/\\?url="),
            Pattern.compile("5znn\\.+xyz/m3u8\\.+php"),
            Pattern.compile("uid=1735&my="),
            Pattern.compile("api\\.+iopenyun\\.+com:88/vip/\\?url="),
            Pattern.compile("api\\.+xkvideo\\.+design/m3u8\\.+php\\?url="),
            Pattern.compile("保佑")
    };

    private static Pattern[] htmlVideoKeyMatch = new Pattern[]{
            Pattern.compile("<div id=\"video\""),
            Pattern.compile("<div id=\"[^\"]*?player\""),
            Pattern.compile("//视频链接"),
            Pattern.compile("HlsJsPlayer\\("),
            Pattern.compile("<iframe[\\s\\S]*?src=\"[^\"]+?\""),
            Pattern.compile("<video[\\s\\S]*?src=\"[^\"]+?\"")
    };

    private String UA(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return "Dart/2.13 (dart:io)";
        } else if (URL.contains(".vod")) {
            return "okhttp/4.1.0";
        } else {
            return "Dalvik/2.1.0";
        }
    }

    // ######POST
    public String post(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return "";
        } else if (URL.contains(".vod")) {
            return "";
        } else {
            return "";
        }
    }

    // ######cookie
    public String cookie(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return "";
        } else if (URL.contains(".vod")) {
            return "";
        } else {
            return "";
        }
    }

    // ######获取分类地址
    public String getCateUrl(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return URL + "nav?token=";
        } else if (URL.contains(".vod")) {
            return URL + "/types";
        } else {
            return "";
        }
    }

    // ######分类筛选前缀地址
    public String getCateFilterUrlPrefix(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            if (URL.contains("fantuan.tv")) {
                URL = "https://fantuan.wkfile.com/api.php/app/";
                return URL + "video?tid=";
            } else {
                return URL + "video?tid=";
            }
        } else if (URL.contains(".vod")) {
            if (URL.contains("iopenyun")) {
                return URL + "/list?type=";
            } else {
                return URL + "?type=";
            }
        } else {
            return URL + "?ac=list&class=";
        }
    }

    // ######分类筛选后缀地址
    public String getCateFilterUrlSuffix(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return "&class=类型&area=地区&lang=语种&year=年份&limit=20&pg=#PN#";
        } else if (URL.contains(".vod")) {
            return "&class=类型&area=地区&lang=语种&year=年份&by=排序&limit=20&page=#PN#";
        } else {
            return "&page=#PN#&area=地区&type=类型&start=年份";
        }
    }

    // ######筛选内容
    public String getFilterTypes(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return "类型+全部=+喜剧+爱情+恐怖+动作+科幻+剧情+战争+警匪+犯罪+动画+奇幻+武侠+冒险+枪战+恐怖+悬疑+惊悚+经典+青春+文艺+微电影+古装+历史+运动+农村+惊悚+伦理+情色+福利+惊悚+儿童+网络电影\n地区+全部=+中国大陆+香港+台湾+美国+英国+法国+日本+韩国+德国+泰国+印度+西班牙+加拿大+其他\n语种+全部=+国语+英语+粤语+闽南语+韩语+日语+法语+德语+其他\n年份+全部=+2021+2020+2019+2018+2017+2016+2015+2014+2013+2012+2011+2010+2009+2008+2007+2006+2005+2004+2003+2002+2001+2000";
        } else if (URL.contains(".vod")) {
            return "类型+全部=+喜剧+爱情+恐怖+动作+科幻+剧情+战争+警匪+犯罪+动画+奇幻+武侠+冒险+枪战+恐怖+悬疑+惊悚+经典+青春+文艺+微电影+古装+历史+运动+农村+惊悚+伦理+情色+福利+惊悚+儿童+网络电影\n地区+全部=+中国大陆+香港+台湾+美国+英国+法国+日本+韩国+德国+泰国+印度+西班牙+加拿大+其他\n语种+全部=+国语+英语+粤语+闽南语+韩语+日语+法语+德语+其他\n年份+全部=+2021+2020+2019+2018+2017+2016+2015+2014+2013+2012+2011+2010+2009+2008+2007+2006+2005+2004+2003+2002+2001+2000\n排序+全部=+最新=time+最热=hits+评分=score";
        } else {
            return "分类+电影=movie+连续剧=tvplay+综艺=tvshow+动漫=comic+4K=movie_4k+体育=tiyu\n类型+全部=+喜剧+爱情+恐怖+动作+科幻+剧情+战争+警匪+犯罪+动画+奇幻+武侠+冒险+枪战+恐怖+悬疑+惊悚+经典+青春+文艺+微电影+古装+历史+运动+农村+惊悚+惊悚+伦理+情色+福利+儿童+网络电影\n地区+全部=+大陆+香港+台湾+美国+英国+法国+日本+韩国+德国+泰国+印度+西班牙+加拿大+其他\n年份+全部=+2021+2020+2019+2018+2017+2016+2015+2014+2013+2012+2011+2010+2009+2008+2007+2006+2005+2004+2003+2002+2001+2000";
        }
    }

    // ######推荐地址
    public String getRecommendUrl(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            return URL + "index_video?token=";
        } else if (URL.contains(".vod")) {
            return URL + "/vodPhbAll";
        } else {
            return "";
        }
    }

    // ######播放器前缀地址
    public String getPlayUrlPrefix(String URL) {
        if (URL.contains("api.php/app") || URL.contains("xgapp.php/v1")) {
            if (URL.contains("fantuan.tv")) {
                URL = "https://fantuan.wkfile.com/api.php/app/";
                return URL + "video_detail?id=";
            } else {
                return URL + "video_detail?id=";
            }
        } else if (URL.contains(".vod")) {
            if (URL.contains("iopenyun")) {
                return URL + "/detailID?vod_id=";
            } else {
                return URL + "/detail?vod_id=";
            }
        } else {
            return "";
        }
    }

    // ######选集
    private HashMap<String, String> parseUrlMap = new HashMap<>();

    private void genPlayList(String URL, JSONObject object, String json, JSONObject vod, String vid) throws JSONException {
        ArrayList<String> playUrls = new ArrayList<>();
        ArrayList<String> playFlags = new ArrayList<>();
        if (URL.contains("api.php/app/")) {
            JSONObject data = object.getJSONObject("data");
            vod.put("vod_id", data.optString("vod_id", vid));
            vod.put("vod_name", data.getString("vod_name"));
            vod.put("vod_pic", data.getString("vod_pic"));
            vod.put("type_name", data.optString("vod_class"));
            vod.put("vod_year", data.optString("vod_year"));
            vod.put("vod_area", data.optString("vod_area"));
            vod.put("vod_remarks", data.optString("vod_remarks"));
            vod.put("vod_actor", data.optString("vod_actor"));
            vod.put("vod_director", data.optString("vod_director"));
            vod.put("vod_content", data.optString("vod_content"));
            JSONArray vodUrlWithPlayer = data.getJSONArray("vod_url_with_player");
            for (int i = 0; i < vodUrlWithPlayer.length(); i++) {
                JSONObject from = vodUrlWithPlayer.getJSONObject(i);
                String flag = stopVipFlag(from.getString("name"));
                playFlags.add(flag);
                playUrls.add(from.getString("url"));
                String purl = from.optString("parse_api");
                if (purl.contains("jpg.hou.lu/jm/za/index.php")) {
                    purl = "http://vip.mengx.vip/home/api?type=ys&uid=3249696&key=aefqrtuwxyEFHKNOQY&url=";
                }
                parseUrlMap.put(flag, purl);
            }
        } else if (URL.contains("xgapp.php/v1/")) {
            JSONObject data = object.getJSONObject("data").getJSONObject("vod_info");
            vod.put("vod_id", data.optString("vod_id", vid));
            vod.put("vod_name", data.getString("vod_name"));
            vod.put("vod_pic", data.getString("vod_pic"));
            vod.put("type_name", data.optString("vod_class"));
            vod.put("vod_year", data.optString("vod_year"));
            vod.put("vod_area", data.optString("vod_area"));
            vod.put("vod_remarks", data.optString("vod_remarks"));
            vod.put("vod_actor", data.optString("vod_actor"));
            vod.put("vod_director", data.optString("vod_director"));
            vod.put("vod_content", data.optString("vod_content"));
            JSONArray vodUrlWithPlayer = data.getJSONArray("vod_url_with_player");
            for (int i = 0; i < vodUrlWithPlayer.length(); i++) {
                JSONObject from = vodUrlWithPlayer.getJSONObject(i);
                String flag = stopVipFlag(from.getString("name"));
                playFlags.add(flag);
                playUrls.add(from.getString("url"));
                String purl = from.optString("parse_api");
                if (purl.contains("jpg.hou.lu/jm/za/index.php")) {
                    purl = "http://vip.mengx.vip/home/api?type=ys&uid=3249696&key=aefqrtuwxyEFHKNOQY&url=";
                }
                parseUrlMap.put(flag, purl);
            }
        } else if (URL.contains(".vod")) {
            JSONObject data = object.getJSONObject("data");
            vod.put("vod_id", data.optString("vod_id", vid));
            vod.put("vod_name", data.getString("vod_name"));
            vod.put("vod_pic", data.getString("vod_pic"));
            vod.put("type_name", data.optString("vod_class"));
            vod.put("vod_year", data.optString("vod_year"));
            vod.put("vod_area", data.optString("vod_area"));
            vod.put("vod_remarks", data.optString("vod_remarks"));
            vod.put("vod_actor", data.optString("vod_actor"));
            vod.put("vod_director", data.optString("vod_director"));
            vod.put("vod_content", data.optString("vod_content"));
            JSONArray vodUrlWithPlayer = data.getJSONArray("vod_play_list");
            for (int i = 0; i < vodUrlWithPlayer.length(); i++) {
                JSONObject from = vodUrlWithPlayer.getJSONObject(i);
                String flag = stopVipFlag(from.getJSONObject("player_info").getString("show"));
                playFlags.add(flag);
                playUrls.add(from.getString("url"));
                try {
                    ArrayList<String> parses = new ArrayList<>();
                    String[] parse1 = from.getJSONObject("player_info").optString("parse").split(",");
                    String[] parse2 = from.getJSONObject("player_info").optString("parse2").split(",");
                    for (String p : parse1) {
                        if (parsePattern.matcher(p).find()) {
                            boolean add = true;
                            for (Pattern pt : parseMatchForDotVod) {
                                if (pt.matcher(p).find()) {
                                    add = false;
                                    break;
                                }
                            }
                            if (add) {
                                parses.add(p);
                            }
                        }
                    }
                    for (String p : parse2) {
                        if (parsePattern.matcher(p).find()) {
                            boolean add = true;
                            for (Pattern pt : parseMatchForDotVod) {
                                if (pt.matcher(p).find()) {
                                    add = false;
                                    break;
                                }
                            }
                            if (add) {
                                parses.add(p);
                            }
                        }
                    }
                    if (parses.size() > 0 && parses.get(0).contains("=")) {
                        String purl = parses.get(0);
                        if (purl.contains("http")) {
                            Matcher matcher = parsePattern1.matcher(purl);
                            if (matcher.find()) {
                                purl = matcher.group(0).replace("..", ".").replace("vip.aotian.love", "vip.gaotian.love");
                            }
                        } else if (purl.contains("//")) {
                            Matcher matcher = parsePattern1.matcher(purl);
                            if (matcher.find()) {
                                purl = "http:" + matcher.group(0).replace("..", ".");
                            }
                        } else {
                            Matcher matcher = parsePattern2.matcher(URL);
                            if (matcher.find()) {
                                Matcher matcher1 = parsePattern1.matcher(URL);
                                if (matcher1.find()) {
                                    purl = matcher.group(0) + matcher1.group(0).replace("..", ".");
                                }
                            }
                        }
                        parseUrlMap.put(flag, purl);
                    } else {
                        parseUrlMap.put(flag, "http://egwang186.gitee.io/?url=");
                    }
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
        } else if (urlPattern1.matcher(URL).find()) {
            JSONObject data = object;
            vod.put("vod_id", data.optString("vod_id", vid));
            vod.put("vod_name", data.getString("title"));
            vod.put("vod_pic", data.getString("img_url"));
            vod.put("type_name", jsonArr2Str(data.optJSONArray("type")));
            vod.put("vod_year", data.optString("pubtime"));
            vod.put("vod_area", jsonArr2Str(data.optJSONArray("area")));
            vod.put("vod_remarks", data.optString("trunk"));
            vod.put("vod_actor", jsonArr2Str(data.optJSONArray("actor")));
            vod.put("vod_director", jsonArr2Str(data.optJSONArray("director")));
            vod.put("vod_content", data.optString("intro"));
            JSONObject playList = data.getJSONObject("videolist");
            Iterator<String> playListKeys = playList.keys();
            while (playListKeys.hasNext()) {
                String from = playListKeys.next();
                JSONArray playListUrls = playList.getJSONArray(from);
                ArrayList<String> urls = new ArrayList<>();
                for (int j = 0; j < playListUrls.length(); j++) {
                    JSONObject urlObj = playListUrls.getJSONObject(j);
                    urls.add(urlObj.getString("title") + "$" + urlObj.getString("url"));
                }
                playFlags.add(stopVipFlag(from));
                playUrls.add(TextUtils.join("#", urls));
            }
        }
//        for (int i = 0; i < playFlags.size(); i++) {
//            String pu = playUrls.get(i).split("#")[0].split("\\$")[1];
//            System.out.println(pu + " is video?" + isVideoFormat(pu));
//            playerContent(playFlags.get(i), pu, new ArrayList<>());
//        }
        vod.put("vod_play_from", TextUtils.join("$$$", playFlags));
        vod.put("vod_play_url", TextUtils.join("$$$", playUrls));
    }

    private static HashMap<String, String> fakeVips = null;

    private String stopVipFlag(String flag) {
        if (fakeVips == null) {
            fakeVips = new HashMap<>();
            fakeVips.put("youku", "优酷M");
            fakeVips.put("qq", "腾讯M");
            fakeVips.put("iqiyi", "爱奇艺M");
            fakeVips.put("qiyi", "奇艺M");
            fakeVips.put("letv", "乐视M");
            fakeVips.put("sohu", "搜狐M");
            fakeVips.put("tudou", "土豆M");
            fakeVips.put("pptv", "PPTVM");
            fakeVips.put("mgtv", "芒果TVM");
            fakeVips.put("wasu", "华数M");
            fakeVips.put("bilibili", "哔哩M");
        }
        if (fakeVips.containsKey(flag)) {
            return fakeVips.get(flag);
        }
        return flag;
    }

    private String getParseUrl(String URL, String flag) {
        String parseUrl = "";
        if (URL.contains(".vod")) {
            if (parseUrlMap.containsKey(flag))
                parseUrl = parseUrlMap.get(flag);
        } else if (URL.contains("cokemv")) {
            parseUrl = "https://player.90mm.me/play.php?url=";
        } else if (URL.contains("api.php/app/") || URL.contains("xgapp.php/v1/")) {
            if (parseUrlMap.containsKey(flag))
                parseUrl = parseUrlMap.get(flag);
        } else {
            parseUrl = URL;
        }
        if (parseUrl.contains("svip.jhyun.jx.cn") || parseUrl.contains("svip.jhdyw.vip") || parseUrl.contains("v.jhdyw.vip/nhdz666")) {
            if (flag.contains("人人迷")) {
                parseUrl = "http://www.1080kan.cc/jiexi/rrmi.php?url=";
            } else if (flag.contains("人人")) {
                parseUrl = "http://www.1080kan.cc/jiexi/rr.php?url=";
            } else if (flag.contains("番茄")) {
                parseUrl = "http://www.1080kan.cc/jiexi/fq.php?url=";
            } else {
                parseUrl = "https://api.m3u8.tv:5678/home/api?type=ys&uid=233711&key=bgjnopvDHPUY035689&url=";
            }
        } else if (parseUrl.contains("jhsj.manduhu.com")) {
            parseUrl = "https://api.m3u8.tv:5678/home/api?type=ys&uid=233711&key=bgjnopvDHPUY035689&url=";
        }
        return parseUrl;
    }

    private String getPlayerUrl(String apiUrl, String parseUrl, String playUrl) {
        if (apiUrl.contains("xgapp.php/v1/") || apiUrl.contains("api.php/app/") || apiUrl.contains(".vod")) {
            if (playUrl.indexOf(".m3u8") > 15 || playUrl.indexOf(".mp4") > 15 || playUrl.contains("/obj/tos")) {
                return "https://www.baidu.com/s?wd=" + playUrl;
            } else {
                return "https://www.baidu.com/s?wd=" + parseUrl + playUrl;
            }
        } else if (urlPattern1.matcher(apiUrl).find()) {
            if (apiUrl.contains("fit:8") || apiUrl.contains("diliktv.xyz") || apiUrl.contains("ppzhu.vip") || apiUrl.contains("api.8d8q.com")) {
                return "https://www.baidu.com/s?wd=" + playUrl + "&app=10003&account=272775028&password=qq272775028";
            } else {
                if (playUrl.contains("=") || playUrl.indexOf(".m3u8") > 15 || playUrl.indexOf(".mp4") > 15 || playUrl.contains("/obj/tos")) {
                    return playUrl;
                } else {
                    return "https://www.baidu.com/s?wd=https://api.m3u8.tv:5678/home/api?type=ys&uid=233711&key=bgjnopvDHPUY035689&url=" + playUrl;
                }
            }
        } else if (playUrl.contains("http")) {
            return playUrl;
        } else {
            return parseUrl + playUrl;
        }
    }

    // ######视频地址
    private void getFinalVideo(String uu, JSONObject result) throws JSONException {
        if (uu.contains("1080kan.cc/jiexi/")) {
            result.put("parse", 1);
            result.put("playUrl", "");
            result.put("url", uu.split("wd=")[1]);
            result.put("header", "{\"Referer\":\"http://www.1080kan.cc/\"}");
        } else if (uu.contains("baidu.com")) {
            String playurl = uu.split("wd=")[1];
            if (playurl.contains("duoduozy.com")) {
                String uuu = "https://player.duoduozy.com/ddplay/?url=" + playurl;
                HashMap<String, String> headers = new HashMap();
                headers.put("referer", "https://www.duoduozy.com/");
                SpiderReqResult srr = SpiderReq.get(new SpiderUrl(uuu, headers));
                Matcher matcher = Pattern.compile("var urls.+?\"(.+?)\"").matcher(srr.content);
                if (matcher.find()) {
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", matcher.group(1));
                } else {
                    result.put("parse", 1);
                    result.put("playUrl", "");
                    result.put("url", playurl);
                    result.put("header", "{\"Referer\":\"https://www.duoduozy.com/\"}");
                }
            } else if (playurl.contains("cat.wkfile.com")) {
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", playurl);
                result.put("header", "{\"User-Agent\":\" Mozilla/5.0\",\"Referer\":\" https://qian.wkfile.com/\"}");
            } else if (!playurl.contains("=") && playurl.indexOf(".m3u8") > 15 || playurl.indexOf(".mp4") > 15 || playurl.contains("/obj/tos")) {
                Matcher matcher = Pattern.compile(".*(http.*)").matcher(playurl);
                if (matcher.find()) {
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", matcher.group(1));
                } else {
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", playurl);
                }
            } else if (playurl.contains("=")) {
                SpiderReqResult resp = SpiderReq.get(SpiderOKClient.noRedirectClient(), new SpiderUrl(playurl, null));
                String redLoc = SpiderOKClient.getRedirectLocation(resp.headers);
                if (redLoc != null) {
                    String finalurl = "";
                    while (redLoc != null) {
                        finalurl = redLoc;
                        if (redLoc.indexOf(".mp4") > 30) {
                            break;
                        } else {
                            HashMap<String, String> headers = new HashMap();
                            headers.put("User-Agent", "Mozilla/5.0 Android");
                            resp = SpiderReq.get(SpiderOKClient.noRedirectClient(), new SpiderUrl(finalurl, headers));
                            redLoc = SpiderOKClient.getRedirectLocation(resp.headers);
                        }
                    }
                    String realurl = finalurl;
                    if (realurl.contains("=http") || realurl.contains("url=")) {
                        if (resp.content.contains("<html")) {
                            result.put("parse", 1);
                            result.put("playUrl", "");
                            result.put("url", realurl);
                        } else {
                            JSONObject obj = new JSONObject(resp.content);
                            String ppurl = obj.getString("url");
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", ppurl);
                            if (realurl.contains("mgtv.com")) {
                                result.put("header", "{\"User-Agent\":\" Mozilla/5.0\", \"Referer\":\" \"}");
                            } else if (realurl.contains("bilibili.com")) {
                                result.put("header", "{\"User-Agent\":\" Mozilla/5.0\", \"Referer\":\" https://www.bilibili.com/\"}");
                            }
                        }
                    } else {
                        if (playurl.contains("www.mgtv.com")) {
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", realurl);
                            result.put("header", "{\"User-Agent\":\" Mozilla/5.0\", \"Referer\":\" \"}");
                        } else {
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", realurl);
                        }
                    }
                } else {
                    if (resp.content.contains("<html")) {
                        boolean sniffer = false;
                        for (Pattern p : htmlVideoKeyMatch) {
                            if (p.matcher(resp.content).find()) {
                                sniffer = true;
                                break;
                            }
                        }
                        if (sniffer) {
                            result.put("parse", 1);
                            result.put("playUrl", "");
                            result.put("url", playurl);
                        } else {
                            if (playurl.split("url=")[1].contains("http")) {
                                result.put("parse", 1);
                                result.put("playUrl", "");
                                result.put("url", "http://egwang186.gitee.io/?url=" + playurl.split("url=")[1]);
                            } else if (playurl.split("url=")[1].contains("renrenmi")) {
                                result.put("parse", 1);
                                result.put("playUrl", "");
                                result.put("url", "http://www.1080kan.cc/jiexi/rrmi.php?url=" + playurl.split("url=")[1]);
                                result.put("header", "{\"Referer\":\"http://www.1080kan.cc/\"}");
                            } else {
                                String id = playurl.split("url=")[1];
                                String uuu = "https://vip.gaotian.love/api/?key=sRy0QAq8hqXRlrEtrq&url=" + id;
                                resp = SpiderReq.get(new SpiderUrl(uuu, null));
                                JSONObject obj = new JSONObject(resp.content);
                                String realurl = obj.optString("url", "");
                                if (realurl.isEmpty())
                                    realurl = obj.optString("msg", "");
                                result.put("parse", 0);
                                result.put("playUrl", "");
                                result.put("url", realurl);
                            }
                        }
                    } else {
                        String jsonUrl = "";
                        try {
                            JSONObject obj = new JSONObject(resp.content);
                            jsonUrl = obj.optString("url");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (jsonUrl.length() > 1) {
                            String realurl = jsonUrl;
                            if (playurl.contains("mgtv.com")) {
                                result.put("parse", 0);
                                result.put("playUrl", "");
                                result.put("url", realurl);
                                result.put("header", "{\"User-Agent\":\" Mozilla/5.0\", \"Referer\":\" \"}");
                            } else if (playurl.contains("bilibili.com")) {
                                result.put("parse", 0);
                                result.put("playUrl", "");
                                result.put("url", realurl);
                                result.put("header", "{\"User-Agent\":\" Mozilla/5.0\", \"Referer\":\" https://www.bilibili.com/\"}");
                            } else {
                                result.put("parse", 0);
                                result.put("playUrl", "");
                                result.put("url", realurl);
                            }
                        } else if (playurl.split("url=")[1].contains("http")) {
                            result.put("parse", 1);
                            result.put("playUrl", "");
                            result.put("url", "http://egwang186.gitee.io/?url=" + playurl.split("url=")[1]);
                        } else if (playurl.split("url=")[1].contains("renrenmi")) {
                            result.put("parse", 1);
                            result.put("playUrl", "");
                            result.put("url", "http://www.1080kan.cc/jiexi/rrmi.php?url=" + playurl.split("url=")[1]);
                            result.put("header", "{\"Referer\":\"http://www.1080kan.cc/\"}");
                        } else {
                            String id = playurl.split("url=")[1];
                            String uuu = "https://vip.gaotian.love/api/?key=sRy0QAq8hqXRlrEtrq&url=" + id;
                            resp = SpiderReq.get(new SpiderUrl(uuu, null));
                            JSONObject obj = new JSONObject(resp.content);
                            String realurl = obj.optString("url", "");
                            if (realurl.isEmpty())
                                realurl = obj.optString("msg", "");
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", realurl);
                        }
                    }
                }
            } else {
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", playurl);
            }
        } else {
            result.put("parse", 1);
            result.put("playUrl", "");
            result.put("url", uu);
        }
    }

    @Override
    public boolean manualVideoCheck() {
        return true;
    }

    @Override
    public boolean isVideoFormat(String url) {
        return snifferMatch.matcher(url).find();
    }
}
