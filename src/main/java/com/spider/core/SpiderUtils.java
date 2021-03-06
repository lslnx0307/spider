package com.spider.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

/**
 * spider 工具类
 *
 * @author shiliang.li
 * @date 2020/5/15
 */
public class SpiderUtils {

    /** 定义一个线程池 */
    public static ThreadPoolExecutor POOLEXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    public static final String MASTER_M3U8_URL = "https://secure.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId={videoId}&secure=true";

    private static InputStream copyWriteInputStream;

    public static void downZip(SpiderCommand spiderCommand) throws Exception {
        ChromeDriver chromeDriver = null;
        try {
            if (spiderCommand != null) {
                System.setProperty("webdriver.chrome.driver", spiderCommand.getChromDriverPath());
                System.setProperty("https.protocols", "TLSv1.2");
                // todo 打开浏览器
                chromeDriver = openChrome(spiderCommand);
                // todo 解析页面中的素材和文案
                Set<String> strings = parsePage(chromeDriver, spiderCommand);
                // todo 打包下载图片
                packaging(strings, spiderCommand);

            }
        } finally {
            if (chromeDriver != null) {
                chromeDriver.quit();
            }
            // 视频文件
            File ts = new File(spiderCommand.getExportPath() + "\\video.ts");
            if (ts.exists()) {
                ts.delete();
            }
        }

    }

    private static void packaging(Set<String> imageSet, SpiderCommand spiderCommand) throws Exception {
        if (CollectionUtils.isNotEmpty(imageSet)) {
            imageSet.forEach(e -> System.out.println(e));
            AtomicInteger index = new AtomicInteger();
            ZipFile zipFile = new ZipFile(
                    spiderCommand.getExportPath() + spiderCommand.getSkuCode() + ".zip");

            CountDownLatch countDownLatch = new CountDownLatch(imageSet.size());
            for (String s : imageSet) {
                InputStream inputStream = null;
                try {
                    ZipParameters zipParams = createZipParams();
                    if (s.contains("m3u8")) {
                        zipParams.setFileNameInZip(
                                spiderCommand.getSkuCode() + "-" + index.incrementAndGet() + ".ts");
                    } else {
                        zipParams.setFileNameInZip(spiderCommand.getSkuCode() + "-" + index.incrementAndGet()
                                + getImgFileType(s));
                    }
                    SpiderCallable spiderCallable = new SpiderCallable(countDownLatch, s,
                            spiderCommand.getExportPath());
                    Future<InputStream> future = POOLEXECUTOR.submit(spiderCallable);
                    inputStream = future.get();

                    if (inputStream != null) {
                        zipFile.addStream(inputStream, zipParams);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                }

            }
            countDownLatch.await();
            ZipParameters zipParams = createZipParams();
            zipParams.setFileNameInZip(spiderCommand.getSkuCode() + ".txt");
            zipFile.addStream(copyWriteInputStream, zipParams);
            copyWriteInputStream.close();

        }

        System.out.println("down success");
    }

    private static ZipParameters createZipParams() {
        ZipParameters zipParameters = new ZipParameters();
        // 设置压缩方法
        zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
        // 设置压缩级别
        zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
        return zipParameters;
    }

    private static String getImgFileType(String s) {
        String[] split = s.split("/");
        return split[split.length - 1];
    }

    private static Set<String> parsePage(ChromeDriver chromeDriver, SpiderCommand spiderCommand) {
        Set<String> imageSet = new HashSet<String>();
        if (spiderCommand != null && chromeDriver != null) {
            if (StringUtils.isNotBlank(spiderCommand.getSkuCode())) {
                WebElement pdpElement = chromeDriver.findElement(By.ByCssSelector.cssSelector(
                        "#Wall > div > div.results__body > div > main > section > div > div > div > figure > a.product-card__link-overlay"));
                String pdpUrl = pdpElement.getAttribute("href");
                spiderCommand.setMasterStationUrl(pdpUrl);
                chromeDriver.get(pdpUrl);
            } else {
                String pdpUrl = spiderCommand.getPdpUrl();
                String[] split = pdpUrl.split("/");
                String skuCode = split[split.length - 1];
                spiderCommand.setSkuCode(skuCode);
            }
            // 滚动条置地
            chromeDriver.executeScript("window.scrollTo(0,document.body.scrollHeight)");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chromeDriver.executeScript("window.scrollTo(0,document.body.scrollHeight)");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chromeDriver.executeScript("window.scrollTo(0,document.body.scrollHeight)");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 直接进入pdp页面
            String context = (String) chromeDriver.executeScript("return document.documentElement.outerHTML");
            Document document = Jsoup.parse(context);
            Elements select = document.getElementsByClass(spiderCommand.getCssStyle());
            if (select != null) {
                String html = select.html();
                System.out.println("html->" + html);
                getFileUrlList(html, imageSet);
                // 获取文案内容
                StringBuffer copyWriting = new StringBuffer();
                for (Element element : select) {
                    copyWriting.append(element.getElementsByTag("h3").text() + "\n");
                    copyWriting.append(element.getElementsByTag("p").text() + "\n");
                    copyWriting.append("\n");
                }
                copyWriteInputStream = new ByteArrayInputStream(copyWriting.toString().getBytes());
            }

        }
        return imageSet;
    }

    private static ChromeDriver openChrome(SpiderCommand spiderCommand) {
        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(spiderCommand.getChromDriverPath())).usingAnyFreePort()
                .build();
        // get system os type mac or windows
        ChromeOptions options = new ChromeOptions();
        List<String> op = new ArrayList<String>();
        // 设置无操作界面
        op.add("--headless");
        // 设置浏览器最大window size
        op.add("--start-maximized");
        // 无图加载
        op.add("blink-setting-imagesEnabled-false");
        options.addArguments(op);
        ChromeDriver chromeDriver = new ChromeDriver(options);
        // 设置超时时间
        chromeDriver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
        System.out.println(spiderCommand.getMasterStationUrl());
        chromeDriver.get(spiderCommand.getMasterStationUrl());
        refushCookie(chromeDriver, spiderCommand.getMasterStationUrl());
        chromeDriver.get(spiderCommand.getMasterStationUrl());
        return chromeDriver;
    }

