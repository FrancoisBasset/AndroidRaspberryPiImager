package francoisbasset.androidraspberrypiimager;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public class MainActivity extends AppCompatActivity {
    private Button chooseOSButton;
    private Button chooseSDCardButton;
    private Button writeButton;
    private TextView writingPercentLabel;
    private ProgressBar progressBar;

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.chooseOSButton = findViewById(R.id.chooseOSButton);
        this.chooseSDCardButton = findViewById(R.id.chooseSDCardButton);
        this.writeButton = findViewById(R.id.writeButton);
        this.writingPercentLabel = findViewById(R.id.writingPercentLabel);
        this.progressBar = findViewById(R.id.progressBar);

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

                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                SDCard.setInstance(new SDCard(pickedDir));

                chooseSDCardButton.setText(SDCard.getInstance().getName());

                checkGoodForWriting();
                break;
            case 1:
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

    public void setInstallProgress(int percent) {
        this.writingPercentLabel.setText("Writing... " + percent + "%");
        this.progressBar.setProgress(percent);
    }
}