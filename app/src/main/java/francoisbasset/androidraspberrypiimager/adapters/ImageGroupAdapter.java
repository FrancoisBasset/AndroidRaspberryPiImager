package francoisbasset.androidraspberrypiimager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import francoisbasset.androidraspberrypiimager.ImageGroup;
import francoisbasset.androidraspberrypiimager.R;

public final class ImageGroupAdapter extends ArrayAdapter<ImageGroup> {
    public ImageGroupAdapter(Context context, List<ImageGroup> users) {
        super(context, 0, users);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        ImageGroup imageGroup = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_image_group, parent, false);
        }

        TextView nameLabel = convertView.findViewById(R.id.nameLabel);
        TextView descriptionLabel = convertView.findViewById(R.id.descriptionLabel);

        nameLabel.setText(imageGroup.getName());
        descriptionLabel.setText(imageGroup.getDescription());

        return convertView;
    }
}