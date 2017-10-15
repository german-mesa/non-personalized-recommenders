import org.lenskit.api.ItemBasedItemRecommender
import org.lenskit.mooc.nonpers.assoc.AssociationItemBasedItemRecommender
import org.lenskit.mooc.nonpers.assoc.AssociationModel
import org.lenskit.mooc.nonpers.assoc.BasicAssociationModelProvider

bind ItemBasedItemRecommender to AssociationItemBasedItemRecommender
bind AssociationModel toProvider BasicAssociationModelProvider
