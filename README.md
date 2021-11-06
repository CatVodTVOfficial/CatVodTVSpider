# 猫影视TV客户端爬虫自定义接口工程

```
思维没有边界 一切皆有可能
```

本工程最终食用需配合 猫影视TV新版（一下简称为软件） **v2.0.0**及以上版本。

![logo](app/src/main/res/drawable-xhdpi/app_icon.png)

### 欢迎各路大佬踊跃提PR，分享爬虫代码。

### 这里是用户分享的爬虫代码打包的共享包，可以配合自定义配置，直接食用 [custom_spider.jar](/jar/)

## 快速开始
----
本工程是一个完整的AndroidStudio工程，请你用AS打开编辑。 

工程调试完毕后要需要导出生成jar文件配合软件使用，执行根目录下的 `buildAndGenJar.bat` 会在`jar`目录生成一个名为`custom_spider.jar`的jar文件，这个文件就是我们最终要是用的代码包。

### 代码包食用方式
----
本地加载：将`custom_spider.jar`放入设备sd卡根目录即可。 **注意，如需本地加载，请手动赋予App存储空间读写权限，App默认不申请存储空间读写权限**

远程加载：将`custom_spider.jar`上传到你的网络空间，获取对应的文件下载地址，在软件自定义配置的json文件中加入下面格式的键值对。
```json 
"spider": "http://xxx.xxx.xxx/custom_spider.jar"
```
支持jar文件本地缓存（需v2.0.5及以上版本）
```json 
"spider": "http://xxx.xxx.xxx/custom_spider.jar;md5;jar文件md5"
// 例如
"spider": "https://github.com/catvod/CatVodTVSpider/blob/master/jar/custom_spider.jar?raw=true;md5;c6ed6bc8285f0aca90e7cb3abf7f9caa",
```

### 如何在自定义配置中调用我们代码包中的Spider
----
同样在自定义json中加入相应的播放源即可，**type=3, api对应你代码工程中自定义的爬虫类名（api必须是`csp_`开头），例如实例工程中的`Aidi`**
```json
{
    "key": "csp_Aidi",
    "name": "爱迪",
    "type": 3,
    "api": "csp_Aidi",
    "searchable": 1,
    "quickSearch": 0,
    "filterable": 1
}
```
---
**Json解析扩展（需v2.0.2及以上版本）**

通过jar包可以实现json解析并发、轮询等相关功能，**参与并发和轮询的json解析地址，默认为解析地址列表中的所有json解析（即type=1）**。

在自定义json中的`parse`里加入相应的解析配置（type=2）即可启用。**调用扩展类的名称配置在`parse`的`url`字段里，例如扩展类`JsonParallel`的json配置`url`字段值为`Parallel`**。如下：

```json
{
    "name": "Json并发",
    "type": 2,
    "url": "Parallel"
},
{
    "name": "Json轮询",
    "type": 2,
    "url": "Sequence"
}
```

**XPath规则套娃（需v2.0.4及以上版本）**
套娃规则祥见[XPath.md](/XPath.md)

---

**M浏览器中APP影视规则支持（测试版）**（2021.10.30 by 小黄瓜）支持筛选，接口太多，建议自测，哪个能用用哪个。

代码里读取<a href="https://inmemory.coding.net/p/InMemory/d/MBrowser/git/raw/master/AppFile/APP%E5%BD%B1%E8%A7%86%E5%88%97%E8%A1%A8" target="_blank">APP影视配置</a>中的Api接口

所以你需要在自定义配置中增加相关站配置，ext字段填APP影视配置json中的`title`。

<details>
<summary>配置代码 点我展开</summary>

