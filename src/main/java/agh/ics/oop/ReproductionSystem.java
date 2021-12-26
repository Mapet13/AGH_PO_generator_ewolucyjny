package agh.ics.oop;

import agh.ics.oop.utilities.IDProvider;
import agh.ics.oop.utilities.Pair;

public record ReproductionSystem(IDProvider idProvider, IMoveObserver moveObserver) {
    public final static float parentalEnergyFactor = 0.25f;
    public final static float minimalReproductionEnergyFactor = 0.5f;

    public Animal createChildrenFrom(Animal firstParent, Animal secondParent) {
        Pair<Animal, Animal> parents = Pair.ShuffledPair(firstParent, secondParent);

        Genome genome = Genome.From(
                parents.first().getGenome(),
                parents.second().getGenome(),
                (float) parents.first().getEnergy() / (float) parents.second().getEnergy());

        int energy = appropriateParentalEnergy(parents);


        Animal child = new Animal(idProvider.getNext(), firstParent.getPosition(), energy, moveObserver, genome);

        parents.first().addChild(child);
        parents.second().addChild(child);

        return child;
    }

    private int appropriateParentalEnergy(Pair<Animal, Animal> parents) {
        int childEnergy = 0;

        for (Animal parent : new Animal[]{parents.first(), parents.second()}) {
            int parentalEnergy = Math.round(parent.getEnergy() * parentalEnergyFactor);
            parent.subtractEnergy(parentalEnergy);
            childEnergy += parentalEnergy;
        }

        return childEnergy;
    }
}
