package org.zhvtsv.models;

import java.util.List;

public class StacItem {
    private String id;

    private String thumbnail;

    private String downloadUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toString() {
        return "StacItem{" +
                "id='" + id + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