```json
{"key":"csp_appys_xiaogui_007影视", "name":"007影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_007影视"},
{"key":"csp_appys_xiaogui_555电影", "name":"555电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_555电影"},
{"key":"csp_appys_xiaogui_5060影院", "name":"5060影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_5060影院"},
{"key":"csp_appys_xiaogui_913e影视", "name":"913e影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_913e影视"},
{"key":"csp_appys_xiaogui_BD电影", "name":"BD电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_BD电影"},
{"key":"csp_appys_xiaogui_COKEMV", "name":"COKEMV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_COKEMV"},
{"key":"csp_appys_xiaogui_one影视", "name":"one影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_one影视"},
{"key":"csp_appys_xiaogui_爱尚影视", "name":"爱尚影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_爱尚影视"},
{"key":"csp_appys_xiaogui_爱影吧", "name":"爱影吧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_爱影吧"},
{"key":"csp_appys_xiaogui_播放呀", "name":"播放呀(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_播放呀"},
{"key":"csp_appys_xiaogui_畅视影视", "name":"畅视影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_畅视影视"},
{"key":"csp_appys_xiaogui_迪迪影院", "name":"迪迪影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_迪迪影院"},
{"key":"csp_appys_xiaogui_嘀哩嘀哩", "name":"嘀哩嘀哩(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_嘀哩嘀哩"},
{"key":"csp_appys_xiaogui_毒舌电影", "name":"毒舌电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_毒舌电影"},
{"key":"csp_appys_xiaogui_饭团影院", "name":"饭团影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_饭团影院"},
{"key":"csp_appys_xiaogui_大师兄", "name":"大师兄(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_大师兄"},
{"key":"csp_appys_xiaogui_段友影视", "name":"段友影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_段友影视"},
{"key":"csp_appys_xiaogui_瓜皮TV", "name":"瓜皮TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_瓜皮TV"},
{"key":"csp_appys_xiaogui_海胆影视", "name":"海胆影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_海胆影视"},
{"key":"csp_appys_xiaogui_火箭影视", "name":"火箭影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_火箭影视"},
{"key":"csp_appys_xiaogui_海绵影视", "name":"海绵影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_海绵影视"},
{"key":"csp_appys_xiaogui_海棠视频", "name":"海棠视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_海棠视频"},
{"key":"csp_appys_xiaogui_京广航", "name":"京广航(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_京广航"},
{"key":"csp_appys_xiaogui_九合视频", "name":"九合视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_九合视频"},
{"key":"csp_appys_xiaogui_久九影视", "name":"久九影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_久九影视"},
{"key":"csp_appys_xiaogui_蓝果影视", "name":"蓝果影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_蓝果影视"},
{"key":"csp_appys_xiaogui_琳琅影视", "name":"琳琅影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_琳琅影视"},
{"key":"csp_appys_xiaogui_极乐阁", "name":"极乐阁(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_极乐阁"},
{"key":"csp_appys_xiaogui_抹茶猪", "name":"抹茶猪(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_抹茶猪"},
{"key":"csp_appys_xiaogui_萌蛋蛋", "name":"萌蛋蛋(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_萌蛋蛋"},
{"key":"csp_appys_xiaogui_迷你库", "name":"迷你库(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_迷你库"},
{"key":"csp_appys_xiaogui_思古影视", "name":"思古影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_思古影视"},
{"key":"csp_appys_xiaogui_双十电影", "name":"双十电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_双十电影"},
{"key":"csp_appys_xiaogui_天方影视", "name":"天方影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_天方影视"},
{"key":"csp_appys_xiaogui_天空影视", "name":"天空影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_天空影视"},
{"key":"csp_appys_xiaogui_糖果影视", "name":"糖果影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_糖果影视"},
{"key":"csp_appys_xiaogui_污妖动漫", "name":"污妖动漫(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_污妖动漫"},
{"key":"csp_appys_xiaogui_小龟视频", "name":"小龟视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_小龟视频"},
{"key":"csp_appys_xiaogui_星空影视", "name":"星空影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_星空影视"},
{"key":"csp_appys_xiaogui_雪人影视", "name":"雪人影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_雪人影视"},
{"key":"csp_appys_xiaogui_小易影视", "name":"小易影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_小易影视"},
{"key":"csp_appys_xiaogui_影视工场", "name":"影视工场(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_影视工场"},
{"key":"csp_appys_xiaogui_追剧达人", "name":"追剧达人(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"xiaogui_追剧达人"},
{"key":"csp_appys_v1_80影视", "name":"80影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_80影视"},
{"key":"csp_appys_v1_80K影视", "name":"80K影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_80K影视"},
{"key":"csp_appys_v1_CJT影院", "name":"CJT影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_CJT影院"},
{"key":"csp_appys_v1_HG影视", "name":"HG影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_HG影视"},
{"key":"csp_appys_v1_U5影视", "name":"U5影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_U5影视"},
{"key":"csp_appys_v1_VIP影视", "name":"VIP影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_VIP影视"},
{"key":"csp_appys_v1_爱影视", "name":"爱影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_爱影视"},
{"key":"csp_appys_v1_艾特影视", "name":"艾特影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_艾特影视"},
{"key":"csp_appys_v1_阿姨追剧", "name":"阿姨追剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_阿姨追剧"},
{"key":"csp_appys_v1_白菜追剧", "name":"白菜追剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_白菜追剧"},
{"key":"csp_appys_v1_比邻影视", "name":"比邻影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_比邻影视"},
{"key":"csp_appys_v1_百讯视频", "name":"百讯视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_百讯视频"},
{"key":"csp_appys_v1_沧海影视", "name":"沧海影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_沧海影视"},
{"key":"csp_appys_v1_菜鸟动漫", "name":"菜鸟动漫(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_菜鸟动漫"},
{"key":"csp_appys_v1_畅优视界", "name":"畅优视界(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_畅优视界"},
{"key":"csp_appys_v1_初心影视", "name":"初心影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_初心影视"},
{"key":"csp_appys_v1_多多影视", "name":"多多影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_多多影视"},
{"key":"csp_appys_v1_大海影视", "name":"大海影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_大海影视"},
{"key":"csp_appys_v1_东南影院", "name":"东南影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_东南影院"},
{"key":"csp_appys_v1_大熊追剧", "name":"大熊追剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_大熊追剧"},
{"key":"csp_appys_v1_段友影视", "name":"段友影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_段友影视"},
{"key":"csp_appys_v1_蝶众影院", "name":"蝶众影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_蝶众影院"},
{"key":"csp_appys_v1_二狗电影", "name":"二狗电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_二狗电影"},
{"key":"csp_appys_v1_饭后电影", "name":"饭后电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_饭后电影"},
{"key":"csp_appys_v1_飞捷影视", "name":"飞捷影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_飞捷影视"},
{"key":"csp_appys_v1_疯狂看", "name":"疯狂看(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_疯狂看"},
{"key":"csp_appys_v1_公主影视", "name":"公主影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_公主影视"},
{"key":"csp_appys_v1_辉哥影视", "name":"辉哥影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_辉哥影视"},
{"key":"csp_appys_v1_黄河影视", "name":"黄河影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_黄河影视"},
{"key":"csp_appys_v1_哈尼视频", "name":"哈尼视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_哈尼视频"},
{"key":"csp_appys_v1_红兔视频", "name":"红兔视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_红兔视频"},
{"key":"csp_appys_v1_盒子影院", "name":"盒子影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_盒子影院"},
{"key":"csp_appys_v1_筋斗云", "name":"筋斗云(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_筋斗云"},
{"key":"csp_appys_v1_极简影视", "name":"极简影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_极简影视"},
{"key":"csp_appys_v1_久久追剧", "name":"久久追剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_久久追剧"},
{"key":"csp_appys_v1_剧迷视频", "name":"剧迷视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_剧迷视频"},
{"key":"csp_appys_v1_橘子影视", "name":"橘子影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_橘子影视"},
{"key":"csp_appys_v1_看剧吧", "name":"看剧吧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_看剧吧"},
{"key":"csp_appys_v1_阔贼影院", "name":"阔贼影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_阔贼影院"},
{"key":"csp_appys_v1_蓝光视频", "name":"蓝光视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_蓝光视频"},
{"key":"csp_appys_v1_林谷影视", "name":"林谷影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_林谷影视"},
{"key":"csp_appys_v1_绿箭影视", "name":"绿箭影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_绿箭影视"},
{"key":"csp_appys_v1_乐看视频", "name":"乐看视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_乐看视频"},
{"key":"csp_appys_v1_蓝猫影视", "name":"蓝猫影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_蓝猫影视"},
{"key":"csp_appys_v1_冷视TV", "name":"冷视TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_冷视TV"},
{"key":"csp_appys_v1_木白影视", "name":"木白影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_木白影视"},
{"key":"csp_appys_v1_麻瓜视频", "name":"麻瓜视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_麻瓜视频"},
{"key":"csp_appys_v1_麻腾TV", "name":"麻腾TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_麻腾TV"},
{"key":"csp_appys_v1_喵乐影视", "name":"喵乐影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_喵乐影视"},
{"key":"csp_appys_v1_命运影视", "name":"命运影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_命运影视"},
{"key":"csp_appys_v1_喵影影视", "name":"喵影影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_喵影影视"},
{"key":"csp_appys_v1_木子电影", "name":"木子电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_木子电影"},
{"key":"csp_appys_v1_麻子追剧", "name":"麻子追剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_麻子追剧"},
{"key":"csp_appys_v1_南府追剧", "name":"南府追剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_南府追剧"},
{"key":"csp_appys_v1_南府影视", "name":"南府影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_南府影视"},
{"key":"csp_appys_v1_暖光影视", "name":"暖光影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_暖光影视"},
{"key":"csp_appys_v1_美剧虫", "name":"美剧虫(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_美剧虫"},
{"key":"csp_appys_v1_内涵影视", "name":"内涵影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_内涵影视"},
{"key":"csp_appys_v1_柠檬影视", "name":"柠檬影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_柠檬影视"},
{"key":"csp_appys_v1_皮影视", "name":"皮影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_皮影视"},
{"key":"csp_appys_v1_奇趣影视", "name":"奇趣影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_奇趣影视"},
{"key":"csp_appys_v1_骑士影院", "name":"骑士影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_骑士影院"},
{"key":"csp_appys_v1_人人动漫", "name":"人人动漫(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_人人动漫"},
{"key":"csp_appys_v1_神马影视", "name":"神马影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_神马影视"},
{"key":"csp_appys_v1_视听星球", "name":"视听星球(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_视听星球"},
{"key":"csp_appys_v1_手指影视", "name":"手指影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_手指影视"},
{"key":"csp_appys_v1_淘剧社", "name":"淘剧社(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_淘剧社"},
{"key":"csp_appys_v1_天天影视", "name":"天天影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_天天影视"},
{"key":"csp_appys_v1_团夕影院", "name":"团夕影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_团夕影院"},
{"key":"csp_appys_v1_兔子窝", "name":"兔子窝(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_兔子窝"},
{"key":"csp_appys_v1_我爱电影", "name":"我爱电影(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_我爱电影"},
{"key":"csp_appys_v1_我爱跟剧", "name":"我爱跟剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_我爱跟剧"},
{"key":"csp_appys_v1_吾爱影视", "name":"吾爱影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_吾爱影视"},
{"key":"csp_appys_v1_蜗牛看鸭", "name":"蜗牛看鸭(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_蜗牛看鸭"},
{"key":"csp_appys_v1_忘忧果TV", "name":"忘忧果TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_忘忧果TV"},
{"key":"csp_appys_v1_渔渔影视", "name":"渔渔影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_渔渔影视"},
{"key":"csp_appys_v1_小城影视", "name":"小城影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_小城影视"},
{"key":"csp_appys_v1_先锋视频", "name":"先锋视频(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_先锋视频"},
{"key":"csp_appys_v1_小极影视", "name":"小极影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_小极影视"},
{"key":"csp_appys_v1_星空影视", "name":"星空影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_星空影视"},
{"key":"csp_appys_v1_玺娜影视", "name":"玺娜影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_玺娜影视"},
{"key":"csp_appys_v1_小蜻蜓", "name":"小蜻蜓(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_小蜻蜓"},
{"key":"csp_appys_v1_休闲影视", "name":"休闲影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_休闲影视"},
{"key":"csp_appys_v1_星影相随", "name":"星影相随(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_星影相随"},
{"key":"csp_appys_v1_小易影视", "name":"小易影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_小易影视"},
{"key":"csp_appys_v1_益达影院", "name":"益达影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_益达影院"},
{"key":"csp_appys_v1_元芳影视", "name":"元芳影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_元芳影视"},
{"key":"csp_appys_v1_雨果影视", "name":"雨果影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_雨果影视"},
{"key":"csp_appys_v1_颖家影院", "name":"颖家影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_颖家影院"},
{"key":"csp_appys_v1_月亮影视", "name":"月亮影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_月亮影视"},
{"key":"csp_appys_v1_影视热剧", "name":"影视热剧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_影视热剧"},
{"key":"csp_appys_v1_杨桃影视", "name":"杨桃影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_杨桃影视"},
{"key":"csp_appys_v1_月色影视", "name":"月色影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_月色影视"},
{"key":"csp_appys_v1_影阅阁", "name":"影阅阁(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_影阅阁"},
{"key":"csp_appys_v1_优全影视", "name":"优全影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_优全影视"},
{"key":"csp_appys_v1_优优影院", "name":"优优影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_优优影院"},
{"key":"csp_appys_v1_一只鱼", "name":"一只鱼(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_一只鱼"},
{"key":"csp_appys_v1_追番猫", "name":"追番猫(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_追番猫"},
{"key":"csp_appys_v1_追剧吧", "name":"追剧吧(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_追剧吧"},
{"key":"csp_appys_v1_追剧影院", "name":"追剧影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_追剧影院"},
{"key":"csp_appys_v1_宅男影院", "name":"宅男影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_宅男影院"},
{"key":"csp_appys_v1_侦探影视", "name":"侦探影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_侦探影视"},
{"key":"csp_appys_v1_追影兔", "name":"追影兔(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_追影兔"},
{"key":"csp_appys_v1_猪猪影院", "name":"猪猪影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"v1_猪猪影院"},
{"key":"csp_appys_gctv_555TV", "name":"555TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"gctv_555TV"},
{"key":"csp_appys_gctv_红牛影视", "name":"红牛影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"gctv_红牛影视"},
{"key":"csp_appys_iptv_2号币", "name":"2号币(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_2号币"},
{"key":"csp_appys_iptv_HG影视", "name":"HG影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_HG影视"},
{"key":"csp_appys_iptv_傲视影院", "name":"傲视影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_傲视影院"},
{"key":"csp_appys_iptv_尘梓TV", "name":"尘梓TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_尘梓TV"},
{"key":"csp_appys_iptv_稻草人TV", "name":"稻草人TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_稻草人TV"},
{"key":"csp_appys_iptv_嘀哩嘀哩", "name":"嘀哩嘀哩(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_嘀哩嘀哩"},
{"key":"csp_appys_iptv_动力影视", "name":"动力影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_动力影视"},
{"key":"csp_appys_iptv_大师兄", "name":"大师兄(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_大师兄"},
{"key":"csp_appys_iptv_疯狂看TV", "name":"疯狂看TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_疯狂看TV"},
{"key":"csp_appys_iptv_火箭影视", "name":"火箭影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_火箭影视"},
{"key":"csp_appys_iptv_黑龙影视", "name":"黑龙影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_黑龙影视"},
{"key":"csp_appys_iptv_红牛TV", "name":"红牛TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_红牛TV"},
{"key":"csp_appys_iptv_核桃影视", "name":"核桃影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_核桃影视"},
{"key":"csp_appys_iptv_聚多影视", "name":"聚多影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_聚多影视"},
{"key":"csp_appys_iptv_久久影院", "name":"久久影院(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_久久影院"},
{"key":"csp_appys_iptv_流星雨", "name":"流星雨(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_流星雨"},
{"key":"csp_appys_iptv_冷月TV", "name":"冷月TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_冷月TV"},
{"key":"csp_appys_iptv_双子星", "name":"双子星(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_双子星"},
{"key":"csp_appys_iptv_云播影视", "name":"云播影视(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_云播影视"},
{"key":"csp_appys_iptv_影酷TV", "name":"影酷TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_影酷TV"},
{"key":"csp_appys_iptv_乐酷TV", "name":"乐酷TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_乐酷TV"},
{"key":"csp_appys_iptv_小极TV", "name":"小极TV(M)", "type":3, "api":"csp_AppYs","searchable":1,"quickSearch":0,"filterable":1,"ext":"iptv_小极TV"}
```
</details>

