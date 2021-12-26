package agh.ics.oop.GUI;

import agh.ics.oop.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AnimalSelectionDisplayer implements IGuiElement {
    private final Label genomeText = new Label();
    private final Label childrenText = new Label();
    private final Label ancestorsText = new Label();
    private final Label dayOfDeathText = new Label();
    private final VBox body = new VBox(genomeText, childrenText, ancestorsText, dayOfDeathText);
    private Animal followingAnimal;
    private MapTypes followingAnimalMapType;
    private int childrenCountUntilFollowing;
    private int ancestorsCountUntilFollowing;

    public void clear() {
        followingAnimal = null;
        genomeText.setText("");
        childrenText.setText("");
        ancestorsText.setText("");
        dayOfDeathText.setText("");
    }

    @Override
    public Node getBody() {
        return body;
    }

    public boolean isFollowing() {
        return followingAnimal != null;
    }

    public void set(Animal animal, MapTypes type) {
        followingAnimal = animal;
        followingAnimalMapType = type;
        childrenCountUntilFollowing = animal.getChildrenCount();
        ancestorsCountUntilFollowing = animal.getAncestorsCount();

        genomeText.setText(String.format("Genome: %s", animal.getGenome().getSorted()));
        updateCountingLabel(ancestorsText, "Ancestors", 0);
        updateCountingLabel(childrenText, "Children", 0);
    }

    private void updateCountingLabel(Label label, String name, Number count) {
        label.setText(String.format("%s count since following: %s", name, count));
    }

    public void update(MapTypes type, WorldMap map, MapTile[][][] tiles, int day) {
        if(followingAnimal != null && followingAnimalMapType.equals(type)) {
            Object atPos = map.objectAt(followingAnimal.getPosition());
            if(atPos != null && atPos.equals(followingAnimal))
                tiles[followingAnimalMapType.value][followingAnimal.getPosition().x()][followingAnimal.getPosition().y()].applySelectionOnContent();
            else
                tiles[followingAnimalMapType.value][followingAnimal.getPosition().x()][followingAnimal.getPosition().y()].removeSelectionOnContent();
            updateCountingLabel(childrenText, "Children", followingAnimal.getChildrenCount() - childrenCountUntilFollowing);
            updateCountingLabel(ancestorsText, "Ancestors", followingAnimal.getAncestorsCount() - ancestorsCountUntilFollowing);

            if(followingAnimal.isDead() && dayOfDeathText.getText().isEmpty())
                dayOfDeathText.setText(String.format("Day of death: %s", day));
        }
    }
}
