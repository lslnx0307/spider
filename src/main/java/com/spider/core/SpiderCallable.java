package com.spider.core;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author shiliang.li
 * @date 2020/5/22
 */
public class SpiderCallable implements Callable<InputStream> {

    private CountDownLatch countDownLatch;

    private String exportPath;

    private String downUrl;

    public SpiderCallable(CountDownLatch countDownLatch, String downUrl, String exportPath) {
        this.countDownLatch = countDownLatch;
        this.downUrl = downUrl;
        this.exportPath = exportPath;
    }

    @Override
    public InputStream call() throws Exception {
        InputStream inputStream = null;
        try {
            if (downUrl.contains("m3u8")) {
                inputStream = M3U8Downloader.downVideo(downUrl, exportPath);
            } else {
                URL url = new URL(downUrl);
                inputStream = url.openConnection().getInputStream();
            }
        }finally {
            countDownLatch.countDown();
        }

        return inputStream;
    }
}
