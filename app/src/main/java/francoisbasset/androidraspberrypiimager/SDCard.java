package francoisbasset.androidraspberrypiimager;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SDCard {
    Uri treeUri;
    DocumentFile pickedDir;

    public SDCard(Uri treeUri, DocumentFile pickedDir) {
        this.treeUri = treeUri;
        this.pickedDir = pickedDir;
    }

    public void clean() {
        for (DocumentFile documentFile : pickedDir.listFiles()) {
            if (documentFile.getName() != null) {
                documentFile.delete();
            }
        }
    }

    public void extractZip(String zipPath) {
        clean();

        try {
            ZipFile zipFile = new ZipFile(zipPath);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                String path = entries.nextElement().getName();
                ZipEntry zipEntry = zipFile.getEntry(path);

                writeZipEntry(zipEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeZipEntry(ZipEntry zipEntry) {
        String[] names = zipEntry.getName().split("/");

        DocumentFile doc = pickedDir, previous;
        DocumentFile fileToCreate;

        for (int i = 0; i < names.length; i++) {
            previous = doc;
            doc = doc.findFile(names[i]);

            if (i < names.length - 1 || zipEntry.getName().endsWith("/")) {
                if (doc == null) {
                    doc = previous.createDirectory(names[i]);
                }
            } else {
                if (doc == null) {
                    fileToCreate = previous.createFile(MimeTypeMap.getFileExtensionFromUrl(zipEntry.getName()), names[i]);
                } else {
                    fileToCreate = doc.createFile(MimeTypeMap.getFileExtensionFromUrl(zipEntry.getName()), names[i]);
                }

                /*try {
                    OutputStream outStream = ContentResolver.getContentResolver().openOutputStream(fileToCreate.getUri());
                    OutputStreamWriter osw = new OutputStreamWriter(outStream);

                    osw.write("bonjour nico");
                    osw.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/



            }
        }

/*

        DocumentFile oktxt = this.sdCard.pickedDir.createFile("plain/text", "ok.txt");

        try {
            OutputStream outStream = this.getContentResolver().openOutputStream(oktxt.getUri());
            OutputStreamWriter osw = new OutputStreamWriter(outStream);

            osw.write("bonjour nico");
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}