---

**影视大全搜索接口支持（不再更新维护，建议使用M浏览器APP影视规则）**（2021.10.20 by 小黄瓜） 接口杂乱，搜出来能不能播随缘了。

代码里读取<a href="https://pj567.coding.net/p/source/d/source/git/raw/master/mobile/config.json" target="_blank">影视大全配置json</a>中的搜索接口

所以你需要在自定义配置中增加相关搜索站配置，ext字段填影视大全配置json中的`sourceName`，当前支持type为AppV0、AppTV、aiKanTv的搜索接口。

注意：这些配置只能用于搜索，也只支持在搜索界面搜索，不支持相关搜索，更不能作为首页源。
<details>
<summary>配置代码 点我展开</summary>

```json
{"key":"csp_ysdq_007影视", "name":"007影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"007影视"},
{"key":"csp_ysdq_555电影", "name":"555电影(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"555电影"},
{"key":"csp_ysdq_5060影院", "name":"5060影院(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"5060影院"},
{"key":"csp_ysdq_913E影视", "name":"913E影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"913E影视"},
{"key":"csp_ysdq_播放呀", "name":"播放呀(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"播放呀"},
{"key":"csp_ysdq_畅视影视", "name":"畅视影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"畅视影视"},
{"key":"csp_ysdq_点播TV", "name":"点播TV(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"点播TV"},
{"key":"csp_ysdq_迪迪影院", "name":"迪迪影院(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"迪迪影院"},
{"key":"csp_ysdq_嘀哩嘀哩", "name":"嘀哩嘀哩(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"嘀哩嘀哩"},
{"key":"csp_ysdq_毒舌电影", "name":"毒舌电影(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"毒舌电影"},
{"key":"csp_ysdq_大师兄", "name":"大师兄(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"大师兄"},
{"key":"csp_ysdq_瓜皮TV", "name":"瓜皮TV(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"瓜皮TV"},
{"key":"csp_ysdq_海胆影视", "name":"海胆影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"海胆影视"},
{"key":"csp_ysdq_海绵影视", "name":"海绵影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"海绵影视"},
{"key":"csp_ysdq_京广航", "name":"京广航(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"京广航"},
{"key":"csp_ysdq_九合视频", "name":"九合视频(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"九合视频"},
{"key":"csp_ysdq_江湖影视", "name":"江湖影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"江湖影视"},
{"key":"csp_ysdq_极客", "name":"极客(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"极客"},
{"key":"csp_ysdq_久九影视", "name":"久九影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"久九影视"},
{"key":"csp_ysdq_蓝果影视", "name":"蓝果影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"蓝果影视"},
{"key":"csp_ysdq_琳琅影视", "name":"琳琅影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"琳琅影视"},
{"key":"csp_ysdq_极乐阁", "name":"极乐阁(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"极乐阁"},
{"key":"csp_ysdq_萌蛋蛋", "name":"萌蛋蛋(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"萌蛋蛋"},
{"key":"csp_ysdq_思古影视", "name":"思古影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"思古影视"},
{"key":"csp_ysdq_污妖动漫", "name":"污妖动漫(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"污妖动漫"},
{"key":"csp_ysdq_小易影视", "name":"小易影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"小易影视"},
{"key":"csp_ysdq_影视工场", "name":"影视工场(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"影视工场"},
{"key":"csp_ysdq_追剧达人", "name":"追剧达人(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"追剧达人"},
{"key":"csp_ysdq_80影视", "name":"80影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"80影视"},
{"key":"csp_ysdq_80k影视", "name":"80k影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"80k影视"},
{"key":"csp_ysdq_FUFU影视", "name":"FUFU影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"FUFU影视"},
{"key":"csp_ysdq_VIP影视", "name":"VIP影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"VIP影视"},
{"key":"csp_ysdq_爱影视", "name":"爱影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"爱影视"},
{"key":"csp_ysdq_白菜追剧", "name":"白菜追剧(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"白菜追剧"},
{"key":"csp_ysdq_初心影视", "name":"初心影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"初心影视"},
{"key":"csp_ysdq_段友影视", "name":"段友影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"段友影视"},
{"key":"csp_ysdq_飞捷影视", "name":"飞捷影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"飞捷影视"},
{"key":"csp_ysdq_公主影视", "name":"公主影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"公主影视"},
{"key":"csp_ysdq_辉哥影视", "name":"辉哥影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"辉哥影视"},
{"key":"csp_ysdq_哈尼视频", "name":"哈尼视频(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"哈尼视频"},
{"key":"csp_ysdq_核桃影视V1", "name":"核桃影视V1(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"核桃影视V1"},
{"key":"csp_ysdq_懒猫电影", "name":"懒猫电影(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"懒猫电影"},
{"key":"csp_ysdq_冷视TV", "name":"冷视TV(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"冷视TV"},
{"key":"csp_ysdq_喵乐影视", "name":"喵乐影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"喵乐影视"},
{"key":"csp_ysdq_南府追剧", "name":"南府追剧(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"南府追剧"},
{"key":"csp_ysdq_内涵影视", "name":"内涵影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"内涵影视"},
{"key":"csp_ysdq_柠檬影视", "name":"柠檬影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"柠檬影视"},
{"key":"csp_ysdq_手指影视", "name":"手指影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"手指影视"},
{"key":"csp_ysdq_淘剧社", "name":"淘剧社(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"淘剧社"},
{"key":"csp_ysdq_团夕影院", "name":"团夕影院(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"团夕影院"},
{"key":"csp_ysdq_我爱跟剧", "name":"我爱跟剧(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"我爱跟剧"},
{"key":"csp_ysdq_吾爱影视", "name":"吾爱影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"吾爱影视"},
{"key":"csp_ysdq_小城影视", "name":"小城影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"小城影视"},
{"key":"csp_ysdq_先锋影视", "name":"先锋影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"先锋影视"},
{"key":"csp_ysdq_小极影视", "name":"小极影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"小极影视"},
{"key":"csp_ysdq_小蜻蜓视频", "name":"小蜻蜓视频(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"小蜻蜓视频"},
{"key":"csp_ysdq_休闲影视", "name":"休闲影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"休闲影视"},
{"key":"csp_ysdq_远方影视", "name":"远方影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"远方影视"},
{"key":"csp_ysdq_颖家影院", "name":"颖家影院(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"颖家影院"},
{"key":"csp_ysdq_优炫影视", "name":"优炫影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"优炫影视"},
{"key":"csp_ysdq_一只鱼影视", "name":"一只鱼影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"一只鱼影视"},
{"key":"csp_ysdq_追番猫", "name":"追番猫(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"追番猫"},
{"key":"csp_ysdq_追剧吧", "name":"追剧吧(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"追剧吧"},
{"key":"csp_ysdq_侦探影视", "name":"侦探影视(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"侦探影视"},
{"key":"csp_ysdq_追影兔", "name":"追影兔(搜)", "type":3, "api":"csp_Ysdq","searchable":1,"quickSearch":0, "ext":"追影兔"}
```
</details>

