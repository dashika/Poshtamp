package cf.dashika.poshtamp;


import java.util.List;

import cf.dashika.poshtamp.Model.Poshtamps;
import cf.dashika.poshtamp.Model.Postamat;
import cf.dashika.poshtamp.Util.Events;

/**
 * Created by Belyaeva on 2/24/2016.
 */
 class GroupingForMap {

     int Grouping(MyClusterManager mMyClusterManager) {
        List<Postamat> poshtamps = PoshtampApllication.get().getPoshtampses().getPostamats();
        mMyClusterManager.clearItems();
        for (Postamat u : poshtamps) {
            mMyClusterManager.addItem(u);

        }
        mMyClusterManager.cluster();
        return poshtamps.size();
    }


     int FilterByName(MyClusterManager mMyClusterManager, String name) {
         List<Postamat> poshtamps = PoshtampApllication.get().getPoshtampses().getPostamats();
         if(poshtamps == null) return 0;
         List<Postamat> fposhtamps = PoshtampApllication.get().getFilter_poshtampses().getPostamats();
         fposhtamps.clear();
         mMyClusterManager.clearItems();

         for (Postamat u : poshtamps) {
             if(u.getAddress().toUpperCase().contains(name.toUpperCase())
                     || u.getAddressUa().toUpperCase().contains(name.toUpperCase()) ) {
                 mMyClusterManager.addItem(u);
                 fposhtamps.add(u);
             }
         }
         PoshtampApllication.get().bus().send(new Events.filterPoshtamps(fposhtamps));
         mMyClusterManager.cluster();
         return poshtamps.size();
    }


}
