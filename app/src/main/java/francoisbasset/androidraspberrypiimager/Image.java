package francoisbasset.androidraspberrypiimager;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Image {
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

        this.setInfos();
    }

    public void createFolder() {
        File imagesFolder = new File(Environment.getExternalStorageDirectory() + "/raspberrypiimager");

        if (!imagesFolder.exists()) {
            imagesFolder.mkdir();
        }
    }

    public void download() {
        if (!this.isCached()) {
            Uri uri = Uri.parse(this.url);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir("/raspberrypiimager", this.file.getName());

            Image image = this;

            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    new Thread() {
                        @Override
                        public void run() {
                            SDCard.getInstance().writeImage(image);
                        }
                    }.start();
                }
            };

            MainActivity.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            DownloadManager dm = (DownloadManager) MainActivity.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);
        } else {
            Image image = this;

            new Thread() {
                @Override
                public void run() {
                    //SDCard.getInstance().writeImage(image);

                    MainActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());

                            LayoutInflater inflater = MainActivity.getInstance().getLayoutInflater();
                            View dialogWriteSuccessfulView = inflater.inflate(R.layout.dialog_write_successful, null);

                            builder.setView(dialogWriteSuccessfulView);
                            builder.setCancelable(false);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                            View.OnClickListener onClick = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            };

                            TextView closeLabel = dialogWriteSuccessfulView.findViewById(R.id.closeLabel);
                            closeLabel.setOnClickListener(onClick);

                            Button continueButton = dialogWriteSuccessfulView.findViewById(R.id.continueButton);
                            continueButton.setOnClickListener(onClick);

                            TextView imageLabel = dialogWriteSuccessfulView.findViewById(R.id.imageLabel);
                            imageLabel.setText(image.getName());

                            TextView sdCardLabel = dialogWriteSuccessfulView.findViewById(R.id.sdCardLabel);
                            sdCardLabel.setText(SDCard.getInstance().getName());
                        }
                    });
                }
            }.start();
        }
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

    private void setInfos() {
        Image image = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
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

    public boolean isCached() {
        return this.file.exists();
    }

    public String getFileSize() {
        return this.fileSize;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public static Image getInstance() {
        return instance;
    }

    public static void setInstance(Image image) {
        instance = image;
    }
}