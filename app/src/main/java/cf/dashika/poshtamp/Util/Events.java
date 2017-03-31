package cf.dashika.poshtamp.Util;

import java.util.List;

import cf.dashika.poshtamp.Model.Poshtamps;
import cf.dashika.poshtamp.Model.Postamat;
import cf.dashika.poshtamp.Model.TypePoshtamps;

/**
 * Created by dashika on 26/03/17.
 */

public class Events {

    private Events(){}

    public static class setPoshtamps {
        public final Poshtamps poshtamps;

        public setPoshtamps(Poshtamps poshtamps) {
            this.poshtamps = poshtamps;
        }
    }

    public static class filterPoshtamps {
        public final List<Postamat> poshtamps;

        public filterPoshtamps(List<Postamat> poshtamps) {
            this.poshtamps = poshtamps;
        }
    }

    public static class setTypePoshtamps {
        public final TypePoshtamps typePoshtamps;

        public setTypePoshtamps(TypePoshtamps typePoshtamps) {
            this.typePoshtamps = typePoshtamps;
        }
    }

    public static class ChangeDialog{
        public ChangeDialog(){};
    }
}
