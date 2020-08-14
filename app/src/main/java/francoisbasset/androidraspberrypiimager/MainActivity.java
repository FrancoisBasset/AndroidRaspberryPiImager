package francoisbasset.androidraspberrypiimager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainActivity extends AppCompatActivity {
    private String os;
    private SDCard sdCard = null;

    private boolean canWrite = false;

    private Button chooseOSButton;
    private Button chooseSDCardButton;
    private Button writeButton;
    public TextView index;

    private View indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseOSButton = findViewById(R.id.chooseOSButton);
        chooseSDCardButton = findViewById(R.id.chooseSDCardButton);
        writeButton = findViewById(R.id.writeButton);
        index = findViewById(R.id.index);
        indicator = findViewById(R.id.indicator);
    }

    public void chooseOS(View v) {
        /*SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.os));

        Spinner spinner = new Spinner(this);
        spinner.setAdapter(spinnerAdapter);*/

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("os");

        final String[] allOs = getResources().getStringArray(R.array.os);

        b.setItems(allOs, (dialog, which) -> {
            os = allOs[which].toLowerCase();

            chooseOSButton.setText(os);

            checkGoodForWriting();
        });

        b.show();
    }

    public void chooseSD(View v) {
        startActivityForResult(
                new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                42
        );

        startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
    }

    public void write(View v) {
        if (!canWrite) {
            return;
        }

        String path = "/storage/sdcard0/raspberrypiimager/" + os + ".zip";

        new Thread() {
            public void run() {
                sdCard.extractZip(path);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indicator.setBackgroundColor(Color.GREEN);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (resultCode != RESULT_OK) {
            return;
        }

        Uri treeUri = resultData.getData();

        String sdCardLabel = treeUri.getLastPathSegment().split(":")[0];
        chooseSDCardButton.setText(sdCardLabel);

        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
        grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        this.sdCard = new SDCard(pickedDir, this);

        checkGoodForWriting();
    }

    private void checkGoodForWriting() {
        canWrite = os != null && sdCard != null;

        if (canWrite) {
            writeButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            writeButton.setTextColor(getResources().getColor(R.color.raspberry));
        }
    }
}