package francoisbasset.androidraspberrypiimager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImageGroup {
    private static JSONArray jsonArray;

    private String name;
    private String description;
    private List<Image> images;

    public ImageGroup(String name, String description, List<Image> images) {
        this.name = name;
        this.description = description;
        this.images = images;
    }

    public static List<ImageGroup> getGroupImages() {
        List<ImageGroup> imageGroups = new ArrayList<>();

        try {
            if (jsonArray == null) {
                InputStream inputStream = MainActivity.getInstance().getResources().openRawResource(R.raw.images);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder jsonString = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonString.append(line);
                }

                jsonArray = new JSONArray(jsonString.toString());
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject imageGroupJsonObject = jsonArray.getJSONObject(i);

                imageGroups.add(
                        new ImageGroup(
                                imageGroupJsonObject.getString("name"),
                                imageGroupJsonObject.getString("description"),
                                getImages(imageGroupJsonObject)
                        )
                );
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return imageGroups;
    }

    private static List<Image> getImages(JSONObject imageGroupJsonObject) {
        List<Image> images = new ArrayList<>();

        try {
            JSONArray imagesJsonArray = imageGroupJsonObject.getJSONArray("images");

            for (int i = 0; i < imagesJsonArray.length(); i++) {
                JSONObject imageJsonObject = imagesJsonArray.getJSONObject(i);

                images.add(
                        new Image(
                                imageJsonObject.getString("name"),
                                imageJsonObject.getString("description"),
                                imageJsonObject.getString("filename"),
                                imageJsonObject.getString("url")
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return images;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Image> getImages() {
        return this.images;
    }
}