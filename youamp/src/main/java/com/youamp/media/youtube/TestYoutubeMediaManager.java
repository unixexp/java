package com.youamp.media.youtube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TestYoutubeMediaManager {

    private String videoID;
    private VideoMeta videoMeta;

    protected abstract void onLoadComplete(Map<Integer, YtFile> ytFiles, VideoMeta videoMeta);
    protected abstract void onError(String errorMessage);

    protected void onPostExecute(Map<Integer, YtFile> ytFiles) {
        onLoadComplete(ytFiles, videoMeta);
    }

    public void load(String youtubeLink) {
        System.out.println("Param 'youtubeLink' is hardcoded for tests and will not using");
        new Thread(() -> {
            videoID = "tBN7zJ-QdUQ";
            try {
                onPostExecute(getStreamUrls());
            } catch (Exception e) {
                onError("Extraction failed" + e.toString());
            }
        }).start();
    }

    private Map<Integer, YtFile> getStreamUrls() {
        Map<Integer, YtFile> ytFiles = new HashMap<>();

        ytFiles.put(18, new YtFile(
                FormatMap.get(18),
                "https://rr14---sn-3c27sn7d.googlevideo.com/videoplayback?expire=1708549149&ei=vQ_WZflA-qGL2g-atb-QCg&ip=82.193.123.165&id=o-AOKkwRfm_3MnGpNgjx8q37NmM5tpivOIcS5lLZVupsVD&itag=18&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&mh=hf&mm=31%2C26&mn=sn-3c27sn7d%2Csn-f5f7knee&ms=au%2Conr&mv=m&mvi=14&pl=20&initcwndbps=1393750&spc=UWF9fyROxNgfTgrGSeE-BRbPUU-gf7n6czdtJd_Jv1O4crc&vprv=1&svpuc=1&mime=video%2Fmp4&ns=tUJYTf5PTX834zx55Z_DfnwQ&cnr=14&ratebypass=yes&dur=495.978&lmt=1664772386974754&mt=1708527184&fvip=3&fexp=24007246&c=WEB&sefc=1&txp=4438434&n=k0nrE8ftFXha4t7&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Cspc%2Cvprv%2Csvpuc%2Cmime%2Cns%2Ccnr%2Cratebypass%2Cdur%2Clmt&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=APTiJQcwRAIgKdSckv-Ld254AxPm-SUvE9oM2TySamITeKI0QzYNF1oCIGebYhL7iMU-QO_ElE-ejb2_sP6HQ7OZEaTH2kA7nHjh&sig=AJfQdSswRQIgPDrthnsipa1TprNSCyVCYelwFNOQuGT1w--E9UMbufsCIQCaJ_B0t6IolpRzY68YEIiE0QFnuYG4lsCoLhPfDyReag==",
                "UUlo=g==gaeRyDfPhL=CsE4GYunFQ0oiIEY86YzRploI6t0B_JaCQICsfubMl9E--w1TGuQONFwleYCVyCSNrpT1apisnhtrDPgIQRwsSdQfJA")
        );
        ytFiles.put(22, new YtFile(
                FormatMap.get(22),
                "https://rr14---sn-3c27sn7d.googlevideo.com/videoplayback?expire=1708549149&ei=vQ_WZflA-qGL2g-atb-QCg&ip=82.193.123.165&id=o-AOKkwRfm_3MnGpNgjx8q37NmM5tpivOIcS5lLZVupsVD&itag=18&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&mh=hf&mm=31%2C26&mn=sn-3c27sn7d%2Csn-f5f7knee&ms=au%2Conr&mv=m&mvi=14&pl=20&initcwndbps=1393750&spc=UWF9fyROxNgfTgrGSeE-BRbPUU-gf7n6czdtJd_Jv1O4crc&vprv=1&svpuc=1&mime=video%2Fmp4&ns=tUJYTf5PTX834zx55Z_DfnwQ&cnr=14&ratebypass=yes&dur=495.978&lmt=1664772386974754&mt=1708527184&fvip=3&fexp=24007246&c=WEB&sefc=1&txp=4438434&n=k0nrE8ftFXha4t7&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Cspc%2Cvprv%2Csvpuc%2Cmime%2Cns%2Ccnr%2Cratebypass%2Cdur%2Clmt&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=APTiJQcwRAIgKdSckv-Ld254AxPm-SUvE9oM2TySamITeKI0QzYNF1oCIGebYhL7iMU-QO_ElE-ejb2_sP6HQ7OZEaTH2kA7nHjh&sig=AJfQdSswRQIgPDrthnsipa1TprNSCyVCYelwFNOQuGT1w--E9UMbufsCIQCaJ_B0t6IolpRzY68YEIiE0QFnuYG4lsCoLhPfDyReag==",
                "UUlo=g==gaeRyDfPhL=CsE4GYunFQ0oiIEY86YzRploI6t0B_JaCQICsfubMl9E--w1TGuQONFwleYCVyCSNrpT1apisnhtrDPgIQRwsSdQfJA")
        );

        List<Thumbnail> thumbnails = new ArrayList<>();
        thumbnails.add(new Thumbnail(168, 94, "https://i.ytimg.com/vi/tBN7zJ-QdUQ/hqdefault.jpg?sqp=-oaymwEbCKgBEF5IVfKriqkDDggBFQAAiEIYAXABwAEG&rs=AOn4CLA0i57NO3M7SZ0ZhAZkPm5Flm_mzg"));
        thumbnails.add(new Thumbnail(196, 110, "https://i.ytimg.com/vi/tBN7zJ-QdUQ/hqdefault.jpg?sqp=-oaymwEbCMQBEG5IVfKriqkDDggBFQAAiEIYAXABwAEG&rs=AOn4CLBBfd7dWAbKzKHwAROxdEV9xsdyZg"));
        this.videoMeta = new VideoMeta(
                "tBN7zJ-QdUQ",
                "Voodoo Child (Slight Return) - Jimi Hendrix/Stevie Ray Vaughan | Full Cover/Improv",
                "Sean Mann",
                "UC7NnnLf5zS9QE-iQWuKuNGA",
                496,
                901162,
                false,
                "Voodoo Child (Slight Return) - Jimi Hendrix/Stevie Ray Vaughan",
                thumbnails
        );

        return ytFiles;
    }

}
