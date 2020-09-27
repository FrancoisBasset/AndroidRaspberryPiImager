package francoisbasset.androidraspberrypiimager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

public final class MainActivity extends AppCompatActivity {
    private Button chooseOSButton;
    private Button chooseSDCardButton;
    private Button writeButton;
    private TextView writingPercentLabel;
    private ProgressBar progressBar;
    private Button cancelWriteButton;

    public static MainActivity instance;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.chooseOSButton = findViewById(R.id.chooseOSButton);
        this.chooseSDCardButton = findViewById(R.id.chooseSDCardButton);
        this.writeButton = findViewById(R.id.writeButton);
        this.writingPercentLabel = findViewById(R.id.writingPercentLabel);
        this.progressBar = findViewById(R.id.progressBar);
        this.cancelWriteButton = findViewById(R.id.cancelWriteButton);

        this.writingPercentLabel.setVisibility(View.INVISIBLE);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.cancelWriteButton.setVisibility(View.INVISIBLE);

        instance = this;
    }

    public final static MainActivity getInstance() {
        return instance;
    }

    public final void chooseOS(View v) {
        this.startActivityForResult(new Intent(this, ImageChooserActivity.class), 1);
    }

    public final void chooseSD(View v) {
        this.startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),42);
        this.startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
    }

    public final void write(View v) {
        this.setProgressBarVisibility(View.VISIBLE);
        SDCard.getInstance().installImage(Image.getInstance());
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent resultData) {
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

                this.chooseSDCardButton.setText(SDCard.getInstance().getName());

                this.checkWrite();
                break;
            case 1:
                chooseOSButton.setText(Image.getInstance().getName());
                break;
        }
    }

    private final void checkWrite() {
        if (Image.getInstance() != null && SDCard.getInstance() != null) {
            writeButton.setEnabled(true);
            writeButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            writeButton.setTextColor(getResources().getColor(R.color.raspberry));
        }
    }

    public final void setProgressBarPercent(int percent) {
        this.writingPercentLabel.setText("Writing... " + percent + "%");
        this.progressBar.setProgress(percent);
    }

    public final void setProgressBarVisibility(int visibility) {
        this.writingPercentLabel.setVisibility(visibility);
        this.progressBar.setVisibility(visibility);
        this.cancelWriteButton.setVisibility(visibility);
    }

    public final void showWriteSuccessfulDialog() {
        this.setProgressBarVisibility(View.INVISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());

        LayoutInflater inflater = MainActivity.getInstance().getLayoutInflater();
        View dialogWriteSuccessfulView = inflater.inflate(R.layout.dialog_write_successful, null);

        builder.setView(dialogWriteSuccessfulView);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public final void onClick(View v) {
                alertDialog.dismiss();
            }
        };

        TextView closeLabel = dialogWriteSuccessfulView.findViewById(R.id.closeLabel);
        closeLabel.setOnClickListener(onClick);

        Button continueButton = dialogWriteSuccessfulView.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(onClick);

        TextView imageLabel = dialogWriteSuccessfulView.findViewById(R.id.imageLabel);
        imageLabel.setText(Image.getInstance().getName());

        TextView sdCardLabel = dialogWriteSuccessfulView.findViewById(R.id.sdCardLabel);
        sdCardLabel.setText(SDCard.getInstance().getName());
    }
}