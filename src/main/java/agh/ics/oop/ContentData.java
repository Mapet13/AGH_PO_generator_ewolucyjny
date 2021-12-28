package agh.ics.oop;

public class ContentData {
    public final boolean isEmpty;
    public final String contentPath;
    public final String text;

    private ContentData(String contentPath, String text, boolean isEmpty) {
        this.contentPath = contentPath;
        this.isEmpty = isEmpty;
        this.text = text;
    }

    public ContentData(String contentPath, String text) {
        this(contentPath, text, false);
    }

    public static ContentData Empty() {
        return new ContentData("", "", true);
    }
}
