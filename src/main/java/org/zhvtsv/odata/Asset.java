package org.zhvtsv.odata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Asset {
    @JsonProperty("DownloadLink")
    private String downloadLink;

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "downloadLink='" + downloadLink + '\'' +
                '}';
    }
}
