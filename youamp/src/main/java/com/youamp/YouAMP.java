package com.youamp;

import com.youamp.media.youtube.TestYoutubeMediaManager;
import com.youamp.media.youtube.VideoMeta;
import com.youamp.media.youtube.YoutubeMediaManager;
import com.youamp.media.youtube.YtFile;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Iterator;
import java.util.Map;

public class YouAMP extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox vBox = new VBox(new Label("Main"));
        Scene scene = new Scene(vBox);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        System.out.println(primaryScreenBounds.getWidth());
        System.out.println(primaryScreenBounds.getHeight());

        stage.setWidth(400);
        stage.setHeight(200);
        stage.setScene(scene);
        stage.show();

        loadMedia();
    }

    public void loadMedia() {
        final String youtubeLink = "https://www.youtube.com/watch?v=tBN7zJ-QdUQ";
        // YoutubeMediaManager youtubeMediaManager = new YoutubeMediaManager() {
        TestYoutubeMediaManager youtubeMediaManager = new TestYoutubeMediaManager() {

            @Override
            protected void onLoadComplete(Map<Integer, YtFile> ytFiles, VideoMeta videoMeta) {
                Iterator<Map.Entry<Integer, YtFile>> iterator = ytFiles.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, YtFile> entry = iterator.next();
                    YtFile ytFile = entry.getValue();
                    System.out.println(ytFile.toString());
                }
                System.out.println(videoMeta);
            }

            @Override
            protected void onError(String errorMessage) {
                System.out.println(errorMessage);
            }
        };
        youtubeMediaManager.load(youtubeLink);
    }
}