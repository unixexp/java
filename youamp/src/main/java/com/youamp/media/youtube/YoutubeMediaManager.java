package com.youamp.media.youtube;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class YoutubeMediaManager {

    private String videoID;
    private VideoMeta videoMeta;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/97.0.4692.98 Safari/537.36";

    private static final Pattern patYouTubeShortLink = Pattern.compile(
            "(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)");
    private static final Pattern patYouTubePageLink = Pattern.compile(
            "(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)");
    private static final Pattern patPlayerResponse = Pattern.compile(
            "var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\s*;");

    private static final Pattern patSigEncUrl = Pattern.compile("url=(.+?)(\\u0026|$)");
    private static final Pattern patSignature = Pattern.compile("s=(.+?)(\\u0026|$)");

    protected abstract void onLoadComplete(Map<Integer, YtFile> ytFiles, VideoMeta videoMeta);
    protected abstract void onError(String errorMessage);

    protected void onPostExecute(Map<Integer, YtFile> ytFiles) {
        onLoadComplete(ytFiles, videoMeta);
    }

    public void load(String youtubeLink) {
        new Thread(() -> {
            videoID = null;
            if (youtubeLink == null) {
                onError("Invalid youtube link");
            } else {
                Matcher mat = patYouTubePageLink.matcher(youtubeLink);
                if (mat.find()) {
                    videoID = mat.group(3);
                } else {
                    mat = patYouTubeShortLink.matcher(youtubeLink);
                    if (mat.find()) {
                        videoID = mat.group(3);
                    } else if (youtubeLink.matches("\\p{Graph}+?")) {
                        videoID = youtubeLink;
                    }
                }
                if (videoID != null) {
                    try {
                        onPostExecute(getStreamUrls());
                    } catch (Exception e) {
                        onError("Extraction failed" + e.toString());
                    }
                } else {
                    onError("Wrong YouTube link format");
                }
            }
        }).start();
    }

    private Map<Integer, YtFile> getStreamUrls() throws IOException, ParseException {
        String pageHtml;
        Map<Integer, YtFile> ytFiles = new HashMap<>();

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        URL getUrl = new URL("https://youtube.com/watch?v=" + videoID);

        try {
            urlConnection = (HttpURLConnection) getUrl.openConnection();
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sbPageHtml = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sbPageHtml.append(line);
            }
            pageHtml = sbPageHtml.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        Matcher mat = patPlayerResponse.matcher(pageHtml);
        if (mat.find()) {
            JSONObject ytPlayerResponse = new JSONObject(mat.group(1));
            JSONObject streamingData = ytPlayerResponse.getJSONObject("streamingData");

            JSONArray formats = streamingData.getJSONArray("formats");
            for (int i = 0; i < formats.length(); i++) {

                JSONObject format = formats.getJSONObject(i);

                // FORMAT_STREAM_TYPE_OTF(otf=1) requires downloading the init fragment (adding
                // `&sq=0` to the URL) and parsing emsg box to determine the number of fragment that
                // would subsequently requested with (`&sq=N`) (cf. youtube-dl)
                String type = format.optString("type");
                if (type != null && type.equals("FORMAT_STREAM_TYPE_OTF"))
                    continue;

                int itag = format.getInt("itag");

                if (FormatMap.get(itag) != null) {
                    if (format.has("url")) {
                        String url = format.getString("url").replace("\\u0026", "&");
                        ytFiles.put(itag, new YtFile(FormatMap.get(itag), url));
                    } else if (format.has("signatureCipher")) {
                        mat = patSigEncUrl.matcher(format.getString("signatureCipher"));
                        Matcher matSig = patSignature.matcher(format.getString("signatureCipher"));
                        if (mat.find() && matSig.find()) {
                            String url = URLDecoder.decode(mat.group(1), "UTF-8");
                            String signature = URLDecoder.decode(matSig.group(1), "UTF-8");
                            ytFiles.put(itag, new YtFile(FormatMap.get(itag), url, signature));
                        }
                    }
                }
            }

            JSONArray adaptiveFormats = streamingData.getJSONArray("adaptiveFormats");
            for (int i = 0; i < adaptiveFormats.length(); i++) {

                JSONObject adaptiveFormat = adaptiveFormats.getJSONObject(i);

                String type = adaptiveFormat.optString("type");
                if (type != null && type.equals("FORMAT_STREAM_TYPE_OTF"))
                    continue;

                int itag = adaptiveFormat.getInt("itag");

                if (FormatMap.get(itag) != null) {
                    if (adaptiveFormat.has("url")) {
                        String url = adaptiveFormat.getString("url").replace("\\u0026", "&");
                        ytFiles.put(itag, new YtFile(FormatMap.get(itag), url));
                    } else if (adaptiveFormat.has("signatureCipher")) {

                        mat = patSigEncUrl.matcher(adaptiveFormat.getString("signatureCipher"));
                        Matcher matSig = patSignature.matcher(adaptiveFormat.getString("signatureCipher"));
                        if (mat.find() && matSig.find()) {
                            String url = URLDecoder.decode(mat.group(1), "UTF-8");
                            String signature = URLDecoder.decode(matSig.group(1), "UTF-8");
                            ytFiles.put(itag, new YtFile(FormatMap.get(itag), url, signature));
                        }
                    }
                }
            }

            JSONObject videoDetails = ytPlayerResponse.getJSONObject("videoDetails");
            JSONArray thumbnailsJA = videoDetails.getJSONObject("thumbnail").getJSONArray("thumbnails");
            List<Thumbnail> thumbnails = new ArrayList<>();
            for (Object iThumbnail : thumbnailsJA) {
                thumbnails.add(new Thumbnail(
                        ((JSONObject) iThumbnail).getInt("width"),
                        ((JSONObject) iThumbnail).getInt("height"),
                        ((JSONObject) iThumbnail).getString("url")
                ));
            }
            this.videoMeta = new VideoMeta(videoDetails.getString("videoId"),
                    videoDetails.getString("title"),
                    videoDetails.getString("author"),
                    videoDetails.getString("channelId"),
                    Long.parseLong(videoDetails.getString("lengthSeconds")),
                    Long.parseLong(videoDetails.getString("viewCount")),
                    videoDetails.getBoolean("isLiveContent"),
                    videoDetails.getString("shortDescription"),
                    thumbnails
            );

        } else {
            throw new ParseException("ytPlayerResponse was not found", 0);
        }

        SignatureEngine signatureEngine = SignatureEngine.create(pageHtml);
        Iterator<Map.Entry<Integer, YtFile>> iterator = ytFiles.entrySet().iterator();
        int[] failedList = new int[ytFiles.size()];
        int failed = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, YtFile> entry = iterator.next();
            YtFile ytFile = entry.getValue();
            if (ytFile.getSignature() != null) {
                try {
                    String decipheredSignature = signatureEngine.decipher(ytFile.getSignature());
                    ytFile.setUrl(ytFile.getUrl() + "&sig=" + decipheredSignature);
                } catch (ScriptException e) {
                    /*
                    System.out.println("Error decipher signature for file " +
                            ytFile.getFormat().toString() +
                            "\n" +
                            e.toString());

                     */
                    failedList[failed] = entry.getKey();
                    failed++;
                }
            }
        }

        for (int f : failedList) {
            ytFiles.remove(f);
        }

        return ytFiles;
    }

}
