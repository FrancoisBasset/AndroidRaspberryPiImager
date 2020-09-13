package francoisbasset.androidraspberrypiimager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class Image {
    private String name;
    private String description;
    private String url;
    private File file;

    private static Image instance;

    public Image(String name, String description, String fileName, String url) {
        this.name = name;
        this.description = description;
        this.file = new File(Environment.getExternalStorageDirectory() + "raspberrypiimager/" + fileName);

        this.url = url;
    }

    public void createFolder() {
        File imagesFolder = new File(Environment.getExternalStorageDirectory() + "/raspberrypiimager");

        if (!imagesFolder.exists()) {
            imagesFolder.mkdir();
        }
    }

    public void download() {
        Uri uri = Uri.parse(this.url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir("/raspberrypiimager", this.file.getName());

        Image image = this;

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                new Thread() {
                    public void run() {
                        SDCard.getInstance().writeImage(image);

                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.getInstance().indicator.setBackgroundColor(Color.GREEN);
                            }
                        });
                    }
                }.start();
            }
        };

        MainActivity.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        DownloadManager dm = (DownloadManager) MainActivity.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public File getFile() {
        return this.file;
    }

    public boolean isCached() {
        return this.file != null;
    }

    public static Image getInstance() {
        return instance;
    }

    public static void setInstance(Image image) {
        instance = image;
    }
}