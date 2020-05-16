package com.spider.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;

/**
 * @author shiliang.li
 * @date 2020/5/15
 */
public class M3U8Downloader {

    public static InputStream downVideo(String m3u8Url, String toFile) throws IOException {
        System.out.println("resource url: " + m3u8Url);
        M3U8 m3u8 = parseIndex(m3u8Url);
        m3u8 = parseIndex(m3u8.getTsList().get(0).getFile());
        File file = new File(toFile + "\\video.ts");
        merge(m3u8, file);
        System.out.println(m3u8.getTsList().toString());
        InputStream inputStream = new FileInputStream(file);
        if (file.exists()) {
            System.out.println(file.delete());
        }
        return inputStream;
    }



    public static void main(String[] args) {
        String url = "https://secure.brightcove.com/services/mobile/streaming/index/rendition.m3u8?assetId=6125477020001&secure=true&videoId=6125468486001";
        try {
            M3U8 m3u8 = parseIndex(url);
//            m3u8 = parseIndex(m3u8.getTsList().get(0).getFile());
            File file = new File("/Users/lslnx_0307/Desktop/video.ts");
            merge(m3u8, file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void merge(M3U8 m3u8, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        for (M3U8Ts ts : m3u8.getTsList()) {
            URL url = new URL(ts.getFile());
            InputStream inputStream = url.openConnection().getInputStream();
            IOUtils.copyLarge(inputStream, fos);
            inputStream.close();
        }
        fos.close();
    }

    static M3U8 parseIndex(String url) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));

        String basepath = url.substring(0, url.lastIndexOf("/") + 1);

        M3U8 ret = new M3U8();
        ret.setBasepath(basepath);

        String line;
        float seconds = 0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                if (line.startsWith("#EXTINF:")) {
                    line = line.substring(8);
                    if (line.endsWith(",")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    seconds = Float.parseFloat(line);
                }
                continue;
            }
            if (line.endsWith("m3u8")) {
                return parseIndex(basepath + line);
            }
            ret.addTs(new M3U8Ts(line, seconds));
            seconds = 0;
        }
        reader.close();

        return ret;
    }

    static class M3U8 {
        private String basepath;
        private List<M3U8Ts> tsList = new ArrayList<M3U8Ts>();

        public String getBasepath() {
            return basepath;
        }

        public void setBasepath(String basepath) {
            this.basepath = basepath;
        }

        public List<M3U8Ts> getTsList() {
            return tsList;
        }

        public void setTsList(List<M3U8Ts> tsList) {
            this.tsList = tsList;
        }

        public void addTs(M3U8Ts ts) {
            this.tsList.add(ts);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("basepath: " + basepath);
            for (M3U8Ts ts : tsList) {
                sb.append("\nts: " + ts);
            }

            return sb.toString();
        }

    }

    static class M3U8Ts {
        private String file;
        private float seconds;

        public M3U8Ts(String file, float seconds) {
            this.file = file;
            this.seconds = seconds;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public float getSeconds() {
            return seconds;
        }

        public void setSeconds(float seconds) {
            this.seconds = seconds;
        }

        @Override
        public String toString() {
            return file + " (" + seconds + "sec)";
        }

    }
}