---

## 基础类
----

> com.github.catvod.crawler.Spider 爬虫基类

> com.github.catvod.crawler.SpiderReq 用于发起网络请求 获取网络数据

> com.github.catvod.crawler.SpiderReqResult 网络请求结果

> com.github.catvod.crawler.SpiderUrl 用于定义一个网络请求


## 示例
----

请查看 仓库中的爱迪影视 相关实现 ，调试可参考 `com.github.catvod.demo.MainActivity` ，直接调用对应爬虫相关接口

> com.github.catvod.spider.Aidi

## 注意事项!!
---- 
1. 除了`com.github.catvod.spider`包以外的代码，最终都会被软件本身内置的代码代替掉，所以，建议你不要修改除`com.github.catvod.spider`包以外的代码。

2. 不要在`Spider`中使用`Gson`

3. 待补充

## 爬虫类返回的相关Json字符串格式说明
----
### homeContent
```json
{
	"class": [{   // 分类
		"type_id": "dianying", // 分类id 
		"type_name": "电影" // 分类名
	}, {
		"type_id": "lianxuju",
		"type_name": "连续剧"
	}],
	"filters": { // 筛选
		"dianying": [{ // 分类id 就是上面class中的分类id
			"key": "0", // 筛选key
			"name": "分类", // 筛选名称
			"value": [{ // 筛选选项 
				"n": "全部", // 选项展示的名称
				"v": "dianying" // 选项最终在url中的展现
			}, {
				"n": "动作片",
				"v": "dongzuopian"
			}]
		}],
		"lianxuju": [{
			"key": 0,
			"name": "分类",
			"value": [{
				"n": "全部",
				"v": "lianxuju"
			}, {
				"n": "国产剧",
				"v": "guochanju"
			}, {
				"n": "港台剧",
				"v": "gangtaiju"
			}]
		}]
	},
	"list": [{ // 首页最近更新视频列表
		"vod_id": "1901", // 视频id
		"vod_name": "判决", // 视频名
		"vod_pic": "https:\/\/pic.imgdb.cn\/item\/614631e62ab3f51d918e9201.jpg", // 展示图片
		"vod_remarks": "6.8" // 视频信息 展示在 视频名上方
	}, {
		"vod_id": "1908",
		"vod_name": "移山的父亲",
		"vod_pic": "https:\/\/pic.imgdb.cn\/item\/6146fab82ab3f51d91c01af1.jpg",
		"vod_remarks": "6.7"
	}]
}
```

