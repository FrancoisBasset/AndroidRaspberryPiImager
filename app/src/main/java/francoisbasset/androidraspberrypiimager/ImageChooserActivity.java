package francoisbasset.androidraspberrypiimager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import francoisbasset.androidraspberrypiimager.adapters.ImageAdapter;
import francoisbasset.androidraspberrypiimager.adapters.ImageGroupAdapter;

public class ImageChooserActivity extends AppCompatActivity {
    private int step = 1;
    private List<ImageGroup> imageGroups;

    private ListView imagesListView;

    private ImageGroupAdapter imageGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_chooser);

        this.imageGroups = ImageGroup.getGroupImages(this);
        this.imageGroupAdapter = new ImageGroupAdapter(this, this.imageGroups);

        this.imagesListView = findViewById(R.id.imagesListView);
        this.imagesListView.setAdapter(this.imageGroupAdapter);

        imagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (step) {
                    case 1:
                        ImageGroup imageGroup = (ImageGroup) parent.getItemAtPosition(position);

                        ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), imageGroup.getImages());
                        imagesListView.setAdapter(imageAdapter);

                        step = 2;
                        break;
                    case 2:
                        Image image = (Image) parent.getItemAtPosition(position);
                        Image.setInstance(image);
                        setResult(RESULT_OK, new Intent());
                        finish();
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (this.step) {
            case 1:
                finish();
                break;
            case 2:
                this.step = 1;
                imagesListView.setAdapter(imageGroupAdapter);
                break;
        }
    }
}