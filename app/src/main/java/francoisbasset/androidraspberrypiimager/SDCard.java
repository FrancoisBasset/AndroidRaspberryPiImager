package francoisbasset.androidraspberrypiimager;

import android.webkit.MimeTypeMap;

import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class SDCard {
    private DocumentFile pickedDir;

    private int totalFilesCount = 0;
    private int copiedFilesCount = 0;

    private static SDCard instance;

    public final void installImage(Image image) {
        image.createFolder();

        image.download();
    }

    public final void writeImage(Image image) {
        try {
            ZipFile zipFile = new ZipFile(image.getFile().getPath());

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                entries.nextElement();
                totalFilesCount++;
            }

            entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                String path = entries.nextElement().getName();
                ZipEntry zipEntry = zipFile.getEntry(path);

                writeZipEntry(zipEntry.getName(), zipFile.getInputStream(zipEntry));

                copiedFilesCount++;

                MainActivity.getInstance().runOnUiThread(() -> {
                    MainActivity.getInstance().index.setText(copiedFilesCount + " / " + totalFilesCount);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void writeZipEntry(String fileName, InputStream inputStream) {
        String[] names = fileName.split("/");

        DocumentFile doc = pickedDir, previous;
        DocumentFile fileToCreate;

        for (int i = 0; i < names.length; i++) {
            previous = doc;
            doc = doc.findFile(names[i]);

            if (i < names.length - 1 || fileName.endsWith("/")) {
                if (doc == null) {
                    doc = previous.createDirectory(names[i]);
                }
            } else {
                if (doc == null) {
                    fileToCreate = previous.createFile(MimeTypeMap.getFileExtensionFromUrl(fileName), names[i]);
                } else {
                    fileToCreate = doc.createFile(MimeTypeMap.getFileExtensionFromUrl(fileName), names[i]);
                }

                try {
                    OutputStream outStream = MainActivity.getInstance().getContentResolver().openOutputStream(fileToCreate.getUri());

                    byte[] bytes = new byte[1024];
                    int length;

                    while ((length = inputStream.read(bytes)) >= 0) {
                        outStream.write(bytes, 0, length);
                    }

                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static SDCard getInstance() {
        return instance;
    }

    public static void setInstance(DocumentFile pickedDir) {
        instance = new SDCard();
        instance.pickedDir = pickedDir;
    }
}