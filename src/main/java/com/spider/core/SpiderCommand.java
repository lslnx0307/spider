package com.spider.core;

/**
 * @author shiliang.li
 * @date 2020/5/15
 */
public class SpiderCommand {

    private String skuCode;

    private String pdpUrl;

    private String cssStyle;

    /**
     * 爬取sku的主站的地址
     */
    private String masterStationUrl;

    private String exportPath;

    private String chromDriverPath;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getPdpUrl() {

        return pdpUrl;
    }

    public void setPdpUrl(String pdpUrl) {
        this.pdpUrl = pdpUrl;
    }

    public String getCssStyle() {

        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public String getMasterStationUrl() {
        return masterStationUrl;
    }

    public void setMasterStationUrl(String masterStationUrl) {
        this.masterStationUrl = masterStationUrl;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public String getChromDriverPath() {
        return chromDriverPath;
    }

    public void setChromDriverPath(String chromDriverPath) {
        this.chromDriverPath = chromDriverPath;
    }

}
