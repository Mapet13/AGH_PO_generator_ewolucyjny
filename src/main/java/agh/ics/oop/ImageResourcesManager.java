package agh.ics.oop;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class ImageResourcesManager {
    private final HashMap<String, Image> images = new HashMap<>();

    public Image getImage(String path) {
        if (!images.containsKey(path))
            images.put(path, getImgFromPath(path));

        return images.get(path);
    }

    private Image getImgFromPath(String path) {
        try {
            return new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
