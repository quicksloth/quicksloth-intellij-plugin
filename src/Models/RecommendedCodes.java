package Models;

import java.util.Collections;
import java.util.List;

/**
 * Created by pamelaiupipeixinho on 10/09/17.
 */
public class RecommendedCodes {

    private List<Codes> codes;

    public RecommendedCodes(List<Codes> codes) {
        this.codes = codes;
        Collections.sort(this.codes, (o1, o2) -> Float.compare(o1.getScore(), o2.getScore()));
    }

    public List<Codes> getCodes() {
        return codes;
    }

    public void setCodes(List<Codes> codes) {
        this.codes = codes;
    }
}
