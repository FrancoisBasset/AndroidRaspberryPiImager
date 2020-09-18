package francoisbasset.androidraspberrypiimager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class MainActivity extends AppCompatActivity {
    private Button chooseOSButton;
    private Button chooseSDCardButton;
    private Button writeButton;

    public View indicator;

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.chooseOSButton = findViewById(R.id.chooseOSButton);
        this.chooseSDCardButton = findViewById(R.id.chooseSDCardButton);
        this.writeButton = findViewById(R.id.writeButton);

        this.indicator = findViewById(R.id.indicator);

        instance = this;
    }

    public void chooseOS(View v) {
        startActivityForResult(new Intent(this, ImageChooserActivity.class), 1);
    }

    public void chooseSD(View v) {
        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),42);
        startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
    }

    public void write(View v) {
        SDCard.getInstance().installImage(Image.getInstance());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 42:
                Uri treeUri = resultData.getData();

                chooseSDCardButton.setText(treeUri.getLastPathSegment().split(":")[0]);

                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                SDCard.setInstance(pickedDir);

                checkGoodForWriting();
                break;
            case 1:
                Toast.makeText(this, Image.getInstance().getName(), Toast.LENGTH_SHORT).show();
                chooseOSButton.setText(Image.getInstance().getName());
                break;
        }
    }

    private void checkGoodForWriting() {
        if (Image.getInstance() != null && SDCard.getInstance() != null) {
            writeButton.setEnabled(true);
            writeButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            writeButton.setTextColor(getResources().getColor(R.color.raspberry));
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }
}