package com.spider.core;


import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * spider 工具类
 *
 * @author shiliang.li
 * @date 2020/5/15
 */
public class SpiderUtils {

    public static final String MASTER_M3U8_URL = "https://secure.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId={videoId}&secure=true";

    private static InputStream copyWriteInputStream;

    public static void downZip(SpiderCommand spiderCommand) {
        if (spiderCommand != null) {
            //todo 打开浏览器
            ChromeDriver chromeDriver = openChrome(spiderCommand);
            //todo 解析页面中的素材和文案
            Set<String> strings = parsePage(chromeDriver, spiderCommand);
            //todo 打包下载图片
            packaging(strings, spiderCommand);
        }
    }

    private static void packaging(Set<String> imageSet, SpiderCommand spiderCommand) {
        try {
            if (CollectionUtils.isNotEmpty(imageSet)) {
                imageSet.forEach(e -> System.out.println(e));
                AtomicInteger index = new AtomicInteger();
                ZipFile zipFile = new ZipFile(spiderCommand.getExportPath() + spiderCommand.getSkuCode() + ".zip");
                ZipParameters zipParameters = new ZipParameters();
                //设置压缩方法
                zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
                //设置压缩级别
                zipParameters.setCompressionLevel(CompressionLevel.NORMAL);
                for (String s : imageSet) {
                    index.getAndIncrement();
                    try {
                        InputStream inputStream;
                        if (s.contains("m3u8")) {
                            zipParameters.setFileNameInZip(spiderCommand.getSkuCode() + "-" + index + ".ts");
                            inputStream = M3U8Downloader.downVideo(s, spiderCommand.getExportPath());
                        } else {
                            String imgFileType = getImgFileType(s);
                            zipParameters.setFileNameInZip(spiderCommand.getSkuCode() + "-" + index + "." + imgFileType);
                            URL url = new URL(s);
                            inputStream = url.openConnection().getInputStream();
                        }
                        zipFile.addStream(inputStream, zipParameters);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                zipParameters.setFileNameInZip(spiderCommand.getSkuCode() + ".txt");
                zipFile.addStream(copyWriteInputStream, zipParameters);
                copyWriteInputStream.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("down success");
    }

    private static String getImgFileType(String s) {
        String[] split = s.split("/");
        return split[split.length - 1];
    }

    private static Set<String> parsePage(ChromeDriver chromeDriver, SpiderCommand spiderCommand) {
        Set<String> imageSet = new HashSet<String>();
        if (spiderCommand != null && chromeDriver != null) {
            if (StringUtils.isNotBlank(spiderCommand.getSkuCode())) {
                WebElement pdpElement = chromeDriver.findElement(By.xpath(
                        "//*[@id=\"Wall\"]/div/div[5]/div/main/section/div/div/div/figure/a[1]"));
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
            chromeDriver.executeScript("window.scrollTo(0,document.body.scrollHeight)");
            chromeDriver.executeScript("window.scrollTo(0,document.body.scrollHeight)");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 直接进入pdp页面
            String context = (String) chromeDriver.executeScript("return document.documentElement.outerHTML");
            Document document = Jsoup.parse(context);
            Elements select = document.getElementsByClass(spiderCommand.getCssStyle());
            if (select != null) {
                String html = select.html();
                getFileUrlList(html, imageSet);
                //获取文案内容
                StringBuffer copyWriting = new StringBuffer();
                for (Element element : select) {
                    copyWriting.append(element.getElementsByTag("h3").text() + "\n");
                    copyWriting.append(element.getElementsByTag("p").text() + "\n");
                    copyWriting.append("\n");
                    System.out.println(element.getElementsByTag("h3").text());
                    System.out.println(element.getElementsByTag("p").text());
                }
                copyWriteInputStream = new ByteArrayInputStream(copyWriting.toString().getBytes());
            }

        }
        return imageSet;
    }

    private static ChromeDriver openChrome(SpiderCommand spiderCommand) {
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        //get system os type mac or windows
        String osName = System.getProperty("os.name");
        SpiderUtils spiderUtils = new SpiderUtils();
        System.out.println("lsl_"+ spiderUtils.getFilePath());
        String chromeDriverPath = SpiderMainUI.class.getResource("/").getPath() + "chromedriver";
        if (!osName.contains("Mac")) {
            chromeDriverPath = "driver/chromedriver.exe";
        }
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("https.protocols", "TLSv1.2");
        ChromeOptions options = new ChromeOptions();
        List<String> op = new ArrayList<String>();
        // 设置浏览器最大window size
        op.add("--start-maximized");
        // 设置无操作界面
        op.add("--headless");
        options.addArguments(op);
        ChromeDriver chromeDriver = new ChromeDriver(options);
        // 设置超时时间
        chromeDriver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
        chromeDriver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
        System.out.println(spiderCommand.getMasterStationUrl());
        chromeDriver.get(spiderCommand.getMasterStationUrl());
        return chromeDriver;
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

    private String getFilePath() {
        URL chromedriver = this.getClass().getResource("/chromedriver");
        return chromedriver.getPath();
    }
}
