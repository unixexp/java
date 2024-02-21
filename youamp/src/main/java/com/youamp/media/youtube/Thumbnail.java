package com.youamp.media.youtube;

public class Thumbnail {

    private final Integer width;
    private final Integer height;
    private final String url;

    public Thumbnail(Integer width, Integer height, String url) {
        this.width = width;
        this.height = height;
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Thumbnail thumbnail = (Thumbnail) o;

        if (width != thumbnail.width) return false;
        if (height != thumbnail.height) return false;
        if (url != thumbnail.url) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Thumbnail{" +
                "width=" + width +
                ", height=" + height +
                ", url='" + url +  '\'' +
                '}';
    }
}
