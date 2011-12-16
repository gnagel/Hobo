package org.styloot.hobo;

import java.util.*;
import java.io.*;

import org.styloot.hobo.*;
import org.styloot.hobo.iterators.*;

public class HoboIndex {
    public HoboIndex(Collection<Item> items) {
	this(items.iterator());
    }

    public HoboIndex(Iterator<Item> items) {
	Map<String,List<Item>> catToItems = categoriesToItems(items);
	//Now we need to build ItemFinders
	for (String cat : catToItems.keySet()) {
	    categoryMap.put(cat, new VectorItemFinder(catToItems.get(cat)));
	}
    }

    public Iterator<Item> findByCategory(String cat) {
	Vector<Iterator<Item>> iters = new Vector<Iterator<Item>>();
	for (ItemFinder f : categoryMap.itemFinders(cat)) {
	    iters.add(f.getItems());
	}
	return new CombinedIterator(iters);
    }



    public Iterator<Item> find(String cat, Collection<String> features) {
	Vector<Iterator<Item>> iters = new Vector<Iterator<Item>>();

	Collection<ItemFinder> categories;
	if ((cat != "") && (cat != null)) {
	    categories = categoryMap.itemFinders(cat);
	} else {
	    categories = categoryMap.values(); //Small performance improvement in case of no category
	}

	if (features != null && features.size() > 0) {
	    for (ItemFinder f : categories) {
		iters.add(f.findItemsWithFeatures(features));
	    }
	} else {
	    for (ItemFinder f : categories) {
		iters.add(f.getItems());
	    }
	}
	return new CombinedIterator(iters);
    }

    private CategoryMap categoryMap = new CategoryMap();

    protected Map<String,List<Item>> categoriesToItems(Iterator<Item> items) {
	Map<String,List<Item>> result = new HashMap<String,List<Item>>();

	for (Iterator<Item> iter = items; iter.hasNext();) {
	    Item item = iter.next();
	    if (!result.containsKey(item.category)) {
		result.put(item.category, new LinkedList<Item>());
	    }
	    result.get(item.category).add(item);
	}
	return result;
    }

    private static class CategoryMap extends TreeMap<String,ItemFinder> {
	public NavigableMap<String,ItemFinder> findSubCategoriesMap(String key) {
	    return subMap(key, true, key+Character.MAX_VALUE, true);
	}

	public Collection<ItemFinder> itemFinders(String key) {
	    return findSubCategoriesMap(key).values();
	}

	public Set<String> subcategories(String key) {
	    return findSubCategoriesMap(key).keySet();
	}
    }

    public static void main(String[] args) {
	Vector<Item> items = new Vector<Item>();
	for (int i=0;i<10;i++) {
	    Vector<String> f = new Vector<String>();
	    f.add("foo");
	    if (i % 2 == 0)
		f.add("bar");
	    if (i % 3 == 0)
		items.add(new Item("id" + i, "/dress", f, i));
	    if (i % 3 == 1)
		items.add(new Item("id" + i, "/dress/short", f, i));
	    if (i % 3 == 2)
		items.add(new Item("id" + i, "/skirt", f, i));
	}

	HoboIndex idx = new HoboIndex(items);

	Vector<String> f = new Vector<String>();
	f.add("foo");
	f.add("bar");
	for (Iterator<Item> i = idx.find("/dress", f); i.hasNext(); ) {
	    Item item = (Item)i.next();
	    System.out.println(item.id + " -> " + item.category + " , " + item.quality);
	}

    }
}