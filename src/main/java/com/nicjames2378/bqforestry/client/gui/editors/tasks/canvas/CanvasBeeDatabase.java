package com.nicjames2378.bqforestry.client.gui.editors.tasks.canvas;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.lists.CanvasSearch;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;

import java.util.*;

public abstract class CanvasBeeDatabase extends CanvasSearch<String, IAllele> {
    public CanvasBeeDatabase(IGuiRect rect) {
        super(rect);
    }

    @Override
    protected Iterator<IAllele> getIterator() {
        Collection<IAllele> species = AlleleManager.alleleRegistry.getRegisteredAlleles(EnumBeeChromosome.SPECIES);
        List<IAllele> temp = new ArrayList<>(species);
        temp.sort(advComparator);

        return temp.iterator();
    }

    //@Override
    protected void queryMatches(IAllele value, String query, ArrayDeque<String> results) {
        if (value.getAlleleName().toLowerCase().contains(query.toLowerCase())) {
            results.add(value.getAlleleName());
        } // Could search the description but that'd make the results less relevant to the query
    }

    private static final Comparator<IAllele> advComparator = (o1, o2) -> {
        if (o1 != null && o2 == null) {
            return -1;
        } else if (o1 == null && o2 != null) {
            return 1;
        }

        String s1 = o1.toString(); //.getDisplay() == null ? o1.getId().toString().toLowerCase() : o1.getDisplay().getTitle().getFormattedText().toLowerCase();
        String s2 = o2.toString(); //.getDisplay() == null ? o2.getId().toString().toLowerCase() : o2.getDisplay().getTitle().getFormattedText().toLowerCase();

        return s1.compareTo(s2);
    };
}