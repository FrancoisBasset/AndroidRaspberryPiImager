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
        this.showInstalling();

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
            this.writeButton.setEnabled(true);
            this.writeButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            this.writeButton.setTextColor(getResources().getColor(R.color.raspberry));
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
        this.showStopInstalling();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        View dialogWriteSuccessfulView = MainActivity.getInstance().getLayoutInflater().inflate(R.layout.dialog_write_successful, null);

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
        Button continueButton = dialogWriteSuccessfulView.findViewById(R.id.continueButton);
        TextView imageLabel = dialogWriteSuccessfulView.findViewById(R.id.imageLabel);
        TextView sdCardLabel = dialogWriteSuccessfulView.findViewById(R.id.sdCardLabel);

        closeLabel.setOnClickListener(onClick);
        continueButton.setOnClickListener(onClick);
        imageLabel.setText(Image.getInstance().getName());
        sdCardLabel.setText(SDCard.getInstance().getName());
    }

    public void showInstalling() {
        ColorStateList colorStateList = new ColorStateList(new int[][] {{-android.R.attr.state_enabled}}, new int[] { getResources().getColor(R.color.cannotWriteBackground)});

        this.chooseSDCardButton.setEnabled(false);
        this.chooseSDCardButton.setBackgroundTintList(colorStateList);
        this.chooseSDCardButton.setTextColor(getResources().getColor(R.color.cannotWriteLabel));

        this.chooseOSButton.setEnabled(false);
        this.chooseOSButton.setBackgroundTintList(colorStateList);
        this.chooseOSButton.setTextColor(getResources().getColor(R.color.cannotWriteLabel));

        this.writeButton.setEnabled(false);
        this.writeButton.setBackgroundTintList(colorStateList);
        this.writeButton.setTextColor(getResources().getColor(R.color.cannotWriteLabel));

        this.setProgressBarPercent(0);
        this.setProgressBarVisibility(View.VISIBLE);
    }

    public void showStopInstalling() {
        this.chooseSDCardButton.setEnabled(true);
        this.chooseSDCardButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        this.chooseSDCardButton.setTextColor(getResources().getColor(R.color.raspberry));

        this.chooseOSButton.setEnabled(true);
        this.chooseOSButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        this.chooseOSButton.setTextColor(getResources().getColor(R.color.raspberry));

        this.writeButton.setEnabled(true);
        this.writeButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        this.writeButton.setTextColor(getResources().getColor(R.color.raspberry));

        this.setProgressBarPercent(0);
        this.setProgressBarVisibility(View.INVISIBLE);
    }

    public void stopInstallation(View v) {
        SDCard.getInstance().stopInstallation();

        this.showStopInstalling();
    }
}