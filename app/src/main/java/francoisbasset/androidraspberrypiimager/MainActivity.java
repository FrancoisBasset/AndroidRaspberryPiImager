package francoisbasset.androidraspberrypiimager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;

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
        EditText input = findViewById(R.id.input);

        String path = "/storage/sdcard0/raspberrypiimager/" + input.getText() + ".zip";

        this.sdCard.extractZip(path);

        View indicator = findViewById(R.id.indicator);
        indicator.setBackgroundColor(Color.GREEN);
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

        this.sdCard = new SDCard(pickedDir, this);

        startActivityForResult(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS), -1);
    }
}