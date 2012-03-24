package org.styloot.hobo.itemfinders;

import java.util.*;

import org.styloot.hobo.*;
import org.styloot.hobo.itemfinders.*;
import org.styloot.hobo.iterators.*;

public class VectorItemFinder implements ItemFinder {
    public VectorItemFinder(Collection<Item> myItems, String cat) {
	items = new Vector<Item>(myItems);
	Collections.sort(items);
	category = cat;
    }
    private Vector<Item> items;
    private String category;

    public int size() {
	return items.size();
    }

    public Iterator<Item> getItems() {
	return items.iterator();
    }

    public Iterator<Item> find(Collection<String> features, CIELabColor color, double distance, int minPrice, int maxPrice) {
	Iterator<Item> iterator = items.iterator();
	iterator = CostFilterIterator.wrap(iterator, minPrice, maxPrice);

	if ((features != null) && (features.size() > 0)) {
	    iterator = new FeaturesFilterIterator(iterator, features);
	}

	if (color != null && distance > 0) {
	    iterator = new ColorFilterIterator(iterator, color, distance);
	}
	return iterator;
    };

}