package francoisbasset.androidraspberrypiimager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import francoisbasset.androidraspberrypiimager.Image;
import francoisbasset.androidraspberrypiimager.R;

public class ImageAdapter extends ArrayAdapter<Image> {
    public ImageAdapter(Context context, List<Image> images) {
        super(context, 0, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Image image = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_image, parent, false);
        }

        TextView nameLabel = convertView.findViewById(R.id.nameLabel);
        TextView descriptionLabel = convertView.findViewById(R.id.descriptionLabel);

        nameLabel.setText(image.getName());
        descriptionLabel.setText(image.getDescription());

        return convertView;
    }
}