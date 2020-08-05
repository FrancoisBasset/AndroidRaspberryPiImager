package francoisbasset.androidraspberrypiimager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class MainActivity extends AppCompatActivity {
    SDCard sdCard = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chooseSD(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        startActivityForResult(intent, 42);
    }

    public void write(View v) {
        String path = "/storage/sdcard0/zip.zip";

        this.sdCard.extractZip(path);
    }

    public void clean(View v) {
        this.sdCard.clean();
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode != RESULT_OK)
            return;

        Uri treeUri = resultData.getData();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
        grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        this.sdCard = new SDCard(treeUri, pickedDir);
    }
}