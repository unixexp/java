package com.youamp;

import com.youamp.gui.MainWindow;
import com.youamp.media.youtube.VideoMeta;
import com.youamp.media.youtube.YoutubeMediaManager;
import com.youamp.media.youtube.YtFile;

import java.util.Iterator;
import java.util.Map;

public class YouAMP {

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.create();

        final String youtubeLink = "https://www.youtube.com/watch?v=AqJ0Vi9Qx5U";
        YoutubeMediaManager youtubeMediaManager = new YoutubeMediaManager() {

            @Override
            protected void onLoadComplete(Map<Integer, YtFile> ytFiles, VideoMeta videoMeta) {
                Iterator<Map.Entry<Integer, YtFile>> iterator = ytFiles.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, YtFile> entry = iterator.next();
                    YtFile ytFile = entry.getValue();
                    System.out.println(ytFile.toString());
                }
            }

            @Override
            protected void onError(String errorMessage) {
                System.out.println(errorMessage);
            }
        };
        youtubeMediaManager.load(youtubeLink);

    }

}