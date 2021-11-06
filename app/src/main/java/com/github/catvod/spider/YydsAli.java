package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.crawler.SpiderReq;
import com.github.catvod.crawler.SpiderReqResult;
import com.github.catvod.crawler.SpiderUrl;
import com.github.catvod.okhttp.SpiderOKClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class YydsAli extends XPath {
    Pattern aliyun = Pattern.compile(">.+aliyundrive.com/s/(\\S+)</a");
    Pattern aliyunShort = Pattern.compile(">.+alywp.net/(\\S+)</a");
    Pattern aliyunUrl = Pattern.compile(".+aliyundrive.com/s/(\\S+)");

    @Override
    protected void detailContentExt(String content, JSONObject vod) {
        super.detailContentExt(content, vod);
        try {
            String shareId = null;
            Matcher matcher = aliyun.matcher(content);
            if (matcher.find()) {
                shareId = matcher.group(1);
            } else {
                matcher = aliyunShort.matcher(content);
                if (matcher.find()) {
                    shareId = matcher.group(1);
                    SpiderReqResult resp = SpiderReq.get(SpiderOKClient.noRedirectClient(), new SpiderUrl("https://alywp.net/" + shareId, null));
                    shareId = SpiderOKClient.getRedirectLocation(resp.headers);
                    if (shareId != null) {
                        matcher = aliyunUrl.matcher(shareId);
                        if (matcher.find()) {
                            shareId = matcher.group(1);
                        }
                    }
                }
            }
            if (shareId != null) {
                String shareToken = getShareTk(shareId, "");
                ArrayList<String> vodItems = new ArrayList<>();
                accessTk = "";
                getFileList(shareToken, shareId, "", "root", vodItems);
                vod.put("vod_play_from", "阿里云盘");
                vod.put("vod_play_url", TextUtils.join("#", vodItems));
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            String[] infos = id.split("\\+");
            String shareTk = getShareTk(infos[0], "");
            refreshTk();
            if (!accessTk.isEmpty()) {
                JSONObject json = new JSONObject();
                json.put("share_id", infos[0]);
                json.put("category", "live_transcoding");
                json.put("file_id", infos[1]);
                json.put("template_id", "");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("x-share-token", shareTk);
                headers.put("authorization", accessTk);
                SpiderReqResult srr = SpiderReq.postBody("https://api.aliyundrive.com/v2/file/get_share_link_video_preview_play_info", RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString()), headers);
                JSONArray playList = new JSONObject(srr.content).getJSONObject("video_preview_play_info").getJSONArray("live_transcoding_task_list");
                String videoUrl = "";
                String[] orders = new String[]{"FHD", "HD", "SD"};
                for (String or : orders) {
                    for (int i = 0; i < playList.length(); i++) {
                        JSONObject obj = playList.getJSONObject(i);
                        if (obj.optString("template_id").equals(or)) {
                            videoUrl = obj.getString("url");
                            break;
                        }
                    }
                    if (!videoUrl.isEmpty())
                        break;
                }
                if (videoUrl.isEmpty() && playList.length() > 0) {
                    videoUrl = playList.getJSONObject(0).getString("url");
                }
                JSONObject headerObj = new JSONObject();
                headerObj.put("user-agent", " Dalvik/2.1.0 (Linux; U; Android 7.0; ZTE BA520 Build/MRA58K)");
                headerObj.put("referer", " https://www.aliyundrive.com/");
                JSONObject result = new JSONObject();
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", videoUrl);
                result.put("header", headerObj.toString());
                return result.toString();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    private String aliTk = "";

    private void fetchAliTk() {
        if (aliTk.isEmpty()) {
            try {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "okhttp/4.5.0");
                SpiderReqResult srr = SpiderReq.get(new SpiderUrl("http://81.68.244.5/tv/alitk", headers));
                aliTk = new JSONObject(srr.content).optString("alitk");
            } catch (JSONException e) {
                SpiderDebug.log(e);
            }
        }
    }

    private String accessTk = "";

    private void refreshTk() {
        fetchAliTk();
        if (!aliTk.isEmpty() && accessTk.isEmpty()) {
            try {
                HashMap<String, String> json = new HashMap<>();
                json.put("refresh_token", aliTk);
                SpiderReqResult srr = SpiderReq.postJson("https://api.aliyundrive.com/token/refresh", json, new HashMap<>());
                JSONObject obj = new JSONObject(srr.content);
                accessTk = obj.getString("token_type") + " " + obj.getString("access_token");
            } catch (JSONException e) {
                SpiderDebug.log(e);
            }
        }
    }

    private String getShareTk(String shareId, String sharePwd) {
        try {
            HashMap<String, String> json = new HashMap<>();
            json.put("share_id", shareId);
            json.put("share_pwd", "");
            SpiderReqResult srr = SpiderReq.postJson("https://api.aliyundrive.com/v2/share_link/get_share_token", json, new HashMap<>());
            return new JSONObject(srr.content).optString("share_token");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    private void getFileList(String shareTk, String shareId, String sharePwd, String root, ArrayList<String> vodItems) {
        try {
            // 取文件夹
            JSONObject json = new JSONObject();
            json.put("share_id", shareId);
            json.put("parent_file_id", root);
            json.put("limit", 100);
            json.put("image_thumbnail_process", "image/resize,w_160/format,jpeg");
            json.put("image_url_process", "image/resize,w_1920/format,jpeg");
            json.put("video_thumbnail_process", "video/snapshot,t_1000,f_jpg,ar_auto,w_300");
            json.put("order_by", "name");
            json.put("order_direction", "ASC");
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-share-token", shareTk);
            SpiderReqResult srr = SpiderReq.postBody("https://api.aliyundrive.com/adrive/v3/file/list", RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString()), headers);
            JSONArray rootList = new JSONObject(srr.content).optJSONArray("items");
            if (rootList != null && rootList.length() > 0) {
                for (int i = 0; i < rootList.length(); i++) {
                    JSONObject item = rootList.getJSONObject(i);
                    if (item.getString("type").equals("folder")) {
                        String dirId = item.getString("file_id");
                        getFileList(shareTk, shareId, sharePwd, dirId, vodItems);
                    } else {
                        if (item.getString("type").equals("file") && !item.getString("file_extension").equals("txt")) {
                            String fileId = item.getString("file_id");
                            String fileName = item.getString("name");
                            vodItems.add(fileName + "$" + shareId + "+" + fileId);
                        }
                    }
                }
            }
            // 取文件列表
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }
}