### categoryContent
```json
{
	"page": 1, // 当前页
	"pagecount": 2, // 总共几页
	"limit": 60, // 每页几条数据
	"total": 120, // 总共多少调数据
	"list": [{ // 视频列表 下面的视频结构 同上面homeContent中的
		"vod_id": "1897",
		"vod_name": "北区侦缉队",
		"vod_pic": "https:\/\/pic.imgdb.cn\/item\/6145d4b22ab3f51d91bd98b6.jpg",
		"vod_remarks": "7.3"
	}, {
		"vod_id": "1879",
		"vod_name": "浪客剑心 最终章 人诛篇",
		"vod_pic": "https:\/\/pic.imgdb.cn\/item\/60e3f37e5132923bf82ef95e.jpg",
		"vod_remarks": "8.0"
	}]
}
```

### detailContent
```json
{
	"list": [{
		"vod_id": "1902",
		"vod_name": "海岸村恰恰恰",
		"vod_pic": "https:\/\/pic.imgdb.cn\/item\/61463fd12ab3f51d91a0f44d.jpg",
		"type_name": "剧情",
		"vod_year": "2021",
		"vod_area": "韩国",
		"vod_remarks": "更新至第8集",
		"vod_actor": "申敏儿,金宣虎,李相二,孔敏晶,徐尚沅,禹美华,朴艺荣,李世亨,边胜泰,金贤佑,金英玉",
		"vod_director": "柳济元",
		"vod_content": "海岸村恰恰恰剧情:　　韩剧海岸村恰恰恰 갯마을 차차차改编自2004年的电影《我的百事通男友洪班长》，海岸村恰恰恰 갯마을 차차차讲述来自大都市的牙医（申敏儿 饰）到充满人情味的海岸村开设牙医诊所，那里住着一位各方面都",
        // 播放源 多个用$$$分隔
		"vod_play_from": "qiepian$$$yun3edu", 
        // 播放列表 注意分隔符 分别是 多个源$$$分隔，源中的剧集用#分隔，剧集的名称和地址用$分隔
		"vod_play_url": "第1集$1902-1-1#第2集$1902-1-2#第3集$1902-1-3#第4集$1902-1-4#第5集$1902-1-5#第6集$1902-1-6#第7集$1902-1-7#第8集$1902-1-8$$$第1集$1902-2-1#第2集$1902-2-2#第3集$1902-2-3#第4集$1902-2-4#第5集$1902-2-5#第6集$1902-2-6#第7集$1902-2-7#第8集$1902-2-8" 
	}]
}
```

### searchContent
```json
{
	"list": [{ // 视频列表 下面的视频结构 同上面homeContent中的
		"vod_id": "1606",
		"vod_name": "陪你一起长大",
		"vod_pic": "https:\/\/img.aidi.tv\/img\/upload\/vod\/20210417-1\/e27d4eb86f7cde375171dd324b2c19ae.jpg",
		"vod_remarks": "更新至第37集"
	}]
}
```




