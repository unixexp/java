package com.youamp.media.youtube;

public class YtFile {

    private Format format;
    private String url;
    private String signature;

    YtFile(Format format, String url) {
        this.format = format;
        this.url = url;
        this.signature = null;
    }

    YtFile(Format format, String url, String signature) {
        this.format = format;
        this.url = url;
        this.signature = signature;
    }

    public String getSignature() { return signature; }

    public String getUrl() {
        return url;
    }

    public Format getFormat() {
        return format;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YtFile ytFile = (YtFile) o;

        if (format != null ? !format.equals(ytFile.format) : ytFile.format != null) return false;
        return url != null ? url.equals(ytFile.url) : ytFile.url == null;
    }

    @Override
    public int hashCode() {
        int result = format != null ? format.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "YtFile{" +
                "format=" + format +
                ", url='" + url + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
