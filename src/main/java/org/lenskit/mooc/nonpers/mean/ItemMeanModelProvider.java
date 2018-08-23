package org.lenskit.mooc.nonpers.mean;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.ratings.Rating;
import org.lenskit.inject.Transient;
import org.lenskit.util.io.ObjectStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Provider class that builds the mean rating item scorer, computing item means from the
 * ratings in the DAO.
 */
public class ItemMeanModelProvider implements Provider<ItemMeanModel> {
    /**
     * A logger that you can use to emit debug messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(ItemMeanModelProvider.class);

    /**
     * The data access object, to be used when computing the mean ratings.
     */
    private final DataAccessObject dao;

    /**
     * Constructor for the mean item score provider.
     *
     * <p>The {@code @Inject} annotation tells LensKit to use this constructor.
     *
     * @param dao The data access object (DAO), where the builder will get ratings.  The {@code @Transient}
     *            annotation on this parameter means that the DAO will be used to build the model, but the
     *            model will <strong>not</strong> retain a reference to the DAO.  This is standard procedure
     *            for LensKit models.
     */
    @Inject
    public ItemMeanModelProvider(@Transient DataAccessObject dao) {
        this.dao = dao;
    }

    /**
     * Construct an item mean model.
     *
     * <p>The {@link Provider#get()} method constructs whatever object the provider class is intended to build.</p>
     *
     * @return The item mean model with mean ratings for all items.
     */
    @Override
    public ItemMeanModel get() {
        // TODO Set up data structures for computing means
        Long2DoubleMap itemRatingSums = new Long2DoubleOpenHashMap();
        Long2IntMap itemRatingCounts = new Long2IntOpenHashMap();

        itemRatingSums.defaultReturnValue(0.0);
        itemRatingCounts.defaultReturnValue(0);

        try (ObjectStream<Rating> ratings = dao.query(Rating.class).stream()) {
            for (Rating r: ratings) {
                // this loop will run once for each rating in the data set
                // TODO process this rating
                long i = r.getItemId();

                itemRatingSums.put(i, r.getValue() + itemRatingSums.get(i));
                itemRatingCounts.put(i, itemRatingCounts.get(i) + 1);
            }
        }

        Long2DoubleOpenHashMap means = new Long2DoubleOpenHashMap();
        // TODO Finalize means to store them in the mean model
        for(Long2DoubleMap.Entry itemRatingSum: itemRatingSums.long2DoubleEntrySet()) {
            long i = itemRatingSum.getLongKey();
            double itemCount = itemRatingCounts.get(i);

            means.put(i, itemRatingSum.getDoubleValue() / itemCount);
        }

        // Check results:
        // logger.info("item [1203] " + means.get(1203));
        // logger.info("item [2959] " + means.get(2959));

        logger.info("computed mean ratings for {} items", means.size());
        return new ItemMeanModel(means);
    }
}
