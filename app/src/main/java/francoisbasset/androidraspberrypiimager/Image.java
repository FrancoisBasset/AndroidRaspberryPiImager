package francoisbasset.androidraspberrypiimager;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public final class Image {
    private String name;
    private String description;
    private String url;
    private File file;
    private String fileSize = "";
    private String releaseDate;

    private static Image instance;

    public Image(String name, String description, String fileName, String url) {
        this.name = name;
        this.description = description;
        this.file = new File(Environment.getExternalStorageDirectory() + "/raspberrypiimager/" + fileName);
        this.url = url;

        this.setFileSizeReleaseDate();
    }

    public final static Image getInstance() {
        return instance;
    }

    public final static void setInstance(Image image) {
        instance = image;
    }

    public final void createFolder() {
        File imagesFolder = new File(Environment.getExternalStorageDirectory() + "/raspberrypiimager");

        if (!imagesFolder.exists()) {
            imagesFolder.mkdir();
        }
    }

    public final void download() {
        Image image = this;

        Thread installThread = new Thread() {
            @Override
            public final void run() {
                if (SDCard.getInstance().writeImage(image)) {

                    MainActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public final void run() {
                            MainActivity.getInstance().showWriteSuccessfulDialog();
                        }
                    });
                }
            }
        };

        if (!this.isCached()) {
            Uri uri = Uri.parse(this.url);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir("/raspberrypiimager", this.file.getName());

            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public final void onReceive(Context ctxt, Intent intent) {
                    installThread.start();
                }
            };

            MainActivity.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            DownloadManager dm = (DownloadManager) MainActivity.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);
        } else {
            installThread.start();
        }
    }

    public final String getName() {
        return this.name;
    }

    public final String getDescription() {
        return this.description;
    }

    public final File getFile() {
        return this.file;
    }

    private final void setFileSizeReleaseDate() {
        Image image = this;

        new Thread(new Runnable() {
            @Override
            public final void run() {
                try {
                    URL url = new URL(image.url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Accept-Encoding", "identity");

                    if (connection.getHeaderField("Location") != null) {
                        connection.setInstanceFollowRedirects(false);

                        url = new URL(connection.getHeaderField("Location"));
                        connection = (HttpURLConnection) url.openConnection();
                    }

                    float fileSize = Float.parseFloat(connection.getHeaderField("Content-Length")) / 1024 / 1024;

                    if (fileSize >= 1000) {
                        fileSize /= 1024;
                        image.fileSize = String.format("%.1f Gb", fileSize);
                    } else {
                        image.fileSize = String.format("%.0f Mb", fileSize);
                    }

                    Date date = new Date(connection.getHeaderField("Last-Modified"));

                    int year = date.getYear() + 1900;

                    String month = String.valueOf(date.getMonth() + 1);
                    if (Integer.parseInt(month) < 10) {
                        month = "0" + month;
                    }

                    String day = String.valueOf(date.getDate());
                    if (Integer.parseInt(day) < 10) {
                        day = "0" + day;
                    }

                    image.releaseDate = year + "-" + month + "-" + day;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public final boolean isCached() {
        return this.file.exists();
    }

    public final String getFileSize() {
        return this.fileSize;
    }

    public final String getReleaseDate() {
        return this.releaseDate;
    }
}