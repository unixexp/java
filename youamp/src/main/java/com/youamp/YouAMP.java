package com.youamp;

import com.youamp.gui.MainWindow;
import com.youamp.media.youtube.VideoMeta;
import com.youamp.media.youtube.YoutubeExtractor;
import com.youamp.media.youtube.YtFile;

import java.util.Iterator;
import java.util.Map;

public class YouAMP {

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.create();

        final String youtubeLink = "https://www.youtube.com/watch?v=oPkaHxvxoso&list=RDoPkaHxvxoso&start_radio=1";
        new YoutubeExtractor() {

            @Override
            protected void onExtractionComplete(Map<Integer, YtFile> ytFiles, VideoMeta videoMeta) {
                Iterator<Map.Entry<Integer, YtFile>> iterator = ytFiles.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, YtFile> entry = iterator.next();
                    YtFile ytFile = entry.getValue();
                    if (ytFile.getFormat().getExt().equals("mp4")) {
                        System.out.println(entry.getValue().toString());
                    }
                }
            }

            @Override
            protected void onError(String errorMessage) {
                System.out.println(errorMessage);
            }
        }.extract(youtubeLink);

    }

}