    private static void refushCookie(ChromeDriver chromeDriver, String url) {
        // 如果是中文后缀,则跳转到中文地址
        Cookie cookieLocal;
        Cookie consumer_choice;
        if (!url.contains("com/cn")) {
            cookieLocal = new Cookie("nike_locale", "us/en_us", ".nike.com", "/", null, false, false);
            consumer_choice = new Cookie("CONSUMERCHOICE", "us/en_us", ".nike.com", "/",
                    DateUtils.addYears(new Date(), 1), false, false);
        } else {
            cookieLocal = new Cookie("nike_locale", "cn/zh_cn", ".nike.com", "/", null, false, false);
            consumer_choice = new Cookie("CONSUMERCHOICE", "cn/zh_cn", ".nike.com", "/",
                    DateUtils.addYears(new Date(), 1), false, false);
        }
        chromeDriver.manage().addCookie(cookieLocal);
        chromeDriver.manage().addCookie(consumer_choice);
        // 页面刷新
        chromeDriver.navigate().refresh();
    }

    private static void getFileUrlList(String context, Set<String> imgUrls) {
        eachTag(context, imgUrls, "source", "srcset");
        eachTag(context, imgUrls, "img", "src");
        eachTag(context, imgUrls, "video", "data-video-id");

    }

    private static void eachTag(String context, Set<String> result, String tag, String attr) {
        Document jsoup = Jsoup.parse(context);
        Elements sources = jsoup.getElementsByTag(tag);
        String srcset;
        for (Element source : sources) {
            if (tag.equals("video")) {
                srcset = MASTER_M3U8_URL.replace("{videoId}", source.attr(attr));
            } else {
                srcset = source.attr(attr);
            }
            result.add(srcset);
        }
    }

}
